package org.confluence.mod.common.summoner;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;

import java.util.Optional;
import java.util.UUID;


public interface LyraStreamCodecs extends ByteBufCodecs {
    StreamCodec<ByteBuf, PathNode> PATH_NODE = StreamCodec.composite(
            LibStreamCodecUtils.VEC_3, PathNode::pos,
            ByteBufCodecs.FLOAT, PathNode::yaw,
            ByteBufCodecs.FLOAT, PathNode::pitch,
            ByteBufCodecs.FLOAT, PathNode::roll,
            PathNode::new
    );
    StreamCodec<FriendlyByteBuf, Optional<UUID>> OPTIONAL_UUID = ByteBufCodecs.optional(LibStreamCodecUtils.UUID);
}
