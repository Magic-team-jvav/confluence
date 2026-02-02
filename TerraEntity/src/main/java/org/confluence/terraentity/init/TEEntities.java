package org.confluence.terraentity.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.entity.*;


public final class TEEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, TerraEntity.MODID);

    public static String Key(String key){
        return TerraEntity.MODID + ":" + key;
    }

    public static <T extends Mob> DeferredHolder<EntityType<?>,EntityType<T>> registerMonster(String name, EntityType.EntityFactory<T> entityFactory, float width, float height){
        return registerEntity(name, entityFactory, MobCategory.MONSTER, width, height);
    }

    public static <T extends Mob> DeferredHolder<EntityType<?>,EntityType<T>> registerCreature(String name, EntityType.EntityFactory<T> entityFactory, float width, float height){
        return registerEntity(name, entityFactory, MobCategory.CREATURE, width, height);
    }

    public static <T extends Mob> DeferredHolder<EntityType<?>,EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> entityFactory, MobCategory category, float width, float height){
        return ENTITIES.register(name, () -> EntityType.Builder.of(entityFactory, category).sized(width, height).clientTrackingRange(10).build(Key(name)));
    }


    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        TEMonsterEntities.registerRenderers(event);
        TEBossEntities.registerRenderers(event);
        TEProjectileEntities.registerRenderers(event);
        TESummonEntities.registerRenderers(event);
        TERideableEntities.registerRenderers(event);
        TENpcEntities.registerRenderers(event);
        TEAnimals.registerRenderers(event);

    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        TEBossEntities.registerEntityAttributes(event);
        TEMonsterEntities.registerEntityAttributes(event);
        TERideableEntities.registerEntityAttributes(event);
        TESummonEntities.registerEntityAttributes(event);
        TENpcEntities.registerEntityAttributes(event);
        TEAnimals.registerEntityAttributes(event);

    }

    public static void spawnPlacementRegister(RegisterSpawnPlacementsEvent event) {
        TEMonsterEntities.spawnPlacementRegister(event);
        TENpcEntities.spawnPlacementRegister(event);
        TEAnimals.spawnPlacementRegister(event);
    }

    public static void register(IEventBus bus){
        TEBossEntities.register();
        TESummonEntities.register();
        TERideableEntities.register();
        TEMonsterEntities.register();
        TEProjectileEntities.register();
        TENpcEntities.register();
        TEAnimals.register();
        ENTITIES.register(bus);
    }
}
