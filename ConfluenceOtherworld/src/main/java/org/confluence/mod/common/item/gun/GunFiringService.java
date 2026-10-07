package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.item.BaseBullet;
import org.confluence.mod.common.item.gun.definition.BulletDefinition;
import org.confluence.mod.common.item.gun.definition.GunDefinition;
import org.mesdag.portlib.event.PortEventHandler;

/// 直接合并枪械和弹药定义中的参数，并交给射弹工厂。
public final class GunFiringService {
    public static int fire(ServerPlayer player, BaseGun gun, ItemStack gunStack, ItemStack ammo) {
        if (ammo == null) return 0;
        GunDefinition gunDefinition = gun.getDefinition();
        BulletDefinition ammoDefinition = ammo.getItem() instanceof BaseBullet bullet ? bullet.getDefinition() : null;
        /// 非子弹物品保留零加成与单位速度倍率，不引入额外射弹行为。
        float ammoDamage = ammoDefinition == null ? 0.0F : ammoDefinition.damage();
        float ammoVelocity = ammoDefinition == null ? 0.0F : ammoDefinition.velocity();
        float ammoVelocityMultiplier = ammoDefinition == null ? 1.0F : ammoDefinition.velocityMultiplier();
        float ammoKnockback = ammoDefinition == null ? 0.0F : ammoDefinition.knockback();
        int ammoPenetrate = ammoDefinition == null ? 0 : ammoDefinition.penetrate();
        int penetrate = gunDefinition.penetrate() == -1 || ammoPenetrate == -1
                ? -1 : gunDefinition.penetrate() + ammoPenetrate;
        GunEvent.AmmoData event = new GunEvent.AmmoData(player, gun, gunStack,
                gunDefinition.damage() + ammoDamage, gunDefinition.critical(),
                gunDefinition.knockback() + ammoKnockback,
                (gunDefinition.velocity() + ammoVelocity) * ammoVelocityMultiplier,
                penetrate, gunDefinition.inaccuracy());
        PortEventHandler.postEvent(event);
        float damage = LibMathUtils.criticalDamageTotal(event.getCritical(), event.getDamage(), player.getRandom1211());
        return GunProjectileFactory.spawn(new ShotContext(player, gunStack, ammo, damage, event.getKnockback(), event.getVelocity(), event.getPenetrate(), event.getInaccuracy()), gun.getDefinition().projectilePattern());
    }

    public static boolean isInfinite(ItemStack ammo) {
        return ammo.getItem() instanceof BaseBullet bullet && bullet.getDefinition().infinity();
    }

    private GunFiringService() {}
}
