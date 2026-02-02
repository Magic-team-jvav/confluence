package org.confluence.terraentity.init.entity;

import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.rideable.RideableBee;
import org.confluence.terraentity.entity.rideable.RideableSlime;
import org.confluence.terraentity.entity.util.AttBuilder;
import org.confluence.terraentity.init.TEEntities;

public class TERideableEntities {

    public static final DeferredHolder<EntityType<?>, EntityType<RideableSlime>> RIDEABLE_SLIME = TEEntities.registerMonster("rideable_slime", RideableSlime::new,0.5F,0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<RideableBee>> RIDEABLE_BEE = TEEntities.registerMonster("rideable_bee", RideableBee::new,0.5F,0.5F);

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(TERideableEntities.RIDEABLE_SLIME.get(), c->new GeoNormalRenderer<>(c, TERideableEntities.RIDEABLE_SLIME.getId().withPrefix("rideable/"),false, 1.6f,0));
        event.registerEntityRenderer(TERideableEntities.RIDEABLE_BEE.get(), c->new GeoNormalRenderer<>(c, TERideableEntities.RIDEABLE_BEE.getId().withPrefix("rideable/"),false));

    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(TERideableEntities.RIDEABLE_SLIME.get(), AttBuilder.createAttributes().build());
        event.put(TERideableEntities.RIDEABLE_BEE.get(), AttBuilder.createAttributes().build());

    }
    public static void register(){

    }
}
