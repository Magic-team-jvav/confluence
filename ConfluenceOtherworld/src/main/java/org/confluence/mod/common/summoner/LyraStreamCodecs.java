package org.confluence.mod.common.summoner;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;


public interface LyraStreamCodecs extends ByteBufCodecs {
    StreamCodec<ByteBuf, PathNode> PATH_NODE = StreamCodec.composite(
            LibStreamCodecUtils.VEC_3, PathNode::pos,
            ByteBufCodecs.FLOAT, PathNode::yaw,
            ByteBufCodecs.FLOAT, PathNode::pitch,
            ByteBufCodecs.FLOAT, PathNode::roll,
            PathNode::new
    );
    /// 1.21 的 `ByteBufCodecs` 没有 `UUID` 常量，用 Magic-Lib 既有的 `LibStreamCodecUtils.UUID`
    /// （`LibStreamCodecUtils.java:51`）。
    StreamCodec<FriendlyByteBuf, Optional<UUID>> OPTIONAL_UUID = ByteBufCodecs.optional(LibStreamCodecUtils.UUID);
}
