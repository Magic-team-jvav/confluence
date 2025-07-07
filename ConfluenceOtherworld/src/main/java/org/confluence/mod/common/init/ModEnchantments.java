package org.confluence.mod.common.init;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.confluence.mod.Confluence;

import java.util.List;

public final class ModEnchantments {
    public static final ResourceKey<Enchantment> MANA_REGENERATION = Confluence.asResourceKey(Registries.ENCHANTMENT, "mana_regeneration");
    public static final ResourceKey<Enchantment> EFFICIENT_MAGIC = Confluence.asResourceKey(Registries.ENCHANTMENT, "efficient_magic");
    public static final ResourceKey<Enchantment> MANA_MENDING = Confluence.asResourceKey(Registries.ENCHANTMENT, "mana_mending");

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> item = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
        register(context, MANA_REGENERATION, Enchantment.enchantment(
                        Enchantment.definition(
                                new OrHolderSet<>(item.getOrThrow(Tags.Items.ARMORS), item.getOrThrow(ModTags.Items.MANA_WEAPON)),
                                2,
                                3,
                                Enchantment.dynamicCost(25, 25),
                                Enchantment.dynamicCost(75, 25),
                                4,
                                EquipmentSlotGroup.ARMOR, EquipmentSlotGroup.MAINHAND
                        ))
                .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MANA_IO_EXCLUSIVE))
                .withEffect(EffectComponentTypes.MANA_REGENERATION.get(), new AddValue(LevelBasedValue.perLevel(0.1F)))
        );
        register(context, EFFICIENT_MAGIC, Enchantment.enchantment(
                        Enchantment.definition(
                                item.getOrThrow(ModTags.Items.MANA_WEAPON),
                                2,
                                1,
                                Enchantment.dynamicCost(25, 25),
                                Enchantment.dynamicCost(75, 25),
                                4,
                                EquipmentSlotGroup.MAINHAND
                        ))
                .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MANA_IO_EXCLUSIVE))
                .withEffect(EffectComponentTypes.EFFICIENT_MAGIC.get())
        );
        register(context, MANA_MENDING, Enchantment.enchantment(
                        Enchantment.definition(
                                item.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                                2,
                                3,
                                Enchantment.dynamicCost(25, 25),
                                Enchantment.dynamicCost(75, 25),
                                4,
                                EquipmentSlotGroup.ANY
                        ))
                .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MENDING_EXCLUSIVE))
                .withEffect(EffectComponentTypes.MANA_MENDING.get(), new AddValue(LevelBasedValue.perLevel(8, -2)))
        );
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    public static final class EffectComponentTypes {
        public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Confluence.MODID);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_REGENERATION = registerCommon("mana_regeneration", LootContextParamSets.ENCHANTED_ENTITY);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> EFFICIENT_MAGIC = registerUnit("efficient_magic");
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_MENDING = registerCommon("mana_mending", LootContextParamSets.ENCHANTED_ITEM);

        private static DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> registerCommon(String name, LootContextParamSet paramSet) {
            return TYPES.register(name, () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder().persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, paramSet).listOf()).build());
        }

        private static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> registerUnit(String name) {
            return TYPES.register(name, () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
        }
    }

    public static void register(IEventBus eventBus) {
        EffectComponentTypes.TYPES.register(eventBus);
    }
}
