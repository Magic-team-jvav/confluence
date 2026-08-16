package org.confluence.mod.common.combat.gun;

/// 合并枪械与弹药数值。
///
/// <p>这里只做一次纯数值合成，不抽取暴击，也不应用玩家属性。最终伤害和暴击由
/// MagicLib 的发射快照统一裁定，防止特殊箭或特殊子弹被重复增伤。</p>
public final class BallisticsResolver {
    private BallisticsResolver() {}

    public static Ballistics resolve(GunStats gun, AmmoStats ammo) {
        int penetrate = gun.penetrate() == -1 || ammo.penetrate() == -1
                ? -1
                : gun.penetrate() + ammo.penetrate();
        return new Ballistics(
                gun.damage() + ammo.damage(),
                gun.critical(),
                (gun.velocity() + ammo.velocity()) * ammo.velocityMultiplier(),
                gun.knockback() + ammo.knockback(),
                penetrate,
                gun.inaccuracy()
        );
    }
}
