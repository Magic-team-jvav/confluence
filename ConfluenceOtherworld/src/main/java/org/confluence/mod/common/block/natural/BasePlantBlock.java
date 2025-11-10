package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasePlantBlock extends BushBlock {
    public static final MapCodec<BasePlantBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(propertiesCodec(),
            BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("ground").forGetter(block -> block.survive)
    ).apply(instance, BasePlantBlock::new));
    protected static final VoxelShape SHAPE = box(2, 0, 2, 14, 13, 14);

    protected final List<Block> survive;
    private transient Set<Block> cache;

    public BasePlantBlock(Block... survive) {
        this(Properties.ofFullCopy(Blocks.DANDELION).replaceable(), Arrays.stream(survive).toList());
    }

    public BasePlantBlock(Properties prop, List<Block> survive) {
        super(prop);
        this.survive = survive;
    }

    @Override
    @NotNull
    protected MapCodec<BasePlantBlock> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(level, pos);
        return SHAPE.move(offset.x, offset.y, offset.z);
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        if (cache == null) this.cache = new HashSet<>(survive);
        return cache.contains(state.getBlock());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return mayPlaceOn(belowState, level, below);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (canSurvive(defaultBlockState(), level, pos)) {
            return defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    /**
     * 邪恶草和蘑菇下方的方块被转化时转化自身
     */
    @Override
    @NotNull
    public BlockState updateShape(BlockState originState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState after = super.updateShape(originState, facing, facingState, level, currentPos, facingPos);
        if (facing != Direction.DOWN) return after;
        ISpreadable.Type type = facingState.getBlock() instanceof ISpreadable sp ? sp.getSpreadType() : ISpreadable.Type.PURE;
        BlockState transformResult = type.getNotNull(originState);  // 默认不转化，如果结果是摧毁则是写到表里面
        return transformResult.canSurvive(level, currentPos) ? transformResult : Blocks.AIR.defaultBlockState();
    }
}
