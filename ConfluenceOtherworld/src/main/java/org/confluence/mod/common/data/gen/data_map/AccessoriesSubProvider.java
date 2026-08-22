package org.confluence.mod.common.data.gen.data_map;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import com.google.common.collect.ImmutableListMultimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibEffects;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCDataMaps;
import org.confluence.terra_curio.common.init.TCItems;
import org.mesdag.portlib.datamap.PortDataMapProvider;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.confluence.terra_curio.common.datagen.TCDataMapProvider.Helper;
import static org.confluence.terra_curio.common.datagen.TCDataMapProvider.wrap;

public class AccessoriesSubProvider {
    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        // 数据映射的编解码器会按集合迭代顺序写出免疫效果；LinkedHashSet 避免每次 DataGen 随机重排。
        Set<MobEffect> ankh = Collections.unmodifiableSet(new LinkedHashSet<>(List.of(
                MobEffects.POISON,
                MobEffects.BLINDNESS,
                MobEffects.MOVEMENT_SLOWDOWN,
                MobEffects.WEAKNESS,
                ModEffects.BLEEDING.get(),
                ModEffects.BROKEN_ARMOR.get(),
                LibEffects.CONFUSED.get(),
                ModEffects.CURSED.get(),
                ModEffects.SILENCED.get(),
                ModEffects.STONED.get()
        )));
        Consumer<Helper> celestial = helper -> {
            ResourceLocation id = helper.asId();
            helper.entry(TCItems.ATTRIBUTES, fourClassesAttribute(id, 0.1)
                    .add(Attributes.ATTACK_SPEED, id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getCriticalChance(), id, 0.02, PortAttributeModifier.Operation.ADD_VALUE)
                    .add(Attributes.ARMOR, id, 4, PortAttributeModifier.Operation.ADD_VALUE)
                    .add(PortAttributesExtension.blockBreakSpeed(), id, 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(ConfluenceMagicLib.SUMMON_KNOCKBACK, id, 0.5, PortAttributeModifier.Operation.ADD_VALUE)
                    .build());
        };
        appender.create()
                .add(TCItems.HAND_WARMER, helper -> {
                    helper.unit(TCItems.FROZEN$IMMUNE);
                    helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.FROZEN.get()));
                })
                .add(TCItems.ICE_SKATES, helper -> helper.unit(TCItems.ICE$SAFE))
                .add(TCItems.ANGLER_EARRING, helper -> {
                    helper.of(AccessoryItems.FISHING$POWER, 10.0F);
                    helper.of(TCItems.ATTRIBUTES, ImmutableListMultimap.of());
                }, true)
                .add(TCItems.BASE_POINT, helper -> helper.unit(TCItems.ICE$SAFE))
                .add(TCItems.ANKH_CHARM, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, ankh))
                .add(TCItems.ANKH_SHIELD, helper -> {
                    helper.unit(TCItems.FROZEN$IMMUNE);
                    helper.unit(TCItems.FIRE$IMMUNE);
                    helper.of(TCItems.EFFECT$IMMUNITIES, ankh);
                })
                .add(TCItems.EVERLASTING, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, ankh))
                .add(TCItems.AVENGER_EMBLEM, helper -> helper.entry(TCItems.ATTRIBUTES, fourClassesAttribute(helper.asId(), 0.12).build()))
                .add(TCItems.DESTROYER_EMBLEM, helper -> {
                    ResourceLocation id = helper.asId();
                    helper.entry(TCItems.ATTRIBUTES, fourClassesAttribute(id, 0.1)
                            .add(LibAttributes.getCriticalChance(), id, 0.08, PortAttributeModifier.Operation.ADD_VALUE)
                            .build());
                })
                .add(TCItems.PUTRID_SCENT, helper -> {
                    ResourceLocation id = helper.asId();
                    helper.entry(TCItems.ATTRIBUTES, fourClassesAttribute(id, 0.05)
                            .add(ConfluenceMagicLib.AGGRO, id, -400, PortAttributeModifier.Operation.ADD_VALUE)
                            .build());
                })
                .add(TCItems.MOON_STONE, celestial)
                .add(TCItems.SUN_STONE, celestial)
                .add(TCItems.CELESTIAL_STONE, celestial)
                .add(TCItems.CELESTIAL_SHELL, celestial)
                .add(TCItems.HAND_OF_CREATION, helper -> helper.of(AccessoryItems.COIN$PICKUP$RANGE, new Tuple<>(6.25F, 0)))
                .add(TCItems.TREASURE_MAGNET, helper -> helper.of(AccessoryItems.COIN$PICKUP$RANGE, new Tuple<>(6.25F, 0)))
        ;
    }

    private static AttributeModifiersValue.Builder fourClassesAttribute(ResourceLocation id, double amount) {
        return AttributeModifiersValue.builder()
                .add(LibAttributes.getAttackDamage(), id, amount, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .add(LibAttributes.getRangedDamage(), id, amount, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .add(LibAttributes.getMagicDamage(), id, amount, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .add(LibAttributes.getSummonDamage(), id, amount, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    public static class Builder extends PortDataMapProvider.Builder<PrimitiveValueComponent, Item> {
        public Builder() {
            super(TCDataMaps.ACCESSORIES);
        }

        public Builder add(ItemLike item, Consumer<Helper> consumer) {
            return add(item, consumer, false);
        }

        public Builder add(ItemLike item, Consumer<Helper> consumer, boolean replace) {
            add(item.asItem().builtInRegistryHolder().key(), new PrimitiveValueComponent(wrap(item.asItem(), consumer)), replace);
            return this;
        }
    }
}
