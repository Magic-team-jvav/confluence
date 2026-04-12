package org.confluence.mod.common.event.game.entity;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.*;
import org.confluence.lib.common.event.LibGameEvents;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.lib.event.CustomPickupRangeEvent;
import org.confluence.lib.event.PlayerNaturalHealEvent;
import org.confluence.lib.event.SwitchItemFunctionEvent;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.*;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.*;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.saved.HardmodeConvertor;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.data.saved.Team;
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
import org.confluence.mod.common.menu.FletchingTableMenu;
import org.confluence.mod.common.worldgen.secret_seed.BoulderWorld;
import org.confluence.mod.common.worldgen.secret_seed.NeverSleep;
import org.confluence.mod.common.worldgen.secret_seed.ReallySmall;
import org.confluence.mod.common.worldgen.secret_seed.TooEasy;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
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
import org.confluence.terraentity.entity.monster.WoodenMimic;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.Objects;

import static org.confluence.mod.api.event.MinecartAbilityEvent.RightClickRailBlock;

@EventBusSubscriber(modid = Confluence.MODID)
public final class PlayerEvents {
    @SubscribeEvent
    public static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
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
                player.server.getPlayerList().broadcastSystemMessage(Component.translatable(
                        "message.confluence.join_team", player.getName(), team.getLowerCaseName()
                ).withColor(team.getColor().getTextColor()), false);
            }
        }

        if (ModSecretSeeds.REALLY_SMALL.match(player.server)) {
            ReallySmall.giveStepStool(player);
            ReallySmall.scalePlayer(player);
        }
        TooEasy.setToHardmode(player.server);

        PlayerUtils.askForSoftcore(player);
    }

    @SubscribeEvent
    public static void loggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ChunkDropletsData.of(player.serverLevel()).getLastSync().remove(player.getUUID());
        GameEventSystem.INSTANCE.clearAll(player);
        PlayerSpecialData.of(player).setPvP(false);
        CommonConfigs.reset();
    }

    @SubscribeEvent
    public static void interact$LeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            AltarBlock.onLeftClick(level.getBlockState(pos), level, pos, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void interact$RightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos blockPos = event.getPos();
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (!event.getUseBlock().isTrue() && block instanceof AltarBlock) {
            event.setUseBlock(TriState.TRUE);
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
            RightClickRailBlock e = NeoForge.EVENT_BUS.post(new RightClickRailBlock(player, minecartItemStack, blockState, railBlock, blockPos));
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

    @SubscribeEvent
    public static void interact$EntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer player &&
                event.getTarget() instanceof LivingEntity living
        ) healChocking:{
            if (!living.hasEffect(ModEffects.CHOKING)) break healChocking;
            ItemStack stack = player.getMainHandItem();
            if (!ModUtils.isWaterBottle(stack)) break healChocking;
            living.removeEffect(ModEffects.CHOKING);
            ItemStack emptyBottle = stack.is(PotionItems.BOTTLED_WATER)
                    ? PotionItems.BOTTLE.toStack()
                    : Items.GLASS_BOTTLE.getDefaultInstance();
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemUtils.createFilledResult(stack, player, emptyBottle));
        }
    }

    @SubscribeEvent
    public static void itemEntityPickup$Pre(ItemEntityPickupEvent.Pre event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();
        if (IServerPlayer.of(player).confluence$isCouldPickupItem()) {
            if (CommonConfigs.AUTO_STACK_GELS_COLOR.get()) autoStackGelsColor:{
                Item gel = MaterialItems.GEL.get();
                if (!itemStack.is(gel)) break autoStackGelsColor;
                int defaultMaxStackSize = gel.getDefaultMaxStackSize();
                for (ItemStack stack : player.getInventory().items) {
                    if (!stack.isEmpty() && stack.is(gel) && stack.getCount() + itemStack.getCount() <= defaultMaxStackSize) {
                        ColoredItem.setRGBA(itemStack, ColoredItem.getRGBA(stack));
                        break;
                    }
                }
            }
            if (itemEntity instanceof TreasureBagItemEntity entity) {
                if (!entity.isOwner(player)) event.setCanPickup(TriState.FALSE);
            }
        } else {
            event.setCanPickup(TriState.FALSE);
        }

        if (itemStack.is(ModTags.Items.PROVIDE_MANA)) {
            ManaStorage.of(player).receiveMana(() -> itemStack.getCount() * 100.0F);
            itemEntity.discard();
            event.setCanPickup(TriState.FALSE);
        } else if (itemStack.is(ModTags.Items.PROVIDE_LIFE)) {
            player.heal(itemStack.getCount() * 4.0F);
            itemEntity.discard();
            event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void itemEntityPickup$Post(ItemEntityPickupEvent.Post event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = event.getOriginalStack();
        CoinItem.onPickup(itemStack, itemEntity);
        LucyTheAxe.onPickup(player, itemStack);
    }

    @SubscribeEvent
    public static void itemFished(ItemFishedEvent event) {
        Player player = event.getEntity();

        if (!TCUtils.hasType(player, AccessoryItems.HIGH$TEST$FISHING$LINE) && player.getRandom().nextFloat() < 0.1429F) {
            player.level().playSound(null, event.getHookEntity().blockPosition(), ModSoundEvents.DECOUPLING.get(), SoundSource.AMBIENT);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void interact$RightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.isSpectator()) return;
        ItemStack itemStack = event.getItemStack();
        if (itemStack.is(ModTags.Items.MANA_WEAPON) && player.hasEffect(ModEffects.SILENCED)) {
            event.setCanceled(true);
        } else if (!itemStack.isEmpty()) {
            if (player.hasEffect(ModEffects.STONED) || player.hasEffect(ModEffects.FROZEN) || player.hasEffect(ModEffects.CURSED)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void attackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            AccessoryItems.applyLuckyCoin(serverPlayer, event.getTarget());
        }
        if (player.getMainHandItem().is(ModTags.Items.SPEAR)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        Player old = event.getOriginal();
        Player neo = event.getEntity();

        /// 保留下来的flask effect
        /// @see org.confluence.mod.common.effect.flask.FlaskEffect#saveFlaskEffects
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
        PlayerUtils.flushLocalData(player, player);
        PlayerUtils.syncPlayerData(player);
        if (ModSecretSeeds.REALLY_SMALL.match(player.server)) {
            ReallySmall.scalePlayer(player);
        }
    }

    @SubscribeEvent
    public static void harvestCheck(PlayerEvent.HarvestCheck event) {
        ItemStack itemStack = event.getEntity().getMainHandItem();
        if (!itemStack.isEmpty() && itemStack.is(ItemTags.PICKAXES)) {
            event.setCanHarvest(ModTiers.isCorrectToolForDrops(DiggingPower.getPower(itemStack), itemStack, event.getTargetBlock()));
        }
    }

    @SubscribeEvent
    public static void advancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        AdvancementHolder advancement = event.getAdvancement();
        if (!advancement.value().display().map(DisplayInfo::shouldAnnounceChat).orElse(true) && AchievementOffsetLoader.getDisplayOffset().containsKey(advancement.id())) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            player.server.getPlayerList().broadcastSystemMessage(Component.translatable("chat.type.advancement.achievement", player.getDisplayName(), Advancement.name(advancement)), false);
        }
    }

    @SubscribeEvent
    public static void advancementProgress(AdvancementEvent.AdvancementProgressEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!LibUtils.isSingleplayerOwner(player) &&
                AchievementOffsetLoader.getDisplayOffset().containsKey(event.getAdvancement().id())
        ) {
            AchievementsDataSyncPacketS2C.sendToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer target) {
            ServerPlayer sendTo = (ServerPlayer) event.getEntity();
            PlayerUtils.flushLocalData(sendTo, target);
        } else if (event.getTarget() instanceof AbstractTerraNPC npc) {
            NPCSpawner.INSTANCE.applyBenedictions(npc);
        }
    }

    @SubscribeEvent
    public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerUtils.flushLocalData(player, player);
        PlayerUtils.syncPlayerData(player);
    }

    @SubscribeEvent
    public static void container$Close(PlayerContainerEvent.Close event) {
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
            if (key.is(ToolItems.KEY_OF_LIGHT)) {
                WoodenMimic mimic = TEMonsterEntities.HALLOWED_MIMIC.get().create(level);
                if (mimic != null) {
                    CustomMimicSummonKeyEvent.summon(mimic, blockEntity);
                }
            } else if (key.is(ToolItems.KEY_OF_NIGHT)) {
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
                NeoForge.EVENT_BUS.post(new CustomMimicSummonKeyEvent(player, key, menu, blockEntity));
            }
        }
    }

    @SubscribeEvent
    public static void canSleep(CanPlayerSleepEvent event) {
        if (BloodMoonGameEvent.INSTANCE.started()) {
            event.setProblem(Player.BedSleepingProblem.NOT_SAFE);
        } else if (ModSecretSeeds.NEVER_SLEEP.match(event.getEntity().server)) {
            event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);
        }
    }

    @SubscribeEvent
    public static void canContinueSleeping(CanContinueSleepingEvent event) {
        if (event.mayContinueSleeping() && BloodMoonGameEvent.INSTANCE.started()) {
            event.setContinueSleeping(false);
        }
    }

    @SubscribeEvent
    public static void canSpawnPhantom(PlayerSpawnPhantomsEvent event) {
        if (ModSecretSeeds.NEVER_SLEEP.match(((ServerPlayer) event.getEntity()).server)) {
            event.setResult(PlayerSpawnPhantomsEvent.Result.ALLOW);
        }
    }

    /// 阻止自然回血的药水效果，已改为使用EffectCure，并提取到Lib了
    ///
    /// @see LibGameEvents#playerNaturalHeal
    @SubscribeEvent
    public static void naturalHeal(PlayerNaturalHealEvent event) {
        if (PlayerUtils.skipHealIfOnFire(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
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
                ParticleUtils.spawnParticles(level, pos, 45, 3.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
            }
            event.setSuccessful(true);
        }
    }

    @SubscribeEvent
    public static void afterFlushArmorSetBonus(AfterFlushArmorSetBonusEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerUtils.flushPrimitiveValueData(player);
        }
    }

    @SubscribeEvent
    public static void additionalMana(AdditionalManaEvent event) {
        ArsNouveauHelper.additionalMana(event);
        IronSpellHelper.additionalMana(event);
    }

    @SubscribeEvent
    public static void switchItemFunction$Post(SwitchItemFunctionEvent.Post event) {
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

    @SubscribeEvent
    public static void rightClickRailBlock(MinecartAbilityEvent.RightClickRailBlock event) {
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

    @SubscribeEvent
    public static void dismountOnMinecart(MinecartAbilityEvent.DismountOnMinecart event) {
        if (event.getMinecartItem() == null && event.getMinecart().getMinecartType() == AbstractMinecart.Type.RIDEABLE) {
            event.setMinecartItem(IAbstractMinecart.of(event.getMinecart()).confluence$getDropItem().getDefaultInstance());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void getArmorSetBonus(GetArmorSetBonusDataEvent event) {
        ArmorSetBonusKey key = event.getKey();
        if (key.head().builtInRegistryHolder().is(ModTags.Items.ROBE)) {
            if (key.chest() == ArmorItems.WIZARD_HAT.get()) {
                event.setNeoData(ModArmorBonus.WIZARD_HAT_SET_BONUS);
            } else if (key.chest() == ArmorItems.MAGIC_HAT.get()) {
                event.setNeoData(ModArmorBonus.MAGIC_HAT_SET_BONUS);
            }
        }
    }

    @SubscribeEvent
    public static void customPickupRange(CustomPickupRangeEvent event) {
        Player player = event.getEntity();
        event.addRange(PlayerUtils.MANA_RANGE, TCUtils.getValue(player, AccessoryItems.MANA$PICKUP$RANGE).getA(), stack -> stack.is(ModTags.Items.PROVIDE_MANA));
        event.addRange(PlayerUtils.COIN_RANGE, TCUtils.getValue(player, AccessoryItems.COIN$PICKUP$RANGE).getA(), stack -> stack.is(ModTags.Items.COINS));
        event.addRange(PlayerUtils.HEART_RANGE, ModEffects.getHeartReachRange(player), stack -> stack.is(ModTags.Items.PROVIDE_LIFE));
    }
}
