package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.fishing.BaitItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.confluence.lib.common.component.ModRarity.*;

public class BaitItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaitItem> APPRENTICE_BAIT = register("apprentice_bait", BLUE, 0.15F),
            JOURNEYMAN_BAIT = register("journeyman_bait", GREEN, 0.3F),
            MASTER_BAIT = register("master_bait", ORANGE, 0.5F),
            BLACK_DRAGONFLY = register("black_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(0)),
            BLACK_SCORPION = register("black_scorpion", BLUE, 0.15F, TEAnimals.SCORPION, entity -> entity.setVariant(0)),
            BLUE_DRAGONFLY = register("blue_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(1)),
            BLUE_JELLYFISH = register("blue_jellyfish", BLUE, 0.2F),
            BUGGY = register("buggy", GREEN, 0.4F),
            ENCHANTED_NIGHTCRAWLER = register("enchanted_nightcrawler", GREEN, 0.35F, TEAnimals.WORM, entity -> entity.setVariant(0)),
            FIREFLY = register("firefly", BLUE, 0.2F),
            GLOWING_SNAIL = register("glowing_snail", BLUE, 0.15F, TEAnimals.GLOWING_SNAIL),
            GOLD_BUTTERFLY = register("gold_butterfly", ORANGE, 0.5F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(0)),
            GOLD_DRAGONFLY = register("gold_dragonfly", ORANGE, 0.5F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(2)),
            GOLD_GRASSHOPPER = register("gold_grasshopper", ORANGE, 0.5F, TEAnimals.GRASSHOPPER, entity -> entity.setVariant(0)),
            GOLD_LADYBUG = register("gold_ladybug", ORANGE, 0.5F, TEAnimals.LADYBUG, entity -> entity.setVariant(0)),
            GOLD_WATER_STRIDER = register("gold_water_strider", ORANGE, 0.5F),
            GOLD_WORM = register("gold_warm", ORANGE, 0.5F, TEAnimals.WORM, entity -> entity.setVariant(1)),
            GRASSHOPPER = register("grasshopper", WHITE, 0.1F, TEAnimals.GRASSHOPPER, entity -> entity.setVariant(1)),
            GREEN_DRAGONFLY = register("green_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(3)),
            GREEN_JELLYFISH = register("green_jellyfish", BLUE, 0.2F),
            GRUBBY = register("grubby", BLUE, 0.15F, TEAnimals.GRUBBY),
            HELL_BUTTERFLY = register("hell_butterfly", BLUE, 0.15F, TEAnimals.HELL_BUTTERFLY),
            JULIA_BUTTERFLY = register("julia_butterfly", BLUE, 0.25F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(1)),
            LADYBUG = register("ladybug", BLUE, 0.17F, TEAnimals.LADYBUG, entity -> entity.setVariant(1)),
            LAVAFLY = register("lavafly", BLUE, 0.25F),
            LIGHTNING_BUG = register("lightning_bug", GREEN, 0.35F),
            MAGGOT = register("maggot", BLUE, 0.22F, TEAnimals.MAGGOT),
            MAGMA_SNAIL = register("magma_snail", GREEN, 0.35F, TEAnimals.MAGMA_SNAIL),
            MONARCH_BUTTERFLY = register("monarch_butterfly", WHITE, 0.05F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(2)),
            ORANGE_DRAGONFLY = register("orange_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(4)),
            PINK_JELLYFISH = register("pink_jellyfish", BLUE, 0.2F),
            PURPLE_EMPEROR_BUTTERFLY = register("purple_emperor_butterfly", GREEN, 0.35F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(3)),
            RED_ADMIRAL_BUTTERFLY = register("red_admiral_butterfly", GREEN, 0.3F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(4)),
            RED_DRAGONFLY = register("red_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(5)),
            SCORPION = register("scorpion", WHITE, 0.1F, TEAnimals.SCORPION, entity -> entity.setVariant(1)),
            SLUGGY = register("sluggy", BLUE, 0.25F, TEAnimals.SLUGGY),
            SNAIL = register("snail", WHITE, 0.1F, TEAnimals.SNAIL),
            STINKBUG = register("stinkbug", WHITE, 0.1F),
            SULPHUR_BUTTERFLY = register("sulphur_butter", WHITE, 0.1F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(5)),
            TREE_NYMPH_BUTTERFLY = register("tree_numph_butterfly", ORANGE, 0.5F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(6)),
            TRUFFLE_WORM = register("truffle_worm", ORANGE, 6.66F),
            PRISMATIC_LACEWING = register("prismatic_lacewing", ORANGE, 0F, TEAnimals.PRISMATIC_LACEWING),
            ULYSSES_BUTTERFLY = register("ulysses_butterfly", BLUE, 0.2F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(7)),
            WATER_STRIDER = register("water_strider", BLUE, 0.17F),
            WORM = register("worm", BLUE, 0.25F, TEAnimals.WORM, entity -> entity.setVariant(2)),
            YELLOW_DRAGONFLY = register("yellow_dragonfly", BLUE, 0.2F, TEAnimals.DRAGONFLY, entity -> entity.setVariant(6)),
            ZEBRA_SWALLOWTAIL_BUTTERFLY = register("zebra_swallowtail_butterfly", BLUE, 0.15F, TEAnimals.BUTTERFLY, entity -> entity.setVariant(8));

    public static PortDeferredItem<BaitItem> register(String name, ModRarity rarity, float bonus) {
        return register(name, rarity, bonus, () -> null, entity -> {});
    }

    public static <T extends Entity> PortDeferredItem<BaitItem> register(String name, ModRarity rarity, float bonus, Supplier<? extends EntityType<T>> supplier) {
        return register(name, rarity, bonus, supplier, entity -> {});
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> PortDeferredItem<BaitItem> register(String name, ModRarity rarity, float bonus, Supplier<? extends EntityType<T>> supplier, Consumer<T> consumer) {
        return ITEMS.register(name, () -> new BaitItem(rarity, bonus, supplier, (Consumer<Entity>) consumer));
    }
}
