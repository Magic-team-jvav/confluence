package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
 * 连枷物品手持渲染器。
 *
 * <p>1.21 侧会优先使用单个连枷自己的 Geo 模型，缺失时才回退到公共手柄。1.20
 * 现在暂时只有公共手柄资源，但这里仍保留同样的解析流程，避免以后补资源时还要改代码。</p>
 */
public final class BaseFlailItemRenderer extends GeoItemRenderer<BaseFlailItem> {
    private static final ResourceLocation HANDLE_MODEL =
            Confluence.asResource("geo/item/flail/handle.geo.json");
    private static final ResourceLocation FALLBACK_TEXTURE =
            Confluence.asResource("textures/entity/flail/flail.png");

    private final FlailItemModel model;
    private final Map<String, ResourceLocation> modelCache = new HashMap<>();

    public BaseFlailItemRenderer() {
        super(new FlailItemModel(HANDLE_MODEL, FALLBACK_TEXTURE));
        this.model = (FlailItemModel) getGeoModel();
    }

    /**
     * 按当前物品更新模型和贴图。
     *
     * <p>模型优先查找 {@code geo/item/flail/<物品名>.geo.json}，没有时回退到
     * {@code handle.geo.json}；贴图优先查找 {@code textures/item/flail/<物品名>.png}，
     * 没有时使用实体连枷默认贴图。</p>
     */
    private void updateModelForStack(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String name = itemId.getPath();
        model.model = modelCache.computeIfAbsent(name, key -> {
            ResourceLocation specific =
                    Confluence.asResource("geo/item/flail/" + key + ".geo.json");
            return resourceExists(specific) ? specific : HANDLE_MODEL;
        });

        ResourceLocation texture =
                Confluence.asResource("textures/item/flail/" + name + ".png");
        model.texture = resourceExists(texture) ? texture : FALLBACK_TEXTURE;
    }

    /**
     * 检查客户端资源是否存在；缺资源只回退，不在渲染帧里抛错。
     */
    private static boolean resourceExists(ResourceLocation location) {
        return Minecraft.getInstance()
                .getResourceManager()
                .getResource(location)
                .isPresent();
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        updateModelForStack(stack);
        if (isHand(displayContext)) {
            Player player = Minecraft.getInstance().player;
            if (player == null || activePhase(player)
                    != BaseFlailEntity.PHASE_SPIN) {
                return;
            }
        }
        super.renderByItem(stack, displayContext, poseStack, buffer,
                packedLight, packedOverlay);
    }

    private static boolean isHand(ItemDisplayContext context) {
        return context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }

    private static int activePhase(Player player) {
        return player.level().getEntitiesOfClass(
                        BaseFlailEntity.class,
                        player.getBoundingBox().inflate(30.0),
                        entity -> entity.getOwner() == player)
                .stream()
                .findFirst()
                .map(BaseFlailEntity::getPhase)
                .orElse(-1);
    }

    /**
     * 运行时切换模型和贴图路径的轻量 GeoModel。
     */
    private static final class FlailItemModel extends GeoModel<BaseFlailItem> {
        private ResourceLocation model;
        private ResourceLocation texture;

        private FlailItemModel(ResourceLocation model, ResourceLocation texture) {
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
        public @Nullable ResourceLocation getAnimationResource(
                BaseFlailItem animatable
        ) {
            return null;
        }
    }
}
