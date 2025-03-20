package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import org.confluence.mod.common.entity.projectile.WhipEntity;
import org.confluence.terraentity.entity.ai.keyframe.FrameUtil;

import java.util.ArrayList;
import java.util.List;

public class WhipEntityRenderer extends EntityRenderer<WhipEntity> {

    public WhipEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(WhipEntity whipEntity) {
        return whipEntity.texture;
    }

    public boolean shouldRender(WhipEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    public void render(WhipEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        if(entity.keyPositions == null) return;

        if(entity.getOwner() instanceof Player player) {
            poseStack.pushPose();
            poseStack.translate(-0.5, -0.5f, -0.5);
            float f = player.getAttackAnim(partialTick);
            float f1 = Mth.sin(Mth.sqrt(f) * 3.1415927F);
            Vec3 vec3 = this.getPlayerHandPos(player, f1, partialTick);
            List<Vec3> toInterpoloate = new ArrayList<>(entity.keyPositions.stream().map(Vec3::new).toList());
            List<Vec3> toInterpoloateO = new ArrayList<>(entity.keyPositionsO.stream().map(Vec3::new).toList());
            for(int i = 0; i < toInterpoloateO.size(); i++){
                // 关键点位置插值
                double lerpx = Mth.lerp(partialTick, toInterpoloateO.get(i).x(), toInterpoloate.get(i).x());
                double lerpy = Mth.lerp(partialTick, toInterpoloateO.get(i).y(), toInterpoloate.get(i).y());
                double lerpz = Mth.lerp(partialTick, toInterpoloateO.get(i).z(), toInterpoloate.get(i).z());
                toInterpoloate.set(i, new Vec3(lerpx, lerpy, lerpz));
            }
            // 位置插值
            double lerpx = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double lerpy = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double lerpz = Mth.lerp(partialTick, entity.zOld, entity.getZ());

            // 这里要 * 2
            toInterpoloate.add(vec3.subtract(new Vec3(lerpx, lerpy, lerpz)).scale(2));

            // Catmull-Rom样条插值
            List<Vec3> positions = FrameUtil.getInterpolatedPoints(toInterpoloate, 30);

            for(Vec3 vec : positions){
                // 变换到相机坐标系
                float f2 = (float) (vec3.x - vec.x - lerpx);
                float f3 = (float) (vec3.y - vec.y - lerpy);
                float f4 = (float) (vec3.z - vec.z - lerpz);
                poseStack.pushPose();
                poseStack.translate(-f2 , -f3, -f4);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                        Blocks.ACACIA_FENCE.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.pack(0, 10));
                poseStack.popPose();
            }
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private Vec3 getPlayerHandPos(Player player, float angle, float partialTick) {
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemstack = player.getMainHandItem();
        if (!itemstack.canPerformAction(ItemAbilities.FISHING_ROD_CAST)) {
            i = -i;
        }

        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double d4 = 960.0 / (double) this.entityRenderDispatcher.options.fov().get();
            Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane()
                    .getPointOnPlane((float)i * 0.525F, -0.1F)
                    .scale(d4)
                    .yRot(angle * 0.5F - 1F)
                    .xRot(-angle * 0.7F);
            return player.getEyePosition(partialTick).add(vec3);
        } else {
            float f = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * 0.017453292F + 1f;
            double d0 = Mth.sin(f);
            double d1 = Mth.cos(f);
            float f1 = player.getScale();
            double d2 = (double)i * 0.35 * (double)f1;
            double d3 = 0.8 * (double)f1;
            float f2 = player.isCrouching() ? -0.1875F : 0.0F;
            return player.getEyePosition(partialTick).add(-d1 * d2 - d0 * d3, (double)f2 - 0.45 * (double)f1, -d0 * d2 + d1 * d3);
        }
    }

}
