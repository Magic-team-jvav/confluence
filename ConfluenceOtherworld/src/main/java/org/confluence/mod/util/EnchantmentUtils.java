package org.confluence.mod.util;

import com.google.common.collect.Streams;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.confluence.mod.common.init.ModEnchantments;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.minecraft.world.item.enchantment.Enchantment.applyEffects;
import static net.minecraft.world.item.enchantment.Enchantment.entityContext;
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

    public static float processManaRegenerationBoost(ServerPlayer player) {
        MutableFloat value = new MutableFloat(1);
        for (EquipmentSlot slot : HUMANOID_ARMOR_AND_MAIN_HAND) {
            runIterationOnItem(player.getItemBySlot(slot), slot, player, (enchantment, level, item) -> {
                List<ConditionalEffect<EnchantmentValueEffect>> effects = enchantment.value().getEffects(ModEnchantments.EffectComponentTypes.MANA_REGENERATION.get());
                LootContext context = entityContext(player.serverLevel(), level, player, player.position());
                applyEffects(effects, context, effect -> value.setValue(effect.process(level, player.getRandom(), value.floatValue())));
            });
        }
        return value.floatValue();
    }
}
