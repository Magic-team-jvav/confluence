package org.confluence.mod.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.track.TrackTypeProvider;
import org.mesdag.portlib.registries.PortCustomRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static net.minecraft.resources.ResourceKey.createRegistryKey;

public final class ModCustomRegistries {
    public static final PortCustomRegistration<TrackTypeProvider> TRACK_TYPE_PROVIDERS = createRegistry(Keys.TRACK_TYPE_PROVIDER);

    private static <T> PortCustomRegistration<T> createRegistry(ResourceKey<Registry<T>> key) {
        return PortRegisterHandler.custom(Confluence.MODID, key, maker -> {});
    }

    public static class Keys {
        public static final ResourceKey<Registry<TrackTypeProvider>> TRACK_TYPE_PROVIDER = createRegistryKey(Confluence.asResource("track_type_provider"));
    }

    public static void register(IEventBus bus) {
        ModTrackTypeProviderTypes.TYPES.register(bus);
    }
}
