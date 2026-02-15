package org.confluence.mod.api.event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;

public class RegisterEvilMaterialReplacesEvent extends Event implements IModBusEvent {
    private static final BiMap<Item, Item> corrupt2crimson = HashBiMap.create();

    public static @Nullable Item fromCorrupt(Item corruptItem) {
        return corrupt2crimson.inverse().get(corruptItem);
    }

    public static @Nullable Item fromCrimson(Item crimsonItem) {
        return corrupt2crimson.get(crimsonItem);
    }

    public static @Nullable Item getPossible(Item source, boolean corrupt, boolean crimson) {
        if (corrupt) return fromCorrupt(source);
        if (crimson) return fromCrimson(source);
        return null;
    }

    public void register(ItemLike corruptItem, ItemLike crimsonItem) {
        corrupt2crimson.put(corruptItem.asItem(), crimsonItem.asItem());
    }
}
