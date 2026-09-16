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
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.WormPartGeoModel;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.confluence.mod.common.entity.monster.BaseWormPart;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

/// 在同一个体节实体类型上选择各蠕虫家族的模型，并处理飞龙模型内部的体节分组。
///
/// 普通蠕虫分别使用身体和尾部文件；飞龙则与 1.21 实现一致，复用 {@code wyvern.geo.json}，
/// 根据体节位置仅显示普通身体、带翼身体或尾部中的一个分组。
public final class WormPartRenderer extends GeoNormalRenderer<BaseWormPart> {
    private final WormPartGeoModel<BaseWormPart> wormModel;

    public WormPartRenderer(EntityRendererProvider.Context context) {
        this(context, new WormPartGeoModel<>(
                Confluence.asResource("geo/entity/giant_worm_segment.geo.json"),
                Confluence.asResource("textures/entity/giant_worm_segment.png"),
                Confluence.asResource("geo/entity/giant_worm_tail.geo.json"),
                Confluence.asResource("textures/entity/giant_worm_tail.png")));
    }

    private WormPartRenderer(EntityRendererProvider.Context context, WormPartGeoModel<BaseWormPart> model) {
        super(context, model, true, 1.0F, 0.0F);
        this.wormModel = model;
    }

    @Override
    protected boolean usesInterpolatedLight(BaseWormPart segment) {
        return true;
    }

    @Override
    protected Vector3f getWormModelCenter(BaseWormPart segment) {
        return WormHeadRenderer.sharedModelCenter(segment.getOwner());
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
        if (wormModel.usesWyvernGeometry(segment)) {
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
        return !wormModel.usesWyvernGeometry(segment);
    }

    @Override
    protected void applyRotations(BaseWormPart segment, PoseStack poseStack, float age, float yaw, float partialTick) {
        if (!wormModel.usesWyvernGeometry(segment)) {
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
        if (wormModel.usesWyvernGeometry(segment)) {
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
    protected float getEffectiveModelScale(BaseWormPart segment) {
        BaseWormMonster owner = segment.getOwner();
        if (!wormModel.usesWyvernGeometry(segment)) {
            return owner != null && (owner.getType() == MonsterEntities.DIGGER.get() || owner.getType() == MonsterEntities.WORLD_FEEDER.get()) ? 1.0F : 2.0F;
        }
        ResourceLocation ownerId = owner == null ? null : BuiltInRegistries.ENTITY_TYPE.getKey(owner.getType());
        return ownerId != null && "arch_wyvern".equals(ownerId.getPath()) ? 1.25F : 1.0F;
    }

    @Override
    public int getPackedOverlay(BaseWormPart segment, float u, float partialTick) {
        return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(segment.isHurtFlashing()));
    }

    private static void setHidden(BakedGeoModel model, String name, boolean hidden) {
        model.getBone(name).ifPresent(bone -> bone.setHidden(hidden));
    }

}
