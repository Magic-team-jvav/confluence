package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.*;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.monster.slime.*;
import org.confluence.mod.common.entity.npc.TownSlimeRescue;

public class MonsterEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    // 地表与森林：史莱姆（passiveByDay 控制白天是否被动）
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_SLIME = registerSlime("green_slime", true, 2, true);
    public static final RegistryObject<EntityType<BaseSlime>> BLUE_SLIME = registerSlime("blue_slime", true, 2, true);
    public static final RegistryObject<EntityType<BaseSlime>> PURPLE_SLIME = registerSlime("purple_slime", true, 2, true);
    public static final RegistryObject<EntityType<Pinky>> PINK_SLIME = registerEntity("pink_slime", EntityType.Builder.of(Pinky::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    // 地表与森林：金史莱姆
    public static final RegistryObject<EntityType<GoldenSlime>> GOLDEN_SLIME = registerEntity("golden_slime", EntityType.Builder.of(GoldenSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    // 地表与森林：僵尸与夜间敌怪
    public static final RegistryObject<EntityType<Zombie>> ZOMBIE = registerEntity("zombie", EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DemonEye>> DEMON_EYE = registerEntity("demon_eye", EntityType.Builder.<DemonEye>of(DemonEye::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidWarriorMonster>> POSSESS_ARMOR = registerHumanoidLand("possess_armor", 1F, 2F, Items.AIR.getDefaultInstance(),
            BaseWarriorMonster.LandSoundProfile.POSSESSED_ARMOR, BaseWarriorMonster.LandAnimationProfile.NONE);
    public static final RegistryObject<EntityType<Wraith>> WRAITH = registerEntity("wraith", EntityType.Builder.of(Wraith::new, MobCategory.MONSTER).sized(1F, 2F).clientTrackingRange(10));

    // 地表与森林：宝箱怪
    public static final RegistryObject<EntityType<WoodenMimic>> WOODEN_MIMIC = registerEntity("wooden_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));

    // 沼泽：史莱姆
    public static final RegistryObject<EntityType<BaseSlime>> SWAMP_SLIME = registerSlime("swamp_slime", false, 2);

    // 空岛与高空：鸟妖和飞龙
    public static final RegistryObject<EntityType<Harpy>> HARPY = registerEntity("harpy", EntityType.Builder.of(Harpy::new, MobCategory.MONSTER).sized(1f, 2f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Wyvern>> WYVERN = registerEntity("wyvern", EntityType.Builder.of(Wyvern::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10).updateInterval(1));

    // 地下与洞穴：史莱姆及分裂体
    public static final RegistryObject<EntityType<BaseSlime>> RED_SLIME = registerSlime("red_slime", false, 2);
    public static final RegistryObject<EntityType<BaseSlime>> YELLOW_SLIME = registerSlime("yellow_slime", false, 2);
    public static final RegistryObject<EntityType<BlackSlime>> BLACK_SLIME = registerEntity("black_slime", EntityType.Builder.of(BlackSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MotherSlime>> MOTHER_SLIME = registerEntity("mother_slime", EntityType.Builder.of(MotherSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BabySlime>> BABY_SLIME = registerEntity("baby_slime", EntityType.Builder.of(BabySlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    // 地下与洞穴：骷髅与蝙蝠
    public static final RegistryObject<EntityType<MeleeSkeleton>> ARMORED_SKELETON = DevelopmentSpawnPolicy.developmentOnly(registerSkeleton("armored_skeleton", 0.8F, 2.45F, MeleeSkeleton.BehaviorProfile.ARMORED_SKELETON));
    public static final RegistryObject<EntityType<Decayeder>> DECAYEDER = registerEntity("decayeder", EntityType.Builder.of(Decayeder::new, MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> CAVE_BAT = registerEntity("cave_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> GIANT_BAT = DevelopmentSpawnPolicy.developmentOnly(registerEntity("giant_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.GIANT), MobCategory.MONSTER).sized(0.6F, 1.4F).clientTrackingRange(10)));

    // 地下与洞穴：蠕虫
    public static final RegistryObject<EntityType<SimpleWormMonster>> GIANT_WORM = registerWorm("giant_worm", 12, 2F, 2F);
    public static final RegistryObject<EntityType<SimpleWormMonster>> DIGGER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("digger", EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, SimpleWormMonster.Role.UNDERGROUND, SimpleWormMonster.Anatomy.DIGGER), MobCategory.MONSTER).sized(1.6F, 1.05F).clientTrackingRange(10).updateInterval(1)));

    // 地下与洞穴：法师
    public static final RegistryObject<EntityType<DarkCaster>> TIM = DevelopmentSpawnPolicy.developmentOnly(registerCaster("tim", 1.0F, 3.2F, DarkCaster.Profile.TIM));
    public static final RegistryObject<EntityType<DarkCaster>> RUNE_WIZARD = DevelopmentSpawnPolicy.developmentOnly(registerCaster("rune_wizard", 1.0F, 3.2F, DarkCaster.Profile.RUNE_WIZARD));

    // 地下与洞穴：岩石巨人及稀有敌怪
    public static final RegistryObject<EntityType<RockGolem>> ROCK_GOLEM = DevelopmentSpawnPolicy.developmentOnly(registerEntity("rock_golem", EntityType.Builder.of(RockGolem::new, MobCategory.MONSTER).sized(1.5F, 2.8F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<GiantShelly>> GIANT_SHELLY = registerEntity("giant_shelly", EntityType.Builder.of(GiantShelly::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Crawdad>> CRAWDAD = registerEntity("crawdad", EntityType.Builder.of(Crawdad::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Nymph>> NYMPH = registerEntity("nymph", EntityType.Builder.of(Nymph::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(10));

    // 地下与洞穴：宝箱怪
    public static final RegistryObject<EntityType<WoodenMimic>> GOLDEN_MIMIC = registerEntity("golden_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));

    // 地下水域：水母与琵琶鱼
    public static final RegistryObject<EntityType<JellyFish>> BLUE_JELLYFISH = registerJellyFish("blue_jellyfish", JellyFish.Profile.ROUTINE);
    public static final RegistryObject<EntityType<JellyFish>> GREEN_JELLYFISH = registerJellyFish("green_jellyfish", JellyFish.Profile.GREEN);
    public static final RegistryObject<EntityType<AnglerFish>> ANGLER_FISH = DevelopmentSpawnPolicy.developmentOnly(registerEntity("angler_fish", EntityType.Builder.of(AnglerFish::new, MobCategory.MONSTER).sized(0.8F, 0.6F).clientTrackingRange(10)));

    // 蜘蛛洞：爬墙蜘蛛与黑隐士
    public static final RegistryObject<EntityType<ClimbingSpider>> WALL_CREEPER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("wall_creeper", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.WALL), MobCategory.MONSTER).sized(2.0F, 2.0F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<ClimbingSpider>> BLACK_RECLUSE = DevelopmentSpawnPolicy.developmentOnly(registerEntity("black_recluse", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.BLACK_RECLUSE), MobCategory.MONSTER).sized(2.0F, 2.2F).clientTrackingRange(10)));

    // 生命树：侏儒
    public static final RegistryObject<EntityType<Gnome>> GNOME = DevelopmentSpawnPolicy.developmentOnly(registerEntity("gnome", EntityType.Builder.of(Gnome::new, MobCategory.MONSTER).sized(0.5F, 0.8F).clientTrackingRange(10)));

    // 花岗岩洞：元素与巨人
    public static final RegistryObject<EntityType<GraniteElemental>> GRANITE_ELEMENTAL = registerEntity("granite_elemental", EntityType.Builder.of(GraniteElemental::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<GraniteGolem>> GRANITE_GOLEM = DevelopmentSpawnPolicy.developmentOnly(registerEntity("granite_golem", EntityType.Builder.of(GraniteGolem::new, MobCategory.MONSTER).sized(1.5F, 2.05F).clientTrackingRange(10)));

    // 大理石洞：装甲步兵
    public static final RegistryObject<EntityType<Hoplite>> HOPLITE = DevelopmentSpawnPolicy.developmentOnly(registerEntity("hoplite", EntityType.Builder.of(Hoplite::new, MobCategory.MONSTER).sized(1.0F, 2.7F).clientTrackingRange(10)));

    // 丛林：史莱姆
    public static final RegistryObject<EntityType<BaseSlime>> JUNGLE_SLIME = registerSlime("jungle_slime", true, 2);
    public static final RegistryObject<EntityType<SpikedJungleSlime>> SPIKED_JUNGLE_SLIME = registerEntity("spiked_jungle_slime", EntityType.Builder.of(SpikedJungleSlime::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));

    // 丛林：黄蜂及幼蜂
    public static final RegistryObject<EntityType<Hornet>> HORNET = registerEntity("hornet", EntityType.Builder.<Hornet>of(Hornet::new, MobCategory.MONSTER).sized(0.8F, 1.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Hornet>> MOSS_HORNET = DevelopmentSpawnPolicy.developmentOnly(registerEntity("moss_hornet", EntityType.Builder.<Hornet>of((type, level) -> new Hornet(type, level, 6.0F / 7.0F), MobCategory.MONSTER).sized(1.0F, 2.1F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<LittleHornet>> LITTLE_HORNET = registerEntity("little_hornet", EntityType.Builder.of(LittleHornet::new, MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10));

    // 丛林：蝙蝠与飞狐
    public static final RegistryObject<EntityType<CaveBat>> JUNGLE_BAT = registerEntity("jungle_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> GIANT_FLYING_FOX = registerEntity("giant_flying_fox", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.GIANT), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));

    // 丛林：食人植物
    public static final RegistryObject<EntityType<Snatcher>> SNATCHER = registerSnatcher("snatcher", Snatcher.Profile.SNATCHER);
    public static final RegistryObject<EntityType<Snatcher>> MAN_EATER = registerSnatcher("man_eater", Snatcher.Profile.MAN_EATER);

    // 丛林：蜘蛛、蹦跳兽与陆龟
    public static final RegistryObject<EntityType<ClimbingSpider>> JUNGLE_CREEPER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("jungle_creeper", EntityType.Builder.<ClimbingSpider>of((type, level) -> new ClimbingSpider(type, level, ClimbingSpider.Kind.JUNGLE), MobCategory.MONSTER).sized(2.0F, 2.2F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<Derpling>> DERPLING = registerEntity("derpling", EntityType.Builder.of(Derpling::new, MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<GiantTortoise>> GIANT_TORTOISE = registerEntity("giant_tortoise", EntityType.Builder.of(GiantTortoise::new, MobCategory.MONSTER).sized(2.25F, 1.85F).clientTrackingRange(10));

    // 丛林：骷髅博士
    public static final RegistryObject<EntityType<BaseWarriorMonster>> DOCTOR_BONES = DevelopmentSpawnPolicy.developmentOnly(registerLand("doctor_bones", 1.0F, 2.0F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON));

    // 丛林水域：食人鱼与巨骨舌鱼
    public static final RegistryObject<EntityType<Piranha>> PIRANHA = registerEntity("piranha", EntityType.Builder.<Piranha>of(Piranha::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Arapaima>> ARAPAIMA = registerEntity("arapaima", EntityType.Builder.of(Arapaima::new, MobCategory.MONSTER).sized(2.2F, 0.7F).clientTrackingRange(10));

    // 丛林：宝箱怪
    public static final RegistryObject<EntityType<BaseMimic>> JUNGLE_MIMIC = registerEntity("jungle_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));

    // 冰雪：史莱姆
    public static final RegistryObject<EntityType<IceSlime>> ICE_SLIME = registerEntity("ice_slime", EntityType.Builder.of(IceSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SpikedIceSlime>> SPIKED_ICE_SLIME = registerEntity("spiked_ice_slime", EntityType.Builder.of(SpikedIceSlime::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));

    // 冰雪：维京海盗
    public static final RegistryObject<EntityType<MeleeSkeleton>> UNDEAD_VIKING = registerSkeleton("undead_viking", 1F, 2.6F);
    public static final RegistryObject<EntityType<MeleeSkeleton>> ARMORED_VIKING = DevelopmentSpawnPolicy.developmentOnly(registerSkeleton("armored_viking", 0.8F, 2.35F, MeleeSkeleton.BehaviorProfile.ARMORED_VIKING));

    // 冰雪：蝙蝠、雪怪与陆龟
    public static final RegistryObject<EntityType<CaveBat>> ICE_BAT = registerEntity("ice_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ICE), MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseWarriorMonster>> SNOW_FLINX = registerLand("snow_flinx", 1.25F, 1.25F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 0.8, false);
    public static final RegistryObject<EntityType<GiantTortoise>> ICE_TORTOISE = DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_tortoise", EntityType.Builder.of(GiantTortoise::new, MobCategory.MONSTER).sized(2.25F, 1.85F).clientTrackingRange(10)));

    // 冰雪：冰雪精与鱼人
    public static final RegistryObject<EntityType<IceElemental>> ICE_ELEMENTAL = DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_elemental", EntityType.Builder.of(IceElemental::new, MobCategory.MONSTER).sized(1.3F, 1.85F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<FrostFighter>> ICY_MERMAN = DevelopmentSpawnPolicy.developmentOnly(registerEntity("icy_merman", EntityType.Builder.<FrostFighter>of((type, level) -> new FrostFighter(type, level, FrostFighter.Kind.MERMAN), MobCategory.MONSTER).sized(0.8F, 2.1F).clientTrackingRange(10)));

    // 冰雪：宝箱怪
    public static final RegistryObject<EntityType<WoodenMimic>> ICE_MIMIC = registerEntity("ice_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));

    // 沙漠：史莱姆与木乃伊
    public static final RegistryObject<EntityType<BaseSlime>> DESERT_SLIME = registerSlime("desert_slime", false, 2);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> MUMMY = registerJumpingLand("mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.0, JumpingWarriorMonster.ContactProfile.MUMMY);

    // 地下沙漠：蚁狮及幼虫
    public static final RegistryObject<EntityType<Antlion>> ANTLION = DevelopmentSpawnPolicy.developmentOnly(registerEntity("antlion", EntityType.Builder.of(Antlion::new, MobCategory.MONSTER).sized(2.85F, 1.2F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<BaseWarriorMonster>> ANTLION_LARVA = DevelopmentSpawnPolicy.developmentOnly(registerLand("antlion_larva", 0.6F, 0.4F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.3, true));
    public static final RegistryObject<EntityType<AntlionCharger>> ANTLION_CHARGER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("antlion_charger", EntityType.Builder.of(AntlionCharger::new, MobCategory.MONSTER).sized(2.85F, 1.2F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<AntlionSwarmer>> ANTLION_SWARMER = registerEntity("antlion_swarmer", EntityType.Builder.of(AntlionSwarmer::new, MobCategory.MONSTER).sized(3.0F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AntlionSwarmer>> GIANT_ANTLION_SWARMER = registerEntity("giant_antlion_swarmer", EntityType.Builder.of(AntlionSwarmer::new, MobCategory.MONSTER).sized(3.5F, 2.0F).clientTrackingRange(10));

    // 地下沙漠：蛇蜥怪、沙贼与沙漠蠕虫
    public static final RegistryObject<EntityType<BaseWarriorMonster>> BASILISK = DevelopmentSpawnPolicy.developmentOnly(registerLand("basilisk", 1.2F, 1.55F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.8, true));
    public static final RegistryObject<EntityType<SandPoacher>> SAND_POACHER = registerEntity("sand_poacher", EntityType.Builder.of(SandPoacher::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SimpleWormMonster>> TOMB_CRAWLER = registerWorm("tomb_crawler", 12, 2F, 2F);

    // 地下沙漠：拉弥亚与食尸鬼
    public static final RegistryObject<EntityType<BaseWarriorMonster>> DARK_LAMIA = registerLand("dark_lamia", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.3, true);
    public static final RegistryObject<EntityType<BaseWarriorMonster>> LIGHT_LAMIA = registerLand("light_lamia", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.3, true);
    public static final RegistryObject<EntityType<BaseWarriorMonster>> GHOUL = registerLand("ghoul", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.6, true);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> TAINTED_GHOUL = registerJumpingLand("tainted_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, JumpingWarriorMonster.ContactProfile.TAINTED_GHOUL);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> VILE_GHOUL = registerJumpingLand("vile_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, JumpingWarriorMonster.ContactProfile.VILE_GHOUL);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> DREAMER_GHOUL = registerJumpingLand("dreamer_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, JumpingWarriorMonster.ContactProfile.DREAMER_GHOUL);

    // 邪恶沙漠：木乃伊与沙漠幽魂
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> DARK_MUMMY = registerJumpingLand("dark_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.5, JumpingWarriorMonster.ContactProfile.DARK_MUMMY);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> BLOOD_MUMMY = registerJumpingLand("blood_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.5, JumpingWarriorMonster.ContactProfile.BLOOD_MUMMY);
    public static final RegistryObject<EntityType<DesertSpirit>> DESERT_SPIRIT = DevelopmentSpawnPolicy.developmentOnly(registerEntity("desert_spirit", EntityType.Builder.of(DesertSpirit::new, MobCategory.MONSTER).sized(1.0F, 2.2F).clientTrackingRange(10)));

    // 神圣沙漠：光明木乃伊
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> LIGHT_MUMMY = registerJumpingLand("light_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.0, JumpingWarriorMonster.ContactProfile.LIGHT_MUMMY);

    // 腐化：史莱姆及分裂体
    public static final RegistryObject<EntityType<CorruptSlime>> CORRUPT_SLIME = registerEntity("corrupt_slime", EntityType.Builder.of(CorruptSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Slimeling>> SLIMELING = registerEntity("slimeling", EntityType.Builder.of(Slimeling::new, MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Slimer>> SLIMER = registerEntity("slimer", EntityType.Builder.of(Slimer::new, MobCategory.MONSTER).sized(1.0F, 0.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WinglessSlimer>> WINGLESS_SLIMER = registerEntity("wingless_slimer", EntityType.Builder.of(WinglessSlimer::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10));

    // 腐化：噬魂怪与腐化者
    public static final RegistryObject<EntityType<EaterOfSouls>> EATER_OF_SOULS = registerEntity("eater_of_souls", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Corruptor>> CORRUPTOR = registerEntity("corruptor", EntityType.Builder.of(Corruptor::new, MobCategory.MONSTER).sized(2.2F, 1.2F).clientTrackingRange(10));

    // 腐化：蠕虫
    public static final RegistryObject<EntityType<SimpleWormMonster>> DEVOURER = registerWorm("devourer", 12, 2F, 2F, SimpleWormMonster.Role.SURFACE);
    public static final RegistryObject<EntityType<SimpleWormMonster>> WORLD_FEEDER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("world_feeder", EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, SimpleWormMonster.Role.SURFACE, SimpleWormMonster.Anatomy.WORLD_FEEDER), MobCategory.MONSTER).sized(2.0F, 1.1F).clientTrackingRange(10).updateInterval(1)));

    // 腐化：爬藤怪与宝箱怪
    public static final RegistryObject<EntityType<SpittingPlant>> CLINGER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("clinger", EntityType.Builder.<SpittingPlant>of((type, level) -> new SpittingPlant(type, level, Snatcher.Profile.CLINGER), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10)));
    public static final RegistryObject<EntityType<BaseMimic>> CORRUPT_MIMIC = registerEntity("corrupt_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));

    // 猩红：史莱姆、脸怪与蹦跳兽
    public static final RegistryObject<EntityType<Crimslime>> CRIMSLIME = registerEntity("crimslime", EntityType.Builder.of(Crimslime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseWarriorMonster>> FACE_MONSTER = registerLand("face_monster", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.FACE_MONSTER, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.0, true, BaseWarriorMonster.DoorBehavior.OPEN);
    public static final RegistryObject<EntityType<Derpling>> HERPLING = registerEntity("herpling", EntityType.Builder.of(Derpling::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 猩红：蜘蛛与飞行敌怪
    public static final RegistryObject<EntityType<BloodCrawler>> BLOOD_CRAWLER = registerEntity("blood_crawler", EntityType.Builder.of(BloodCrawler::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EaterOfSouls>> CRIMERA = registerEntity("crimera", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BloodySpore>> BLOODY_SPORE = registerEntity("bloody_spore", EntityType.Builder.of(BloodySpore::new, MobCategory.MONSTER).sized(1, 1.5f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BloodTumor>> BLOOD_TUMORS = registerEntity("blood_tumors", EntityType.Builder.of(BloodTumor::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));

    // 猩红水域：血蛭与血水母
    public static final RegistryObject<EntityType<Piranha>> BLOOD_FEEDER = registerEntity("blood_feeder", EntityType.Builder.<Piranha>of(Piranha::new, MobCategory.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> BLOOD_JELLY = DevelopmentSpawnPolicy.developmentOnly(registerEntity("blood_jelly", EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, JellyFish.Profile.ROUTINE), MobCategory.MONSTER).sized(0.55F, 0.85F).clientTrackingRange(10)));

    // 猩红：宝箱怪
    public static final RegistryObject<EntityType<BaseMimic>> CRIMSON_MIMIC = registerEntity("crimson_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));

    // 神圣：史莱姆、蝙蝠与妖精
    public static final RegistryObject<EntityType<LuminousSlime>> LUMINOUS_SLIME = registerEntity("luminous_slime", EntityType.Builder.of(LuminousSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> ILLUMINANT_BAT = DevelopmentSpawnPolicy.developmentOnly(registerEntity("illuminant_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ILLUMINANT), MobCategory.MONSTER).sized(0.8F, 1.35F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<Pixie>> PIXIE = registerEntity("pixie", EntityType.Builder.of(Pixie::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));

    // 神圣：独角兽与腹足怪
    public static final RegistryObject<EntityType<Unicorn>> UNICORN = registerEntity("unicorn", EntityType.Builder.of(Unicorn::new, MobCategory.MONSTER).sized(1.4F, 2.25F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Gastropod>> GASTROPOD = registerEntity("gastropod", EntityType.Builder.of(Gastropod::new, MobCategory.MONSTER).sized(0.7F, 0.75F).clientTrackingRange(10));

    // 地下神圣：附魔剑、混沌精与宝箱怪
    public static final RegistryObject<EntityType<EnchantedSword>> ENCHANTED_SWORD = registerEntity("enchanted_sword_monster", EntityType.Builder.of(EnchantedSword::new, MobCategory.MONSTER).sized(0.35F, 1.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<ChaosElemental>> CHAOS_ELEMENTAL = registerEntity("chaos_elemental", EntityType.Builder.of(ChaosElemental::new, MobCategory.MONSTER).sized(0.7F, 1.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseMimic>> HALLOWED_MIMIC = registerEntity("hallowed_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));

    // 发光蘑菇：孢子僵尸、骷髅与蝙蝠
    public static final RegistryObject<EntityType<SporeZombie>> SPORE_ZOMBIE = registerEntity("spore_zombie", EntityType.Builder.of(SporeZombie::new, MobCategory.MONSTER).sized(0.75F, 1.95F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HatSporeZombie>> HAT_SPORE_ZOMBIE = registerEntity("hat_spore_zombie", EntityType.Builder.of(HatSporeZombie::new, MobCategory.MONSTER).sized(0.75F, 1.95F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> SPORE_SKELETON = registerEntity("spore_skeleton", EntityType.Builder.<MeleeSkeleton>of((type, level) -> new MeleeSkeleton(type, level, true, MeleeSkeleton.BehaviorProfile.OPEN_DOORS), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> SPORE_BAT = registerEntity("spore_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));

    // 发光蘑菇：真菌球怪
    public static final RegistryObject<EntityType<Snatcher>> FUNGI_BULB = DevelopmentSpawnPolicy.developmentOnly(registerSnatcher("fungi_bulb", Snatcher.Profile.FUNGI_BULB));
    public static final RegistryObject<EntityType<SpittingPlant>> GIANT_FUNGI_BULB = DevelopmentSpawnPolicy.developmentOnly(registerEntity("giant_fungi_bulb", EntityType.Builder.<SpittingPlant>of((type, level) -> new SpittingPlant(type, level, Snatcher.Profile.GIANT_FUNGI_BULB), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10)));

    // 发光蘑菇水域：蘑菇水母
    public static final RegistryObject<EntityType<JellyFish>> FUNGO_FISH = DevelopmentSpawnPolicy.developmentOnly(registerEntity("fungo_fish", EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, JellyFish.Profile.FUNGO), MobCategory.MONSTER).sized(0.65F, 1.0F).clientTrackingRange(10)));

    // 海洋：鲨鱼、水母与史莱姆
    public static final RegistryObject<EntityType<Shark>> SHARK = registerEntity("shark", EntityType.Builder.of(Shark::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> PINK_JELLYFISH = registerJellyFish("pink_jellyfish", JellyFish.Profile.ROUTINE);
    public static final RegistryObject<EntityType<TropicSlime>> TROPIC_SLIME = registerEntity("tropic_slime", EntityType.Builder.of(TropicSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    // 地牢：骷髅及体型变种
    public static final RegistryObject<EntityType<MeleeSkeleton>> BASE_BONES = registerSkeleton("base_bones", 0.65F, 1.85F);
    public static final RegistryObject<EntityType<MeleeSkeleton>> ANGER_BONES = registerSkeleton("anger_bones", 0.65F, 1.85F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> SHORT_BONES = registerSkeleton("short_bones", 0.55F, 1.65F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_BONES = registerSkeleton("big_bones", 0.85F, 2.25F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_ANGER_BONES = registerSkeleton("big_anger_bones", 0.9F, 2.4F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_MUSCLE_ANGER_BONES = registerSkeleton("big_muscle_anger_bones", 0.95F, 2.45F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_HELMET_ANGER_BONES = registerSkeleton("big_helmet_anger_bones", 1F, 2.6F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);

    // 地牢：法师
    public static final RegistryObject<EntityType<DarkCaster>> DARK_CASTER = registerCaster("dark_caster", 0.65F, 1.85F, DarkCaster.Profile.DARK_CASTER);
    public static final RegistryObject<EntityType<DarkCaster>> NECROMANCER = registerCaster("necromancer", 0.7F, 1.9F, DarkCaster.Profile.NECROMANCER);
    public static final RegistryObject<EntityType<DarkCaster>> DIABOLIST = registerCaster("diabolist", 0.7F, 1.9F, DarkCaster.Profile.DIABOLIST);
    public static final RegistryObject<EntityType<DarkCaster>> RAGGED_CASTER = registerCaster("ragged_caster", 0.7F, 1.9F, DarkCaster.Profile.RAGGED_CASTER);

    // 地牢：诅咒骷髅与幽魂
    public static final RegistryObject<EntityType<CursedSkull>> CURSED_SKULL = registerEntity("cursed_skull", EntityType.Builder.of(CursedSkull::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DungeonSpirit>> DUNGEON_SPIRIT = DevelopmentSpawnPolicy.developmentOnly(registerEntity("dungeon_spirit", EntityType.Builder.of(DungeonSpirit::new, MobCategory.MONSTER).sized(0.8F, 0.6F).clientTrackingRange(10)));

    // 地牢：圣骑士与骷髅李
    public static final RegistryObject<EntityType<Paladin>> PALADIN = registerEntity("paladin", EntityType.Builder.of(Paladin::new, MobCategory.MONSTER).sized(1.2F, 2.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<ChargingMonster>> BONE_LEE = registerCharger("bone_lee", 0.7F, 1.9F, 0.82, 6);

    // 地牢：史莱姆与水矢怪
    public static final RegistryObject<EntityType<BaseSlime>> DUNGEON_SLIME = registerSlime("dungeon_slime", false, 3);
    public static final RegistryObject<EntityType<WaterBoltMimic>> WATER_BOLT_MIMIC = DevelopmentSpawnPolicy.developmentOnly(registerEntity("water_bolt_mimic", EntityType.Builder.of(WaterBoltMimic::new, MobCategory.MONSTER).sized(1.0F, 0.9F).clientTrackingRange(10)));

    // 地狱：史莱姆与蝙蝠
    public static final RegistryObject<EntityType<LavaSlime>> LAVA_SLIME = registerEntity("lava_slime", EntityType.Builder.of(LavaSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<CaveBat>> HELL_BAT = registerEntity("hell_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.HELL), MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<CaveBat>> LAVA_BAT = DevelopmentSpawnPolicy.developmentOnly(registerEntity("lava_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.LAVA), MobCategory.MONSTER).sized(0.8F, 1.25F).clientTrackingRange(10).fireImmune()));

    // 地狱：恶魔与火小鬼
    public static final RegistryObject<EntityType<Demon>> DEMON = registerEntity("demon", EntityType.Builder.of(Demon::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Demon>> VOODOO_DEMON = registerEntity("voodoo_demon", EntityType.Builder.of(Demon::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<RedDevil>> RED_DEVIL = DevelopmentSpawnPolicy.developmentOnly(registerEntity("red_devil", EntityType.Builder.<RedDevil>of(RedDevil::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10).fireImmune()));
    public static final RegistryObject<EntityType<FireImp>> FIRE_IMP = registerEntity("fire_imp", EntityType.Builder.of(FireImp::new, MobCategory.MONSTER).sized(0.65F, 1F).clientTrackingRange(10).fireImmune());

    // 地狱：骨蛇与宝箱怪
    public static final RegistryObject<EntityType<SimpleWormMonster>> BONE_SERPENT = registerWorm("bone_serpent", 18, 2F, 2F, SimpleWormMonster.Role.BONE_SERPENT);
    public static final RegistryObject<EntityType<SimpleWormMonster>> WITHER_BONE_SERPENT = registerWorm("wither_bone_serpent", 18, 2F, 2F, SimpleWormMonster.Role.BONE_SERPENT);
    public static final RegistryObject<EntityType<WoodenMimic>> SHADOW_MIMIC = registerEntity("shadow_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));

    // 陨石：流星头
    public static final RegistryObject<EntityType<MeteorHead>> METEOR_HEAD = registerEntity("meteor_head", EntityType.Builder.of(MeteorHead::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 墓地：幽灵
    public static final RegistryObject<EntityType<Ghost>> GHOST = registerEntity("ghost", EntityType.Builder.of(Ghost::new, MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10));

    // 血月：血腥僵尸与滴滴怪
    public static final RegistryObject<EntityType<BaseWarriorMonster>> BLOOD_ZOMBIE = registerAcceleratingLand("blood_zombie", 0.75F, 1.95F, 0.25, 0.8, true,
            BaseWarriorMonster.LandAnimationProfile.WALK_RUN_IDLE_ATTACK, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.DoorBehavior.OPEN);
    public static final RegistryObject<EntityType<Drippler>> DRIPPLER = registerEntity("drippler", EntityType.Builder.of(Drippler::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));

    // 血月与墓地：僵尸新郎、新娘
    public static final RegistryObject<EntityType<BaseWarriorMonster>> THE_GROOM = DevelopmentSpawnPolicy.developmentOnly(registerLand("the_groom", 1.0F, 2.5F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON));
    public static final RegistryObject<EntityType<BaseWarriorMonster>> THE_BRIDE = DevelopmentSpawnPolicy.developmentOnly(registerLand("the_bride", 1.0F, 2.0F, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.0, true, BaseWarriorMonster.DoorBehavior.BLOOD_MOON));

    // 血月垂钓：游荡眼球怪与僵尸鱼人
    public static final RegistryObject<EntityType<FlyingFishMonster>> WANDERING_EYE_FISH = registerFlyingFish(
            "wandering_eye_fish", 1.2F, 1.2F,
            new FlyingFishMonster.PursuitProfile(0.98, 2.2, 0.01, 10));
    public static final RegistryObject<EntityType<ZombieMerman>> ZOMBIE_MERMAN = DevelopmentSpawnPolicy.developmentOnly(registerEntity("zombie_merman", EntityType.Builder.<ZombieMerman>of(ZombieMerman::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10)));

    // 血月与邪恶转化：企鹅和金鱼
    public static final RegistryObject<EntityType<EvilPenguin>> CORRUPT_PENGUIN = DevelopmentSpawnPolicy.developmentOnly(registerEntity("corrupt_penguin", EntityType.Builder.of(EvilPenguin::new, MobCategory.MONSTER).sized(0.7F, 1.0F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<EvilPenguin>> VICIOUS_PENGUIN = DevelopmentSpawnPolicy.developmentOnly(registerEntity("vicious_penguin", EntityType.Builder.of(EvilPenguin::new, MobCategory.MONSTER).sized(0.7F, 1.0F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<Piranha>> CORRUPT_GOLDFISH = DevelopmentSpawnPolicy.developmentOnly(registerEntity("corrupt_goldfish", EntityType.Builder.<Piranha>of((type, level) -> new Piranha(type, level, Piranha.AnimationProfile.GOLDFISH), MobCategory.MONSTER).sized(0.7F, 0.45F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<Piranha>> VICIOUS_GOLDFISH = DevelopmentSpawnPolicy.developmentOnly(registerEntity("vicious_goldfish", EntityType.Builder.<Piranha>of((type, level) -> new Piranha(type, level, Piranha.AnimationProfile.GOLDFISH), MobCategory.MONSTER).sized(0.7F, 0.45F).clientTrackingRange(10)));

    // 满月：狼人
    public static final RegistryObject<EntityType<Werewolf>> WEREWOLF = DevelopmentSpawnPolicy.developmentOnly(registerEntity("werewolf", EntityType.Builder.of(Werewolf::new, MobCategory.MONSTER).sized(0.9F, 2.2F).clientTrackingRange(10)));

    // 雨天：飞鱼与愤怒雨云怪
    public static final RegistryObject<EntityType<FlyingFishMonster>> FLYING_FISH = registerFlyingFish(
            "flying_fish", 0.9F, 0.9F,
            new FlyingFishMonster.PursuitProfile(0.95, 0.5, 0.02, 5));
    public static final RegistryObject<EntityType<AngryNimbus>> ANGRY_NIMBUS = DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_nimbus", EntityType.Builder.of(AngryNimbus::new, MobCategory.MONSTER).sized(2.0F, 1.4F).clientTrackingRange(10)));

    // 暴风雪：冰雪巨人
    public static final RegistryObject<EntityType<FrostFighter>> ICE_GOLEM = DevelopmentSpawnPolicy.developmentOnly(registerEntity("ice_golem", EntityType.Builder.<FrostFighter>of((type, level) -> new FrostFighter(type, level, FrostFighter.Kind.GOLEM), MobCategory.MONSTER).sized(1.5F, 4.0F).clientTrackingRange(12)));

    // 大风天：大风气球怪与愤怒蒲公英
    public static final RegistryObject<EntityType<WindyBalloon>> WINDY_BALLOON = DevelopmentSpawnPolicy.developmentOnly(registerEntity("windy_balloon", EntityType.Builder.<WindyBalloon>of(WindyBalloon::new, MobCategory.MONSTER).sized(0.8F, 1.0F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<AngryDandelion>> ANGRY_DANDELION = DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_dandelion", EntityType.Builder.of(AngryDandelion::new, MobCategory.MONSTER).sized(1.0F, 1.2F).clientTrackingRange(10)));

    // 沙尘暴：愤怒翻滚怪
    public static final RegistryObject<EntityType<AngryTumbler>> ANGRY_TUMBLER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("angry_tumbler", EntityType.Builder.<AngryTumbler>of(AngryTumbler::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10)));

    // 沙尘暴：普通、腐化、猩红与神圣沙鲨
    public static final RegistryObject<EntityType<SandShark>> SAND_SHARK = DevelopmentSpawnPolicy.developmentOnly(registerEntity("sand_shark", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(2.0F, 1.25F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<SandShark>> BONE_BITER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("bone_biter", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(2.0F, 1.25F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<SandShark>> FLESH_REAVER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("flesh_reaver", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(2.0F, 1.25F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<SandShark>> CRYSTAL_THRESHER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("crystal_thresher", EntityType.Builder.<SandShark>of(SandShark::new, MobCategory.MONSTER).sized(2.0F, 1.25F).clientTrackingRange(10)));

    // 哥布林入侵前哨：侦察兵
    public static final RegistryObject<EntityType<GoblinMonster>> GOBLIN_SCOUT = registerGoblinLand("goblin_scout", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN);

    // 哥布林入侵：近战与弓箭手
    public static final RegistryObject<EntityType<GoblinMonster>> GOBLIN_PEON = registerGoblinLand("goblin_peon", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.BREAK);
    public static final RegistryObject<EntityType<GoblinMonster>> GOBLIN_WARRIOR = registerGoblinLand("goblin_warrior", 0.65F, 1.85F, Items.STONE_SWORD.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN);
    public static final RegistryObject<EntityType<GoblinMonster>> GOBLIN_THIEF = registerGoblinLand("goblin_thief", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.BREAK);
    public static final RegistryObject<EntityType<GoblinArcher>> GOBLIN_ARCHER = registerEntity("goblin_archer", EntityType.Builder.of(GoblinArcher::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AngerGoblin>> ANGER_GOBLIN = registerEntity("anger_goblin", EntityType.Builder.of(AngerGoblin::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));

    // 哥布林入侵：法师及召唤体
    public static final RegistryObject<EntityType<DarkCaster>> GOBLIN_SORCERER = registerCaster("goblin_sorcerer", 0.65F, 1.85F, DarkCaster.Profile.GOBLIN_SORCERER);
    public static final RegistryObject<EntityType<GoblinWarlock>> GOBLIN_WARLOCK = DevelopmentSpawnPolicy.developmentOnly(registerEntity("goblin_warlock", EntityType.Builder.of(GoblinWarlock::new, MobCategory.MONSTER).sized(0.8F, 1.9F).clientTrackingRange(10)));
    public static final RegistryObject<EntityType<ShadowflameApparition>> SHADOWFLAME_APPARITION = DevelopmentSpawnPolicy.developmentOnly(registerEntity("shadowflame_apparition", EntityType.Builder.of(ShadowflameApparition::new, MobCategory.MONSTER).sized(0.8F, 1.0F).clientTrackingRange(10)));

    // 海盗入侵：水手与私船海盗
    public static final RegistryObject<EntityType<BaseWarriorMonster>> PIRATE_DECKHAND = DevelopmentSpawnPolicy.developmentOnly(registerLand("pirate_deckhand", 0.6F, 1.85F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.2, true));
    public static final RegistryObject<EntityType<BaseWarriorMonster>> PIRATE_CORSAIR = DevelopmentSpawnPolicy.developmentOnly(registerLand("pirate_corsair", 0.6F, 1.85F, BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.NONE, 1.3, true));

    // 海盗入侵：神射手、弩手与船长
    public static final RegistryObject<EntityType<PirateRangedMonster>> PIRATE_DEADEYE = DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_deadeye", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.DEADEYE), MobCategory.MONSTER).sized(0.6F, 1.85F)));
    public static final RegistryObject<EntityType<PirateRangedMonster>> PIRATE_CROSSBOWER = DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_crossbower", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.CROSSBOWER), MobCategory.MONSTER).sized(0.6F, 1.85F)));
    public static final RegistryObject<EntityType<PirateRangedMonster>> PIRATE_CAPTAIN = DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_captain", EntityType.Builder.<PirateRangedMonster>of((type, level) -> new PirateRangedMonster(type, level, PirateRangedMonster.Profile.CAPTAIN), MobCategory.MONSTER).sized(0.8F, 2.0F)));

    // 海盗入侵：鹦鹉与海盗诅咒
    public static final RegistryObject<EntityType<PirateFlyingMonster>> PIRATE_PARROT = DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirate_parrot", EntityType.Builder.<PirateFlyingMonster>of((type, level) -> new PirateFlyingMonster(type, level, false), MobCategory.MONSTER).sized(0.5F, 0.7F)));
    public static final RegistryObject<EntityType<PirateFlyingMonster>> PIRATES_CURSE = DevelopmentSpawnPolicy.developmentOnly(registerEntity("pirates_curse", EntityType.Builder.<PirateFlyingMonster>of((type, level) -> new PirateFlyingMonster(type, level, true), MobCategory.MONSTER).sized(0.7F, 1.2F)));

    // 新年：大飞龙
    public static final RegistryObject<EntityType<SimpleWormMonster>> ARCH_WYVERN = registerWorm("arch_wyvern", 12, 1.8F, 1.8F, SimpleWormMonster.Role.FLYING);

    // Boss 附属生物：克苏鲁之脑
    public static final RegistryObject<EntityType<VisualNeuron>> VISUAL_NEURON = registerEntity("visual_neuron", EntityType.Builder.of(VisualNeuron::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));

    // Boss 附属生物：血肉墙与血肉山
    public static final RegistryObject<EntityType<TheHungry>> THE_HUNGRY = registerEntity("the_hungry", EntityType.Builder.of(TheHungry::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SimpleWormMonster>> LEECH = registerWorm("leech", 12, 2F, 2F);
    public static final RegistryObject<EntityType<HillHungry>> HILL_HUNGRY = registerEntity("hill_hungry", EntityType.Builder.of(HillHungry::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FleshSlime>> FLESH_SLIME = registerEntity("flesh_slime", EntityType.Builder.of(FleshSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune());

    // Boss 附属生物：尖刺史莱姆
    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_SLIME = registerEntity("spiked_slime", EntityType.Builder.of(SpikedSlime::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));

    // 城镇史莱姆救援：摇晃宝箱与气球史莱姆
    public static final RegistryObject<EntityType<TownSlimeRescue>> OLD_SHAKING_CHEST = DevelopmentSpawnPolicy.developmentOnly(registerEntity("old_shaking_chest", EntityType.Builder.<TownSlimeRescue>of((type, level) -> new TownSlimeRescue(type, level, false), MobCategory.CREATURE).sized(0.8F, 0.8F)));
    public static final RegistryObject<EntityType<TownSlimeRescue>> CLUMSY_BALLOON_SLIME = DevelopmentSpawnPolicy.developmentOnly(registerEntity("clumsy_balloon_slime", EntityType.Builder.<TownSlimeRescue>of((type, level) -> new TownSlimeRescue(type, level, true), MobCategory.CREATURE).sized(0.8F, 0.8F)));

    // 特殊史莱姆：青团与甜蜜变种
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_DUMPLING_SLIME = registerSlime("green_dumpling_slime", false, 2);
    public static final RegistryObject<EntityType<SweetSlime>> SWEET_SLIME = registerEntity("sweet_slime", EntityType.Builder.of(SweetSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    // 多部位实体：共用蠕虫体节
    public static final RegistryObject<EntityType<BaseWormPart>> WORM_SEGMENT = registerEntity("worm_segment", EntityType.Builder.of(BaseWormPart::new, MobCategory.MISC).sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave());

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return PortDeferredRegisterExtension.register(ENTITIES, name, id -> builder.build(id.toString()));
    }

    private static RegistryObject<EntityType<SimpleWormMonster>> registerWorm(String name, int segments, float w, float h) {
        return registerWorm(name, segments, w, h, SimpleWormMonster.Role.UNDERGROUND);
    }

    private static RegistryObject<EntityType<SimpleWormMonster>> registerWorm(String name, int segments, float width, float height, SimpleWormMonster.Role role) {
        return registerEntity(name, EntityType.Builder.<SimpleWormMonster>of((type, level) -> new SimpleWormMonster(type, level, segments, role), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10).updateInterval(1));
    }

    private static RegistryObject<EntityType<DarkCaster>> registerCaster(String name, float width, float height, DarkCaster.Profile profile) {
        return registerEntity(name, EntityType.Builder.<DarkCaster>of((type, level) -> new DarkCaster(type, level, profile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<JellyFish>> registerJellyFish(String name, JellyFish.Profile profile) {
        return registerEntity(name, EntityType.Builder.<JellyFish>of((type, level) -> new JellyFish(type, level, profile), MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<Snatcher>> registerSnatcher(String name, Snatcher.Profile profile) {
        return registerEntity(name, EntityType.Builder.<Snatcher>of((type, level) -> new Snatcher(type, level, profile), MobCategory.MONSTER).sized(1.0F, 1.0F).fireImmune().clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<MeleeSkeleton>> registerSkeleton(String name, float w, float h) {
        return registerSkeleton(name, w, h, MeleeSkeleton.BehaviorProfile.BLOOD_MOON_DOORS);
    }

    private static RegistryObject<EntityType<MeleeSkeleton>> registerSkeleton(String name, float width, float height, MeleeSkeleton.BehaviorProfile behaviorProfile) {
        return registerEntity(name, EntityType.Builder.<MeleeSkeleton>of((type, level) -> new MeleeSkeleton(type, level, false, behaviorProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<FlyingFishMonster>> registerFlyingFish(String name, float width, float height, FlyingFishMonster.PursuitProfile pursuitProfile) {
        return registerEntity(name, EntityType.Builder.<FlyingFishMonster>of(
                (type, level) -> new FlyingFishMonster(type, level, pursuitProfile, 0.2),
                MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<ChargingMonster>> registerCharger(String name, float width, float height, double chargeSpeed, int windupTicks) {
        return registerEntity(name, EntityType.Builder.<ChargingMonster>of((type, level) -> new ChargingMonster(type, level, chargeSpeed, windupTicks), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<BaseWarriorMonster>> registerLand(String name, float w, float h) {
        return registerLand(name, w, h, BaseWarriorMonster.LandSoundProfile.ROUTINE);
    }

    private static RegistryObject<EntityType<BaseWarriorMonster>> registerLand(String name, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerLand(name, width, height, soundProfile, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 1.0, false);
    }

    private static RegistryObject<EntityType<BaseWarriorMonster>> registerLand(String name, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile, double meleeSpeed, boolean ignoreLightPathCost) {
        return registerLand(name, width, height, soundProfile, animationProfile, meleeSpeed, ignoreLightPathCost, BaseWarriorMonster.DoorBehavior.NONE);
    }

    private static RegistryObject<EntityType<BaseWarriorMonster>> registerLand(String name, float width, float height, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.DoorBehavior doorBehavior) {
        return registerEntity(name, EntityType.Builder.<BaseWarriorMonster>of((type, level) -> new BaseWarriorMonster(type, level, 0.0, animationProfile, soundProfile, meleeSpeed, ignoreLightPathCost, doorBehavior), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String name, float width, float height, net.minecraft.world.item.ItemStack defaultMainHand) {
        return registerHumanoidLand(name, width, height, defaultMainHand, BaseWarriorMonster.LandSoundProfile.ROUTINE);
    }

    private static RegistryObject<EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String name, float width, float height, net.minecraft.world.item.ItemStack defaultMainHand, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerHumanoidLand(name, width, height, defaultMainHand, soundProfile, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE);
    }

    private static RegistryObject<EntityType<HumanoidWarriorMonster>> registerHumanoidLand(String name, float width, float height, net.minecraft.world.item.ItemStack defaultMainHand, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        return registerEntity(name, EntityType.Builder.<HumanoidWarriorMonster>of((type, level) -> new HumanoidWarriorMonster(type, level, defaultMainHand, soundProfile, animationProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    /// 注册需要哥布林专用浮水行为的人形敌怪。
    ///
    /// 装备和动画仍由实体注册项直接声明；这里只把哥布林独有的水中行为与
    /// 普通人形怪分开，避免通过类型判断或注册表名称推测运行逻辑。
    private static RegistryObject<EntityType<GoblinMonster>> registerGoblinLand(String name, float width, float height, net.minecraft.world.item.ItemStack defaultMainHand, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        return registerGoblinLand(name, width, height, defaultMainHand, animationProfile, GoblinMonster.DoorBehavior.NONE);
    }

    private static RegistryObject<EntityType<GoblinMonster>> registerGoblinLand(String name, float width, float height, net.minecraft.world.item.ItemStack defaultMainHand,
                                                                                BaseWarriorMonster.LandAnimationProfile animationProfile, GoblinMonster.DoorBehavior doorBehavior) {
        return registerEntity(name, EntityType.Builder.<GoblinMonster>of(
                (type, level) -> new GoblinMonster(type, level, defaultMainHand, animationProfile, doorBehavior),
                MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    /// 注册发现目标后加速的普通陆行怪，实体仍复用通用近战行为。
    private static RegistryObject<EntityType<BaseWarriorMonster>> registerAcceleratingLand(String name, float width, float height, double pursuitSpeedBonus, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerAcceleratingLand(name, width, height, pursuitSpeedBonus, meleeSpeed, ignoreLightPathCost, animationProfile, soundProfile, BaseWarriorMonster.DoorBehavior.NONE);
    }

    private static RegistryObject<EntityType<BaseWarriorMonster>> registerAcceleratingLand(String name, float width, float height, double pursuitSpeedBonus, double meleeSpeed, boolean ignoreLightPathCost, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, BaseWarriorMonster.DoorBehavior doorBehavior) {
        return registerEntity(name, EntityType.Builder.<BaseWarriorMonster>of((type, level) -> new BaseWarriorMonster(type, level, pursuitSpeedBonus, animationProfile, soundProfile, meleeSpeed, ignoreLightPathCost, doorBehavior), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile) {
        return registerJumpingLand(name, width, height, profile, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY);
    }

    private static RegistryObject<EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile) {
        return registerEntity(
                name,
                EntityType.Builder.<JumpingWarriorMonster>of(
                                (type, level) -> new JumpingWarriorMonster(
                                        type, level, profile, animationProfile),
                        MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile) {
        return registerJumpingLand(name, width, height, profile, animationProfile, soundProfile, 1.0);
    }

    private static RegistryObject<EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, double meleeSpeed) {
        return registerEntity(name, EntityType.Builder.<JumpingWarriorMonster>of((type, level) -> new JumpingWarriorMonster(type, level, profile, animationProfile, soundProfile, meleeSpeed), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<JumpingWarriorMonster>> registerJumpingLand(String name, float width, float height, BaseWarriorMonster.JumpProfile profile, BaseWarriorMonster.LandAnimationProfile animationProfile, BaseWarriorMonster.LandSoundProfile soundProfile, double meleeSpeed, JumpingWarriorMonster.ContactProfile contactProfile) {
        return registerEntity(name, EntityType.Builder.<JumpingWarriorMonster>of((type, level) -> new JumpingWarriorMonster(type, level, profile, animationProfile, soundProfile, meleeSpeed, contactProfile), MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
    }

    private static RegistryObject<EntityType<BaseSlime>> registerSlime(String name, boolean passiveByDay, int size) {
        return registerSlime(name, passiveByDay, size, false);
    }

    private static RegistryObject<EntityType<BaseSlime>> registerSlime(String name, boolean passiveByDay, int size, boolean honeyConvertible) {
        return registerEntity(name, EntityType.Builder.<BaseSlime>of((entityType, level) -> new BaseSlime(entityType, level, passiveByDay, size, honeyConvertible), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    }

}
