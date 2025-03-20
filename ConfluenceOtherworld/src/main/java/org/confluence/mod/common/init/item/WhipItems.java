package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.Whip.BaseWhipItem;

import java.util.function.Supplier;

public class WhipItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseWhipItem> TEST_WHIP = register("test_whip", ()->new BaseWhipItem(new Item.Properties()));

    public static DeferredItem<BaseWhipItem> register(String name, Supplier<BaseWhipItem> supplier) {
        return ITEMS.register(name, supplier);
    }
}
