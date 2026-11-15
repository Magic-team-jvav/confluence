package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.util.generation.GenerationProvider;
import org.confluence.mod.util.track.TrackTypeProvider;

import static net.minecraft.resources.ResourceKey.createRegistryKey;

///
///
///
public final class ModCustomRegistries {
    public static final Registry<MapCodec<? extends TradeCondition>> TRADE_CONDITIONS = createRegistry(Keys.TRADE_CONDITIONS);
    public static final Registry<TrackTypeProvider> TRACK_TYPE_PROVIDERS = createRegistry(Keys.TRACK_TYPE_PROVIDER);
    public static final Registry<GenerationProvider> GENERATION_PROVIDERS = createRegistry(Keys.GENERATION_PROVIDER);

    private static <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
        return new RegistryBuilder<>(key).create();
    }

    public static class Keys {
        public static final ResourceKey<Registry<MapCodec<? extends TradeCondition>>> TRADE_CONDITIONS = createRegistryKey(Confluence.asResource("trade_conditions"));
        public static final ResourceKey<Registry<TrackTypeProvider>> TRACK_TYPE_PROVIDER = createRegistryKey(Confluence.asResource("track_type_provider"));
        public static final ResourceKey<Registry<GenerationProvider>> GENERATION_PROVIDER = createRegistryKey(Confluence.asResource("generation_provider"));
    }

    /// 由 `NewRegistryEvent` 调用：把上面建好的 `Registry` 对象交给原版注册表系统。
    public static void newRegistry(NewRegistryEvent event) {
        event.register(TRADE_CONDITIONS);
        event.register(TRACK_TYPE_PROVIDERS);
        event.register(GENERATION_PROVIDERS);
    }

    /// 由 `Confluence` 构造器调用：挂 `NewRegistryEvent`，再把贸易条件的 `DeferredRegister` 挂到模组事件总线。
    public static void register(IEventBus bus) {
        bus.addListener(ModCustomRegistries::newRegistry);
        ModTradeConditions.TYPES.register(bus);
        ModTrackTypeProviderTypes.TYPES.register(bus);
        ModGenerationProviderTypes.TYPES.register(bus);
    }
}
