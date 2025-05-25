package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MoistSandBlock extends Block implements BonemealableBlock {

    private record PlantEntry(Supplier<? extends Block> plant, int weight) {
    }

    private static final List<PlantEntry> PLANTS = List.of(
            new PlantEntry(NatureBlocks.SMALL_DESERT_PLANT, 4),
            new PlantEntry(NatureBlocks.BIG_DESERT_PLANT, 2),
            new PlantEntry(NatureBlocks.SMALL_CACTUS, 4)
    );

    private final Block TargetBlock;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public MoistSandBlock(BlockBehaviour.Properties properties, Block TargetSand) {
        super(properties.randomTicks().instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
        this.TargetBlock = TargetSand;
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, true)
                .setValue(EAST, true)
                .setValue(SOUTH, true)
                .setValue(WEST, true)
                .setValue(UP, true)
                .setValue(DOWN, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter blockgetter = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(DOWN, !blockgetter.getBlockState(blockpos.below()).is(TargetBlock))
                .setValue(UP, !blockgetter.getBlockState(blockpos.above()).is(TargetBlock))
                .setValue(NORTH, !blockgetter.getBlockState(blockpos.north()).is(TargetBlock))
                .setValue(EAST, !blockgetter.getBlockState(blockpos.east()).is(TargetBlock))
                .setValue(SOUTH, !blockgetter.getBlockState(blockpos.south()).is(TargetBlock))
                .setValue(WEST, !blockgetter.getBlockState(blockpos.west()).is(TargetBlock));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (!facingState.is(TargetBlock)) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(facing), true);
        }
        return state.setValue(PROPERTY_BY_DIRECTION.get(facing), false);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.NORTH)), state.getValue(NORTH))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.SOUTH)), state.getValue(SOUTH))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.EAST)), state.getValue(EAST))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.WEST)), state.getValue(WEST))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.UP)), state.getValue(UP))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.DOWN)), state.getValue(DOWN));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.NORTH)), state.getValue(NORTH))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.SOUTH)), state.getValue(SOUTH))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.EAST)), state.getValue(EAST))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.WEST)), state.getValue(WEST))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.UP)), state.getValue(UP))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.DOWN)), state.getValue(DOWN));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        BlockPos blockpos = blockPos.above();
        for (int i = 0; i < 128; i++) {
            BlockPos blockpos1 = blockpos;
            if (!isDesertBiomes(serverLevel, blockpos1)) {
                continue;
            }
            for (int j = 0; j < i / 16; j++) {
                blockpos1 = blockpos1.offset(randomSource.nextInt(3) - 1, (randomSource.nextInt(3) - 1) * randomSource.nextInt(3) / 2, randomSource.nextInt(3) - 1);
                if (!isMoistSand(serverLevel.getBlockState(blockpos1.below())) || serverLevel.getBlockState(blockpos1).isCollisionShapeFullBlock(serverLevel, blockpos1)) {
                    break;
                }
            }
            BlockState blockstate1 = serverLevel.getBlockState(blockpos1);
            if (blockstate1.isAir()) {
                BlockState randomPlant = selectRandomPlant(randomSource);
                if (randomPlant.canSurvive(serverLevel, blockpos1)) {
                    serverLevel.setBlock(blockpos1, randomPlant, 3);
                }
            }
        }
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }

    private static boolean isMoistSand(BlockState state) {
        return state.is(NatureBlocks.MOIST_SAND_BLOCK.get()) || state.is(NatureBlocks.RED_MOIST_SAND_BLOCK.get());
    }

    private static boolean isDesertBiomes(ServerLevel serverLevel, BlockPos pos) {
        Holder<Biome> holder = serverLevel.getBiome(pos);
        return holder.is(Biomes.DESERT);
    }

    private static BlockState selectRandomPlant(RandomSource randomSource) {
        int totalWeight = PLANTS.stream().mapToInt(p -> p.weight).sum();
        int randomValue = randomSource.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (MoistSandBlock.PlantEntry entry : PLANTS) {
            cumulativeWeight += entry.weight;
            if (randomValue < cumulativeWeight) {
                return entry.plant.get().defaultBlockState();
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.dimension() == Level.NETHER) {
            if (state.is(NatureBlocks.RED_MOIST_SAND_BLOCK.get())) {
                level.setBlock(pos, Blocks.RED_SAND.defaultBlockState(), 3);
            } else if (state.is(NatureBlocks.MOIST_SAND_BLOCK.get())) {
                level.setBlock(pos, Blocks.SAND.defaultBlockState(), 3);
            }
            level.levelEvent(2009, pos, 0);
            level.playSound(null, pos, SoundEvents.WET_SPONGE_DRIES, SoundSource.BLOCKS, 1.0F, (1.0F + level.getRandom().nextFloat() * 0.2F) * 0.7F);
        }
    }
}
