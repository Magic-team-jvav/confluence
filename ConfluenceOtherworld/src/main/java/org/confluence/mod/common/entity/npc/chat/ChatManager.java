package org.confluence.mod.common.entity.npc.chat;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// 全局 NPC 对话管理器。
public final class ChatManager {
    private static volatile Map<EntityType<?>, List<ChatLine>> chatTable = Map.of();
    private static final int TRY_INTERVAL = 60;

    private ChatManager() {}

    /// 每 tick 检查，由 [BaseNPC#customServerAiStep] 调用。
    public static void tickNPC(BaseNPC npc) {
        if (npc.tickCount % TRY_INTERVAL != 0 || npc.level().isClientSide) return;

        List<ChatLine> lines = chatTable.get(npc.getType());
        if (lines == null || lines.isEmpty()) return;

        RandomSource random = npc.getRandom();
        for (int i = 0; i < 3; i++) {
            ChatLine line = lines.get(random.nextInt(lines.size()));
            if (line.canTrigger(null, npc)) {
                npc.setCurrentChat(line.chat());
                break;
            }
        }
    }

    @Nullable
    public static List<ChatLine> getChats(EntityType<?> npcType) {
        return chatTable.get(npcType);
    }

    public static final class Loader extends SimpleJsonResourceReloadListener {
        private static Loader instance;

        public Loader() {
            super(new com.google.gson.GsonBuilder().create(), "npc/chat");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager rm, ProfilerFiller pf) {
            Codec<List<ChatLine>> listCodec = ChatLine.CODEC.listOf();
            Map<EntityType<?>, List<ChatLine>> newTable = new LinkedHashMap<>();
            List<String> errors = new ArrayList<>();
            for (var entry : map.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                if (!Confluence.MODID.equals(entry.getKey().getNamespace())) continue;
                var type = BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getKey());
                if (type.isEmpty()) {
                    errors.add("Unknown NPC type " + entry.getKey());
                    continue;
                }
                DataResult<List<ChatLine>> decoded = listCodec.parse(JsonOps.INSTANCE, entry.getValue());
                var lines = decoded.result();
                if (lines.isEmpty()) {
                    errors.add(entry.getKey() + ": " + decoded.error().map(DataResult.PartialResult::message).orElse("unknown decode error"));
                    continue;
                }
                newTable.put(type.get(), List.copyOf(lines.get()));
            }
            if (!errors.isEmpty()) {
                errors.forEach(error -> Confluence.LOGGER.error("Cannot load NPC chat: {}", error));
                Confluence.LOGGER.error("NPC chat reload was rejected; the previous valid table remains active");
                return;
            }
            chatTable = Map.copyOf(newTable);
        }

        public static Loader getInstance() {
            if (instance == null) instance = new Loader();
            return instance;
        }
    }
}
