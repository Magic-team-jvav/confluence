package org.confluence.mod.network.s2c;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.util.AchievementUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public record AchievementsDataSyncPacketS2C(PlayerAdvancements.Data data) implements IPacketS2C {
    public static final Type<AchievementsDataSyncPacketS2C> TYPE = Confluence.createType("achievements_data_sync");
    public static final StreamCodec<FriendlyByteBuf, AchievementsDataSyncPacketS2C> STREAM_CODEC = AchievementUtils.DATA_STREAM_CODEC
            .map(AchievementsDataSyncPacketS2C::new, AchievementsDataSyncPacketS2C::data);

    @Override
    public void work(Player player) {
        AchievementUtils.handleData(data);
    }

    @Override
    public Type<AchievementsDataSyncPacketS2C> type() {
        return TYPE;
    }

    public static void sendToPlayer(ServerPlayer player) {
        Map<ResourceLocation, AdvancementProgress> map = new LinkedHashMap<>();
        for (Map.Entry<AdvancementHolder, AdvancementProgress> entry : player.getAdvancements().progress.entrySet()) {
            if (entry.getValue().hasProgress() && AchievementOffsetLoader.getDisplayOffset().containsKey(entry.getKey().id())) {
                map.put(entry.getKey().id(), entry.getValue());
            }
        }
        PacketDistributor.sendToPlayer(player, new AchievementsDataSyncPacketS2C(new PlayerAdvancements.Data(map)));
    }
}
