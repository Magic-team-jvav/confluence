package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.BaseLanceItem;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class LanceItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseLanceItem> JOUSTING_LANCE = register("jousting_lance", "骑枪", ModRarity.LIGHT_RED, 4, 9, 32, 1.5);
    public static final DeferredItem<BaseLanceItem> HALLOWED_JOUSTING_LANCE = register("hallowed_jousting_lance", "神圣骑枪", ModRarity.PINK, 3, 10, 47, 1.625);
    public static final DeferredItem<BaseLanceItem> SHADOW_JOUSTING_LANCE = register("shadow_jousting_lance", "暗影骑枪", ModRarity.YELLOW, 2, 11, 67, 1.75);

    private static DeferredItem<BaseLanceItem> register(String registryId, String chineseName, ModRarity rarity, int attackInterval, double attackDistance, double baseAttackDamage, double baseKnockback) {
        DeferredItem<BaseLanceItem> entry = ITEMS.register(registryId, () -> new BaseLanceItem(
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
