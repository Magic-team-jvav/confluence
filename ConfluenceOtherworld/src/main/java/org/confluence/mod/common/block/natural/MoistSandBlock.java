package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.worldgen.feature.PlantPatchFeature;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MoistSandBlock extends Block implements BonemealableBlock {
    private final Block TargetBlock;
    protected static final BooleanProperty NORTH = PipeBlock.NORTH;
    protected static final BooleanProperty EAST = PipeBlock.EAST;
    protected static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    protected static final BooleanProperty WEST = PipeBlock.WEST;
    protected static final BooleanProperty UP = PipeBlock.UP;
    protected static final BooleanProperty DOWN = PipeBlock.DOWN;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

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
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos pos, BlockState state) {
        BlockPos targetPos = pos.above();
        PlantPatchFeature plantPatchFeature = new PlantPatchFeature(PlantPatchFeature.Config.CODEC);
        WeightedStateProvider plantProvider = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                .add(NatureBlocks.SMALL_DESERT_PLANT.get().defaultBlockState(), 50)
                .add(NatureBlocks.SMALL_CACTUS.get().defaultBlockState(), 30)
                .add(NatureBlocks.BIG_DESERT_PLANT.get().defaultBlockState(), 20)
                .build());
        PlantPatchFeature.Config config = new PlantPatchFeature.Config(plantProvider,
                NatureBlocks.BIG_DESERT_PLANT.get(), 1, 6, 128,
                List.of(serverLevel.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.DESERT)));
        if (!serverLevel.getBlockState(targetPos).isAir()) return;
        if (!isMoistSand(serverLevel.getBlockState(targetPos.below()))) return;
        plantPatchFeature.place(new FeaturePlaceContext<>(
                Optional.empty(),
                serverLevel,
                serverLevel.getChunkSource().getGenerator(),
                randomSource,
                targetPos,
                config
        ));
    }

    private boolean isMoistSand(BlockState state) {
        return state.is(NatureBlocks.MOISTENED_SAND_BLOCK.get()) || state.is(NatureBlocks.MOISTENED_RED_SAND_BLOCK.get());
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.dimension() == Level.NETHER) {
            if (state.is(NatureBlocks.MOISTENED_RED_SAND_BLOCK.get())) {
                level.setBlock(pos, Blocks.RED_SAND.defaultBlockState(), 3);
            } else if (state.is(NatureBlocks.MOISTENED_SAND_BLOCK.get())) {
                level.setBlock(pos, Blocks.SAND.defaultBlockState(), 3);
            }
            level.levelEvent(2009, pos, 0);
            level.playSound(null, pos, SoundEvents.WET_SPONGE_DRIES, SoundSource.BLOCKS, 1.0F, (1.0F + level.getRandom().nextFloat() * 0.2F) * 0.7F);
        }
    }
}
