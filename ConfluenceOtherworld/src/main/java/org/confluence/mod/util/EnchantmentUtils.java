package org.confluence.mod.util;

import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.confluence.lib.util.LibEnchantmentUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.enchantment.ManaAffectiveEnchantment;
import org.confluence.mod.common.init.ModEnchantments;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.Map;

public final class EnchantmentUtils {
    public static float processManaRegeneration(ServerPlayer player) {
        MutableFloat value = new MutableFloat(1);
        for (EquipmentSlot slot : LibEnchantmentUtils.SlotGroups.ARMOR_N_MAINHAND) {
            PortEnchantmentHelperExtension.runIterationOnItem(player.getItemBySlot(slot), (enchantment, level) -> {
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
        PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
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
                    PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
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
            PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
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
        LibEnchantmentUtils.runIterationOnHand(player, stack -> PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
            if (enchantment == ModEnchantments.SPELL_DESPERATION.get()) {
                lm.add(0.5F * level);
            }
        }));
        MutableFloat mm = new MutableFloat();
        LibEnchantmentUtils.runIterationOnHand(player, stack -> PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
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
}
