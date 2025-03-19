package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public record TreasureBagDrop(Item item) {
    public static final Codec<TreasureBagDrop> CODEC = BuiltInRegistries.ITEM.byNameCodec().xmap(TreasureBagDrop::new, TreasureBagDrop::item);
}
