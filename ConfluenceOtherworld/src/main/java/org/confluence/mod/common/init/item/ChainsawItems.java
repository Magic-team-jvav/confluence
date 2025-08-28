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

    public static final DeferredItem<BaseChainSawItem> COBALT_CHAINSAW = ITEMS.register("cobalt_chainsaw", () -> new BaseChainSawItem(ModTiers.COBALT, 18, 2.2F, unbreakable(), attributes(-1, 0.0275), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseChainSawItem> PALLADIUM_CHAINSAW = ITEMS.register("palladium_chainsaw", () -> new BaseChainSawItem(ModTiers.PALLADIUM, 19, 2.2F, unbreakable(), attributes(-1, 0.029), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseChainSawItem> MYTHRIL_CHAINSAW = ITEMS.register("mythril_chainsaw", () -> new BaseChainSawItem(ModTiers.MYTHRIL, 20, 2.2F, unbreakable(), attributes(-1, 0.03), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseChainSawItem> ORICHALCUM_CHAINSAW = ITEMS.register("orichalcum_chainsaw", () -> new BaseChainSawItem(ModTiers.ORICHALCUM, 21, 2.2F, unbreakable(), attributes(-1, 0.0375), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseChainSawItem> ADAMANTITE_CHAINSAW = ITEMS.register("adamantite_chainsaw", () -> new BaseChainSawItem(ModTiers.ADAMANTITE, 25, 2.2F, unbreakable(), attributes(-1, 0.045), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseChainSawItem> TITANIUM_CHAINSAW = ITEMS.register("titanium_chainsaw", () -> new BaseChainSawItem(ModTiers.TITANIUM, 25, 2.2F, unbreakable(), attributes(-1, 0.046), ModRarity.LIGHT_RED));
}
