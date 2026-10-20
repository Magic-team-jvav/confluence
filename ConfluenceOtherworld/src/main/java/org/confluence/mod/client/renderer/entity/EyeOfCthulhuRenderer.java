package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.boss.EyeOfCthulhu;

import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 残影只重绘模型，复用本帧骨骼姿态，不重复执行动画和附加渲染层。
public final class EyeOfCthulhuRenderer extends BossGeoRenderer<EyeOfCthulhu> {
    private static final float AFTERIMAGE_ALPHA = 0.5F;
    private EyeOfCthulhu.AfterimageSnapshot activeSnapshot;

    public EyeOfCthulhuRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/eye_of_cthulhu"), true, 1.0F, 1.5F);
    }

    @Override
    public void render(EyeOfCthulhu entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
        var snapshots = entity.getAfterimageSnapshots();
        if (entity.isInvisible() || snapshots.isEmpty()) return;
        BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(entity));
        ResourceLocation texture = getTextureLocation(entity);
        RenderType type = hasNegativeCubes(entity)
                ? RenderStateShardAccessor.entityTranslucentCullOverlay(texture) : RenderType.entityTranslucent(texture);
        Vec3 current = entity.getPosition(partialTick);
        try {
            for (EyeOfCthulhu.AfterimageSnapshot snapshot : snapshots) {
                float remaining = Mth.clamp(1.0F - (entity.tickCount - snapshot.tick() + partialTick)
                        / EyeOfCthulhu.AFTERIMAGE_LIFETIME_TICKS, 0.0F, 1.0F);
                if (remaining <= 0.0F) continue;
                activeSnapshot = snapshot;
                this.animatable = entity;
                poseStack.pushPose();
                try {
                    Vec3 offset = snapshot.position().subtract(current);
                    poseStack.translate(offset.x, offset.y, offset.z);
                    reRender(model, poseStack, buffers, entity, type, buffers.getBuffer(type), partialTick,
                            packedLight, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, AFTERIMAGE_ALPHA * remaining * remaining);
                } finally {
                    poseStack.popPose();
                }
            }
        } finally {
            activeSnapshot = null;
            this.animatable = null;
        }
    }

    /// 身体朝向也使用采样值，避免残影跟着本体一起转向。
    @Override
    protected void applyRotations(EyeOfCthulhu entity, PoseStack poseStack, float age, float yaw, float partialTick) {
        super.applyRotations(entity, poseStack, age, activeSnapshot == null ? yaw : activeSnapshot.yRot(), partialTick);
    }

    @Override
    protected float getRenderPitch(EyeOfCthulhu entity, float partialTick) {
        return activeSnapshot == null ? super.getRenderPitch(entity, partialTick) : activeSnapshot.xRot();
    }

    @Override
    protected float getRenderYaw(EyeOfCthulhu entity, float partialTick) {
        return activeSnapshot == null ? super.getRenderYaw(entity, partialTick) : activeSnapshot.yRot();
    }
}
