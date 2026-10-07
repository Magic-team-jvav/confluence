package org.confluence.mod.common.data.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
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
        return value == null ? map.keySet().stream().filter(gamePhase::isAtLeast)
                .max(Comparator.comparingInt(GamePhase::getOrder))
                .map(map::get).orElse(AttributeModifiersValue.EMPTY) : value;
    }

    public static void applyModifiers(LivingEntity living) {
        applyModifiers(living, true);
    }

    /// 所有生成与读取路径共用；非新生成实体仅限制当前生命，不因阶段刷新而回血。
    public static void applyModifiers(LivingEntity living, boolean freshSpawn) {
        if (living.level().isClientSide) return;
        if (living instanceof Player) return;
        EntityType<?> type = living.getType();
        if (!CommonConfigs.ALLOWS_VANILLA_ENTITIES_TO_PERFORM_STAGE_ATTRIBUTES.get() &&
                ResourceLocation.DEFAULT_NAMESPACE.equals(type.builtInRegistryHolder().key().location().getNamespace())
        ) return;

        Difficulty difficulty = living.level().getDifficulty();
        if ((difficulty == Difficulty.PEACEFUL || difficulty == Difficulty.EASY)
                && !CreatureDifficultyScaling.isManaged(living)) return;
        GamePhase2AttributeModifiers data = ModDataMaps.getEntityData(ModDataMaps.GAME_PHASE_2_ATTRIBUTE_MODIFIERS, type);
        boolean managed = CreatureDifficultyScaling.isManaged(living);
        if (!managed && data == null) return;
        float health = living.getHealth();
        boolean full = health >= living.getMaxHealth();
        if (managed) {
            ResourceLocation valueId = Confluence.asResource("game_phase_modifier");
            ResourceLocation multiplierId = Confluence.asResource("game_phase_multiplier");
            for (Holder<Attribute> attribute : BuiltInRegistries.ATTRIBUTE.holders().toList()) {
                AttributeInstance instance = living.getAttribute(attribute);
                if (instance != null) {
                    instance.removeModifier(valueId);
                    instance.removeModifier(multiplierId);
                }
            }
        }
        ImmutableListMultimap<Holder<Attribute>, AttributeModifier> modifiers = data == null ? ImmutableListMultimap.of()
                : data.get(KillBoard.INSTANCE.getGamePhase()).get();
        if (!managed && modifiers.isEmpty()) return;
        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : modifiers.asMap().entrySet()) {
            AttributeInstance instance = living.getAttribute(entry.getKey());
            if (instance == null) continue;
            for (AttributeModifier modifier : entry.getValue()) {
                instance.addOrReplacePermanentModifier(modifier);
            }
        }
        if (managed) CreatureDifficultyScaling.apply(living, false);
        living.setHealth(!managed || freshSpawn && full ? living.getMaxHealth() : Math.min(health, living.getMaxHealth()));
    }

    public record Remover(Map<GamePhase, ImmutableListMultimap<Holder<Attribute>, ResourceLocation>> map) implements DataMapValueRemover<EntityType<?>, GamePhase2AttributeModifiers> {
        public static final Codec<Remover> CODEC = Codec.unboundedMap(GamePhase.CODEC, LibCodecUtils.multimap(Attribute.CODEC, ResourceLocation.CODEC)).xmap(Remover::new, Remover::map);

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
