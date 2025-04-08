package org.confluence.mod.integration.terra_entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TEWhipItems;

public class TEItemComponentModify {

    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {

            // 设置无限耐久
        TEBoomerangItems.ITEMS.getEntries().forEach(i->event.modify(i.get(), b->b.set(DataComponents.UNBREAKABLE, new Unbreakable(true))));
        TEWhipItems.ITEMS.getEntries().forEach(i->event.modify(i.get(), b->b.set(DataComponents.UNBREAKABLE, new Unbreakable(true))));

    }
}