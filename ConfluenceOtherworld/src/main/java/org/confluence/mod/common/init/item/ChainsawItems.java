package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseChainSawItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

/// [链锯](https://terraria.wiki.gg/zh/wiki/%E9%93%BE%E9%94%AF)
public class ChainsawItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseChainSawItem> COBALT_CHAINSAW = registerItem("cobalt_chainsaw", "钴链锯", () -> new BaseChainSawItem(ModTiers.COBALT, 18, 2.2F, unbreakable(), attributes(-1, 0.0275), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> PALLADIUM_CHAINSAW = registerItem("palladium_chainsaw", "钯金链锯", () -> new BaseChainSawItem(ModTiers.PALLADIUM, 19, 2.2F, unbreakable(), attributes(-1, 0.029), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> MYTHRIL_CHAINSAW = registerItem("mythril_chainsaw", "秘银链锯", () -> new BaseChainSawItem(ModTiers.MYTHRIL, 20, 2.2F, unbreakable(), attributes(-1, 0.03), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> ORICHALCUM_CHAINSAW = registerItem("orichalcum_chainsaw", "山铜链锯", () -> new BaseChainSawItem(ModTiers.ORICHALCUM, 21, 2.2F, unbreakable(), attributes(-1, 0.0375), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> ADAMANTITE_CHAINSAW = registerItem("adamantite_chainsaw", "精金链锯", () -> new BaseChainSawItem(ModTiers.ADAMANTITE, 25, 2.2F, unbreakable(), attributes(-1, 0.045), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseChainSawItem> TITANIUM_CHAINSAW = registerItem("titanium_chainsaw", "钛金链锯", () -> new BaseChainSawItem(ModTiers.TITANIUM, 25, 2.2F, unbreakable(), attributes(-1, 0.046), ModRarity.LIGHT_RED));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
