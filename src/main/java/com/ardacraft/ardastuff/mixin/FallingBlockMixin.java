package com.ardacraft.ardastuff.mixin;

import net.minecraft.block.FallingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlock.class)
public class FallingBlockMixin {

    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
    private void onBlockAdded(CallbackInfo ci) {
        ci.cancel();
    }
}
