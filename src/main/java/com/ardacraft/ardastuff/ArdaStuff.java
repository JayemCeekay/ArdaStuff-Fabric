package com.ardacraft.ardastuff;

import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlotsUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.projectile.ProjectileHitEvent;
import xyz.nucleoid.stimuli.event.world.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ArdaStuff implements ModInitializer {

    public static ArrayList<ServerPlayerEntity> paintingBreakers;
    public static HashMap<ServerPlayerEntity, Long> playerTimeMap;
    public int ticks = 0;
    public static boolean disableWaterSpread = true;
    public static HashSet<ServerPlayerEntity> waterSpreaders;

    public static boolean eventBypass = false;

    @Override
    public void onInitialize() {
        waterSpreaders = new HashSet<>();
        paintingBreakers = new ArrayList<>();
        playerTimeMap = new HashMap<>();
/*
        Stimuli.global().listen(FluidFlowEvent.EVENT, (world, fluidPos, fluidBlock, flowDirection, flowTo, flowToBlock) -> {
            for (ServerPlayerEntity player : ArdaStuff.waterSpreaders) {
                LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getGameProfile().getName());
                try {
                    if (session != null && session.getSelection().contains(FabricAdapter.adapt(fluidPos))) {
                        return ActionResult.SUCCESS;
                    }
                } catch (IncompleteRegionException e) {
                    e.printStackTrace();
                }
            }
            return ActionResult.FAIL;
        });

*/
        Stimuli.global().listen(ProjectileHitEvent.ENTITY, (projectileEntity, hitResult) -> {
            if (eventBypass) {
                if (hitResult.getEntity().getType().getLootTableId().equals(new Identifier("minecraft:entities/painting"))) {
                    return ActionResult.FAIL;
                }
                if (hitResult.getEntity().getType().getLootTableId().equals(new Identifier("conquest:entities/painting"))) {
                    return ActionResult.FAIL;
                }
                if (hitResult.getEntity().getType().getLootTableId().equals(new Identifier("minecraft:entities/item_frame"))) {
                    return ActionResult.FAIL;
                }

                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        });

        Stimuli.global().listen(FireTickEvent.EVENT, (world, pos) -> {
            return ActionResult.FAIL;
        });

        Stimuli.global().listen(IceMeltEvent.EVENT, (world, pos) -> {
            return ActionResult.FAIL;
        });

        Stimuli.global().listen(WitherSummonEvent.EVENT, (world, pos) -> {
            return ActionResult.FAIL;
        });

        Stimuli.global().listen(SnowFallEvent.EVENT, (world, pos) -> {
            return ActionResult.FAIL;
        });

        Stimuli.global().listen(TntIgniteEvent.EVENT, (world, pos, entity) -> {
            return ActionResult.FAIL;
        });


        Stimuli.global().listen(ExplosionDetonatedEvent.EVENT, (explosion, particles) -> {
            return;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (environment.dedicated) {
                ArdaStuffCommandHandler.ArdaStuffCommands(dispatcher, registryAccess, environment);
            }
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (!LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.create").asBoolean() && Registry.BLOCK.getId(state.getBlock()).getNamespace().equalsIgnoreCase("create")) {
                    return false;
                }
            }
            return true;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (!LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.protection").asBoolean()) {
                    return false;
                }
            }
            return true;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player.getMainHandStack().getItem() instanceof net.minecraft.item.SpawnEggItem) {
                Log.info(LogCategory.LOG, "Spawn egg used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
                return ActionResult.FAIL;
            }

            if (!hasPermission(player, "metatweaks.protection")) {
                ActionResult fail = getUseBlockOrEntityResult(player, hand);
                if (fail != null) return fail;

            }

            if (Registry.ITEM.getId(player.getStackInHand(hand).getItem()).toString().startsWith("create:")) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    try {
                        if (!LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.create").asBoolean()) {
                            return ActionResult.FAIL;
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(player instanceof ServerPlayerEntity serverPlayer) {
                if (LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getPrimaryGroup().equalsIgnoreCase("default")) {
                    if (serverPlayer.getWorld().getRegistryKey() == PocketDimensionPlots.VOID) {
                        if (PocketDimensionPlotsUtils.getPlotFromCoordinates(serverPlayer.getBlockPos()).playerOwner == serverPlayer.getUuid()) {
                            return ActionResult.PASS;
                        }
                        return ActionResult.FAIL;
                    }
                }
            }

            return isBlockProtectedAgainstUseAction(player, world, hand, hitResult) ? ActionResult.FAIL : ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getMainHandStack().getItem() instanceof net.minecraft.item.SpawnEggItem) {
                Log.info(LogCategory.LOG, "Spawn egg used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
                return ActionResult.FAIL;
            }

            if (!hasPermission(player, "metatweaks.protection")) {

                ActionResult fail = getUseBlockOrEntityResult(player, hand);
                if (fail != null) return fail;

            }

            if (player instanceof ServerPlayerEntity serverPlayer) {
                try {
                    if (LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.protection").asBoolean()) {
                        //check if user is default rank (guest)
                        //check if use is in their plot, if not, fail
                        if (LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getPrimaryGroup().equalsIgnoreCase("default")) {
                            if (serverPlayer.getWorld().getRegistryKey() == PocketDimensionPlots.VOID) {
                                if (PocketDimensionPlotsUtils.getPlotFromCoordinates(serverPlayer.getBlockPos()).playerOwner == serverPlayer.getUuid()) {
                                    return ActionResult.PASS;
                                }
                                return ActionResult.FAIL;
                            }
                        }
                        return ActionResult.PASS;
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            return ActionResult.FAIL;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {

            if (Registry.ITEM.getId(player.getStackInHand(hand).getItem()).toString().startsWith("patchouli:guide_book")) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            if (player.getMainHandStack().getItem() instanceof net.minecraft.item.SpawnEggItem) {
                Log.info(LogCategory.LOG, "Spawn egg used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }

            if (!hasPermission(player, "metatweaks.protection")) {

                TypedActionResult<ItemStack> EMPTY = getUseItemResult(player, hand);
                if (EMPTY != null) return EMPTY;

            }

            if (Registry.ITEM.getId(player.getStackInHand(hand).getItem()).toString().startsWith("create:")) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    try {
                        if (!LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.create").asBoolean()) {
                            return TypedActionResult.fail(ItemStack.EMPTY);
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (player instanceof ServerPlayerEntity serverPlayer) {
                try {
                    if (LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.protection").asBoolean()) {
                        if (LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getPrimaryGroup().equalsIgnoreCase("default")) {
                            if (serverPlayer.getWorld().getRegistryKey() == PocketDimensionPlots.VOID) {
                                if (PocketDimensionPlotsUtils.getPlotFromCoordinates(serverPlayer.getBlockPos()).playerOwner == serverPlayer.getUuid()) {
                                    return TypedActionResult.pass(player.getMainHandStack());
                                }
                                return TypedActionResult.fail(player.getMainHandStack());
                            }
                        }
                        return TypedActionResult.pass(player.getMainHandStack());
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }


            return TypedActionResult.fail(ItemStack.EMPTY);
        });


        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            //if (Registry.ENTITY_TYPE.getId(entity.getType()).getPath().equalsIgnoreCase("painting") || Registry.ENTITY_TYPE.getId(entity.getType()).getPath().equalsIgnoreCase("item_frame")) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.guestPaintingBreaking").asBoolean()) {
                    if (PocketDimensionPlotsUtils.getPlotFromCoordinates(serverPlayer.getBlockPos()).playerOwner == serverPlayer.getUuid() && serverPlayer.getWorld().getRegistryKey() == PocketDimensionPlots.VOID) {
                        return ActionResult.PASS;
                    }
                }

                if (LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.paintingBreaking").asBoolean()) {
                    return ActionResult.PASS;
                }
                //    }
            }
            return ActionResult.FAIL;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                try {
                    if (!LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission("metatweaks.protection").asBoolean()) {
                        return ActionResult.FAIL;
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            return ActionResult.PASS;
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                try {
                    if (!LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(player).getCachedData().getPermissionData().checkPermission("metatweaks.hasJoined").asBoolean()) {
                        world.getServer().getPlayerManager().broadcast(Texts.setStyleIfAbsent(Text.literal("Welcome to ArdaCraft, " + player.getDisplayName().getString() + "! Please check out your guide book!"), Style.EMPTY.withColor(TextColor.parse("#416cba"))), false);
                        ItemStack guideBook = Registry.ITEM.get(new Identifier("patchouli", "guide_book")).getDefaultStack();
                        ItemStack pathfinder = Registry.ITEM.get(new Identifier("ardapaths", "path_revealer")).getDefaultStack();

                        if (guideBook == null || pathfinder == null) {
                            guideBook.getOrCreateNbt().putString("patchouli:book", "patchouli:ac_guide");
                            player.giveItemStack(pathfinder);
                            player.giveItemStack(guideBook);
                        }

                        LuckPermsProvider.get().getUserManager().modifyUser(player.getUuid(), user -> user.data().add(Node.builder("metatweaks.hasJoined").build()));
                        player.teleport(player.getWorld(), -1468, 25, -826, 0, -10);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                try {
                    String group = LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(player).getPrimaryGroup().toLowerCase();

                    var teamName = switch (group) {
                        case "admin" -> "Admin";
                        case "steward" -> "Steward";
                        case "developer" -> "Developer";
                        case "overseer" -> "Overseer";
                        case "landscaperplus" -> "Landscaper+";
                        case "landscaper" -> "Landscaper";
                        case "builderplus" -> "Builder+";
                        case "builder" -> "Builder";
                        case "community_manager" -> "Community_Manager";
                        case "apprentice" -> "Apprentice";
                        case "media_manager" -> "Media_Manager";
                        case "patron" -> "Patron";
                        default -> "Guest";
                    };

                    var team = player.getScoreboard().getTeam(teamName);

                    if (team != null) {
                        player.getScoreboard().addPlayerToTeam(player.getEntityName(), team);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Nullable
    private static ActionResult getUseBlockOrEntityResult(PlayerEntity player, Hand hand) {
        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.MinecartItem) {
            Log.info(LogCategory.LOG, "Minecart used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return ActionResult.FAIL;
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.BucketItem) {
            Log.info(LogCategory.LOG, "Bucket used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return ActionResult.FAIL;
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.FlintAndSteelItem) {
            Log.info(LogCategory.LOG, "Flint and Steel used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return ActionResult.FAIL;
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.TridentItem) {
            Log.info(LogCategory.LOG, "Trident used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return ActionResult.FAIL;
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.BoatItem) {
            Log.info(LogCategory.LOG, "Boat used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return ActionResult.FAIL;
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.EggItem) {
            Log.info(LogCategory.LOG, "Egg used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return ActionResult.FAIL;
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.MilkBucketItem) {
            Log.info(LogCategory.LOG, "Milk Bucket used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return ActionResult.FAIL;
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.ThrowablePotionItem) {
            Log.info(LogCategory.LOG, "Throwable Potion used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return ActionResult.FAIL;
        }
        return null;
    }

    @Nullable
    private static TypedActionResult<ItemStack> getUseItemResult(PlayerEntity player, Hand hand) {
        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.MinecartItem) {
            Log.info(LogCategory.LOG, "Minecart used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return TypedActionResult.fail(ItemStack.EMPTY);
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.BucketItem) {
            Log.info(LogCategory.LOG, "Bucket used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return TypedActionResult.fail(ItemStack.EMPTY);
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.FlintAndSteelItem) {
            Log.info(LogCategory.LOG, "Flint and Steel used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return TypedActionResult.fail(ItemStack.EMPTY);
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.TridentItem) {
            Log.info(LogCategory.LOG, "Trident used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return TypedActionResult.fail(ItemStack.EMPTY);
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.BoatItem) {
            Log.info(LogCategory.LOG, "Boat used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return TypedActionResult.fail(ItemStack.EMPTY);
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.EggItem) {
            Log.info(LogCategory.LOG, "Egg used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return TypedActionResult.fail(ItemStack.EMPTY);
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.MilkBucketItem) {
            Log.info(LogCategory.LOG, "Milk Bucket used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return TypedActionResult.fail(ItemStack.EMPTY);
        }

        if (player.getMainHandStack().getItem() instanceof net.minecraft.item.ThrowablePotionItem) {
            Log.info(LogCategory.LOG, "Throwable Potion used " + Registry.ITEM.getId(player.getStackInHand(hand).getItem()));
            return TypedActionResult.fail(ItemStack.EMPTY);
        }
        return null;
    }


    public boolean isHandEmpty(PlayerEntity player) {
        return player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty();
    }

    public boolean hasPermission(PlayerEntity player, String permission) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            try {
                if (LuckPermsProvider.get().getPlayerAdapter(ServerPlayerEntity.class).getUser(serverPlayer).getCachedData().getPermissionData().checkPermission(permission).asBoolean()) {
                    return true;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean isBlockProtectedAgainstUseAction(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {

        var blockName = Registry.BLOCK.getId(world.getBlockState(hitResult.getBlockPos()).getBlock()).toString().toLowerCase();

        if (isHandEmpty(player) && (blockName.endsWith("_door") || blockName.endsWith("_gate"))) return false;
        return !hasPermission(player, "metatweaks.protection");
    }

}
