package org.confluence.mod.common.init.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.*;
import org.confluence.mod.common.entity.model.CrownOfKingSlimeModelEntity;
import org.confluence.mod.common.entity.monster.CreatureAttributeBuilder;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.entity.monster.CreatureAttributeBuilder.DynamicValue.*;
import static org.confluence.mod.common.init.entity.ModEntities.withAttributes;

///
///
public final class BossEntities {
    private BossEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Skeletron>> SKELETRON = withAttributes(registerEntity("skeletron", "骷髅王", EntityType.Builder.of(Skeletron::new, MobCategory.MONSTER).sized(2.3F, 2.3F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(2288).armor(5).armorToughness(2).attackDamage(19).followRange(300)
                    .state(Skeletron.CombatState.FLOATING, state -> state
                            .armor(5, 11, value(mob -> ((Skeletron) mob).getRemainingHandCount()).expertOnly(0))
                            .armorToughness(2, 5, value(mob -> ((Skeletron) mob).getRemainingHandCount()).expertOnly(0))
                            .duration(267))
                    .state(Skeletron.CombatState.SPINNING, state -> state
                            .armor(0, 11.25, value(mob -> ((Skeletron) mob).getRemainingHandCount()).expertOnly(0))
                            .armorToughness(0, 5, value(mob -> ((Skeletron) mob).getRemainingHandCount()).expertOnly(0))
                            .attribute(Attributes.ATTACK_DAMAGE, value(24).expertOnly(19))
                            .duration(133))
                    .state(Skeletron.CombatState.ENRAGED, state -> state.attackDamage(9999).armor(9999))
                    .projectile(ModEntities.SKELETRON_SKULL, projectile -> projectile.damage(18).speed(0.001).lifetime(100))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronHand>> SKELETRON_HAND = withAttributes(registerEntity("skeletron_hand", "骷髅王之手", EntityType.Builder.of(SkeletronHand::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(406).armor(7).armorToughness(2).attackDamage(12).followRange(300).build());

    // ------------------------------------------------------------------
    // 地表：史莱姆王及王冠模型
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<KingSlime>> KING_SLIME = withAttributes(registerEntity("king_slime", "史莱姆王", EntityType.Builder.of(KingSlime::new, MobCategory.MONSTER).sized(3.0F, 3.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(728).armor(5).armorToughness(2).attackDamage(17).followRange(100).attackKnockback(2.2)
                    .state(KingSlime.CombatPhase.NORMAL, state -> state
                            .chargeSpeed(byDifficulty(1.1F, 1.35F, 1.55F, 1.8F)).attackCount(belowHealth(0.5F, 2, 1).expertOnly(1).asInt()))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CrownOfKingSlimeModelEntity>> CROWN_OF_KING_SLIME_MODEL = registerEntity("crown_of_king_slime_model", "史莱姆王王冠模型", EntityType.Builder.<CrownOfKingSlimeModelEntity>of(CrownOfKingSlimeModelEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10));

    // ------------------------------------------------------------------
    // 地表夜间：克苏鲁之眼及仆从
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<EyeOfCthulhu>> EYE_OF_CTHULHU = withAttributes(registerEntityWithEnglishName("eye_of_cthulhu", "克苏鲁之眼", "Eye of Cthulhu", EntityType.Builder.of(EyeOfCthulhu::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(947).armor(6).armorToughness(2).attackDamage(8).followRange(300).attackKnockback(2)
                    .state(EyeOfCthulhu.DefenseState.WOUNDED, state -> state.attackDamage(2))
                    .state(EyeOfCthulhu.DefenseState.CRITICAL, state -> state.attackDamage(4))
                    .state(EyeOfCthulhu.CombatState.IDLE, state -> state
                            .multiply(Attributes.ATTACK_DAMAGE, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, belowHealth(0.04F, 11, 10), value(8)).divide(8))
                            .attribute(Attributes.ARMOR, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, value(0), value(mob -> mob.getAttribute(Attributes.ARMOR).getBaseValue())))
                            .attribute(Attributes.ARMOR_TOUGHNESS, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, 0, 2)))
                    .state(EyeOfCthulhu.CombatState.STARING, state -> state
                            .multiply(Attributes.ATTACK_DAMAGE, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, belowHealth(0.04F, 11, 10), value(8)).divide(8))
                            .attribute(Attributes.ARMOR, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, value(0), value(mob -> mob.getAttribute(Attributes.ARMOR).getBaseValue())))
                            .attribute(Attributes.ARMOR_TOUGHNESS, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, 0, 2)))
                    .state(EyeOfCthulhu.CombatState.DASH_WINDUP, state -> state
                            .multiply(Attributes.ATTACK_DAMAGE, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, belowHealth(0.04F, 11, 10), value(8)).divide(8))
                            .attribute(Attributes.ARMOR, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, value(0), value(mob -> mob.getAttribute(Attributes.ARMOR).getBaseValue())))
                            .attribute(Attributes.ARMOR_TOUGHNESS, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, 0, 2)))
                    .state(EyeOfCthulhu.CombatState.DASHING, state -> state
                            .multiply(Attributes.ATTACK_DAMAGE, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, belowHealth(0.04F, 11, 10), value(8)).divide(8).scale(1.5))
                            .attribute(Attributes.ARMOR, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, value(0), value(mob -> mob.getAttribute(Attributes.ARMOR).getBaseValue())))
                            .attribute(Attributes.ARMOR_TOUGHNESS, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, 0, 2)))
                    .state(EyeOfCthulhu.CombatState.TRANSFORMING, state -> state.armor(0).attribute(Attributes.ARMOR_TOUGHNESS, 0))
                    .state(EyeOfCthulhu.CombatState.LEAVING, state -> state
                            .multiply(Attributes.ATTACK_DAMAGE, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, belowHealth(0.04F, 11, 10), value(8)).divide(8))
                            .attribute(Attributes.ARMOR, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, value(0), value(mob -> mob.getAttribute(Attributes.ARMOR).getBaseValue())))
                            .attribute(Attributes.ARMOR_TOUGHNESS, when(mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2, 0, 2)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ServantOfCthulhu>> SERVANT_OF_CTHULHU = withAttributes(registerEntityWithEnglishName("servant_of_cthulhu", "克苏鲁之仆", "Servant of Cthulhu", EntityType.Builder.of(ServantOfCthulhu::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(4).armor(0).armorToughness(0).attackDamage(7).followRange(30).attackKnockback(0.5).knockbackResistance(0.3).movementSpeed(0.25).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<DeerClops>> DEERCLOPS = withAttributes(registerEntity("deerclops", "独眼巨鹿", EntityType.Builder.of(DeerClops::new, MobCategory.MONSTER).sized(3.0F, 7.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(3094).armor(5).armorToughness(2).attackDamage(11).followRange(300).movementSpeed(0.4)
                    .state(DeerClops.CombatState.ATTACK, state -> state.windupTicks(12).duration(15).attackInterval(30))
                    .projectile(ModEntities.THROWN_ICE_PROJECTILE, projectile -> projectile.damage(19))
                    .projectile(ModEntities.SHADOW_HAND, projectile -> projectile.damage(11))
                    .projectile(ModEntities.ICE_PILLAR, projectile -> projectile.damage(14))
                    .build());

    // ------------------------------------------------------------------
    // 腐化：世界吞噬者及体节
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<EaterOfWorlds>> EATER_OF_WORLDS = withAttributes(registerEntityWithEnglishName("eater_of_worlds", "世界吞噬怪", "Eater of Worlds", EntityType.Builder.of(EaterOfWorlds::new, MobCategory.MONSTER).sized(3.0F, 2.0F).clientTrackingRange(24).updateInterval(1)),
            () -> CreatureAttributeBuilder.boss().maxHealth(55).armor(2).armorToughness(0).attackDamage(13).followRange(300)
                    .state(EaterOfWorlds.BodySection.TAIL, state -> state.attackDamage(5).armor(5).attribute(Attributes.ARMOR_TOUGHNESS, 2))
                    .projectile(ModEntities.VILE_SPIT_PROJECTILE, projectile -> projectile.damage(5))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BossWormPart>> EATER_OF_WORLDS_SEGMENT = withAttributes(registerEntity("boss_worm_segment", "世界吞噬者体节", EntityType.Builder.of(BossWormPart::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(32).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(55).armor(3).armorToughness(1).attackDamage(6).followRange(300).build());

    // ------------------------------------------------------------------
    // 机械 Boss：毁灭者、体节与探测怪
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<TheDestroyer>> THE_DESTROYER = withAttributes(registerEntity("the_destroyer", "毁灭者", EntityType.Builder.of(TheDestroyer::new, MobCategory.MONSTER).sized(TheDestroyer.BODY_WIDTH * TheDestroyer.HITBOX_SCALE, TheDestroyer.BODY_HEIGHT * TheDestroyer.HITBOX_SCALE).clientTrackingRange(32).updateInterval(1)),
            () -> CreatureAttributeBuilder.boss().maxHealth(31200).armor(0).armorToughness(0).attackDamage(73).followRange(300)
                    .state(TheDestroyer.BodySection.BODY, state -> state.attackDamage(25).armor(14).attribute(Attributes.ARMOR_TOUGHNESS, 6))
                    .state(TheDestroyer.BodySection.TAIL, state -> state.attackDamage(18).armor(16).attribute(Attributes.ARMOR_TOUGHNESS, 7))
                    .projectile(ModEntities.DESTROYER_LASER, projectile -> projectile.damage(19))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<TheDestroyerProbe>> THE_DESTROYER_PROBE = withAttributes(registerEntity("the_destroyer_probe", "毁灭者探测器", EntityType.Builder.of(TheDestroyerProbe::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(78).armor(9).armorToughness(4).attackDamage(21).followRange(64)
                    .projectile(ModEntities.DESTROYER_LASER, projectile -> projectile.damage(23))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BrainOfCthulhu>> BRAIN_OF_CTHULHU = withAttributes(registerEntityWithEnglishName("brain_of_cthulhu", "克苏鲁之脑", "Brain of Cthulhu", EntityType.Builder.of(BrainOfCthulhu::new, MobCategory.MONSTER).sized(4.0F, 4.0F).clientTrackingRange(10).updateInterval(1)),
            () -> CreatureAttributeBuilder.boss().maxHealth(553).armor(7).armorToughness(2).attackDamage(15).followRange(300).attackKnockback(2.5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BrainFake>> BRAIN_FAKE = registerEntity("brain_fake", "克苏鲁之脑幻象", EntityType.Builder.of(BrainFake::new, MobCategory.MISC).sized(4.0F, 4.0F).clientTrackingRange(10).updateInterval(1).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<QueenBee>> QUEEN_BEE = withAttributes(registerEntity("queen_bee", "蜂王", EntityType.Builder.of(QueenBee::new, MobCategory.MONSTER).sized(2.5F, 3.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(1238).armor(4).armorToughness(1).attackDamage(15).followRange(300).attackKnockback(2)
                    .state(QueenBee.CombatState.IDLE, state -> state.duration(50))
                    .state(QueenBee.CombatState.SUMMONING_BEES, state -> state.duration(60).moveSpeed(value(mob -> ((QueenBee) mob).enrageStrength()).scale(0.5).add(1)))
                    .state(QueenBee.CombatState.SUMMONING_STINGERS, state -> state.duration(belowHealth(0.3F, 50, 60).asInt()))
                    .state(QueenBee.CombatState.PRE_DASH_IDLE, state -> state.duration(20).moveSpeed(value(mob -> ((QueenBee) mob).enrageStrength()).scale(0.5).add(1).scale(1.2)))
                    .state(QueenBee.CombatState.PRE_DASH, state -> state.duration(value(15).subtract(value(mob -> ((QueenBee) mob).enrageStrength()).scale(3).floor()).atLeast(5).asInt()))
                    .state(QueenBee.CombatState.DASHING, state -> state.duration(50).chargeSpeed(value(mob -> ((QueenBee) mob).enrageStrength()).scale(0.5).add(1).scale(2)).attackCount(mob -> ((QueenBee) mob).getDashCount()))
                    .state(QueenBee.Temperament.CALM, state -> state
                            .armor(4, 9, healthLoss().expertOnly(0))
                            .armorToughness(1, 4, healthLoss().expertOnly(0))
                            .attackInterval(value(10).subtract(value(mob -> ((QueenBee) mob).enrageStrength()).scale(2).floor())
                                    .subtract(healthLoss().expertOnly(0).scale(3).floor()).atLeast(2).asInt()))
                    .state(QueenBee.Temperament.ENRAGED, state -> state
                            .armor(4, 9, healthLoss().expertOnly(0))
                            .armorToughness(1, 4, healthLoss().expertOnly(0))
                            .attackInterval(value(10).subtract(value(mob -> ((QueenBee) mob).enrageStrength()).scale(2).floor())
                                    .subtract(healthLoss().expertOnly(0).scale(3).floor()).atLeast(2).asInt()))
                    .projectile(ModEntities.HORNET_STINGER, projectile -> projectile.damage(12).speed(1).inaccuracy(5).lifetime(100))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DungeonGuardian>> DUNGEON_GUARDIAN = withAttributes(registerEntity("dungeon_guardian", "地牢守卫", EntityType.Builder.of(DungeonGuardian::new, MobCategory.MONSTER).sized(2.5F, 2.5F).clientTrackingRange(10).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(9999).armor(9999).attackDamage(9999).followRange(100).build());

    public static final DeferredHolder<EntityType<?>, EntityType<WallOfFlesh>> WALL_OF_FLESH = withAttributes(registerEntityWithEnglishName("wall_of_flesh", "血肉墙", "Wall of Flesh", EntityType.Builder.of(WallOfFlesh::new, MobCategory.MONSTER).sized(8.0F, 8.0F).clientTrackingRange(48)),
            () -> CreatureAttributeBuilder.boss().maxHealth(2912).armor(9).armorToughness(3).attackDamage(39).followRange(120).movementSpeed(0.125)
                    .state(WallOfFlesh.BodySection.EYE, state -> state.armor(3).attribute(Attributes.ARMOR_TOUGHNESS, 1))
                    .state(WallOfFlesh.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 1.45))
                    .projectile(ModEntities.WALL_OF_FLESH_LASER, projectile -> projectile.damage(12))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Retinazer>> RETINAZER = withAttributes(registerEntity("retinazer", "激光眼", EntityType.Builder.of(Retinazer::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(16).updateInterval(1)),
            () -> CreatureAttributeBuilder.boss().maxHealth(7800).armor(5).armorToughness(2).attackDamage(20).followRange(300).movementSpeed(0.3).flyingSpeed(0.6)
                    .state(AbstractTwinEye.Form.TRANSFORMED, state -> state.armor(9).attackDamage(30).attribute(Attributes.ARMOR_TOUGHNESS, 4))
                    .projectile(ModEntities.RETINAZER_LASER, projectile -> projectile.damage(when(mob -> ((Retinazer) mob).isTransformed(), 18, 20)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Spazmatism>> SPAZMATISM = withAttributes(registerEntity("spazmatism", "魔焰眼", EntityType.Builder.of(Spazmatism::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(16).updateInterval(1)),
            () -> CreatureAttributeBuilder.boss().maxHealth(8970).armor(5).armorToughness(2).attackDamage(23).followRange(300).movementSpeed(0.3).flyingSpeed(0.6)
                    .state(AbstractTwinEye.Form.TRANSFORMED, state -> state.armor(13).attackDamage(34).attribute(Attributes.ARMOR_TOUGHNESS, 5))
                    .projectile(ModEntities.SPAZMATISM_FLAME, projectile -> projectile.damage(when(mob -> ((Spazmatism) mob).isTransformed(), 29, 23)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronPrime>> SKELETRON_PRIME = withAttributes(registerEntity("skeletron_prime", "机械骷髅王", EntityType.Builder.of(SkeletronPrime::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(10920).armor(11).armorToughness(4).attackDamage(21).followRange(300)
                    .state(SkeletronPrime.CombatState.HOVERING, state -> state.duration(200).attackInterval(20))
                    .state(SkeletronPrime.CombatState.SPINNING, state -> state.attackDamage(42).armor(22).attribute(Attributes.ARMOR_TOUGHNESS, 8).duration(50).chargeSpeed(0.8).attackInterval(20))
                    .state(SkeletronPrime.CombatState.ENRAGED, state -> state.attackDamage(9999).armor(9999).chargeSpeed(2).attackInterval(20))
                    .projectile(ModEntities.PRIME_LASER, projectile -> projectile.damage(26).speed(1.55).lifetime(80))
                    .projectile(ModEntities.PRIME_CANNONBALL, projectile -> projectile.damage(42).speed(0.72).inaccuracy(0.03).lifetime(80))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronPrimeArm>> SKELETRON_PRIME_PART = withAttributes(registerEntity("skeletron_prime_arm", "机械骷髅王机械臂", EntityType.Builder.of(SkeletronPrimeArm::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(3510).armor(16).armorToughness(6).attackDamage(23).followRange(64)
                    .state(SkeletronPrimeArm.ArmRole.LASER, state -> state.attribute(Attributes.MAX_HEALTH, 2340).attackDamage(13).armor(9).attribute(Attributes.ARMOR_TOUGHNESS, 4))
                    .state(SkeletronPrimeArm.ArmRole.SAW, state -> state.attribute(Attributes.MAX_HEALTH, 3510).attackDamage(25).armor(18).attribute(Attributes.ARMOR_TOUGHNESS, 7))
                    .state(SkeletronPrimeArm.ArmRole.VICE, state -> state.attribute(Attributes.MAX_HEALTH, 3510).attackDamage(23).armor(16).attribute(Attributes.ARMOR_TOUGHNESS, 6))
                    .state(SkeletronPrimeArm.ArmRole.CANNON, state -> state.attribute(Attributes.MAX_HEALTH, 2730).attackDamage(14).armor(11).attribute(Attributes.ARMOR_TOUGHNESS, 4))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Plantera>> PLANTERA = withAttributes(registerEntity("plantera", "世纪之花", EntityType.Builder.of(Plantera::new, MobCategory.MONSTER).sized(10.0F, 10.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(10920).armor(17).armorToughness(7).attackDamage(26).followRange(300)
                    .state(Plantera.Temperament.CALM, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, when(mob -> ((Plantera) mob).getPhase() == 0, 26, 37))
                            .attribute(Attributes.ARMOR, when(mob -> ((Plantera) mob).getPhase() == 0, 17, 5))
                            .attribute(Attributes.ARMOR_TOUGHNESS, when(mob -> ((Plantera) mob).getPhase() == 0, 7, 2))
                            .moveSpeed(when(mob -> ((Plantera) mob).getPhase() == 0, 0.1, 0.2)))
                    .state(Plantera.Temperament.ENRAGED, state -> state
                            .attribute(Attributes.ATTACK_DAMAGE, when(mob -> ((Plantera) mob).getPhase() == 0, 52, 73))
                            .attribute(Attributes.ARMOR, when(mob -> ((Plantera) mob).getPhase() == 0, 33, 18))
                            .attribute(Attributes.ARMOR_TOUGHNESS, 8)
                            .moveSpeed(0.2))
                    .projectile(ModEntities.PLANTERA_SEED, projectile -> projectile.damage(when(mob -> ((Plantera) mob).isEnraged(), 40, 20)))
                    .projectile(ModEntities.PLANTERA_THORN_BALL, projectile -> projectile.damage(when(mob -> ((Plantera) mob).isEnraged(), 58, 29)))
                    .projectile(ModEntities.PLANTERA_SPORE, projectile -> projectile.damage(when(mob -> ((Plantera) mob).isEnraged(), 74, 37)))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraHook>> PLANTERA_HOOK = withAttributes(registerEntity("plantera_hook", "世纪之花钩", EntityType.Builder.of(PlanteraHook::new, MobCategory.MONSTER).sized(1.25F, 1.25F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(2080).armor(11).armorToughness(4).attackDamage(32).followRange(64).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraTentacle>> PLANTERA_TENTACLE = withAttributes(registerEntity("plantera_tentacle", "世纪之花触手", EntityType.Builder.of(PlanteraTentacle::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(520).armor(9).armorToughness(4).attackDamage(36).followRange(64).build());

    public static final DeferredHolder<EntityType<?>, EntityType<LunaticCultist>> LUNATIC_CULTIST = withAttributes(registerEntity("lunatic_cultist", "拜月教邪教徒", EntityType.Builder.of(LunaticCultist::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(12480).armor(19).armorToughness(8).attackDamage(20).followRange(64)
                    .state(LunaticCultist.CombatState.WOUNDED, state -> state.armor(13).attribute(Attributes.ARMOR_TOUGHNESS, 5))
                    .state(LunaticCultist.CombatState.CASTING, state -> state.attackInterval(50, 69))
                    .state(LunaticCultist.CombatState.RELOCATING, state -> state.duration(20).attackInterval(60, 99))
                    .state(LunaticCultist.CombatState.RITUAL, state -> state.duration(120).attackCount(belowHealth(0.5F, 6, 5).asInt()))
                    .projectile(ModEntities.CULTIST_FIREBALL, projectile -> projectile.damage(21).speed(1.15).inaccuracy(0).lifetime(120))
                    .projectile(ModEntities.CULTIST_ICE_MIST, projectile -> projectile.damage(26).speed(0.72).inaccuracy(0).lifetime(120))
                    .projectile(ModEntities.CULTIST_LIGHTNING_ORB, projectile -> projectile.damage(32).speed(0.88).inaccuracy(0).lifetime(120))
                    .projectile(ModEntities.ANCIENT_LIGHT, projectile -> projectile.damage(47).lifetime(100))
                    .projectile(ModEntities.PROPHECYS_END, projectile -> projectile.damage(47).speed(0.8).lifetime(100))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<AncientDoom>> ANCIENT_DOOM = withAttributes(registerEntity("ancient_doom", "远古噩运妖", EntityType.Builder.of(AncientDoom::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(195).armor(0).attackDamage(0).followRange(64).knockbackResistance(1).build());
    public static final DeferredHolder<EntityType<?>, EntityType<AncientVision>> ANCIENT_VISION = withAttributes(registerEntity("ancient_vision", "远古幻影妖", EntityType.Builder.of(AncientVision::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(1040).armor(14).armorToughness(6).attackDamage(47).followRange(64).knockbackResistance(1).build());

    public static final DeferredHolder<EntityType<?>, EntityType<LunaticCultistClone>> LUNATIC_CULTIST_CLONE = withAttributes(registerEntity("lunatic_cultist_clone", "拜月教邪教徒幻影", EntityType.Builder.of(LunaticCultistClone::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(1).armor(16).armorToughness(7).attackDamage(8).followRange(64).movementSpeed(0.3).flyingSpeed(0.6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PhantasmDragon>> PHANTASM_DRAGON = withAttributes(registerEntity("phantasm_dragon", "幻影龙", EntityType.Builder.of(PhantasmDragon::new, MobCategory.MONSTER).sized(2.0F, 1.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(3900).armor(7).armorToughness(3).attackDamage(39).followRange(48).knockbackResistance(0.5).movementSpeed(0.3).flyingSpeed(0.6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<HillOfFlesh>> HILL_OF_FLESH = withAttributes(registerEntity("hill_of_flesh", "血肉山", EntityType.Builder.of(HillOfFlesh::new, MobCategory.MONSTER).sized(10.0F, 10.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(3824).armor(3).attackDamage(1).followRange(75).armorToughness(1)
                    .state(HillOfFlesh.DamageZone.ATTACHED, state -> state.attackDamage(10))
                    .state(HillOfFlesh.DamageZone.INNER, state -> state.attackDamage(40))
                    .state(HillOfFlesh.DamageZone.OUTER, state -> state.attackDamage(40))
                    .projectile(ModEntities.HILL_FIRE_BOUND, projectile -> projectile.damage(10))
                    .projectile(ModEntities.HILL_LAVA_PILLAR, projectile -> projectile.damage(17))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PrimeEnderDragon>> PRIME_ENDER_DRAGON = withAttributes(registerEntity("prime_ender_dragon", "本源末影龙", EntityType.Builder.of(PrimeEnderDragon::new, MobCategory.MONSTER).sized(10.0F, 10.0F).clientTrackingRange(12)),
            () -> CreatureAttributeBuilder.boss().maxHealth(4624).attackDamage(32).armor(9).movementSpeed(1).followRange(300).armorToughness(4)
                    .state(PrimeEnderDragon.AttackType.LASER, state -> state.attackDamage(5)).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PrimeEnderDragonPart>> PRIME_ENDER_DRAGON_PART = registerEntity("prime_ender_dragon_part", "本源末影龙部件", EntityType.Builder.of(PrimeEnderDragonPart::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(12).updateInterval(1).noSave());

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String registryId, String chineseName, EntityType.Builder<T> builder) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, id -> builder.build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntityWithEnglishName(String registryId, String chineseName, String englishName, EntityType.Builder<T> builder) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, id -> builder.build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
