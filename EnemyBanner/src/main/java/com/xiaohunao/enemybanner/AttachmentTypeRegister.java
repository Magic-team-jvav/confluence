package com.xiaohunao.enemybanner;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentTypeRegister {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EnemyBanner.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Object2IntOpenHashMap<String>>> PLAYER_BANNER_COUNT = ATTACHMENT_TYPES.register("player_banner_count",
            () -> AttachmentType.builder((Supplier<Object2IntOpenHashMap<String>>) Object2IntOpenHashMap::new)
                    .serialize(Codec.unboundedMap(Codec.STRING, Codec.INT).xmap(Object2IntOpenHashMap::new, Object2ObjectOpenHashMap::new))
                    .copyOnDeath().build()
    );

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
