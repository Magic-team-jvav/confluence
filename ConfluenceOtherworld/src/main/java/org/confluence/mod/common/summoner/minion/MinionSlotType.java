package org.confluence.mod.common.summoner.minion;

import net.minecraft.network.FriendlyByteBuf;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum MinionSlotType {
    Minion,
    Sentry,
    None;

    public static final PortStreamCodec<FriendlyByteBuf, MinionSlotType> STREAM_CODEC = LibStreamCodecUtils.fromEnum(values());
}
