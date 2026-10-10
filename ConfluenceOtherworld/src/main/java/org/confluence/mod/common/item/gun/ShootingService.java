package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.network.s2c.GunMuzzleFlashPacketS2C;
import org.confluence.mod.util.ModGunUtils;

/// 服务端统一校验开火条件、消耗弹药并设置冷却。
public final class ShootingService {
    public static boolean tryShoot(ServerPlayer player) {
        if (player.isSpectator()) return false;
        ItemStack gunStack = player.getMainHandItem();
        if (!(gunStack.getItem() instanceof BaseGun gun) || player.getCooldowns().isOnCooldown(gun))
            return false;

        GunEvent.Use useEvent = new GunEvent.Use(player, gun, gun.getCooldown());
        NeoForge.EVENT_BUS.post(useEvent);
        if (useEvent.isCanceled()) return false;

        ItemStack ammo = ModGunUtils.getAmmo(player, gunStack);
        GunEvent.Fire fireEvent = new GunEvent.Fire(player, gun, ammo, !ammo.isEmpty());
        NeoForge.EVENT_BUS.post(fireEvent);
        if (!fireEvent.isFire() || fireEvent.getAmmo() == null) return false;
        ItemStack selectedAmmo = fireEvent.getAmmo();

        int projectileCount = GunFiringService.fire(player, gun, gunStack, selectedAmmo);
        if (projectileCount <= 0) return false;
        gun.fireAnimator(gunStack, player);
        GunMuzzleFlashPacketS2C.send(player);
        consumeAmmo(player, gun, gunStack, selectedAmmo);
        int cooldown = Math.max(0, useEvent.getCooldowns());
        if (cooldown > 0) player.getCooldowns().addCooldown(gun, cooldown);
        return true;
    }

    private static void consumeAmmo(ServerPlayer player, BaseGun gun, ItemStack gunStack, ItemStack ammo) {
        if (ammo.isEmpty()) return;
        GunEvent.ShrinkBullet event = new GunEvent.ShrinkBullet(player, gun, gunStack, ammo, GunFiringService.isInfinite(ammo));
        NeoForge.EVENT_BUS.post(event);
        ItemStack bulletStack = event.getBulletStack();
        int shrink = Math.max(0, event.getShrink());
        if (!event.isInfinity() && !event.isCanceled() && bulletStack != null && shrink > 0)
            bulletStack.shrink(shrink);
    }

    private ShootingService() {}
}
