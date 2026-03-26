package org.confluence.mod.common.block.natural;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Map;
import java.util.Optional;

public class BalloonAttachedStemBlock extends BushBlock {
    public static final MapCodec<BalloonAttachedStemBlock> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                    ResourceKey.codec(Registries.BLOCK).fieldOf("stem").forGetter(b -> b.stem),
                    ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter(b -> b.fruit),
                    ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter(b -> b.seed),
                    propertiesCodec())
            .apply(builder, BalloonAttachedStemBlock::new));

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.SOUTH, Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 16.0))
            .put(Direction.WEST, Block.box(0.0, 0.0, 6.0, 10.0, 10.0, 10.0))
            .put(Direction.NORTH, Block.box(6.0, 0.0, 0.0, 10.0, 10.0, 10.0))
            .put(Direction.EAST, Block.box(6.0, 0.0, 6.0, 16.0, 10.0, 10.0))
            .put(Direction.UP, Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0))
            .build());

    private final ResourceKey<Block> fruit;
    private final ResourceKey<Block> stem;
    private final ResourceKey<Item> seed;

    public BalloonAttachedStemBlock(ResourceKey<Block> stem, ResourceKey<Block> fruit, ResourceKey<Item> seed, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
        this.stem = stem;
        this.fruit = fruit;
        this.seed = seed;
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABBS.get(state.getValue(FACING));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Direction attachedDirection = state.getValue(FACING);
        if (facing == attachedDirection) {
            if (!facingState.is(this.fruit)) {
                Optional<Block> optionalStem = level.registryAccess()
                        .registryOrThrow(Registries.BLOCK)
                        .getOptional(this.stem);
                if (optionalStem.isPresent()) {
                    BlockState stemState = optionalStem.get().defaultBlockState();
                    if (stemState.hasProperty(StemBlock.AGE)) {
                        return stemState.setValue(StemBlock.AGE, 7);
                    }
                }
            }
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() instanceof FarmBlock ||
                state.is(NatureBlocks.CLOUD_BLOCK.get()) ||
                state.is(NatureBlocks.RAIN_CLOUD_BLOCK.get());
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(this.seed)
                .map(ItemStack::new)
                .orElse(new ItemStack(net.minecraft.world.item.Items.AIR));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
