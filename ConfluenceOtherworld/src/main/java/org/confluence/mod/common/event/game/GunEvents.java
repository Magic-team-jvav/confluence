package org.confluence.mod.common.event.game;

import org.confluence.mod.api.event.BulletEvent;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.item.gun.BeeGunItem;
import org.confluence.mod.common.item.gun.ManaGunItem;
import org.confluence.mod.common.item.gun.SpaceGunItem;
import org.confluence.mod.common.item.gun.StarCannonItem;
import org.confluence.mod.network.s2c.BulletImpactPacketS2C;
import org.mesdag.portlib.event.PortEventHandler;

import java.util.List;

public final class GunEvents {
    public static void init() {
        PortEventHandler.addListener(GunEvents::createProjectile);
        PortEventHandler.addListener(GunEvents::bulletImpact);
    }

    private static void createProjectile(GunEvent.ProjectileCreationEvent event) {
        if (event.getGun() instanceof StarCannonItem starCannon) {
            event.setProjectiles(List.of(starCannon.createProjectile(event.getContext().shooter(), event.getContext().ammo())));
            return;
        }
        if (event.getGun() instanceof BeeGunItem beeGun) {
            event.setProjectiles(beeGun.createProjectiles(event.getContext().shooter()));
            return;
        }
        if (event.getGun() instanceof ManaGunItem manaGun) {
            event.setProjectiles(List.of(manaGun.createProjectile(event.getContext().shooter(), event.getContext().ammo())));
        }
        if (event.getGun() instanceof SpaceGunItem) {
            event.getProjectiles().stream()
                    .filter(BaseBulletEntity.class::isInstance)
                    .map(BaseBulletEntity.class::cast)
                    .forEach(projectile -> projectile.setColorID("space_gun"));
        }
    }

    private static void bulletImpact(BulletEvent.HitEvent event) {
        if (!event.getBulletEntity().level().isClientSide) {
            BulletImpactPacketS2C.send(event.getBulletEntity(), event.getHitResult().getLocation());
        }
    }
}
