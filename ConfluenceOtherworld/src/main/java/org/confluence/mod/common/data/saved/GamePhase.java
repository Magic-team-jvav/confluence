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


/**
 * BEFORE_SKELETRON:骷髅王前 <p>
 * AFTER_SKELETRON:骷髅王后 <p>
 * WALL_OF_FLESH:肉后 <p>
 * MECHANICAL_BOSSES:新三王后 <p>
 * PLANTERA:世花后 <p>
 * GOLEM:石巨人后 <p>
 * MOON_LORD:月后 <p>
 */
public enum GamePhase implements StringRepresentable {
    BEFORE_SKELETRON,
    AFTER_SKELETRON,
    WALL_OF_FLESH,
    MECHANICAL_BOSSES,
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

    /**
     * 判断阶段是否高于other
     */
    public boolean isAboveThan(GamePhase other) {
        return ordinal() > other.ordinal();
    }
}
