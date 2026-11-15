package org.confluence.mod.common.entity.boss;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import org.confluence.lib.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/// 在命中结算前反射弹幕，避免弹丸先伤害 Boss 或先消失后再改变速度。
public final class BossProjectileReflection {
    private BossProjectileReflection() {}

    public static boolean reflect(Projectile projectile, Entity target) {
        if (projectile.level().isClientSide || projectile.isRemoved()) return false;
        Entity owner = target instanceof PartHitTarget part ? part.encounterOwner()
                : target instanceof BossOwnedEntity owned ? owned.getBossOwner() : target;
        if (!(owner instanceof BaseBoss boss) || !boss.isAlive() || !boss.isFtw()
                || !(projectile.getOwner() instanceof LivingEntity shooter) || !boss.canAttack(shooter))
            return false;
        boolean reflectsStar = projectile.getType() == ModEntities.STAR_CANNON_BULLET.get()
                && (boss instanceof EyeOfCthulhu || boss instanceof EaterOfWorlds || boss instanceof BrainOfCthulhu
                || boss instanceof Skeletron || boss instanceof WallOfFlesh || boss instanceof AbstractTwinEye
                || boss instanceof TheDestroyer || boss instanceof SkeletronPrime || boss instanceof Plantera);
        boolean reflectsAttack = target instanceof EyeOfCthulhu eye && eye.getCombatState() == EyeOfCthulhu.CombatState.TRANSFORMING
                || target instanceof Skeletron skeleton && skeleton.isSpinning() && skeleton.getRemainingHandCount() > 0
                || target instanceof PlanteraTentacle && boss.getRandom().nextInt(10) == 0;
        if (!reflectsStar && !reflectsAttack) return false;
        Vec3 velocity = projectile.getDeltaMovement().scale(-1.0);
        if (velocity.lengthSqr() < 1.0E-7) return false;
        if (projectile instanceof SwordProjectile sword) sword.reflectFrom(boss, velocity);
        else {
            projectile.setOwner(boss);
            projectile.setDeltaMovement(velocity);
        }
        projectile.setPos(projectile.position().add(velocity.normalize().scale(0.25)));
        projectile.hasImpulse = true;
        projectile.hurtMarked = true;
        return true;
    }
}
