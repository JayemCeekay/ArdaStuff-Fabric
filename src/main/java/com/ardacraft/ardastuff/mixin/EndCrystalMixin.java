package com.ardacraft.ardastuff.mixin;

import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalMixin {

    @Shadow public abstract void kill();

    @Inject(method = "damage", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), cancellable = true)
    public void damage(net.minecraft.entity.damage.DamageSource source, float amount, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
       this.kill();
         cir.setReturnValue(false);
    }
}
