package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.mod.common.init.ModTags;

public class ShortSwordInHandRenderer implements IClientItemExtensions {
    public static final ShortSwordInHandRenderer INSTANCE = new ShortSwordInHandRenderer();

    private ShortSwordInHandRenderer() {}

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
        if (arm == HumanoidArm.RIGHT && itemInHand.is(ModTags.Items.SHORT_SWORD)) {
            // 手部初始位置
            poseStack.translate(0.56F, -0.52F, -0.72F);
            poseStack.mulPose(Axis.XP.rotation(Mth.PI * -0.55F));
            // 攻击动画
            poseStack.translate(0, Mth.sin(swingProcess * Mth.PI), 0);
            return true;
        }
        return false;
    }

    public static boolean setupAttackAnimation(LivingEntity living, HumanoidModel<?> model) {
        ItemStack stack = living.getMainHandItem();
        if (stack.is(ModTags.Items.SHORT_SWORD)) {
            ModelPart rightArm = model.rightArm;
            rightArm.xRot = Mth.HALF_PI * -0.55F * Mth.sin(model.attackTime * Mth.PI);
            rightArm.yRot = 0;
            rightArm.zRot = 0;
            return true;
        }
        return false;
    }

    public static boolean renderArmWithItem(ItemStack stack, ItemDisplayContext context, HumanoidArm arm, PoseStack poseStack, float x, float z, EntityModel<?> parentModel) {
        if (arm == HumanoidArm.RIGHT &&
                context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND &&
                parentModel instanceof HumanoidModel<?> model &&
                stack.is(ModTags.Items.SHORT_SWORD)
        ) {
            poseStack.translate(x, 0, z);
            poseStack.mulPose(Axis.XP.rotation(Mth.sin(model.attackTime * Mth.PI) * Mth.HALF_PI * -0.55F));
            return false;
        }
        return true;
    }
}
