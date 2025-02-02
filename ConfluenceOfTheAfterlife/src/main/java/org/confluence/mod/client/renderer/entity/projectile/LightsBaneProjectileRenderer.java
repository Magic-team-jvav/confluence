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
import org.confluence.mod.client.model.entity.projectile.EnchantedSwordProjectileModel;
import org.confluence.mod.common.entity.projectile.StillSwordProjectile;
import org.joml.Vector3f;

public class LightsBaneProjectileRenderer extends EntityRenderer<StillSwordProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/lights_bane.png");
    private final EnchantedSwordProjectileModel model;

    public LightsBaneProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new EnchantedSwordProjectileModel(pContext.bakeLayer(EnchantedSwordProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(StillSwordProjectile swordProjectile) {
        return TEXTURE;
    }

    @Override
    public void render(StillSwordProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        float scale;
        if(entity.tickCount > entity.TIME_EXISTENCE - 10){
            scale = 1 - (entity.tickCount + partialTick - (entity.TIME_EXISTENCE - 10)) * 0.1f;
        }else
            scale = Math.min((entity.tickCount + partialTick) * 0.2f, 1.0f);
        poseStack.scale(scale, scale, scale);

        poseStack.translate(0.00F, 0.125F, -0.125F);

        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, entity.getYRot(), entity.getYRot())) );
        float yRot =  entity.getYHeadRot();
        double rad = yRot*Math.PI/180;
        float xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(rad), 0, (float) Math.sin(rad))).rotationDegrees(xRot));
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(
                        RenderType.energySwirl(TEXTURE, (float) Math.sin(entity.tickCount * 0.1F),(float) Math.sin(entity.tickCount * 0.2F))
        ), packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}
