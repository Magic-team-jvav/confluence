package org.confluence.mod.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.confluence.lib.util.LibEnchantmentUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.enchantment.ManaAffectiveEnchantment;
import org.confluence.mod.common.enchantment.WindBurstEnchantment;
import org.confluence.mod.common.init.ModEnchantments;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;
import org.mesdag.portlib.wrapper.common.extensions.IPortEnchantmentHelperExtension;

import java.util.Map;

public final class EnchantmentUtils {
    /// 玩家身上参与附魔判定的格子：四件盔甲 + 主副手。
    private static final EquipmentSlot[] PLAYER_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
    };

    /// 快速蓄力各等级减少的蓄力时间比例，下标即等级（0 为占位）。
    private static final float[] RAPID_CHARGE_REDUCTION = {0.0F, 0.30F, 0.50F, 0.80F};

    /// 取玩家身上某附魔的最高等级，没有则 0。
    public static int getLevel(Player player, Enchantment enchantment) {
        int level = 0;
        for (EquipmentSlot slot : PLAYER_SLOTS) {
            level = Math.max(level, getLevel(player.getItemBySlot(slot), enchantment));
        }
        return level;
    }

    /// 取单件物品上某附魔的等级，没有则 0。
    public static int getLevel(@Nullable ItemStack stack, Enchantment enchantment) {
        return LibEnchantmentUtils.getEnchantmentLevel(enchantment, stack);
    }

    /// 快速蓄力：按等级减少 30%/50%/80% 的蓄力时间。
    ///
    /// 所有判断蓄力程度的地方（射击结算、满蓄力判定、HUD、模型拉弓动画）都必须走这个入口，
    /// 否则只有伤害变了、视觉上完全看不出区别。
    public static int applyRapidCharge(@Nullable ItemStack stack, int chargeTicks) {
        float multiplier = rapidChargeMultiplier(stack);
        if (multiplier == 1.0F) return chargeTicks;
        return Math.round(chargeTicks * multiplier);
    }

    /// 快速蓄力：按等级减少 30%/50%/80% 的蓄力时间。
    ///
    /// 所有判断蓄力程度的地方（射击结算、满蓄力判定、HUD、模型拉弓动画）都必须走这个入口，
    /// 否则只有伤害变了、视觉上完全看不出区别。
    public static float applyRapidCharge(@Nullable ItemStack stack, float chargeTicks) {
        return chargeTicks * rapidChargeMultiplier(stack);
    }

    /// 快速蓄力的「已蓄力刻数」放大倍率：减少 30%/50%/80% 蓄力时间等价于把刻数放大 1/0.7、1/0.5、1/0.2。
    private static float rapidChargeMultiplier(@Nullable ItemStack stack) {
        int level = getLevel(stack, ModEnchantments.RAPID_CHARGE.get());
        if (level <= 0) return 1.0F;
        float reduction = RAPID_CHARGE_REDUCTION[Math.min(level, RAPID_CHARGE_REDUCTION.length - 1)];
        return 1.0F / (1.0F - reduction);
    }

    public static float processManaRegeneration(ServerPlayer player) {
        MutableFloat value = new MutableFloat(1);
        for (EquipmentSlot slot : LibEnchantmentUtils.SlotGroups.ARMOR_N_MAINHAND) {
            IPortEnchantmentHelperExtension.runIterationOnItem(player.getItemBySlot(slot), (enchantment, level) -> {
                if (enchantment == ModEnchantments.MANA_REGENERATION.get()) {
                    value.add(level * 0.1F);
                }
            });
        }
        return value.floatValue();
    }

    public static FloatSupplier processEfficientMagic(FloatSupplier sup, ServerPlayer player) {
        if (LibEntityUtils.anyHandHasItem(player, stack -> EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.EFFICIENT_MAGIC.get(), stack) > 0)) {
            ManaStorage manaStorage = ManaStorage.of(player);
            return () -> sup.getAsFloat() * Mth.lerp(manaStorage.getCurrentMana() / manaStorage.getMaxMana(), 0.5F, 1.0F);
        }
        return sup;
    }

    public static void repairPlayerItems(ServerPlayer player, float consumedManaAmount) {
        Map.Entry<EquipmentSlot, ItemStack> optional = EnchantmentHelper.getRandomItemWith(ModEnchantments.MANA_MENDING.get(), player, ItemStack::isDamaged);
        if (optional == null) return;
        ItemStack stack = optional.getValue();
        MutableFloat threshold = new MutableFloat(10);
        IPortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
            if (enchantment == ModEnchantments.MANA_MENDING.get()) {
                threshold.subtract(2);
            }
        });
        int delta = (int) (consumedManaAmount - Math.max(0, threshold.floatValue()));
        if (delta > 1) {
            stack.setDamageValue(stack.getDamageValue() - delta);
        }
    }

    public static void affect(ServerPlayer player, LivingEntity victim, DamageSource damageSource) {
        if (damageSource.is(PortTags.DamageTypes.IS_MAGIC)) {
            LibEnchantmentUtils.runIterationOnHand(player, stack ->
                    IPortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
                        if (enchantment instanceof ManaAffectiveEnchantment ench) {
                            ench.affect(player, victim, level);
                        }
                    })
            );
        }
    }

    public static int processManaSicknessDuration(ServerPlayer player, int duration) {
        float ratio = 1 - EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SOOTHED_MANA.get(), player) * 0.1F;
        return (int) (duration * Math.max(0, ratio));
    }

    public static float processManaProtection(ServerPlayer player, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS) || damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return amount;
        }
        MutableFloat ratio = new MutableFloat();
        for (EquipmentSlot slot : LibEnchantmentUtils.SlotGroups.ARMOR) {
            ItemStack stack = player.getItemBySlot(slot);
            IPortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
                if (enchantment == ModEnchantments.ARCANE_PROTECTION.get()) {
                    ratio.add(level * 0.05F);
                }
            });
        }
        if (ratio.floatValue() <= 0) return amount;
        ManaStorage manaStorage = ManaStorage.of(player);
        float clamp = 1 - Mth.clamp(ratio.floatValue(), 0, 0.8F);
        if (manaStorage.forceExtractMana(() -> manaStorage.getMaxMana() * clamp)) {
            PlayerUtils.syncMana2Client(player, manaStorage);
            return amount * clamp;
        }
        return amount;
    }

    public static float processMagicAttack(ServerPlayer player, DamageSource damageSource, float amount) {
        if (!damageSource.is(PortTags.DamageTypes.IS_MAGIC)) {
            return amount;
        }
        MutableFloat lm = new MutableFloat();
        LibEnchantmentUtils.runIterationOnHand(player, stack -> IPortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
            if (enchantment == ModEnchantments.SPELL_DESPERATION.get()) {
                lm.add(0.5F * level);
            }
        }));
        MutableFloat mm = new MutableFloat();
        LibEnchantmentUtils.runIterationOnHand(player, stack -> IPortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
            if (enchantment == ModEnchantments.MYSTIC_SURGE.get()) {
                mm.add(0.5F * level);
            }
        }));
        if (lm.floatValue() > 0 || mm.floatValue() > 0) {
            ManaStorage manaStorage = ManaStorage.of(player);
            float ratio = manaStorage.getCurrentMana() / manaStorage.getMaxMana();
            return amount + amount * ((1 - ratio) * lm.floatValue() + ratio * mm.floatValue());
        }
        return amount;
    }

    /// 处理连枷的风暴锤附魔效果（flail_wind_burst）。
    ///
    /// 1.20.1 没有附魔效果组件，因此这里直接读取手持物品上的附魔等级并执行
    /// [WindBurstEnchantment#applyWindBurst]。伤害来源仅用于与 1.21 侧保持调用签名一致，
    /// 当前实现不参与判定。
    ///
    /// @param player       攻击者（连枷持有者）
    /// @param victim       被击中的实体
    /// @param damageSource 伤害来源
    /// @return 是否触发了风爆效果
    public static boolean processFlailWindBurst(ServerPlayer player, LivingEntity victim, DamageSource damageSource) {
        int level = WindBurstEnchantment.getLevel(player);
        if (level <= 0) return false;
        WindBurstEnchantment.applyWindBurst(player.serverLevel(), level, player, victim);
        return true;
    }
}
