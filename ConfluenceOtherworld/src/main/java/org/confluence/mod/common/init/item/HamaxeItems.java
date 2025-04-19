package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.hamaxe.HamaxeItem;

import java.util.function.Supplier;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HamaxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<HamaxeItem> METEOR_HAMAXE = ITEMS.register("meteor_hamaxe", () -> new HamaxeItem(ModTiers.METEOR, 20, 1.6F, unbreakable(), attributes(0, 0.7), ModRarity.BLUE));
    public static final Supplier<HamaxeItem> MOLTEN_HAMAXE = ITEMS.register("molten_hamaxe", () -> new HamaxeItem(ModTiers.HELLSTONE, 23, 1.6F, unbreakable(), attributes(0, 0.7), ModRarity.ORANGE));
    // todo 血锤斧
    public static final Supplier<HamaxeItem> SPECTRE_HAMAXE = ITEMS.register("spectre_hamaxe", () -> new HamaxeItem(ModTiers.SPECTRE, 45, 1.8F, unbreakable(), attributes(3, 0.7), ModRarity.YELLOW));
    public static final Supplier<HamaxeItem> SOLAR_FLARE_HAMAXE = ITEMS.register("solar_flare_hamaxe", () -> new HamaxeItem(ModTiers.LUMINITE, 60, 2.2F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final Supplier<HamaxeItem> VORTEX_HAMAXE = ITEMS.register("vortex_hamaxe", () -> new HamaxeItem(ModTiers.LUMINITE, 60, 2.2F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final Supplier<HamaxeItem> NEBULA_HAMAXE = ITEMS.register("nebula_hamaxe", () -> new HamaxeItem(ModTiers.LUMINITE, 60, 2.2F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final Supplier<HamaxeItem> STARDUST_HAMAXE = ITEMS.register("stardust_hamaxe", () -> new HamaxeItem(ModTiers.LUMINITE, 60, 2.2F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    // todo 吉他斧

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
