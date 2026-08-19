package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.boss.PrimeEnderDragon;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 本源末影龙专用渲染器。
///
/// <p>通用 GeckoLib 生物渲染器只处理水平身体朝向，而本源末影龙会沿三维速度
/// 改变俯仰角，因此这里额外插值实体俯仰，避免模型水平飞行而碰撞部件已经上下转向。
/// 激光使用服务端同步的长度绘制为四面封闭光束，既能从任意观察方向看到，也不会用
/// 半透明粒子冒充实际攻击范围。</p>
public final class PrimeEnderDragonRenderer extends BossGeoRenderer<PrimeEnderDragon> {
    private static final ResourceLocation BEAM_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");
    private static final float CORE_HALF_WIDTH = 0.35F;
    private static final float GLOW_HALF_WIDTH = 0.55F;

    public PrimeEnderDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplicitGeoModel<>(
                Confluence.asResource(
                        "geo/entity/boss/prime_ender_dragon.geo.json"),
                Confluence.asResource(
                        "textures/entity/boss/prime_ender_dragon.png"),
                Confluence.asResource(
                        "animations/entity/boss/"
                                + "prime_ender_dragon.animation.json")));
    }

    @Override
    protected void applyRotations(PrimeEnderDragon dragon, PoseStack poseStack, float ageInTicks, float bodyYaw, float partialTick) {
        super.applyRotations(dragon, poseStack, ageInTicks, bodyYaw, partialTick);
        float pitch = Mth.rotLerp(partialTick, dragon.xRotO, dragon.getXRot());
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }

    @Override
    public void render(PrimeEnderDragon dragon, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.render(dragon, entityYaw, partialTick, poseStack, buffers, packedLight);
        renderLaser(dragon, partialTick, poseStack, buffers);
    }

    /// 在实体世界坐标系中绘制光束，避免模型骨骼动画改变攻击结算起点。
    private static void renderLaser(PrimeEnderDragon dragon, float partialTick, PoseStack poseStack, MultiBufferSource buffers) {
        float range = dragon.getLaserRange();
        if (range <= 0.0F) {
            return;
        }

        double renderX = Mth.lerp(partialTick, dragon.xo, dragon.getX());
        double renderY = Mth.lerp(partialTick, dragon.yo, dragon.getY());
        double renderZ = Mth.lerp(partialTick, dragon.zo, dragon.getZ());
        Vec3 origin = dragon.getLaserOrigin(partialTick);
        float pitch = Mth.rotLerp(partialTick, dragon.xRotO, dragon.getXRot());
        float yaw = Mth.rotLerp(partialTick, dragon.yRotO, dragon.getYRot());
        Vec3 direction = Vec3.directionFromRotation(pitch, yaw);

        poseStack.pushPose();
        poseStack.translate(origin.x - renderX, origin.y - renderY, origin.z - renderZ);
        poseStack.mulPose(new Quaternionf().rotateTo(new Vector3f(0.0F, 1.0F, 0.0F), direction.toVector3f()));

        float time = dragon.tickCount + partialTick;
        float vOffset = -time * 0.03F;
        renderBeamLayer(poseStack.last(), buffers.getBuffer(RenderType.beaconBeam(BEAM_TEXTURE, false)), range, CORE_HALF_WIDTH, vOffset, 112, 54, 255, 255);
        renderBeamLayer(poseStack.last(), buffers.getBuffer(RenderType.beaconBeam(BEAM_TEXTURE, true)), range, GLOW_HALF_WIDTH, vOffset + 0.25F, 172, 102, 255, 150);
        poseStack.popPose();
    }

    /// 提交四个侧面和远端封口。每个面的顶点顺序保持朝外，继续使用渲染类型
    /// 自带的背面剔除，不会在相反方向额外绘制一层重叠面。
    private static void renderBeamLayer(PoseStack.Pose pose, VertexConsumer consumer, float length, float halfWidth, float vOffset, int red, int green, int blue, int alpha) {
        float vEnd = vOffset + length * 0.5F;
        beamQuad(pose, consumer, length, -halfWidth, -halfWidth, halfWidth, -halfWidth, 0.0F, 1.0F, vOffset, vEnd, red, green, blue, alpha);
        beamQuad(pose, consumer, length, halfWidth, -halfWidth, halfWidth, halfWidth, 0.0F, 1.0F, vOffset, vEnd, red, green, blue, alpha);
        beamQuad(pose, consumer, length, halfWidth, halfWidth, -halfWidth, halfWidth, 0.0F, 1.0F, vOffset, vEnd, red, green, blue, alpha);
        beamQuad(pose, consumer, length, -halfWidth, halfWidth, -halfWidth, -halfWidth, 0.0F, 1.0F, vOffset, vEnd, red, green, blue, alpha);

        vertex(pose, consumer, -halfWidth, length, -halfWidth, 0.0F, 0.0F, red, green, blue, alpha);
        vertex(pose, consumer, -halfWidth, length, halfWidth, 0.0F, 1.0F, red, green, blue, alpha);
        vertex(pose, consumer, halfWidth, length, halfWidth, 1.0F, 1.0F, red, green, blue, alpha);
        vertex(pose, consumer, halfWidth, length, -halfWidth, 1.0F, 0.0F, red, green, blue, alpha);
    }

    private static void beamQuad(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            float length,
            float firstX,
            float firstZ,
            float secondX,
            float secondZ,
            float minU,
            float maxU,
            float minV,
            float maxV,
            int red,
            int green,
            int blue,
            int alpha) {
        vertex(pose, consumer, firstX, 0.0F, firstZ, minU, minV, red, green, blue, alpha);
        vertex(pose, consumer, firstX, length, firstZ, minU, maxV, red, green, blue, alpha);
        vertex(pose, consumer, secondX, length, secondZ, maxU, maxV, red, green, blue, alpha);
        vertex(pose, consumer, secondX, 0.0F, secondZ, maxU, minV, red, green, blue, alpha);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, int red, int green, int blue, int alpha) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(0x00F000F0)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
