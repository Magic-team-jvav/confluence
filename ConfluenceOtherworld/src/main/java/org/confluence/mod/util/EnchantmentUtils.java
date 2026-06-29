package org.confluence.mod.util;

import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.item.sword.SweetSword;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public final class EnchantmentUtils {
    public static final EquipmentSlot[] HUMANOID_ARMOR = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };
    public static final EquipmentSlot[] HUMANOID_ARMOR_AND_MAIN_HAND = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND
    };

    public static float processManaRegeneration(ServerPlayer player) {
        MutableFloat value = new MutableFloat(1);
        for (EquipmentSlot slot : HUMANOID_ARMOR_AND_MAIN_HAND) {
            PortEnchantmentHelperExtension.runIterationOnItem(player.getItemBySlot(slot), (enchantment, level) -> {
                List<ConditionalEffect<EnchantmentValueEffect>> effects = enchantment.value().getEffects(ModEnchantments.EffectComponentTypes.MANA_REGENERATION.get());
                LootContext context = entityContext(player.serverLevel(), level, player, player.position());
                SweetSword.applyEffects(effects, context, effect -> value.setValue(effect.process(level, player.getRandom(), value.floatValue())));
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

    public static void dropsStar(ServerPlayer player, LivingEntity victim, DamageSource damageSource) {
        runIterationOnHand(player, stack -> {
            EnchantedItemInUse item = new EnchantedItemInUse(stack, EquipmentSlot.MAINHAND, player);
            PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
                for (TargetedConditionalEffect<EnchantmentEntityEffect> effect : enchantment.value().getEffects(ModEnchantments.EffectComponentTypes.ATTACK_DROPS_MANA.get())) {
                    if (effect.enchanted() == EnchantmentTarget.ATTACKER) {
                        doPostAttack(effect, player.serverLevel(), level, item, victim, damageSource);
                    }
                }
            });
        });
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
        for (EquipmentSlot slot : HUMANOID_ARMOR) {
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
        runIterationOnHand(player, stack -> PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
            if (enchantment == ModEnchantments.SPELL_DESPERATION.get()) {
                lm.add(0.5F * level);
            }
        }));
        MutableFloat mm = new MutableFloat();
        runIterationOnHand(player, stack -> PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> {
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

    public static ItemStack enchantedBook(Enchantment enchantment, int level) {
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
        book.enchant(enchantment, level);
        return book;
    }

    public static void runIterationOnHand(ServerPlayer player, Consumer<ItemStack> consumer) {
        consumer.accept(player.getMainHandItem());
        consumer.accept(player.getOffhandItem());
    }
}
