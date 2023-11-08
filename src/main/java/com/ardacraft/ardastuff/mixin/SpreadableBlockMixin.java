package com.ardacraft.ardastuff.mixin;

import net.minecraft.block.SpreadableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpreadableBlock.class)
public class SpreadableBlockMixin {

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    private void injected(CallbackInfo ci) {
        ci.cancel();
    }

}
