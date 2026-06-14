package org.confluence.mod.network.task;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.server.PlayerAdvancements;
import org.confluence.lib.network.IPacket;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;

import java.util.UUID;

/// onlineMode为false时服务端会根据玩家名称生成uuid
///
/// [net.minecraft.core.UUIDUtil#createOfflinePlayerUUID(java.lang.String)]
///
/// 但是客户端还是原先的profile
public record RequestAchievementsPacketS2C(boolean onlineMode) implements IPacket {
    public static final Type<RequestAchievementsPacketS2C> TYPE = Confluence.createType("request_achievements");
    public static final PortStreamCodec<ByteBuf, RequestAchievementsPacketS2C> STREAM_CODEC = PortByteBufCodecs.BOOL
            .map(RequestAchievementsPacketS2C::new, RequestAchievementsPacketS2C::onlineMode);

    @Override
    public void handle(IPayloadContext context) {
        GameProfile gameProfile = LibClientUtils.getGameProfile();
        if (onlineMode) {
            UUID id = gameProfile.getId();
            PlayerAdvancements.Data data = AchievementUtils.loadData(id);
            context.reply(new ReplyAchievementsPacketC2S(id, data));
        } else {
            UUID id = UUIDUtil.createOfflinePlayerUUID(gameProfile.getName());
            PlayerAdvancements.Data data = AchievementUtils.loadData(gameProfile.getId());
            context.reply(new ReplyAchievementsPacketC2S(id, data));
        }
    }

    @Override
    public Type<RequestAchievementsPacketS2C> type() {
        return TYPE;
    }
}
