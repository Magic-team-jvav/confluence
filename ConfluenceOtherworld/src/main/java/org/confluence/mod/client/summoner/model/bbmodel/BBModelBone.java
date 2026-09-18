package org.confluence.mod.client.summoner.model.bbmodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

final class BBModelBone {

    private final String name;
    private final List<BBModelCube> cubes = new ArrayList<>();
    private final List<BBModelBone> children = new ArrayList<>();
    private final float pivotX;
    private final float pivotY;
    private final float pivotZ;
    private final float bindRotX;
    private final float bindRotY;
    private final float bindRotZ;
    private final boolean neverRender;

    private float posX;
    private float posY;
    private float posZ;
    private float rotX;
    private float rotY;
    private float rotZ;
    private float scaleX = 1;
    private float scaleY = 1;
    private float scaleZ = 1;
    private boolean hidden;

    BBModelBone(
            String name,
            float pivotX,
            float pivotY,
            float pivotZ,
            float bindRotX,
            float bindRotY,
            float bindRotZ,
            boolean neverRender
    ) {
        this.name = name;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.pivotZ = pivotZ;
        this.bindRotX = bindRotX;
        this.bindRotY = bindRotY;
        this.bindRotZ = bindRotZ;
        this.neverRender = neverRender;
        this.hidden = neverRender;
    }

    String getName() {
        return name;
    }

    List<BBModelBone> getChildren() {
        return children;
    }

    void addCube(BBModelCube cube) {
        cubes.add(cube);
    }

    void addChild(BBModelBone child) {
        children.add(child);
    }

    void reset() {
        posX = 0;
        posY = 0;
        posZ = 0;
        rotX = bindRotX;
        rotY = bindRotY;
        rotZ = bindRotZ;
        scaleX = 1;
        scaleY = 1;
        scaleZ = 1;
        hidden = neverRender;
        for (BBModelBone child : children) {
            child.reset();
        }
    }

    void setPosition(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    void setRotation(float x, float y, float z) {
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
    }

    void setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
    }

    void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    void render(
            PoseStack poseStack,
            IntFunction<VertexConsumer> consumers,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        if (hidden) {
            return;
        }

        poseStack.pushPose();
        applyBonePose(poseStack);
        for (BBModelCube cube : cubes) {
            cube.render(poseStack, consumers, packedLight, packedOverlay, color);
        }
        for (BBModelBone child : children) {
            child.render(poseStack, consumers, packedLight, packedOverlay, color);
        }
        poseStack.popPose();
    }

    private void applyBonePose(PoseStack poseStack) {
        poseStack.translate(posX / 16f, posY / 16f, posZ / 16f);
        poseStack.translate(pivotX / 16f, pivotY / 16f, pivotZ / 16f);
        if (rotZ != 0) {
            poseStack.mulPose(Axis.ZP.rotation(rotZ));
        }
        if (rotY != 0) {
            poseStack.mulPose(Axis.YP.rotation(rotY));
        }
        if (rotX != 0) {
            poseStack.mulPose(Axis.XP.rotation(rotX));
        }
        if (scaleX != 1 || scaleY != 1 || scaleZ != 1) {
            poseStack.scale(scaleX, scaleY, scaleZ);
        }
        poseStack.translate(-pivotX / 16f, -pivotY / 16f, -pivotZ / 16f);
    }
}
