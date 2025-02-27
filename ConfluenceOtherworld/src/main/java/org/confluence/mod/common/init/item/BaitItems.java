package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.fishing.BaitItem;
import org.confluence.terra_curio.common.component.ModRarity;

import static org.confluence.terra_curio.common.component.ModRarity.*;


public class BaitItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaitItem> APPRENTICE_BAIT = register("apprentice_bait", BLUE, 0.15F),
            JOURNEYMAN_BAIT = register("journeyman_bait", GREEN, 0.3F),
            MASTER_BAIT = register("master_bait", ORANGE, 0.5F),
            BLACK_DRAGONFLY = register("black_dragonfly", BLUE, 0.2F),
            BLACK_SCORPION = register("black_scorpion", BLUE, 0.15F),
            BLUE_DRAGONFLY = register("blue_dragonfly", BLUE, 0.2F),
            BLUE_JELLYFISH = register("blue_jellyfish", BLUE, 0.2F),
            BUGGY = register("buggy", GREEN, 0.4F),
            ENCHANTED_NIGHTCRAWLER = register("enchanted_nightcrawler", GREEN, 0.35F),
            FIREFLY = register("firefly", BLUE, 0.2F),
            GLOWING_SNAIL = register("glowing_snail", BLUE, 0.15F),
            GOLD_BUTTERFLY = register("gold_butterfly", ORANGE, 0.5F),
            GOLD_DRAGONFLY = register("gold_dragonfly", ORANGE, 0.5F),
            GOLD_GRASSHOPPER = register("gold_grasshopper", ORANGE, 0.5F),
            GOLD_LADYBUG = register("gold_ladybug", ORANGE, 0.5F),
            GOLD_WATER_STRIDER = register("gold_water_strider", ORANGE, 0.5F),
            GOLD_WORM = register("gold_warm", ORANGE, 0.5F),
            GRASSHOPPER = register("grasshopper", WHITE, 0.1F),
            GREEN_DRAGONFLY = register("green_dragonfly", BLUE, 0.2F),
            GREEN_JELLYFISH = register("green_jellyfish", BLUE, 0.2F),
            GRUBBY = register("grubby", BLUE, 0.15F),
            HELL_BUTTERFLY = register("hell_butterfly", BLUE, 0.15F),
            JULIA_BUTTERFLY = register("julia_butterfly", BLUE, 0.25F),
            LADYBUG = register("ladybug", BLUE, 0.17F),
            LAVAFLY = register("lavafly", BLUE, 0.25F),
            LIGHTNING_BUG = register("lightning_bug", GREEN, 0.35F),
            MAGGOT = register("maggot", BLUE, 0.22F),
            MAGMA_SNAIL = register("magma_snail", GREEN, 0.35F),
            MONARCH_BUTTERFLY = register("monarch_butterfly", WHITE, 0.05F),
            ORANGE_DRAGONFLY = register("orange_dragonfly", BLUE, 0.2F),
            PINK_JELLYFISH = register("pink_jellyfish", BLUE, 0.2F),
            PURPLE_EMPEROR_BUTTERFLY = register("purple_emperor_butterfly", GREEN, 0.35F),
            RED_ADMIRAL_BUTTERFLY = register("red_admiral_butterfly", GREEN, 0.3F),
            RED_DRAGONFLY = register("red_dragonfly", BLUE, 0.2F),
            SCORPION = register("scorpion", WHITE, 0.1F),
            SLUGGY = register("sluggy", BLUE, 0.25F),
            SNAIL = register("snail", WHITE, 0.1F),
            STINKBUG = register("stinkbug", WHITE, 0.1F),
            SULPHUR_BUTTERFLY = register("sulphur_butter", WHITE, 0.1F),
            TREE_NYMPH_BUTTERFLY = register("tree_numph_butterfly", ORANGE, 0.5F),
            TRUFFLE_WORM = register("truffle_worm", ORANGE, 6.66F),
            ULYSSES_BUTTERFLY = register("ulysses_butterfly", BLUE, 0.2F),
            WATER_STRIDER = register("water_strider", BLUE, 0.17F),
            WORM = register("worm", BLUE, 0.25F),
            YELLOW_DRAGONFLY = register("yellow_dragonfly", BLUE, 0.2F),
            ZEBRA_SWALLOWTAIL_BUTTERFLY = register("zebra_swallowtail_butterfly", BLUE, 0.15F);

    public static DeferredItem<BaitItem> register(String name, ModRarity rarity, float bonus) {
        return ITEMS.register(name, () -> new BaitItem(rarity, bonus));
    }
}
