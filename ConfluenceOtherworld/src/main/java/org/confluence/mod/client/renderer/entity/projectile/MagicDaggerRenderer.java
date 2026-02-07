package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.projectile.mana.MagicDaggerProjectile;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terraentity.client.entity.renderer.mob.GeoNegativeVolumeRenderer;

public class MagicDaggerRenderer extends GeoNegativeVolumeRenderer<MagicDaggerProjectile> {
    public MagicDaggerRenderer(EntityRendererProvider.Context context) {
        super(context, ModEntities.MAGIC_DAGGER_PROJECTILE.getId(), false);
    }

    @Override
    public void render(MagicDaggerProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.tickCount > 1) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 0xF000F0);
        }
    }

    @Override
    protected void applyRotations(MagicDaggerProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getYRot() + 180));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
    }
}
