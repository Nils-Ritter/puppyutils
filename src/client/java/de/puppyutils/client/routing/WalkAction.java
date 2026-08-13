package de.puppyutils.client.routing;

import net.minecraft.core.BlockPos;

public record WalkAction(int x, int y, int z) implements MacroAction {
    @Override
    public String type() {
        return "walk";
    }

    @Override
    public BlockPos position() {
        return new BlockPos(x, y, z);
    }

    @Override
    public float red() {
        return 0.0f;
    }

    @Override
    public float green() {
        return 1.0f;
    }

    @Override
    public float blue() {
        return 0.0f;
    }

    @Override
    public float alpha() {
        return 0.35f;
    }
}
