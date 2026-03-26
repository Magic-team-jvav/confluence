package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.List;
import java.util.Optional;

public class BalloonMelonBlock extends Block {
    public BalloonMelonBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_GREEN)
                .strength(1.0F)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.getBlock() instanceof BalloonAttachedStemBlock) {
                level.setBlockAndUpdate(belowPos, NatureBlocks.BALLOON_STEM.get().defaultBlockState().setValue(StemBlock.AGE, 7));
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        this.spawnDestroyParticles(level, player, pos, state);
        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
        if (!level.isClientSide) {
            this.spawnRecoveryCloud(level, pos);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private void spawnRecoveryCloud(Level level, BlockPos pos) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        cloud.setRadius(3.0F);
        cloud.setDuration(100);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-3.0F / 600.0F);

        cloud.setPotionContents(new PotionContents(
                Optional.empty(),
                Optional.of(0x33FFCC),
                List.of()
        ));

        cloud.addEffect(new MobEffectInstance(ModEffects.AROMATIC_SATIATION, 400, 0));
        level.addFreshEntity(cloud);
    }
}
