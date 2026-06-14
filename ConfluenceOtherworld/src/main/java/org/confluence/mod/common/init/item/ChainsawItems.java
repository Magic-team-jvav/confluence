package org.confluence.mod.common.init.item;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseChainSawItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

/// [链锯](https://terraria.wiki.gg/zh/wiki/%E9%93%BE%E9%94%AF)
public class ChainsawItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseChainSawItem> COBALT_CHAINSAW = ITEMS.register("cobalt_chainsaw", () -> new BaseChainSawItem(ModTiers.COBALT, 18, 2.2F, unbreakable(), attributes(-1, 0.0275), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> PALLADIUM_CHAINSAW = ITEMS.register("palladium_chainsaw", () -> new BaseChainSawItem(ModTiers.PALLADIUM, 19, 2.2F, unbreakable(), attributes(-1, 0.029), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> MYTHRIL_CHAINSAW = ITEMS.register("mythril_chainsaw", () -> new BaseChainSawItem(ModTiers.MYTHRIL, 20, 2.2F, unbreakable(), attributes(-1, 0.03), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> ORICHALCUM_CHAINSAW = ITEMS.register("orichalcum_chainsaw", () -> new BaseChainSawItem(ModTiers.ORICHALCUM, 21, 2.2F, unbreakable(), attributes(-1, 0.0375), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> ADAMANTITE_CHAINSAW = ITEMS.register("adamantite_chainsaw", () -> new BaseChainSawItem(ModTiers.ADAMANTITE, 25, 2.2F, unbreakable(), attributes(-1, 0.045), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> TITANIUM_CHAINSAW = ITEMS.register("titanium_chainsaw", () -> new BaseChainSawItem(ModTiers.TITANIUM, 25, 2.2F, unbreakable(), attributes(-1, 0.046), ModRarity.LIGHT_RED));
}
