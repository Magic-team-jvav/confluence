package org.confluence.mod.common.init.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.animal.*;
import org.confluence.mod.common.entity.monster.CreatureAttributeBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

/// 小动物（动物）注册层（1.20.1 分叉后架构的 `common/init/entity/CritterEntities`）。
///
/// **本文件是增量落地**：1.20 的 `CritterEntities` 有 36 条注册（177 行），这里只写
/// **类已存在于 1.21** 的条目，其余随物种批次一起长 —— 与 `MonsterEntities` 的既有做法相同
/// （详见 `notes/WP2Q-SUBSET.md`）。
///
/// **为什么必须先落这个类**：1.20 的动物类普遍在自己的 `getBreedOffspring` 里回指注册条目
/// （如 `Cluckshroom.java:73` 用 `CritterEntities.CLUCKSHROOM` / `GLOWING_CLUCKSHROOM`、
/// `CloudSheep.java:56` 用 `CLOUD_SHEEP`、`GlowingMooshroom.java:32` 用 `GLOWING_MOOSHROOM`），
/// 于是「单文件闭包」的动物实际被这个类挡住 —— `dep_subset.py` 从它们出发会把整个 1.20
/// 注册层（171 个候选）算成本批新增。
///
/// 属性声明统一汇入 {@link ModEntities#withAttributes}，由 `ModEvents.entityAttributeCreation`
/// → `ModEntities.registerAttributes` 一次注册，不另开一套。
///
public final class CritterEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    // ------------------------------------------------------------------
    // 地表与森林：鸟类
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<Bird>> BIRD = withAttributes(register("bird", "鸟", Bird::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(6).movementSpeed(0.2).flyingSpeed(0.4).magicLibAttackDamage(3).fallDamageMultiplier(0).build());

    public static final DeferredHolder<EntityType<?>, EntityType<BlueJay>> BLUE_JAY = withAttributes(register("blue_jay", "冠蓝鸦", BlueJay::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(6).movementSpeed(0.2).flyingSpeed(0.4).magicLibAttackDamage(3).fallDamageMultiplier(0).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Cardinal>> CARDINAL = withAttributes(register("cardinal", "红雀", Cardinal::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(6).movementSpeed(0.2).flyingSpeed(0.4).magicLibAttackDamage(3).fallDamageMultiplier(0).build());

    // 地表与森林：兔兔
    public static final DeferredHolder<EntityType<?>, EntityType<Bunny>> BUNNY = withAttributes(register("bunny", "兔兔", Bunny::new),
            () -> CreatureAttributeBuilder.rabbit().jumpStrength(0.6).safeFallDistance(6).build());

    // 地表与森林：松鼠
    public static final DeferredHolder<EntityType<?>, EntityType<Squirrel>> SQUIRREL = withAttributes(register("squirrel", "松鼠", Squirrel::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(10).movementSpeed(0.2).safeFallDistance(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<RedSquirrel>> RED_SQUIRREL = withAttributes(register("red_squirrel", "红松鼠", RedSquirrel::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(10).movementSpeed(0.2).safeFallDistance(6).build());

    // 地表与森林：蝴蝶、蚱蜢与臭虫
    public static final DeferredHolder<EntityType<?>, EntityType<Butterfly>> BUTTERFLY = withAttributes(registerInsect("butterfly", "蝴蝶", Butterfly::new),
            () -> CreatureAttributeBuilder.flyingCritter().build());

    public static final DeferredHolder<EntityType<?>, EntityType<Grasshopper>> GRASSHOPPER = withAttributes(registerInsect("grasshopper", "蚱蜢", Grasshopper::new),
            () -> CreatureAttributeBuilder.insect().build());

    public static final DeferredHolder<EntityType<?>, EntityType<Stinkbug>> STINKBUG = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("stinkbug", "臭虫", Stinkbug::new, 0.6F, 0.3F, 8)),
            () -> CreatureAttributeBuilder.flyingCritter().maxHealth(5).build());

    // 地表夜间：萤火虫与仙灵
    public static final DeferredHolder<EntityType<?>, EntityType<GlowBug>> FIREFLY = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("firefly", "萤火虫", (type, level) -> new GlowBug(type, level, "fly"), 0.4F, 0.3F, 8)),
            () -> CreatureAttributeBuilder.flyingCritter().build());

    public static final DeferredHolder<EntityType<?>, EntityType<Fairy>> FAIRY = withAttributes(registerInsect("fairy", "仙灵", Fairy::new),
            () -> CreatureAttributeBuilder.flyingCritter().build());

    // ------------------------------------------------------------------
    // 地表动物：飘飘羊与红棕咯菇
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<CloudSheep>> CLOUD_SHEEP = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("cloud_sheep", "飘飘羊", CloudSheep::new, 1.2F, 1.2F, 10)),
            () -> CreatureAttributeBuilder.from(Sheep.createAttributes()).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Cluckshroom>> CLUCKSHROOM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("cluckshroom", "咯菇", (type, level) -> new Cluckshroom(type, level, false), 0.6F, 1.2F, 10)),
            () -> CreatureAttributeBuilder.from(Chicken.createAttributes()).build());

    // ------------------------------------------------------------------
    // 淡水与岸边：鸭、金鱼与蜻蜓
    // ------------------------------------------------------------------

    public static final DeferredHolder<EntityType<?>, EntityType<Duck>> DUCK = withAttributes(registerEntityType("duck", "鸭", id -> EntityType.Builder.of(Duck::new, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).passengerAttachments(new Vec3(0.0, 0.7, -0.1)).clientTrackingRange(10).build(id.toString())),
            () -> CreatureAttributeBuilder.critter().maxHealth(4).movementSpeed(0.25).flyingSpeed(0.35).waterMovementEfficiency(1).fallDamageMultiplier(0).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Goldfish>> GOLDFISH = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("goldfish", "金鱼", Goldfish::new, 0.6F, 0.45F, 8)),
            () -> CreatureAttributeBuilder.critter().maxHealth(5).movementSpeed(0.2).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Dragonfly>> DRAGONFLY = withAttributes(registerInsect("dragonfly", "蜻蜓", Dragonfly::new),
            () -> CreatureAttributeBuilder.flyingCritter().build());

    public static final DeferredHolder<EntityType<?>, EntityType<Crab>> CRAB = withAttributes(registerHostileCompact("crab", "螃蟹", Crab::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(21).armor(5).attackDamage(10).movementSpeed(0.2).followRange(20).knockbackResistance(0.25).build());

    // 冰雪：企鹅
    public static final DeferredHolder<EntityType<?>, EntityType<Penguin>> PENGUIN = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("penguin", "企鹅", Penguin::new, 0.7F, 1.0F, 8)),
            () -> CreatureAttributeBuilder.critter().maxHealth(5).movementSpeed(0.2).build());

    // 沙漠：蝎子
    public static final DeferredHolder<EntityType<?>, EntityType<Scorpion>> SCORPION = withAttributes(registerInsect("scorpion", "蝎子", Scorpion::new),
            () -> CreatureAttributeBuilder.insect().build());

    // 丛林：蛆虫、鼻涕虫与蚜虫
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleCritter>> GRUBBY = withAttributes(registerInsect("grubby", "蛆虫", SimpleCritter::new),
            () -> CreatureAttributeBuilder.insect().build());

    public static final DeferredHolder<EntityType<?>, EntityType<Sluggy>> SLUGGY = withAttributes(registerInsect("sluggy", "鼻涕虫", Sluggy::new),
            () -> CreatureAttributeBuilder.insect().build());

    public static final DeferredHolder<EntityType<?>, EntityType<SimpleCritter>> BUGGY = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("buggy", "蚜虫", (type, level) -> new SimpleCritter(type, level, "no_wing"), 0.45F, 0.35F, 8)),
            () -> CreatureAttributeBuilder.insect().maxHealth(5).build());

    // 丛林：神秘青蛙
    public static final DeferredHolder<EntityType<?>, EntityType<MysticFrog>> MYSTIC_FROG = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("mystic_frog", "神秘青蛙", MysticFrog::new, 0.5F, 0.5F, 8)),
            () -> CreatureAttributeBuilder.from(Frog.createAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 5)).build());

    // 地下与洞穴：蠕虫和蜗牛
    public static final DeferredHolder<EntityType<?>, EntityType<Worm>> WORM = withAttributes(registerCompact("worm", "蠕虫", Worm::new),
            () -> CreatureAttributeBuilder.insect().build());

    public static final DeferredHolder<EntityType<?>, EntityType<Snail>> SNAIL = withAttributes(registerInsect("snail", "蜗牛", (type, level) -> new Snail(type, level, Snail.Profile.NORMAL)),
            () -> CreatureAttributeBuilder.insect().build());

    // 地下与洞穴：宝石兔与宝石松鼠
    public static final DeferredHolder<EntityType<?>, EntityType<JewelBunny>> JEWEL_BUNNY = withAttributes(register("jewel_bunny", "宝石兔", JewelBunny::new),
            () -> CreatureAttributeBuilder.rabbit().jumpStrength(0.6).safeFallDistance(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<JewelSquirrel>> JEWEL_SQUIRREL = withAttributes(register("jewel_squirrel", "宝石松鼠", JewelSquirrel::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(10).movementSpeed(0.2).safeFallDistance(6).build());

    // 地下与洞穴：飞灵
    public static final DeferredHolder<EntityType<?>, EntityType<Fealing>> FEALING = withAttributes(registerInsectWithEnglishName("fealing", "飞灵", "Flying Spirit", Fealing::new),
            () -> CreatureAttributeBuilder.flyingCritter().build());

    // 发光蘑菇：发光蜗牛与松露虫
    public static final DeferredHolder<EntityType<?>, EntityType<Snail>> GLOWING_SNAIL = withAttributes(registerInsect("glowing_snail", "发光蜗牛", (type, level) -> new Snail(type, level, Snail.Profile.GLOWING)),
            () -> CreatureAttributeBuilder.insect().build());

    public static final DeferredHolder<EntityType<?>, EntityType<TruffleWorm>> TRUFFLE_WORM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("truffle_worm", "松露虫", TruffleWorm::new, 0.7F, 0.25F, 8)),
            () -> CreatureAttributeBuilder.insect().build());

    // 发光蘑菇：发光哞菇与发光咯菇
    public static final DeferredHolder<EntityType<?>, EntityType<GlowingMooshroom>> GLOWING_MOOSHROOM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("glowing_mooshroom", "发光哞菇", GlowingMooshroom::new, 1.0F, 2.15F, 10)),
            () -> CreatureAttributeBuilder.from(Cow.createAttributes()).build());

    public static final DeferredHolder<EntityType<?>, EntityType<Cluckshroom>> GLOWING_CLUCKSHROOM = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("glowing_cluckshroom", "发光咯菇", (type, level) -> new Cluckshroom(type, level, true), 0.6F, 1.2F, 10)),
            () -> CreatureAttributeBuilder.from(Chicken.createAttributes()).build());

    // 神圣：荧光虫与七彩草蛉
    public static final DeferredHolder<EntityType<?>, EntityType<GlowBug>> LIGHTNING_BUG = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("lightning_bug", "荧光虫", (type, level) -> new GlowBug(type, level, "idle"), 0.4F, 0.3F, 8)),
            () -> CreatureAttributeBuilder.flyingCritter().build());

    public static final DeferredHolder<EntityType<?>, EntityType<PrismaticLacewing>> PRISMATIC_LACEWING = withAttributes(registerInsect("prismatic_lacewing", "七彩草蛉", PrismaticLacewing::new),
            () -> CreatureAttributeBuilder.flyingCritter().build());

    // 地狱：地狱蝴蝶与岩浆蜗牛
    public static final DeferredHolder<EntityType<?>, EntityType<HellButterfly>> HELL_BUTTERFLY = withAttributes(registerInsect("hell_butterfly", "地狱蝴蝶", HellButterfly::new),
            () -> CreatureAttributeBuilder.flyingCritter().build());

    public static final DeferredHolder<EntityType<?>, EntityType<Snail>> MAGMA_SNAIL = withAttributes(registerInsect("magma_snail", "岩浆蜗牛", (type, level) -> new Snail(type, level, Snail.Profile.MAGMA)),
            () -> CreatureAttributeBuilder.insect().build());

    // 墓地：蛆虫
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleCritter>> MAGGOT = withAttributes(registerInsect("maggot", "蝇蛆", SimpleCritter::new),
            () -> CreatureAttributeBuilder.insect().build());

    // 大风天：瓢虫
    public static final DeferredHolder<EntityType<?>, EntityType<Ladybug>> LADYBUG = withAttributes(registerInsect("ladybug", "瓢虫", Ladybug::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(3).movementSpeed(0.18).flyingSpeed(0.25).build());

    // 特殊兔兔：爆炸兔与敌对变种
    public static final DeferredHolder<EntityType<?>, EntityType<ExplosiveBunny>> EXPLOSIVE_BUNNY = withAttributes(register("explosive_bunny", "爆炸兔", ExplosiveBunny::new),
            () -> CreatureAttributeBuilder.rabbit().jumpStrength(0.6).safeFallDistance(6).build());

    public static final DeferredHolder<EntityType<?>, EntityType<HostileBunny>> HOSTILE_BUNNY = withAttributes(registerHostile("hostile_bunny", "敌对兔兔", HostileBunny::new),
            () -> CreatureAttributeBuilder.rabbit().maxHealth(70).armor(4).jumpStrength(0.6).safeFallDistance(6).attackDamage(20).followRange(16).build());

    // ------------------------------------------------------------------
    // ------------------------------------------------------------------

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerCompact(String name, EntityType.EntityFactory<T> factory) {
        return register(name, factory, 0.5F, 0.3F, 8);
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, float width, float height, int trackingRange) {
        return ENTITIES.register(name, id -> EntityType.Builder.of(factory, MobCategory.CREATURE)
                .sized(width, height)
                .clientTrackingRange(trackingRange)
                .build(id.toString()));
    }

    /// 属性声明汇入 {@link ModEntities#withAttributes}（同一个 `ATTRIBUTES` 表）。
    private static <T extends LivingEntity> DeferredHolder<EntityType<?>, EntityType<T>> withAttributes(DeferredHolder<EntityType<?>, EntityType<T>> type, Supplier<CreatureAttributeBuilder.Definition> attributes) {
        return ModEntities.withAttributes(type, attributes);
    }

    private CritterEntities() {}

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryId, String chineseName, EntityType.EntityFactory<T> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = register(registryId, factory, 0.4F, 0.5F, 8);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerInsect(String registryId, String chineseName, EntityType.EntityFactory<T> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = registerCompact(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerCompact(String registryId, String chineseName, EntityType.EntityFactory<T> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = register(registryId, factory, 0.5F, 0.3F, 8);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryId, String chineseName, EntityType.EntityFactory<T> factory, float width, float height, int trackingRange) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, id -> EntityType.Builder.of(factory, MobCategory.CREATURE)
                .sized(width, height)
                .clientTrackingRange(trackingRange)
                .build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerHostile(String registryId, String chineseName, EntityType.EntityFactory<T> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, id -> EntityType.Builder.of(factory, MobCategory.MONSTER).sized(0.4F, 0.5F).clientTrackingRange(10).build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerHostileCompact(String registryId, String chineseName, EntityType.EntityFactory<T> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, id -> EntityType.Builder.of(factory, MobCategory.MONSTER).sized(0.5F, 0.3F).clientTrackingRange(10).build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntityType(String registryId, String chineseName, Function<ResourceLocation, EntityType<T>> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerInsectWithEnglishName(String registryId, String chineseName, String englishName, EntityType.EntityFactory<T> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = registerCompact(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
