package org.confluence.mod.network.task;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ReplyAchievementsPacketC2S(
        UUID uuid,
        Map<ResourceLocation, AdvancementProgress> data
) implements IPortPacket {
    public static final ResourceLocation ID = Confluence.asResource("sync_achievements");
    public static final PortStreamCodec<FriendlyByteBuf, ReplyAchievementsPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.UUID, ReplyAchievementsPacketC2S::uuid,
            AchievementUtils.DATA_STREAM_CODEC, ReplyAchievementsPacketC2S::data,
            ReplyAchievementsPacketC2S::new
    );
    public static final AttributeKey<Map<UUID, Map<ResourceLocation, AdvancementProgress>>> ACHIEVEMENTS = AttributeKey.valueOf(Confluence.asPlainId("achievements"));

    @Override
    public void handle(Context context) {
        Attribute<Map<UUID, Map<ResourceLocation, AdvancementProgress>>> attr = context.channelHandlerContext().attr(ACHIEVEMENTS);
        Map<UUID, Map<ResourceLocation, AdvancementProgress>> map = attr.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(uuid, data);
        attr.set(map);
        context.finishCurrentTask(AchievementsTask.TYPE);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }
}
