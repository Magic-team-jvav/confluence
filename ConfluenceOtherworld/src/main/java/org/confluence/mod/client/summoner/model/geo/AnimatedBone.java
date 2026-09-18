package org.confluence.mod.client.summoner.model.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.ArrayList;
import java.util.List;

/**
 * 骨骼模型中的可变骨骼节点。
 * <p>
 * {@code rotX/rotY/rotZ} 与 GeckoLib 保持同一约定：
 * 模型 JSON 的 rotation(x,y,z) 烘焙为 (-x°, -y°, z°) 弧度；
 * 动画 keyframe 的 rotation 也按相同规则转换。
 * </p>
 */
public final class AnimatedBone {

    private final String name;
    private final List<AnimatedCube> cubes = new ArrayList<>();
    private final List<AnimatedBone> children = new ArrayList<>();

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

    AnimatedBone(
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

    public String getName() {
        return name;
    }

    public List<AnimatedBone> getChildren() {
        return children;
    }

    void addCube(AnimatedCube cube) {
        cubes.add(cube);
    }

    void addChild(AnimatedBone child) {
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
        for (AnimatedBone child : children) {
            child.reset();
        }
    }

    /** 供动画采样器写入位置。单位与模型 JSON 相同（像素/16）。 */
    void setPosition(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    /** 供动画采样器写入旋转（弧度，已按 Gecko 轴约定转换）。 */
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

    /** 隐藏此骨骼及其子树。 */
    void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, int color) {
        if (hidden) {
            return;
        }

        poseStack.pushPose();
        applyBonePose(poseStack);

        for (AnimatedCube cube : cubes) {
            cube.render(poseStack, consumer, packedLight, packedOverlay, color);
        }
        for (AnimatedBone child : children) {
            child.render(poseStack, consumer, packedLight, packedOverlay, color);
        }

        poseStack.popPose();
    }

    private void applyBonePose(PoseStack poseStack) {
        poseStack.translate(-posX / 16f, posY / 16f, posZ / 16f);
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
