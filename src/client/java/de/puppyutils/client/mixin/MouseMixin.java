package de.puppyutils.client.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import de.puppyutils.client.utils.GlobalState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MouseHandler.class)
public class MouseMixin {
    private final Minecraft minecraft;
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    public MouseMixin(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Inject(method = "onMove", at = @At("RETURN"))
    private void onMove(long handle, double xpos, double ypos, CallbackInfo ci) {
        if(GlobalState.isPlayerControlDisabled()){
            accumulatedDX = 0;
            accumulatedDY = 0;
        }
    }

    @Inject(method = "grabMouse", at = @At("RETURN"))
    private void grabMouse(CallbackInfo ci) {
        if(GlobalState.isPlayerControlDisabled()) {
            double xpos = (double)(this.minecraft.getWindow().getScreenWidth() / 2);
            double ypos = (double)(this.minecraft.getWindow().getScreenHeight() / 2);
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow(), 212993, xpos, ypos);
        }
    }
}