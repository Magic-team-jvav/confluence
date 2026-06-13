package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.VilethronProjectileModel;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;

public class VilethronProjectileRenderer extends EntityRenderer<VilethronProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/vilethron_projectile.png");
    private final VilethronProjectileModel model;

    public VilethronProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new VilethronProjectileModel(context.bakeLayer(VilethronProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(VilethronProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(VilethronProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float[] rot = entity.getRot();
        poseStack.mulPose(Axis.YP.rotation(rot[0] - Mth.HALF_PI));
        poseStack.mulPose(Axis.ZP.rotation(rot[1]));
        model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, entity.getAlpha() / 255F);
        poseStack.popPose();
    }
}
