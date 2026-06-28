package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.animal.Bunny;
import org.confluence.mod.common.entity.animal.HostileBunny;
import org.confluence.mod.common.entity.animal.JewelBunny;

public class CritterEntities {
    public static final net.minecraftforge.registries.DeferredRegister<EntityType<?>> ENTITIES = net.minecraftforge.registries.DeferredRegister.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, Confluence.MODID);

    public static final RegistryObject<EntityType<Bunny>> BUNNY = PortDeferredRegisterExtension.register(ENTITIES, "bunny", id -> EntityType.Builder.of(Bunny::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<JewelBunny>> JEWEL_BUNNY = PortDeferredRegisterExtension.register(ENTITIES, "jewel_bunny", id -> EntityType.Builder.of(JewelBunny::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<HostileBunny>> HOSTILE_BUNNY = PortDeferredRegisterExtension.register(ENTITIES, "hostile_bunny", id -> EntityType.Builder.of(HostileBunny::new, MobCategory.MONSTER).sized(0.4F, 0.5F).clientTrackingRange(10).build(id.toString()));
}
