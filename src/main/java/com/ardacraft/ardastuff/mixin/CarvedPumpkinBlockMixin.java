package com.ardacraft.ardastuff.mixin;

import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    @Inject(method = "trySpawnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 0), cancellable = true)
    public void noSpawnSnowGolem(World world, BlockPos pos, CallbackInfo ci) {
        if(!world.isClient) {
            world.playSound(null, pos, SoundEvents.GOAT_HORN_SOUNDS.get(1), SoundCategory.RECORDS, 1f, 1f);
        }
        ci.cancel();
    }

    @Inject(method = "trySpawnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 1), cancellable = true)
    public void noSpawnIronGolem(World world, BlockPos pos, CallbackInfo ci) {
        if(!world.isClient) {
            world.playSound(null, pos, SoundEvents.GOAT_HORN_SOUNDS.get(1), SoundCategory.RECORDS, 1f, 1f);
        }
        ci.cancel();
    }
}
