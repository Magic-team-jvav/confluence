package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.confluence.terraentity.entity.monster.BloodySpore;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords;

public class BloodySporeRenderer extends GeoEntityRenderer<BloodySpore> {

    public BloodySporeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeoNormalModel<>(TerraEntity.space("bloody_spore")));
    }

    @Override
    protected float getDeathMaxRotation(BloodySpore animatable){
        return 0;
    }

    @Override
    public void preRender(PoseStack poseStack, BloodySpore animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        scale(animatable, poseStack, partialTick);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    public int getPackedOverlay(BloodySpore animatable, float u, float partialTick) {
        return getOverlayCoords(animatable, this.getWhiteOverlayProgress(animatable, partialTick));
    }
    public Color getRenderColor(BloodySpore animatable, float partialTick, int packedLight) {
        Color from = Color.ORANGE;
        Color to = Color.RED;
        float f = animatable.getSwelling(partialTick);
        if(f <= 0.0f) return Color.WHITE;
        int r = (int) (from.getRed() + (to.getRed() - from.getRed()) * f);
        int g = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * f);
        int b = (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * f);
        return Color.ofARGB(255, r, g, b);
    }



    protected void scale(Creeper livingEntity, PoseStack poseStack, float partialTickTime) {
        float scaleSpeed = 1.2f;
        float f = livingEntity.getSwelling(partialTickTime)* scaleSpeed;
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, scaleSpeed);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.4F) * f1;
        poseStack.scale(f2, f3, f2);
    }

    protected float getWhiteOverlayProgress(Creeper livingEntity, float partialTicks) {
        float f = livingEntity.getSwelling(partialTicks);
        return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }
}
