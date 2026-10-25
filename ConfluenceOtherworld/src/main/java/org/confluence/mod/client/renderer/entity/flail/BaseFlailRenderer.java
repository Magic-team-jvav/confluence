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
import org.confluence.mod.client.renderer.ColoredGeoRenderer;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.entity.flail.GuardianFlailEntity;
import org.confluence.mod.common.item.flail.BaseFlailItem.Parameters;
import org.confluence.mod.util.HandPositionUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BaseFlailRenderer extends GeoEntityRenderer<BaseFlailEntity> implements ColoredGeoRenderer<BaseFlailEntity> {
    private static final ResourceLocation DEFAULT_BALL_MODEL = Confluence.asResource("geo/entity/flail/flail.geo.json");
    private static final ResourceLocation DEFAULT_BALL_TEXTURE = Confluence.asResource("textures/entity/flail.png");
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
        ResourceLocation texture = entity.getBallTexture();
        return texture == null ? DEFAULT_BALL_TEXTURE : texture;
    }

    @Override
    public void render(BaseFlailEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        if (!(entity.getOwner() instanceof Player owner)) {
            return;
        }
        Parameters parameters = entity.parameters();
        if (parameters == null) {
            return;
        }

        ResourceLocation ballModel = resolveBallModel(parameters);
        if (resourceExists(ballModel)) {
            renderGeoHead(entity, parameters, ballModel, entityYaw, partialTick, poseStack, buffers, packedLight);
        } else {
            renderSpriteHead(entity, poseStack, buffers, packedLight);
        }

        renderChain(entity, owner, parameters, poseStack, buffers, packedLight, partialTick);
        if (entity instanceof GuardianFlailEntity guardianFlail) {
            GuardianFlailBeamRenderer.render(guardianFlail, poseStack, buffers, partialTick);
        }
    }

    private void renderGeoHead(BaseFlailEntity entity, Parameters parameters, ResourceLocation ballModel, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        FlailGeoModel model = (FlailGeoModel) getGeoModel();
        model.model = ballModel;
        model.texture = getTextureLocation(entity);

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
        spriteVertex(consumer, pose, packedLight, -1.0F, -1.0F, 0.0F, 0.0F);
        spriteVertex(consumer, pose, packedLight, 1.0F, -1.0F, 1.0F, 0.0F);
        spriteVertex(consumer, pose, packedLight, 1.0F, 1.0F, 1.0F, 1.0F);
        spriteVertex(consumer, pose, packedLight, -1.0F, 1.0F, 0.0F, 1.0F);
        poseStack.popPose();
    }

    private static void spriteVertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float u, float v) {
        consumer.addVertex(pose.pose(), x, y, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private void renderChain(BaseFlailEntity entity, Player owner, Parameters parameters, PoseStack poseStack, MultiBufferSource buffers, int packedLight, float partialTick) {
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

        ResourceLocation chainTexture = parameters.chainTexture() == null
                ? DEFAULT_CHAIN_TEXTURE
                : parameters.chainTexture();
        int chainLight = LightTexture.pack(10, LightTexture.sky(packedLight));

        poseStack.pushPose();
        Vec3 renderOffset = handPos.subtract(renderPos);
        poseStack.translate(renderOffset.x, renderOffset.y, renderOffset.z);
        poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI - (float) Math.atan2(direction.z, direction.x)));
        poseStack.mulPose(Axis.XP.rotation((float) Math.acos(Mth.clamp(direction.y, -1.0, 1.0))));

        renderChainSegments(poseStack, buffers, chainTexture, chainLight, distance, entity.isBurningFlail());
        poseStack.popPose();
    }

    private static void renderChainSegments(PoseStack poseStack, MultiBufferSource buffers, ResourceLocation texture, int packedLight, double distance, boolean heated) {
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
            renderChainSegment(poseStack, buffers, texture, packedLight, position, position + actualLength, distance, heated);
            poseStack.popPose();
            geometricLength *= 0.5F;
        }
        poseStack.translate(0.0, handEndLength, 0.0);

        int middleSegments = fullSegments >= 2 ? fullSegments - 2 : 0;
        for (int i = 0; i < middleSegments; i++) {
            float start = handEndLength + i * segmentLength;
            renderChainSegment(poseStack, buffers, texture, packedLight, start, start + segmentLength, distance, heated);
            poseStack.translate(0.0, segmentLength, 0.0);
        }

        if (remainder > 0.001F && fullSegments >= 2) {
            poseStack.pushPose();
            poseStack.scale(1.0F, remainder / segmentLength, 1.0F);
            renderChainSegment(poseStack, buffers, texture, packedLight, handEndLength + middleSegments, (float) distance, distance, heated);
            poseStack.popPose();
        }
    }

    /// 渲染一段 X 形交叉平面链条；正反面通过背面剔除保证每侧只绘制一次。
    private static void renderChainSegment(PoseStack poseStack, MultiBufferSource buffer, ResourceLocation texture, int packedLight, float start, float end, double distance, boolean heated) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        float halfWidth = 1.5F / 16.0F;
        float u0 = 3.0F / 16.0F;
        float u1 = 6.0F / 16.0F;

        for (int pass = 0; pass < (heated ? 2 : 1); pass++) {
            boolean glow = pass == 1;
            // 加法混合的自发光层只覆盖受热部分，保留手柄端原有的环境光和铁色。
            VertexConsumer consumer = buffer.getBuffer(glow ? RenderType.eyes(texture) : RenderType.entityCutout(texture));
            for (int plane = 0; plane < 2; plane++) {
                float angle = (float) (Math.PI / 4.0 + plane * Math.PI / 2.0);
                float x = (float) Math.cos(angle) * halfWidth;
                float z = (float) Math.sin(angle) * halfWidth;
                float normalX = -(float) Math.sin(angle);
                float normalZ = (float) Math.cos(angle);

                // 细分受热链条，短距离挥舞时也能完整显示各个温度色阶；UV 保持连续。
                int slices = heated ? Math.max(1, Mth.ceil((end - start) / 0.2F)) : 1;
                for (int slice = 0; slice < slices; slice++) {
                    float bottom = (float) slice / slices;
                    float top = (float) (slice + 1) / slices;
                    float startHeat = heated ? chainHeat(Mth.lerp(bottom, start, end), distance) : 0.0F;
                    float endHeat = heated ? chainHeat(Mth.lerp(top, start, end), distance) : 0.0F;
                    int startColor = chainVertexColor(startHeat, glow);
                    int endColor = chainVertexColor(endHeat, glow);
                    int startLight = glow ? LightTexture.FULL_BRIGHT : heatedChainLight(packedLight, startHeat);
                    int endLight = glow ? LightTexture.FULL_BRIGHT : heatedChainLight(packedLight, endHeat);

                    vertex(consumer, matrix, pose, startLight, startColor, -x, bottom, -z, u0, bottom, normalX, normalZ);
                    vertex(consumer, matrix, pose, startLight, startColor, x, bottom, z, u1, bottom, normalX, normalZ);
                    vertex(consumer, matrix, pose, endLight, endColor, x, top, z, u1, top, normalX, normalZ);
                    vertex(consumer, matrix, pose, endLight, endColor, -x, top, -z, u0, top, normalX, normalZ);

                    vertex(consumer, matrix, pose, startLight, startColor, x, bottom, z, u1, bottom, -normalX, -normalZ);
                    vertex(consumer, matrix, pose, startLight, startColor, -x, bottom, -z, u0, bottom, -normalX, -normalZ);
                    vertex(consumer, matrix, pose, endLight, endColor, -x, top, -z, u0, top, -normalX, -normalZ);
                    vertex(consumer, matrix, pose, endLight, endColor, x, top, z, u1, top, -normalX, -normalZ);
                }
            }
        }
    }

    /// 手柄端保留铁色，沿链条向球头依次过渡到饱和橙红、熔橙和金橙。
    private static float chainHeat(float position, double distance) {
        float heat = Mth.clamp((float) (position / distance), 0.0F, 1.0F);
        return heat * heat * (3.0F - 2.0F * heat);
    }

    private static int chainColor(float heat) {
        if (heat < 0.35F) {
            return lerpChainColor(heat / 0.35F, 0xFFFFFF, 0xFF4A10);
        }
        if (heat < 0.7F) {
            return lerpChainColor((heat - 0.35F) / 0.35F, 0xFF4A10, 0xFF8612);
        }
        return lerpChainColor((heat - 0.7F) / 0.3F, 0xFF8612, 0xFFC13D);
    }

    private static int chainGlowAlpha(float heat) {
        float glow = Mth.clamp((heat - 0.12F) / 0.88F, 0.0F, 1.0F);
        // 限制加法叠亮，保留金橙色高光，避免高温端被冲成黄白。
        return Math.round(112.0F * glow * glow * (3.0F - 2.0F * glow));
    }

    private static int chainVertexColor(float heat, boolean glow) {
        int color = chainColor(heat);
        if (!glow) return color | 0xFF000000;
        int alpha = chainGlowAlpha(heat);
        // eyes 使用 ONE/ONE 混合，须把亮度乘进 RGB，才能平滑淡出发光层。
        int red = ((color >> 16) & 255) * alpha / 255;
        int green = ((color >> 8) & 255) * alpha / 255;
        int blue = (color & 255) * alpha / 255;
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private static int lerpChainColor(float amount, int from, int to) {
        int red = Math.round(Mth.lerp(amount, (from >> 16) & 255, (to >> 16) & 255));
        int green = Math.round(Mth.lerp(amount, (from >> 8) & 255, (to >> 8) & 255));
        int blue = Math.round(Mth.lerp(amount, from & 255, to & 255));
        return (red << 16) | (green << 8) | blue;
    }

    private static int heatedChainLight(int packedLight, float heat) {
        if (heat <= 0.0F) return packedLight;
        int blockLight = Math.round(Mth.lerp(heat, LightTexture.block(packedLight), 15));
        int skyLight = Math.round(Mth.lerp(heat, LightTexture.sky(packedLight), 15));
        return LightTexture.pack(blockLight, skyLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, PoseStack.Pose normal, int packedLight, int color, float x, float y, float z, float u, float v, float normalX, float normalZ) {
        consumer.addVertex(matrix, x, y, z)
                .setColor((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >>> 24) & 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(normal, normalX, 0, normalZ);
    }

    private static ResourceLocation resolveBallModel(Parameters parameters) {
        if (parameters.ballTexture() == null) {
            return DEFAULT_BALL_MODEL;
        }
        String path = parameters.ballTexture().getPath();
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
