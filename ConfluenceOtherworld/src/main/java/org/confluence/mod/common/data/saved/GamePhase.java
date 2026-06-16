package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Locale;
import java.util.function.IntFunction;


/// BEFORE_SKELETRON:骷髅王前
/// AFTER_SKELETRON:骷髅王后
/// WALL_OF_FLESH:肉后
/// MECHANICAL_BOSSES:新三王后
/// PLANTERA:世花后
/// GOLEM:石巨人后
/// MOON_LORD:月后
public enum GamePhase implements StringRepresentable, IExtensibleEnum {
    BEFORE_SKELETRON(0),
    AFTER_SKELETRON(100),
    WALL_OF_FLESH(200),
    MECHANICAL_BOSSES(300),
    PLANTERA(400),
    GOLEM(500),
    MOON_LORD(600);

    public static final Codec<GamePhase> CODEC = StringRepresentable.fromEnum(GamePhase::values);
    public static final IntFunction<GamePhase> BY_ORDER = ByIdMap.sparse(GamePhase::getOrder, values(), AFTER_SKELETRON);
    public static final PortStreamCodec<ByteBuf, GamePhase> STREAM_CODEC = PortByteBufCodecs.idMapper(BY_ORDER, GamePhase::getOrder);
    private final int order;

    GamePhase(int order) {
        this.order = order;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    /// @see org.confluence.mod.mixed.IMinecraftServer#isHardmode
    public boolean isHardmode() {
        return isAtLeast(WALL_OF_FLESH);
    }

    public boolean isGraduated() {
        return isAtLeast(MOON_LORD);
    }

    /// 判断阶段是否高于other
    public boolean isAboveThan(GamePhase other) {
        return getOrder() > other.getOrder();
    }

    public boolean isAtLeast(GamePhase other) {
        return getOrder() >= other.getOrder();
    }

    public int getOrder() {
        return order;
    }

    public static GamePhase getByOrder(int order) {
        return BY_ORDER.apply(order);
    }

    public static GamePhase create(String name, int order) {
        throw new IllegalStateException("Enum not extended");
    }
}
