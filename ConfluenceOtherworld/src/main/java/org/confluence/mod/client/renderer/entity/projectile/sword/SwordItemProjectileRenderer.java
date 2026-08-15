package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;

/**
 * 用冻结武器副本渲染没有独立实体模型的剑气。
 */
public class SwordItemProjectileRenderer<T extends SwordProjectile> extends EntityRenderer<T> {
    private final float scale;

    public SwordItemProjectileRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0F);
    }

    public SwordItemProjectileRenderer(EntityRendererProvider.Context context, float scale) {
        super(context);
        if (!Float.isFinite(scale) || scale <= 0.0F) {
            throw new IllegalArgumentException("Sword item projectile render scale must be finite and positive");
        }
        this.scale = scale;
    }

    @Override
    public void render(
            T entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        ItemStack weapon = entity.getWeaponItem();
        if (weapon == null || weapon.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        applyTransform(entity, partialTick, poseStack);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                weapon,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                entity.getId());
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    /**
     * 默认沿运动方向定向，并以实体年龄滚转；特殊剑气可以覆写为自己的局部动画。
     */
    protected void applyTransform(T entity, float partialTick, PoseStack poseStack) {
        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() > 1.0E-10) {
            float yaw = (float) Math.atan2(motion.z, motion.x);
            float pitch = (float) Math.atan2(motion.y, motion.horizontalDistance());
            poseStack.mulPose(Axis.YN.rotation(yaw + Mth.PI));
            poseStack.mulPose(Axis.ZN.rotation(pitch - Mth.PI * 0.25F));
        }
        poseStack.mulPose(Axis.ZP.rotation((entity.tickCount + partialTick) * 0.25F));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
