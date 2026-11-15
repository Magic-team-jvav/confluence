package org.confluence.mod.common.entity.npc.house;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;

import java.util.Optional;
import java.util.UUID;

public record House(Optional<UUID> uuid, BlockPos min, BlockPos max) {
    public static final House EMPTY = new House(Optional.empty(), BlockPos.ZERO, BlockPos.ZERO);

    public static final Codec<House> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(UUIDUtil.CODEC.optionalFieldOf("uuid").forGetter(House::uuid),
                    BlockPos.CODEC.fieldOf("min").forGetter(House::min),
                    BlockPos.CODEC.fieldOf("max").forGetter(House::max))
            .apply(builder, House::new));

    public BlockPos center() {
        return new BlockPos(
                (min.getX() + max.getX()) / 2,
                min.getY() + 2,
                (min.getZ() + max.getZ()) / 2);
    }

    public boolean contains(BlockPos pos) {
        return pos.getX() >= min.getX() && pos.getX() <= max.getX()
                && pos.getY() >= min.getY() && pos.getY() <= max.getY()
                && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }

    public boolean isValid() {
        return uuid.isPresent();
    }
}
