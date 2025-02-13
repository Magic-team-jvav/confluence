package org.confluence.mod.common.data.saved;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class KillBoard implements INBTSerializable<ListTag> {
    private final Object2BooleanMap<EntityType<?>> defeatedMap = new Object2BooleanOpenHashMap<>();

    public boolean isDefeated(EntityType<?> entityType) {
        return defeatedMap.getBoolean(entityType);
    }

    public boolean isAnyDefeated(EntityType<?>... entityTypes) {
        return Arrays.stream(entityTypes).anyMatch(this::isDefeated);
    }

    public int countDefeated(EntityType<?>... entityTypes) {
        int count = 0;
        for (EntityType<?> entityType : entityTypes) {
            if (isDefeated(entityType)) count++;
        }
        return count;
    }

    public boolean defeat(EntityType<?> entityType) {
        return !defeatedMap.put(entityType, true);
    }

    public void defeated(EntityType<?> entityType, ConfluenceData data) {
        if (defeat(entityType)) {
            data.setDirty();
        }
    }

    @Override
    public ListTag serializeNBT(HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        for (Object2BooleanMap.Entry<EntityType<?>> entry : defeatedMap.object2BooleanEntrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Id", BuiltInRegistries.ENTITY_TYPE.getKey(entry.getKey()).toString());
            nbt.putBoolean("Defeated", entry.getBooleanValue());
            list.add(nbt);
        }
        return list;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, ListTag list) {
        defeatedMap.clear();
        for (Tag tag : list) {
            if (tag instanceof CompoundTag nbt) {
                try {
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(nbt.getString("Id")));
                    defeatedMap.put(entityType, nbt.getBoolean("Defeated"));
                } catch (Exception ignored) {}
            }
        }
    }
}
