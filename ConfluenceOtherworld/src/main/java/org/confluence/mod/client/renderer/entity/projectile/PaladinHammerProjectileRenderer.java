package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.confluence.mod.common.entity.projectile.PaladinHammerProjectile;

/// 绘制连续旋转的圣骑士重锤。
///
/// <p>旋转角使用实体年龄和渲染帧插值计算，不依赖二十次每秒的服务端角度同步，
/// 因而高速飞行时不会出现一格一格跳动的视觉。</p>
public final class PaladinHammerProjectileRenderer
        extends EntityRenderer<PaladinHammerProjectile> {
    public PaladinHammerProjectileRenderer(
            EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            PaladinHammerProjectile entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight) {
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.mulPose(Axis.ZP.rotation(
                (entity.tickCount + partialTick) * 0.45F));
        Minecraft.getInstance().getItemRenderer().renderStatic(
                entity.getItem(),
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                entity.getId());
        poseStack.popPose();
        super.render(
                entity,
                entityYaw,
                partialTick,
                poseStack,
                bufferSource,
                packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(
            PaladinHammerProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
