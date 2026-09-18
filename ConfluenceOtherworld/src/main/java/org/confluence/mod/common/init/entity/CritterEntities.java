package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.animal.*;

public class CritterEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Confluence.MODID);

    // 地表与森林：兔兔
    public static final RegistryObject<EntityType<Bunny>> BUNNY = register("bunny", Bunny::new);

    // 地表与森林：鸟类
    public static final RegistryObject<EntityType<Bird>> BIRD = register("bird", Bird::new);
    public static final RegistryObject<EntityType<BlueJay>> BLUE_JAY = register("blue_jay", BlueJay::new);
    public static final RegistryObject<EntityType<Cardinal>> CARDINAL = register("cardinal", Cardinal::new);

    // 地表与森林：松鼠
    public static final RegistryObject<EntityType<Squirrel>> SQUIRREL = register("squirrel", Squirrel::new);
    public static final RegistryObject<EntityType<RedSquirrel>> RED_SQUIRREL = register("red_squirrel", RedSquirrel::new);

    // 地表与森林：蝴蝶、蚱蜢与臭虫
    public static final RegistryObject<EntityType<Butterfly>> BUTTERFLY = registerInsect("butterfly", Butterfly::new);
    public static final RegistryObject<EntityType<Grasshopper>> GRASSHOPPER = registerInsect("grasshopper", Grasshopper::new);
    public static final RegistryObject<EntityType<Stinkbug>> STINKBUG = DevelopmentSpawnPolicy.developmentOnly(register("stinkbug", Stinkbug::new, 0.6F, 0.3F, 8));

    // 地表夜间：萤火虫与仙灵
    public static final RegistryObject<EntityType<GlowBug>> FIREFLY = DevelopmentSpawnPolicy.developmentOnly(register("firefly", (type, level) -> new GlowBug(type, level, "fly"), 0.4F, 0.3F, 8));
    public static final RegistryObject<EntityType<Fairy>> FAIRY = registerInsect("fairy", Fairy::new);

    // 地表动物：飘飘羊与红棕咯菇
    public static final RegistryObject<EntityType<CloudSheep>> CLOUD_SHEEP = DevelopmentSpawnPolicy.developmentOnly(register("cloud_sheep", CloudSheep::new, 1.2F, 1.2F, 10));
    public static final RegistryObject<EntityType<Cluckshroom>> CLUCKSHROOM = DevelopmentSpawnPolicy.developmentOnly(register("cluckshroom", (type, level) -> new Cluckshroom(type, level, false), 0.6F, 1.2F, 10));

    // 淡水与岸边：鸭、金鱼与蜻蜓
    public static final RegistryObject<EntityType<Duck>> DUCK = PortDeferredRegisterExtension.register(ENTITIES, "duck", id -> EntityType.Builder.of(Duck::new, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).passengerAttachments(new Vec3(0.0, 0.7, -0.1)).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<Goldfish>> GOLDFISH = DevelopmentSpawnPolicy.developmentOnly(register("goldfish", Goldfish::new, 0.7F, 0.45F, 8));
    public static final RegistryObject<EntityType<Dragonfly>> DRAGONFLY = registerInsect("dragonfly", Dragonfly::new);

    // 海洋：螃蟹
    public static final RegistryObject<EntityType<Crab>> CRAB = registerHostileCompact("crab", Crab::new);

    // 冰雪：企鹅
    public static final RegistryObject<EntityType<Penguin>> PENGUIN = DevelopmentSpawnPolicy.developmentOnly(register("penguin", Penguin::new, 0.7F, 1.0F, 8));

    // 沙漠：蝎子
    public static final RegistryObject<EntityType<Scorpion>> SCORPION = registerInsect("scorpion", Scorpion::new);

    // 丛林：蛆虫、鼻涕虫与蚜虫
    public static final RegistryObject<EntityType<SimpleCritter>> GRUBBY = registerInsect("grubby", SimpleCritter::new);
    public static final RegistryObject<EntityType<Sluggy>> SLUGGY = registerInsect("sluggy", Sluggy::new);
    public static final RegistryObject<EntityType<SimpleCritter>> BUGGY = DevelopmentSpawnPolicy.developmentOnly(register("buggy", (type, level) -> new SimpleCritter(type, level, "no_wing"), 0.45F, 0.35F, 8));

    // 丛林：神秘青蛙
    public static final RegistryObject<EntityType<MysticFrog>> MYSTIC_FROG = DevelopmentSpawnPolicy.developmentOnly(register("mystic_frog", MysticFrog::new, 0.5F, 0.5F, 8));

    // 地下与洞穴：蠕虫和蜗牛
    public static final RegistryObject<EntityType<Worm>> WORM = registerCompact("worm", Worm::new);
    public static final RegistryObject<EntityType<Snail>> SNAIL = registerInsect("snail", (type, level) -> new Snail(type, level, Snail.Profile.NORMAL));

    // 地下与洞穴：宝石兔与宝石松鼠
    public static final RegistryObject<EntityType<JewelBunny>> JEWEL_BUNNY = register("jewel_bunny", JewelBunny::new);
    public static final RegistryObject<EntityType<JewelSquirrel>> JEWEL_SQUIRREL = register("jewel_squirrel", JewelSquirrel::new);

    // 地下与洞穴：飞灵
    public static final RegistryObject<EntityType<Fealing>> FEALING = registerInsect("fealing", Fealing::new);

    // 发光蘑菇：发光蜗牛与松露虫
    public static final RegistryObject<EntityType<Snail>> GLOWING_SNAIL = registerInsect("glowing_snail", (type, level) -> new Snail(type, level, Snail.Profile.GLOWING));
    public static final RegistryObject<EntityType<TruffleWorm>> TRUFFLE_WORM = DevelopmentSpawnPolicy.developmentOnly(register("truffle_worm", TruffleWorm::new, 0.7F, 0.25F, 8));

    // 发光蘑菇：发光哞菇与发光咯菇
    public static final RegistryObject<EntityType<GlowingMooshroom>> GLOWING_MOOSHROOM = DevelopmentSpawnPolicy.developmentOnly(register("glowing_mooshroom", GlowingMooshroom::new, 1.2F, 1.7F, 10));
    public static final RegistryObject<EntityType<Cluckshroom>> GLOWING_CLUCKSHROOM = DevelopmentSpawnPolicy.developmentOnly(register("glowing_cluckshroom", (type, level) -> new Cluckshroom(type, level, true), 0.6F, 1.0F, 10));

    // 神圣：荧光虫与七彩草蛉
    public static final RegistryObject<EntityType<GlowBug>> LIGHTNING_BUG = DevelopmentSpawnPolicy.developmentOnly(register("lightning_bug", (type, level) -> new GlowBug(type, level, "idle"), 0.4F, 0.3F, 8));
    public static final RegistryObject<EntityType<PrismaticLacewing>> PRISMATIC_LACEWING = registerInsect("prismatic_lacewing", PrismaticLacewing::new);

    // 地狱：地狱蝴蝶与岩浆蜗牛
    public static final RegistryObject<EntityType<HellButterfly>> HELL_BUTTERFLY = registerInsect("hell_butterfly", HellButterfly::new);
    public static final RegistryObject<EntityType<Snail>> MAGMA_SNAIL = registerInsect("magma_snail", (type, level) -> new Snail(type, level, Snail.Profile.MAGMA));

    // 墓地：蛆虫
    public static final RegistryObject<EntityType<SimpleCritter>> MAGGOT = registerInsect("maggot", SimpleCritter::new);

    // 大风天：瓢虫
    public static final RegistryObject<EntityType<Ladybug>> LADYBUG = registerInsect("ladybug", Ladybug::new);

    // 特殊兔兔：爆炸兔与敌对变种
    public static final RegistryObject<EntityType<ExplosiveBunny>> EXPLOSIVE_BUNNY = register("explosive_bunny", ExplosiveBunny::new);
    public static final RegistryObject<EntityType<HostileBunny>> HOSTILE_BUNNY = registerHostile("hostile_bunny", HostileBunny::new);

    private static <T extends Mob> RegistryObject<EntityType<T>> register(String name, EntityType.EntityFactory<T> factory) {
        return register(name, factory, 0.4F, 0.5F, 8);
    }

    private static <T extends Mob> RegistryObject<EntityType<T>> registerInsect(String name, EntityType.EntityFactory<T> factory) {
        return registerCompact(name, factory);
    }

    private static <T extends Mob> RegistryObject<EntityType<T>> registerCompact(String name, EntityType.EntityFactory<T> factory) {
        return register(name, factory, 0.5F, 0.3F, 8);
    }

    /// 集中创建普通小动物实体类型，保证尺寸和追踪距离只在注册入口声明一次。
    private static <T extends Mob> RegistryObject<EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, float width, float height, int trackingRange) {
        return PortDeferredRegisterExtension.register(ENTITIES, name,
                id -> EntityType.Builder.of(factory, MobCategory.CREATURE)
                        .sized(width, height)
                        .clientTrackingRange(trackingRange)
                        .build(id.toString()));
    }

    private static <T extends Mob> RegistryObject<EntityType<T>> registerHostile(String name, EntityType.EntityFactory<T> factory) {
        return PortDeferredRegisterExtension.register(ENTITIES, name,
                id -> EntityType.Builder.of(factory, MobCategory.MONSTER).sized(0.4F, 0.5F).clientTrackingRange(10).build(id.toString()));
    }

    private static <T extends Mob> RegistryObject<EntityType<T>> registerHostileCompact(String name, EntityType.EntityFactory<T> factory) {
        return PortDeferredRegisterExtension.register(ENTITIES, name,
                id -> EntityType.Builder.of(factory, MobCategory.MONSTER).sized(0.5F, 0.3F).clientTrackingRange(10).build(id.toString()));
    }
}
