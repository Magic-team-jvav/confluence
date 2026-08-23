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
import org.confluence.mod.common.init.item.ModItems;
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
        Consumer<Helper> sentry = helper -> {
            ResourceLocation id = helper.asId();
            helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(ConfluenceMagicLib.SENTRY_CAPACITY, id, 1.0, PortAttributeModifier.Operation.ADD_VALUE)
                    .add(LibAttributes.getSummonDamage(), id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
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
                // 免疫
                .add(AccessoryItems.ADHESIVE_BANDAGE, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.BLEEDING.get())))
                .add(AccessoryItems.MEDICATED_BANDAGE, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.POISON, ModEffects.BLEEDING.get())))
                .add(AccessoryItems.POCKET_MIRROR, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.STONED.get())))
                .add(AccessoryItems.REFLECTIVE_SHADES, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.BLINDNESS, ModEffects.STONED.get())))
                .add(AccessoryItems.ARMOR_POLISH, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.BROKEN_ARMOR.get())))
                .add(AccessoryItems.ARMOR_BRACING, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(MobEffects.WEAKNESS, ModEffects.BROKEN_ARMOR.get())))
                .add(AccessoryItems.MEGAPHONE, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SILENCED.get())))
                .add(AccessoryItems.NAZAR, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.CURSED.get())))
                .add(AccessoryItems.COUNTERCURSE_MANTRA, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SILENCED.get(), ModEffects.CURSED.get())))
                // 魔力
                .add(AccessoryItems.NATURES_GIFT, helper -> helper.of(AccessoryItems.MANA$USE$REDUCE, 0.06F))
                .add(AccessoryItems.MANA_FLOWER, helper -> {
                    helper.unit(AccessoryItems.AUTO$GET$MANA);
                    helper.of(AccessoryItems.MANA$USE$REDUCE, 0.08F);
                })
                .add(AccessoryItems.CELESTIAL_MAGNET, helper -> helper.of(AccessoryItems.MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0)))
                .add(AccessoryItems.CELESTIAL_EMBLEM, helper -> {
                    helper.of(AccessoryItems.MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0));
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getMagicDamage(), helper.asId(), 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                })
                .add(AccessoryItems.MAGNET_FLOWER, helper -> {
                    helper.unit(AccessoryItems.AUTO$GET$MANA);
                    helper.of(AccessoryItems.MANA$USE$REDUCE, 0.08F);
                    helper.of(AccessoryItems.MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0));
                })
                .add(AccessoryItems.ARCANE_FLOWER, helper -> {
                    helper.unit(AccessoryItems.AUTO$GET$MANA);
                    helper.of(AccessoryItems.MANA$USE$REDUCE, 0.08F);
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(ConfluenceMagicLib.AGGRO, helper.asId(), -400, PortAttributeModifier.Operation.ADD_VALUE));
                })
                .add(AccessoryItems.BAND_OF_STARPOWER, helper -> helper.of(AccessoryItems.ADDITIONAL$MANA, 20))
                .add(AccessoryItems.MANA_REGENERATION_BAND, helper -> {
                    helper.unit(AccessoryItems.FAST$MANA$GENERATION);
                    helper.of(AccessoryItems.ADDITIONAL$MANA, 20);
                })
                .add(AccessoryItems.MAGIC_CUFFS, helper -> {
                    helper.unit(AccessoryItems.HURT$GET$MANA);
                    helper.unit(AccessoryItems.FAST$MANA$GENERATION);
                    helper.of(AccessoryItems.ADDITIONAL$MANA, 20);
                })
                .add(AccessoryItems.CELESTIAL_CUFFS, helper -> {
                    helper.unit(AccessoryItems.HURT$GET$MANA);
                    helper.unit(AccessoryItems.FAST$MANA$GENERATION);
                    helper.of(AccessoryItems.ADDITIONAL$MANA, 20);
                    helper.of(AccessoryItems.MANA$PICKUP$RANGE, new Tuple<>(12.5F, 0));
                })
                .add(AccessoryItems.MANA_CLOAK, helper -> {
                    helper.unit(AccessoryItems.AUTO$GET$MANA);
                    helper.of(TCItems.STAR$CLOCK, true);
                    helper.of(AccessoryItems.MANA$USE$REDUCE, 0.08F);
                })
                .add(AccessoryItems.PHILOSOPHERS_STONE, helper -> helper.of(AccessoryItems.REDUCE$HEALING$COOLDOWN, 0.25F))
                .add(AccessoryItems.CHARM_OF_MYTHS, helper -> helper.of(AccessoryItems.REDUCE$HEALING$COOLDOWN, 0.25F))
                // 钓鱼
                .add(AccessoryItems.HIGH_TEST_FISHING_LINE, helper -> helper.unit(AccessoryItems.HIGH$TEST$FISHING$LINE)) // 优质钓鱼线
                .add(AccessoryItems.TACKLE_BOX, helper -> helper.unit(AccessoryItems.TACKLE$BOX)) // 钓具箱
                .add(AccessoryItems.ANGLER_TACKLE_BAG, helper -> {
                    helper.unit(AccessoryItems.HIGH$TEST$FISHING$LINE);
                    helper.unit(AccessoryItems.TACKLE$BOX);
                    helper.of(AccessoryItems.FISHING$POWER, 10.0F);
                }) // 渔夫渔具袋
                .add(AccessoryItems.LAVAPROOF_FISHING_HOOK, helper -> helper.unit(AccessoryItems.LAVAPROOF$FISHING$HOOK)) // 防熔岩钓钩
                .add(AccessoryItems.LAVAPROOF_TACKLE_BAG, helper -> {
                    helper.unit(AccessoryItems.HIGH$TEST$FISHING$LINE);
                    helper.unit(AccessoryItems.TACKLE$BOX);
                    helper.unit(AccessoryItems.LAVAPROOF$FISHING$HOOK);
                    helper.of(AccessoryItems.FISHING$POWER, 10.0F);
                }) // 防熔岩渔具袋
                // 信息
                .add(AccessoryItems.MECHANICAL_LENS, helper -> helper.of(TCItems.INFORMATION, List.of(TCItems.MECHANICAL$LENS))) // 机械晶状体
                // 建筑
                .add(AccessoryItems.PAINT_SPRAYER, helper -> helper.unit(AccessoryItems.PAINT$SPRAYER)) // 喷漆器
                // 钱币
                .add(AccessoryItems.LUCKY_COIN, helper -> {
                    helper.unit(AccessoryItems.LUCKY$COIN);
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.LUCK, helper.asId(), 0.05, PortAttributeModifier.Operation.ADD_VALUE));
                }) // 幸运币
                .add(AccessoryItems.GOLD_RING, helper -> helper.of(AccessoryItems.COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0))) // 金戒指
                .add(AccessoryItems.COIN_RING, helper -> {
                    helper.unit(AccessoryItems.LUCKY$COIN);
                    helper.of(AccessoryItems.COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0));
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.LUCK, helper.asId(), 0.05, PortAttributeModifier.Operation.ADD_VALUE));
                }) // 钱币戒指
                .add(AccessoryItems.DISCOUNT_CARD, helper -> helper.of(AccessoryItems.SPECIAL$PRICE, 1)) // 优惠卡
                .add(AccessoryItems.GREEDY_RING, helper -> {
                    helper.unit(AccessoryItems.LUCKY$COIN);
                    helper.of(AccessoryItems.COIN$PICKUP$RANGE, new Tuple<>(14.67F, 0));
                    helper.of(AccessoryItems.SPECIAL$PRICE, 1);
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.LUCK, helper.asId(), 0.05, PortAttributeModifier.Operation.ADD_VALUE));
                }) // 贪婪戒指
                .add(AccessoryItems.GUIDE_TO_PLANT_FIBER_CORDAGE, helper -> helper.unit(AccessoryItems.VINE$ROPE)) // 植物纤维绳索宝典
                .add(AccessoryItems.SPECTRE_GOGGLES, helper -> helper.unit(AccessoryItems.SPECTRE$GOGGLES)) // 幽灵护目镜
                .add(AccessoryItems.CHROMATIC_CLOAK, helper -> helper.of(TCItems.EFFECT$IMMUNITIES, Set.of(ModEffects.SHIMMER.get()))) // 炫彩斗篷
                .add(AccessoryItems.STRESS_BALL, helper -> helper.unit(AccessoryItems.$AFK)) // 压力球
                // 召唤
                .add(AccessoryItems.SUMMONER_EMBLEM, helper -> helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getSummonDamage(), helper.asId(), 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))) // 召唤师徽章
                .add(AccessoryItems.APPRENTICES_SCARF, sentry) // 学徒围巾
                .add(AccessoryItems.SQUIRES_SHIELD, sentry) // 侍卫护盾
                .add(AccessoryItems.HUNTRESSS_BUCKLER, sentry) // 女猎人圆盾
                .add(AccessoryItems.MONKS_BELT, sentry) // 武僧腰带
                .add(AccessoryItems.HERCULES_BEETLE, helper -> {
                    ResourceLocation id = helper.asId();
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                            .add(LibAttributes.getSummonDamage(), id, 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                            .add(ConfluenceMagicLib.SUMMON_KNOCKBACK, id, 2.0, PortAttributeModifier.Operation.ADD_VALUE)
                            .build());
                }) // 大力士甲虫
                .add(AccessoryItems.NECROMANTIC_SCROLL, helper -> {
                    ResourceLocation id = helper.asId();
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                            .add(ConfluenceMagicLib.MINION_CAPACITY, id, 1.0, PortAttributeModifier.Operation.ADD_VALUE)
                            .add(LibAttributes.getSummonDamage(), id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                            .build());
                }) // 死灵卷轴
                .add(AccessoryItems.PAPYRUS_SCARAB, helper -> {
                    ResourceLocation id = helper.asId();
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                            .add(ConfluenceMagicLib.MINION_CAPACITY, id, 1.0, PortAttributeModifier.Operation.ADD_VALUE)
                            .add(LibAttributes.getSummonDamage(), id, 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                            .add(ConfluenceMagicLib.SUMMON_KNOCKBACK, id, 2.0, PortAttributeModifier.Operation.ADD_VALUE)
                            .build());
                }) // 甲虫莎草纸
                .add(AccessoryItems.PYGMY_NECKLACE, helper -> helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(ConfluenceMagicLib.MINION_CAPACITY, helper.asId(), 1.0, PortAttributeModifier.Operation.ADD_VALUE))) // 矮人项链
                // 其他
                .add(AccessoryItems.CLOTHIER_VOODOO_DOLL, helper -> helper.unit(AccessoryItems.CLOTHIER$KILLER)) // 服装商巫毒娃娃
                .add(ModItems.PARADOX_INTERACTIVE_MEDAL, helper -> {
                    ResourceLocation id = helper.asId();
                    helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                            .add(LibAttributes.getAttackDamage(), id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                            .add(PortAttributesExtension.entityInteractionRange(), id, 3, PortAttributeModifier.Operation.ADD_VALUE)
                            .add(ConfluenceMagicLib.MINION_CAPACITY, id, 2, PortAttributeModifier.Operation.ADD_VALUE)
                            .add(Attributes.MAX_HEALTH, id, 10, PortAttributeModifier.Operation.ADD_VALUE)
                            .add(Attributes.ARMOR, id, 6, PortAttributeModifier.Operation.ADD_VALUE)
                            .build());
                }) // 帕拉多克斯·英特拉克缇福勋章
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
