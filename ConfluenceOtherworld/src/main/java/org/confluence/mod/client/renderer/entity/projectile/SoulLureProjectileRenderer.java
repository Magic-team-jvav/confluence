package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.renderer.entity.projectile.sword.SwordProjectileRenderer;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;

/// 引魂弹幕平时没有模型（外观全部交给粒子风暴），
/// 只有魂引天星换成星怒坠星的交叉面片，因此这里只处理那一种情况。
public final class SoulLureProjectileRenderer extends EntityRenderer<SoulLureProjectile> {
    public SoulLureProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(SoulLureProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!entity.isStarAppearance()) {
            return;
        }
        SwordProjectileRenderer.renderCross(SoulLureProjectile.STAR_APPEARANCE, entity.tickCount + partialTick, poseStack, bufferSource, packedLight);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulLureProjectile entity) {
        return SoulLureProjectile.STAR_APPEARANCE.texture();
    }
}
