package com.xiaohunao.xhn_lib.example.init;

import com.xiaohunao.xhn_lib.XHN_Lib;
import com.xiaohunao.xhn_lib.example.quality.ItemQuality;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Objects;
import java.util.function.Supplier;

public class ModRegistries {
    public static final Registry<ItemQuality> ITEM_QUALITIES = new RegistryBuilder<>(Keys.ITEM_QUALITIES).sync(true).create();

    public static class Keys {
        public static final ResourceKey<Registry<ItemQuality>> ITEM_QUALITIES = XHN_Lib.asResourceKey("item_qualities");
    }


    public static void registerRegistries(NewRegistryEvent event) {
        event.register(ITEM_QUALITIES);

    }

    static <T> Supplier<T> supplyRegistry(ResourceKey<T> key) {
        return com.google.common.base.Suppliers.memoize(() -> Objects.requireNonNull((T) BuiltInRegistries.REGISTRY.get((ResourceKey) key)));
    }

}
