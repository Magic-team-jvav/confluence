package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.IceBladeSwordProjectileModel;
import org.confluence.mod.common.entity.projectile.SwordProjectile;

public class IceBladeSwordProjectileRenderer extends EntityRenderer<SwordProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/ice_blade_sword_projectile.png");
    private final IceBladeSwordProjectileModel model;

    public IceBladeSwordProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new IceBladeSwordProjectileModel(pContext.bakeLayer(IceBladeSwordProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(SwordProjectile swordProjectile) {
        return TEXTURE;
    }

    @Override
    public void render(SwordProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.00F, 0.125F, -0.125F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
