package org.confluence.mod.common.init.item;

import com.google.common.base.Supplier;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.fishing.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class FishingPoleItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<AbstractFishingPole> WOOD_FISHING_POLE = register("wood_fishing_pole", "木钓竿", WoodFishingPole::new),
            REINFORCED_FISHING_POLE = register("reinforced_fishing_pole", "强化钓竿", ReinforcedFishingPole::new),
            FISHER_OF_SOULS = registerWithEnglishName("fisher_of_souls", "灵魂钓手", "Fisher of Souls", FisherOfSouls::new),
            FLESHCATCHER = register("fleshcatcher", "捕肉手", Fleshcatcher::new),
            SCARAB_FISHING_ROD = register("scarab_fishing_rod", "甲虫钓竿", ScarabFishingRod::new),
            CHUM_CASTER = register("chum_caster", "鱼饵投掷者", ChumCaster::new),
            FIBERGLASS_FISHING_POLE = register("fiberglass_fishing_pole", "玻璃钢钓竿", FiberglassFishingPole::new),
            MECHANICS_ROD = registerWithEnglishName("mechanics_rod", "机械师钓竿", "Mechanic's Rod", MechanicsRod::new),
            SITTING_DUCKS_FISHING_POLE = registerWithEnglishName("sitting_ducks_fishing_pole", "冤大头钓竿", "Sitting Duck's Fishing Pole", SittingDucksFishingPole::new),
            HOTLINE_FISHING_HOOK = register("hotline_fishing_hook", "熔线钓钩", HotlineFishingHookItem::new),
            GOLDEN_FISHING_ROD = register("golden_fishing_rod", "金钓竿", GoldenFishingRod::new),
            DEV_FISHING_ROD = register("dev_fishing_rod", "开发者钓竿", DevFishingRod::new);

    private static PortDeferredItem<AbstractFishingPole> register(String registryId, String chineseName, Supplier<AbstractFishingPole> supplier) {
        PortDeferredItem<AbstractFishingPole> entry = ITEMS.register(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<AbstractFishingPole> registerWithEnglishName(String registryId, String chineseName, String englishName, Supplier<AbstractFishingPole> supplier) {
        PortDeferredItem<AbstractFishingPole> entry = ITEMS.register(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
