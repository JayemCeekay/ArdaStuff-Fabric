package com.ardacraft.ardastuff.mixin;

import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBucketItem.class)
public class EntityBucketItemMixin {

    @Inject(method = "spawnEntity", at = @At(value = "HEAD"), cancellable = true)
    public void cancelSpawnBucket(ServerWorld world, ItemStack stack, BlockPos pos, CallbackInfo ci) {
        ci.cancel();
    }

}
