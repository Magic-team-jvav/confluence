package org.confluence.terra_guns.client.event;

import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.api.event.GunEvent;
import org.confluence.terra_guns.client.init.TGKeys;
import org.confluence.terra_guns.common.init.TGGunSounds;
import org.confluence.terra_guns.common.init.TGTags;
import org.confluence.terra_guns.common.item.gun.BaseGun;
import org.confluence.terra_guns.impl.BulletHandler;
import org.confluence.terra_guns.network.c2s.ShootPacketC2S;

@EventBusSubscriber(modid = TerraGuns.MODID, value = Dist.CLIENT)
public class GameEvent {
    private static final Minecraft minecraft = Minecraft.getInstance();

    @SubscribeEvent
    public static void firstPersonHandRender(RenderHandEvent event) {
        float equipProgress = event.getEquipProgress();
    }

    @SubscribeEvent
    public static void firstPersonArmRender(RenderArmEvent event) {
        HumanoidArm arm = event.getArm();
    }

    @SubscribeEvent
    public static void cameraDis(CalculateDetachedCameraDistanceEvent event) {
        Camera camera = event.getCamera();
    }

    @SubscribeEvent
    public static void gunShot(ClientTickEvent.Post event) {
        KeyMapping shoot = TGKeys.SHOOT.get();
        if (shoot.isDown()) {
            LocalPlayer player = minecraft.player;
            if (player.isSpectator()) return;

            ItemStack mainHandItem = player.getMainHandItem();
            ItemCooldowns cooldowns = player.getCooldowns();
            if (mainHandItem.getItem() instanceof BaseGun baseGun && !cooldowns.isOnCooldown(baseGun)){
                if (mainHandItem.is(TGTags.MANUAL_GUN) && !shoot.consumeClick()) return;

                GunEvent.UseGunEvent useGunEvent = new GunEvent.UseGunEvent(player, baseGun, baseGun.getCooldown());
                NeoForge.EVENT_BUS.post(useGunEvent);
                if (useGunEvent.isCanceled() || !BulletHandler.canShoot(player, mainHandItem)) return;

                player.playSound(TGGunSounds.getSound(mainHandItem), 1f, 1f);
                ShootPacketC2S.sendToServer();
                cooldowns.addCooldown(baseGun, useGunEvent.getCooldowns());
            }

        }
    }

    @SubscribeEvent
    public static void cancelSwap(InputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = minecraft.player;
        if (player.getItemInHand(event.getHand()).getItem() instanceof BaseGun) {
            event.setSwingHand(false);
            if (event.isAttack()) event.setCanceled(true);
        }
    }
}
