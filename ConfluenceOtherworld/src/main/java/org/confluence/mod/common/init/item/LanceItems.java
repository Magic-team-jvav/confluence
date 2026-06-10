package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.BaseLanceItem;

public class LanceItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<BaseLanceItem> JOUSTING_LANCE = register("jousting_lance", ModRarity.LIGHT_RED, 4, 9, 32, 1.5);
    public static final RegistryObject<BaseLanceItem> HALLOWED_JOUSTING_LANCE = register("hallowed_jousting_lance", ModRarity.PINK, 3, 10, 47, 1.625);
    public static final RegistryObject<BaseLanceItem> SHADOW_JOUSTING_LANCE = register("shadow_jousting_lance", ModRarity.YELLOW, 2, 11, 67, 1.75);

    private static RegistryObject<BaseLanceItem> register(String name, ModRarity rarity, int attackInterval, double attackDistance, double baseAttackDamage, double baseKnockback) {
        return ITEMS.register(name, () -> new BaseLanceItem(
                new Item.Properties(),
                rarity,
                attackInterval,
                attackDistance,
                baseAttackDamage,
                baseKnockback
        ));
    }
}
