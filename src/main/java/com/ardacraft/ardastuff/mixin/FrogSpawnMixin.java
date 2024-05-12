package com.ardacraft.ardastuff.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FrogspawnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FrogspawnBlock.class)
public class FrogSpawnMixin {

    @Inject(method = "onBlockAdded", at = @At(value = "HEAD"), cancellable = true)
    public void cancelFrogSpawning(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        ci.cancel();
    }
}
