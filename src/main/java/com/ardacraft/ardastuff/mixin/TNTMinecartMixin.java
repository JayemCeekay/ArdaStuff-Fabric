package com.ardacraft.ardastuff.mixin;

import net.minecraft.entity.vehicle.TntMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntMinecartEntity.class)
public class TNTMinecartMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void explode(CallbackInfo ci) {
        ci.cancel();
    }
}
