package org.confluence.mod.util;

import net.minecraft.SharedConstants;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.common.entity.PartHitTarget;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.SoulCollectionEvent;
import org.confluence.mod.api.event.SoulConsumeEvent;
import org.confluence.mod.api.event.SoulRecoveryEvent;
import org.confluence.mod.api.event.SoulSkillDamageEvent;
import org.confluence.mod.api.item.ISoulBow;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.attachment.SoulSkillStorage;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.common.enchantment.EnchantmentBehaviors;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.entity.soulcerer.SpiritSurgeEntity;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.SoulItems;
import org.confluence.mod.common.item.soulcerer.SoulArtifactItem;
import org.confluence.mod.common.item.soulcerer.SoulBooks;
import org.confluence.mod.common.item.soulcerer.SoulWeaponItem;
import org.confluence.mod.common.soulcerer.skill.*;
import org.confluence.mod.network.s2c.SoulSkillIndexSyncPacketS2C;
import org.confluence.mod.network.s2c.SoulSkillSyncPacketS2C;
import org.confluence.mod.network.s2c.SoulSkillUsePacketS2C;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.jetbrains.annotations.Nullable;
import org.confluence.mod.mixed.IDamageContainer;
import org.mesdag.portlib.diff.IPortLivingEntity;
import org.mesdag.portlib.wrapper.common.damagesource.PortDamageContainer;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.entity.living.PortLivingIncomingDamageEvent;
import org.mesdag.portlib.event.entity.living.PortLivingDamageEvent;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.*;
import java.util.function.Supplier;

public final class SoulcererUtils {
    public static final String SOUL_BOW_SOURCE_TAG = "confluence:soul_bow";
    private static final float HEALTH_PER_SOUL = 4.0F;
    private static final float SOUL_CONSUMPTION_FAILED = -1.0F;
    private static final float ENVIRONMENT_SOUL_RECOVERY_RATIO = 0.05F;
    private static final float SOUL_HEALTH_WEIGHT = 0.8F;
    private static final float SOUL_ATTACK_WEIGHT = 0.1F;
    private static final float SOUL_ARMOR_WEIGHT = 0.1F;
    private static final float SOUL_COLLECTION_DIVISOR = 20.0F;
    private static final float MINIMUM_COLLECTED_SOUL = 1.0F;
    private static final String FIRST_OBTAIN_SOUL_SKILL_MESSAGE = "message.confluence.soulcerer_skill.first_obtain";
    private static final ResourceLocation SOULDANCER_CRIT_ID = Confluence.asResource("souldancer_set_crit");
    private static final ResourceLocation SOULDANCER_SPEED_ID = Confluence.asResource("souldancer_set_speed");
    private static final ResourceLocation SOUL_ROBE_SET_SOUL_DAMAGE_ID = Confluence.asResource("soul_robe_set_soul_damage");
    private static final ResourceLocation STAR_LINK_MAGIC_ID = Confluence.asResource("star_link_magic");
    private static final ResourceLocation VERDANT_CRIT_ID = Confluence.asResource("verdant_soul_crit");
    private static final ResourceLocation VERDANT_MAGIC_ID = Confluence.asResource("verdant_soul_magic");
    private static final String VERDANT_BUFF_END = "confluence.verdant_soul_buff_end";

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
                equippedSkills.remove(equippedSkills.size() - 1);
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
            SoulSkillStorage.of(player).setCurrentSoulSkillIndex(Mth.clamp(currentIndex, 0, Math.max(0, getEquippedSoulSkillMaxNumber(player) - 1)));
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
        float soulCost = getSoulSkillCost(player, stack);
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
    ///
    /// 玩家**手动**装备一个「武器专属技能」时，佩戴的刻骨的记忆会把锁定切到它（对应「最近的一个」）。
    /// 内部自动替换（跟随手持武器）走 {@link #applySoulSkillStack}，不会改写锁定。
    public static boolean setSoulSkillStack(@Nullable Player player, int index, SoulSkillStack skillStack) {
        if (player == null) {
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            refreshSoulSkills(serverPlayer);
        }
        /// 被动技能同样占槽：只有装备后才结算效果，见 isPassiveSoulSkillActive。
        if (!skillStack.isEmpty() && !isSoulSkillAvailable(player, skillStack.getSoulSkill().getId())) {
            return false;
        }
        if (!applySoulSkillStack(player, index, skillStack)) {
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            rememberManuallyEquippedSkill(serverPlayer, skillStack);
        }
        mirrorSoulSkillCooldowns(player);
        return true;
    }

    /// 只写入槽位内容，不做可用性校验、不改写刻骨的记忆的锁定；供内部自动替换使用。
    private static boolean applySoulSkillStack(Player player, int index, SoulSkillStack skillStack) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        List<SoulSkillStack> equippedSkills = storage.getEquippedSoulSkills();
        if (index < 0 || index >= storage.getEquippedSoulSkillMaxNumber() || index >= equippedSkills.size()) {
            return false;
        }
        equippedSkills.set(index, skillStack);
        return true;
    }

    /// 手动装备「武器专属技能」时把刻骨的记忆的锁定切到它；已学会的技能不作为锁定目标。
    private static void rememberManuallyEquippedSkill(ServerPlayer player, SoulSkillStack skillStack) {
        if (skillStack.isEmpty() || !TCUtils.hasType(player, AccessoryItems.MEMORY$OF$BONEBRAND)) {
            return;
        }
        ResourceLocation id = skillStack.getSoulSkill().getId();
        if (isSoulSkillLearned(player, id)) {
            return;
        }
        SoulSkillStorage.of(player).setMemoryOfBonebrandSkill(id);
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
        if (!isSoulMode(player) && !(amount > 0 && hasBandOfSoul(player))) {
            return;
        }
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
    ///
    /// 佩戴灵魂手环系列时，切换只改变当前生效的资源条，**不清空另一条的数值**；
    /// 未佩戴时沿用旧行为（把另一条清零），避免普通玩家靠来回切换白嫖资源。
    public static boolean switchSoulMode(ServerPlayer player, boolean soulMode, float targetResource) {
        if (!Confluence.SOUL_SKILLS || isSoulMode(player) == soulMode) {
            return false;
        }

        SoulStorage storage = SoulStorage.of(player);
        storage.setSoulMode(soulMode);
        ManaStorage manaStorage = ManaStorage.of(player);
        if (hasBandOfSoul(player)) {
            if (targetResource > 0.0F) {
                if (soulMode) {
                    storage.setCurrentSoul(Math.max(storage.getCurrentSoul(), targetResource));
                } else {
                    manaStorage.setCurrentMana(Math.max(manaStorage.getCurrentMana(), targetResource));
                }
            }
        } else {
            storage.setCurrentSoul(soulMode ? targetResource : 0.0F);
            manaStorage.setCurrentMana(soulMode ? 0.0F : targetResource);
        }
        PlayerUtils.syncMana2Client(player);
        return true;
    }

    /// 是否佩戴灵魂手环系列（灵魂手环 / 灵魂再生手环 / 通灵手环共用同一标记）。
    public static boolean hasBandOfSoul(@Nullable Player player) {
        return player != null && TCUtils.hasType(player, AccessoryItems.BAND$OF$SOUL);
    }

    /// 法器只在魂条下生效：已处于魂条直接放行；佩戴灵魂手环系列时像主武器那样自动把
    /// 当前资源条切成灵魂条（两条数值都不清空）；都没有则不满足使用条件。
    public static boolean ensureSoulModeForArtifact(ServerPlayer player) {
        if (isSoulMode(player)) {
            return true;
        }
        if (!hasBandOfSoul(player)) {
            return false;
        }
        return switchSoulMode(player, true);
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

    /// 主手是否拿着带指定「自带效果」的魂师主武器；换手即失效。
    public static boolean holdsSoulWeaponTrait(@Nullable Player player, SoulWeaponItem.Trait trait) {
        return player != null && player.getMainHandItem().getItem() instanceof SoulWeaponItem weapon && weapon.hasTrait(trait);
    }

    /// 用过堕魂星核之后：使用魂师主武器时自动把魔力条切成灵魂条。
    ///
    /// 没解锁时保持原样（只能用堕魂星核手动切换），避免开局就把资源模式抢走。
    public static void ensureResourceModeForSoulWeapon(ServerPlayer player) {
        if (Confluence.SOUL_SKILLS && SoulStorage.of(player).isMasterSwitchUnlocked() && !isSoulMode(player)) {
            switchSoulMode(player, true);
        }
    }

    /// 用过堕魂星核之后：使用魔法武器时自动把灵魂条切成魔力条。
    public static void ensureResourceModeForMagicWeapon(ServerPlayer player) {
        if (Confluence.SOUL_SKILLS && SoulStorage.of(player).isMasterSwitchUnlocked() && isSoulMode(player)) {
            switchSoulMode(player, false);
        }
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
    /// 灵魂枯竭（灵魂透支结束后的代价）期间被动技能同样失效：效果保留在槽位上，只是不再结算。
    public static boolean isPassiveSoulSkillActive(@Nullable Player player, ResourceLocation skillId) {
        if (player == null || player.hasEffect(ModEffects.SOUL_DRAIN_LOCK.get())) {
            return false;
        }
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
    ///
    /// 顺序很关键：
    /// 1. 先维护刻骨的记忆锁定的技能（稳定、不随机）；
    /// 2. 再让「非锁定」的武器专属技能槽跟随手持武器；
    /// 3. 最后按 `已学会 ∪ 手持武器授予 ∪ 锁定技能` 重算可用性。
    public static boolean refreshSoulSkills(ServerPlayer player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        Set<ResourceLocation> temporary = new HashSet<>();
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof SoulWeaponItem weapon) {
            temporary.addAll(weapon.getGrantedSoulSkills(mainHand));
        }
        resolveMemoryOfBonebrandSkill(player);
        boolean slotsChanged = syncWeaponExclusiveSlots(player);
        Set<ResourceLocation> available = new HashSet<>(storage.getUnlockedSoulSkills().keySet());
        available.addAll(temporary);
        ResourceLocation memorySkill = storage.getMemoryOfBonebrandSkill();
        if (memorySkill != null) {
            available.add(memorySkill);
        }
        available.removeIf(id -> {
            SoulSkill skill = SoulSkillStorage.findSoulSkill(id);
            return skill == null || skill == ModSoulSkills.EMPTY.get() || !skill.isAvailable(player);
        });
        boolean changed = slotsChanged || !temporary.equals(storage.getTemporarySkills()) || !available.equals(storage.getAvailableSkills());
        storage.getTemporarySkills().clear();
        storage.getTemporarySkills().addAll(temporary);
        storage.getAvailableSkills().clear();
        storage.getAvailableSkills().addAll(available);
        mirrorSoulSkillCooldowns(player);
        return changed;
    }

    /// 武器专属技能槽跟随手持武器；刻骨的记忆锁定一个技能，它所在的槽不参与跟随。
    ///
    /// 规则：
    /// 1. 「专属槽」= 未永久学会的技能槽；已学会技能永远不会被替换。
    /// 2. 手持武器授予的技能按授予顺序填入非锁定专属槽，每个技能最多占一个槽（不重复入槽）；
    ///    没有空闲技能可填时保留原样。
    /// 3. 手持武器没有专属技能时，非锁定专属槽保留原样（变灰，不可释放）。
    /// 4. 佩戴刻骨的记忆时锁定一个专属技能并由它的槽保护；但整个技能栏只有这一个专属槽时，
    ///    它也跟着手持武器走，锁定随后转移到替换后的技能上。
    /// 5. 未佩戴刻骨的记忆时没有锁定，所有专属槽都跟随手持武器。
    ///
    /// @return 槽位内容或锁定技能是否发生变化
    private static boolean syncWeaponExclusiveSlots(ServerPlayer player) {
        SoulSkillStorage storage = SoulSkillStorage.of(player);
        List<SoulSkillStack> slots = storage.getEquippedSoulSkills();
        if (slots.isEmpty()) {
            return false;
        }
        boolean memoryWorn = TCUtils.hasType(player, AccessoryItems.MEMORY$OF$BONEBRAND);
        ResourceLocation locked = storage.getMemoryOfBonebrandSkill();
        int exclusiveCount = countExclusiveSkillSlots(player, slots);
        /// 只有一个专属槽时它必须跟随手持武器，此时不保护锁定技能。
        ResourceLocation protectedSkill = memoryWorn && exclusiveCount > 1 ? locked : null;

        boolean changed = false;
        boolean lockDisplaced = false;
        List<ResourceLocation> candidates = getHeldWeaponSkillCandidates(player);
        if (!candidates.isEmpty()) {
            Set<ResourceLocation> present = new HashSet<>();
            for (SoulSkillStack slot : slots) {
                if (!slot.isEmpty()) {
                    present.add(slot.getSoulSkill().getId());
                }
            }
            for (int i = 0; i < slots.size(); i++) {
                SoulSkillStack slot = slots.get(i);
                if (slot.isEmpty()) {
                    continue;
                }
                ResourceLocation id = slot.getSoulSkill().getId();
                if (isSoulSkillLearned(player, id) || id.equals(protectedSkill) || candidates.contains(id)) {
                    continue;
                }
                ResourceLocation next = null;
                for (ResourceLocation candidate : candidates) {
                    if (!present.contains(candidate)) {
                        next = candidate;
                        break;
                    }
                }
                if (next == null) {
                    continue;
                }
                SoulSkill soulSkill = SoulSkillStorage.findSoulSkill(next);
                if (soulSkill == null) {
                    continue;
                }
                slots.set(i, soulSkill.getStack());
                present.add(next);
                changed = true;
                if (id.equals(locked)) {
                    /// 唯一专属槽被跟随替换：锁定跟着槽走到新技能上，避免记忆指向一个不在技能栏里的旧技能。
                    lockDisplaced = true;
                    locked = next;
                }
            }
        }
        if (memoryWorn && lockDisplaced) {
            storage.setMemoryOfBonebrandSkill(locked);
        }
        return changed;
    }

    /// 统计未永久学会的技能槽数量（即「武器专属技能槽」）。
    private static int countExclusiveSkillSlots(Player player, List<SoulSkillStack> slots) {
        int count = 0;
        for (SoulSkillStack slot : slots) {
            if (!slot.isEmpty() && !isSoulSkillLearned(player, slot.getSoulSkill().getId())) {
                count++;
            }
        }
        return count;
    }

    /// 手持武器授予、且尚未永久学会的技能（按授予顺序去重）；没有手持魂师武器时为空。
    private static List<ResourceLocation> getHeldWeaponSkillCandidates(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SoulWeaponItem weapon)) {
            return List.of();
        }
        List<ResourceLocation> candidates = new ArrayList<>();
        for (ResourceLocation id : weapon.getGrantedSoulSkills(mainHand)) {
            SoulSkill skill = SoulSkillStorage.findSoulSkill(id);
            if (skill == null || skill.isPassive() || isSoulSkillLearned(player, id) || candidates.contains(id)) {
                continue;
            }
            candidates.add(id);
        }
        return candidates;
    }

    /// 当前选中槽是「未解锁且当前武器不提供」的技能时，回退到手持武器的专属技能。
    ///
    /// 判定顺序：
    /// 1. 已永久解锁 / 当前可用（含手持武器临时授予、刻骨的记忆放行）→ 保持不动；
    /// 2. 手持武器提供了同标签（引魂 / 附魂）的技能 → 优先同标签；
    /// 3. 没有同标签技能时，退回该武器授予的第一个可释放技能。
    ///
    /// 若目标技能已装在其它槽位，直接把选中位切过去；否则用目标技能替换当前槽内容。
    ///
    /// @return 是否发生了替换 / 切换（调用方据此补发同步包）
    public static boolean ensureHeldWeaponSkill(ServerPlayer player) {
        SoulSkillStack current = getCurrentSkillStack(player);
        if (current.isEmpty() || current.getSoulSkill().isPassive()) {
            return false;
        }
        SoulSkill locked = current.getSoulSkill();
        /// available 已包含永久解锁、手持武器临时授予与刻骨的记忆放行。
        if (isSoulSkillAvailable(player, locked.getId())) {
            return false;
        }
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SoulWeaponItem weapon)) {
            return false;
        }
        ResourceLocation fallbackId = pickHeldWeaponFallbackSkill(player, locked, weapon.getGrantedSoulSkills(mainHand));
        if (fallbackId == null) {
            return false;
        }
        SoulSkill fallback = SoulSkillStorage.findSoulSkill(fallbackId);
        if (fallback == null || fallback.isPassive() || !isSoulSkillAvailable(player, fallbackId)) {
            return false;
        }
        List<Integer> equippedIndexes = getSoulSkillIndexes(player, fallback);
        if (!equippedIndexes.isEmpty()) {
            int index = equippedIndexes.get(0);
            if (index == getCurrentSoulSkillIndex(player)) {
                return false;
            }
            setCurrentSoulSkillIndex(player, index);
        } else if (!applySoulSkillStack(player, getCurrentSoulSkillIndex(player), fallback.getStack())) {
            return false;
        }
        SoulSkillSyncPacketS2C.sendToClient(player);
        SoulSkillIndexSyncPacketS2C.sendToClient(player);
        return true;
    }

    /// 在手持武器授予的技能里选一个与锁定技能同标签的；没有同标签时取第一个可释放技能。
    ///
    /// 引魂标签只会换成引魂技能，附魂标签只会换成附魂技能；两者都不匹配时才退化到武器首个技能。
    @Nullable
    private static ResourceLocation pickHeldWeaponFallbackSkill(Player player, SoulSkill locked, List<ResourceLocation> granted) {
        boolean lure = locked.is(ModTags.SoulSkills.SOUL_LURE);
        boolean attach = locked.is(ModTags.SoulSkills.SOUL_ATTACH);
        ResourceLocation first = null;
        for (ResourceLocation id : granted) {
            SoulSkill candidate = SoulSkillStorage.findSoulSkill(id);
            if (candidate == null || candidate.isPassive() || !isSoulSkillAvailable(player, id)) {
                continue;
            }
            if (first == null) {
                first = id;
            }
            if (lure && candidate.is(ModTags.SoulSkills.SOUL_LURE)) {
                return id;
            }
            if (attach && candidate.is(ModTags.SoulSkills.SOUL_ATTACH)) {
                return id;
            }
        }
        return first;
    }

    /// 佩戴刻骨的记忆时维护它锁定的那一个技能；没佩戴则清空。
    ///
    /// 锁定是**稳定**的：只要技能仍由背包里的某把武器提供就保持不变（不会因为换武器或施放随机改写）。
    /// 只有锁定失效（对应武器离开背包 / 已被永久学会）时才确定性重选：先取手持武器授予的技能，
    /// 再按背包（含副手）扫描顺序取第一个候选。
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

    /// 该技能是否是刻骨的记忆当前锁定的技能。
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

    /// 确定性挑选锁定技能：当前选中槽的专属技能 → 手持武器授予的第一个候选 → 背包扫描顺序的第一个候选。
    ///
    /// 旧实现是「从候选里均匀随机取一个」，会让刻骨的记忆锁定的技能在换武器/施放后乱跳，因此改为确定性。
    @Nullable
    private static ResourceLocation pickMemoryOfBonebrandSkill(Player player, List<ResourceLocation> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        SoulSkillStack current = getCurrentSkillStack(player);
        if (!current.isEmpty() && candidates.contains(current.getSoulSkill().getId())) {
            return current.getSoulSkill().getId();
        }
        for (ResourceLocation granted : getHeldWeaponSkillCandidates(player)) {
            if (candidates.contains(granted)) {
                return granted;
            }
        }
        return candidates.get(0);
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

    /// 法器冷却：先按技能那套「分魂驾驭 / 冰冷魂戒 / 松露灯」修正，再乘「法器冷却减免」属性。
    public static int getSoulArtifactCooldownTicks(Player player, int ticks) {
        int base = getSoulCooldownTicks(player, ticks);
        if (base <= 0) {
            return base;
        }
        double reduction = Mth.clamp(player.getAttributeValue(LibAttributes.getArtifactCooldownReduction()), 0.0, 1.0);
        return (int) Math.max(1L, Math.round(base * (1.0 - reduction)));
    }

    /// 统一结算「分魂驾驭延长」与「冰冷魂戒 / 松露灯缩短」后的灵魂技能冷却。
    public static int getSoulCooldownTicks(Player player, int ticks) {
        if (ticks <= 0) {
            return ticks;
        }
        /// 分魂驾驭：技能冷却延长
        if (isPassiveSoulSkillActive(player, ModSoulSkills.SOUL_CONTROL.get())) {
            ticks = Math.round(ticks * SoulSkillParameters.SOUL_CONTROL_COOLDOWN_MULTIPLIER);
        }
        /// 冰冷魂戒：技能冷却缩短
        if (TCUtils.hasType(player, AccessoryItems.FROSTBITE$SOUL$RING)) {
            ticks = Math.round(ticks * (1.0F - SoulSkillParameters.FROSTBITE_SOUL_RING_COOLDOWN_REDUCTION));
        }
        /// 松露灯：手持时技能冷却额外缩短
        if (player.getMainHandItem().is(SoulItems.TRUFFLE_LANTERN.get())) {
            ticks = Math.round(ticks * (1.0F - SoulSkillParameters.TRUFFLE_LANTERN_COOLDOWN_REDUCTION));
        }
        return ticks;
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
        /// 换武器后当前选中的技能可能已经失效：立即回退到手持武器的专属技能。
        if (changed) {
            ensureHeldWeaponSkill(player);
        }
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
    /// 当前主手武器的灵魂伤害（武器自身基数，不含属性与增益）。
    public static float getWeaponSoulDamage(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        return mainHand.getItem() instanceof SoulWeaponItem weapon ? weapon.getSoulDamage(mainHand) : 0.0F;
    }

    /// 协同类（延时 / 多段）攻击专用的伤害：武器部分取「该技能最近一次施法时」的快照。
    ///
    /// 因此放出去之后换手、空手都不会改变已施放技能的伤害；技能基础伤害、引渡者、
    /// 分魂驾驭以及其它实时增益仍然照常结算。
    public static float getCastSoulSkillDamage(ServerPlayer player, SoulSkill skill) {
        return getSoulSkillDamage(player, skill.getStack(), SoulStorage.of(player).getCastWeaponDamage(skill.getId()));
    }

    /// 返回尚未乘灵魂属性倍率的技能与主武器总伤害（武器部分取当前主手）。
    public static float getSoulSkillDamage(Player player, SoulSkillStack stack) {
        return getSoulSkillDamage(player, stack, getWeaponSoulDamage(player));
    }

    /// 返回尚未乘灵魂属性倍率的技能与主武器总伤害；武器部分由调用方给定。
    public static float getSoulSkillDamage(Player player, SoulSkillStack stack, float weaponDamage) {
        SoulSkillDamageEvent event = PortEventHandler.postEventWithReturn(new SoulSkillDamageEvent(player, stack, stack.getDamage(), weaponDamage));
        float amount = Math.max(0.0F, event.getAmount());
        /// 引渡者：引魂标签内的技能基础伤害固定 +1，不看技能分类
        if (isSoulBookLearned(player, SoulBooks.PSYCHOPOMP) && stack.getSoulSkill().is(ModTags.SoulSkills.SOUL_LURE)) {
            amount += SoulSkillParameters.PSYCHOPOMP_SOUL_LURE_DAMAGE_BONUS;
        }
        /// 分魂驾驭：技能伤害翻倍（冷却同时延长）
        if (isPassiveSoulSkillActive(player, ModSoulSkills.SOUL_CONTROL.get())) {
            amount *= SoulSkillParameters.SOUL_CONTROL_DAMAGE_MULTIPLIER;
        }
        return amount;
    }

    /// 技能说明里的实时单次伤害：包含武器份额、被动与灵魂伤害属性倍率。
    ///
    /// 与释放时 `getSoulSkillDamage(...)` 再乘一次灵魂属性的口径一致，因此描述里的数字就是这一发的期望伤害。
    public static float getSoulSkillDisplayDamage(Player player, SoulSkillStack stack) {
        SoulSkill skill = stack.getSoulSkill();
        float raw = getSoulSkillDamage(player, stack, getWeaponSoulDamage(player) * skill.getWeaponShare());
        return raw * (float) player.getAttributeValue(LibAttributes.getSoulDamage().value());
    }

    /// 灵魂突击：增益期间用枪械开火时消耗 1 魂强化这一发子弹（伤害 +N），并有概率返还 1 魂。
    ///
    /// 灵魂不足时**不触发**（也不走生命代偿），普通子弹照常打出。
    public static float applySoulAssaultGunBonus(ServerPlayer player, float damage) {
        if (!player.hasEffect(ModEffects.SOUL_ASSAULT.get())
                || getSoul(player) < calculateSoulCost(player, SoulSkillParameters.SOUL_ASSAULT_GUN_SOUL_COST)
                || !consumeSoul(player, SoulSkillParameters.SOUL_ASSAULT_GUN_SOUL_COST)) {
            return damage;
        }
        if (player.getRandom().nextFloat() < SoulSkillParameters.SOUL_ASSAULT_GUN_REFUND_CHANCE) {
            adjustSoul(player, SoulSkillParameters.SOUL_ASSAULT_GUN_REFUND_SOUL);
        }
        return damage + SoulSkillParameters.SOUL_ASSAULT_GUN_DAMAGE_BONUS;
    }

    /// 先扣可用灵魂，返回需在效果执行后结算的生命代偿；负值表示取消。
    private static float prepareSoulConsumption(ServerPlayer player, float amount) {
        SoulConsumptionPlan payment = planSoulConsumption(player, amount);
        return payment == null ? SOUL_CONSUMPTION_FAILED : payment.commit(player);
    }

    private record SoulConsumptionPlan(float cost, float healthPerSoul) {
        private float commit(ServerPlayer player) {
            SoulStorage storage = SoulStorage.of(player);
            float paidSoul = Math.min(storage.getCurrentSoul(), cost);
            storage.extract(paidSoul);
            onSoulSpent(player, paidSoul);
            return (cost - paidSoul) * healthPerSoul;
        }
    }

    @Nullable
    private static SoulConsumptionPlan planSoulConsumption(ServerPlayer player, float amount) {
        if (!isSoulMode(player)) {
            return null;
        }
        /// 灵魂透支：不消耗灵魂
        if (amount > 0.0F && isSoulSkillLearned(player, ModSoulSkills.SOUL_DRAIN.get().getId()) && player.hasEffect(ModEffects.SOUL_DRAIN.get())) {
            return new SoulConsumptionPlan(0.0F, 0.0F);
        }
        /// 自然法则：概率不消耗灵魂
        if (amount > 0.0F && isPassiveSoulSkillActive(player, ModSoulSkills.LAW_OF_NATURE.get()) && player.getRandom().nextFloat() < SoulSkillParameters.LAW_OF_NATURE_FREE_CHANCE) {
            return new SoulConsumptionPlan(0.0F, 0.0F);
        }
        SoulConsumeEvent event = PortEventHandler.postEventWithReturn(new SoulConsumeEvent(player, amount, HEALTH_PER_SOUL));
        if (event.isCanceled()) {
            return null;
        }
        /// 创造模式照常发事件，但不扣灵魂、不结算生命代偿。
        if (player.isCreative() || player.hasInfiniteMaterials()) {
            return new SoulConsumptionPlan(0.0F, 0.0F);
        }
        return new SoulConsumptionPlan(Math.max(0.0F, event.getAmount()), Math.max(0.0F, event.getHealthPerSoul()));
    }

    public static class SoulDamageState {
        private SoulConsumptionPlan plan;
        private MobEffectInstance boilingBlood;
        private boolean finalPositive;
        private boolean knockbackApplied;
        private boolean finalDamageCaptured;
        private float finalDamage;

        public void clear() {
            plan = null;
            boilingBlood = null;
            finalPositive = false;
            knockbackApplied = false;
            finalDamageCaptured = false;
            finalDamage = 0.0F;
        }
    }

    public static void clearCanceledSoulDamage(PortLivingIncomingDamageEvent event) {
        if (event.isCanceled()) {
            IDamageContainer.of(event.getContainer()).confluence$getSoulDamageState().clear();
        }
    }

    @Nullable
    private static PortDamageContainer currentDamageContainer(LivingEntity victim) {
        var containers = IPortLivingEntity.of(victim).portlib$getDamageContainers();
        return containers.isEmpty() ? null : containers.peek();
    }

    public static void soulPrefixKnockback(LivingKnockBackEvent event) {
        PortDamageContainer container = currentDamageContainer(event.getEntity());
        if (container == null) {
            return;
        }

        SoulDamageState state = IDamageContainer.of(container).confluence$getSoulDamageState();
        DamageSource source = container.getSource();
        if (!state.finalPositive || state.knockbackApplied
                || source.is(LibTags.DamageTypes.SOUL_OVERDRAFT)
                || !source.is(LibDamageTypes.SOUL)
                || !(source.getEntity() instanceof LivingEntity owner)) {
            return;
        }

        state.knockbackApplied = true;
        event.setStrength((float) getSoulKnockback(owner, event.getStrength()));
    }

    public static float captureSoulHitDamage(LivingEntity victim, DamageSource source, float damage) {
        PortDamageContainer container = currentDamageContainer(victim);
        if (container != null && container.getSource() == source) {
            SoulDamageState state = IDamageContainer.of(container).confluence$getSoulDamageState();
            state.finalDamageCaptured = true;
            state.finalDamage = damage;
        }
        return damage;
    }

    /// 蓝隐匿者全套：每累计消耗 10 点灵魂，削减 3 秒耐药性剩余时长。
    ///
    /// 另外结算星魂逆转：实际消耗掉的灵魂按 1 : 4 补充魔力（每 1 点灵魂补充 4 点魔力）。
    public static void onSoulSpent(ServerPlayer player, float paidSoul) {
        if (paidSoul <= 0.0F) {
            return;
        }
        /// 星魂逆转：消耗的灵魂按 1 : 4 补充魔力（保留在魔力条里，切回魔力条即可使用）。
        if (isPassiveSoulSkillActive(player, ModSoulSkills.STAR_REVERSAL.get())) {
            ManaStorage manaStorage = ManaStorage.of(player);
            manaStorage.setCurrentMana(manaStorage.getCurrentMana() + paidSoul * SoulSkillParameters.STAR_REVERSAL_MANA_PER_SOUL);
            PlayerUtils.syncMana2Client(player);
        }
        SoulStorage storage = SoulStorage.of(player);
        float unit = SoulSkillParameters.BLUE_HERMIT_SOUL_PER_REDUCTION;
        float accumulated = storage.getConsumedSoul() + paidSoul;
        int triggers = (int) (accumulated / unit);
        storage.setConsumedSoul(accumulated - triggers * unit);
        if (triggers <= 0 || PlayerSpecialData.of(player).getArmorSetBonusKey() != ModArmorBonus.BLUE_HERMIT_SET) {
            return;
        }
        MobEffectInstance sickness = player.getEffect(ModEffects.POTION_SICKNESS.get());
        if (sickness == null) {
            return;
        }
        int reduction = triggers * SoulSkillParameters.BLUE_HERMIT_SICKNESS_REDUCTION_TICKS;
        int duration = sickness.isInfiniteDuration() ? sickness.getDuration() : Math.max(0, sickness.getDuration() - reduction);
        player.removeEffect(ModEffects.POTION_SICKNESS.get());
        if (duration > 0) {
            player.addEffect(new MobEffectInstance(ModEffects.POTION_SICKNESS.get(), duration, sickness.getAmplifier(), sickness.isAmbient(), sickness.isVisible(), sickness.showIcon()));
        }
    }

    /// 判断玩家是否穿戴灵魂长袍两件套（灵魂头套 + 灵魂长袍）。
    public static boolean isWearingSoulRobeSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.SOUL_HOOD.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.SOUL_ROBE.get());
    }

    /// 碧绿长袍全套：收魂时按概率额外收 1 魂。
    public static void verdantSoulChance(SoulCollectionEvent event) {
        Player player = event.getEntity();
        float chance = ModArmorBonus.getValue(player, ModArmorBonus.SOUL$COLLECT$CHANCE);
        if (chance > 0.0F && player.getRandom().nextFloat() < chance) {
            event.setAmount(event.getAmount() + 1.0F);
        }
    }

    /// 收集灵魂时刷新魂舞限时增益。
    public static void onSoulCollected(Player player, long gameTime) {
        if (PlayerSpecialData.of(player).getArmorSetBonusKey() == ModArmorBonus.SOULDANCER_ROBE_SET) {
            SoulStorage.of(player).setSouldancerBuffEnd(gameTime + SoulSkillParameters.SOULDANCER_BUFF_DURATION_TICKS);
        }
        if (PlayerSpecialData.of(player).getArmorSetBonusKey() == ModArmorBonus.VERDANT_ROBE_SET) {
            player.getPersistentData().putLong(VERDANT_BUFF_END, gameTime + 200);
        }
    }

    /// 每刻同步魂舞增益的临时属性修饰符，到期自行移除。
    public static void tickSoulArmorSets(ServerPlayer player, long gameTime) {
        boolean active = gameTime < SoulStorage.of(player).getSouldancerBuffEnd();
        applyTransientModifier(player, LibAttributes.getCriticalChance().value(), SOULDANCER_CRIT_ID, SoulSkillParameters.SOULDANCER_CRIT_BONUS, PortAttributeModifier.Operation.ADD_VALUE, active);
        applyTransientModifier(player, Attributes.MOVEMENT_SPEED, SOULDANCER_SPEED_ID, SoulSkillParameters.SOULDANCER_SPEED_BONUS, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
        /// 灵魂长袍套（头套 + 长袍）：套装效果，灵魂伤害 +5%（与单件的 +5% 叠加）
        applyTransientModifier(player, LibAttributes.getSoulDamage().value(), SOUL_ROBE_SET_SOUL_DAMAGE_ID, SoulSkillParameters.SOUL_ROBE_SET_SOUL_DAMAGE_BONUS, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, isWearingSoulRobeSet(player));
        /// 碧绿长袍全套：收魂后 10 秒 +5% 暴击、+7% 魔法伤害
        boolean verdant = player.getPersistentData().getLong(VERDANT_BUFF_END) > gameTime;
        applyTransientModifier(player, LibAttributes.getCriticalChance().value(), VERDANT_CRIT_ID, 0.05, PortAttributeModifier.Operation.ADD_VALUE, verdant);
        applyTransientModifier(player, LibAttributes.getMagicDamage().value(), VERDANT_MAGIC_ID, 0.07, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, verdant);
    }

    private static void applyTransientModifier(Player player, Attribute attribute, ResourceLocation id, double value, PortAttributeModifier.Operation operation, boolean active) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        UUID uuid = PortAttributeModifier.rl2uuid(id);
        if (active) {
            instance.addOrUpdateTransientModifier(new AttributeModifier(uuid, id.getPath(), value, operation.unwrap()));
        } else if (instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }

    /// 生命代偿使用独立伤害来源，由伤害标签与 PortLib 事件结算。
    private static void applySoulOverdraft(ServerPlayer player, float healthCost) {
        /// 反噬抵抗：生命代偿伤害减免
        if (healthCost > 0.0F && isPassiveSoulSkillActive(player, ModSoulSkills.BACKLASH_RESISTANCE.get())) {
            healthCost *= 1.0F - SoulSkillParameters.BACKLASH_RESISTANCE_REDUCTION;
        }
        if (healthCost <= 0.0F || !player.isAlive()) {
            return;
        }

        LibDamageTypes.hurtWithoutKnockback(player, LibDamageTypes.soulOverdraft(player.level()), healthCost);
    }

    public static boolean consumeSoul(ServerPlayer player, float amount) {
        float healthCost = prepareSoulConsumption(player, calculateSoulCost(player, amount));
        if (healthCost < 0.0F) {
            return false;
        }
        applySoulOverdraft(player, healthCost);
        return true;
    }

    /// 直接从已存的灵魂值里扣除，不做资源条模式校验、不走技能与透支结算。
    ///
    /// 用于「把灵魂固化进物品」这类合成式消耗（玻璃瓶 → 魂瓶）：灵魂值本身在灵魂条/魔力条之间共享保留，
    /// 所以魔力条状态下也应该能把已存的灵魂装进瓶子；数量不够时返回 false。
    public static boolean consumeStoredSoul(Player player, float amount) {
        if (player == null || amount <= 0.0F) return true;
        SoulStorage storage = SoulStorage.of(player);
        if (storage.getCurrentSoul() < amount) return false;
        storage.setCurrentSoul(storage.getCurrentSoul() - amount);
        if (player instanceof ServerPlayer serverPlayer) {
            onSoulSpent(serverPlayer, amount);
        }
        return true;
    }

    /// 成功后提交冷却、两端执行，再结算可能致死的生命代偿。
    public static boolean tryUseSoulSkill(ServerPlayer player) {
        /// 灵魂枯竭：暂时无法使用主武器
        if (player.hasEffect(ModEffects.SOUL_DRAIN_LOCK.get())) {
            return false;
        }
        if (!Confluence.SOUL_SKILLS || !isSoulMode(player)) {
            return false;
        }
        boolean availabilityChanged = refreshSoulSkills(player);
        /// 当前槽若已变成未解锁技能，先回退到手持武器的专属技能再继续判定。
        ensureHeldWeaponSkill(player);
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
        if (!castSoulSkill(player, skillStack)) {
            return rejectSoulSkillUse(player, availabilityChanged);
        }
        /// 束魂锁链 / 魂术轮转：饰品与被动各自的联动
        applySoulSkillSynergy(player, skill);
        return true;
    }

    /// 一次成功的单体施放：结算耗魂、冷却、两端效果与生命代偿；取消时返回 false。
    private static boolean castSoulSkill(ServerPlayer player, SoulSkillStack skillStack) {
        SoulSkill skill = skillStack.getSoulSkill();
        float healthCost = prepareSoulConsumption(player, skill.consumesSoulInternally() ? 0.0F : getSoulSkillCost(player, skillStack));
        if (healthCost < 0.0F) {
            return false;
        }
        /// 记录本次施法时的武器灵魂伤害：魂引天星、灵魂突击这类延时/多段攻击之后换手也不掉伤害。
        float weaponDamage = getWeaponSoulDamage(player);
        if (!skill.consumesSoulInternally()) {
            SoulStorage.of(player).setCastWeaponDamage(skill.getId(), weaponDamage);
        }
        if (!skill.tryUse(player, skillStack)) {
            return false;
        }
        if (skill.consumesSoulInternally()) {
            SoulStorage.of(player).setCastWeaponDamage(skill.getId(), weaponDamage);
        }
        setSoulSkillCooldown(player, skill, skill.getCooldown(skillStack));
        SoulSkillStack castStack = skillStack.copy();
        SoulSkillSyncPacketS2C.sendToClient(player);
        SoulSkillUsePacketS2C.sendToClient(player, castStack);
        applySoulOverdraft(player, healthCost);
        return true;
    }

    /// 施放成功后的饰品与被动联动。
    ///
    /// 「附魂」与「强力附魂系列」已合并为同一个标签 `SOUL_ATTACH`，所以下面两条联动认的是同一份名单：
    ///
    /// - `束魂锁链`：施放任意「附魂」技能时，把其余已装备的「附魂」技能同时放出。
    ///   登记在「强主动技能」(`MAJOR_ACTIVE`) 里的技能**不参与**这套联动。
    /// - `魂术轮转`：施放任意技能后都自动切到下一个可释放的技能，不再区分是不是「附魂」技能。
    ///   连锁施放附魂技能是束魂锁链的专属效果，轮转不再复制。
    ///   「强主动技能」不会被轮转选中，只能手动放。
    ///
    /// 连锁施放统一走 `castSoulSkill`，不会再回到本方法，因此不存在自递归。
    private static void applySoulSkillSynergy(ServerPlayer player, SoulSkill primary) {
        if (TCUtils.hasType(player, AccessoryItems.SOUL$BINDING$CHAIN) && primary.is(ModTags.SoulSkills.SOUL_ATTACH)) {
            for (SoulSkillStack stack : new ArrayList<>(getEquippedSoulSkills(player))) {
                if (stack.isEmpty() || stack.getSoulSkill() == primary) {
                    continue;
                }
                if (stack.getSoulSkill().is(ModTags.SoulSkills.SOUL_ATTACH)
                        && !stack.getSoulSkill().is(ModTags.SoulSkills.MAJOR_ACTIVE)) {
                    castSoulSkillIfReady(player, stack);
                }
            }
        }
        if (isPassiveSoulSkillActive(player, ModSoulSkills.SOUL_ROTATION.get())) {
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

    private static boolean rejectSoulSkillUse(ServerPlayer player, boolean availabilityChanged) {
        if (availabilityChanged) {
            SoulSkillSyncPacketS2C.sendToClient(player);
        }
        return false;
    }

    /// 正在冷却的法器数量：按物品 ID 统计，不按堆叠个数，也不要求物品还在背包里。
    public static int countCoolingSoulArtifacts(Player player) {
        int count = 0;
        for (Map.Entry<ResourceLocation, Integer> entry : SoulSkillStorage.of(player).getArtifactCooldowns().entrySet()) {
            if (entry.getValue() > 0 && BuiltInRegistries.ITEM.get(entry.getKey()) instanceof SoulArtifactItem) {
                count++;
            }
        }
        return count;
    }

    /// 法器剩余冷却刻数：0 表示可用。冷却存在玩家附件里，退出存档不会丢失。
    public static int getSoulArtifactCooldown(@Nullable Player player, Item item) {
        if (player == null) {
            return 0;
        }
        return Math.max(0, SoulSkillStorage.of(player).getArtifactCooldowns()
                .getOrDefault(BuiltInRegistries.ITEM.getKey(item), 0));
    }

    /// 写入法器冷却：附件里记剩余刻数（存档保留），同时进原版冷却表以显示物品栏冷却动画。
    public static void setSoulArtifactCooldown(ServerPlayer player, Item item, int ticks) {
        if (ticks <= 0) {
            return;
        }
        SoulSkillStorage.of(player).getArtifactCooldowns().put(BuiltInRegistries.ITEM.getKey(item), ticks);
        player.getCooldowns().addCooldown(item, ticks);
    }

    /// 清空全部法器冷却（附件记录 + 原版冷却动画），供腐根 / 黑斑全套重置用。
    public static void resetSoulArtifactCooldowns(ServerPlayer player) {
        SoulSkillStorage.of(player).getArtifactCooldowns().clear();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof SoulArtifactItem) {
                player.getCooldowns().removeCooldown(item);
            }
        }
    }

    /// 冷却射击：把全部法器的剩余冷却刻数减掉 ticks（到 0 的直接清掉）。
    public static void reduceSoulArtifactCooldowns(ServerPlayer player, int ticks) {
        if (ticks <= 0) {
            return;
        }
        Map<ResourceLocation, Integer> cooldowns = SoulSkillStorage.of(player).getArtifactCooldowns();
        Iterator<Map.Entry<ResourceLocation, Integer>> iterator = cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, Integer> entry = iterator.next();
            int remaining = entry.getValue() - ticks;
            if (remaining <= 0) {
                iterator.remove();
                player.getCooldowns().removeCooldown(BuiltInRegistries.ITEM.get(entry.getKey()));
                continue;
            }
            entry.setValue(remaining);
            player.getCooldowns().addCooldown(BuiltInRegistries.ITEM.get(entry.getKey()), remaining);
        }
    }

    /// 每刻推进法器冷却。读档后原版冷却表是空的，这里按剩余刻数把动画补回去。
    public static void tickSoulArtifactCooldowns(ServerPlayer player) {
        Map<ResourceLocation, Integer> cooldowns = SoulSkillStorage.of(player).getArtifactCooldowns();
        if (cooldowns.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<ResourceLocation, Integer>> iterator = cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
                continue;
            }
            entry.setValue(remaining);
            Item item = BuiltInRegistries.ITEM.get(entry.getKey());
            if (!player.getCooldowns().isOnCooldown(item)) {
                player.getCooldowns().addCooldown(item, remaining);
            }
        }
    }

    public static boolean tryUseSoulArtifact(ServerPlayer player, ItemStack stack) {
        if (!Confluence.SOUL_SKILLS || !(stack.getItem() instanceof SoulArtifactItem artifact)
                || getSoulArtifactCooldown(player, stack.getItem()) > 0
                || countCoolingSoulArtifacts(player) >= SoulSkillParameters.MAX_COOLING_SOUL_ARTIFACT_SLOTS
                || !artifact.canUse(player, stack)) {
            return false;
        }
        /// 法器只在灵魂条下可用；佩戴灵魂手环系列时会自动切换，否则给出提示而不是静默失败。
        if (!ensureSoulModeForArtifact(player)) {
            player.displayClientMessage(Component.translatable("message.confluence.artifact.need_soul_mode"), true);
            return false;
        }
        float healthCost = prepareSoulConsumption(player, artifact.getSoulCost(stack));
        if (healthCost < 0.0F) {
            return false;
        }
        /// 冰冷魂戒：法器冷却同样缩短
        setSoulArtifactCooldown(player, stack.getItem(), getSoulArtifactCooldownTicks(player, artifact.getCooldown(stack)));
        artifact.useArtifact(player, stack);
        applySoulOverdraft(player, healthCost);
        /// 附魔：法器协同（强化下一击）与速度协同（限时移速）
        EnchantmentBehaviors.onArtifactUsed(player);
        return true;
    }

    /// 每秒按最终恢复比例结算一次，两个基础环境不叠加。
    public static void regenerateSoul(ServerPlayer player) {
        if (!Confluence.SOUL_SKILLS || !isSoulMode(player) && !hasBandOfSoul(player)
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
        SoulRecoveryEvent event = PortEventHandler.postEventWithReturn(new SoulRecoveryEvent(player, ratio));
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
        if (!(owner instanceof ServerPlayer player) || !isSoulMode(player) && !hasBandOfSoul(player)) {
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
        SoulCollectionEvent event = PortEventHandler.postEventWithReturn(new SoulCollectionEvent(player, victim, amount));
        if (!event.isCanceled()) {
            storage.receive(Math.max(0.0F, event.getAmount()));
        }
    }

    /// ===== 魂师：仪式、栏位、技能书与技能状态 =====
    private static final String SOUL_RITUAL_MESSAGE = "message.confluence.soul_ritual.activate";
    private static final String SOUL_RITUAL_UNLOCK_MESSAGE = "message.confluence.soul_ritual.unlock";
    private static final String SOUL_RITUAL_SLOT_MESSAGE = "message.confluence.soul_ritual.slot";
    private static final int[] RITUAL_SLOT_UNLOCKS = {1, 3, 5};

    public static int getRitualSlotCount(int ritualCount) {
        int slots = SoulSkillStorage.DEFAULT_EQUIPPED_SOUL_SKILL_COUNT;
        for (int threshold : RITUAL_SLOT_UNLOCKS) {
            if (ritualCount >= threshold) {
                slots++;
            }
        }
        return Math.min(slots, SoulSkillStorage.MAX_EQUIPPED_SOUL_SKILL_COUNT);
    }

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
        int slots = getRitualSlotCount(current);
        if (getEquippedSoulSkillMaxNumber(player) != slots) {
            setSoulSkillSlotCount(player, slots);
            if (current > previous && slots > SoulSkillStorage.DEFAULT_EQUIPPED_SOUL_SKILL_COUNT) {
                player.displayClientMessage(Component.translatable(SOUL_RITUAL_SLOT_MESSAGE, slots), true);
            }
        }
        learnSoulSkillsOnRitual(player);
    }

    public static void learnSoulSkillsOnRitual(ServerPlayer player) {
        if (!Confluence.SOUL_SKILLS) {
            return;
        }
        int rituals = SoulStorage.of(player).getRitualCount();
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
                player.displayClientMessage(Component.translatable(SOUL_RITUAL_UNLOCK_MESSAGE, skill.getNameComponent(skill.getStack())), true);
            }
        }
    }

    /// 祈灵之刃：向前贯穿扁矩形范围，主目标仍由原版结算，这里只补打其余敌怪。
    public static void soulKnifeReach(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.getMainHandItem().is(SoulItems.SPIRITWARD_BLADE.get())) {
            return;
        }
        float scale = player.getAttackStrengthScale(0.5F);
        if (scale < SoulSkillParameters.SPIRITWARD_BLADE_MIN_CHARGE) {
            return;
        }
        Vec3 eye = player.getEyePosition();
        AABB box = new AABB(eye, eye.add(player.getLookAngle().scale(SoulSkillParameters.SPIRITWARD_BLADE_REACH)))
                .inflate(SoulSkillParameters.SPIRITWARD_BLADE_HALF_WIDTH, SoulSkillParameters.SPIRITWARD_BLADE_HALF_HEIGHT, SoulSkillParameters.SPIRITWARD_BLADE_HALF_WIDTH);
        Entity primary = event.getTarget();
        float damage = (float) player.getAttributeValue(LibAttributes.getAttackDamage().value()) * (0.2F + scale * scale * 0.8F);
        DamageSource source = player.damageSources().playerAttack(player);
        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, box,
                entity -> entity != player && entity != primary && entity.isAlive() && !entity.isSpectator() && player.canAttack(entity))) {
            target.hurt(source, damage);
        }
    }

    /// 祈灵之刃：当前灵魂高于阈值时，持有者造成的近战伤害额外提升。
    public static void soulKnifeBonusDamage(PortLivingIncomingDamageEvent event) {
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer player)
                || !player.getMainHandItem().is(SoulItems.SPIRITWARD_BLADE.get())) {
            return;
        }
        if (getSoul(player) > SoulSkillParameters.SPIRITWARD_BLADE_SOUL_THRESHOLD) {
            event.setAmount(event.getAmount() + SoulSkillParameters.SPIRITWARD_BLADE_DAMAGE_BONUS);
        }
    }

    /// 灵魂短弓 / 灵魂弓：当前灵魂高于阈值时，由它射出的箭矢额外提升伤害。
    public static void soulBowBonusDamage(PortLivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)
                || !(arrow.getOwner() instanceof ServerPlayer player)
                || !(ISoulBow.isSoulBow(player.getMainHandItem()) || ISoulBow.isSoulBow(player.getOffhandItem()))) {
            return;
        }
        if (getSoul(player) > SoulSkillParameters.SOUL_BOW_SOUL_THRESHOLD) {
            event.setAmount(event.getAmount() + SoulSkillParameters.SOUL_BOW_DAMAGE_BONUS);
        }
    }

    /// 祈灵之刃 / 灵魂弓 / 灵魂短弓：攻击命中敌怪时有 50% 概率额外获得 1 点灵魂。
    public static void commitSoulDamage(PortLivingDamageEvent.Post event) {
        LivingEntity victim = event.getEntity();
        PortDamageContainer container = currentDamageContainer(victim);
        if (container == null || container.getSource() != event.getSource()) {
            return;
        }

        SoulDamageState state = IDamageContainer.of(container).confluence$getSoulDamageState();
        if (state.finalPositive) {
            return;
        }
        SoulConsumptionPlan plan = state.plan;
        MobEffectInstance effect = state.boilingBlood;
        boolean positive = state.finalDamageCaptured && state.finalDamage > 0.0F;
        state.clear();
        if (!positive) {
            return;
        }

        state.finalPositive = true;
        if (plan != null && LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer owner) {
            applySoulOverdraft(owner, plan.commit(owner));
        }
        if (effect != null && event.getSource().getDirectEntity() instanceof ServerPlayer player
                && player.getEffect(ModEffects.BOILING_BLOOD.get()) == effect) {
            player.removeEffect(ModEffects.BOILING_BLOOD.get());
        }
        soulWeaponHitSoul(event);
        soulWeaponTraitsOnSkillHit(event);
    }

    public static void soulWeaponHitSoul(PortLivingDamageEvent.Post event) {
        if (!Confluence.SOUL_SKILLS || !(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer player)
                || !EnemyDamageRules.isEnemy(event.getEntity()) || !SoulStorage.of(player).canReceive()) {
            return;
        }
        Entity direct = event.getSource().getDirectEntity();
        boolean blade = direct == player && event.getSource().is(DamageTypes.PLAYER_ATTACK)
                && player.getMainHandItem().is(SoulItems.SPIRITWARD_BLADE.get());
        boolean bow = direct instanceof AbstractArrow arrow && arrow.getPersistentData().getBoolean(SOUL_BOW_SOURCE_TAG);
        float chance = Math.min(1.0F, (blade || bow ? SoulSkillParameters.SOUL_WEAPON_HIT_SOUL_CHANCE : 0.0F)
                + getSoulHarvestHitChance(player, direct, event.getEntity()) + getSoulHitChanceBonus(player));
        if (chance > 0 && player.getRandom().nextFloat() < chance) {
            SoulStorage.of(player).receive(SoulSkillParameters.SOUL_WEAPON_HIT_SOUL
                    + getSoulDropBonus(player));
        }
    }

    private static float getSoulHarvestHitChance(ServerPlayer player, @Nullable Entity direct, LivingEntity target) {
        return direct != player && target != player && isPassiveSoulSkillActive(player, ModSoulSkills.SOUL_HARVEST.get())
                ? SoulSkillParameters.SOUL_HARVEST_CHANCE : 0.0F;
    }

    public static float getSoulHitChanceBonus(Player player) {
        float chance = 0;
        for (ItemStack accessory : CuriosUtils.getCurios(player)) {
            PrefixComponent prefix = PrefixUtils.getPrefix(accessory);
            if (prefix != null && prefix.type() == PrefixType.ACCESSORY && prefix.name().equals(ModPrefix.Accessory.HAUNTING.name())) {
                chance += SoulSkillParameters.HAUNTING_SOUL_CHANCE;
            }
        }
        return chance;
    }

    public static double getSoulKnockback(LivingEntity owner, double strength) {
        if (strength <= 0 || !(owner.getMainHandItem().getItem() instanceof SoulWeaponItem)) {
            return strength;
        }
        return strength * Math.max(0.0, PrefixUtils.heldItemContribution(owner.getMainHandItem(), 1.0, Attributes.ATTACK_KNOCKBACK));
    }

    public static float getSoulSkillCost(@Nullable Player player, SoulSkillStack stack) {
        SoulSkill skill = stack.getSoulSkill();
        return calculateSoulCost(player, player == null ? skill.getSoulCost(stack) : skill.getSoulCost(player, stack));
    }

    public static float calculateSoulCost(@Nullable Player player, float baseCost) {
        if (player == null || baseCost <= 0 || !(player.getMainHandItem().getItem() instanceof SoulWeaponItem)) {
            return baseCost;
        }
        PrefixComponent prefix = PrefixUtils.getPrefix(player.getMainHandItem());
        ModPrefix.Soul soul = prefix != null && prefix.type() == PrefixType.SOUL ? ModPrefix.Soul.VALUES.get(prefix.name()) : null;
        return soul == null ? baseCost : Math.max(1, baseCost + soul.soulCost());
    }

    public static float getSoulDropBonus(Player player) {
        float bonus = ModArmorBonus.getValue(player, ModArmorBonus.SOUL$DROP);
        if (PlayerSpecialData.of(player).getArmorSetBonusKey() == ModArmorBonus.PHANTOM_SET && LibDateUtils.isNight(player.level())) {
            bonus += SoulSkillParameters.PHANTOM_NIGHT_SOUL_DROP_BONUS;
        }
        return Math.max(0.0F, bonus) + getSoulCollectionAccessoryBonus(player);
    }


    public static float getSoulCollectionAccessoryBonus(Player player) {
        float bonus = 0.0F;
        if (TCUtils.hasType(player, AccessoryItems.SOUL$BINDING$CHAIN)) {
            bonus += SoulSkillParameters.SOUL_COLLECTION_ACCESSORY_BONUS;
        }
        if (TCUtils.hasType(player, AccessoryItems.BAND$OF$SOUL)) {
            bonus += SoulSkillParameters.SOUL_COLLECTION_ACCESSORY_BONUS;
        }
        if (TCUtils.hasType(player, AccessoryItems.MEMORY$OF$BONEBRAND)) {
            bonus += SoulSkillParameters.SOUL_COLLECTION_ACCESSORY_BONUS;
        }
        return bonus;
    }

    /// 主武器自带效果里「技能命中」的那几条：星魄补一颗坠星、丛林的灯附带中毒、无分昼夜附带暗影焰；
    /// 另外统一结算**附魂印记**的回魂：技能命中带印记的敌人回 1 魂。
    ///
    /// 只认灵魂伤害来源，因此普通近战 / 弓箭不会触发。
    public static void soulWeaponTraitsOnSkillHit(PortLivingDamageEvent.Post event) {
        if (!event.getSource().is(LibDamageTypes.SOUL)) {
            return;
        }
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (victim == player) {
            return;
        }
        /// 附魂印记：这只敌人身上有印记，任何技能（灵魂伤害）打中它都回 1 魂。
        if (victim.hasEffect(ModEffects.SOUL_MARK.get())) {
            adjustSoul(player, SoulSkillParameters.SOUL_MARK_HIT_SOUL);
        }
        if (holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.STAR_ON_SKILL_HIT)
                && !isStarProjectile(event)
                && player.getRandom().nextFloat() < SoulSkillParameters.STAR_SPIRIT_STAR_CHANCE) {
            Vec3 center = victim.getBoundingBox().getCenter();
            float damage = getSoulSkillDamage(player, ModSoulSkills.STAR_CALL.get().getStack());
            SoulLureProjectile star = SoulLureProjectile.star(player,
                    new Vec3(center.x, starCallSpawnY(player.level(), victim), center.z),
                    new Vec3(0.0, -1.0, 0.0), damage, 2.0F, 0.0);
            player.level().addFreshEntity(star);
        }
        if (victim.isAlive() && holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.POISON_ON_SKILL_HIT)) {
            victim.addEffect(new MobEffectInstance(MobEffects.POISON, SoulSkillParameters.LIFE_SPLENDOR_POISON_TICKS, 0, false, true, true));
        }
        if (victim.isAlive() && holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.SHADOWFLAME_ON_SKILL_HIT)) {
            victim.addEffect(new MobEffectInstance(ModEffects.SHADOWFLAME.get(), SoulSkillParameters.DAY_AND_NIGHT_ALIKE_SHADOWFLAME_TICKS, 0, false, true, true));
        }
    }

    /// 星魄的坠星本身也是灵魂伤害：它命中时不再触发星魄，避免自我循环。
    private static boolean isStarProjectile(PortLivingDamageEvent.Post event) {
        return event.getSource().getDirectEntity() instanceof SoulLureProjectile lure && lure.isStarAppearance();
    }

    /// 逐灵全套：当前灵魂高于阈值时造成的伤害提高。
    public static void seekerSetDamage(PortLivingIncomingDamageEvent event) {
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer player)
                || PlayerSpecialData.of(player).getArmorSetBonusKey() != ModArmorBonus.SEEKER_SET
                || getSoul(player) <= SoulSkillParameters.SEEKER_DAMAGE_SOUL_THRESHOLD) {
            return;
        }
        event.setAmount(event.getAmount() * (1.0F + SoulSkillParameters.SEEKER_DAMAGE_BONUS));
    }

    /// 耐力效果：所受伤害降低 10%。
    public static void enduranceDamageReduction(PortLivingIncomingDamageEvent event) {
        if (event.getEntity().hasEffect(ModEffects.ENDURANCE.get())) {
            event.setAmount(event.getAmount() * 0.9F);
        }
    }

    /// 魂舞全套：收集灵魂后的限时减伤。
    public static void souldancerDamageReduction(PortLivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.level().getGameTime() < SoulStorage.of(player).getSouldancerBuffEnd()) {
            event.setAmount(event.getAmount() * (1.0F - SoulSkillParameters.SOULDANCER_DAMAGE_REDUCTION));
        }
    }

    /// 魂师召唤物被动：耗魂换取增伤。
    public static void soulSkillSummonDamage(PortLivingIncomingDamageEvent event) {
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer owner)) {
            return;
        }
        Entity direct = event.getSource().getDirectEntity();
        if (direct == owner || event.getEntity() == owner) {
            return;
        }
        if (isPassiveSoulSkillActive(owner, ModSoulSkills.SOUL_DEVOUR.get())) {
            SoulConsumptionPlan plan = planSoulConsumption(owner, calculateSoulCost(owner, SoulSkillParameters.SOUL_DEVOUR_SOUL_COST));
            if (plan == null) {
                return;
            }
            IDamageContainer.of(event.getContainer()).confluence$getSoulDamageState().plan = plan;
            event.setAmount(event.getAmount() * (1.0F + SoulSkillParameters.SOUL_DEVOUR_DAMAGE_BONUS));
        }
    }

    /// 血液沸腾：把下一次近战攻击放大并消耗掉增益。
    public static void boilingBloodHit(PortLivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof ServerPlayer player) || !player.hasEffect(ModEffects.BOILING_BLOOD.get())) {
            return;
        }
        MobEffectInstance effect = player.getEffect(ModEffects.BOILING_BLOOD.get());
        IDamageContainer.of(event.getContainer()).confluence$getSoulDamageState().boilingBlood = effect;
        event.setAmount(event.getAmount() * (1.0F + SoulSkillParameters.BOILING_BLOOD_DAMAGE_MULTIPLIER));
    }

    /// 灵魂缓冲把摔落伤害改成回复；生命归还记录期间失去的生命。
    public static void soulSkillOnHurt(PortLivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.IS_FALL) && player.hasEffect(ModEffects.SOUL_BUFFER.get())) {
            player.heal(event.getAmount() * SoulSkillParameters.SOUL_BUFFER_CONVERT_RATIO);
            event.setCanceled(true);
            return;
        }
        if (player.hasEffect(ModEffects.LIFE_RETURN.get())) {
            SoulStorage storage = SoulStorage.of(player);
            storage.setLifeReturnLost(storage.getLifeReturnLost() + event.getAmount());
        }
    }

    /// 灵魂短弓 / 灵魂弓：用它击杀的敌怪额外收取灵魂。
    public static void soulBowExtraSoul(SoulCollectionEvent event) {
        Player player = event.getEntity();
        if (ISoulBow.isSoulBow(player.getMainHandItem()) || ISoulBow.isSoulBow(player.getOffhandItem())) {
            event.setAmount(event.getAmount() + SoulSkillParameters.SOUL_BOW_KILL_SOUL_BONUS);
        }
    }

    /// 祈灵之刃：用它击杀的敌怪额外收取灵魂。
    public static void soulKnifeExtraSoul(SoulCollectionEvent event) {
        if (event.getEntity().getMainHandItem().is(SoulItems.SPIRITWARD_BLADE.get())) {
            event.setAmount(event.getAmount() + SoulSkillParameters.SPIRITWARD_BLADE_KILL_SOUL_BONUS);
        }
    }

    /// 束魂锁链 / 手环系列（灵魂手环、灵魂再生手环、通灵手环共用 `band_of_soul` 标记）/ 刻骨的记忆：
    /// 每佩戴一种，每次收魂额外 +1 灵魂。
    ///
    /// 三种手环共用同一个 `ValueType.UnitType`，因此戴多只也只算一份加成。
    public static void soulCollectionAccessoryBonus(SoulCollectionEvent event) {
        event.setAmount(event.getAmount() + getSoulCollectionAccessoryBonus(event.getEntity()));
    }

    /// 残破微光：手持时获得灵魂，有 20% 概率回复 4 点生命。
    ///
    /// 「获得灵魂」走收魂事件（击杀结算），灵魂再生与灵魂手环的缓慢回复不受它影响。
    public static void brokenGlimmerHeal(SoulCollectionEvent event) {
        Player player = event.getEntity();
        if (!holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.HEAL_ON_SOUL_GAIN)) {
            return;
        }
        if (player.getRandom().nextFloat() < SoulSkillParameters.BROKEN_GLIMMER_HEAL_CHANCE) {
            player.heal(SoulSkillParameters.BROKEN_GLIMMER_HEAL_AMOUNT);
        }
    }

    /// 魂师套装：逐件与套装的灵魂掉落加成统一汇总，幻翼套在夜间再加。
    public static void soulDropBonus(SoulCollectionEvent event) {
        Player player = event.getEntity();
        event.setAmount(event.getAmount() + getSoulDropBonus(player));
        onSoulCollected(player, player.level().getGameTime());
    }

    /// 血怒：击杀额外收魂。
    public static void bloodRageSoul(SoulCollectionEvent event) {
        if (event.getEntity().hasEffect(ModEffects.BLOOD_RAGE.get())) {
            event.setAmount(event.getAmount() + SoulSkillParameters.BLOOD_RAGE_KILL_SOUL_BONUS);
        }
    }

    /// 索敌：半径内**最近的敌怪**（只认敌怪阵营，球形距离），没有目标返回 null。
    ///
    /// 魂引天星与亵渎之魂共用这一套口径。
    public static @Nullable LivingEntity findNearestEnemy(ServerPlayer player, double radius) {
        double limit = radius * radius;
        double best = limit;
        LivingEntity nearest = null;
        for (LivingEntity candidate : player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(radius),
                target -> target.isAlive() && !target.isSpectator() && !target.isAlliedTo(player)
                        && target.distanceToSqr(player) <= limit
                        && EnemyDamageRules.isEnemy(target))) {
            double distance = candidate.distanceToSqr(player);
            if (distance < best) {
                best = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    public record RitualUnlock(int ritual, List<Supplier<SoulSkill>> required, Supplier<SoulSkill> skill) {}

    private static final List<RitualUnlock> RITUAL_UNLOCKS = List.of(
            new RitualUnlock(1, List.of(), ModSoulSkills.ENHANCED_LURE),
            new RitualUnlock(1, List.of(), ModSoulSkills.SOUL_MARK),
            new RitualUnlock(1, List.of(), ModSoulSkills.SOUL_DRAIN),
            new RitualUnlock(1, List.of(ModSoulSkills.SPIRIT_SURGE), ModSoulSkills.SOUL_SURGE),
            new RitualUnlock(1, List.of(ModSoulSkills.SPIRIT_SURGE), ModSoulSkills.LURE_SURGE),
            new RitualUnlock(1, List.of(ModSoulSkills.SPIRIT_SURGE), ModSoulSkills.EMPOWERED_SURGE),
            new RitualUnlock(1, List.of(ModSoulSkills.SPIRIT_SURGE), ModSoulSkills.SPIRIT_TRIGGER),
            new RitualUnlock(1, List.of(), ModSoulSkills.SOUL_ROTATION),
            new RitualUnlock(2, List.of(), ModSoulSkills.STAR_REVERSAL),
            new RitualUnlock(2, List.of(), ModSoulSkills.STAR_LINK),
            new RitualUnlock(2, List.of(), ModSoulSkills.SOUL_HARVEST),
            new RitualUnlock(3, List.of(), ModSoulSkills.SOUL_ASSAULT),
            new RitualUnlock(3, List.of(), ModSoulSkills.SOUL_DEVOUR),
            new RitualUnlock(4, List.of(), ModSoulSkills.BACKLASH_RESISTANCE),
            new RitualUnlock(4, List.of(), ModSoulSkills.SOUL_CONTROL),
            new RitualUnlock(4, List.of(), ModSoulSkills.LIFE_RETURN));

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
            if (!PlayerUtils.hasItemInInventory(player, item)) {
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

    public static boolean isSoulBookGranted(Player player, SoulBooks.Definition book) {
        return book.skill() == null ? isSoulBookLearned(player, book.id()) : isSoulSkillLearned(player, book.skill());
    }

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

    public static boolean isSoulBookLearned(Player player, ResourceLocation bookId) {
        return player != null && SoulSkillStorage.of(player).getLearnedSoulBooks().contains(bookId);
    }

    public static void applySoulBookReview(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(ModEffects.GO_OVER.get(), SoulSkillParameters.SOUL_BOOK_REVIEW_DURATION_TICKS, 0, false, true, true));
    }

    /// 从当前槽向后（含回绕）找第一个「非空、非被动、当前可用且未冷却」的槽位；找不到返回 -1。
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
            if (candidate.isEmpty() || candidate.getSoulSkill().isPassive()
                    || candidate.getSoulSkill().is(ModTags.SoulSkills.MAJOR_ACTIVE)) {
                continue;
            }
            ResourceLocation id = candidate.getSoulSkill().getId();
            /// 冷却中的技能不跳过：只按“空槽 / 被动 / 当前不可用”筛选。
            if (!isSoulSkillAvailable(player, id)) {
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
            if (candidate.isEmpty() || candidate.getSoulSkill().isPassive()
                    || candidate.getSoulSkill().is(ModTags.SoulSkills.MAJOR_ACTIVE)) {
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

    public static void tickSoulSkillStates(ServerPlayer player, long gameTime) {
        if (!Confluence.SOUL_SKILLS) {
            return;
        }
        SoulStorage storage = SoulStorage.of(player);
        if (player.hasEffect(ModEffects.STAR_CALL.get()) && gameTime % SoulSkillParameters.STAR_CALL_INTERVAL_TICKS == 0) {
            tickStarCall(player, storage);
        }
        /// 附魂激荡：「附魂激荡」增益期间按间隔自动释放灵魂激荡，且完全不消耗灵魂
        if (gameTime % SoulSkillParameters.SOUL_SURGE_INTERVAL_TICKS == 0
                && player.hasEffect(ModEffects.SOUL_SURGE.get())
                && isSoulSkillLearned(player, ModSoulSkills.SOUL_SURGE.get().getId())) {
            float damage = getSoulSkillDamage(player, ModSoulSkills.SPIRIT_SURGE.get().getStack());
            player.level().addFreshEntity(new SpiritSurgeEntity(player, damage));
            /// 灵魂回响对自动释放的灵魂激荡同样生效（额外那一发不耗魂）。
            SpiritSurgeSkill.rollSoulEcho(player, damage);
        }
        /// 灵魂突击：增益期间按间隔把灵魂转成子弹
        if (player.hasEffect(ModEffects.SOUL_ASSAULT.get()) && gameTime % SoulSkillParameters.SOUL_ASSAULT_INTERVAL_TICKS == 0) {
            SoulAssaultSkill.fireBurst(player);
        }
        boolean draining = player.hasEffect(ModEffects.SOUL_DRAIN.get());
        if (storage.isSoulDrainActive() && !draining) {
            player.addEffect(new MobEffectInstance(ModEffects.SOUL_DRAIN_LOCK.get(), SoulSkillParameters.SOUL_DRAIN_LOCKOUT_TICKS, 0, false, true, true));
        }
        storage.setSoulDrainActive(draining);
        boolean returning = player.hasEffect(ModEffects.LIFE_RETURN.get());
        if (storage.isLifeReturnActive() && !returning) {
            float lost = storage.getLifeReturnLost();
            if (lost > 0.0F) {
                storage.receive(lost * SoulSkillParameters.LIFE_RETURN_RATIO);
            }
            storage.setLifeReturnLost(0.0F);
        }
        storage.setLifeReturnActive(returning);
        /// 星魂连结：灵魂增伤增益结束时继承一半数值、一半时长的魔法增伤
        tickStarLink(player, storage);
    }

    /// 星魂连结：检测「强力附魂 / 复习」这两个带灵魂伤害加成的增益是否结束。
    ///
    /// 结束时按记录到的完整时长与数值各折半，挂上 `STAR_LINK` 并写入临时魔法伤害修饰符；
    /// 增益不在身时移除修饰符。被动未装备时不生成新的继承增益。
    private static void tickStarLink(ServerPlayer player, SoulStorage storage) {
        boolean passive = isPassiveSoulSkillActive(player, ModSoulSkills.STAR_LINK.get());
        MobEffectInstance enhanced = player.getEffect(ModEffects.ENHANCED_SOUL.get());
        boolean enhancedNow = enhanced != null;
        if (enhancedNow) {
            int duration = enhanced.getDuration();
            storage.setEnhancedSoulFullDuration(storage.isEnhancedSoulActive()
                    ? Math.max(storage.getEnhancedSoulFullDuration(), duration) : duration);
        } else if (storage.isEnhancedSoulActive()) {
            grantStarLink(player, storage, storage.getEnhancedSoulFullDuration(), SoulSkillParameters.ENHANCED_SOUL_DAMAGE_BONUS, passive);
        }
        storage.setEnhancedSoulActive(enhancedNow);

        MobEffectInstance goOver = player.getEffect(ModEffects.GO_OVER.get());
        boolean goOverNow = goOver != null;
        if (goOverNow) {
            int duration = goOver.getDuration();
            storage.setGoOverFullDuration(storage.isGoOverActive()
                    ? Math.max(storage.getGoOverFullDuration(), duration) : duration);
        } else if (storage.isGoOverActive()) {
            grantStarLink(player, storage, storage.getGoOverFullDuration(), SoulSkillParameters.GO_OVER_SOUL_DAMAGE_BONUS, passive);
        }
        storage.setGoOverActive(goOverNow);

        boolean active = player.hasEffect(ModEffects.STAR_LINK.get());
        if (!active && storage.getStarLinkMagicBonus() > 0.0F) {
            storage.setStarLinkMagicBonus(0.0F);
        }
        applyTransientModifier(player, LibAttributes.getMagicDamage().value(), STAR_LINK_MAGIC_ID,
                storage.getStarLinkMagicBonus(), PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                active && storage.getStarLinkMagicBonus() > 0.0F);
    }

    /// 给玩家挂上继承用的魔法增伤增益；数值与时长为来源增益的一半。
    ///
    /// 若继承增益尚未结束又触发了另一次继承，保留更高的数值，并取较长的剩余时长，避免被较弱来源覆盖。
    private static void grantStarLink(ServerPlayer player, SoulStorage storage, int sourceDuration, float sourceBonus, boolean passive) {
        if (!passive || sourceDuration <= 0) {
            return;
        }
        int duration = Math.max(1, Math.round(sourceDuration * SoulSkillParameters.STAR_LINK_MAGIC_DURATION_RATIO));
        float bonus = sourceBonus * SoulSkillParameters.STAR_LINK_MAGIC_BONUS_RATIO;
        MobEffectInstance active = player.getEffect(ModEffects.STAR_LINK.get());
        if (active != null && storage.getStarLinkMagicBonus() > bonus) {
            bonus = storage.getStarLinkMagicBonus();
            duration = Math.max(duration, active.getDuration());
        }
        storage.setStarLinkMagicBonus(bonus);
        player.addEffect(new MobEffectInstance(ModEffects.STAR_LINK.get(), duration, 0, false, true, true));
        applyTransientModifier(player, LibAttributes.getMagicDamage().value(), STAR_LINK_MAGIC_ID,
                storage.getStarLinkMagicBonus(), PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, true);
    }

    /// 魂引天星：每轮给半径内**敌怪**各落一颗坠星，只只之间错开 `STAR_CALL_STAGGER_TICKS` 刻。
    ///
    /// **额外坠星齐射不消耗灵魂**：灵魂只在施放时按 `STAR_CALL_SOUL_COST` 扣一次，
    /// 之后增益期间的每一轮齐射都是免费的（与附魂激荡的免费自动施放同一口径）。
    /// 需要恢复「每轮扣魂」时把 `STAR_CALL_VOLLEY_SOUL_COST` 调成非 0，届时才结算概率返还。
    ///
    /// **有索敌**：先取半径内的敌怪（按距离由近到远，最多 `STAR_CALL_STARS_PER_VOLLEY` 只），
    /// 再在每只头顶落一颗；范围内没有敌怪时这一轮**完全不落星**。
    private static void tickStarCall(ServerPlayer player, SoulStorage storage) {
        float volleyCost = SoulSkillParameters.STAR_CALL_VOLLEY_SOUL_COST;
        /// 只有显式给了轮次成本时才按轮扣魂并结算返还；默认 0，整轮免费。
        if (volleyCost > 0.0F) {
            if (storage.getCurrentSoul() < calculateSoulCost(player, volleyCost) || !consumeSoul(player, volleyCost)) {
                return;
            }
            if (player.getRandom().nextFloat() < SoulSkillParameters.STAR_CALL_REFUND_CHANCE) {
                storage.receive(1.0F);
            }
        }
        List<LivingEntity> targets = starCallTargets(player);
        if (targets.isEmpty()) {
            return;
        }
        float damage = getCastSoulSkillDamage(player, ModSoulSkills.STAR_CALL.get());
        int delay = 0;
        for (LivingEntity target : targets) {
            Vec3 position = new Vec3(target.getX(), starCallSpawnY(player.level(), target), target.getZ());
            SoulLureProjectile star = SoulLureProjectile.star(player, position, new Vec3(0.0, -1.0, 0.0), damage, 2.0F, 0.0);
            star.setSpawnDelay(delay);
            player.level().addFreshEntity(star);
            delay += SoulSkillParameters.STAR_CALL_STAGGER_TICKS;
        }
    }

    /// 索敌：半径内的敌怪，按距离由近到远，最多 `STAR_CALL_STARS_PER_VOLLEY` 只。
    ///
    /// 用球形距离判定而不是单纯的包围盒，`STAR_CALL_TARGET_RADIUS` 才是真正的「多少格以内」。
    private static List<LivingEntity> starCallTargets(ServerPlayer player) {
        double radius = SoulSkillParameters.STAR_CALL_TARGET_RADIUS;
        double radiusSqr = radius * radius;
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(radius),
                candidate -> candidate.isAlive() && !candidate.isSpectator() && !candidate.isAlliedTo(player)
                        && candidate.distanceToSqr(player) <= radiusSqr
                        && EnemyDamageRules.isEnemy(candidate));
        targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(player)));
        int cap = Math.max(1, SoulSkillParameters.STAR_CALL_STARS_PER_VOLLEY);
        return targets.size() > cap ? targets.subList(0, cap) : targets;
    }

    /// 落点高度：目标上方 `STAR_CALL_SPAWN_HEIGHT` 格；若那里被实心方块占住，向下找第一个空位。
    public static double starCallSpawnY(Level level, LivingEntity target) {
        double y = target.getY() + SoulSkillParameters.STAR_CALL_SPAWN_HEIGHT;
        double floor = target.getY() + 1.0;
        while (y > floor && !level.isEmptyBlock(BlockPos.containing(target.getX(), y, target.getZ()))) {
            y -= 1.0;
        }
        return Math.max(floor, y);
    }
}
