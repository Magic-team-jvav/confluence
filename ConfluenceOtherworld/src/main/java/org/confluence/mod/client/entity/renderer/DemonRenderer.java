package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.confluence.mod.common.entity.monster.Demon;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class DemonRenderer extends GeoNormalRenderer<Demon> {
    public DemonRenderer(EntityRendererProvider.Context context, ResourceLocation path, float scale) {
        super(context, new GeoNormalModel<Demon>(path).setHeadName("head"), false, scale, 0.0F);
    }

    @Override
    public void preRender(PoseStack poseStack, Demon demon, BakedGeoModel model, MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        model.getBone("voodoo_doll").ifPresent(bone -> bone.setHidden(demon.getType() != MonsterEntities.VOODOO_DEMON.get()));
        super.preRender(poseStack, demon, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
