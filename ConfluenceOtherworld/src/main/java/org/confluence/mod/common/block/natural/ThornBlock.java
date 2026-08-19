package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ThornBlock extends PipeBlock {
    public static final IntegerProperty PROP_AGE = IntegerProperty.create("age", 0, 7);

    protected final float damageAmount;
    protected final Supplier<? extends Block> ground;

    public ThornBlock(float damageAmount, Supplier<? extends Block> ground) {
        super(0.3125f, Properties.of().replaceable().noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY).randomTicks());
        this.damageAmount = damageAmount;
        this.ground = ground;
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacement(context.getLevel(), context.getClickedPos());
    }

    public BlockState getStateForPlacement(BlockGetter level, BlockPos pos) {
        BlockState blockState = defaultBlockState();
        for (Direction direction : Direction.values()) {
            BlockState nearState = level.getBlockState(pos.relative(direction));
            blockState = blockState.setValue(PROPERTY_BY_DIRECTION.get(direction), nearState.is(this) || nearState.is(ground.get()));
        }
        return blockState;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide()) return;
        if (entity instanceof ServerPlayer player) {
            player.hurt(LibDamageTypes.of(level, DamageTypes.THORNS), damageAmount);
            level.destroyBlock(pos, false);
        }
        if (entity instanceof Projectile) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, PROP_AGE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.is(NatureBlocks.PLANTERA_THORN.get()) && state.getValue(PROP_AGE) < 7;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this == NatureBlocks.PLANTERA_THORN.get() || random.nextInt(50) != 0) return;
        Direction dir = Direction.getRandom(random);
        BlockPos targetPos = pos.relative(dir);
        if (!level.getBlockState(targetPos).isAir() || checkBorder(level, targetPos, dir)) return;
        int age = state.getValue(PROP_AGE);
        level.setBlock(targetPos, getStateForPlacement(level, targetPos).setValue(PROP_AGE, Math.min(7, age + random.nextInt(1, 3))), Block.UPDATE_CLIENTS);
        level.setBlock(pos, state.setValue(PROPERTY_BY_DIRECTION.get(dir), true).setValue(PROP_AGE, random.nextBoolean() ? 7 : age + 1), Block.UPDATE_CLIENTS);
    }

    private boolean checkBorder(Level level, BlockPos pos, Direction direction) {
        for (Direction value : Direction.values()) {
            if (value == direction || value == direction.getOpposite()) continue;
            if (level.getBlockState(pos.relative(value)).is(this)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), neighborState.is(this) || neighborState.is(ground.get()));
    }
}
