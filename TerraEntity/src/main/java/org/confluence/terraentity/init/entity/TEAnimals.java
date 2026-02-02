package org.confluence.terraentity.init.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.model.GeoModelTextureDecoration;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.confluence.terraentity.client.entity.model.VariantTexModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.client.entity.renderer.mob.FairyRenderer;
import org.confluence.terraentity.entity.animal.*;
import org.confluence.terraentity.init.TEEntities;

import java.util.List;

public class TEAnimals {

    public static final DeferredHolder<EntityType<?>, EntityType<Duck>> DUCK = TEEntities.ENTITIES.register("duck", () -> EntityType.Builder.of(Duck::new, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).passengerAttachments(new Vec3(0.0, 0.7, -0.1)).clientTrackingRange(10).build(TEEntities.Key("duck")));
    public static final DeferredHolder<EntityType<?>, EntityType<Bunny>> BUNNY = TEEntities.ENTITIES.register("bunny", () -> EntityType.Builder.of(Bunny::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build(TEEntities.Key("bunny")));
    public static final DeferredHolder<EntityType<?>, EntityType<JewelBunny>> JEWEL_BUNNY = TEEntities.ENTITIES.register("jewel_bunny", () -> EntityType.Builder.of(JewelBunny::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build(TEEntities.Key("jewel_bunny")));
    public static final DeferredHolder<EntityType<?>, EntityType<BoomBunny>> EXPLOSIVE_BUNNY = TEEntities.ENTITIES.register("explosive_bunny", () -> EntityType.Builder.of(BoomBunny::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build(TEEntities.Key("boom_bunny")));
    public static final DeferredHolder<EntityType<?>, EntityType<Squirrel>> SQUIRREL = TEEntities.ENTITIES.register("squirrel", () -> EntityType.Builder.of(Squirrel::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build(TEEntities.Key("squirrel")));
    public static final DeferredHolder<EntityType<?>, EntityType<JewelSquirrel>> JEWEL_SQUIRREL = TEEntities.ENTITIES.register("jewel_squirrel", () -> EntityType.Builder.of(JewelSquirrel::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build(TEEntities.Key("jewel_squirrel")));
    public static final DeferredHolder<EntityType<?>, EntityType<Bird>> BIRD = TEEntities.ENTITIES.register("bird", () -> EntityType.Builder.of(Bird::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build(TEEntities.Key("bird")));
    public static final DeferredHolder<EntityType<?>, EntityType<Bird>> BLUE_JAY = TEEntities.ENTITIES.register("blue_jay", () -> EntityType.Builder.of(Bird::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build(TEEntities.Key("blue_jay")));
    public static final DeferredHolder<EntityType<?>, EntityType<Bird>> CARDINAL = TEEntities.ENTITIES.register("cardinal", () -> EntityType.Builder.of(Bird::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8).build(TEEntities.Key("cardinal")));
    public static final DeferredHolder<EntityType<?>, EntityType<Crab>> CRAB = TEEntities.registerCreature("crab", Crab::new, 0.5F, 0.3F);

    // 昆虫
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleAnimal>> GLOWING_SNAIL = TEEntities.registerCreature("glowing_snail", SimpleAnimal::new, 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleAnimal>> GRUBBY = TEEntities.registerCreature("grubby", SimpleAnimal::new, 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleAnimal>> MAGGOT = TEEntities.registerCreature("maggot", SimpleAnimal::new, 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleAnimal>> MAGMA_SNAIL = TEEntities.registerCreature("magma_snail", SimpleAnimal::new, 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleAnimal>> SLUGGY = TEEntities.registerCreature("sluggy", SimpleAnimal::new, 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleAnimal>> SNAIL = TEEntities.registerCreature("snail", SimpleAnimal::new, 0.5F, 0.3F);

    public static final DeferredHolder<EntityType<?>, EntityType<BirdVariantAnimal>> BUTTERFLY = TEEntities.registerCreature("butterfly", (e, l) -> new BirdVariantAnimal(e, l, VariantsTextureMaps.butterflyTextures), 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<Bird>> HELL_BUTTERFLY = TEEntities.registerCreature("hell_butterfly", Bird::new, 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<Bird>> PRISMATIC_LACEWING = TEEntities.registerCreature("prismatic_lacewing", Bird::new, 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<BirdVariantAnimal>> DRAGONFLY = TEEntities.registerCreature("dragonfly", (e, l) -> new BirdVariantAnimal(e, l, VariantsTextureMaps.dragonflyTextures), 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<Fairy>> FAIRY = TEEntities.registerCreature("fairy", (e, l) -> new Fairy(e, l, VariantsTextureMaps.fairyTextures), 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<Fairy>> FEALING = TEEntities.registerCreature("fealing", (e, l) -> new Fairy(e, l, VariantsTextureMaps.fealingTextures), 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<JumpableVariantAnimal>> GRASSHOPPER = TEEntities.registerCreature("grasshopper", (e, l) -> new JumpableVariantAnimal(e, l, VariantsTextureMaps.grasshopperTextures), 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<BirdVariantAnimal>> LADYBUG = TEEntities.registerCreature("ladybug", (e, l) -> new BirdVariantAnimal(e, l, VariantsTextureMaps.ladybugTextures), 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleVariantAnimal>> SCORPION = TEEntities.registerCreature("scorpion", (e, l) -> new SimpleVariantAnimal(e, l, VariantsTextureMaps.scorpionTextures), 0.5F, 0.3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleVariantAnimal>> WORM = TEEntities.registerCreature("worm", (e, l) -> new SimpleVariantAnimal(e, l, VariantsTextureMaps.wormTextures), 0.5F, 0.3F);

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DUCK.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<Duck>(DUCK.getId().withPrefix("animal/"), true).setHeadName("bone3"), false, 1, -0.01f));
        event.registerEntityRenderer(BUNNY.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<Bunny>(BUNNY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(JEWEL_BUNNY.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<JewelBunny>(BUNNY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(EXPLOSIVE_BUNNY.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<BoomBunny>(EXPLOSIVE_BUNNY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(SQUIRREL.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<Squirrel>(SQUIRREL.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(JEWEL_SQUIRREL.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<JewelSquirrel>(SQUIRREL.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(BIRD.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<Bird>(BIRD.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(BLUE_JAY.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<Bird>(BLUE_JAY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(CARDINAL.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<Bird>(CARDINAL.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(CRAB.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<SimpleAnimal>(CRAB.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));

        // 昆虫
        event.registerEntityRenderer(GLOWING_SNAIL.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<SimpleAnimal>(GLOWING_SNAIL.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(GRUBBY.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<SimpleAnimal>(GRUBBY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(MAGGOT.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<SimpleAnimal>(MAGGOT.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(MAGMA_SNAIL.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<SimpleAnimal>(MAGMA_SNAIL.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(SLUGGY.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<SimpleAnimal>(SLUGGY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(SNAIL.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<SimpleAnimal>(SNAIL.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));

        event.registerEntityRenderer(BUTTERFLY.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<>(BUTTERFLY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(HELL_BUTTERFLY.get(), c -> new GeoNormalRenderer<>(c, new GeoModelTextureDecoration<>(new GeoNormalModel<Bird>(BUTTERFLY.getId().withPrefix("animal/"), true).setHeadName("head"), TerraEntity.space("animal/butterfly/hell_butterfly")), false, 1, 0));
        event.registerEntityRenderer(PRISMATIC_LACEWING.get(), c -> new GeoNormalRenderer<>(c, new GeoModelTextureDecoration<>(new GeoNormalModel<Bird>(BUTTERFLY.getId().withPrefix("animal/"), true).setHeadName("head"), TerraEntity.space("animal/butterfly/prismatic_lacewing")), false, 1, 0));
        event.registerEntityRenderer(DRAGONFLY.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<>(DRAGONFLY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(FAIRY.get(), c -> new FairyRenderer<>(c, new VariantTexModel<Fairy>(FAIRY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0).setBoneToGlow(List.of("Outline", "Outline2", "Outline3", "Outline4", "Outline5"), List.of("Body", "Internal", "Internal2", "Internal3", "Internal4")));
        event.registerEntityRenderer(FEALING.get(), c -> new FairyRenderer<>(c, new VariantTexModel<Fairy>(FAIRY.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0).setBoneToGlow(List.of("Outline", "Outline2", "Outline3", "Outline4", "Outline5"), List.of("Body", "Internal", "Internal2", "Internal3", "Internal4")));
        event.registerEntityRenderer(GRASSHOPPER.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<>(GRASSHOPPER.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(LADYBUG.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<>(LADYBUG.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(SCORPION.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<>(SCORPION.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));
        event.registerEntityRenderer(WORM.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<>(WORM.getId().withPrefix("animal/"), true).setHeadName("head"), false, 1, 0));

    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DUCK.get(), Chicken.createAttributes().build());
        event.put(BUNNY.get(), Rabbit.createAttributes().add(Attributes.SAFE_FALL_DISTANCE, 6).build());
        event.put(JEWEL_BUNNY.get(), Rabbit.createAttributes().add(Attributes.SAFE_FALL_DISTANCE, 6).build());
        event.put(EXPLOSIVE_BUNNY.get(), Rabbit.createAttributes().add(Attributes.SAFE_FALL_DISTANCE, 6).build());
        event.put(SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(JEWEL_SQUIRREL.get(), Squirrel.createAttributes().build());
        event.put(BIRD.get(), Bird.createAttributes().build());
        event.put(BLUE_JAY.get(), Bird.createAttributes().build());
        event.put(CARDINAL.get(), Bird.createAttributes().build());
        event.put(CRAB.get(), SimpleAnimal.createInsectAttributes().build());

        // 昆虫
        event.put(GLOWING_SNAIL.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());
        event.put(GRUBBY.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());
        event.put(MAGGOT.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());
        event.put(MAGMA_SNAIL.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());
        event.put(SLUGGY.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());
        event.put(SNAIL.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());

        event.put(BUTTERFLY.get(), Bird.createInspectAttributes().build());
        event.put(HELL_BUTTERFLY.get(), Bird.createInspectAttributes().build());
        event.put(PRISMATIC_LACEWING.get(), Bird.createInspectAttributes().build());
        event.put(DRAGONFLY.get(), Bird.createInspectAttributes().build());
        event.put(FAIRY.get(), Bird.createInspectAttributes().build());
        event.put(FEALING.get(), Bird.createInspectAttributes().build());
        event.put(GRASSHOPPER.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());
        event.put(LADYBUG.get(), Bird.createInspectAttributes().build());
        event.put(SCORPION.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());
        event.put(WORM.get(), SimpleAnimal.createInsectAttributes().add(Attributes.FALL_DAMAGE_MULTIPLIER, 0).build());
    }

    public static void spawnPlacementRegister(RegisterSpawnPlacementsEvent event) {
        event.register(DUCK.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BUNNY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(JEWEL_BUNNY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EXPLOSIVE_BUNNY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SQUIRREL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(JEWEL_SQUIRREL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BIRD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BLUE_JAY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CARDINAL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CRAB.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(GLOWING_SNAIL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GRUBBY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MAGGOT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MAGMA_SNAIL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SLUGGY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SNAIL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(BUTTERFLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(HELL_BUTTERFLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PRISMATIC_LACEWING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DRAGONFLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(FAIRY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(FEALING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GRASSHOPPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LADYBUG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SCORPION.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(WORM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, wormSpawnRules(), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static SpawnPlacements.SpawnPredicate<SimpleVariantAnimal> wormSpawnRules() {
        return Animal::checkAnimalSpawnRules; // confluence mixin here
    }

    public static void register() {
    }
}
