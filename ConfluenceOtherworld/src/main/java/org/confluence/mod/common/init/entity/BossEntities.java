package org.confluence.mod.common.init.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.*;
import org.confluence.mod.common.entity.model.CrownOfKingSlimeModelEntity;
import org.confluence.mod.common.entity.monster.CreatureAttributeBuilder;

import static org.confluence.mod.common.init.entity.ModEntities.withAttributes;

///
///
public final class BossEntities {
    private BossEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Skeletron>> SKELETRON = withAttributes(registerEntity("skeletron", EntityType.Builder.of(Skeletron::new, MobCategory.MONSTER).sized(2.3F, 2.3F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(2288).armor(5).attackDamage(18.2).followRange(300).armorToughness(2)
                    .state(Skeletron.CombatState.FLOATING, state -> state.bonus(Attributes.ARMOR, mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? 25 * ((Skeletron) mob).getRemainingHandCount() : 0).duration(267))
                    .state(Skeletron.CombatState.SPINNING, state -> state.attribute(Attributes.ARMOR, mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? 25 * ((Skeletron) mob).getRemainingHandCount() : 0).multiply(Attributes.ATTACK_DAMAGE, mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? 1.3 : 1).duration(133))
                    .state(Skeletron.CombatState.ENRAGED, state -> state.attackDamage(9999).armor(9999))
                    .projectile(ModEntities.SKELETRON_SKULL, projectile -> projectile.damage(6).speed(0.001).lifetime(100))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronHand>> SKELETRON_HAND = withAttributes(registerEntity("skeletron_hand", EntityType.Builder.of(SkeletronHand::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(405).armor(7).attackDamage(10).followRange(300).armorToughness(2).build());

    // ------------------------------------------------------------------
    // 地表：史莱姆王及王冠模型
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<KingSlime>> KING_SLIME = withAttributes(registerEntity("king_slime", EntityType.Builder.of(KingSlime::new, MobCategory.MONSTER).sized(3.0F, 3.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(728).armor(5).attackDamage(16.5).followRange(100).attackKnockback(2.2).armorToughness(2)
                    .state(KingSlime.CombatPhase.NORMAL, state -> state.chargeSpeed(mob -> LibUtils.switchByDifficulty(mob.level(), mob.blockPosition(), 1.1F, 1.35F, 1.55F, 1.8F)).attackCount(mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) && mob.getHealth() / mob.getMaxHealth() < 0.5F ? 2 : 1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<CrownOfKingSlimeModelEntity>> CROWN_OF_KING_SLIME_MODEL = registerEntity("crown_of_king_slime_model", EntityType.Builder.<CrownOfKingSlimeModelEntity>of(CrownOfKingSlimeModelEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10));

    // ------------------------------------------------------------------
    // 地表夜间：克苏鲁之眼及仆从
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<EyeOfCthulhu>> EYE_OF_CTHULHU = withAttributes(registerEntity("eye_of_cthulhu", EntityType.Builder.of(EyeOfCthulhu::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(946).armor(6).attackDamage(8).followRange(300).attackKnockback(2).armorToughness(2)
                    .state(EyeOfCthulhu.CombatState.IDLE, state -> state.multiply(Attributes.ATTACK_DAMAGE, mob -> (((EyeOfCthulhu) mob).getCombatStage() == 2 ? 1.5 : 1.0)).attribute(Attributes.ARMOR, mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2 ? 0 : mob.getAttribute(Attributes.ARMOR).getBaseValue()))
                    .state(EyeOfCthulhu.CombatState.STARING, state -> state.multiply(Attributes.ATTACK_DAMAGE, mob -> (((EyeOfCthulhu) mob).getCombatStage() == 2 ? 1.5 : 1.0)).attribute(Attributes.ARMOR, mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2 ? 0 : mob.getAttribute(Attributes.ARMOR).getBaseValue()))
                    .state(EyeOfCthulhu.CombatState.DASH_WINDUP, state -> state.multiply(Attributes.ATTACK_DAMAGE, mob -> (((EyeOfCthulhu) mob).getCombatStage() == 2 ? 1.5 : 1.0)).attribute(Attributes.ARMOR, mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2 ? 0 : mob.getAttribute(Attributes.ARMOR).getBaseValue()))
                    .state(EyeOfCthulhu.CombatState.DASHING, state -> state.multiply(Attributes.ATTACK_DAMAGE, mob -> (((EyeOfCthulhu) mob).getCombatStage() == 2 ? 1.5 : 1.0) * 1.5).attribute(Attributes.ARMOR, mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2 ? 0 : mob.getAttribute(Attributes.ARMOR).getBaseValue()))
                    .state(EyeOfCthulhu.CombatState.TRANSFORMING, state -> state.armor(0))
                    .state(EyeOfCthulhu.CombatState.LEAVING, state -> state.multiply(Attributes.ATTACK_DAMAGE, mob -> (((EyeOfCthulhu) mob).getCombatStage() == 2 ? 1.5 : 1.0)).attribute(Attributes.ARMOR, mob -> ((EyeOfCthulhu) mob).getCombatStage() == 2 ? 0 : mob.getAttribute(Attributes.ARMOR).getBaseValue()))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<ServantOfCthulhu>> SERVANT_OF_CTHULHU = withAttributes(registerEntity("servant_of_cthulhu", EntityType.Builder.of(ServantOfCthulhu::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(10).armor(0).attackDamage(3).followRange(30).attackKnockback(0.5).knockbackResistance(0.3).movementSpeed(0.25).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<DeerClops>> DEERCLOPS = withAttributes(registerEntity("deerclops", EntityType.Builder.of(DeerClops::new, MobCategory.MONSTER).sized(3.0F, 7.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(3094).armor(5).attackDamage(10.4).followRange(300).movementSpeed(0.4).armorToughness(2)
                    .state(DeerClops.CombatState.ATTACK, state -> state.windupTicks(12).duration(15).attackInterval(30))
                    .projectile(ModEntities.THROWN_ICE_PROJECTILE, projectile -> projectile.damage(10))
                    .projectile(ModEntities.SHADOW_HAND, projectile -> projectile.damage(10))
                    .projectile(ModEntities.ICE_PILLAR, projectile -> projectile.damage(10))
                    .build());

    // ------------------------------------------------------------------
    // 腐化：世界吞噬者及体节
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<EaterOfWorlds>> EATER_OF_WORLDS = withAttributes(registerEntity("eater_of_worlds", EntityType.Builder.of(EaterOfWorlds::new, MobCategory.MONSTER).sized(3.0F, 2.0F).clientTrackingRange(24).updateInterval(1)),
            () -> CreatureAttributeBuilder.boss().maxHealth(54).armor(2).attackDamage(11.5).followRange(300).armorToughness(0)
                    .projectile(ModEntities.VILE_SPIT_PROJECTILE, projectile -> projectile.damage(5))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BossWormPart>> EATER_OF_WORLDS_SEGMENT = withAttributes(registerEntity("boss_worm_segment", EntityType.Builder.of(BossWormPart::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(32).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(50).armor(3).attackDamage(4).followRange(300).armorToughness(1).build());

    // ------------------------------------------------------------------
    // 机械 Boss：毁灭者、体节与探测怪
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<TheDestroyer>> THE_DESTROYER = withAttributes(registerEntity("the_destroyer", EntityType.Builder.of(TheDestroyer::new, MobCategory.MONSTER).sized(3.0F, 3.0F).clientTrackingRange(32).updateInterval(1)),
            () -> CreatureAttributeBuilder.boss().maxHealth(31200).armor(0).attackDamage(72.8).followRange(300)
                    .projectile(ModEntities.DESTROYER_LASER, projectile -> projectile.damage(18))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BossWormPart>> THE_DESTROYER_PART = withAttributes(registerEntity("the_destroyer_part", EntityType.Builder.of(BossWormPart::new, MobCategory.MONSTER).sized(3.0F, 3.0F).clientTrackingRange(32).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(31200).armor(1).attackDamage(66).followRange(96).build());

    public static final DeferredHolder<EntityType<?>, EntityType<TheDestroyerProbe>> THE_DESTROYER_PROBE = withAttributes(registerEntity("the_destroyer_probe", EntityType.Builder.of(TheDestroyerProbe::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(100).armor(5).attackDamage(12).followRange(64).armorToughness(2)
                    .projectile(ModEntities.DESTROYER_LASER, projectile -> projectile.attackDamage(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<BrainOfCthulhu>> BRAIN_OF_CTHULHU = withAttributes(registerEntity("brain_of_cthulhu", EntityType.Builder.of(BrainOfCthulhu::new, MobCategory.MONSTER).sized(4.0F, 4.0F).clientTrackingRange(10).updateInterval(1)),
            () -> CreatureAttributeBuilder.boss().maxHealth(552).armor(7).attackDamage(14).followRange(300).attackKnockback(2.5).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BrainFake>> BRAIN_FAKE = registerEntity("brain_fake", EntityType.Builder.of(BrainFake::new, MobCategory.MISC).sized(4.0F, 4.0F).clientTrackingRange(10).updateInterval(1).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<QueenBee>> QUEEN_BEE = withAttributes(registerEntity("queen_bee", EntityType.Builder.of(QueenBee::new, MobCategory.MONSTER).sized(2.5F, 3.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(1237).armor(4).attackDamage(14).followRange(300).attackKnockback(2).armorToughness(1)
                    .state(QueenBee.CombatState.IDLE, state -> state.duration(50))
                    .state(QueenBee.CombatState.SUMMONING_BEES, state -> state.duration(60).moveSpeed(mob -> 1 + ((QueenBee) mob).enrageStrength() * 0.5))
                    .state(QueenBee.CombatState.SUMMONING_STINGERS, state -> state.duration(mob -> mob.getHealth() / mob.getMaxHealth() < 0.3F ? 50 : 60))
                    .state(QueenBee.CombatState.PRE_DASH_IDLE, state -> state.duration(20).moveSpeed(mob -> 1.2 * (1 + ((QueenBee) mob).enrageStrength() * 0.5)))
                    .state(QueenBee.CombatState.PRE_DASH, state -> state.duration(mob -> Math.max(5, 15 - (int) (((QueenBee) mob).enrageStrength() * 3))))
                    .state(QueenBee.CombatState.DASHING, state -> state.duration(50).chargeSpeed(mob -> 2 * (1 + ((QueenBee) mob).enrageStrength() * 0.5)).attackCount(mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? Math.min(6, 3 + (int) ((1 - mob.getHealth() / mob.getMaxHealth()) * 4)) : 3))
                    .state(QueenBee.Temperament.CALM, state -> state.bonus(Attributes.ARMOR, mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? 20 * (1.0 - mob.getHealth() / mob.getMaxHealth()) : 0).attackInterval(mob -> Math.max(2, 10 - (int) (((QueenBee) mob).enrageStrength() * 2) - (LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? (int) ((1 - mob.getHealth() / mob.getMaxHealth()) * 3) : 0))))
                    .state(QueenBee.Temperament.ENRAGED, state -> state.bonus(Attributes.ARMOR, mob -> LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? 20 * (1.0 - mob.getHealth() / mob.getMaxHealth()) : 0).attackInterval(mob -> Math.max(2, 10 - (int) (((QueenBee) mob).enrageStrength() * 2) - (LibUtils.isAtLeastExpert(mob.level(), mob.blockPosition()) ? (int) ((1 - mob.getHealth() / mob.getMaxHealth()) * 3) : 0))))
                    .projectile(ModEntities.HORNET_STINGER, projectile -> projectile.attackDamage(1).speed(1).inaccuracy(5).lifetime(100))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<DungeonGuardian>> DUNGEON_GUARDIAN = withAttributes(registerEntity("dungeon_guardian", EntityType.Builder.of(DungeonGuardian::new, MobCategory.MONSTER).sized(2.5F, 2.5F).clientTrackingRange(10).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(9999).armor(9999).attackDamage(9999).followRange(100).build());

    public static final DeferredHolder<EntityType<?>, EntityType<WallOfFlesh>> WALL_OF_FLESH = withAttributes(registerEntity("wall_of_flesh", EntityType.Builder.of(WallOfFlesh::new, MobCategory.MONSTER).sized(8.0F, 8.0F).clientTrackingRange(48)),
            () -> CreatureAttributeBuilder.boss().maxHealth(3096).armor(9).attackDamage(39).followRange(120).movementSpeed(0.125).armorToughness(3)
                    .state(WallOfFlesh.CombatState.WOUNDED, state -> state.multiply(Attributes.MOVEMENT_SPEED, 1.45))
                    .projectile(ModEntities.WALL_OF_FLESH_LASER, projectile -> projectile.damage(12))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<TheTwins>> THE_TWINS = withAttributes(registerEntity("the_twins", EntityType.Builder.of(TheTwins::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(1).armor(5).attackDamage(15).followRange(0).armorToughness(2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Retinazer>> RETINAZER = withAttributes(registerEntity("retinazer", EntityType.Builder.of(Retinazer::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(7800).armor(5).attackDamage(19).followRange(300).movementSpeed(0.3).flyingSpeed(0.6).armorToughness(2)
                    .state(AbstractTwinEye.Form.TRANSFORMED, state -> state.multiply(Attributes.ARMOR, 2))
                    .projectile(ModEntities.RETINAZER_LASER, projectile -> projectile.attackDamage(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<Spazmatism>> SPAZMATISM = withAttributes(registerEntity("spazmatism", EntityType.Builder.of(Spazmatism::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(8970).armor(5).attackDamage(22).followRange(300).movementSpeed(0.3).flyingSpeed(0.6).armorToughness(2)
                    .state(AbstractTwinEye.Form.TRANSFORMED, state -> state.multiply(Attributes.ARMOR, 2.8))
                    .projectile(ModEntities.SPAZMATISM_FLAME, projectile -> projectile.attackDamage(1))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronPrime>> SKELETRON_PRIME = withAttributes(registerEntity("skeletron_prime", EntityType.Builder.of(SkeletronPrime::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(10920).armor(11).attackDamage(21).followRange(300).armorToughness(4)
                    .state(SkeletronPrime.CombatState.HOVERING, state -> state.duration(200).attackInterval(20))
                    .state(SkeletronPrime.CombatState.SPINNING, state -> state.multiply(Attributes.ATTACK_DAMAGE, 2).multiply(Attributes.ARMOR, 2).duration(50).chargeSpeed(0.8).attackInterval(20))
                    .state(SkeletronPrime.CombatState.ENRAGED, state -> state.attackDamage(9999).armor(9999).chargeSpeed(2).attackInterval(20))
                    .projectile(ModEntities.PRIME_LASER, projectile -> projectile.damage(8).speed(1.55).lifetime(80))
                    .projectile(ModEntities.PRIME_CANNONBALL, projectile -> projectile.damage(22).speed(0.72).inaccuracy(0.03).lifetime(80))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronPrimeArm>> SKELETRON_PRIME_PART = withAttributes(registerEntity("skeletron_prime_arm", EntityType.Builder.of(SkeletronPrimeArm::new, MobCategory.MONSTER).sized(2.6F, 2.6F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(3510).armor(12).attackDamage(22.88).followRange(64).armorToughness(5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Plantera>> PLANTERA = withAttributes(registerEntity("plantera", EntityType.Builder.of(Plantera::new, MobCategory.MONSTER).sized(10.0F, 10.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(10920).armor(17).attackDamage(26).followRange(300).armorToughness(7)
                    .state(Plantera.Temperament.CALM, state -> state.moveSpeed(mob -> ((Plantera) mob).getPhase() == 0 ? 0.1 : 0.2))
                    .state(Plantera.Temperament.ENRAGED, state -> state.multiply(Attributes.ATTACK_DAMAGE, 2).multiply(Attributes.ARMOR, mob -> ((Plantera) mob).getPhase() == 0 ? 2 : 4).moveSpeed(0.2))
                    .projectile(ModEntities.PLANTERA_SEED, projectile -> projectile.damage(mob -> (((Plantera) mob).isEnraged() ? 2 : 1) * 19))
                    .projectile(ModEntities.PLANTERA_THORN_BALL, projectile -> projectile.damage(mob -> (((Plantera) mob).isEnraged() ? 2 : 1) * 28))
                    .projectile(ModEntities.PLANTERA_SPORE, projectile -> projectile.damage(mob -> (((Plantera) mob).isEnraged() ? 2 : 1) * 19))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraHook>> PLANTERA_HOOK = withAttributes(registerEntity("plantera_hook", EntityType.Builder.of(PlanteraHook::new, MobCategory.MONSTER).sized(1.25F, 1.25F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(2080).armor(11).attackDamage(31.2).followRange(64).armorToughness(5).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraTentacle>> PLANTERA_TENTACLE = withAttributes(registerEntity("plantera_tentacle", EntityType.Builder.of(PlanteraTentacle::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(10).updateInterval(1).noSave()),
            () -> CreatureAttributeBuilder.boss().maxHealth(520).armor(9).attackDamage(35.88).followRange(64).armorToughness(4).build());

    public static final DeferredHolder<EntityType<?>, EntityType<LunaticCultist>> LUNATIC_CULTIST = withAttributes(registerEntity("lunatic_cultist", EntityType.Builder.of(LunaticCultist::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(12480).armor(19).attackDamage(20).followRange(64).armorToughness(8)
                    .state(LunaticCultist.CombatState.WOUNDED, state -> state.multiply(Attributes.ARMOR, 27.0 / 42.0))
                    .state(LunaticCultist.CombatState.CASTING, state -> state.attackInterval(50, 69))
                    .state(LunaticCultist.CombatState.RELOCATING, state -> state.duration(20).attackInterval(60, 99))
                    .state(LunaticCultist.CombatState.RITUAL, state -> state.duration(120).attackCount(mob -> mob.getHealth() / mob.getMaxHealth() < 0.5F ? 6 : 5))
                    .projectile(ModEntities.CULTIST_FIREBALL, projectile -> projectile.damage(16).speed(1.15).inaccuracy(0).lifetime(120))
                    .projectile(ModEntities.CULTIST_ICE_MIST, projectile -> projectile.damage(12).speed(0.72).inaccuracy(0).lifetime(120))
                    .projectile(ModEntities.CULTIST_LIGHTNING_ORB, projectile -> projectile.damage(14).speed(0.88).inaccuracy(0).lifetime(120))
                    .projectile(ModEntities.ANCIENT_LIGHT, projectile -> projectile.damage(16).lifetime(100))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<LunaticCultistClone>> LUNATIC_CULTIST_CLONE = withAttributes(registerEntity("lunatic_cultist_clone", EntityType.Builder.of(LunaticCultistClone::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(1).armor(16).armorToughness(7).attackDamage(8).followRange(64).movementSpeed(0.3).flyingSpeed(0.6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PhantasmDragon>> PHANTASM_DRAGON = withAttributes(registerEntity("phantasm_dragon", EntityType.Builder.of(PhantasmDragon::new, MobCategory.MONSTER).sized(2.0F, 1.5F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(150).armor(7).attackDamage(12).followRange(48).knockbackResistance(0.5).movementSpeed(0.3).flyingSpeed(0.6).armorToughness(3).build());

    public static final DeferredHolder<EntityType<?>, EntityType<HillOfFlesh>> HILL_OF_FLESH = withAttributes(registerEntity("hill_of_flesh", EntityType.Builder.of(HillOfFlesh::new, MobCategory.MONSTER).sized(10.0F, 10.0F).clientTrackingRange(10)),
            () -> CreatureAttributeBuilder.boss().maxHealth(3824).armor(3).attackDamage(1).followRange(75).armorToughness(1)
                    .state(HillOfFlesh.DamageZone.ATTACHED, state -> state.attackDamage(10))
                    .state(HillOfFlesh.DamageZone.INNER, state -> state.attackDamage(40))
                    .state(HillOfFlesh.DamageZone.OUTER, state -> state.attackDamage(40))
                    .projectile(ModEntities.HILL_FIRE_BOUND, projectile -> projectile.damage(10))
                    .projectile(ModEntities.HILL_LAVA_PILLAR, projectile -> projectile.damage(17))
                    .build());

    public static final DeferredHolder<EntityType<?>, EntityType<PrimeEnderDragon>> PRIME_ENDER_DRAGON = withAttributes(registerEntity("prime_ender_dragon", EntityType.Builder.of(PrimeEnderDragon::new, MobCategory.MONSTER).sized(10.0F, 10.0F).clientTrackingRange(12)),
            () -> CreatureAttributeBuilder.boss().maxHealth(4624).attackDamage(32).armor(9).movementSpeed(1).followRange(300).armorToughness(4)
                    .state(PrimeEnderDragon.AttackType.LASER, state -> state.attackDamage(5)).build());

    public static final DeferredHolder<EntityType<?>, EntityType<PrimeEnderDragonPart>> PRIME_ENDER_DRAGON_PART = registerEntity("prime_ender_dragon_part", EntityType.Builder.of(PrimeEnderDragonPart::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(12).updateInterval(1).noSave());

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, id -> builder.build(id.toString()));
    }
}
