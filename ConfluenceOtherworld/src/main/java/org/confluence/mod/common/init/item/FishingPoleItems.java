package org.confluence.mod.common.init.item;

import com.google.common.base.Supplier;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.fishing.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class FishingPoleItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<AbstractFishingPole> WOOD_FISHING_POLE = register("wood_fishing_pole", WoodFishingPole::new),
            REINFORCED_FISHING_POLE = register("reinforced_fishing_pole", ReinforcedFishingPole::new),
            FISHER_OF_SOULS = register("fisher_of_souls", FisherOfSouls::new),
            FLESHCATCHER = register("fleshcatcher", Fleshcatcher::new),
            SCARAB_FISHING_ROD = register("scarab_fishing_rod", ScarabFishingRod::new),
            CHUM_CASTER = register("chum_caster", ChumCaster::new),
            FIBERGLASS_FISHING_POLE = register("fiberglass_fishing_pole", FiberglassFishingPole::new),
            MECHANICS_ROD = register("mechanics_rod", MechanicsRod::new),
            SITTING_DUCKS_FISHING_POLE = register("sitting_ducks_fishing_pole", SittingDucksFishingPole::new),
            HOTLINE_FISHING_HOOK = register("hotline_fishing_hook", HotlineFishingHookItem::new),
            GOLDEN_FISHING_ROD = register("golden_fishing_rod", GoldenFishingRod::new),
            DEV_FISHING_ROD = register("dev_fishing_rod", DevFishingRod::new);

    private static PortDeferredItem<AbstractFishingPole> register(String name, Supplier<AbstractFishingPole> supplier) {
        return ITEMS.register(name, supplier);
    }
}
