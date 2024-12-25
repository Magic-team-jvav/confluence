package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.CollectedBookItem;

import java.util.function.Supplier;

public class NoTabItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<CollectedBookItem> COLLECTED_BOOK_ITEM = ITEMS.registerItem("collected_book_item", CollectedBookItem::new);

}
