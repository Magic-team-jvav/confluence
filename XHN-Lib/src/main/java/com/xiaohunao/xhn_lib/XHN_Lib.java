package com.xiaohunao.xhn_lib;

import com.xiaohunao.xhn_lib.common.DynamicLoaderHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;


/**
 * XHN Lib
 * 
 * @author Xiaohunao
 */
@Mod(XHN_Lib.MODID)
public class XHN_Lib {
    public static final String MODID = "xhn_lib";
    public XHN_Lib(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(DynamicLoaderHelper::registerAllDynamicLoaders);
    }
    

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String asDescriptionId(String path) {
        return MODID + "." + path;
    }

    public static <T> ResourceKey<Registry<T>> asResourceKey(String path) {
        return ResourceKey.createRegistryKey(asResource(path));
    }
    public static <T> ResourceKey<T> asResourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, asResource(path));
    }



}
