package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.fishing.*;

import java.util.function.Supplier;

public class FishingPoleItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<AbstractFishingPole> WOOD_FISHING_POLE = register("wood_fishing_pole", WoodFishingPole::new),
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

    private static RegistryObject<AbstractFishingPole> register(String name, Supplier<AbstractFishingPole> supplier) {
        return ITEMS.register(name, supplier);
    }
}
