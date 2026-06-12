package org.confluence.mod.network.task;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.server.PlayerAdvancements;
import org.confluence.lib.network.IPacket;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ReplyAchievementsPacketC2S(
        UUID uuid,
        PlayerAdvancements.Data data
) implements IPacket {
    public static final Type<ReplyAchievementsPacketC2S> TYPE = Confluence.createType("sync_achievements");
    public static final PortStreamCodec<FriendlyByteBuf, ReplyAchievementsPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ReplyAchievementsPacketC2S::uuid,
            AchievementUtils.DATA_STREAM_CODEC, ReplyAchievementsPacketC2S::data,
            ReplyAchievementsPacketC2S::new
    );
    public static final AttributeKey<Map<UUID, PlayerAdvancements.Data>> ACHIEVEMENTS = AttributeKey.valueOf(Confluence.asPlainId("achievements"));

    @Override
    public void handle(IPayloadContext context) {
        Attribute<Map<UUID, PlayerAdvancements.Data>> attr = context.channelHandlerContext().attr(ACHIEVEMENTS);
        Map<UUID, PlayerAdvancements.Data> map = attr.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(uuid, data);
        attr.set(map);
        context.finishCurrentTask(AchievementsTask.TYPE);
    }

    @Override
    public Type<ReplyAchievementsPacketC2S> type() {
        return TYPE;
    }
}
