package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public final class MissingModelRenderer<T extends Entity> extends EntityRenderer<T> {
    public MissingModelRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        if (entity.isInvisible()) return;
        AABB box = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
        LevelRenderer.renderLineBox(poseStack, buffers.getBuffer(RenderType.lines()), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 1.0F, 0.65F, 0.1F, 1.0F);
        renderNameTag(entity, Component.translatable("entity.confluence.model_pending", entity.getDisplayName()), poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
