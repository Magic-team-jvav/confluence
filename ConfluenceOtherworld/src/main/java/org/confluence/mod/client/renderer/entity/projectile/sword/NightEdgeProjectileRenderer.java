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
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class NightEdgeProjectileRenderer extends EntityRenderer<NightEdgeProjectile> {

    public NightEdgeProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(NightEdgeProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        if(entity.getOwner() == null || entity.tickCount <= 1){
            return;
        }

        double lerpX = Mth.lerp(partialTick, entity.xo - entity.getOwner().xo, entity.getX() - entity.getOwner().getX());
        double lerpY = Mth.lerp(partialTick, entity.yo - entity.getOwner().yo, entity.getY() - entity.getOwner().getY());
        double lerpZ = Mth.lerp(partialTick, entity.zo - entity.getOwner().zo, entity.getZ() - entity.getOwner().getZ());
        float y = -Mth.lerp(partialTick, entity.getOwner().yRotO, entity.getOwner().getYRot()) + 70;
        Vec3 entityPos = new Vec3(lerpX, lerpY, lerpZ);

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(y));
        poseStack.mulPose(Axis.XP.rotationDegrees(-15));
        poseStack.mulPose(Axis.YP.rotationDegrees(-y));

        entity.trail.renderTrail(entity, entityPos , poseStack, bufferSource, packedLight);
        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(-entityPos.x, -entityPos.y, -entityPos.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(y));
        poseStack.scale(1,1,-1);
        poseStack.mulPose(Axis.YP.rotationDegrees(-y));
        poseStack.translate(entityPos.x, entityPos.y, entityPos.z);

        poseStack.translate(0, 0.1,0 );
        poseStack.scale(0.8f, 0.8f, 0.8f);

        poseStack.mulPose(Axis.YP.rotationDegrees(y + 180));
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(-y - 180));

        entity.trail.renderTrail(entity, entityPos, poseStack, bufferSource, packedLight);
        poseStack.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull NightEdgeProjectile trailSwordProj) {
        return null;
    }
}