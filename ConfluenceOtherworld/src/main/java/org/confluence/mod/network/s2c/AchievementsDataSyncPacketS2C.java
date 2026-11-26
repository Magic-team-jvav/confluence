package org.confluence.mod.network.s2c;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementProgress;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.util.AchievementUtils;
import org.mesdag.portlib.diff.mixin.CriterionProgressAccessor;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/// 把玩家已完成的成就进度同步到客户端，供标题界面的成就界面与本地缓存使用。
///
/// 1.20.1 没有 `PlayerAdvancements.Data`，这里直接以 `Map<ResourceLocation, AdvancementProgress>` 承载。
public record AchievementsDataSyncPacketS2C(
        Map<ResourceLocation, AdvancementProgress> value) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("achievements_data_sync");

    public static final PortStreamCodec<FriendlyByteBuf, AchievementsDataSyncPacketS2C> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public AchievementsDataSyncPacketS2C decode(FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            Map<ResourceLocation, AdvancementProgress> map = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                ResourceLocation id = buffer.readResourceLocation();
                int criteriaSize = buffer.readVarInt();
                Map<String, CriterionProgress> criteria = new LinkedHashMap<>();
                for (int j = 0; j < criteriaSize; j++) {
                    String name = buffer.readUtf();
                    long obtained = buffer.readVarLong();
                    CriterionProgress progress = new CriterionProgress();
                    if (obtained > 0) {
                        ((CriterionProgressAccessor) progress).setObtained(new Date(obtained));
                    }
                    criteria.put(name, progress);
                }
                map.put(id, new AchievementProgress(criteria, buffer.readBoolean()));
            }
            return new AchievementsDataSyncPacketS2C(map);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, AchievementsDataSyncPacketS2C packet) {
            Map<ResourceLocation, AdvancementProgress> map = packet.value();
            buffer.writeVarInt(map.size());
            map.forEach((id, progress) -> {
                buffer.writeResourceLocation(id);
                Map<String, CriterionProgress> obtained = new LinkedHashMap<>();
                for (Map.Entry<String, CriterionProgress> entry : progress.criteria.entrySet()) {
                    CriterionProgress criterion = entry.getValue();
                    if (criterion.isDone() && criterion.getObtained() != null) {
                        obtained.put(entry.getKey(), criterion);
                    }
                }
                buffer.writeVarInt(obtained.size());
                obtained.forEach((name, criterion) -> {
                    buffer.writeUtf(name);
                    buffer.writeVarLong(criterion.getObtained().getTime());
                });
                buffer.writeBoolean(progress.isDone());
            });
        }
    };

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        AchievementUtils.handleData(value);
    }

    /// 不带 id 时同步全部已展示成就，否则只同步指定成就。
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
        PortPacketDistributor.sendToPlayer(player, new AchievementsDataSyncPacketS2C(map));
    }

    private static void putIfHasProgress(ServerPlayer player, ResourceLocation id, Map<ResourceLocation, AdvancementProgress> map) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(id);
        if (advancement == null) return;
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.hasProgress()) {
            map.put(id, progress);
        }
    }
}
