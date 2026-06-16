package org.confluence.mod.common.data.saved;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.util.NaturalSpawnerUtils;
import org.confluence.lib.util.function.ints.ToIntFunction4;
import org.confluence.mod.util.OverworldUtils;

import java.util.Optional;

public class SpaceSpawner implements CustomSpawner {
    private int nextTick;
    private final WeightedRandomList<Pair> spawnerData = WeightedRandomList.create(
            new Pair(new MobSpawnSettings.SpawnerData(TEMonsterEntities.HARPY.get(), 3, 1, 1), (level, pos, csd, sd) -> {
                RandomSource random = level.random;
                int i = 0;
                DifficultyInstance difficulty = level.getCurrentDifficultyAt(pos);
                if (difficulty.getEffectiveDifficulty() * csd.speedMultiplier() > random.nextFloat() * 3) {
                    BlockPos spawnPos = pos.offset(
                            -10 + random.nextInt(21),
                            10 + random.nextInt(15),
                            -10 + random.nextInt(21)
                    );
                    if (level.getEntities(sd.type, new AABB(spawnPos).inflate(36.0, 36.0, 36.0), EntitySelector.NO_SPECTATORS).size() < csd.getCount(8)) {
                        BlockState blockState = level.getBlockState(spawnPos);
                        FluidState fluidState = level.getFluidState(spawnPos);
                        if (SpawnPlacements.isSpawnPositionOk(sd.type, level, spawnPos) &&
                                NaturalSpawner.isValidEmptySpawnBlock(level, spawnPos, blockState, fluidState, sd.type) &&
                                SpawnPlacements.checkSpawnRules(sd.type, level, MobSpawnType.SPAWNER, spawnPos, random)
                        ) {
                            SpawnGroupData spawnGroupData = null;
                            int amount = Mth.randomBetweenInclusive(random, sd.minCount, sd.maxCount);

                            for (int j = 0; j < amount; j++) {
                                Entity entity = sd.type.create(level);
                                if (entity != null) {
                                    entity.moveTo(SpawnPlacements.getPlacementType(sd.type).adjustSpawnPosition(level, spawnPos), 0.0F, 0.0F);
                                    if (entity instanceof Mob mob) {
                                        spawnGroupData = mob.finalizeSpawn(level, difficulty, MobSpawnType.NATURAL, spawnGroupData);
                                    }
                                    level.addFreshEntityWithPassengers(entity);
                                    i++;
                                }
                            }
                        }
                    }
                }
                return i;
            }),
            new Pair(new MobSpawnSettings.SpawnerData(TEMonsterEntities.WYVERN.get(), 2, 1, 1), (level, pos, csd, sd) -> {
                RandomSource random = level.random;
                int i = 0;
                DifficultyInstance difficulty = level.getCurrentDifficultyAt(pos);
                if (difficulty.getEffectiveDifficulty() * csd.speedMultiplier() > random.nextFloat() * 3) {
                    BlockPos spawnPos = pos.offset(
                            -10 + random.nextInt(21),
                            10 + random.nextInt(15),
                            -10 + random.nextInt(21)
                    );
                    if (level.getEntities(sd.type, new AABB(spawnPos).inflate(36.0, 36.0, 36.0), EntitySelector.NO_SPECTATORS).size() < csd.getCount(6)) {
                        BlockState blockState = level.getBlockState(spawnPos);
                        FluidState fluidState = level.getFluidState(spawnPos);
                        if (SpawnPlacements.isSpawnPositionOk(sd.type, level, spawnPos) &&
                                NaturalSpawner.isValidEmptySpawnBlock(level, spawnPos, blockState, fluidState, sd.type) &&
                                SpawnPlacements.checkSpawnRules(sd.type, level, MobSpawnType.SPAWNER, spawnPos, random)
                        ) {
                            SpawnGroupData spawnGroupData = null;
                            int amount = Mth.randomBetweenInclusive(random, sd.minCount, sd.maxCount);

                            for (int j = 0; j < amount; j++) {
                                Entity entity = sd.type.create(level);
                                if (entity != null) {
                                    entity.moveTo(SpawnPlacements.getPlacementType(sd.type).adjustSpawnPosition(level, spawnPos), 0.0F, 0.0F);
                                    if (entity instanceof Mob mob) {
                                        spawnGroupData = mob.finalizeSpawn(level, difficulty, MobSpawnType.NATURAL, spawnGroupData);
                                    }
                                    level.addFreshEntityWithPassengers(entity);
                                    i++;
                                }
                            }
                        }
                    }
                }
                return i;
            })
    );

    @Override
    public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if (!spawnEnemies) return 0;
        RandomSource random = level.random;
        --this.nextTick;
        if (nextTick > 0) return 0;
        this.nextTick = nextTick + (5 + random.nextInt(5)) * 20;

        Long2ObjectMap<NaturalSpawnerUtils.ChunkSpawnData> map = NaturalSpawnerUtils.getDimensionChunkSpawnData(level.dimension());
        if (map == null) return 0;

        int count = 0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.getY() < OverworldUtils.getSpaceY()) continue;
            BlockPos pos = player.blockPosition();
            NaturalSpawnerUtils.ChunkSpawnData data = map.getOrDefault(player.chunkPosition().toLong(), NaturalSpawnerUtils.ChunkSpawnData.DEFAULT);
            for (int i = 0; i < 4; i++) {
                Optional<Pair> optional = spawnerData.getRandom(random);
                if (optional.isPresent()) {
                    Pair pair = optional.get();
                    int j = pair.function.applyAsInt(level, pos, data, pair.data);
                    if (j > 0) {
                        count += j;
                        break;
                    }
                }
            }
        }

        return count;
    }

    private record Pair(
            MobSpawnSettings.SpawnerData data,
            ToIntFunction4<ServerLevel, BlockPos, NaturalSpawnerUtils.ChunkSpawnData, MobSpawnSettings.SpawnerData> function
    ) implements WeightedEntry {
        @Override
        public Weight getWeight() {
            return data.getWeight();
        }
    }
}
