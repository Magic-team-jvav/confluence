package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.RainProjectileModel;
import org.confluence.mod.common.entity.projectile.mana.RainProjectile;

public class BloodRainProjectileRenderer extends EntityRenderer<RainProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/blood_rain_projectile.png");
    private final RainProjectileModel model;

    public BloodRainProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new RainProjectileModel(pContext.bakeLayer(RainProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(RainProjectile pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(RainProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.translate(0F, 0, 0);
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
    }
}
