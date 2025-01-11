package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class WeaponStorage implements INBTSerializable<CompoundTag> {

    public Map<Item , Integer> boomerangCounter = new HashMap<>();

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<Item, Integer> entry : boomerangCounter.entrySet()) {
            tag.putInt(entry.getKey().toString(), entry.getValue());
        }
        return tag;
    }


    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        for (String key : compoundTag.getAllKeys()) {
            Item item = BuiltInRegistries.ITEM.get(Confluence.asResource(key));
            boomerangCounter.put(item, compoundTag.getInt(key));
        }
    }
}
