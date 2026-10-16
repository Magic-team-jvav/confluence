package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.BaseLanceItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class LanceItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseLanceItem> JOUSTING_LANCE = register("jousting_lance", "骑枪", ModRarity.LIGHT_RED, 4, 9, 32, 1.5);
    public static final PortDeferredItem<BaseLanceItem> HALLOWED_JOUSTING_LANCE = register("hallowed_jousting_lance", "神圣骑枪", ModRarity.PINK, 3, 10, 47, 1.625);
    public static final PortDeferredItem<BaseLanceItem> SHADOW_JOUSTING_LANCE = register("shadow_jousting_lance", "暗影骑枪", ModRarity.YELLOW, 2, 11, 67, 1.75);

    private static PortDeferredItem<BaseLanceItem> register(String registryId, String chineseName, ModRarity rarity, int attackInterval, double attackDistance, double baseAttackDamage, double baseKnockback) {
        PortDeferredItem<BaseLanceItem> entry = ITEMS.register(registryId, () -> new BaseLanceItem(
                new Item.Properties(),
                rarity,
                attackInterval,
                attackDistance,
                baseAttackDamage,
                baseKnockback
        ));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
