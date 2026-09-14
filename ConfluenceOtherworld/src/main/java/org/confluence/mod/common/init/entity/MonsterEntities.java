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

public class MonsterEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    // 史莱姆 —— passiveByDay: 地表白天不主动攻击，除非被激怒或处于地下
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_SLIME = registerSlime("green_slime", true, 2, true);
    public static final RegistryObject<EntityType<BaseSlime>> BLUE_SLIME = registerSlime("blue_slime", true, 2, true);
    public static final RegistryObject<EntityType<BaseSlime>> JUNGLE_SLIME = registerSlime("jungle_slime", true, 2);
    public static final RegistryObject<EntityType<BaseSlime>> PURPLE_SLIME = registerSlime("purple_slime", true, 2, true);
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_DUMPLING_SLIME = registerSlime("green_dumpling_slime", false, 2);
    public static final RegistryObject<EntityType<BaseSlime>> SWAMP_SLIME = registerSlime("swamp_slime", false, 2);
    public static final RegistryObject<EntityType<BaseSlime>> DESERT_SLIME = registerSlime("desert_slime", false, 2);
    public static final RegistryObject<EntityType<BaseSlime>> RED_SLIME = registerSlime("red_slime", false, 2);
    public static final RegistryObject<EntityType<BaseSlime>> YELLOW_SLIME = registerSlime("yellow_slime", false, 2);
    public static final RegistryObject<EntityType<BaseSlime>> DUNGEON_SLIME = registerSlime("dungeon_slime", false, 3);
    // 有自定义行为的子类
    public static final RegistryObject<EntityType<Pinky>> PINK_SLIME = registerEntity("pink_slime", EntityType.Builder.of(Pinky::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<IceSlime>> ICE_SLIME = registerEntity("ice_slime", EntityType.Builder.of(IceSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<LavaSlime>> LAVA_SLIME = registerEntity("lava_slime", EntityType.Builder.of(LavaSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<TropicSlime>> TROPIC_SLIME = registerEntity("tropic_slime", EntityType.Builder.of(TropicSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CorruptSlime>> CORRUPT_SLIME = registerEntity("corrupt_slime", EntityType.Builder.of(CorruptSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Slimeling>> SLIMELING = registerEntity("slimeling", EntityType.Builder.of(Slimeling::new, MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WinglessSlimer>> WINGLESS_SLIMER = registerEntity("wingless_slimer", EntityType.Builder.of(WinglessSlimer::new, MobCategory.MONSTER).sized(0.8F, 0.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Crimslime>> CRIMSLIME = registerEntity("crimslime", EntityType.Builder.of(Crimslime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<LuminousSlime>> LUMINOUS_SLIME = registerEntity("luminous_slime", EntityType.Builder.of(LuminousSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BlackSlime>> BLACK_SLIME = registerEntity("black_slime", EntityType.Builder.of(BlackSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MotherSlime>> MOTHER_SLIME = registerEntity("mother_slime", EntityType.Builder.of(MotherSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BabySlime>> BABY_SLIME = registerEntity("baby_slime", EntityType.Builder.of(BabySlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SweetSlime>> SWEET_SLIME = registerEntity("sweet_slime", EntityType.Builder.of(SweetSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<GoldenSlime>> GOLDEN_SLIME = registerEntity("golden_slime", EntityType.Builder.of(GoldenSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FleshSlime>> FLESH_SLIME = registerEntity("flesh_slime", EntityType.Builder.of(FleshSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_SLIME = registerEntity("spiked_slime", EntityType.Builder.of(SpikedSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SpikedJungleSlime>> SPIKED_JUNGLE_SLIME = registerEntity("spiked_jungle_slime", EntityType.Builder.of(SpikedJungleSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SpikedIceSlime>> SPIKED_ICE_SLIME = registerEntity("spiked_ice_slime", EntityType.Builder.of(SpikedIceSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    // 飞行怪
    public static final RegistryObject<EntityType<DemonEye>> DEMON_EYE = registerEntity("demon_eye", EntityType.Builder.<DemonEye>of(DemonEye::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Harpy>> HARPY = registerEntity("harpy", EntityType.Builder.of(Harpy::new, MobCategory.MONSTER).sized(1f, 2f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Pixie>> PIXIE = registerEntity("pixie", EntityType.Builder.of(Pixie::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EaterOfSouls>> EATER_OF_SOULS = registerEntity("eater_of_souls", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EaterOfSouls>> CRIMERA = registerEntity("crimera", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CursedSkull>> CURSED_SKULL = registerEntity("cursed_skull", EntityType.Builder.of(CursedSkull::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Corruptor>> CORRUPTOR = registerEntity("corruptor", EntityType.Builder.of(Corruptor::new, MobCategory.MONSTER).sized(2.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Slimer>> SLIMER = registerEntity("slimer", EntityType.Builder.of(Slimer::new, MobCategory.MONSTER).sized(1.0F, 0.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EnchantedSword>> ENCHANTED_SWORD = registerEntity("enchanted_sword_monster", EntityType.Builder.of(EnchantedSword::new, MobCategory.MONSTER).sized(0.35F, 1.4F).clientTrackingRange(10));

    // 陆行怪
    public static final RegistryObject<EntityType<Zombie>> ZOMBIE = registerEntity("zombie", EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> SPORE_SKELETON = registerEntity("spore_skeleton", EntityType.Builder.<MeleeSkeleton>of((type, level) -> new MeleeSkeleton(type, level, true, MeleeSkeleton.BehaviorProfile.OPEN_DOORS), MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    // 地牢骷髅
    public static final RegistryObject<EntityType<MeleeSkeleton>> BASE_BONES = registerSkeleton("base_bones", 0.65F, 1.85F);
    public static final RegistryObject<EntityType<MeleeSkeleton>> ANGER_BONES = registerSkeleton("anger_bones", 0.65F, 1.85F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> SHORT_BONES = registerSkeleton("short_bones", 0.55F, 1.65F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_BONES = registerSkeleton("big_bones", 0.85F, 2.25F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_ANGER_BONES = registerSkeleton("big_anger_bones", 0.9F, 2.4F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_MUSCLE_ANGER_BONES = registerSkeleton("big_muscle_anger_bones", 0.95F, 2.45F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> BIG_HELMET_ANGER_BONES = registerSkeleton("big_helmet_anger_bones", 1F, 2.6F, MeleeSkeleton.BehaviorProfile.ANGRY_BONES);
    public static final RegistryObject<EntityType<MeleeSkeleton>> UNDEAD_VIKING = registerSkeleton("undead_viking", 1F, 2.6F);
    public static final RegistryObject<EntityType<GiantTortoise>> GIANT_TORTOISE = registerEntity("giant_tortoise", EntityType.Builder.of(GiantTortoise::new, MobCategory.MONSTER).sized(3F, 2.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Unicorn>> UNICORN = registerEntity("unicorn", EntityType.Builder.of(Unicorn::new, MobCategory.MONSTER).sized(1.5F, 1.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Gastropod>> GASTROPOD = registerEntity("gastropod", EntityType.Builder.of(Gastropod::new, MobCategory.MONSTER).sized(1.2F, 1.0F).clientTrackingRange(10));

    // 发光蘑菇生物群系的蝙蝠类敌怪。
    public static final RegistryObject<EntityType<CaveBat>> SPORE_BAT = registerEntity("spore_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));

    // 简单飞行怪
    public static final RegistryObject<EntityType<Drippler>> DRIPPLER = registerEntity("drippler", EntityType.Builder.of(Drippler::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FlyingFishMonster>> FLYING_FISH = registerFlyingFish(
            "flying_fish", 0.9F, 0.9F,
            new FlyingFishMonster.PursuitProfile(0.95, 0.5, 0.02, 5));
    public static final RegistryObject<EntityType<FlyingFishMonster>> WANDERING_EYE_FISH = registerFlyingFish(
            "wandering_eye_fish", 1.2F, 1.2F,
            new FlyingFishMonster.PursuitProfile(0.98, 2.2, 0.01, 10));
    public static final RegistryObject<EntityType<VisualNeuron>> VISUAL_NEURON = registerEntity("visual_neuron", EntityType.Builder.of(VisualNeuron::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> GIANT_FLYING_FOX = registerEntity("giant_flying_fox", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.GIANT), MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));

    // 恶魔
    public static final RegistryObject<EntityType<Demon>> DEMON = registerEntity("demon", EntityType.Builder.of(Demon::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Demon>> VOODOO_DEMON = registerEntity("voodoo_demon", EntityType.Builder.of(Demon::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(10).fireImmune());

    // 黄蜂
    public static final RegistryObject<EntityType<Hornet>> HORNET = registerEntity("hornet", EntityType.Builder.of(Hornet::new, MobCategory.MONSTER).sized(0.8F, 1.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<LittleHornet>> LITTLE_HORNET = registerEntity("little_hornet", EntityType.Builder.of(LittleHornet::new, MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10));

    // 火小鬼
    public static final RegistryObject<EntityType<FireImp>> FIRE_IMP = registerEntity("fire_imp", EntityType.Builder.of(FireImp::new, MobCategory.MONSTER).sized(0.65F, 1F).clientTrackingRange(10).fireImmune());

    // 衰败者 (远程骷髅)
    public static final RegistryObject<EntityType<Decayeder>> DECAYEDER = registerEntity("decayeder", EntityType.Builder.of(Decayeder::new, MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10));

    // 幽灵
    public static final RegistryObject<EntityType<Ghost>> GHOST = registerEntity("ghost", EntityType.Builder.of(Ghost::new, MobCategory.MONSTER).sized(1F, 1.8F).clientTrackingRange(10));

    // 蹦跳兽
    public static final RegistryObject<EntityType<Derpling>> DERPLING = registerEntity("derpling", EntityType.Builder.of(Derpling::new, MobCategory.MONSTER).sized(2F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Derpling>> HERPLING = registerEntity("herpling", EntityType.Builder.of(Derpling::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 流星头
    public static final RegistryObject<EntityType<MeteorHead>> METEOR_HEAD = registerEntity("meteor_head", EntityType.Builder.of(MeteorHead::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 花岗岩元素
    public static final RegistryObject<EntityType<GraniteElemental>> GRANITE_ELEMENTAL = registerEntity("granite_elemental", EntityType.Builder.of(GraniteElemental::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10));

    // 蚁狮蜂
    public static final RegistryObject<EntityType<AntlionSwarmer>> ANTLION_SWARMER = registerEntity("antlion_swarmer", EntityType.Builder.of(AntlionSwarmer::new, MobCategory.MONSTER).sized(3.0F, 1.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<AntlionSwarmer>> GIANT_ANTLION_SWARMER = registerEntity("giant_antlion_swarmer", EntityType.Builder.of(AntlionSwarmer::new, MobCategory.MONSTER).sized(3.5F, 2.0F).clientTrackingRange(10));

    // 饿鬼 (独立版)
    public static final RegistryObject<EntityType<TheHungry>> THE_HUNGRY = registerEntity("the_hungry", EntityType.Builder.of(TheHungry::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HillHungry>> HILL_HUNGRY = registerEntity("hill_hungry", EntityType.Builder.of(HillHungry::new, MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));

    // 陆行怪 —— BaseWarriorMonster 匿名子类
    public static final RegistryObject<EntityType<BaseWarriorMonster>> BLOOD_ZOMBIE = registerAcceleratingLand("blood_zombie", 0.75F, 1.95F, 0.25, 0.8, true,
            BaseWarriorMonster.LandAnimationProfile.WALK_RUN_IDLE_ATTACK, BaseWarriorMonster.LandSoundProfile.ZOMBIE, BaseWarriorMonster.DoorBehavior.OPEN);
    public static final RegistryObject<EntityType<BaseWarriorMonster>> SNOW_FLINX = registerLand("snow_flinx", 1.25F, 1.25F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, 0.8, false);
    public static final RegistryObject<EntityType<BaseWarriorMonster>> FACE_MONSTER = registerLand("face_monster", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.FACE_MONSTER, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.0, true, BaseWarriorMonster.DoorBehavior.OPEN);
    public static final RegistryObject<EntityType<BloodTumor>> BLOOD_TUMORS = registerEntity("blood_tumors", EntityType.Builder.of(BloodTumor::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HumanoidWarriorMonster>> POSSESS_ARMOR = registerHumanoidLand("possess_armor", 1F, 2F, Items.AIR.getDefaultInstance(),
            BaseWarriorMonster.LandSoundProfile.POSSESSED_ARMOR, BaseWarriorMonster.LandAnimationProfile.NONE);
    public static final RegistryObject<EntityType<HumanoidWarriorMonster>> POSSESS_ARMOR_VOID_VESSEL = registerHumanoidLand("possess_armor_void_vessel", 1F, 2F, Items.AIR.getDefaultInstance(),
            BaseWarriorMonster.LandSoundProfile.POSSESSED_ARMOR, BaseWarriorMonster.LandAnimationProfile.NONE);
    // 木乃伊
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> MUMMY = registerJumpingLand("mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.0, JumpingWarriorMonster.ContactProfile.MUMMY);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> DARK_MUMMY = registerJumpingLand("dark_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.5, JumpingWarriorMonster.ContactProfile.DARK_MUMMY);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> BLOOD_MUMMY = registerJumpingLand("blood_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.5, JumpingWarriorMonster.ContactProfile.BLOOD_MUMMY);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> LIGHT_MUMMY = registerJumpingLand("light_mummy", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_IDLE, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.0, JumpingWarriorMonster.ContactProfile.LIGHT_MUMMY);
    // 拉米亚
    public static final RegistryObject<EntityType<BaseWarriorMonster>> DARK_LAMIA = registerLand("dark_lamia", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.3, true);
    public static final RegistryObject<EntityType<BaseWarriorMonster>> LIGHT_LAMIA = registerLand("light_lamia", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.3, true);
    // 食尸鬼
    public static final RegistryObject<EntityType<BaseWarriorMonster>> GHOUL = registerLand("ghoul", 0.75F, 1.95F,
            BaseWarriorMonster.LandSoundProfile.ROUTINE, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, 1.6, true);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> TAINTED_GHOUL = registerJumpingLand("tainted_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, JumpingWarriorMonster.ContactProfile.TAINTED_GHOUL);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> VILE_GHOUL = registerJumpingLand("vile_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, JumpingWarriorMonster.ContactProfile.VILE_GHOUL);
    public static final RegistryObject<EntityType<JumpingWarriorMonster>> DREAMER_GHOUL = registerJumpingLand("dreamer_ghoul", 0.75F, 1.95F,
            null, BaseWarriorMonster.LandAnimationProfile.WALK_ONLY, BaseWarriorMonster.LandSoundProfile.ROUTINE, 1.6, JumpingWarriorMonster.ContactProfile.DREAMER_GHOUL);
    public static final RegistryObject<EntityType<Paladin>> PALADIN = registerEntity("paladin", EntityType.Builder.of(Paladin::new, MobCategory.MONSTER).sized(1.2F, 2.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<ChargingMonster>> BONE_LEE = registerCharger("bone_lee", 0.7F, 1.9F, 0.82, 6);
    // 哥布林
    public static final RegistryObject<EntityType<GoblinArcher>> GOBLIN_ARCHER = registerEntity("goblin_archer", EntityType.Builder.of(GoblinArcher::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<GoblinMonster>> GOBLIN_PEON = registerGoblinLand("goblin_peon", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.BREAK);
    public static final RegistryObject<EntityType<GoblinMonster>> GOBLIN_WARRIOR = registerGoblinLand("goblin_warrior", 0.65F, 1.85F, Items.STONE_SWORD.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN);
    public static final RegistryObject<EntityType<GoblinMonster>> GOBLIN_THIEF = registerGoblinLand("goblin_thief", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN);
    public static final RegistryObject<EntityType<GoblinMonster>> GOBLIN_SCOUT = registerGoblinLand("goblin_scout", 0.65F, 1.85F, Items.AIR.getDefaultInstance(), BaseWarriorMonster.LandAnimationProfile.NONE, GoblinMonster.DoorBehavior.OPEN);
    public static final RegistryObject<EntityType<AngerGoblin>> ANGER_GOBLIN = registerEntity("anger_goblin", EntityType.Builder.of(AngerGoblin::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BloodySpore>> BLOODY_SPORE = registerEntity("bloody_spore", EntityType.Builder.of(BloodySpore::new, MobCategory.MONSTER).sized(1, 1.5f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BloodCrawler>> BLOOD_CRAWLER = registerEntity("blood_crawler", EntityType.Builder.of(BloodCrawler::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SporeZombie>> SPORE_ZOMBIE = registerEntity("spore_zombie", EntityType.Builder.of(SporeZombie::new, MobCategory.MONSTER).sized(0.75F, 1.95F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HatSporeZombie>> HAT_SPORE_ZOMBIE = registerEntity("hat_spore_zombie", EntityType.Builder.of(HatSporeZombie::new, MobCategory.MONSTER).sized(0.75F, 1.95F).clientTrackingRange(10));

    // 水怪
    public static final RegistryObject<EntityType<Piranha>> PIRANHA = registerEntity("piranha", EntityType.Builder.of(Piranha::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Piranha>> BLOOD_FEEDER = registerEntity("blood_feeder", EntityType.Builder.of(Piranha::new, MobCategory.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> BLUE_JELLYFISH = registerJellyFish("blue_jellyfish", JellyFish.Profile.ROUTINE);
    public static final RegistryObject<EntityType<JellyFish>> PINK_JELLYFISH = registerJellyFish("pink_jellyfish", JellyFish.Profile.ROUTINE);
    public static final RegistryObject<EntityType<Shark>> SHARK = registerEntity("shark", EntityType.Builder.of(Shark::new, MobCategory.MONSTER).sized(2.5F, 1F).clientTrackingRange(10));

    // 蝙蝠共用 CaveBat 行为树，由构造参数声明物种特性，属性仍由数据映射区分。
    public static final RegistryObject<EntityType<CaveBat>> CAVE_BAT = registerEntity("cave_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> JUNGLE_BAT = registerEntity("jungle_bat", EntityType.Builder.<CaveBat>of(CaveBat::new, MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> ICE_BAT = registerEntity("ice_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.ICE), MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> GIANT_BAT = registerEntity("giant_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.GIANT), MobCategory.MONSTER).sized(1.4F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> HELL_BAT = registerEntity("hell_bat", EntityType.Builder.<CaveBat>of((type, level) -> new CaveBat(type, level, CaveBat.Variant.HELL), MobCategory.MONSTER).sized(1.6F, 1.6F).clientTrackingRange(10).fireImmune());

    // 蠕虫
    public static final RegistryObject<EntityType<BaseWormPart>> WORM_SEGMENT = registerEntity("worm_segment", EntityType.Builder.of(BaseWormPart::new, MobCategory.MISC).sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave());
    public static final RegistryObject<EntityType<Wyvern>> WYVERN = registerEntity("wyvern", EntityType.Builder.of(Wyvern::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10).updateInterval(1));
    public static final RegistryObject<EntityType<SimpleWormMonster>> ARCH_WYVERN = registerWorm("arch_wyvern", 12, 1.8F, 1.8F, SimpleWormMonster.Role.FLYING);
    public static final RegistryObject<EntityType<SimpleWormMonster>> DEVOURER = registerWorm("devourer", 12, 2F, 2F, SimpleWormMonster.Role.SURFACE);
    public static final RegistryObject<EntityType<SimpleWormMonster>> TOMB_CRAWLER = registerWorm("tomb_crawler", 12, 2F, 2F);
    public static final RegistryObject<EntityType<SimpleWormMonster>> GIANT_WORM = registerWorm("giant_worm", 12, 2F, 2F);
    public static final RegistryObject<EntityType<SimpleWormMonster>> LEECH = registerWorm("leech", 12, 2F, 2F);
    public static final RegistryObject<EntityType<SimpleWormMonster>> BONE_SERPENT = registerWorm("bone_serpent", 18, 2F, 2F, SimpleWormMonster.Role.BONE_SERPENT);
    public static final RegistryObject<EntityType<SimpleWormMonster>> WITHER_BONE_SERPENT = registerWorm("wither_bone_serpent", 18, 2F, 2F, SimpleWormMonster.Role.BONE_SERPENT);

    // 法师
    public static final RegistryObject<EntityType<DarkCaster>> DARK_CASTER = registerCaster("dark_caster", 0.65F, 1.85F, DarkCaster.Profile.DARK_CASTER);
    public static final RegistryObject<EntityType<DarkCaster>> GOBLIN_SORCERER = registerCaster("goblin_sorcerer", 0.65F, 1.85F, DarkCaster.Profile.GOBLIN_SORCERER);
    public static final RegistryObject<EntityType<ChaosElemental>> CHAOS_ELEMENTAL = registerEntity("chaos_elemental", EntityType.Builder.of(ChaosElemental::new, MobCategory.MONSTER).sized(0.7F, 1.9F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DarkCaster>> NECROMANCER = registerCaster("necromancer", 0.7F, 1.9F, DarkCaster.Profile.NECROMANCER);
    public static final RegistryObject<EntityType<DarkCaster>> DIABOLIST = registerCaster("diabolist", 0.7F, 1.9F, DarkCaster.Profile.DIABOLIST);
    public static final RegistryObject<EntityType<DarkCaster>> RAGGED_CASTER = registerCaster("ragged_caster", 0.7F, 1.9F, DarkCaster.Profile.RAGGED_CASTER);

    // 卷壳怪
    public static final RegistryObject<EntityType<GiantShelly>> GIANT_SHELLY = registerEntity("giant_shelly", EntityType.Builder.of(GiantShelly::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Crawdad>> CRAWDAD = registerEntity("crawdad", EntityType.Builder.of(Crawdad::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 宁芙
    public static final RegistryObject<EntityType<Nymph>> NYMPH = registerEntity("nymph", EntityType.Builder.of(Nymph::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(10));

    // 抓人草
    public static final RegistryObject<EntityType<Snatcher>> SNATCHER = registerSnatcher("snatcher", Snatcher.Profile.SNATCHER);
    public static final RegistryObject<EntityType<Snatcher>> MAN_EATER = registerSnatcher("man_eater", Snatcher.Profile.MAN_EATER);

    // 幻灵与宝箱怪使用各自完整的实体实现，不经过通用注册模板。
    public static final RegistryObject<EntityType<Wraith>> WRAITH = registerEntity("wraith", EntityType.Builder.of(Wraith::new, MobCategory.MONSTER).sized(1F, 2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoodenMimic>> WOODEN_MIMIC = registerEntity("wooden_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoodenMimic>> GOLDEN_MIMIC = registerEntity("golden_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoodenMimic>> ICE_MIMIC = registerEntity("ice_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WoodenMimic>> SHADOW_MIMIC = registerEntity("shadow_mimic", EntityType.Builder.of(WoodenMimic::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseMimic>> CRIMSON_MIMIC = registerEntity("crimson_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseMimic>> CORRUPT_MIMIC = registerEntity("corrupt_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseMimic>> HALLOWED_MIMIC = registerEntity("hallowed_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BaseMimic>> JUNGLE_MIMIC = registerEntity("jungle_mimic", EntityType.Builder.of(BaseMimic::new, MobCategory.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SandPoacher>> SAND_POACHER = registerEntity("sand_poacher", EntityType.Builder.of(SandPoacher::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Arapaima>> ARAPAIMA = registerEntity("arapaima", EntityType.Builder.of(Arapaima::new, MobCategory.MONSTER).sized(2.2F, 0.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> GREEN_JELLYFISH = registerJellyFish("green_jellyfish", JellyFish.Profile.GREEN);

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
        return registerEntity(name, EntityType.Builder.<Snatcher>of((type, level) -> new Snatcher(type, level, profile), MobCategory.MONSTER).sized(1.0F, 1.0F).clientTrackingRange(10));
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

    private static RegistryObject<EntityType<PhasingChargeMonster>> registerPhasingCharger(String name, float width, float height, double chargeSpeed, double wanderSpeed) {
        return registerEntity(name, EntityType.Builder.<PhasingChargeMonster>of(
                (type, level) -> new PhasingChargeMonster(type, level, chargeSpeed, wanderSpeed),
                MobCategory.MONSTER).sized(width, height).clientTrackingRange(10));
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
