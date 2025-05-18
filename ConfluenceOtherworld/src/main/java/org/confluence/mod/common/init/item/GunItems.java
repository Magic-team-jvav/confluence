package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.terra_guns.common.item.gun.BaseGun;
import org.confluence.terra_guns.common.item.gun.CustomGun;

public class GunItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredItem<BaseGun> STAR_CANNON = ITEMS.registerItem("star_cannon", properties -> new CustomGun(properties, 4, 11, 1.8f, 0.15f, 0.04f, -1, 0.0f, ModRarity.GREEN, 0f)); // 桑百颗

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
