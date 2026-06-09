package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>连枷物品手持渲染器</h1>
 * 使用 Geo 模型渲染连枷物品的手持外观。
 */
public class BaseFlailItemRenderer extends GeoItemRenderer<BaseFlailItem> {
    /** 手持模型回退：通用手柄模型 */
    private static final ResourceLocation HANDLE_MODEL = Confluence.asResource("geo/item/flail/handle.geo.json");
    /** 回退贴图：使用实体连枷默认贴图 */
    private static final ResourceLocation FALLBACK_TEXTURE = Confluence.asResource("textures/entity/flail/flail.png");
    private final FlailItemModel model;
    private final Map<String, ResourceLocation> modelCache = new HashMap<>();

    public BaseFlailItemRenderer() {
        super(new FlailItemModel(HANDLE_MODEL, FALLBACK_TEXTURE));
        this.model = (FlailItemModel) getGeoModel();
    }

    /**
     * 根据物品 ID 更新模型和贴图：
     * 优先使用 {@code geo/item/flail/<name>.geo.json}，
     * 若不存在则回退到 {@code handle.geo.json}
     */
    public void updateModelForStack(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String name = itemId.getPath();
        ResourceLocation resolvedModel = modelCache.computeIfAbsent(name, key -> {
            ResourceLocation specific = Confluence.asResource("geo/item/flail/" + key + ".geo.json");
            if (resourceExists(specific)) {
                return specific;
            }
            return HANDLE_MODEL;
        });
        model.model = resolvedModel;

        // 贴图：优先物品专属贴图，回退到实体默认贴图
        ResourceLocation texture = Confluence.asResource("textures/item/flail/" + name + ".png");
        model.texture = resourceExists(texture) ? texture : FALLBACK_TEXTURE;
    }

    /** 检查资源是否存在 */
    private static boolean resourceExists(ResourceLocation location) {
        try {
            return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /** 获取活跃连枷实体的当前阶段，无活跃连枷时返回 -1 */
    private static int getActiveFlailPhase(Player player) {
        return player.level().getEntitiesOfClass(BaseFlailEntity.class,
                        player.getBoundingBox().inflate(30),
                        e -> e.getOwner() == player)
                .stream()
                .findFirst()
                .map(BaseFlailEntity::getPhase)
                .orElse(-1);
    }

    
    //渲染入口
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        updateModelForStack(stack);

        if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            int phase = getActiveFlailPhase(player);
            if (phase != BaseFlailEntity.PHASE_SPIN) return; // 仅 SPIN 阶段显示手持模型

            super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
            return;
        }

        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
    }

    /** 内部 GeoModel，支持运行时切换模型/贴图路径 */
    static class FlailItemModel extends GeoModel<BaseFlailItem> {
        ResourceLocation model;
        ResourceLocation texture;

        FlailItemModel(ResourceLocation model, ResourceLocation texture) {
            this.model = model;
            this.texture = texture;
        }

        @Override
        public ResourceLocation getModelResource(BaseFlailItem animatable) {
            return model;
        }

        @Override
        public ResourceLocation getTextureResource(BaseFlailItem animatable) {
            return texture;
        }

        @Override
        public @Nullable ResourceLocation getAnimationResource(BaseFlailItem animatable) {
            return null;
        }
    }
}
