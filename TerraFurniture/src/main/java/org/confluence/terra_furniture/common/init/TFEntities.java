package org.confluence.terra_furniture.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.entity.RideableEntityNull;

public final class TFEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, TerraFurniture.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<RideableEntityNull>> NULL_RIDE = ENTITIES.register(
        "null_ride",
        ()-> EntityType.Builder.<RideableEntityNull>of(
                RideableEntityNull::new,
                MobCategory.MISC
            ).sized(0,0)
            .clientTrackingRange(10)
            .noSave()
            .build("null_ride")
    );
}
