package org.confluence.terraentity.client.post;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.attachment.ItemInHandTrailAttachment;
import org.confluence.terraentity.entity.util.trail.player.ItemInHandTail;
import org.confluence.terraentity.init.TEAttachments;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 玩家手持物品的拖尾渲染器
 */
public class PlayerSwordTrailRenderer {


    public static void render(ItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer, LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int packedLight){
        if(livingEntity instanceof Player player && !itemStack.isEmpty() && displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            ItemInHandTrailAttachment data = player.getData(TEAttachments.TRAIL_STORAGE);
            ItemInHandTail trail = ItemInHandTrailAttachment.updateTrails(player);
            if (trail != null) {
                float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);

                // 获取玩家模型的位置
                poseStack.pushPose();
                PoseStack poseStack1 = new PoseStack();
                Vector3f pos = new Vector3f();
                poseStack.last().pose().transformPosition(pos);
                poseStack1.translate(pos.x(), pos.y(), pos.z());
                poseStack.popPose();

                // 获取手臂的朝向
                PoseStack poseStack2 = new PoseStack();
                Quaternionf q = Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot));
                poseStack2.mulPose(q);
                (layer.getParentModel()).translateToHand(arm, poseStack2);
                poseStack2.mulPose(Axis.XP.rotationDegrees(-90.0F));
                poseStack2.mulPose(Axis.YP.rotationDegrees(180.0F));
//                boolean flag = arm == HumanoidArm.LEFT;
                poseStack2.translate(1 / 16.0F, 0.125F, -0.625F);
                poseStack2.mulPose(Axis.XP.rotationDegrees(-90));


                // 计算手臂位置偏移
                Vector3f dist = new Vector3f();
                poseStack2.last().pose().transformPosition(dist);
                dist = dist.mul(1,-1,1);

                // 计算手臂朝向，以修正到剑的位置
                Vector3f dir = q.transform(new Vector3f(0,0,1));
                dist = dist.add(dir.mul(0.7f));

                // 玩家位置插值
                double lerpx = Mth.lerp(partialTicks, player.xOld, player.getX());
                double lerpy = Mth.lerp(partialTicks, player.yOld, player.getY());
                double lerpz = Mth.lerp(partialTicks, player.zOld, player.getZ());
                Vec3 playerPos = new Vec3(lerpx, lerpy, lerpz);

                // 渲染拖尾
                trail.renderTrail(player, playerPos, poseStack1, buffer, packedLight, poseStack2.last(), Vec3.ZERO );

                // 添加拖尾
                Vec3 handPos = playerPos.add(new Vec3(dist));

                trail.generateTrail(player, player.tickCount, partialTicks, data, handPos);
            }

        }

    }

}
