package org.confluence.terraentity.init.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.model.*;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.client.entity.renderer.TheHungryRenderer;
import org.confluence.terraentity.client.entity.renderer.mob.*;
import org.confluence.terraentity.config.ClientConfig;
import org.confluence.terraentity.entity.monster.*;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import org.confluence.terraentity.entity.monster.humanoid.HumanoidMonster;
import org.confluence.terraentity.entity.monster.humanoid.Wraith;
import org.confluence.terraentity.entity.monster.prefab.AbstractPrefab;
import org.confluence.terraentity.entity.monster.prefab.AttributeBuilder;
import org.confluence.terraentity.entity.monster.prefab.FlyMonsterPrefab;
import org.confluence.terraentity.entity.monster.prefab.LandMonsterPrefab;
import org.confluence.terraentity.entity.monster.skeleton.MeleeSkeleton;
import org.confluence.terraentity.entity.monster.skeleton.RangeSkeleton;
import org.confluence.terraentity.entity.monster.slime.*;
import org.confluence.terraentity.entity.util.AttBuilder;
import org.confluence.terraentity.entity.util.SpawnPlacementChecks;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TESounds;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import java.util.List;
import java.util.function.Supplier;

public class TEMonsterEntities {
    // 史莱姆
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> BLUE_SLIME = registerSlime("blue_slime", 0x73bcf4, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> GREEN_SLIME = registerSlime("green_slime", 0x48E920, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> PINK_SLIME = registerSlime("pink_slime", 0xFF87B3, 1);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> DUNGEON_SLIME = registerSlime("dungeon_slime", 0x6d697b, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> CORRUPT_SLIME = registerSlime("corrupt_slime", 0xC91717, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> DESERT_SLIME = registerSlime("desert_slime", 0xDCC59a, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> JUNGLE_SLIME = registerSlime("jungle_slime", 0x9ae920, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> EVIL_SLIME = registerSlime("evil_slime", 0xFF00FF, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> ICE_SLIME = registerSlime("ice_slime", 0xB3F0EA, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> LAVA_SLIME = TEEntities.ENTITIES.register("lava_slime", () -> EntityType.Builder.<BaseSlime>of((entityType, level) -> new BaseSlime(entityType, level, 0xFFB150, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune().build(TEEntities.Key("lava_slime")));
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> LUMINOUS_SLIME = registerSlime("luminous_slime", 0xFFFFFF, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> CRIMSLIME = registerSlime("crimson_slime", 0x8B4949, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> PURPLE_SLIME = registerSlime("purple_slime", 0xf334f8, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> RED_SLIME = registerSlime("red_slime", 0xf83434, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> TROPIC_SLIME = registerSlime("tropic_slime", 0x73bcf4, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> YELLOW_SLIME = registerSlime("yellow_slime", 0xf8e234, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<HoneySlime>> HONEY_SLIME = TEEntities.ENTITIES.register("honey_slime", () -> EntityType.Builder.<HoneySlime>of((entityType, level) -> new HoneySlime(entityType, level, 0xf8e234), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("honey_slime")));
    public static final DeferredHolder<EntityType<?>, EntityType<BlackSlime>> BLACK_SLIME = TEEntities.ENTITIES.register("black_slime", () -> EntityType.Builder.of(BlackSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("black_slime")));
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> GREEN_DUMPLING_SLIME = registerSlime("green_dumpling_slime", 0x32CD32, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseSlime>> SWAMP_SLIME = registerSlime("swamp_slime", 0x556B2F, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<GoldenSlime>> GOLDEN_SLIME = TEEntities.ENTITIES.register("golden_slime", () -> EntityType.Builder.of(GoldenSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("golden_slime")));
    public static final DeferredHolder<EntityType<?>, EntityType<FleshSlime>> FLESH_SLIME = TEEntities.ENTITIES.register("flesh_slime", () -> EntityType.Builder.<FleshSlime>of((entityType, level) -> new FleshSlime(entityType, level, 0xFF0000, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("fleshed_slime")));

    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_SLIME = TEEntities.ENTITIES.register("spiked_slime", () -> EntityType.Builder.<SpikedSlime>of((entityType, level) -> new SpikedSlime(entityType, level,  2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("spiked_slime")));
    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_JUNGLE_SLIME = TEEntities.ENTITIES.register("spiked_jungle_slime", () -> EntityType.Builder.<SpikedSlime>of((entityType, level) -> SpikedJungleSlime.createSpikedJungleSlime(entityType, level,  2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("spiked_jungle_slime")));
    public static final DeferredHolder<EntityType<?>, EntityType<SpikedSlime>> SPIKED_ICE_SLIME = TEEntities.ENTITIES.register("spiked_ice_slime", () -> EntityType.Builder.<SpikedSlime>of((entityType, level) -> SpikedJungleSlime.createSpikedIceSlime(entityType, level,  2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("spiked_ice_slime")));

    // 飞行怪
    public static final DeferredHolder<EntityType<?>, EntityType<DemonEye>> DEMON_EYE = TEEntities.registerMonster("demon_eye", DemonEye::new, 1.1F, 1.1F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> CRIMERA = registerSimpleMonster("crimera", FlyMonsterPrefab.CRIMERA_BUILDER, 1.2f, 1.2f);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> EATER_OF_SOULS = registerSimpleMonster("eater_of_souls", FlyMonsterPrefab.EATER_OF_SOULS_BUILDER, 1.2f, 1.2f);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> DRIPPLER = registerSimpleMonster("drippler", FlyMonsterPrefab.DRIPPLER_BUILDER, 1.6f, 1.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> SERVANT_OF_CTHULHU = registerSimpleMonster("servant_of_cthulhu", FlyMonsterPrefab.SERVANT_OF_CTHULHU_BUILDER, 1.1f, 1.1f);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> WANDERING_EYE_FISH = registerSimpleMonster("wandering_eye_fish", FlyMonsterPrefab.WANDERING_EYE_FISH_BUILDER, 1.4f, 1.4f);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> FLYING_FISH = registerSimpleMonster("flying_fish", FlyMonsterPrefab.FLYING_FISH_BUILDER, 0.9F, 0.9F);
    public static final DeferredHolder<EntityType<?>, EntityType<VisualNeuron>> VISUAL_NEURON = TEEntities.registerMonster("visual_neuron", VisualNeuron::new, 1.2f, 1.2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Harpy>> HARPY = TEEntities.registerMonster("harpy", (e, l) -> new Harpy(e, l, new FlyMonsterPrefab().getPrefab().setSpawnWithoutLight()), 1f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Demon>> DEMON = TEEntities.registerMonster("demon", (e, l) -> new Demon(e, l, new FlyMonsterPrefab().getPrefab().setSpawnWithoutLight().setNoFriction()), 1f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Demon>> VOODOO_DEMON = TEEntities.registerMonster("voodoo_demon", (e, l) -> new Demon(e, l, new FlyMonsterPrefab().getPrefab().setSpawnWithoutLight().setNoFriction()), 1f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<AntlionSwarmer>> ANTLION_SWARMER = TEEntities.registerMonster("antlion_swarmer", (e, l) -> new AntlionSwarmer(e, l, new FlyMonsterPrefab().getPrefab()), 3f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<AntlionSwarmer>> GIANT_ANTLION_SWARMER = TEEntities.registerMonster("giant_antlion_swarmer", (e, l) -> new AntlionSwarmer(e, l, new FlyMonsterPrefab().getPrefab()), 3.5f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<GraniteElemental>> GRANITE_ELEMENTAL = TEEntities.registerMonster("granite_elemental", (e, l) -> new GraniteElemental(e, l, new FlyMonsterPrefab().getPrefab()), 1.5f, 1.5f);


    // 陆行怪
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> SPORE_SKELETON = TEEntities.registerMonster("spore_skeleton",(e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> SPORE_ZOMBIE = registerSimpleMonster("spore_zombie", LandMonsterPrefab.SPORE_ZOMBIE_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> HAT_SPORE_ZOMBIE = registerSimpleMonster("hat_spore_zombie", LandMonsterPrefab.HAT_SPORE_ZOMBIE_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<RangeSkeleton>> DECAYEDER = TEEntities.registerMonster("decayeder", (e, l) -> new Decayeder(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), 1, 1.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<BloodySpore>> BLOODY_SPORE = TEEntities.registerMonster("bloody_spore", BloodySpore::new, 1, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<BloodCrawler>> BLOOD_CRAWLER = TEEntities.registerMonster("blood_crawler", BloodCrawler::new, 1.8F, 1.2F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> FACE_MONSTER = registerSimpleMonster("face_monster", LandMonsterPrefab.FACE_MONSTER_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> BLOOD_TUMORS = registerSimpleMonster("blood_tumors", LandMonsterPrefab.BLOOD_TUMORS, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> BLOOD_ZOMBIE = registerSimpleMonster("blood_zombie", LandMonsterPrefab.BLOOD_ZOMBIE_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> SNOW_FLINX = registerSimpleMonster("snow_flinx", LandMonsterPrefab.SNOW_FLINX_BUILDER, 1.25F, 1.25F);

    // 水怪
    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> PIRANHA = TEEntities.registerMonster("piranha", (e, l)->new Piranha(e,l), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> BLUE_JELLYFISH = TEEntities.registerMonster("blue_jellyfish", (e, l)->new JellyFish(e,l), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> PINK_JELLYFISH = TEEntities.registerMonster("pink_jellyfish", (e, l)->new JellyFish(e,l), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> SHARK = TEEntities.registerMonster("shark", (e, l)->new Piranha(e,l), 2.5F, 1F);


    // 蜜蜂
    public static final DeferredHolder<EntityType<?>, EntityType<Hornet>> HORNET = TEEntities.registerMonster("hornet", (e, l) -> new Hornet(e, l, FlyMonsterPrefab.BEE_BUILDER.get()), 0.8f, 1.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<LittleHornet>> LITTLE_HORNET = TEEntities.registerEntity("little_hornet", LittleHornet::new, MobCategory.CREATURE, 0.4f, 0.4f);
    // 蝙蝠
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> CAVE_BAT = registerSimpleMonster("cave_bat", FlyMonsterPrefab.CAVE_BAT_BUILDER, 1.6f, 1.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> JUNGLE_BAT = registerSimpleMonster("jungle_bat", FlyMonsterPrefab.JUNGLE_BAT_BUILDER, 1.6f, 1.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> HELL_BAT = TEEntities.ENTITIES.register("hell_bat",
            () -> EntityType.Builder.<AbstractMonster>of((type, level) -> new AbstractMonster(type, level, FlyMonsterPrefab.HELL_BAT_BUILDER.get()), MobCategory.MONSTER)
                    .clientTrackingRange(10).setTrackingRange(50).sized(1.6f, 1.6f).fireImmune().build(TEEntities.Key("hell_bat")));
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> ICE_BAT = registerSimpleMonster("ice_bat", FlyMonsterPrefab.ICE_BAT_BUILDER, 1.6f, 1.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> SPORE_BAT = registerSimpleMonster("spore_bat", FlyMonsterPrefab.SPORE_BAT_BUILDER, 1.6f, 1.6f);
    // 蠕虫
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWorm<BaseWormPart>>> DEVOURER = TEEntities.registerMonster("devourer", (e, l) -> BaseWorm.simpleWorm(e, l, AbstractPrefab.WARM_BUILDER.get().setSpawnWithoutLight()), 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWorm<BaseWormPart>>> TOMB_CRAWLER = TEEntities.registerMonster("tomb_crawler", (e, l) -> BaseWorm.simpleWorm(e, l, AbstractPrefab.WARM_BUILDER.get()), 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWorm<BaseWormPart>>> GIANT_WORM = TEEntities.registerMonster("giant_worm", (e, l) -> BaseWorm.simpleWorm(e, l, AbstractPrefab.WARM_BUILDER.get()), 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseWorm<BaseWormPart>>> LEECH = TEEntities.registerMonster("leech", (e, l) -> BaseWorm.simpleWorm(e, l, AbstractPrefab.WARM_BUILDER.get()), 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<BoneSerpent<BaseWormPart>>> BONE_SERPENT = TEEntities.registerMonster("bone_serpent", (e, l) -> new BoneSerpent<>(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight().setNoGravity()), 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<BoneSerpent<BaseWormPart>>> WITHER_BONE_SERPENT = TEEntities.registerMonster("wither_bone_serpent", (e, l) -> new BoneSerpent<>(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight().setNoGravity()), 2F, 2F);
    // 卷壳怪
    public static final DeferredHolder<EntityType<?>, EntityType<GiantShelly>> GIANT_SHELLY = TEEntities.registerMonster("giant_shelly", GiantShelly::new, 1F, 1F);

    public static final DeferredHolder<EntityType<?>, EntityType<Crawdad>> CRAWDAD = TEEntities.registerMonster("crawdad", Crawdad::new, 1F, 1F);
    // 宁芙
    public static final DeferredHolder<EntityType<?>, EntityType<Nymph>> NYMPH = TEEntities.registerMonster("nymph", Nymph::new, 0.8F, 1.95F);
    // 抓人草
    public static final DeferredHolder<EntityType<?>, EntityType<Snatcher>> SNATCHER = TEEntities.registerMonster("snatcher", (e, l) -> new Snatcher(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), 1F, 1F);
    public static final DeferredHolder<EntityType<?>, EntityType<Snatcher>> MAN_EATER = TEEntities.registerMonster("man_eater", (e, l) -> new Snatcher(e, l, new AbstractPrefab().getPrefab()), 1F, 1F);
    // 地牢骷髅
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BASE_BONES = TEEntities.registerMonster("base_bones", (e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), 0.65F, 1.85F);

    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> ANGER_BONES = TEEntities.registerMonster("anger_bones", (e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> SHORT_BONES = TEEntities.registerMonster("short_bones", (e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), 0.55F, 1.65F);
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_BONES = TEEntities.registerMonster("big_bones", (e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), 0.85F, 2.25F);
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_ANGER_BONES = TEEntities.registerMonster("big_anger_bones", (e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), 0.9F, 2.4F);
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_MUSCLE_ANGER_BONES = TEEntities.registerMonster("big_muscle_anger_bones", (e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), 0.95F, 2.45F);
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> BIG_HELMET_ANGER_BONES = TEEntities.registerMonster("big_helmet_anger_bones", (e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), 1F, 2.6F);
    public static final DeferredHolder<EntityType<?>, EntityType<MeleeSkeleton>> UNDEAD_VIKING = TEEntities.registerMonster("undead_viking", (e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), 1F, 2.6F);
    // 穿墙怪
    public static final DeferredHolder<EntityType<?>, EntityType<CursedSkull>> CURSED_SKULL = TEEntities.registerMonster("cursed_skull", (e, l) -> new CursedSkull(e, l, new AbstractPrefab().getPrefab()), 1F, 1F);

    public static final DeferredHolder<EntityType<?>, EntityType<Ghost>> GHOST = TEEntities.registerMonster("ghost", (e, l) -> new Ghost(e, l, new AbstractPrefab().getPrefab()), 1F, 1.8F);
    public static final DeferredHolder<EntityType<?>, EntityType<MeteorHead>> METEOR_HEAD = TEEntities.registerMonster("meteor_head", (e, l) -> new MeteorHead(e, l, new AbstractPrefab().getPrefab()), 1F, 1F);
    // 远程法师
    public static final DeferredHolder<EntityType<?>, EntityType<RangeShooter>> DARK_CASTER = TEEntities.registerMonster("dark_caster", (e, l) -> new RangeShooter(e, l, TEProjectileEntities.DARK_CASTER_PROJ, new AbstractPrefab().getPrefab()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<RangeShooter>> GOBLIN_SORCERER = TEEntities.registerMonster("goblin_sorcerer", (e, l) -> new RangeShooter(e, l, TEProjectileEntities.DARK_CASTER_PROJ, new AbstractPrefab().getPrefab()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<FireImpEntity>> FIRE_IMP = TEEntities.registerMonster("fire_imp", (e, l) -> new FireImpEntity(e, l, TEProjectileEntities.FIRE_IMP_PROJ, new AbstractPrefab().getPrefab()), 0.65F, 1);

    // 哥布林军队
    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidMonster>> GOBLIN_ARCHER = TEEntities.registerMonster("goblin_archer", (e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setMainHand(Items.BOW.getDefaultInstance()).setSpawnWithoutLight()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidMonster>> GOBLIN_PEON = TEEntities.registerMonster("goblin_peon", (e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setSpawnWithoutLight()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidMonster>> GOBLIN_WARRIOR = TEEntities.registerMonster("goblin_warrior", (e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setMainHand(Items.STONE_SWORD.getDefaultInstance()).setSpawnWithoutLight()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidMonster>> GOBLIN_THIEF = TEEntities.registerMonster("goblin_thief", (e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setSpawnWithoutLight()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidMonster>> GOBLIN_SCOUT = TEEntities.registerMonster("goblin_scout", (e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setSpawnWithoutLight()), 0.65F, 1.85F);
    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidMonster>> ANGER_GOBLIN = TEEntities.registerMonster("anger_goblin", (e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setMainHand(Items.GOLDEN_SWORD.getDefaultInstance()).setSpawnWithoutLight()), 0.65F, 1.85F);

    //饿鬼
    public static final DeferredHolder<EntityType<?>, EntityType<TheHungry>> THE_HUNGRY = TEEntities.registerMonster("the_hungry", (e, l) -> new TheHungry(e, l, new AbstractPrefab().getPrefab()), 1F, 1F);
    public static final DeferredHolder<EntityType<?>, EntityType<HillHungry>> HILL_HUNGRY = TEEntities.registerMonster("hill_hungry", (e, l) -> new HillHungry(e, l, new AbstractPrefab().getPrefab()), 1F, 1F);


    /* *********肉后***************** */
    public static final DeferredHolder<EntityType<?>, EntityType<Wyvern<BaseWormPart>>> WYVERN = TEEntities.registerMonster("wyvern", (e, l) -> new Wyvern<>(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight().setNoGravity()), 1F, 1F);
    public static final DeferredHolder<EntityType<?>, EntityType<Pixie>> PIXIE = TEEntities.registerMonster("pixie", (e, l) -> new Pixie(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), 1F, 1F);
    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidMonster>> POSSESS_ARMOR = TEEntities.registerMonster("possess_armor", (e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().getPrefab().setDeathSound(TESounds.SOUL_DEATH).setHurtSound(TESounds.METAL_HURT)), 1F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<HumanoidMonster>> POSSESS_ARMOR_VOID_VESSEL = TEEntities.registerMonster("possess_armor_void_vessel", (e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().getPrefab().setDeathSound(TESounds.SOUL_DEATH).setHurtSound(TESounds.METAL_HURT)), 1F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<Wraith>> WRAITH = TEEntities.registerMonster("wraith", (e, l) -> new Wraith(e, l), 1F, 2F);


    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> WOODEN_MIMIC = TEEntities.registerMonster("wooden_mimic", WoodenMimic::new, 0.8f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> GOLDEN_MIMIC = TEEntities.registerMonster("golden_mimic", WoodenMimic::new, 0.8f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> ICE_MIMIC = TEEntities.registerMonster("ice_mimic", WoodenMimic::new, 0.8f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> SHADOW_MIMIC = TEEntities.registerMonster("shadow_mimic", WoodenMimic::new, 0.8f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> CRIMSON_MIMIC = TEEntities.registerMonster("crimson_mimic", CrimsonMimic::new, 1.6f, 1.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> CORRUPT_MIMIC = TEEntities.registerMonster("corrupt_mimic", CrimsonMimic::new, 1.6f, 1.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> HALLOWED_MIMIC = TEEntities.registerMonster("hallowed_mimic", CrimsonMimic::new, 1.6f, 1.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<WoodenMimic>> JUNGLE_MIMIC = TEEntities.registerMonster("jungle_mimic", CrimsonMimic::new, 1.6f, 1.6f);

    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> MUMMY = registerSimpleMonster("mummy", LandMonsterPrefab.MUMMY_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> DARK_MUMMY = registerSimpleMonster("dark_mummy", LandMonsterPrefab.EVIL_MUMMY_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> BLOOD_MUMMY = registerSimpleMonster("blood_mummy", LandMonsterPrefab.EVIL_MUMMY_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> LIGHT_MUMMY = registerSimpleMonster("light_mummy", LandMonsterPrefab.MUMMY_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> DARK_LAMIA = registerSimpleMonster("dark_lamia", LandMonsterPrefab.LAMIA_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> LIGHT_LAMIA = registerSimpleMonster("light_lamia", LandMonsterPrefab.LAMIA_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> GHOUL = registerSimpleMonster("ghoul", LandMonsterPrefab.GHOUL_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> TAINTED_GHOUL = registerSimpleMonster("tainted_ghoul", LandMonsterPrefab.GHOUL_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> VILE_GHOUL = registerSimpleMonster("vile_ghoul", LandMonsterPrefab.GHOUL_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> DREAMER_GHOUL  = registerSimpleMonster("dreamer_ghoul", LandMonsterPrefab.GHOUL_BUILDER, 0.75F, 1.95F);
    public static final DeferredHolder<EntityType<?>, EntityType<SandPoacher>> SAND_POACHER = TEEntities.registerMonster("sand_poacher", SandPoacher::new, 1.8F, 1.2F);

    public static final DeferredHolder<EntityType<?>, EntityType<Piranha>> ARAPAIMA = TEEntities.registerMonster("arapaima", (e, l)->new Piranha(e,l), 2.2F, 0.7F);
    public static final DeferredHolder<EntityType<?>, EntityType<JellyFish>> GREEN_JELLYFISH = TEEntities.registerMonster("green_jellyfish", (e, l)->new JellyFish(e,l), 0.5F, 0.5F);

    public static final DeferredHolder<EntityType<?>, EntityType<JumpAttackMonster>> DERPLING = TEEntities.registerMonster("derpling", (e, l)->new JumpAttackMonster(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), 2F, 2F);
    public static final DeferredHolder<EntityType<?>, EntityType<JumpAttackMonster>> HERPLING = TEEntities.registerMonster("herpling", (e, l)->new JumpAttackMonster(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), 1F, 1F);


    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ResourceLocation defaultHumanoidModel = TEMonsterEntities.POSSESS_ARMOR_VOID_VESSEL.getId();

        event.registerEntityRenderer(TEMonsterEntities.BLUE_SLIME.get(), c -> new CustomSlimeRenderer(c, "blue"));
        event.registerEntityRenderer(TEMonsterEntities.GREEN_SLIME.get(), c -> new CustomSlimeRenderer(c, "green"));
        event.registerEntityRenderer(TEMonsterEntities.PINK_SLIME.get(), c -> new CustomSlimeRenderer(c, "pink"));
        event.registerEntityRenderer(TEMonsterEntities.DUNGEON_SLIME.get(), c -> new CustomSlimeRenderer(c, "dungeon"));
        event.registerEntityRenderer(TEMonsterEntities.CORRUPT_SLIME.get(), c -> new CustomSlimeRenderer(c, "corrupted"));
        event.registerEntityRenderer(TEMonsterEntities.DESERT_SLIME.get(), c -> new CustomSlimeRenderer(c, "desert"));
        event.registerEntityRenderer(TEMonsterEntities.JUNGLE_SLIME.get(), c -> new CustomSlimeRenderer(c, "jungle"));
        event.registerEntityRenderer(TEMonsterEntities.EVIL_SLIME.get(), c -> new CustomSlimeRenderer(c, "evil"));
        event.registerEntityRenderer(TEMonsterEntities.ICE_SLIME.get(), c -> new CustomSlimeRenderer(c, "ice"));
        event.registerEntityRenderer(TEMonsterEntities.LAVA_SLIME.get(), c -> new CustomSlimeRenderer(c, "lava"));
        event.registerEntityRenderer(TEMonsterEntities.LUMINOUS_SLIME.get(), c -> new CustomSlimeRenderer(c, "luminous"));
        event.registerEntityRenderer(TEMonsterEntities.CRIMSLIME.get(), c -> new CustomSlimeRenderer(c, "crimson"));
        event.registerEntityRenderer(TEMonsterEntities.PURPLE_SLIME.get(), c -> new CustomSlimeRenderer(c, "purple"));
        event.registerEntityRenderer(TEMonsterEntities.RED_SLIME.get(), c -> new CustomSlimeRenderer(c, "red"));
        event.registerEntityRenderer(TEMonsterEntities.TROPIC_SLIME.get(), c -> new CustomSlimeRenderer(c, "tropic"));
        event.registerEntityRenderer(TEMonsterEntities.YELLOW_SLIME.get(), c -> new CustomSlimeRenderer(c, "yellow"));
        event.registerEntityRenderer(TEMonsterEntities.HONEY_SLIME.get(), c -> new CustomSlimeRenderer(c, "honey"));
        event.registerEntityRenderer(TEMonsterEntities.BLACK_SLIME.get(), c -> new CustomSlimeRenderer(c, "black"));
        event.registerEntityRenderer(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), c -> new CustomSlimeRenderer(c, "green_dumpling"));
        event.registerEntityRenderer(TEMonsterEntities.SWAMP_SLIME.get(), c -> new CustomSlimeRenderer(c, "swamp"));
        event.registerEntityRenderer(TEMonsterEntities.GOLDEN_SLIME.get(), c -> new CustomSlimeRenderer(c, "yellow"));
        event.registerEntityRenderer(TEMonsterEntities.FLESH_SLIME.get(), c -> new CustomSlimeRenderer(c, "flesh"));

        event.registerEntityRenderer(TEMonsterEntities.SPIKED_SLIME.get(), c -> new GeoSpecialSlimeRenderer<>(c, SPIKED_SLIME.getId().withPrefix("slime/")));
        event.registerEntityRenderer(TEMonsterEntities.SPIKED_JUNGLE_SLIME.get(), c -> new GeoSpecialSlimeRenderer<>(c, SPIKED_JUNGLE_SLIME.getId().withPrefix("slime/")));
        event.registerEntityRenderer(TEMonsterEntities.SPIKED_ICE_SLIME.get(), c -> new GeoSpecialSlimeRenderer<>(c, SPIKED_ICE_SLIME.getId().withPrefix("slime/")));


        event.registerEntityRenderer(TEMonsterEntities.CRIMERA.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.CRIMERA.getId(), true));
        event.registerEntityRenderer(TEMonsterEntities.EATER_OF_SOULS.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.EATER_OF_SOULS.getId(), true));
        event.registerEntityRenderer(TEMonsterEntities.SERVANT_OF_CTHULHU.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.SERVANT_OF_CTHULHU.getId(), true));
        event.registerEntityRenderer(TEMonsterEntities.DRIPPLER.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.DRIPPLER.getId(), false, 2f, 0));
        event.registerEntityRenderer(TEMonsterEntities.WANDERING_EYE_FISH.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.WANDERING_EYE_FISH.getId(), false, 1.5f, 0));
        event.registerEntityRenderer(TEMonsterEntities.FLYING_FISH.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.FLYING_FISH.getId(), true, 0.75f, 0));
        event.registerEntityRenderer(TEMonsterEntities.HARPY.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(TEMonsterEntities.HARPY.getId()), false, 1f, 0));
        event.registerEntityRenderer(TEMonsterEntities.DEMON.get(), c -> new DemonRenderer(c, new DemonModel(TEMonsterEntities.DEMON.getId()), false, 1f, 0));
        event.registerEntityRenderer(TEMonsterEntities.VOODOO_DEMON.get(), c -> new DemonRenderer(c, new DemonModel(TEMonsterEntities.VOODOO_DEMON.getId()), false, 1.1f, 0));
        event.registerEntityRenderer(TEMonsterEntities.ANTLION_SWARMER.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.ANTLION_SWARMER.getId(), true, 1.0f, 0f));
        event.registerEntityRenderer(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.ANTLION_SWARMER.getId(), true, 1.25f, 0f));
        event.registerEntityRenderer(TEMonsterEntities.GRANITE_ELEMENTAL.get(), c -> new GeoNegativeVolumeRenderer<>(c, TEMonsterEntities.GRANITE_ELEMENTAL.getId(), true).addBoneToGlow("Core"));
        event.registerEntityRenderer(TEMonsterEntities.METEOR_HEAD.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.METEOR_HEAD.getId(), true));

        event.registerEntityRenderer(TEMonsterEntities.DEMON_EYE.get(), DemonEyeRenderer::new);
        if (!ClientConfig.ENABLE_NON_SPIDER_MODEL.get()) {
            event.registerEntityRenderer(TEMonsterEntities.BLOOD_CRAWLER.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.BLOOD_CRAWLER.getId()));
        }
        event.registerEntityRenderer(TEMonsterEntities.BLOODY_SPORE.get(), BloodySporeRenderer::new);
        event.registerEntityRenderer(TEMonsterEntities.DECAYEDER.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.DECAYEDER.getId()));


        event.registerEntityRenderer(TEMonsterEntities.SPORE_SKELETON.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.SPORE_SKELETON.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.SPORE_ZOMBIE.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.SPORE_ZOMBIE.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.HAT_SPORE_ZOMBIE.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.HAT_SPORE_ZOMBIE.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.FACE_MONSTER.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.FACE_MONSTER.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.BLOOD_TUMORS.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.BLOOD_TUMORS.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.BLOOD_ZOMBIE.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.BLOOD_ZOMBIE.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.SNOW_FLINX.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.SNOW_FLINX.getId(), false){
            @Override
            protected void adjustPose(PoseStack poseStack, AbstractMonster animatable, BakedGeoModel model, float partialTick){
                poseStack.mulPose(Axis.YP.rotationDegrees(90+ Mth.lerp(partialTick, animatable.yBodyRotO - animatable.yHeadRotO,animatable.yBodyRot - animatable.yHeadRot)) );
            }
        });

        event.registerEntityRenderer(TEMonsterEntities.PIRANHA.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.PIRANHA.getId(), true));
        event.registerEntityRenderer(TEMonsterEntities.SHARK.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.SHARK.getId(), true, 1.8f, 0.0f));
        event.registerEntityRenderer(TEMonsterEntities.ARAPAIMA.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.ARAPAIMA.getId(), true, 1.0f, 0.0f));
        event.registerEntityRenderer(TEMonsterEntities.BLUE_JELLYFISH.get(), c -> new JellyFishRenderer(c, new GeoModelTextureDecoration<>(new GeoNormalModel<>(TerraEntity.space("jellyfish")), TerraEntity.space("blue_jellyfish") )));
        event.registerEntityRenderer(TEMonsterEntities.PINK_JELLYFISH.get(), c -> new JellyFishRenderer(c, new GeoModelTextureDecoration<>(new GeoNormalModel<>(TerraEntity.space("jellyfish")), TerraEntity.space("pink_jellyfish") )));
        event.registerEntityRenderer(TEMonsterEntities.GREEN_JELLYFISH.get(), c -> new JellyFishRenderer(c, new GeoModelTextureDecoration<>(new GeoNormalModel<>(TerraEntity.space("jellyfish")), TerraEntity.space("green_jellyfish") )));

        event.registerEntityRenderer(TEMonsterEntities.DEVOURER.get(), c -> new GeoWormRenderer<>(c, TEMonsterEntities.DEVOURER.getId(), 2.0f, 0.0f));
        event.registerEntityRenderer(TEMonsterEntities.GIANT_WORM.get(), c -> new GeoWormRenderer<>(c, TEMonsterEntities.GIANT_WORM.getId(), 2.0f, 0.0f));
        event.registerEntityRenderer(TEMonsterEntities.BONE_SERPENT.get(), c -> new GeoWormRenderer<>(c, TEMonsterEntities.BONE_SERPENT.getId(), 2.0f, 0.0f));
        event.registerEntityRenderer(TEMonsterEntities.WITHER_BONE_SERPENT.get(), c -> new GeoWormRenderer<>(c, TEMonsterEntities.WITHER_BONE_SERPENT.getId(), 2.0f, 0.0f));

        event.registerEntityRenderer(TEMonsterEntities.LEECH.get(), c -> new GeoWormRenderer<>(c, TEMonsterEntities.LEECH.getId(), 2.0f, 0.0f));
        event.registerEntityRenderer(TEMonsterEntities.TOMB_CRAWLER.get(), c -> new GeoWormRenderer<>(c, TEMonsterEntities.TOMB_CRAWLER.getId(), 2.0f, 0.0f));
        event.registerEntityRenderer(TEMonsterEntities.GIANT_SHELLY.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<>(TEMonsterEntities.GIANT_SHELLY.getId()), false, 1, 0));
        event.registerEntityRenderer(TEMonsterEntities.CRAWDAD.get(), c -> new GeoNormalRenderer<>(c, new VariantTexModel<>(TEMonsterEntities.CRAWDAD.getId()), false, 1, 0));

        // bat
        event.registerEntityRenderer(TEMonsterEntities.CAVE_BAT.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.CAVE_BAT.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.JUNGLE_BAT.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.JUNGLE_BAT.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.HELL_BAT.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.HELL_BAT.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.ICE_BAT.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.ICE_BAT.getId(), false));
        event.registerEntityRenderer(TEMonsterEntities.SPORE_BAT.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.SPORE_BAT.getId(), false));

        // bee
        event.registerEntityRenderer(TEMonsterEntities.LITTLE_HORNET.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(TEMonsterEntities.LITTLE_HORNET.getId(), false), true, 1, 0.1f));
        event.registerEntityRenderer(TEMonsterEntities.HORNET.get(), c -> new GeoNormalRenderer<>(c, new GeoNormalModel<>(TEMonsterEntities.HORNET.getId(), false), true, 1, 0.2f));

        event.registerEntityRenderer(TEMonsterEntities.NYMPH.get(), c -> new GeoNormalRenderer<>(c, new NymphModel<>(TEMonsterEntities.NYMPH.getId()), false, 1, 0f));
        event.registerEntityRenderer(TEMonsterEntities.SNATCHER.get(), c -> new SnatcherRenderer<>(c, TEMonsterEntities.SNATCHER.getId()));
        event.registerEntityRenderer(TEMonsterEntities.MAN_EATER.get(), c -> new SnatcherRenderer<>(c, TEMonsterEntities.MAN_EATER.getId()));
        event.registerEntityRenderer(TEMonsterEntities.THE_HUNGRY.get(), c -> new TheHungryRenderer<>(c, TEMonsterEntities.THE_HUNGRY.getId()));
        event.registerEntityRenderer(TEMonsterEntities.HILL_HUNGRY.get(), c -> new TheHungryRenderer<>(c, TEMonsterEntities.THE_HUNGRY.getId()));

        // 地牢骷髅
        event.registerEntityRenderer(TEMonsterEntities.BASE_BONES.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.BASE_BONES.getId(), 0.9f, 0));

        event.registerEntityRenderer(TEMonsterEntities.ANGER_BONES.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.ANGER_BONES.getId(), 0.9f, 0));
        event.registerEntityRenderer(TEMonsterEntities.SHORT_BONES.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.SHORT_BONES.getId(), 0.8f, 0));
        event.registerEntityRenderer(TEMonsterEntities.BIG_BONES.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.ANGER_BONES.getId(), 1.1f, 0));
        event.registerEntityRenderer(TEMonsterEntities.BIG_ANGER_BONES.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.BIG_ANGER_BONES.getId(), 1.15f, 0));
        event.registerEntityRenderer(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.getId(), 1.2f, 0));
        event.registerEntityRenderer(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.BIG_HELMET_ANGER_BONES.getId(), 1.25f, 0));
        event.registerEntityRenderer(TEMonsterEntities.UNDEAD_VIKING.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.UNDEAD_VIKING.getId(), false));

        // 穿墙怪
        event.registerEntityRenderer(TEMonsterEntities.CURSED_SKULL.get(), c -> new GeoNegativeVolumeRenderer<>(c, TEMonsterEntities.CURSED_SKULL.getId(), true, 1f, 0).addBoneToGlow(List.of("outline")));

        event.registerEntityRenderer(TEMonsterEntities.GHOST.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.GHOST.getId(), false, 1f, 0));

        // 远程法师
        event.registerEntityRenderer(TEMonsterEntities.DARK_CASTER.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.DARK_CASTER.getId()));
        event.registerEntityRenderer(TEMonsterEntities.FIRE_IMP.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.FIRE_IMP.getId()));

        event.registerEntityRenderer(TEMonsterEntities.GOBLIN_SORCERER.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.GOBLIN_SORCERER.getId().withPrefix("goblin/")));
        event.registerEntityRenderer(TEMonsterEntities.GOBLIN_PEON.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.GOBLIN_PEON.getId().withPrefix("goblin/")));
        event.registerEntityRenderer(TEMonsterEntities.GOBLIN_ARCHER.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.GOBLIN_ARCHER.getId().withPrefix("goblin/")));
        event.registerEntityRenderer(TEMonsterEntities.GOBLIN_WARRIOR.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.GOBLIN_WARRIOR.getId().withPrefix("goblin/")));
        event.registerEntityRenderer(TEMonsterEntities.GOBLIN_THIEF.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.GOBLIN_THIEF.getId().withPrefix("goblin/")));
        event.registerEntityRenderer(TEMonsterEntities.GOBLIN_SCOUT.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.GOBLIN_SCOUT.getId().withPrefix("goblin/")));
        event.registerEntityRenderer(TEMonsterEntities.ANGER_GOBLIN.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.ANGER_GOBLIN.getId().withPrefix("goblin/")));

        /* *********肉后***************** */
        event.registerEntityRenderer(TEMonsterEntities.WYVERN.get(), c -> new WyvernRenderer<>(c, TEMonsterEntities.WYVERN.getId()));
        event.registerEntityRenderer(TEMonsterEntities.PIXIE.get(), c -> new FairyRenderer<>(c, TEMonsterEntities.PIXIE.getId(), false, 1, 0).setBoneToGlow(List.of("Outline","Outline2","Outline3"), List.of("bone","bone2","bone3")));
        event.registerEntityRenderer(TEMonsterEntities.POSSESS_ARMOR.get(), c -> new HumanoidRenderer<>(c, defaultHumanoidModel));
        event.registerEntityRenderer(TEMonsterEntities.POSSESS_ARMOR_VOID_VESSEL.get(), c -> new HumanoidRenderer<>(c, TEMonsterEntities.POSSESS_ARMOR_VOID_VESSEL.getId()));
        event.registerEntityRenderer(TEMonsterEntities.WRAITH.get(), c -> new HumanoidRenderer<>(c, defaultHumanoidModel).setDisableRender());

        event.registerEntityRenderer(TEMonsterEntities.WOODEN_MIMIC.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.WOODEN_MIMIC.getId()));
        event.registerEntityRenderer(TEMonsterEntities.GOLDEN_MIMIC.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.GOLDEN_MIMIC.getId()));
        event.registerEntityRenderer(TEMonsterEntities.SHADOW_MIMIC.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.SHADOW_MIMIC.getId()));
        event.registerEntityRenderer(TEMonsterEntities.ICE_MIMIC.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.ICE_MIMIC.getId()));
        event.registerEntityRenderer(TEMonsterEntities.CRIMSON_MIMIC.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.CRIMSON_MIMIC.getId(), false, 2f, 0));
        event.registerEntityRenderer(TEMonsterEntities.CORRUPT_MIMIC.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.CORRUPT_MIMIC.getId(), false, 2f, 0));
        event.registerEntityRenderer(TEMonsterEntities.HALLOWED_MIMIC.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.HALLOWED_MIMIC.getId(), false, 2f, 0));
        event.registerEntityRenderer(TEMonsterEntities.JUNGLE_MIMIC.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.JUNGLE_MIMIC.getId(), false, 2f, 0));

        event.registerEntityRenderer(TEMonsterEntities.MUMMY.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.MUMMY.getId()));
        event.registerEntityRenderer(TEMonsterEntities.DARK_MUMMY.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.DARK_MUMMY.getId()));
        event.registerEntityRenderer(TEMonsterEntities.BLOOD_MUMMY.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.BLOOD_MUMMY.getId()));
        event.registerEntityRenderer(TEMonsterEntities.LIGHT_MUMMY.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.LIGHT_MUMMY.getId()));
        event.registerEntityRenderer(TEMonsterEntities.DARK_LAMIA.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.DARK_LAMIA.getId()));
        event.registerEntityRenderer(TEMonsterEntities.LIGHT_LAMIA.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.LIGHT_LAMIA.getId()));
        event.registerEntityRenderer(TEMonsterEntities.GHOUL.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.GHOUL.getId()));
        event.registerEntityRenderer(TEMonsterEntities.TAINTED_GHOUL.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.TAINTED_GHOUL.getId()));
        event.registerEntityRenderer(TEMonsterEntities.VILE_GHOUL.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.VILE_GHOUL.getId()));
        event.registerEntityRenderer(TEMonsterEntities.DREAMER_GHOUL.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.DREAMER_GHOUL.getId()));
        event.registerEntityRenderer(TEMonsterEntities.SAND_POACHER.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.SAND_POACHER.getId()));

        event.registerEntityRenderer(TEMonsterEntities.DERPLING.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.DERPLING.getId()));
        event.registerEntityRenderer(TEMonsterEntities.HERPLING.get(), c -> new GeoNormalRenderer<>(c, TEMonsterEntities.HERPLING.getId()));
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {

        // slime
        event.put(BLUE_SLIME.get(), BaseSlime.createSlimeAttributes(4.0F, 2, 16.0F).build());
        event.put(GREEN_SLIME.get(), BaseSlime.createSlimeAttributes(3.0F, 0, 9.0F).build());
        event.put(PINK_SLIME.get(), BaseSlime.createSlimeAttributes(2.0F, 2, 97.0F).build());
        event.put(CORRUPT_SLIME.get(), BaseSlime.createSlimeAttributes(28.0F, 20, 88.0F).build());
        event.put(DUNGEON_SLIME.get(), BaseSlime.createSlimeAttributes(15.6F, 2, 78.0F).build());
        event.put(DESERT_SLIME.get(), BaseSlime.createSlimeAttributes(6.0F, 5, 21.0F).build());
        event.put(JUNGLE_SLIME.get(), BaseSlime.createSlimeAttributes(12.0F, 6, 46.0F).build());
        event.put(EVIL_SLIME.get(), BaseSlime.createSlimeAttributes(29.0F, 2, 58.0F).build());
        event.put(ICE_SLIME.get(), BaseSlime.createSlimeAttributes(5.0F, 4, 13.0F).build());
        event.put(LAVA_SLIME.get(), BaseSlime.createSlimeAttributes(10.0F, 10, 30.0F).build());
        event.put(LUMINOUS_SLIME.get(), BaseSlime.createSlimeAttributes(36.4F, 30, 93.0F).build());
        event.put(CRIMSLIME.get(), BaseSlime.createSlimeAttributes(31.2F, 26, 104.0F).build());
        event.put(PURPLE_SLIME.get(), BaseSlime.createSlimeAttributes(5.0F, 6, 25.0F).build());
        event.put(RED_SLIME.get(), BaseSlime.createSlimeAttributes(5.0F, 4, 25.0F).build());
        event.put(TROPIC_SLIME.get(), BaseSlime.createSlimeAttributes(5.0F, 1, 13.0F).build());
        event.put(YELLOW_SLIME.get(), BaseSlime.createSlimeAttributes(6.0F, 7, 25.0F).build());
        event.put(HONEY_SLIME.get(), HoneySlime.createSlimeAttributes(0F, 0, 16.0F).build());
        event.put(GREEN_DUMPLING_SLIME.get(), BaseSlime.createSlimeAttributes(5.0F, 0, 25.0F).build());
        event.put(SWAMP_SLIME.get(), BaseSlime.createSlimeAttributes(5.0F, 1, 25.0F).build());
        event.put(BLACK_SLIME.get(), Monster.createMonsterAttributes().add(Attributes.WATER_MOVEMENT_EFFICIENCY, BaseSlime.slimeWaterMoveSpeed).build()); // 由finalizeSpawn设置
        event.put(GOLDEN_SLIME.get(), GoldenSlime.createSlimeAttributes().build());
        event.put(FLESH_SLIME.get(), BaseSlime.createSlimeAttributes(14.0F, 6, 50.0F).build());

        event.put(SPIKED_SLIME.get(), BaseSlime.createSlimeAttributes(7.0F, 5, 26.0F).build());
        event.put(SPIKED_JUNGLE_SLIME.get(), BaseSlime.createSlimeAttributes(15.0F, 8, 33.0F).build());
        event.put(SPIKED_ICE_SLIME.get(), BaseSlime.createSlimeAttributes(6.0F, 8, 31.0F).build());

        // land
        event.put(BLOOD_CRAWLER.get(), BloodCrawler.createAttributes().build());
        event.put(SPORE_SKELETON.get(), AttBuilder.createAttributes(31, 8, 11, 60, 0.5f, 0.28f).build());
        event.put(SPORE_ZOMBIE.get(), AttBuilder.createAttributes(93,10,20,60,0.6f,0.1f).moveSpeed(0.08f).build());
        event.put(HAT_SPORE_ZOMBIE.get(), AttBuilder.createAttributes(114,16,19,60,0.6f,0.72f).moveSpeed(0.08f).build());
        event.put(DECAYEDER.get(), AttBuilder.createAttributes(10, 6, 6).build());
        event.put(BLOODY_SPORE.get(), BloodySpore.createAttributes().build());
        event.put(FACE_MONSTER.get(), AttBuilder.createAttributes(36,10,13).stepLength(3.2).jumpHeight(0.8).build());
        event.put(BLOOD_TUMORS.get(), AttBuilder.createAttributes(5,2,0,0,0,0).moveSpeed(0).safeFall(100).build());
        event.put(BLOOD_ZOMBIE.get(), AttBuilder.createAttributes(39,8,10,60,0.5f,0.1f).moveSpeed(0.15).build());
        event.put(SNOW_FLINX.get(), AttBuilder.createAttributes(36,12,13,60,0.1f,0.1f).build());

        event.put(GIANT_SHELLY.get(), AttBuilder.createAttributes(26,12,9,20,0,0.4f).moveSpeed(0.1).build());
        event.put(CRAWDAD.get(), AttBuilder.createAttributes(26,6,15,25,0,0.1f).jumpHeight(0.8).build());

        event.put(NYMPH.get(), AttBuilder.createAttributes(156,16,15,15,1,0.5f).build());
        event.put(SNATCHER.get(), AttBuilder.createAttributes(31, 10, 13, 20, 1, 1).build());
        event.put(MAN_EATER.get(), AttBuilder.createAttributes(57, 10, 15, 20, 1, 1).build());
        event.put(THE_HUNGRY.get(), AttBuilder.createAttributes(87,16,15,32,0.75f,1).build());
        event.put(HILL_HUNGRY.get(), AttBuilder.createAttributes(87,16,15,32,0.75f,1).build());

        // fly
        event.put(DEMON_EYE.get(), DemonEye.createAttributes().build());
        event.put(SERVANT_OF_CTHULHU.get(), AttBuilder.fly(AttBuilder.createAttributes(10,1,3,30,0.5f,0.3f)).build());
        event.put(FLYING_FISH.get(), AttBuilder.fly(AttBuilder.createAttributes(10,1,2,30,0.5f,0.3f)).build());
        event.put(CRIMERA.get(), AttBuilder.fly(AttBuilder.createAttributes(20,6,11,30,0.5f,0.1f)).build());
        event.put(DRIPPLER.get(), AttBuilder.fly(AttBuilder.createAttributes(26,7,14,64,0.5f,0.2f)).build());
        event.put(WANDERING_EYE_FISH.get(), AttBuilder.fly(AttBuilder.createAttributes(156,18,15,60,1f,1f)).moveSpeed(2.2).build());
        event.put(EATER_OF_SOULS.get(), AttBuilder.fly(AttBuilder.createAttributes(20,6,11,30,0.5f,0.1f)).build());
        event.put(HARPY.get(), AttBuilder.fly(AttBuilder.createAttributes(41,8,13)).build());
        event.put(DEMON.get(), AttBuilder.fly(AttBuilder.createAttributes(62, 8, 20, 16, 1f, 0.28f)).build());
        event.put(VOODOO_DEMON.get(), AttBuilder.fly(AttBuilder.createAttributes(62, 8, 20, 16, 1f, 0.28f)).build());
        event.put(ANTLION_SWARMER.get(), AttBuilder.fly(AttBuilder.createAttributes(31, 8, 15, 32, 1f, 0.55f)).build());
        event.put(GIANT_ANTLION_SWARMER.get(), AttBuilder.fly(AttBuilder.createAttributes(46, 12, 17, 32, 1f, 0.73f)).build());
        event.put(GRANITE_ELEMENTAL.get(), AttBuilder.fly(AttBuilder.createAttributes(46, 8, 17, 32, 1f, 0.73f)).build());
        event.put(METEOR_HEAD.get(), AttBuilder.fly(AttBuilder.createAttributes(13, 6, 21, 32, 1f, 0.64f)).build());

        // swim
        event.put(PIRANHA.get(), AttBuilder.createAttributes(15,2,13,20,0.1f,0.5f).build());
        event.put(BLUE_JELLYFISH.get(), AttBuilder.createAttributes(17,4,13,16,0.1f,0.5f).build());
        event.put(PINK_JELLYFISH.get(), AttBuilder.createAttributes(36,6,15,16,0.1f,0.5f).build());
        event.put(GREEN_JELLYFISH.get(), AttBuilder.createAttributes(62,18,41,20,0.1f,0.5f).build());
        event.put(SHARK.get(), AttBuilder.createAttributes(156,2,20,48,0.1f,0.37f).build());
        event.put(ARAPAIMA.get(), AttBuilder.createAttributes(104,30,39,32,0.1f,0.1f).build());


        // bat
        event.put(CAVE_BAT.get(), AttBuilder.fly(AttBuilder.createAttributes(8,1,4,16,0.2f,0.5f)).build());
        event.put(JUNGLE_BAT.get(), AttBuilder.fly(AttBuilder.createAttributes(17,1,8,16,0.2f,0.5f)).build());
        event.put(HELL_BAT.get(), AttBuilder.fly(AttBuilder.createAttributes(23,2,15,16,0.2f,0.5f)).build());
        event.put(ICE_BAT.get(), AttBuilder.fly(AttBuilder.createAttributes(15,2,7,16,0.2f,0.5f)).build());
        event.put(SPORE_BAT.get(), AttBuilder.fly(AttBuilder.createAttributes(15,2,7,16,0.2f,0.5f)).build());
        // worm
        event.put(GIANT_WORM.get(), AttBuilder.createAttributes(31,3,9).build());
        event.put(LEECH.get(), AttBuilder.createAttributes(36,4,10).moveSpeed(0.145).build());
        event.put(DEVOURER.get(), AttBuilder.createAttributes(52,2,8).build());
        event.put(TOMB_CRAWLER.get(), AttBuilder.createAttributes(16,2,4).build());
        event.put(BONE_SERPENT.get(), AttBuilder.createAttributes(156,12,18).build());
        event.put(WITHER_BONE_SERPENT.get(), AttBuilder.createAttributes(186,15,22).build());

        // bee
        event.put(HORNET.get(), AttBuilder.fly(AttBuilder.createAttributes(32,6,13,32,0,0.55f).moveSpeed(0.5)).build());
        event.put(LITTLE_HORNET.get(), AttBuilder.fly(AttBuilder.createAttributes(3,1,3,20,0,0.2f)).build());

        // 地牢骷髅
        event.put(BASE_BONES.get(), AttBuilder.createAttributes(41, 2, 13, 20, 1, 0.28f).moveSpeed(0.3).build());

        event.put(ANGER_BONES.get(), AttBuilder.createAttributes(41, 8, 13).build());
        event.put(SHORT_BONES.get(), AttBuilder.createAttributes(37, 7, 12).build());
        event.put(BIG_BONES.get(), AttBuilder.createAttributes(52, 9, 17).build());
        event.put(BIG_ANGER_BONES.get(), AttBuilder.createAttributes(36, 6, 17).build());
        event.put(BIG_MUSCLE_ANGER_BONES.get(), AttBuilder.createAttributes(36, 12, 14).build());
        event.put(BIG_HELMET_ANGER_BONES.get(), AttBuilder.createAttributes(62, 14, 12).build());
        event.put(UNDEAD_VIKING.get(), AttBuilder.createAttributes(36, 10, 12).build());

        // 穿墙怪
        event.put(CURSED_SKULL.get(), AttBuilder.createAttributes(21, 6, 18, 32, 1, 0.82f).build());

        event.put(GHOST.get(), AttBuilder.createAttributes(26, 4, 8, 16, 0, 0.55f).gravity(0).build());

        // 远程法师
        event.put(DARK_CASTER.get(), AttBuilder.createAttributes(26, 2, 10, 20, 1, 0.82f).build());
        event.put(FIRE_IMP.get(), AttBuilder.createAttributes(36, 16, 15, 20, 1, 0.55f).build());

        // 哥布林军队
        event.put(GOBLIN_SORCERER.get(), AttBuilder.createAttributes(20, 2, 10, 32, 1, 0.46f).build());
        event.put(GOBLIN_ARCHER.get(), AttBuilder.createAttributes(41, 6, 11, 32, 1, 0.37f).build());
        event.put(GOBLIN_PEON.get(), AttBuilder.createAttributes(31, 4, 6, 32, 1, 0.2f).build());
        event.put(GOBLIN_WARRIOR.get(), AttBuilder.createAttributes(57, 8, 13, 32, 1, 0.6f).build());
        event.put(GOBLIN_THIEF.get(), AttBuilder.createAttributes(41, 6, 10, 32, 1, 0.37f).build());
        event.put(GOBLIN_SCOUT.get(), AttBuilder.createAttributes(41, 6, 10, 32, 1, 0.37f).build());
        event.put(ANGER_GOBLIN.get(), AttBuilder.createAttributes(220, 0, 15, 32, 1, 0.88f).build());

        event.put(TEMonsterEntities.VISUAL_NEURON.get(), AttBuilder.createAttributes(44,10,9,0,0,0.1f).build());

        /* *********肉后***************** */
        event.put(WYVERN.get(), AttBuilder.createAttributes(2080, 10, 41, 50, 1f, 0.28f).build());
        event.put(PIXIE.get(), AttBuilder.createAttributes(78, 20, 28, 16, 1, 0.46f).build());
        event.put(POSSESS_ARMOR.get(), AttBuilder.createAttributes(135, 10, 28, 32, 1, 0.64f).build());
        event.put(POSSESS_ARMOR_VOID_VESSEL.get(), AttBuilder.createAttributes(1, 0, 28, 32, 1, 0.64f).build());
        event.put(WRAITH.get(), AttBuilder.createAttributes(83, 0, 33, 32, 1, 0.37f).gravity(0).build());

        event.put(WOODEN_MIMIC.get(), AttBuilder.createAttributes(260, 30, 42, 32, 1, 0.73f).build());
        event.put(ICE_MIMIC.get(), AttBuilder.createAttributes(260, 30, 42, 32, 1, 0.73f).build());
        event.put(GOLDEN_MIMIC.get(), AttBuilder.createAttributes(260, 30, 42, 32, 1, 0.73f).build());
        event.put(SHADOW_MIMIC.get(), AttBuilder.createAttributes(260, 30, 42, 32, 1, 0.73f).build());
        event.put(CRIMSON_MIMIC.get(), AttBuilder.createAttributes(1820, 34, 47, 32, 1, 0.9f).build());
        event.put(CORRUPT_MIMIC.get(), AttBuilder.createAttributes(1820, 34, 47, 32, 1, 0.9f).build());
        event.put(HALLOWED_MIMIC.get(), AttBuilder.createAttributes(1820, 34, 47, 32, 1, 0.9f).build());
        event.put(JUNGLE_MIMIC.get(), AttBuilder.createAttributes(1820, 34, 47, 32, 1, 0.9f).build());

        event.put(MUMMY.get(), AttBuilder.createAttributes(67,16,26,48,1,0.46f).stepLength(3.2).jumpHeight(0.5).build());
        event.put(DARK_MUMMY.get(), AttBuilder.createAttributes(93,18,32,48,1,0.55f).stepLength(3.2).jumpHeight(0.5).build());
        event.put(BLOOD_MUMMY.get(), AttBuilder.createAttributes(93,18,32,48,1,0.55f).stepLength(3.2).jumpHeight(0.5).build());
        event.put(LIGHT_MUMMY.get(), AttBuilder.createAttributes(104,18,28,48,1,0.51f).stepLength(3.2).jumpHeight(0.5).build());
        event.put(DARK_LAMIA.get(), AttBuilder.createAttributes(182,28,27,48,1,0.69f).stepLength(3.2).jumpHeight(0.5).build());
        event.put(LIGHT_LAMIA.get(), AttBuilder.createAttributes(182,28,27,48,1,0.69f).stepLength(3.2).jumpHeight(0.5).build());
        event.put(GHOUL.get(), AttBuilder.createAttributes(93,26,26,64,1,0.46f).stepLength(3.2).jumpHeight(0.7).build());
        event.put(TAINTED_GHOUL.get(), AttBuilder.createAttributes(114,32,33,64,1,0.55f).stepLength(3.2).jumpHeight(0.7).build());
        event.put(DREAMER_GHOUL.get(), AttBuilder.createAttributes(156,32,28,64,1,0.55f).stepLength(3.2).jumpHeight(0.7).build());
        event.put(VILE_GHOUL.get(), AttBuilder.createAttributes(130,30,31,64,1,0.64f).stepLength(3.2).jumpHeight(0.7).build());
        event.put(SAND_POACHER.get(), AttBuilder.createAttributes(166,24,34,64,1,0.55f).stepLength(3.2).jumpHeight(0.5).build());

        event.put(DERPLING.get(), AttBuilder.createAttributes(156,26,41,48,1,0.55f).stepLength(3.2).jumpHeight(0.5).build());
        event.put(HERPLING.get(), AttBuilder.createAttributes(114,26,33,48,1,0.73f).stepLength(3.2).jumpHeight(0.5).build());
    }


    public static void spawnPlacementRegister(RegisterSpawnPlacementsEvent event) {

        event.register(BLUE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GREEN_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PURPLE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PINK_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DESERT_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(JUNGLE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ICE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TROPIC_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(YELLOW_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(RED_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BLACK_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LAVA_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SWAMP_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DUNGEON_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GREEN_DUMPLING_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BaseSlime::checkSlimeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(SPIKED_ICE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SPIKED_JUNGLE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);



        // land
        event.register(BLOOD_CRAWLER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BLOOD_ZOMBIE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BLOODY_SPORE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(FACE_MONSTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SPORE_SKELETON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SPORE_ZOMBIE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(HAT_SPORE_ZOMBIE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DECAYEDER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GIANT_SHELLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CRAWDAD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(NYMPH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SNATCHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MAN_EATER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SNOW_FLINX.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // fly
        event.register(DEMON_EYE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDemonEyeSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(FLYING_FISH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkFlyingFishSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CRIMERA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DRIPPLER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EATER_OF_SOULS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(HARPY.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkHighLevelMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DEMON.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkNetherMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(VOODOO_DEMON.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkNetherMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ANTLION_SWARMER.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GIANT_ANTLION_SWARMER.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GRANITE_ELEMENTAL.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(METEOR_HEAD.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // swim
        event.register(PIRANHA.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SHARK.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BLUE_JELLYFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PINK_JELLYFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GREEN_JELLYFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(WaterAnimal::checkSurfaceWaterAnimalSpawnRules), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ARAPAIMA.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(WaterAnimal::checkSurfaceWaterAnimalSpawnRules), RegisterSpawnPlacementsEvent.Operation.REPLACE);


        // worm
        event.register(DEVOURER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GIANT_WORM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkCaveMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TOMB_CRAWLER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkCaveMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BONE_SERPENT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkNetherMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(WITHER_BONE_SERPENT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkNetherMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // bee
        event.register(HORNET.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // bat
        event.register(CAVE_BAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(JUNGLE_BAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(HELL_BAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkNetherMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ICE_BAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SPORE_BAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 地牢骷髅
        event.register(BASE_BONES.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ANGER_BONES.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDungeonMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SHORT_BONES.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDungeonMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BIG_BONES.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDungeonMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BIG_ANGER_BONES.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDungeonMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BIG_MUSCLE_ANGER_BONES.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDungeonMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BIG_HELMET_ANGER_BONES.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDungeonMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(UNDEAD_VIKING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkUndergroundMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 穿墙怪
        event.register(CURSED_SKULL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDungeonMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(GHOST.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkRoutineMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 远程法师
        event.register(DARK_CASTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkDungeonMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(FIRE_IMP.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkNetherMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);


        // 哥布林军队
        event.register(GOBLIN_SORCERER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GOBLIN_PEON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GOBLIN_ARCHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GOBLIN_WARRIOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GOBLIN_THIEF.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GOBLIN_SCOUT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGoblinScoutSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ANGER_GOBLIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkGroundSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // 本体没有生成但是字模块需要的
        event.register(WANDERING_EYE_FISH.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkNetherMonsterSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        /* *********肉后***************** */
        event.register(WYVERN.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkHighLevelMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(PIXIE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkGroundSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LUMINOUS_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(POSSESS_ARMOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkPossessArmorSpawnCondition), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(WRAITH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkDemonEyeSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CRIMSLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkGroundSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CORRUPT_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkGroundSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(WOODEN_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GOLDEN_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SHADOW_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkNetherMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ICE_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CRIMSON_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CORRUPT_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(HALLOWED_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(JUNGLE_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(MUMMY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DARK_MUMMY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(BLOOD_MUMMY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LIGHT_MUMMY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DARK_LAMIA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LIGHT_LAMIA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(GHOUL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TAINTED_GHOUL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(VILE_GHOUL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(DREAMER_GHOUL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(SAND_POACHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(DERPLING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(HERPLING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks.checkHardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), RegisterSpawnPlacementsEvent.Operation.REPLACE);

    }

    private static DeferredHolder<EntityType<?>, EntityType<BaseSlime>> registerSlime(String name, int color, int size) {
        return TEEntities.ENTITIES.register(
                name,
                () -> EntityType.Builder.<BaseSlime>of((entityType, level) -> new BaseSlime(entityType, level, color, size), MobCategory.MONSTER)
                        .sized(0.6f, 0.6f).clientTrackingRange(10)
                        .build(TEEntities.Key(name)));
    }

    // 用于调整包围盒
    public static DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> registerSimpleMonster(String name, Supplier<AttributeBuilder> builder, float width, float height) {
        return TEEntities.ENTITIES.register(name, () -> EntityType.Builder.<AbstractMonster>of((type, level) -> new AbstractMonster(type, level, builder.get()), MobCategory.MONSTER).clientTrackingRange(10).setTrackingRange(50).sized(width, height).build(TEEntities.Key(name)));
    }

    public static DeferredHolder<EntityType<?>, EntityType<AbstractMonster>> registerSimpleMonster(String name, Supplier<AttributeBuilder> builder) {
        return registerSimpleMonster(name, builder, 1, 1);
    }

    public static void register() {


    }
}
