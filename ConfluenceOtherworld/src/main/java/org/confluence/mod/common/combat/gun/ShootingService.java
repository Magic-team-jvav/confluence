package org.confluence.mod.common.combat.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.util.ModGunUtils;
import org.mesdag.portlib.event.PortEventHandler;

/// 所有枪械射击请求的服务端权威入口。
public final class ShootingService {
    public static boolean tryShoot(ServerPlayer player) {
        if (player.isSpectator()) return false;
        ItemStack gunStack = player.getMainHandItem();
        if (!(gunStack.getItem() instanceof BaseGun gun) || player.getCooldowns().isOnCooldown(gun))
            return false;

        GunEvent.UseGunEvent useEvent = new GunEvent.UseGunEvent(player, gun, gun.getCooldown());
        PortEventHandler.postEvent(useEvent);
        if (useEvent.isCanceled()) return false;

        ItemStack ammo = ModGunUtils.getAmmo(player, gunStack);
        GunEvent.GunFireEvent fireEvent = new GunEvent.GunFireEvent(player, gun, ammo, !ammo.isEmpty());
        PortEventHandler.postEvent(fireEvent);
        if (!fireEvent.isFire() || fireEvent.getAmmo() == null) return false;
        ItemStack selectedAmmo = fireEvent.getAmmo();

        int projectileCount = GunFiringService.fire(player, gun, gunStack, selectedAmmo);
        if (projectileCount <= 0) return false;
        gun.fireAnimator(gunStack, player);
        consumeAmmo(player, gun, gunStack, selectedAmmo);
        int cooldown = Math.max(0, useEvent.getCooldowns());
        if (cooldown > 0) player.getCooldowns().addCooldown(gun, cooldown);
        return true;
    }

    private static void consumeAmmo(ServerPlayer player, BaseGun gun, ItemStack gunStack, ItemStack ammo) {
        if (ammo.isEmpty()) return;
        GunEvent.ShrinkBulletEvent event = new GunEvent.ShrinkBulletEvent(player, gun, gunStack, ammo, GunFiringService.isInfinite(ammo));
        PortEventHandler.postEvent(event);
        ItemStack bulletStack = event.getBulletStack();
        int shrink = Math.max(0, event.getShrink());
        if (!event.isInfinity() && !event.isCanceled() && bulletStack != null && shrink > 0)
            bulletStack.shrink(shrink);
    }

    private ShootingService() {}
}
