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

    public static final RegistryObject<EntityType<Bunny>> BUNNY = register("bunny", Bunny::new);
    public static final RegistryObject<EntityType<JewelBunny>> JEWEL_BUNNY = register("jewel_bunny", JewelBunny::new);
    public static final RegistryObject<EntityType<ExplosiveBunny>> EXPLOSIVE_BUNNY = register("explosive_bunny", ExplosiveBunny::new);
    public static final RegistryObject<EntityType<HostileBunny>> HOSTILE_BUNNY = registerHostile("hostile_bunny", HostileBunny::new);
    public static final RegistryObject<EntityType<Bird>> BIRD = register("bird", Bird::new);
    public static final RegistryObject<EntityType<BlueJay>> BLUE_JAY = register("blue_jay", BlueJay::new);
    public static final RegistryObject<EntityType<Cardinal>> CARDINAL = register("cardinal", Cardinal::new);
    public static final RegistryObject<EntityType<Squirrel>> SQUIRREL = register("squirrel", Squirrel::new);
    public static final RegistryObject<EntityType<RedSquirrel>> RED_SQUIRREL = register("red_squirrel", RedSquirrel::new);
    public static final RegistryObject<EntityType<JewelSquirrel>> JEWEL_SQUIRREL = register("jewel_squirrel", JewelSquirrel::new);
    public static final RegistryObject<EntityType<Duck>> DUCK = PortDeferredRegisterExtension.register(ENTITIES, "duck", id -> EntityType.Builder.of(Duck::new, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).passengerAttachments(new Vec3(0.0, 0.7, -0.1)).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<Crab>> CRAB = registerCompact("crab", Crab::new);
    public static final RegistryObject<EntityType<Worm>> WORM = registerCompact("worm", Worm::new);

    // Insects
    public static final RegistryObject<EntityType<Butterfly>> BUTTERFLY = registerInsect("butterfly", Butterfly::new);
    public static final RegistryObject<EntityType<Fairy>> FAIRY = registerInsect("fairy", Fairy::new);
    public static final RegistryObject<EntityType<Fealing>> FEALING = registerInsect("fealing", Fealing::new);
    public static final RegistryObject<EntityType<SimpleCritter>> GLOWING_SNAIL = registerInsect("glowing_snail", SimpleCritter::new);
    public static final RegistryObject<EntityType<SimpleCritter>> GRUBBY = registerInsect("grubby", SimpleCritter::new);
    public static final RegistryObject<EntityType<SimpleCritter>> MAGGOT = registerInsect("maggot", SimpleCritter::new);
    public static final RegistryObject<EntityType<SimpleCritter>> MAGMA_SNAIL = registerInsect("magma_snail", SimpleCritter::new);
    public static final RegistryObject<EntityType<Sluggy>> SLUGGY = registerInsect("sluggy", Sluggy::new);
    public static final RegistryObject<EntityType<SimpleCritter>> SNAIL = registerInsect("snail", SimpleCritter::new);
    public static final RegistryObject<EntityType<Scorpion>> SCORPION = registerInsect("scorpion", Scorpion::new);
    public static final RegistryObject<EntityType<HellButterfly>> HELL_BUTTERFLY = registerInsect("hell_butterfly", HellButterfly::new);
    public static final RegistryObject<EntityType<PrismaticLacewing>> PRISMATIC_LACEWING = registerInsect("prismatic_lacewing", PrismaticLacewing::new);
    public static final RegistryObject<EntityType<Dragonfly>> DRAGONFLY = registerInsect("dragonfly", Dragonfly::new);
    public static final RegistryObject<EntityType<Grasshopper>> GRASSHOPPER = registerInsect("grasshopper", Grasshopper::new);
    public static final RegistryObject<EntityType<Ladybug>> LADYBUG = registerInsect("ladybug", Ladybug::new);

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
}
