package org.confluence.mod.common.item.spear;

import PortLib.extensions.java.util.List.PortListExtension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.component.SpearProjectileComponent;
import org.confluence.mod.common.entity.projectile.spear.GhastlyProjectile;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import software.bernie.geckolib.core.animation.EasingType;

import java.util.Comparator;
import java.util.List;

public class GhastlyglaiveItem extends AbstractSpearItem {
    /// 索敌范围（格）
    private static final double SEARCH_RANGE = 20.0;
    /// 生成圆半径（格），x²+z²=25
    private static final double SPAWN_RADIUS = 5.0;

    public GhastlyglaiveItem() {
        super(new PortItem.PortProperties().attributes(attributes(6, 30F)), ModRarity.LIME, 10, 3, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.17, 6, EasingType.EASE_OUT_BACK),
                K.of(0.33, -16, EasingType.EASE_IN_EXPO),
                K.of(0.5, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        LibEntityUtils.knockBackA2B(owner, victim, 0.31, 0.2);

        // 生成恶魂弹射物
        if (!owner.level().isClientSide && owner.level() instanceof ServerLevel serverLevel) {
            spawnGhastlyProjectile(serverLevel, owner, victim);
        }
    }

    /// 在受害者周围搜寻最近敌人，并在其周围圆形区域生成 [GhastlyProjectile]。
    private void spawnGhastlyProjectile(ServerLevel level, LivingEntity owner, Entity victim) {
        Vec3 victimPos = victim.position();
        AABB searchBox = new AABB(victimPos.add(-SEARCH_RANGE, -SEARCH_RANGE, -SEARCH_RANGE),
                victimPos.add(SEARCH_RANGE, SEARCH_RANGE, SEARCH_RANGE));

        List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                e -> e.isAlive() && e != owner && LibEntityUtils.canHitEntity(e, owner));

        if (enemies.isEmpty()) return;

        // 取最近敌人
        enemies.sort(Comparator.comparingDouble(e -> e.distanceToSqr(victim)));
        LivingEntity nearestEnemy = PortListExtension.getFirst(enemies);

        // 在最近敌人周围圆上随机生成：x²+z²=SPAWN_RADIUS²，y 随机偏移 [-2, 2]
        double angle = owner.getRandom().nextDouble() * Math.PI * 2;
        double spawnX = nearestEnemy.getX() + SPAWN_RADIUS * Math.cos(angle);
        double spawnZ = nearestEnemy.getZ() + SPAWN_RADIUS * Math.sin(angle);
        double spawnY = nearestEnemy.getY() + (owner.getRandom().nextDouble() * 4.0 - 2.0);

        // 发射方向：水平指向锁定敌人
        Vec3 dir = new Vec3(
                nearestEnemy.getX() - spawnX,
                0.0,
                nearestEnemy.getZ() - spawnZ
        ).normalize();

        SpearProjectileComponent component = SpearProjectileComponent.GHASTLY_PROJECTILE.get();
        GhastlyProjectile projectile = spawnProjectile(level, owner,
                new Vec3(spawnX, spawnY, spawnZ), dir, component);
        // addFreshEntity 会触发 onAddedToLevel 自动索敌，此处显式覆盖以确保锁定正确目标
        projectile.setLockedTarget(nearestEnemy);
    }

    /// 生成 [GhastlyProjectile] 并添加到世界。
    ///
    /// @param level     服务端世界
    /// @param owner     弹射物主人
    /// @param pos       生成位置
    /// @param direction 发射方向
    /// @param component 弹射物配置组件
    /// @return 已生成的弹射物实例
    private GhastlyProjectile spawnProjectile(ServerLevel level, LivingEntity owner, Vec3 pos, Vec3 direction, SpearProjectileComponent component) {
        GhastlyProjectile projectile = new GhastlyProjectile(
                ModEntities.GHASTLY.get(), level);
        projectile.setOwner(owner);
        projectile.setWeapon(owner.getMainHandItem());
        // setProjComponent 自动从 owner 获取基础攻击伤害
        projectile.setProjComponent(component, owner);
        projectile.setPos(pos.x, pos.y, pos.z);
        projectile.fire(direction, component.getVelocity(owner), 0.0f);
        level.addFreshEntity(projectile);
        return projectile;
    }

    @Override
    protected void onStingTick(ItemStack stack, ServerLevel level, LivingEntity owner, Vec3 tipPos, boolean last) {
        // 恶魂长戟不通过刺痛 Tick 生成弹射物，改为在 onHitEntity 中生成
    }
}
