package com.ardacraft.ardastuff.mixin;

import net.minecraft.block.LeavesBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    @Inject(at = @At("HEAD"), method = "shouldDecay", cancellable = true)
    public void shouldDecayCheck(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

}
