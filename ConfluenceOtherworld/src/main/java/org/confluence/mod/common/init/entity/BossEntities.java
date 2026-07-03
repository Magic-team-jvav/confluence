package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.*;

public class BossEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    public static final RegistryObject<EntityType<KingSlime>> KING_SLIME = registerEntity("king_slime", EntityType.Builder.of(KingSlime::new, MobCategory.MONSTER).sized(3.0F, 3.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EyeOfCthulhu>> EYE_OF_CTHULHU = registerEntity("eye_of_cthulhu", EntityType.Builder.of(EyeOfCthulhu::new, MobCategory.MONSTER).sized(2.5F, 2.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<ServantOfCthulhu>> SERVANT_OF_CTHULHU = registerEntity("servant_of_cthulhu", EntityType.Builder.of(ServantOfCthulhu::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EaterOfWorlds>> EATER_OF_WORLDS = registerEntity("eater_of_worlds", EntityType.Builder.of(EaterOfWorlds::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<QueenBee>> QUEEN_BEE = registerEntity("queen_bee", EntityType.Builder.of(QueenBee::new, MobCategory.MONSTER).sized(2.0F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BrainOfCthulhu>> BRAIN_OF_CTHULHU = registerEntity("brain_of_cthulhu", EntityType.Builder.of(BrainOfCthulhu::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CreeperOfCthulhu>> CREEPER_OF_CTHULHU = registerEntity("creeper_of_cthulhu", EntityType.Builder.of(CreeperOfCthulhu::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Skeletron>> SKELETRON = registerEntity("skeletron", EntityType.Builder.of(Skeletron::new, MobCategory.MONSTER).sized(1.5F, 2.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SkeletronHand>> SKELETRON_HAND = registerEntity("skeletron_hand", EntityType.Builder.of(SkeletronHand::new, MobCategory.MONSTER).sized(1.2F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DungeonGuardian>> DUNGEON_GUARDIAN = registerEntity("dungeon_guardian", EntityType.Builder.of(DungeonGuardian::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<TheDestroyer>> THE_DESTROYER = registerEntity("the_destroyer", EntityType.Builder.of(TheDestroyer::new, MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<TheTwins>> THE_TWINS = registerEntity("the_twins", EntityType.Builder.of(TheTwins::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Retinazer>> RETINAZER = registerEntity("retinazer", EntityType.Builder.of(Retinazer::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Spazmatism>> SPAZMATISM = registerEntity("spazmatism", EntityType.Builder.of(Spazmatism::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SkeletronPrime>> SKELETRON_PRIME = registerEntity("skeletron_prime", EntityType.Builder.of(SkeletronPrime::new, MobCategory.MONSTER).sized(1.5F, 2.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SkeletronPrimeArm>> SKELETRON_PRIME_ARM = registerEntity("skeletron_prime_arm", EntityType.Builder.of(SkeletronPrimeArm::new, MobCategory.MONSTER).sized(1.0F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WallOfFlesh>> WALL_OF_FLESH = registerEntity("wall_of_flesh", EntityType.Builder.of(WallOfFlesh::new, MobCategory.MONSTER).sized(8.0F, 8.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Hungry>> HUNGRY = registerEntity("hungry", EntityType.Builder.of(Hungry::new, MobCategory.MONSTER).sized(0.6F, 0.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Plantera>> PLANTERA = registerEntity("plantera", EntityType.Builder.of(Plantera::new, MobCategory.MONSTER).sized(3.0F, 3.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<PlanteraTentacle>> PLANTERA_TENTACLE = registerEntity("plantera_tentacle", EntityType.Builder.of(PlanteraTentacle::new, MobCategory.MONSTER).sized(0.3F, 2.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<LunaticCultist>> LUNATIC_CULTIST = registerEntity("lunatic_cultist", EntityType.Builder.of(LunaticCultist::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<PhantasmDragon>> PHANTASM_DRAGON = registerEntity("phantasm_dragon", EntityType.Builder.of(PhantasmDragon::new, MobCategory.MONSTER).sized(2.0F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HillOfFlesh>> HILL_OF_FLESH = registerEntity("hill_of_flesh", EntityType.Builder.of(HillOfFlesh::new, MobCategory.MONSTER).sized(3.0F, 3.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HillOfFleshEye>> HILL_OF_FLESH_EYE = registerEntity("hill_of_flesh_eye", EntityType.Builder.of(HillOfFleshEye::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HillOfFleshMouth>> HILL_OF_FLESH_MOUTH = registerEntity("hill_of_flesh_mouth", EntityType.Builder.of(HillOfFleshMouth::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DeerClops>> DEERCLOPS = registerEntity("deerclops", EntityType.Builder.of(DeerClops::new, MobCategory.MONSTER));

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return PortDeferredRegisterExtension.register(ENTITIES, name, id -> builder.build(id.toString()));
    }
}
