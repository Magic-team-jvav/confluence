package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/// 风矢的运行实体。
///
/// 箭速更慢（出手时结算一次减速），但穿透一个目标，命中后附加缓慢。
public class WindArrowEntity extends BaseArrowEntity {
    /// 相比普通箭矢的速度倍率。
    private static final double SPEED_SCALE = 0.78;
    /// 命中的减速时长（tick）。
    private static final int SLOW_DURATION = 80;

    public WindArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public WindArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected int getPenetrationCount() {
        return 1;
    }

    /// 更慢的箭速。
    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        setDeltaMovement(getDeltaMovement().scale(SPEED_SCALE));
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION, 0), getOwner());
    }
}
