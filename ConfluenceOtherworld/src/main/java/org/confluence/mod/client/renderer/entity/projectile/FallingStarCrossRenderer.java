package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.renderer.entity.projectile.sword.SwordProjectileRenderer;
import org.confluence.mod.common.entity.projectile.mana.HomingFallingStarProjectile;
import org.confluence.mod.common.init.item.SwordItems;

/// 陨星类弹幕的通用渲染：直接复用星怒弹幕的交叉面片模型。
///
/// 星泣累积释放的坠星、星乐爆开的坠星与星惧逃出的坠星共用它；
/// 星乐蓄力中的聚星通过 {@link #scale(Entity)} 放大表现。
public class FallingStarCrossRenderer<T extends Entity> extends EntityRenderer<T> {
    public FallingStarCrossRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        /// 星泣的多颗坠星会在上方排队等待，延迟期间不显示，落到时才出现。
        if (entity instanceof HomingFallingStarProjectile star && star.getSpawnDelay() > 0) {
            return;
        }
        float scale = scale(entity);
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        SwordProjectileRenderer.renderCross(SwordItems.STAR_FURY_APPEARANCE, entity.tickCount + partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    /// 子类可覆盖以按蓄力时间放大，默认保持星怒原尺寸。
    protected float scale(T entity) {
        return 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return SwordItems.STAR_FURY_APPEARANCE.texture();
    }
}
