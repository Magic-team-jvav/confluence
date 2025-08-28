package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseChainSawItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

/**
 * <a href="https://terraria.wiki.gg/zh/wiki/%E9%93%BE%E9%94%AF">链锯</a>
 */
public class ChainsawItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseChainSawItem> COBALT_CHAINSAW = ITEMS.register("cobalt_chainsaw", () -> new BaseChainSawItem(ModTiers.COBALT, 23, 4, unbreakable(), attributes(-1, 0.0275), ModRarity.LIGHT_RED));
}
