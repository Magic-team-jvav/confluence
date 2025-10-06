package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.init.ModDataMaps;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record BlockBreakSpawns(List<Spawn> spawns) {
    public static final Codec<BlockBreakSpawns> CODEC = Spawn.CODEC.listOf().xmap(BlockBreakSpawns::new, BlockBreakSpawns::spawns);

    public static void spawn(ServerLevel level, BlockPos pos, Holder<Block> holder) {
        BlockBreakSpawns data = holder.getData(ModDataMaps.BLOCK_BREAK_SPAWNS);
        if (data == null) return;
        Holder<Biome> biome = level.getBiome(pos);
        for (Spawn spawn : data.spawns) {
            if (spawn.maxAmount != 0x3F3F3F3F) {
                long count = level.getEntities(null, new AABB(pos).inflate(7.5)).stream()
                        .filter(entity -> spawn.containsType(entity.getType())).count();
                if (count >= spawn.maxAmount) continue;
            }
            if ((spawn.biomes.size() <= 0 || spawn.biomes.contains(biome)) && level.random.nextFloat() < spawn.chance) {
                spawn.types.getRandomValue(level.random).ifPresent(type -> type.spawn(level, pos, MobSpawnType.MOB_SUMMONED));
            }
        }
    }

    public static void spawn(ServerLevel level, BlockPos pos, BlockState state) {
        spawn(level, pos, state.getBlockHolder());
    }

    public static void spawn(ServerLevel level, BlockPos pos, Block block) {
        spawn(level, pos, block.builtInRegistryHolder());
    }

    public static class Spawn {
        public static final Codec<Spawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SimpleWeightedRandomList.wrappedCodec(BuiltInRegistries.ENTITY_TYPE.byNameCodec()).fieldOf("types").forGetter(Spawn::types),
                HolderSetCodec.create(Registries.BIOME, Biome.CODEC, false).lenientOptionalFieldOf("biomes", HolderSet.empty()).forGetter(Spawn::biomes),
                ExtraCodecs.POSITIVE_FLOAT.lenientOptionalFieldOf("chance", 1.0F).forGetter(Spawn::chance),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("maxAmount", 0x3F3F3F3F).forGetter(Spawn::maxAmount)
        ).apply(instance, Spawn::new));
        private final SimpleWeightedRandomList<EntityType<?>> types;
        private final HolderSet<Biome> biomes;
        private final float chance;
        private final int maxAmount;

        private transient Set<EntityType<?>> cachedTypes;

        public Spawn(SimpleWeightedRandomList<EntityType<?>> types, HolderSet<Biome> biomes, float chance, int maxAmount) {
            this.types = types;
            this.biomes = biomes;
            this.chance = chance;
            this.maxAmount = maxAmount;
        }

        public SimpleWeightedRandomList<EntityType<?>> types() {
            return types;
        }

        public HolderSet<Biome> biomes() {
            return biomes;
        }

        public float chance() {
            return chance;
        }

        public int maxAmount() {
            return maxAmount;
        }

        public boolean containsType(EntityType<?> type) {
            if (cachedTypes == null) {
                this.cachedTypes = types.unwrap().stream().map(WeightedEntry.Wrapper::data).collect(Collectors.toSet());
            }
            return cachedTypes.contains(type);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Spawn) obj;
            return Objects.equals(this.types, that.types) &&
                    Objects.equals(this.biomes, that.biomes) &&
                    Float.floatToIntBits(this.chance) == Float.floatToIntBits(that.chance) &&
                    this.maxAmount == that.maxAmount;
        }

        @Override
        public int hashCode() {
            return Objects.hash(types, biomes, chance, maxAmount);
        }

        @Override
        public String toString() {
            return "Spawn[" +
                    "types=" + types + ", " +
                    "biomes=" + biomes + ", " +
                    "chance=" + chance + ", " +
                    "maxAmount=" + maxAmount + ']';
        }
    }
}
