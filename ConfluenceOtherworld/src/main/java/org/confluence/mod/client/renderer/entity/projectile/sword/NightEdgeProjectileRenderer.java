package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.projectile.sword.NightEdgeProjectile;

/// 永夜刃的剑气拖尾独立于通用剑气组件渲染，避免因组件外观未同步而整帧跳过。
public final class NightEdgeProjectileRenderer extends EntityRenderer<NightEdgeProjectile> {
    public NightEdgeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(NightEdgeProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.getOwner() != null && entity.tickCount > 1) {
            SwordProjectileRenderer.renderNightEdge(entity, partialTick, poseStack, bufferSource, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(NightEdgeProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
