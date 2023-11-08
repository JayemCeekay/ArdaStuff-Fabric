package com.ardacraft.ardastuff.mixin;


import com.ardacraft.ardastuff.ArdaStuff;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.fabric.FabricAdapter;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidState.class)
public class FluidSpreadMixin {

    @Inject(method = "onScheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/Fluid;onScheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/FluidState;)V"), cancellable = true)
    public void onWaterSpread(World world, BlockPos pos, CallbackInfo ci) {
        if (ArdaStuff.disableWaterSpread) {
            boolean flag = true;
            for (ServerPlayerEntity player : ArdaStuff.waterSpreaders) {
                LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getGameProfile().getName());
                try {
                    if (session != null && session.getSelection().contains(FabricAdapter.adapt(pos))) {
                        flag = false;
                        break;
                    }
                } catch (IncompleteRegionException e) {
                    e.printStackTrace();
                }
            }
            if (flag) {
                ci.cancel();
            }
        }
    }
}
