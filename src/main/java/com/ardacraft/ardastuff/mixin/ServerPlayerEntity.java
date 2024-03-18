package com.ardacraft.ardastuff.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.server.network.ServerPlayerEntity.class)
public class ServerPlayerEntity
{
    @Inject(method = "stopRiding()V", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void stopRidingInject(CallbackInfo ci, Entity entity)
    {
        try
        {
            if (entity != null && entity.getCustomName() != null && entity.getCustomName().getString().equals("deleteme"))
            {
                entity.discard();
            }
        }
        catch(Exception ignored)
        {

        }
    }
}
