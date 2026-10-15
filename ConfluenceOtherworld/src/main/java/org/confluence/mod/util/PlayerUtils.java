package org.confluence.mod.util;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.api.event.CustomPickupRangeEvent;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.mixed.ILevelChunkSection;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.SoulCollectionEvent;
import org.confluence.mod.api.event.SoulConsumeEvent;
import org.confluence.mod.api.event.SoulRecoveryEvent;
import org.confluence.mod.api.event.SoulSkillAvailabilityEvent;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.*;
import org.confluence.mod.common.component.SoulArtifactCooldown;
import org.confluence.mod.common.data.MoonPhase;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.boss.BossOwnedEntity;
import org.confluence.mod.common.entity.boss.TheTwins;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.item.potion.ManaPotionItem;
import org.confluence.mod.common.item.soul.SoulArtifactItem;
import org.confluence.mod.common.item.soul.SoulWeaponItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.soul.skill.SoulSkill;
import org.confluence.mod.common.soul.skill.SoulSkillCastContext;
import org.confluence.mod.common.soul.skill.SoulSkillStack;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.network.AskForSoftcorePacket;
import org.confluence.mod.network.TeamPacket;
import org.confluence.mod.network.s2c.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.integration.bettercombat.BetterCombatHelper;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static org.confluence.lib.util.LibUtils.MAX_STACK_SIZE;
import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_COINS;
import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class PlayerUtils {
    public static final ToIntFunction<Item> COIN_2_INDEX = coin -> {
        if (coin == ModItems.PLATINUM_COIN.get()) return 0;
        if (coin == ModItems.GOLD_COIN.get()) return 1;
        if (coin == ModItems.SILVER_COIN.get()) return 2;
        if (coin == ModItems.COPPER_COIN.get()) return 3;
        return -1;
    };
    public static final IntFunction<CoinItem> INDEX_2_COIN = index -> switch (index) {
        case 0 -> ModItems.COPPER_COIN.get();
        case 1 -> ModItems.SILVER_COIN.get();
        case 2 -> ModItems.GOLD_COIN.get();
        default -> ModItems.PLATINUM_COIN.get();
    };
    public static final CustomPickupRangeEvent.RangeType MANA_RANGE = CustomPickupRangeEvent.RangeType.get(Confluence.asResource("mana"));
    public static final CustomPickupRangeEvent.RangeType COIN_RANGE = CustomPickupRangeEvent.RangeType.get(Confluence.asResource("coin"));
    public static final CustomPickupRangeEvent.RangeType HEART_RANGE = CustomPickupRangeEvent.RangeType.get(Confluence.asResource("heart"));
    public static final int DEFAULT_EQUIPPED_SOUL_SKILL_COUNT = SoulSkillStorage.DEFAULT_EQUIPPED_SKILL_COUNT;
    public static final int SOUL_ARTIFACT_QUICK_SLOT_COUNT = 3;
    public static final int SOUL_ARTIFACT_COOLDOWN_LIMIT = 3;
    private static final float EMPTY_RESOURCE = 0.0F;
    private static final float SOUL_ENVIRONMENT_RECOVERY_RATIO = 0.05F;
    private static final int SOUL_CORE_ADJUSTMENT = 5;
    private static final double SOUL_COLLECTION_HEALTH_WEIGHT = 0.8D;
    private static final double SOUL_COLLECTION_ATTACK_WEIGHT = 0.1D;
    private static final double SOUL_COLLECTION_ARMOR_WEIGHT = 0.1D;
    private static final double SOUL_COLLECTION_DIVISOR = 20.0D;
    private static final double SOUL_COLLECTION_BASE_MULTIPLIER = 1.0D;
    private static final float MIN_SOUL_COLLECTION = 1.0F;
    private static final String SOUL_OVERDRAFT_DEATH_MESSAGE = "death.attack.soul_overdraft";
    private static final Set<LivingEntity> SOUL_COLLECTION_SETTLED = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<TheTwins, UUID> TWINS_SOUL_KILLERS = new WeakHashMap<>();

    public static void syncMana2Client(ServerPlayer player, ManaStorage manaStorage) {
        PacketDistributor.sendToPlayer(player, new ManaPacketS2C(manaStorage.getMaxMana(), manaStorage.getCurrentMana()));
    }

    public static void syncMana2Client(ServerPlayer player) {
        syncMana2Client(player, ManaStorage.of(player));
    }

    public static void regenerateMana(ServerPlayer player) {
        ManaStorage manaStorage = ManaStorage.of(player);
        if (!manaStorage.canReceive()) return;

        int delay = manaStorage.getRegenerateDelay();
        boolean notMove = isNotMove(player);
        if (delay > 0) {
            if (manaStorage.isArcaneCrystalUsed())
                delay = (int) ((float) delay * (notMove ? 0.975F : 0.95F));
            if (delay > 20 && player.hasEffect(ModEffects.MANA_REGENERATION)) delay = 20;
            int delayReduce = notMove ? 2 : 1;
            if (manaStorage.isFastManaRegeneration()) delayReduce += 1;
            manaStorage.setRegenerateDelay(delay - delayReduce);
            return;
        }
        boolean hasStarBottle = player.hasEffect(ModEffects.STAR_IN_A_BOTTLE);
        float starBottleBonusPerTick = hasStarBottle ? 0.25F : 0.0F;

        FloatSupplier receive = () -> {
            // 1.0F / 7.0F = 0.14285715F
            float a = manaStorage.getMaxMana() * 0.14285715F + (manaStorage.isFastManaRegeneration() ? 25 : 0) + 1;
            if (notMove) a += manaStorage.getMaxMana() * 0.5F;
            float b = manaStorage.getCurrentMana() * 0.8F / manaStorage.getMaxMana() + 0.2F;
            float baseRegen = a * b * 0.0115F * EnchantmentUtils.processManaRegeneration(player);
            return baseRegen + starBottleBonusPerTick;
        };

        if (manaStorage.receiveMana(receive)) syncMana2Client(player, manaStorage);
    }

    public static void regenerateSoul(ServerPlayer player) {
        SoulStorage storage = player.getData(ModAttachmentTypes.SOUL_STORAGE);
        if (!player.isAlive() || player.isSpectator() || !storage.canReceive()) {
            return;
        }
        float recoveryRatio = storage.getRecoveryRatio();
        if (ModBlockCounters.isGraveyard(player.level(), player.blockPosition())
                || player.level().getBiome(player.blockPosition()).is(Biomes.SOUL_SAND_VALLEY)) {
            recoveryRatio += SOUL_ENVIRONMENT_RECOVERY_RATIO;
        }
        SoulRecoveryEvent event = NeoForge.EVENT_BUS.post(new SoulRecoveryEvent(player, recoveryRatio));
        float ratio = event.getRecoveryRatio();
        if (event.isCanceled() || !Float.isFinite(ratio) || ratio < EMPTY_RESOURCE
                || !player.isAlive() || player.isSpectator() || !storage.canReceive()) {
            return;
        }
        int maxSoul = storage.getMaxSoul();
        float amount = (float) Math.ceil(Math.min(maxSoul * ratio, maxSoul));
        if (Float.isFinite(amount) && amount > EMPTY_RESOURCE) {
            storage.receive(amount);
        }
    }

    private static boolean isNotMove(ServerPlayer player) {
        Vector3f vector3f = IServerPlayer.of(player).confluence$getMovementSpeed();
        return Math.abs(vector3f.x) < Mth.EPSILON && Math.abs(vector3f.y) < Mth.EPSILON && Math.abs(vector3f.z) < Mth.EPSILON;
    }

    public static boolean extractMana(ServerPlayer player, ItemStack itemStack, FloatSupplier sup) {
        if (player.getData(ModAttachmentTypes.SOUL_STORAGE).isSoulMode()) {
            return false;
        }
        if (player.isCreative()) return true;
        ManaStorage manaStorage = ManaStorage.of(player);
        if (!manaStorage.canExtract()) return false;
        if (itemStack.is(ManaWeaponItems.SPACE_GUN) && ModArmorBonus.hasType(player, ModArmorBonus.SPACE$GUN$FREE)) {
            sup = () -> 0;
        }
        if (manaStorage.extractMana(EnchantmentUtils.processEfficientMagic(sup, player), player)) {
            syncMana2Client(player, manaStorage);
            return true;
        }
        return false;
    }

    public static void receiveMana(ServerPlayer player, FloatSupplier sup) {
        ManaStorage manaStorage = ManaStorage.of(player);
        if (manaStorage.receiveMana(sup)) syncMana2Client(player, manaStorage);
    }

    public static void convertManaToSoul(Player player, float amount) {
        if (player instanceof ServerPlayer serverPlayer) {
            switchSoulMode(serverPlayer, true, amount);
        }
    }

    public static void convertSoulToMana(Player player, float amount) {
        if (player instanceof ServerPlayer serverPlayer) {
            switchSoulMode(serverPlayer, false, amount);
        }
    }

    public static boolean toggleSoulMode(ServerPlayer player, float targetAmount) {
        return switchSoulMode(player, !player.getData(ModAttachmentTypes.SOUL_STORAGE).isSoulMode(), targetAmount);
    }

    public static boolean switchSoulMode(ServerPlayer player, boolean soulMode, float targetAmount) {
        if (!Float.isFinite(targetAmount) || targetAmount < EMPTY_RESOURCE) {
            return false;
        }
        SoulStorage soulStorage = player.getData(ModAttachmentTypes.SOUL_STORAGE);
        if (soulStorage.isSoulMode() == soulMode) {
            return false;
        }
        ManaStorage manaStorage = player.getData(ModAttachmentTypes.MANA_STORAGE);
        manaStorage.setCurrentMana(EMPTY_RESOURCE);
        soulStorage.setCurrentSoul(EMPTY_RESOURCE);
        soulStorage.setSoulMode(soulMode);
        if (soulMode) {
            IronSpellHelper.cancelCasting(player);
            soulStorage.setCurrentSoul(targetAmount);
        } else {
            manaStorage.setCurrentMana(targetAmount);
        }
        syncMana2Client(player, manaStorage);
        soulStorage.syncIfNeeded(player);
        return true;
    }

    public static float getSoul(Player player) {
        return player.getData(ModAttachmentTypes.SOUL_STORAGE).getCurrentSoul();
    }

    public static void setSoul(Player player, float amount) {
        if (player instanceof ServerPlayer) {
            player.getData(ModAttachmentTypes.SOUL_STORAGE).setCurrentSoul(amount);
        }
    }

    public static void adjustSoul(Player player, float offset) {
        if (player instanceof ServerPlayer) {
            SoulStorage storage = player.getData(ModAttachmentTypes.SOUL_STORAGE);
            storage.setCurrentSoul(storage.getCurrentSoul() + offset);
        }
    }

    public static boolean canAdjustSoulWithCore(@Nullable Player player, ItemStack stack) {
        return player != null && player.isAlive() && !player.isSpectator()
                && player.isCreative() && player.isShiftKeyDown() && stack == player.getMainHandItem()
                && stack.is(ConsumableItems.FALLEN_SOUL_CORE.get())
                && player.getData(ModAttachmentTypes.SOUL_STORAGE).isSoulMode();
    }

    public static boolean adjustSoulWithCore(ServerPlayer player, ItemStack stack, boolean increase) {
        if (!canAdjustSoulWithCore(player, stack)) {
            return false;
        }
        SoulStorage storage = player.getData(ModAttachmentTypes.SOUL_STORAGE);
        boolean changed = storage.adjustBaseSoul(increase ? SOUL_CORE_ADJUSTMENT : -SOUL_CORE_ADJUSTMENT);
        storage.syncIfNeeded(player);
        return changed;
    }

    public static boolean isHoldingSoulWeapon(@Nullable Player player) {
        return player != null && player.getMainHandItem().getItem() instanceof SoulWeaponItem;
    }

    public static boolean castSoulSkill(ServerPlayer player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return false;
        }
        if (!isHoldingSoulWeapon(player)) {
            return false;
        }
        if (!player.isAlive()) {
            return false;
        }
        if (player.isSpectator()) {
            return false;
        }
        if (!player.getData(ModAttachmentTypes.SOUL_STORAGE).isSoulMode()) {
            return false;
        }
        refreshSoulSkillAvailability(player);
        SoulSkillStorage storage = player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE);
        SoulSkillStack skillStack = storage.getCurrentSkill();
        if (SoulSkillStack.isEmpty(skillStack) || skillStack.getCd() > 0
                || !storage.isSkillAvailable(skillStack.getSoulSkill().getId())) {
            return false;
        }
        ItemStack weaponStack = player.getMainHandItem();
        SoulWeaponItem weapon = (SoulWeaponItem) weaponStack.getItem();
        SoulSkill skill = skillStack.getSoulSkill();
        float damage = skillStack.getDamage() + weapon.getSoulDamage(weaponStack);
        float soulCost = skill.getSoulCost(skillStack);
        int cooldown = skill.getCooldownTicks(skillStack);
        if (!Float.isFinite(damage) || damage < EMPTY_RESOURCE || cooldown < 0) {
            return false;
        }
        SoulSkillCastContext context = new SoulSkillCastContext(player, weaponStack, skillStack, damage);
        if (!skill.canCast(context)) {
            return false;
        }
        return consumeSoul(player, weaponStack, skillStack, soulCost,
                () -> player.getMainHandItem() == weaponStack && storage.getCurrentSkill() == skillStack
                        && skillStack.getCd() <= 0 && storage.isSkillAvailable(skill.getId()) && skill.canCast(context),
                () -> {
                    skillStack.startCooldown(cooldown);
                    storage.markChanged();
                    try {
                        skill.cast(context);
                    } finally {
                        storage.syncIfNeeded(player);
                    }
                });
    }

    public static boolean consumeSoul(ServerPlayer player, ItemStack itemStack, float soulCost, Runnable effect) {
        return consumeSoul(player, itemStack, null, soulCost, () -> true, effect);
    }

    public static boolean useSoulArtifact(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof SoulArtifactItem artifact) || stack.isEmpty()
                || isSoulArtifactOnCooldown(stack) || getCoolingSoulArtifactCount(player) >= SOUL_ARTIFACT_COOLDOWN_LIMIT
                || !player.isAlive() || player.isSpectator()
                || !player.getData(ModAttachmentTypes.SOUL_STORAGE).isSoulMode()) {
            return false;
        }
        float soulCost = artifact.getSoulCost(player, stack);
        int cooldown = artifact.getCooldownTicks(player, stack);
        if (cooldown < 0 || !artifact.canUse(player, stack)) {
            return false;
        }
        return consumeSoul(player, stack, null, soulCost,
                () -> player.getItemInHand(hand) == stack && !stack.isEmpty() && stack.getItem() == artifact
                        && !isSoulArtifactOnCooldown(stack) && getCoolingSoulArtifactCount(player) < SOUL_ARTIFACT_COOLDOWN_LIMIT
                        && artifact.canUse(player, stack),
                () -> {
                    if (cooldown > 0) {
                        stack.set(ModDataComponentTypes.SOUL_ARTIFACT_COOLDOWN, new SoulArtifactCooldown(cooldown, cooldown));
                    }
                    artifact.activate(player, stack);
                });
    }

    public static boolean isSoulArtifactOnCooldown(ItemStack stack) {
        if (!(stack.getItem() instanceof SoulArtifactItem)) {
            return false;
        }
        SoulArtifactCooldown cooldown = stack.get(ModDataComponentTypes.SOUL_ARTIFACT_COOLDOWN);
        return cooldown != null && cooldown.remainingTicks() > 0 && cooldown.totalTicks() > 0;
    }

    public static int getCoolingSoulArtifactCount(Player player) {
        Inventory inventory = player.getInventory();
        int count = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (isSoulArtifactOnCooldown(inventory.getItem(slot))) {
                count++;
            }
        }
        return count;
    }

    public static float getSoulArtifactCooldownPercent(ItemStack stack) {
        if (!isSoulArtifactOnCooldown(stack)) {
            return 0.0F;
        }
        SoulArtifactCooldown cooldown = stack.get(ModDataComponentTypes.SOUL_ARTIFACT_COOLDOWN);
        if (cooldown == null) {
            return 0.0F;
        }
        return Mth.clamp((float) cooldown.remainingTicks() / cooldown.totalTicks(), 0.0F, 1.0F);
    }

    public static void tickSoulArtifactCooldowns(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!(stack.getItem() instanceof SoulArtifactItem)) {
                continue;
            }
            SoulArtifactCooldown cooldown = stack.get(ModDataComponentTypes.SOUL_ARTIFACT_COOLDOWN);
            if (cooldown == null) {
                continue;
            }
            if (cooldown.remainingTicks() <= 1 || cooldown.totalTicks() <= 0) {
                stack.remove(ModDataComponentTypes.SOUL_ARTIFACT_COOLDOWN);
            } else {
                stack.set(ModDataComponentTypes.SOUL_ARTIFACT_COOLDOWN, new SoulArtifactCooldown(cooldown.remainingTicks() - 1, cooldown.totalTicks()));
            }
        }
    }

    private static boolean consumeSoul(ServerPlayer player, ItemStack itemStack, @Nullable SoulSkillStack skillStack, float soulCost, BooleanSupplier canExecute, Runnable effect) {
        SoulStorage storage = player.getData(ModAttachmentTypes.SOUL_STORAGE);
        if (!storage.isSoulMode() || !player.isAlive() || player.isSpectator()
                || !Float.isFinite(soulCost) || soulCost < EMPTY_RESOURCE) {
            return false;
        }
        if (player.isCreative()) {
            if (!canExecute.getAsBoolean()) {
                return false;
            }
            effect.run();
            return true;
        }
        SoulConsumeEvent event = NeoForge.EVENT_BUS.post(new SoulConsumeEvent(player, itemStack, skillStack, soulCost));
        if (event.isCanceled() || !Float.isFinite(event.getSoulCost()) || event.getSoulCost() < EMPTY_RESOURCE
                || !Float.isFinite(event.getHealthPerSoul()) || event.getHealthPerSoul() < EMPTY_RESOURCE
                || !Float.isFinite(event.getMinimumHealth()) || event.getMinimumHealth() < EMPTY_RESOURCE
                || !player.isAlive() || !storage.isSoulMode() || !canExecute.getAsBoolean()) {
            return false;
        }
        float soulPayment = Math.min(storage.getCurrentSoul(), event.getSoulCost());
        float healthPayment = (event.getSoulCost() - soulPayment) * event.getHealthPerSoul();
        if (!Float.isFinite(healthPayment) || !storage.tryExtract(soulPayment)) {
            return false;
        }
        float health = player.getHealth();
        player.setHealth(Math.max(Math.min(health, event.getMinimumHealth()), health - healthPayment));
        boolean lethalCompensation = player.getHealth() <= EMPTY_RESOURCE;
        try {
            effect.run();
        } finally {
            storage.syncIfNeeded(player);
            if (lethalCompensation && player.getHealth() <= EMPTY_RESOURCE && !player.isRemoved()) {
                DamageSource source = new DamageSource(player.damageSources().genericKill().typeHolder()) {
                    @Override
                    public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
                        return Component.translatable(SOUL_OVERDRAFT_DEATH_MESSAGE, livingEntity.getDisplayName());
                    }
                };
                player.getCombatTracker().recordDamage(source, healthPayment);
                player.die(source);
            }
        }
        return true;
    }

    public static void collectSoul(LivingEntity victim, DamageSource source) {
        if (!(victim.level() instanceof ServerLevel level) || !SOUL_COLLECTION_SETTLED.add(victim)) {
            return;
        }
        Entity attacker = LibEntityUtils.getOwner(source);
        if (victim instanceof AbstractTwinEye eye) {
            TheTwins twins = eye.getMaster();
            if (twins != null && !twins.isRetinazerAlive() && !twins.isSpazmatismAlive()) {
                if (attacker instanceof ServerPlayer player) {
                    TWINS_SOUL_KILLERS.put(twins, player.getUUID());
                } else {
                    TWINS_SOUL_KILLERS.remove(twins);
                }
            }
        }
        boolean bossBody = victim instanceof Boss boss ? boss.isMainBody()
                : !(victim instanceof BossOwnedEntity) && !(victim instanceof PartHitTarget) && victim.getType().is(Tags.EntityTypes.BOSSES);
        if (!bossBody && (victim instanceof Boss || victim instanceof AbstractTwinEye
                || (victim instanceof PartHitTarget part && part.encounterOwner() instanceof Boss)
                || victim instanceof OwnedSummon || !EnemyDamageRules.isEnemy(victim))) {
            return;
        }
        if (victim instanceof TheTwins twins) {
            UUID killer = TWINS_SOUL_KILLERS.remove(twins);
            if (attacker == null && source.is(DamageTypes.GENERIC) && killer != null) {
                attacker = level.getServer().getPlayerList().getPlayer(killer);
            }
        }
        if (!(attacker instanceof ServerPlayer player)) {
            return;
        }
        SoulStorage storage = player.getData(ModAttachmentTypes.SOUL_STORAGE);
        if (!storage.canReceive()) {
            return;
        }
        if (bossBody) {
            storage.setCurrentSoul(storage.getMaxSoul());
            storage.syncIfNeeded(player);
            return;
        }
        AttributeInstance attack = victim.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armor = victim.getAttribute(Attributes.ARMOR);
        double attackDamage = attack == null ? EMPTY_RESOURCE : attack.getValue();
        double armorValue = armor == null ? EMPTY_RESOURCE : armor.getValue();
        double calculated = (victim.getMaxHealth() * SOUL_COLLECTION_HEALTH_WEIGHT + attackDamage * SOUL_COLLECTION_ATTACK_WEIGHT
                + armorValue * SOUL_COLLECTION_ARMOR_WEIGHT) / SOUL_COLLECTION_DIVISOR;
        if (!Double.isFinite(calculated)) {
            return;
        }
        float original = (float) Math.max(MIN_SOUL_COLLECTION, calculated);
        if (!Float.isFinite(original)) {
            return;
        }
        float bonus = TCUtils.getValue(player, AccessoryItems.SOUL$COLLECTION$BONUS);
        float ratio = TCUtils.getValue(player, AccessoryItems.SOUL$COLLECTION$RATIO);
        double adjusted = (original + (double) bonus) * (SOUL_COLLECTION_BASE_MULTIPLIER + ratio);
        float amount = (float) adjusted;
        if (!Double.isFinite(adjusted) || !Float.isFinite(amount)) {
            return;
        }
        SoulCollectionEvent event = new SoulCollectionEvent(player, victim, source, original);
        event.setAmount(amount);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled() && Float.isFinite(event.getAmount())) {
            storage.receive(Math.max(MIN_SOUL_COLLECTION, event.getAmount()));
            storage.syncIfNeeded(player);
        }
    }

    @Nullable
    public static SoulSkillStack getCurrentSoulSkill(Player player) {
        return player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getCurrentSkill();
    }

    public static List<SoulSkillStack> getPlayerSoulSkills(Player player) {
        return player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getEquippedSkills();
    }

    /// 初始化数据到player客户端
    public static void syncSavedData(ServerPlayer player) {
        ConfluenceData data = ConfluenceData.get(player.serverLevel());
        WindSpeedPacketS2C.sendToClient(player);
        if (CommonConfigs.STAR_PHASE.get()) {
            StarPhasesPacketS2C.sendToClient(player, data.getStarPhases());
        }
        KillBoardSyncPacketS2C.sendToClient(player);
        GlobalCloakSyncPacketS2C.sendToClient(player);
        MeteoriteLocationPacketS2C.sendToClient(player, data.getMeteoriteLocation(), 0);
        BestiarySyncPacketS2C.syncEntries(player);
        ExtraInventorySyncPacketS2C.sendToClient(player, player);
        PlayerPiggyBankContainer.of(player).setChanged(); // 自动同步
        FishingPowerInfoPacketS2C.sendToClient(player);
        VisibilityPacketS2C.sendEcho(player);
        syncMana2Client(player);
        VisibilityPacketS2C.sendTheConstantPostEffect(player);
        SecretFlagSyncPacketS2C.sendToClient(player, IMinecraftServer.of(player.server).confluence$getSecretFlag());
        CompatibilitySyncPacketS2c.sendToClient(player);
        TeamPacket.sendToClient(player, player);
        DragonChargePlayerConfigPacketS2C.sendToPlayer(player);
    }

    /// 将target的数据同步到sendTo
    public static void flushLocalData(ServerPlayer sendTo, ServerPlayer target) {
        ExtraInventorySyncPacketS2C.sendToClient(sendTo, target);
        FlushArmorSetBonusPacketS2C.sendToClient(sendTo, target);
        TeamPacket.sendToClient(sendTo, target);
    }

    /// 同步数据到player客户端
    public static void syncPlayerData(ServerPlayer player) {
        PlayerVoidVaultContainer.of(player).sync(player, true);
        ManaStorage manaStorage = player.getData(ModAttachmentTypes.MANA_STORAGE);
        manaStorage.setCurrentMana(manaStorage.getCurrentMana());
        SoulStorage soulStorage = player.getData(ModAttachmentTypes.SOUL_STORAGE);
        soulStorage.freshMaxSoul();
        syncMana2Client(player, manaStorage);
        soulStorage.syncIfNeeded(player);
        GameEventSystem.INSTANCE.syncAll(player);
        SoulSkillStorage skillStorage = player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE);
        skillStorage.addUnlockedSkill(ModSoulSkills.SOUL_LURE.get());
        skillStorage.addUnlockedSkill(ModSoulSkills.ENHANCED_SOUL.get());
        refreshSoulSkillAvailability(player);
        skillStorage.sync(player);
    }

    public static float getFishingPower(ServerPlayer player) {
        float base = player.getLuck() + TCUtils.getValue(player, AccessoryItems.FISHING$POWER);
        if (player.fishing != null) {
            base += player.fishing.luck;
        }
        if (EverBeneficial.of(player).isGummyWormUsed()) {
            base += 3.0F;
        }
        if (player.isInFluidType() && TCUtils.hasType(player, TCItems.FLOAT$ON$LIQUID$SURFACE)) {
            base += 5.0F;
        }
        if (player.hasEffect(ModEffects.TIPSY)) {
            base += 5.0F;
        }
        Level level = player.level();
        if (level.isRaining()) {
            base *= 1.1F;
        } else if (level.isThundering()) {
            base *= 1.2F;
        }
        int dayTime = LibDateUtils.getDayTime(level);
        if (LibDateUtils.isWithinDayTime(LibDateUtils._04$30, LibDateUtils._06$00, dayTime)) {
            base *= 1.3F;
        } else if (LibDateUtils.isWithinDayTime(9, 0, 15, 0, dayTime)) {
            base *= 0.8F;
        } else if (LibDateUtils.isWithinDayTime(LibDateUtils._18$00, LibDateUtils._19$30, dayTime)) {
            base *= 1.3F;
        } else if (LibDateUtils.isWithinDayTime(21, 18, 2, 12, dayTime)) {
            base *= 0.8F;
        }
        base *= switch (MoonPhase.of(level)) {
            case FULL_MOON -> 1.1F;
            case WANING_GIBBOUS, WAXING_GIBBOUS -> 1.05F;
            case WAXING_CRESCENT -> 0.95F;
            case NEW_MOON -> 0.9F;
            default -> 1.0F;
        };
        if (BloodMoonGameEvent.INSTANCE.started()) {
            base *= 1.1F;
        }
        return base;
    }

    public static Tuple<ItemStack, Integer> getMaxDiggingPowerItem(Player player) {
        int max = -1;
        ItemStack ret = ItemStack.EMPTY;
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.isEmpty() || !itemStack.is(ItemTags.PICKAXES)) continue;
            int power = DiggingPower.getPower(itemStack);
            if (power > max) {
                max = power;
                ret = itemStack;
            }
        }
        return new Tuple<>(ret, max);
    }

    public static void consumeItemCount(Iterable<ItemStack> have, Item item, int consumeCount) {
        int count = 0;
        for (ItemStack stack : have) {
            if (stack.is(item) && count < consumeCount) {
                int toConsume = Math.min(stack.getCount(), consumeCount - count);
                stack.shrink(toConsume);
                count += toConsume;
            }
        }
    }

    public static Coins getCoins(Player player, boolean withPiggyBank) {
        Coins coins = withPiggyBank ? decodeCoin(PlayerPiggyBankContainer.of(player).getTotalMoney() + PlayerVoidVaultContainer.of(player).getMoney()) : Coins.createEmpty();
        for (ItemStack stack : Iterables.concat(player.getInventory().items, ExtraInventory.of(player).getAllCoins())) {
            if (!stack.isEmpty() && stack.getItem() instanceof CoinItem coin) {
                coins.increase(coin, stack.getCount());
            }
        }
        return coins;
    }

    public static long getMoney(Player player, boolean withPiggyBank) {
        long res = withPiggyBank ? PlayerPiggyBankContainer.of(player).getTotalMoney() + PlayerVoidVaultContainer.of(player).getMoney() : 0;
        for (ItemStack stack : Iterables.concat(player.getInventory().items, ExtraInventory.of(player).getAllCoins())) {
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                int index = COIN_2_INDEX.applyAsInt(stack.getItem());
                if (index != -1) {
                    res += (long) (stack.getCount() * Math.pow(UPGRADES_COUNT, 3 - index));
                }
            }
        }
        return res;
    }

    public static boolean tryCostMoney(Player player, long cost, boolean withPiggyBank) {
        return tryCostMoney(getMoney(player, withPiggyBank), player, cost, withPiggyBank);
    }

    public static boolean tryCostMoney(long have, Player player, long cost, boolean withPiggyBank) {
        if (have < cost) return false;

        if (withPiggyBank) {
            long res = PlayerPiggyBankContainer.of(player).tryCostMoney(cost);
            if (res == 0) return true;
            have -= cost - res;
            cost = res;
            res = PlayerVoidVaultContainer.of(player).tryCostMoney(player, cost);
            if (res == 0) return true;
            have -= cost - res;
            cost = res;
        }

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }

        ExtraInventory extraInventory = ExtraInventory.of(player);
        for (int i = 0; i < SIZE_COINS; i++) {
            extraInventory.setCoins(i, ItemStack.EMPTY);
        }
        Coins coins = decodeCoin(have - cost);

        for (Object2IntMap.Entry<CoinItem> entry : coins.copper2PlatinumEntries()) {
            int coin = entry.getIntValue();
            if (coin <= 0) continue;
            CoinItem coinItem = entry.getKey();
            while (coin > UPGRADES_COUNT) {
                inventory.add(new ItemStack(coinItem, UPGRADES_COUNT));
                coin -= UPGRADES_COUNT;
            }
            inventory.add(new ItemStack(coinItem, coin));
        }
        return true;
    }

    /// 扣款：先用手上的钱（背包 + 钱币栏），不够再动存钱罐。
    public static boolean debit(Player player, long cost, boolean withPiggyBank) {
        return tryCostMoney(player, cost, withPiggyBank);
    }

    /// 买下商品：扣款成功后把商品塞进背包，塞不下就掉在脚边。
    public static boolean purchase(Player player, long cost, boolean withPiggyBank, ItemStack result) {
        if (result.isEmpty() || !tryCostMoney(player, cost, withPiggyBank)) return false;
        ItemStack stack = result.copy();
        if (!player.getInventory().add(stack)) player.drop(stack, false);
        return true;
    }

    /// 售出所得按面额发到背包。
    public static boolean credit(Player player, long amount) {
        if (amount < 0) return false;
        giveCoins(player, amount);
        return true;
    }

    /// 从玩家背包的指定槽位卖出物品：槽位内容与卖出时不一致就整笔取消。
    public static boolean creditFromInventory(Player player, int slotIndex, ItemStack expected, long amount, boolean withPiggyBank) {
        if (expected.isEmpty() || slotIndex < 0 || slotIndex >= player.getInventory().getContainerSize())
            return false;
        ItemStack source = player.getInventory().getItem(slotIndex);
        if (!ItemStack.isSameItemSameComponents(source, expected) || source.getCount() < expected.getCount())
            return false;
        source.shrink(expected.getCount());
        if (source.isEmpty()) player.getInventory().setItem(slotIndex, ItemStack.EMPTY);
        player.getInventory().setChanged();
        giveCoins(player, amount);
        return true;
    }

    /// 按面额把钱币发到背包；背包放不下就掉在脚边。
    public static void giveCoins(Player player, long amount) {
        if (amount <= 0) return;
        for (Object2IntMap.Entry<CoinItem> entry : decodeCoin(amount).copper2PlatinumEntries()) {
            int count = entry.getIntValue();
            if (count <= 0) continue;
            ItemStack stack = new ItemStack(entry.getKey(), count);
            if (!player.getInventory().add(stack)) player.drop(stack, false);
        }
    }

    public static Coins decodeCoin(long money) {
        if (money < 0) throw new IllegalArgumentException("Money cannot be negative");

        // jit自动优化
        int p = (int) (money / (UPGRADES_COUNT * UPGRADES_COUNT * UPGRADES_COUNT)); // 铂金
        money %= UPGRADES_COUNT * UPGRADES_COUNT * UPGRADES_COUNT;
        int g = (int) (money / (UPGRADES_COUNT * UPGRADES_COUNT)); // 金币
        money %= UPGRADES_COUNT * UPGRADES_COUNT;
        int s = (int) (money / UPGRADES_COUNT); // 银币
        money %= UPGRADES_COUNT;
        int c = (int) money; // 铜币

        return new Coins(c, s, g, p);
    }

    public static Coins decodeCoin(int money) {
        int copper_count = money % UPGRADES_COUNT;
        int i = ((money - copper_count) / UPGRADES_COUNT);
        int silver_count = i % UPGRADES_COUNT;
        int j = ((i - silver_count) / UPGRADES_COUNT);
        int golden_count = j % UPGRADES_COUNT;
        int k = (j - golden_count) / UPGRADES_COUNT;
        return new Coins(copper_count, silver_count, golden_count, k);
    }

    public static void sortCoins(Player player) {
        ExtraInventory extraInventory = ExtraInventory.of(player);
        Object2IntOpenHashMap<Integer> map = new Object2IntOpenHashMap<>();
        for (int i = 0; i < SIZE_COINS; i++) {
            ItemStack coins = extraInventory.getCoins(i);
            if (coins.isEmpty() || !coins.is(ModTags.Items.COINS)) continue;
            extraInventory.setCoins(i, ItemStack.EMPTY);
            int index = COIN_2_INDEX.applyAsInt(coins.getItem());
            int count = coins.getCount();
            Supplier<CoinItem> upgrade;
            while (map.addTo(index, count) + count >= UPGRADES_COUNT && (upgrade = INDEX_2_COIN.apply(3 - index).upgrade) != null) {
                map.addTo(index, -UPGRADES_COUNT);
                index = COIN_2_INDEX.applyAsInt(upgrade.get());
                count = 1;
            }
        }
        for (int i = 0, j = 0; i < SIZE_COINS; i++) {
            int count = map.getInt(i);
            if (count <= 0) continue;
            CoinItem coinItem = INDEX_2_COIN.apply(3 - i);
            while (count > MAX_STACK_SIZE) {
                extraInventory.setCoins(j++, new ItemStack(coinItem, MAX_STACK_SIZE));
                count -= MAX_STACK_SIZE;
            }
            if (count > 0) {
                extraInventory.setCoins(j++, new ItemStack(coinItem, count));
            }
        }
    }

    /// @see PlayerDeathInfoPacketS2C#replaceCombatKillPacket(ServerPlayer, Component)
    public static void dropMoney(Player player) {
        if (!CommonConfigs.PLAYER_DROPS_MONEY.get()) return;
        long money = getMoney(player, false);
        long drops;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            int ratio = LibUtils.switchByDifficulty(player.level(), 2, 3, 4);
            drops = money * ratio / 4;
        } else {
            drops = money;
        }
        tryCostMoney(money, player, drops, false);
        ModUtils.dropMoney(drops, player.getX(), player.getY(), player.getZ(), player.level());

        if (CommonConfigs.SHOW_MONEY_DROPS.get()) {
            LibEntityUtils.getOrCreatePersistedData(player).putLong("confluence:drops_money", drops);
        }
    }

    /// 获取玩家复活时间
    ///
    /// @param player 玩家
    /// @return 复活时间
    public static int getRespawnWaitTime(ServerPlayer player) {
        AABB aabb = new AABB(player.blockPosition()).inflate(Short.MAX_VALUE);
        int min, max;
        if (player.level().getEntitiesOfClass(LivingEntity.class, aabb, living -> living instanceof Boss).isEmpty()) {
            min = CommonConfigs.DEFAULT_RESPAWN_TIME_MIN.get();
            max = CommonConfigs.DEFAULT_RESPAWN_TIME_MAX.get();
        } else {
            min = CommonConfigs.BOSS_RESPAWN_TIME_MIN.get();
            max = CommonConfigs.BOSS_RESPAWN_TIME_MAX.get();
        }
        if (min == max) return min;
        return player.getRandom().nextInt(Math.min(min, max), Math.max(min, max));
    }

    /// @return true表示魔力值不够
    public static boolean applyAutoGetMana(ServerPlayer player, float currentMana, float extract) {
        if (currentMana < extract) {
            if (!TCUtils.hasType(player, AccessoryItems.AUTO$GET$MANA)) return true;
            ItemStack toUse = null;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() instanceof ManaPotionItem manaPotion) {
                    int amount = manaPotion.getAmount();
                    if (currentMana + amount < extract) continue;
                    if (toUse == null || amount < ((ManaPotionItem) toUse.getItem()).getAmount())
                        toUse = itemStack;
                    if (amount == 50) break;
                }
            }
            if (toUse == null) return true;
            toUse.finishUsingItem(player.level(), player);
        }
        return false;
    }

    public static boolean couldPerformEmptyTargetSweep(Player player) {
        if (!player.isAutoSpinAttack()) {
            ItemStack stack = player.getMainHandItem();
            if (BetterCombatHelper.hasWeaponAttributes(stack)) return false;
            return stack.canPerformAction(ItemAbilities.SWORD_SWEEP) && stack.getItem() instanceof BaseSwordItem sword && sword.hasSpecialSweep();
        }
        return false;
    }

    public static boolean shouldSkipConsumeAmmo(Player player) {
        if (player.hasEffect(ModEffects.AMMO_BOX) && player.getRandom().nextFloat() < 0.2F) {
            return true;
        }
        return LibMathUtils.checkChance(ModArmorBonus.getValue(player, ModArmorBonus.SKIP$CONSUME$AMMO$CHANCE), player.getRandom());
    }

    public static boolean skipHealIfOnFire(Player player) {
        return CommonConfigs.TERRA_STYLE_FIRE_DAMAGE.get() && player.isOnFire();
    }

    public static float applyTerraFire(DamageSource damageSource, float amount) {
        if (CommonConfigs.TERRA_STYLE_FIRE_DAMAGE.get() && damageSource.is(DamageTypeTags.IS_FIRE)) {
            return amount * 4;
        }
        return amount;
    }

    public static void applySunflowerEffect(ServerPlayer player, ServerLevel level, long gameTime) {
        if (gameTime % 200 == 0) {
            ILevelChunkSection iSection = DynamicBiomeUtils.getISection(level, player.blockPosition());
            if (iSection != null && ModBlockCounters.SUNFLOWER.get(iSection.confluence$getBlockCounts()) > 0) {
                player.addEffect(new MobEffectInstance(ModEffects.HAPPY, 220));
            }
        }
    }

    public static void flushPrimitiveValueData(ServerPlayer player) {
        ManaStorage.of(player).flushAbility(player);
        player.getData(ModAttachmentTypes.SOUL_STORAGE).flushAbility(player);
        FishingPowerInfoPacketS2C.sendToClient(player);
        VisibilityPacketS2C.sendEcho(player);
    }

    public static void askForSoftcore(ServerPlayer player) {
        if (CommonConfigs.STOP_ASK_FOR_SOFTCORE.get()) return;
        ServerLevel overworld = player.server.getLevel(OverworldUtils.dimension());
        if (overworld == null) return;
        if (overworld.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get()) return;
        if (ConfluenceData.get(overworld).isStopAskForSoftcore()) return;
        PacketDistributor.sendToPlayer(player, new AskForSoftcorePacket(true));
    }

    public static void maxNumberUpdate(@Nullable Player player) {
        if (player instanceof ServerPlayer) {
            player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).maxNumberUpdate();
        }
    }

    public static int getEquippedSkillMaxNumber(@Nullable Player player) {
        return player == null ? DEFAULT_EQUIPPED_SOUL_SKILL_COUNT : player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getEquippedSkillMaxNumber();
    }

    public static void setEquippedSkillMaxNumber(@Nullable Player player, int equippedSkillMaxNumber) {
        if (player instanceof ServerPlayer) {
            player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).setEquippedSkillMaxNumber(equippedSkillMaxNumber);
        }
    }

    public static int getCurrentIndex(@Nullable Player player) {
        return player == null ? 0 : player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getCurrentIndex();
    }

    @Nullable
    public static SoulSkillStack getCurrentSkill(@Nullable Player player) {
        return player == null ? null : player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getCurrentSkill();
    }

    public static void setCurrentIndex(@Nullable Player player, int currentIndex) {
        if (player instanceof ServerPlayer) {
            player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).setCurrentIndex(currentIndex);
        }
    }

    public static List<Integer> getSkillIndexes(@Nullable Player player, SoulSkill skill) {
        return player == null ? List.of() : player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getSkillIndexes(skill);
    }

    public static List<SoulSkillStack> getEquippedSkills(@Nullable Player player) {
        return player == null ? Collections.nCopies(DEFAULT_EQUIPPED_SOUL_SKILL_COUNT, SoulSkillStack.getEmptyStack())
                : player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getEquippedSkills();
    }

    public static boolean equipSkill(@Nullable Player player, int index, @Nullable SoulSkillStack skillStack) {
        return player instanceof ServerPlayer && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).equipSkill(index, skillStack);
    }

    public static boolean unequipSkill(@Nullable Player player, int index) {
        return player instanceof ServerPlayer && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).unequipSkill(index);
    }

    public static boolean setSoulSkillStack(@Nullable Player player, int index, @Nullable SoulSkillStack skillStack) {
        return player instanceof ServerPlayer && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).setSoulSkillStack(index, skillStack);
    }

    public static boolean isSkillEquipped(@Nullable Player player, ResourceLocation skillId) {
        return player != null && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).isSkillEquipped(skillId);
    }

    public static boolean isSkillEquipped(@Nullable Player player, SoulSkillStack stack) {
        return player != null && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).isSkillEquipped(stack);
    }

    public static boolean isSkillEquipped(@Nullable Player player, SoulSkill skill) {
        return player != null && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).isSkillEquipped(skill);
    }

    // ---- 解锁技能 ----

    public static boolean learnSoulSkill(ServerPlayer player, ResourceLocation skillId) {
        SoulSkill skill = ModSoulSkills.getById(skillId);
        SoulSkillStorage storage = player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE);
        if (skill == null || skill == ModSoulSkills.EMPTY.get() || storage.isSkillLearned(skillId)) {
            return false;
        }
        storage.addUnlockedSkill(skill);
        storage.syncIfNeeded(player);
        return true;
    }

    public static boolean isSoulSkillLearned(@Nullable Player player, ResourceLocation skillId) {
        return player != null && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).isSkillLearned(skillId);
    }

    public static boolean isSoulSkillAvailable(@Nullable Player player, ResourceLocation skillId) {
        return player != null && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).isSkillAvailable(skillId);
    }

    public static void refreshSoulSkillAvailability(ServerPlayer player) {
        SoulSkillAvailabilityEvent event = new SoulSkillAvailabilityEvent(player);
        if (player.getMainHandItem().is(ModItems.BROKEN_GLIMMER.get())) {
            event.addSkill(ModSoulSkills.SURVIVAL_INSTINCT.get());
        }
        NeoForge.EVENT_BUS.post(event);
        player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).setTemporarySkills(event.getSkills());
    }

    public static void addUnlockedSkill(@Nullable Player player, SoulSkill skill) {
        if (player instanceof ServerPlayer) {
            player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).addUnlockedSkill(skill);
        }
    }

    public static void removeUnlockedSkill(@Nullable Player player, ResourceLocation skillId) {
        if (player instanceof ServerPlayer) {
            player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).removeUnlockedSkill(skillId);
        }
    }

    public static boolean isSkillUnlocked(@Nullable Player player, ResourceLocation skillId) {
        return player != null && player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).isSkillUnlocked(skillId);
    }

    public static Map<ResourceLocation, SoulSkill> getUnlockedSkills(@Nullable Player player) {
        return player == null ? Map.of() : player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getUnlockedSkills();
    }

    @Nullable
    public static SoulSkillStack getCurrentSkillStack(@Nullable Player player, int index) {
        return player == null ? null : player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE).getCurrentSkillStack(index);
    }
}
