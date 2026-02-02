package org.confluence.terra_curio.common.event;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.common.init.TCCommonConfigs;
import org.confluence.terra_curio.network.InfoDisablePacket;
import org.confluence.terra_curio.network.c2s.*;
import org.confluence.terra_curio.network.s2c.*;

@EventBusSubscriber(modid = TerraCurio.MODID)
public final class ModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
//            TCCommonConfigs.onLoad();
            NeoForgeMod.enableMergedAttributeTooltips();
        });
    }

    @SubscribeEvent
    public static void modConfig$Loading(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && TerraCurio.MODID.equals(event.getConfig().getModId())) {
            TCCommonConfigs.onLoad();
        }
    }

    @SubscribeEvent
    public static void modConfig$Reloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && TerraCurio.MODID.equals(event.getConfig().getModId())) {
            TCCommonConfigs.onLoad();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.ATTRIBUTE) {
            TCAttributes.prepareReplacements();
        }
    }

    @SubscribeEvent
    public static void entityAttributeModification(EntityAttributeModificationEvent event) {
        TCAttributes.registerAttribute(TCAttributes.CRIT_CHANCE, event::add);
        TCAttributes.registerAttribute(TCAttributes.RANGED_VELOCITY, event::add);
        TCAttributes.registerAttribute(TCAttributes.RANGED_DAMAGE, event::add);
        TCAttributes.registerAttribute(TCAttributes.DODGE_CHANCE, event::add);
        TCAttributes.registerAttribute(TCAttributes.AGGRO, event::add);
        if (ConfluenceMagicLib.IS_CONFLUENCE_LOADED.get()) {
            TCAttributes.registerAttribute(TCAttributes.MAGIC_DAMAGE, event::add);
        }
        TCAttributes.registerAttribute(TCAttributes.ARMOR_PENETRATION, event::add);
        TCAttributes.registerAttribute(TCAttributes.PICKUP_RANGE, event::add);
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(GravitationPacketC2S.TYPE, GravitationPacketC2S.STREAM_CODEC, GravitationPacketC2S::handle);
        registrar.playToServer(StepStoolSteppingPacketC2S.TYPE, StepStoolSteppingPacketC2S.STREAM_CODEC, StepStoolSteppingPacketC2S::handle);
        registrar.playToServer(PlayerJumpPacketC2S.TYPE, PlayerJumpPacketC2S.STREAM_CODEC, PlayerJumpPacketC2S::handle);
        registrar.playToServer(SpeedBootsNBTPacketC2S.TYPE, SpeedBootsNBTPacketC2S.STREAM_CODEC, SpeedBootsNBTPacketC2S::handle);
        registrar.playToServer(PlayerSprintPacketC2S.TYPE, PlayerSprintPacketC2S.STREAM_CODEC, PlayerSprintPacketC2S::handle);
        registrar.playToServer(ShootXBonePacketC2S.TYPE, ShootXBonePacketC2S.STREAM_CODEC, ShootXBonePacketC2S::handle);

        registrar.playToClient(BroadcastGravitationRotPacketS2C.TYPE, BroadcastGravitationRotPacketS2C.STREAM_CODEC, BroadcastGravitationRotPacketS2C::handle);
        registrar.playToClient(CurioExistsPacketS2C.TYPE, CurioExistsPacketS2C.STREAM_CODEC, CurioExistsPacketS2C::handle);
        registrar.playToClient(EntityKilledPacketS2C.TYPE, EntityKilledPacketS2C.STREAM_CODEC, EntityKilledPacketS2C::handle);
        registrar.playToClient(InfoCurioCheckPacketS2C.TYPE, InfoCurioCheckPacketS2C.STREAM_CODEC, InfoCurioCheckPacketS2C::handle);
        registrar.playToClient(StepStoolSteppingPacketS2C.TYPE, StepStoolSteppingPacketS2C.STREAM_CODEC, StepStoolSteppingPacketS2C::handle);
        registrar.playToClient(PlayerJumpPacketS2C.TYPE, PlayerJumpPacketS2C.STREAM_CODEC, PlayerJumpPacketS2C::handle);
        registrar.playToClient(PlayerFlyPacketS2C.TYPE, PlayerFlyPacketS2C.STREAM_CODEC, PlayerFlyPacketS2C::handle);
        registrar.playToClient(PlayerClimbPacketS2C.TYPE, PlayerClimbPacketS2C.STREAM_CODEC, PlayerClimbPacketS2C::handle);
        registrar.playToClient(RightClickSubtractorPacketS2C.TYPE, RightClickSubtractorPacketS2C.STREAM_CODEC, RightClickSubtractorPacketS2C::handle);
        registrar.playToClient(SetItemEntityPickupDelayPacketS2C.TYPE, SetItemEntityPickupDelayPacketS2C.STREAM_CODEC, SetItemEntityPickupDelayPacketS2C::handle);
        registrar.playToClient(BroadcastRenderPacketS2C.TYPE, BroadcastRenderPacketS2C.STREAM_CODEC, BroadcastRenderPacketS2C::handle);
        registrar.playToClient(InfiniteFlightPacketS2C.TYPE, InfiniteFlightPacketS2C.STREAM_CODEC, InfiniteFlightPacketS2C::handle);
        registrar.playToClient(FluidWalkUpdatePacketS2C.TYPE, FluidWalkUpdatePacketS2C.STREAM_CODEC, FluidWalkUpdatePacketS2C::handle);

        registrar.playBidirectional(InfoDisablePacket.TYPE, InfoDisablePacket.STREAM_CODEC, InfoDisablePacket::handle);
    }
}
