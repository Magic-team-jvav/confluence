package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.*;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.monster.slime.*;

public class MonsterEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    // 史莱姆 —— passiveByDay: 地表白天不主动攻击，除非被激怒或处于地下
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_SLIME = registerSlime("green_slime", 0x48E920, true, -20);
    public static final RegistryObject<EntityType<BaseSlime>> BLUE_SLIME = registerSlime("blue_slime", 0x73bcf4, true, 0);
    public static final RegistryObject<EntityType<BaseSlime>> JUNGLE_SLIME = registerSlime("jungle_slime", 0x9ae920, true, 30);
    public static final RegistryObject<EntityType<BaseSlime>> PURPLE_SLIME = registerSlime("purple_slime", 0xf334f8, true, 10);
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_DUMPLING_SLIME = registerSlime("green_dumpling_slime", 0x32CD32, true, -10);
    public static final RegistryObject<EntityType<BaseSlime>> SWAMP_SLIME = registerSlime("swamp_slime", 0x556B2F, true, 10);
    public static final RegistryObject<EntityType<BaseSlime>> DESERT_SLIME = registerSlime("desert_slime", 0xDCC59a, false, 20);
    public static final RegistryObject<EntityType<BaseSlime>> EVIL_SLIME = registerSlime("evil_slime", 0xFF00FF, false, 40);
    public static final RegistryObject<EntityType<BaseSlime>> RED_SLIME = registerSlime("red_slime", 0xf83434, false, 10);
    public static final RegistryObject<EntityType<BaseSlime>> YELLOW_SLIME = registerSlime("yellow_slime", 0xf8e234, false, 10);
    public static final RegistryObject<EntityType<BaseSlime>> DUNGEON_SLIME = registerSlime("dungeon_slime", 0x6d697b, false, 50);
    // 有自定义行为的子类
    public static final RegistryObject<EntityType<Pinky>> PINK_SLIME = registerEntity("pink_slime", EntityType.Builder.of(Pinky::new, MobCategory.MONSTER).sized(0.3F, 0.3F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<IceSlime>> ICE_SLIME = registerEntity("ice_slime", EntityType.Builder.of(IceSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<LavaSlime>> LAVA_SLIME = registerEntity("lava_slime", EntityType.Builder.of(LavaSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<TropicSlime>> TROPIC_SLIME = registerEntity("tropic_slime", EntityType.Builder.of(TropicSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CorruptSlime>> CORRUPT_SLIME = registerEntity("corrupt_slime", EntityType.Builder.of(CorruptSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Slimeling>> SLIMELING = registerEntity("slimeling", EntityType.Builder.of(Slimeling::new, MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Crimslime>> CRIMSLIME = registerEntity("crimslime", EntityType.Builder.of(Crimslime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<LuminousSlime>> LUMINOUS_SLIME = registerEntity("luminous_slime", EntityType.Builder.of(LuminousSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BlackSlime>> BLACK_SLIME = registerEntity("black_slime", EntityType.Builder.of(BlackSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<HoneySlime>> HONEY_SLIME = registerEntity("honey_slime", EntityType.Builder.of(HoneySlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<GoldenSlime>> GOLDEN_SLIME = registerEntity("golden_slime", EntityType.Builder.of(GoldenSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<FleshSlime>> FLESH_SLIME = registerEntity("flesh_slime", EntityType.Builder.of(FleshSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_SLIME = registerEntity("spiked_slime", EntityType.Builder.of(SpikedSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SpikedJungleSlime>> SPIKED_JUNGLE_SLIME = registerEntity("spiked_jungle_slime", EntityType.Builder.of(SpikedJungleSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SpikedIceSlime>> SPIKED_ICE_SLIME = registerEntity("spiked_ice_slime", EntityType.Builder.of(SpikedIceSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));

    // 飞行怪
    public static final RegistryObject<EntityType<DemonEye>> DEMON_EYE = registerEntity("demon_eye", EntityType.Builder.<DemonEye>of(DemonEye::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Harpy>> HARPY = registerEntity("harpy", EntityType.Builder.of(Harpy::new, MobCategory.MONSTER).sized(1f, 2f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Pixie>> PIXIE = registerEntity("pixie", EntityType.Builder.of(Pixie::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EaterOfSouls>> EATER_OF_SOULS = registerEntity("eater_of_souls", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EaterOfSouls>> CRIMERA = registerEntity("crimera", EntityType.Builder.of(EaterOfSouls::new, MobCategory.MONSTER).sized(1.2F, 1.2F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CursedSkull>> CURSED_SKULL = registerEntity("cursed_skull", EntityType.Builder.of(CursedSkull::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));

    // 陆行怪
    public static final RegistryObject<EntityType<Zombie>> ZOMBIE = registerEntity("zombie", EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<MeleeSkeleton>> SPORE_SKELETON = registerEntity("spore_skeleton", EntityType.Builder.of(MeleeSkeleton::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    // TODO: port prefab-based monsters (SporeZombie, Decayeder, BloodCrawler, etc.)
    public static final RegistryObject<EntityType<BloodySpore>> BLOODY_SPORE = registerEntity("bloody_spore", EntityType.Builder.of(BloodySpore::new, MobCategory.MONSTER).sized(1, 1.5f).clientTrackingRange(10));
    public static final RegistryObject<EntityType<BloodCrawler>> BLOOD_CRAWLER = registerEntity("blood_crawler", EntityType.Builder.of(BloodCrawler::new, MobCategory.MONSTER).sized(1.8F, 1.2F).clientTrackingRange(10));

    // 水怪
    public static final RegistryObject<EntityType<Piranha>> PIRANHA = registerEntity("piranha", EntityType.Builder.of(Piranha::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> BLUE_JELLYFISH = registerEntity("blue_jellyfish", EntityType.Builder.of(JellyFish::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> PINK_JELLYFISH = registerEntity("pink_jellyfish", EntityType.Builder.of(JellyFish::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Shark>> SHARK = registerEntity("shark", EntityType.Builder.of(Shark::new, MobCategory.MONSTER).sized(2.5F, 1F).clientTrackingRange(10));

    // 蝙蝠 —— 共用 CaveBat BT，属性区分
    public static final RegistryObject<EntityType<CaveBat>> CAVE_BAT = registerEntity("cave_bat", EntityType.Builder.of(CaveBat::new, MobCategory.MONSTER).sized(1.0F, 0.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> JUNGLE_BAT = registerEntity("jungle_bat", EntityType.Builder.of(CaveBat::new, MobCategory.MONSTER).sized(1.0F, 0.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> ICE_BAT = registerEntity("ice_bat", EntityType.Builder.of(CaveBat::new, MobCategory.MONSTER).sized(1.0F, 0.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> GIANT_BAT = registerEntity("giant_bat", EntityType.Builder.of(CaveBat::new, MobCategory.MONSTER).sized(1.4F, 1.1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<CaveBat>> HELL_BAT = registerEntity("hell_bat", EntityType.Builder.of(CaveBat::new, MobCategory.MONSTER).sized(1.0F, 0.8F).clientTrackingRange(10).fireImmune());

    // 蠕虫
    public static final RegistryObject<EntityType<Wyvern>> WYVERN = registerEntity("wyvern", EntityType.Builder.of(Wyvern::new, MobCategory.MONSTER).sized(1.5F, 1.5F).clientTrackingRange(10));

    // 法师
    public static final RegistryObject<EntityType<DarkCaster>> DARK_CASTER = registerEntity("dark_caster", EntityType.Builder.of(DarkCaster::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<DarkCaster>> GOBLIN_SORCERER = registerEntity("goblin_sorcerer", EntityType.Builder.of(DarkCaster::new, MobCategory.MONSTER).sized(0.65F, 1.85F).clientTrackingRange(10));

    // 卷壳怪
    public static final RegistryObject<EntityType<GiantShelly>> GIANT_SHELLY = registerEntity("giant_shelly", EntityType.Builder.of(GiantShelly::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Crawdad>> CRAWDAD = registerEntity("crawdad", EntityType.Builder.of(Crawdad::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // 宁芙
    public static final RegistryObject<EntityType<Nymph>> NYMPH = registerEntity("nymph", EntityType.Builder.of(Nymph::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(10));

    // 抓人草
    public static final RegistryObject<EntityType<Snatcher>> SNATCHER = registerEntity("snatcher", EntityType.Builder.of(Snatcher::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<Snatcher>> MAN_EATER = registerEntity("man_eater", EntityType.Builder.of(Snatcher::new, MobCategory.MONSTER).sized(1F, 1F).clientTrackingRange(10));

    // Wraith + Mimics (non-prefab, self-contained)
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
    public static final RegistryObject<EntityType<Piranha>> ARAPAIMA = registerEntity("arapaima", EntityType.Builder.of(Piranha::new, MobCategory.MONSTER).sized(2.2F, 0.7F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<JellyFish>> GREEN_JELLYFISH = registerEntity("green_jellyfish", EntityType.Builder.of(JellyFish::new, MobCategory.MONSTER).sized(0.5F, 0.5F).clientTrackingRange(10));

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return PortDeferredRegisterExtension.register(ENTITIES, name, id -> builder.build(id.toString()));
    }

    private static RegistryObject<EntityType<BaseSlime>> registerSlime(
            String name, int color, boolean passiveByDay, float terrariaKbResist) {
        return registerEntity(name, EntityType.Builder.<BaseSlime>of(
                (entityType, level) -> new BaseSlime(entityType, level, color, passiveByDay, terrariaKbResist),
                MobCategory.MONSTER
        ).sized(0.6F, 0.6F).clientTrackingRange(10));
    }

}
