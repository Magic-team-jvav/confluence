package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.HamaxeItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HamaxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<HamaxeItem> METEOR_HAMAXE = ITEMS.register("meteor_hamaxe", () -> new HamaxeItem(ModTiers.METEOR, 20, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.BLUE));
    public static final DeferredItem<HamaxeItem> MOLTEN_HAMAXE = ITEMS.register("molten_hamaxe", () -> new HamaxeItem(ModTiers.HELLSTONE, 23, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.ORANGE));
    public static final DeferredItem<HamaxeItem> HAEMORRHAXE = ITEMS.register("haemorrhaxe", () -> new HamaxeItem(ModTiers.CRIMTANE, 30, 0.9F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HamaxeItem> SPECTRE_HAMAXE = ITEMS.register("spectre_hamaxe", () -> new HamaxeItem(ModTiers.SPECTRE, 45, 1.0F, unbreakable(), attributes(3, 0.7), ModRarity.YELLOW));
    public static final DeferredItem<HamaxeItem> SOLAR_FLARE_HAMAXE = ITEMS.register("solar_flare_hamaxe", () -> new HamaxeItem(ModTiers.LUMINITE, 60, 1.2F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final DeferredItem<HamaxeItem> VORTEX_HAMAXE = ITEMS.register("vortex_hamaxe", () -> new HamaxeItem(ModTiers.LUMINITE, 60, 1.3F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final DeferredItem<HamaxeItem> NEBULA_HAMAXE = ITEMS.register("nebula_hamaxe", () -> new HamaxeItem(ModTiers.LUMINITE, 60, 1.4F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final DeferredItem<HamaxeItem> STARDUST_HAMAXE = ITEMS.register("stardust_hamaxe", () -> new HamaxeItem(ModTiers.LUMINITE, 60, 1.6F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final DeferredItem<HamaxeItem> THE_AXE = ITEMS.register("the_axe", () -> new HamaxeItem(ModTiers.CHLOROPHYTE, 72, 1.8F, unbreakable(), attributes(1, 0.725), ModRarity.YELLOW));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
