package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.boss.Skeletron;
import org.confluence.mod.common.entity.boss.SkeletronHand;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;

import java.util.Map;
import java.util.WeakHashMap;

/// 以手掌实体为末端、以头部侧面为根点绘制骷髅王手臂。
///
/// 模型中的三组网格使用绝对枢轴，网格表面并不恰好落在枢轴上。渲染器先在世界空间
/// 解出手肘，再按网格的真实表面边界连接掌部、前臂和上臂，避免在腕部和肘部留下缝隙。
public class SkeletronBossHandRenderer extends BossGeoRenderer<SkeletronHand> {
    // 世界空间的肘部解算使用 Y 轴为稳定的参考上方向。
    private static final Vec3 WORLD_UP = new Vec3(0.0, 1.0, 0.0);
    // 小于该平方长度的向量视为退化，防止归一化产生 NaN。
    private static final double EPSILON = 1.0E-7;
    // 无法读取烘焙网格时使用的掌至肘、肘至肩长度，模型像素已换算为方块。
    private static final double FALLBACK_DISTAL_LENGTH = 85.0 / 16.0;
    private static final double FALLBACK_UPPER_ARM_LENGTH = 62.0 / 16.0;
    // 目标贴近肩部时允许手臂压缩，但不会缩到原模型长度的 58% 以下。
    private static final double MINIMUM_CLOSE_SCALE = 0.58;
    // 掌部几乎与肩部重合时沿用上一帧方向，阈值单位为方块。
    private static final double NEAR_ZERO_DIRECTION_RADIUS = 0.25;
    // 肘部保留轻微网格交叠避免漏缝，腕部反向留出可见间隙；单位为方块。
    private static final double JOINT_OVERLAP = 0.22;
    private static final double WRIST_GAP = 0.125;
    // 在上一版已有的 15° 外偏基础上再增加 15°，总外偏为 30°。
    private static final double ELBOW_OUTWARD_ANGLE = Math.toRadians(30.0D);

    // 这些值只在模型没有可读顶点时使用；正常渲染会直接从烘焙网格求边界。
    private static final SurfaceRange FALLBACK_PALM_SURFACE =
            new SurfaceRange(-18.686 / 16.0, 13.170 / 16.0);
    private static final SurfaceRange FALLBACK_FOREARM_SURFACE =
            new SurfaceRange(21.670 / 16.0, 78.080 / 16.0);
    private static final SurfaceRange FALLBACK_UPPER_ARM_SURFACE =
            new SurfaceRange(92.670 / 16.0, 149.080 / 16.0);

    private final Map<SkeletronHand, Vec3> previousReachDirections = new WeakHashMap<>();

    public SkeletronBossHandRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new ExplicitGeoModel<>(
                        Confluence.asResource("geo/entity/boss/skeletron.geo.json"),
                        Confluence.asResource("textures/entity/boss/skeletron.png"),
                        null),
                false,
                1.0F,
                0.0F);
        this.shadowRadius = 0.5F;
    }

    /// 各部位已使用世界空间坐标，不能再次叠加实体旋转。
    @Override
    protected void applyRotations(
            SkeletronHand hand,
            PoseStack poseStack,
            float ageInTicks,
            float rotationYaw,
            float partialTick) {}

    @Override
    public void renderRecursively(
            PoseStack poseStack,
            SkeletronHand hand,
            GeoBone bone,
            RenderType renderType,
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
        if (!bone.getName().equals("bone3")) {
            return;
        }

        GeoBone palm = findDirectChild(bone, "hand");
        GeoBone forearm = findDirectChild(bone, "arm2");
        GeoBone upperArm = forearm == null ? null : findDirectChild(forearm, "arm1");
        if (palm == null) {
            return;
        }

        Vec3 entityPosition = hand.getPosition(partialTick);
        Vec3 palmPosition = entityPosition.add(0.0, palm.getPivotY() / 16.0, 0.0);
        // 模型手掌与实体左右槽位相反；右侧实体需要镜像，交换后拇指才位于身体外侧。
        boolean mirrorPalm = hand.getHandIndex() == 1;
        if (forearm == null || upperArm == null) {
            Basis basis = makeBasis(new Vec3(1.0, 0.0, 0.0), WORLD_UP);
            renderBoneAtPivot(
                    poseStack,
                    palm,
                    palmPosition.subtract(entityPosition),
                    basis,
                    mirrorPalm,
                    packedLight,
                    packedOverlay,
                    red,
                    green,
                    blue,
                    alpha,
                    buffer);
            return;
        }

        Skeletron owner = hand.getOwner();
        if (owner == null) {
            Basis basis = makeBasis(new Vec3(1.0, 0.0, 0.0), WORLD_UP);
            renderBoneAtPivot(
                    poseStack,
                    palm,
                    palmPosition.subtract(entityPosition),
                    basis,
                    mirrorPalm,
                    packedLight,
                    packedOverlay,
                    red,
                    green,
                    blue,
                    alpha,
                    buffer);
            return;
        }

        Vec3 shoulderPosition =
                hand.getRootPosition(partialTick).add(0.0, bone.getPivotY() / 16.0, 0.0);
        Vec3 palmToShoulder = shoulderPosition.subtract(palmPosition);
        double jointDistance = palmToShoulder.length();

        Vec3 ownerFront = horizontalFacing(owner.getFacingYaw(partialTick));
        Vec3 ownerSide = shoulderPosition.subtract(owner.getPosition(partialTick));
        ownerSide = new Vec3(ownerSide.x, 0.0, ownerSide.z);
        if (ownerSide.lengthSqr() <= EPSILON) {
            ownerSide = new Vec3(ownerFront.z, 0.0, -ownerFront.x);
        } else {
            ownerSide = ownerSide.normalize();
        }

        Vec3 reachDirection = resolveReachDirection(hand, palmToShoulder, ownerFront);
        // 向后为主、向外和向上为辅。即便整条手臂接近竖直，投影后仍保留稳定的水平极向量。
        Vec3 preferredPole =
                ownerFront.scale(-Math.cos(ELBOW_OUTWARD_ANGLE))
                        .add(ownerSide.scale(Math.sin(ELBOW_OUTWARD_ANGLE)))
                        .add(WORLD_UP.scale(0.08));
        Vec3 poleDirection = stablePole(preferredPole, ownerSide, reachDirection);

        double distalLength = Math.abs(upperArm.getPivotX() - palm.getPivotX()) / 16.0;
        if (!Double.isFinite(distalLength) || distalLength <= EPSILON) {
            distalLength = FALLBACK_DISTAL_LENGTH;
        }
        double upperArmLength = Math.abs(bone.getPivotX() - upperArm.getPivotX()) / 16.0;
        if (!Double.isFinite(upperArmLength) || upperArmLength <= EPSILON) {
            upperArmLength = FALLBACK_UPPER_ARM_LENGTH;
        }

        double maximumReach = distalLength + upperArmLength;
        double minimumReach = Math.abs(distalLength - upperArmLength);
        double solvedDistalLength = distalLength;
        double solvedUpperArmLength = upperArmLength;
        if (jointDistance >= maximumReach) {
            double stretch = jointDistance / maximumReach;
            solvedDistalLength *= stretch;
            solvedUpperArmLength *= stretch;
        } else if (jointDistance < minimumReach && minimumReach > EPSILON) {
            // 极近目标使用两条仍然可见的腰构成折叠三角形，不能把手臂压缩成几个像素。
            double commonLength = Math.max(distalLength, upperArmLength) * MINIMUM_CLOSE_SCALE;
            double ratio = Mth.clamp(jointDistance / minimumReach, 0.0, 1.0);
            double blend = ratio * ratio;
            solvedDistalLength = Mth.lerp(blend, commonLength, distalLength);
            solvedUpperArmLength = Mth.lerp(blend, commonLength, upperArmLength);
        }

        Vec3 elbowPosition;
        if (jointDistance <= EPSILON) {
            elbowPosition = palmPosition.add(poleDirection.scale(solvedDistalLength));
        } else {
            double along =
                    (solvedDistalLength * solvedDistalLength
                            + jointDistance * jointDistance
                            - solvedUpperArmLength * solvedUpperArmLength)
                            / (2.0 * jointDistance);
            double height =
                    Math.sqrt(Math.max(0.0, solvedDistalLength * solvedDistalLength - along * along));
            elbowPosition =
                    palmPosition.add(reachDirection.scale(along)).add(poleDirection.scale(height));
        }

        Vec3 distalDirection = elbowPosition.subtract(palmPosition);
        if (distalDirection.lengthSqr() <= EPSILON) {
            distalDirection = reachDirection;
        } else {
            distalDirection = distalDirection.normalize();
        }
        Vec3 upperDirection = shoulderPosition.subtract(elbowPosition);
        if (upperDirection.lengthSqr() <= EPSILON) {
            upperDirection = reachDirection;
        } else {
            upperDirection = upperDirection.normalize();
        }

        Vec3 bendNormal = reachDirection.cross(poleDirection).normalize();
        Basis distalBasis = makeBendBasis(distalDirection, bendNormal);
        Basis upperBasis = makeBendBasis(upperDirection, bendNormal);
        // 用户确认滚动状态的掌面姿态正确；普通悬浮/拍击状态需要沿腕轴翻转半圈。
        // 只翻手掌坐标系，不改变前臂、肘部解算或左右镜像关系。
        Basis palmBasis = owner.isSpinning() ? distalBasis : halfTurnAroundArm(distalBasis);
        SurfaceRange palmSurface = surfaceRange(palm, FALLBACK_PALM_SURFACE);
        SurfaceRange forearmSurface = surfaceRange(forearm, FALLBACK_FOREARM_SURFACE);
        SurfaceRange upperArmSurface = surfaceRange(upperArm, FALLBACK_UPPER_ARM_SURFACE);

        double palmForwardExtent = palmSurface.maximumX - palm.getPivotX() / 16.0;
        Vec3 palmSeam = palmPosition.add(distalDirection.scale(Math.max(0.0, palmForwardExtent)));
        Vec3 localPalm = palmPosition.subtract(entityPosition);
        Vec3 localPalmSeam =
                palmSeam.add(distalDirection.scale(WRIST_GAP)).subtract(entityPosition);
        Vec3 localDistalElbow =
                elbowPosition.add(distalDirection.scale(JOINT_OVERLAP)).subtract(entityPosition);
        Vec3 localUpperElbow =
                elbowPosition.subtract(upperDirection.scale(JOINT_OVERLAP)).subtract(entityPosition);
        Vec3 localShoulder =
                shoulderPosition.add(upperDirection.scale(JOINT_OVERLAP)).subtract(entityPosition);

        // 手掌与前臂共用同一末端坐标系，腕部不会因各自选取“上方向”而发生滚转错位。
        renderBoneAtPivot(
                poseStack,
                palm,
                localPalm,
                palmBasis,
                mirrorPalm,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha,
                buffer);
        renderBoneBetweenSurfaces(
                poseStack,
                forearm,
                localPalmSeam,
                localDistalElbow,
                forearmSurface,
                distalBasis,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha,
                buffer);
        renderBoneBetweenSurfaces(
                poseStack,
                upperArm,
                localUpperElbow,
                localShoulder,
                upperArmSurface,
                upperBasis,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha,
                buffer);
    }

    private void renderBoneAtPivot(
            PoseStack poseStack,
            GeoBone bone,
            Vec3 anchor,
            Basis basis,
            boolean mirrorDepth,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha,
            VertexConsumer buffer) {
        poseStack.pushPose();
        poseStack.translate(anchor.x, anchor.y, anchor.z);
        poseStack.mulPose(rotationFromBasis(basis));
        poseStack.scale(1.0F, 1.0F, 1.0F);
        if (mirrorDepth) {
            reflectDepth(poseStack);
        }
        poseStack.translate(
                -bone.getPivotX() / 16.0F, -bone.getPivotY() / 16.0F, -bone.getPivotZ() / 16.0F);
        renderBoneCubes(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    private void renderBoneBetweenSurfaces(
            PoseStack poseStack,
            GeoBone bone,
            Vec3 start,
            Vec3 end,
            SurfaceRange surface,
            Basis basis,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha,
            VertexConsumer buffer) {
        double modelLength = surface.maximumX - surface.minimumX;
        double renderedLength = start.distanceTo(end);
        if (modelLength <= EPSILON || renderedLength <= EPSILON) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);
        poseStack.mulPose(rotationFromBasis(basis));
        poseStack.scale((float) (renderedLength / modelLength), 1.0F, 1.0F);
        poseStack.translate(-surface.minimumX, -bone.getPivotY() / 16.0F, -bone.getPivotZ() / 16.0F);
        renderBoneCubes(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    private void renderBoneCubes(
            PoseStack poseStack,
            GeoBone bone,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        for (GeoCube cube : bone.getCubes()) {
            poseStack.pushPose();
            renderCube(poseStack, cube, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }

    /// 镜像位置和法线矩阵，但不把负值交给 PoseStack 的非等比缩放分支。
    private static void reflectDepth(PoseStack poseStack) {
        poseStack.last().pose().scale(1.0F, 1.0F, -1.0F);
        poseStack.last().normal().scale(1.0F, 1.0F, -1.0F);
    }

    private static Basis makeBasis(Vec3 xAxis, Vec3 poleDirection) {
        Vec3 x = xAxis.lengthSqr() <= EPSILON ? new Vec3(1.0, 0.0, 0.0) : xAxis.normalize();
        Vec3 y = reject(poleDirection, x);
        if (y.lengthSqr() <= EPSILON) {
            y = stablePole(WORLD_UP, new Vec3(0.0, 0.0, 1.0), x);
        } else {
            y = y.normalize();
        }
        Vec3 z = x.cross(y);
        if (z.lengthSqr() <= EPSILON) {
            z = x.cross(new Vec3(0.0, 0.0, 1.0));
        }
        z = z.normalize();
        y = z.cross(x).normalize();
        return new Basis(x, y, z);
    }

    private static Basis makeBendBasis(Vec3 xAxis, Vec3 bendNormal) {
        Vec3 x = xAxis.lengthSqr() <= EPSILON ? new Vec3(1.0, 0.0, 0.0) : xAxis.normalize();
        Vec3 z = reject(bendNormal, x);
        if (z.lengthSqr() <= EPSILON) {
            z = x.cross(stablePole(WORLD_UP, new Vec3(0.0, 0.0, 1.0), x));
        }
        z = z.normalize();
        Vec3 y = z.cross(x).normalize();
        return new Basis(x, y, z);
    }

    private static Basis halfTurnAroundArm(Basis basis) {
        return new Basis(basis.x, basis.y.scale(-1.0D), basis.z.scale(-1.0D));
    }

    private Vec3 resolveReachDirection(SkeletronHand hand, Vec3 displacement, Vec3 fallback) {
        double distance = displacement.length();
        Vec3 previous = previousReachDirections.get(hand);
        if (distance <= EPSILON) {
            Vec3 resolved = previous == null ? fallback.normalize() : previous;
            previousReachDirections.put(hand, resolved);
            return resolved;
        }

        Vec3 current = displacement.scale(1.0 / distance);
        if (previous != null && distance < NEAR_ZERO_DIRECTION_RADIUS) {
            double blend = Mth.clamp(distance / NEAR_ZERO_DIRECTION_RADIUS, 0.0, 1.0);
            current = slerpDirection(previous, current, blend, fallback);
        }
        previousReachDirections.put(hand, current);
        return current;
    }

    private static Vec3 slerpDirection(Vec3 from, Vec3 to, double progress, Vec3 fallbackPole) {
        double dot = Mth.clamp(from.dot(to), -1.0, 1.0);
        if (dot > 0.9995) {
            return from.scale(1.0 - progress).add(to.scale(progress)).normalize();
        }
        if (dot < -0.9995) {
            Vec3 tangent = stablePole(fallbackPole, WORLD_UP, from);
            double angle = Math.PI * progress;
            return from.scale(Math.cos(angle)).add(tangent.scale(Math.sin(angle))).normalize();
        }

        double angle = Math.acos(dot);
        double inverseSine = 1.0 / Math.sin(angle);
        double fromWeight = Math.sin((1.0 - progress) * angle) * inverseSine;
        double toWeight = Math.sin(progress * angle) * inverseSine;
        return from.scale(fromWeight).add(to.scale(toWeight)).normalize();
    }

    private static Vec3 stablePole(Vec3 preferred, Vec3 secondary, Vec3 reachDirection) {
        Vec3 pole = reject(preferred, reachDirection);
        if (pole.lengthSqr() <= 1.0E-5) {
            pole = reject(secondary, reachDirection);
        }
        if (pole.lengthSqr() <= EPSILON) {
            Vec3 fallback = Math.abs(reachDirection.y) < 0.9 ? WORLD_UP : new Vec3(0.0, 0.0, 1.0);
            pole = reject(fallback, reachDirection);
        }
        if (pole.lengthSqr() <= EPSILON) {
            pole = new Vec3(1.0, 0.0, 0.0);
        }
        return pole.normalize();
    }

    /// 求立方体自身旋转后的真实 X 表面边界；顶点坐标已经包含膨胀和镜像结果。
    private static SurfaceRange surfaceRange(GeoBone bone, SurfaceRange fallback) {
        double minimumX = Double.POSITIVE_INFINITY;
        double maximumX = Double.NEGATIVE_INFINITY;
        for (GeoCube cube : bone.getCubes()) {
            double pivotX = cube.pivot().x / 16.0;
            double pivotY = cube.pivot().y / 16.0;
            double pivotZ = cube.pivot().z / 16.0;
            Quaternionf rotation =
                    new Quaternionf()
                            .rotationZ((float) cube.rotation().z)
                            .rotateY((float) cube.rotation().y)
                            .rotateX((float) cube.rotation().x);
            for (GeoQuad quad : cube.quads()) {
                if (quad == null) {
                    continue;
                }
                for (var vertex : quad.vertices()) {
                    Vector3f point =
                            new Vector3f(vertex.position()).sub((float) pivotX, (float) pivotY, (float) pivotZ);
                    rotation.transform(point);
                    point.add((float) pivotX, (float) pivotY, (float) pivotZ);
                    minimumX = Math.min(minimumX, point.x);
                    maximumX = Math.max(maximumX, point.x);
                }
            }
    }
        return Double.isFinite(minimumX) && Double.isFinite(maximumX) && maximumX - minimumX > EPSILON
                ? new SurfaceRange(minimumX, maximumX)
                : fallback;
    }

    private static Quaternionf rotationFromBasis(Basis basis) {
        Matrix3f matrix = new Matrix3f();
        matrix.setColumn(0, new Vector3f((float) basis.x.x, (float) basis.x.y, (float) basis.x.z));
        matrix.setColumn(1, new Vector3f((float) basis.y.x, (float) basis.y.y, (float) basis.y.z));
        matrix.setColumn(2, new Vector3f((float) basis.z.x, (float) basis.z.y, (float) basis.z.z));
        return new Quaternionf().setFromNormalized(matrix);
    }

    private static GeoBone findDirectChild(GeoBone parent, String name) {
        for (GeoBone child : parent.getChildBones()) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    private static Vec3 reject(Vec3 vector, Vec3 axis) {
        return vector.subtract(axis.scale(vector.dot(axis)));
    }

    private static Vec3 horizontalFacing(float yawDegrees) {
        float yaw = yawDegrees * Mth.DEG_TO_RAD;
        return new Vec3(-Mth.sin(yaw), 0.0, Mth.cos(yaw));
    }

    private record Basis(Vec3 x, Vec3 y, Vec3 z) {}

    private record SurfaceRange(double minimumX, double maximumX) {}
}
