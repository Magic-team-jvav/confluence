package org.confluence.mod.common.init;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public final class ModArmPoses {
    public static final EnumProxy<ArmPose> BREATHING_REED = new EnumProxy<>(ArmPose.class, false, (IArmPoseTransformer)
            (model, living, arm) -> {
                if (arm == HumanoidArm.RIGHT) {
                    model.rightArm.xRot = -Mth.PI * 0.6F;
                    model.rightArm.yRot = model.head.yRot - (float) (Math.PI * 0.1F);
                } else {
                    model.leftArm.xRot = -Mth.PI * 0.6F;
                    model.leftArm.yRot = model.head.yRot + (float) (Math.PI * 0.1F);
                }
            });
}
