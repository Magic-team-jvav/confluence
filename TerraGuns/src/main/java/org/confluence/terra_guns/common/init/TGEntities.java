package org.confluence.terra_guns.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.common.entity.bullet.BaseBulletEntity;
import org.confluence.terra_guns.common.entity.bullet.CustomBulletEntity;

import java.util.function.Supplier;

public class TGEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, TerraGuns.MODID);
    public static final Supplier<EntityType<BaseBulletEntity>> BASE_BULLET_ENTITY = ENTITY_TYPES.register("base_bullet", () -> EntityType.Builder.<BaseBulletEntity>of(BaseBulletEntity::new, MobCategory.MISC).sized(0.1f,0.1f).build(TerraGuns.asResourceString("base_bullet")));
    public static final Supplier<EntityType<CustomBulletEntity>> GRAVITY_BULLET_ENTITY = ENTITY_TYPES.register("gravity_bullet", () -> EntityType.Builder.<CustomBulletEntity>of(CustomBulletEntity::new, MobCategory.MISC).sized(0.1f,0.1f).build(TerraGuns.asResourceString("gravity_bullet")));
}
