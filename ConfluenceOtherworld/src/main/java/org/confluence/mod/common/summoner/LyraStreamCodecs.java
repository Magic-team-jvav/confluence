package org.confluence.mod.common.summoner;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Optional;
import java.util.UUID;

public interface LyraStreamCodecs extends PortByteBufCodecs {
    PortStreamCodec<ByteBuf, PathNode> PATH_NODE = PortStreamCodec.composite(
            LibStreamCodecUtils.VEC_3, PathNode::pos,
            PortByteBufCodecs.FLOAT, PathNode::yaw,
            PortByteBufCodecs.FLOAT, PathNode::pitch,
            PortByteBufCodecs.FLOAT, PathNode::roll,
            PathNode::new
    );
    PortStreamCodec<FriendlyByteBuf, Optional<UUID>> OPTIONAL_UUID = PortByteBufCodecs.optional(PortByteBufCodecs.UUID);
}
