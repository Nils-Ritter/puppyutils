package de.puppyutils.client.mixin;

import net.fabricmc.loader.impl.lib.classtweaker.impl.AccessWidenerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class MinecraftOptionsMixin {
    @Shadow public boolean pauseOnLostFocus = false;
}