package org.confluence.mod.common.init;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public final class ModArmPoses {
    public static final EnumProxy<ArmPose> BREATHING_REED = new EnumProxy<>(ArmPose.class, false, (IArmPoseTransformer)
            (HumanoidModel<?> model, LivingEntity living, HumanoidArm arm) -> {
                if (living.isUnderWater()) {
                    if (arm == HumanoidArm.RIGHT) {
                        model.rightArm.xRot = -Mth.PI * 0.6F;
                        model.rightArm.yRot = model.head.yRot - Mth.PI * 0.2F;
                    } else {
                        model.leftArm.xRot = -Mth.PI * 0.6F;
                        model.leftArm.yRot = model.head.yRot + Mth.PI * 0.2F;
                    }
                }
            });
    public static final EnumProxy<ArmPose> LANCE = new EnumProxy<>(ArmPose.class, true, (IArmPoseTransformer)
            (HumanoidModel<?> model, LivingEntity living, HumanoidArm arm) -> {
                if (arm == HumanoidArm.RIGHT) {
                    if (model.crouching) {
                        model.rightArm.xRot = model.head.xRot;
                    } else {
                        model.rightArm.xRot = model.head.xRot + 0.4F;
                    }
                    model.rightArm.yRot = Mth.clamp(model.head.yRot, -0.8F, 0.8F);
                    model.rightArm.x = -3;
                    model.rightArm.z = 2;
                    model.leftArm.xRot = Mth.clamp(model.head.xRot * 0.8F, -0.48F, 0.32F) - Mth.PI * 0.25F;
                    model.leftArm.yRot = Mth.clamp(model.head.yRot, -1.2F, 0.4F) + 1.2F;
                    model.leftArm.x = 3;
                    model.leftArm.z = -1.5F;
                    model.body.yRot = 0.4F;
                }
            });
    public static final EnumProxy<ArmPose> UMBRELLA = new EnumProxy<>(ArmPose.class, false, (IArmPoseTransformer)
            (HumanoidModel<?> model, LivingEntity living, HumanoidArm arm) -> {
                if (arm == HumanoidArm.RIGHT) {
                    model.rightArm.xRot = -135 * Mth.DEG_TO_RAD;
                } else {
                    model.leftArm.xRot = -135 * Mth.DEG_TO_RAD;
                }
            }
    );
}
