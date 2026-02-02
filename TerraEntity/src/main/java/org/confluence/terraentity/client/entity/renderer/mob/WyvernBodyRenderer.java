package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.entity.monster.BaseWormPart;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class WyvernBodyRenderer<S extends BaseWormPart> extends GeoWormSegmentRenderer<S, WyvernRenderer> {
    public WyvernBodyRenderer(EntityRendererProvider.Context renderManager, WyvernRenderer parent, ResourceLocation body) {
        super(renderManager, parent, body, body);
    }

    public WyvernBodyRenderer(EntityRendererProvider.Context renderManager, WyvernRenderer parent, ResourceLocation body, float scale, float offsetY) {
        super(renderManager, parent, body, body, scale, offsetY);
    }

    @Override
    public void preRender(PoseStack poseStack, S animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        if(animatable.isTail){
            this.parent.bone.setHidden(true);
            this.parent.bone2.setHidden(true);
            this.parent.bone3.setHidden(true);
            this.parent.bone4.setHidden(false);
            poseStack.translate(0, 0, 3.5f);
        }else {
            switch (animatable.index) {
                case 3, 9 -> {
                    this.parent.bone.setHidden(true);
                    this.parent.bone2.setHidden(true);
                    this.parent.bone3.setHidden(false);
                    this.parent.bone4.setHidden(true);
                    poseStack.translate(0, 0, 2.37f);
                }
                default -> {
                    this.parent.bone.setHidden(true);
                    this.parent.bone2.setHidden(false);
                    this.parent.bone3.setHidden(true);
                    this.parent.bone4.setHidden(true);
                    poseStack.translate(0, 0, 1.3f);
                }
            }
        }
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
