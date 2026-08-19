package org.confluence.mod.client.renderer.entity.flail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.entity.flail.GuardianFlailEntity;
import org.confluence.mod.util.HandPositionUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/// 连枷实体渲染器。
///
/// <p>普通连枷头优先使用 1.21 同步过来的具体 Geo 模型；没有具体模型的直接发射型连枷
/// 仍使用平面精灵回退。链条保持 1.20 新架构里的分段渲染和方向平滑，只修正外观资源缺失，
/// 不改变连枷实体状态机、伤害频率和飞行行为。</p>
public class BaseFlailRenderer extends GeoEntityRenderer<BaseFlailEntity> {
    private static final ResourceLocation DEFAULT_BALL_MODEL = Confluence.asResource("geo/entity/flail/flail.geo.json");
    private static final ResourceLocation DEFAULT_BALL_TEXTURE = Confluence.asResource("textures/entity/flail/flail.png");
    private static final ResourceLocation DEFAULT_CHAIN_TEXTURE = Confluence.asResource("textures/entity/flail/flail_chain.png");

    public BaseFlailRenderer(EntityRendererProvider.Context context) {
        super(context, new FlailGeoModel());
    }

    @Override
    public boolean shouldRender(BaseFlailEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        if (super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ)) {
            return true;
        }
        Entity owner = entity.getOwner();
        if (!(owner instanceof Player player)) {
            return false;
        }
        Vec3 handPos = HandPositionUtils.getPalmPosition(player, 1.0F);
        Vec3 ballPos = entity.getBoundingBox().getCenter();
        return frustum.isVisible(new AABB(ballPos.x, ballPos.y, ballPos.z, handPos.x, handPos.y, handPos.z));
    }

    @Override
    public ResourceLocation getTextureLocation(BaseFlailEntity entity) {
        FlailComponent component = entity.getComponent();
        if (component != null && component.ballTexture() != null) {
            return component.ballTexture();
        }
        return DEFAULT_BALL_TEXTURE;
    }

    @Override
    public void render(BaseFlailEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        if (!(entity.getOwner() instanceof Player owner)) {
            return;
        }
        FlailComponent component = entity.getComponent();
        if (component == null) {
            return;
        }

        ResourceLocation ballModel = resolveBallModel(component);
        if (resourceExists(ballModel)) {
            renderGeoHead(entity, component, ballModel, entityYaw, partialTick, poseStack, buffers, packedLight);
        } else {
            renderSpriteHead(entity, poseStack, buffers, packedLight);
        }

        renderChain(entity, owner, component, poseStack, buffers, packedLight, partialTick);
        if (entity instanceof GuardianFlailEntity guardianFlail) {
            GuardianFlailBeamRenderer.render(guardianFlail, poseStack, buffers, partialTick);
        }
    }

    private void renderGeoHead(BaseFlailEntity entity, FlailComponent component, ResourceLocation ballModel, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        FlailGeoModel model = (FlailGeoModel) getGeoModel();
        model.model = ballModel;
        model.texture = component.ballTexture() == null
                ? DEFAULT_BALL_TEXTURE
                : component.ballTexture();

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.25F, 0.0F);
        applyHeadRotation(entity, poseStack);
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
        poseStack.popPose();
    }

    private static void applyHeadRotation(BaseFlailEntity entity, PoseStack poseStack) {
        int phase = entity.getPhase();
        if (phase == BaseFlailEntity.PHASE_SPIN || phase == BaseFlailEntity.PHASE_THROWN) {
            poseStack.mulPose(new Quaternionf().rotateAxis(entity.spinAngle, entity.getSpinAxis()));
            return;
        }

        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() <= 0.001) {
            return;
        }
        float yRot = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(motion.x, motion.z)));
        float xRot = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(-motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
    }

    private void renderSpriteHead(BaseFlailEntity entity, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.35, 0.0);
        poseStack.scale(0.55F, 0.55F, 0.55F);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotation(
                entity.getPhase() == BaseFlailEntity.PHASE_SPIN
                        ? entity.spinAngle
                        : entity.tickCount * 0.2F));

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        spriteVertex(consumer, pose, packedLight, -1.0F, -1.0F, 0.0F, 1.0F);
        spriteVertex(consumer, pose, packedLight, 1.0F, -1.0F, 1.0F, 1.0F);
        spriteVertex(consumer, pose, packedLight, 1.0F, 1.0F, 1.0F, 0.0F);
        spriteVertex(consumer, pose, packedLight, -1.0F, 1.0F, 0.0F, 0.0F);
        poseStack.popPose();
    }

    private static void spriteVertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float u, float v) {
        consumer.vertex(pose.pose(), x, y, 0.0F)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private void renderChain(BaseFlailEntity entity, Player owner, FlailComponent component, PoseStack poseStack, MultiBufferSource buffers, int packedLight, float partialTick) {
        Vec3 renderPos = entity.getPosition(partialTick);
        Vec3 ballPos = entity.getBoundingBox().getCenter();
        Vec3 chainOffset = entity.getPhase() == BaseFlailEntity.PHASE_SPIN
                ? new Vec3(0.25, 0.25, -0.2)
                : new Vec3(0.0, 0.25, -0.2);
        Vec3 handPos = HandPositionUtils.getPalmPosition(owner, partialTick, chainOffset);
        Vec3 diff = ballPos.subtract(handPos);
        double distance = diff.length();
        if (distance < 0.2) {
            return;
        }

        Vec3 direction = diff.normalize();
        if (entity.smoothedChainDir != null) {
            direction = entity.smoothedChainDir.lerp(direction, 0.35);
        }
        entity.smoothedChainDir = direction;

        ResourceLocation chainTexture = component.chainTexture() == null
                ? DEFAULT_CHAIN_TEXTURE
                : component.chainTexture();
        int chainLight = LightTexture.pack(10, LightTexture.sky(packedLight));

        poseStack.pushPose();
        Vec3 renderOffset = handPos.subtract(renderPos);
        poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);
        poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI - (float) Math.atan2(direction.z, direction.x)));
        poseStack.mulPose(Axis.XP.rotation((float) Math.acos(Mth.clamp(direction.y, -1.0, 1.0))));

        renderChainSegments(poseStack, buffers, chainTexture, chainLight, distance);
        poseStack.popPose();
    }

    private static void renderChainSegments(PoseStack poseStack, MultiBufferSource buffers, ResourceLocation texture, int packedLight, double distance) {
        float segmentLength = 1.0F;
        int fullSegments = (int) distance;
        float remainder = (float) (distance - fullSegments);

        float handEndLength = Math.min(2.0F, (float) distance);
        float geometricLength = 1.0F;
        float position = handEndLength;
        while (position > 0.001F) {
            float actualLength = Math.min(geometricLength, position);
            position -= actualLength;
            poseStack.pushPose();
            poseStack.translate(0.0, position, 0.0);
            poseStack.scale(1.0F, actualLength / segmentLength, 1.0F);
            renderChainSegment(poseStack, buffers, texture, packedLight);
            poseStack.popPose();
            geometricLength *= 0.5F;
        }
        poseStack.translate(0.0, handEndLength, 0.0);

        int middleSegments = fullSegments >= 2 ? fullSegments - 2 : 0;
        for (int i = 0; i < middleSegments; i++) {
            renderChainSegment(poseStack, buffers, texture, packedLight);
            poseStack.translate(0.0, segmentLength, 0.0);
        }

        if (remainder > 0.001F && fullSegments >= 2) {
            poseStack.pushPose();
            poseStack.scale(1.0F, remainder / segmentLength, 1.0F);
            renderChainSegment(poseStack, buffers, texture, packedLight);
            poseStack.popPose();
        }
    }

    /// 渲染一段 X 形交叉平面链条。
    private static void renderChainSegment(PoseStack poseStack, MultiBufferSource buffer, ResourceLocation texture, int packedLight) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float halfWidth = 1.5F / 16.0F;
        float u0 = 3.0F / 16.0F;
        float u1 = 6.0F / 16.0F;

        for (int plane = 0; plane < 2; plane++) {
            float angle = (float) (Math.PI / 4.0 + plane * Math.PI / 2.0);
            float x = (float) Math.cos(angle) * halfWidth;
            float z = (float) Math.sin(angle) * halfWidth;
            float normalX = (float) Math.cos(angle);
            float normalZ = (float) Math.sin(angle);

            vertex(consumer, matrix, pose, packedLight, -x, 0, -z, u0, 1, normalX, normalZ);
            vertex(consumer, matrix, pose, packedLight, x, 0, z, u1, 1, normalX, normalZ);
            vertex(consumer, matrix, pose, packedLight, x, 1, z, u1, 0, normalX, normalZ);
            vertex(consumer, matrix, pose, packedLight, -x, 1, -z, u0, 0, normalX, normalZ);

            vertex(consumer, matrix, pose, packedLight, x, 0, z, u0, 1, -normalX, -normalZ);
            vertex(consumer, matrix, pose, packedLight, -x, 0, -z, u1, 1, -normalX, -normalZ);
            vertex(consumer, matrix, pose, packedLight, -x, 1, -z, u1, 0, -normalX, -normalZ);
            vertex(consumer, matrix, pose, packedLight, x, 1, z, u0, 0, -normalX, -normalZ);
        }
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, PoseStack.Pose normal, int packedLight, float x, float y, float z, float u, float v, float normalX, float normalZ) {
        consumer.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal.normal(), normalX, 0, normalZ)
                .endVertex();
    }

    private static ResourceLocation resolveBallModel(FlailComponent component) {
        if (component.ballTexture() == null) {
            return DEFAULT_BALL_MODEL;
        }
        String path = component.ballTexture().getPath();
        int slash = path.lastIndexOf('/');
        int dot = path.lastIndexOf('.');
        if (dot <= slash) {
            return DEFAULT_BALL_MODEL;
        }
        String name = path.substring(slash + 1, dot);
        return Confluence.asResource("geo/entity/flail/" + name + ".geo.json");
    }

    private static boolean resourceExists(ResourceLocation location) {
        return Minecraft.getInstance().getResourceManager().getResource(location).isPresent();
    }

    private static final class FlailGeoModel
            extends GeoModel<BaseFlailEntity> {
        private ResourceLocation model = DEFAULT_BALL_MODEL;
        private ResourceLocation texture = DEFAULT_BALL_TEXTURE;

        @Override
        public ResourceLocation getModelResource(BaseFlailEntity animatable) {
            return model;
        }

        @Override
        public ResourceLocation getTextureResource(BaseFlailEntity animatable) {
            return texture;
        }

        @Override
        public @Nullable ResourceLocation getAnimationResource(BaseFlailEntity animatable) {
            return null;
        }
    }
}
