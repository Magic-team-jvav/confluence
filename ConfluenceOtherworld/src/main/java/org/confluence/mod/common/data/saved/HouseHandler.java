package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.house.House;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/// 生物模块移到主模块之后使用这个
public class HouseHandler implements IGlobalData {
    //    public static final HouseHandler INSTANCE = new HouseHandler(); 现在还不能用
    private static final Codec<Map<ResourceKey<Level>, Map<NPCSpawner.Region, Map<UUID, House>>>> DATA_CODEC = LibCodecUtils.notStringKeyMap(
            "dimension", ResourceKey.codec(Registries.DIMENSION),
            "regions", LibCodecUtils.notStringKeyMap(
                    "region", NPCSpawner.Region.CODEC,
                    "houses", LibCodecUtils.notStringKeyMap(
                            "uuid", UUIDUtil.CODEC,
                            "house", House.CODEC
                    )
            )
    );

    private Map<ResourceKey<Level>, Map<NPCSpawner.Region, Map<UUID, House>>> data = new Object2ObjectOpenHashMap<>();

    private HouseHandler() {}

    public Map<NPCSpawner.Region, Map<UUID, House>> getOrCreateRegions(ResourceKey<Level> dimension) {
        return data.computeIfAbsent(dimension, d -> new Object2ObjectOpenHashMap<>());
    }

    public Map<UUID, House> getOrCreateHouses(ResourceKey<Level> dimension, NPCSpawner.Region region) {
        return getOrCreateRegions(dimension).computeIfAbsent(region, r -> new Object2ObjectOpenHashMap<>());
    }

    public void setHouse(ResourceKey<Level> dimension, NPCSpawner.Region region, UUID uuid, House house) {
        getOrCreateHouses(dimension, region).put(uuid, house);
    }

    public @Nullable House getHouse(ResourceKey<Level> dimension, NPCSpawner.Region region, UUID uuid) {
        Map<NPCSpawner.Region, Map<UUID, House>> map = data.get(dimension);
        if (map == null) return null;
        Map<UUID, House> map1 = map.get(region);
        if (map1 == null) return null;
        return map1.get(uuid);
    }

    public void setHouse(AbstractTerraNPC npc, House house) {
        setHouse(npc.level().dimension(), new NPCSpawner.Region(house.center()), npc.getUUID(), house);
    }

    public @Nullable House getHouse(AbstractTerraNPC npc) {
        BlockPos pos = npc.getSpawnAtPos();
        if (pos == null) return null;
        return getHouse(npc.level().dimension(), new NPCSpawner.Region(pos), npc.getUUID());
    }

    @Override
    public void decode(CompoundTag tag) {
        DATA_CODEC.parse(NbtOps.INSTANCE, tag.get("data"))
                .ifSuccess(result -> this.data = new Object2ObjectOpenHashMap<>(result));
    }

    @Override
    public void encode(CompoundTag tag) {
        DATA_CODEC.encodeStart(NbtOps.INSTANCE, data)
                .ifSuccess(nbt -> tag.put("data", nbt));
    }

    @Override
    public String serializeKey() {
        return "confluence:house_handler";
    }

    @Override
    public void clear() {
        data.clear();
    }
}
