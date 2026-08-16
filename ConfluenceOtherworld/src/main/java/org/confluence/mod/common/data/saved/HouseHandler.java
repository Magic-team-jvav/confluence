package org.confluence.mod.common.data.saved;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
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
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.house.House;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public enum HouseHandler implements IGlobalData {
    INSTANCE;
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

    public void setHouse(BaseNPC npc, House house) {
        ResourceKey<Level> dimension = npc.level().dimension();
        UUID uuid = npc.getUUID();
        if (!house.isValid()) {
            removeHouse(dimension, uuid);
        } else {
            NPCSpawner.Region region = new NPCSpawner.Region(house.center());
            setHouse(dimension, region, uuid, house);
        }
    }

    public @Nullable House getHouse(BaseNPC npc) {
        return getHouse(npc.level().dimension(), new NPCSpawner.Region(npc.blockPosition()), npc.getUUID());
    }

    public void removeHouse(ResourceKey<Level> dimension, NPCSpawner.Region region, UUID uuid) {
        Map<NPCSpawner.Region, Map<UUID, House>> map = data.get(dimension);
        if (map == null) return;
        Map<UUID, House> map1 = map.get(region);
        if (map1 == null) return;
        map1.remove(uuid);
    }

    /// 在一个维度的所有区域中解除指定 NPC 的房屋。
    ///
    /// <p>清空房屋时已经没有可用于反推区域的房屋中心，不能再拿 {@link House#EMPTY}
    /// 的零坐标删除，否则只会清理世界原点区域并留下幽灵占用记录。</p>
    public void removeHouse(ResourceKey<Level> dimension, UUID uuid) {
        Map<NPCSpawner.Region, Map<UUID, House>> regions = data.get(dimension);
        if (regions == null) return;
        regions.values().forEach(houses -> houses.remove(uuid));
        regions.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public @Nullable House findHouseAt(ResourceKey<Level> dimension, BlockPos pos) {
        NPCSpawner.Region region = new NPCSpawner.Region(pos);
        Map<UUID, House> houses = getOrCreateHouses(dimension, region);
        for (House house : houses.values()) {
            if (house.contains(pos)) return house;
        }
        return null;
    }

    @Override
    public void decode(CompoundTag tag) {
        if (tag.isEmpty()) {
            return;
        }
        Map<ResourceKey<Level>, Map<NPCSpawner.Region, Map<UUID, House>>> decoded =
                PortDataResultExtension.getOrThrow(
                        DATA_CODEC.parse(NbtOps.INSTANCE, tag.get("data")),
                        message -> new IllegalArgumentException(
                                "Failed to decode NPC house data: " + message));
        Object2ObjectOpenHashMap<ResourceKey<Level>, Map<NPCSpawner.Region, Map<UUID, House>>>
                mutableData = new Object2ObjectOpenHashMap<>();
        decoded.forEach((dimension, regions) -> {
            Object2ObjectOpenHashMap<NPCSpawner.Region, Map<UUID, House>> mutableRegions =
                    new Object2ObjectOpenHashMap<>();
            regions.forEach((region, houses) -> mutableRegions.put(
                    region,
                    new Object2ObjectOpenHashMap<>(houses)));
            mutableData.put(dimension, mutableRegions);
        });
        // Codec 可能返回不可变的嵌套映射；住房数据在运行期需要增删，三层都必须复制。
        this.data = mutableData;
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.put("data", PortDataResultExtension.getOrThrow(
                DATA_CODEC.encodeStart(NbtOps.INSTANCE, data),
                message -> new IllegalStateException(
                        "Failed to encode NPC house data: " + message)));
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
