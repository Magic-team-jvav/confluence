package org.confluence.terraentity.data.enchantment;


import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.init.TETags;

import java.util.List;
import java.util.function.Supplier;

// 自定义附魔类，用于定义和注册新的附魔
public class TEEnchantments {
    // 自定义附魔资源键
    public static final ResourceKey<Enchantment> WHIP_SWEEP = key("whip_sweep");
    public static final ResourceKey<Enchantment> MULTI_BOOMERANG = key("multi_boomerang");
    public static final ResourceKey<Enchantment> SUMMONER_PACT = key("summoner_pact");


    // *********************

    public static void bootstrap(BootstrapContext<Enchantment> context) {

        HolderGetter<net.minecraft.world.damagesource.DamageType> damageLookup = context.lookup(Registries.DAMAGE_TYPE);
        HolderGetter<Enchantment> enchantLookup = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> itemLookup = context.lookup(Registries.ITEM);


        // 注册自定义附魔
        register(context, WHIP_SWEEP,
                Enchantment.enchantment(Enchantment.definition(
                        itemLookup.getOrThrow(TETags.Items.WHIP_ENCHANTABLE),
                        2,
                        1,
                        Enchantment.dynamicCost(10, 8),
                        Enchantment.dynamicCost(18, 8),
                        8,
                        EquipmentSlotGroup.MAINHAND
        )));

        register(context, MULTI_BOOMERANG,
                Enchantment.enchantment(Enchantment.definition(
                        itemLookup.getOrThrow(TETags.Items.BOOMERANG_ENCHANTABLE),
                        2,
                        3,
                        Enchantment.dynamicCost(10, 8),
                        Enchantment.dynamicCost(18, 8),
                        8,
                        EquipmentSlotGroup.MAINHAND
        )));

        register(context, SUMMONER_PACT,
                Enchantment.enchantment(Enchantment.definition(
                        itemLookup.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(10, 8),
                        Enchantment.dynamicCost(18, 8),
                        10,
                        EquipmentSlotGroup.HEAD))
                .exclusiveWith(enchantLookup.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                TerraEntity.space("enchantment.summoner_pact"),
                                TEAttributes.MINION_CAPACITY,
                                LevelBasedValue.perLevel(1,0.5f),
                                AttributeModifier.Operation.ADD_VALUE))
        );
    }

    public static final class EffectComponentTypes {
        public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, TerraEntity.MODID);

        public static final Supplier<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> MINION_CAPACITY = registerValue("damage", LootContextParamSets.ENCHANTED_ITEM);


        private static DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> registerValue(String name, LootContextParamSet paramSet) {
            return TYPES.register(name, () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder().persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, paramSet).listOf()).build());
        }
    }
    // 注册附魔的方法
    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    // 创建附魔资源键的方法
    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(TerraEntity.MODID,name));
    }

    public static void register(IEventBus bus) {
//        EffectComponentTypes.TYPES.register(bus);

    }
}
