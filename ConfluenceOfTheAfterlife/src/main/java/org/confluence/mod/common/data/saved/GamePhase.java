package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.IntFunction;

public enum GamePhase implements StringRepresentable {
    // 0:骷髅王前, 1:骷髅王后, 2:肉后, 3:新三王后, 4:花后, 5:石巨人后, 6:月后
    BEFORE_SKELETRON,
    AFTER_SKELETRON,
    WALL_OF_FLESH,
    KING_NEO_THREE,
    PLANTERA,
    GOLEM,
    MOON_LORD;

    public static final Codec<GamePhase> CODEC = StringRepresentable.fromValues(GamePhase::values);
    public static final IntFunction<GamePhase> BY_ID = ByIdMap.continuous(GamePhase::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, GamePhase> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GamePhase::ordinal);

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static GamePhase getById(int id) {
        return BY_ID.apply(id);
    }

    public boolean isHardmode() {
        return ordinal() >= WALL_OF_FLESH.ordinal();
    }

    public boolean isGraduated() {
        return this == MOON_LORD;
    }

    public boolean isOtherBelowThenMe(GamePhase other) {
        return other.ordinal() < ordinal();
    }
}
