package org.confluence.mod.util;

import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.supplier.FloatSupplier;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.item.sword.SweetSword;

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
        if (LibEntityUtils.anyHandHasItem(player, stack -> false)) {
            ManaStorage manaStorage = ManaStorage.of(player);
            return () -> sup.getAsFloat() * Mth.lerp(manaStorage.getCurrentMana() / manaStorage.getMaxMana(), 0.5F, 1.0F);
        }
        return sup;
    }

    public static void repairPlayerItems(ServerPlayer player, float consumedManaAmount) {
        Optional<EnchantedItemInUse> optional = Optional.empty();
        if (optional.isEmpty()) return;
        ItemStack stack = optional.get().itemStack();
        MutableFloat threshold = new MutableFloat(10);
        PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyItemFilteredCount(
                ModEnchantments.EffectComponentTypes.MANA_MENDING.get(), player.serverLevel(), level, stack, threshold
        ));
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
        MutableFloat ratio = new MutableFloat(1);
        runIterationOnHand(player, stack -> PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyEntityFilteredValue(
                ModEnchantments.EffectComponentTypes.MANA_SICKNESS_DURATION_REDUCE.get(), player.serverLevel(), level, stack, player, ratio
        )));
        return (int) (duration * Math.max(0, ratio.floatValue()));
    }

    public static float processManaProtection(ServerPlayer player, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) return amount;
        MutableFloat ratio = new MutableFloat();
        for (EquipmentSlot slot : HUMANOID_ARMOR) {
            ItemStack itemStack = player.getItemBySlot(slot);
            PortEnchantmentHelperExtension.runIterationOnItem(itemStack, (enchantment, level) -> enchantment.value().modifyDamageFilteredValue(
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
        runIterationOnHand(player, stack -> PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyDamageFilteredValue(
                ModEnchantments.EffectComponentTypes.LESS_MANA_MORE_ATTACK.get(), player.serverLevel(), level, stack, player, damageSource, lm
        )));
        MutableFloat mm = new MutableFloat();
        runIterationOnHand(player, stack -> PortEnchantmentHelperExtension.runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyDamageFilteredValue(
                ModEnchantments.EffectComponentTypes.MORE_MANA_MORE_ATTACK.get(), player.serverLevel(), level, stack, player, damageSource, mm
        )));
        if (lm.floatValue() > 0 || mm.floatValue() > 0) {
            ManaStorage manaStorage = ManaStorage.of(player);
            float ratio = manaStorage.getCurrentMana() / manaStorage.getMaxMana();
            return amount + amount * ((1 - ratio) * lm.floatValue() + ratio * mm.floatValue());
        }
        return amount;
    }

    public static ItemStack enchantedBook(HolderLookup.RegistryLookup<Enchantment> registryLookup, ResourceKey<Enchantment> key, int level) {
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
        book.enchant(registryLookup.getOrThrow(key), level);
        return book;
    }

    public static void runIterationOnHand(ServerPlayer player, Consumer<ItemStack> consumer) {
        consumer.accept(player.getMainHandItem());
        consumer.accept(player.getOffhandItem());
    }
}
