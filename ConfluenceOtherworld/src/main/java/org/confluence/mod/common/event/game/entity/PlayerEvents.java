package org.confluence.mod.common.event.game.entity;

import PortLib.extensions.net.minecraft.advancements.Advancement.PortAdvancementExtension;
import PortLib.extensions.net.minecraft.network.chat.MutableComponent.PortMutableComponentExtension;
import PortLib.extensions.net.minecraft.util.ParticleUtils.PortParticleUtilsExtension;
import PortLib.extensions.net.minecraft.world.item.Item.PortItemExtension;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.confluence.lib.api.event.CustomPickupRangeEvent;
import org.confluence.lib.api.event.PlayerNaturalHealEvent;
import org.confluence.lib.api.event.SwitchItemFunctionEvent;
import org.confluence.lib.common.event.LibGameEvents;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.event.AfterFlushArmorSetBonusEvent;
import org.confluence.mod.api.event.CustomMimicSummonKeyEvent;
import org.confluence.mod.api.event.GetArmorSetBonusDataEvent;
import org.confluence.mod.api.event.MinecartAbilityEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.*;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.saved.HardmodeConvertor;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.data.saved.Team;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.entity.minecart.BaseMinecartEntity;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.axe.LucyTheAxe;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.sword.StarSteelSword;
import org.confluence.mod.common.menu.FletchingTableMenu;
import org.confluence.mod.common.worldgen.secret_seed.BoulderWorld;
import org.confluence.mod.common.worldgen.secret_seed.NeverSleep;
import org.confluence.mod.common.worldgen.secret_seed.ReallySmall;
import org.confluence.mod.common.worldgen.secret_seed.TooEasy;
import org.confluence.mod.mixed.IAbstractMinecart;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.AchievementsDataSyncPacketS2C;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.PortEventPriority;
import org.mesdag.portlib.event.entity.player.*;
import org.mesdag.portlib.wrapper.advancements.PortAdvancementHolder;
import org.mesdag.portlib.wrapper.common.util.PortTriState;
import org.mesdag.portlib.wrapper.world.PortItemInteractionResult;

import java.util.Objects;

import static org.confluence.mod.api.event.MinecartAbilityEvent.RightClickRailBlock;

public final class PlayerEvents {
    public static void init() {
        PortEventHandler.addListener(PlayerEvents::loggedIn);
        PortEventHandler.addListener(PlayerEvents::loggedOut);
        PortEventHandler.addListener(PlayerEvents::interact$LeftClickBlock);
        PortEventHandler.addListener(PlayerEvents::interact$RightClickBlock);
        PortEventHandler.addListener(PlayerEvents::interact$EntityInteract);
        PortEventHandler.addListener(PlayerEvents::itemEntityPickup$Pre);
        PortEventHandler.addListener(PlayerEvents::itemEntityPickup$Post);
        PortEventHandler.addListener(PlayerEvents::itemFished);
        PortEventHandler.addListener(PlayerEvents::interact$RightClickItem);
        PortEventHandler.addListener(PlayerEvents::useItemOnBlock);
        PortEventHandler.addListener(PlayerEvents::attackEntity);
        PortEventHandler.addListener(PlayerEvents::respawn);
        PortEventHandler.addListener(PlayerEvents::harvestCheck);
        PortEventHandler.addListener(PlayerEvents::advancementEarn);
        PortEventHandler.addListener(PlayerEvents::advancementProgress);
        PortEventHandler.addListener(PlayerEvents::startTracking);
        PortEventHandler.addListener(PlayerEvents::changedDimension);
        PortEventHandler.addListener(PlayerEvents::container$Close);
        PortEventHandler.addListener(PlayerEvents::canSleep);
        PortEventHandler.addListener(PlayerEvents::canContinueSleeping);
        PortEventHandler.addListener(PlayerEvents::canSpawnPhantom);
        PortEventHandler.addListener(PlayerEvents::naturalHeal);
        PortEventHandler.addListener(PlayerEvents::onBonemeal);
        PortEventHandler.addListener(PlayerEvents::afterFlushArmorSetBonus);
        PortEventHandler.addListener(PlayerEvents::switchItemFunction$Post);
        PortEventHandler.addListener(PlayerEvents::rightClickRailBlock);
        PortEventHandler.addListener(PlayerEvents::dismountOnMinecart);
        PortEventHandler.addListener(PortEventPriority.HIGHEST, PlayerEvents::getArmorSetBonus);
        PortEventHandler.addListener(PlayerEvents::customPickupRange);
    }

    private static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerUtils.syncSavedData(player);
        BoulderWorld.forceSetAccessory(player);
        if (HardmodeConvertor.INSTANCE.isCompleted()) {
            AchievementUtils.awardAchievement(player, "its_hard");
        }
        if (CommonConfigs.DO_NPC_SPAWNING.get() && player.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            NPCSpawner.INSTANCE.trySpawnGuide(player);
        }
//        PlayerUtils.syncSoul2Client(player);
        PlayerUtils.syncPlayerData(player);

        if (ModUtils.shouldDisplayTeam()) {
            Team team = PlayerSpecialData.of(player).getTeam();
            if (team != Team.WHITE) {
                player.server.getPlayerList().broadcastSystemMessage(PortMutableComponentExtension.withColor(Component.translatable(
                        "message.confluence.join_team", player.getName(), team.getLowerCaseName()
                ), team.getColor().getTextColor()), false);
            }
        }

        if (ModSecretSeeds.REALLY_SMALL.match(player.server)) {
            ReallySmall.giveStepStool(player);
            ReallySmall.scalePlayer(player);
        }
        TooEasy.setToHardmode(player.server);

        PlayerUtils.askForSoftcore(player);
    }

    private static void loggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ChunkDropletsData.of(player.serverLevel()).getLastSync().remove(player.getUUID());
        GameEventSystem.INSTANCE.clearAll(player);
        PlayerSpecialData.of(player).setPvP(false);
        CommonConfigs.reset();
    }

    private static void interact$LeftClickBlock(PortPlayerInteractEvent.PortLeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (event.getAction() == PortPlayerInteractEvent.PortLeftClickBlock.PortAction.START) {
            AltarBlock.onLeftClick(level.getBlockState(pos), level, pos, event.getEntity());
        }
    }

    private static void interact$RightClickBlock(PortPlayerInteractEvent.PortRightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos blockPos = event.getPos();
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (!event.getUseBlock().isTrue() && block instanceof AltarBlock) {
            event.setUseBlock(PortTriState.TRUE);
        }

        if (player.isCrouching()) return;

        ItemStack itemStack = event.getItemStack();

        if (CommonConfigs.RIGHT_CLICK_RIDE_MINECART.get() &&
                !(itemStack.getItem() instanceof BlockItem) &&
                !itemStack.is(ModTags.Items.MINECART) &&
                block instanceof BaseRailBlock railBlock &&
                !player.isSpectator() &&
                !player.isPassenger()
        ) rightClickRideMinecart:{
            player.swing(InteractionHand.MAIN_HAND);
            event.setCanceled(true);
            if (level.isClientSide) break rightClickRideMinecart;
            ExtraInventory extraInventory = ExtraInventory.of(player);
            ItemStack minecartItemStack = extraInventory.getMinecart(false);
            RightClickRailBlock e = PortEventHandler.postEventWithReturn(new RightClickRailBlock(player, minecartItemStack, blockState, railBlock, blockPos));
            if (e.isCanceled()) break rightClickRideMinecart;
            AbstractMinecart minecart = e.getMinecart();
            if (minecart == null) break rightClickRideMinecart;
            extraInventory.setEquipment(ExtraInventory.MINECART_INDEX, ItemStack.EMPTY, false);
            level.addFreshEntity(minecart);
            player.startRiding(minecart, true);
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
            StaffOfRegrowth.dropAndPlaceOnRightClick(player, itemStack, blockPos);
        }

        DungeonCompass.matches(player, event.getHand(), level, itemStack, blockState, blockPos);

        if (player instanceof ServerPlayer serverPlayer) {
            if (NeverSleep.boom(serverPlayer.server, blockState, level, blockPos)) {
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    private static void interact$EntityInteract(PortPlayerInteractEvent.PortEntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer player &&
                event.getTarget() instanceof LivingEntity living
        ) healChocking:{
            if (!living.hasEffect(ModEffects.CHOKING.get())) break healChocking;
            ItemStack stack = player.getMainHandItem();
            if (!ModUtils.isWaterBottle(stack)) break healChocking;
            living.removeEffect(ModEffects.CHOKING.get());
            ItemStack emptyBottle = stack.is(PotionItems.BOTTLED_WATER.get())
                    ? PotionItems.BOTTLE.get().getDefaultInstance()
                    : Items.GLASS_BOTTLE.getDefaultInstance();
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemUtils.createFilledResult(stack, player, emptyBottle));
        }
    }

    private static void itemEntityPickup$Pre(PortItemEntityPickupEvent.PortPre event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();
        if (IServerPlayer.of(player).confluence$isCouldPickupItem()) {
            if (CommonConfigs.AUTO_STACK_GELS_COLOR.get()) autoStackGelsColor:{
                Item gel = MaterialItems.GEL.get();
                if (!itemStack.is(gel)) break autoStackGelsColor;
                int defaultMaxStackSize = PortItemExtension.getDefaultMaxStackSize(gel);
                for (ItemStack stack : player.getInventory().items) {
                    if (!stack.isEmpty() && stack.is(gel) && stack.getCount() + itemStack.getCount() <= defaultMaxStackSize) {
                        ColoredItem.setRGBA(itemStack, ColoredItem.getRGBA(stack));
                        break;
                    }
                }
            }
            if (itemEntity instanceof TreasureBagItemEntity entity) {
                if (!entity.isOwner(player)) event.setCanPickup(PortTriState.FALSE);
            }
        } else {
            event.setCanPickup(PortTriState.FALSE);
        }

        if (itemStack.is(ModTags.Items.PROVIDE_MANA)) {
            ManaStorage.of(player).receiveMana(() -> itemStack.getCount() * 100.0F);
            StarSteelSword.onManaStarPickup(player);
            itemEntity.discard();
            event.setCanPickup(PortTriState.FALSE);
        } else if (itemStack.is(ModTags.Items.PROVIDE_LIFE)) {
            player.heal(itemStack.getCount() * 4.0F);
            itemEntity.discard();
            event.setCanPickup(PortTriState.FALSE);
        }
    }

    private static void itemEntityPickup$Post(PortItemEntityPickupEvent.PortPost event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = event.getOriginalStack();
        CoinItem.onPickup(itemStack, itemEntity);
        LucyTheAxe.onPickup(player, itemStack);
    }

    private static void itemFished(PortItemFishedEvent event) {
        Player player = event.getEntity();

        if (!TCUtils.hasType(player, AccessoryItems.HIGH$TEST$FISHING$LINE) && player.getRandom().nextFloat() < 0.1429F) {
            player.level().playSound(null, event.getHookEntity().blockPosition(), ModSoundEvents.DECOUPLING.get(), SoundSource.AMBIENT);
            event.setCanceled(true);
        }
    }

    private static void interact$RightClickItem(PortPlayerInteractEvent.PortRightClickItem event) {
        Player player = event.getEntity();
        if (player.isSpectator()) return;
        ItemStack stack = event.getItemStack();

        if (stack.is(ModTags.Items.MANA_WEAPON) && player.hasEffect(ModEffects.SILENCED.get())) {
            event.setCanceled(true);
        } else if (!stack.isEmpty()) {
            if (player.hasEffect(ModEffects.STONED.get()) || player.hasEffect(ModEffects.FROZEN.get()) || player.hasEffect(ModEffects.CURSED.get())) {
                event.setCanceled(true);
            }
        }
    }

    private static void useItemOnBlock(PortUseItemOnBlockEvent event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getPlayer();
        if (player != null && stack.is(Items.GLASS_BOTTLE)) {
            Level level = event.getLevel();
            BlockHitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hitResult.getBlockPos();
                if (level.mayInteract(player, pos) && level.getFluidState(pos).is(ModFluids.HONEY.fluid().get())) {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    ItemUtils.createFilledResult(stack, player, Items.HONEY_BOTTLE.getDefaultInstance());
                    player.swing(event.getHand());
                    event.cancelWithResult(PortItemInteractionResult.sidedSuccess(level.isClientSide));
                }
            }
        }
    }

    private static void attackEntity(PortAttackEntityEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            AccessoryItems.applyLuckyCoin(serverPlayer, event.getTarget());
        }
        if (player.getMainHandItem().is(ModTags.Items.SPEAR)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    private static void clone(PlayerEvent.Clone event) {
        Player old = event.getOriginal();
        Player neo = event.getEntity();

        FlaskEffect.cloneFlaskEffects(old, neo);
    }

    private static void respawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        EverBeneficial everBeneficial = EverBeneficial.of(player);
        EverBeneficialItem.LIFE_CRYSTAL.recovery(everBeneficial, eb -> eb.getUsedLifeCrystals() > 0, player);
        EverBeneficialItem.LIFE_FRUITS.recovery(everBeneficial, eb -> eb.getUsedLifeFruits() > 0, player);
        EverBeneficialItem.AEGIS_APPLE.recovery(everBeneficial, EverBeneficial::isAegisAppleUsed, player);
        EverBeneficialItem.AMBROSIA.recovery(everBeneficial, EverBeneficial::isAmbrosiaUsed, player);
        EverBeneficialItem.GALAXY_PEARL.recovery(everBeneficial, EverBeneficial::isGalaxyPearlUsed, player);
        EverBeneficialItem.ARTISAN_LOAF.recovery(everBeneficial, EverBeneficial::isArtisanLoafUsed, player);

        BoulderWorld.forceSetAccessory(player);
        PlayerUtils.flushLocalData(player, player);
        PlayerUtils.syncPlayerData(player);
        if (ModSecretSeeds.REALLY_SMALL.match(player.server)) {
            ReallySmall.scalePlayer(player);
        }
    }

    private static void harvestCheck(PlayerEvent.HarvestCheck event) {
        ItemStack itemStack = event.getEntity().getMainHandItem();
        if (!itemStack.isEmpty() && itemStack.is(ItemTags.PICKAXES)) {
            event.setCanHarvest(ModTiers.isCorrectToolForDrops(DiggingPower.getPower(itemStack), itemStack, event.getTargetBlock()));
        }
    }

    private static void advancementEarn(PortAdvancementEvent.PortAdvancementEarnEvent event) {
        PortAdvancementHolder advancement = event.getAdvancement();
        ServerPlayer player = (ServerPlayer) event.getEntity();
        DisplayInfo display = advancement.value().getDisplay();
        if (display != null && !display.shouldAnnounceChat() && AchievementOffsetLoader.getDisplayOffset().containsKey(advancement.id())) {
            player.server.getPlayerList().broadcastSystemMessage(Component.translatable("chat.type.advancement.achievement", player.getDisplayName(), PortAdvancementExtension.name(advancement)), false);
        }
    }

    private static void advancementProgress(PortAdvancementEvent.PortAdvancementProgressEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!LibEntityUtils.isSingleplayerOwner(player) &&
                AchievementOffsetLoader.getDisplayOffset().containsKey(event.getAdvancement().id())
        ) {
            AchievementsDataSyncPacketS2C.sendToPlayer(player);
        }
    }

    private static void startTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer target) {
            ServerPlayer sendTo = (ServerPlayer) event.getEntity();
            PlayerUtils.flushLocalData(sendTo, target);
        } else if (event.getTarget() instanceof AbstractTerraNPC npc) {
            NPCSpawner.INSTANCE.applyBenedictions(npc);
        }
    }

    private static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerUtils.flushLocalData(player, player);
        PlayerUtils.syncPlayerData(player);
    }

    private static void container$Close(PortPlayerContainerEvent.PortClose event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        IMinecraftServer server = IMinecraftServer.of(player.server);
        if (!server.confluence$matchesSecretFlag(IWorldOptions.HARDMODE)) return;
        Level level = player.level();
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;
        if (event.getContainer() instanceof ChestMenu menu &&
                menu.getContainer() instanceof ChestBlockEntity blockEntity
        ) mimic:{
            ItemStack key = null;
            for (int i = 0; i < blockEntity.getContainerSize(); ++i) {
                ItemStack stack = blockEntity.getItem(i);
                if (stack.isEmpty()) continue;
                if (stack.is(ModTags.Items.MIMIC_SUMMON_KEY)) {
                    if (key != null) break mimic;
                    key = stack;
                } else {
                    break mimic;
                }
            }
            if (key == null) break mimic;
            if (key.is(ToolItems.KEY_OF_LIGHT.get())) {
                WoodenMimic mimic = TEMonsterEntities.HALLOWED_MIMIC.get().create(level);
                if (mimic != null) {
                    CustomMimicSummonKeyEvent.summon(mimic, blockEntity);
                }
            } else if (key.is(ToolItems.KEY_OF_NIGHT.get())) {
                boolean summonCorruption;
                if (server.confluence$matchesSecretFlag(IWorldOptions.DOUBLE_EVIL) && !server.confluence$equalsSecretFlag(IWorldOptions.DOUBLE_EVIL)) {
                    summonCorruption = server.confluence$matchesSecretFlag(IWorldOptions.THE_CORRUPTION);
                } else {
                    summonCorruption = (level.getGameTime() / 24000L) % 2 == 0;
                }
                WoodenMimic mimic;
                if (summonCorruption) {
                    mimic = TEMonsterEntities.CORRUPT_MIMIC.get().create(level);
                } else {
                    mimic = TEMonsterEntities.CRIMSON_MIMIC.get().create(level);
                }
                if (mimic != null) {
                    CustomMimicSummonKeyEvent.summon(mimic, blockEntity);
                }
            } else {
                PortEventHandler.postEvent(new CustomMimicSummonKeyEvent(player, key, menu, blockEntity));
            }
        }
    }

    private static void canSleep(PortCanPlayerSleepEvent event) {
        if (BloodMoonGameEvent.INSTANCE.started()) {
            event.setProblem(Player.BedSleepingProblem.NOT_SAFE);
        } else if (ModSecretSeeds.NEVER_SLEEP.match(event.getEntity().server)) {
            event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);
        }
    }

    private static void canContinueSleeping(PortCanContinueSleepingEvent event) {
        if (event.mayContinueSleeping() && BloodMoonGameEvent.INSTANCE.started()) {
            event.setContinueSleeping(false);
        }
    }

    private static void canSpawnPhantom(PortPlayerSpawnPhantomsEvent event) {
        if (ModSecretSeeds.NEVER_SLEEP.match(((ServerPlayer) event.getEntity()).server)) {
            event.setResult(PortPlayerSpawnPhantomsEvent.Result.ALLOW);
        }
    }

    /// 阻止自然回血的药水效果，已改为使用EffectCure，并提取到Lib了
    ///
    /// [LibGameEvents#playerNaturalHeal]
    private static void naturalHeal(PlayerNaturalHealEvent event) {
        if (PlayerUtils.skipHealIfOnFire(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static void onBonemeal(PortBonemealEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (!state.is(Blocks.END_STONE)) return;

        Block moonlitBlock = NatureBlocks.MOONLIT_GRASS_BLOCK.get();
        Block inverseBlock = NatureBlocks.INVERSE_GRASS_BLOCK.get();

        boolean canGrowMoonlit = false;
        boolean canGrowInverse = false;

        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            Block neighbor = level.getBlockState(nearbyPos).getBlock();
            if (neighbor == moonlitBlock) canGrowMoonlit = true;
            if (neighbor == inverseBlock) canGrowInverse = true;
            if (canGrowMoonlit && canGrowInverse) break;
        }

        boolean upSpace = level.getBlockState(pos.above()).propagatesSkylightDown(level, pos);
        boolean downSpace = level.getBlockState(pos.below()).propagatesSkylightDown(level, pos);

        BlockState result = null;

        if (canGrowMoonlit && canGrowInverse && upSpace && downSpace) {
            result = level.random.nextBoolean() ? moonlitBlock.defaultBlockState() : inverseBlock.defaultBlockState();
        } else if (canGrowMoonlit && upSpace) {
            result = moonlitBlock.defaultBlockState();
        } else if (canGrowInverse && downSpace) {
            result = inverseBlock.defaultBlockState();
        }

        if (result != null) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, result);
                level.levelEvent(1505, pos, 15);
                PortParticleUtilsExtension.spawnParticles(level, pos, 45, 3.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
            }
            event.setSuccessful(true);
        }
    }

    private static void afterFlushArmorSetBonus(AfterFlushArmorSetBonusEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerUtils.flushPrimitiveValueData(player);
        }
    }


    private static void switchItemFunction$Post(SwitchItemFunctionEvent.Post event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            VisibilityPacketS2C.sendEcho(serverPlayer);
        }
        PlayerSpecialData data = PlayerSpecialData.of(player);
        Item item = event.getStack().getItem();
        boolean c = item == ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.get();
        if (c || item == ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.get()) {
            data.setCouldHurtCritters(event.isEnabled());
        }
        if (c || item == ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.get()) {
            data.setCouldDamageEnvironment(event.isEnabled());
        }
    }

    private static void rightClickRailBlock(MinecartAbilityEvent.RightClickRailBlock event) {
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
            event.setMinecart(new BaseMinecartEntity(level, x, y, z, MinecartItems.Types.WOODEN));
        } else if (minecartItem.getItem() == Items.MINECART) {
            event.setMinecart(new BaseMinecartEntity(level, x, y, z, MinecartItems.Types.VANILLA));
        } else if (minecartItem.getItem() instanceof BaseMinecartItem baseMinecartItem) {
            event.setMinecart(Objects.requireNonNull(baseMinecartItem.createMinecart(level, x, y, z, AbstractMinecart.Type.RIDEABLE, minecartItem, event.getEntity())));
        }
    }

    private static void dismountOnMinecart(MinecartAbilityEvent.DismountOnMinecart event) {
        if (event.getMinecartItem() == null && event.getMinecart().getMinecartType() == AbstractMinecart.Type.RIDEABLE) {
            event.setMinecartItem(IAbstractMinecart.of(event.getMinecart()).confluence$getDropItem().getDefaultInstance());
        }
    }

    private static void getArmorSetBonus(GetArmorSetBonusDataEvent event) {
        ArmorSetBonusKey key = event.getKey();
        if (key.head().builtInRegistryHolder().is(ModTags.Items.ROBE)) {
            if (key.chest() == ArmorItems.WIZARD_HAT.get()) {
                event.setNeoData(ModArmorBonus.WIZARD_HAT_SET_BONUS);
            } else if (key.chest() == ArmorItems.MAGIC_HAT.get()) {
                event.setNeoData(ModArmorBonus.MAGIC_HAT_SET_BONUS);
            }
        }
    }

    private static void customPickupRange(CustomPickupRangeEvent event) {
        Player player = event.getEntity();
        event.addRange(PlayerUtils.MANA_RANGE, TCUtils.getValue(player, AccessoryItems.MANA$PICKUP$RANGE).getA(), stack -> stack.is(ModTags.Items.PROVIDE_MANA));
        event.addRange(PlayerUtils.COIN_RANGE, TCUtils.getValue(player, AccessoryItems.COIN$PICKUP$RANGE).getA(), stack -> stack.is(ModTags.Items.COINS));
        event.addRange(PlayerUtils.HEART_RANGE, ModEffects.getHeartReachRange(player), stack -> stack.is(ModTags.Items.PROVIDE_LIFE));
    }
}
