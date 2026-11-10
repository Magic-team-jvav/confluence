package org.confluence.mod.common.init.armor;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.MobEffectInstanceData;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.GetArmorSetBonusDataEvent;
import org.confluence.mod.api.event.RegisterArmorSetBonusEvent;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.entity.projectile.FlowerPetalProjectile;
import org.confluence.mod.common.entity.projectile.TitaniumShardsProjectile;
import org.confluence.mod.common.event.game.GameEvents;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.armor.type.EnhanceEffectDuration;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.util.SoulcererUtils;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.terra_curio.api.primitive.*;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.wrapper.common.PortTags;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.confluence.mod.common.init.item.ArmorItems.*;

public final class ModArmorBonus {
    private static final Object2ObjectMap<ArmorSetBonusKey, ArmorSetBonusData> VALUE_MAP = new Object2ObjectOpenHashMap<>();

    // region preset bonus
    public static final ArmorSetBonusData WIZARD_HAT_SET_BONUS = new ArmorSetBonusData(PrimitiveValueComponent.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(
            LibAttributes.getCriticalChance(),
            Confluence.asResource("wizard_hat_set_bonus"),
            0.1,
            PortAttributeModifier.Operation.ADD_VALUE
    )), 1);
    public static final ArmorSetBonusData MAGIC_HAT_SET_BONUS = new ArmorSetBonusData(PrimitiveValueComponent.of(AccessoryItems.ADDITIONAL$MANA, 60), 1);
    // endregion

    // region type
    public static final CombineRule<Integer, IntegerValue> AS_ENCHANTMENT_INT_CR = CombineRule.register((a, b) -> {
        if (a.equals(b)) {
            return a == 0 ? 0 : a + 1;
        }
        return Math.max(a, b);
    }, Confluence.asResource("as_enchantment"));

    public static final ValueType.UnitType CACTUS$THORNS = ValueType.UnitType.of(Confluence.asResource("thorns"));
    public static final ValueType.FloatType SKIP$CONSUME$AMMO$CHANCE = ValueType.FloatType.of(Confluence.asResource("skip_consume_ammo_chance"), FloatValue.ADDITION_WITHIN_0_TO_1, 0);
    /// 收魂时额外获得的灵魂，逐件与套装共用同一个值类型。
    public static final ValueType.FloatType SOUL$DROP = ValueType.FloatType.of(Confluence.asResource("soul_drop"), FloatValue.ADDITION, 0);
    public static final ValueType.UnitType SPACE$GUN$FREE = ValueType.UnitType.of(Confluence.asResource("space_gun_free"));
    public static final ValueType.FloatType HEAL$AMOUNT$MULTIPLIER = ValueType.FloatType.of(Confluence.asResource("heal_amount_multiplier"), FloatValue.ADDITION_WITHIN_0_TO_1, 0);
    public static final ValueType<List<MobEffectInstanceData>, MobEffectInstancesValue> HURT$ENEMY$AWARD$EFFECTS = ValueType.create(Confluence.asResource("hurt_enemy_award_effects"), MobEffectInstancesValue.MERGE, MobEffectInstancesValue.CODEC, List.of(), MobEffectInstancesValue::new);
    public static final ValueType.UnitType FLOWER$PETAL = ValueType.UnitType.of(Confluence.asResource("flower_petal"));
    public static final ValueType.UnitType TITANIUM$SHARDS = ValueType.UnitType.of(Confluence.asResource("titanium_shards"));
    public static final ValueType.UnitType LAVA$IMMUNE = ValueType.UnitType.of(Confluence.asResource("lava_immune"));
    public static final ValueType.IntegerType DURABILITY$REPAIR$AMOUNT$PER$SECOND$IN$LAVA = ValueType.IntegerType.of(Confluence.asResource("durability_repair_amount_per_second_in_lava"), IntegerValue.GET_MAX, 0);
    public static final ValueType.IntegerType FORTUNE = ValueType.IntegerType.of(Confluence.asResource("fortune"), AS_ENCHANTMENT_INT_CR, 0);
    public static final ValueType<Object2IntMap<MobEffect>, EnhanceEffectDuration> ENHANCE$EFFECT$DURATION = ValueType.create(Confluence.asResource("enhance_effect_duration"), EnhanceEffectDuration.MERGE, EnhanceEffectDuration.CODEC, Object2IntMaps.emptyMap(), EnhanceEffectDuration::new);
    /// 灵魂收集概率加成（0.20 = 收魂时 20% 概率额外收 1 魂）
    public static final ValueType.FloatType SOUL$COLLECT$CHANCE = ValueType.FloatType.of(Confluence.asResource("soul_collect_chance"), FloatValue.ADDITION_WITHIN_0_TO_1, 0);
    public static final ValueType.FloatType BENEFICIAL$EFFECT$DURATION = ValueType.FloatType.of(Confluence.asResource("beneficial_effect_duration"), FloatValue.ADDITION_WITHIN_0_TO_1, 0);
    // endregion

    // region key
    public static ArmorSetBonusKey WHITE_PUMPKIN_SET;
    public static ArmorSetBonusKey SEEKER_SET;
    public static ArmorSetBonusKey SOULDANCER_ROBE_SET;
    public static ArmorSetBonusKey PHANTOM_SET;
    public static ArmorSetBonusKey BLUE_HERMIT_SET;
    public static ArmorSetBonusKey NINJA_SET;
    public static ArmorSetBonusKey COLD_CRYSTAL_SET;
    public static ArmorSetBonusKey HEIM_SET;
    public static ArmorSetBonusKey EMBER_ROBE_SET;
    public static ArmorSetBonusKey HIGHLAND_SET;
    public static ArmorSetBonusKey REINFORCED_MAIL_SET;
    public static ArmorSetBonusKey CLIMBING_SET;
    public static ArmorSetBonusKey VERDANT_ROBE_SET;
    public static ArmorSetBonusKey BLACK_SPOT_SET;
    public static ArmorSetBonusKey ROOT_ROT_SET;
    public static ArmorSetBonusKey WOLF_SET;
    private static final String WOLF_BUFF_END = "confluence.wolf_buff_end";
    private static final ResourceLocation WOLF_ATTACK_ID = Confluence.asResource("wolf_pack_attack");
    private static final ResourceLocation WOLF_SPEED_ID = Confluence.asResource("wolf_pack_speed");

    /// 起跳时刻（世界时间）与「跳跃后 3 秒」的窗口刻数。
    public static final String LAST_JUMP_TAG = "confluence.armor_last_jump";
    private static final int JUMP_WINDOW_TICKS = 60;
    private static final ResourceLocation HIGHLAND_JUMP_SPEED_ID = Confluence.asResource("highland_jump_speed");
    private static final ResourceLocation HEAVY_JUMP_SLOW_ID = Confluence.asResource("reinforced_mail_jump_slow");
    // endregion

    @SuppressWarnings("all")
    public static void registerArmorSetBonus() {
        register("mining_set", 1, MINING_HELMET, MINING_CHESTPLATE, MINING_LEGGINGS, MINING_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.BLOCK_BREAK_SPEED, key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });
        register("plank_set", 1, PLANK_HELMET, PLANK_CHESTPLATE, PLANK_LEGGINGS, PLANK_BOOTS, armor(1));
        register("ash_set", 1, ASH_HELMET, ASH_CHESTPLATE, ASH_LEGGINGS, ASH_BOOTS, key -> {
            key.of(TCItems.LAVA$HURT$REDUCE, 0.5F);
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.BURNING_TIME, key.id, -0.35, PortAttributeModifier.Operation.ADD_VALUE));
        });
        register("snow_set", 1, SNOW_CAPS, SNOW_SUITS, INSULATED_PANTS, INSULATED_SHOES, key -> {
            key.unit(TCItems.FROZEN$IMMUNE);
        });
        register("pink_snow_set", 1, PINK_SNOW_CAPS, PINK_SNOW_SUITS, PINK_INSULATED_PANTS, PINK_INSULATED_SHOES, key -> {
            key.unit(TCItems.FROZEN$IMMUNE);
        });
        register("angler_set", 1, ANGLER_HAT, ANGLER_VEST, ANGLER_PANTS, Items.AIR, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER, key.id, -0.23, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER, key.id, -0.30, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        register("cactus_set", 1, CACTUS_HELMET, CACTUS_CHESTPLATE, CACTUS_LEGGINGS, CACTUS_BOOTS, key -> {
            key.unit(CACTUS$THORNS);
        });
        register("copper_set", 1, COPPER_HELMET, COPPER_CHESTPLATE, COPPER_LEGGINGS, COPPER_BOOTS, armor(1));
        register("tin_set", 1, TIN_HELMET, TIN_CHESTPLATE, TIN_LEGGINGS, TIN_BOOTS, armor(2));
        register("pumpkin_set", 1, PUMPKIN_HELMET, PUMPKIN_CHESTPLATE, PUMPKIN_LEGGINGS, PUMPKIN_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(LibAttributes.getAttackDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getRangedDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getMagicDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getSummonDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        // 白南瓜套装：近战暴击回魂由 LivingEntityEvents.processCriticalDamage 结算，这里只保留套装识别与说明行
        WHITE_PUMPKIN_SET = register("white_pumpkin_set", 1, WHITE_PUMPKIN_HELMET, WHITE_PUMPKIN_CHESTPLATE, WHITE_PUMPKIN_LEGGINGS, WHITE_PUMPKIN_BOOTS, key -> {
        });
        // 魂师套装：灵魂上限直接加在 ADDITIONAL_SOUL 属性上，SoulStorage 会实时读取
        register("hermit_set", 1, HERMIT_HELMET, HERMIT_CHESTPLATE, HERMIT_LEGGINGS, HERMIT_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(ConfluenceMagicLib.ADDITIONAL_SOUL, key.id, 20, PortAttributeModifier.Operation.ADD_VALUE));
        });
        SEEKER_SET = register("seeker_set", 2, SEEKER_HELLMET, SEEKER_CHESTPLATE, SEEKER_LEGGINGS, SEEKER_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(ConfluenceMagicLib.ADDITIONAL_SOUL, key.id, 40, PortAttributeModifier.Operation.ADD_VALUE));
        });
        SOULDANCER_ROBE_SET = register("souldancer_robe_set", 2, SOULDANCER_HOOD, SOULDANCER_ROBE, Items.AIR, Items.AIR, key -> {
            // 收集灵魂后的限时增益由 PlayerUtils.tickSoulArmorSets 结算，这里只保留套装识别与说明行
        });
        PHANTOM_SET = register("phantom_set", 1, PHANTOM_HELMET, PHANTOM_CHESTPLATE, PHANTOM_LEGGINGS, PHANTOM_BOOTS, key -> {
            key.of(SOUL$DROP, 1.0F); // 夜间再 +2 由 SoulCollectionEvent 监听补足
        });
        // 蓝隐匿者的耐药性削减由 PlayerUtils.onSoulSpent 结算，这里只保留套装识别与说明行
        BLUE_HERMIT_SET = register("blue_hermit_set", 1, BLUE_HERMIT_HELMET, BLUE_HERMIT_CHESTPLATE, BLUE_HERMIT_LEGGINGS, BLUE_HERMIT_BOOTS, key -> {
        });
        NINJA_SET = register("ninja_set", 1, NINJA_HELMET, NINJA_CHESTPLATE, NINJA_LEGGINGS, NINJA_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.MOVEMENT_SPEED, key.id, 0.2, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            // 移动拖影由 AfterimageHelper 在客户端渲染。
        });
        register("guards_set", 1, GUARDS_HELMET, GUARDS_CHESTPLATE, GUARDS_LEGGINGS, GUARDS_BOOTS, key -> {
            key.of(SKIP$CONSUME$AMMO$CHANCE, 0.1F);
        });
        register("spelunker_set", 2, SPELUNKER_HELMET, SPELUNKER_CHESTPLATE, SPELUNKER_LEGGINGS, SPELUNKER_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(ConfluenceMagicLib.MINION_CAPACITY, key.id, 1, PortAttributeModifier.Operation.ADD_VALUE));
            // todo 蜡烛粒子
            key.of(ENHANCE$EFFECT$DURATION, Object2IntMaps.singleton(ModEffects.SPELUNKER.get(), 2400));
        });
        register("splendid_robe_set", 1, SPLENDID_COLLAR, SPLENDID_ROBE, SPLENDID_LEGGINGS, SPLENDID_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.MOVEMENT_SPEED, key.id, 0.7, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });
        register("lead_set", 1, LEAD_HELMET, LEAD_CHESTPLATE, LEAD_LEGGINGS, LEAD_BOOTS, armor(1));
        register("silver_set", 1, SILVER_HELMET, SILVER_CHESTPLATE, SILVER_LEGGINGS, SILVER_BOOTS, armor(2));
        register("tungsten_set", 1, TUNGSTEN_HELMET, TUNGSTEN_CHESTPLATE, TUNGSTEN_LEGGINGS, TUNGSTEN_BOOTS, armor(1));
        register("golden_set", 1, GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS, armor(1));
        register("platinum_set", 1, PLATINUM_HELMET, PLATINUM_CHESTPLATE, PLATINUM_LEGGINGS, PLATINUM_BOOTS, armor(2));
        register("fossil_set", 1, FOSSIL_HELMET, FOSSIL_CHESTPLATE, FOSSIL_LEGGINGS, FOSSIL_BOOTS, key -> {
            key.of(SKIP$CONSUME$AMMO$CHANCE, 0.2F);
        });
        COLD_CRYSTAL_SET = register("cold_crystal_set", 1, COLD_CRYSTAL_HELMET, COLD_CRYSTAL_CHESTPLATE, COLD_CRYSTAL_LEGGINGS, COLD_CRYSTAL_BOOTS, key -> {});
        HEIM_SET = register("heim_set", 1, HEIM_HELMET, HEIM_CHESTPLATE, HEIM_LEGGINGS, HEIM_BOOTS, key -> {});
        register("spore_root_set", 1, SPORE_ROOT_HELMET, SPORE_ROOT_CHESTPLATE, SPORE_ROOT_LEGGINGS, SPORE_ROOT_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(ConfluenceMagicLib.MINION_CAPACITY, key.id, 1, PortAttributeModifier.Operation.ADD_VALUE));
        });
        register("bee_set", 1, BEE_HELMET, BEE_CHESTPLATE, BEE_LEGGINGS, BEE_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getSummonDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });
        register("obsidian_set", 2, OBSIDIAN_HELMET, OBSIDIAN_CHESTPLATE, OBSIDIAN_LEGGINGS, OBSIDIAN_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(ConfluenceMagicLib.WHIP_RANGE, key.id, 0.3, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(Attributes.ATTACK_SPEED, key.id, 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getSummonDamage(), key.id, 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        register("gladiator_set", 1, GLADIATOR_HELMET, GLADIATOR_CHESTPLATE, GLADIATOR_LEGGINGS, GLADIATOR_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(Attributes.KNOCKBACK_RESISTANCE, key.id, 1, PortAttributeModifier.Operation.ADD_VALUE)
                    .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, key.id, 1, PortAttributeModifier.Operation.ADD_VALUE)
                    .build());
        });
        register("meteor_set", 1, METEOR_HELMET, METEOR_CHESTPLATE, METEOR_LEGGINGS, METEOR_BOOTS, key -> {
            key.unit(SPACE$GUN$FREE);
            // todo 移动时有火焰微粒（类似于火箭靴，但不发光）
        });
        register("jungle_set", 1, JUNGLE_HELMET, JUNGLE_CHESTPLATE, JUNGLE_LEGGINGS, JUNGLE_BOOTS, key -> {
            key.of(AccessoryItems.MANA$USE$REDUCE, 0.16F);
            // todo 移动时草粒飞舞
        });
        register("necro_set", 1, NECRO_HELMET, NECRO_CHESTPLATE, NECRO_LEGGINGS, NECRO_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getCriticalChance(), key.id, 0.1, PortAttributeModifier.Operation.ADD_VALUE));
            // todo 移动时身后有拖影，受伤时发出骨头碎裂的声音
        });
        register("shadow_set", 1, SHADOW_HELMET, SHADOW_CHESTPLATE, SHADOW_LEGGINGS, SHADOW_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.MOVEMENT_SPEED, key.id, 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            // todo 加速度增加 75%；移动时身后有拖影，并有火箭靴那样的火焰，但是紫色的。
        });
        register("crimson_set", 1, CRIMSON_HELMET, CRIMSON_CHESTPLATE, CRIMSON_LEGGINGS, CRIMSON_BOOTS, key -> {
            key.of(HEAL$AMOUNT$MULTIPLIER, 0.5F);
            // todo 在移动时与再生生命时会发出红色微粒
        });
        register("molten_set", 2, MOLTEN_HELMET, MOLTEN_CHESTPLATE, MOLTEN_LEGGINGS, MOLTEN_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getAttackDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            key.unit(TCItems.FIRE$IMMUNE);
            // todo 移动时有火焰微粒
        });
        register("pearlwood_set", 1, PEARL_HELMET, PEARL_CHESTPLATE, PEARL_LEGGINGS, PEARL_BOOTS, armor(1));
        register("spider_set", 1, SPIDER_HELMET, SPIDER_CHESTPLATE, SPIDER_LEGGINGS, SPIDER_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getSummonDamage(), key.id, 0.12, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });
        register("cobalt_helmet_set", 1, COBALT_HELMET, COBALT_CHESTPLATE, COBALT_LEGGINGS, COBALT_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.ATTACK_SPEED, key.id, 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            // todo 移动时产生残影效果
        });
        register("cobalt_mask_set", 1, COBALT_MASK, COBALT_CHESTPLATE, COBALT_LEGGINGS, COBALT_BOOTS, key -> {
            key.of(SKIP$CONSUME$AMMO$CHANCE, 0.2F);
            // todo 移动时产生残影效果
        });
        register("cobalt_hat_set", 1, COBALT_HAT, COBALT_CHESTPLATE, COBALT_LEGGINGS, COBALT_BOOTS, key -> {
            key.of(AccessoryItems.MANA$USE$REDUCE, 0.14F);
            // todo 移动时产生残影效果
        });
        Consumer<ArmorSetBonusKey> palladiumSet = key -> {
            key.of(HURT$ENEMY$AWARD$EFFECTS, List.of(new MobEffectInstanceData(MobEffects.REGENERATION, 100, 1)));
        };
        register("palladium_mask_set", 1, PALLADIUM_MASK, PALLADIUM_CHESTPLATE, PALLADIUM_LEGGINGS, PALLADIUM_BOOTS, palladiumSet);
        register("palladium_helmet_set", 1, PALLADIUM_HELMET, PALLADIUM_CHESTPLATE, PALLADIUM_LEGGINGS, PALLADIUM_BOOTS, palladiumSet);
        register("palladium_headgear_set", 1, PALLADIUM_HEADGEAR, PALLADIUM_CHESTPLATE, PALLADIUM_LEGGINGS, PALLADIUM_BOOTS, palladiumSet);
        register("mythril_hood_set", 1, MYTHRIL_HOOD, MYTHRIL_CHESTPLATE, MYTHRIL_LEGGINGS, MYTHRIL_BOOTS, key -> {
            key.of(AccessoryItems.MANA$USE$REDUCE, 0.17F);
        });
        register("mythril_helmet_set", 1, MYTHRIL_HELMET, MYTHRIL_CHESTPLATE, MYTHRIL_LEGGINGS, MYTHRIL_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getCriticalChance().value(), key.id, 0.1, PortAttributeModifier.Operation.ADD_VALUE));
        });
        register("mythril_hat_set", 1, MYTHRIL_HAT, MYTHRIL_CHESTPLATE, MYTHRIL_LEGGINGS, MYTHRIL_BOOTS, key -> {
            key.of(SKIP$CONSUME$AMMO$CHANCE, 0.2F);
        });
        Consumer<ArmorSetBonusKey> orichalcumSet = key -> {
            key.unit(FLOWER$PETAL);
        };
        register("orichalcum_headgear_set", 1, ORICHALCUM_HEADGEAR, ORICHALCUM_CHESTPLATE, ORICHALCUM_LEGGINGS, ORICHALCUM_BOOTS, orichalcumSet);
        register("orichalcum_mask_set", 1, ORICHALCUM_MASK, ORICHALCUM_CHESTPLATE, ORICHALCUM_LEGGINGS, ORICHALCUM_BOOTS, orichalcumSet);
        register("orichalcum_helmet_set", 1, ORICHALCUM_HELMET, ORICHALCUM_CHESTPLATE, ORICHALCUM_LEGGINGS, ORICHALCUM_BOOTS, orichalcumSet);
        register("adamantite_headgear_set", 1, ADAMANTITE_HEADGEAR, ADAMANTITE_CHESTPLATE, ADAMANTITE_LEGGINGS, ADAMANTITE_BOOTS, key -> {
            key.of(AccessoryItems.MANA$USE$REDUCE, 0.19F);
            // todo 玩家身周发出微弱的脉动光环。
        });
        register("adamantite_helmet_set", 1, ADAMANTITE_HELMET, ADAMANTITE_CHESTPLATE, ADAMANTITE_LEGGINGS, ADAMANTITE_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(Attributes.ATTACK_SPEED, key.id, 0.2, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(Attributes.MOVEMENT_SPEED, key.id, 0.2, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .build());
            // todo 玩家身周发出微弱的脉动光环。
        });
        register("adamantite_mask_set", 1, ADAMANTITE_MASK, ADAMANTITE_CHESTPLATE, ADAMANTITE_LEGGINGS, ADAMANTITE_BOOTS, key -> {
            key.of(SKIP$CONSUME$AMMO$CHANCE, 0.25F);
            // todo 玩家身周发出微弱的脉动光环。
        });
        Consumer<ArmorSetBonusKey> titaniumSet = key -> {
            key.unit(TITANIUM$SHARDS);
        };
        register("titanium_mask_set", 1, TITANIUM_MASK, TITANIUM_CHESTPLATE, TITANIUM_LEGGINGS, TITANIUM_BOOTS, titaniumSet);
        register("titanium_helmet_set", 1, TITANIUM_HELMET, TITANIUM_CHESTPLATE, TITANIUM_LEGGINGS, TITANIUM_BOOTS, titaniumSet);
        register("titanium_headgear_set", 1, TITANIUM_HEADGEAR, TITANIUM_CHESTPLATE, TITANIUM_LEGGINGS, TITANIUM_BOOTS, titaniumSet);

        // todo	水晶刺客盔甲、神圣盔甲

        register("diamond_set", 1, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, key -> {
            key.of(FORTUNE, 2);
        });
        register("netherite_set", 4, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, key -> {
            key.unit(LAVA$IMMUNE);
            key.of(DURABILITY$REPAIR$AMOUNT$PER$SECOND$IN$LAVA, 50);
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(LibAttributes.getAttackDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getRangedDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getMagicDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getSummonDamage(), key.id, 0.1, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(Attributes.MOVEMENT_SPEED, key.id, 0.05, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        register("tiki_set", 2, TIKI_MASK, TIKI_SHIRT, TIKI_LEGGINGS, TIKI_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(ConfluenceMagicLib.MINION_CAPACITY, key.id, 1, PortAttributeModifier.Operation.ADD_VALUE)
                    .add(ConfluenceMagicLib.WHIP_RANGE, key.id, 0.2, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        register("hunters", 1, HUNERS_HELMET, HUNERS_CHESTPLATE, HUNERS_LEGGINGS, HUNERS_BOOTS, key -> {
            key.of(HURT$ENEMY$AWARD$EFFECTS, List.of(new MobEffectInstanceData(ModEffects.HUNTER, 100)));
        });
        register("archers_set", 1, ARCHERS_HELMET, ARCHERS_CHESTPLATE, ARCHERS_LEGGINGS, ARCHERS_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getRangedVelocity(), key.id, 0.05,
PortAttributeModifier.Operation
.ADD_MULTIPLIED_TOTAL));
        });
        register("mercenary_set", 2, MERCENARY_HELMET, MERCENARY_CHESTPLATE, MERCENARY_LEGGINGS, MERCENARY_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(Attributes.MOVEMENT_SPEED, key.id, -0.30,
PortAttributeModifier.Operation
.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getAttackDamage(), key.id, 0.20,
PortAttributeModifier.Operation
.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        register("renegade_set", 3, RENEGADE_HELMET, RENEGADE_CHESTPLATE, RENEGADE_LEGGINGS, RENEGADE_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(Attributes.MOVEMENT_SPEED, key.id, -0.15,
PortAttributeModifier.Operation
.ADD_MULTIPLIED_TOTAL)
                    .add(LibAttributes.getAttackDamage(), key.id, 0.20,
PortAttributeModifier.Operation
.ADD_MULTIPLIED_TOTAL)
                    .add(Attributes.ATTACK_SPEED, key.id, 0.30,
PortAttributeModifier.Operation
.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        REINFORCED_MAIL_SET = register("reinforced_mail_set", 3, REINFORCED_MAIL_HELMET, REINFORCED_MAIL_CHESTPLATE, REINFORCED_MAIL_LEGGINGS, REINFORCED_MAIL_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getDodgeChance(), key.id, 0.05,
PortAttributeModifier.Operation
.ADD_VALUE));
            key.of(TCItems.INJURY$FREE, 0.07F);
        });
        CLIMBING_SET = register("climbing_set", 3, CLIMBING_HELMET, CLIMBING_CHESTPLATE, CLIMBING_LEGGINGS, CLIMBING_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(Attributes.KNOCKBACK_RESISTANCE, key.id, 0.75,
PortAttributeModifier.Operation
.ADD_VALUE)
                    .add(Attributes.STEP_HEIGHT, key.id, 0.5,
PortAttributeModifier.Operation
.ADD_VALUE)
                    .build());
        });
        WOLF_SET = register("wolf_set", 2, WOLF_HELMET, WOLF_CHESTPLATE, WOLF_LEGGINGS, WOLF_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getAttackDamage(), key.id, 0.11,
PortAttributeModifier.Operation
.ADD_MULTIPLIED_TOTAL));
        });
        EMBER_ROBE_SET = register("ember_robe_set", 1, EMBER_HAT, EMBER_ROBE, EMBER_LEGGINGS, EMBER_BOOTS, key -> {
            /// 常驻狱火由 ModArmorBonus#afterTick 刷新，这里只保留套装识别与说明行
        });
        register("entertainers_garb_set", 1, ENTERTAINERS_GARB_HAT, ENTERTAINERS_GARB_SHIRT, ENTERTAINERS_GARB_PANTS, ENTERTAINERS_GARB_SHOES, key -> {
            key.of(BENEFICIAL$EFFECT$DURATION, 0.30F);
        });
        HIGHLAND_SET = register("highland_set", 2, HIGHLAND_HELMET, HIGHLAND_CHESTPLATE, HIGHLAND_LEGGINGS, HIGHLAND_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.JUMP_STRENGTH, key.id, 0.2, PortAttributeModifier.Operation.ADD_VALUE));
        });
        VERDANT_ROBE_SET = register("verdant_robe_set", 2, VERDANT_HAT, VERDANT_ROBE, VERDANT_LEGGINGS, VERDANT_BOOTS, key -> {
            key.of(SOUL$COLLECT$CHANCE, 0.20F);
        });
        BLACK_SPOT_SET = register("black_spot_set", 4, BLACK_SPOT_HELMET, BLACK_SPOT_CHESTPLATE, BLACK_SPOT_LEGGINGS, BLACK_SPOT_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(LibAttributes.getArtifactCooldownReduction(), key.id, 0.10, PortAttributeModifier.Operation.ADD_VALUE));
        });
        ROOT_ROT_SET = register("root_rot_set", 2, ROOT_ROT_HELMET, ROOT_ROT_CHESTPLATE, ROOT_ROT_LEGGINGS, ROOT_ROT_BOOTS, key -> {
        });

        /// todo 巫师套装
        /// @see GameEvents#getArmorSetBonus(GetArmorSetBonusDataEvent)

        PortEventHandler.postEvent(new RegisterArmorSetBonusEvent(ModArmorBonus::register));
    }

    /// 按条件维护一条瞬态属性修饰符（条件失效自动摘除）。
    private static void applyTransient(ServerPlayer player, Attribute attribute, ResourceLocation id, double value, PortAttributeModifier.Operation operation, boolean active) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        UUID uuid = PortAttributeModifier.rl2uuid(id);
        if (active) {
            if (instance.getModifier(uuid) == null) {
                instance.addTransientModifier(new AttributeModifier(uuid, id.toString(), value, operation.unwrap()));
            }
        } else if (instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }

    /// 使用治疗药水时：腐根 / 黑斑全套重置法器冷却，黑斑另给周围玩家和自己回饱和。
    /// @param healAmount 本次喝下的治疗药水的治疗量
    public static void onHealingPotionUsed(ServerPlayer player, int healAmount) {
        if (isArmorSet(player, WOLF_SET)) {
            player.getPersistentData().putLong(WOLF_BUFF_END, player.level().getGameTime() + 600);
        }
        boolean blackSpot = isArmorSet(player, BLACK_SPOT_SET);
        if (!blackSpot && !isArmorSet(player, ROOT_ROT_SET)) {
            return;
        }
        SoulcererUtils.resetSoulArtifactCooldowns(player);
        /// 队友回复 20% 药水治疗量，且不会产生耐药性（队友没有喝药，自然不会获得）
        float allyHeal = healAmount * 0.2F;
        if (allyHeal > 0.0F) {
            for (Player other : player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(16.0), p -> p != player)) {
                other.heal(allyHeal);
            }
        }
        if (blackSpot) {
            player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() + 4.0F);
            for (Player other : player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(16.0), p -> p != player)) {
                other.getFoodData().setSaturation(other.getFoodData().getSaturationLevel() + 4.0F);
            }
        }
    }

    private static Consumer<ArmorSetBonusKey> armor(double value) {
        return key -> key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.ARMOR, key.id, value, PortAttributeModifier.Operation.ADD_VALUE));
    }

    private static ArmorSetBonusKey register(
            String path,
            int tooltipCount,
            ItemLike head,
            ItemLike chest,
            ItemLike legs,
            ItemLike feet,
            Consumer<ArmorSetBonusKey> consumer
    ) {
        return register(Confluence.asResource(path), tooltipCount, head, chest, legs, feet, consumer);
    }

    private static ArmorSetBonusKey register(
            ResourceLocation id,
            int tooltipCount,
            ItemLike head,
            ItemLike chest,
            ItemLike legs,
            ItemLike feet,
            Consumer<ArmorSetBonusKey> consumer
    ) {
        ArmorSetBonusKey key = new ArmorSetBonusKey(head, chest, legs, feet, ArmorSetBonusKey.mixHash(head, chest, legs, feet, true));
        if (ArmorSetBonusKey.MAP.put(id, key) != null) {
            throw new IllegalArgumentException("Duplicated ArmorBonusKey with id '" + id + "'");
        }
        key.types = new HashMap<>();
        key.id = id;
        consumer.accept(key);
        VALUE_MAP.put(key, new ArmorSetBonusData(new PrimitiveValueComponent(key.types), tooltipCount));
        key.types = null;
        return key;
    }

    public static boolean isArmorSet(Player player, ResourceLocation id) {
        return isArmorSet(player, ArmorSetBonusKey.byId(id));
    }

    public static boolean isArmorSet(Player player, ArmorSetBonusKey key) {
        return PlayerSpecialData.of(player).getArmorSetBonusKey().equals(key);
    }

    /// 仅使用套装奖励注册的type
    ///
    /// 如果type是配饰用的就使用[org.confluence.terra_curio.util.TCUtils#hasType(LivingEntity,ValueType)]
    public static <T, V extends PrimitiveValue<T>> boolean hasType(Player player, ValueType<T, V> type) {
        return PlayerSpecialData.of(player).contains(type);
    }

    /// 仅使用套装奖励注册的type
    ///
    /// 如果type是配饰用的就使用[org.confluence.terra_curio.util.TCUtils#getValue(LivingEntity,ValueType)]
    public static <T, V extends PrimitiveValue<T>> T getValue(Player player, ValueType<T, V> type) {
        return PlayerSpecialData.of(player).getValue(type);
    }

    /// 仅使用套装奖励注册的type
    ///
    /// 如果type是配饰用的就使用[org.confluence.terra_curio.util.TCUtils#getPrimitiveValue(LivingEntity,ValueType)]
    public static <T, V extends PrimitiveValue<T>> @Nullable V getPrimitiveValue(Player player, ValueType<T, V> type) {
        return PlayerSpecialData.of(player).getPrimitiveValue(type);
    }

    public static @Nullable PrimitiveValueComponent getArmorStackBonus(ItemStack itemStack) {
        return itemStack.get(ModDataComponentTypes.ARMOR_BONUS);
    }

    public static @Nullable ArmorSetBonusData getArmorSetBonusData(Player player, ArmorSetBonusKey key) {
        if (key == ArmorSetBonusKey.NONE) return null;
        return PortEventHandler.postEventWithReturn(new GetArmorSetBonusDataEvent(player, key, VALUE_MAP.get(key))).getNeoData();
    }

    public static Object2ObjectMap<ArmorSetBonusKey, ArmorSetBonusData> getValueMap() {
        return VALUE_MAP;
    }

    public static void addTooltip(@Nullable Player player, ItemStack itemStack, List<Component> toolTip) {
        if (player == null) return;
        ArmorSetBonusKey key = PlayerSpecialData.of(player).getArmorSetBonusKey();
        if (key == ArmorSetBonusKey.NONE) return;
        if (itemStack.getItem() instanceof ArmorItem armorItem && switch (armorItem.getEquipmentSlot()) {
            case FEET -> key.feet() == armorItem;
            case LEGS -> key.legs() == armorItem;
            case CHEST -> key.chest() == armorItem;
            case HEAD -> key.head() == armorItem;
            default -> false;
        }) {
            ArmorSetBonusData data = getArmorSetBonusData(player, key);
            if (data == null || data.tooltipCount() == 0) return;
            String descriptionKey = key.getDescriptionKey();
            toolTip.add(Component.translatable("armor_set_bonus.when_applied").withStyle(ChatFormatting.GRAY));
            for (int i = 0; i < data.tooltipCount(); i++) {
                toolTip.add(Component.translatable("armor_set_bonus." + descriptionKey + "." + i).withStyle(ChatFormatting.DARK_AQUA));
            }
        }
    }

    public static float applyHealAmount(Player player, float amount) {
        return amount * (1 + getValue(player, HEAL$AMOUNT$MULTIPLIER));
    }

    public static void beAttacked(ServerPlayer player, DamageSource damageSource) {
        if (hasType(player, CACTUS$THORNS)) {
            Entity entity = damageSource.getEntity();
            if (entity != null && entity.isRemoved()) {
                entity = damageSource.getDirectEntity();
            }
            if (entity != null && !entity.isRemoved()) {
                entity.hurt(player.damageSources().cactus(), switch (player.level().getDifficulty()) {
                    case EASY -> 3;
                    case NORMAL -> 8;
                    case HARD -> 11;
                    default -> 0;
                });
            }
        }
    }

    public static void onAttacked(ServerPlayer player, DamageSource damageSource, LivingEntity victim) {
        if (victim instanceof Enemy) hurtEnemyAwardEffects:{
            List<MobEffectInstanceData> effects = getValue(player, HURT$ENEMY$AWARD$EFFECTS);
            if (effects.isEmpty()) break hurtEnemyAwardEffects;
            for (MobEffectInstanceData effect : effects) {
                player.addEffect(effect.create());
            }
        }
        if (hasType(player, FLOWER$PETAL)) flowerPetal:{
            if (damageSource.getDirectEntity() instanceof FlowerPetalProjectile) break flowerPetal;
            CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(player);
            long gameTime = player.level().getGameTime();
            if (gameTime - tag.getLong("confluence:last_flower_petal_attack") < 6) {
                break flowerPetal;
            }
            tag.putLong("confluence:last_flower_petal_attack", gameTime);
            FlowerPetalProjectile projectile = new FlowerPetalProjectile(player);
            Vec3 position = victim.position().add(0, victim.getBbHeight() * 0.5, 0);
            RandomSource random = player.getRandom1211();
            double y = (random.nextFloat() - 0.5) * 10;
            Vec3 offset = position.add(
                    (random.nextFloat() - 0.5) * 10,
                    y > 0.0 ? y + 5 : y,
                    (random.nextFloat() - 0.5) * 10
            );
            projectile.setPos(offset);
            projectile.shoot(position.x - offset.x, position.y - offset.y, position.z - offset.z, 1.2F, 0.0F);
            player.level().addFreshEntity(projectile);
        }
        if (player instanceof IServerPlayer serverPlayer && hasType(player, TITANIUM$SHARDS)) {
            titaniumShards:
            {
                if (player.hasEffect(ModEffects.TITANIUM_BARRIER) ||
                        damageSource.getDirectEntity() instanceof TitaniumShardsProjectile ||
                        serverPlayer.confluence$hasTitaniumShards()
                ) {
                    break titaniumShards;
                }

                player.addEffect(new MobEffectInstance(ModEffects.TITANIUM_BARRIER.get(), 200));
                TitaniumShardsProjectile projectile = new TitaniumShardsProjectile(player);
                serverPlayer.confluence$setTitaniumShards(projectile);
                player.level().addFreshEntity(projectile);
            }
        }
        if (damageSource.is(PortTags.DamageTypes.IS_MAGIC) && isArmorSet(player, COLD_CRYSTAL_SET)) {
            victim.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), 100));
        }
    }

    public static void onBreath(LivingBreatheEvent event) {
        if (event.canBreathe() || !(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.getAirSupply() > 0 && player.level().getGameTime() % 20 == 0 && isArmorSet(player, HEIM_SET)) { // 延长5%
            event.setConsumeAirAmount(0);
        }
    }

    public static void afterTick(ServerPlayer player, long gameTime) {
        if (gameTime % 80 == 0 && isArmorSet(player, HEIM_SET)) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1, false, false));
        }
        if (gameTime % 40 == 0 && isArmorSet(player, EMBER_ROBE_SET) && !player.hasEffect(ModEffects.INFERNO)) {
            player.addEffect(new MobEffectInstance(ModEffects.INFERNO.get(), 200, 0, false, false));
        }
        /// 跳跃后 3 秒：高地加速，强化链甲减速
        long lastJump = player.getPersistentData().getLong(LAST_JUMP_TAG);
        boolean justJumped = lastJump > 0L && gameTime - lastJump < JUMP_WINDOW_TICKS;
        applyTransient(player, Attributes.MOVEMENT_SPEED, HIGHLAND_JUMP_SPEED_ID, 0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, justJumped && isArmorSet(player, HIGHLAND_SET));
        applyTransient(player, Attributes.MOVEMENT_SPEED, HEAVY_JUMP_SLOW_ID, -0.15, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, justJumped && isArmorSet(player, REINFORCED_MAIL_SET));
        /// 狼甲：喝治疗药水后 30 秒的野性之力（攻击 +7%、移速 +10%）
        boolean wolfBuff = player.getPersistentData().getLong(WOLF_BUFF_END) > gameTime;
        applyTransient(player, LibAttributes.getAttackDamage().value(), WOLF_ATTACK_ID, 0.07, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, wolfBuff);
        applyTransient(player, Attributes.MOVEMENT_SPEED, WOLF_SPEED_ID, 0.10, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, wolfBuff);
        if (gameTime % 20 == 0 && player.isInLava()) drapsil:{
            int amount = getValue(player, DURABILITY$REPAIR$AMOUNT$PER$SECOND$IN$LAVA);
            if (amount <= 0) break drapsil;
            for (ItemStack stack : player.getArmorSlots()) {
                int j = Math.min(amount, stack.getDamageValue());
                stack.setDamageValue(stack.getDamageValue() - j);
            }
        }
    }
}
