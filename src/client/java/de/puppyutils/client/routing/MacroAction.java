package de.puppyutils.client.routing;

import net.minecraft.core.BlockPos;

public sealed interface MacroAction permits WalkAction, WaitAction {
    String type();

    default BlockPos position() {
        return null;
    }

    default float red() {
        return 1.0f;
    }

    default float green() {
        return 1.0f;
    }

    default float blue() {
        return 1.0f;
    }

    default float alpha() {
        return 0.35f;
    }
}
