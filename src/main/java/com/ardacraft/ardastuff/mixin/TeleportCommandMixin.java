package com.ardacraft.ardastuff.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.command.TeleportCommand")
public class TeleportCommandMixin {

    @Inject(method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;register(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;", ordinal = 1), cancellable = true)
    private static void doNotRegister(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {
        System.out.println("Not registering vanilla /tp alias!");
        ci.cancel();
    }

}
