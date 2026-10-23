package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

/// 坠落之星箭的运行实体：无重力、发光、穿透一个目标，短寿命。
public class FallenStarArrowEntity extends BaseArrowEntity {
    private static final ResourceLocation PARTICLE = Confluence.asResource("falling_star");
    private static final int LIFETIME = 200;

    public FallenStarArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public FallenStarArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected int getPenetrationCount() {
        return 1;
    }

    @Override
    public double getDefaultGravity() {
        return 0.0;
    }

    @Override
    protected int getAutoDiscardTick() {
        return LIFETIME;
    }

    @Override
    public int confluence$getLuminance() {
        return 8;
    }

    @Override
    protected @Nullable ResourceLocation getParticleId() {
        return PARTICLE;
    }
}
