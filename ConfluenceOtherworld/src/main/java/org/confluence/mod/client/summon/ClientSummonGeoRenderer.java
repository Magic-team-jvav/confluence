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
    private boolean shellPass;

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
    public void preRender(PoseStack poseStack, ClientSummonVisual visual, BakedGeoModel model, MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, visual, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.translate(-0.5F, -0.51F, -0.5F);
        scaleModelForRender(scale, scale, poseStack, visual, model, isReRender, partialTick, packedLight, packedOverlay);
        poseStack.translate(0.0F, offsetY, 0.0F);
        if (yawOffset != 0.0F) poseStack.mulPose(Axis.YP.rotationDegrees(yawOffset));
    }

    @Override
    public void actuallyRender(PoseStack poses, ClientSummonVisual visual, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource buffers, VertexConsumer buffer, boolean reRender, float partialTick,
                               int light, int overlay, float red, float green, float blue, float alpha) {
        shellPass = false;
        super.actuallyRender(poses, visual, model, renderType, buffers, buffer, reRender,
                partialTick, light, overlay, red, green, blue, alpha);
        if (!type.getPath().equals("slime_baby")) return;
        if (buffers instanceof MultiBufferSource.BufferSource source) source.endBatch(renderType);
        RenderType shell = RenderType.entityTranslucentCull(getTextureLocation(visual));
        shellPass = true;
        try {
            super.actuallyRender(poses, visual, model, shell, buffers, buffers.getBuffer(shell), true,
                    partialTick, light, overlay, red, green, blue, alpha);
        } finally {
            shellPass = false;
        }
    }

    @Override
    public void renderCubesOfBone(PoseStack poses, GeoBone bone, VertexConsumer buffer, int light,
                                  int overlay, float red, float green, float blue, float alpha) {
        if (!type.getPath().equals("slime_baby")) {
            super.renderCubesOfBone(poses, bone, buffer, light, overlay, red, green, blue, alpha);
            return;
        }
        if (bone.isHidden()) return;
        for (int index = 0; index < bone.getCubes().size(); index++) {
            boolean shell = bone.getName().equals("outer") && index == 0;
            if (shell != shellPass) continue;
            poses.pushPose();
            renderCube(poses, bone.getCubes().get(index), buffer, light, overlay, red, green, blue, alpha);
            poses.popPose();
        }
    }

    private static final class Model extends GeoModel<ClientSummonVisual> {
        private final ResourceLocation model;
        private final ResourceLocation texture;
        private final ResourceLocation animation;
        private final boolean noCull;

        private Model(ResourceLocation type) {
            noCull = switch (type.getPath()) {
                case "finch_baby", "hornet_baby", "slime_baby", "summon_imp", "summon_snow_flinx",
                     "vampire_bat", "vampire_frog", "deadly_sphere_blade" -> true;
                default -> false;
            };
            if (type.getPath().equals("hornet_baby")) {
                model = Confluence.asResource("geo/entity/summon/hornet_baby.geo.json");
                texture = Confluence.asResource("textures/entity/summon/hornet_baby.png");
                animation = Confluence.asResource("animations/entity/hornet.animation.json");
            } else if (type.getPath().startsWith("spider_")) {
                model = Confluence.asResource("geo/entity/summon/spider.geo.json");
                animation = Confluence.asResource("animations/entity/summon/spider.animation.json");
                texture = Confluence.asResource(type.getPath().equals("spider_venom") ? "textures/entity/summon/spider.png" : "textures/entity/summon/spider/" + type.getPath().substring(7) + ".png");
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
            return noCull ? RenderType.entityCutoutNoCull(texture) : RenderType.entityCutout(texture);
        }
    }
}
