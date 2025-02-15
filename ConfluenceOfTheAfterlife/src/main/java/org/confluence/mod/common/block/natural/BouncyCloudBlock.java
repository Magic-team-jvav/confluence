package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BouncyCloudBlock extends Block {
    private double jumpBoost = 0.0;
    private static final double JUMP_INCREMENT = 0.02;

    public BouncyCloudBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).sound(SoundType.SNOW));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity && !entity.isSteppingCarefully()) {
            jumpBoost += JUMP_INCREMENT;
            if (jumpBoost > 10) jumpBoost = 10;
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, jumpBoost, 0));
        } else {
            jumpBoost = 0.0;
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }
}
