package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.IPortBlock;

public class CrackedBricksBlock extends Block implements IPortBlock {
    public CrackedBricksBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        scheduleTick(level, pos);
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        scheduleTick(level, hit.getBlockPos());
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        scheduleTick(level, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.destroyBlock(pos, false);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.getDeltaMovement().y > 0 && level instanceof Level level1) {
            scheduleTick(level1, entity.getOnPosLegacy());
        }
        super.updateEntityAfterFallOn(level, entity);
    }

    @Override
    public void onDestroyedByPushReaction(BlockState state, Level level, BlockPos pos, Direction pushDirection, FluidState fluid) {
        scheduleTick(level, pos);
        IPortBlock.super.onDestroyedByPushReaction(state, level, pos, pushDirection, fluid);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    private void scheduleTick(Level level, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-7, -7, -7), pos.offset(7, 7, 7))) {
            level.scheduleTick(blockPos, this, 1);
        }
    }

    @Override
    public void portlib$setRenderPropertiesInternal(Object properties) {
    }
}
