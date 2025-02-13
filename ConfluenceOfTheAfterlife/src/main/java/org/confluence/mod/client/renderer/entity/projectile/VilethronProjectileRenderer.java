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
import org.confluence.mod.client.model.entity.projectile.ThrownKnivesProjectileModel;
import org.confluence.mod.common.entity.projectile.VilethronProjectile;
import org.confluence.mod.util.ClientUtils;

public class VilethronProjectileRenderer extends EntityRenderer<VilethronProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/thrown_knives_projectile.png");
    private final ThrownKnivesProjectileModel model;

    public VilethronProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ThrownKnivesProjectileModel(context.bakeLayer(ThrownKnivesProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(VilethronProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(VilethronProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.125F, 0.0F);
        float[] rot = entity.getRot();
        poseStack.mulPose(Axis.YP.rotation(rot[0] - Mth.HALF_PI));
        poseStack.mulPose(Axis.ZP.rotation(rot[1]));
        poseStack.mulPose(ClientUtils.ANGLE_N90);
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
