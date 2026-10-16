package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBoatTypes;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

@SuppressWarnings("all")
public class BoatItems {
    public static final DeferredRegister.Items BOAT_ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items CHEST_BOAT_ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BoatItem> ASH_BOAT = boat("ash_boat", "灰烬木船", () -> ModBoatTypes.ASH.getValue());
    public static final DeferredItem<BoatItem> ASH_CHEST_BOAT = chestBoat("ash_chest_boat", "灰烬木运输船", () -> ModBoatTypes.ASH.getValue());

    public static final DeferredItem<BoatItem> BAOBAB_BOAT = boat("baobab_boat", "猴面包木船", () -> ModBoatTypes.BAOBAB.getValue());
    public static final DeferredItem<BoatItem> BAOBAB_CHEST_BOAT = chestBoat("baobab_chest_boat", "猴面包木运输船", () -> ModBoatTypes.BAOBAB.getValue());

    public static final DeferredItem<BoatItem> EBONY_BOAT = boat("ebony_boat", "乌木船", () -> ModBoatTypes.EBONY.getValue());
    public static final DeferredItem<BoatItem> EBONY_CHEST_BOAT = chestBoat("ebony_chest_boat", "乌木运输船", () -> ModBoatTypes.EBONY.getValue());

    public static final DeferredItem<BoatItem> GLOWING_MUSHROOM_BOAT = boat("glowing_mushroom_boat", "发光蘑菇船", () -> ModBoatTypes.GLOWING_MUSHROOM.getValue());
    public static final DeferredItem<BoatItem> GLOWING_MUSHROOM_CHEST_BOAT = chestBoat("glowing_mushroom_chest_boat", "发光蘑菇运输船", () -> ModBoatTypes.GLOWING_MUSHROOM.getValue());

    public static final DeferredItem<BoatItem> LIVING_BOAT = boat("living_boat", "生命木船", () -> ModBoatTypes.LIVING.getValue());
    public static final DeferredItem<BoatItem> LIVING_CHEST_BOAT = chestBoat("living_chest_boat", "生命木运输船", () -> ModBoatTypes.LIVING.getValue());

    public static final DeferredItem<BoatItem> LIVING_MAHOGANY_BOAT = boat("living_mahogany_boat", "生命红木船", () -> ModBoatTypes.LIVING_MAHOGANY.getValue());
    public static final DeferredItem<BoatItem> LIVING_MAHOGANY_CHEST_BOAT = chestBoat("living_mahogany_chest_boat", "生命红木运输船", () -> ModBoatTypes.LIVING_MAHOGANY.getValue());

    public static final DeferredItem<BoatItem> PALM_BOAT = boat("palm_boat", "棕榈木船", () -> ModBoatTypes.PALM.getValue());
    public static final DeferredItem<BoatItem> PALM_CHEST_BOAT = chestBoat("palm_chest_boat", "棕榈木运输船", () -> ModBoatTypes.PALM.getValue());

    public static final DeferredItem<BoatItem> PEARL_BOAT = boat("pearl_boat", "珍珠木船", () -> ModBoatTypes.PEARL.getValue());
    public static final DeferredItem<BoatItem> PEARL_CHEST_BOAT = chestBoat("pearl_chest_boat", "珍珠木运输船", () -> ModBoatTypes.PEARL.getValue());

    public static final DeferredItem<BoatItem> SHADOW_BOAT = boat("shadow_boat", "暗影木船", () -> ModBoatTypes.SHADOW.getValue());
    public static final DeferredItem<BoatItem> SHADOW_CHEST_BOAT = chestBoat("shadow_chest_boat", "暗影木运输船", () -> ModBoatTypes.SHADOW.getValue());

    public static final DeferredItem<BoatItem> SPOOKY_BOAT = boat("spooky_boat", "阴森木船", () -> ModBoatTypes.SPOOKY.getValue());
    public static final DeferredItem<BoatItem> SPOOKY_CHEST_BOAT = chestBoat("spooky_chest_boat", "阴森木运输船", () -> ModBoatTypes.SPOOKY.getValue());

    public static final DeferredItem<BoatItem> YELLOW_WILLOW_BOAT = boat("yellow_willow_boat", "黄柳木船", () -> ModBoatTypes.YELLOW_WILLOW.getValue());
    public static final DeferredItem<BoatItem> YELLOW_WILLOW_CHEST_BOAT = chestBoat("yellow_willow_chest_boat", "黄柳木运输船", () -> ModBoatTypes.YELLOW_WILLOW.getValue());

    public static final DeferredItem<BoatItem> DYNASTY_BOAT = boat("dynasty_boat", "王朝木船", () -> ModBoatTypes.DYNASTY.getValue());
    public static final DeferredItem<BoatItem> DYNASTY_CHEST_BOAT = chestBoat("dynasty_chest_boat", "王朝木运输船", () -> ModBoatTypes.DYNASTY.getValue());

    public static final DeferredItem<BoatItem> PINE_BOAT = boat("pine_boat", "松木船", () -> ModBoatTypes.PINE.getValue());
    public static final DeferredItem<BoatItem> PINE_CHEST_BOAT = chestBoat("pine_chest_boat", "松木运输船", () -> ModBoatTypes.PINE.getValue());

    public static final DeferredItem<BoatItem> FEY_BOAT = boat("fey_boat", "仙灵木船", () -> ModBoatTypes.FEY.getValue());
    public static final DeferredItem<BoatItem> FEY_CHEST_BOAT = chestBoat("fey_chest_boat", "仙灵木运输船", () -> ModBoatTypes.FEY.getValue());

    public static void register(IEventBus eventBus) {
        BOAT_ITEMS.register(eventBus);
        CHEST_BOAT_ITEMS.register(eventBus);
    }

    private static DeferredItem<BoatItem> boat(String registryId, String chineseName, Supplier<Boat.Type> type) {
        DeferredItem<BoatItem> entry = BOAT_ITEMS.register(registryId, () -> new BoatItem(false, type.get(), new Item.Properties().stacksTo(1)));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<BoatItem> chestBoat(String registryId, String chineseName, Supplier<Boat.Type> type) {
        DeferredItem<BoatItem> entry = CHEST_BOAT_ITEMS.register(registryId, () -> new BoatItem(true, type.get(), new Item.Properties().stacksTo(1)));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
