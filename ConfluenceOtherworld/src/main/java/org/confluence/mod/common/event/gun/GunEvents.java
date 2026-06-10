package org.confluence.mod.common.event.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.event.NameFixRegisterEvent;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.network.c2s.ShootPacketC2S;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.entity.living.PortLivingEquipmentChangeEvent;
import org.mesdag.portlib.event.entity.living.PortLivingIncomingDamageEvent;
import org.mesdag.portlib.event.lifecycle.PortFMLCommonSetupEvent;

public final class GunEvents {
    public static void init() {
        Confluence.NETWORK_HANDLER.registerInGameC2S(ShootPacketC2S.class, ShootPacketC2S.ID, ShootPacketC2S.STREAM_CODEC);
        PortEventHandler.addListener(GunEvents::fmlCommonSetup);
        PortEventHandler.addListener(GunEvents::livingEquipmentChange);
        PortEventHandler.addListener(GunEvents::livingIncomingDamage);
        PortEventHandler.addListener(GunEvents::nameFixRegister);
    }

    private static void fmlCommonSetup(PortFMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GunSounds.init();
            GunTrailColors.init();
        });
    }

    private static void livingEquipmentChange(PortLivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getSlot() == EquipmentSlot.MAINHAND && event.getTo().getItem() instanceof BaseGun baseGun) {
                baseGun.pickAnimator(event.getTo(), player);
            }
        }
    }

    private static void livingIncomingDamage(PortLivingIncomingDamageEvent event) {
        if (event.getSource().is(LibDamageTypes.BULLET_DAMAGE)) {
            if (ConfluenceMagicLib.IS_CONFLUENCE_LOAD) return;
            event.setInvulnerabilityTicks(0);
        }
    }

    private static void nameFixRegister(NameFixRegisterEvent.Item event) {
        event.register("terra_guns:blowpipe", "confluence:blowgun");
    }
}
