package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.monster.Demon;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class DemonRenderer extends GeoNormalRenderer<Demon> {
    GeoBone voodoo = null;
    public DemonRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path);
    }

    public DemonRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX) {
        super(renderManager, path, ifRotX);
    }

    public DemonRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, path, ifRotX, scale, offsetY);
    }

    public DemonRenderer(EntityRendererProvider.Context renderManager, GeoModel<Demon> model, boolean ifRotX, float scale, float offsetY) {
        super(renderManager, model, ifRotX, scale, offsetY);
    }

    public void preRender(PoseStack poseStack, Demon animatable, BakedGeoModel model, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        if(voodoo == null){
            voodoo = model.topLevelBones().get(0).getChildBones().get(1);
        }
        if(voodoo!= null) {
            if (animatable.getType() == TEMonsterEntities.VOODOO_DEMON.get()) {
//            model.getBone()
                voodoo.setHidden(false);
            } else {
                voodoo.setHidden(true);
            }
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}