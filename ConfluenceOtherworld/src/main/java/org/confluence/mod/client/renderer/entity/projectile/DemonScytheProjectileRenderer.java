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
import org.confluence.mod.client.model.entity.projectile.DemonScytheProjectileModel;
import org.confluence.mod.common.entity.projectile.mana.DemonScytheProjectile;

public class DemonScytheProjectileRenderer extends EntityRenderer<DemonScytheProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/demon_scythe_projectile.png");
    private final DemonScytheProjectileModel model;

    public DemonScytheProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new DemonScytheProjectileModel(context.bakeLayer(DemonScytheProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(DemonScytheProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(DemonScytheProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.75F, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        poseStack.mulPose(Axis.ZP.rotation(-Mth.lerp(partialTick, entity.rotate.old, entity.rotate.neo)));
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
