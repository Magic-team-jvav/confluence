package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.StarCannonItem;

public class GunItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    public static final RegistryObject<StarCannonItem> STAR_CANNON = ITEMS.registerItem("star_cannon", StarCannonItem::new); // 桑百颗
}
