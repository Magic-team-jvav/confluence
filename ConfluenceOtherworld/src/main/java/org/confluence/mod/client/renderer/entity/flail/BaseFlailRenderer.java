package org.confluence.mod.client.renderer.entity.flail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.util.HandPositionUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/**
 * <h1>连枷渲染器</h1>
 * 弹球模型以链条朝向为轴自旋；链条逐段渲染四边形，待美术提供模型/贴图后替换。
 */
public class BaseFlailRenderer extends EntityRenderer<BaseFlailEntity> {
    private static final ResourceLocation DEFAULT_TEXTURE = Confluence.asResource("textures/entity/flail.png");
    private static final ResourceLocation DEFAULT_CHAIN = Confluence.asResource("textures/entity/flail_chain.png");

    /**
     * 链条模型位置（待美术提供）
     */
    @Nullable
    private ResourceLocation chainModelLocation;
    /**
     * 链条贴图位置（待美术提供）
     */
    @Nullable
    private ResourceLocation chainTextureLocation;

    private final EntityModel<BaseFlailEntity> model;

    public BaseFlailRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new FlailModel(context.bakeLayer(FlailModel.LAYER_LOCATION));
    }

    public void setChainModel(@Nullable ResourceLocation modelLocation) {this.chainModelLocation = modelLocation;}

    public void setChainTexture(@Nullable ResourceLocation textureLocation) {this.chainTextureLocation = textureLocation;}

    @Override
    public boolean shouldRender(BaseFlailEntity entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double camX, double camY, double camZ) {
        return entity.getOwner() != null;
    }

    @Override
    public ResourceLocation getTextureLocation(BaseFlailEntity entity) {
        FlailComponent comp = entity.getComponent();
        if (comp != null && comp.chainTexture() != null) return comp.chainTexture();
        return DEFAULT_TEXTURE;
    }

    @Override
    public void render(BaseFlailEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        Player owner = (Player) entity.getOwner();
        if (owner == null) return;

        FlailComponent component = entity.getComponent();
        if (component == null) return;

        // ── 渲染弹球 ──
        poseStack.pushPose();
        poseStack.translate(0, 0.25F, 0);

        int phase = entity.getPhase();
        if (phase == BaseFlailEntity.PHASE_SPIN) {
            // 自转：实体层提供的纯线段方向（玩家面朝水平方向），不含公转偏移
            poseStack.mulPose(new Quaternionf().rotateAxis(entity.spinAngle, entity.getSpinAxis()));
        } else {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.lengthSqr() > 0.001) {
                float yRot = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(motion.x, motion.z)));
                float xRot = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(-motion.y,
                        Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
                poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
                poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
            }
        }

        model.renderToBuffer(poseStack,
                bufferSource.getBuffer(model.renderType(getTextureLocation(entity))),
                packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFF);
        poseStack.popPose();

        // ── 渲染链条 ──
        renderChain(entity, owner, poseStack, bufferSource, packedLight);
    }

    private void renderChain(BaseFlailEntity entity, Player owner, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight) {
        Vec3 ballPos = entity.getBoundingBox().getCenter();
        Vec3 handPos = HandPositionUtils.getPalmPosition(owner, 1.0F);
        Vec3 diff = ballPos.subtract(handPos);
        double distance = diff.length();
        if (distance < 0.2) return;

        // 链条贴图：优先自定义，否则默认
        ResourceLocation chainTex = chainTextureLocation != null ? chainTextureLocation : DEFAULT_CHAIN;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(chainTex));

        float segLength = 0.25f;
        int segments = (int) (distance / segLength);
        Vec3 step = diff.scale(1.0 / segments);

        poseStack.pushPose();
        Vec3 renderOffset = handPos.subtract(entity.position());
        poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);

        // 整体朝向弹球方向
        float baseYaw = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(diff.x, diff.z)));
        float basePitch = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(-diff.y,
                Math.sqrt(diff.x * diff.x + diff.z * diff.z))));
        poseStack.mulPose(Axis.YP.rotationDegrees(baseYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(basePitch));

        float halfS = 0.0625f; // 链条半宽，中心对齐

        for (int i = 0; i < segments; i++) {
            float zOffset = i * segLength + segLength * 0.5f; // 段中心位置
            poseStack.pushPose();
            poseStack.translate(0, 0, zOffset);

            if (i % 2 == 0) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            }

            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix = pose.pose();
            vertex(consumer, matrix, pose, packedLight, -halfS, -halfS, 0, 0, 0);
            vertex(consumer, matrix, pose, packedLight, halfS, -halfS, 0, 0, 1);
            vertex(consumer, matrix, pose, packedLight, halfS, halfS, 0, 1, 1);
            vertex(consumer, matrix, pose, packedLight, -halfS, halfS, 0, 1, 0);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, PoseStack.Pose normal,
                               int packedLight, float x, float y, float z, float u, float v) {
        consumer.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .setNormal(normal, 0, 1, 0);
    }
}
