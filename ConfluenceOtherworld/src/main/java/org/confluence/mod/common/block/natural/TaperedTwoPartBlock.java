package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static org.confluence.mod.common.block.natural.TaperedTwoPartBlock.TaperedTwoPart.*;

public class TaperedTwoPartBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
    public static final EnumProperty<TaperedTwoPart> PART = EnumProperty.create("stalagmite_thickness", TaperedTwoPart.class);
    protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape TIP_SHAPE_UP = Block.box(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
    protected static final VoxelShape TIP_SHAPE_DOWN = Block.box(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape BASE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    /// 崩落补掉落时最多顺延处理的格数，防御性上限
    private static final int MAX_ORPHAN_CHAIN = 64;

    public TaperedTwoPartBlock() {
        super(BlockBehaviour.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY));
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false).setValue(TIP_DIRECTION, Direction.UP).setValue(PART, TIP_SINGLE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, TIP_DIRECTION, PART);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        Direction ownDirection = state.getValue(TIP_DIRECTION);
        if ((state.getValue(PART) == TIP_SINGLE) && level.getBlockState(pos.relative(ownDirection)).is(this.asBlock()) && ownDirection == level.getBlockState(pos.relative(ownDirection)).getValue(TIP_DIRECTION)) {
            return state.setValue(PART, BASE);
        } else if ((state.getValue(PART) == BASE) && level.getBlockState(pos.relative(ownDirection)).is(this.asBlock()) && ownDirection == level.getBlockState(pos.relative(ownDirection)).getValue(TIP_DIRECTION)) {
            return state.setValue(PART, BASE);
        } else if ((state.getValue(PART) == TIP_MERGE) && level.getBlockState(pos.relative(ownDirection.getOpposite())).is(this.asBlock()) && ownDirection == level.getBlockState(pos.relative(ownDirection.getOpposite())).getValue(TIP_DIRECTION)) {
            return state.setValue(PART, TIP_MERGE);
        }
        if (!canSurvive(state, level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return state.setValue(PART, TIP_SINGLE);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, false);
        }
    }

    /// 两格锥体（上半 + 下半）被玩家挖掉其中一格时，另一格会因为失去支撑而崩掉。
    /// 这里让崩掉的每一格都按「玩家亲手挖掉它」的规则结算掉落，
    /// 石笋 / 冰锥都只在精准采集下掉落：精准采集挖掉下半时上半也一起掉，普通工具则整串消失。
    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!level.isClientSide) {
            dropOrphanedTips(level, player, pos, state, tool);
        }
    }

    /// 沿着锥尖方向把「靠刚被挖掉的那格撑着」的方块逐个结算掉落并移除。
    /// 正常只会有一格（世界生成最多 2 格），玩家叠高时也能整串处理。
    private void dropOrphanedTips(Level level, Player player, BlockPos pos, BlockState state, ItemStack tool) {
        Direction dir = state.getValue(TIP_DIRECTION);
        BlockPos orphanPos = pos.relative(dir);
        for (int i = 0; i < MAX_ORPHAN_CHAIN; i++) {
            BlockState orphan = level.getBlockState(orphanPos);
            if (!orphan.is(this) || orphan.getValue(TIP_DIRECTION) != dir) return;
            // 支撑位正是上一格；若它还撑得住（例如接在别的实心方块上）就到此为止
            if (isValidTaperedPlacement(level, orphanPos, dir)) return;
            Block.dropResources(orphan, level, orphanPos, level.getBlockEntity(orphanPos), player, tool);
            level.destroyBlock(orphanPos, false);
            orphanPos = orphanPos.relative(dir);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction opposite = context.getNearestLookingVerticalDirection().getOpposite();
        Direction dir = calculateTipDirection(level, blockpos, opposite);
        if (dir == null) return null;
        TaperedTwoPart part = calculatePart(level, blockpos, dir);
        return defaultBlockState().setValue(TIP_DIRECTION, dir).setValue(PART, part)
                .setValue(WATERLOGGED, level.getFluidState(blockpos).getType() == Fluids.WATER);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = switch (state.getValue(PART)) {
            case BASE -> BASE_SHAPE;
            case TIP_MERGE, TIP_SINGLE -> state.getValue(TIP_DIRECTION) == Direction.DOWN ? TIP_SHAPE_DOWN : TIP_SHAPE_UP;
        };
        Vec3 offset = state.getOffset(level, pos);
        return shape.move(offset.x, 0.0, offset.z);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isValidTaperedPlacement(level, pos, state.getValue(TIP_DIRECTION));
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (state.getValue(TIP_DIRECTION) == Direction.UP && state.getValue(PART).isTip()) {
            entity.causeFallDamage(fallDistance + 2.0F, 2.0F, level.damageSources().stalagmite());
        } else {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected float getMaxHorizontalOffset() {
        return 0.125F;
    }

    private boolean isValidTaperedPlacement(LevelReader level, BlockPos pos, Direction dir) {
        BlockPos support = pos.relative(dir.getOpposite());
        BlockState state = level.getBlockState(support);
        return state.isFaceSturdy(level, support, dir) || state.is(this.asBlock());
    }

    private @Nullable Direction calculateTipDirection(LevelReader level, BlockPos pos, Direction dir) {
        if (isValidTaperedPlacement(level, pos, dir)) {
            return dir;
        } else {
            return isValidTaperedPlacement(level, pos, dir.getOpposite()) ? dir.getOpposite() : null;
        }
    }

    private TaperedTwoPart calculatePart(LevelReader level, BlockPos pos, Direction dir) {
        BlockState base = level.getBlockState(pos.relative(dir.getOpposite()));
        if (base.is(this) && base.getValue(TIP_DIRECTION) == dir && base.getValue(PART) == TIP_SINGLE) {
            return TIP_MERGE;
        }
        return TIP_SINGLE;
    }

    public enum TaperedTwoPart implements StringRepresentable {
        BASE("base"),
        TIP_MERGE("tip_merge"),
        TIP_SINGLE("tip_single");

        private final String name;

        TaperedTwoPart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public boolean isTip() {
            return this != BASE;
        }
    }
}
