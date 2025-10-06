package org.confluence.mod.integration.terra_entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.init.item.TEYoyosItems;

import java.util.function.Consumer;

public class TEItemComponentModify {
    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        // 设置无限耐久
        Consumer<? super DeferredHolder<Item, ?>> setUnbreakable = i -> event.modify(i.get(), b -> b.set(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE));

        TEBoomerangItems.ITEMS.getEntries().forEach(setUnbreakable);
        TEWhipItems.ITEMS.getEntries().forEach(setUnbreakable);
        TEYoyosItems.ITEMS.getEntries().forEach(setUnbreakable);

    }
}
