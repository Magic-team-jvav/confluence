package org.confluence.mod.api.event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /// 无法解决两种邪恶材料同时各自存在于合成表中的情况，但这种情况几乎没有，且可以通过预写微光分解合成表来替换
    public static List<ItemStack> replaceTargets(List<ItemStack> original, boolean corrupt, boolean crimson) {
        Set<Item> all = original.stream().map(ItemStack::getItem).collect(Collectors.toSet());
        List<ItemStack> targets = new ArrayList<>(original.size());
        for (ItemStack stack : original) {
            Item target = getPossible(stack.getItem(), corrupt, crimson);
            if (target != null && all.contains(target)) {
                targets.add(stack.transmuteCopy(target));
            } else {
                targets.add(stack);
            }
        }
        return targets;
    }

    public void register(ItemLike corruptItem, ItemLike crimsonItem) {
        corrupt2crimson.put(corruptItem.asItem(), crimsonItem.asItem());
    }
}
