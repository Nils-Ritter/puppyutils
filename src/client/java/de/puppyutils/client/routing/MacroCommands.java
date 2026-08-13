package de.puppyutils.client.routing;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public final class MacroCommands {
    private static final MacroManager MACROS = new MacroManager();
    private static String currentMacroName = null;
    private static final WaypointRenderer RENDERER = WaypointRenderer.getInstance();

    private MacroCommands() {}

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommands.literal("macro")
                    .then(ClientCommands.literal("recordwalk")
                        .executes(ctx -> {
                            LocalPlayer player = Minecraft.getInstance().player;
                            if (player == null) return 0;
                            BlockPos pos = player.blockPosition();
                            MACROS.addWalkAction(pos);
                            WaypointRenderer.getInstance().setHighlightedBlocksFromMacro(MACROS.getLoadedActions());
                            syncRenderer();
                            ctx.getSource().sendFeedback(Component.literal("Recorded walk: " + pos));
                            return 1;
                        })
                        .then(ClientCommands.argument("x", IntegerArgumentType.integer())
                            .then(ClientCommands.argument("y", IntegerArgumentType.integer())
                                .then(ClientCommands.argument("z", IntegerArgumentType.integer())
                                    .executes(ctx -> {
                                        int x = IntegerArgumentType.getInteger(ctx, "x");
                                        int y = IntegerArgumentType.getInteger(ctx, "y");
                                        int z = IntegerArgumentType.getInteger(ctx, "z");
                                        MACROS.addWalkAction(x, y, z);
                                        syncRenderer();
                                        ctx.getSource().sendFeedback(Component.literal("Recorded walk: " + x + " " + y + " " + z));
                                        return 1;
                                    })))))
                    .then(ClientCommands.literal("recordwait")
                        .then(ClientCommands.argument("ticks", IntegerArgumentType.integer(1))
                            .executes(ctx -> {
                                int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
                                MACROS.addWaitAction(ticks);
                                WaypointRenderer.getInstance().setHighlightedBlocksFromMacro(MACROS.getLoadedActions());
                                ctx.getSource().sendFeedback(Component.literal("Recorded wait: " + ticks + " ticks"));
                                return 1;
                            })))
                    .then(ClientCommands.literal("save")
                        .then(ClientCommands.argument("name", StringArgumentType.string())
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "name");
                                try {
                                    MACROS.saveMacro(name);
                                    currentMacroName = name;
                                    ctx.getSource().sendFeedback(Component.literal("Saved macro: " + name));
                                    return 1;
                                } catch (IOException e) {
                                    ctx.getSource().sendError(Component.literal("Failed to save macro: " + e.getMessage()));
                                    return 0;
                                }
                            })))
                    .then(ClientCommands.literal("load")
                        .then(ClientCommands.argument("name", StringArgumentType.string())
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "name");
                                try {
                                    if (!MACROS.loadMacro(name)) {
                                        ctx.getSource().sendError(Component.literal("Macro not found: " + name));
                                        return 0;
                                    }
                                    currentMacroName = name;
                                    syncRenderer();
                                    WaypointRenderer.getInstance().setHighlightedBlocksFromMacro(MACROS.getLoadedActions());
                                    ctx.getSource().sendFeedback(Component.literal("Loaded macro: " + name));
                                    return 1;
                                } catch (IOException e) {
                                    ctx.getSource().sendError(Component.literal("Failed to load macro: " + e.getMessage()));
                                    return 0;
                                }
                            })))
                    .then(ClientCommands.literal("clear")
                        .executes(ctx -> {
                            MACROS.clearActions();
                            syncRenderer();
                            ctx.getSource().sendFeedback(Component.literal("Cleared current macro"));
                            return 1;
                        }))
                    .then(ClientCommands.literal("reload")
                        .executes(ctx -> {
                            if (currentMacroName == null) {
                                ctx.getSource().sendError(Component.literal("No macro loaded"));
                                return 0;
                            }
                            try {
                                MACROS.loadMacro(currentMacroName);
                                syncRenderer();
                                WaypointRenderer.getInstance().setHighlightedBlocksFromMacro(MACROS.getLoadedActions());
                                ctx.getSource().sendFeedback(Component.literal("Reloaded macro: " + currentMacroName));
                                return 1;
                            } catch (IOException e) {
                                ctx.getSource().sendError(Component.literal("Failed to reload macro: " + e.getMessage()));
                                return 0;
                            }
                        }))
                    .then(ClientCommands.literal("list")
                        .executes(ctx -> {
                            try {
                                Path dir = FabricLoaderPaths.macrosDir();
                                if (!Files.exists(dir)) {
                                    ctx.getSource().sendFeedback(Component.literal("No macros folder yet"));
                                    return 1;
                                }

                                StringBuilder sb = new StringBuilder("Macros: ");
                                try (Stream<Path> stream = Files.list(dir)) {
                                    ArrayList<String> names = new ArrayList<>();
                                    stream.filter(p -> p.getFileName().toString().endsWith(".json"))
                                        .forEach(p -> names.add(p.getFileName().toString().replace(".json", "")));

                                    if (names.isEmpty()) {
                                        ctx.getSource().sendFeedback(Component.literal("No saved macros"));
                                    } else {
                                        ctx.getSource().sendFeedback(Component.literal(String.join(", ", names)));
                                    }
                                }
                                return 1;
                            } catch (IOException e) {
                                ctx.getSource().sendError(Component.literal("Failed to list macros: " + e.getMessage()));
                                return 0;
                            }
                        })
            ));
        });
    }

    private static void syncRenderer() {
        RENDERER.setHighlightedBlocksFromMacro(MACROS.getLoadedActions());
    }

    private static final class FabricLoaderPaths {
        static Path macrosDir() {
            return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getConfigDir()
                .resolve("puppyutils")
                .resolve("macros");
        }
    }
}
