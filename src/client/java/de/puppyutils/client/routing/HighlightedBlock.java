package de.puppyutils.client.routing;

import net.minecraft.core.BlockPos;
public record HighlightedBlock(
    BlockPos pos,
    String type,
    int index,
    float red,
    float green,
    float blue,
    float alpha
) {}
