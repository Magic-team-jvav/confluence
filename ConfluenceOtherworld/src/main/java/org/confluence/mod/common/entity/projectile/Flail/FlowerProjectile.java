package org.confluence.mod.common.entity.projectile.Flail;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.*;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/**
 * <h1>花之力花瓣投射物</h1>
 * 无追踪能力，沿初始方向直线飞行，造成武器基础伤害的 1/3 和一半击退。
 * <p>
 * 内置 {@link AttackStrategy} 负责索敌和发射逻辑：
 * SPIN/THROWN/RETRACT 每 4 tick、STAY 每 2 tick 向最近生物发射一枚花瓣。
 *
 * @see AttackStrategy
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

    /**
     * <h1>花之力攻击策略</h1>
     * SPIN/THROWN/RETRACT 每 10 tick、STAY 每 5 tick 向索敌范围内最近的生物发射花瓣。
     * <p>索敌半径 = {@link FlailComponent#maxDistance()}。
     */
    public static final class AttackStrategy implements FlailAttackStrategy {

        public static final AttackStrategy INSTANCE = new AttackStrategy();

        private int shootTimer;

        private AttackStrategy() {}

        @Override
        public void onSpinTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onThrownTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onRetractTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onStayTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 5);
        }

        @Override
        public void onDiscard(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {
            shootTimer = 0;
        }

        private void tryShoot(BaseFlailEntity flail, Player player, FlailComponent component, int interval) {
            if (--shootTimer > 0) return;
            shootTimer = interval;

            Level level = flail.level();
            if (level.isClientSide()) return;

            float maxDist = component.maxDistance();
            AABB searchBox = flail.getBoundingBox().inflate(maxDist);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                    e -> e != player && e.isAlive() && TEUtils.projectileCanHurtEntityTest.test(flail, e));

            LivingEntity nearest = targets.stream()
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(flail)))
                    .orElse(null);
            if (nearest == null) return;

            Vec3 direction = nearest.getBoundingBox().getCenter().subtract(flail.position()).normalize();
            double speed = component.throwSpeed();
            Vec3 velocity = direction.scale(speed);

            float baseDamage = (float) (component.damageFactor() * player.getAttributeValue(LibAttributes.getAttackDamage()));
            float petalDamage = baseDamage / 3.0f;

            FlowerProjectile petal = new FlowerProjectile(
                    ModEntities.FLOWER_PROJECTILE.get(), level, flail, player, velocity);
            petal.setPos(flail.position());
            petal.setBaseDamage(petalDamage);
            petal.setMaxLifetime(100); 
            level.addFreshEntity(petal);
        }
    }
}
