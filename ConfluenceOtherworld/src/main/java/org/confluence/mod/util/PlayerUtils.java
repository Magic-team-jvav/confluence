package org.confluence.mod.util;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.api.event.CustomPickupRangeEvent;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.entity.PartHitTarget;
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
import org.confluence.mod.api.event.SoulSkillDamageEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.*;
import org.confluence.mod.common.data.MoonPhase;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.entity.soulcerer.SpiritSurgeEntity;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.item.potion.ManaPotionItem;
import org.confluence.mod.common.item.soulcerer.SoulArtifactItem;
import org.confluence.mod.common.item.soulcerer.SoulBooks;
import org.confluence.mod.common.item.soulcerer.SoulWeaponItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.soulcerer.skill.SoulAssaultSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
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
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static org.confluence.lib.util.LibUtils.MAX_STACK_SIZE;
import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_COINS;
import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class PlayerUtils {
    private static final ResourceLocation SOULDANCER_CRIT_ID = Confluence.asResource("souldancer_set_crit");
    private static final ResourceLocation SOULDANCER_SPEED_ID = Confluence.asResource("souldancer_set_speed");
    private static final float HEALTH_PER_SOUL = 4.0F;
    private static final float SOUL_CONSUMPTION_FAILED = -1.0F;
    private static final float ENVIRONMENT_SOUL_RECOVERY_RATIO = 0.05F;
    private static final float SOUL_HEALTH_WEIGHT = 0.8F;
    private static final float SOUL_ATTACK_WEIGHT = 0.1F;
    private static final float SOUL_ARMOR_WEIGHT = 0.1F;
    private static final float SOUL_COLLECTION_DIVISOR = 20.0F;
    private static final float MINIMUM_COLLECTED_SOUL = 1.0F;
    private static final String FIRST_OBTAIN_SOUL_SKILL_MESSAGE = "message.confluence.soulcerer_skill.first_obtain";
    private static final String SOUL_RITUAL_MESSAGE = "message.confluence.soul_ritual.activate";
    private static final String SOUL_RITUAL_UNLOCK_MESSAGE = "message.confluence.soul_ritual.unlock";
    private static final String SOUL_RITUAL_SLOT_MESSAGE = "message.confluence.soul_ritual.slot";

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

    /// 根据可装备槽数补齐或裁剪灵魂技能槽位。
    public static void updateSoulSkillSlots(@Nullable Player player) {
        if (player == null) {
            return;
        }
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        int equippedSkillMaxNumber = storage.getEquippedSoulSkillMaxNumber();
        List<SoulSkillStack> equippedSkills = storage.getEquippedSoulSkills();
        if (equippedSkillMaxNumber <= 0) {
            equippedSkills.clear();
            return;
        }

        int currentSize = equippedSkills.size();
        int disparity = equippedSkillMaxNumber - currentSize;

        if (disparity > 0) {
            for (int i = 0; i < disparity; i++) {
                equippedSkills.add(SoulSkillStack.Empty);
            }
        } else if (disparity < 0) {
            for (int i = 0; i > disparity; i--) {
                equippedSkills.removeLast();
            }
        }
    }

    /// 读取可装备的灵魂技能槽数；无玩家时使用默认值。
    public static int getEquippedSoulSkillMaxNumber(@Nullable Player player) {
        return player == null ? SoulSkillStorage.DEFAULT_EQUIPPED_SOUL_SKILL_COUNT : SoulSkillStorage.of(player).getEquippedSoulSkillMaxNumber();
    }

    /// 设置可装备槽数，实际槽位由 updateSoulSkillSlots 整理。
    public static void setEquippedSoulSkillMaxNumber(@Nullable Player player, int equippedSkillMaxNumber) {
        if (player != null) {
            SoulSkillStorage.of(player).setEquippedSoulSkillMaxNumber(Math.max(0, equippedSkillMaxNumber));
        }
    }

    /// 读取玩家当前选中的灵魂技能槽位索引。
    public static int getCurrentSoulSkillIndex(@Nullable Player player) {
        return player == null ? 0 : SoulSkillStorage.of(player).getCurrentSoulSkillIndex();
    }

    /// 获取当前选中槽位的技能栈；无玩家或槽位无效时返回 Empty。
    public static SoulSkillStack getCurrentSkillStack(@Nullable Player player) {
        return getSkillStack(player, getCurrentSoulSkillIndex(player));
    }

    /// 设置当前选中槽位，并按可装备槽数限制索引范围。
    public static void setCurrentSoulSkillIndex(@Nullable Player player, int currentIndex) {
        if (player != null) {
            SoulSkillStorage.of(player).setCurrentSoulSkillIndex(Math.clamp(currentIndex, 0, Math.max(0, getEquippedSoulSkillMaxNumber(player) - 1)));
        }
    }

    /// 列出指定灵魂技能已装备的全部槽位索引。
    public static List<Integer> getSoulSkillIndexes(@Nullable Player player, SoulSkill skill) {
        List<Integer> indexes = new ArrayList<>();
        List<SoulSkillStack> equippedSkills = getEquippedSoulSkills(player);
        for (int i = 0; i < equippedSkills.size(); i++) {
            SoulSkillStack equipped = equippedSkills.get(i);
            if (!equipped.getSoulSkill().equals(skill)) {
                continue;
            }
            indexes.add(i);
        }
        return Collections.unmodifiableList(indexes);
    }

    /// 返回玩家灵魂技能装备槽的只读列表视图。
    public static List<SoulSkillStack> getEquippedSoulSkills(@Nullable Player player) {
        return player == null ? Collections.emptyList() : Collections.unmodifiableList(SoulSkillStorage.of(player).getEquippedSoulSkills());
    }

    /// 下一次施法的灵魂是否不够付：为 true 时这一下会走生命代偿（扣血）。
    ///
    /// 只按「当前选中技能 vs 当前灵魂」预测，供 HUD 做透支警示；
    /// 创造模式、非法术状态、零消耗技能都不提示。
    public static boolean isSoulOverdraftPending(@Nullable Player player) {
        if (player == null || !isSoulMode(player) || player.isCreative()) {
            return false;
        }
        SoulSkillStack stack = getCurrentSkillStack(player);
        if (stack.isEmpty() || stack.getSoulSkill().isPassive()) {
            return false;
        }
        float soulCost = stack.getSoulSkill().getSoulCost(stack);
        return soulCost > 0.0F && getSoul(player) < soulCost;
    }

    /// 将非空技能栈装入指定槽位；空槽使用 Empty。
    public static boolean equipSoulSkill(@Nullable Player player, int index, SoulSkillStack skillStack) {
        return setSoulSkillStack(player, index, skillStack);
    }

    /// 清空指定灵魂技能槽，保留槽位本身。
    public static boolean unequipSoulSkill(@Nullable Player player, int index) {
        return setSoulSkillStack(player, index, SoulSkillStack.Empty);
    }

    /// 替换指定槽位内容并保留传入的技能栈实例；空槽使用 Empty。
    public static boolean setSoulSkillStack(@Nullable Player player, int index, SoulSkillStack skillStack) {
        if (player == null) {
            return false;
        }
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        List<SoulSkillStack> equippedSkills = storage.getEquippedSoulSkills();
        if (index < 0 || index >= storage.getEquippedSoulSkillMaxNumber()) {
            return false;
        }
        if (index >= equippedSkills.size()) {
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            refreshSoulSkills(serverPlayer);
        }
        /// 被动技能同样占槽：只有装备后才结算效果，见 isPassiveSoulSkillActive。
        if (!skillStack.isEmpty() && !isSoulSkillAvailable(player, skillStack.getSoulSkill().getId())) {
            return false;
        }
        equippedSkills.set(index, skillStack);
        mirrorSoulSkillCooldowns(player);
        return true;
    }

    /// 按技能 ID 判断是否已装备。
    public static boolean isSoulSkillEquipped(@Nullable Player player, ResourceLocation skillId) {
        for (SoulSkillStack equipped : getEquippedSoulSkills(player)) {
            if (!equipped.getSoulSkill().getId().equals(skillId)) {
                continue;
            }
            return true;
        }
        return false;
    }

    /// 判断指定技能栈实例是否已装备。
    public static boolean isSoulSkillEquipped(@Nullable Player player, SoulSkillStack stack) {
        for (SoulSkillStack equipped : getEquippedSoulSkills(player)) {
            if (!equipped.equals(stack)) {
                continue;
            }
            return true;
        }
        return false;
    }

    /// 按技能 ID 判断指定灵魂技能是否已装备。
    public static boolean isSoulSkillEquipped(@Nullable Player player, SoulSkill skill) {
        ResourceLocation id = skill.getId();
        for (SoulSkillStack equipped : getEquippedSoulSkills(player)) {
            if (equipped.getSoulSkill().getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /// ---- 解锁技能 ----

    /// 将灵魂技能登记到玩家的已解锁集合。
    public static void addUnlockedSoulSkill(@Nullable Player player, SoulSkill skill) {
        if (player != null) {
            SoulSkillStorage.of(player).getUnlockedSoulSkills().put(skill.getId(), skill);
        }
    }

    /// 移除玩家对应的技能解锁记录，不改动装备槽位。
    public static void removeUnlockedSoulSkill(@Nullable Player player, ResourceLocation skillId) {
        if (player != null) {
            SoulSkillStorage.of(player).getUnlockedSoulSkills().remove(skillId);
        }
    }

    /// 判断玩家是否已登记对应的灵魂技能解锁记录。
    public static boolean isSoulSkillUnlocked(@Nullable Player player, ResourceLocation skillId) {
        return player != null && SoulSkillStorage.of(player).getUnlockedSoulSkills().containsKey(skillId);
    }

    /// 读取玩家的技能解锁集合；有玩家时返回可修改的原集合。
    public static Map<ResourceLocation, SoulSkill> getUnlockedSoulSkills(@Nullable Player player) {
        return player == null ? Collections.emptyMap() : SoulSkillStorage.of(player).getUnlockedSoulSkills();
    }

    /// 读取指定装备槽的技能栈；无玩家或槽位无效时返回 Empty。
    public static SoulSkillStack getSkillStack(@Nullable Player player, int index) {
        if (player == null) {
            return SoulSkillStack.Empty;
        }
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        List<SoulSkillStack> equippedSkills = storage.getEquippedSoulSkills();
        if (index < 0 || index >= storage.getEquippedSoulSkillMaxNumber()) {
            return SoulSkillStack.Empty;
        }
        if (index >= equippedSkills.size()) {
            return SoulSkillStack.Empty;
        }
        return equippedSkills.get(index);
    }

    /// 读取当前资源模式；未创建附件的玩家使用魔力模式。
    public static boolean isSoulMode(@Nullable Player player) {
        return player != null && player.hasData(ModAttachmentTypes.SOUL_STORAGE) && SoulStorage.of(player).isSoulMode();
    }

    public static float getSoul(@Nullable Player player) {
        return player == null ? 0.0F : SoulStorage.of(player).getCurrentSoul();
    }

    public static void setSoul(Player player, float amount) {
        SoulStorage.of(player).setCurrentSoul(amount);
    }

    public static void adjustSoul(Player player, float amount) {
        setSoul(player, getSoul(player) + amount);
    }

    /// 切换资源模式，默认不补充目标资源。
    public static boolean toggleSoulMode(ServerPlayer player) {
        return switchSoulMode(player, !isSoulMode(player));
    }

    public static boolean switchSoulMode(ServerPlayer player, boolean soulMode) {
        return switchSoulMode(player, soulMode, 0.0F);
    }

    /// 提供切换后立即补充目标资源的扩展入口，不表示一比一兑换。
    public static boolean switchSoulMode(ServerPlayer player, boolean soulMode, float targetResource) {
        if (!Confluence.SOUL_SKILLS || isSoulMode(player) == soulMode) {
            return false;
        }

        SoulStorage storage = SoulStorage.of(player);
        storage.setSoulMode(soulMode);
        if (soulMode) {
            IronSpellHelper.cancelCasting(player);
        }
        storage.setCurrentSoul(soulMode ? targetResource : 0.0F);
        ManaStorage.of(player).setCurrentMana(soulMode ? 0.0F : targetResource);
        syncMana2Client(player);
        return true;
    }

    /// 兑换比例尚未定义，保留入口供后续机制实现。
    public static boolean convertManaToSoul(ServerPlayer player, float amount) {
        return false;
    }

    /// 兑换比例尚未定义，保留入口供后续机制实现。
    public static boolean convertSoulToMana(ServerPlayer player, float amount) {
        return false;
    }

    public static boolean isHoldingSoulWeapon(@Nullable Player player) {
        return player != null && player.getMainHandItem().getItem() instanceof SoulWeaponItem;
    }

    public static boolean isSoulSkillLearned(@Nullable Player player, ResourceLocation skillId) {
        return player != null && SoulSkillStorage.of(player).getUnlockedSoulSkills().containsKey(skillId);
    }

    /// 永久学习与当前可用性分别保存，条件失效不改变装备槽位。
    public static boolean isSoulSkillAvailable(@Nullable Player player, ResourceLocation skillId) {
        return player != null && SoulSkillStorage.of(player).getAvailableSkills().contains(skillId);
    }

    /// 被动技能是否正在生效：既已学会（可用），又装备在灵魂技能槽里。
    ///
    /// 被动技能的效果一律走这里判定，不再用“已学会”直接放行；卸下即失效。
    public static boolean isPassiveSoulSkillActive(@Nullable Player player, ResourceLocation skillId) {
        return isSoulSkillAvailable(player, skillId) && isSoulSkillEquipped(player, skillId);
    }

    /// 被动技能是否正在生效，参数为技能定义。
    public static boolean isPassiveSoulSkillActive(@Nullable Player player, SoulSkill skill) {
        return skill != null && isPassiveSoulSkillActive(player, skill.getId());
    }

    public static int getSoulSkillCooldown(@Nullable Player player, ResourceLocation skillId) {
        if (player == null) {
            return 0;
        }
        SoulSkillStack stack = SoulSkillStorage.of(player).getSkillCooldowns().get(skillId);
        return stack == null ? 0 : stack.getCd();
    }

    public static int getSoulSkillMaxCooldown(@Nullable Player player, ResourceLocation skillId) {
        if (player == null) {
            return 0;
        }
        SoulSkillStack stack = SoulSkillStorage.of(player).getSkillCooldowns().get(skillId);
        return stack == null ? 0 : stack.getMaxCd();
    }

    /// 重算武器临时授予和技能条件；调用方合并本次技能数据同步。
    public static boolean refreshSoulSkills(ServerPlayer player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        Set<ResourceLocation> temporary = new HashSet<>();
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof SoulWeaponItem weapon) {
            temporary.addAll(weapon.getGrantedSoulSkills(mainHand));
        }
        Set<ResourceLocation> available = new HashSet<>(storage.getUnlockedSoulSkills().keySet());
        available.addAll(temporary);
        /// 刻骨的记忆：每次刷新时维护它当前放行的那一个技能。
        ResourceLocation memorySkill = resolveMemoryOfBonebrandSkill(player);
        if (memorySkill != null) {
            available.add(memorySkill);
        }
        available.removeIf(id -> {
            SoulSkill skill = SoulSkillStorage.findSoulSkill(id);
            return skill == null || skill == ModSoulSkills.EMPTY.get() || !skill.isAvailable(player);
        });
        boolean changed = !temporary.equals(storage.getTemporarySkills()) || !available.equals(storage.getAvailableSkills());
        storage.getTemporarySkills().clear();
        storage.getTemporarySkills().addAll(temporary);
        storage.getAvailableSkills().clear();
        storage.getAvailableSkills().addAll(available);
        mirrorSoulSkillCooldowns(player);
        return changed;
    }

    /// 佩戴刻骨的记忆时，保证「武器临时技能」里有一个被放行；没佩戴则清空选择。
    ///
    /// 候选是背包里所有 {@link SoulWeaponItem} 提供的技能中**尚未永久学会**的那些；
    /// 每次刷新只在选择失效时重掷，真正使用技能后由 {@link #rerollMemoryOfBonebrandSkill} 重掷。
    @Nullable
    private static ResourceLocation resolveMemoryOfBonebrandSkill(ServerPlayer player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        if (!TCUtils.hasType(player, AccessoryItems.MEMORY$OF$BONEBRAND)) {
            if (storage.getMemoryOfBonebrandSkill() != null) {
                storage.setMemoryOfBonebrandSkill(null);
            }
            return null;
        }
        List<ResourceLocation> candidates = collectMemoryOfBonebrandCandidates(player);
        ResourceLocation current = storage.getMemoryOfBonebrandSkill();
        if (current != null && candidates.contains(current)) {
            return current;
        }
        ResourceLocation picked = pickMemoryOfBonebrandSkill(player, candidates);
        storage.setMemoryOfBonebrandSkill(picked);
        return picked;
    }

    /// 使用技能后重掷刻骨的记忆放行的技能（未佩戴时清空）。
    public static void rerollMemoryOfBonebrandSkill(ServerPlayer player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        if (!TCUtils.hasType(player, AccessoryItems.MEMORY$OF$BONEBRAND)) {
            storage.setMemoryOfBonebrandSkill(null);
            return;
        }
        storage.setMemoryOfBonebrandSkill(pickMemoryOfBonebrandSkill(player, collectMemoryOfBonebrandCandidates(player)));
    }

    /// 该技能是否是刻骨的记忆当前放行的技能。
    public static boolean isMemoryOfBonebrandSkill(Player player, @Nullable SoulSkill skill) {
        if (skill == null || !TCUtils.hasType(player, AccessoryItems.MEMORY$OF$BONEBRAND)) {
            return false;
        }
        return skill.getId().equals(SoulSkillStorage.of(player).getMemoryOfBonebrandSkill());
    }

    /// 收集「只由武器临时提供」的候选技能：背包（含副手）里魂师武器授予的技能，排除已永久学会的。
    private static List<ResourceLocation> collectMemoryOfBonebrandCandidates(Player player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        Set<ResourceLocation> candidates = new LinkedHashSet<>();
        for (ItemStack stack : player.getInventory().items) {
            collectGrantedSkills(stack, storage, candidates);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            collectGrantedSkills(stack, storage, candidates);
        }
        return new ArrayList<>(candidates);
    }

    private static void collectGrantedSkills(ItemStack stack, SoulSkillStorage storage, Set<ResourceLocation> out) {
        if (!(stack.getItem() instanceof SoulWeaponItem weapon)) {
            return;
        }
        for (ResourceLocation id : weapon.getGrantedSoulSkills(stack)) {
            if (SoulSkillStorage.findSoulSkill(id) != null && !storage.getUnlockedSoulSkills().containsKey(id)) {
                out.add(id);
            }
        }
    }

    /// 从候选里均匀随机取一个；没有候选时返回 null。
    @Nullable
    private static ResourceLocation pickMemoryOfBonebrandSkill(Player player, List<ResourceLocation> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(player.getRandom().nextInt(candidates.size()));
    }

    /// 永久学习成功才消耗技能书，已有记录时不重复学习。
    public static boolean learnSoulSkill(ServerPlayer player, SoulSkill skill) {
        if (skill == ModSoulSkills.EMPTY.get() || isSoulSkillLearned(player, skill.getId())) {
            return false;
        }
        SoulSkillStorage.of(player).getUnlockedSoulSkills().put(skill.getId(), skill);
        refreshSoulSkills(player);
        SoulSkillSyncPacketS2C.sendToClient(player);
        return true;
    }

    /// 判断技能书给玩家的效果是否已经生效：技能书看永久学习记录，被动书看书籍登记。
    public static boolean isSoulBookGranted(Player player, SoulBooks.Definition book) {
        return book.skill() == null ? isSoulBookLearned(player, book.id()) : isSoulSkillLearned(player, book.skill());
    }

    /// 首次使用技能书：技能书复用永久学习入口，被动书只登记书籍 ID。
    public static boolean grantSoulBook(ServerPlayer player, SoulBooks.Definition book) {
        if (book.skill() != null) {
            SoulSkill skill = SoulSkillStorage.findSoulSkill(book.skill());
            return skill != null && learnSoulSkill(player, skill);
        }
        if (!SoulSkillStorage.of(player).learnSoulBook(book.id())) {
            return false;
        }
        SoulSkillSyncPacketS2C.sendToClient(player);
        return true;
    }

    /// 判断被动技能书是否已被玩家登记。
    public static boolean isSoulBookLearned(Player player, ResourceLocation bookId) {
        return player != null && SoulSkillStorage.of(player).getLearnedSoulBooks().contains(bookId);
    }

    /// 复习：重复使用技能书时刷新为固定时长，不叠加层数。
    public static void applySoulBookReview(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(ModEffects.GO_OVER, SoulSkillParameters.SOUL_BOOK_REVIEW_DURATION_TICKS, 0, false, true, true));
    }

    /// 重置灵魂技能装备状态：清空槽位与冷却、索引归零；永久学习与书籍记录保持不变。
    public static void resetSoulSkillEquip(ServerPlayer player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        storage.getEquippedSoulSkills().clear();
        storage.setCurrentSoulSkillIndex(0);
        storage.getSkillCooldowns().clear();
        updateSoulSkillSlots(player);
        refreshSoulSkills(player);
        SoulSkillSyncPacketS2C.sendToClient(player);
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
    }

    /// 设置可装备槽数并同步两端，槽数范围由调用方限制。
    public static void setSoulSkillSlotCount(ServerPlayer player, int count) {
        setEquippedSoulSkillMaxNumber(player, count);
        updateSoulSkillSlots(player);
        setCurrentSoulSkillIndex(player, getCurrentSoulSkillIndex(player));
        refreshSoulSkills(player);
        SoulSkillSyncPacketS2C.sendToClient(player);
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
    }

    /// 首次获得登记过的主武器即永久学会其附带技能，并在真正学会时提示一次。
    public static void learnSoulSkillsOnFirstObtain(ServerPlayer player) {
        if (!Confluence.SOUL_SKILLS || SoulItems.FIRST_OBTAIN_SOUL_SKILLS.isEmpty()) {
            return;
        }
        for (var entry : SoulItems.FIRST_OBTAIN_SOUL_SKILLS.entrySet()) {
            List<ResourceLocation> skillIds = entry.getValue();
            if (skillIds.stream().allMatch(id -> isSoulSkillLearned(player, id))) {
                continue;
            }
            Item item = entry.getKey().get();
            if (!hasItemInInventory(player, item)) {
                continue;
            }
            Component itemName = Component.translatable(item.getDescriptionId());
            for (ResourceLocation skillId : skillIds) {
                SoulSkill skill = SoulSkillStorage.findSoulSkill(skillId);
                if (skill != null && learnSoulSkill(player, skill)) {
                    player.displayClientMessage(Component.translatable(FIRST_OBTAIN_SOUL_SKILL_MESSAGE, itemName, skill.getNameComponent(skill.getStack())), true);
                }
            }
        }
    }

    /// 判断主背包与副手是否持有目标物品，不比较物品组件。
    private static boolean hasItemInInventory(Player player, Item item) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(item)) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(item)) {
                return true;
            }
        }
        return false;
    }

    /// 冷却按技能 ID 共享，卸下和重复装备不会清除冷却。
    private static void setSoulSkillCooldown(Player player, SoulSkill skill, int ticks) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        if (ticks > 0) {
            ticks = getSoulCooldownTicks(player, ticks);
            SoulSkillStack cooldown = skill.getStack();
            cooldown.setCd(ticks);
            cooldown.setMaxCd(ticks);
            storage.getSkillCooldowns().put(skill.getId(), cooldown);
        } else {
            storage.getSkillCooldowns().remove(skill.getId());
        }
        mirrorSoulSkillCooldowns(player);
    }

    private static void mirrorSoulSkillCooldowns(Player player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        for (SoulSkillStack stack : storage.getEquippedSoulSkills()) {
            if (!stack.isEmpty()) {
                SoulSkillStack cooldown = storage.getSkillCooldowns().get(stack.getSoulSkill().getId());
                stack.setCd(cooldown == null ? 0 : cooldown.getCd());
                stack.setMaxCd(cooldown == null ? 0 : cooldown.getMaxCd());
            }
        }
    }

    /// 服务端推进冷却，仅结束或可用性改变时发送校正。
    public static void tickSoulSkills(ServerPlayer player) {
        if (!Confluence.SOUL_SKILLS) {
            return;
        }
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        boolean changed = refreshSoulSkills(player);
        var iterator = storage.getSkillCooldowns().values().iterator();
        while (iterator.hasNext()) {
            SoulSkillStack stack = iterator.next();
            stack.setCd(stack.getCd() - 1);
            if (stack.getCd() <= 0) {
                iterator.remove();
                changed = true;
            }
        }
        mirrorSoulSkillCooldowns(player);
        if (changed) {
            SoulSkillSyncPacketS2C.sendToClient(player);
        }
    }

    /// 返回尚未乘灵魂属性倍率的技能与主武器总伤害。
    public static float getSoulSkillDamage(Player player, SoulSkillStack stack) {
        ItemStack mainHand = player.getMainHandItem();
        float weaponDamage = mainHand.getItem() instanceof SoulWeaponItem weapon ? weapon.getSoulDamage(mainHand) : 0.0F;
        SoulSkillDamageEvent event = NeoForge.EVENT_BUS.post(new SoulSkillDamageEvent(player, stack, stack.getDamage(), weaponDamage));
        float amount = Math.max(0.0F, event.getAmount());
        /// 引渡者：引魂标签内的技能基础伤害固定 +1，不看技能分类
        if (isSoulBookLearned(player, SoulBooks.PSYCHOPOMP) && stack.getSoulSkill().is(ModTags.SoulSkills.SOUL_LURE)) {
            amount += SoulSkillParameters.PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS;
        }
        /// 星魂联结：灵魂伤害共用一半的魔法伤害加成
        if (isPassiveSoulSkillActive(player, ModSoulSkills.STAR_LINK.get())) {
            double magicBonus = player.getAttributeValue(LibAttributes.getMagicDamage());
            amount *= (float) (1.0 + magicBonus * SoulSkillParameters.STAR_LINK_SHARE_RATIO);
        }
        /// 分魂驾驭：技能伤害翻倍（冷却同时延长）
        if (isPassiveSoulSkillActive(player, ModSoulSkills.SOUL_CONTROL.get())) {
            amount *= SoulSkillParameters.SOUL_CONTROL_DAMAGE_MULTIPLIER;
        }
        return amount;
    }

    /// 先扣可用灵魂，返回需在效果执行后结算的生命代偿；负值表示取消。
    private static float prepareSoulConsumption(ServerPlayer player, float amount) {
        if (!isSoulMode(player)) {
            return SOUL_CONSUMPTION_FAILED;
        }
        /// 灵魂透支：持续期间完全不消耗灵魂
        if (amount > 0.0F && isSoulSkillLearned(player, ModSoulSkills.SOUL_DRAIN.get().getId()) && player.hasEffect(ModEffects.SOUL_DRAIN)) {
            return 0.0F;
        }
        /// 自然法则：概率不消耗灵魂
        if (amount > 0.0F && isPassiveSoulSkillActive(player, ModSoulSkills.LAW_OF_NATURE.get())
                && player.getRandom().nextFloat() < SoulSkillParameters.LAW_OF_NATURE_FREE_CHANCE) {
            return 0.0F;
        }
        SoulConsumeEvent event = NeoForge.EVENT_BUS.post(new SoulConsumeEvent(player, amount, HEALTH_PER_SOUL));
        if (event.isCanceled()) {
            return SOUL_CONSUMPTION_FAILED;
        }
        /// 创造模式：照常发事件与走施法流程，但不扣灵魂、不结算生命代偿
        if (player.isCreative() || player.hasInfiniteMaterials()) {
            return 0.0F;
        }
        float cost = Math.max(0.0F, event.getAmount());
        SoulStorage storage = SoulStorage.of(player);
        float paidSoul = Math.min(storage.getCurrentSoul(), cost);
        storage.extract(paidSoul);
        onSoulSpent(player, paidSoul);
        return (cost - paidSoul) * Math.max(0.0F, event.getHealthPerSoul());
    }

    /// 蓝隐匿者全套：每累计消耗 10 点灵魂，削减 3 秒耐药性剩余时长。
    public static void onSoulSpent(ServerPlayer player, float paidSoul) {
        if (paidSoul <= 0.0F) {
            return;
        }
        SoulStorage storage = SoulStorage.of(player);
        float unit = SoulSkillParameters.BLUE_HERMIT_SOUL_PER_REDUCTION;
        float accumulated = storage.getConsumedSoul() + paidSoul;
        int triggers = (int) (accumulated / unit);
        storage.setConsumedSoul(accumulated - triggers * unit);
        if (triggers <= 0 || !isWearingBlueHermitSet(player)) {
            return;
        }
        MobEffectInstance sickness = player.getEffect(ModEffects.POTION_SICKNESS);
        if (sickness == null) {
            return;
        }
        int reduction = triggers * SoulSkillParameters.BLUE_HERMIT_SICKNESS_REDUCTION_TICKS;
        int duration = sickness.isInfiniteDuration() ? sickness.getDuration() : Math.max(0, sickness.getDuration() - reduction);
        player.removeEffect(ModEffects.POTION_SICKNESS);
        if (duration > 0) {
            player.addEffect(new MobEffectInstance(ModEffects.POTION_SICKNESS, duration, sickness.getAmplifier(), sickness.isAmbient(), sickness.isVisible(), sickness.showIcon()));
        }
    }

    /// 判断玩家是否穿戴完整的蓝隐匿者四件套。
    public static boolean isWearingBlueHermitSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.BLUE_HERMIT_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.BLUE_HERMIT_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.BLUE_HERMIT_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.BLUE_HERMIT_BOOTS.get());
    }

    /// 判断玩家是否穿戴完整的幻翼四件套。
    public static boolean isWearingPhantomSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.PHANTOM_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.PHANTOM_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.PHANTOM_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.PHANTOM_BOOTS.get());
    }

    /// 判断玩家是否穿戴完整的白南瓜四件套。
    public static boolean isWearingWhitePumpkinSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.WHITE_PUMPKIN_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.WHITE_PUMPKIN_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.WHITE_PUMPKIN_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.WHITE_PUMPKIN_BOOTS.get());
    }

    /// 判断玩家是否穿戴完整的逐灵四件套。
    public static boolean isWearingSeekerSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.SEEKER_HELLMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.SEEKER_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.SEEKER_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.SEEKER_BOOTS.get());
    }

    /// 判断玩家是否穿戴完整的魂舞两件套（头 + 长袍）。
    public static boolean isWearingSouldancerSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.SOULDANCER_HOOD.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.SOULDANCER_ROBE.get());
    }

    /// 收集灵魂时刷新魂舞限时增益。
    public static void onSoulCollected(Player player, long gameTime) {
        if (isWearingSouldancerSet(player)) {
            SoulStorage.of(player).setSouldancerBuffEnd(gameTime + SoulSkillParameters.SOULDANCER_BUFF_DURATION_TICKS);
        }
    }

    /// 每刻同步魂舞增益的临时属性修饰符，到期自行移除。
    public static void tickSoulArmorSets(ServerPlayer player, long gameTime) {
        boolean active = gameTime < SoulStorage.of(player).getSouldancerBuffEnd();
        applyTransientModifier(player, LibAttributes.getCriticalChance(), SOULDANCER_CRIT_ID, SoulSkillParameters.SOULDANCER_CRIT_BONUS, AttributeModifier.Operation.ADD_VALUE, active);
        applyTransientModifier(player, Attributes.MOVEMENT_SPEED, SOULDANCER_SPEED_ID, SoulSkillParameters.SOULDANCER_SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
    }

    private static void applyTransientModifier(Player player, Holder<Attribute> attribute, ResourceLocation id, double value, AttributeModifier.Operation operation, boolean active) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        if (active) {
            instance.addOrUpdateTransientModifier(new AttributeModifier(id, value, operation));
        } else if (instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
    }

    /// 生命代偿使用独立伤害来源，由伤害标签与 NeoForge 事件结算。
    private static void applySoulOverdraft(ServerPlayer player, float healthCost) {
        if (healthCost <= 0.0F || !player.isAlive()) {
            return;
        }
        /// 反噬抵抗：生命代偿伤害减免
        if (isPassiveSoulSkillActive(player, ModSoulSkills.BACKLASH_RESISTANCE.get())) {
            healthCost *= 1.0F - SoulSkillParameters.BACKLASH_RESISTANCE_REDUCTION;
        }
        player.hurt(LibDamageTypes.soulOverdraft(player.level()), healthCost);
    }

    public static boolean consumeSoul(ServerPlayer player, float amount) {
        float healthCost = prepareSoulConsumption(player, amount);
        if (healthCost < 0.0F) {
            return false;
        }
        applySoulOverdraft(player, healthCost);
        return true;
    }

    /// 成功后提交冷却、两端执行，再结算可能致死的生命代偿。
    public static boolean tryUseSoulSkill(ServerPlayer player) {
        if (!Confluence.SOUL_SKILLS || !isSoulMode(player) || player.hasEffect(ModEffects.SOUL_DRAIN_LOCK)) {
            return false;
        }
        boolean availabilityChanged = refreshSoulSkills(player);
        SoulSkillStack skillStack = getCurrentSkillStack(player);
        /// 当前槽是空槽或被动时，同一次按键直接跳到下一个可释放的技能，避免按键毫无反应。
        if (skillStack.isEmpty() || skillStack.getSoulSkill().isPassive()) {
            int next = findNextCastableSoulSkillIndex(player);
            if (next < 0) {
                return rejectSoulSkillUse(player, availabilityChanged);
            }
            setCurrentSoulSkillIndex(player, next);
            SoulSkillIndexSyncPacketS2C.sendToClient(player);
            skillStack = getSkillStack(player, next);
        }
        if (skillStack.isEmpty()) {
            return rejectSoulSkillUse(player, availabilityChanged);
        }
        /// 魂术轮转：当前技能还在冷却时，直接跳到下一个能马上施放的技能，不让这次按键空转。
        if (isPassiveSoulSkillActive(player, ModSoulSkills.SOUL_ROTATION.get())
                && getSoulSkillCooldown(player, skillStack.getSoulSkill().getId()) > 0) {
            int next = findNextReadySoulSkillIndex(player, getCurrentSoulSkillIndex(player));
            if (next >= 0) {
                setCurrentSoulSkillIndex(player, next);
                SoulSkillIndexSyncPacketS2C.sendToClient(player);
                skillStack = getSkillStack(player, next);
            }
        }
        SoulSkill skill = skillStack.getSoulSkill();
        /// 不持武器时只能放刻骨的记忆当前放行的那一个技能。
        if (!isHoldingSoulWeapon(player) && !isMemoryOfBonebrandSkill(player, skill)) {
            return rejectSoulSkillUse(player, availabilityChanged);
        }
        if (!isSoulSkillAvailable(player, skill.getId()) || getSoulSkillCooldown(player, skill.getId()) > 0 || !skill.canUse(player, skillStack)) {
            return rejectSoulSkillUse(player, availabilityChanged);
        }
        int castIndex = getCurrentSoulSkillIndex(player);
        if (!castSoulSkill(player, skillStack)) {
            return rejectSoulSkillUse(player, availabilityChanged);
        }
        /// 刻骨的记忆：用掉一次技能后重掷它放行的那个技能。
        rerollMemoryOfBonebrandSkill(player);
        /// 束魂锁链 / 魂术轮转：整套附魂技能的联动
        applySoulSkillSynergy(player, skill, castIndex);
        return true;
    }

    /// 一次成功的单体施放：结算耗魂、冷却、两端效果与生命代偿；取消时返回 false。
    private static boolean castSoulSkill(ServerPlayer player, SoulSkillStack skillStack) {
        SoulSkill skill = skillStack.getSoulSkill();
        float healthCost = prepareSoulConsumption(player, skill.getSoulCost(skillStack));
        if (healthCost < 0.0F) {
            return false;
        }
        setSoulSkillCooldown(player, skill, skill.getCooldown(skillStack));
        SoulSkillStack castStack = skillStack.copy();
        SoulSkillSyncPacketS2C.sendToClient(player);
        skill.use(player, skillStack);
        SoulSkillUsePacketS2C.sendToClient(player, castStack);
        applySoulOverdraft(player, healthCost);
        return true;
    }

    /// 施放成功后的饰品与被动联动。
    ///
    /// - `束魂锁链`：施放任意「附魂」技能时，把其余已装备的「附魂」技能同时放出。
    /// - `魂术轮转`：施放「强力附魂系列」技能时，沿技能栏把该系列其余技能依次连锁放出，结束后选择回到起手技能；
    ///   非该系列技能仍沿用原来的「自动切到下一个可释放技能」，且同样会跳过冷却中的技能。
    ///
    /// 连锁施放统一走 `castSoulSkill`，不会再回到本方法，因此不存在自递归。
    private static void applySoulSkillSynergy(ServerPlayer player, SoulSkill primary, int castIndex) {
        if (TCUtils.hasType(player, AccessoryItems.SOUL$BINDING$CHAIN) && primary.is(ModTags.SoulSkills.SOUL_ATTACH)) {
            for (SoulSkillStack stack : new ArrayList<>(getEquippedSoulSkills(player))) {
                if (stack.isEmpty() || stack.getSoulSkill() == primary) {
                    continue;
                }
                if (stack.getSoulSkill().is(ModTags.SoulSkills.SOUL_ATTACH)) {
                    castSoulSkillIfReady(player, stack);
                }
            }
        }
        if (!isPassiveSoulSkillActive(player, ModSoulSkills.SOUL_ROTATION.get())) {
            return;
        }
        if (primary.is(ModTags.SoulSkills.ENHANCED_SOUL_SERIES)) {
            chainCastEnhancedSoulSeries(player, castIndex);
        } else {
            rotateToNextSkill(player);
        }
    }

    /// 联动施放：只放当前「已学会 + 可用 + 不在冷却 + 非被动」的技能，各自结算自己的耗魂与冷却。
    private static void castSoulSkillIfReady(ServerPlayer player, SoulSkillStack stack) {
        SoulSkill skill = stack.getSoulSkill();
        if (skill.isPassive() || !isSoulSkillAvailable(player, skill.getId())
                || getSoulSkillCooldown(player, skill.getId()) > 0 || !skill.canUse(player, stack)) {
            return;
        }
        castSoulSkill(player, stack);
    }

    /// 魂术轮转：沿技能栏把「强力附魂系列」技能逐个连锁放出，结束后落到下一个能施放的槽位。
    private static void chainCastEnhancedSoulSeries(ServerPlayer player, int startIndex) {
        List<SoulSkillStack> equipped = new ArrayList<>(getEquippedSoulSkills(player));
        int size = equipped.size();
        if (size <= 0) {
            return;
        }
        for (int step = 1; step <= size; step++) {
            SoulSkillStack candidate = equipped.get((startIndex + step) % size);
            if (candidate.isEmpty() || !candidate.getSoulSkill().is(ModTags.SoulSkills.ENHANCED_SOUL_SERIES)) {
                continue;
            }
            castSoulSkillIfReady(player, candidate);
        }
        /// 连锁过后起手技能本身也在冷却中，所以优先挑一个不在冷却的槽位；全都在冷却才留在起手技能。
        int next = findNextReadySoulSkillIndex(player, startIndex);
        setCurrentSoulSkillIndex(player, next >= 0 ? next : startIndex);
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
    }

    /// 从当前槽向后（含回绕）找第一个「非空、非被动、当前可用」的槽位；找不到返回 -1。
    ///
    /// 冷却中的技能**不**跳过：轮到它就停在它上面，避免按一次键把选中位一路甩到别的技能。
    private static int findNextCastableSoulSkillIndex(ServerPlayer player) {
        List<SoulSkillStack> equipped = getEquippedSoulSkills(player);
        int size = equipped.size();
        if (size <= 0) {
            return -1;
        }
        int current = getCurrentSoulSkillIndex(player);
        for (int step = 1; step <= size; step++) {
            int index = (current + step) % size;
            SoulSkillStack candidate = equipped.get(index);
            if (candidate.isEmpty() || candidate.getSoulSkill().isPassive()) {
                continue;
            }
            /// 冷却中的技能不跳过：只按“空槽 / 被动 / 当前不可用”筛选。
            if (!isSoulSkillAvailable(player, candidate.getSoulSkill().getId())) {
                continue;
            }
            return index;
        }
        return -1;
    }

    /// 魂术轮转：从 `start` 之后（含回绕，不含 `start` 本身）找第一个「非空、非被动、当前可用且不在冷却」的槽位；
    /// 找不到返回 -1，调用方保持当前选中位不变。
    private static int findNextReadySoulSkillIndex(ServerPlayer player, int start) {
        List<SoulSkillStack> equipped = getEquippedSoulSkills(player);
        int size = equipped.size();
        for (int step = 1; step < size; step++) {
            int index = Math.floorMod(start + step, size);
            SoulSkillStack candidate = equipped.get(index);
            if (candidate.isEmpty() || candidate.getSoulSkill().isPassive()) {
                continue;
            }
            ResourceLocation id = candidate.getSoulSkill().getId();
            if (!isSoulSkillAvailable(player, id) || getSoulSkillCooldown(player, id) > 0) {
                continue;
            }
            return index;
        }
        return -1;
    }

    /// 魂术轮转：沿装备栏向后找下一个非空、非被动、且不在冷却中的技能槽；找不到就保持当前。
    private static void rotateToNextSkill(ServerPlayer player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        if (storage.getEquippedSoulSkills().size() <= 1) {
            return;
        }
        int next = findNextReadySoulSkillIndex(player, storage.getCurrentSoulSkillIndex());
        if (next < 0) {
            return;
        }
        setCurrentSoulSkillIndex(player, next);
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
    }

    private static boolean rejectSoulSkillUse(ServerPlayer player, boolean availabilityChanged) {
        if (availabilityChanged) {
            SoulSkillSyncPacketS2C.sendToClient(player);
        }
        return false;
    }

    /// 以物品栏格数统计正在冷却的法器，数量不按堆叠个数计算。
    public static int countCoolingSoulArtifacts(Player player) {
        int count = 0;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof SoulArtifactItem && player.getCooldowns().isOnCooldown(stack.getItem())) {
                count++;
            }
        }
        return count;
    }

    public static boolean tryUseSoulArtifact(ServerPlayer player, ItemStack stack) {
        if (!Confluence.SOUL_SKILLS || !isSoulMode(player) || !(stack.getItem() instanceof SoulArtifactItem artifact)
                || player.getCooldowns().isOnCooldown(stack.getItem())
                || countCoolingSoulArtifacts(player) >= SoulSkillParameters.MAX_COOLING_SOUL_ARTIFACT_SLOTS
                || !artifact.canUse(player, stack)) {
            return false;
        }
        float healthCost = prepareSoulConsumption(player, artifact.getSoulCost(stack));
        if (healthCost < 0.0F) {
            return false;
        }
        /// 冰冷魂戒：法器冷却同样缩短
        int cooldown = getSoulCooldownTicks(player, artifact.getCooldown(stack));
        player.getCooldowns().addCooldown(stack.getItem(), cooldown);
        artifact.useArtifact(player, stack);
        applySoulOverdraft(player, healthCost);
        return true;
    }

    /// 统一结算「分魂驾驭延长」与「冰冷魂戒缩短」后的灵魂技能冷却。
    private static int getSoulCooldownTicks(Player player, int ticks) {
        if (ticks <= 0) {
            return ticks;
        }
        if (isPassiveSoulSkillActive(player, ModSoulSkills.SOUL_CONTROL.get())) {
            ticks = Math.round(ticks * SoulSkillParameters.SOUL_CONTROL_COOLDOWN_MULTIPLIER);
        }
        if (TCUtils.hasType(player, AccessoryItems.FROSTBITE$SOUL$RING)) {
            ticks = Math.round(ticks * (1.0F - SoulSkillParameters.FROSTBITE_SOUL_RING_COOLDOWN_REDUCTION));
        }
        return ticks;
    }

    /// 每秒按最终恢复比例结算一次，两个基础环境不叠加。
    public static void regenerateSoul(ServerPlayer player) {
        if (!Confluence.SOUL_SKILLS || !isSoulMode(player)
                || player.level().getGameTime() % SharedConstants.TICKS_PER_SECOND != 0) {
            return;
        }
        SoulStorage storage = SoulStorage.of(player);
        storage.freshMaxSoul();
        /// 灵魂手环：每 3 秒回复 1 点灵魂
        if (TCUtils.hasType(player, AccessoryItems.BAND$OF$SOUL) && player.level().getGameTime() % 60 == 0 && (player.level().getGameTime() / 60) % 3 == 0) {
            storage.receive(SoulSkillParameters.BAND_OF_SOUL_RECOVERY);
        }
        float ratio = storage.getRecoveryRatio();
        if (ModBlockCounters.isGraveyard(player.level(), player.blockPosition())
                || player.level().getBiome(player.blockPosition()).is(Biomes.SOUL_SAND_VALLEY)) {
            ratio += ENVIRONMENT_SOUL_RECOVERY_RATIO;
        }
        SoulRecoveryEvent event = NeoForge.EVENT_BUS.post(new SoulRecoveryEvent(player, ratio));
        if (!event.isCanceled() && event.getRecoveryRatio() > 0.0F) {
            storage.receive(Mth.ceil(storage.getMaxSoul() * event.getRecoveryRatio()));
        }
    }

    /// 击杀时直接补充灵魂；Boss 部件不参与普通敌怪收益。
    public static void collectSoul(LivingEntity victim, DamageSource source) {
        if (!Confluence.SOUL_SKILLS || !(victim.level() instanceof ServerLevel level)
                || victim instanceof Boss boss && !boss.isMainBody()
                || victim instanceof AbstractTwinEye eye && !eye.shouldShowMessage()
                || victim instanceof PartHitTarget part && part.encounterOwner() instanceof Boss && part.encounterOwner() != victim
                || !EnemyDamageRules.isEnemy(victim)) {
            return;
        }
        Entity owner = LibEntityUtils.getOwner(source);
        if (!(owner instanceof ServerPlayer player) || !isSoulMode(player)) {
            return;
        }

        SoulStorage storage = SoulStorage.of(player);
        if (victim instanceof Boss) {
            storage.setCurrentSoul(storage.getMaxSoul());
            return;
        }
        double attack = victim.getAttribute(Attributes.ATTACK_DAMAGE) == null ? 0.0D : victim.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float amount = Math.max(MINIMUM_COLLECTED_SOUL, (float) ((victim.getMaxHealth() * SOUL_HEALTH_WEIGHT
                + attack * SOUL_ATTACK_WEIGHT + victim.getArmorValue() * SOUL_ARMOR_WEIGHT) / SOUL_COLLECTION_DIVISOR));
        SoulCollectionEvent event = NeoForge.EVENT_BUS.post(new SoulCollectionEvent(player, victim, amount));
        if (!event.isCanceled()) {
            storage.receive(Math.max(0.0F, event.getAmount()));
        }
    }

    public static void syncMana2Client(ServerPlayer player, ManaStorage manaStorage) {
        PacketDistributor.sendToPlayer(player, new ManaPacketS2C(manaStorage.getMaxMana(), manaStorage.getCurrentMana()));
    }

    public static void syncMana2Client(ServerPlayer player) {
        syncMana2Client(player, ManaStorage.of(player));
    }

    public static void regenerateMana(ServerPlayer player) {
        if (isSoulMode(player)) {
            return;
        }
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
            /// 1.0F / 7.0F = 0.14285715F
            float a = manaStorage.getMaxMana() * 0.14285715F + (manaStorage.isFastManaRegeneration() ? 25 : 0) + 1;
            if (notMove) a += manaStorage.getMaxMana() * 0.5F;
            float b = manaStorage.getCurrentMana() * 0.8F / manaStorage.getMaxMana() + 0.2F;
            float baseRegen = a * b * 0.0115F * EnchantmentUtils.processManaRegeneration(player);
            return baseRegen + starBottleBonusPerTick;
        };

        if (manaStorage.receiveMana(receive)) syncMana2Client(player, manaStorage);
    }

    private static boolean isNotMove(ServerPlayer player) {
        Vector3f vector3f = IServerPlayer.of(player).confluence$getMovementSpeed();
        return Math.abs(vector3f.x) < Mth.EPSILON && Math.abs(vector3f.y) < Mth.EPSILON && Math.abs(vector3f.z) < Mth.EPSILON;
    }

    public static boolean extractMana(ServerPlayer player, ItemStack itemStack, FloatSupplier sup) {
        if (isSoulMode(player)) {
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
        if (isSoulMode(player)) {
            return;
        }
        ManaStorage manaStorage = ManaStorage.of(player);
        if (manaStorage.receiveMana(sup)) syncMana2Client(player, manaStorage);
    }

    /// 初始化数据到player客户端
    public static void syncSavedData(ServerPlayer player) {
        ConfluenceData data = ConfluenceData.get(player.serverLevel());
        WindSpeedPacketS2C.sendToClient(player);
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
        SoulStorage soulStorage = SoulStorage.of(player);
        soulStorage.freshMaxSoul();
        if (soulStorage.isSoulMode()) {
            ManaStorage.of(player).setCurrentMana(0.0F);
        } else {
            soulStorage.setCurrentSoul(0.0F);
        }
        refreshSoulSkills(player);
        syncMana2Client(player);
        PlayerVoidVaultContainer.of(player).sync(player, true);
        GameEventSystem.INSTANCE.syncAll(player);
        if (player.hasData(ModAttachmentTypes.SOUL_STORAGE)) {
            player.syncData(ModAttachmentTypes.SOUL_STORAGE);
        } else {
            SoulStorage.of(player);
        }
        SoulSkillSyncPacketS2C.sendToClient(player);
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
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
        if (expected.isEmpty() || slotIndex < 0 || slotIndex >= player.getInventory().getContainerSize()) return false;
        ItemStack source = player.getInventory().getItem(slotIndex);
        if (!ItemStack.isSameItemSameComponents(source, expected) || source.getCount() < expected.getCount()) return false;
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

        /// jit自动优化
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
        if (isSoulMode(player)) {
            return true;
        }
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
        SoulStorage.of(player).freshMaxSoul();
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

    /// ===== 技能持续状态 =====

    /// 仪式次数改变时同步上限，并在提升时播放一次“唤醒古老之魂”的提示。
    public static void setSoulRitualCount(ServerPlayer player, int ritualCount) {
        SoulStorage storage = SoulStorage.of(player);
        int previous = storage.getRitualCount();
        storage.setRitualCount(ritualCount);
        int current = storage.getRitualCount();
        if (current == previous) {
            return;
        }
        storage.freshMaxSoul();
        if (current > previous) {
            player.displayClientMessage(Component.translatable(SOUL_RITUAL_MESSAGE), true);
        }
        /// 第 1 / 3 / 5 次仪式各解锁一个技能栏位
        int slots = getRitualSlotCount(current);
        if (getEquippedSoulSkillMaxNumber(player) != slots) {
            setSoulSkillSlotCount(player, slots);
            if (current > previous && slots > SoulSkillStorage.DEFAULT_EQUIPPED_SOUL_SKILL_COUNT) {
                player.displayClientMessage(Component.translatable(SOUL_RITUAL_SLOT_MESSAGE, slots), true);
            }
        }
        learnSoulSkillsOnRitual(player);
        SoulSkillSyncPacketS2C.sendToClient(player);
    }

    /// 仪式门槛：第 N 次仪式后再满足前置技能，即可自动学会对应技能。
    ///
    /// 表项为 `{仪式次数, 需要同时已学会的技能..., 目标技能}`，前置技能必须全部永久学会。
    public static void learnSoulSkillsOnRitual(ServerPlayer player) {
        if (!Confluence.SOUL_SKILLS) {
            return;
        }
        int rituals = SoulStorage.of(player).getRitualCount();
        boolean changed = false;
        for (RitualUnlock unlock : RITUAL_UNLOCKS) {
            if (rituals < unlock.ritual()) {
                continue;
            }
            SoulSkill skill = unlock.skill().get();
            if (isSoulSkillLearned(player, skill.getId())) {
                continue;
            }
            if (!unlock.required().stream().allMatch(required -> isSoulSkillLearned(player, required.get().getId()))) {
                continue;
            }
            if (learnSoulSkill(player, skill)) {
                changed = true;
                player.displayClientMessage(Component.translatable(SOUL_RITUAL_UNLOCK_MESSAGE, skill.getNameComponent(skill.getStack())), true);
            }
        }
        if (changed) {
            SoulSkillSyncPacketS2C.sendToClient(player);
        }
    }

    /// 仪式对技能栏位的解锁进度：第 1 / 3 / 5 次仪式各 +1 个栏位（3 → 4 → 5 → 6）。
    private static final int[] RITUAL_SLOT_UNLOCKS = {1, 3, 5};

    /// 按已完成的仪式次数计算应有的技能栏位数。
    public static int getRitualSlotCount(int ritualCount) {
        int slots = SoulSkillStorage.DEFAULT_EQUIPPED_SOUL_SKILL_COUNT;
        for (int threshold : RITUAL_SLOT_UNLOCKS) {
            if (ritualCount >= threshold) {
                slots++;
            }
        }
        return Math.min(slots, SoulSkillStorage.MAX_EQUIPPED_SOUL_SKILL_COUNT);
    }
    /// 一次仪式解锁项。
    public record RitualUnlock(int ritual, List<Supplier<SoulSkill>> required, Supplier<SoulSkill> skill) {}

    private static final List<RitualUnlock> RITUAL_UNLOCKS = List.of(
            /// 第一次仪式
            new RitualUnlock(1, List.of(), ModSoulSkills.ENHANCED_LURE),
            new RitualUnlock(1, List.of(), ModSoulSkills.SOUL_MARK),
            new RitualUnlock(1, List.of(), ModSoulSkills.SOUL_DRAIN),
            new RitualUnlock(1, List.of(ModSoulSkills.SPIRIT_SURGE), ModSoulSkills.SOUL_SURGE),
            new RitualUnlock(1, List.of(ModSoulSkills.SPIRIT_SURGE), ModSoulSkills.LURE_SURGE),
            new RitualUnlock(1, List.of(ModSoulSkills.SPIRIT_SURGE), ModSoulSkills.EMPOWERED_SURGE),
            new RitualUnlock(1, List.of(ModSoulSkills.SPIRIT_SURGE), ModSoulSkills.SPIRIT_TRIGGER),
            new RitualUnlock(1, List.of(), ModSoulSkills.SOUL_ROTATION),
            /// 第二次仪式
            new RitualUnlock(2, List.of(), ModSoulSkills.STAR_REVERSAL),
            new RitualUnlock(2, List.of(), ModSoulSkills.STAR_LINK),
            new RitualUnlock(2, List.of(), ModSoulSkills.SOUL_HARVEST),
            /// 第三次仪式
            new RitualUnlock(3, List.of(), ModSoulSkills.SOUL_ASSAULT),
            new RitualUnlock(3, List.of(), ModSoulSkills.SOUL_DEVOUR),
            /// 第四次仪式
            new RitualUnlock(4, List.of(), ModSoulSkills.BACKLASH_RESISTANCE),
            new RitualUnlock(4, List.of(), ModSoulSkills.SOUL_CONTROL),
            new RitualUnlock(4, List.of(), ModSoulSkills.LIFE_RETURN)
    );

    /// 每刻推进依赖“已学会 + 效果是否在身”的技能状态。
    public static void tickSoulSkillStates(ServerPlayer player, long gameTime) {
        if (!Confluence.SOUL_SKILLS) {
            return;
        }
        SoulStorage storage = SoulStorage.of(player);
        /// 魂引天星：持续期间按间隔消耗灵魂召唤坠星
        if (player.hasEffect(ModEffects.STAR_CALL) && gameTime % SoulSkillParameters.STAR_CALL_INTERVAL_TICKS == 0) {
            tickStarCall(player, storage);
        }
        /// 附魂激荡：「附魂激荡」增益期间按间隔自动释放灵魂激荡，且完全不消耗灵魂
        if (gameTime % SoulSkillParameters.SOUL_SURGE_INTERVAL_TICKS == 0
                && player.hasEffect(ModEffects.SOUL_SURGE)
                && isSoulSkillLearned(player, ModSoulSkills.SOUL_SURGE.get().getId())) {
            float damage = getSoulSkillDamage(player, ModSoulSkills.SPIRIT_SURGE.get().getStack());
            player.level().addFreshEntity(new SpiritSurgeEntity(player, damage));
        }
        /// 灵魂突击：增益期间按间隔把灵魂转成子弹
        if (player.hasEffect(ModEffects.SOUL_ASSAULT) && gameTime % SoulSkillParameters.SOUL_ASSAULT_INTERVAL_TICKS == 0) {
            SoulAssaultSkill.fireBurst(player);
        }
        /// 灵魂透支：结束时施加无法使用主武器的代价
        boolean draining = player.hasEffect(ModEffects.SOUL_DRAIN);
        if (storage.isSoulDrainActive() && !draining) {
            player.addEffect(new MobEffectInstance(ModEffects.SOUL_DRAIN_LOCK, SoulSkillParameters.SOUL_DRAIN_LOCKOUT_TICKS, 0, false, true, true));
        }
        storage.setSoulDrainActive(draining);
        /// 生命归还：结束时把记录到的生命损失按比例返还为灵魂
        boolean returning = player.hasEffect(ModEffects.LIFE_RETURN);
        if (storage.isLifeReturnActive() && !returning) {
            float lost = storage.getLifeReturnLost();
            if (lost > 0.0F) {
                storage.receive(lost * SoulSkillParameters.LIFE_RETURN_RATIO);
            }
            storage.setLifeReturnLost(0.0F);
        }
        storage.setLifeReturnActive(returning);
    }

    /// 魂引天星：每颗星消耗固定灵魂，命中前按概率返还。
    private static void tickStarCall(ServerPlayer player, SoulStorage storage) {
        if (storage.getCurrentSoul() < SoulSkillParameters.STAR_CALL_SOUL_PER_STAR) {
            return;
        }
        LivingEntity target = null;
        for (LivingEntity candidate : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(16.0),
                candidate -> candidate.isAlive() && !candidate.isSpectator() && !candidate.isAlliedTo(player)
                        && EnemyDamageRules.isEnemy(candidate))) {
            target = candidate;
            break;
        }
        double x = target != null ? target.getX() : player.getX() + player.getLookAngle().x * 4.0;
        double z = target != null ? target.getZ() : player.getZ() + player.getLookAngle().z * 4.0;
        double y = player.getY() + 8.0;
        float damage = getSoulSkillDamage(player, ModSoulSkills.STAR_CALL.get().getStack());
        /// 外观改用星怒的坠星弹射物，不再走 `falling_star` 粒子。
        player.level().addFreshEntity(SoulLureProjectile.star(player, new Vec3(x, y, z), new Vec3(0, -1, 0), damage, 1.2F, 0.02));
        if (!consumeSoul(player, SoulSkillParameters.STAR_CALL_SOUL_PER_STAR)) {
            return;
        }
        if (player.getRandom().nextFloat() < SoulSkillParameters.STAR_CALL_REFUND_CHANCE) {
            storage.receive(1.0F);
        }
    }
}
