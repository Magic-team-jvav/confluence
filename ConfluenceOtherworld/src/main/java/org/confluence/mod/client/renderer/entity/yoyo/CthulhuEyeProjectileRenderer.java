package org.confluence.mod.client.renderer.entity.yoyo;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.confluence.mod.client.entity.renderer.GeoNormalRenderer;
import org.confluence.mod.common.entity.yoyo.CthulhuEyeProjectile;
import software.bernie.geckolib.core.object.Color;

/// 复用克眼二阶段冲锋模型，缩小为短寿命的半透明影子。
public final class CthulhuEyeProjectileRenderer extends GeoNormalRenderer<CthulhuEyeProjectile> {
    public CthulhuEyeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, new GeoNormalModel<>(Confluence.asResource("boss/eye_of_cthulhu"), false), true, 1.0F, 1.5F);
        shadowRadius = 0;
    }

    @Override
    public Color getRenderColor(CthulhuEyeProjectile projectile, float partialTick, int packedLight) {
        float age = projectile.tickCount + partialTick;
        float remaining = Mth.clamp(1.0F - age / 20.0F, 0.0F, 1.0F);
        int alpha = (int) (160 * remaining * remaining);
        return Color.ofRGBA(255, 255, 255, alpha);
    }

    @Override
    protected float getRenderYaw(CthulhuEyeProjectile projectile, float partialTick) {
        Vec3 motion = projectile.getDeltaMovement();
        return (float) (Mth.atan2(motion.z, motion.x) * Mth.RAD_TO_DEG) - 90;
    }

    @Override
    protected float getRenderPitch(CthulhuEyeProjectile projectile, float partialTick) {
        Vec3 motion = projectile.getDeltaMovement();
        return (float) (-Mth.atan2(motion.y, motion.horizontalDistance()) * Mth.RAD_TO_DEG);
    }
}
