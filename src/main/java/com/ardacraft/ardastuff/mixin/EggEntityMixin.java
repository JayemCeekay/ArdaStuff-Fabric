package com.ardacraft.ardastuff.mixin;

import net.minecraft.entity.projectile.thrown.EggEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EggEntity.class)
public class EggEntityMixin {

    @Inject(at = @At("HEAD"), method = "onCollision", cancellable = true)
    private void onCollision(CallbackInfo ci) {
        ci.cancel();
    }

}
