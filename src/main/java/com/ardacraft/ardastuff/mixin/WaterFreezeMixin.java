package com.ardacraft.ardastuff.mixin;


import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class WaterFreezeMixin {

    @Inject(at = @At("HEAD"), method = "canSetIce(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private void injected(WorldView p_47478_, BlockPos p_47479_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
