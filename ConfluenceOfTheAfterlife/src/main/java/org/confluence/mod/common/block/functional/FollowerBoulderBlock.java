package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.projectile.BoulderEntity;
import org.confluence.mod.common.entity.projectile.FollowerBoulderEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class FollowerBoulderBlock extends BoulderBlock {
    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        summon(pLevel, pPos, pState, entity -> pPlayer);
    }

    @Override
    public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
        summon(pLevel, pPos, defaultBlockState(), entity -> pLevel.getNearestPlayer(entity, BoulderEntity.SEARCH_RANGE));
    }

    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        pLevel.removeBlock(pPos, false);
        summon(pLevel, pPos, pState, entity -> pLevel.getNearestPlayer(entity, BoulderEntity.SEARCH_RANGE));
    }

    public static void summon(Level level, BlockPos pos, BlockState blockState, Function<BoulderEntity, Player> function) {
        Vec3 position = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        FollowerBoulderEntity entity = new FollowerBoulderEntity(level, position, Blocks.OBSERVER.defaultBlockState());
        if (level.getBlockState(pos.below()).isAir()) {
            entity.getEntityData().set(FollowerBoulderEntity.DATA_VERTICAL, true);
        } else {
            entity.targetTo(function.apply(entity));
            entity.getEntityData().set(FollowerBoulderEntity.DATA_VERTICAL, false);
        }
        level.addFreshEntity(entity);
    }
}
