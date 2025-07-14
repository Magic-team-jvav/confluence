package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.sword.NightEdgeProjectile;
import org.confluence.mod.common.init.item.SwordItems;
import org.jetbrains.annotations.NotNull;

public class NightEdgeProjectileRenderer extends EntityRenderer<NightEdgeProjectile> {

    public NightEdgeProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(NightEdgeProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        float pitch;
        float yaw;
        if(entity.getOwner() != null && entity.tickCount > 1){
            pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
            yaw = entityYaw;

        }else{
            return;
        }
        // 位置插值
        double lerpX = Mth.lerp(partialTick, entity.xo - entity.getOwner().xo, entity.getX() - entity.getOwner().getX());
        double lerpY = Mth.lerp(partialTick, entity.yo - entity.getOwner().yo, entity.getY() - entity.getOwner().getY());
        double lerpZ = Mth.lerp(partialTick, entity.zo - entity.getOwner().zo, entity.getZ() - entity.getOwner().getZ());

        entity.trail.renderTrail(entity, new Vec3(lerpX, lerpY, lerpZ), poseStack, bufferSource, packedLight);

        poseStack.pushPose();

        // 旋转到正前方yaw
        poseStack.mulPose(Axis.YN.rotationDegrees(yaw - 90));

        // 旋转到正前方pitch
        poseStack.mulPose(Axis.ZN.rotationDegrees(-pitch));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.updateZRot(entity.tickCount + partialTick)));
        poseStack.mulPose(Axis.ZN.rotationDegrees(-45));


//        Minecraft.getInstance().getItemRenderer().renderStatic(SwordItems.NIGHTS_EDGE.toStack(), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();


    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull NightEdgeProjectile trailSwordProj) {
        return null;
    }
}