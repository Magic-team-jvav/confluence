package org.confluence.mod.common.entity.npc.chat;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// 全局 NPC 对话管理器——按每个 NPC 的独立冷却触发附近玩家可见的对话。
public final class ChatManager {
    private static Map<EntityType<?>, List<ChatLine>> chatTable = Map.of();
    private static Loader loader;

    /// 每 tick 更新该 NPC 自己的聊天冷却，并在附近有玩家时尝试触发一条可用对话。
    public static void tickNPC(BaseNPC npc) {
        if (npc.level().isClientSide) return;
        npc.tickChatCooldowns();

        List<ChatLine> lines = chatTable.get(npc.getType());
        if (lines == null || lines.isEmpty()) return;

        ServerLevel level = (ServerLevel) npc.level();
        ServerPlayer player = null;
        double closestDistance = 32 * 32;
        for (ServerPlayer candidate : level.players()) {
            double distance = candidate.distanceToSqr(npc);
            if (distance <= closestDistance) {
                closestDistance = distance;
                player = candidate;
            }
        }
        if (player == null) return;

        RandomSource random = npc.getRandom();
        int start = random.nextInt(lines.size());
        for (int offset = 0; offset < lines.size(); offset++) {
            ChatLine line = lines.get((start + offset) % lines.size());
            if (npc.canTriggerChat(line) && line.canTrigger(player, npc)) {
                npc.setCurrentChat(line.chat());
                npc.markChatTriggered(line);
                break;
            }
        }
    }

    @Nullable
    public static List<ChatLine> getChats(EntityType<?> npcType) {
        return chatTable.get(npcType);
    }

    public static Loader getLoader() {
        if (loader == null) loader = new Loader();
        return loader;
    }

    public static final class Loader extends SimpleJsonResourceReloadListener {
        public Loader() {
            super(new com.google.gson.GsonBuilder().create(), "npc/chat");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager rm, ProfilerFiller pf) {
            Codec<List<ChatLine>> listCodec = ChatLine.CODEC.listOf();
            Map<EntityType<?>, List<ChatLine>> newTable = new HashMap<>();
            boolean failed = false;
            for (var entry : map.entrySet()) {
                if (!Confluence.MODID.equals(entry.getKey().getNamespace())) continue;
                var type = BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getKey());
                var result = listCodec.parse(JsonOps.INSTANCE, entry.getValue());
                if (type.isEmpty() || result.result().isEmpty()) {
                    Confluence.LOGGER.error("Failed to reload NPC chat {}", entry.getKey());
                    failed = true;
                    continue;
                }
                newTable.put(type.get(), List.copyOf(result.result().get()));
            }
            if (!failed) chatTable = Map.copyOf(newTable);
        }
    }
}
