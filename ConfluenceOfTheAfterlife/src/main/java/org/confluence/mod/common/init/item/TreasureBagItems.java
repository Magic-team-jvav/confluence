package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.TreasureBagItem;

import java.util.function.Supplier;

public class TreasureBagItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<TreasureBagItem> KING_SLIME_TREASURE_BAG = ITEMS.register("king_slime_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("entity/boss/king_slime")));
    public static final Supplier<TreasureBagItem> EYE_OF_CTHULHU_TREASURE_BAG = ITEMS.register("eye_of_cthulhu_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("entity/boss/eye_of_cthulhu")));
    public static final Supplier<TreasureBagItem> EATER_OF_WORLDS_TREASURE_BAG = ITEMS.register("eater_of_worlds_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("entity/boss/eater_of_worlds")));
    public static final Supplier<TreasureBagItem> BRAIN_OF_CTHULHU_TREASURE_BAG = ITEMS.register("brain_of_cthulhu_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("entity/boss/brain_of_cthulhu")));
}
