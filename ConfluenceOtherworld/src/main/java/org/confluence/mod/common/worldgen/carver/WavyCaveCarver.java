package org.confluence.mod.common.worldgen.carver;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.confluence.lib.util.LibGeometryUtils;
import org.confluence.lib.util.LibVectorUtils;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.joml.Vector3d;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class WavyCaveCarver extends WorldCarver<CarverConfiguration> {
    public WavyCaveCarver(Codec<CarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, CarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        BlockPos start = chunkPos.getBlockAt(random.nextInt(16, 32) * (random.nextBoolean() ? -1 : 1), config.y.sample(random, context) - random.nextInt(Math.abs(context.getMinGenY())), random.nextInt(16, 32) * (random.nextBoolean() ? -1 : 1));
        BlockPos end = chunkPos.getBlockAt(random.nextInt(32, 48) * (random.nextBoolean() ? -1 : 1), config.y.sample(random, context) - random.nextInt(Math.abs(context.getMinGenY())), random.nextInt(32, 48) * (random.nextBoolean() ? -1 : 1));
        BlockPos deltaPos = end.subtract(start);
        BlockPos middle = start.offset(deltaPos.getX() / 2, deltaPos.getY() / 2, deltaPos.getZ() / 2);
        deltaPos = middle.subtract(start);
        BlockPos a = start.offset(deltaPos.getX() / 2, deltaPos.getY() / 2 + random.nextInt(32, 48), deltaPos.getZ() / 2);
        deltaPos = end.subtract(middle);
        BlockPos b = start.offset(deltaPos.getX() / 2, deltaPos.getY() / 2 + random.nextInt(32, 48), deltaPos.getZ() / 2);

        List<Vector3d> positions = Lists.newArrayList(Stream.of(start, a, middle, b, end).map(LibVectorUtils::toVector3d).toList());
        LibGeometryUtils.lightningPathList(positions, 2.5, 0.125F, random);
        float yScale = config.yScale.sample(random);
        int size = positions.size();
        for (int i = 0; i < size; i++) {
            Vector3d position = positions.get(i);
            float delta = Math.abs(i - size * 0.5F) / size;
            int radius = 8 - Mth.lerpInt(delta, 4, 8);
            carveEllipsoid(context, config, chunk, biomeAccessor, aquifer, position.x, position.y, position.z, radius, yScale, carvingMask, (context1, relativeX, relativeY, relativeZ, y) -> false);
        }
        return true;
    }

    @Override
    public boolean isStartChunk(CarverConfiguration config, RandomSource random) {
        if (ModSecretSeeds.THE_CONSTANT.match()) {
            return random.nextFloat() < config.probability;
        }
        return false;
    }
}
