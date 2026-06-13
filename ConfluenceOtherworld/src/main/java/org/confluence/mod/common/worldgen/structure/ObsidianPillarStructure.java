package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.util.LibGeometryUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

import static org.confluence.lib.util.LibStructureUtils.frustumSet;
import static org.confluence.lib.util.LibStructureUtils.getHeight;

public class ObsidianPillarStructure extends Structure {
    public static final Codec<ObsidianPillarStructure> CODEC = simpleCodec(ObsidianPillarStructure::new);

    protected ObsidianPillarStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        int lowestY = getHeight(x, z, context);
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            int mainY = random.nextInt(150, 200);
            BlockPos centerPos = startChunk.getMiddleBlockPosition(mainY);
            long seed = random.nextLong();
            NormalNoise normalNoise = NormalNoise.create(RandomSource.create(seed), new NormalNoise.NoiseParameters(-5, 1.0));

            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            int height = (mainY - lowestY);
            float radiusD = 20;

            float xMove = random.nextFloat() * 0.3F;
            float zMove = random.nextFloat() * 0.3F;
            int size = 70;
            int sizePillar = 30;
            float firstRotate = 2 * Mth.PI * random.nextFloat();
            float rotateX = Mth.cos(firstRotate);
            float rotateZ = Mth.sin(firstRotate);
            Vector3f debugX = new Vector3f(rotateX, rotateX * xMove + rotateZ * zMove, rotateZ).normalize(1);
            Vector3f debugZ = new Vector3f(xMove, -1, zMove).normalize(1);
            Vector3f debugY = debugZ.cross(debugX).normalize(1);
            float r45 = Mth.PI / 4;

            for (int xRoll = -size; xRoll < (size + 1); xRoll++) {
                int xP2 = xRoll * xRoll;
                for (int zRoll = -size; zRoll < (size + 1); zRoll++) {
                    int zP2 = zRoll * zRoll;
                    int yRoll = (int) (xRoll * xMove + zRoll * zMove + 0.5);
                    int yP2 = yRoll * yRoll;
                    int radiusP2 = xP2 + yP2 + zP2;
                    Vector3f posNow = new Vector3f(xRoll, yRoll, zRoll);
                    float checkX = posNow.dot(debugX);
                    float checkY = posNow.dot(debugY);
                    float star = (float) (Math.pow(checkX * checkX, 0.2) + Math.pow(checkY * checkY, 0.2));
                    float star2 = (float) (Math.pow(Math.pow(checkX * Mth.cos(r45) - checkY * Mth.sin(r45), 2), 0.3333) + Math.pow(Math.pow(checkX * Mth.sin(r45) + checkY * Mth.cos(r45), 2), 0.3333));
                    if (
                            ((radiusP2 > 2200) && (radiusP2 < 2500)) ||
                                    ((radiusP2 > 1800) && (radiusP2 < 2000)) ||
                                    ((radiusP2 > 400) && (radiusP2 < 500)) ||
                                    ((star < 5.4706) && (star > 4.7817) && ((radiusP2 < 2000) || (radiusP2 > 2200))) ||
                                    ((star2 < 10.6998) && (star2 > 8.5498) && (radiusP2 > 400))
                    ) blockMap.put(centerPos.offset(xRoll, yRoll, zRoll), 3);
                }
            }

            for (float i = 0; i < height; i += 0.3F) {
                float smallRadius = radiusD * 0.5F * (i / height);
                if (random.nextFloat() < (i / height)) {
                    float rotate = i * Mth.PI * 0.1F;
                    BlockPos pos = centerPos.offset((int) (Mth.sin(rotate) * smallRadius), (int) i - height, (int) (Mth.cos(rotate) * smallRadius));
                    blockMap.put(pos, 3);
                }
            }

            int pillarCount = random.nextInt(3, 6);
            float baseRotate = 2 * Mth.PI * random.nextFloat();
            float stepRotate = 2 * Mth.PI / pillarCount;
            List<BlockPos> centerStone = LibGeometryUtils.getBlocksInConvexHull(
                    LibGeometryUtils.ellipsoidPos(
                            random.nextInt(10, 16),
                            random.nextInt(30, 46),
                            random.nextInt(10, 16),
                            centerPos,
                            0.002F,
                            random
                    )
            );
            float scale = 2.0F;
            centerStone.forEach(p -> {
                if (Mth.sin(15 * (float) normalNoise.getValue(p.getX() * scale, p.getY() * scale, p.getZ() * scale)) > -0.3F)
                    blockMap.put(p, 2);
                else blockMap.put(p, 3);
            });
            for (int i = 0; i < pillarCount; i++) {
                float rotate = baseRotate + stepRotate * i;
                int radius = random.nextInt(30, 60);
                int pillarRadius = random.nextInt(2, 5);
                Vector3f pillarPos = new Vector3f(
                        centerPos.getX() + radius * Mth.sin(rotate) + random.nextInt(-15, 16),
                        centerPos.getY() + random.nextInt(-45, 16),
                        centerPos.getZ() + radius * Mth.cos(rotate) + random.nextInt(-5, 6)
                );
                Vector3f pillarEnd = new Vector3f(0, 0, 0).add(pillarPos).add(
                        random.nextInt(-5, 6),
                        -random.nextInt(10, 50),
                        random.nextInt(-5, 6)
                );
                if (random.nextBoolean()) {
                    Vector3f midPoint = new Vector3f(
                            (pillarPos.x + pillarEnd.x) * 0.5F,
                            (pillarPos.y + pillarEnd.y) * 0.5F,
                            (pillarPos.z + pillarEnd.z) * 0.5F
                    );
                    float xMove1 = random.nextFloat() * 0.3F;
                    float zMove1 = random.nextFloat() * 0.3F;
                    float radiusPillar = random.nextInt(5, 18);
                    float radius1P2 = Mth.square(radiusPillar);
                    float radius2P2 = Mth.square(radiusPillar + 1.3877F);
                    float radius3P2 = Mth.square(radiusPillar + 2.6794F);
                    float radius4P2 = Mth.square(radiusPillar + 5.0401F);
                    for (int xRoll = -sizePillar; xRoll < (sizePillar + 1); xRoll++) {
                        int xP2 = xRoll * xRoll;
                        for (int zRoll = -sizePillar; zRoll < (sizePillar + 1); zRoll++) {
                            int zP2 = zRoll * zRoll;
                            int yRoll = (int) (xRoll * xMove1 + zRoll * zMove1 + 0.5);
                            int yP2 = yRoll * yRoll;
                            int radiusP2 = xP2 + yP2 + zP2;
                            if (
                                    ((radiusP2 > radius3P2) && (radiusP2 < radius4P2)) ||
                                            ((radiusP2 > radius1P2) && (radiusP2 < radius2P2))
                            )
                                blockMap.put(LibMathUtils.fromVector3f(midPoint).offset(xRoll, yRoll, zRoll), 3);
                        }
                    }
                }
                frustumSet(pillarEnd, pillarPos, pillarRadius + 0.5F, pillarRadius + 0.5F, 2, blockMap);
            }

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.GLOOM_OBSIDIAN.get().defaultBlockState(),
                    DecorativeBlocks.GLOOM_OBSIDIAN_BRICKS.FULL.get().defaultBlockState(),
                    NatureBlocks.VOID_WEAVE.get().defaultBlockState()
            ), builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.OBSIDIAN_PILLAR.get();
    }
}
