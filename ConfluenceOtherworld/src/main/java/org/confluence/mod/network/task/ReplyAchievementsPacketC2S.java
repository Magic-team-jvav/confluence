package org.confluence.mod.network.task;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ReplyAchievementsPacketC2S(
        UUID uuid,
        PlayerAdvancements.Data data
) implements IPacketC2S {
    public static final Type<ReplyAchievementsPacketC2S> TYPE = Confluence.createType("sync_achievements");
    public static final StreamCodec<FriendlyByteBuf, ReplyAchievementsPacketC2S> STREAM_CODEC = StreamCodec.composite(
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
    public void work(ServerPlayer player) {}

    @Override
    public Type<ReplyAchievementsPacketC2S> type() {
        return TYPE;
    }
}
