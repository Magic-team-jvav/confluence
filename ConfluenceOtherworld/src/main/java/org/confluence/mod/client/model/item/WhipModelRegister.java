package org.confluence.mod.client.model.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

// 只负责发现并烘焙鞭子分段模型；组合规则由 WhipAppearance 保存。
// 同路径覆盖交给原版资源管理器处理，不扫描物品注册表。
public final class WhipModelRegister {
    private static final String RESOURCE_FOLDER = "models/item/whip";
    private static final String MODEL_PREFIX = "models/";
    private static final String JSON_SUFFIX = ".json";

    private WhipModelRegister() {}

    public static void register(ModelEvent.RegisterAdditional event) {
        Minecraft.getInstance().getResourceManager()
                .listResources(RESOURCE_FOLDER, location -> location.getPath().endsWith(JSON_SUFFIX) && !location.getPath().endsWith("/config.json"))
                .keySet().stream().map(WhipModelRegister::stripModelResourcePath).forEach(event::register);
    }

    private static ModelResourceLocation stripModelResourcePath(ResourceLocation resource) {
        String path = resource.getPath();
        path = path.substring(MODEL_PREFIX.length(), path.length() - JSON_SUFFIX.length());
        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(resource.getNamespace(), path), ModelResourceLocation.STANDALONE_VARIANT);
    }
}
