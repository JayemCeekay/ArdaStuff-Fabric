package com.ardacraft.ardastuff.mixin;


import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Inject(at = @At("HEAD"), method = "onCollision", cancellable = true)
    public void onCollision(HitResult hitResult, CallbackInfo ci) {
        ci.cancel();
    }

}
