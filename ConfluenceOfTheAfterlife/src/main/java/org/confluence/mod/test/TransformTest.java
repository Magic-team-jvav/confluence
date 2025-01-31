package org.confluence.mod.test;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class TransformTest {
    public static void main(String[] args){
        PoseStack poseStack = new PoseStack();

        poseStack.translate(5, 5, 5);

        poseStack.mulPose(Axis.XP.rotationDegrees(40));
        poseStack.mulPose(Axis.YP.rotationDegrees(40));
        poseStack.mulPose(Axis.ZP.rotationDegrees(40));

        poseStack.translate(-5, -5, -5);
        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();
        Vector4f vertex = new Vector4f(0,0,0, 1.0f);
        Vector4f newVertex = matrix.transform(vertex);
        System.out.printf("   vertex: (%.2f, %.2f, %.2f)%n", vertex.x(), vertex.y(), vertex.z());
        System.out.printf("newVertex: (%.2f, %.2f, %.2f)%n", newVertex.x(), newVertex.y(), newVertex.z());


    }
}
