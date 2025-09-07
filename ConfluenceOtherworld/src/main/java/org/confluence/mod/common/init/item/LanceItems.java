package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.LanceItem;

public class LanceItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<LanceItem> JOUSTING_LANCE = ITEMS.register("jousting_lance", () -> new LanceItem(
            new Item.Properties(),
            ModRarity.LIGHT_RED,
            4,
            9,
            12,
            0.12,
            TooltipItem.getTooltipsFromString("jousting_lance", 2, ChatFormatting.GRAY)
    ));
}
