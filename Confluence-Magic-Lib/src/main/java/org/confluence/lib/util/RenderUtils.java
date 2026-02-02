package org.confluence.lib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import static org.confluence.lib.client.render.visual_effects.VisualEffects.getCamera;

public class RenderUtils {
    public static Vector3d toVector3d(Vec3 vec3) {
        return new Vector3d(vec3.x, vec3.y, vec3.z);
    }

    public static boolean calculateNormal(Vector3d p0, Vector3d p1, Vector3d p2, Vector3d cameraPos) {

        Vector3d edge1 = new Vector3d(p1).sub(p0);
        Vector3d edge2 = new Vector3d(p2).sub(p0);

        Vector3d normal = new Vector3d(edge1).cross(edge2);

        normal.normalize();

        Vector3d cameraV = new Vector3d(cameraPos.x - p0.x, cameraPos.y - p0.y, cameraPos.z - p0.z);

        cameraV.normalize();

        double cosCamera = (cameraV.x * normal.x + cameraV.y * normal.y + cameraV.z * normal.z) / (cameraV.length() * normal.length());

        return (cosCamera > 0);
    }

    public static void drawCube(PoseStack poseStack, MultiBufferSource bufferSource, double ballSide, int red, int green, int blue, int alpha, Vector3d entityPos, Vector3d pos0, boolean face, double rotate0, double rotate1) {

        PoseStack.Pose pose = poseStack.last();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());

        double ballSide1 = ballSide * Math.sqrt(2);

        double x0_y = ballSide1 * Math.cos(rotate0) / 2;
        double x1_y = -ballSide1 * Math.sin(rotate0) / 2;
        double x2_y = -x0_y;
        double x3_y = -x1_y;
        double z0_y = ballSide1 * Math.sin(rotate0) / 2;
        double z1_y = ballSide1 * Math.cos(rotate0) / 2;
        double z2_y = -z0_y;
        double z3_y = -z1_y;

        Vector3d point0_y = new Vector3d(x0_y, ballSide / 2, z0_y);
        Vector3d point1_y = new Vector3d(x1_y, ballSide / 2, z1_y);
        Vector3d point2_y = new Vector3d(x2_y, ballSide / 2, z2_y);
        Vector3d point3_y = new Vector3d(x3_y, ballSide / 2, z3_y);

        double cosZ = Math.cos(rotate1);
        double sinZ = Math.sin(rotate1);

        Vector3d ballPoint0 = new Vector3d(
                point0_y.x * cosZ - point0_y.y * sinZ,
                point0_y.x * sinZ + point0_y.y * cosZ,
                point0_y.z
        );
        Vector3d ballPoint1 = new Vector3d(
                point1_y.x * cosZ - point1_y.y * sinZ,
                point1_y.x * sinZ + point1_y.y * cosZ,
                point1_y.z
        );
        Vector3d ballPoint2 = new Vector3d(
                point2_y.x * cosZ - point2_y.y * sinZ,
                point2_y.x * sinZ + point2_y.y * cosZ,
                point2_y.z
        );
        Vector3d ballPoint3 = new Vector3d(
                point3_y.x * cosZ - point3_y.y * sinZ,
                point3_y.x * sinZ + point3_y.y * cosZ,
                point3_y.z
        );
        Vector3d cameraPos = toVector3d(getCamera().getPosition());
        cameraPos = new Vector3d(cameraPos.x - entityPos.x, cameraPos.y - entityPos.y, cameraPos.z - entityPos.z);

        poseStack.pushPose();
        if (face || calculateNormal(
                new Vector3d(ballPoint0).add(pos0),
                new Vector3d(ballPoint2).negate().add(pos0),
                new Vector3d(ballPoint3).negate().add(pos0),
                cameraPos)) {
            consumer.addVertex(pose, (float) (ballPoint0.x + pos0.x), (float) (ballPoint0.y + pos0.y), (float) (ballPoint0.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint2.x + pos0.x), (float) (-ballPoint2.y + pos0.y), (float) (-ballPoint2.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint3.x + pos0.x), (float) (-ballPoint3.y + pos0.y), (float) (-ballPoint3.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (ballPoint1.x + pos0.x), (float) (ballPoint1.y + pos0.y), (float) (ballPoint1.z + pos0.z))
                    .setColor(red, green, blue, alpha);
        }
        if (face || calculateNormal(
                new Vector3d(ballPoint1).add(pos0),
                new Vector3d(ballPoint3).negate().add(pos0),
                new Vector3d(ballPoint0).negate().add(pos0),
                cameraPos)) {
            consumer.addVertex(pose, (float) (ballPoint1.x + pos0.x), (float) (ballPoint1.y + pos0.y), (float) (ballPoint1.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint3.x + pos0.x), (float) (-ballPoint3.y + pos0.y), (float) (-ballPoint3.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint0.x + pos0.x), (float) (-ballPoint0.y + pos0.y), (float) (-ballPoint0.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (ballPoint2.x + pos0.x), (float) (ballPoint2.y + pos0.y), (float) (ballPoint2.z + pos0.z))
                    .setColor(red, green, blue, alpha);
        }
        if (face || calculateNormal(
                new Vector3d(ballPoint2).add(pos0),
                new Vector3d(ballPoint0).negate().add(pos0),
                new Vector3d(ballPoint1).negate().add(pos0),
                cameraPos)) {
            consumer.addVertex(pose, (float) (ballPoint2.x + pos0.x), (float) (ballPoint2.y + pos0.y), (float) (ballPoint2.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint0.x + pos0.x), (float) (-ballPoint0.y + pos0.y), (float) (-ballPoint0.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint1.x + pos0.x), (float) (-ballPoint1.y + pos0.y), (float) (-ballPoint1.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (ballPoint3.x + pos0.x), (float) (ballPoint3.y + pos0.y), (float) (ballPoint3.z + pos0.z))
                    .setColor(red, green, blue, alpha);
        }
        if (face || calculateNormal(
                new Vector3d(ballPoint3).add(pos0),
                new Vector3d(ballPoint1).negate().add(pos0),
                new Vector3d(ballPoint2).negate().add(pos0),
                cameraPos)) {
            consumer.addVertex(pose, (float) (ballPoint3.x + pos0.x), (float) (ballPoint3.y + pos0.y), (float) (ballPoint3.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint1.x + pos0.x), (float) (-ballPoint1.y + pos0.y), (float) (-ballPoint1.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint2.x + pos0.x), (float) (-ballPoint2.y + pos0.y), (float) (-ballPoint2.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (ballPoint0.x + pos0.x), (float) (ballPoint0.y + pos0.y), (float) (ballPoint0.z + pos0.z))
                    .setColor(red, green, blue, alpha);
        }
        if (face || calculateNormal(
                new Vector3d(ballPoint0).add(pos0),
                new Vector3d(ballPoint1).add(pos0),
                new Vector3d(ballPoint2).add(pos0),
                cameraPos)) {
            consumer.addVertex(pose, (float) (ballPoint0.x + pos0.x), (float) (ballPoint0.y + pos0.y), (float) (ballPoint0.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (ballPoint1.x + pos0.x), (float) (ballPoint1.y + pos0.y), (float) (ballPoint1.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (ballPoint2.x + pos0.x), (float) (ballPoint2.y + pos0.y), (float) (ballPoint2.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (ballPoint3.x + pos0.x), (float) (ballPoint3.y + pos0.y), (float) (ballPoint3.z + pos0.z))
                    .setColor(red, green, blue, alpha);
        }
        if (face || calculateNormal(
                new Vector3d(ballPoint2).negate().add(pos0),
                new Vector3d(ballPoint1).negate().add(pos0),
                new Vector3d(ballPoint0).negate().add(pos0),
                cameraPos)) {
            consumer.addVertex(pose, (float) (-ballPoint0.x + pos0.x), (float) (-ballPoint0.y + pos0.y), (float) (-ballPoint0.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint1.x + pos0.x), (float) (-ballPoint1.y + pos0.y), (float) (-ballPoint1.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint2.x + pos0.x), (float) (-ballPoint2.y + pos0.y), (float) (-ballPoint2.z + pos0.z))
                    .setColor(red, green, blue, alpha);
            consumer.addVertex(pose, (float) (-ballPoint3.x + pos0.x), (float) (-ballPoint3.y + pos0.y), (float) (-ballPoint3.z + pos0.z))
                    .setColor(red, green, blue, alpha);
        }

        poseStack.popPose();
    }
}
