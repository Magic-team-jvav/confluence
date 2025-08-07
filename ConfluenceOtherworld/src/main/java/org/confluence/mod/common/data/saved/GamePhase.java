package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.NetworkedEnum;
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
@NetworkedEnum(NetworkedEnum.NetworkCheck.BIDIRECTIONAL)
public enum GamePhase implements StringRepresentable, IExtensibleEnum {
    BEFORE_SKELETRON(0),
    AFTER_SKELETRON(100),
    WALL_OF_FLESH(200),
    MECHANICAL_BOSSES(300),
    PLANTERA(400),
    GOLEM(500),
    MOON_LORD(600);

    public static final Codec<GamePhase> CODEC = StringRepresentable.fromValues(GamePhase::values);
    public static final IntFunction<GamePhase> BY_ORDER = ByIdMap.sparse(GamePhase::getOrder, values(), AFTER_SKELETRON);
    public static final StreamCodec<ByteBuf, GamePhase> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ORDER, GamePhase::getOrder);
    private final int order;

    GamePhase(int order) {
        this.order = order;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isHardmode() {
        return isAtLeast(WALL_OF_FLESH);
    }

    public boolean isGraduated() {
        return isAtLeast(MOON_LORD);
    }

    /**
     * 判断阶段是否高于other
     */
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

    public static ExtensionInfo getExtensionInfo() {
        return ExtensionInfo.nonExtended(GamePhase.class);
    }
}
