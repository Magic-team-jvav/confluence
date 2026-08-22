package org.confluence.mod.common.combat.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.entity.projectile.CustomBulletEntity;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.gun.definition.GunProjectilePattern;
import org.mesdag.portlib.event.PortEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// Creates projectile entities from already-resolved shot data.
///
/// <p>Gun items do not know about entity constructors. That makes a new firing
/// pattern a combat concern instead of another Item subclass.</p>
public final class GunProjectileFactory {
    public static int spawn(ShotContext context, GunProjectilePattern pattern) {
        BaseGun gun = (BaseGun) context.gun().getItem();
        GunEvent.ProjectileCreationEvent event = new GunEvent.ProjectileCreationEvent(gun, context, createDefaults(context, pattern));
        PortEventHandler.postEvent(event);
        List<Projectile> projectiles = event.getProjectiles();
        projectiles.removeIf(Objects::isNull);
        projectiles.forEach(projectile -> configureProjectile(context, projectile));
        projectiles.forEach(context.level()::addFreshEntity);
        return projectiles.size();
    }

    public static List<BaseBulletEntity> create(ShotContext context, GunProjectilePattern pattern) {
        List<BaseBulletEntity> projectiles = createDefaults(context, pattern);
        projectiles.forEach(projectile -> configureProjectile(context, projectile));
        return projectiles;
    }

    private static List<BaseBulletEntity> createDefaults(ShotContext context, GunProjectilePattern pattern) {
        int count = pattern.type() == GunProjectilePattern.Type.SHOTGUN
                ? pattern.sampleProjectileCount(context.shooter().getRandom()) : 1;
        List<BaseBulletEntity> projectiles = new ArrayList<>(count);
        for (int index = 0; index < count; index++)
            projectiles.add(createDefaultProjectile(context, pattern));
        return projectiles;
    }

    private static BaseBulletEntity createDefaultProjectile(ShotContext context, GunProjectilePattern pattern) {
        ServerPlayer shooter = context.shooter();
        ItemStack ammo = context.ammo();
        return pattern.type() == GunProjectilePattern.Type.GRAVITY
                ? new CustomBulletEntity(shooter, pattern.gravity(), ammo)
                : new BaseBulletEntity(shooter, ammo);
    }

    private static void configureProjectile(ShotContext context, Projectile projectile) {
        ServerPlayer shooter = context.shooter();
        float speed = Math.max(0.0F, context.velocity());
        float inaccuracy = Math.max(0.0F, context.inaccuracy());
        projectile.setOwner(shooter);
        if (!(projectile instanceof BaseBulletEntity bullet)) {
            projectile.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
            projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, speed, inaccuracy);
            return;
        }
        bullet.setDamage(context.damage());
        bullet.setKnockback(context.knockback());
        bullet.setPenetrate(context.penetrate());
        Vec3 direction = shooter.getViewVector(1.0F);
        bullet.shoot(direction.x, direction.y, direction.z, speed, inaccuracy);
        bullet.setInitialVelocity(bullet.getDeltaMovement());
        bullet.setPos(bullet.position().add(direction.scale(0.18D)));
    }

    private GunProjectileFactory() {}
}
