package com.ardacraft.ardastuff;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class ArdaStuffCommandHandler {

    public static void ArdaStuffCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {

        FabricWorldEdit.inst.getPermissionsProvider().registerPermission("metatweaks.cwaterspread");
        FabricWorldEdit.inst.getPermissionsProvider().registerPermission("metatweaks.cpaintingbreaking");

        dispatcher.register(CommandManager.literal("guide").executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            ItemStack stack = Registry.ITEM.get(new Identifier("patchouli", "guide_book")).getDefaultStack();
            stack.getOrCreateNbt().putString("patchouli:book", "patchouli:ac_guide");
            player.giveItemStack(stack);
            return 0;
        }));

        //register cwaterspread
        dispatcher.register(CommandManager.literal("cwaterspread").requires(serverCommandSource -> {
                            try {
                                return FabricWorldEdit.inst.getPermissionsProvider().hasPermission(serverCommandSource.getPlayerOrThrow(), "metatweaks.cwaterspread");
                            } catch (CommandSyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            if (ArdaStuff.waterSpreaders.contains(source.getPlayer())) {
                                ArdaStuff.waterSpreaders.remove(source.getPlayer());
                                source.sendFeedback(Texts.setStyleIfAbsent(Text.literal("Water spread disabled."), Style.EMPTY.withFormatting(Formatting.RED)), false);
                            } else {
                                ArdaStuff.waterSpreaders.add(source.getPlayer());
                                source.sendFeedback(Texts.setStyleIfAbsent(Text.literal("Water spread enabled."), Style.EMPTY.withFormatting(Formatting.GREEN)), false);
                            }
                            return 1;
                        })
        );

        //register cpaintingbreaking
        dispatcher.register(CommandManager.literal("cpaintingbreaking").requires(serverCommandSource -> {
                            try {
                                return FabricWorldEdit.inst.getPermissionsProvider().hasPermission(serverCommandSource.getPlayerOrThrow(), "metatweaks.cpaintingbreaking");
                            } catch (CommandSyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            if (ArdaStuff.paintingBreakers.contains(source.getPlayer())) {
                                ArdaStuff.paintingBreakers.remove(source.getPlayer());
                                source.sendFeedback(Texts.setStyleIfAbsent(Text.literal("Painting breaking disabled."), Style.EMPTY.withFormatting(Formatting.RED)), false);
                            } else {
                                ArdaStuff.paintingBreakers.add(source.getPlayer());
                                source.sendFeedback(Texts.setStyleIfAbsent(Text.literal("Painting breaking enabled."), Style.EMPTY.withFormatting(Formatting.GREEN)), false);
                            }
                            return 1;
                        })
        );

        dispatcher.register(CommandManager.literal("sauronsays")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("message", StringArgumentType.greedyString()).executes(
                        context -> {
                            var text = Text.empty()
                                    .append(Text.literal("[SAURON] ").formatted(Formatting.DARK_RED))
                                    .append(Text.literal(StringArgumentType.getString(context, "message")).formatted(Formatting.RED));

                            context.getSource().getServer().getPlayerManager().broadcast(text, false);

                            return Command.SINGLE_SUCCESS;
                        }
                )));

        //register nightvision
        dispatcher.register(CommandManager.literal("nightvision").executes(context -> ArdaStuffCommandHandler.processNightvision(context.getSource())));
        dispatcher.register(CommandManager.literal("nv").executes(context -> ArdaStuffCommandHandler.processNightvision(context.getSource())));
    }

    private static int processNightvision(ServerCommandSource source)
    {
        if (!source.isExecutedByPlayer()) return 0;

        var player = source.getPlayer();
        if (player == null) return 0;

        var enabled = Text.empty()
                .append(Text.literal("ArdaStuff: ").formatted(Formatting.DARK_AQUA))
                .append(Text.literal("Night Vision Enabled").formatted(Formatting.GREEN));

        var disabled = Text.empty()
                .append(Text.literal("ArdaStuff: ").formatted(Formatting.DARK_AQUA))
                .append(Text.literal("Night Vision Disabled").formatted(Formatting.RED));

        var statusEffect = player.getStatusEffect(StatusEffects.NIGHT_VISION);

        if (statusEffect == null) player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false));
        else player.removeStatusEffect(StatusEffects.NIGHT_VISION);

        source.sendFeedback(statusEffect == null ? enabled : disabled, false);

        return Command.SINGLE_SUCCESS;
    }
}
