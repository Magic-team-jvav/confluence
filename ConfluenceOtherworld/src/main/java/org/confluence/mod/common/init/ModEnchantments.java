package org.confluence.mod.common.init;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.confluence.mod.Confluence;

import java.util.List;
import java.util.function.UnaryOperator;

public final class ModEnchantments {
    public static final ResourceKey<Enchantment> MANA_REGENERATION_BOOST = Confluence.asResourceKey(Registries.ENCHANTMENT, "mana_regeneration_boost");

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> lookup = context.lookup(Registries.ITEM);
        register(context, MANA_REGENERATION_BOOST, Enchantment.enchantment(
                        Enchantment.definition(
                                new OrHolderSet<>(lookup.getOrThrow(Tags.Items.ARMORS), lookup.getOrThrow(ModTags.Items.MANA_WEAPON)),
                                10,
                                3,
                                Enchantment.dynamicCost(15, 15),
                                Enchantment.dynamicCost(45, 15),
                                1,
                                EquipmentSlotGroup.ARMOR, EquipmentSlotGroup.MAINHAND
                        ))
                .withEffect(EffectComponentTypes.MANA_REGENERATION.get(), new AddValue(LevelBasedValue.perLevel(0.1F)))
        );
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    public static final class EffectComponentTypes {
        public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Confluence.MODID);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_REGENERATION = register("mana_regeneration", builder -> builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));

        private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
            return TYPES.register(name, () -> operator.apply(DataComponentType.builder()).build());
        }
    }

    public static void register(IEventBus eventBus) {
        EffectComponentTypes.TYPES.register(eventBus);
    }
}
