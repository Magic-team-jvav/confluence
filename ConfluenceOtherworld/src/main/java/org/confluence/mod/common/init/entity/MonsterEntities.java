package org.confluence.mod.common.init.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.entity.monster.*;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.monster.slime.*;
import org.confluence.mod.common.entity.npc.TownSlimeRescue;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.entity.monster.CreatureAttributeBuilder.DynamicValue.byWorldPhase;

/// 怪物注册层（1.20.1 分叉后架构的 `common/init/entity/MonsterEntities`）。
///
/// **为什么新建这个包**：1.20 侧的注册层住在 `common/init/entity/`（`ModEntities`、
/// `MonsterEntities`、`BossEntities`、`NpcEntities`、`CritterEntities`、
/// `CreatureSpawnPlacements`、`DevelopmentSpawnPolicy` 共 7 个文件），而 1.21 侧的 `ModEntities`
/// 住在扁平的 `common/init/`。按本仓库「1.21 对齐 1.20」的方针，**新搬来的注册类一律放
/// `common/init/entity/`** —— 这样 31 个待移植物种里
/// `import org.confluence.mod.common.init.entity.MonsterEntities;` 这句可以原样照搬，
/// 不必逐个改包路径。`ModEntities` 留在原处是历史原因（1b 落地时它已经在那儿，
/// 靠 `dep_subset.py` 的 `--alias` 处理）。
///
/// **本文件是增量落地**：1.20 的 `MonsterEntities` 有 945 行 / 94 条注册，这里先搬**已落地物种**
/// 的条目，其余随物种批次一起长（与 1.21 侧 `ModEntities` 的增量注册是同一做法）。
/// 属性声明统一汇入 {@link ModEntities#withAttributes}，由 `ModEvents.entityAttributeCreation`
/// → `ModEntities.registerAttributes` 一次注册，不另开一套。
public final class MonsterEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    /// ------------------------------------------------------------------
    /// 血腥之地 / 腐化之地
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<BloodCrawler>> BLOOD_CRAWLER = withAttributes(registerEntity("blood_crawler", "血爬虫", EntityType.Builder.of(BloodCrawler::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(4).armorToughness(1).attackDamage(16).followRange(32).attackKnockback(1).knockbackResistance(0.8).movementSpeed(0.38).spawnReinforcementsChance(0.01)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(35).attackDamage(14).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(35).attackDamage(14).armor(4).armorToughness(1))
                    .build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<Werewolf>> WEREWOLF = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("werewolf", "狼人", EntityType.Builder.of(Werewolf::new, MobCategory.MONSTER).sized(0.9F, 2.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(18).armorToughness(7).attackDamage(37).followRange(32).attackKnockback(1).knockbackResistance(0.64).movementSpeed(0.3).build());

    public static final DeferredHolder<EntityType<?>, EntityType<WindyBalloon>> WINDY_BALLOON = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("windy_balloon", "大风气球怪", EntityType.Builder.<WindyBalloon>of(WindyBalloon::new, MobCategory.MONSTER).sized(0.8F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(1).armor(0).attackDamage(0).knockbackResistance(0.7).followRange(16).build());

    public static final DeferredHolder<EntityType<?>, EntityType<AngryDandelion>> ANGRY_DANDELION = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_dandelion", "愤怒蒲公英", EntityType.Builder.of(AngryDandelion::new, MobCategory.MONSTER).sized(1.0F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(0).armorToughness(0).attackDamage(8).knockbackResistance(1.0).movementSpeed(0.0).followRange(32)
                    .projectile(ModEntities.DANDELION_SEED, projectile -> projectile.damage(8))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(14).armor(0).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(21).armor(0).armorToughness(0))
                    .build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> CAVE_BAT = withAttributes(registerEntity("cave_bat", "洞穴蝙蝠", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(9).armor(1).armorToughness(0).attackDamage(7).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(37).attackDamage(24).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(46).attackDamage(31).armor(5).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> GIANT_BAT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("giant_bat", "巨型蝙蝠", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ROUTINE), MobCategory.MONSTER).sized(0.6F, 1.4F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(52).armor(8).armorToughness(3).attackDamage(24).followRange(32).attackKnockback(0.2).knockbackResistance(0.33).phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(21).armor(8).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> JUNGLE_BAT = withAttributes(registerEntity("jungle_bat", "丛林蝙蝠", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(18).armor(2).armorToughness(0).attackDamage(11).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(39).attackDamage(19).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(59).attackDamage(28).armor(6).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> GIANT_FLYING_FOX = withAttributes(registerEntity("giant_flying_fox", "巨型飞狐", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ROUTINE), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(11).armorToughness(4).attackDamage(42).followRange(48).attackKnockback(0.5).knockbackResistance(0.64).build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> ICE_BAT = withAttributes(registerEntity("ice_bat", "冰雪蝙蝠", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ICE), MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(16).armor(3).armorToughness(1).attackDamage(10).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(35).attackDamage(17).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(52).attackDamage(25).armor(9).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> ILLUMINANT_BAT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("illuminant_bat", "夜明蝙蝠", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ILLUMINANT), MobCategory.MONSTER).sized(0.8F, 1.35F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(14).armorToughness(6).attackDamage(39).followRange(32).attackKnockback(0.2).knockbackResistance(0.33).build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> SPORE_BAT = withAttributes(registerEntity("spore_bat", "孢子蝙蝠", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(9).armor(1).armorToughness(0).attackDamage(7).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(37).attackDamage(24).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(46).attackDamage(31).armor(5).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> HELL_BAT = withAttributes(registerEntityWithEnglishName("hell_bat", "地狱蝙蝠", "Hellbat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.HELL), MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.creature().maxHealth(24).armor(4).armorToughness(1).attackDamage(19).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(26).attackDamage(17).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(26).attackDamage(17).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> LAVA_BAT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("lava_bat", "熔岩蝙蝠", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.LAVA), MobCategory.MONSTER).sized(0.8F, 1.25F).clientTrackingRange(10).fireImmune())),
            () -> CreatureAttributeBuilder.creature().maxHealth(84).armor(8).armorToughness(3).attackDamage(26).followRange(32).attackKnockback(0.2).knockbackResistance(0.46).build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> WOODEN_MIMIC = withAttributes(registerEntity("wooden_mimic", "木宝箱怪", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(6).armorToughness(2).attackDamage(16).followRange(32).attackKnockback(1).knockbackResistance(0.73).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(260).attackDamage(42).armor(14).armorToughness(6))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(260).attackDamage(42).armor(14).armorToughness(6))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> GOLDEN_MIMIC = withAttributes(registerEntity("golden_mimic", "金宝箱怪", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(6).armorToughness(2).attackDamage(16).followRange(32).attackKnockback(1).knockbackResistance(0.73).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(260).attackDamage(42).armor(14).armorToughness(6))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(260).attackDamage(42).armor(14).armorToughness(6))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> ICE_MIMIC = withAttributes(registerEntity("ice_mimic", "冰雪宝箱怪", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(6).armorToughness(2).attackDamage(16).followRange(32).attackKnockback(1).knockbackResistance(0.73).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(260).attackDamage(42).armor(14).armorToughness(6))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(260).attackDamage(42).armor(14).armorToughness(6))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> SHADOW_MIMIC = withAttributes(registerEntity("shadow_mimic", "暗影宝箱怪", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(6).armorToughness(2).attackDamage(16).followRange(32).attackKnockback(1).knockbackResistance(0.73).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(260).attackDamage(42).armor(14).armorToughness(6))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(260).attackDamage(42).armor(14).armorToughness(6))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseMimic>> JUNGLE_MIMIC = withAttributes(registerEntity("jungle_mimic", "丛林宝箱怪", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1820).armor(16).armorToughness(6).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9)
                    .state(BaseMimic.CombatState.DEFENDING, state -> state.duration(40))
                    .state(BaseMimic.CombatState.RISING, state -> state.duration(30))
                    .state(BaseMimic.CombatState.SLAMMING, state -> state.duration(40).chargeSpeed(1.2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseMimic>> CORRUPT_MIMIC = withAttributes(registerEntity("corrupt_mimic", "腐化宝箱怪", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1820).armor(16).armorToughness(6).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9)
                    .state(BaseMimic.CombatState.DEFENDING, state -> state.duration(40))
                    .state(BaseMimic.CombatState.RISING, state -> state.duration(30))
                    .state(BaseMimic.CombatState.SLAMMING, state -> state.duration(40).chargeSpeed(1.2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseMimic>> CRIMSON_MIMIC = withAttributes(registerEntity("crimson_mimic", "猩红宝箱怪", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1820).armor(16).armorToughness(6).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9)
                    .state(BaseMimic.CombatState.DEFENDING, state -> state.duration(40))
                    .state(BaseMimic.CombatState.RISING, state -> state.duration(30))
                    .state(BaseMimic.CombatState.SLAMMING, state -> state.duration(40).chargeSpeed(1.2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseMimic>> HALLOWED_MIMIC = withAttributes(registerEntity("hallowed_mimic", "神圣宝箱怪", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1820).armor(16).armorToughness(6).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9)
                    .state(BaseMimic.CombatState.DEFENDING, state -> state.duration(40))
                    .state(BaseMimic.CombatState.RISING, state -> state.duration(30))
                    .state(BaseMimic.CombatState.SLAMMING, state -> state.duration(40).chargeSpeed(1.2))
                    .build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> SPORE_SKELETON = withAttributes(registerEntity("spore_skeleton", "孢子骷髅", EntityType.Builder.<MeleeSkeleton>of((type, level) -> new MeleeSkeleton(type, level, true, MeleeSkeleton.BehaviorProfile.OPEN_DOORS), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(4).armorToughness(1).attackDamage(11).followRange(60).attackKnockback(0.5).knockbackResistance(0.28)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(35).attackDamage(9).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(19).armor(8).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BASE_BONES = withAttributes(registerSkeleton("base_bones", "地牢骷髅", 0.65F, 1.85F),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(4).armorToughness(1).attackDamage(11).followRange(20).attackKnockback(1).knockbackResistance(0.28).movementSpeed(0.3).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(35).attackDamage(9).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(19).armor(8).armorToughness(3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> ANGER_BONES = withAttributes(registerSkeleton("anger_bones", "愤怒骷髅", 0.65F, 1.85F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(42).armor(4).armorToughness(1).attackDamage(14)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(12).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(46).attackDamage(12).armor(4).armorToughness(1))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> SHORT_BONES = withAttributes(registerSkeleton("short_bones", "矮骷髅", 0.55F, 1.65F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(38).armor(4).armorToughness(1).attackDamage(12)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(42).attackDamage(11).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(42).attackDamage(11).armor(4).armorToughness(1))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_BONES = withAttributes(registerSkeleton("big_bones", "大骷髅", 0.85F, 2.25F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(53).armor(5).armorToughness(1).attackDamage(17)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(16).armor(5).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(16).armor(5).armorToughness(1))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_ANGER_BONES = withAttributes(registerSkeleton("big_anger_bones", "大愤怒骷髅", 0.9F, 2.4F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(3).armorToughness(1).attackDamage(18)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(16).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(41).attackDamage(16).armor(3).armorToughness(1))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_MUSCLE_ANGER_BONES = withAttributes(registerSkeleton("big_muscle_anger_bones", "大块头愤怒骷髅", 0.95F, 2.45F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(6).armorToughness(2).attackDamage(15)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(13).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(41).attackDamage(13).armor(6).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_HELMET_ANGER_BONES = withAttributes(registerSkeleton("big_helmet_anger_bones", "大头盔愤怒骷髅", 1F, 2.6F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(63).armor(7).armorToughness(2).attackDamage(13)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(11).armor(7).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(11).armor(7).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> UNDEAD_VIKING = withAttributes(registerSkeleton("undead_viking", "亡灵维京海盗", 1F, 2.6F),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(5).armorToughness(2).attackDamage(13)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(11).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(41).attackDamage(11).armor(5).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> ARMORED_VIKING = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerSkeleton("armored_viking", "装甲维京海盗", 0.8F, 2.35F, MeleeSkeleton.BehaviorProfile.ARMORED_VIKING)),
            () -> CreatureAttributeBuilder.creature().maxHealth(146).armor(13).armorToughness(5).attackDamage(26).knockbackResistance(0.6).movementSpeed(0.23).followRange(32).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Decayeder>> DECAYEDER = withAttributes(registerEntity("decayeder", "腐骴", EntityType.Builder.of(Decayeder::new, MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(10).armor(3).attackDamage(6).armorToughness(1)
                    /// 不指定 damage 时保留原版弓箭结算；可在这里显式覆盖箭的 baseDamage。
                    .projectile(() -> EntityType.ARROW, projectile -> projectile.attackDamage(1.0 / 1.6).speed(1.6))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> ARMORED_SKELETON = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerSkeleton("armored_skeleton", "装甲骷髅", 0.8F, 2.45F, MeleeSkeleton.BehaviorProfile.NORMAL)),
            () -> CreatureAttributeBuilder.creature().maxHealth(136).armor(13).armorToughness(5).attackDamage(21).knockbackResistance(0.64).movementSpeed(0.23).followRange(32).build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> GOBLIN_SCOUT = withAttributes(registerGoblinLand("goblin_scout", "哥布林侦察兵", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN),
            () -> CreatureAttributeBuilder.creature().maxHealth(42).armor(3).armorToughness(1).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.37)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(9).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(92).attackDamage(19).armor(6).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> GOBLIN_PEON = withAttributes(registerGoblinLand("goblin_peon", "哥布林苦力", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.BREAK),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(2).armorToughness(0).attackDamage(7).followRange(32).attackKnockback(1).knockbackResistance(0.2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(11).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(103).attackDamage(17).armor(6).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> GOBLIN_WARRIOR = withAttributes(registerGoblinLand("goblin_warrior", "哥布林战士", 0.65F, 1.85F, Items.STONE_SWORD.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN),
            () -> CreatureAttributeBuilder.creature().maxHealth(58).armor(4).armorToughness(1).attackDamage(13).followRange(32).attackKnockback(1).knockbackResistance(0.6)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(63).attackDamage(12).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(63).attackDamage(12).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> GOBLIN_THIEF = withAttributes(registerGoblinLand("goblin_thief", "哥布林盗贼", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.BREAK),
            () -> CreatureAttributeBuilder.creature().maxHealth(42).armor(3).armorToughness(1).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.37)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(9).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(92).attackDamage(19).armor(6).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinArcher>> GOBLIN_ARCHER = withAttributes(registerEntity("goblin_archer", "哥布林弓箭手", EntityType.Builder.of(GoblinArcher::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(42).armor(3).armorToughness(1).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.37)
                    /// 不指定 damage 时保留原版弓箭结算；可在这里显式覆盖箭的 baseDamage。
                    .projectile(() -> EntityType.ARROW, projectile -> projectile.attackDamage(1.0 / 1.6).speed(1.6))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(9).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(92).attackDamage(19).armor(6).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AngerGoblin>> ANGER_GOBLIN = withAttributes(registerEntity("anger_goblin", "愤怒哥布林", EntityType.Builder.of(AngerGoblin::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(220).armor(0).attackDamage(15).followRange(32).attackKnockback(1).knockbackResistance(0.88).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SporeZombie>> SPORE_ZOMBIE = withAttributes(registerEntity("spore_zombie", "孢子僵尸", EntityType.Builder.of(SporeZombie::new, MobCategory.MONSTER).sized(0.75F, 1.95F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(94).armor(5).armorToughness(2).attackDamage(21).followRange(60).attackKnockback(0.6).knockbackResistance(0.1).movementSpeed(0.08).phase(GamePhase.PLANTERA, phase -> phase.maxHealth(103).attackDamage(19).armor(5).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<SporeZombie>> HAT_SPORE_ZOMBIE = withAttributes(registerEntity("hat_spore_zombie", "帽子蘑菇僵尸", EntityType.Builder.of(SporeZombie::new, MobCategory.MONSTER).sized(0.75F, 1.95F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(8).armorToughness(3).attackDamage(20).followRange(60).attackKnockback(0.6).knockbackResistance(0.72).movementSpeed(0.08).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Zombie>> ZOMBIE = withAttributes(registerEntity("zombie", "僵尸", EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(24).armor(3).armorToughness(1).attackDamage(8).followRange(16).attackKnockback(0.5).knockbackResistance(0.0).movementSpeed(0.23)
                    .state(Zombie.Variant.NORMAL, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(24, 52, 77))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(8, 13, 20))
                            .attribute(Attributes.ARMOR, byWorldPhase(3, 6, 9))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 2, 3))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(1, 3, 5)))
                    .state(Zombie.Variant.ARMED, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(24, 52, 77))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(8, 13, 20))
                            .attribute(Attributes.ARMOR, byWorldPhase(3, 6, 9))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 2, 3))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(1, 3, 5)))
                    .state(Zombie.Variant.SLIMED, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(21, 46, 69))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(7, 12, 19))
                            .attribute(Attributes.ARMOR, byWorldPhase(3, 6, 9))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 2, 3))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(1, 3, 5)))
                    .state(Zombie.Variant.PINCUSHION, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(26, 58, 58))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 15, 15))
                            .attribute(Attributes.ARMOR, byWorldPhase(4, 8, 8))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 3, 3))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 4)))
                    .state(Zombie.Variant.TWIGGY, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(24, 52, 77))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 15, 23))
                            .attribute(Attributes.ARMOR, byWorldPhase(2, 4, 6))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 1, 2))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 7)))
                    .state(Zombie.Variant.SWAMP, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(24, 52, 77))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(7, 12, 19))
                            .attribute(Attributes.ARMOR, byWorldPhase(4, 8, 11))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 3, 4))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(1, 3, 5)))
                    .state(Zombie.Variant.RAINCOAT, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(26, 58, 58))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 15, 15))
                            .attribute(Attributes.ARMOR, byWorldPhase(4, 8, 8))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 3, 3))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 4)))
                    .state(Zombie.Variant.BLOOD, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(39, 43, 86))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(11, 9, 19))
                            .attribute(Attributes.ARMOR, byWorldPhase(4, 4, 8))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 1, 3))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 2, 5)))
                    .state(Zombie.Variant.ESKIMO, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(26, 58, 58))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 15, 15))
                            .attribute(Attributes.ARMOR, byWorldPhase(4, 8, 8))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 3, 3))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 4)))
                    .state(Zombie.Variant.BALD, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(21, 46, 69))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(8, 14, 21))
                            .attribute(Attributes.ARMOR, byWorldPhase(3, 5, 7))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 2, 3))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(1, 3, 6)))
                    .build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> DOCTOR_BONES = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("doctor_bones", "骷髅博士", 1.0F, 2.0F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON)),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(5).armorToughness(2).attackDamage(11).knockbackResistance(0.55).followRange(32).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> SNOW_FLINX = withAttributes(registerLand("snow_flinx", "小雪怪", 1.25F, 1.25F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 0.8, false),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(6).armorToughness(2).attackDamage(14).followRange(60).attackKnockback(0.1).knockbackResistance(0.1).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(12).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(41).attackDamage(12).armor(6).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> BASILISK = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("basilisk", "蛇蜥怪", 1.6F, 1.55F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.8, true)),
            () -> CreatureAttributeBuilder.creature().maxHealth(141).armor(16).armorToughness(6).attackDamage(34).followRange(32).movementSpeed(0.35).attackKnockback(1).knockbackResistance(0.73).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> BLOOD_ZOMBIE = withAttributes(registerAcceleratingLand("blood_zombie", "血腥僵尸", 0.75F, 1.95F, 0.25, 0.8, true, BaseWarriorMonster.LandAnimationProfile.WALK_RUN_IDLE_ATTACK, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.DoorBehavior.OPEN),
            () -> CreatureAttributeBuilder.creature().maxHealth(39).armor(4).armorToughness(1).attackDamage(11).followRange(60).attackKnockback(0.5).knockbackResistance(0.1).movementSpeed(0.15)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(43).attackDamage(9).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(19).armor(8).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> THE_GROOM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("the_groom", "僵尸新郎", 1.0F, 2.5F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON)),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(4).armorToughness(1).attackDamage(8).knockbackResistance(0.55).followRange(32).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(115).attackDamage(7).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(115).attackDamage(7).armor(4).armorToughness(1))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> THE_BRIDE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("the_bride", "僵尸新娘", 1.0F, 2.0F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON)),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(4).armorToughness(1).attackDamage(8).knockbackResistance(0.55).followRange(32).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(115).attackDamage(7).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(115).attackDamage(7).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<FlyingFishMonster>> FLYING_FISH = withAttributes(registerFlyingFish("flying_fish", "飞鱼", 0.9F, 0.9F, new FlyingFishMonster.PursuitProfile(0.95, 0.5, 0.02, 5)),
            () -> CreatureAttributeBuilder.creature().maxHealth(11).armor(2).armorToughness(0).attackDamage(5).followRange(30).attackKnockback(0.5).knockbackResistance(0.3)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(17).armor(8).armorToughness(3))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(21).armor(9).armorToughness(4))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> ANTLION_LARVA = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("antlion_larva", "蚁狮幼虫", 0.7F, 0.5F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.3, true)),
            () -> CreatureAttributeBuilder.creature().maxHealth(16).armor(1).armorToughness(0).attackDamage(6).movementSpeed(0.3).knockbackResistance(0.33).followRange(24).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(19).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(23).armor(5).armorToughness(2))
                    .build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    /// 木乃伊（1.20 `:308`，按脚下沙块分型；`JumpProfile` 传 `null`，`mummy=true`）。
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> MUMMY = withAttributes(registerJumpingLand("mummy", "木乃伊", 0.75F, 1.95F, null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.0, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(68).armor(8).armorToughness(3).attackDamage(26).followRange(48).attackKnockback(1).knockbackResistance(0.46).stepHeight(3.2).jumpStrength(0.5)
                    .state(JumpingWarriorMonster.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(75).attackDamage(23).armor(8).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> DARK_MUMMY = withAttributes(registerJumpingLand("dark_mummy", "暗黑木乃伊", 0.75F, 1.95F, null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.5, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(94).armor(9).armorToughness(3).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5)
                    .state(JumpingWarriorMonster.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> BLOOD_MUMMY = withAttributes(registerJumpingLand("blood_mummy", "血木乃伊", 0.75F, 1.95F, null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.5, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(94).armor(9).armorToughness(3).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5)
                    .state(JumpingWarriorMonster.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> LIGHT_MUMMY = withAttributes(registerJumpingLand("light_mummy", "光明木乃伊", 0.75F, 1.95F, null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.0, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(9).armorToughness(3).attackDamage(29).followRange(48).attackKnockback(1).knockbackResistance(0.51).stepHeight(3.2).jumpStrength(0.5)
                    .state(JumpingWarriorMonster.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> DARK_LAMIA = withAttributes(registerLand("dark_lamia", "黑暗拉弥亚", 0.75F, 1.95F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.3, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(13).armorToughness(5).attackDamage(28).followRange(48).attackKnockback(1).knockbackResistance(0.69).stepHeight(3.2).jumpStrength(0.5).build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> LIGHT_LAMIA = withAttributes(registerLand("light_lamia", "光明拉弥亚", 0.75F, 1.95F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.3, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(13).armorToughness(5).attackDamage(28).followRange(48).attackKnockback(1).knockbackResistance(0.69).stepHeight(3.2).jumpStrength(0.5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> GHOUL = withAttributes(registerLand("ghoul", "食尸鬼", 0.75F, 1.95F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.6, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(94).armor(12).armorToughness(5).attackDamage(26).followRange(64).attackKnockback(1).knockbackResistance(0.46).stepHeight(3.2).jumpStrength(0.7).build());

    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> VILE_GHOUL = withAttributes(registerJumpingLand("vile_ghoul", "腐恶食尸鬼", 0.75F, 1.95F, null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, false),
            () -> CreatureAttributeBuilder.creature().maxHealth(130).armor(14).armorToughness(6).attackDamage(32).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.7).build());
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> TAINTED_GHOUL = withAttributes(registerJumpingLand("tainted_ghoul", "红染食尸鬼", 0.75F, 1.95F, null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, false),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(15).armorToughness(6).attackDamage(34).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.7).build());
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> DREAMER_GHOUL = withAttributes(registerJumpingLand("dreamer_ghoul", "神梦食尸鬼", 0.75F, 1.95F, null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, false),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(15).armorToughness(6).attackDamage(29).followRange(64).attackKnockback(1).knockbackResistance(0.64).stepHeight(3.2).jumpStrength(0.7).build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> WYVERN_SEGMENT = registerWormSegment("wyvern_segment", "飞龙体节");
    public static final DeferredHolder<EntityType<?>, EntityType<Wyvern>> WYVERN = withAttributes(registerEntity("wyvern", "飞龙", EntityType.Builder.<Wyvern>of((type, level) -> new Wyvern(type, level, MonsterEntities.WYVERN_SEGMENT.get()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10).updateInterval(1)),
            () -> CreatureAttributeBuilder.creature().maxHealth(2080).armor(5).armorToughness(2).attackDamage(42).followRange(50).attackKnockback(1).knockbackResistance(0.28).state(BaseWormMonster.BodySection.HEAD, state -> state.attribute(Attributes.ATTACK_DAMAGE, 42).attribute(Attributes.ARMOR, 5).attribute(Attributes.ARMOR_TOUGHNESS, 2))
                    .state(BaseWormMonster.BodySection.BODY, state -> state.attribute(Attributes.ATTACK_DAMAGE, 21).attribute(Attributes.ARMOR, 9).attribute(Attributes.ARMOR_TOUGHNESS, 4))
                    .state(BaseWormMonster.BodySection.TAIL, state -> state.attribute(Attributes.ATTACK_DAMAGE, 21).attribute(Attributes.ARMOR, 9).attribute(Attributes.ARMOR_TOUGHNESS, 4))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> GIANT_WORM_SEGMENT = registerWormSegment("giant_worm_segment", "巨型蠕虫体节");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> GIANT_WORM = withAttributes(registerWorm("giant_worm", "巨型蠕虫", 2F, 2F, SimpleWormMonster.Role.UNDERGROUND, SimpleWormMonster.Anatomy.GIANT_WORM, MonsterEntities.GIANT_WORM_SEGMENT),
            () -> CreatureAttributeBuilder.creature().maxHealth(16).armor(0).armorToughness(0).attackDamage(5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(86).attackDamage(19).armor(0).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(103).attackDamage(23).armor(0).armorToughness(0))
                    .state(BaseWormMonster.BodySection.HEAD, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(5, 19, 23))
                            .attribute(Attributes.ARMOR, 0)
                            .attribute(Attributes.ARMOR_TOUGHNESS, 0))
                    .state(BaseWormMonster.BodySection.BODY, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(3, 9, 11))
                            .attribute(Attributes.ARMOR, byWorldPhase(2, 9, 11))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 4, 4)))
                    .state(BaseWormMonster.BodySection.TAIL, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(3, 8, 9))
                            .attribute(Attributes.ARMOR, byWorldPhase(3, 11, 14))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 4, 6)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> DIGGER_SEGMENT = registerWormSegment("digger_segment", "挖掘怪体节");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> DIGGER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("digger", "挖掘怪", EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, SimpleWormMonster.Role.UNDERGROUND, SimpleWormMonster.Anatomy.DIGGER, MonsterEntities.DIGGER_SEGMENT.get()), MobCategory.MONSTER).sized(1.6F, 1.05F).clientTrackingRange(10).updateInterval(1))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(5).armorToughness(2).attackDamage(24).knockbackResistance(1.0).followRange(32).state(BaseWormMonster.BodySection.HEAD, state -> state.attribute(Attributes.ATTACK_DAMAGE, 24).attribute(Attributes.ARMOR, 5).attribute(Attributes.ARMOR_TOUGHNESS, 2))
                    .state(BaseWormMonster.BodySection.BODY, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(15, 15, 13))
                            .attribute(Attributes.ARMOR, 9)
                            .attribute(Attributes.ARMOR_TOUGHNESS, 4))
                    .state(BaseWormMonster.BodySection.TAIL, state -> state.attribute(Attributes.ATTACK_DAMAGE, 14).attribute(Attributes.ARMOR, 14).attribute(Attributes.ARMOR_TOUGHNESS, 6))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> DEVOURER_SEGMENT = registerWormSegment("devourer_segment", "吞噬怪体节");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> DEVOURER = withAttributes(registerWorm("devourer", "吞噬怪", 2F, 2F, SimpleWormMonster.Role.CORRUPTION, SimpleWormMonster.Anatomy.DEVOURER, () -> MonsterEntities.DEVOURER_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(52).armor(1).armorToughness(0).attackDamage(17)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(15).armor(1).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(15).armor(1).armorToughness(0))
                    .state(BaseWormMonster.BodySection.HEAD, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(17, 15, 15))
                            .attribute(Attributes.ARMOR, 1)
                            .attribute(Attributes.ARMOR_TOUGHNESS, 0))
                    .state(BaseWormMonster.BodySection.BODY, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 8, 15))
                            .attribute(Attributes.ARMOR, byWorldPhase(3, 3, 6))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 1, 2)))
                    .state(BaseWormMonster.BodySection.TAIL, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(7, 6, 12))
                            .attribute(Attributes.ARMOR, byWorldPhase(5, 5, 9))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(2, 2, 4)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> WORLD_FEEDER_SEGMENT = registerWormSegment("world_feeder_segment", "吞世怪体节");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> WORLD_FEEDER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("world_feeder", "吞世怪", EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, SimpleWormMonster.Role.CORRUPTION, SimpleWormMonster.Anatomy.WORLD_FEEDER, MonsterEntities.WORLD_FEEDER_SEGMENT.get()), MobCategory.MONSTER).sized(2.0F, 1.1F).clientTrackingRange(10).updateInterval(1))),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(17).armorToughness(7).attackDamage(37).knockbackResistance(1.0).followRange(32).state(BaseWormMonster.BodySection.HEAD, state -> state.attribute(Attributes.ATTACK_DAMAGE, 37).attribute(Attributes.ARMOR, 17).attribute(Attributes.ARMOR_TOUGHNESS, 7))
                    .state(BaseWormMonster.BodySection.BODY, state -> state.attribute(Attributes.ATTACK_DAMAGE, 29).attribute(Attributes.ARMOR, 18).attribute(Attributes.ARMOR_TOUGHNESS, 8))
                    .state(BaseWormMonster.BodySection.TAIL, state -> state.attribute(Attributes.ATTACK_DAMAGE, 21).attribute(Attributes.ARMOR, 20).attribute(Attributes.ARMOR_TOUGHNESS, 8))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> BONE_SERPENT_SEGMENT = registerWormSegment("bone_serpent_segment", "骨蛇体节");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> BONE_SERPENT = withAttributes(registerWorm("bone_serpent", "骨蛇", 2F, 2F, SimpleWormMonster.Role.BONE_SERPENT, SimpleWormMonster.Anatomy.BONE_SERPENT, () -> MonsterEntities.BONE_SERPENT_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(6).armorToughness(2).attackDamage(19).state(BaseWormMonster.BodySection.HEAD, state -> state.attribute(Attributes.ATTACK_DAMAGE, 19).attribute(Attributes.ARMOR, 6).attribute(Attributes.ARMOR_TOUGHNESS, 2))
                    .state(BaseWormMonster.BodySection.BODY, state -> state.attribute(Attributes.ATTACK_DAMAGE, 11).attribute(Attributes.ARMOR, 9).attribute(Attributes.ARMOR_TOUGHNESS, 3))
                    .state(BaseWormMonster.BodySection.TAIL, state -> state.attribute(Attributes.ATTACK_DAMAGE, 9).attribute(Attributes.ARMOR, 9).attribute(Attributes.ARMOR_TOUGHNESS, 3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> WITHER_BONE_SERPENT_SEGMENT = registerWormSegment("wither_bone_serpent_segment", "凋零骨蛇体节");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> WITHER_BONE_SERPENT = withAttributes(registerWorm("wither_bone_serpent", "凋零骨蛇", 2F, 2F, SimpleWormMonster.Role.BONE_SERPENT, SimpleWormMonster.Anatomy.BONE_SERPENT, () -> MonsterEntities.WITHER_BONE_SERPENT_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(186).armor(7).attackDamage(22).armorToughness(3).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> ARCH_WYVERN_SEGMENT = registerWormSegment("arch_wyvern_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> ARCH_WYVERN = withAttributes(registerWorm("arch_wyvern", "大飞龙", 12, 1.8F, 1.8F, SimpleWormMonster.Role.FLYING, () -> MonsterEntities.ARCH_WYVERN_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(3120).armor(9).attackDamage(52).followRange(64).attackKnockback(1).knockbackResistance(0.37).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> LEECH_SEGMENT = registerWormSegment("leech_segment", "血蛭体节");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> LEECH = withAttributes(registerWorm("leech", "血蛭", 2F, 2F, SimpleWormMonster.Role.UNDERWORLD, SimpleWormMonster.Anatomy.LEECH, () -> MonsterEntities.LEECH_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(25).armor(1).armorToughness(0).attackDamage(14).movementSpeed(0.145).state(BaseWormMonster.BodySection.HEAD, state -> state.attribute(Attributes.ATTACK_DAMAGE, 14).attribute(Attributes.ARMOR, 1).attribute(Attributes.ARMOR_TOUGHNESS, 0))
                    .state(BaseWormMonster.BodySection.BODY, state -> state.attribute(Attributes.ATTACK_DAMAGE, 12).attribute(Attributes.ARMOR, 3).attribute(Attributes.ARMOR_TOUGHNESS, 1))
                    .state(BaseWormMonster.BodySection.TAIL, state -> state.attribute(Attributes.ATTACK_DAMAGE, 10).attribute(Attributes.ARMOR, 5).attribute(Attributes.ARMOR_TOUGHNESS, 2))
                    .build());

    /// 地下水域：水母与琵琶鱼
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> BLUE_JELLYFISH = withAttributes(registerJellyFish("blue_jellyfish", "蓝水母", JellyFish.Profile.ROUTINE),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(18).armor(2).armorToughness(0).attackDamage(13).followRange(16).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(39).attackDamage(23).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(39).attackDamage(23).armor(4).armorToughness(1))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> GREEN_JELLYFISH = withAttributes(registerJellyFish("green_jellyfish", "绿水母", JellyFish.Profile.ROUTINE),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(63).armor(14).armorToughness(6).attackDamage(42).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<AnglerFish>> ANGLER_FISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("angler_fish", "琵琶鱼", EntityType.Builder.of(AnglerFish::new, MobCategory.MONSTER).sized(0.8F, 0.6F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(47).armor(10).armorToughness(4).attackDamage(42).followRange(24).movementSpeed(1.5).attackKnockback(0.5).knockbackResistance(0.1).build());

    /// 花岗岩洞：元素与巨人
    public static final DeferredHolder<EntityType<?>, EntityType<GraniteElemental>> GRANITE_ELEMENTAL = withAttributes(registerEntity("granite_elemental", "花岗精", EntityType.Builder.of(GraniteElemental::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(21).armor(4).armorToughness(1).attackDamage(13).followRange(32).attackKnockback(1).knockbackResistance(0.73)
                    .state(GraniteElemental.DefensePhase.ENTERING, state -> state.duration(7))
                    .state(GraniteElemental.DefensePhase.DEFENDING, state -> state.duration(40))
                    .state(GraniteElemental.DefensePhase.EXITING, state -> state.duration(7))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(23).attackDamage(11).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(46).attackDamage(23).armor(8).armorToughness(3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<GraniteGolem>> GRANITE_GOLEM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("granite_golem", "花岗岩巨人", EntityType.Builder.of(GraniteGolem::new, MobCategory.MONSTER).sized(1.7F, 2.3F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(58).armor(9).armorToughness(3).attackDamage(16).knockbackResistance(0.69).movementSpeed(0.2)
                    .state(GraniteGolem.DefensePhase.ACTIVE, state -> state.attackInterval(100))
                    .state(GraniteGolem.DefensePhase.ENTERING, state -> state.duration(5))
                    .state(GraniteGolem.DefensePhase.DEFENDING, state -> state.duration(40))
                    .state(GraniteGolem.DefensePhase.EXITING, state -> state.duration(5))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(63).attackDamage(14).armor(9).armorToughness(3))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(63).attackDamage(14).armor(9).armorToughness(3))
                    .build());

    /// 丛林：蜘蛛、蹦跳兽与陆龟
    public static final DeferredHolder<EntityType<?>, EntityType<GiantShelly>> GIANT_SHELLY = withAttributes(registerEntity("giant_shelly", "巨型卷壳怪", EntityType.Builder.of(GiantShelly::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(6).armorToughness(2).attackDamage(9).followRange(20).attackKnockback(0).knockbackResistance(0.4).movementSpeed(0.1)
                    .state(GiantShelly.Phase.ENTERING_SHELL, state -> state.bonus(Attributes.ARMOR, 12).duration(20))
                    .state(GiantShelly.Phase.ROLLING, state -> state.bonus(Attributes.ATTACK_DAMAGE, 4).bonus(Attributes.ARMOR, 12).duration(30))
                    .state(GiantShelly.Phase.FREE, state -> state.duration(40))
                    .state(GiantShelly.Phase.RECOVERING, state -> state.duration(20))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(15).armor(11).armorToughness(4))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(15).armor(11).armorToughness(4))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<Crawdad>> CRAWDAD = withAttributes(registerEntity("crawdad", "龙虾", EntityType.Builder.of(Crawdad::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(3).armorToughness(1).attackDamage(15).followRange(25).attackKnockback(0).knockbackResistance(0.1).jumpStrength(0.8)
                    .state(Crawdad.AttackState.CLAW_ATTACK, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.875))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(29).attackDamage(13).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(26).armor(6).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<Derpling>> DERPLING = withAttributes(registerEntity("derpling", "跳跳兽", EntityType.Builder.of(Derpling::new, MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(12).armorToughness(5).attackDamage(42).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).build());

    /// 冰雪：蝙蝠、雪怪与陆龟
    public static final DeferredHolder<EntityType<?>, EntityType<GiantTortoise>> ICE_TORTOISE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_tortoise", "冰雪陆龟", EntityType.Builder.of(GiantTortoise::new, MobCategory.MONSTER).sized(2.25F, 1.85F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(13).armorToughness(5).attackDamage(29).followRange(48).attackKnockback(1).knockbackResistance(0.73).movementSpeed(0.2)
                    .state(GiantTortoise.Phase.SPINNING, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.8).multiplyBase(Attributes.ARMOR, 2))
                    .state(GiantTortoise.Phase.RETRACTING, state -> state.duration(10))
                    .state(GiantTortoise.Phase.WINDING_UP, state -> state.duration(12))
                    .state(GiantTortoise.Phase.EMERGING, state -> state.duration(10))
                    .state(GiantTortoise.Phase.WALK, state -> state.attackInterval(20))
                    .build());

    /// 地下沙漠：蛇蜥怪、沙贼与沙漠蠕虫
    public static final DeferredHolder<EntityType<?>, EntityType<SandPoacher>> SAND_POACHER = withAttributes(registerEntity("sand_poacher", "沙贼", EntityType.Builder.of(SandPoacher::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(167).armor(11).armorToughness(4).attackDamage(35).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5)
                    .state(SandPoacher.CombatState.CLIMBING, state -> state.bonus(Attributes.MOVEMENT_SPEED, 0.25))
                    .build());

    /// 猩红：史莱姆、脸怪与蹦跳兽
    public static final DeferredHolder<EntityType<?>, EntityType<Derpling>> HERPLING = withAttributes(registerEntity("herpling", "蹦蹦兽", EntityType.Builder.of(Derpling::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(12).armorToughness(5).attackDamage(34).followRange(48).attackKnockback(1).knockbackResistance(0.73).stepHeight(3.2).jumpStrength(0.5).build());

    /// 海洋：鲨鱼、水母与史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<Shark>> SHARK = withAttributes(registerEntity("shark", "鲨鱼", EntityType.Builder.of(Shark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(156).armor(1).armorToughness(0).attackDamage(21).followRange(48).movementSpeed(1.2).attackKnockback(0.37).knockbackResistance(0.1).build());

    /// 地牢：诅咒骷髅与幽魂
    public static final DeferredHolder<EntityType<?>, EntityType<CursedSkull>> CURSED_SKULL = withAttributes(registerEntity("cursed_skull", "诅咒骷髅头", EntityType.Builder.of(CursedSkull::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(21).armor(3).armorToughness(1).attackDamage(19).followRange(32).attackKnockback(1).knockbackResistance(0.82)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(23).attackDamage(17).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(23).attackDamage(17).armor(3).armorToughness(1))
                    .build());

    /// 陨石：流星头
    public static final DeferredHolder<EntityType<?>, EntityType<MeteorHead>> METEOR_HEAD = withAttributes(registerEntity("meteor_head", "流星头", EntityType.Builder.of(MeteorHead::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(14).armor(3).armorToughness(1).attackDamage(21).followRange(32).attackKnockback(1).knockbackResistance(0.64).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(15).attackDamage(19).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(15).attackDamage(19).armor(3).armorToughness(1))
                    .build());

    /// 墓地：幽灵
    public static final DeferredHolder<EntityType<?>, EntityType<Ghost>> GHOST = withAttributes(registerEntity("ghost", "鬼魂", EntityType.Builder.of(Ghost::new, MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(2).armorToughness(0).attackDamage(8).followRange(16).attackKnockback(0).knockbackResistance(0.55).gravity(0)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(14).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(21).armor(6).armorToughness(2))
                    .build());

    /// 血月：血腥僵尸与滴滴怪
    public static final DeferredHolder<EntityType<?>, EntityType<Drippler>> DRIPPLER = withAttributes(registerEntity("drippler", "滴滴怪", EntityType.Builder.of(Drippler::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(7).armorToughness(2).attackDamage(15).followRange(64).attackKnockback(0.5).knockbackResistance(0.2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(29).attackDamage(13).armor(7).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(29).attackDamage(13).armor(7).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<EaterOfSouls>> CRIMERA = withAttributes(registerEntity("crimera", "猩红喀迈拉", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(21).armor(4).armorToughness(1).attackDamage(12).followRange(30).attackKnockback(0.5).knockbackResistance(0.1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(21).armor(8).armorToughness(3))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(46).attackDamage(21).armor(8).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<EaterOfSouls>> EATER_OF_SOULS = withAttributes(registerEntityWithEnglishName("eater_of_souls", "噬魂怪", "Eater of Souls", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(21).armor(4).armorToughness(1).attackDamage(12).followRange(30).attackKnockback(0.5).knockbackResistance(0.1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(21).armor(8).armorToughness(3))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(46).attackDamage(21).armor(8).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BloodySpore>> BLOODY_SPORE = withAttributes(registerEntity("bloody_spore", "血腥芽孢", EntityType.Builder.of(BloodySpore::new, MobCategory.MONSTER).sized(1, 1.5f).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(47).armor(3).attackDamage(0).followRange(32).attackKnockback(0).knockbackResistance(0.8).spawnReinforcementsChance(0.01).armorToughness(1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BloodTumor>> BLOOD_TUMORS = withAttributes(registerEntity("blood_tumors", "血瘤", EntityType.Builder.of(BloodTumor::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(5).armor(1).attackDamage(0).followRange(0).attackKnockback(0).knockbackResistance(0).movementSpeed(0).safeFallDistance(100).build());

    /// ------------------------------------------------------------------
    /// 地表人形（`registerLand` 系列）
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> FACE_MONSTER = withAttributes(registerLand("face_monster", "脸怪", 0.75F, 1.95F, BaseWarriorMonster.LandSoundProfile.FACE_MONSTER, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.0, true, BaseWarriorMonster.DoorBehavior.OPEN),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(5).armorToughness(2).attackDamage(13).stepHeight(3.2).jumpStrength(0.8)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(12).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(41).attackDamage(12).armor(5).armorToughness(2))
                    .build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> DARK_CASTER = withAttributes(registerCaster("dark_caster", "暗黑法师", 0.65F, 1.85F, DarkCaster.Profile.DARK_CASTER),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(1).armorToughness(0).attackDamage(11).followRange(20).attackKnockback(1).knockbackResistance(0.82)
                    .projectile(ModEntities.DARK_CASTER_PROJECTILE, projectile -> projectile.damage(byWorldPhase(11, 37, 47)))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(19).armor(2).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(19).armor(2).armorToughness(0))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<FireImp>> FIRE_IMP = withAttributes(registerEntity("fire_imp", "火焰小鬼", EntityType.Builder.of(FireImp::new, MobCategory.MONSTER).sized(0.65F, 1F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(8).armorToughness(3).attackDamage(16).followRange(20).attackKnockback(1).knockbackResistance(0.55)
                    .state(FireImp.CombatState.WALL_SERVANT, state -> state.multiply(Attributes.MAX_HEALTH, 1.5).bonus(Attributes.ARMOR, 10))
                    .projectile(ModEntities.FIRE_IMP_PROJECTILE, projectile -> projectile.damage(byWorldPhase(16, 28, 42)))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(14).armor(8).armorToughness(3))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(41).attackDamage(14).armor(8).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Gastropod>> GASTROPOD = withAttributes(registerEntity("gastropod", "腹足怪", EntityType.Builder.of(Gastropod::new, MobCategory.MONSTER).sized(0.7F, 0.75F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(10).armorToughness(4).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.64)
                    .projectile(ModEntities.GASTROPOD_PROJECTILE, projectile -> projectile.damage(26))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinWarlock>> GOBLIN_WARLOCK = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("goblin_warlock", "哥布林术士", EntityType.Builder.of(GoblinWarlock::new, MobCategory.MONSTER).sized(0.8F, 1.9F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(884).armor(12).armorToughness(5).attackDamage(42).movementSpeed(0.3).followRange(48).knockbackResistance(0.87)
                    .projectile(ModEntities.CHAOS_BALL_PROJECTILE, projectile -> projectile.damage(42).scaleWithAttack(42))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<WaterBoltMimic>> WATER_BOLT_MIMIC = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("water_bolt_mimic", "水矢宝箱怪", EntityType.Builder.of(WaterBoltMimic::new, MobCategory.MONSTER).sized(1.0F, 0.9F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(2).armorToughness(0).attackDamage(11).followRange(32).knockbackResistance(0.82)
                    .projectile(ModEntities.DARK_CASTER_PROJECTILE, projectile -> projectile.attackDamage(1))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(19).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(19).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Corruptor>> CORRUPTOR = withAttributes(registerEntity("corruptor", "腐化者", EntityType.Builder.of(Corruptor::new, MobCategory.MONSTER).sized(2.2F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(120).armor(15).armorToughness(6).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.51)
                    .projectile(ModEntities.VILE_SPIT_PROJECTILE, projectile -> projectile.damage(byWorldPhase(34, 31, 31)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ShadowflameApparition>> SHADOWFLAME_APPARITION = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("shadowflame_apparition", "暗影焰幻鬼", EntityType.Builder.of(ShadowflameApparition::new, MobCategory.MONSTER).sized(0.8F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(80).armor(9).armorToughness(3).attackDamage(21).followRange(48).knockbackResistance(1).build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<Gnome>> GNOME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("gnome", "侏儒", EntityType.Builder.of(Gnome::new, MobCategory.MONSTER).sized(0.5F, 0.8F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(13).armor(0).armorToughness(0).attackDamage(6).movementSpeed(0.3).followRange(24).knockbackResistance(0.1).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(72).attackDamage(23).armor(0).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(28).armor(0).armorToughness(0))
                    .build());

    /// ------------------------------------------------------------------
    /// 血月与邪恶转化：企鹅和金鱼
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<EvilPenguin>> CORRUPT_PENGUIN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("corrupt_penguin", "腐化企鹅", EntityType.Builder.of(EvilPenguin::new, MobCategory.MONSTER).sized(0.7F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(2).armorToughness(0).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.1).movementSpeed(0.25).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(9).armor(2).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(81).attackDamage(19).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<EvilPenguin>> VICIOUS_PENGUIN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("vicious_penguin", "毒企鹅", EntityType.Builder.of(EvilPenguin::new, MobCategory.MONSTER).sized(0.7F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(39).armor(3).armorToughness(1).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.1).movementSpeed(0.25).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(43).attackDamage(10).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(20).armor(5).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> CORRUPT_GOLDFISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("corrupt_goldfish", "腐化金鱼", EntityType.Builder.<Piranha>of((type, level) -> new Piranha(type, level, Piranha.AnimationProfile.GOLDFISH), MobCategory.MONSTER).sized(0.6F, 0.45F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(52).armor(3).armorToughness(1).attackDamage(16).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(14).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(14).armor(3).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> VICIOUS_GOLDFISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("vicious_goldfish", "毒金鱼", EntityType.Builder.<Piranha>of((type, level) -> new Piranha(type, level, Piranha.AnimationProfile.GOLDFISH), MobCategory.MONSTER).sized(0.6F, 0.45F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(58).armor(4).armorToughness(1).attackDamage(17).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(63).attackDamage(15).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(63).attackDamage(15).armor(4).armorToughness(1))
                    .build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<LittleHornet>> LITTLE_HORNET = withAttributes(registerEntity("little_hornet", "小黄蜂", EntityType.Builder.of(LittleHornet::new, MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(4).armor(1).armorToughness(0).attackDamage(5).followRange(20).attackKnockback(0).knockbackResistance(0.2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .armorToughness(0).build());

    /// 沙虫的段体与本体（`registerWorm` 系列见下面的辅助方法）。
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> TOMB_CRAWLER_SEGMENT = registerWormSegment("tomb_crawler_segment", "墓穴爬虫体节");

    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> TOMB_CRAWLER = withAttributes(registerWorm("tomb_crawler", "墓穴爬虫", 2F, 2F, SimpleWormMonster.Role.UNDERGROUND_DESERT, SimpleWormMonster.Anatomy.TOMB_CRAWLER, () -> MonsterEntities.TOMB_CRAWLER_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(0).armorToughness(0).attackDamage(10)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(17).armor(0).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(103).attackDamage(25).armor(0).armorToughness(0))
                    .state(BaseWormMonster.BodySection.HEAD, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(10, 17, 25))
                            .attribute(Attributes.ARMOR, 0)
                            .attribute(Attributes.ARMOR_TOUGHNESS, 0))
                    .state(BaseWormMonster.BodySection.BODY, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(4, 7, 7))
                            .attribute(Attributes.ARMOR, byWorldPhase(6, 11, 11))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(2, 4, 4)))
                    .state(BaseWormMonster.BodySection.TAIL, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(4, 7, 7))
                            .attribute(Attributes.ARMOR, byWorldPhase(7, 13, 13))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(2, 5, 5)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> PIRATE_DECKHAND = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("pirate_deckhand", "海盗水手", 0.6F, 1.85F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.2, true)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(8).armorToughness(3).attackDamage(19).movementSpeed(0.3).followRange(40).knockbackResistance(0.64).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> PIRATE_CORSAIR = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("pirate_corsair", "私船海盗", 0.6F, 1.85F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.3, true)),
            () -> CreatureAttributeBuilder.creature().maxHealth(234).armor(10).armorToughness(4).attackDamage(26).movementSpeed(0.3).followRange(40).knockbackResistance(0.82).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateRangedMonster>> PIRATE_DEADEYE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_deadeye", "海盗神射手", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.DEADEYE), MobCategory.MONSTER).sized(0.6F, 1.85F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(117).armor(7).armorToughness(2).attackDamage(16).movementSpeed(0.3).followRange(40).knockbackResistance(0.73)
                    .projectile(ModEntities.PIRATE_BULLET, projectile -> projectile.damage(25))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateRangedMonster>> PIRATE_CROSSBOWER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_crossbower", "海盗弩手", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.CROSSBOWER), MobCategory.MONSTER).sized(0.6F, 1.85F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(9).armorToughness(4).attackDamage(19).movementSpeed(0.3).followRange(40).knockbackResistance(0.69)
                    .projectile(ModEntities.PIRATE_FLAMING_ARROW, projectile -> projectile.damage(37))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateRangedMonster>> PIRATE_CAPTAIN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_captain", "海盗船长", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.CAPTAIN), MobCategory.MONSTER).sized(0.8F, 2.0F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(1560).armor(14).armorToughness(6).attackDamage(37).movementSpeed(0.15).followRange(40).knockbackResistance(1)
                    .projectile(ModEntities.PIRATE_BULLET, projectile -> projectile.damage(16))
                    .projectile(ModEntities.PIRATE_CANNONBALL, projectile -> projectile.damage(104))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateFlyingMonster>> PIRATE_PARROT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_parrot", "海盗鹦鹉", EntityType.Builder.<PirateFlyingMonster>of((type, level) -> new PirateFlyingMonster(type, level, false), MobCategory.MONSTER).sized(0.5F, 0.7F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(52).armor(6).armorToughness(2).attackDamage(42).movementSpeed(0.3).followRange(40).knockbackResistance(0.37).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateFlyingMonster>> PIRATES_CURSE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntityWithEnglishName("pirates_curse", "海盗诅咒", "Pirate's Curse", EntityType.Builder.<PirateFlyingMonster>of((type, level) -> new PirateFlyingMonster(type, level, true), MobCategory.MONSTER).sized(0.8F, 1.3F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(10).armorToughness(4).attackDamage(39).movementSpeed(0.3).followRange(40).knockbackResistance(0.82).build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------
    ///
    ///

    public static final DeferredHolder<EntityType<?>, EntityType<DemonEye>> DEMON_EYE = withAttributes(registerEntity("demon_eye", "恶魔眼", EntityType.Builder.<DemonEye>of(DemonEye::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(1).armorToughness(0).attackDamage(10).followRange(40).attackKnockback(0).knockbackResistance(0).movementSpeed(0.2).flyingSpeed(0.6)
                    .state(DemonEye.Variant.NORMAL, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(32, 69, 69))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(10, 17, 17))
                            .attribute(Attributes.ARMOR, byWorldPhase(1, 2, 2))
                            .attribute(Attributes.ARMOR_TOUGHNESS, 0)
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 4))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.2))
                    .state(DemonEye.Variant.NORMAL_BIG, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(36, 79, 79))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(11, 19, 19))
                            .attribute(Attributes.ARMOR, byWorldPhase(1, 2, 2))
                            .attribute(Attributes.ARMOR_TOUGHNESS, 0)
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 5, 5))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.1))
                    .state(DemonEye.Variant.CATARACT, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(34, 75, 75))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(10, 17, 17))
                            .attribute(Attributes.ARMOR, byWorldPhase(2, 4, 4))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 1, 1))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 4))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.2))
                    .state(DemonEye.Variant.CATARACT_BIG, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(39, 86, 86))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(11, 19, 19))
                            .attribute(Attributes.ARMOR, byWorldPhase(2, 5, 5))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 1, 1))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 5, 5))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.1))
                    .state(DemonEye.Variant.SLEEPY, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(32, 69, 103))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 15, 23))
                            .attribute(Attributes.ARMOR, byWorldPhase(1, 2, 3))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 0, 1))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 7))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.2))
                    .state(DemonEye.Variant.SLEEPY_BIG, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(35, 76, 114))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(10, 16, 25))
                            .attribute(Attributes.ARMOR, byWorldPhase(1, 2, 3))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 0, 1))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 7))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.1))
                    .state(DemonEye.Variant.DILATED, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(26, 58, 86))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(10, 17, 25))
                            .attribute(Attributes.ARMOR, byWorldPhase(1, 2, 3))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 0, 1))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 7))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.1))
                    .state(DemonEye.Variant.DILATED_SMALL, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(24, 52, 77))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 15, 23))
                            .attribute(Attributes.ARMOR, byWorldPhase(1, 2, 3))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 0, 1))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 7))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.2))
                    .state(DemonEye.Variant.GREEN, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(32, 69, 69))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(11, 19, 19))
                            .attribute(Attributes.ARMOR, 0)
                            .attribute(Attributes.ARMOR_TOUGHNESS, 0)
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 5, 5))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.1))
                    .state(DemonEye.Variant.GREEN_SMALL, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(27, 59, 59))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 16, 16))
                            .attribute(Attributes.ARMOR, 0)
                            .attribute(Attributes.ARMOR_TOUGHNESS, 0)
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 4))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.2))
                    .state(DemonEye.Variant.PURPLE, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(32, 69, 103))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(8, 13, 20))
                            .attribute(Attributes.ARMOR, byWorldPhase(2, 4, 6))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 1, 2))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(1, 3, 5))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.2))
                    .state(DemonEye.Variant.PURPLE_BIG, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(35, 76, 114))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(8, 15, 22))
                            .attribute(Attributes.ARMOR, byWorldPhase(2, 4, 6))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 1, 2))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(1, 4, 6))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.1))
                    .state(DemonEye.Variant.OWL, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(39, 86, 86))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(9, 15, 15))
                            .attribute(Attributes.ARMOR, byWorldPhase(3, 6, 6))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(1, 2, 2))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 4, 4))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.2))
                    .state(DemonEye.Variant.SPACESHIP, state -> state
                            .attribute(Attributes.MAX_HEALTH, byWorldPhase(32, 69, 69))
                            .attribute(Attributes.ATTACK_DAMAGE, byWorldPhase(11, 19, 19))
                            .attribute(Attributes.ARMOR, byWorldPhase(2, 4, 4))
                            .attribute(Attributes.ARMOR_TOUGHNESS, byWorldPhase(0, 1, 1))
                            .attribute(LibAttributes.getArmorPenetration(), byWorldPhase(2, 5, 5))
                            .attribute(Attributes.MOVEMENT_SPEED, 0.2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> POSSESS_ARMOR = withAttributes(registerHumanoidLand("possess_armor", "装甲幻影魔", 1F, 2F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandSoundProfile.POSSESSED_ARMOR, BaseWarriorMonster.LandAnimationProfile.NONE),
            () -> CreatureAttributeBuilder.creature().maxHealth(136).armor(13).armorToughness(5).attackDamage(29).followRange(32).attackKnockback(1).knockbackResistance(0.64).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Wraith>> WRAITH = withAttributes(registerEntity("wraith", "幻灵", EntityType.Builder.of(Wraith::new, MobCategory.MONSTER).sized(1F, 2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(84).armor(8).armorToughness(3).attackDamage(34).followRange(32).attackKnockback(1).knockbackResistance(0.37).gravity(0).build());

    /// 空岛与高空：鸟妖和飞龙
    public static final DeferredHolder<EntityType<?>, EntityType<Harpy>> HARPY = withAttributes(registerEntity("harpy", "鸟妖", EntityType.Builder.of(Harpy::new, MobCategory.MONSTER).sized(1f, 2f).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(52).armor(4).armorToughness(1).attackDamage(13).followRange(50)
                    .projectile(ModEntities.HARPY_FEATHER, projectile -> projectile.damage(16))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(12).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(12).armor(4).armorToughness(1))
                    .build());

    /// 地下与洞穴：法师
    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> TIM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerCaster("tim", "蒂姆", 1.0F, 3.2F, DarkCaster.Profile.TIM)),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(2).armorToughness(0).attackDamage(11).knockbackResistance(0.46).followRange(32)
                    .projectile(ModEntities.CHAOS_BALL_PROJECTILE, projectile -> projectile.damage(byWorldPhase(11, 37, 47)))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(115).attackDamage(9).armor(2).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(115).attackDamage(9).armor(2).armorToughness(0))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> RUNE_WIZARD = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerCaster("rune_wizard", "符文巫师", 1.0F, 3.2F, DarkCaster.Profile.RUNE_WIZARD)),
            () -> CreatureAttributeBuilder.creature().maxHealth(312).armor(14).armorToughness(6).attackDamage(104).knockbackResistance(0.73).followRange(32)
                    .projectile(ModEntities.RUNE_BLAST, projectile -> projectile.damage(42))
                    .build());

    /// 地下与洞穴：岩石巨人及稀有敌怪
    public static final DeferredHolder<EntityType<?>, EntityType<RockGolem>> ROCK_GOLEM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("rock_golem", "岩石巨人", EntityType.Builder.of(RockGolem::new, MobCategory.MONSTER).sized(1.5F, 2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(520).armor(16).armorToughness(7).attackDamage(45).knockbackResistance(0.91).movementSpeed(0.2).followRange(32)
                    .projectile(ModEntities.THROWN_ROCK, projectile -> projectile.damage(21))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Nymph>> NYMPH = withAttributes(registerEntity("nymph", "宁芙", EntityType.Builder.of(Nymph::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(8).armorToughness(3).attackDamage(19).followRange(15).attackKnockback(1).knockbackResistance(0.5)
                    .state(Nymph.CombatState.PURSUING, state -> state.bonus(Attributes.MOVEMENT_SPEED, 0.25))
                    .build());

    /// 蜘蛛洞：爬墙蜘蛛与黑隐士
    public static final DeferredHolder<EntityType<?>, EntityType<ClimbingSpider>> WALL_CREEPER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("wall_creeper", "爬墙蜘蛛", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.WALL), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(42).armor(5).armorToughness(2).attackDamage(16).followRange(32).knockbackResistance(0.78).movementSpeed(0.23)
                    .state(ClimbingSpider.CombatState.SPITTING, state -> state.attackInterval(60))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(14).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(46).attackDamage(14).armor(5).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ClimbingSpider>> BLACK_RECLUSE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("black_recluse", "黑隐士", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.BLACK_RECLUSE), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(18).armorToughness(8).attackDamage(47).followRange(32).knockbackResistance(0.78).movementSpeed(0.28)
                    .state(ClimbingSpider.CombatState.CLIMBING, state -> state.multiply(Attributes.ATTACK_DAMAGE, 10.0 / 9.0))
                    .state(ClimbingSpider.CombatState.SPITTING, state -> state.attackInterval(60))
                    .projectile(ModEntities.SPIDER_WEB_SPIT, projectile -> projectile.damage(10).speed(0.8).lifetime(80))
                    .build());

    /// 大理石洞：装甲步兵
    public static final DeferredHolder<EntityType<?>, EntityType<Hoplite>> HOPLITE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("hoplite", "装甲步兵", EntityType.Builder.of(Hoplite::new, MobCategory.MONSTER).sized(1.0F, 2.7F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(5).armorToughness(2).attackDamage(12).knockbackResistance(0.64).movementSpeed(0.23).followRange(32)
                    .projectile(ModEntities.HOPLITE_JAVELIN, projectile -> projectile.damage(19))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(10).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(81).attackDamage(21).armor(9).armorToughness(4))
                    .build());

    /// 丛林：黄蜂及幼蜂
    public static final DeferredHolder<EntityType<?>, EntityType<Hornet>> HORNET = withAttributes(registerEntity("hornet", "黄蜂", EntityType.Builder.<Hornet>of(Hornet::new, MobCategory.MONSTER).sized(0.8F, 1.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(25).armor(6).armorToughness(2).attackDamage(14).followRange(32).attackKnockback(0).knockbackResistance(0.55).movementSpeed(0.5)
                    .projectile(ModEntities.HORNET_STINGER, projectile -> projectile.damage(11))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(28).attackDamage(12).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(55).attackDamage(24).armor(11).armorToughness(4))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Hornet>> MOSS_HORNET = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("moss_hornet", "苔藓黄蜂", EntityType.Builder.<Hornet>of(Hornet::new, MobCategory.MONSTER).sized(0.8F, 1.8F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(10).armorToughness(4).attackDamage(37).followRange(32).attackKnockback(0).knockbackResistance(0.55).movementSpeed(0.5)
                    .projectile(ModEntities.HORNET_STINGER, projectile -> projectile.damage(32))
                    .build());

    /// 丛林：食人植物
    public static final DeferredHolder<EntityType<?>, EntityType<Snatcher>> SNATCHER = withAttributes(registerSnatcher("snatcher", "抓人草", Snatcher.Profile.SNATCHER),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(5).armorToughness(2).attackDamage(13).followRange(20).attackKnockback(1).knockbackResistance(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(35).attackDamage(12).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(23).armor(9).armorToughness(4))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Snatcher>> MAN_EATER = withAttributes(registerSnatcher("man_eater", "食人怪", Snatcher.Profile.MAN_EATER),
            () -> CreatureAttributeBuilder.creature().maxHealth(58).armor(5).armorToughness(2).attackDamage(18).followRange(20).attackKnockback(1).knockbackResistance(1).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(63).attackDamage(16).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(63).attackDamage(16).armor(5).armorToughness(2))
                    .build());

    /// 丛林：蜘蛛、蹦跳兽与陆龟
    public static final DeferredHolder<EntityType<?>, EntityType<ClimbingSpider>> JUNGLE_CREEPER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("jungle_creeper", "丛林蜘蛛", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.JUNGLE), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(13).armorToughness(5).attackDamage(52).followRange(32).knockbackResistance(0.78).movementSpeed(0.28)
                    .state(ClimbingSpider.CombatState.SPITTING, state -> state.attackInterval(60))
                    .projectile(ModEntities.SPIDER_WEB_SPIT, projectile -> projectile.damage(10).speed(0.8).lifetime(80))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GiantTortoise>> GIANT_TORTOISE = withAttributes(registerEntity("giant_tortoise", "巨型陆龟", EntityType.Builder.of(GiantTortoise::new, MobCategory.MONSTER).sized(2.25F, 1.85F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(245).armor(14).armorToughness(6).attackDamage(42).followRange(48).attackKnockback(1).knockbackResistance(0.73).movementSpeed(0.2)
                    .state(GiantTortoise.Phase.SPINNING, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.8).multiplyBase(Attributes.ARMOR, 2))
                    .state(GiantTortoise.Phase.RETRACTING, state -> state.duration(10))
                    .state(GiantTortoise.Phase.WINDING_UP, state -> state.duration(12))
                    .state(GiantTortoise.Phase.EMERGING, state -> state.duration(10))
                    .state(GiantTortoise.Phase.WALK, state -> state.attackInterval(20))
                    .build());

    /// 丛林水域：食人鱼与巨骨舌鱼
    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> PIRANHA = withAttributes(registerEntity("piranha", "食人鱼", EntityType.Builder.<Piranha>of(Piranha::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(16).armor(1).armorToughness(0).attackDamage(13).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(35).attackDamage(23).armor(2).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(35).attackDamage(23).armor(2).armorToughness(0))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Arapaima>> ARAPAIMA = withAttributes(registerEntity("arapaima", "巨骨舌鱼", EntityType.Builder.of(Arapaima::new, MobCategory.MONSTER).sized(2.2F, 0.7F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(104).armor(14).armorToughness(6).attackDamage(39).followRange(32).movementSpeed(1.2).attackKnockback(0.1).knockbackResistance(0.1).build());

    /// 冰雪：冰雪精与鱼人
    public static final DeferredHolder<EntityType<?>, EntityType<IceElemental>> ICE_ELEMENTAL = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_elemental", "冰雪精", EntityType.Builder.of(IceElemental::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(9).armorToughness(4).attackDamage(29).knockbackResistance(0.46).movementSpeed(0.18).followRange(40)
                    .projectile(ModEntities.FROST_BLAST, projectile -> projectile.damage(47))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<FrostFighter>> ICY_MERMAN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("icy_merman", "冰雪人鱼", EntityType.Builder.<FrostFighter>of((type, level) -> new FrostFighter(type, level, FrostFighter.Kind.MERMAN), MobCategory.MONSTER).sized(0.8F, 2.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(146).armor(14).armorToughness(6).attackDamage(32).knockbackResistance(0.55).movementSpeed(0.23).followRange(32)
                    .state(FrostFighter.CombatState.SHOOTING, state -> state.moveSpeed(1).attackInterval(35))
                    .state(FrostFighter.CombatState.WOUNDED, state -> state.moveSpeed(2).attackInterval(10))
                    .projectile(ModEntities.ICEWATER_SPIT, projectile -> projectile.damage(39).speed(0.8).inaccuracy(0).lifetime(100))
                    .build());

    /// 地下沙漠：蚁狮及幼虫
    public static final DeferredHolder<EntityType<?>, EntityType<Antlion>> ANTLION = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("antlion", "蚁狮", EntityType.Builder.of(Antlion::new, MobCategory.MONSTER).sized(1.2F, 0.8F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(24).armor(3).armorToughness(1).attackDamage(6).movementSpeed(0.0).knockbackResistance(1.0).followRange(24).projectile(ModEntities.ANTLION_SAND_BALL, projectile -> projectile.damage(10).speed(1.1).lifetime(120))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(52).attackDamage(9).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(77).attackDamage(14).armor(9).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AntlionCharger>> ANTLION_CHARGER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("antlion_charger", "蚁狮马", EntityType.Builder.of(AntlionCharger::new, MobCategory.MONSTER).sized(1.2F, 0.8F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(42).armor(5).armorToughness(2).attackDamage(13).movementSpeed(0.25).knockbackResistance(0.55).followRange(32).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(12).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(46).attackDamage(12).armor(5).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AntlionSwarmer>> ANTLION_SWARMER = withAttributes(registerEntity("antlion_swarmer", "蚁狮蜂", EntityType.Builder.of(AntlionSwarmer::new, MobCategory.MONSTER).sized(3.0F, 1.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(4).armorToughness(1).attackDamage(16).followRange(32).attackKnockback(1).knockbackResistance(0.55)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(35).attackDamage(14).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(35).attackDamage(14).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AntlionSwarmer>> GIANT_ANTLION_SWARMER = withAttributes(registerEntity("giant_antlion_swarmer", "巨型蚁狮蜂", EntityType.Builder.of(AntlionSwarmer::new, MobCategory.MONSTER).sized(3.5F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(47).armor(6).armorToughness(2).attackDamage(18).followRange(32).attackKnockback(1).knockbackResistance(0.73)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(52).attackDamage(16).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(52).attackDamage(16).armor(6).armorToughness(2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DesertSpirit>> DESERT_SPIRIT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("desert_spirit", "沙漠幽魂", EntityType.Builder.of(DesertSpirit::new, MobCategory.MONSTER).sized(1.0F, 2.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(9).armorToughness(4).attackDamage(21).knockbackResistance(1.0).followRange(32)
                    .projectile(ModEntities.DESERT_SPIRIT_CURSE, projectile -> projectile.damage(23))
                    .build());

    /// 腐化：爬藤怪与宝箱怪
    public static final DeferredHolder<EntityType<?>, EntityType<SpittingPlant>> CLINGER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("clinger", "爬藤怪", EntityType.Builder.<SpittingPlant>of((type, level) -> new SpittingPlant(type, level, Snatcher.Profile.CLINGER), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(167).armor(14).armorToughness(6).attackDamage(37).followRange(32).knockbackResistance(0.82)
                    .projectile(ModEntities.CLINGER_FLAME, projectile -> projectile.damage(18))
                    .build());

    /// 猩红水域：血蛭与血水母
    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> BLOOD_FEEDER = withAttributes(registerEntity("blood_feeder", "嗜血怪", EntityType.Builder.<Piranha>of(Piranha::new, MobCategory.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(78).armor(9).armorToughness(4).attackDamage(26).followRange(32).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.10).build());

    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> BLOOD_JELLY = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("blood_jelly", "血水母", EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, JellyFish.Profile.ROUTINE), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(78).armor(9).armorToughness(4).attackDamage(39).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Pixie>> PIXIE = withAttributes(registerEntity("pixie", "妖精", EntityType.Builder.of(Pixie::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(78).armor(9).armorToughness(4).attackDamage(29).followRange(16).attackKnockback(1).knockbackResistance(0.46).build());

    /// 神圣：独角兽与腹足怪
    public static final DeferredHolder<EntityType<?>, EntityType<Unicorn>> UNICORN = withAttributes(registerEntity("unicorn", "独角兽", EntityType.Builder.of(Unicorn::new, MobCategory.MONSTER).sized(1.4F, 2.25F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(14).armorToughness(6).attackDamage(34).followRange(64).attackKnockback(1).knockbackResistance(0.82).movementSpeed(0.35)
                    .state(Unicorn.MovementState.PURSUING, state -> state.chargeSpeed(0.36).duration(40))
                    .state(Unicorn.MovementState.RECOVERING, state -> state.duration(12))
                    .build());

    /// 地下神圣：附魔剑、混沌精与宝箱怪
    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedSword>> ENCHANTED_SWORD = withAttributes(registerEntity("enchanted_sword_monster", "魔法剑", EntityType.Builder.of(EnchantedSword::new, MobCategory.MONSTER).sized(0.35F, 1.4F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(9).armorToughness(3).attackDamage(42).followRange(48).attackKnockback(1).knockbackResistance(0.82)
                    .state(EnchantedSword.CombatState.WINDUP, state -> state.duration(40))
                    .state(EnchantedSword.CombatState.CHARGING, state -> state.chargeSpeed(0.8))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ChaosElemental>> CHAOS_ELEMENTAL = withAttributes(registerEntity("chaos_elemental", "混沌精", EntityType.Builder.of(ChaosElemental::new, MobCategory.MONSTER).sized(0.7F, 1.9F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(193).armor(14).armorToughness(6).attackDamage(21).followRange(48).attackKnockback(1).knockbackResistance(0.64).build());

    /// 发光蘑菇：真菌球怪
    public static final DeferredHolder<EntityType<?>, EntityType<Snatcher>> FUNGI_BULB = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerSnatcher("fungi_bulb", "真菌球怪", Snatcher.Profile.FUNGI_BULB)),
            () -> CreatureAttributeBuilder.creature().maxHealth(47).armor(2).armorToughness(0).attackDamage(13).followRange(20).knockbackResistance(1).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(52).attackDamage(11).armor(2).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(103).attackDamage(23).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<SpittingPlant>> GIANT_FUNGI_BULB = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("giant_fungi_bulb", "巨型真菌球怪", EntityType.Builder.<SpittingPlant>of((type, level) -> new SpittingPlant(type, level, Snatcher.Profile.GIANT_FUNGI_BULB), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(9).armorToughness(4).attackDamage(37).followRange(40).knockbackResistance(1)
                    .projectile(ModEntities.FUNGI_SPORE, projectile -> projectile.damage(byWorldPhase(42, 42, 37)))
                    .build());

    /// 发光蘑菇水域：蘑菇水母
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> FUNGO_FISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("fungo_fish", "蘑菇鱼", EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, JellyFish.Profile.FUNGO), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(73).armor(9).armorToughness(4).attackDamage(47).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> PINK_JELLYFISH = withAttributes(registerJellyFish("pink_jellyfish", "粉水母", JellyFish.Profile.ROUTINE),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(37).armor(3).armorToughness(1).attackDamage(16).followRange(16).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(41).attackDamage(14).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(41).attackDamage(14).armor(3).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> NECROMANCER = withAttributes(registerCaster("necromancer", "死灵法师", 0.7F, 1.9F, DarkCaster.Profile.NECROMANCER),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(9).armorToughness(3).attackDamage(26).followRange(48).attackKnockback(1).knockbackResistance(0.51)
                    .projectile(ModEntities.SHADOW_BEAM_PROJECTILE, projectile -> projectile.damage(25))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> DIABOLIST = withAttributes(registerCaster("diabolist", "魔教徒", 0.7F, 1.9F, DarkCaster.Profile.DIABOLIST),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(6).armorToughness(2).attackDamage(26).followRange(48).attackKnockback(1).knockbackResistance(0.73)
                    .projectile(ModEntities.INFERNO_BOLT_PROJECTILE, projectile -> projectile.damage(34))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> RAGGED_CASTER = withAttributes(registerCaster("ragged_caster", "褴褛邪教徒法师", 0.7F, 1.9F, DarkCaster.Profile.RAGGED_CASTER),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(9).armorToughness(4).attackDamage(21).followRange(48).attackKnockback(1).knockbackResistance(0.73)
                    .projectile(ModEntities.LOST_SOUL_PROJECTILE, projectile -> projectile.damage(34))
                    .build());

    /// 地牢：圣骑士与骷髅李
    public static final DeferredHolder<EntityType<?>, EntityType<Paladin>> PALADIN = withAttributes(registerEntity("paladin", "圣骑士", EntityType.Builder.of(Paladin::new, MobCategory.MONSTER).sized(1.2F, 2.4F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(2600).armor(23).armorToughness(8).attackDamage(52).followRange(64).attackKnockback(1).knockbackResistance(1)
                    .projectile(ModEntities.PALADIN_HAMMER_PROJECTILE, projectile -> projectile.damage(47))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ChargingMonster>> BONE_LEE = withAttributes(registerCharger("bone_lee", "骷髅李", 0.7F, 1.9F, 0.82, 6),
            () -> CreatureAttributeBuilder.creature().maxHealth(520).armor(19).armorToughness(8).attackDamage(47).followRange(48).attackKnockback(1).knockbackResistance(0.95).movementSpeed(0.38).build());

    /// 地狱：恶魔与火小鬼
    public static final DeferredHolder<EntityType<?>, EntityType<Demon>> DEMON = withAttributes(registerEntity("demon", "恶魔", EntityType.Builder.of(Demon::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(63).armor(4).armorToughness(1).attackDamage(17).followRange(16).attackKnockback(1).knockbackResistance(0.28)
                    .projectile(ModEntities.HOSTILE_DEMON_SCYTHE, projectile -> projectile.damage(22))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(15).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(15).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Demon>> VOODOO_DEMON = withAttributes(registerEntity("voodoo_demon", "巫毒恶魔", EntityType.Builder.of(Demon::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.creature().maxHealth(73).armor(4).armorToughness(1).attackDamage(17).followRange(16).attackKnockback(1).knockbackResistance(0.28)
                    .projectile(ModEntities.HOSTILE_DEMON_SCYTHE, projectile -> projectile.damage(22))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(81).attackDamage(15).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(81).attackDamage(15).armor(4).armorToughness(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<RedDevil>> RED_DEVIL = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("red_devil", "红魔鬼", EntityType.Builder.<RedDevil>of(RedDevil::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10).fireImmune())),
            () -> CreatureAttributeBuilder.creature().maxHealth(312).armor(18).armorToughness(8).attackDamage(26).followRange(32).knockbackResistance(0.55)
                    .projectile(ModEntities.UNHOLY_TRIDENT, projectile -> projectile.damage(84))
                    .build());

    /// 血月垂钓：游荡眼球怪与僵尸鱼人
    public static final DeferredHolder<EntityType<?>, EntityType<FlyingFishMonster>> WANDERING_EYE_FISH = withAttributes(registerFlyingFish("wandering_eye_fish", "游荡眼球怪鱼", 1.2F, 1.2F, new FlyingFishMonster.PursuitProfile(0.98, 2.2, 0.01, 10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(9).armorToughness(3).attackDamage(19).followRange(60).attackKnockback(1).knockbackResistance(1).movementSpeed(2.2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<ZombieMerman>> ZOMBIE_MERMAN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("zombie_merman", "僵尸人鱼", EntityType.Builder.<ZombieMerman>of(ZombieMerman::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(9).armorToughness(4).attackDamage(21).followRange(48).knockbackResistance(1).movementSpeed(0.2)
                    .state(ZombieMerman.MovementState.SWIMMING, state -> state.moveSpeed(0.6))
                    .state(ZombieMerman.MovementState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 3.5))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AngryNimbus>> ANGRY_NIMBUS = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_nimbus", "愤怒雨云怪", EntityType.Builder.of(AngryNimbus::new, MobCategory.MONSTER).sized(2.0F, 1.4F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(11).armorToughness(4).attackDamage(26).knockbackResistance(0.73).movementSpeed(0.3).flyingSpeed(0.5).followRange(48)
                    .projectile(ModEntities.NIMBUS_RAIN, projectile -> projectile.damage(21))
                    .build());

    /// 暴风雪：冰雪巨人
    public static final DeferredHolder<EntityType<?>, EntityType<FrostFighter>> ICE_GOLEM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_golem", "冰雪巨人", EntityType.Builder.<FrostFighter>of((type, level) -> new FrostFighter(type, level, FrostFighter.Kind.GOLEM), MobCategory.MONSTER).sized(3F, 8.0F).clientTrackingRange(12))),
            () -> CreatureAttributeBuilder.creature().maxHealth(2080).armor(15).armorToughness(6).attackDamage(32).knockbackResistance(0.96).movementSpeed(0.2).followRange(48)
                    .state(FrostFighter.CombatState.SHOOTING, state -> state.moveSpeed(0.6).attackInterval(20, 309))
                    .state(FrostFighter.CombatState.WOUNDED, state -> state.moveSpeed(2).attackInterval(10))
                    .projectile(ModEntities.FROST_BEAM, projectile -> projectile.damage(34).speed(2.5).inaccuracy(0).lifetime(100))
                    .build());

    /// 沙尘暴：愤怒翻滚怪
    public static final DeferredHolder<EntityType<?>, EntityType<AngryTumbler>> ANGRY_TUMBLER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_tumbler", "愤怒翻滚怪", EntityType.Builder.<AngryTumbler>of(AngryTumbler::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(3).armorToughness(1).attackDamage(16).knockbackResistance(0.28).movementSpeed(0.25).followRange(32)
                    .state(AngryTumbler.MovementState.PURSUING, state -> state.chargeSpeed(2).duration(25))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(29).attackDamage(14).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(28).armor(6).armorToughness(2))
                    .build());

    /// 沙尘暴：普通、腐化、猩红与神圣沙鲨
    public static final DeferredHolder<EntityType<?>, EntityType<SandShark>> SAND_SHARK = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("sand_shark", "沙鲨", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(188).armor(9).armorToughness(4).attackDamage(26).knockbackResistance(0.19).followRange(40).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SandShark>> BONE_BITER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("bone_biter", "噬骨沙鲨", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(198).armor(11).armorToughness(4).attackDamage(32).knockbackResistance(0.28).followRange(40).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SandShark>> FLESH_REAVER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("flesh_reaver", "戮血沙鲨", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(10).armorToughness(4).attackDamage(34).knockbackResistance(0.28).followRange(40).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SandShark>> CRYSTAL_THRESHER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("crystal_thresher", "水晶沙鲨", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(234).armor(12).armorToughness(5).attackDamage(29).knockbackResistance(0.37).followRange(40).build());

    /// 哥布林入侵：法师及召唤体
    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> GOBLIN_SORCERER = withAttributes(registerCaster("goblin_sorcerer", "哥布林巫士", 0.65F, 1.85F, DarkCaster.Profile.GOBLIN_SORCERER),
            () -> CreatureAttributeBuilder.creature().maxHealth(21).armor(1).armorToughness(0).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.46)
                    .projectile(ModEntities.CHAOS_BALL_PROJECTILE, projectile -> projectile.damage(byWorldPhase(11, 37, 47)))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(19).armor(2).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(28).armor(3).armorToughness(1))
                    .build());

    /// 火星人事件
    public static final DeferredHolder<EntityType<?>, EntityType<MartianProbe>> MARTIAN_PROBE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("martian_probe", "火星探测器", EntityType.Builder.of(MartianProbe::new, MobCategory.MONSTER).sized(1.4F, 1.0F).clientTrackingRange(12))),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(3).armorToughness(1).attackDamage(3).followRange(48).movementSpeed(0.25).flyingSpeed(0.65).knockbackResistance(0.4).build());
    public static final DeferredHolder<EntityType<?>, EntityType<MartianEngineer>> MARTIAN_ENGINEER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("martian_engineer", "火星工程师", EntityType.Builder.of(MartianEngineer::new, MobCategory.MONSTER).sized(0.8F, 2.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(16).armorToughness(6).attackDamage(21).followRange(40).movementSpeed(0.24).knockbackResistance(0.55).build());
    public static final DeferredHolder<EntityType<?>, EntityType<TeslaTurret>> TESLA_TURRET = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("tesla_turret", "特斯拉炮塔", EntityType.Builder.of(TeslaTurret::new, MobCategory.MONSTER).sized(1.0F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(18).armorToughness(8).attackDamage(6).followRange(36).movementSpeed(0.01).knockbackResistance(1.0)
                    .projectile(ModEntities.MARTIAN_ELECTRIC_BOLT, projectile -> projectile.damage(30))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<RayGunner>> RAY_GUNNER = withAttributes(registerEntity("ray_gunner", "激光枪手", EntityType.Builder.of(RayGunner::new, MobCategory.MONSTER).sized(0.8F, 1.6F).clientTrackingRange(12)),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(12).armorToughness(5).attackDamage(26).followRange(44).movementSpeed(0.25).knockbackResistance(0.65)
                    .state(RayGunner.CombatState.MOUNTED, state -> state.attackDamage(34).armor(14).attribute(Attributes.ARMOR_TOUGHNESS, 6)
                            .attribute(LibAttributes.getArmorPenetration(), 10).knockbackResistance(0.0))
                    .projectile(ModEntities.MONSTER_LASER, projectile -> projectile.damage(32))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<Scutlix>> SCUTLIX = withAttributes(registerEntity("scutlix", "鳞甲怪", EntityType.Builder.of(Scutlix::new, MobCategory.MONSTER).sized(1.8F, 1.7F).clientTrackingRange(12)),
            () -> CreatureAttributeBuilder.creature().maxHealth(312).armor(14).armorToughness(6).attackDamage(45).followRange(44).movementSpeed(0.32).knockbackResistance(0.9)
                    .state(Scutlix.CombatState.MOUNTED, state -> state.attackDamage(45))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MartianOfficer>> MARTIAN_OFFICER = withAttributes(registerEntity("martian_officer", "火星军官", EntityType.Builder.of(MartianOfficer::new, MobCategory.MONSTER).sized(0.85F, 2.05F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(23).armorToughness(8).attackDamage(39).followRange(36).movementSpeed(0.25).knockbackResistance(0.75)
                    .state(MartianOfficer.ShieldState.BARRIER, state -> state.attribute(Attributes.MAX_HEALTH, 260).armor(9).attribute(Attributes.ARMOR_TOUGHNESS, 4))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MartianWalker>> MARTIAN_WALKER = withAttributes(registerEntity("martian_walker", "火星走妖", EntityType.Builder.of(MartianWalker::new, MobCategory.MONSTER).sized(2.8F, 7.5F).clientTrackingRange(16)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1040).armor(18).armorToughness(8).attackDamage(32).followRange(48).movementSpeed(0.55).knockbackResistance(1)
                    .projectile(ModEntities.MONSTER_LASER, projectile -> projectile.damage(37))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BrainScrambler>> BRAIN_SCRAMBLER = withAttributes(registerEntity("brain_scrambler", "扰脑怪", EntityType.Builder.of(BrainScrambler::new, MobCategory.MONSTER).sized(0.8F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(12).armorToughness(5).attackDamage(26).followRange(40).movementSpeed(0.24).knockbackResistance(0.65)
                    .projectile(ModEntities.MONSTER_LASER, projectile -> projectile.damage(25))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<GrayGrunt>> GRAY_GRUNT = withAttributes(registerEntity("gray_grunt", "灰咕噜", EntityType.Builder.of(GrayGrunt::new, MobCategory.MONSTER).sized(0.8F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(390).armor(14).armorToughness(6).attackDamage(42).followRange(36).movementSpeed(0.28).knockbackResistance(1.0).build());
    public static final DeferredHolder<EntityType<?>, EntityType<Gigazapper>> GIGAZAPPER = withAttributes(registerEntity("gigazapper", "电击怪", EntityType.Builder.of(Gigazapper::new, MobCategory.MONSTER).sized(0.9F, 2.1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(312).armor(18).armorToughness(7).attackDamage(39).followRange(36).movementSpeed(0.26).knockbackResistance(0.7)
                    .projectile(ModEntities.ELECTRIC_SPEAR, projectile -> projectile.damage(26))
                    .build());

    /// Boss 附属生物：克苏鲁之脑

    /// Boss 附属生物：血肉墙与血肉山

    public static final DeferredHolder<EntityType<?>, EntityType<GigazapperAf2>> GIGAZAPPER_AF2 = withAttributes(registerEntity("gigazapper_af2", "电击怪 - AF2", EntityType.Builder.of(GigazapperAf2::new, MobCategory.MONSTER).sized(0.9F, 2.1F).clientTrackingRange(16)),
            () -> gigazapperAttributes(72));

    private static CreatureAttributeBuilder.Definition gigazapperAttributes(double followRange) {
        return CreatureAttributeBuilder.creature().maxHealth(312).armor(18).attackDamage(39)
                .add(LibAttributes.getArmorPenetration(), 12).armorToughness(7)
                .followRange(followRange).movementSpeed(0.26).knockbackResistance(0.7)
                .state(Gigazapper.AttackType.SPEAR, state -> state.attackDamage(26))
                .build();
    }

    public static final DeferredHolder<EntityType<?>, EntityType<DungeonSpirit>> DUNGEON_SPIRIT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("dungeon_spirit", "地牢幽魂", EntityType.Builder.of(DungeonSpirit::new, MobCategory.MONSTER).sized(0.8F, 0.7F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(14).armorToughness(6).attackDamage(37).movementSpeed(0.35).followRange(32).knockbackResistance(0.8).gravity(0).build());

    public static final DeferredHolder<EntityType<?>, EntityType<VisualNeuron>> VISUAL_NEURON = withAttributes(registerEntity("visual_neuron", "视神经元", EntityType.Builder.of(VisualNeuron::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(45).armor(5).armorToughness(2).attackDamage(10).followRange(0).attackKnockback(0).knockbackResistance(0.1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<TheHungry>> THE_HUNGRY = withAttributes(registerEntity("the_hungry", "饿鬼", EntityType.Builder.of(TheHungry::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(88).armor(8).armorToughness(3).attackDamage(16).followRange(32).attackKnockback(0.75).knockbackResistance(1)
                    .state(TheHungry.FeedingState.WOUNDED_OWNER, state -> state.attackDamage(24).armor(9).attribute(Attributes.ARMOR_TOUGHNESS, 4))
                    .state(TheHungry.FeedingState.CRITICAL_OWNER, state -> state.attackDamage(32).armor(14).attribute(Attributes.ARMOR_TOUGHNESS, 6))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<HillHungry>> HILL_HUNGRY = withAttributes(registerEntity("hill_hungry", "饿鬼", EntityType.Builder.of(HillHungry::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(87).armor(8).attackDamage(15).followRange(32).attackKnockback(0.75).knockbackResistance(1).armorToughness(3).build());

    private static DeferredHolder<EntityType<?>, EntityType<BaseSlime>> registerSlime(String name, boolean passiveByDay, int size, boolean honeyConvertible) {
        return registerEntity(name, EntityType.Builder.<BaseSlime>of((entityType, level) -> new BaseSlime(entityType, level, passiveByDay, size, honeyConvertible), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    }

    /// 地表与森林：史莱姆（passiveByDay 控制白天是否被动）
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> GREEN_SLIME = withAttributes(registerSlime("green_slime", "绿史莱姆", true, 2, true),
            () -> CreatureAttributeBuilder.slime().maxHealth(8).armor(0).armorToughness(0).attackDamage(4)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(64).attackDamage(23).armor(0).armorToughness(0))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(88).attackDamage(31).armor(0).armorToughness(0))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> BLUE_SLIME = withAttributes(registerSlime("blue_slime", "蓝史莱姆", true, 2, true),
            () -> CreatureAttributeBuilder.slime().maxHealth(13).armor(1).armorToughness(0).attackDamage(4)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(72).attackDamage(17).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(20).armor(6).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> PURPLE_SLIME = withAttributes(registerSlime("purple_slime", "紫史莱姆", true, 2, true),
            () -> CreatureAttributeBuilder.slime().maxHealth(21).armor(3).armorToughness(1).attackDamage(7)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(46).attackDamage(11).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(17).armor(9).armorToughness(3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> PINK_SLIME = withAttributes(registerSlime("pink_slime", "粉色史莱姆", true, 1),
            () -> CreatureAttributeBuilder.slime().maxHealth(78).armor(3).armorToughness(1).attackDamage(3)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(86).attackDamage(3).armor(3).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(172).attackDamage(5).armor(5).armorToughness(2))
                    .build());

    /// 地表与森林：金史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<GoldenSlime>> GOLDEN_SLIME = withAttributes(registerEntity("golden_slime", "金史莱姆", EntityType.Builder.of(GoldenSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(156).armor(3).armorToughness(1).attackDamage(3).phase(GamePhase.PLANTERA, phase -> phase.maxHealth(172).attackDamage(3).armor(3).armorToughness(1))
                    .build());

    /// 沼泽：史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> SWAMP_SLIME = withAttributes(registerSlime("swamp_slime", "沼泽史莱姆", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(1).attackDamage(5).build());

    /// 地下与洞穴：史莱姆及分裂体
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> RED_SLIME = withAttributes(registerSlime("red_slime", "红史莱姆", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(19).armor(2).armorToughness(0).attackDamage(7)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(60).attackDamage(17).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(81).attackDamage(23).armor(8).armorToughness(3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> YELLOW_SLIME = withAttributes(registerSlime("yellow_slime", "黄史莱姆", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(24).armor(4).armorToughness(1).attackDamage(8)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(52).attackDamage(14).armor(7).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(77).attackDamage(21).armor(10).armorToughness(4))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> BLACK_SLIME = withAttributes(registerEntity("black_slime", "黑史莱姆", EntityType.Builder.<BaseSlime>of((type, level) -> new BaseSlime(type, level, false, 2, false, true), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(24).armor(2).armorToughness(0).attackDamage(8)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(52).attackDamage(14).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(77).attackDamage(21).armor(6).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MotherSlime>> MOTHER_SLIME = withAttributes(registerEntity("mother_slime", "史莱姆之母", EntityType.Builder.of(MotherSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(47).armor(4).armorToughness(1).attackDamage(11)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(52).attackDamage(9).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(103).attackDamage(19).armor(7).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> BABY_SLIME = withAttributes(registerEntity("baby_slime", "史莱姆宝宝", EntityType.Builder.<BaseSlime>of((type, level) -> new BaseSlime(type, level, false, 1, false, true), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(16).armor(2).armorToughness(0).attackDamage(7)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(52).attackDamage(19).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(24).armor(8).armorToughness(3))
                    .build());

    /// 丛林：史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> JUNGLE_SLIME = withAttributes(registerSlime("jungle_slime", "丛林史莱姆", true, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(32).armor(3).armorToughness(1).attackDamage(10)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(17).armor(6).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(17).armor(6).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_JUNGLE_SLIME = withAttributes(registerEntity("spiked_jungle_slime", "尖刺丛林史莱姆", EntityType.Builder.<SpikedSlime>of((type, level) -> new SpikedSlime(type, level, SlimeSpikeEntity.Variant.JUNGLE), MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(34).armor(4).armorToughness(1).attackDamage(15)
                    .projectile(ModEntities.SLIME_SPIKE, projectile -> projectile.damage(14))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(37).attackDamage(13).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(37).attackDamage(13).armor(4).armorToughness(1))
                    .build());

    /// 冰雪：史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> ICE_SLIME = withAttributes(registerSlime("ice_slime", "冰雪史莱姆", true, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(16).armor(2).armorToughness(0).attackDamage(5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(15).armor(8).armorToughness(3))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(19).armor(9).armorToughness(4))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_ICE_SLIME = withAttributes(registerEntity("spiked_ice_slime", "尖刺冰雪史莱姆", EntityType.Builder.<SpikedSlime>of((type, level) -> new SpikedSlime(type, level, SlimeSpikeEntity.Variant.ICE), MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(32).armor(4).armorToughness(1).attackDamage(7)
                    .projectile(ModEntities.SLIME_SPIKE, projectile -> projectile.damage(10))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(69).attackDamage(11).armor(8).armorToughness(3))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(69).attackDamage(11).armor(8).armorToughness(3))
                    .build());

    /// 沙漠：史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> DESERT_SLIME = withAttributes(registerSlime("desert_slime", "沙漠史莱姆", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(26).armor(3).armorToughness(1).attackDamage(8)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(14).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(21).armor(7).armorToughness(3))
                    .build());

    /// 地牢与腐化：史莱姆及分裂体
    public static final DeferredHolder<EntityType<?>, EntityType<CorruptSlime>> CORRUPT_SLIME = withAttributes(registerEntity("corrupt_slime", "腐化史莱姆", EntityType.Builder.of(CorruptSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(89).armor(9).armorToughness(4).attackDamage(29).build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> SLIMELING = withAttributes(registerEntity("slimeling", "小史莱姆", EntityType.Builder.<BaseSlime>of((type, level) -> new BaseSlime(type, level, false, 1), MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(47).armor(5).armorToughness(2).attackDamage(24).phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(52).attackDamage(21).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(52).attackDamage(21).armor(5).armorToughness(2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<Slimer>> SLIMER = withAttributes(registerEntityWithEnglishName("slimer", "恶翅史莱姆", "Slimer", EntityType.Builder.of(Slimer::new, MobCategory.MONSTER).sized(1.0F, 0.9F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(9).armorToughness(4).attackDamage(24).followRange(48).attackKnockback(1).knockbackResistance(0.28)
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(35).attackDamage(21).armor(9).armorToughness(4))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> WINGLESS_SLIMER = withAttributes(registerEntityWithEnglishName("wingless_slimer", "恶翅史莱姆", "Slimer", EntityType.Builder.<BaseSlime>of(BaseSlime::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(47).armor(9).armorToughness(4).attackDamage(24).followRange(48).attackKnockback(1).knockbackResistance(0.3)
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(52).attackDamage(21).armor(9).armorToughness(4))
                    .build());

    /// 血腥之地：猩红史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> CRIMSLIME = withAttributes(registerEntity("crimslime", "猩红史莱姆", EntityType.Builder.<BaseSlime>of((type, level) -> new BaseSlime(type, level, false, 2, false, true), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(104).armor(12).armorToughness(5).attackDamage(32).build());

    /// 神圣之地：夜明史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<LuminousSlime>> LUMINOUS_SLIME = withAttributes(registerEntity("luminous_slime", "夜明史莱姆", EntityType.Builder.of(LuminousSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(94).armor(14).armorToughness(6).attackDamage(37).build());

    /// 丛林水域：热带史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<TropicSlime>> TROPIC_SLIME = withAttributes(registerEntity("tropic_slime", "热带史莱姆", EntityType.Builder.of(TropicSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(13).armor(1).attackDamage(5).build());

    /// 地牢：地牢史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> DUNGEON_SLIME = withAttributes(registerSlime("dungeon_slime", "地牢史莱姆", false, 3),
            () -> CreatureAttributeBuilder.slime().maxHealth(78).armor(4).armorToughness(1).attackDamage(16)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(86).attackDamage(14).armor(4).armorToughness(1))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(14).armor(4).armorToughness(1))
                    .build());

    /// 地狱：熔岩史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<LavaSlime>> LAVA_SLIME = withAttributes(registerEntity("lava_slime", "熔岩史莱姆", EntityType.Builder.of(LavaSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.slime().maxHealth(26).armor(5).armorToughness(2).attackDamage(8)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(14).armor(9).armorToughness(4))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(58).attackDamage(14).armor(9).armorToughness(4))
                    .build());

    /// 血腥之地（血肉）：血肉史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<FleshSlime>> FLESH_SLIME = withAttributes(registerEntity("flesh_slime", "血肉史莱姆", EntityType.Builder.of(FleshSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.slime().maxHealth(50).armor(3).attackDamage(14).armorToughness(1).build());

    /// Boss 附属生物：尖刺史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_SLIME = withAttributes(registerEntity("spiked_slime", "尖刺史莱姆", EntityType.Builder.<SpikedSlime>of(SpikedSlime::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(26).armor(3).armorToughness(1).attackDamage(8)
                    .projectile(ModEntities.SLIME_SPIKE, projectile -> projectile.damage(10))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase.maxHealth(58).attackDamage(13).armor(5).armorToughness(2))
                    .phase(GamePhase.PLANTERA, phase -> phase.maxHealth(86).attackDamage(20).armor(7).armorToughness(3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeRescue>> OLD_SHAKING_CHEST = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("old_shaking_chest", "老旧摇摇箱", EntityType.Builder.<TownSlimeRescue>of((type, level) -> new TownSlimeRescue(type, level, false), MobCategory.CREATURE).sized(0.8F, 0.8F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(250).armor(5).attackDamage(0).movementSpeed(0.2).knockbackResistance(0.3).armorToughness(2).build());
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeRescue>> CLUMSY_BALLOON_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("clumsy_balloon_slime", "笨拙气球史莱姆", EntityType.Builder.<TownSlimeRescue>of((type, level) -> new TownSlimeRescue(type, level, true), MobCategory.CREATURE).sized(0.8F, 0.8F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(1).attackDamage(0).movementSpeed(0.0).build());

    /// 丛林：绿饺史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> GREEN_DUMPLING_SLIME = withAttributes(registerSlime("green_dumpling_slime", "青团史莱姆", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(0).attackDamage(5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SweetSlime>> SWEET_SLIME = withAttributes(registerEntity("sweet_slime", "甜蜜史莱姆", EntityType.Builder.of(SweetSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(16).armor(0).attackDamage(0).build());

    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, id -> builder.build(id.toString()));
    }

    /// 属性声明汇入 {@link ModEntities#withAttributes}（同一个 `ATTRIBUTES` 表）。
    private static <T extends LivingEntity> DeferredHolder<EntityType<?>, EntityType<T>> withAttributes(DeferredHolder<EntityType<?>, EntityType<T>> type, Supplier<CreatureAttributeBuilder.Definition> attributes) {
        return ModEntities.withAttributes(type, attributes);
    }

    private static DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> registerSkeleton(String name, float width, float height, MeleeSkeleton.BehaviorProfile behaviorProfile) {
        return registerEntity(name, EntityType.Builder.<MeleeSkeleton>of((type, level) -> new MeleeSkeleton(type, level, false, behaviorProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    /// 注册需要哥布林专用浮水行为的人形敌怪。

    private static DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> registerGoblinLand(String name, float width, float height, ItemStack defaultMainHand,
                                                                                             BaseWarriorMonster.LandAnimationProfile animationProfile, GoblinMonster.DoorBehavior doorBehavior) {
        return registerEntity(name, EntityType.Builder.<GoblinMonster>of(
                (type, level) -> new GoblinMonster(type, level, defaultMainHand, animationProfile, doorBehavior),
                MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String name, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerHumanoidLand(name, width, height, defaultMainHand, soundProfile, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE);
    }

    private static DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String name, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        return registerEntity(name, EntityType.Builder.<HumanoidWarriorMonster>of((type, level) -> new HumanoidWarriorMonster(type, level, defaultMainHand, soundProfile, animationProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerAcceleratingLand(String name, float width, float height, double pursuitSpeedBonus, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.DoorBehavior doorBehavior) {
        return registerEntity(name, EntityType.Builder.<BaseWarriorMonster>of((type, level) -> new BaseWarriorMonster(type, level, pursuitSpeedBonus, animationProfile, soundProfile, meleeSpeed, ignoreLightPathCost, doorBehavior), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        return registerEntity(
                name,
                EntityType.Builder.<JumpingWarriorMonster>of(
                                (type, level) -> new JumpingWarriorMonster(
                                        type, level, profile, animationProfile),
                        MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, double meleeSpeed) {
        return registerEntity(name, EntityType.Builder.<JumpingWarriorMonster>of((type, level) -> new JumpingWarriorMonster(type, level, profile, animationProfile, soundProfile, meleeSpeed), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> registerWormSegment(String name) {
        return registerEntity(name, EntityType.Builder.of(BaseWormPart::new, MobCategory.MISC)
                .sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave());
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerLand(String name, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerLand(name, width, height, soundProfile, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.0, false);
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerLand(String name, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile, double meleeSpeed, boolean ignoreLightPathCost) {
        return registerLand(name, width, height, soundProfile, animationProfile, meleeSpeed, ignoreLightPathCost, BaseWarriorMonster.DoorBehavior.NONE);
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerLand(String name, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.DoorBehavior doorBehavior) {
        return registerEntity(name, EntityType.Builder.<BaseWarriorMonster>of((type, level) -> new BaseWarriorMonster(type, level, 0.0, animationProfile, soundProfile, meleeSpeed, ignoreLightPathCost, doorBehavior), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private MonsterEntities() {}

    private static DeferredHolder<EntityType<?>, EntityType<BaseSlime>> registerSlime(String registryId, String chineseName, boolean passiveByDay, int size) {
        DeferredHolder<EntityType<?>, EntityType<BaseSlime>> entry = registerSlime(registryId, passiveByDay, size, false);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseSlime>> registerSlime(String registryId, String chineseName, boolean passiveByDay, int size, boolean honeyConvertible) {
        DeferredHolder<EntityType<?>, EntityType<BaseSlime>> entry = registerEntity(registryId, EntityType.Builder.<BaseSlime>of((entityType, level) -> new BaseSlime(entityType, level, passiveByDay, size, honeyConvertible), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String registryId, String chineseName, EntityType.Builder<T> builder) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, id -> builder.build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerLand(String registryId, String chineseName, float w, float h) {
        DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> entry = registerLand(registryId, w, h, BaseWarriorMonster.LandSoundProfile.ROUTINE);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> registerSkeleton(String registryId, String chineseName, float w, float h) {
        DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> entry = registerSkeleton(registryId, w, h, MeleeSkeleton.BehaviorProfile.BLOOD_MOON_DOORS);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> registerSkeleton(String registryId, String chineseName, float width, float height, MeleeSkeleton.BehaviorProfile behaviorProfile) {
        DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> entry = registerEntity(registryId, EntityType.Builder.<MeleeSkeleton>of((type, level) -> new MeleeSkeleton(type, level, false, behaviorProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> registerGoblinLand(String registryId, String chineseName, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> entry = registerGoblinLand(registryId, width, height, defaultMainHand, animationProfile, GoblinMonster.DoorBehavior.NONE);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> registerGoblinLand(String registryId, String chineseName, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandAnimationProfile animationProfile, GoblinMonster.DoorBehavior doorBehavior) {
        DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> entry = registerEntity(registryId, EntityType.Builder.<GoblinMonster>of(
                (type, level) -> new GoblinMonster(type, level, defaultMainHand, animationProfile, doorBehavior),
                MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<JellyFish>> registerJellyFish(String registryId, String chineseName, JellyFish.Profile profile) {
        DeferredHolder<EntityType<?>, EntityType<JellyFish>> entry = registerEntity(registryId, EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, profile), MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<Snatcher>> registerSnatcher(String registryId, String chineseName, Snatcher.Profile profile) {
        DeferredHolder<EntityType<?>, EntityType<Snatcher>> entry = registerEntity(registryId, EntityType.Builder.<Snatcher>of((type, level) -> new Snatcher(type, level, profile), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<ChargingMonster>> registerCharger(String registryId, String chineseName, float width, float height, double chargeSpeed, int windupTicks) {
        DeferredHolder<EntityType<?>, EntityType<ChargingMonster>> entry = registerEntity(registryId, EntityType.Builder.<ChargingMonster>of((type, level) -> new ChargingMonster(type, level, chargeSpeed, windupTicks), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String registryId, String chineseName, float width, float height, ItemStack defaultMainHand) {
        DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> entry = registerHumanoidLand(registryId, width, height, defaultMainHand, BaseWarriorMonster.LandSoundProfile.ROUTINE);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String registryId, String chineseName, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandSoundProfile soundProfile) {
        DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> entry = registerHumanoidLand(registryId, width, height, defaultMainHand, soundProfile, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String registryId, String chineseName, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> entry = registerEntity(registryId, EntityType.Builder.<HumanoidWarriorMonster>of((type, level) -> new HumanoidWarriorMonster(type, level, defaultMainHand, soundProfile, animationProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<FlyingFishMonster>> registerFlyingFish(String registryId, String chineseName, float width, float height, FlyingFishMonster.PursuitProfile pursuitProfile) {
        DeferredHolder<EntityType<?>, EntityType<FlyingFishMonster>> entry = registerEntity(registryId, EntityType.Builder.<FlyingFishMonster>of(
                (type, level) -> new FlyingFishMonster(type, level, pursuitProfile, 0.2),
                MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerAcceleratingLand(String registryId, String chineseName, float width, float height, double pursuitSpeedBonus, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile) {
        DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> entry = registerAcceleratingLand(registryId, width, height, pursuitSpeedBonus, meleeSpeed, ignoreLightPathCost, animationProfile, soundProfile, BaseWarriorMonster.DoorBehavior.NONE);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerAcceleratingLand(String registryId, String chineseName, float width, float height, double pursuitSpeedBonus, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.DoorBehavior doorBehavior) {
        DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> entry = registerEntity(registryId, EntityType.Builder.<BaseWarriorMonster>of((type, level) -> new BaseWarriorMonster(type, level, pursuitSpeedBonus, animationProfile, soundProfile, meleeSpeed, ignoreLightPathCost, doorBehavior), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String registryId, String chineseName, float width, float height, BaseWarriorMonster.JumpProfile profile) {
        DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> entry = registerJumpingLand(registryId, width, height, profile, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String registryId, String chineseName, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> entry = registerEntity(
                registryId,
                EntityType.Builder.<JumpingWarriorMonster>of(
                        (type, level) -> new JumpingWarriorMonster(
                                type, level, profile, animationProfile),
                        MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String registryId, String chineseName, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile) {
        DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> entry = registerJumpingLand(registryId, width, height, profile, animationProfile, soundProfile, 1.0);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String registryId, String chineseName, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, double meleeSpeed) {
        DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> entry = registerEntity(registryId, EntityType.Builder.<JumpingWarriorMonster>of((type, level) -> new JumpingWarriorMonster(type, level, profile, animationProfile, soundProfile, meleeSpeed), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String registryId, String chineseName, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, double meleeSpeed, boolean mummy) {
        DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> entry = registerEntity(registryId, EntityType.Builder.<JumpingWarriorMonster>of((type, level) -> new JumpingWarriorMonster(type, level, profile, animationProfile, soundProfile, meleeSpeed, mummy), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<DarkCaster>> registerCaster(String registryId, String chineseName, float width, float height, DarkCaster.Profile profile) {
        DeferredHolder<EntityType<?>, EntityType<DarkCaster>> entry = registerEntity(registryId, EntityType.Builder.<DarkCaster>of((type, level) -> new DarkCaster(type, level, profile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> registerWormSegment(String registryId, String chineseName) {
        DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> entry = registerEntity(registryId, EntityType.Builder.of(BaseWormPart::new, MobCategory.MISC)
                .sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave());
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> registerWorm(String registryId, String chineseName, int segments, float width, float height, SimpleWormMonster.Role role, Supplier<EntityType<BaseWormPart>> segmentType) {
        DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> entry = registerEntity(registryId, EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, segments, role, segmentType.get()), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10).updateInterval(1));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> registerWorm(String registryId, String chineseName, float width, float height, SimpleWormMonster.Role role, SimpleWormMonster.Anatomy anatomy, Supplier<EntityType<BaseWormPart>> segmentType) {
        DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> entry = registerEntity(registryId, EntityType.Builder.<SimpleWormMonster>of(
                        (type, level) -> new SimpleWormMonster(type, level, role, anatomy, segmentType.get()), MobCategory.MONSTER)
                .sized(width, height).clientTrackingRange(10).updateInterval(1));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerLand(String registryId, String chineseName, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile) {
        DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> entry = registerLand(registryId, width, height, soundProfile, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.0, false);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerLand(String registryId, String chineseName, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile, double meleeSpeed, boolean ignoreLightPathCost) {
        DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> entry = registerLand(registryId, width, height, soundProfile, animationProfile, meleeSpeed, ignoreLightPathCost, BaseWarriorMonster.DoorBehavior.NONE);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerLand(String registryId, String chineseName, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.DoorBehavior doorBehavior) {
        DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> entry = registerEntity(registryId, EntityType.Builder.<BaseWarriorMonster>of((type, level) -> new BaseWarriorMonster(type, level, 0.0, animationProfile, soundProfile, meleeSpeed, ignoreLightPathCost, doorBehavior), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntityWithEnglishName(String registryId, String chineseName, String englishName, EntityType.Builder<T> builder) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, id -> builder.build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
