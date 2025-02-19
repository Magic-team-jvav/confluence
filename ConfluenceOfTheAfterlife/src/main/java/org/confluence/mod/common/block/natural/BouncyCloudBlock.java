package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;

public class BouncyCloudBlock extends Block {
    public static final IntegerProperty HIGH = IntegerProperty.create("high", 0, 10);

    public BouncyCloudBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).sound(SoundType.SNOW));
        this.registerDefaultState(defaultBlockState().setValue(HIGH, 0));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            int currentHigh = state.getValue(HIGH);
            boolean isSteppingCarefully = livingEntity.isSteppingCarefully();
            if (isSteppingCarefully) {
                level.setBlockAndUpdate(pos, state.setValue(HIGH, 0));
                livingEntity.setDeltaMovement(0, 0, 0);
            } else {
                int newHigh = Math.min(currentHigh + 1, 10);
                level.setBlockAndUpdate(pos, state.setValue(HIGH, newHigh));
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, currentHigh, 0));
            }
            level.scheduleTick(pos, this, 20);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        AABB detectionArea = new AABB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 201, pos.getZ() + 1);
        boolean hasPlayer = level.getEntitiesOfClass(LivingEntity.class, detectionArea)
                .stream()
                .anyMatch(LivingEntity::isAlive);

        if (!hasPlayer) {
            level.setBlockAndUpdate(pos, state.setValue(HIGH, 0));
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HIGH);
    }
}
