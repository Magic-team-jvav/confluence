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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.entity.monster.*;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.monster.slime.*;
import org.confluence.mod.common.entity.npc.TownSlimeRescue;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;

import java.util.function.Supplier;

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

    // ------------------------------------------------------------------
    // 血腥之地 / 腐化之地
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<BloodCrawler>> BLOOD_CRAWLER = withAttributes(registerEntity("blood_crawler", EntityType.Builder.of(BloodCrawler::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(31).armor(4).attackDamage(15).followRange(32).attackKnockback(1).knockbackResistance(0.8).movementSpeed(0.38).spawnReinforcementsChance(0.01).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<Werewolf>> WEREWOLF = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("werewolf", EntityType.Builder.of(Werewolf::new, MobCategory.MONSTER).sized(0.9F, 2.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(18).attackDamage(37).followRange(32).attackKnockback(1).knockbackResistance(0.64).movementSpeed(0.3).armorToughness(7).build());

    public static final DeferredHolder<EntityType<?>, EntityType<WindyBalloon>> WINDY_BALLOON = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("windy_balloon", EntityType.Builder.<WindyBalloon>of(WindyBalloon::new, MobCategory.MONSTER).sized(0.8F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(1).armor(0).attackDamage(0).knockbackResistance(0.7).followRange(16).build());

    public static final DeferredHolder<EntityType<?>, EntityType<AngryDandelion>> ANGRY_DANDELION = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_dandelion", EntityType.Builder.of(AngryDandelion::new, MobCategory.MONSTER).sized(1.0F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(0).attackDamage(8).knockbackResistance(1.0).movementSpeed(0.0).followRange(32)
                    .projectile(ModEntities.DANDELION_SEED, projectile -> projectile.damage(8).scaleWithAttack(8))
                    .build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> CAVE_BAT = withAttributes(registerEntity("cave_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(8).armor(1).attackDamage(4).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 4.3)
                            .multiply(LibAttributes.getAttackDamage(), 3.5)
                            .multiply(Attributes.ARMOR, 4))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 5.5)
                            .multiply(LibAttributes.getAttackDamage(), 4.4)
                            .multiply(Attributes.ARMOR, 5))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> GIANT_BAT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("giant_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ROUTINE), MobCategory.MONSTER).sized(0.6F, 1.4F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(52).armor(8).attackDamage(24).followRange(32).attackKnockback(0.2).knockbackResistance(0.33).armorToughness(3).build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> JUNGLE_BAT = withAttributes(registerEntity("jungle_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(17).armor(2).attackDamage(8).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> GIANT_FLYING_FOX = withAttributes(registerEntity("giant_flying_fox", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ROUTINE), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(114).armor(11).attackDamage(38).followRange(48).attackKnockback(0.5).knockbackResistance(0.64).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> ICE_BAT = withAttributes(registerEntity("ice_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ICE), MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(15).armor(3).armorToughness(1).attackDamage(7).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> ILLUMINANT_BAT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("illuminant_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ILLUMINANT), MobCategory.MONSTER).sized(0.8F, 1.35F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(14).attackDamage(39).followRange(32).attackKnockback(0.2).knockbackResistance(0.33).armorToughness(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> SPORE_BAT = withAttributes(registerEntity("spore_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(8).armor(1).attackDamage(7).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 4.3)
                            .multiply(LibAttributes.getAttackDamage(), 3.5)
                            .multiply(Attributes.ARMOR, 4))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 5.5)
                            .multiply(LibAttributes.getAttackDamage(), 4.4)
                            .multiply(Attributes.ARMOR, 5))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> HELL_BAT = withAttributes(registerEntity("hell_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.HELL), MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.creature().maxHealth(23).armor(4).armorToughness(1).attackDamage(15).followRange(16).attackKnockback(0.2).knockbackResistance(0.5)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CaveBat>> LAVA_BAT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("lava_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.LAVA), MobCategory.MONSTER).sized(0.8F, 1.25F).clientTrackingRange(10).fireImmune())),
            () -> CreatureAttributeBuilder.creature().maxHealth(84).armor(8).attackDamage(26).followRange(32).attackKnockback(0.2).knockbackResistance(0.46).armorToughness(3).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> WOODEN_MIMIC = withAttributes(registerEntity("wooden_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(14).attackDamage(42).followRange(32).attackKnockback(1).knockbackResistance(0.73).armorToughness(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> GOLDEN_MIMIC = withAttributes(registerEntity("golden_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(14).attackDamage(42).followRange(32).attackKnockback(1).knockbackResistance(0.73).armorToughness(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> ICE_MIMIC = withAttributes(registerEntity("ice_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(14).attackDamage(42).followRange(32).attackKnockback(1).knockbackResistance(0.73).armorToughness(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> SHADOW_MIMIC = withAttributes(registerEntity("shadow_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(14).attackDamage(42).followRange(32).attackKnockback(1).knockbackResistance(0.73).armorToughness(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseMimic>> JUNGLE_MIMIC = withAttributes(registerEntity("jungle_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1820).armor(16).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9).armorToughness(6)
                    .state(BaseMimic.CombatState.DEFENDING, state -> state.duration(40))
                    .state(BaseMimic.CombatState.RISING, state -> state.duration(30))
                    .state(BaseMimic.CombatState.SLAMMING, state -> state.duration(40).chargeSpeed(1.2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseMimic>> CORRUPT_MIMIC = withAttributes(registerEntity("corrupt_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1820).armor(16).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9).armorToughness(6)
                    .state(BaseMimic.CombatState.DEFENDING, state -> state.duration(40))
                    .state(BaseMimic.CombatState.RISING, state -> state.duration(30))
                    .state(BaseMimic.CombatState.SLAMMING, state -> state.duration(40).chargeSpeed(1.2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseMimic>> CRIMSON_MIMIC = withAttributes(registerEntity("crimson_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1820).armor(16).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9).armorToughness(6)
                    .state(BaseMimic.CombatState.DEFENDING, state -> state.duration(40))
                    .state(BaseMimic.CombatState.RISING, state -> state.duration(30))
                    .state(BaseMimic.CombatState.SLAMMING, state -> state.duration(40).chargeSpeed(1.2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseMimic>> HALLOWED_MIMIC = withAttributes(registerEntity("hallowed_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1820).armor(16).attackDamage(47).followRange(32).attackKnockback(1).knockbackResistance(0.9).armorToughness(6)
                    .state(BaseMimic.CombatState.DEFENDING, state -> state.duration(40))
                    .state(BaseMimic.CombatState.RISING, state -> state.duration(30))
                    .state(BaseMimic.CombatState.SLAMMING, state -> state.duration(40).chargeSpeed(1.2))
                    .build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> SPORE_SKELETON = withAttributes(registerEntity("spore_skeleton", EntityType.Builder.<MeleeSkeleton>of((type, level) -> new MeleeSkeleton(type, level, true, MeleeSkeleton.BehaviorProfile.OPEN_DOORS), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(31).armor(4).attackDamage(11).followRange(60).attackKnockback(0.5).knockbackResistance(0.28).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BASE_BONES = withAttributes(registerSkeleton("base_bones", 0.65F, 1.85F),
            () -> CreatureAttributeBuilder.creature().maxHealth(41).armor(1).attackDamage(13).followRange(20).attackKnockback(1).knockbackResistance(0.28).movementSpeed(0.3).build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> ANGER_BONES = withAttributes(registerSkeleton("anger_bones", 0.65F, 1.85F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(41).armor(4).attackDamage(13).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> SHORT_BONES = withAttributes(registerSkeleton("short_bones", 0.55F, 1.65F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(4).attackDamage(12).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_BONES = withAttributes(registerSkeleton("big_bones", 0.85F, 2.25F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(52).armor(5).attackDamage(17).armorToughness(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_ANGER_BONES = withAttributes(registerSkeleton("big_anger_bones", 0.9F, 2.4F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(36).armor(3).attackDamage(17).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_MUSCLE_ANGER_BONES = withAttributes(registerSkeleton("big_muscle_anger_bones", 0.95F, 2.45F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(36).armor(6).attackDamage(14).armorToughness(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_HELMET_ANGER_BONES = withAttributes(registerSkeleton("big_helmet_anger_bones", 1F, 2.6F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES),
            () -> CreatureAttributeBuilder.creature().maxHealth(62).armor(7).attackDamage(12).armorToughness(3)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> UNDEAD_VIKING = withAttributes(registerSkeleton("undead_viking", 1F, 2.6F),
            () -> CreatureAttributeBuilder.creature().maxHealth(36).armor(5).attackDamage(12).armorToughness(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> ARMORED_VIKING = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerSkeleton("armored_viking", 0.8F, 2.35F, MeleeSkeleton.BehaviorProfile.ARMORED_VIKING)),
            () -> CreatureAttributeBuilder.creature().maxHealth(146).armor(13).attackDamage(26).knockbackResistance(0.6).movementSpeed(0.23).followRange(32).armorToughness(5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Decayeder>> DECAYEDER = withAttributes(registerEntity("decayeder", EntityType.Builder.of(Decayeder::new, MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(10).armor(3).attackDamage(6).armorToughness(1)
                    /// 不指定 damage 时保留原版弓箭结算；可在这里显式覆盖箭的 baseDamage。
                    .projectile(() -> EntityType.ARROW, projectile -> projectile.attackDamage(1.0 / 1.6).speed(1.6))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> ARMORED_SKELETON = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerSkeleton("armored_skeleton", 0.8F, 2.45F, MeleeSkeleton.BehaviorProfile.NORMAL)),
            () -> CreatureAttributeBuilder.creature().maxHealth(136).armor(13).attackDamage(21).knockbackResistance(0.64).movementSpeed(0.23).followRange(32).armorToughness(5).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> GOBLIN_SCOUT = withAttributes(registerGoblinLand("goblin_scout", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN),
            () -> CreatureAttributeBuilder.creature().maxHealth(41).armor(3).attackDamage(10).followRange(32).attackKnockback(1).knockbackResistance(0.37).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> GOBLIN_PEON = withAttributes(registerGoblinLand("goblin_peon", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.BREAK),
            () -> CreatureAttributeBuilder.creature().maxHealth(31).armor(2).attackDamage(6).followRange(32).attackKnockback(1).knockbackResistance(0.2).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> GOBLIN_WARRIOR = withAttributes(registerGoblinLand("goblin_warrior", 0.65F, 1.85F, Items.STONE_SWORD.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN),
            () -> CreatureAttributeBuilder.creature().maxHealth(57).armor(4).attackDamage(13).followRange(32).attackKnockback(1).knockbackResistance(0.6).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> GOBLIN_THIEF = withAttributes(registerGoblinLand("goblin_thief", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.BREAK),
            () -> CreatureAttributeBuilder.creature().maxHealth(41).armor(3).attackDamage(10).followRange(32).attackKnockback(1).knockbackResistance(0.37).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinArcher>> GOBLIN_ARCHER = withAttributes(registerEntity("goblin_archer", EntityType.Builder.of(GoblinArcher::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(41).armor(3).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.37).armorToughness(1)
                    /// 不指定 damage 时保留原版弓箭结算；可在这里显式覆盖箭的 baseDamage。
                    .projectile(() -> EntityType.ARROW, projectile -> projectile.attackDamage(1.0 / 1.6).speed(1.6))

                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AngerGoblin>> ANGER_GOBLIN = withAttributes(registerEntity("anger_goblin", EntityType.Builder.of(AngerGoblin::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(220).armor(0).attackDamage(15).followRange(32).attackKnockback(1).knockbackResistance(0.88).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SporeZombie>> SPORE_ZOMBIE = withAttributes(registerEntity("spore_zombie", EntityType.Builder.of(SporeZombie::new, MobCategory.MONSTER).sized(0.75F, 1.95F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(93).armor(5).attackDamage(20).followRange(60).attackKnockback(0.6).knockbackResistance(0.1).movementSpeed(0.08).armorToughness(2).build());
    public static final DeferredHolder<EntityType<?>, EntityType<SporeZombie>> HAT_SPORE_ZOMBIE = withAttributes(registerEntity("hat_spore_zombie", EntityType.Builder.of(SporeZombie::new, MobCategory.MONSTER).sized(0.75F, 1.95F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(114).armor(8).attackDamage(19).followRange(60).attackKnockback(0.6).knockbackResistance(0.72).movementSpeed(0.08).armorToughness(3).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Zombie>> ZOMBIE = withAttributes(registerEntity("zombie", EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(20).armor(3).armorToughness(1).attackDamage(4).followRange(16).attackKnockback(0.5).knockbackResistance(0.0).movementSpeed(0.23).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> DOCTOR_BONES = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("doctor_bones", 1.0F, 2.0F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON)),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(5).attackDamage(11).knockbackResistance(0.55).followRange(32).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> SNOW_FLINX = withAttributes(registerLand("snow_flinx", 1.25F, 1.25F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 0.8, false),
            () -> CreatureAttributeBuilder.creature().maxHealth(36).armor(6).attackDamage(13).followRange(60).attackKnockback(0.1).knockbackResistance(0.1).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> BASILISK = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("basilisk", 1.6F, 1.55F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.8, true)),
            () -> CreatureAttributeBuilder.creature().maxHealth(141).armor(16).attackDamage(34).followRange(32).movementSpeed(0.35).attackKnockback(1).knockbackResistance(0.73).armorToughness(7).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> BLOOD_ZOMBIE = withAttributes(registerAcceleratingLand("blood_zombie", 0.75F, 1.95F, 0.25, 0.8, true,
            BaseWarriorMonster.LandAnimationProfile.WALK_RUN_IDLE_ATTACK, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.DoorBehavior.OPEN),
            () -> CreatureAttributeBuilder.creature().maxHealth(39).armor(4).attackDamage(10).followRange(60).attackKnockback(0.5).knockbackResistance(0.1).movementSpeed(0.15).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> THE_GROOM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("the_groom", 1.0F, 2.5F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON)),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(4).attackDamage(8).knockbackResistance(0.55).followRange(32).armorToughness(1).build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> THE_BRIDE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("the_bride", 1.0F, 2.0F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON)),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(4).attackDamage(8).knockbackResistance(0.55).followRange(32).armorToughness(1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<FlyingFishMonster>> FLYING_FISH = withAttributes(registerFlyingFish(
            "flying_fish", 0.9F, 0.9F,
                    new FlyingFishMonster.PursuitProfile(0.95, 0.5, 0.02, 5)),
            () -> CreatureAttributeBuilder.creature().maxHealth(10).armor(1).attackDamage(2).followRange(30).attackKnockback(0.5).knockbackResistance(0.3)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 4.3)
                            .multiply(LibAttributes.getAttackDamage(), 3.5)
                            .multiply(Attributes.ARMOR, 4))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 5.5)
                            .multiply(LibAttributes.getAttackDamage(), 4.4)
                            .multiply(Attributes.ARMOR, 5))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> ANTLION_LARVA = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("antlion_larva", 0.7F, 0.5F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.3, true)),
            () -> CreatureAttributeBuilder.creature().maxHealth(16).armor(1).attackDamage(6).movementSpeed(0.3).knockbackResistance(0.33).followRange(24).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    /// 木乃伊（1.20 `:308`，按脚下沙块分型；`JumpProfile` 传 `null`，`mummy=true`）。
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> MUMMY = withAttributes(registerJumpingLand("mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.0, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(68).armor(16).attackDamage(26).followRange(48).attackKnockback(1).knockbackResistance(0.46).stepHeight(3.2).jumpStrength(0.5).armorToughness(7)
                    .state(JumpingWarriorMonster.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))

                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> DARK_MUMMY = withAttributes(registerJumpingLand("dark_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.5, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(94).armor(18).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).armorToughness(8)
                    .state(JumpingWarriorMonster.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> BLOOD_MUMMY = withAttributes(registerJumpingLand("blood_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.5, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(94).armor(18).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).armorToughness(8)
                    .state(JumpingWarriorMonster.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> LIGHT_MUMMY = withAttributes(registerJumpingLand("light_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.0, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(18).attackDamage(29).followRange(48).attackKnockback(1).knockbackResistance(0.51).stepHeight(3.2).jumpStrength(0.5).armorToughness(8)
                    .state(JumpingWarriorMonster.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 2))

                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> DARK_LAMIA = withAttributes(registerLand("dark_lamia", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.3, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(13).attackDamage(28).followRange(48).attackKnockback(1).knockbackResistance(0.69).stepHeight(3.2).jumpStrength(0.5).armorToughness(5).build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> LIGHT_LAMIA = withAttributes(registerLand("light_lamia", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.3, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(13).attackDamage(28).followRange(48).attackKnockback(1).knockbackResistance(0.69).stepHeight(3.2).jumpStrength(0.5).armorToughness(5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> GHOUL = withAttributes(registerLand("ghoul", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.6, true),
            () -> CreatureAttributeBuilder.creature().maxHealth(94).armor(12).attackDamage(26).followRange(64).attackKnockback(1).knockbackResistance(0.46).stepHeight(3.2).jumpStrength(0.7).armorToughness(5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> VILE_GHOUL = withAttributes(registerJumpingLand("vile_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, false),
            () -> CreatureAttributeBuilder.creature().maxHealth(130).armor(14).attackDamage(32).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.7).armorToughness(6).build());
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> TAINTED_GHOUL = withAttributes(registerJumpingLand("tainted_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, false),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(15).attackDamage(34).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.7).armorToughness(6).build());
    public static final DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> DREAMER_GHOUL = withAttributes(registerJumpingLand("dreamer_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, false),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(15).attackDamage(29).followRange(64).attackKnockback(1).knockbackResistance(0.64).stepHeight(3.2).jumpStrength(0.7).armorToughness(6).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> WYVERN_SEGMENT = registerWormSegment("wyvern_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<Wyvern>> WYVERN = withAttributes(registerEntity("wyvern", EntityType.Builder.<Wyvern>of((type, level) -> new Wyvern(type, level, MonsterEntities.WYVERN_SEGMENT.get()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10).updateInterval(1)),
            () -> CreatureAttributeBuilder.creature().maxHealth(2080).armor(5).attackDamage(41).followRange(50).attackKnockback(1).knockbackResistance(0.28).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> GIANT_WORM_SEGMENT = registerWormSegment("giant_worm_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> GIANT_WORM = withAttributes(registerWorm("giant_worm", 2F, 2F, SimpleWormMonster.Role.UNDERGROUND, SimpleWormMonster.Anatomy.GIANT_WORM, MonsterEntities.GIANT_WORM_SEGMENT),
            () -> CreatureAttributeBuilder.creature().maxHealth(16).armor(2).attackDamage(9).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 5.5)
                            .multiply(LibAttributes.getAttackDamage(), 4.5)
                            .multiply(Attributes.ARMOR, 5))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 5.5)
                            .multiply(LibAttributes.getAttackDamage(), 4.4)
                            .multiply(Attributes.ARMOR, 5))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> DIGGER_SEGMENT = registerWormSegment("digger_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> DIGGER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("digger", EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, SimpleWormMonster.Role.UNDERGROUND, SimpleWormMonster.Anatomy.DIGGER, MonsterEntities.DIGGER_SEGMENT.get()), MobCategory.MONSTER).sized(1.6F, 1.05F).clientTrackingRange(10).updateInterval(1))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(5).attackDamage(24).knockbackResistance(1.0).followRange(32).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> DEVOURER_SEGMENT = registerWormSegment("devourer_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> DEVOURER = withAttributes(registerWorm("devourer", 2F, 2F, SimpleWormMonster.Role.CORRUPTION, SimpleWormMonster.Anatomy.DEVOURER, () -> MonsterEntities.DEVOURER_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(52).armor(1).attackDamage(8)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> WORLD_FEEDER_SEGMENT = registerWormSegment("world_feeder_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> WORLD_FEEDER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("world_feeder", EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, SimpleWormMonster.Role.CORRUPTION, SimpleWormMonster.Anatomy.WORLD_FEEDER, MonsterEntities.WORLD_FEEDER_SEGMENT.get()), MobCategory.MONSTER).sized(2.0F, 1.1F).clientTrackingRange(10).updateInterval(1))),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(17).attackDamage(37).knockbackResistance(1.0).followRange(32).armorToughness(7).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> BONE_SERPENT_SEGMENT = registerWormSegment("bone_serpent_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> BONE_SERPENT = withAttributes(registerWorm("bone_serpent", 2F, 2F, SimpleWormMonster.Role.BONE_SERPENT, SimpleWormMonster.Anatomy.BONE_SERPENT, () -> MonsterEntities.BONE_SERPENT_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(6).attackDamage(18).armorToughness(2).build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> WITHER_BONE_SERPENT_SEGMENT = registerWormSegment("wither_bone_serpent_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> WITHER_BONE_SERPENT = withAttributes(registerWorm("wither_bone_serpent", 2F, 2F, SimpleWormMonster.Role.BONE_SERPENT, SimpleWormMonster.Anatomy.BONE_SERPENT, () -> MonsterEntities.WITHER_BONE_SERPENT_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(186).armor(7).attackDamage(22).armorToughness(3).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> ARCH_WYVERN_SEGMENT = registerWormSegment("arch_wyvern_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> ARCH_WYVERN = withAttributes(registerWorm("arch_wyvern", 12, 1.8F, 1.8F, SimpleWormMonster.Role.FLYING, () -> MonsterEntities.ARCH_WYVERN_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(3120).armor(9).attackDamage(52).followRange(64).attackKnockback(1).knockbackResistance(0.37).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> LEECH_SEGMENT = registerWormSegment("leech_segment");
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> LEECH = withAttributes(registerWorm("leech", 2F, 2F, SimpleWormMonster.Role.UNDERWORLD, SimpleWormMonster.Anatomy.LEECH, () -> MonsterEntities.LEECH_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(36).armor(2).attackDamage(10).movementSpeed(0.145).armorToughness(1).build());

    // 地下水域：水母与琵琶鱼
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> BLUE_JELLYFISH = withAttributes(registerJellyFish("blue_jellyfish", JellyFish.Profile.ROUTINE),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(17).armor(2).attackDamage(13).followRange(16).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(1)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> GREEN_JELLYFISH = withAttributes(registerJellyFish("green_jellyfish", JellyFish.Profile.ROUTINE),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(62).armor(9).attackDamage(41).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(4)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<AnglerFish>> ANGLER_FISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("angler_fish", EntityType.Builder.of(AnglerFish::new, MobCategory.MONSTER).sized(0.8F, 0.6F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(47).armor(10).attackDamage(42).followRange(24).movementSpeed(1.5).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(4).build());

    // 花岗岩洞：元素与巨人
    public static final DeferredHolder<EntityType<?>, EntityType<GraniteElemental>> GRANITE_ELEMENTAL = withAttributes(registerEntity("granite_elemental", EntityType.Builder.of(GraniteElemental::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(46).armor(4).attackDamage(17).followRange(32).attackKnockback(1).knockbackResistance(0.73).armorToughness(1)
                    .state(GraniteElemental.DefensePhase.ENTERING, state -> state.duration(7))
                    .state(GraniteElemental.DefensePhase.DEFENDING, state -> state.duration(40))
                    .state(GraniteElemental.DefensePhase.EXITING, state -> state.duration(7))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<GraniteGolem>> GRANITE_GOLEM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("granite_golem", EntityType.Builder.of(GraniteGolem::new, MobCategory.MONSTER).sized(1.7F, 2.3F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(58).armor(9).attackDamage(16).knockbackResistance(0.69).movementSpeed(0.2).armorToughness(3)
                    .state(GraniteGolem.DefensePhase.ACTIVE, state -> state.attackInterval(100))
                    .state(GraniteGolem.DefensePhase.ENTERING, state -> state.duration(5))
                    .state(GraniteGolem.DefensePhase.DEFENDING, state -> state.duration(40))
                    .state(GraniteGolem.DefensePhase.EXITING, state -> state.duration(5))
                    .build());

    // 丛林：蜘蛛、蹦跳兽与陆龟
    public static final DeferredHolder<EntityType<?>, EntityType<GiantShelly>> GIANT_SHELLY = withAttributes(registerEntity("giant_shelly", EntityType.Builder.of(GiantShelly::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(6).attackDamage(9).followRange(20).attackKnockback(0).knockbackResistance(0.4).movementSpeed(0.1).armorToughness(2)
                    .state(GiantShelly.Phase.ENTERING_SHELL, state -> state.bonus(Attributes.ARMOR, 12).duration(20))
                    .state(GiantShelly.Phase.ROLLING, state -> state.bonus(Attributes.ATTACK_DAMAGE, 4).bonus(Attributes.ARMOR, 12).duration(30))
                    .state(GiantShelly.Phase.FREE, state -> state.duration(40))
                    .state(GiantShelly.Phase.RECOVERING, state -> state.duration(20))

                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<Crawdad>> CRAWDAD = withAttributes(registerEntity("crawdad", EntityType.Builder.of(Crawdad::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(3).attackDamage(15).followRange(25).attackKnockback(0).knockbackResistance(0.1).jumpStrength(0.8).armorToughness(1)
                    .state(Crawdad.AttackState.CLAW_ATTACK, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.875))

                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<Derpling>> DERPLING = withAttributes(registerEntity("derpling", EntityType.Builder.of(Derpling::new, MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(12).attackDamage(41).followRange(48).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).armorToughness(5).build());

    // 冰雪：蝙蝠、雪怪与陆龟
    public static final DeferredHolder<EntityType<?>, EntityType<GiantTortoise>> ICE_TORTOISE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_tortoise", EntityType.Builder.of(GiantTortoise::new, MobCategory.MONSTER).sized(2.25F, 1.85F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(13).attackDamage(29).followRange(48).attackKnockback(1).knockbackResistance(0.73).movementSpeed(0.2).armorToughness(5)
                    .state(GiantTortoise.Phase.SPINNING, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.8).multiplyBase(Attributes.ARMOR, 2))
                    .state(GiantTortoise.Phase.RETRACTING, state -> state.duration(10))
                    .state(GiantTortoise.Phase.WINDING_UP, state -> state.duration(12))
                    .state(GiantTortoise.Phase.EMERGING, state -> state.duration(10))
                    .state(GiantTortoise.Phase.WALK, state -> state.attackInterval(20))
                    .build());

    // 地下沙漠：蛇蜥怪、沙贼与沙漠蠕虫
    public static final DeferredHolder<EntityType<?>, EntityType<SandPoacher>> SAND_POACHER = withAttributes(registerEntity("sand_poacher", EntityType.Builder.of(SandPoacher::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(166).armor(11).attackDamage(34).followRange(64).attackKnockback(1).knockbackResistance(0.55).stepHeight(3.2).jumpStrength(0.5).armorToughness(4)
                    .state(SandPoacher.CombatState.CLIMBING, state -> state.bonus(Attributes.MOVEMENT_SPEED, 0.25))
                    .build());

    // 猩红：史莱姆、脸怪与蹦跳兽
    public static final DeferredHolder<EntityType<?>, EntityType<Derpling>> HERPLING = withAttributes(registerEntity("herpling", EntityType.Builder.of(Derpling::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(114).armor(12).attackDamage(33).followRange(48).attackKnockback(1).knockbackResistance(0.73).stepHeight(3.2).jumpStrength(0.5).armorToughness(5).build());

    // 海洋：鲨鱼、水母与史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<Shark>> SHARK = withAttributes(registerEntity("shark", EntityType.Builder.of(Shark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(156).armor(1).attackDamage(20).followRange(48).movementSpeed(1.2).attackKnockback(0.37).knockbackResistance(0.1).build());

    // 地牢：诅咒骷髅与幽魂
    public static final DeferredHolder<EntityType<?>, EntityType<CursedSkull>> CURSED_SKULL = withAttributes(registerEntity("cursed_skull", EntityType.Builder.of(CursedSkull::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(21).armor(3).attackDamage(18).followRange(32).attackKnockback(1).knockbackResistance(0.82).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    // 陨石：流星头
    public static final DeferredHolder<EntityType<?>, EntityType<MeteorHead>> METEOR_HEAD = withAttributes(registerEntity("meteor_head", EntityType.Builder.of(MeteorHead::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(13).armor(3).attackDamage(21).followRange(32).attackKnockback(1).knockbackResistance(0.64).armorToughness(1).build());

    // 墓地：幽灵
    public static final DeferredHolder<EntityType<?>, EntityType<Ghost>> GHOST = withAttributes(registerEntity("ghost", EntityType.Builder.of(Ghost::new, MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(2).attackDamage(8).followRange(16).attackKnockback(0).knockbackResistance(0.55).gravity(0).armorToughness(0)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    // 血月：血腥僵尸与滴滴怪
    public static final DeferredHolder<EntityType<?>, EntityType<Drippler>> DRIPPLER = withAttributes(registerEntity("drippler", EntityType.Builder.of(Drippler::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(7).attackDamage(14).followRange(64).attackKnockback(0.5).knockbackResistance(0.2).armorToughness(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<EaterOfSouls>> CRIMERA = withAttributes(registerEntity("crimera", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(20).armor(4).attackDamage(11).followRange(30).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<EaterOfSouls>> EATER_OF_SOULS = withAttributes(registerEntity("eater_of_souls", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(20).armor(4).attackDamage(11).followRange(30).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BloodySpore>> BLOODY_SPORE = withAttributes(registerEntity("bloody_spore", EntityType.Builder.of(BloodySpore::new, MobCategory.MONSTER).sized(1, 1.5f).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(47).armor(3).attackDamage(0).followRange(32).attackKnockback(0).knockbackResistance(0.8).spawnReinforcementsChance(0.01).armorToughness(1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BloodTumor>> BLOOD_TUMORS = withAttributes(registerEntity("blood_tumors", EntityType.Builder.of(BloodTumor::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(5).armor(1).attackDamage(0).followRange(0).attackKnockback(0).knockbackResistance(0).movementSpeed(0).safeFallDistance(100).build());

    // ------------------------------------------------------------------
    // 地表人形（`registerLand` 系列）
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> FACE_MONSTER = withAttributes(registerLand("face_monster", 0.75F, 1.95F,
                    BaseWarriorMonster.LandSoundProfile.FACE_MONSTER, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.0, true, BaseWarriorMonster.DoorBehavior.OPEN),
            () -> CreatureAttributeBuilder.creature().maxHealth(36).armor(5).attackDamage(13).stepHeight(3.2).jumpStrength(0.8).armorToughness(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> DARK_CASTER = withAttributes(registerCaster("dark_caster", 0.65F, 1.85F, DarkCaster.Profile.DARK_CASTER),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(1).attackDamage(10).followRange(20).attackKnockback(1).knockbackResistance(0.82)
                    .projectile(ModEntities.DARK_CASTER_PROJECTILE, projectile -> projectile.attackDamage(1))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.6)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<FireImp>> FIRE_IMP = withAttributes(registerEntity("fire_imp", EntityType.Builder.of(FireImp::new, MobCategory.MONSTER).sized(0.65F, 1F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.creature().maxHealth(36).armor(8).attackDamage(15).followRange(20).attackKnockback(1).knockbackResistance(0.55).armorToughness(3)
                    .projectile(ModEntities.FIRE_IMP_PROJECTILE, projectile -> projectile.attackDamage(1))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Gastropod>> GASTROPOD = withAttributes(registerEntity("gastropod", EntityType.Builder.of(Gastropod::new, MobCategory.MONSTER).sized(0.7F, 0.75F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(143).armor(10).attackDamage(40).followRange(48).attackKnockback(1).knockbackResistance(0.64).armorToughness(4)
                    .projectile(ModEntities.GASTROPOD_PROJECTILE, projectile -> projectile.attackDamage(mob -> CreatureDefinition.get(mob.getType()).behavior().shotMultiplierOr(0.8)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinWarlock>> GOBLIN_WARLOCK = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("goblin_warlock", EntityType.Builder.of(GoblinWarlock::new, MobCategory.MONSTER).sized(0.8F, 1.9F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(884).armor(12).attackDamage(42).movementSpeed(0.3).followRange(48).knockbackResistance(0.87).armorToughness(5)
                    .projectile(ModEntities.CHAOS_BALL_PROJECTILE, projectile -> projectile.damage(42).scaleWithAttack(42))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<WaterBoltMimic>> WATER_BOLT_MIMIC = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("water_bolt_mimic", EntityType.Builder.of(WaterBoltMimic::new, MobCategory.MONSTER).sized(1.0F, 0.9F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(2).attackDamage(11).followRange(32).knockbackResistance(0.82).armorToughness(0)
                    .projectile(ModEntities.DARK_CASTER_PROJECTILE, projectile -> projectile.attackDamage(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Corruptor>> CORRUPTOR = withAttributes(registerEntity("corruptor", EntityType.Builder.of(Corruptor::new, MobCategory.MONSTER).sized(2.2F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(120).armor(15).attackDamage(32).followRange(48).attackKnockback(1).knockbackResistance(0.51).armorToughness(6)
                    .projectile(ModEntities.VILE_SPIT_PROJECTILE, projectile -> projectile.attackDamage(mob -> CreatureDefinition.get(mob.getType()).behavior().shotMultiplierOr(0.8)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ShadowflameApparition>> SHADOWFLAME_APPARITION = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("shadowflame_apparition", EntityType.Builder.of(ShadowflameApparition::new, MobCategory.MONSTER).sized(0.8F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(80).armor(9).attackDamage(21).followRange(48).knockbackResistance(1).armorToughness(3).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<Gnome>> GNOME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("gnome", EntityType.Builder.of(Gnome::new, MobCategory.MONSTER).sized(0.5F, 0.8F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(13).armor(0).attackDamage(6).movementSpeed(0.3).followRange(24).knockbackResistance(0.1).build());

    // ------------------------------------------------------------------
    // 血月与邪恶转化：企鹅和金鱼
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<EvilPenguin>> CORRUPT_PENGUIN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("corrupt_penguin", EntityType.Builder.of(EvilPenguin::new, MobCategory.MONSTER).sized(0.7F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(2).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.1).movementSpeed(0.25).armorToughness(0).build());

    public static final DeferredHolder<EntityType<?>, EntityType<EvilPenguin>> VICIOUS_PENGUIN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("vicious_penguin", EntityType.Builder.of(EvilPenguin::new, MobCategory.MONSTER).sized(0.7F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(39).armor(3).attackDamage(11).followRange(32).attackKnockback(1).knockbackResistance(0.1).movementSpeed(0.25).armorToughness(1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> CORRUPT_GOLDFISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("corrupt_goldfish", EntityType.Builder.<Piranha>of((type, level) -> new Piranha(type, level, Piranha.AnimationProfile.GOLDFISH), MobCategory.MONSTER).sized(0.6F, 0.45F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(52).armor(3).attackDamage(16).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> VICIOUS_GOLDFISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("vicious_goldfish", EntityType.Builder.<Piranha>of((type, level) -> new Piranha(type, level, Piranha.AnimationProfile.GOLDFISH), MobCategory.MONSTER).sized(0.6F, 0.45F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(58).armor(4).attackDamage(17).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(1).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<LittleHornet>> LITTLE_HORNET = withAttributes(registerEntity("little_hornet", EntityType.Builder.of(LittleHornet::new, MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(3).armor(1).attackDamage(3).followRange(20).attackKnockback(0).knockbackResistance(0.2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    /// 沙虫的段体与本体（`registerWorm` 系列见下面的辅助方法）。
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> TOMB_CRAWLER_SEGMENT = registerWormSegment("tomb_crawler_segment");

    public static final DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> TOMB_CRAWLER = withAttributes(registerWorm("tomb_crawler", 2F, 2F, SimpleWormMonster.Role.UNDERGROUND_DESERT, SimpleWormMonster.Anatomy.TOMB_CRAWLER, () -> MonsterEntities.TOMB_CRAWLER_SEGMENT.get()),
            () -> CreatureAttributeBuilder.creature().maxHealth(16).armor(1).attackDamage(4)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> PIRATE_DECKHAND = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("pirate_deckhand", 0.6F, 1.85F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.2, true)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(8).attackDamage(19).movementSpeed(0.3).followRange(40).knockbackResistance(0.64).armorToughness(3).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> PIRATE_CORSAIR = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerLand("pirate_corsair", 0.6F, 1.85F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.3, true)),
            () -> CreatureAttributeBuilder.creature().maxHealth(234).armor(10).attackDamage(26).movementSpeed(0.3).followRange(40).knockbackResistance(0.82).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateRangedMonster>> PIRATE_DEADEYE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_deadeye", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.DEADEYE), MobCategory.MONSTER).sized(0.6F, 1.85F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(117).armor(7).attackDamage(16).movementSpeed(0.3).followRange(40).knockbackResistance(0.73).armorToughness(2)
                    .projectile(ModEntities.PIRATE_BULLET, projectile -> projectile.damage(25).scaleWithAttack(25))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateRangedMonster>> PIRATE_CROSSBOWER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_crossbower", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.CROSSBOWER), MobCategory.MONSTER).sized(0.6F, 1.85F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(9).attackDamage(19).movementSpeed(0.3).followRange(40).knockbackResistance(0.69).armorToughness(4)
                    .projectile(ModEntities.PIRATE_FLAMING_ARROW, projectile -> projectile.damage(37).scaleWithAttack(19))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateRangedMonster>> PIRATE_CAPTAIN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_captain", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.CAPTAIN), MobCategory.MONSTER).sized(0.8F, 2.0F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(1560).armor(14).attackDamage(37).movementSpeed(0.15).followRange(40).knockbackResistance(1).armorToughness(6)
                    .projectile(ModEntities.PIRATE_BULLET, projectile -> projectile.damage(16).scaleWithAttack(37))
                    .projectile(ModEntities.PIRATE_CANNONBALL, projectile -> projectile.damage(104).scaleWithAttack(37))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateFlyingMonster>> PIRATE_PARROT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_parrot", EntityType.Builder.<PirateFlyingMonster>of((type, level) -> new PirateFlyingMonster(type, level, false), MobCategory.MONSTER).sized(0.5F, 0.7F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(52).armor(6).attackDamage(42).movementSpeed(0.3).followRange(40).knockbackResistance(0.37).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PirateFlyingMonster>> PIRATES_CURSE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirates_curse", EntityType.Builder.<PirateFlyingMonster>of((type, level) -> new PirateFlyingMonster(type, level, true), MobCategory.MONSTER).sized(0.8F, 1.3F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(10).attackDamage(39).movementSpeed(0.3).followRange(40).knockbackResistance(0.82).armorToughness(4).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------
    //
    //

    public static final DeferredHolder<EntityType<?>, EntityType<DemonEye>> DEMON_EYE = withAttributes(registerEntity("demon_eye", EntityType.Builder.<DemonEye>of(DemonEye::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(15).armor(1).attackDamage(3.5).followRange(40).attackKnockback(0).knockbackResistance(0).movementSpeed(0.2).flyingSpeed(0.6)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> POSSESS_ARMOR = withAttributes(registerHumanoidLand("possess_armor", 1F, 2F, Items.AIR.getDefaultInstance(),
                    BaseWarriorMonster.LandSoundProfile.POSSESSED_ARMOR, BaseWarriorMonster.LandAnimationProfile.NONE),
            () -> CreatureAttributeBuilder.creature().maxHealth(135).armor(5).attackDamage(28).followRange(32).attackKnockback(1).knockbackResistance(0.64).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Wraith>> WRAITH = withAttributes(registerEntity("wraith", EntityType.Builder.of(Wraith::new, MobCategory.MONSTER).sized(1F, 2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(83).armor(8).armorToughness(3).attackDamage(33).followRange(32).attackKnockback(1).knockbackResistance(0.37).gravity(0).build());

    // 空岛与高空：鸟妖和飞龙
    public static final DeferredHolder<EntityType<?>, EntityType<Harpy>> HARPY = withAttributes(registerEntity("harpy", EntityType.Builder.of(Harpy::new, MobCategory.MONSTER).sized(1f, 2f).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(41).armor(4).attackDamage(13).followRange(50).armorToughness(1)
                    .projectile(ModEntities.HARPY_FEATHER, projectile -> projectile.attackDamage(1))

                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    // 地下与洞穴：法师
    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> TIM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerCaster("tim", 1.0F, 3.2F, DarkCaster.Profile.TIM)),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(2).attackDamage(11).knockbackResistance(0.46).followRange(32).armorToughness(1)
                    .projectile(ModEntities.CHAOS_BALL_PROJECTILE, projectile -> projectile.damage(38))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> RUNE_WIZARD = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerCaster("rune_wizard", 1.0F, 3.2F, DarkCaster.Profile.RUNE_WIZARD)),
            () -> CreatureAttributeBuilder.creature().maxHealth(312).armor(14).attackDamage(104).knockbackResistance(0.73).followRange(32).armorToughness(6)
                    .projectile(ModEntities.RUNE_BLAST, projectile -> projectile.damage(42))
                    .build());

    // 地下与洞穴：岩石巨人及稀有敌怪
    public static final DeferredHolder<EntityType<?>, EntityType<RockGolem>> ROCK_GOLEM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("rock_golem", EntityType.Builder.of(RockGolem::new, MobCategory.MONSTER).sized(1.5F, 2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(520).armor(16).attackDamage(45).knockbackResistance(0.91).movementSpeed(0.2).followRange(32).armorToughness(7)
                    .projectile(ModEntities.THROWN_ROCK, projectile -> projectile.attackDamage(40.0 / 85.0))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Nymph>> NYMPH = withAttributes(registerEntity("nymph", EntityType.Builder.of(Nymph::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(8).attackDamage(15).followRange(15).attackKnockback(1).knockbackResistance(0.5).armorToughness(3)
                    .state(Nymph.CombatState.PURSUING, state -> state.bonus(Attributes.MOVEMENT_SPEED, 0.25))
                    .build());

    // 蜘蛛洞：爬墙蜘蛛与黑隐士
    public static final DeferredHolder<EntityType<?>, EntityType<ClimbingSpider>> WALL_CREEPER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("wall_creeper", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.WALL), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(42).armor(5).attackDamage(16).followRange(32).knockbackResistance(0.78).movementSpeed(0.23).armorToughness(2)
                    .state(ClimbingSpider.CombatState.SPITTING, state -> state.attackInterval(60))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ClimbingSpider>> BLACK_RECLUSE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("black_recluse", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.BLACK_RECLUSE), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(18).attackDamage(47).followRange(32).knockbackResistance(0.78).movementSpeed(0.28).armorToughness(8)
                    .state(ClimbingSpider.CombatState.CLIMBING, state -> state.multiply(Attributes.ATTACK_DAMAGE, 10.0 / 9.0))
                    .state(ClimbingSpider.CombatState.SPITTING, state -> state.attackInterval(60))
                    .projectile(ModEntities.SPIDER_WEB_SPIT, projectile -> projectile.damage(10).speed(0.8).lifetime(80))
                    .build());

    // 大理石洞：装甲步兵
    public static final DeferredHolder<EntityType<?>, EntityType<Hoplite>> HOPLITE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("hoplite", EntityType.Builder.of(Hoplite::new, MobCategory.MONSTER).sized(1.0F, 2.7F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(37).armor(5).attackDamage(12).knockbackResistance(0.64).movementSpeed(0.23).followRange(32).armorToughness(2)
                    .projectile(ModEntities.HOPLITE_JAVELIN, projectile -> projectile.damage(19).scaleWithAttack(19))
                    .build());

    // 丛林：黄蜂及幼蜂
    public static final DeferredHolder<EntityType<?>, EntityType<Hornet>> HORNET = withAttributes(registerEntity("hornet", EntityType.Builder.<Hornet>of(Hornet::new, MobCategory.MONSTER).sized(0.8F, 1.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(6).attackDamage(13).followRange(32).attackKnockback(0).knockbackResistance(0.55).movementSpeed(0.5).armorToughness(2)
                    .projectile(ModEntities.HORNET_STINGER, projectile -> projectile.attackDamage(1))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Hornet>> MOSS_HORNET = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("moss_hornet", EntityType.Builder.<Hornet>of(Hornet::new, MobCategory.MONSTER).sized(0.8F, 1.8F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(10).attackDamage(37).followRange(32).attackKnockback(0).knockbackResistance(0.55).movementSpeed(0.5).armorToughness(4)
                    .projectile(ModEntities.HORNET_STINGER, projectile -> projectile.attackDamage(6.0 / 7.0))
                    .build());

    // 丛林：食人植物
    public static final DeferredHolder<EntityType<?>, EntityType<Snatcher>> SNATCHER = withAttributes(registerSnatcher("snatcher", Snatcher.Profile.SNATCHER),
            () -> CreatureAttributeBuilder.creature().maxHealth(31).armor(5).attackDamage(13).followRange(20).attackKnockback(1).knockbackResistance(1).armorToughness(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Snatcher>> MAN_EATER = withAttributes(registerSnatcher("man_eater", Snatcher.Profile.MAN_EATER),
            () -> CreatureAttributeBuilder.creature().maxHealth(57).armor(5).attackDamage(15).followRange(20).attackKnockback(1).knockbackResistance(1).armorToughness(2).build());

    // 丛林：蜘蛛、蹦跳兽与陆龟
    public static final DeferredHolder<EntityType<?>, EntityType<ClimbingSpider>> JUNGLE_CREEPER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("jungle_creeper", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.JUNGLE), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(13).attackDamage(52).followRange(32).knockbackResistance(0.78).movementSpeed(0.28).armorToughness(5)
                    .state(ClimbingSpider.CombatState.SPITTING, state -> state.attackInterval(60))
                    .projectile(ModEntities.SPIDER_WEB_SPIT, projectile -> projectile.damage(10).speed(0.8).lifetime(80))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<GiantTortoise>> GIANT_TORTOISE = withAttributes(registerEntity("giant_tortoise", EntityType.Builder.of(GiantTortoise::new, MobCategory.MONSTER).sized(2.25F, 1.85F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(244).armor(14).attackDamage(41).followRange(48).attackKnockback(1).knockbackResistance(0.73).movementSpeed(0.2).armorToughness(6)
                    .state(GiantTortoise.Phase.SPINNING, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.8).multiplyBase(Attributes.ARMOR, 2))
                    .state(GiantTortoise.Phase.RETRACTING, state -> state.duration(10))
                    .state(GiantTortoise.Phase.WINDING_UP, state -> state.duration(12))
                    .state(GiantTortoise.Phase.EMERGING, state -> state.duration(10))
                    .state(GiantTortoise.Phase.WALK, state -> state.attackInterval(20))
                    .build());

    // 丛林水域：食人鱼与巨骨舌鱼
    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> PIRANHA = withAttributes(registerEntity("piranha", EntityType.Builder.<Piranha>of(Piranha::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(15).armor(1).attackDamage(13).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Arapaima>> ARAPAIMA = withAttributes(registerEntity("arapaima", EntityType.Builder.of(Arapaima::new, MobCategory.MONSTER).sized(2.2F, 0.7F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(104).armor(14).attackDamage(39).followRange(32).movementSpeed(1.2).attackKnockback(0.1).knockbackResistance(0.1).armorToughness(6).build());

    // 冰雪：冰雪精与鱼人
    public static final DeferredHolder<EntityType<?>, EntityType<IceElemental>> ICE_ELEMENTAL = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_elemental", EntityType.Builder.of(IceElemental::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(9).attackDamage(29).knockbackResistance(0.46).movementSpeed(0.18).followRange(40).armorToughness(4)
                    .projectile(ModEntities.FROST_BLAST, projectile -> projectile.attackDamage(180.0 / 110.0))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<FrostFighter>> ICY_MERMAN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("icy_merman", EntityType.Builder.<FrostFighter>of((type, level) -> new FrostFighter(type, level, FrostFighter.Kind.MERMAN), MobCategory.MONSTER).sized(0.8F, 2.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(146).armor(14).attackDamage(32).knockbackResistance(0.55).movementSpeed(0.23).followRange(32).armorToughness(6)
                    .state(FrostFighter.CombatState.SHOOTING, state -> state.moveSpeed(1).attackInterval(35))
                    .state(FrostFighter.CombatState.WOUNDED, state -> state.moveSpeed(2).attackInterval(10))
                    .projectile(ModEntities.ICEWATER_SPIT, projectile -> projectile.attackDamage(148.0 / 120.0).speed(0.8).inaccuracy(0).lifetime(100))
                    .build());

    // 地下沙漠：蚁狮及幼虫
    public static final DeferredHolder<EntityType<?>, EntityType<Antlion>> ANTLION = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("antlion", EntityType.Builder.of(Antlion::new, MobCategory.MONSTER).sized(1.2F, 0.8F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(24).armor(3).attackDamage(6).movementSpeed(0.0).knockbackResistance(1.0).followRange(24).projectile(ModEntities.ANTLION_SAND_BALL, projectile -> projectile.damage(10).speed(1.1).lifetime(120)).armorToughness(1)
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AntlionCharger>> ANTLION_CHARGER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("antlion_charger", EntityType.Builder.of(AntlionCharger::new, MobCategory.MONSTER).sized(1.2F, 0.8F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(42).armor(5).attackDamage(13).movementSpeed(0.25).knockbackResistance(0.55).followRange(32).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<AntlionSwarmer>> ANTLION_SWARMER = withAttributes(registerEntity("antlion_swarmer", EntityType.Builder.of(AntlionSwarmer::new, MobCategory.MONSTER).sized(3.0F, 1.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(32).armor(4).attackDamage(16).followRange(32).attackKnockback(1).knockbackResistance(0.55).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AntlionSwarmer>> GIANT_ANTLION_SWARMER = withAttributes(registerEntity("giant_antlion_swarmer", EntityType.Builder.of(AntlionSwarmer::new, MobCategory.MONSTER).sized(3.5F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(47).armor(6).attackDamage(18).followRange(32).attackKnockback(1).knockbackResistance(0.73).armorToughness(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DesertSpirit>> DESERT_SPIRIT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("desert_spirit", EntityType.Builder.of(DesertSpirit::new, MobCategory.MONSTER).sized(1.0F, 2.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(115).armor(9).attackDamage(21).knockbackResistance(1.0).followRange(32).armorToughness(4)
                    .projectile(ModEntities.DESERT_SPIRIT_CURSE, projectile -> projectile.damage(23))
                    .build());

    // 腐化：爬藤怪与宝箱怪
    public static final DeferredHolder<EntityType<?>, EntityType<SpittingPlant>> CLINGER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("clinger", EntityType.Builder.<SpittingPlant>of((type, level) -> new SpittingPlant(type, level, Snatcher.Profile.CLINGER), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(167).armor(14).attackDamage(37).followRange(32).knockbackResistance(0.82).armorToughness(6)
                    .projectile(ModEntities.CLINGER_FLAME, projectile -> projectile.damage(18).scaleWithAttack(37))
                    .build());

    // 猩红水域：血蛭与血水母
    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> BLOOD_FEEDER = withAttributes(registerEntity("blood_feeder", EntityType.Builder.<Piranha>of(Piranha::new, MobCategory.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(78).armor(9).attackDamage(26).followRange(32).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.10).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> BLOOD_JELLY = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("blood_jelly", EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, JellyFish.Profile.ROUTINE), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(78).armor(9).attackDamage(39).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(4)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Pixie>> PIXIE = withAttributes(registerEntity("pixie", EntityType.Builder.of(Pixie::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(78).armor(9).attackDamage(28).followRange(16).attackKnockback(1).knockbackResistance(0.46).armorToughness(4).build());

    // 神圣：独角兽与腹足怪
    public static final DeferredHolder<EntityType<?>, EntityType<Unicorn>> UNICORN = withAttributes(registerEntity("unicorn", EntityType.Builder.of(Unicorn::new, MobCategory.MONSTER).sized(1.4F, 2.25F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(14).attackDamage(65).followRange(64).attackKnockback(1).knockbackResistance(0.82).movementSpeed(0.35).armorToughness(6)
                    .state(Unicorn.MovementState.PURSUING, state -> state.chargeSpeed(0.36).duration(40))
                    .state(Unicorn.MovementState.RECOVERING, state -> state.duration(12))
                    .build());

    // 地下神圣：附魔剑、混沌精与宝箱怪
    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedSword>> ENCHANTED_SWORD = withAttributes(registerEntity("enchanted_sword_monster", EntityType.Builder.of(EnchantedSword::new, MobCategory.MONSTER).sized(0.35F, 1.4F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(9).attackDamage(41).followRange(48).attackKnockback(1).knockbackResistance(0.82).armorToughness(3)
                    .state(EnchantedSword.CombatState.WINDUP, state -> state.duration(40))
                    .state(EnchantedSword.CombatState.CHARGING, state -> state.chargeSpeed(0.8))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ChaosElemental>> CHAOS_ELEMENTAL = withAttributes(registerEntity("chaos_elemental", EntityType.Builder.of(ChaosElemental::new, MobCategory.MONSTER).sized(0.7F, 1.9F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(193).armor(14).attackDamage(21).followRange(48).attackKnockback(1).knockbackResistance(0.64).armorToughness(6).build());

    // 发光蘑菇：真菌球怪
    public static final DeferredHolder<EntityType<?>, EntityType<Snatcher>> FUNGI_BULB = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerSnatcher("fungi_bulb", Snatcher.Profile.FUNGI_BULB)),
            () -> CreatureAttributeBuilder.creature().maxHealth(47).armor(2).attackDamage(13).followRange(20).knockbackResistance(1).armorToughness(1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SpittingPlant>> GIANT_FUNGI_BULB = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("giant_fungi_bulb", EntityType.Builder.<SpittingPlant>of((type, level) -> new SpittingPlant(type, level, Snatcher.Profile.GIANT_FUNGI_BULB), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(9).attackDamage(37).followRange(40).knockbackResistance(1).armorToughness(4)
                    .projectile(ModEntities.FUNGI_SPORE, projectile -> projectile.damage(42).scaleWithAttack(37))
                    .build());

    // 发光蘑菇水域：蘑菇水母
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> FUNGO_FISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("fungo_fish", EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, JellyFish.Profile.FUNGO), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(73).armor(9).attackDamage(47).followRange(20).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(4)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> PINK_JELLYFISH = withAttributes(registerJellyFish("pink_jellyfish", JellyFish.Profile.ROUTINE),
            () -> CreatureAttributeBuilder.aquatic().maxHealth(36).armor(3).attackDamage(15).followRange(16).movementSpeed(1.2).attackKnockback(0.5).knockbackResistance(0.1).armorToughness(1)
                    .state(JellyFish.CombatState.PURSUING, state -> state.duration(150))
                    .state(JellyFish.CombatState.PULSING, state -> state.duration(80).attackInterval(20))
                    .state(JellyFish.SpecialAttack.THORNS, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.3))

                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> NECROMANCER = withAttributes(registerCaster("necromancer", 0.7F, 1.9F, DarkCaster.Profile.NECROMANCER),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(9).attackDamage(26).followRange(48).attackKnockback(1).knockbackResistance(0.51).armorToughness(4)
                    .projectile(ModEntities.SHADOW_BEAM_PROJECTILE, projectile -> projectile.damage(25))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> DIABOLIST = withAttributes(registerCaster("diabolist", 0.7F, 1.9F, DarkCaster.Profile.DIABOLIST),
            () -> CreatureAttributeBuilder.creature().maxHealth(130).armor(13).attackDamage(52).followRange(48).attackKnockback(1).knockbackResistance(0.73).armorToughness(5)
                    .projectile(ModEntities.INFERNO_BOLT_PROJECTILE, projectile -> projectile.attackDamage(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> RAGGED_CASTER = withAttributes(registerCaster("ragged_caster", 0.7F, 1.9F, DarkCaster.Profile.RAGGED_CASTER),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(10).attackDamage(44).followRange(48).attackKnockback(1).knockbackResistance(0.73).armorToughness(4)
                    .projectile(ModEntities.LOST_SOUL_PROJECTILE, projectile -> projectile.attackDamage(1))
                    .build());


    // 地牢：圣骑士与骷髅李
    public static final DeferredHolder<EntityType<?>, EntityType<Paladin>> PALADIN = withAttributes(registerEntity("paladin", EntityType.Builder.of(Paladin::new, MobCategory.MONSTER).sized(1.2F, 2.4F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(520).armor(23).attackDamage(52).followRange(64).attackKnockback(1).knockbackResistance(1).armorToughness(8)
                    .projectile(ModEntities.PALADIN_HAMMER_PROJECTILE, projectile -> projectile.attackDamage(mob -> CreatureDefinition.get(mob.getType()).behavior().shotMultiplierOr(1.1)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ChargingMonster>> BONE_LEE = withAttributes(registerCharger("bone_lee", 0.7F, 1.9F, 0.82, 6),
            () -> CreatureAttributeBuilder.creature().maxHealth(520).armor(16).attackDamage(48).followRange(48).attackKnockback(1).knockbackResistance(0.95).movementSpeed(0.38).armorToughness(7).build());

    // 地狱：恶魔与火小鬼
    public static final DeferredHolder<EntityType<?>, EntityType<Demon>> DEMON = withAttributes(registerEntity("demon", EntityType.Builder.of(Demon::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(62).armor(4).attackDamage(20).followRange(16).attackKnockback(1).knockbackResistance(0.28).armorToughness(1)
                    .projectile(ModEntities.HOSTILE_DEMON_SCYTHE, projectile -> projectile.attackDamage(1))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Demon>> VOODOO_DEMON = withAttributes(registerEntity("voodoo_demon", EntityType.Builder.of(Demon::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.creature().maxHealth(62).armor(4).attackDamage(20).followRange(16).attackKnockback(1).knockbackResistance(0.28).armorToughness(1)
                    .projectile(ModEntities.HOSTILE_DEMON_SCYTHE, projectile -> projectile.attackDamage(1))
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<RedDevil>> RED_DEVIL = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("red_devil", EntityType.Builder.<RedDevil>of(RedDevil::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10).fireImmune())),
            () -> CreatureAttributeBuilder.creature().maxHealth(312).armor(18).attackDamage(26).followRange(32).knockbackResistance(0.55).armorToughness(8)
                    .projectile(ModEntities.UNHOLY_TRIDENT, projectile -> projectile.attackDamage(3.2))
                    .build());

    // 血月垂钓：游荡眼球怪与僵尸鱼人
    public static final DeferredHolder<EntityType<?>, EntityType<FlyingFishMonster>> WANDERING_EYE_FISH = withAttributes(registerFlyingFish(
            "wandering_eye_fish", 1.2F, 1.2F,
                    new FlyingFishMonster.PursuitProfile(0.98, 2.2, 0.01, 10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(9).attackDamage(15).followRange(60).attackKnockback(1).knockbackResistance(1).movementSpeed(2.2).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<ZombieMerman>> ZOMBIE_MERMAN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("zombie_merman", EntityType.Builder.<ZombieMerman>of(ZombieMerman::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(9).attackDamage(21).followRange(48).knockbackResistance(1).movementSpeed(0.2).armorToughness(4)
                    .state(ZombieMerman.MovementState.SWIMMING, state -> state.moveSpeed(0.6))
                    .state(ZombieMerman.MovementState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 3.5))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AngryNimbus>> ANGRY_NIMBUS = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_nimbus", EntityType.Builder.of(AngryNimbus::new, MobCategory.MONSTER).sized(2.0F, 1.4F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(11).attackDamage(26).knockbackResistance(0.73).movementSpeed(0.3).flyingSpeed(0.5).followRange(48).armorToughness(4)
                    .projectile(ModEntities.NIMBUS_RAIN, projectile -> projectile.damage(21).scaleWithAttack(21))
                    .build());

    // 暴风雪：冰雪巨人
    public static final DeferredHolder<EntityType<?>, EntityType<FrostFighter>> ICE_GOLEM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_golem", EntityType.Builder.<FrostFighter>of((type, level) -> new FrostFighter(type, level, FrostFighter.Kind.GOLEM), MobCategory.MONSTER).sized(3F, 8.0F).clientTrackingRange(12))),
            () -> CreatureAttributeBuilder.creature().maxHealth(2080).armor(15).attackDamage(32).knockbackResistance(0.96).movementSpeed(0.2).followRange(48).armorToughness(6)
                    .state(FrostFighter.CombatState.SHOOTING, state -> state.moveSpeed(0.6).attackInterval(20, 309))
                    .state(FrostFighter.CombatState.WOUNDED, state -> state.moveSpeed(2).attackInterval(10))
                    .projectile(ModEntities.FROST_BEAM, projectile -> projectile.attackDamage(128.0 / 120.0).speed(2.5).inaccuracy(0).lifetime(100))
                    .build());

    // 沙尘暴：愤怒翻滚怪
    public static final DeferredHolder<EntityType<?>, EntityType<AngryTumbler>> ANGRY_TUMBLER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_tumbler", EntityType.Builder.<AngryTumbler>of(AngryTumbler::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(26).armor(3).attackDamage(16).knockbackResistance(0.28).movementSpeed(0.25).followRange(32).armorToughness(1)
                    .state(AngryTumbler.MovementState.PURSUING, state -> state.chargeSpeed(2).duration(25))
                    .build());

    // 沙尘暴：普通、腐化、猩红与神圣沙鲨
    public static final DeferredHolder<EntityType<?>, EntityType<SandShark>> SAND_SHARK = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("sand_shark", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(188).armor(9).attackDamage(26).knockbackResistance(0.19).followRange(40).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SandShark>> BONE_BITER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("bone_biter", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(198).armor(11).attackDamage(32).knockbackResistance(0.28).followRange(40).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SandShark>> FLESH_REAVER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("flesh_reaver", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(10).attackDamage(34).knockbackResistance(0.28).followRange(40).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SandShark>> CRYSTAL_THRESHER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("crystal_thresher", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(1.8F, 1.1F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(234).armor(12).attackDamage(29).knockbackResistance(0.37).followRange(40).armorToughness(5).build());

    // 哥布林入侵：法师及召唤体
    public static final DeferredHolder<EntityType<?>, EntityType<DarkCaster>> GOBLIN_SORCERER = withAttributes(registerCaster("goblin_sorcerer", 0.65F, 1.85F, DarkCaster.Profile.GOBLIN_SORCERER),
            () -> CreatureAttributeBuilder.creature().maxHealth(20).armor(1).attackDamage(10).followRange(32).attackKnockback(1).knockbackResistance(0.46)
                    .projectile(ModEntities.CHAOS_BALL_PROJECTILE, projectile -> projectile.attackDamage(1))

                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    //火星人事件
    public static final DeferredHolder<EntityType<?>, EntityType<MartianProbe>> MARTIAN_PROBE = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("martian_probe", EntityType.Builder.of(MartianProbe::new, MobCategory.MONSTER).sized(1.4F, 1.0F).clientTrackingRange(12))),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(3).attackDamage(3).add(LibAttributes.getArmorPenetration(), 0).add(Attributes.ARMOR_TOUGHNESS, 1).followRange(48).movementSpeed(0.25).flyingSpeed(0.65).knockbackResistance(0.4).build());
    public static final DeferredHolder<EntityType<?>, EntityType<MartianEngineer>> MARTIAN_ENGINEER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("martian_engineer", EntityType.Builder.of(MartianEngineer::new, MobCategory.MONSTER).sized(0.8F, 2.0F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(208).armor(16).attackDamage(21).add(LibAttributes.getArmorPenetration(), 6).add(Attributes.ARMOR_TOUGHNESS, 6).followRange(40).movementSpeed(0.24).knockbackResistance(0.55).build());
    public static final DeferredHolder<EntityType<?>, EntityType<TeslaTurret>> TESLA_TURRET = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("tesla_turret", EntityType.Builder.of(TeslaTurret::new, MobCategory.MONSTER).sized(1.0F, 1.2F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(18).attackDamage(6).add(LibAttributes.getArmorPenetration(), 1).add(Attributes.ARMOR_TOUGHNESS, 8).followRange(36).movementSpeed(0.01).knockbackResistance(1.0)
                    .projectile(ModEntities.MARTIAN_ELECTRIC_BOLT, projectile -> projectile.damage(30))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<RayGunner>> RAY_GUNNER = withAttributes(registerEntity("ray_gunner", EntityType.Builder.of(RayGunner::new, MobCategory.MONSTER).sized(0.8F, 1.6F).clientTrackingRange(12)),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(12).attackDamage(26).add(LibAttributes.getArmorPenetration(), 8).add(Attributes.ARMOR_TOUGHNESS, 5).followRange(44).movementSpeed(0.25).knockbackResistance(0.65)
                    .state(RayGunner.CombatState.MOUNTED, state -> state.attackDamage(34).armor(14).attribute(Attributes.ARMOR_TOUGHNESS, 6)
                            .attribute(LibAttributes.getArmorPenetration(), 10).knockbackResistance(0.0))
                    .projectile(ModEntities.MONSTER_LASER, projectile -> projectile.damage(32))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<Scutlix>> SCUTLIX = withAttributes(registerEntity("scutlix", EntityType.Builder.of(Scutlix::new, MobCategory.MONSTER).sized(1.8F, 1.7F).clientTrackingRange(12)),
            () -> CreatureAttributeBuilder.creature().maxHealth(312).armor(14).attackDamage(45).add(LibAttributes.getArmorPenetration(), 12).add(Attributes.ARMOR_TOUGHNESS, 6).followRange(44).movementSpeed(0.32).knockbackResistance(0.9)
                    .state(Scutlix.CombatState.MOUNTED, state -> state.attackDamage(45))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MartianOfficer>> MARTIAN_OFFICER = withAttributes(registerEntity("martian_officer", EntityType.Builder.of(MartianOfficer::new, MobCategory.MONSTER).sized(0.85F, 2.05F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(156).armor(23).attackDamage(39).add(LibAttributes.getArmorPenetration(), 12).add(Attributes.ARMOR_TOUGHNESS, 8).followRange(36).movementSpeed(0.25).knockbackResistance(0.75).build());
    public static final DeferredHolder<EntityType<?>, EntityType<MartianWalker>> MARTIAN_WALKER = withAttributes(registerEntity("martian_walker", EntityType.Builder.of(MartianWalker::new, MobCategory.MONSTER).sized(2.8F, 7.5F).clientTrackingRange(16)),
            () -> CreatureAttributeBuilder.creature().maxHealth(1040).armor(18).attackDamage(32).add(LibAttributes.getArmorPenetration(), 10).add(Attributes.ARMOR_TOUGHNESS, 8).followRange(48).movementSpeed(0.55).knockbackResistance(1)
                    .projectile(ModEntities.MONSTER_LASER, projectile -> projectile.damage(37))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<WalkerWeapon>> WALKER_WEAPON = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("martian_walker_weapon", EntityType.Builder.of(WalkerWeapon::new, MobCategory.MONSTER).sized(0.8F, 1.0F).clientTrackingRange(16))),
            () -> CreatureAttributeBuilder.creature().maxHealth(260).armor(18).attackDamage(0).add(LibAttributes.getArmorPenetration(), 0).add(Attributes.ARMOR_TOUGHNESS, 8).followRange(0).movementSpeed(0).knockbackResistance(1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BrainScrambler>> BRAIN_SCRAMBLER = withAttributes(registerEntity("brain_scrambler", EntityType.Builder.of(BrainScrambler::new, MobCategory.MONSTER).sized(0.8F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(182).armor(12).attackDamage(26).add(LibAttributes.getArmorPenetration(), 8).add(Attributes.ARMOR_TOUGHNESS, 5).followRange(40).movementSpeed(0.24).knockbackResistance(0.65)
                    .projectile(ModEntities.MONSTER_LASER, projectile -> projectile.damage(25))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<GrayGrunt>> GRAY_GRUNT = withAttributes(registerEntity("gray_grunt", EntityType.Builder.of(GrayGrunt::new, MobCategory.MONSTER).sized(0.8F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(390).armor(14).attackDamage(42).add(LibAttributes.getArmorPenetration(), 12).add(Attributes.ARMOR_TOUGHNESS, 6).followRange(36).movementSpeed(0.28).knockbackResistance(1.0).build());
    public static final DeferredHolder<EntityType<?>, EntityType<Gigazapper>> GIGAZAPPER = withAttributes(registerEntity("gigazapper", EntityType.Builder.of(Gigazapper::new, MobCategory.MONSTER).sized(0.9F, 2.1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(312).armor(18).attackDamage(39).add(LibAttributes.getArmorPenetration(), 12).add(Attributes.ARMOR_TOUGHNESS, 7).followRange(36).movementSpeed(0.26).knockbackResistance(0.7)
                    .projectile(ModEntities.ELECTRIC_SPEAR, projectile -> projectile.damage(26))
                    .build());

    // Boss 附属生物：克苏鲁之脑

    // Boss 附属生物：血肉墙与血肉山


    public static final DeferredHolder<EntityType<?>, EntityType<DungeonSpirit>> DUNGEON_SPIRIT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("dungeon_spirit", EntityType.Builder.of(DungeonSpirit::new, MobCategory.MONSTER).sized(0.8F, 0.7F).clientTrackingRange(10))),
            () -> CreatureAttributeBuilder.creature().maxHealth(104).armor(14).attackDamage(70).movementSpeed(0.35).followRange(32).knockbackResistance(0.8).gravity(0).armorToughness(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<VisualNeuron>> VISUAL_NEURON = withAttributes(registerEntity("visual_neuron", EntityType.Builder.of(VisualNeuron::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(VisualNeuron.BASE_MAX_HEALTH).armor(5).attackDamage(9).followRange(0).attackKnockback(0).knockbackResistance(0.1).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<TheHungry>> THE_HUNGRY = withAttributes(registerEntity("the_hungry", EntityType.Builder.of(TheHungry::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(87).armor(8).attackDamage(15).followRange(32).attackKnockback(0.75).knockbackResistance(1).armorToughness(3)
                    .state(TheHungry.FeedingState.WOUNDED_OWNER, state -> state.multiply(Attributes.ATTACK_DAMAGE, 1.5).multiply(Attributes.ARMOR, mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? 20.0 / 16.0 : 2.0))
                    .state(TheHungry.FeedingState.CRITICAL_OWNER, state -> state.multiply(Attributes.ATTACK_DAMAGE, 2).multiply(Attributes.ARMOR, mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? 30.0 / 16.0 : 3.0))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<HillHungry>> HILL_HUNGRY = withAttributes(registerEntity("hill_hungry", EntityType.Builder.of(HillHungry::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(87).armor(8).attackDamage(15).followRange(32).attackKnockback(0.75).knockbackResistance(1).armorToughness(3).build());

    static {
        DemonEye.registerVariantStats(DemonEye.Variant.NORMAL, 15, 3.5, 1, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.NORMAL_BIG, 12, 4, 2, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.CATARACT, 11.5, 3.5, 2, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.CATARACT_BIG, 14, 4, 2, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.SLEEPY, 15, 3, 1, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.SLEEPY_BIG, 16, 3.5, 1, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.DILATED, 12, 3.5, 1, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.DILATED_SMALL, 11.5, 3, 0, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.GREEN, 15, 4, 0, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.GREEN_SMALL, 12.5, 3, 0, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.PURPLE, 15, 3, 2, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.PURPLE_BIG, 16, 3, 2, 0.1);
        DemonEye.registerVariantStats(DemonEye.Variant.OWL, 18.5, 3, 3, 0.2);
        DemonEye.registerVariantStats(DemonEye.Variant.SPACESHIP, 15, 3, 2, 0.2);
        Zombie.registerVariantStats(Zombie.Variant.NORMAL, 20, 4, 2);
        Zombie.registerVariantStats(Zombie.Variant.ARMED, 24, 6, 3);
        Zombie.registerVariantStats(Zombie.Variant.SLIMED, 18, 3.5, 2);
        Zombie.registerVariantStats(Zombie.Variant.PINCUSHION, 22, 5, 3);
        Zombie.registerVariantStats(Zombie.Variant.TWIGGY, 20, 5, 1);
        Zombie.registerVariantStats(Zombie.Variant.SWAMP, 20, 3.5, 3);
        Zombie.registerVariantStats(Zombie.Variant.RAINCOAT, 22, 4.5, 2);
        Zombie.registerVariantStats(Zombie.Variant.BLOOD, 28, 6, 3);
        Zombie.registerVariantStats(Zombie.Variant.ESKIMO, 24, 5, 4);
        Zombie.registerVariantStats(Zombie.Variant.BALD, 18, 4.5, 1);
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseSlime>> registerSlime(String name, boolean passiveByDay, int size) {
        return registerSlime(name, passiveByDay, size, false);
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseSlime>> registerSlime(String name, boolean passiveByDay, int size, boolean honeyConvertible) {
        return registerEntity(name, EntityType.Builder.<BaseSlime>of((entityType, level) -> new BaseSlime(entityType, level, passiveByDay, size, honeyConvertible), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    }

    // 地表与森林：史莱姆（passiveByDay 控制白天是否被动）
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> GREEN_SLIME = withAttributes(registerSlime("green_slime", true, 2, true),
            () -> CreatureAttributeBuilder.slime().maxHealth(9).armor(0).attackDamage(3)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 8.7)
                            .multiply(LibAttributes.getAttackDamage(), 7.1))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 9.8)
                            .multiply(LibAttributes.getAttackDamage(), 12))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> BLUE_SLIME = withAttributes(registerSlime("blue_slime", true, 2, true),
            () -> CreatureAttributeBuilder.slime().maxHealth(16).armor(1).attackDamage(4)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 5.4)
                            .multiply(LibAttributes.getAttackDamage(), 4.4)
                            .multiply(Attributes.ARMOR, 5))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 6.6)
                            .multiply(LibAttributes.getAttackDamage(), 5.2)
                            .multiply(Attributes.ARMOR, 6))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> PURPLE_SLIME = withAttributes(registerSlime("purple_slime", true, 2, true),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(3).attackDamage(5).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> PINK_SLIME = withAttributes(registerSlime("pink_slime", true, 1),
            () -> CreatureAttributeBuilder.slime().maxHealth(97).armor(1).attackDamage(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    // 地表与森林：金史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<GoldenSlime>> GOLDEN_SLIME = withAttributes(registerEntity("golden_slime", EntityType.Builder.of(GoldenSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(97).armor(3).armorToughness(1).attackDamage(5).build());

    // 沼泽：史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> SWAMP_SLIME = withAttributes(registerSlime("swamp_slime", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(1).attackDamage(5).build());

    // 地下与洞穴：史莱姆及分裂体
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> RED_SLIME = withAttributes(registerSlime("red_slime", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(2).attackDamage(5).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> YELLOW_SLIME = withAttributes(registerSlime("yellow_slime", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(4).attackDamage(6).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> BLACK_SLIME = withAttributes(registerEntity("black_slime", EntityType.Builder.<BaseSlime>of((type, level) -> new BaseSlime(type, level, false, 2, false, true), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(2).attackDamage(6).armorToughness(0)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<MotherSlime>> MOTHER_SLIME = withAttributes(registerEntity("mother_slime", EntityType.Builder.of(MotherSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(58).armor(4).attackDamage(10).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> BABY_SLIME = withAttributes(registerEntity("baby_slime", EntityType.Builder.<BaseSlime>of((type, level) -> new BaseSlime(type, level, false, 1, false, true), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(2).attackDamage(6).armorToughness(0)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    // 丛林：史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> JUNGLE_SLIME = withAttributes(registerSlime("jungle_slime", true, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(46).armor(3).attackDamage(12).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_JUNGLE_SLIME = withAttributes(registerEntity("spiked_jungle_slime", EntityType.Builder.<SpikedSlime>of((type, level) -> new SpikedSlime(type, level, SlimeSpikeEntity.Variant.JUNGLE), MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(33).armor(4).attackDamage(15).armorToughness(1)
                    .projectile(ModEntities.SLIME_SPIKE, projectile -> projectile.attackDamage(1))
                    .build());

    // 冰雪：史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> ICE_SLIME = withAttributes(registerSlime("ice_slime", true, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(13).armor(2).attackDamage(5).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 4.4)
                            .multiply(LibAttributes.getAttackDamage(), 3.5)
                            .multiply(Attributes.ARMOR, 4))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 5.5)
                            .multiply(LibAttributes.getAttackDamage(), 4.5)
                            .multiply(Attributes.ARMOR, 5))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_ICE_SLIME = withAttributes(registerEntity("spiked_ice_slime", EntityType.Builder.<SpikedSlime>of((type, level) -> new SpikedSlime(type, level, SlimeSpikeEntity.Variant.ICE), MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(31).armor(4).attackDamage(6).armorToughness(1)
                    .projectile(ModEntities.SLIME_SPIKE, projectile -> projectile.attackDamage(1))
                    .build());

    // 沙漠：史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> DESERT_SLIME = withAttributes(registerSlime("desert_slime", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(21).armor(3).attackDamage(6).armorToughness(1)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 3.2)
                            .multiply(LibAttributes.getAttackDamage(), 2.6)
                            .multiply(Attributes.ARMOR, 3))
                    .build());

    // 地牢与腐化：史莱姆及分裂体
    public static final DeferredHolder<EntityType<?>, EntityType<CorruptSlime>> CORRUPT_SLIME = withAttributes(registerEntity("corrupt_slime", EntityType.Builder.of(CorruptSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(88).armor(9).attackDamage(28).armorToughness(4).build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> SLIMELING = withAttributes(registerEntity("slimeling", EntityType.Builder.<BaseSlime>of((type, level) -> new BaseSlime(type, level, false, 1), MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(51).armor(5).attackDamage(20).armorToughness(2).build());
    public static final DeferredHolder<EntityType<?>, EntityType<Slimer>> SLIMER = withAttributes(registerEntity("slimer", EntityType.Builder.of(Slimer::new, MobCategory.MONSTER).sized(1.0F, 0.9F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.creature().maxHealth(31).armor(9).attackDamage(23).followRange(48).attackKnockback(1).knockbackResistance(0.28).armorToughness(4)
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> WINGLESS_SLIMER = withAttributes(registerEntity("wingless_slimer", EntityType.Builder.<BaseSlime>of(BaseSlime::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(46).armor(9).attackDamage(23).followRange(48).attackKnockback(1).knockbackResistance(0.3).armorToughness(4)
                    .phase(GamePhase.PLANTERA, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    // 血腥之地：猩红史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> CRIMSLIME = withAttributes(registerEntity("crimslime", EntityType.Builder.<BaseSlime>of((type, level) -> new BaseSlime(type, level, false, 2, false, true), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(104).armor(12).attackDamage(31.2).armorToughness(5).build());

    // 神圣之地：夜明史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<LuminousSlime>> LUMINOUS_SLIME = withAttributes(registerEntity("luminous_slime", EntityType.Builder.of(LuminousSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(93).armor(14).attackDamage(36.4).armorToughness(6).build());

    // 丛林水域：热带史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<TropicSlime>> TROPIC_SLIME = withAttributes(registerEntity("tropic_slime", EntityType.Builder.of(TropicSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(13).armor(1).attackDamage(5).build());

    // 地牢：地牢史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> DUNGEON_SLIME = withAttributes(registerSlime("dungeon_slime", false, 3),
            () -> CreatureAttributeBuilder.slime().maxHealth(78).armor(1).attackDamage(15.6)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 1.1)
                            .multiply(LibAttributes.getAttackDamage(), 0.9))
                    .build());

    // 地狱：熔岩史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<LavaSlime>> LAVA_SLIME = withAttributes(registerEntity("lava_slime", EntityType.Builder.of(LavaSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.slime().maxHealth(30).armor(5).attackDamage(10).armorToughness(2)
                    .phase(GamePhase.WALL_OF_FLESH, phase -> phase
                            .multiply(Attributes.MAX_HEALTH, 2.2)
                            .multiply(LibAttributes.getAttackDamage(), 1.8)
                            .multiply(Attributes.ARMOR, 2))
                    .build());

    // 血腥之地（血肉）：血肉史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<FleshSlime>> FLESH_SLIME = withAttributes(registerEntity("flesh_slime", EntityType.Builder.of(FleshSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune()),
            () -> CreatureAttributeBuilder.slime().maxHealth(50).armor(3).attackDamage(14).armorToughness(1).build());

    // Boss 附属生物：尖刺史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_SLIME = withAttributes(registerEntity("spiked_slime", EntityType.Builder.<SpikedSlime>of(SpikedSlime::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(26).armor(3).attackDamage(7).armorToughness(1)
                    .projectile(ModEntities.SLIME_SPIKE, projectile -> projectile.attackDamage(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeRescue>> OLD_SHAKING_CHEST = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("old_shaking_chest", EntityType.Builder.<TownSlimeRescue>of((type, level) -> new TownSlimeRescue(type, level, false), MobCategory.CREATURE).sized(0.8F, 0.8F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(250).armor(5).attackDamage(0).movementSpeed(0.2).knockbackResistance(0.3).armorToughness(2).build());
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeRescue>> CLUMSY_BALLOON_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(registerEntity("clumsy_balloon_slime", EntityType.Builder.<TownSlimeRescue>of((type, level) -> new TownSlimeRescue(type, level, true), MobCategory.CREATURE).sized(0.8F, 0.8F))),
            () -> CreatureAttributeBuilder.creature().maxHealth(1).attackDamage(0).movementSpeed(0.0).build());

    // 丛林：绿饺史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> GREEN_DUMPLING_SLIME = withAttributes(registerSlime("green_dumpling_slime", false, 2),
            () -> CreatureAttributeBuilder.slime().maxHealth(25).armor(0).attackDamage(5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<SweetSlime>> SWEET_SLIME = withAttributes(registerEntity("sweet_slime", EntityType.Builder.of(SweetSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.slime().maxHealth(16).armor(0).attackDamage(0).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, id -> builder.build(id.toString()));
    }

    /// 属性声明汇入 {@link ModEntities#withAttributes}（同一个 `ATTRIBUTES` 表）。
    private static <T extends LivingEntity> DeferredHolder<EntityType<?>, EntityType<T>> withAttributes(DeferredHolder<EntityType<?>, EntityType<T>> type, Supplier<CreatureAttributeBuilder.Definition> attributes) {
        return ModEntities.withAttributes(type, attributes);
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerLand(String name, float w, float h) {
        return registerLand(name, w, h, BaseWarriorMonster.LandSoundProfile.ROUTINE);
    }

    private static DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> registerSkeleton(String name, float w, float h) {
        return registerSkeleton(name, w, h, MeleeSkeleton.BehaviorProfile.BLOOD_MOON_DOORS);
    }

    private static DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> registerSkeleton(String name, float width, float height, MeleeSkeleton.BehaviorProfile behaviorProfile) {
        return registerEntity(name, EntityType.Builder.<MeleeSkeleton>of((type, level) -> new MeleeSkeleton(type, level, false, behaviorProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    /// 注册需要哥布林专用浮水行为的人形敌怪。
    private static DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> registerGoblinLand(String name, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        return registerGoblinLand(name, width, height, defaultMainHand, animationProfile, GoblinMonster.DoorBehavior.NONE);
    }

    private static DeferredHolder<EntityType<?>, EntityType<GoblinMonster>> registerGoblinLand(String name, float width, float height, ItemStack defaultMainHand,
                                                                                             BaseWarriorMonster.LandAnimationProfile animationProfile, GoblinMonster.DoorBehavior doorBehavior) {
        return registerEntity(name, EntityType.Builder.<GoblinMonster>of(
                (type, level) -> new GoblinMonster(type, level, defaultMainHand, animationProfile, doorBehavior),
                MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<JellyFish>> registerJellyFish(String name, JellyFish.Profile profile) {
        return registerEntity(name, EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, profile), MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<Snatcher>> registerSnatcher(String name, Snatcher.Profile profile) {
        return registerEntity(name, EntityType.Builder.<Snatcher>of((type, level) -> new Snatcher(type, level, profile), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<ChargingMonster>> registerCharger(String name, float width, float height, double chargeSpeed, int windupTicks) {
        return registerEntity(name, EntityType.Builder.<ChargingMonster>of((type, level) -> new ChargingMonster(type, level, chargeSpeed, windupTicks), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String name, float width, float height, ItemStack defaultMainHand) {
        return registerHumanoidLand(name, width, height, defaultMainHand, BaseWarriorMonster.LandSoundProfile.ROUTINE);
    }

    private static DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String name, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerHumanoidLand(name, width, height, defaultMainHand, soundProfile, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE);
    }

    private static DeferredHolder<EntityType<?>, EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String name, float width, float height, ItemStack defaultMainHand, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        return registerEntity(name, EntityType.Builder.<HumanoidWarriorMonster>of((type, level) -> new HumanoidWarriorMonster(type, level, defaultMainHand, soundProfile, animationProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<FlyingFishMonster>> registerFlyingFish(String name, float width, float height, FlyingFishMonster.PursuitProfile pursuitProfile) {
        return registerEntity(name, EntityType.Builder.<FlyingFishMonster>of(
                (type, level) -> new FlyingFishMonster(type, level, pursuitProfile, 0.2),
                MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerAcceleratingLand(String name, float width, float height, double pursuitSpeedBonus, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerAcceleratingLand(name, width, height, pursuitSpeedBonus, meleeSpeed, ignoreLightPathCost, animationProfile, soundProfile, BaseWarriorMonster.DoorBehavior.NONE);
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWarriorMonster>> registerAcceleratingLand(String name, float width, float height, double pursuitSpeedBonus, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.DoorBehavior doorBehavior) {
        return registerEntity(name, EntityType.Builder.<BaseWarriorMonster>of((type, level) -> new BaseWarriorMonster(type, level, pursuitSpeedBonus, animationProfile, soundProfile, meleeSpeed, ignoreLightPathCost, doorBehavior), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile) {
        return registerJumpingLand(name, width, height, profile, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY);
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        return registerEntity(
                name,
                EntityType.Builder.<JumpingWarriorMonster>of(
                                (type, level) -> new JumpingWarriorMonster(
                                        type, level, profile, animationProfile),
                        MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerJumpingLand(name, width, height, profile, animationProfile, soundProfile, 1.0);
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, double meleeSpeed) {
        return registerEntity(name, EntityType.Builder.<JumpingWarriorMonster>of((type, level) -> new JumpingWarriorMonster(type, level, profile, animationProfile, soundProfile, meleeSpeed), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, double meleeSpeed, boolean mummy) {
        return registerEntity(name, EntityType.Builder.<JumpingWarriorMonster>of((type, level) -> new JumpingWarriorMonster(type, level, profile, animationProfile, soundProfile, meleeSpeed, mummy), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<DarkCaster>> registerCaster(String name, float width, float height, DarkCaster.Profile profile) {
        return registerEntity(name, EntityType.Builder.<DarkCaster>of((type, level) -> new DarkCaster(type, level, profile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseWormPart>> registerWormSegment(String name) {
        return registerEntity(name, EntityType.Builder.of(BaseWormPart::new, MobCategory.MISC)
                .sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave());
    }

    private static DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> registerWorm(String name, int segments, float width, float height, SimpleWormMonster.Role role, Supplier<EntityType<BaseWormPart>> segmentType) {
        return registerEntity(name, EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, segments, role, segmentType.get()), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10).updateInterval(1));
    }

    private static DeferredHolder<EntityType<?>, EntityType<SimpleWormMonster>> registerWorm(String name, float width, float height,
                                                                                            SimpleWormMonster.Role role, SimpleWormMonster.Anatomy anatomy,
                                                                                            Supplier<EntityType<BaseWormPart>> segmentType) {
        return registerEntity(name, EntityType.Builder.<SimpleWormMonster>of(
                        (type, level) -> new SimpleWormMonster(type, level, role, anatomy, segmentType.get()), MobCategory.MONSTER)
                .sized(width, height).clientTrackingRange(10).updateInterval(1));
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
}
