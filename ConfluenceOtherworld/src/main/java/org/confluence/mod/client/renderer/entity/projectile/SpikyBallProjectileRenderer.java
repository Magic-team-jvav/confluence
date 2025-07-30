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
import org.confluence.mod.client.model.entity.projectile.SpikyBallProjectileModel;
import org.confluence.mod.common.entity.projectile.SpikyBallProjectile;


public class SpikyBallProjectileRenderer extends EntityRenderer<SpikyBallProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/spiky_ball_projectile.png");
    private final SpikyBallProjectileModel model;
    public SpikyBallProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SpikyBallProjectileModel(context.bakeLayer(SpikyBallProjectileModel.LAYER_LOCATION));
    }
    @Override
    public ResourceLocation getTextureLocation(SpikyBallProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(SpikyBallProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate(0, 0.2f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        poseStack.mulPose(Axis.ZP.rotation(-Mth.lerp(partialTick, entity.rotate.old, entity.rotate.neo)));
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
