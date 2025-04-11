package com.ardacraft.ardastuff;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.projectile.ProjectileHitEvent;
import xyz.nucleoid.stimuli.event.world.*;

import java.util.ArrayList;
import java.util.HashSet;

public class ArdaStuff implements ModInitializer {

    public static ArrayList<ServerPlayerEntity> paintingBreakers;
    public static boolean disableWaterSpread = true;
    public static HashSet<ServerPlayerEntity> waterSpreaders;
    public static boolean eventBypass = false;

    public ArrayList<Identifier> allowedCreateBlocks;

    @Override
    public void onInitialize() {
        waterSpreaders = new HashSet<>();
        paintingBreakers = new ArrayList<>();

        //initialize create block whitelist
        allowedCreateBlocks = new ArrayList<>();
        allowedCreateBlocks.add(new Identifier("create:warped_window_pane"));
        allowedCreateBlocks.add(new Identifier("create:crimson_window_pane"));
        allowedCreateBlocks.add(new Identifier("create:brown_valve_handle"));
        allowedCreateBlocks.add(new Identifier("create:turntable"));
        allowedCreateBlocks.add(new Identifier("create:black_seat"));
        allowedCreateBlocks.add(new Identifier("create:white_seat"));
        allowedCreateBlocks.add(new Identifier("create:rose_quartz_tiles"));
        allowedCreateBlocks.add(new Identifier("create:brass_block"));
        allowedCreateBlocks.add(new Identifier("create:small_rose_quartz_tiles"));
        allowedCreateBlocks.add(new Identifier("create:chute"));
        allowedCreateBlocks.add(new Identifier("create:framed_glass_trapdoor"));
        allowedCreateBlocks.add(new Identifier("create:schematic_table"));
        allowedCreateBlocks.add(new Identifier("create:train_door"));
        allowedCreateBlocks.add(new Identifier("create:yellow_valve_handle"));
        allowedCreateBlocks.add(new Identifier("create:red_valve_handle"));
        allowedCreateBlocks.add(new Identifier("create:gray_valve_handle"));
        allowedCreateBlocks.add(new Identifier("create:schematicannon"));


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

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player) {
                if (hasPermission(player, "metatweaks.candie")) {
                    return true;
                }
                return false;
            }
            return true;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (Registries.BLOCK.getId(state.getBlock()).getNamespace().equalsIgnoreCase("create")) {
                    if (!hasPermission(serverPlayer, "metatweaks.create")) {
                        return false;
                    } else if (!hasPermission(serverPlayer, "metatweaks.createAll")) {
                        if (!allowedCreateBlocks.contains(Registries.BLOCK.getId(state.getBlock()))) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
            return true;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (!hasPermission(serverPlayer, "metatweaks.protection")) {
                    return false;
                }
            }
            return true;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player.getMainHandStack().getItem() instanceof SpawnEggItem) {
                Log.info(LogCategory.LOG, "Spawn egg used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                return ActionResult.FAIL;
            }


                if (player.getMainHandStack().getItem() instanceof MinecartItem) {
                    Log.info(LogCategory.LOG, "Minecart used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof BucketItem) {
                    Log.info(LogCategory.LOG, "Bucket used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof FlintAndSteelItem) {
                    Log.info(LogCategory.LOG, "Flint and Steel used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof TridentItem) {
                    Log.info(LogCategory.LOG, "Trident used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof BoatItem) {
                    Log.info(LogCategory.LOG, "Boat used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof EggItem) {
                    Log.info(LogCategory.LOG, "Egg used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof MilkBucketItem) {
                    Log.info(LogCategory.LOG, "Milk Bucket used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof ThrowablePotionItem) {
                    Log.info(LogCategory.LOG, "Throwable Potion used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()) + " by " + player.getName().getString());
                    return ActionResult.FAIL;
                }
            if (!hasPermission(player, "metatweaks.protection")) {
                return ActionResult.FAIL;
            }

            if (Registries.ITEM.getId(player.getStackInHand(hand).getItem()).toString().startsWith("create:")) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    try {
                        if (hasPermission(serverPlayer, "metatweaks.protection")) {
                            if (!hasPermission(serverPlayer, "metatweaks.create")) {
                                return ActionResult.FAIL;
                            } else if (!hasPermission(serverPlayer, "metatweaks.createAll")) {
                                if (!allowedCreateBlocks.contains(Registries.ITEM.getId(serverPlayer.getStackInHand(hand).getItem()))) {
                                    return ActionResult.FAIL;
                                } else {
                                    return ActionResult.PASS;
                                }
                            } else {
                                return ActionResult.PASS;
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }

            return isBlockProtectedAgainstUseAction(player, world, hand, hitResult) ? ActionResult.FAIL : ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getMainHandStack().getItem() instanceof SpawnEggItem) {
                Log.info(LogCategory.LOG, "Spawn egg used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return ActionResult.FAIL;
            }



                if (player.getMainHandStack().getItem() instanceof MinecartItem) {
                    Log.info(LogCategory.LOG, "Minecart used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof BucketItem) {
                    Log.info(LogCategory.LOG, "Bucket used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof FlintAndSteelItem) {
                    Log.info(LogCategory.LOG, "Flint and Steel used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof TridentItem) {
                    Log.info(LogCategory.LOG, "Trident used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof BoatItem) {
                    Log.info(LogCategory.LOG, "Boat used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof EggItem) {
                    Log.info(LogCategory.LOG, "Egg used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof MilkBucketItem) {
                    Log.info(LogCategory.LOG, "Milk Bucket used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                    return ActionResult.FAIL;
                }

                if (player.getMainHandStack().getItem() instanceof ThrowablePotionItem) {
                    Log.info(LogCategory.LOG, "Throwable Potion used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                    return ActionResult.FAIL;
                }
            if (!hasPermission(player, "metatweaks.protection")) {
                return ActionResult.FAIL;
            }

            if (Registries.ITEM.getId(player.getStackInHand(hand).getItem()).toString().startsWith("create:")) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    try {
                        if (hasPermission(serverPlayer, "metatweaks.protection")) {
                            if (!hasPermission(serverPlayer, "metatweaks.create")) {
                                return ActionResult.FAIL;
                            } else if (!hasPermission(serverPlayer, "metatweaks.createAll")) {
                                if (!allowedCreateBlocks.contains(Registries.ITEM.getId(serverPlayer.getStackInHand(hand).getItem()))) {
                                    return ActionResult.FAIL;
                                } else {
                                    return ActionResult.PASS;
                                }
                            } else {
                                return ActionResult.PASS;
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (player instanceof ServerPlayerEntity serverPlayer) {
                try {
                    if (hasPermission(serverPlayer, "metatweaks.protection")) {
                        return ActionResult.PASS;
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            return ActionResult.FAIL;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {

            if (Registries.ITEM.getId(player.getStackInHand(hand).getItem()).toString().startsWith("patchouli:guide_book")) {
                return TypedActionResult.pass(player.getStackInHand(hand));
            }

            if (player.getMainHandStack().getItem() instanceof SpawnEggItem) {
                Log.info(LogCategory.LOG, "Spawn egg used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }


            if (player.getMainHandStack().getItem() instanceof MinecartItem) {
                Log.info(LogCategory.LOG, "Minecart used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }

            if (player.getMainHandStack().getItem() instanceof BucketItem) {
                Log.info(LogCategory.LOG, "Bucket used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }


            if (player.getMainHandStack().getItem() instanceof FlintAndSteelItem) {
                Log.info(LogCategory.LOG, "Flint and Steel used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }

            if (player.getMainHandStack().getItem() instanceof TridentItem) {
                Log.info(LogCategory.LOG, "Trident used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }

            if (player.getMainHandStack().getItem() instanceof BoatItem) {
                Log.info(LogCategory.LOG, "Boat used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }

            if (player.getMainHandStack().getItem() instanceof EggItem) {
                Log.info(LogCategory.LOG, "Egg used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }

            if (player.getMainHandStack().getItem() instanceof MilkBucketItem) {
                Log.info(LogCategory.LOG, "Milk Bucket used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }

            if (player.getMainHandStack().getItem() instanceof ThrowablePotionItem) {
                Log.info(LogCategory.LOG, "Throwable Potion used " + Registries.ITEM.getId(player.getStackInHand(hand).getItem()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }
            if (!hasPermission(player, "metatweaks.protection")) {

                return TypedActionResult.pass(ItemStack.EMPTY);
            }

            if (Registries.ITEM.getId(player.getStackInHand(hand).getItem()).toString().startsWith("create:")) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    try {
                        if (hasPermission(serverPlayer, "metatweaks.protection")) {
                            if (!hasPermission(serverPlayer, "metatweaks.create")) {
                                return TypedActionResult.fail(ItemStack.EMPTY);
                            } else if (!hasPermission(serverPlayer, "metatweaks.createAll")) {
                                if (!allowedCreateBlocks.contains(Registries.ITEM.getId(serverPlayer.getStackInHand(hand).getItem()))) {
                                    return TypedActionResult.fail(ItemStack.EMPTY);
                                } else {
                                    return TypedActionResult.pass(serverPlayer.getStackInHand(hand));
                                }
                            } else {
                                return TypedActionResult.pass(serverPlayer.getStackInHand(hand));
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }

            return TypedActionResult.pass(ItemStack.EMPTY);
        });

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

        var blockName = Registries.BLOCK.getId(world.getBlockState(hitResult.getBlockPos()).getBlock()).toString().toLowerCase();

        if (isHandEmpty(player) && (blockName.endsWith("_door") || blockName.endsWith("_gate"))) return false;
        return !hasPermission(player, "metatweaks.protection");
    }
}
