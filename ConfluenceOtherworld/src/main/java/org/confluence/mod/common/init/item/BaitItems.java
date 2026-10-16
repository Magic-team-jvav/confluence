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
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class BaitItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaitItem> APPRENTICE_BAIT = register("apprentice_bait", "学徒诱饵", BLUE, 0.15F),
            JOURNEYMAN_BAIT = register("journeyman_bait", "熟手诱饵", GREEN, 0.3F),
            MASTER_BAIT = register("master_bait", "大师诱饵", ORANGE, 0.5F),
            BLACK_DRAGONFLY = register("black_dragonfly", "黑蜻蜓", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.BLACK)),
            BLACK_SCORPION = register("black_scorpion", "黑蝎子", BLUE, 0.15F, CritterEntities.SCORPION, entity -> entity.setVariant(Scorpion.Variant.BLACK)),
            BLUE_DRAGONFLY = register("blue_dragonfly", "蓝蜻蜓", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.BLUE)),
            BLUE_JELLYFISH = register("blue_jellyfish", "蓝水母", BLUE, 0.2F),
            BUGGY = register("buggy", "蚜虫", GREEN, 0.4F, CritterEntities.BUGGY),
            ENCHANTED_NIGHTCRAWLER = register("enchanted_nightcrawler", "附魔夜行者", GREEN, 0.35F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.NIGHTCRAWLER)),
            FIREFLY = register("firefly", "萤火虫", BLUE, 0.2F, CritterEntities.FIREFLY),
            GLOWING_SNAIL = register("glowing_snail", "发光蜗牛", BLUE, 0.15F, CritterEntities.GLOWING_SNAIL),
            GOLD_BUTTERFLY = register("gold_butterfly", "金蝴蝶", ORANGE, 0.5F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.GOLD)),
            GOLD_DRAGONFLY = register("gold_dragonfly", "金蜻蜓", ORANGE, 0.5F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.GOLD)),
            GOLD_GRASSHOPPER = register("gold_grasshopper", "金蚱蜢", ORANGE, 0.5F, CritterEntities.GRASSHOPPER, entity -> entity.setVariant(Grasshopper.Variant.GOLD)),
            GOLD_LADYBUG = register("gold_ladybug", "金瓢虫", ORANGE, 0.5F, CritterEntities.LADYBUG, entity -> entity.setVariant(Ladybug.Variant.GOLD)),
            GOLD_WATER_STRIDER = register("gold_water_strider", "金水黾", ORANGE, 0.5F),
            GOLD_WORM = register("gold_warm", "金蠕虫", ORANGE, 0.5F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.GOLD)),
            GRASSHOPPER = register("grasshopper", "蚱蜢", WHITE, 0.1F, CritterEntities.GRASSHOPPER, entity -> entity.setVariant(Grasshopper.Variant.GREEN)),
            GREEN_DRAGONFLY = register("green_dragonfly", "绿蜻蜓", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.GREEN)),
            GREEN_JELLYFISH = register("green_jellyfish", "绿水母", BLUE, 0.2F),
            GRUBBY = register("grubby", "蛆虫", BLUE, 0.15F, CritterEntities.GRUBBY),
            HELL_BUTTERFLY = register("hell_butterfly", "地狱蝴蝶", BLUE, 0.15F, CritterEntities.HELL_BUTTERFLY),
            JULIA_BUTTERFLY = register("julia_butterfly", "珠袖蝶", BLUE, 0.25F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.JULIA)),
            LADYBUG = register("ladybug", "瓢虫", BLUE, 0.17F, CritterEntities.LADYBUG, entity -> entity.setVariant(Ladybug.Variant.RED)),
            LAVAFLY = register("lavafly", "熔岩萤火虫", BLUE, 0.25F),
            LIGHTNING_BUG = register("lightning_bug", "荧光虫", GREEN, 0.35F, CritterEntities.LIGHTNING_BUG),
            MAGGOT = register("maggot", "蝇蛆", BLUE, 0.22F, CritterEntities.MAGGOT),
            MAGMA_SNAIL = register("magma_snail", "岩浆蜗牛", GREEN, 0.35F, CritterEntities.MAGMA_SNAIL),
            MONARCH_BUTTERFLY = register("monarch_butterfly", "帝王蝶", WHITE, 0.05F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.MONARCH)),
            ORANGE_DRAGONFLY = register("orange_dragonfly", "橙蜻蜓", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.ORANGE)),
            PINK_JELLYFISH = register("pink_jellyfish", "粉水母", BLUE, 0.2F),
            PURPLE_EMPEROR_BUTTERFLY = register("purple_emperor_butterfly", "紫蛱蝶", GREEN, 0.35F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.PURPLE_EMPEROR)),
            RED_ADMIRAL_BUTTERFLY = register("red_admiral_butterfly", "红蛱蝶", GREEN, 0.3F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.RED_ADMIRAL)),
            RED_DRAGONFLY = register("red_dragonfly", "红蜻蜓", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.RED)),
            SCORPION = register("scorpion", "蝎子", WHITE, 0.1F, CritterEntities.SCORPION, entity -> entity.setVariant(Scorpion.Variant.NORMAL)),
            SLUGGY = register("sluggy", "鼻涕虫", BLUE, 0.25F, CritterEntities.SLUGGY),
            SNAIL = register("snail", "蜗牛", WHITE, 0.1F, CritterEntities.SNAIL),
            STINKBUG = register("stinkbug", "臭虫", WHITE, 0.1F, CritterEntities.STINKBUG),
            SULPHUR_BUTTERFLY = register("sulphur_butter", "黄粉蝶", WHITE, 0.1F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.SULPHUR)),
            TREE_NYMPH_BUTTERFLY = register("tree_numph_butterfly", "帛斑蝶", ORANGE, 0.5F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.TREE_NYMPH)),
            TRUFFLE_WORM = register("truffle_worm", "松露虫", ORANGE, 6.66F, CritterEntities.TRUFFLE_WORM),
            PRISMATIC_LACEWING = register("prismatic_lacewing", "七彩草蛉", ORANGE, 0F, CritterEntities.PRISMATIC_LACEWING),
            ULYSSES_BUTTERFLY = register("ulysses_butterfly", "翠凤蝶", BLUE, 0.2F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.ULYSSES)),
            WATER_STRIDER = register("water_strider", "水黾", BLUE, 0.17F),
            WORM = register("worm", "蠕虫", BLUE, 0.25F, CritterEntities.WORM, entity -> entity.setVariant(Worm.Variant.NORMAL)),
            YELLOW_DRAGONFLY = register("yellow_dragonfly", "黄蜻蜓", BLUE, 0.2F, CritterEntities.DRAGONFLY, entity -> entity.setVariant(Dragonfly.Variant.YELLOW)),
            ZEBRA_SWALLOWTAIL_BUTTERFLY = register("zebra_swallowtail_butterfly", "带凤蝶", BLUE, 0.15F, CritterEntities.BUTTERFLY, entity -> entity.setVariant(Butterfly.Variant.ZEBRA_SWALLOWTAIL));

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

    private static PortDeferredItem<BaitItem> register(String registryId, String chineseName, ModRarity rarity, float bonus) {
        PortDeferredItem<BaitItem> entry = register(registryId, rarity, bonus, () -> null, entity -> {});
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Entity> PortDeferredItem<BaitItem> register(String registryId, String chineseName, ModRarity rarity, float bonus, @Nullable Supplier<? extends EntityType<T>> supplier) {
        PortDeferredItem<BaitItem> entry = register(registryId, rarity, bonus, supplier, entity -> {});
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Entity> PortDeferredItem<BaitItem> register(String registryId, String chineseName, ModRarity rarity, float bonus, @Nullable Supplier<? extends EntityType<T>> supplier, Consumer<T> consumer) {
        PortDeferredItem<BaitItem> entry = ITEMS.register(registryId, () -> new BaitItem(rarity, bonus, supplier, (Consumer<Entity>) consumer));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
