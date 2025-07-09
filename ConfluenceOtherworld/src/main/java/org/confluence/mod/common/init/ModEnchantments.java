package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.enchantment.SummonItemEffect;

import java.util.List;

public final class ModEnchantments {
    public static final ResourceKey<Enchantment> MANA_REGENERATION = Confluence.asResourceKey(Registries.ENCHANTMENT, "mana_regeneration");
    public static final ResourceKey<Enchantment> EFFICIENT_MAGIC = Confluence.asResourceKey(Registries.ENCHANTMENT, "efficient_magic");
    public static final ResourceKey<Enchantment> MANA_MENDING = Confluence.asResourceKey(Registries.ENCHANTMENT, "mana_mending");
    public static final ResourceKey<Enchantment> CELESTIAL_ABSORPTION = Confluence.asResourceKey(Registries.ENCHANTMENT, "celestial_absorption");
    public static final ResourceKey<Enchantment> SOOTHED_MANA = Confluence.asResourceKey(Registries.ENCHANTMENT, "soothed_mana");
    public static final ResourceKey<Enchantment> ARCANE_PROTECTION = Confluence.asResourceKey(Registries.ENCHANTMENT, "arcane_protection");
    public static final ResourceKey<Enchantment> SPELL_DESPERATION = Confluence.asResourceKey(Registries.ENCHANTMENT, "spell_desperation");
    public static final ResourceKey<Enchantment> MYSTIC_SURGE = Confluence.asResourceKey(Registries.ENCHANTMENT, "mystic_surge");

    public static final class EffectComponentTypes {
        public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Confluence.MODID);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_REGENERATION = registerCommon("mana_regeneration", LootContextParamSets.ENCHANTED_ENTITY);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> EFFICIENT_MAGIC = registerUnit("efficient_magic");
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_MENDING = registerCommon("mana_mending", LootContextParamSets.ENCHANTED_ITEM);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> ATTACK_DROPS_MANA = registerTargeted("attack_drops_mana", LootContextParamSets.ENCHANTED_DAMAGE);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_SICKNESS_DURATION_REDUCE = registerCommon("mana_sickness_duration_reduce", LootContextParamSets.ENCHANTED_ENTITY);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MANA_PROTECTION = registerCommon("mana_protection", LootContextParamSets.ENCHANTED_DAMAGE);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> LESS_MANA_MORE_ATTACK = registerCommon("less_mana_more_attack", LootContextParamSets.ENCHANTED_DAMAGE);
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MORE_MANA_MORE_ATTACK = registerCommon("more_mana_more_attack", LootContextParamSets.ENCHANTED_DAMAGE);

        private static DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> registerCommon(String name, LootContextParamSet paramSet) {
            return TYPES.register(name, () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder().persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, paramSet).listOf()).build());
        }

        private static DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> registerUnit(String name) {
            return TYPES.register(name, () -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
        }

        private static DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> registerTargeted(String name, LootContextParamSet paramSet) {
            return TYPES.register(name, () -> DataComponentType.<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>builder().persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, paramSet).listOf()).build());
        }
    }

    public static final class EntityEffectTypes {
        public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Confluence.MODID);

        static {
            TYPES.register("summon_item", () -> SummonItemEffect.CODEC);
        }
    }

    public static void register(IEventBus eventBus) {
        EffectComponentTypes.TYPES.register(eventBus);
        EntityEffectTypes.TYPES.register(eventBus);
    }
}
