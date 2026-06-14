package org.confluence.mod.network.s2c;

import net.minecraft.advancements.AdvancementProgress;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.util.AchievementUtils;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.advancements.PortAdvancementHolder;

import java.util.LinkedHashMap;
import java.util.Map;

public record AchievementsDataSyncPacketS2C(
        PlayerAdvancements.Data data) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("achievements_data_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, AchievementsDataSyncPacketS2C> STREAM_CODEC = AchievementUtils.DATA_STREAM_CODEC
            .map(AchievementsDataSyncPacketS2C::new, AchievementsDataSyncPacketS2C::data);

    @Override
    public void work(Player player) {
        AchievementUtils.handleData(data, true);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public static void sendToPlayer(ServerPlayer player) {
        Map<ResourceLocation, AdvancementProgress> map = new LinkedHashMap<>();
        for (Map.Entry<PortAdvancementHolder, AdvancementProgress> entry : player.getAdvancements().progress.entrySet()) {
            if (entry.getValue().hasProgress() && AchievementOffsetLoader.getDisplayOffset().containsKey(entry.getKey().id())) {
                map.put(entry.getKey().id(), entry.getValue());
            }
        }
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new AchievementsDataSyncPacketS2C(new PlayerAdvancements.Data(map)));
    }
}
