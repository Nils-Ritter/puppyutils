package de.puppyutils.client.mixin;

import de.puppyutils.client.utils.GlobalState;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyboardInput.class, remap = false)
public class KeyboardInputMixin {
    @Shadow @Final private Options options;

    @Inject(method = "tick", at = @At("HEAD"))
    private void puppyutils$forceInputs(CallbackInfo ci){
        if(GlobalState.isPlayerControlDisabled()) {
            this.options.keyRight.setDown(GlobalState.movementOpts.getMovRight());
            this.options.keyLeft.setDown(GlobalState.movementOpts.getMovLeft());
            this.options.keyUp.setDown(GlobalState.movementOpts.getMovForward());
            this.options.keyDown.setDown(GlobalState.movementOpts.getMovBack());
            this.options.keyJump.setDown(GlobalState.movementOpts.getMovJump());
            this.options.keyAttack.setDown(GlobalState.movementOpts.getMovAttack());
        }
    }
}