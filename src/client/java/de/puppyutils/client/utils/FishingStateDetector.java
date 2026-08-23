package de.puppyutils.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

public final class FishingStateDetector {

    private static final Minecraft MC = Minecraft.getInstance();

    private static boolean alreadyDetected = false;

    private FishingStateDetector() {
    }

    /**
     * Call this every client tick.
     */
    public static void tick() {
        LocalPlayer player = MC.player;

        if (player == null) {
            alreadyDetected = false;
            return;
        }

        // No fishing bobber means we aren't currently fishing.
        if (player.fishing == null) {
            alreadyDetected = false;
            return;
        }

        // Don't notify the automation thread every tick while the
        // "!!!" armor stand remains present.
        if (alreadyDetected) {
            return;
        }

        if (isFishCaught(player)) {
            alreadyDetected = true;
            FishingHelper.onFishCaught();
        }
    }

    public static void resetFishDetection(){
        alreadyDetected = false;
    }

    private static boolean isFishCaught(LocalPlayer player) {
        var bobber = player.fishing;

        // Only inspect entities close to the bobber.
        var searchBox = bobber.getBoundingBox().inflate(4.0D);

        for (Entity entity : player.level().getEntities(
                bobber,
                searchBox,
                entity -> entity instanceof ArmorStand
        )) {
            ArmorStand armorStand = (ArmorStand) entity;

            if (isFishIndicator(armorStand)
                    && bobber.getBoundingBox()
                        .inflate(4.0D)
                        .contains(armorStand.position())) {
                return true;
            }
        }

        return false;
    }

    private static boolean isFishIndicator(ArmorStand armorStand) {
        if (!armorStand.hasCustomName()) {
            return false;
        }

        var name = armorStand.getCustomName();

        return name != null
                && name.getString().equals("!!!");
    }
}
