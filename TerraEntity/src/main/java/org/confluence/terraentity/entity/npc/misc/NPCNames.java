package org.confluence.terraentity.entity.npc.misc;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.confluence.lib.common.data.SingleJsonFileReloadListener;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.utils.TEUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public record NPCNames(Map<String, Float> namesWeights) {
    public static final Codec<NPCNames> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("names_weights").forGetter(NPCNames::namesWeights)
    ).apply(instance, NPCNames::new));

    public static NPCNames of(Map<String, Float> names) {
        return new NPCNames(names);
    }

    public static class Loader extends SingleJsonFileReloadListener {
        public static final Codec<Map<EntityType<?>, NPCNames>> DATA_GEN_CODEC = Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), NPCNames.CODEC);
        private static Loader INSTANCE;
        private Map<EntityType<?>, NPCNames> npcNames = ImmutableMap.of();

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> resourceList) {
            ConditionalOps<JsonElement> ops = makeConditionalOps();
            Map<EntityType<?>, NPCNames> map = new IdentityHashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
                BuiltInRegistries.ENTITY_TYPE.getOptional(entry.getKey()).ifPresent(entityType -> NPCNames.CODEC.parse(ops, entry.getValue())
                        .resultOrPartial(errorMsg -> TerraEntity.LOGGER.warn("Could not decode npc names with json id {} - error: {}", entry.getKey(), errorMsg))
                        .ifPresent(npcNames -> map.computeIfAbsent(entityType, type -> new NPCNames(new HashMap<>())).namesWeights.putAll(npcNames.namesWeights)));
            }
            ImmutableMap.Builder<EntityType<?>, NPCNames> builder = ImmutableMap.builder();
            for (Map.Entry<EntityType<?>, NPCNames> entry : map.entrySet()) {
                builder.put(entry.getKey(), new NPCNames(ImmutableMap.copyOf(entry.getValue().namesWeights)));
            }
            this.npcNames = builder.build();
        }

        @Override
        protected ResourceLocation resourcePath() {
            return TerraEntity.space("npc/names.json");
        }

        @Override
        protected String identifier() {
            return "NPC Names";
        }

        public Map<EntityType<?>, NPCNames> getNpcNames() {
            return npcNames;
        }

        public @Nullable NPCNames getNames(EntityType<?> entityType) {
            return getNpcNames().get(entityType);
        }

        public @Nullable String getRandomName(EntityType<?> entityType) {
            NPCNames names = getNames(entityType);
            if (names == null || names.namesWeights.isEmpty()) {
                return null;
            }
            return TEUtils.getRandomByWeight(names.namesWeights);
        }

        public static Loader getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new Loader();
            }
            return INSTANCE;
        }
    }
}
