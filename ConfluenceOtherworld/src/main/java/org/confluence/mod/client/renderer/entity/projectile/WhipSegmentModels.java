package org.confluence.mod.client.renderer.entity.projectile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.mod.api.whip.WhipSegment;
import org.confluence.mod.common.item.whip.BaseWhipItem;
import org.mesdag.portlib.event.client.PortModelEvent;

import java.util.HashMap;
import java.util.Map;

/// 注册并缓存鞭子外观引用的物品 JSON 模型。
///
/// <p>模型位置直接来自 {@link org.confluence.mod.api.whip.WhipAppearance}，因此附属模组
/// 不需要使用固定目录或物品同名约定。Forge 1.20.1 必须使用 {@code inventory} 变体
/// 注册这类独立物品模型，否则模型烘焙器会把位置误当成方块状态。</p>
public final class WhipSegmentModels {
    private static final Map<ResourceLocation, ModelResourceLocation> MODELS = new HashMap<>();

    private WhipSegmentModels() {}

    public static void registerAdditionalModels(PortModelEvent.RegisterAdditional event) {
        MODELS.clear();
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(BaseWhipItem.class::isInstance)
                .map(BaseWhipItem.class::cast)
                .flatMap(item -> item.appearance().segments().stream())
                .forEach(segment -> registerSegment(event, segment));
    }

    public static BakedModel model(ResourceLocation location) {
        ModelResourceLocation model = MODELS.get(location);
        return model == null
                ? Minecraft.getInstance().getModelManager().getMissingModel()
                : Minecraft.getInstance().getModelManager().getModel(model);
    }

    private static void registerSegment(PortModelEvent.RegisterAdditional event, WhipSegment segment) {
        registerModel(event, segment.model());
        segment.optionalTipModel().ifPresent(tip -> registerModel(event, tip));
    }

    private static void registerModel(PortModelEvent.RegisterAdditional event, ResourceLocation location) {
        MODELS.computeIfAbsent(location, ignored -> {
            ModelResourceLocation model = inventoryLocation(location);
            event.register(model);
            return model;
        });
    }

    /// 把 1.21 的独立模型路径换算为 Forge 1.20.1 的物品模型键。
    ///
    /// <p>例如 {@code confluence:item/whip_segments/snapthorn} 在 1.20.1 中必须注册成
    /// {@code confluence:whip_segments/snapthorn#inventory}。若保留 {@code item/} 前缀，
    /// 模型烘焙器会再次补上物品目录并静默返回缺失模型。</p>
    private static ModelResourceLocation inventoryLocation(ResourceLocation location) {
        String path = location.getPath();
        String prefix = "item/";
        if (!path.startsWith(prefix) || path.length() == prefix.length()) {
            throw new IllegalArgumentException("Whip segment model must use an item model path: " + location);
        }
        ResourceLocation itemModel = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), path.substring(prefix.length()));
        return new ModelResourceLocation(itemModel, "inventory");
    }
}
