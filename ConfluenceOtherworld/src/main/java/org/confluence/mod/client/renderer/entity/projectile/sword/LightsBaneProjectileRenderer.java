package org.confluence.mod.client.renderer.entity.projectile.sword;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.EnchantedSwordProjectileModel;
import org.confluence.mod.common.entity.projectile.sword.LightBaneProjectile;

/**
 * 魔光剑气渲染器：使用能量流纹理，并按实体寿命完成出现和消隐。
 */
public final class LightsBaneProjectileRenderer
        extends ForwardSwordProjectileRenderer<LightBaneProjectile> {
    public LightsBaneProjectileRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new EnchantedSwordProjectileModel(
                        context.bakeLayer(EnchantedSwordProjectileModel.LAYER_LOCATION)),
                Confluence.asResource("textures/entity/lights_bane.png"),
                1.0F,
                0.125F);
    }

    @Override
    protected float getAgeScale(LightBaneProjectile entity, float partialTick) {
        float age = entity.tickCount + partialTick;
        if (age < 10.0F) {
            return age / 10.0F;
        }
        float fadeTicks = Math.max(entity.lifetime - 10.0F, 1.0F);
        return Math.max(1.0F - (age - 10.0F) / fadeTicks, 0.0F);
    }

    @Override
    protected RenderType getRenderType(LightBaneProjectile entity, float partialTick) {
        float age = entity.tickCount + partialTick;
        return RenderType.energySwirl(
                getTextureLocation(entity),
                (float) Math.sin(age * 0.1F),
                (float) Math.sin(age * 0.2F));
    }
}
