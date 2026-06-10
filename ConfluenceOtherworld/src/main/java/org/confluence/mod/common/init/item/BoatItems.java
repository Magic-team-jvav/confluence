package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.neoforged.bus.api.IEventBus;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBoatTypes;

import java.util.function.Supplier;

@SuppressWarnings("all")
public class BoatItems {
    public static final DeferredRegister<Item> BOAT_ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);
    public static final DeferredRegister<Item> CHEST_BOAT_ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<BoatItem> ASH_BOAT = boat("ash_boat", () -> ModBoatTypes.ASH.getValue());
    public static final RegistryObject<BoatItem> ASH_CHEST_BOAT = chestBoat("ash_chest_boat", () -> ModBoatTypes.ASH.getValue());

    public static final RegistryObject<BoatItem> BAOBAB_BOAT = boat("baobab_boat", () -> ModBoatTypes.BAOBAB.getValue());
    public static final RegistryObject<BoatItem> BAOBAB_CHEST_BOAT = chestBoat("baobab_chest_boat", () -> ModBoatTypes.BAOBAB.getValue());

    public static final RegistryObject<BoatItem> EBONY_BOAT = boat("ebony_boat", () -> ModBoatTypes.EBONY.getValue());
    public static final RegistryObject<BoatItem> EBONY_CHEST_BOAT = chestBoat("ebony_chest_boat", () -> ModBoatTypes.EBONY.getValue());

    public static final RegistryObject<BoatItem> GLOWING_MUSHROOM_BOAT = boat("glowing_mushroom_boat", () -> ModBoatTypes.GLOWING_MUSHROOM.getValue());
    public static final RegistryObject<BoatItem> GLOWING_MUSHROOM_CHEST_BOAT = chestBoat("glowing_mushroom_chest_boat", () -> ModBoatTypes.GLOWING_MUSHROOM.getValue());

    public static final RegistryObject<BoatItem> LIVING_BOAT = boat("living_boat", () -> ModBoatTypes.LIVING.getValue());
    public static final RegistryObject<BoatItem> LIVING_CHEST_BOAT = chestBoat("living_chest_boat", () -> ModBoatTypes.LIVING.getValue());

    public static final RegistryObject<BoatItem> LIVING_MAHOGANY_BOAT = boat("living_mahogany_boat", () -> ModBoatTypes.LIVING_MAHOGANY.getValue());
    public static final RegistryObject<BoatItem> LIVING_MAHOGANY_CHEST_BOAT = chestBoat("living_mahogany_chest_boat", () -> ModBoatTypes.LIVING_MAHOGANY.getValue());

    public static final RegistryObject<BoatItem> PALM_BOAT = boat("palm_boat", () -> ModBoatTypes.PALM.getValue());
    public static final RegistryObject<BoatItem> PALM_CHEST_BOAT = chestBoat("palm_chest_boat", () -> ModBoatTypes.PALM.getValue());

    public static final RegistryObject<BoatItem> PEARL_BOAT = boat("pearl_boat", () -> ModBoatTypes.PEARL.getValue());
    public static final RegistryObject<BoatItem> PEARL_CHEST_BOAT = chestBoat("pearl_chest_boat", () -> ModBoatTypes.PEARL.getValue());

    public static final RegistryObject<BoatItem> SHADOW_BOAT = boat("shadow_boat", () -> ModBoatTypes.SHADOW.getValue());
    public static final RegistryObject<BoatItem> SHADOW_CHEST_BOAT = chestBoat("shadow_chest_boat", () -> ModBoatTypes.SHADOW.getValue());

    public static final RegistryObject<BoatItem> SPOOKY_BOAT = boat("spooky_boat", () -> ModBoatTypes.SPOOKY.getValue());
    public static final RegistryObject<BoatItem> SPOOKY_CHEST_BOAT = chestBoat("spooky_chest_boat", () -> ModBoatTypes.SPOOKY.getValue());

    public static final RegistryObject<BoatItem> YELLOW_WILLOW_BOAT = boat("yellow_willow_boat", () -> ModBoatTypes.YELLOW_WILLOW.getValue());
    public static final RegistryObject<BoatItem> YELLOW_WILLOW_CHEST_BOAT = chestBoat("yellow_willow_chest_boat", () -> ModBoatTypes.YELLOW_WILLOW.getValue());

    public static final RegistryObject<BoatItem> DYNASTY_BOAT = boat("dynasty_boat", () -> ModBoatTypes.DYNASTY.getValue());
    public static final RegistryObject<BoatItem> DYNASTY_CHEST_BOAT = chestBoat("dynasty_chest_boat", () -> ModBoatTypes.DYNASTY.getValue());

    public static final RegistryObject<BoatItem> PINE_BOAT = boat("pine_boat", () -> ModBoatTypes.PINE.getValue());
    public static final RegistryObject<BoatItem> PINE_CHEST_BOAT = chestBoat("pine_chest_boat", () -> ModBoatTypes.PINE.getValue());

    public static final RegistryObject<BoatItem> FEY_BOAT = boat("fey_boat", () -> ModBoatTypes.FEY.getValue());
    public static final RegistryObject<BoatItem> FEY_CHEST_BOAT = chestBoat("fey_chest_boat", () -> ModBoatTypes.FEY.getValue());

    private static RegistryObject<BoatItem> boat(String name, Supplier<Boat.Type> type) {
        return BOAT_ITEMS.register(name, () -> new BoatItem(false, type.get(), new Item.Properties().stacksTo(1)));
    }

    private static RegistryObject<BoatItem> chestBoat(String name, Supplier<Boat.Type> type) {
        return CHEST_BOAT_ITEMS.register(name, () -> new BoatItem(true, type.get(), new Item.Properties().stacksTo(1)));
    }

    public static void register(IEventBus eventBus) {
        BOAT_ITEMS.register(eventBus);
        CHEST_BOAT_ITEMS.register(eventBus);
    }
}
