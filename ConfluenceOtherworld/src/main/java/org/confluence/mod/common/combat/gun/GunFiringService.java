package org.confluence.mod.common.combat.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.component.GunPropertyComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.item.gun.BaseGun;
import org.mesdag.portlib.event.PortEventHandler;

/// 解析枪械与弹药数值，并交给投射物工厂。
public final class GunFiringService {
    public static int fire(ServerPlayer player, BaseGun gun, ItemStack gunStack, ItemStack ammo) {
        if (ammo == null) return 0;
        GunPropertyComponent gunProperties = gunStack.get(ModDataComponentTypes.GUN_PROPERTY);
        if (gunProperties == null) gunProperties = gun.getDefinition().component();
        BulletPropertyComponent ammoProperties = ammo.get(ModDataComponentTypes.BULLET_PROPERTY);
        if (ammoProperties == null && ammo.getItem() instanceof BaseBullet bullet)
            ammoProperties = bullet.getDefinition().component();
        if (ammoProperties == null) ammoProperties = BulletPropertyComponent.EMPTY;

        Ballistics ballistics = BallisticsResolver.resolve(
                new GunStats(gunProperties.damage(), gunProperties.velocity(), gunProperties.knockback(),
                        gunProperties.critical(), gunProperties.penetrate(), gun.getDefinition().inaccuracy()),
                new AmmoStats(ammoProperties.damage(), ammoProperties.velocity(), ammoProperties.velocityMultiplier(),
                        ammoProperties.knockback(), ammoProperties.penetrate()));
        GunEvent.AmmoDataEvent event = new GunEvent.AmmoDataEvent(player, gun, gunStack, ballistics.damage(),
                ballistics.critical(), ballistics.knockback(), ballistics.velocity(), ballistics.penetrate(), ballistics.inaccuracy());
        PortEventHandler.postEvent(event);
        float damage = LibMathUtils.criticalDamageTotal(event.getCritical(), event.getDamage(), player.getRandom());
        ShotContext context = new ShotContext(player, gunStack, ammo, damage, event.getKnockback(), event.getVelocity(),
                event.getPenetrate(), event.getInaccuracy());
        return GunProjectileFactory.spawn(context, gun.getDefinition().projectilePattern());
    }

    public static boolean isInfinite(ItemStack ammo) {
        BulletPropertyComponent component = ammo.get(ModDataComponentTypes.BULLET_PROPERTY);
        return component != null && component.infinity();
    }

    private GunFiringService() {}
}
