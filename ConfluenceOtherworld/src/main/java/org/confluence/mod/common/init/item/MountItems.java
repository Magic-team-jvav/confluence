package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.mount.MountItem;

public final class MountItems {
    private MountItems() {
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<Item> FUZZY_CARROT = ITEMS.register("fuzzy_carrot", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE));
    public static final DeferredItem<Item> SLIMY_SADDLE = ITEMS.register("slimy_saddle", () -> new MountItem<>(ModEntities.RIDEABLE_SLIME));
    public static final DeferredItem<Item> HONEYED_GOGGLES = ITEMS.register("honeyed_goggles", () -> new MountItem<>(ModEntities.RIDEABLE_BEE));
    public static final DeferredItem<Item> BLESSED_APPLE = ITEMS.register("blessed_apple", () -> new MountItem<>(ModEntities.RIDEABLE_UNICORN));
    public static final DeferredItem<Item> SUPERHEATED_BLOOD = ITEMS.register("superheated_blood", () -> new MountItem<>(ModEntities.RIDEABLE_LAVA_SHARK));
}
