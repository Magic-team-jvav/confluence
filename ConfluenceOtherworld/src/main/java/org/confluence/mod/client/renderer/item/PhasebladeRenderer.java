package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.confluence.mod.client.model.item.PhasebladeModel;
import org.confluence.mod.common.item.sword.Phaseblade;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class PhasebladeRenderer extends GeoItemRenderer<Phaseblade> {
    private boolean isTurningOn = false;

    public PhasebladeRenderer(String color) {
        super(new PhasebladeModel(color));
        addRenderLayer(autoGlowingGeoLayer);
    }

    AutoGlowingGeoLayer<Phaseblade> autoGlowingGeoLayer = new AutoGlowingGeoLayer<>(this) {
        @Override
        protected RenderType getRenderType(Phaseblade animatable) {
            // todo frame应该存itemstack
            animatable.frame++;
            return RenderType.energySwirl(((PhasebladeModel) this.getGeoModel()).texture, 0, (float) (Math.cos(animatable.frame * 0.001F) / 4 + Math.cos(animatable.frame * 0.002F) / 2 + Math.cos(animatable.frame * 0.004F)));
        }

        @Override
        public void render(PoseStack poseStack, Phaseblade animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (!isTurningOn) return;
            super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }
    };


    @Override
    public void actuallyRender(PoseStack poseStack, Phaseblade animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.isTurningOn = getCurrentItemStack().getDataOrDefault(TEDataComponentTypes.BOOMERANG_READY, SingleBooleanComponent.FALSE).value();
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
