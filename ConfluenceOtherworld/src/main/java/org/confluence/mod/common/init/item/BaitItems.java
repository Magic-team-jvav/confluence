package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.animal.*;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.item.fishing.BaitItem;
import org.jetbrains.annotations.Nullable;
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
            BLACK_DRAGONFLY = register("black_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.BLACK)),
            BLACK_SCORPION = register("black_scorpion", BLUE, 0.15F, CritterEntities.SCORPION, entity -> entity.setVariant(Scorpion.Variant.BLACK)),
            BLUE_DRAGONFLY = register("blue_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.BLUE)),
            BLUE_JELLYFISH = register("blue_jellyfish", BLUE, 0.2F),
            BUGGY = register("buggy", GREEN, 0.4F),
            NIGHTCRAWLER = register("nightcrawler", GREEN, 0.35F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.NIGHTCRAWLER)),
            FIREFLY = register("firefly", BLUE, 0.2F),
            GLOWING_SNAIL = register("glowing_snail", BLUE, 0.15F, CritterEntities.GLOWING_SNAIL),
            GOLD_BUTTERFLY = register("gold_butterfly", ORANGE, 0.5F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.GOLD)),
            GOLD_DRAGONFLY = register("gold_dragonfly", ORANGE, 0.5F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.GOLD)),
            GOLD_GRASSHOPPER = register("gold_grasshopper", ORANGE, 0.5F, CritterEntities.GRASSHOPPER, entity -> entity.setVariant(Grasshopper.Variant.GOLD)),
            GOLD_LADYBUG = register("gold_ladybug", ORANGE, 0.5F, CritterEntities.LADYBUG, entity -> entity.setVariant(Ladybug.Variant.GOLD)),
            GOLD_WATER_STRIDER = register("gold_water_strider", ORANGE, 0.5F),
            GOLD_WORM = register("gold_warm", ORANGE, 0.5F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.GOLD)),
            GRASSHOPPER = register("grasshopper", WHITE, 0.1F, CritterEntities.GRASSHOPPER, entity -> entity.setVariant(Grasshopper.Variant.GREEN)),
            GREEN_DRAGONFLY = register("green_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.GREEN)),
            GREEN_JELLYFISH = register("green_jellyfish", BLUE, 0.2F),
            GRUBBY = register("grubby", BLUE, 0.15F, CritterEntities.GRUBBY),
            HELL_BUTTERFLY = register("hell_butterfly", BLUE, 0.15F, CritterEntities.HELL_BUTTERFLY),
            JULIA_BUTTERFLY = register("julia_butterfly", BLUE, 0.25F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.JULIA)),
            LADYBUG = register("ladybug", BLUE, 0.17F, CritterEntities.LADYBUG, entity -> entity.setVariant(Ladybug.Variant.RED)),
            LAVAFLY = register("lavafly", BLUE, 0.25F),
            LIGHTNING_BUG = register("lightning_bug", GREEN, 0.35F),
            MAGGOT = register("maggot", BLUE, 0.22F, CritterEntities.MAGGOT),
            MAGMA_SNAIL = register("magma_snail", GREEN, 0.35F, CritterEntities.MAGMA_SNAIL),
            MONARCH_BUTTERFLY = register("monarch_butterfly", WHITE, 0.05F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.MONARCH)),
            ORANGE_DRAGONFLY = register("orange_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.ORANGE)),
            PINK_JELLYFISH = register("pink_jellyfish", BLUE, 0.2F),
            PURPLE_EMPEROR_BUTTERFLY = register("purple_emperor_butterfly", GREEN, 0.35F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.PURPLE_EMPEROR)),
            RED_ADMIRAL_BUTTERFLY = register("red_admiral_butterfly", GREEN, 0.3F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.RED_ADMIRAL)),
            RED_DRAGONFLY = register("red_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.RED)),
            SCORPION = register("scorpion", WHITE, 0.1F, CritterEntities.SCORPION, entity -> entity.setVariant(Scorpion.Variant.NORMAL)),
            SLUGGY = register("sluggy", BLUE, 0.25F, CritterEntities.SLUGGY),
            SNAIL = register("snail", WHITE, 0.1F, CritterEntities.SNAIL),
            STINKBUG = register("stinkbug", WHITE, 0.1F),
            SULPHUR_BUTTERFLY = register("sulphur_butter", WHITE, 0.1F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.SULPHUR)),
            TREE_NYMPH_BUTTERFLY = register("tree_numph_butterfly", ORANGE, 0.5F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.TREE_NYMPH)),
            TRUFFLE_WORM = register("truffle_worm", ORANGE, 6.66F),
            PRISMATIC_LACEWING = register("prismatic_lacewing", ORANGE, 0F, CritterEntities.PRISMATIC_LACEWING),
            ULYSSES_BUTTERFLY = register("ulysses_butterfly", BLUE, 0.2F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.ULYSSES)),
            WATER_STRIDER = register("water_strider", BLUE, 0.17F),
            WORM = register("worm", BLUE, 0.25F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.NORMAL)),
            YELLOW_DRAGONFLY = register("yellow_dragonfly", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.YELLOW)),
            ZEBRA_SWALLOWTAIL_BUTTERFLY = register("zebra_swallowtail_butterfly", BLUE, 0.15F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.ZEBRA_SWALLOWTAIL));

    public static PortDeferredItem<BaitItem> register(String name, ModRarity rarity, float bonus) {
        return register(name, rarity, bonus, () -> null, entity -> {});
    }

    public static <T extends Entity> PortDeferredItem<BaitItem> register(String name, ModRarity rarity, float bonus, @Nullable Supplier<? extends EntityType<T>> supplier) {
        return register(name, rarity, bonus, supplier, entity -> {});
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> PortDeferredItem<BaitItem> register(String name, ModRarity rarity, float bonus, @Nullable Supplier<? extends EntityType<T>> supplier, Consumer<T> consumer) {
        return ITEMS.register(name, () -> new BaitItem(rarity, bonus, supplier, (Consumer<Entity>) consumer));
    }
}
