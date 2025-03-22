package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.projectile.arrow.BeeArrow;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.model.entity.BeeProjectileModel;
import org.confluence.terra_curio.common.entity.projectile.BeeProjectile;
import org.confluence.terraentity.client.entity.renderer.BaseEntityRenderer;

public class BeeArrowRenderer extends BaseEntityRenderer<BeeArrow, BeeProjectile, BeeProjectileModel> {

    public BeeArrowRenderer(EntityRendererProvider.Context context) {
        super(context, new BeeProjectileModel(context.bakeLayer(BeeProjectileModel.LAYER_LOCATION)),1,0);
    }

    @Override
    public ResourceLocation getTextureLocation(BeeArrow beeArrow) {
        return TerraCurio.asResource("textures/entity/bee_projectile.png");
    }

    @Override
    public void preRender(BeeArrow entity, float entityYaw, float partialTick, PoseStack poseStack, int packedLight){
        super.preRender(entity, entityYaw, partialTick, poseStack, packedLight);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));

    }


}
