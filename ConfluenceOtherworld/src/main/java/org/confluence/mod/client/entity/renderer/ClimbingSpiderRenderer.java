package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.entity.model.SpiderSetModel;
import org.confluence.mod.common.entity.monster.ClimbingSpider;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.WeakHashMap;

/// 蜘蛛腹面贴墙；墙面切换、前进方向和贴面位移使用同一时间尺度连续过渡。
public final class ClimbingSpiderRenderer extends GeoNormalRenderer<ClimbingSpider> {
    private static final double POSE_RESPONSE_TICKS = 3.0;
    private final Map<ClimbingSpider, SurfacePose> surfacePoses = new WeakHashMap<>();

    public ClimbingSpiderRenderer(EntityRendererProvider.Context context, String texturePath) {
        super(context, new SpiderSetModel(texturePath));
    }

    @Override
    protected void applyRotations(ClimbingSpider spider, PoseStack poses, float age, float yaw, float partialTick) {
        Direction face = spider.getAttachmentFace();
        boolean onWall = face.getAxis().isHorizontal();
        SurfacePose pose = surfacePoses.computeIfAbsent(spider, entity -> new SurfacePose());
        Quaternionf rotation = switch (face) {
            case NORTH -> Axis.XN.rotationDegrees(90);
            case SOUTH -> Axis.XP.rotationDegrees(90);
            case WEST -> Axis.ZP.rotationDegrees(90);
            case EAST -> Axis.ZN.rotationDegrees(90);
            default -> new Quaternionf();
        };
        float localYaw = yaw;
        if (onWall) {
            Vec3 movement = new Vec3(spider.getX() - spider.xo, spider.getY() - spider.yo, spider.getZ() - spider.zo);
            Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());
            Vec3 tangent = movement.subtract(normal.scale(movement.dot(normal)));
            if (tangent.lengthSqr() < 1.0E-5)
                tangent = pose.forward.subtract(normal.scale(pose.forward.dot(normal)));
            if (tangent.lengthSqr() < 1.0E-5) tangent = new Vec3(0, 1, 0);
            pose.forward = tangent.normalize();
            Vector3f forward = rotation.conjugate(new Quaternionf()).transform(pose.forward.toVector3f());
            localYaw = (float) (Mth.atan2(-forward.x, forward.z) * Mth.RAD_TO_DEG);
        } else {
            pose.forward = Vec3.directionFromRotation(0, yaw);
        }
        Quaternionf target = rotation.rotateY((180.0F - localYaw) * Mth.DEG_TO_RAD);
        float scale = spider.getScale();
        float pivot = spider.getBbHeight() * 0.5F / scale;
        float gap = onWall ? (spider.getBbWidth() - spider.getBbHeight()) * 0.5F / scale : 0;
        Vector3f offset = new Vector3f(-face.getStepX() * gap, 0, -face.getStepZ() * gap);
        double time = spider.tickCount + (double) partialTick;
        if (!pose.initialized) {
            pose.rotation.set(target);
            pose.offset.set(offset);
            pose.time = time;
            pose.initialized = true;
        }
        double elapsed = Math.max(0, Math.min(1, time - pose.time));
        float blend = (float) -Math.expm1(-elapsed / POSE_RESPONSE_TICKS);
        pose.rotation.slerp(target, blend).normalize();
        pose.offset.lerp(offset, blend);
        pose.time = time;

        Quaternionf correction = new Quaternionf(pose.rotation).rotateY(-(180.0F - yaw) * Mth.DEG_TO_RAD);
        poses.translate(pose.offset.x, pivot, pose.offset.z);
        poses.mulPose(correction);
        poses.translate(0, -pivot, 0);
        super.applyRotations(spider, poses, age, yaw, partialTick);
    }

    private static final class SurfacePose {
        private final Quaternionf rotation = new Quaternionf();
        private final Vector3f offset = new Vector3f();
        private Vec3 forward = new Vec3(0, 1, 0);
        private double time;
        private boolean initialized;
    }
}
