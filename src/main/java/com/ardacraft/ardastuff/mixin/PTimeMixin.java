package com.ardacraft.ardastuff.mixin;

import com.ardacraft.ardastuff.ArdaStuff;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public class PTimeMixin {

    @Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToDimension(Lnet/minecraft/network/Packet;Lnet/minecraft/util/registry/RegistryKey;)V"))
    private void broadcastAll(PlayerManager playerManager, Packet packet, RegistryKey registryKey) {
        if (packet instanceof WorldTimeUpdateS2CPacket) {
            for(ServerPlayerEntity player : playerManager.getPlayerList()) {
                if(ArdaStuff.playerTimeMap.containsKey(player)) {
                    player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(player.getWorld().getTime(), ArdaStuff.playerTimeMap.get(player), player.getWorld().getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
                } else {
                    player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(player.getWorld().getTime(), player.getWorld().getTimeOfDay(), player.getWorld().getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
                }
            }
        }
    }

}
