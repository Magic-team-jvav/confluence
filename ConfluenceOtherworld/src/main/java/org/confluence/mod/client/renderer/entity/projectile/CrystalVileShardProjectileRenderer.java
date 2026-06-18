package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.projectile.strip.CrystalVileShardProjectile;
import org.confluence.mod.common.init.ModEntities;
import software.bernie.geckolib.core.object.Color;

import java.util.List;

public class CrystalVileShardProjectileRenderer extends GeoNegativeVolumeRenderer<CrystalVileShardProjectile> {
    public CrystalVileShardProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, ModEntities.CRYSTAL_VILE_SHARD.getId(), false);
        setBoneToGlow(List.of("Outline"), List.of("Internal"));
    }

    @Override
    public Color getRenderColor(CrystalVileShardProjectile animatable, float partialTick, int packedLight) {
        return Color.ofRGBA(255, 255, 255, animatable.getAlpha());
    }

    @Override
    public void render(CrystalVileShardProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.375F, 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 0xF000F0);
        poseStack.popPose();
    }

    @Override
    protected void applyRotations(CrystalVileShardProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float[] rot = animatable.getRot();
        poseStack.mulPose(Axis.YP.rotation(rot[0] + Mth.PI));
        poseStack.mulPose(Axis.XP.rotation(rot[1]));
    }

    @Override
    protected RenderType getGlowRenderType(CrystalVileShardProjectile animatable, ResourceLocation texture) {
        return RenderType.EYES.apply(texture, RenderType.TRANSLUCENT_TRANSPARENCY);
    }
}
