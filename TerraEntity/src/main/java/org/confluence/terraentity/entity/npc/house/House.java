package org.confluence.terraentity.entity.npc.house;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * 房子类,未来可能扩展不同的房子类型
 *
 * @param uuid   房子的uuid == 实体的uuid
 * @param min    左下角的坐标
 * @param max    右上角的坐标
 * @param center 房子的中心坐标
 */
public record House(Optional<UUID> uuid, BlockPos min, BlockPos max, BlockPos center) {
    public static final String KEY = "npc_house";
    public static final House EMPTY = new House(Optional.empty(), BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO);

    public boolean isEmpty() {
        return uuid.isEmpty();
    }

    public boolean contains(BlockPos pos) {
        return pos.getX() >= min.getX() && pos.getX() <= max.getX() && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }

    // 早该用UUIDUtil.CODEC的
    @Deprecated(since = "1.2.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    private static final MapCodec<Optional<UUID>> FIXED_UUID_CODEC = new OptionalFieldCodec<>("uuid", UUIDUtil.CODEC, false) {
        @Override
        public <T> DataResult<Optional<UUID>> decode(DynamicOps<T> ops, MapLike<T> input) {
            DataResult<Optional<UUID>> result = super.decode(ops, input);
            if (result.isError()) {
                Optional<UUID> uuid = ops.getStringValue(input.get("uuid")).result().flatMap(s -> {
                    try {
                        return Optional.of(UUID.fromString(s));
                    } catch (Exception e) {
                        return Optional.empty();
                    }
                });
                return DataResult.success(uuid, Lifecycle.stable());
            }
            return result;
        }
    };
    public static final Codec<House> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            FIXED_UUID_CODEC.forGetter(House::uuid),
            BlockPos.CODEC.fieldOf("min").forGetter(House::min),
            BlockPos.CODEC.fieldOf("max").forGetter(House::max),
            BlockPos.CODEC.fieldOf("center").forGetter(House::center)
    ).apply(builder, House::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, House> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), House::uuid,
            BlockPos.STREAM_CODEC, House::min,
            BlockPos.STREAM_CODEC, House::max,
            BlockPos.STREAM_CODEC, House::center,
            House::new
    );
}
