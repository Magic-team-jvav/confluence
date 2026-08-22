package org.confluence.mod.common.combat.gun;

public final class BallisticsResolver {
    private BallisticsResolver() {}

    public static Ballistics resolve(GunStats gun, AmmoStats ammo) {
        int penetrate = gun.penetrate() == -1 || ammo.penetrate() == -1
                ? -1
                : gun.penetrate() + ammo.penetrate();
        return new Ballistics(gun.damage() + ammo.damage(), gun.critical(), (gun.velocity() + ammo.velocity()) * ammo.velocityMultiplier(), gun.knockback() + ammo.knockback(), penetrate, gun.inaccuracy());
    }
}
