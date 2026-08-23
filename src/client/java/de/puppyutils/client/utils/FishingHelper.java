package de.puppyutils.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.FishingRodItem;

public final class FishingHelper {

    private static final Minecraft MC = Minecraft.getInstance();

    private static final Object FISH_LOCK = new Object();
    private static boolean fishCaught = false;

    private FishingHelper() {}

    public static void cast() {
        MC.execute(() -> {
            LocalPlayer player = MC.player;

            if (player == null || MC.gameMode == null) {
                return;
            }

            if (!(player.getMainHandItem().getItem() instanceof FishingRodItem)) {
                return;
            }

            MC.gameMode.useItem(player, InteractionHand.MAIN_HAND);
        });
    }

    public static void reel() {
        MC.execute(() -> {
            LocalPlayer player = MC.player;

            if (player == null || MC.gameMode == null) {
                return;
            }

            if (!(player.getMainHandItem().getItem() instanceof FishingRodItem)) {
                return;
            }

            MC.gameMode.useItem(player, InteractionHand.MAIN_HAND);
        });
    }

    /**
     * Blocks the automation thread until a fish bites.
     */
    public static void waitForFish() throws InterruptedException {
        synchronized (FISH_LOCK) {
            fishCaught = false;

            while (!fishCaught) {
                FISH_LOCK.wait();
            }
        }
    }

    /**
     * Called by the client-side detection code.
     */
    public static void onFishCaught() {
        synchronized (FISH_LOCK) {
            if (fishCaught) {
                return;
            }

            fishCaught = true;
            FISH_LOCK.notifyAll();
        }
    }
}
