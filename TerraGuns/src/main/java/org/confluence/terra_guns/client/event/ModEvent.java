package org.confluence.terra_guns.client.event;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.client.renderer.entity.BulletRenderer;
import org.confluence.terra_guns.common.init.TGEntities;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terra_guns.util.TGUtil;

import static org.confluence.terra_guns.client.init.TGKeys.AIM;
import static org.confluence.terra_guns.client.init.TGKeys.SHOOT;

@EventBusSubscriber(modid = TerraGuns.MODID, value = Dist.CLIENT)
public class ModEvent {
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        TGItems.GUNS.getEntries().forEach(holder -> TGUtil.registerGunModel(event, holder));
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TGEntities.BASE_BULLET_ENTITY.get(), BulletRenderer::new);
        event.registerEntityRenderer(TGEntities.GRAVITY_BULLET_ENTITY.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void keyBinding(RegisterKeyMappingsEvent event) {
        event.register(SHOOT.get());
        event.register(AIM.get());
    }
}
