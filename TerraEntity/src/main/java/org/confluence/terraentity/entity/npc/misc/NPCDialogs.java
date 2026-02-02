package org.confluence.terraentity.entity.npc.misc;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.terraentity.TerraEntity;

import javax.annotation.Nullable;
import java.util.*;

public record NPCDialogs(List<String> dialogs) {
    public static final Codec<NPCDialogs> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("dialogs").forGetter(NPCDialogs::dialogs)
    ).apply(instance, NPCDialogs::new));

    public static NPCDialogs of(String... dialogs) {
        return new NPCDialogs(Arrays.stream(dialogs).toList());
    }

    public static class Loader extends SingleJsonFileReloadListener {
        public static final Codec<Map<EntityType<?>, NPCDialogs>> CODEC = Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), NPCDialogs.CODEC);
        private static Loader INSTANCE;
        private Map<EntityType<?>, NPCDialogs> dialogs = ImmutableMap.of();

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
            ConditionalOps<JsonElement> ops = makeConditionalOps();
            Map<EntityType<?>, NPCDialogs> map = new IdentityHashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
                BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getKey()).ifPresent(entityType -> NPCDialogs.CODEC.parse(ops, entry.getValue())
                        .resultOrPartial(errorMsg -> TerraEntity.LOGGER.warn("Could not decode npc dialogs with json id {} - error: {}", entry.getKey(), errorMsg))
                        .ifPresent(other -> {
                            NPCDialogs has = map.computeIfAbsent(entityType, type -> new NPCDialogs(new ArrayList<>()));
                            for (String dialog : other.dialogs) {
                                if (!has.dialogs.contains(dialog)) {
                                    has.dialogs.add(dialog);
                                }
                            }
                        }));
            }
            this.dialogs = ImmutableMap.copyOf(map);
        }

        @Override
        protected ResourceLocation resourcePath() {
            return TerraEntity.space("npc/dialogs.json");
        }

        @Override
        protected String identifier() {
            return "NPC Dialogs";
        }

        public Map<EntityType<?>, NPCDialogs> getDialogs() {
            return dialogs;
        }

        public @Nullable String getRandomDialog(RandomSource random, EntityType<?> entityType) {
            NPCDialogs dialogs1 = getDialogs().get(entityType);
            if (dialogs1 == null || dialogs1.dialogs.isEmpty()) {
                return null;
            }
            return Util.getRandom(dialogs1.dialogs, random);
        }

        public static Loader getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new Loader();
            }
            return INSTANCE;
        }

        public static void handle(Map<EntityType<?>, NPCDialogs> entityTypeNPCDialogsMap) {
            getInstance().dialogs = entityTypeNPCDialogsMap;
        }
    }
}
