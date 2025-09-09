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

    public static final DeferredItem<LanceItem> JOUSTING_LANCE = register("jousting_lance", ModRarity.LIGHT_RED, 4, 9, 12, 1.5, 2);
    public static final DeferredItem<LanceItem> HALLOWED_JOUSTING_LANCE = register("hallowed_jousting_lance", ModRarity.PINK, 3, 10, 18, 1.625, 1);
    public static final DeferredItem<LanceItem> SHADOW_JOUSTING_LANCE = register("shadow_jousting_lance", ModRarity.YELLOW, 2, 11, 26, 1.75, 1);

    private static DeferredItem<LanceItem> register(String name, ModRarity rarity, int attackInterval, double attackDistance, double baseAttackDamage, double baseKnockback, int tooltipCount) {
        return ITEMS.register(name, () -> new LanceItem(
                new Item.Properties(),
                rarity,
                attackInterval,
                attackDistance,
                baseAttackDamage,
                baseKnockback,
                TooltipItem.getTooltipsFromString(name, tooltipCount, ChatFormatting.GRAY)
        ));
    }
}
