package com.ardacraft.ardastuff.mixin;

import net.minecraft.block.RespawnAnchorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorMixin {

    @Inject(at = @At("HEAD"), method = "explode", cancellable = true)
    public void explode(CallbackInfo ci) {
        ci.cancel();
    }


}
