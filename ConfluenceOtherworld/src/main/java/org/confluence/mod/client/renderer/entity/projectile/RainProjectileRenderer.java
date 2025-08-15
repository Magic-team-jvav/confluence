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

public class RainProjectileRenderer extends EntityRenderer<RainProjectile> {
    public static final ResourceLocation RAIN = Confluence.asResource("textures/entity/rain_projectile.png");
    public static final ResourceLocation BLOOD_RAIN = Confluence.asResource("textures/entity/blood_rain_projectile.png");
    private final RainProjectileModel model;
    private final ResourceLocation texture;

    public RainProjectileRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context);
        this.model = new RainProjectileModel(context.bakeLayer(RainProjectileModel.LAYER_LOCATION));
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(RainProjectile pEntity) {
        return texture;
    }

    @Override
    public void render(RainProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(texture)), packedLight, OverlayTexture.NO_OVERLAY);
    }
}
