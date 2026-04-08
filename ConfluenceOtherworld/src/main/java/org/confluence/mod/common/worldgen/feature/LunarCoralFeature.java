package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.block.natural.LunarCoralBlock;
import org.confluence.mod.common.block.natural.LunarCoralFanBlock;
import org.confluence.mod.common.block.natural.LunarCoralPlantBlock;
import org.joml.Vector2i;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.confluence.lib.util.VectorUtils.ballPos;
import static org.confluence.lib.util.VectorUtils.lightningPathList;
import static org.confluence.mod.common.block.natural.LunarCoralBlock.HUMIDITY;

public class LunarCoralFeature extends Feature<LunarCoralFeature.Config> {
    public LunarCoralFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    private static final TagKey<Block> END_PLANT_CAN_SURVIVE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("confluence", "end_plant_can_survive"));
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin().offset(0, -5, 0);

        BlockState coralBlock = config.coral().getState(random, basePos);
        BlockState coralPlantBlock = config.coralPlant().getState(random, basePos);
        BlockState coralFanBlock = config.coralFan().getState(random, basePos);

        float density = 1F;
        float length = 1.5F;
        int count = random.nextInt(4, 15);
        float halfCount = (float) count / 2;
        float rotateStep = 2 * Mth.PI / count;
        float rotateStart = random.nextFloat() * 2 * Mth.PI;
        float radiusMax = 1;

        for (int c = 0; c < count; c++) {
            Map<Vector2i, Integer> placeMap = new HashMap<>();
            Map<Vector2i, BlockState> stateMap = new HashMap<>();
            radiusMax = 5 + random.nextFloat() * 3;
            float radius = (halfCount * halfCount - (c - count) * (c - count)) * (radiusMax / (2 * halfCount * halfCount)) + radiusMax / 2;
            float height = (random.nextFloat() + 1) * (c + 1);
            float radius_2 = radius * radius;
            float interNumber = Mth.sqrt(Mth.abs(Mth.cos(radius * density))) * Mth.cos(radius * density) / Mth.abs(Mth.cos(radius * density)) * length;
            float rotate = rotateStart + rotateStep * c * 2;
            float maxDebugZ = 2 * radius + length - interNumber;
            float sinR = Mth.sin(rotate);
            float cosR = Mth.cos(rotate);
            float pX1 = sinR * radius;
            float pX2 = -pX1; // -sinR * radius
            float pX3 = pX1 - maxDebugZ * cosR; // sinR * radius - maxDebugZ * cosR
            float pX4 = pX2 - maxDebugZ * cosR; // -sinR * radius - maxDebugZ * cosR
            float pZ1 = cosR * radius;
            float pZ2 = -pZ1; // -cosR * radius
            float pZ3 = pZ1 + maxDebugZ * sinR; // cosR * radius + maxDebugZ * sinR
            float pZ4 = pZ2 + maxDebugZ * sinR; // -cosR * radius + maxDebugZ * sinR

            int maxX = Mth.floor(Math.max(Math.max(pX1, pX2), Math.max(pX3, pX4))) + 1;
            int minX = Mth.floor(Math.min(Math.min(pX1, pX2), Math.min(pX3, pX4))) - 1;
            int maxZ = Mth.floor(Math.max(Math.max(pZ1, pZ2), Math.max(pZ3, pZ4))) + 1;
            int minZ = Mth.floor(Math.min(Math.min(pZ1, pZ2), Math.min(pZ3, pZ4))) - 1;
            int angleScale = random.nextInt(10, 16);

            for (int x = minX; x <= maxX; x++) {
                int x_2 = x * x;
                for (int z = minZ; z <= maxZ; z++) {
                    int z_2 = z * z;
                    float debugX = x * sinR + z * cosR;
                    float debugZ = z * sinR - x * cosR;
                    if ((debugX < radius) && (debugX > -radius) && (debugZ < maxDebugZ) && (debugZ > 0)) {
                        float upLine = Mth.sqrt(radius_2 - debugX * debugX) + Mth.sqrt(Mth.abs(Mth.cos(debugX * density))) * Mth.cos(debugX * density) / Mth.abs(Mth.cos(debugX * density)) * length + radius - interNumber;
                        float downLine = (Mth.abs(debugX) - Mth.sqrt(radius_2 - debugX * debugX) + radius) / 2;
                        if ((debugZ < upLine) && (debugZ > downLine)) {
                            float dis = Mth.sqrt(x_2 + z_2);
                            float angle = (float) Math.acos(debugX / dis);
                            int y = (int) ((maxDebugZ * maxDebugZ - (dis - maxDebugZ) * (dis - maxDebugZ)) * (height + 1.5 * (Mth.cos(angle * angleScale) + 1)) / (maxDebugZ * maxDebugZ));
                            Vector2i pos = new Vector2i(x, z);
                            placeMap.put(pos, y);
                            stateMap.put(pos, coralBlock.trySetValue(LunarCoralBlock.HUMIDITY, (int) (debugZ / ((upLine + 0.5) / 4))));
                        }
                    }
                }
            }

            for (Map.Entry<Vector2i, Integer> entry : placeMap.entrySet()) {
                Vector2i pos = entry.getKey();
                BlockState set = stateMap.get(pos);
                int y = entry.getValue();
                int minY = y;
                for (int i = 0; i < 4; i++) {
                    Direction direction = Direction.from2DDataValue(i);
                    Vector2i neighborPos = new Vector2i(0, 0).add(pos).add(direction.getNormal().getX(), direction.getNormal().getZ());
                    Integer debugY = placeMap.get(neighborPos);
                    if (debugY != null) {
                        minY = Math.min(debugY, minY);
                    }
                }
                int heightPlace = y - minY;
                if (heightPlace <= 1) {
                    level.setBlock(basePos.offset(pos.x, y, pos.y), set, 3);
                } else {
                    for (int i = 0; i < heightPlace; i++) {
                        level.setBlock(basePos.offset(pos.x, y - i, pos.y), set, 3);
                    }
                }
            }
        }

        radiusMax *= 1.5F;
        int radiusInt = (int) radiusMax + 1;

        for (int x = -radiusInt; x < (radiusInt + 1); x++) {
            int x2 = x * x;
            for (int z = -radiusInt; z < (radiusInt + 1); z++) {
                int z2 = z * z;
                double dis = Mth.sqrt(x2 + z2);
                if (random.nextDouble() > dis / radiusMax) {
                    int offset = 0;
                    for (int i = 0; i < 10; i++) {
                        if (level.getBlockState(basePos.offset(x, offset, z)).is(END_PLANT_CAN_SURVIVE) && level.getBlockState(basePos.offset(x, offset + 1, z)).canBeReplaced()) {
                            int humidity = Mth.clamp((int) ((dis + 1) / (radiusMax / 4)), 0, 3);
                            level.setBlock(basePos.offset(x, offset + 1, z), random.nextBoolean() ? coralFanBlock.trySetValue(LunarCoralFanBlock.HUMIDITY, humidity).trySetValue(WATERLOGGED, false) : coralPlantBlock.trySetValue(LunarCoralPlantBlock.HUMIDITY, humidity).trySetValue(WATERLOGGED, false), 3);
                            break;
                        } else if (level.getBlockState(basePos.offset(x, offset, z)).canBeReplaced()) {
                            offset--;
                        } else if (!level.getBlockState(basePos.offset(x, offset, z)).canBeReplaced() && !level.getBlockState(basePos.offset(x, offset + 1, z)).canBeReplaced()) {
                            offset++;
                        } else break;
                    }
                }
            }
        }

        return true;
    }

    public record Config(BlockStateProvider coral, BlockStateProvider coralPlant, BlockStateProvider coralFan) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("coral_block").forGetter(Config::coral),
                BlockStateProvider.CODEC.fieldOf("coral").forGetter(Config::coralPlant),
                BlockStateProvider.CODEC.fieldOf("coral_fan").forGetter(Config::coralFan)
        ).apply(instance, Config::new));
    }
}
