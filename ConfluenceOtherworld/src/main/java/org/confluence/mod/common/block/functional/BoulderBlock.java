package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BoulderBlock extends AbstractMechanicalBlock {
    private final BoulderFactory factory;

    public BoulderBlock() {
        this(BoulderEntity::new);
    }

    public BoulderBlock(BoulderFactory factory) {
        this(Properties.of(), factory);
    }

    public BoulderBlock(Properties properties, BoulderFactory factory) {
        super(properties);
        this.factory = factory;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        level.destroyBlock(hit.getBlockPos(), false);
        summon(level, hit.getBlockPos(), state, entity -> {
            if (projectile.getOwner() instanceof Player player) {
                return player;
            }
            return level.getNearestPlayer(entity, BoulderEntity.SEARCH_RANGE);
        });
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        summon(level, pos, state, entity -> player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        summon(level, pos, state, entity -> level.getNearestPlayer(entity, BoulderEntity.SEARCH_RANGE));
    }

    @Override
    public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
        summon(pLevel, pPos, defaultBlockState(), entity -> {
            if (pExplosion.getIndirectSourceEntity() instanceof Player player) {
                return player;
            }
            return pLevel.getNearestPlayer(entity, BoulderEntity.SEARCH_RANGE);
        });
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            if (pLevel.hasNeighborSignal(pPos)) {
                execute(pState, (ServerLevel) pLevel, pPos, true);
            } else if (pLevel.getBlockEntity(pPos) instanceof INetworkEntity entity) {
                BlockState below = pLevel.getBlockState(pPos.below());
                if (below.isAir()) onExecute(pState, (ServerLevel) pLevel, pPos, -1, entity);
            }
        }
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        level.removeBlock(pos, false);
        summon(level, pos, state, entity -> level.getNearestPlayer(entity, BoulderEntity.SEARCH_RANGE));
    }

    protected void summon(Level level, BlockPos pos, BlockState blockState, Function<BoulderEntity, Player> function) {
        BoulderEntity entity = factory.create(level, pos.getCenter(), blockState);
        if (level.getBlockState(pos.below()).isAir()) {
            entity.setVertical(true);
        } else {
            entity.targetTo(function.apply(entity));
            entity.setVertical(false);
        }
        level.addFreshEntity(entity);
    }

    @FunctionalInterface
    public interface BoulderFactory {
        BoulderEntity create(Level level, Vec3 position, BlockState blockState);
    }
}
