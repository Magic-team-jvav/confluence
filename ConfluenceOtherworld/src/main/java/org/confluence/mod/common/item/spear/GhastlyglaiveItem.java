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
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.EasingType;

import java.util.Comparator;
import java.util.List;

public class GhastlyglaiveItem extends AbstractSpearItem {
    /// 索敌范围（格）
    private static final double SEARCH_RANGE = 20.0;
    /// 生成圆半径（格），x²+z²=25
    private static final double SPAWN_RADIUS = 5.0;

    public GhastlyglaiveItem() {
        super(new Properties().attributes(attributes(6, 30F)), ModRarity.LIME, 10, 3, createKeyframes(
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
    }

    /// 使用当前挥击已经传入的武器栈生成恶魂弹幕。
    ///
    /// <p>这里不能在命中后重新读取玩家主手，否则玩家切换物品或未来扩展副手攻击时，派生弹幕
    /// 可能冻结错误武器。基础实现仍负责伤害、击退回调和附魔后处理。</p>
    @Override
    protected void onHitEntity(
            ItemStack stack,
            ServerLevel level,
            LivingEntity owner,
            Entity victim
    ) {
        super.onHitEntity(stack, level, owner, victim);
        spawnGhastlyProjectile(stack, level, owner, victim);
    }

    /// 在受害者周围搜寻最近敌人，并在其周围圆形区域生成 [GhastlyProjectile]。
    private void spawnGhastlyProjectile(
            ItemStack weapon,
            ServerLevel level,
            LivingEntity owner,
            Entity victim
    ) {
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
        spawnProjectile(weapon, level, owner,
                new Vec3(spawnX, spawnY, spawnZ), dir, component, nearestEnemy);
    }

    /// 在世界提交前锁定目标，再由统一事务安装 MELEE 快照并生成实体。
    private GhastlyProjectile spawnProjectile(
            ItemStack weapon,
            ServerLevel level,
            LivingEntity owner,
            Vec3 pos,
            Vec3 direction,
            SpearProjectileComponent component,
            LivingEntity target
    ) {
        GhastlyProjectile projectile = new GhastlyProjectile(ModEntities.GHASTLY.get(), level);
        fireDerivedProjectile(
                weapon, level, owner, component, projectile,
                pos, direction, 0.0F, value -> value.setLockedTarget(target));
        return projectile;
    }

    @Override
    protected void onStingTick(ItemStack stack, ServerLevel level, LivingEntity owner, Vec3 tipPos, boolean last) {
        // 恶魂长戟不通过刺痛 Tick 生成弹射物，改为在 onHitEntity 中生成
    }
}
