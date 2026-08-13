package de.puppyutils.client.routing;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

public final class WaypointRenderer {
    private static final ByteBufferBuilder ALLOCATOR =
        new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);

    private static final RenderPipeline FILLED_THROUGH_WALLS =
        RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                .withLocation(
                    Identifier.fromNamespaceAndPath(
                        "puppyutils",
                        "pipeline/debug_filled_box_through_walls"
                    )
                )
                .withDepthStencilState(Optional.empty())
                .build()
        );

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();

    private static WaypointRenderer INSTANCE;

    private final MacroManager macroManager = new MacroManager();
    private final ArrayList<HighlightedBlock> highlightedBlocks = new ArrayList<>();

    private BufferBuilder buffer;
    private MappableRingBuffer vertexBuffer;

    public static WaypointRenderer getInstance() {
        return INSTANCE;
    }

    public static void init() {
        WaypointRenderer renderer = new WaypointRenderer();
        INSTANCE = renderer;
        LevelRenderEvents.END_EXTRACTION.register(renderer::extractWaypoint);
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(renderer::renderAndDrawWaypoint);
    }

    private void extractWaypoint(LevelExtractionContext context) {
    }

    private void renderAndDrawWaypoint(LevelRenderContext context) {
        if (highlightedBlocks.isEmpty()) {
            return;
        }

        this.renderWaypointText(context);
        this.renderHighlightedBlocks(context);
        this.drawFilledThroughWalls(Minecraft.getInstance(), FILLED_THROUGH_WALLS);
    }

    public void setHighlightedBlocksFromMacro(List<MacroAction> actions) {
        highlightedBlocks.clear();

        for (int i = 0; i < actions.size(); i++) {
            MacroAction action = actions.get(i);
            BlockPos pos = action.position();
            if (pos != null) {
                highlightedBlocks.add(
                    new HighlightedBlock(
                        pos,
                        action.type(),
                        i,
                        action.red(),
                        action.green(),
                        action.blue(),
                        action.alpha()
                    )
                );
            }
        }
    }

    private void renderHighlightedBlocks(LevelRenderContext context) {
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        if (this.buffer == null) {
            this.buffer = new BufferBuilder(
                ALLOCATOR,
                FILLED_THROUGH_WALLS.getVertexFormatMode(),
                FILLED_THROUGH_WALLS.getVertexFormat()
            );
        }

        Matrix4fc pose = matrices.last().pose();

        for (HighlightedBlock block : highlightedBlocks) {
            this.renderFilledBox(
                pose,
                this.buffer,
                block.pos().getX(),
                block.pos().getY(),
                block.pos().getZ(),
                block.pos().getX() + 1,
                block.pos().getY() + 1,
                block.pos().getZ() + 1,
                block.red(),
                block.green(),
                block.blue(),
                block.alpha()
            );
        }

        matrices.popPose();
    }

    private void _renderWaypointText(LevelRenderContext context) {
        System.out.println("meow");
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        MultiBufferSource bufferSource = client.renderBuffers().bufferSource();

        for (HighlightedBlock block : highlightedBlocks) {
            String text =
                block.type() +
                " #" +
                block.index() +
                " (" +
                block.pos().getX() +
                ", " +
                block.pos().getY() +
                ", " +
                block.pos().getZ() +
                ")";

            matrices.pushPose();
            matrices.translate(
                block.pos().getX() + 0.5 - camera.x,
                block.pos().getY() + 1.2 - camera.y,
                block.pos().getZ() + 0.5 - camera.z
            );

            matrices.mulPose(client.gameRenderer.getMainCamera().rotation());

            Matrix4f matrix = matrices.last().pose();
            float width = font.width(text);
            float x = -width / 2.0f;

            font.drawInBatch(
                text,
                x,
                0.0f,
                0xFFFFFF,
                false,
                matrix,
                bufferSource,
                Font.DisplayMode.SEE_THROUGH,
                0,
                15728880
            );

            matrices.popPose();
        }

        client.renderBuffers().bufferSource().endBatch();

    }

    private void renderWaypointText(LevelRenderContext context) {
        Vec3 camera = context.levelState().cameraRenderState.pos;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

        PoseStack matrices = new PoseStack(); // fresh stack, not from context

        for (HighlightedBlock block : highlightedBlocks) {
            String text = block.type() + " #" + block.index() +
                " (" + block.pos().getX() + ", " + block.pos().getY() + ", " + block.pos().getZ() + ")";

            matrices.pushPose();
            matrices.translate(
                block.pos().getX() + 0.5 - camera.x,
                block.pos().getY() + 1.2 - camera.y,
                block.pos().getZ() + 0.5 - camera.z
            );
            matrices.mulPose(client.gameRenderer.getMainCamera().rotation());
            matrices.scale(0.025f, -0.025f, 0.025f);

            Matrix4f matrix = matrices.last().pose();
            float x = -font.width(text) / 2.0f;

            font.drawInBatch(text, x, 0f, 0xFFFFFF, false, matrix,
                bufferSource, Font.DisplayMode.SEE_THROUGH, 0, 15728880);

            matrices.popPose();
        }

        bufferSource.endBatch();
    }

    private void renderFilledBox(
        Matrix4fc positionMatrix,
        BufferBuilder buffer,
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
    }

    private void drawFilledThroughWalls(Minecraft client, RenderPipeline pipeline) {
        MeshData builtBuffer = this.buffer.buildOrThrow();
        MeshData.DrawState drawParameters = builtBuffer.drawState();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = this.upload(drawParameters, format, builtBuffer);
        draw(client, pipeline, builtBuffer, drawParameters, vertices, format);

        this.vertexBuffer.rotate();
        this.buffer = null;
    }

    private GpuBuffer upload(
        MeshData.DrawState drawParameters,
        VertexFormat format,
        MeshData builtBuffer
    ) {
        int vertexBufferSize = drawParameters.vertexCount() * format.getVertexSize();

        if (this.vertexBuffer == null || this.vertexBuffer.size() < vertexBufferSize) {
            if (this.vertexBuffer != null) {
                this.vertexBuffer.close();
            }

            this.vertexBuffer = new MappableRingBuffer(
                () -> "puppyutils waypoint render buffer",
                GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE,
                vertexBufferSize
            );
        }

        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        try (
            GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(
                this.vertexBuffer.currentBuffer().slice(0, builtBuffer.vertexBuffer().remaining()),
                false,
                true
            )
        ) {
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), mappedView.data());
        }

        return this.vertexBuffer.currentBuffer();
    }

    private static void draw(
        Minecraft client,
        RenderPipeline pipeline,
        MeshData builtBuffer,
        MeshData.DrawState drawParameters,
        GpuBuffer vertices,
        VertexFormat format
    ) {
        GpuBuffer indices;
        VertexFormat.IndexType indexType;

        if (pipeline.getVertexFormatMode() == VertexFormat.Mode.QUADS) {
            builtBuffer.sortQuads(
                ALLOCATOR,
                RenderSystem.getProjectionType().vertexSorting()
            );
            indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.indexBuffer());
            indexType = builtBuffer.drawState().indexType();
        } else {
            RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer =
                RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices = shapeIndexBuffer.getBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.type();
        }

        GpuBufferSlice dynamicTransforms =
            RenderSystem.getDynamicUniforms().writeTransform(
                RenderSystem.getModelViewMatrix(),
                COLOR_MODULATOR,
                MODEL_OFFSET,
                TEXTURE_MATRIX
            );

        try (
            RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                    () -> "puppyutils waypoint render pass",
                    client.getMainRenderTarget().getColorTextureView(),
                    OptionalInt.empty(),
                    client.getMainRenderTarget().getDepthTextureView(),
                    OptionalDouble.empty()
                )
        ) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            renderPass.setVertexBuffer(0, vertices);
            renderPass.setIndexBuffer(indices, indexType);
            renderPass.drawIndexed(0 / format.getVertexSize(), 0, drawParameters.indexCount(), 1);
        }

        builtBuffer.close();
    }

    public void close() {
        ALLOCATOR.close();
        if (this.vertexBuffer != null) {
            this.vertexBuffer.close();
            this.vertexBuffer = null;
        }
    }
}
