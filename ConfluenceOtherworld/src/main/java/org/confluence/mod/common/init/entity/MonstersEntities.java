package org.confluence.mod.common.init.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
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
    public static final RegistryObject<EntityType<BaseSlime>> LAVA_SLIME = ENTITIES.register("lava_slime", () -> EntityType.Builder.<BaseSlime>of((entityType, level) -> new BaseSlime(entityType, level, 0xFFB150, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune().build(TEEntities.Key("lava_slime")));
    public static final RegistryObject<EntityType<BaseSlime>> LUMINOUS_SLIME = registerSlime("luminous_slime", 0xFFFFFF, 2);
    public static final RegistryObject<EntityType<BaseSlime>> CRIMSLIME = registerSlime("crimslime", 0x8B4949, 2);
    public static final RegistryObject<EntityType<BaseSlime>> PURPLE_SLIME = registerSlime("purple_slime", 0xf334f8, 2);
    public static final RegistryObject<EntityType<BaseSlime>> RED_SLIME = registerSlime("red_slime", 0xf83434, 2);
    public static final RegistryObject<EntityType<BaseSlime>> TROPIC_SLIME = registerSlime("tropic_slime", 0x73bcf4, 2);
    public static final RegistryObject<EntityType<BaseSlime>> YELLOW_SLIME = registerSlime("yellow_slime", 0xf8e234, 2);
    public static final RegistryObject<EntityType<HoneySlime>> HONEY_SLIME = ENTITIES.register("honey_slime", () -> EntityType.Builder.<HoneySlime>of((entityType, level) -> new HoneySlime(entityType, level, 0xf8e234), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("honey_slime")));
    public static final RegistryObject<EntityType<BlackSlime>> BLACK_SLIME = ENTITIES.register("black_slime", () -> EntityType.Builder.of(BlackSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("black_slime")));
    public static final RegistryObject<EntityType<BaseSlime>> GREEN_DUMPLING_SLIME = registerSlime("green_dumpling_slime", 0x32CD32, 2);
    public static final RegistryObject<EntityType<BaseSlime>> SWAMP_SLIME = registerSlime("swamp_slime", 0x556B2F, 2);
    public static final RegistryObject<EntityType<GoldenSlime>> GOLDEN_SLIME = ENTITIES.register("golden_slime", () -> EntityType.Builder.of(GoldenSlime::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("golden_slime")));
    public static final RegistryObject<EntityType<FleshSlime>> FLESH_SLIME = ENTITIES.register("flesh_slime", () -> EntityType.Builder.<FleshSlime>of((entityType, level) -> new FleshSlime(entityType, level, 0xFF0000, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("fleshed_slime")));

    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_SLIME = ENTITIES.register("spiked_slime", () -> EntityType.Builder.<SpikedSlime>of((entityType, level) -> new SpikedSlime(entityType, level, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("spiked_slime")));
    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_JUNGLE_SLIME = ENTITIES.register("spiked_jungle_slime", () -> EntityType.Builder.<SpikedSlime>of((entityType, level) -> SpikedJungleSlime.createSpikedJungleSlime(entityType, level, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("spiked_jungle_slime")));
    public static final RegistryObject<EntityType<SpikedSlime>> SPIKED_ICE_SLIME = ENTITIES.register("spiked_ice_slime", () -> EntityType.Builder.<SpikedSlime>of((entityType, level) -> SpikedJungleSlime.createSpikedIceSlime(entityType, level, 2), MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).build(TEEntities.Key("spiked_ice_slime")));
}
