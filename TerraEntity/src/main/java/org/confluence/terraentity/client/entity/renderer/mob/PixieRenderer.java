package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.terraentity.entity.monster.Pixie;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.stream.Stream;

public class PixieRenderer<T extends Pixie> extends GeoNegativeVolumeRenderer<T> {


    public PixieRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, model, ifRotX, scale, offsetY);
    }

    @Override
    protected void processInit(BakedGeoModel model){

        Stream.of("Outline", "Outline2","Outline3")
                .map(s->model.getBone(s).get())
                .forEach(toHide::add);
        Stream.of("Body", "Internal","Internal2","Internal3","Internal4")
                .map(s->model.getBone(s).get())
                .forEach(notToHide::add);

    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, int colour) {
        if(isReRender){
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, 0xDDDDDDDD);
        }else{
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, 0X0F000F0, packedOverlay, 0xFFFFFFFF);
        }
    }
}
