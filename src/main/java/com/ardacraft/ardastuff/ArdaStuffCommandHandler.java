package com.ardacraft.ardastuff;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class ArdaStuffCommandHandler {

    public static void ArdaStuffCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment)
    {

        FabricWorldEdit.inst.getPermissionsProvider().registerPermission("metatweaks.cwaterspread");
        FabricWorldEdit.inst.getPermissionsProvider().registerPermission("metatweaks.cpaintingbreaking");
        FabricWorldEdit.inst.getPermissionsProvider().registerPermission("metatweaks.askgandalf");

        /*
        dispatcher.register(CommandManager.literal("guide").executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            ItemStack stack = Registries.ITEM.get(new Identifier("patchouli", "guide_book")).getDefaultStack();
            stack.getOrCreateNbt().putString("patchouli:book", "patchouli:ac_guide");
            player.giveItemStack(stack);
            return 0;
        }));
*/
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
                                source.sendMessage(Texts.setStyleIfAbsent(Text.literal("Water spread disabled."), Style.EMPTY.withFormatting(Formatting.RED)));
                            } else {
                                ArdaStuff.waterSpreaders.add(source.getPlayer());
                                source.sendMessage(Texts.setStyleIfAbsent(Text.literal("Water spread enabled."), Style.EMPTY.withFormatting(Formatting.GREEN)));
                            }
                            return 1;
                        })
        );

        //register cpaintingbreaking
       /* dispatcher.register(CommandManager.literal("cpaintingbreaking").requires(serverCommandSource -> {
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
        );*/
/*
        dispatcher.register(CommandManager.literal("askGandalf").requires(serverCommandSource -> {
            try {
                return FabricWorldEdit.inst.getPermissionsProvider().hasPermission(serverCommandSource.getPlayerOrThrow(), "metatweaks.askgandalf");
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        } ).then(CommandManager.argument("message", StringArgumentType.greedyString()).executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayer();
            String message = StringArgumentType.getString(context, "message");
            context.getSource().getServer().getPlayerManager().broadcast(Text.of(context.getSource().getName() + ": " + message), false);
            String response = null;
            try {
                response = ChatGPT.sendPromptToChatGPT("gandalf", message, "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");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            context.getSource().getServer().getPlayerManager().broadcast(Text.empty().append(Text.literal("[GANDALF] ").formatted(Formatting.GOLD)).append(Text.literal(response)), false);
            return Command.SINGLE_SUCCESS;
        })));

        dispatcher.register(CommandManager.literal("askSauron").requires(serverCommandSource -> {
            try {
                return FabricWorldEdit.inst.getPermissionsProvider().hasPermission(serverCommandSource.getPlayerOrThrow(), "metatweaks.askgandalf");
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        } ).then(CommandManager.argument("message", StringArgumentType.greedyString()).executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayer();
            String message = StringArgumentType.getString(context, "message");
            context.getSource().getServer().getPlayerManager().broadcast(Text.of(context.getSource().getName() + ": " + message), false);
            String response = null;
            try {
                response = ChatGPT.sendPromptToChatGPT("sauron", message, "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");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            context.getSource().getServer().getPlayerManager().broadcast(Text.empty().append(Text.literal("[SAURON] ").formatted(Formatting.DARK_RED)).append(Text.literal(response)), false);
            return Command.SINGLE_SUCCESS;
        })));

        dispatcher.register(CommandManager.literal("askGollum").requires(serverCommandSource -> {
            try {
                return FabricWorldEdit.inst.getPermissionsProvider().hasPermission(serverCommandSource.getPlayerOrThrow(), "metatweaks.askgandalf");
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        } ).then(CommandManager.argument("message", StringArgumentType.greedyString()).executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayer();
            String message = StringArgumentType.getString(context, "message");
            context.getSource().getServer().getPlayerManager().broadcast(Text.of(context.getSource().getName() + ": " + message), false);
            String response = null;
            try {
                response = ChatGPT.sendPromptToChatGPT("gollum", message, "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");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            context.getSource().getServer().getPlayerManager().broadcast(Text.empty().append(Text.literal("[GOLLUM] ").formatted(Formatting.YELLOW)).append(Text.literal(response)), false);
            return Command.SINGLE_SUCCESS;
        })));
*/
        /*
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
*/
        //register nightvision
        dispatcher.register(CommandManager.literal("nightvision").executes(context -> ArdaStuffCommandHandler.processNightvision(context.getSource())));
        dispatcher.register(CommandManager.literal("nv").executes(context -> ArdaStuffCommandHandler.processNightvision(context.getSource())));

        dispatcher.register(CommandManager.literal("mount").executes(context -> {
            if (!context.getSource().isExecutedByPlayer()) return Command.SINGLE_SUCCESS;

            var player = context.getSource().getPlayer();
            if (player == null) return Command.SINGLE_SUCCESS;

            var horse = new HorseEntity(EntityType.HORSE, context.getSource().getWorld());
            horse.setTame(true);
            horse.setOwnerUuid(player.getUuid());
            horse.saddle(null);
            horse.setCustomName(Text.literal("deleteme"));
            horse.setCustomNameVisible(false);

            Random random = player.getWorld().getRandom();
            var horseColor = Util.getRandom(HorseColor.values(), random);
            var horseMarkings = Util.getRandom(HorseMarking.values(), random);

            horse.setVariant(horseColor);

            var position = player.getPos();
            horse.teleport(position.x, position.y, position.z);

            player.getWorld().spawnEntity(horse);

            player.startRiding(horse, true);

            return Command.SINGLE_SUCCESS;
        }));

        dispatcher.register(CommandManager.literal("boat").executes(context -> {
            if (!context.getSource().isExecutedByPlayer()) return Command.SINGLE_SUCCESS;

            var player = context.getSource().getPlayer();
            if (player == null) return Command.SINGLE_SUCCESS;
            var position = player.getPos();

            var boat = new BoatEntity(context.getSource().getWorld(), position.x, position.y, position.z);
            boat.setCustomName(Text.literal("deleteme"));

            boat.setYaw(player.getYaw());

            player.getWorld().spawnEntity(boat);

            player.startRiding(boat, true);

            return Command.SINGLE_SUCCESS;
        }));

    }

    private static int processNightvision(ServerCommandSource source) {
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

        if (statusEffect == null)
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false));
        else player.removeStatusEffect(StatusEffects.NIGHT_VISION);

        source.sendMessage(statusEffect == null ? enabled : disabled);

        return Command.SINGLE_SUCCESS;
    }
}
