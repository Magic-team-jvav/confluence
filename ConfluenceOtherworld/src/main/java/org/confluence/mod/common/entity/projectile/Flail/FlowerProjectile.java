package org.confluence.mod.common.entity.projectile.Flail;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModDamageTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <h1>花之力花瓣投射物</h1>
 * 无追踪能力，沿初始方向直线飞行，造成武器基础伤害的 1/3 和一半击退。
 * <p>
 * 发射逻辑由 {@code FlowerPowerItem.FlowerAttackStrategy} 驱动。
 */
public class FlowerProjectile extends BaseFlailProjectile {

    public FlowerProjectile(@NotNull EntityType<? extends BaseFlailProjectile> entityType,
                            @NotNull Level level,
                            @Nullable BaseFlailEntity parentFlail,
                            @Nullable Player owner,
                            @NotNull Vec3 velocity) {
        super(entityType, level, parentFlail, owner);
        setDeltaMovement(velocity);
        setNoGravity(true);
        faceVelocity();
    }

    /** 供 {@link EntityType.Builder} 反射使用的无参构造器 */
    public FlowerProjectile(@NotNull EntityType<? extends BaseFlailProjectile> entityType,
                            @NotNull Level level) {
        super(entityType, level);
    }

    @Override
    protected void subTick() {
    }

    @Override
    public boolean canHitEntity(@NotNull Entity target) {
        // 避免命中持有者和父连枷实体
        Entity owner = getOwner();
        if (target == owner || target == getParentFlail()) return false;
        return super.canHitEntity(target);
    }

    @Override
    protected void onHitLiving(@NotNull LivingEntity target) {
        Player player = getOwnerAsPlayer();
        if (player == null) return;

        DamageSource source = ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, player);
        if (target.hurt(source, baseDamage)) {
            // 一半击退
            VectorUtils.knockBackA2B(this, target, 0.15f, 0.08f);
        }
        discard();
    }
}
