package org.confluence.mod.common.data.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record GamePhase2AttributeModifiers(Map<GamePhase, AttributeModifiersValue> map) {
    public static final Codec<GamePhase2AttributeModifiers> CODEC = Codec.unboundedMap(GamePhase.CODEC, AttributeModifiersValue.CODEC)
            .xmap(GamePhase2AttributeModifiers::new, GamePhase2AttributeModifiers::map);

    public AttributeModifiersValue get(GamePhase gamePhase) {
        AttributeModifiersValue value = map.get(gamePhase);
        if (value != null) return value;
        Optional<GamePhase> min = map.keySet().stream()
                .filter(gamePhase1 -> !gamePhase1.isAtLeast(gamePhase))
                .max(Comparator.comparingInt(GamePhase::getOrder));
        if (min.isEmpty()) return AttributeModifiersValue.EMPTY;
        return map.getOrDefault(min.get(), AttributeModifiersValue.EMPTY);
    }

    public static void applyModifiers(LivingEntity living) {
        if (living instanceof Player) return;
        GamePhase2AttributeModifiers data = ModDataMaps.getEntityData(ModDataMaps.GAME_PHASE_2_ATTRIBUTE_MODIFIERS, living);
        if (data == null) return;
        ImmutableListMultimap<Holder<Attribute>, AttributeModifier> modifiers = data.get(KillBoard.INSTANCE.getGamePhase()).get();
        if (modifiers.isEmpty()) return;
        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : modifiers.asMap().entrySet()) {
            AttributeInstance instance = living.getAttribute(entry.getKey());
            if (instance == null) continue;
            for (AttributeModifier modifier : entry.getValue()) {
                instance.addOrReplacePermanentModifier(modifier);
            }
        }
        living.setHealth(living.getMaxHealth());
    }

    public record Remover(Map<GamePhase, ImmutableListMultimap<Holder<Attribute>, ResourceLocation>> map) implements DataMapValueRemover<EntityType<?>, GamePhase2AttributeModifiers> {
        public static final Codec<Remover> CODEC = Codec.unboundedMap(GamePhase.CODEC, LibCodecUtils.multimapCodec(Attribute.CODEC, ResourceLocation.CODEC)).xmap(Remover::new, Remover::map);

        @Override
        public Optional<GamePhase2AttributeModifiers> remove(
                GamePhase2AttributeModifiers value,
                Registry<EntityType<?>> registry,
                Either<TagKey<EntityType<?>>, ResourceKey<EntityType<?>>> source,
                EntityType<?> object
        ) {
            ImmutableMap.Builder<GamePhase, AttributeModifiersValue> builder = ImmutableMap.builder();
            for (Map.Entry<GamePhase, ImmutableListMultimap<Holder<Attribute>, ResourceLocation>> entry : map.entrySet()) {
                ImmutableListMultimap<Holder<Attribute>, AttributeModifier> multimap = value.map.getOrDefault(entry.getKey(), AttributeModifiersValue.EMPTY).get();
                if (multimap.isEmpty()) continue;
                AttributeModifiersValue.Builder builder1 = AttributeModifiersValue.builder();
                for (Map.Entry<Holder<Attribute>, Collection<ResourceLocation>> entry1 : entry.getValue().asMap().entrySet()) {
                    ImmutableList<AttributeModifier> modifiers = multimap.get(entry1.getKey());
                    if (modifiers.isEmpty()) continue;
                    for (ResourceLocation id : entry1.getValue()) {
                        for (AttributeModifier modifier : modifiers) {
                            if (modifier.id().equals(id)) continue;
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

    public static class Merger implements DataMapValueMerger<EntityType<?>, GamePhase2AttributeModifiers> {
        @Override
        public GamePhase2AttributeModifiers merge(
                Registry<EntityType<?>> registry,
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
