package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.EyeOfCthulhu;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import java.util.List;

/// 克苏鲁之眼在第二阶段强化冲刺时使用的动作残影渲染器。
public final class EyeOfCthulhuRenderer extends BossGeoRenderer<EyeOfCthulhu> {
    private boolean renderingAfterimage;
    private EyeOfCthulhu.AfterimageSnapshot activeSnapshot;
    private float activeAlpha;

    public EyeOfCthulhuRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/eye_of_cthulhu"), true, 1.0F, 1.5F);
    }

    @Override
    public void render(EyeOfCthulhu entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        List<EyeOfCthulhu.AfterimageSnapshot> snapshots = entity.getAfterimageSnapshots();
        if (!snapshots.isEmpty()) {
            Vec3 current = entity.getPosition(partialTick);
            int count = snapshots.size();
            renderingAfterimage = true;
            for (int i = 0; i < count; i++) {
                activeSnapshot = snapshots.get(i);
                float factor = (i + 1.0F) / count;
                activeAlpha = factor * factor * 0.5F;

                poseStack.pushPose();
                Vec3 offset = activeSnapshot.position().subtract(current);
                poseStack.translate(offset.x, offset.y, offset.z);
                super.render(entity, activeSnapshot.yRot(), partialTick, poseStack, buffers, packedLight);
                poseStack.popPose();
            }
            renderingAfterimage = false;
            activeSnapshot = null;
        }
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public RenderType getRenderType(EyeOfCthulhu entity, ResourceLocation texture, @Nullable MultiBufferSource buffers, float partialTick) {
        return renderingAfterimage ? RenderType.entityTranslucent(texture) : super.getRenderType(entity, texture, buffers, partialTick);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            EyeOfCthulhu entity,
            BakedGeoModel model,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        if (renderingAfterimage) {
            super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick,
                    packedLight, packedOverlay, 0.0F, 0.0F, 0.0F, activeAlpha);
        } else {
            super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick,
                    packedLight, packedOverlay, red, green, blue, alpha);
        }
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
