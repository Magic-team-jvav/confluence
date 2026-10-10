package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.boss.PrimeEnderDragon;

/// 本源末影龙专用渲染器。
///
/// 通用 GeckoLib 生物渲染器只处理水平身体朝向，而本源末影龙会沿三维速度
/// 改变俯仰角，因此这里额外插值实体俯仰，避免模型水平飞行而碰撞部件已经上下转向。
/// 激光使用公共三层模型，长度和宽度保持与服务端攻击范围一致。
public final class PrimeEnderDragonRenderer extends BossGeoRenderer<PrimeEnderDragon> {
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
    public boolean shouldRender(PrimeEnderDragon dragon, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        if (super.shouldRender(dragon, frustum, cameraX, cameraY, cameraZ)) return true;
        float range = dragon.getLaserRange();
        if (range <= 0.0F || !dragon.shouldRender(cameraX, cameraY, cameraZ)) return false;
        Vec3 start = dragon.getLaserOrigin(1.0F);
        Vec3 end = start.add(dragon.getViewVector(1.0F).scale(range));
        return frustum.isVisible(new AABB(start, end).inflate(PrimeEnderDragon.LASER_RADIUS));
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
        poseStack.translate(direction.x * range * 0.5, direction.y * range * 0.5, direction.z * range * 0.5);
        LaserProjectileRenderer.renderBeam(poseStack, buffers, direction, range, PrimeEnderDragon.LASER_RADIUS, 0xEEDCFF, 0xAC66FF, 0x7036FF);
        poseStack.popPose();
    }
}
