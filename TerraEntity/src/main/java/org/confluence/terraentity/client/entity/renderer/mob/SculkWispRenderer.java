package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.summon.SculkWisp;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class SculkWispRenderer extends GeoNormalRenderer<SculkWisp> {

    public SculkWispRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path, true);
    }


    @Override
    protected void adjustPose(PoseStack poseStack, SculkWisp animatable, BakedGeoModel model, float partialTick){
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
        poseStack.translate(0, 0.5, 0);
    }
}
