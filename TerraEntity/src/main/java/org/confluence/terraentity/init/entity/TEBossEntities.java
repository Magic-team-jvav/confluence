package org.confluence.terraentity.init.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.client.boss.model.SkeletronHandModel;
import org.confluence.terraentity.client.boss.renderer.*;
import org.confluence.terraentity.client.entity.renderer.CrownOfKingSlimeModelRenderer;
import org.confluence.terraentity.client.entity.renderer.GeoMotionBlurRenderer;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.client.entity.renderer.mob.KingSlimeRenderer;
import org.confluence.terraentity.entity.blur.PosRotMotionBlurRenderer;
import org.confluence.terraentity.entity.boss.*;
import org.confluence.terraentity.entity.boss.hillofflesh.HillOfFlesh;
import org.confluence.terraentity.entity.boss.plantera.Plantera;
import org.confluence.terraentity.entity.boss.plantera.PlanteraHook;
import org.confluence.terraentity.entity.boss.plantera.PlanteraTentacle;
import org.confluence.terraentity.entity.boss.skeletronprime.SkeletronPrime;
import org.confluence.terraentity.entity.boss.skeletronprime.SkeletronPrimePart;
import org.confluence.terraentity.entity.boss.thedestroyer.TheDestroyer;
import org.confluence.terraentity.entity.boss.thedestroyer.TheDestroyerPart;
import org.confluence.terraentity.entity.boss.thedestroyer.TheDestroyerProbe;
import org.confluence.terraentity.entity.boss.thetwins.Retinazer;
import org.confluence.terraentity.entity.boss.thetwins.Spazmatism;
import org.confluence.terraentity.entity.boss.thetwins.TheTwins;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.entity.model.CrownOfKingSlimeModelEntity;
import org.confluence.terraentity.entity.util.AttBuilder;
import org.confluence.terraentity.init.TEEntities;

public class TEBossEntities {
    public static final DeferredHolder<EntityType<?>, EntityType<KingSlime>> KING_SLIME = TEEntities.ENTITIES.register("king_slime", () -> EntityType.Builder.<KingSlime>of(KingSlime::new, MobCategory.MONSTER).sized(0.6f, 0.6f).clientTrackingRange(10).build(TEEntities.Key("king_slime")));
    public static final DeferredHolder<EntityType<?>, EntityType<CrownOfKingSlimeModelEntity>> CROWN_OF_KING_SLIME_MODEL = TEEntities.ENTITIES.register("crown_of_king_slime_model", () -> EntityType.Builder.<CrownOfKingSlimeModelEntity>of(CrownOfKingSlimeModelEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10).build(TEEntities.Key("crown_of_king_slime_model")));
    public static final DeferredHolder<EntityType<?>, EntityType<EyeOfCthulhu>> EYE_OF_CTHULHU = TEEntities.registerMonster("eye_of_cthulhu", EyeOfCthulhu::new, 2.6F, 2.6F);
    public static final DeferredHolder<EntityType<?>, EntityType<EaterOfWorldsSegment>> EATER_OF_WORLDS_SEGMENT = TEEntities.registerMonster("eater_of_worlds_segment", EaterOfWorldsSegment::new, 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<EaterOfWorlds>> EATER_OF_WORLDS = TEEntities.registerMonster("eater_of_worlds", EaterOfWorlds::new, 3F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<BrainOfCthulhu>> BRAIN_OF_CTHULHU = TEEntities.registerMonster("brain_of_cthulhu", BrainOfCthulhu::new, 4F, 4F);
    public static final DeferredHolder<EntityType<?>, EntityType<BrainFake>> BRAIN_FAKE = TEEntities.registerMonster("brain_fake", BrainFake::new, 4F, 4F);
    public static final DeferredHolder<EntityType<?>, EntityType<Deerclops>> DEERCLOPS = TEEntities.registerMonster("deerclops", Deerclops::new, 3F, 7F);
    public static final DeferredHolder<EntityType<?>, EntityType<QueenBee>> QUEEN_BEE = TEEntities.registerMonster("queen_bee", QueenBee::new, 2.5F, 3F);
    public static final DeferredHolder<EntityType<?>, EntityType<Skeletron>> SKELETRON = TEEntities.registerMonster("skeletron", Skeletron::new, 2.3F, 2.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronHand>> SKELETRON_HAND = TEEntities.registerMonster("skeletron_hand", SkeletronHand::new, 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<WallOfFlesh>> WALL_OF_FLESH = TEEntities.registerMonster("wall_of_flesh", WallOfFlesh::new, 0.1F, 0.1F);
    public static final DeferredHolder<EntityType<?>, EntityType<DungeonGuardian>> DUNGEON_GUARDIAN = TEEntities.registerMonster("dungeon_guardian", DungeonGuardian::new, 2.5F, 2.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<HillOfFlesh>> HILL_OF_FLESH = TEEntities.registerMonster("hill_of_flesh", HillOfFlesh::new, 10F, 10F);

    public static final DeferredHolder<EntityType<?>, EntityType<Spazmatism>> SPAZMATISM = TEEntities.registerMonster("spazmatism", Spazmatism::new, 2.6F, 2.6F);
    public static final DeferredHolder<EntityType<?>, EntityType<Retinazer>> RETINAZER = TEEntities.registerMonster("retinazer", Retinazer::new, 2.6F, 2.6F);
    public static final DeferredHolder<EntityType<?>, EntityType<TheTwins>> THE_TWINS = TEEntities.registerMonster("the_twins", TheTwins::new, 2.6F, 2.6F);

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronPrime>> SKELETRON_PRIME = TEEntities.registerMonster("skeletron_prime", SkeletronPrime::new, 2.6F, 2.6F);
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronPrimePart>> SKELETRON_PRIME_PART = TEEntities.registerMonster("skeletron_prime_part", SkeletronPrimePart::new, 2.6F, 2.6F);


    public static final DeferredHolder<EntityType<?>, EntityType<TheDestroyer>> THE_DESTROYER = TEEntities.registerMonster("the_destroyer", TheDestroyer::new, 3F, 3F);
    public static final DeferredHolder<EntityType<?>, EntityType<TheDestroyerPart>> THE_DESTROYER_PART = TEEntities.registerMonster("the_destroyer_part", TheDestroyerPart::new, 3F, 3F);
    public static final DeferredHolder<EntityType<?>, EntityType<TheDestroyerProbe>> THE_DESTROYER_PROBE = TEEntities.registerMonster("the_destroyer_probe", TheDestroyerProbe::new, 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<Plantera>> PLANTERA = TEEntities.registerMonster("plantera", Plantera::new, 10F, 10F);
    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraHook>> PLANTERA_HOOK = TEEntities.registerMonster("plantera_hook", PlanteraHook::new, 1.25F, 1.25F);
    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraTentacle>> PLANTERA_TENTACLE = TEEntities.registerMonster("plantera_tentacle", PlanteraTentacle::new, 2F, 2F);

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TEBossEntities.CROWN_OF_KING_SLIME_MODEL.get(), CrownOfKingSlimeModelRenderer::new);

        event.registerEntityRenderer(TEBossEntities.KING_SLIME.get(), KingSlimeRenderer::new);
        event.registerEntityRenderer(TEBossEntities.EYE_OF_CTHULHU.get(), c -> new GeoMotionBlurRenderer<>(c, new GeoBossModel<>(TEBossEntities.EYE_OF_CTHULHU), true, 1, 1.5f).setMotionBlurRenderer(r -> new PosRotMotionBlurRenderer<>(r.isIfRotX(), r.getOffsetY())));
        event.registerEntityRenderer(TEBossEntities.EATER_OF_WORLDS_SEGMENT.get(), c -> new EaterOfWorldSegmentRenderer(c, 2.2f, 0f));
        event.registerEntityRenderer(TEBossEntities.EATER_OF_WORLDS.get(), c -> new GeoNormalRenderer<>(c, new GeoBossModel<>(TEBossEntities.EATER_OF_WORLDS), true, 2.2f, 0));
        event.registerEntityRenderer(TEBossEntities.BRAIN_OF_CTHULHU.get(), c -> new BrainOfCthulhuRenderer(c, new GeoBossModel<>(TEBossEntities.BRAIN_OF_CTHULHU)));
        event.registerEntityRenderer(TEMonsterEntities.VISUAL_NEURON.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.VISUAL_NEURON.getId(), true));
        event.registerEntityRenderer(TEBossEntities.BRAIN_FAKE.get(), c -> new BrainOfCthulhuRenderer(c, new GeoBossModel<>(TEBossEntities.BRAIN_OF_CTHULHU)));
        event.registerEntityRenderer(TEBossEntities.DEERCLOPS.get(), c -> new DeerclopsRenderer(c, new GeoBossModel<>(TEBossEntities.DEERCLOPS)));
        event.registerEntityRenderer(TEBossEntities.QUEEN_BEE.get(), c -> new QueenBeeRenderer(c, new GeoBossModel<>(TEBossEntities.QUEEN_BEE)));
        event.registerEntityRenderer(TEBossEntities.SKELETRON.get(), c -> new SkeletronRenderer(c, new GeoBossModel<>(TEBossEntities.SKELETRON)));
        event.registerEntityRenderer(TEBossEntities.SKELETRON_HAND.get(), c -> new SkeletronHandRenderer(c, new SkeletronHandModel()));
        event.registerEntityRenderer(TEBossEntities.DUNGEON_GUARDIAN.get(), c -> new SkeletronRenderer(c, new GeoBossModel<>(TEBossEntities.SKELETRON)));

        event.registerEntityRenderer(TEBossEntities.WALL_OF_FLESH.get(), WallOfFleshRenderer::new);
        event.registerEntityRenderer(TEBossEntities.HILL_OF_FLESH.get(), HillOfFleshRenderer::new);

        event.registerEntityRenderer(TEBossEntities.SPAZMATISM.get(), c -> new GeoNormalRenderer<>(c, new GeoBossModel<>(TEBossEntities.SPAZMATISM), true, 1, 0f));
        event.registerEntityRenderer(TEBossEntities.RETINAZER.get(), c -> new GeoNormalRenderer<>(c, new GeoBossModel<>(TEBossEntities.RETINAZER), true, 1, 0f));
        event.registerEntityRenderer(TEBossEntities.THE_TWINS.get(), TheTwinsRenderer::new);

        event.registerEntityRenderer(TEBossEntities.SKELETRON_PRIME.get(), c->new SkeletronPrimeRenderer(c, TEBossEntities.SKELETRON_PRIME));
        event.registerEntityRenderer(TEBossEntities.SKELETRON_PRIME_PART.get(), c->new SkeletronPrimePartRenderer(c, TEBossEntities.SKELETRON_PRIME.getId().withPrefix("boss/")));

        event.registerEntityRenderer(TEBossEntities.THE_DESTROYER.get(), DestroyerRenderer::new);
        event.registerEntityRenderer(TEBossEntities.THE_DESTROYER_PART.get(), DestroyerPartRenderer::new);
        event.registerEntityRenderer(TEBossEntities.THE_DESTROYER_PROBE.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.VISUAL_NEURON.getId(), true));

        event.registerEntityRenderer(TEBossEntities.PLANTERA.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.VISUAL_NEURON.getId(), true, 2.2f, 0));
        event.registerEntityRenderer(TEBossEntities.PLANTERA_HOOK.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.VISUAL_NEURON.getId(), true, 2.2f, 0));
        event.registerEntityRenderer(TEBossEntities.PLANTERA_TENTACLE.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.VISUAL_NEURON.getId(), true, 1f, 0));
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(TEBossEntities.KING_SLIME.get(), KingSlime.createSlimeAttributes().build());
        event.put(TEBossEntities.EYE_OF_CTHULHU.get(), AttBuilder.createBoss(4, 728, 12).build());
        event.put(TEBossEntities.EATER_OF_WORLDS_SEGMENT.get(), AttBuilder.createBoss(4, 50, 6).build());
        event.put(TEBossEntities.EATER_OF_WORLDS.get(), AttBuilder.createBoss(11.5, 54, 4).build());
        event.put(TEBossEntities.BRAIN_OF_CTHULHU.get(), AttBuilder.createBoss(14, 552, 14).knockResistance(0.5f).build());

        event.put(TEBossEntities.BRAIN_FAKE.get(), AbstractTerraBossBase.createAttributes().build());
        event.put(TEBossEntities.DEERCLOPS.get(), AttBuilder.createBoss(10.4, 3094, 10).moveSpeed(0.4f).build());
        event.put(TEBossEntities.QUEEN_BEE.get(), AttBuilder.createBoss(14, 1237, 8).build());
        event.put(TEBossEntities.SKELETRON.get(), AttBuilder.createBoss(18.2, 2288, 10).build());
        event.put(TEBossEntities.SKELETRON_HAND.get(), AttBuilder.createBoss(10, 405, 4).build());

        event.put(TEBossEntities.WALL_OF_FLESH.get(), AttBuilder.createBoss(39, 3096, 6).build());
        event.put(TEBossEntities.DUNGEON_GUARDIAN.get(), AttBuilder.createBoss(9999, 9999, 9999).build());

        event.put(TEBossEntities.HILL_OF_FLESH.get(), AttBuilder.createBoss(3824, 6).build());

        event.put(TEBossEntities.SPAZMATISM.get(), AttBuilder.createBoss(22, 8970, 10).build());
        event.put(TEBossEntities.RETINAZER.get(), AttBuilder.createBoss(19, 7800, 10).build());
        event.put(TEBossEntities.THE_TWINS.get(), AttBuilder.createBoss(0, 16770, 0).build());

        event.put(TEBossEntities.SKELETRON_PRIME.get(), AttBuilder.createBoss(21, 10920, 6).build());
        event.put(TEBossEntities.SKELETRON_PRIME_PART.get(), AttBuilder.createBoss(8, 2080, 26).build());


        event.put(TEBossEntities.THE_DESTROYER.get(), AttBuilder.createBoss(35, 23333, 2).build());
        event.put(TEBossEntities.THE_DESTROYER_PART.get(), AttBuilder.createBoss(66, 23333, 2).build());
        event.put(TEBossEntities.THE_DESTROYER_PROBE.get(), AttBuilder.createBoss(12, 100, 10).build());

        event.put(TEBossEntities.PLANTERA.get(), AttBuilder.createBoss(26, 10920, 36).build());
        event.put(TEBossEntities.PLANTERA_HOOK.get(), AttBuilder.createBoss(15.6, 1040, 24).build());
        event.put(TEBossEntities.PLANTERA_TENTACLE.get(), AttBuilder.createBoss(15.6, PlanteraTentacle.MAX_HEALTH, 20).build());
    }

    public static void register() {}
}
