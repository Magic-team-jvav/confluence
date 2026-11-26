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
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.util.AchievementUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    public static void sendToPlayer(ServerPlayer player, ResourceLocation... ids) {
        Map<ResourceLocation, AdvancementProgress> map = new LinkedHashMap<>();
        Set<ResourceLocation> display = AchievementOffset.Loader.getDisplayOffset().keySet();
        if (ids.length == 0) {
            for (ResourceLocation id : display) {
                putIfHasProgress(player, id, map);
            }
        } else {
            for (ResourceLocation id : ids) {
                if (display.contains(id)) {
                    putIfHasProgress(player, id, map);
                }
            }
        }
        PacketDistributor.sendToPlayer(player, new AchievementsDataSyncPacketS2C(new PlayerAdvancements.Data(map)));
    }

    private static void putIfHasProgress(ServerPlayer player, ResourceLocation id, Map<ResourceLocation, AdvancementProgress> map) {
        AdvancementHolder advancement = player.server.getAdvancements().get(id);
        if (advancement == null) return;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.hasProgress()) {
            map.put(id, progress);
        }
    }
}
