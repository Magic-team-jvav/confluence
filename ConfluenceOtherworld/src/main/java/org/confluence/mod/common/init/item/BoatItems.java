package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBoatTypes;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class BoatItems {
    public static final DeferredRegister.Items BOAT_ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items CHEST_BOAT_ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BoatItem> ASH_BOAT = boat("ash_boat", () -> ModBoatTypes.ASH.getValue());
    public static final DeferredItem<BoatItem> ASH_CHEST_BOAT = chestBoat("ash_chest_boat", () -> ModBoatTypes.ASH.getValue());

    public static final DeferredItem<BoatItem> BAOBAB_BOAT = boat("baobab_boat", () -> ModBoatTypes.BAOBAB.getValue());
    public static final DeferredItem<BoatItem> BAOBAB_CHEST_BOAT = chestBoat("baobab_chest_boat", () -> ModBoatTypes.BAOBAB.getValue());

    public static final DeferredItem<BoatItem> EBONY_BOAT = boat("ebony_boat", () -> ModBoatTypes.EBONY.getValue());
    public static final DeferredItem<BoatItem> EBONY_CHEST_BOAT = chestBoat("ebony_chest_boat", () -> ModBoatTypes.EBONY.getValue());

    public static final DeferredItem<BoatItem> GLOWING_MUSHROOM_BOAT = boat("glowing_mushroom_boat", () -> ModBoatTypes.GLOWING_MUSHROOM.getValue());
    public static final DeferredItem<BoatItem> GLOWING_MUSHROOM_CHEST_BOAT = chestBoat("glowing_mushroom_chest_boat", () -> ModBoatTypes.GLOWING_MUSHROOM.getValue());

    public static final DeferredItem<BoatItem> LIVING_BOAT = boat("living_boat", () -> ModBoatTypes.LIVING.getValue());
    public static final DeferredItem<BoatItem> LIVING_CHEST_BOAT = chestBoat("living_chest_boat", () -> ModBoatTypes.LIVING.getValue());

    public static final DeferredItem<BoatItem> LIVING_MAHOGANY_BOAT = boat("living_mahogany_boat", () -> ModBoatTypes.LIVING_MAHOGANY.getValue());
    public static final DeferredItem<BoatItem> LIVING_MAHOGANY_CHEST_BOAT = chestBoat("living_mahogany_chest_boat", () -> ModBoatTypes.LIVING_MAHOGANY.getValue());

    public static final DeferredItem<BoatItem> PALM_BOAT = boat("palm_boat", () -> ModBoatTypes.PALM.getValue());
    public static final DeferredItem<BoatItem> PALM_CHEST_BOAT = chestBoat("palm_chest_boat", () -> ModBoatTypes.PALM.getValue());

    public static final DeferredItem<BoatItem> PEARL_BOAT = boat("pearl_boat", () -> ModBoatTypes.PEARL.getValue());
    public static final DeferredItem<BoatItem> PEARL_CHEST_BOAT = chestBoat("pearl_chest_boat", () -> ModBoatTypes.PEARL.getValue());

    public static final DeferredItem<BoatItem> SHADOW_BOAT = boat("shadow_boat", () -> ModBoatTypes.SHADOW.getValue());
    public static final DeferredItem<BoatItem> SHADOW_CHEST_BOAT = chestBoat("shadow_chest_boat", () -> ModBoatTypes.SHADOW.getValue());

    public static final DeferredItem<BoatItem> SPOOKY_BOAT = boat("spooky_boat", () -> ModBoatTypes.SPOOKY.getValue());
    public static final DeferredItem<BoatItem> SPOOKY_CHEST_BOAT = chestBoat("spooky_chest_boat", () -> ModBoatTypes.SPOOKY.getValue());

    public static final DeferredItem<BoatItem> YELLOW_WILLOW_BOAT = boat("yellow_willow_boat", () -> ModBoatTypes.YELLOW_WILLOW.getValue());
    public static final DeferredItem<BoatItem> YELLOW_WILLOW_CHEST_BOAT = chestBoat("yellow_willow_chest_boat", () -> ModBoatTypes.YELLOW_WILLOW.getValue());

    public static final DeferredItem<BoatItem> DYNASTY_BOAT = boat("dynasty_boat", () -> ModBoatTypes.DYNASTY.getValue());
    public static final DeferredItem<BoatItem> DYNASTY_CHEST_BOAT = chestBoat("dynasty_chest_boat", () -> ModBoatTypes.DYNASTY.getValue());

    private static DeferredItem<BoatItem> boat(String name, Supplier<Boat.Type> type) {
        return BOAT_ITEMS.register(name, () -> new BoatItem(false, type.get(), new Item.Properties().stacksTo(1)));
    }

    private static DeferredItem<BoatItem> chestBoat(String name, Supplier<Boat.Type> type) {
        return CHEST_BOAT_ITEMS.register(name, () -> new BoatItem(true, type.get(), new Item.Properties().stacksTo(1)));
    }

    public static void forEach(Consumer<DeferredHolder<Item, ? extends Item>> action) {
        BOAT_ITEMS.getEntries().forEach(action);
        CHEST_BOAT_ITEMS.getEntries().forEach(action);
    }

    public static void register(IEventBus eventBus) {
        BOAT_ITEMS.register(eventBus);
        CHEST_BOAT_ITEMS.register(eventBus);
    }
}
