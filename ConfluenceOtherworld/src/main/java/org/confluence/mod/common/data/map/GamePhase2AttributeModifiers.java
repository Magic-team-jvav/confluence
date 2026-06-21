package org.confluence.mod.common.data.map;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attribute.PortAttributeExtension;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.mesdag.portlib.datamap.PortDataMapValueMerger;
import org.mesdag.portlib.datamap.PortDataMapValueRemover;
import org.mesdag.portlib.wrapper.core.PortRegistry;

import java.util.*;
import java.util.stream.Stream;

public record GamePhase2AttributeModifiers(Map<GamePhase, AttributeModifiersValue> map) {
    public static final Codec<GamePhase2AttributeModifiers> CODEC = Codec.unboundedMap(GamePhase.CODEC, AttributeModifiersValue.CODEC)
            .xmap(GamePhase2AttributeModifiers::new, GamePhase2AttributeModifiers::map);

    public AttributeModifiersValue get(GamePhase gamePhase) {
        AttributeModifiersValue value = map.get(gamePhase);
        return value == null ? map.keySet().stream().filter(gamePhase::isAtLeast)
                .max(Comparator.comparingInt(GamePhase::getOrder))
                .map(map::get).orElse(AttributeModifiersValue.EMPTY) : value;
    }

    public static void applyModifiers(LivingEntity living) {
        if (living instanceof Player) return;
        EntityType<?> type = living.getType();
        if (!CommonConfigs.ALLOWS_VANILLA_ENTITIES_TO_PERFORM_STAGE_ATTRIBUTES.get() &&
                ResourceLocation.DEFAULT_NAMESPACE.equals(type.builtInRegistryHolder().key().location().getNamespace())
        ) return;

        Difficulty difficulty = living.level().getDifficulty();
        if (difficulty == Difficulty.PEACEFUL || difficulty == Difficulty.EASY) return;
        GamePhase2AttributeModifiers data = ModDataMaps.getEntityData(ModDataMaps.GAME_PHASE_2_ATTRIBUTE_MODIFIERS, type);
        if (data == null) return;
        ImmutableListMultimap<Attribute, AttributeModifier> modifiers = data.get(KillBoard.INSTANCE.getGamePhase()).get();
        if (modifiers.isEmpty()) return;
        for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : modifiers.asMap().entrySet()) {
            AttributeInstance instance = living.getAttribute(entry.getKey());
            if (instance == null) continue;
            for (AttributeModifier modifier : entry.getValue()) {
                instance.addOrReplacePermanentModifier(modifier);
            }
        }
        living.setHealth(living.getMaxHealth());
    }

    public record Remover(
            Map<GamePhase, ImmutableListMultimap<Attribute, UUID>> map
    ) implements PortDataMapValueRemover<EntityType<?>, GamePhase2AttributeModifiers> {
        public static final Codec<Remover> CODEC = Codec.unboundedMap(GamePhase.CODEC, LibCodecUtils.multimap(PortAttributeExtension.directCodec(), UUIDUtil.STRING_CODEC)).xmap(Remover::new, Remover::map);

        @Override
        public Optional<GamePhase2AttributeModifiers> remove(
                GamePhase2AttributeModifiers value,
                PortRegistry<EntityType<?>> registry,
                Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>> source,
                EntityType<?> object
        ) {
            ImmutableMap.Builder<GamePhase, AttributeModifiersValue> builder = ImmutableMap.builder();
            for (Map.Entry<GamePhase, ImmutableListMultimap<Attribute, UUID>> entry : map.entrySet()) {
                ImmutableListMultimap<Attribute, AttributeModifier> multimap = value.map.getOrDefault(entry.getKey(), AttributeModifiersValue.EMPTY).get();
                if (multimap.isEmpty()) continue;
                AttributeModifiersValue.Builder builder1 = AttributeModifiersValue.builder();
                for (Map.Entry<Attribute, Collection<UUID>> entry1 : entry.getValue().asMap().entrySet()) {
                    ImmutableList<AttributeModifier> modifiers = multimap.get(entry1.getKey());
                    if (modifiers.isEmpty()) continue;
                    for (UUID id : entry1.getValue()) {
                        for (AttributeModifier modifier : modifiers) {
                            if (modifier.getId().equals(id)) continue;
                            builder1.add(entry1.getKey(), modifier);
                        }
                    }
                }
                AttributeModifiersValue value1 = builder1.build();
                if (value1.isEmpty()) continue;
                builder.put(entry.getKey(), value1);
            }
            ImmutableMap<GamePhase, AttributeModifiersValue> map1 = builder.build();
            return map1.isEmpty() ? Optional.empty() : Optional.of(new GamePhase2AttributeModifiers(map1));
        }
    }

    public static class Merger implements PortDataMapValueMerger<EntityType<?>, GamePhase2AttributeModifiers> {
        @Override
        public GamePhase2AttributeModifiers merge(
                PortRegistry<EntityType<?>> registry,
                Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>> first,
                GamePhase2AttributeModifiers firstValue,
                Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>> second,
                GamePhase2AttributeModifiers secondValue
        ) {
            ImmutableMap.Builder<GamePhase, AttributeModifiersValue> builder = ImmutableMap.builder();
            Stream.concat(firstValue.map.keySet().stream(), secondValue.map.keySet().stream()).distinct().forEach(gamePhase -> {
                AttributeModifiersValue.Builder builder1 = AttributeModifiersValue.builder();
                AttributeModifiersValue value = firstValue.map.get(gamePhase);
                if (value != null) builder1.addAll(value.get());
                value = secondValue.map.get(gamePhase);
                if (value != null) builder1.addAll(value.get());
                builder.put(gamePhase, builder1.build());
            });
            return new GamePhase2AttributeModifiers(builder.build());
        }
    }
}
