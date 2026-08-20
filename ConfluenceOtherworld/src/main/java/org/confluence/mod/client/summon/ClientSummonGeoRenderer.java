package org.confluence.mod.client.summon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

/// 使用原有模型资源绘制非实体召唤物。
final class ClientSummonGeoRenderer extends GeoObjectRenderer<ClientSummonVisual> {
    private final ResourceLocation type;
    private final float scale;
    private final float offsetY;
    private final float yawOffset;

    ClientSummonGeoRenderer(ResourceLocation type) {
        super(new Model(type));
        this.type = type;
        scale = switch (type.getPath()) {
            case "hornet_baby" -> 0.6F;
            case "summon_imp" -> 0.8F;
            default -> 1.0F;
        };
        offsetY = switch (type.getPath()) {
            case "hornet_baby", "sculk_wisp" -> 0.5F;
            case "summon_imp" -> -0.5F;
            default -> 0.0F;
        };
        yawOffset = switch (type.getPath()) {
            case "sculk_wisp" -> -90.0F;
            case "summon_snow_flinx" -> 90.0F;
            default -> 0.0F;
        };
    }

    @Override
    public long getInstanceId(ClientSummonVisual visual) {
        return visual.id().getMostSignificantBits() ^ visual.id().getLeastSignificantBits();
    }

    @Override
    public void preRender(PoseStack poseStack, ClientSummonVisual visual, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        scaleModelForRender(scale, scale, poseStack, visual, model, isReRender, partialTick, packedLight, packedOverlay);
        poseStack.translate(0.0F, offsetY, 0.0F);
        if (yawOffset != 0.0F) poseStack.mulPose(Axis.YP.rotationDegrees(yawOffset));
        super.preRender(poseStack, visual, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, ClientSummonVisual visual, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        if (type.getPath().equals("slime_baby") && !bone.getName().equals("outer") && !bone.getName().equals("slime")) {
            renderType = RenderType.entityCutout(getTextureLocation(visual));
            buffer = bufferSource.getBuffer(renderType);
        }
        super.renderRecursively(poseStack, visual, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private static final class Model extends GeoModel<ClientSummonVisual> {
        private final ResourceLocation model;
        private final ResourceLocation texture;
        private final ResourceLocation animation;

        private Model(ResourceLocation type) {
            if (type.getPath().equals("hornet_baby")) {
                model = Confluence.asResource("geo/entity/hornet.geo.json");
                texture = Confluence.asResource("textures/entity/hornet.png");
                animation = Confluence.asResource("animations/entity/hornet.animation.json");
            } else {
                String path = "summon/" + type.getPath();
                model = Confluence.asResource("geo/entity/" + path + ".geo.json");
                texture = Confluence.asResource("textures/entity/" + path + ".png");
                animation = Confluence.asResource("animations/entity/" + path + ".animation.json");
            }
        }

        @Override
        public ResourceLocation getModelResource(ClientSummonVisual visual) {
            return model;
        }

        @Override
        public ResourceLocation getTextureResource(ClientSummonVisual visual) {
            return texture;
        }

        @Override
        public ResourceLocation getAnimationResource(ClientSummonVisual visual) {
            return animation;
        }

        @Override
        public RenderType getRenderType(ClientSummonVisual visual, ResourceLocation texture) {
            return RenderType.entityCutoutNoCull(texture);
        }
    }
}
