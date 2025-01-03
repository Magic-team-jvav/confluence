package org.confluence.mod.common.data.saved;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record BrushData(List<Entry> colors) {
    public static final MapCodec<BrushData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("entries").forGetter(BrushData::colors)
    ).apply(instance, list -> new BrushData(Lists.newArrayList(list))));
    public static final Codec<BrushData> CODEC = MAP_CODEC.codec();

    public BrushData(IAttachmentHolder ignored) {
        this(new ArrayList<>());
    }

    public BrushData(BlockPos pos, BrushData.Facing facing, int color) {
        this(Lists.newArrayList(new BrushData.Entry(pos, new Hashtable<>(Map.of(facing, color)))));
    }

    public boolean putEntry(Entry entry) {
        boolean put = false;
        for (Entry color : colors) {
            if (color.pos.equals(entry.pos)) {
                color.map.putAll(entry.map);
                put = true;
            }
        }
        if (!put) {
            return colors.add(entry);
        }
        return false;
    }

    public boolean putData(BlockPos pos, BrushData.Facing facing, int color) {
        boolean put = false;
        for (Entry entry : colors) {
            if (entry.pos.equals(pos)) {
                entry.map.put(facing, color);
                put = true;
            }
        }
        if (!put) {
            Hashtable<Facing, Integer> map = new Hashtable<>();
            map.put(facing, color);
            return colors.add(new Entry(pos, map));
        }
        return false;
    }

    public boolean mergeData(BrushData data) {
        boolean merge = false;
        for (Entry entry : data.colors) {
            for (Entry color : colors) {
                if (color.pos.equals(entry.pos)) {
                    color.map.putAll(entry.map);
                    merge = true;
                }
            }
        }
        if (!merge) {
            return colors.addAll(data.colors);
        }
        return true;
    }

    public void ensureValid(ServerLevel serverLevel) {
        colors.removeIf(entry -> serverLevel.isLoaded(entry.pos) && serverLevel.getBlockState(entry.pos).isEmpty());
    }

    public boolean removeEntry(BlockPos pos) {
        Iterator<Entry> iterator = colors.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().pos.equals(pos)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public record Entry(BlockPos pos, Map<Facing, Integer> map) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos),
                Codec.unboundedMap(Facing.CODEC, Codec.INT).fieldOf("color").forGetter(Entry::map)
        ).apply(instance, (pos1, stringIntegerMap) -> new Entry(pos1, new Hashtable<>(stringIntegerMap))));
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
        public final Direction dir;

        Facing(@Nullable Direction dir) {
            this.dir = dir;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
