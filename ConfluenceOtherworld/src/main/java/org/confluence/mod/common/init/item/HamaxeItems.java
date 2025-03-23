package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.hamaxe.HamaxeItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;

public class HamaxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<HamaxeItem> METEOR_HAMAXE = ITEMS.register("meteor_hamaxe", () -> new HamaxeItem(ModTiers.METEOR, 10000, 10000, true, ModRarity.BLUE));
    public static final Supplier<HamaxeItem> MOLTEN_HAMAXE = ITEMS.register("molten_hamaxe", () -> new HamaxeItem(ModTiers.HELLSTONE, 10000, 10000, true, ModRarity.BLUE));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
