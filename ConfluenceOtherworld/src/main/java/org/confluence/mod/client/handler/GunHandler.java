package org.confluence.mod.client.handler;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.api.animation.first_person.CameraAnimation;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.renderer.entity.bullet.BulletVfxManager;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.network.c2s.InspectPacketC2S;
import org.confluence.mod.network.c2s.ShootPacketC2S;
import org.confluence.mod.util.ModGunUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.PortEventHandler;
import software.bernie.geckolib.animatable.GeoItem;

public final class GunHandler {
    private static boolean wasDefaultGunShootHeld = false;

    public static void handle(@Nullable LocalPlayer player, boolean attackHeld) {
        if (player == null) {
            CameraAnimation.clear();
            wasDefaultGunShootHeld = false;
            return;
        }
        inspect(player);

        BulletVfxManager.tick();
        KeyMapping shoot = ModKeyBindings.GUN_SHOOT.get();

        updateGunCameraAnimation(player);
        boolean defaultBindingHeld = shoot.isDefault() && attackHeld;
        boolean shootHeld = shoot.isDown() || defaultBindingHeld;
        boolean defaultBindingPressed = defaultBindingHeld && !wasDefaultGunShootHeld;
        wasDefaultGunShootHeld = defaultBindingHeld;
        if (player.isSpectator() || !shootHeld) return;

        ItemStack mainHandItem = player.getMainHandItem();
        ItemCooldowns cooldowns = player.getCooldowns();
        if (!(mainHandItem.getItem() instanceof BaseGun baseGun) || cooldowns.isOnCooldown(baseGun))
            return;
        if (!ModGunUtils.canShoot(player, mainHandItem)) return;
        if (!baseGun.isAutomatic(mainHandItem) && !shoot.consumeClick() && !defaultBindingPressed)
            return;

        GunEvent.Use use = new GunEvent.Use(player, baseGun, baseGun.getCooldown());
        PortEventHandler.postEvent(use);
        if (use.isCanceled()) return;

        player.playSound(GunSounds.getSound(mainHandItem), 1f, 1f);
        ShootPacketC2S.sendToServer();
        cooldowns.addCooldown(baseGun, Math.max(0, use.getCooldowns()));
    }

    private static void updateGunCameraAnimation(LocalPlayer player) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof BaseGun gun) || !gun.isCameraAnimationPlaying(GeoItem.getId(mainHandItem))) {
            CameraAnimation.clear();
        }
    }

    private static void inspect(LocalPlayer player) {
        if (!player.isSpectator() && ModKeyBindings.GUN_INSPECT.get().consumeClick()) {
            if (player.getMainHandItem().getItem() instanceof BaseGun) {
                InspectPacketC2S.sendToServer();
            }
        }
    }

    public static void reset() {
        wasDefaultGunShootHeld = false;
    }
}
