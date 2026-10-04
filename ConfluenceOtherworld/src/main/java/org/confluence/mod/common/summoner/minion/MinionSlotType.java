package org.confluence.mod.common.summoner.minion;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.lib.util.LibStreamCodecUtils;

public enum MinionSlotType {
    Minion,
    Sentry,
    None;

    public static final StreamCodec<FriendlyByteBuf, MinionSlotType> STREAM_CODEC = LibStreamCodecUtils.fromEnum(values());
}
