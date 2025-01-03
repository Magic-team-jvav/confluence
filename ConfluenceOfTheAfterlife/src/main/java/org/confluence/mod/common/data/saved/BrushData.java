package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public record BrushData(Map<BlockPos, Entry> colors) {
    public static final Entry EMPTY_ENTRY = new BrushData.Entry(Map.of());
    public static final Codec<BlockPos> POS_CODEC = Codec.STRING.xmap(str -> {
        String[] split = str.split(", ");
        int[] pos = new int[3];
        for (int i = 0; i < 3; i++) {
            if (i < split.length) {
                pos[i] = Integer.parseInt(split[i]);
            }
        }
        return new BlockPos(pos[0], pos[1], pos[2]);
    }, Vec3i::toShortString);
    public static final MapCodec<BrushData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(POS_CODEC, Entry.CODEC).fieldOf("entries").forGetter(BrushData::colors)
    ).apply(instance, map -> new BrushData(new Hashtable<>(map))));
    public static final Codec<BrushData> CODEC = MAP_CODEC.codec();

    public BrushData(BlockPos pos, Facing facing, int color) {
        this(Util.make(new Hashtable<>(), map -> {
            Map<Facing, Integer> map1 = new Hashtable<>();
            map1.put(facing, color);
            map.put(pos, new Entry(map1));
        }));
    }

    public void putEntry(BlockPos pos, Entry entry) {
        Entry entry1 = colors.get(pos);
        if (entry1 == null) {
            colors.put(pos, entry);
        } else {
            entry1.map.putAll(entry.map);
        }
    }

    public void putData(BlockPos pos, BrushData.Facing facing, int color) {
        colors.computeIfAbsent(pos, pos1 -> new Entry(new Hashtable<>())).map.put(facing, color);
    }

    public void mergeData(BrushData data) {
        for (Map.Entry<BlockPos, Entry> entry : data.colors.entrySet()) {
            BlockPos pos = entry.getKey();
            if (colors.containsKey(pos)) {
                colors.get(pos).map.putAll(entry.getValue().map());
            } else {
                colors.put(pos, entry.getValue());
            }
        }
    }

    public void ensureValid(ServerLevel serverLevel) {
        colors.entrySet().removeIf(entry -> serverLevel.isLoaded(entry.getKey()) && serverLevel.getBlockState(entry.getKey()).isEmpty());
    }

    public boolean removeEntry(BlockPos pos) {
        return colors.remove(pos) != null;
    }

    public record Entry(Map<Facing, Integer> map) {
        public static final Codec<Entry> CODEC = Codec.unboundedMap(Facing.CODEC, Codec.INT).xmap(map -> new Entry(new Hashtable<>(map)), Entry::map);
    }

    public enum Facing implements StringRepresentable {
        ALL(null),
        DOWN(Direction.DOWN),
        UP(Direction.UP),
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        WEST(Direction.WEST),
        EAST(Direction.EAST);

        public static final Codec<Facing> CODEC = StringRepresentable.fromEnum(Facing::values);
        public final @Nullable Direction dir;

        Facing(@Nullable Direction dir) {
            this.dir = dir;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static Facing fromDirection(@Nullable Direction dir) {
            return switch (dir) {
                case null -> ALL;
                case DOWN -> DOWN;
                case UP -> UP;
                case NORTH -> NORTH;
                case SOUTH -> SOUTH;
                case WEST -> WEST;
                case EAST -> EAST;
            };
        }
    }
}
