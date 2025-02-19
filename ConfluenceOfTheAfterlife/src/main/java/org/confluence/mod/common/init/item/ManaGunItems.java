package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.BeeGunItem;

import java.util.function.Supplier;

public class ManaGunItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<BeeGunItem> BEE_GUN = ITEMS.register("bee_gun", BeeGunItem::new);
}
