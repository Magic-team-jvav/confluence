package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.mixed.ILivingEntity;
import org.jetbrains.annotations.Nullable;

public class ThinIceBlock extends IceBlock {
    public ThinIceBlock() {
        super(Properties.copy(Blocks.PACKED_ICE));
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        if (!(entity instanceof LivingEntity living && ILivingEntity.of(living).confluence$isBreakEasyCrashBlock())) {
            super.updateEntityAfterFallOn(blockGetter, entity);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity entity, ItemStack item) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
    }

    public static void fall(LivingEntity living, BlockPos blockPos) {
        Level level = living.level();
        if (!level.isClientSide) {
            BlockPos.betweenClosedStream(living.getBoundingBox().move(0.0, -0.5, 0.0)).forEach(pos -> {
                if (pos.equals(blockPos) || level.getBlockState(pos).is(NatureBlocks.THIN_ICE_BLOCK.get())) {
                    level.destroyBlock(pos, true, living);
                }
            });
        }
        ILivingEntity.of(living).confluence$setBreakEasyCrashBlock(true);
        living.setOnGround(false);
    }
}
