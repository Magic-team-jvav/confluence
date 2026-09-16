package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.RainProjectileModel;

public class RainProjectileRenderer<T extends Entity> extends EntityRenderer<T> {
    public static final ResourceLocation RAIN = Confluence.asResource("textures/entity/rain_projectile.png");
    public static final ResourceLocation BLOOD_RAIN = Confluence.asResource("textures/entity/blood_rain_projectile.png");
    private final RainProjectileModel model;
    private final ResourceLocation texture;
    private final float offsetY;

    public RainProjectileRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        this(context, texture, 0.0F);
    }

    public RainProjectileRenderer(EntityRendererProvider.Context context, ResourceLocation texture, float offsetY) {
        super(context);
        this.offsetY = offsetY;
        this.model = new RainProjectileModel(context.bakeLayer(RainProjectileModel.LAYER_LOCATION));
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return texture;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, offsetY, 0.0);
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entityTranslucentCull(texture)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }
}
