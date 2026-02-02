package org.confluence.terraentity.entity.util.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;

public class PositionPoseProperties {
    public Vec3 position;
    public float xrot;
    public float yrot;
    public PoseStack.Pose lastPose;
    public int color;

    public PositionPoseProperties(Vec3 position, float xRot, float yRot) {
        this.position = position;
        this.xrot = xRot;
        this.yrot = yRot;
        this.lastPose = null;
    }
}
