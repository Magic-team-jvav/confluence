package org.confluence.mod.common.event.game.entity;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.*;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.*;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.saved.HardmodeConvertor;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.entity.minecart.BaseMinecartEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.common.BaseAxeItem;
import org.confluence.mod.common.item.common.BaseMinecartItem;
import org.confluence.mod.common.item.common.DungeonCompass;
import org.confluence.mod.common.item.common.EverBeneficialItem;
import org.confluence.mod.common.menu.FletchingTableMenu;
import org.confluence.mod.common.worldgen.secret_seed.BoulderWorld;
import org.confluence.mod.mixed.IAbstractMinecart;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.network.s2c.*;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

import java.util.Objects;

import static org.confluence.mod.api.event.MinecartAbilityEvent.DismountOnMinecart;
import static org.confluence.mod.api.event.MinecartAbilityEvent.RightClickRailBlock;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class PlayerEvents {
    @SubscribeEvent
    public static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerUtils.syncMana2Client(player);
        PlayerUtils.syncSavedData(player);
        ExtraInventorySyncPacketS2C.sendToClient(player, player, ExtraInventory.of(player));
        PiggyBankTotalMoneyPacket.sendToClient(player, PlayerPiggyBankContainer.of(player), true);
        FishingPowerInfoPacketS2C.sendAndGet(player);
        VisibilityPacketS2C.sendEcho(player);
        BoulderWorld.forceSetAccessory(player);
        VisibilityPacketS2C.sendTheConstantPostEffect(player);
        SecretFlagSyncPacketS2C.sendToAll(IMinecraftServer.of(player.server).confluence$getSecretFlag());
        if (HardmodeConvertor.INSTANCE.isCompleted()) {
            AchievementUtils.awardAchievement(player, "its_hard");
        }
        if (CommonConfigs.DO_NPC_SPAWNING.get() && player.serverLevel().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            NPCSpawner.INSTANCE.trySpawnGuide(player);
        }
        CompatibilitySyncPacketS2c.sendToAll();
    }

    @SubscribeEvent
    public static void loggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ChunkDropletsData.of(player.serverLevel()).getLastSync().remove(player.getUUID());
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        AltarBlock.onLeftClick(level.getBlockState(pos), level, pos, event.getEntity());
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (player.isCrouching()) return;
        BlockPos blockPos = event.getPos();
        BlockState blockState = level.getBlockState(blockPos);
        ItemStack itemStack = event.getItemStack();
        Block block = blockState.getBlock();

        if (CommonConfigs.RIGHT_CLICK_RIDE_MINECART.get() &&
                !(itemStack.getItem() instanceof BlockItem) &&
                !itemStack.is(ModTags.Items.MINECART) &&
                block instanceof BaseRailBlock railBlock &&
                !player.isSpectator() &&
                !player.isPassenger()
        ) {
            player.swing(InteractionHand.MAIN_HAND);
            if (!level.isClientSide) {
                ExtraInventory extraInventory = ExtraInventory.of(player);
                ItemStack minecartItemStack = extraInventory.getMinecart(false);
                RightClickRailBlock e = NeoForge.EVENT_BUS.post(new RightClickRailBlock(player, minecartItemStack, blockState, railBlock, blockPos));
                if (e.isCanceled()) return;
                AbstractMinecart minecart = e.getMinecart();
                if (minecart != null) {
                    extraInventory.setEquipment(ExtraInventory.MINECART_INDEX, ItemStack.EMPTY, false);
                    level.addFreshEntity(minecart);
                    player.startRiding(minecart, true);
                }
            }
            event.setCanceled(true);
        }

        if (CommonConfigs.FLETCHING_MENU.get() && blockState.is(Blocks.FLETCHING_TABLE)) {
            if (!level.isClientSide) {
                player.openMenu(new FletchingTableMenu.Provider(level, blockPos));
            }
            player.swing(InteractionHand.MAIN_HAND);
            event.setCanceled(true);
        }

        // 再生之斧/再生法杖 右键自动收获
        if (!level.isClientSide && itemStack.is(ModTags.Items.CROP_FORTUNE)) {
            BaseAxeItem.dropAndPlaceOnRightClick(player, itemStack, blockPos);
        }

        DungeonCompass.matches(player, event.getHand(), level, itemStack, blockState, blockPos);
    }

    @SubscribeEvent
    public static void playerInteract$EntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getTarget() instanceof LivingEntity targetEntity) {
            ItemStack item = serverPlayer.getMainHandItem();
            boolean isWaterBottle = ModUtils.isWaterBottle(item);
            if (isWaterBottle && targetEntity.hasEffect(ModEffects.CHOKING)) {
                targetEntity.removeEffect(ModEffects.CHOKING);
                ItemStack emptyBottle = item.is(PotionItems.BOTTLED_WATER) ? PotionItems.BOTTLE.toStack() : Items.GLASS_BOTTLE.getDefaultInstance();
                if (!serverPlayer.hasInfiniteMaterials()) {
                    serverPlayer.getInventory().add(emptyBottle);
                    item.shrink(1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void itemEntityPickup$Pre(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();
        Player player = event.getPlayer();
        if (itemStack.is(ModTags.Items.PROVIDE_MANA)) {
            ManaStorage.of(player).receiveMana(() -> itemStack.getCount() * 100.0F);
            itemEntity.discard();
            event.setCanPickup(TriState.FALSE);
        } else if (itemStack.is(ModTags.Items.PROVIDE_LIFE)) {
            player.heal(itemStack.getCount() * 4.0F);
            itemEntity.discard();
            event.setCanPickup(TriState.FALSE);
        } else if (itemEntity instanceof TreasureBagItemEntity entity) {
            if (!entity.isOwner(player)) event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void itemEntityPickup$Post(ItemEntityPickupEvent.Post event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = event.getOriginalStack();
        if (itemStack.is(ModTags.Items.COINS)) {
            if (itemStack.is(ModItems.COPPER_COIN)) {
                itemEntity.playSound(ModSoundEvents.COINS_SMALL.get());
            } else if (itemStack.is(ModItems.SILVER_COIN)) {
                itemEntity.playSound(ModSoundEvents.COINS_MEDIUM.get());
            } else {
                itemEntity.playSound(ModSoundEvents.COINS_LARGE.get());
            }
        }
    }

    @SubscribeEvent
    public static void itemFished(ItemFishedEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!TCUtils.hasAccessoriesType(player, AccessoryItems.HIGH$TEST$FISHING$LINE) && player.getRandom().nextFloat() < 0.1429F) {
            level.playSound(null, event.getHookEntity().blockPosition(), ModSoundEvents.DECOUPLING.get(), SoundSource.AMBIENT);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!player.isSpectator()) {
            ItemStack itemStack = event.getItemStack();
            if (itemStack.is(ModTags.Items.MANA_WEAPON) && player.hasEffect(ModEffects.SILENCED)) {
                event.setCanceled(true);
            } else if (!itemStack.isEmpty()) {
                if (player.hasEffect(ModEffects.STONED) || player.hasEffect(ModEffects.FROZEN) || player.hasEffect(ModEffects.CURSED)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void attackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            Entity target = event.getTarget();
            AccessoryItems.applyLuckyCoin(serverPlayer, target);
        }
        if (player.getMainHandItem().is(ModTags.Items.LANCES)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void rightClickRailBlock(RightClickRailBlock event) {
        AbstractMinecart minecart = event.getMinecart();
        if (minecart != null) return;

        ServerLevel level = (ServerLevel) event.getEntity().level();
        BlockPos blockPos = event.getBlockPos();
        boolean ascending = event.getRailBlock().getRailDirection(event.getBlockState(), level, blockPos, null).isAscending();
        double x = blockPos.getX() + 0.5;
        double y = blockPos.getY() + 0.0625 + (ascending ? 0.5 : 0.0);
        double z = blockPos.getZ() + 0.5;
        ItemStack minecartItem = event.getMinecartItem();

        if (minecartItem.isEmpty()) {
            BaseMinecartEntity baseMinecart = new BaseMinecartEntity(level, x, y, z, MinecartItems.Types.WOODEN);
            event.setMinecart(baseMinecart);
        } else if (minecartItem.getItem() == Items.MINECART) {
            event.setMinecart(new Minecart(level, x, y, z));
        } else if (minecartItem.getItem() instanceof BaseMinecartItem baseMinecartItem) {
            event.setMinecart(Objects.requireNonNull(baseMinecartItem.createMinecart(level, x, y, z, AbstractMinecart.Type.RIDEABLE, minecartItem, event.getEntity())));
        }
    }

    @SubscribeEvent
    public static void dismountOnMinecart(DismountOnMinecart event) {
        if (event.getMinecartItem() == null && event.getMinecart().getMinecartType() == AbstractMinecart.Type.RIDEABLE) {
            event.setMinecartItem(IAbstractMinecart.of(event.getMinecart()).confluence$getDropItem().getDefaultInstance());
        }
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        Player old = event.getOriginal();
        Player neo = event.getEntity();

        for (MobEffectInstance activeEffect : old.getActiveEffects()) {
            neo.forceAddEffect(activeEffect, null);
        }
    }

    @SubscribeEvent
    public static void respawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        EverBeneficial everBeneficial = EverBeneficial.of(player);
        EverBeneficialItem.LIFE_CRYSTAL.recovery(everBeneficial, eb -> eb.getUsedLifeCrystals() > 0, player);
        EverBeneficialItem.LIFE_FRUITS.recovery(everBeneficial, eb -> eb.getUsedLifeFruits() > 0, player);
        EverBeneficialItem.AEGIS_APPLE.recovery(everBeneficial, EverBeneficial::isAegisAppleUsed, player);
        EverBeneficialItem.AMBROSIA.recovery(everBeneficial, EverBeneficial::isAmbrosiaUsed, player);
        EverBeneficialItem.GALAXY_PEARL.recovery(everBeneficial, EverBeneficial::isGalaxyPearlUsed, player);
        EverBeneficialItem.ARTISAN_LOAF.recovery(everBeneficial, EverBeneficial::isArtisanLoafUsed, player);

        BoulderWorld.forceSetAccessory(player);
        ExtraInventorySyncPacketS2C.sendToClient(player, player, ExtraInventory.of(player));
    }

    @SubscribeEvent
    public static void harvestCheck(PlayerEvent.HarvestCheck event) {
        ItemStack itemStack = event.getEntity().getMainHandItem();
        if (!itemStack.isEmpty() && itemStack.is(ItemTags.PICKAXES)) {
            event.setCanHarvest(ModTiers.isCorrectToolForDrops(DiggingPower.getPower(itemStack), itemStack, event.getTargetBlock()));
        }
    }

    @SubscribeEvent
    public static void itemPickup(ItemEntityPickupEvent.Pre event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        if (IServerPlayer.of(player).confluence$isCouldPickupItem()) {
            if (CommonConfigs.AUTO_STACK_GELS_COLOR.get()) {
                ItemStack itemStack = event.getItemEntity().getItem();
                Item gel = MaterialItems.GEL.get();
                if (itemStack.is(gel)) {
                    int defaultMaxStackSize = gel.getDefaultMaxStackSize();
                    for (ItemStack stack : player.getInventory().items) {
                        if (!stack.isEmpty() && stack.is(gel) && stack.getCount() + itemStack.getCount() <= defaultMaxStackSize) {
                            ColoredItem.setColor(itemStack, ColoredItem.getColor(stack));
                            break;
                        }
                    }
                }
            }
        } else {
            event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void advancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        AdvancementHolder advancement = event.getAdvancement();
        if (advancement.value().display().map(DisplayInfo::shouldAnnounceChat).orElse(false) && AchievementOffsetLoader.getDisplayOffset().containsKey(advancement.id())) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            player.server.getPlayerList().broadcastSystemMessage(Component.translatable("chat.type.advancement.achievement", player.getDisplayName(), Advancement.name(advancement)), false);
        }
    }

    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer player) {
            ExtraInventorySyncPacketS2C.sendToClient((ServerPlayer) event.getEntity(), player, ExtraInventory.of(player));
        } else if (event.getTarget() instanceof AbstractTerraNPC npc) {
            NPCSpawner.INSTANCE.applyBenedictions(npc);
        }
    }

    @SubscribeEvent
    public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ExtraInventorySyncPacketS2C.sendToClient(player, player, ExtraInventory.of(player));
    }
}
