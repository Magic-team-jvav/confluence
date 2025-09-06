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
import org.confluence.mod.client.model.entity.projectile.StormSpearShotProjectileModel;
import org.confluence.mod.common.entity.projectile.StormSpearShotProjectile;

public class StormSpearShotProjectileRenderer extends EntityRenderer<StormSpearShotProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/storm_spear_shot_projectile.png");
    private final StormSpearShotProjectileModel model;

    public StormSpearShotProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new StormSpearShotProjectileModel(context.bakeLayer(StormSpearShotProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(StormSpearShotProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(StormSpearShotProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, -1.2F, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRotation()));
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
