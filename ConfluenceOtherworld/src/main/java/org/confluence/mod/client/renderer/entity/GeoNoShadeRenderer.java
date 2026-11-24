package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.effect.ModRenderTypes;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

public class GeoNoShadeRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    public GeoNoShadeRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poses, MultiBufferSource buffers, int packedLight) {
        super.render(entity, yaw, partialTick, poses, buffers, LightTexture.FULL_BRIGHT);
    }

    @Override
    public RenderType getRenderType(T entity, ResourceLocation texture, @Nullable MultiBufferSource buffers, float partialTick) {
        return ModRenderTypes.entityNoShadeCull(texture);
    }
}
