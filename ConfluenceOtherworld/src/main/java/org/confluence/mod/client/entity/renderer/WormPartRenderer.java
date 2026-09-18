package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.client.entity.model.WormPartGeoModel;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.confluence.mod.common.entity.monster.BaseWormPart;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

// 每个部件实体类型独立注册渲染资源，飞龙仍按节段角色显示对应模型分组。
public final class WormPartRenderer extends GeoNormalRenderer<BaseWormPart> {
    private final EntityType<? extends BaseWormMonster> family;
    private final boolean wyvernGeometry;

    public WormPartRenderer(EntityRendererProvider.Context context, EntityType<? extends BaseWormMonster> family, float scale, boolean wyvernGeometry) {
        super(context, createModel(BuiltInRegistries.ENTITY_TYPE.getKey(family), wyvernGeometry), true, scale, 0.0F);
        this.family = family;
        this.wyvernGeometry = wyvernGeometry;
    }

    private static WormPartGeoModel<BaseWormPart> createModel(ResourceLocation family, boolean wyvernGeometry) {
        String body = wyvernGeometry ? "wyvern" : family.getPath() + "_segment";
        String tail = wyvernGeometry ? "wyvern" : family.getPath() + "_tail";
        return new WormPartGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(family.getNamespace(), "geo/entity/" + body + ".geo.json"),
                ResourceLocation.fromNamespaceAndPath(family.getNamespace(), "textures/entity/" + body + ".png"),
                ResourceLocation.fromNamespaceAndPath(family.getNamespace(), "geo/entity/" + tail + ".geo.json"),
                ResourceLocation.fromNamespaceAndPath(family.getNamespace(), "textures/entity/" + tail + ".png"));
    }

    @Override
    protected boolean usesInterpolatedLight(BaseWormPart segment) {
        return true;
    }

    @Override
    protected Vector3f getWormModelCenter(BaseWormPart segment) {
        return WormHeadRenderer.sharedModelCenter(family);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            BaseWormPart segment,
            BakedGeoModel model,
            MultiBufferSource buffers,
            VertexConsumer buffer,
            boolean reRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        if (wyvernGeometry) {
            boolean tail = segment.isTail();
            boolean wing = !tail && (segment.getSegmentIndex() == 3 || segment.getSegmentIndex() == 9);
            setHidden(model, "Bone", true);
            setHidden(model, "Bone2", tail || wing);
            setHidden(model, "Bone3", tail || !wing);
            setHidden(model, "Bone4", !tail);
        }
        super.preRender(poseStack, segment, model, buffers, buffer, reRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected boolean shouldRotateAlongPitch(BaseWormPart segment) {
        return !wyvernGeometry;
    }

    @Override
    protected void applyRotations(BaseWormPart segment, PoseStack poseStack, float age, float yaw, float partialTick) {
        if (!wyvernGeometry) {
            super.applyRotations(segment, poseStack, age, yaw, partialTick);
            return;
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - getRenderYaw(segment, partialTick)));
        poseStack.mulPose(Axis.XP.rotationDegrees(-getRenderPitch(segment, partialTick)));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, BaseWormPart segment, GeoBone bone,
                                  RenderType renderType, MultiBufferSource buffers, VertexConsumer buffer,
                                  boolean reRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        if (wyvernGeometry) {
            // 模型主干的 Z 范围分别为 13..29、30..46、47..63 像素。
            // 在实体旋转和缩放之后，以局部坐标把主干中心移到体节原点。
            double centerZ = switch (bone.getName()) {
                case "Bone2" -> 21.0;
                case "Bone3" -> 38.0;
                case "Bone4" -> 55.0;
                default -> 0.0;
            };
            // 只移动根分组，子骨骼已经继承偏移，不能递归重复下移。
            if (centerZ != 0.0) poseStack.translate(0.0, -4.5 / 16.0, -centerZ / 16.0);
        }
        super.renderRecursively(poseStack, segment, bone, renderType, buffers, buffer, reRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    @Override
    public int getPackedOverlay(BaseWormPart segment, float u, float partialTick) {
        return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(segment.isHurtFlashing()));
    }

    private static void setHidden(BakedGeoModel model, String name, boolean hidden) {
        model.getBone(name).ifPresent(bone -> bone.setHidden(hidden));
    }

}
