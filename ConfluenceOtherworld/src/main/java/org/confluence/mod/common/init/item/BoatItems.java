package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBoatTypes;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

@SuppressWarnings("all")
public class BoatItems {
    public static void init() {}

    public static final PortItemRegistration BOAT_ITEMS = PortRegisterHandler.item(Confluence.MODID);
    public static final PortItemRegistration CHEST_BOAT_ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BoatItem> ASH_BOAT = boat("ash_boat", () -> ModBoatTypes.ASH.getValue());
    public static final PortDeferredItem<BoatItem> ASH_CHEST_BOAT = chestBoat("ash_chest_boat", () -> ModBoatTypes.ASH.getValue());

    public static final PortDeferredItem<BoatItem> BAOBAB_BOAT = boat("baobab_boat", () -> ModBoatTypes.BAOBAB.getValue());
    public static final PortDeferredItem<BoatItem> BAOBAB_CHEST_BOAT = chestBoat("baobab_chest_boat", () -> ModBoatTypes.BAOBAB.getValue());

    public static final PortDeferredItem<BoatItem> EBONY_BOAT = boat("ebony_boat", () -> ModBoatTypes.EBONY.getValue());
    public static final PortDeferredItem<BoatItem> EBONY_CHEST_BOAT = chestBoat("ebony_chest_boat", () -> ModBoatTypes.EBONY.getValue());

    public static final PortDeferredItem<BoatItem> GLOWING_MUSHROOM_BOAT = boat("glowing_mushroom_boat", () -> ModBoatTypes.GLOWING_MUSHROOM.getValue());
    public static final PortDeferredItem<BoatItem> GLOWING_MUSHROOM_CHEST_BOAT = chestBoat("glowing_mushroom_chest_boat", () -> ModBoatTypes.GLOWING_MUSHROOM.getValue());

    public static final PortDeferredItem<BoatItem> LIVING_BOAT = boat("living_boat", () -> ModBoatTypes.LIVING.getValue());
    public static final PortDeferredItem<BoatItem> LIVING_CHEST_BOAT = chestBoat("living_chest_boat", () -> ModBoatTypes.LIVING.getValue());

    public static final PortDeferredItem<BoatItem> LIVING_MAHOGANY_BOAT = boat("living_mahogany_boat", () -> ModBoatTypes.LIVING_MAHOGANY.getValue());
    public static final PortDeferredItem<BoatItem> LIVING_MAHOGANY_CHEST_BOAT = chestBoat("living_mahogany_chest_boat", () -> ModBoatTypes.LIVING_MAHOGANY.getValue());

    public static final PortDeferredItem<BoatItem> PALM_BOAT = boat("palm_boat", () -> ModBoatTypes.PALM.getValue());
    public static final PortDeferredItem<BoatItem> PALM_CHEST_BOAT = chestBoat("palm_chest_boat", () -> ModBoatTypes.PALM.getValue());

    public static final PortDeferredItem<BoatItem> PEARL_BOAT = boat("pearl_boat", () -> ModBoatTypes.PEARL.getValue());
    public static final PortDeferredItem<BoatItem> PEARL_CHEST_BOAT = chestBoat("pearl_chest_boat", () -> ModBoatTypes.PEARL.getValue());

    public static final PortDeferredItem<BoatItem> SHADOW_BOAT = boat("shadow_boat", () -> ModBoatTypes.SHADOW.getValue());
    public static final PortDeferredItem<BoatItem> SHADOW_CHEST_BOAT = chestBoat("shadow_chest_boat", () -> ModBoatTypes.SHADOW.getValue());

    public static final PortDeferredItem<BoatItem> SPOOKY_BOAT = boat("spooky_boat", () -> ModBoatTypes.SPOOKY.getValue());
    public static final PortDeferredItem<BoatItem> SPOOKY_CHEST_BOAT = chestBoat("spooky_chest_boat", () -> ModBoatTypes.SPOOKY.getValue());

    public static final PortDeferredItem<BoatItem> YELLOW_WILLOW_BOAT = boat("yellow_willow_boat", () -> ModBoatTypes.YELLOW_WILLOW.getValue());
    public static final PortDeferredItem<BoatItem> YELLOW_WILLOW_CHEST_BOAT = chestBoat("yellow_willow_chest_boat", () -> ModBoatTypes.YELLOW_WILLOW.getValue());

    public static final PortDeferredItem<BoatItem> DYNASTY_BOAT = boat("dynasty_boat", () -> ModBoatTypes.DYNASTY.getValue());
    public static final PortDeferredItem<BoatItem> DYNASTY_CHEST_BOAT = chestBoat("dynasty_chest_boat", () -> ModBoatTypes.DYNASTY.getValue());

    public static final PortDeferredItem<BoatItem> PINE_BOAT = boat("pine_boat", () -> ModBoatTypes.PINE.getValue());
    public static final PortDeferredItem<BoatItem> PINE_CHEST_BOAT = chestBoat("pine_chest_boat", () -> ModBoatTypes.PINE.getValue());

    public static final PortDeferredItem<BoatItem> FEY_BOAT = boat("fey_boat", () -> ModBoatTypes.FEY.getValue());
    public static final PortDeferredItem<BoatItem> FEY_CHEST_BOAT = chestBoat("fey_chest_boat", () -> ModBoatTypes.FEY.getValue());

    private static PortDeferredItem<BoatItem> boat(String name, Supplier<Boat.Type> type) {
        return BOAT_ITEMS.register(name, () -> new BoatItem(false, type.get(), new Item.Properties().stacksTo(1)));
    }

    private static PortDeferredItem<BoatItem> chestBoat(String name, Supplier<Boat.Type> type) {
        return CHEST_BOAT_ITEMS.register(name, () -> new BoatItem(true, type.get(), new Item.Properties().stacksTo(1)));
    }
}
