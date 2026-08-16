package org.confluence.mod.common.entity.npc.chat;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 数据包对话表与每个 NPC 独立的运行时冷却。
public final class ChatManager {
    private static volatile Map<EntityType<?>, List<ChatLine>> chatTable = Map.of();

    private ChatManager() {}

    public static Runtime createRuntime(BaseNPC owner) {
        return new Runtime(owner);
    }

    public static final class Runtime {
        private final BaseNPC owner;
        private final Map<ChatLine, Integer> cooldowns = new IdentityHashMap<>();
        private List<ChatLine> lines = List.of();
        private int forceCooldown = 50;

        private Runtime(BaseNPC owner) {
            this.owner = owner;
        }

        public void tick() {
            List<ChatLine> current = chatTable.getOrDefault(owner.getType(), List.of());
            if (current != lines) {
                lines = current;
                cooldowns.clear();
                forceCooldown = 50;
            }
            if (forceCooldown > 0) forceCooldown--;
            cooldowns.replaceAll((line, ticks) -> Math.max(0, ticks - 1));
            if (forceCooldown > 0 || lines.isEmpty()) return;

            ServerPlayer player = owner.level().getEntitiesOfClass(ServerPlayer.class, owner.getBoundingBox().inflate(32), ServerPlayer::isAlive).stream().min(java.util.Comparator.comparingDouble(owner::distanceToSqr)).orElse(null);
            if (player == null) return;
            int start = owner.getRandom().nextInt(lines.size());
            for (int offset = 0; offset < lines.size(); offset++) {
                ChatLine line = lines.get((start + offset) % lines.size());
                if (cooldowns.getOrDefault(line, 0) > 0 || !line.canTrigger(player, owner))
                    continue;
                owner.setCurrentChat(line.chat());
                cooldowns.put(line, Math.max(0, line.cooldownTicks()));
                forceCooldown = 50;
                return;
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
