package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import com.google.common.base.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.*;
import org.confluence.mod.common.entity.monster.demoneye.DemonEye;
import org.confluence.mod.common.entity.monster.humanoid.HumanoidMonster;
import org.confluence.mod.common.entity.monster.humanoid.Wraith;
import org.confluence.mod.common.entity.monster.prefab.AbstractPrefab;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.entity.monster.prefab.FlyMonsterPrefab;
import org.confluence.mod.common.entity.monster.prefab.LandMonsterPrefab;
import org.confluence.mod.common.entity.monster.skeleton.MeleeSkeleton;
import org.confluence.mod.common.entity.monster.skeleton.RangeSkeleton;
import org.confluence.mod.common.entity.monster.slime.*;

public class MonstersEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    // 史莱姆
    public static final RegistryObject<EntityType<BaseSlime>> BLUE_SLIME = registerSlime("blue_slime", 0x73bcf4, 2);
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_SLIME = registerSlime("green_slime", 0x48E920, 2);
    public static final RegistryObject<EntityType<BaseSlime>> PINK_SLIME = registerSlime("pink_slime", 0xFF87B3, 1);
    public static final RegistryObject<EntityType<BaseSlime>> DUNGEON_SLIME = registerSlime("dungeon_slime", 0x6d697b, 3);
    public static final RegistryObject<EntityType<BaseSlime>> CORRUPT_SLIME = registerSlime("corrupt_slime", 0xC91717, 2);
    public static final RegistryObject<EntityType<BaseSlime>> DESERT_SLIME = registerSlime("desert_slime", 0xDCC59a, 2);
    public static final RegistryObject<EntityType<BaseSlime>> JUNGLE_SLIME = registerSlime("jungle_slime", 0x9ae920, 2);
    public static final RegistryObject<EntityType<BaseSlime>> EVIL_SLIME = registerSlime("evil_slime", 0xFF00FF, 2);
    public static final RegistryObject<EntityType<BaseSlime>> ICE_SLIME = registerSlime("ice_slime", 0xB3F0EA, 2);
    public static final RegistryObject<EntityType<BaseSlime>> LAVA_SLIME = registerEntity("lava_slime", EntityType.Builder.<BaseSlime>of((entityType, level) -> new BaseSlime(entityType, level, 0xFFB150, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<BaseSlime>> LUMINOUS_SLIME = registerSlime("luminous_slime", 0xFFFFFF, 2);
    public static final RegistryObject<EntityType<BaseSlime>> CRIMSLIME = registerSlime("crimslime", 0x8B4949, 2);
    public static final RegistryObject<EntityType<BaseSlime>> PURPLE_SLIME = registerSlime("purple_slime", 0xf334f8, 2);
    public static final RegistryObject<EntityType<BaseSlime>> RED_SLIME = registerSlime("red_slime", 0xf83434, 2);
    public static final RegistryObject<EntityType<BaseSlime>> TROPIC_SLIME = registerSlime("tropic_slime", 0x73bcf4, 2);
    public static final RegistryObject<EntityType<BaseSlime>> YELLOW_SLIME = registerSlime("yellow_slime", 0xf8e234, 2);
    public static final RegistryObject<EntityType<HoneySlime>> HONEY_SLIME = registerEntity("honey_slime", EntityType.Builder.<HoneySlime>of((entityType, level) -> new HoneySlime(entityType, level, 0xf8e234), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BlackSlime>> BLACK_SLIME = registerEntity("black_slime", EntityType.Builder.of(BlackSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_DUMPLING_SLIME = registerSlime("green_dumpling_slime", 0x32CD32, 2);
    public static final RegistryObject<EntityType<BaseSlime>> SWAMP_SLIME = registerSlime("swamp_slime", 0x556B2F, 2);
    public static final RegistryObject<EntityType<GoldenSlime>> GOLDEN_SLIME = registerEntity("golden_slime", EntityType.Builder.of(GoldenSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FleshSlime>> FLESH_SLIME = registerEntity("flesh_slime", EntityType.Builder.<FleshSlime>of((entityType, level) -> new FleshSlime(entityType, level, 0xFF0000, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_SLIME = registerEntity("spiked_slime", EntityType.Builder.<SpikedSlime>of((entityType, level) -> new SpikedSlime(entityType, level, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_JUNGLE_SLIME = registerEntity("spiked_jungle_slime", EntityType.Builder.<SpikedSlime>of((entityType, level) -> SpikedJungleSlime.createSpikedJungleSlime(entityType, level, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_ICE_SLIME = registerEntity("spiked_ice_slime", EntityType.Builder.<SpikedSlime>of((entityType, level) -> SpikedJungleSlime.createSpikedIceSlime(entityType, level, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    // 飞行怪
    public static final RegistryObject<EntityType<DemonEye>> DEMON_EYE = registerEntity("demon_eye", EntityType.Builder.of(DemonEye::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AbstractMonster>> CRIMERA = registerSimpleMonster("crimera", FlyMonsterPrefab.CRIMERA_BUILDER, 1.2f, 1.2f);
    public static final RegistryObject<EntityType<AbstractMonster>> EATER_OF_SOULS = registerSimpleMonster("eater_of_souls", FlyMonsterPrefab.EATER_OF_SOULS_BUILDER, 1.2f, 1.2f);
    public static final RegistryObject<EntityType<AbstractMonster>> DRIPPLER = registerSimpleMonster("drippler", FlyMonsterPrefab.DRIPPLER_BUILDER, 1.6f, 1.6f);
    public static final RegistryObject<EntityType<AbstractMonster>> SERVANT_OF_CTHULHU = registerSimpleMonster("servant_of_cthulhu", FlyMonsterPrefab.SERVANT_OF_CTHULHU_BUILDER, 1.1f, 1.1f);
    public static final RegistryObject<EntityType<AbstractMonster>> WANDERING_EYE_FISH = registerSimpleMonster("wandering_eye_fish", FlyMonsterPrefab.WANDERING_EYE_FISH_BUILDER, 1.4f, 1.4f);
    public static final RegistryObject<EntityType<AbstractMonster>> FLYING_FISH = registerSimpleMonster("flying_fish", FlyMonsterPrefab.FLYING_FISH_BUILDER, 0.9F, 0.9F);
    public static final RegistryObject<EntityType<VisualNeuron>> VISUAL_NEURON = registerEntity("visual_neuron", EntityType.Builder.of(VisualNeuron::new, MobCategory.MONSTER).sized(1.2f, 1.2f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Harpy>> HARPY = registerEntity("harpy", EntityType.Builder.<Harpy>of((e, l) -> new Harpy(e, l, new FlyMonsterPrefab().getPrefab().setSpawnWithoutLight()), MobCategory.MONSTER).sized(1f, 2f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Demon>> DEMON = registerEntity("demon", EntityType.Builder.<Demon>of((e, l) -> new Demon(e, l, new FlyMonsterPrefab().getPrefab().setSpawnWithoutLight().setNoFriction()), MobCategory.MONSTER).sized(1f, 2f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Demon>> VOODOO_DEMON = registerEntity("voodoo_demon", EntityType.Builder.<Demon>of((e, l) -> new Demon(e, l, new FlyMonsterPrefab().getPrefab().setSpawnWithoutLight().setNoFriction()), MobCategory.MONSTER).sized(1f, 2f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AntlionSwarmer>> ANTLION_SWARMER = registerEntity("antlion_swarmer", EntityType.Builder.<AntlionSwarmer>of((e, l) -> new AntlionSwarmer(e, l, new FlyMonsterPrefab().getPrefab()), MobCategory.MONSTER).sized(3f, 1.5f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AntlionSwarmer>> GIANT_ANTLION_SWARMER = registerEntity("giant_antlion_swarmer", EntityType.Builder.<AntlionSwarmer>of((e, l) -> new AntlionSwarmer(e, l, new FlyMonsterPrefab().getPrefab()), MobCategory.MONSTER).sized(3.5f, 2f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<GraniteElemental>> GRANITE_ELEMENTAL = registerEntity("granite_elemental", EntityType.Builder.<GraniteElemental>of((e, l) -> new GraniteElemental(e, l, new FlyMonsterPrefab().getPrefab()), MobCategory.MONSTER).sized(1.5f, 1.5f).clientTrackingRange(10));

    // 陆行怪
    public static final RegistryObject<EntityType<MeleeSkeleton>> SPORE_SKELETON = registerEntity("spore_skeleton", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AbstractMonster>> SPORE_ZOMBIE = registerSimpleMonster("spore_zombie", LandMonsterPrefab.SPORE_ZOMBIE_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> HAT_SPORE_ZOMBIE = registerSimpleMonster("hat_spore_zombie", LandMonsterPrefab.HAT_SPORE_ZOMBIE_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<RangeSkeleton>> DECAYEDER = registerEntity("decayeder", EntityType.Builder.<RangeSkeleton>of((e, l) -> new Decayeder(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), MobCategory.MONSTER).sized(1, 1.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BloodySpore>> BLOODY_SPORE = registerEntity("bloody_spore", EntityType.Builder.of(BloodySpore::new, MobCategory.MONSTER).sized(1, 1.5f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BloodCrawler>> BLOOD_CRAWLER = registerEntity("blood_crawler", EntityType.Builder.of(BloodCrawler::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AbstractMonster>> FACE_MONSTER = registerSimpleMonster("face_monster", LandMonsterPrefab.FACE_MONSTER_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> BLOOD_TUMORS = registerSimpleMonster("blood_tumors", LandMonsterPrefab.BLOOD_TUMORS, 0.5F, 0.5F);
    public static final RegistryObject<EntityType<AbstractMonster>> BLOOD_ZOMBIE = registerSimpleMonster("blood_zombie", LandMonsterPrefab.BLOOD_ZOMBIE_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> SNOW_FLINX = registerSimpleMonster("snow_flinx", LandMonsterPrefab.SNOW_FLINX_BUILDER, 1.25F, 1.25F);

    // 水怪
    public static final RegistryObject<EntityType<Piranha>> PIRANHA = registerEntity("piranha", EntityType.Builder.of(Piranha::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> BLUE_JELLYFISH = registerEntity("blue_jellyfish", EntityType.Builder.of(JellyFish::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> PINK_JELLYFISH = registerEntity("pink_jellyfish", EntityType.Builder.of(JellyFish::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Shark>> SHARK = registerEntity("shark", EntityType.Builder.of(Shark::new, MobCategory.MONSTER).sized(2.5F, 1F).clientTrackingRange(10));

    // 蜜蜂
    public static final RegistryObject<EntityType<Hornet>> HORNET = registerEntity("hornet", EntityType.Builder.<Hornet>of((e, l) -> new Hornet(e, l, FlyMonsterPrefab.BEE_BUILDER.get()), MobCategory.MONSTER).sized(0.8f, 1.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<LittleHornet>> LITTLE_HORNET = registerEntity("little_hornet", EntityType.Builder.of(LittleHornet::new, MobCategory.CREATURE).sized(0.4f, 0.4f).clientTrackingRange(10));

    // 蝙蝠
    public static final RegistryObject<EntityType<AbstractMonster>> CAVE_BAT = registerSimpleMonster("cave_bat", FlyMonsterPrefab.CAVE_BAT_BUILDER, 1.6f, 1.6f);
    public static final RegistryObject<EntityType<AbstractMonster>> JUNGLE_BAT = registerSimpleMonster("jungle_bat", FlyMonsterPrefab.JUNGLE_BAT_BUILDER, 1.6f, 1.6f);
    public static final RegistryObject<EntityType<AbstractMonster>> HELL_BAT = registerEntity("hell_bat", EntityType.Builder.<AbstractMonster>of((type, level) -> new AbstractMonster(type, level, FlyMonsterPrefab.HELL_BAT_BUILDER.get()), MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<AbstractMonster>> ICE_BAT = registerSimpleMonster("ice_bat", FlyMonsterPrefab.ICE_BAT_BUILDER, 1.6f, 1.6f);
    public static final RegistryObject<EntityType<AbstractMonster>> SPORE_BAT = registerSimpleMonster("spore_bat", FlyMonsterPrefab.SPORE_BAT_BUILDER, 1.6f, 1.6f);

    // 蠕虫
    public static final RegistryObject<EntityType<SurefaceWorm<BaseWormPart>>> DEVOURER = registerEntity("devourer", EntityType.Builder.<SurefaceWorm<BaseWormPart>>of((e, l) -> new SurefaceWorm<>(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight().setNoGravity()), MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseWorm<BaseWormPart>>> TOMB_CRAWLER = registerEntity("tomb_crawler", EntityType.Builder.<BaseWorm<BaseWormPart>>of((e, l) -> BaseWorm.simpleWorm(e, l, AbstractPrefab.WARM_BUILDER.get()), MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseWorm<BaseWormPart>>> GIANT_WORM = registerEntity("giant_worm", EntityType.Builder.<BaseWorm<BaseWormPart>>of((e, l) -> BaseWorm.simpleWorm(e, l, AbstractPrefab.WARM_BUILDER.get()), MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseWorm<BaseWormPart>>> LEECH = registerEntity("leech", EntityType.Builder.<BaseWorm<BaseWormPart>>of((e, l) -> BaseWorm.simpleWorm(e, l, AbstractPrefab.WARM_BUILDER.get()), MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BoneSerpent<BaseWormPart>>> BONE_SERPENT = registerEntity("bone_serpent", EntityType.Builder.<BoneSerpent<BaseWormPart>>of((e, l) -> new BoneSerpent<>(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight().setNoGravity()), MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BoneSerpent<BaseWormPart>>> WITHER_BONE_SERPENT = registerEntity("wither_bone_serpent", EntityType.Builder.<BoneSerpent<BaseWormPart>>of((e, l) -> new BoneSerpent<>(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight().setNoGravity()), MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));

    // 卷壳怪
    public static final RegistryObject<EntityType<GiantShelly>> GIANT_SHELLY = registerEntity("giant_shelly", EntityType.Builder.of(GiantShelly::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Crawdad>> CRAWDAD = registerEntity("crawdad", EntityType.Builder.of(Crawdad::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 宁芙
    public static final RegistryObject<EntityType<Nymph>> NYMPH = registerEntity("nymph", EntityType.Builder.of(Nymph::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(10));

    // 抓人草
    public static final RegistryObject<EntityType<Snatcher>> SNATCHER = registerEntity("snatcher", EntityType.Builder.<Snatcher>of((e, l) -> new Snatcher(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Snatcher>> MAN_EATER = registerEntity("man_eater", EntityType.Builder.<Snatcher>of((e, l) -> new Snatcher(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 地牢骷髅
    public static final RegistryObject<EntityType<MeleeSkeleton>> BASE_BONES = registerEntity("base_bones", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> ANGER_BONES = registerEntity("anger_bones", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> SHORT_BONES = registerEntity("short_bones", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.55F, 1.65F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_BONES = registerEntity("big_bones", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.85F, 2.25F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_ANGER_BONES = registerEntity("big_anger_bones", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.9F, 2.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_MUSCLE_ANGER_BONES = registerEntity("big_muscle_anger_bones", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.95F, 2.45F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_HELMET_ANGER_BONES = registerEntity("big_helmet_anger_bones", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(1F, 2.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> UNDEAD_VIKING = registerEntity("undead_viking", EntityType.Builder.<MeleeSkeleton>of((e, l) -> new MeleeSkeleton(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(1F, 2.6F).clientTrackingRange(10));

    // 穿墙怪
    public static final RegistryObject<EntityType<CursedSkull>> CURSED_SKULL = registerEntity("cursed_skull", EntityType.Builder.<CursedSkull>of((e, l) -> new CursedSkull(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Ghost>> GHOST = registerEntity("ghost", EntityType.Builder.<Ghost>of((e, l) -> new Ghost(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeteorHead>> METEOR_HEAD = registerEntity("meteor_head", EntityType.Builder.<MeteorHead>of((e, l) -> new MeteorHead(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 远程法师
    public static final RegistryObject<EntityType<RangeShooter>> DARK_CASTER = registerEntity("dark_caster", EntityType.Builder.<RangeShooter>of((e, l) -> new RangeShooter(e, l, TEProjectileEntities.DARK_CASTER_PROJ, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<RangeShooter>> GOBLIN_SORCERER = registerEntity("goblin_sorcerer", EntityType.Builder.<RangeShooter>of((e, l) -> new RangeShooter(e, l, TEProjectileEntities.DARK_CASTER_PROJ, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FireImpEntity>> FIRE_IMP = registerEntity("fire_imp", EntityType.Builder.<FireImpEntity>of((e, l) -> new FireImpEntity(e, l, TEProjectileEntities.FIRE_IMP_PROJ, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(0.65F, 1).clientTrackingRange(10));

    // 哥布林军队
    public static final RegistryObject<EntityType<HumanoidMonster>> GOBLIN_ARCHER = registerEntity("goblin_archer", EntityType.Builder.<HumanoidMonster>of((e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setMainHand(Items.BOW.getDefaultInstance()).setSpawnWithoutLight().addGoal((goals, mob)->goals.addGoal(1,new FactorFloatGoal(mob)))), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidMonster>> GOBLIN_PEON = registerEntity("goblin_peon", EntityType.Builder.<HumanoidMonster>of((e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setSpawnWithoutLight().addGoal((goals,mob)->goals.addGoal(1,new FactorFloatGoal(mob)))), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidMonster>> GOBLIN_WARRIOR = registerEntity("goblin_warrior", EntityType.Builder.<HumanoidMonster>of((e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setMainHand(Items.STONE_SWORD.getDefaultInstance()).setSpawnWithoutLight().addGoal((goals,mob)->goals.addGoal(1,new FactorFloatGoal(mob)))), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidMonster>> GOBLIN_THIEF = registerEntity("goblin_thief", EntityType.Builder.<HumanoidMonster>of((e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setSpawnWithoutLight().addGoal((goals,mob)->goals.addGoal(1,new FactorFloatGoal(mob)))), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidMonster>> GOBLIN_SCOUT = registerEntity("goblin_scout", EntityType.Builder.<HumanoidMonster>of((e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setSpawnWithoutLight().addGoal((goals,mob)->goals.addGoal(1,new FactorFloatGoal(mob)))), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidMonster>> ANGER_GOBLIN = registerEntity("anger_goblin", EntityType.Builder.<HumanoidMonster>of((e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().asHumanoid().setMainHand(Items.GOLDEN_SWORD.getDefaultInstance()).setSpawnWithoutLight().addGoal((goals,mob)->goals.addGoal(1,new FactorFloatGoal(mob)))), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));

    // 饿鬼
    public static final RegistryObject<EntityType<TheHungry>> THE_HUNGRY = registerEntity("the_hungry", EntityType.Builder.<TheHungry>of((e, l) -> new TheHungry(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HillHungry>> HILL_HUNGRY = registerEntity("hill_hungry", EntityType.Builder.<HillHungry>of((e, l) -> new HillHungry(e, l, new AbstractPrefab().getPrefab()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 肉后
    public static final RegistryObject<EntityType<Wyvern<BaseWormPart>>> WYVERN = registerEntity("wyvern", EntityType.Builder.<Wyvern<BaseWormPart>>of((e, l) -> new Wyvern<>(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight().setNoGravity()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Pixie>> PIXIE = registerEntity("pixie", EntityType.Builder.<Pixie>of((e, l) -> new Pixie(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidMonster>> POSSESS_ARMOR = registerEntity("possess_armor", EntityType.Builder.<HumanoidMonster>of((e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().getPrefab().setDeathSound(ModSoundEvents.SOUL_DEATH).setHurtSound(ModSoundEvents.METAL_HURT)), MobCategory.MONSTER).sized(1F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidMonster>> POSSESS_ARMOR_VOID_VESSEL = registerEntity("possess_armor_void_vessel", EntityType.Builder.<HumanoidMonster>of((e, l) -> new HumanoidMonster(e, l, new AbstractPrefab().getPrefab().setDeathSound(ModSoundEvents.SOUL_DEATH).setHurtSound(ModSoundEvents.METAL_HURT)), MobCategory.MONSTER).sized(1F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Wraith>> WRAITH = registerEntity("wraith", EntityType.Builder.of(Wraith::new, MobCategory.MONSTER).sized(1F, 2F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<WoodenMimic>> WOODEN_MIMIC = registerEntity("wooden_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoodenMimic>> GOLDEN_MIMIC = registerEntity("golden_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoodenMimic>> ICE_MIMIC = registerEntity("ice_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoodenMimic>> SHADOW_MIMIC = registerEntity("shadow_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CrimsonMimic>> CRIMSON_MIMIC = registerEntity("crimson_mimic", EntityType.Builder.of(CrimsonMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CrimsonMimic>> CORRUPT_MIMIC = registerEntity("corrupt_mimic", EntityType.Builder.of(CrimsonMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CrimsonMimic>> HALLOWED_MIMIC = registerEntity("hallowed_mimic", EntityType.Builder.of(CrimsonMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CrimsonMimic>> JUNGLE_MIMIC = registerEntity("jungle_mimic", EntityType.Builder.of(CrimsonMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));

    public static final RegistryObject<EntityType<AbstractMonster>> MUMMY = registerSimpleMonster("mummy", LandMonsterPrefab.MUMMY_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> DARK_MUMMY = registerSimpleMonster("dark_mummy", LandMonsterPrefab.EVIL_MUMMY_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> BLOOD_MUMMY = registerSimpleMonster("blood_mummy", LandMonsterPrefab.EVIL_MUMMY_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> LIGHT_MUMMY = registerSimpleMonster("light_mummy", LandMonsterPrefab.MUMMY_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> DARK_LAMIA = registerSimpleMonster("dark_lamia", LandMonsterPrefab.LAMIA_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> LIGHT_LAMIA = registerSimpleMonster("light_lamia", LandMonsterPrefab.LAMIA_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> GHOUL = registerSimpleMonster("ghoul", LandMonsterPrefab.GHOUL_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> TAINTED_GHOUL = registerSimpleMonster("tainted_ghoul", LandMonsterPrefab.GHOUL_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> VILE_GHOUL = registerSimpleMonster("vile_ghoul", LandMonsterPrefab.GHOUL_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<AbstractMonster>> DREAMER_GHOUL = registerSimpleMonster("dreamer_ghoul", LandMonsterPrefab.GHOUL_BUILDER, 0.75F, 1.95F);
    public static final RegistryObject<EntityType<SandPoacher>> SAND_POACHER = registerEntity("sand_poacher", EntityType.Builder.of(SandPoacher::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<Piranha>> ARAPAIMA = registerEntity("arapaima", EntityType.Builder.of(Piranha::new, MobCategory.MONSTER).sized(2.2F, 0.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> GREEN_JELLYFISH = registerEntity("green_jellyfish", EntityType.Builder.of(JellyFish::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<JumpAttackMonster>> DERPLING = registerEntity("derpling", EntityType.Builder.<JumpAttackMonster>of((e, l) -> new JumpAttackMonster(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JumpAttackMonster>> HERPLING = registerEntity("herpling", EntityType.Builder.<JumpAttackMonster>of((e, l) -> new JumpAttackMonster(e, l, new AbstractPrefab().getPrefab().setSpawnWithoutLight()), MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return PortDeferredRegisterExtension.register(ENTITIES, name, id -> builder.build(id.toString()));
    }

    private static RegistryObject<EntityType<BaseSlime>> registerSlime(String name, int color, int size) {
        return registerEntity(name, EntityType.Builder.<BaseSlime>of(
                (entityType, level) -> new BaseSlime(entityType, level, color, size),
                MobCategory.MONSTER
        ).sized(0.6F, 0.6F).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<AbstractMonster>> registerSimpleMonster(String name, Supplier<AttributeBuilder> builder, float width, float height) {
        return registerEntity(name, EntityType.Builder.<AbstractMonster>of(
                (type, level) -> new AbstractMonster(type, level, builder.get()),
                MobCategory.MONSTER
        ).sized(width, height).clientTrackingRange(10));
    }
}
