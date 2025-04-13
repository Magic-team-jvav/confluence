package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;

import java.util.function.Supplier;

public class TreasureBagItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<TreasureBagItem> KING_SLIME_TREASURE_BAG = ITEMS.register("king_slime_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/king_slime")));
    public static final Supplier<TreasureBagItem> EYE_OF_CTHULHU_TREASURE_BAG = ITEMS.register("eye_of_cthulhu_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/eye_of_cthulhu"), (level, pos) -> {
        String difficulty = LibUtils.switchByDifficulty(level, pos, "/classic", "/expert", "/master");
        long secretFlag = ((IMinecraftServer) level.getServer()).confluence$getSecretFlag();
        String biome;
        if ((secretFlag & IWorldOptions.DOUBLE_EVIL) == IWorldOptions.DOUBLE_EVIL || (secretFlag & IWorldOptions.DOUBLE_EVIL) == 0) {
            biome = "_double_evil";
        } else if ((secretFlag & IWorldOptions.THE_CORRUPTION) != 0) {
            biome = "_corruption";
        } else if ((secretFlag & IWorldOptions.TR_CRIMSON) != 0) {
            biome = "_crimson";
        } else {
            biome = "";
        }
        return difficulty + biome;
    }));
    public static final Supplier<TreasureBagItem> EATER_OF_WORLDS_TREASURE_BAG = ITEMS.register("eater_of_worlds_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/eater_of_worlds")));
    public static final Supplier<TreasureBagItem> BRAIN_OF_CTHULHU_TREASURE_BAG = ITEMS.register("brain_of_cthulhu_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/brain_of_cthulhu")));
    public static final Supplier<TreasureBagItem> QUEEN_BEE_TREASURE_BAG = ITEMS.register("queen_bee_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/queen_bee")));
}
