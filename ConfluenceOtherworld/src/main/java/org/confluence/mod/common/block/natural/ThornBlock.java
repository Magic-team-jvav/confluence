package org.confluence.mod.common.block.natural;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThornBlock extends PipeBlock {
    public static final MapCodec<ThornBlock> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(Codec.FLOAT.fieldOf("damage_amount").forGetter(thornBlock -> thornBlock.damageAmount),
                Block.CODEC.fieldOf("ground_block").forGetter(thornBlock -> thornBlock.ground))
            .apply(builder, ThornBlock::new)
    );

    private final float damageAmount;
    private static final IntegerProperty PROP_AGE = IntegerProperty.create("age", 0, 7);
    private final Block ground;

    public ThornBlock(float damageAmount, Block ground) {
        super(0.3125f, Properties.of().replaceable().noCollission().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY).randomTicks());
        this.damageAmount = damageAmount;
        this.ground = ground;
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    @NotNull
    protected MapCodec<? extends PipeBlock> codec(){
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return getStateForPlacement(pContext.getLevel(), pContext.getClickedPos());
    }

    public BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos){
        BlockState blockState = defaultBlockState();
        for(Direction direction : Direction.values()){
            BlockState nearState = pLevel.getBlockState(pPos.relative(direction));
            blockState = blockState.setValue(PROPERTY_BY_DIRECTION.get(direction), nearState.is(this) || nearState.is(ground));
        }
        return blockState;
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pLevel.isClientSide()) return;
        if (pEntity instanceof ServerPlayer player) {
            player.hurt(ModDamageTypes.of(pLevel, DamageTypes.THORNS), damageAmount);
            pLevel.destroyBlock(pPos, false);
        }
        if (pEntity instanceof Projectile) {
            pLevel.destroyBlock(pPos, false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, PROP_AGE);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return !pState.is(NatureBlocks.PLANTERA_THORN.get()) && pState.getValue(PROP_AGE) < 7;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if(this == NatureBlocks.PLANTERA_THORN.get() || pRandom.nextInt(50) != 0) return;
        Direction dir = Direction.getRandom(pRandom);
        BlockPos targetPos = pPos.relative(dir);
        if (!pLevel.getBlockState(targetPos).isAir() || checkBorder(pLevel, targetPos, dir)) return;
        int age = pState.getValue(PROP_AGE);
        pLevel.setBlock(targetPos, getStateForPlacement(pLevel, targetPos).setValue(PROP_AGE, Math.min(7, age + pRandom.nextInt(1, 3))), Block.UPDATE_CLIENTS);
        pLevel.setBlock(pPos, pState.setValue(PROPERTY_BY_DIRECTION.get(dir), true).setValue(PROP_AGE, pRandom.nextBoolean() ? 7 : age + 1), Block.UPDATE_CLIENTS);
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
    @NotNull
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pDirection), pNeighborState.is(this) || pNeighborState.is(ground));
    }
}
