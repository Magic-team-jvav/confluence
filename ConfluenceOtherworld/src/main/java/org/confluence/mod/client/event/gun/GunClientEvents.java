package org.confluence.mod.client.event.gun;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.impl.BulletHandler;
import org.confluence.mod.network.c2s.ShootPacketC2S;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.client.PortClientTickEvent;
import org.mesdag.portlib.event.client.PortInputEvent;

public final class GunClientEvents {
    public static void init() {
        PortEventHandler.addListener(GunClientEvents::gunShot);
        PortEventHandler.addListener(GunClientEvents::cancelSwap);
    }

    private static void gunShot(PortClientTickEvent.PortPost event) {
        KeyMapping shoot = ModKeyBindings.GUN_SHOOT.get();
        if (shoot.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) return;

            ItemStack mainHandItem = player.getMainHandItem();
            ItemCooldowns cooldowns = player.getCooldowns();
            if (mainHandItem.getItem() instanceof BaseGun baseGun && !cooldowns.isOnCooldown(baseGun)) {
                if (mainHandItem.is(ModTags.Items.MANUAL_GUN) && !shoot.consumeClick()) return;

                GunEvent.UseGunEvent useGunEvent = new GunEvent.UseGunEvent(player, baseGun, baseGun.getCooldown());
                PortEventHandler.postEvent(useGunEvent);
                if (useGunEvent.isCanceled() || !BulletHandler.canShoot(player, mainHandItem))
                    return;

                player.playSound(GunSounds.getSound(mainHandItem), 1f, 1f);
                ShootPacketC2S.sendToServer();
                cooldowns.addCooldown(baseGun, useGunEvent.getCooldowns());
            }
        }
    }

    private static void cancelSwap(PortInputEvent.PortInteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getItemInHand(event.getHand()).getItem() instanceof BaseGun) {
            event.setSwingHand(false);
            if (event.isAttack()) event.setCanceled(true);
        }
    }
}
