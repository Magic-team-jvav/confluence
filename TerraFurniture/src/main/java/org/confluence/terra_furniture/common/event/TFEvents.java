package org.confluence.terra_furniture.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.confluence.lib.event.NameFixRegisterEvent;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.network.s2c.PlayerCrossDeltaS2C;

@EventBusSubscriber(modid = TerraFurniture.MODID)
public class TFEvents {
    @SubscribeEvent // on the mod event bus
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);
        registrar.playToClient(
            PlayerCrossDeltaS2C.TYPE,
            PlayerCrossDeltaS2C.STREAM_CODEC,
            PlayerCrossDeltaS2C::handle
        );
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            TFBlocks.HANGING_POT_ENTITY.get(),
            (blockEntity, direction) -> blockEntity.getLazyItemHandler().get()
        );
    }
    @SubscribeEvent
    public static void blockWithItemNameFixRegister(NameFixRegisterEvent.BlockWithItem event) {
        event
                // 1.2.0 Terra Furniture
                .register("terra_furniture:blue_brick_chair", "terra_furniture:blue_dungeon_chair")
                .register("terra_furniture:blue_brick_sofa", "terra_furniture:blue_dungeon_sofa")
                .register("terra_furniture:blue_brick_toilet", "terra_furniture:blue_dungeon_toilet")
                .register("terra_furniture:blue_brick_sink", "terra_furniture:blue_dungeon_sink")
                .register("terra_furniture:blue_brick_door", "terra_furniture:blue_dungeon_door")
                .register("terra_furniture:blue_brick_table", "terra_furniture:blue_dungeon_table")
                .register("terra_furniture:blue_brick_candle", "terra_furniture:blue_dungeon_candle")
                .register("terra_furniture:blue_brick_lantern", "terra_furniture:blue_dungeon_lantern")
                .register("terra_furniture:blue_brick_lamp", "terra_furniture:blue_dungeon_lamp")
                .register("terra_furniture:blue_brick_candelabras", "terra_furniture:blue_dungeon_candelabras")
                .register("terra_furniture:blue_brick_bathtub", "terra_furniture:blue_dungeon_bathtub")
                .register("terra_furniture:blue_brick_clock", "terra_furniture:blue_dungeon_clock")
                .register("terra_furniture:blue_brick_chandelier", "terra_furniture:blue_dungeon_chandelier")
                .register("terra_furniture:blue_dungeon_chandeliers", "terra_furniture:blue_dungeon_chandelier");

    }

}
