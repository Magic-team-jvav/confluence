package org.confluence.lib.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.client.particle.CrossDustParticle;

@EventBusSubscriber(modid = ConfluenceMagicLib.LIB_ID, value = Dist.CLIENT)
public final class LibModEvents {
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ConfluenceMagicLib.CROSS_DUST_PARTICLE.get(), CrossDustParticle.Provider::new);
    }
}
