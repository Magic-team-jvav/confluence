package org.confluence.mod.network.task;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;

import java.util.UUID;

public final class RequestAchievementsPacketS2C implements IPacketS2C {
    public static final RequestAchievementsPacketS2C INSTANCE = new RequestAchievementsPacketS2C();
    public static final Type<RequestAchievementsPacketS2C> TYPE = Confluence.createType("request_achievements");
    public static final StreamCodec<ByteBuf, RequestAchievementsPacketS2C> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RequestAchievementsPacketS2C() {}

    @Override
    public void handle(IPayloadContext context) {
        UUID id = LibClientUtils.getGameProfile().getId();
        PlayerAdvancements.Data data = AchievementUtils.loadData(id);
        context.reply(new ReplyAchievementsPacketC2S(id, data));
    }

    @Override
    public void work(Player player) {}

    @Override
    public Type<RequestAchievementsPacketS2C> type() {
        return TYPE;
    }
}
