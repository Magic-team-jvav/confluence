package org.confluence.mod.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.confluence.lib.util.LibEnchantmentUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.item.enchantment.Enchantment.*;
import static net.minecraft.world.item.enchantment.EnchantmentHelper.runIterationOnItem;

public final class EnchantmentUtils {
    /// 玩家身上参与附魔判定的格子：四件盔甲 + 主副手。
    private static final EquipmentSlot[] PLAYER_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
    };

    /// 快速蓄力各等级减少的蓄力时间比例，下标即等级（0 为占位）。
    private static final float[] RAPID_CHARGE_REDUCTION = {0.0F, 0.30F, 0.50F, 0.80F};

    /// 取玩家身上某附魔的最高等级，没有则 0。
    public static int getLevel(Player player, ResourceKey<Enchantment> key) {
        int level = 0;
        for (EquipmentSlot slot : PLAYER_SLOTS) {
            level = Math.max(level, getLevel(player.getItemBySlot(slot), key));
        }
        return level;
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
        int level = getLevel(stack, ModEnchantments.RAPID_CHARGE);
        if (level <= 0) return 1.0F;
        float reduction = RAPID_CHARGE_REDUCTION[Math.min(level, RAPID_CHARGE_REDUCTION.length - 1)];
        return 1.0F / (1.0F - reduction);
    }

    /// 取单件物品上某附魔的等级，没有则 0。
    public static int getLevel(@Nullable ItemStack stack, ResourceKey<Enchantment> key) {
        return LibEnchantmentUtils.getEnchantmentLevel(key, stack);
    }

    public static float processManaRegeneration(ServerPlayer player) {
        MutableFloat value = new MutableFloat(1);
        for (EquipmentSlot slot : LibEnchantmentUtils.SlotGroups.ARMOR_N_MAINHAND) {
            runIterationOnItem(player.getItemBySlot(slot), (enchantment, level) -> {
                List<ConditionalEffect<EnchantmentValueEffect>> effects = enchantment.value().getEffects(ModEnchantments.EffectComponentTypes.MANA_REGENERATION.get());
                LootContext context = entityContext(player.serverLevel(), level, player, player.position());
                applyEffects(effects, context, effect -> value.setValue(effect.process(level, player.getRandom(), value.floatValue())));
            });
        }
        return value.floatValue();
    }

    public static FloatSupplier processEfficientMagic(FloatSupplier sup, ServerPlayer player) {
        if (LibEntityUtils.anyHandHasItem(player, stack -> EnchantmentHelper.has(stack, ModEnchantments.EffectComponentTypes.EFFICIENT_MAGIC.get()))) {
            ManaStorage manaStorage = ManaStorage.of(player);
            return () -> sup.getAsFloat() * Mth.lerp(manaStorage.getCurrentMana() / manaStorage.getMaxMana(), 0.5F, 1.0F);
        }
        return sup;
    }

    public static void repairPlayerItems(ServerPlayer player, float consumedManaAmount) {
        Optional<EnchantedItemInUse> optional = EnchantmentHelper.getRandomItemWith(ModEnchantments.EffectComponentTypes.MANA_MENDING.get(), player, ItemStack::isDamaged);
        if (optional.isEmpty()) return;
        ItemStack stack = optional.get().itemStack();
        MutableFloat threshold = new MutableFloat(10);
        runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyItemFilteredCount(
                ModEnchantments.EffectComponentTypes.MANA_MENDING.get(), player.serverLevel(), level, stack, threshold
        ));
        int delta = (int) (consumedManaAmount - Math.max(0, threshold.floatValue()));
        if (delta > 1) {
            stack.setDamageValue(stack.getDamageValue() - delta);
        }
    }

    public static void dropsStar(ServerPlayer player, LivingEntity victim, DamageSource damageSource) {
        LibEnchantmentUtils.runIterationOnHand(player, stack -> {
            EnchantedItemInUse item = new EnchantedItemInUse(stack, EquipmentSlot.MAINHAND, player);
            runIterationOnItem(stack, (enchantment, level) -> {
                for (TargetedConditionalEffect<EnchantmentEntityEffect> effect : enchantment.value().getEffects(ModEnchantments.EffectComponentTypes.ATTACK_DROPS_MANA.get())) {
                    if (effect.enchanted() == EnchantmentTarget.ATTACKER) {
                        doPostAttack(effect, player.serverLevel(), level, item, victim, damageSource);
                    }
                }
            });
        });
    }

    public static int processManaSicknessDuration(ServerPlayer player, int duration) {
        MutableFloat ratio = new MutableFloat(1);
        LibEnchantmentUtils.runIterationOnHand(player, stack -> runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyEntityFilteredValue(
                ModEnchantments.EffectComponentTypes.MANA_SICKNESS_DURATION_REDUCE.get(), player.serverLevel(), level, stack, player, ratio
        )));
        return (int) (duration * Math.max(0, ratio.floatValue()));
    }

    public static float processManaProtection(ServerPlayer player, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) return amount;
        MutableFloat ratio = new MutableFloat();
        for (EquipmentSlot slot : LibEnchantmentUtils.SlotGroups.ARMOR) {
            ItemStack itemStack = player.getItemBySlot(slot);
            runIterationOnItem(itemStack, (enchantment, level) -> enchantment.value().modifyDamageFilteredValue(
                    ModEnchantments.EffectComponentTypes.MANA_PROTECTION.get(), player.serverLevel(), level, itemStack, player, damageSource, ratio
            ));
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
        MutableFloat lm = new MutableFloat();
        LibEnchantmentUtils.runIterationOnHand(player, stack -> runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyDamageFilteredValue(
                ModEnchantments.EffectComponentTypes.LESS_MANA_MORE_ATTACK.get(), player.serverLevel(), level, stack, player, damageSource, lm
        )));
        MutableFloat mm = new MutableFloat();
        LibEnchantmentUtils.runIterationOnHand(player, stack -> runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyDamageFilteredValue(
                ModEnchantments.EffectComponentTypes.MORE_MANA_MORE_ATTACK.get(), player.serverLevel(), level, stack, player, damageSource, mm
        )));
        if (lm.floatValue() > 0 || mm.floatValue() > 0) {
            ManaStorage manaStorage = ManaStorage.of(player);
            float ratio = manaStorage.getCurrentMana() / manaStorage.getMaxMana();
            return amount + amount * ((1 - ratio) * lm.floatValue() + ratio * mm.floatValue());
        }
        return amount;
    }

    /// 处理连枷的风爆附魔效果（flail_wind_burst）。
    ///
    /// 手动遍历玩家手持物品，查找并应用 [ModEnchantments.EffectComponentTypes#WIND_BURST_AT_HIT] 效果。
    ///
    /// @param player       攻击者（连枷持有者）
    /// @param victim       被击中的实体
    /// @param damageSource 伤害来源
    public static boolean processFlailWindBurst(ServerPlayer player, LivingEntity victim, DamageSource damageSource) {
        boolean[] triggered = {false};
        LibEnchantmentUtils.runIterationOnHand(player, stack -> {
            EnchantedItemInUse item = new EnchantedItemInUse(stack, EquipmentSlot.MAINHAND, player);
            runIterationOnItem(stack, (enchantment, level) -> {
                for (TargetedConditionalEffect<EnchantmentEntityEffect> effect : enchantment.value().getEffects(ModEnchantments.EffectComponentTypes.WIND_BURST_AT_HIT.get())) {
                    if (effect.enchanted() == EnchantmentTarget.ATTACKER) {
                        doPostAttack(effect, player.serverLevel(), level, item, victim, damageSource);
                        triggered[0] = true;
                    }
                }
            });
        });
        return triggered[0];
    }
}
