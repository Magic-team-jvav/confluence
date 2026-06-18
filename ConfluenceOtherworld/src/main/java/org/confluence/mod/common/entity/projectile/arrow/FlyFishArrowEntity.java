package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;

public class FlyFishArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/arrow/fly_fish_arrow.png");

    public FlyFishArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public FlyFishArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    public double getBaseDamage() {
        return 4.2;
    }

    @Override
    protected float getCalculatedDamage() {
        float damage = super.getCalculatedDamage();
        return level().isRaining() ? damage + 3 : damage;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        if (level().isRaining()) {
            this.setDeltaMovement(getDeltaMovement().scale(1.5f));
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.8f * super.getWaterInertia();
    }

    @Override
    public ResourceLocation getTexturePath() {
        return TEXTURE;
    }
}
