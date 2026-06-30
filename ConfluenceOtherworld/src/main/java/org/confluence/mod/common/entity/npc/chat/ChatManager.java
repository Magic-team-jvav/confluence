package org.confluence.mod.common.entity.npc.chat;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

/**
 * 全局 NPC 对话管理器——每 ~3 秒尝试触发随机对话。
 */
public final class ChatManager {
    private static Map<EntityType<?>, List<ChatLine>> chatTable = Map.of();
    private static final int TRY_INTERVAL = 60;

    /**
     * 每 tick 检查。由 {@link BaseNPC#customServerAiStep} 调用。
     */
    public static void tickNPC(BaseNPC npc) {
        if (npc.tickCount % TRY_INTERVAL != 0) return;
        if (!(npc.level() instanceof ServerLevel level)) return;

        List<ChatLine> lines = chatTable.get(npc.getType());
        if (lines == null || lines.isEmpty()) return;

        RandomSource random = npc.getRandom();
        for (int i = 0; i < 3; i++) { // 每次最多尝试 3 次
            ChatLine line = lines.get(random.nextInt(lines.size()));
            if (line.canTrigger(level, npc)) {
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
        public Loader() {
            super(new com.google.gson.GsonBuilder().create(), "npc/chat");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager rm, ProfilerFiller pf) {
            Codec<List<ChatLine>> listCodec = ChatLine.CODEC.listOf();
            Map<EntityType<?>, List<ChatLine>> newTable = new HashMap<>();
            for (var entry : map.entrySet()) {
                if (!Confluence.MODID.equals(entry.getKey().getNamespace())) continue;
                BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getKey()).ifPresent(type ->
                        listCodec.parse(JsonOps.INSTANCE, entry.getValue())
                                .result().ifPresent(lines -> newTable.put(type, lines)));
            }
            chatTable = newTable;
        }
    }
}
