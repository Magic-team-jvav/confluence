package org.confluence.mod.common.item.flail;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.entity.flail.FlailAttackStrategy;
import org.confluence.mod.common.entity.projectile.Flail.FlowerProjectile;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * <h1>花之力连枷物品</h1>
 * 内嵌 {@link FlowerAttackStrategy}，在挥舞/投掷/收回时持续向附近敌人发射花瓣。
 */
public class FlowerPowerItem extends BaseFlailItem {

    public FlowerPowerItem(@NotNull FlailComponent flailComponent, @NotNull ModRarity rarity) {
        super(flailComponent, rarity);
    }

    @Override
    @NotNull
    public FlailAttackStrategy getAttackStrategy() {
        return new FlowerAttackStrategy();
    }

    /**
     * <h1>花之力攻击策略</h1>
     * SPIN/THROWN/RETRACT 每 10 tick、STAY 每 5 tick 向索敌范围内最近的生物发射花瓣。
     * <p>索敌半径 = {@link FlailComponent#maxDistance()}。
     */
    public static final class FlowerAttackStrategy implements FlailAttackStrategy {

        private int shootTimer;

        @Override
        public void onSpinTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                               @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onThrownTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                                 @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onRetractTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                                  @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onStayTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                               @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 5);
        }

        @Override
        public void onDiscard(@NotNull BaseFlailEntity flail, @NotNull Player player,
                              @NotNull FlailComponent component) {
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
                    e -> e != player && e.isAlive()
                            && TEUtils.projectileCanHurtEntityTest.test(flail, e));

            LivingEntity nearest = targets.stream()
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(flail)))
                    .orElse(null);
            if (nearest == null) return;

            Vec3 direction = nearest.getBoundingBox().getCenter()
                    .subtract(flail.position()).normalize();
            Vec3 velocity = direction.scale(component.throwSpeed());

            float baseDamage = (float) (player.getAttributeValue(LibAttributes.getAttackDamage()));
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
