package org.confluence.mod.util;

import com.google.common.collect.Streams;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEnchantments;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.minecraft.world.item.enchantment.Enchantment.*;
import static net.minecraft.world.item.enchantment.EnchantmentHelper.runIterationOnItem;

public final class EnchantmentUtils {
    public static final Function<EquipmentSlot.Type, EquipmentSlot[]> slotsByType = new Function<>() {
        private final EnumMap<EquipmentSlot.Type, EquipmentSlot[]> cache = createCache();

        @Override
        public EquipmentSlot[] apply(EquipmentSlot.Type type) {
            return cache.get(type);
        }

        private static EnumMap<EquipmentSlot.Type, EquipmentSlot[]> createCache() {
            Map<EquipmentSlot.Type, ArrayList<EquipmentSlot>> map = new HashMap<>();
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                map.computeIfAbsent(slot.getType(), type -> new ArrayList<>()).add(slot);
            }
            EnumMap<EquipmentSlot.Type, EquipmentSlot[]> enumMap = new EnumMap<>(EquipmentSlot.Type.class);
            map.forEach((type, slots) -> enumMap.put(type, slots.toArray(EquipmentSlot[]::new)));
            return enumMap;
        }
    };
    public static final EquipmentSlot[] HUMANOID_ARMOR_AND_MAIN_HAND = Streams.concat(Arrays.stream(slotsByType.apply(EquipmentSlot.Type.HUMANOID_ARMOR)), Stream.of(EquipmentSlot.MAINHAND)).toArray(EquipmentSlot[]::new);

    public static float processManaRegeneration(ServerPlayer player) {
        MutableFloat value = new MutableFloat(1);
        for (EquipmentSlot slot : HUMANOID_ARMOR_AND_MAIN_HAND) {
            runIterationOnItem(player.getItemBySlot(slot), (enchantment, level) -> {
                List<ConditionalEffect<EnchantmentValueEffect>> effects = enchantment.value().getEffects(ModEnchantments.EffectComponentTypes.MANA_REGENERATION.get());
                LootContext context = entityContext(player.serverLevel(), level, player, player.position());
                applyEffects(effects, context, effect -> value.setValue(effect.process(level, player.getRandom(), value.floatValue())));
            });
        }
        return value.floatValue();
    }

    public static float processEfficientMagic(ServerPlayer player) {
        if (EnchantmentHelper.has(player.getMainHandItem(), ModEnchantments.EffectComponentTypes.EFFICIENT_MAGIC.get())) {
            ManaStorage manaStorage = player.getData(ModAttachmentTypes.MANA_STORAGE);
            return Mth.lerp(manaStorage.getCurrentMana() / manaStorage.getMaxMana(), 0.5F, 1.0F);
        }
        return 1.0F;
    }

    public static void repairPlayerItems(ServerPlayer player, float consumedManaAmount) {
        Optional<EnchantedItemInUse> optional = EnchantmentHelper.getRandomItemWith(ModEnchantments.EffectComponentTypes.MANA_MENDING.get(), player, ItemStack::isDamaged);
        if (optional.isPresent()) {
            ItemStack stack = optional.get().itemStack();
            MutableFloat threshold = new MutableFloat(8);
            runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyItemFilteredCount(
                    ModEnchantments.EffectComponentTypes.MANA_MENDING.get(), player.serverLevel(), level, stack, threshold
            ));
            int delta = (int) (consumedManaAmount - Math.max(0, threshold.floatValue()));
            if (delta > 1) {
                stack.setDamageValue(stack.getDamageValue() - delta);
            }
        }
    }

    public static void dropsStar(ServerPlayer player, LivingEntity victim, DamageSource damageSource) {
        ItemStack itemStack = player.getMainHandItem();
        EnchantedItemInUse item = new EnchantedItemInUse(itemStack, EquipmentSlot.MAINHAND, player);
        runIterationOnItem(itemStack, (enchantment, level) -> {
            for (TargetedConditionalEffect<EnchantmentEntityEffect> effect : enchantment.value().getEffects(ModEnchantments.EffectComponentTypes.ATTACK_DROPS_STAR.get())) {
                if (effect.enchanted() == EnchantmentTarget.ATTACKER) {
                    doPostAttack(effect, player.serverLevel(), level, item, victim, damageSource);
                }
            }
        });
    }

    public static int processManaSicknessDuration(ServerPlayer player, int duration) {
        MutableFloat ratio = new MutableFloat(1);
        ItemStack itemStack = player.getMainHandItem();
        runIterationOnItem(itemStack, (enchantment, level) -> enchantment.value().modifyEntityFilteredValue(
                ModEnchantments.EffectComponentTypes.MANA_SICKNESS_DURATION_REDUCE.get(), player.serverLevel(), level, itemStack, player, ratio
        ));
        return (int) (duration * Math.max(0, ratio.floatValue()));
    }
}
