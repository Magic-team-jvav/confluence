package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.NatureBlocks;

public class BalloonMelonBlock extends StemGrownBlock {
    public BalloonMelonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public StemBlock getStem() {
        return NatureBlocks.BALLOON_STEM.get();
    }

    @Override
    public AttachedStemBlock getAttachedStem() {
        return NatureBlocks.ATTACHED_BALLOON_STEM.get();
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
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        this.spawnDestroyParticles(level, player, pos, state);
        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));

        if (!level.isClientSide) {
            int silkLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem());
            if (silkLevel <= 0 && !player.getMainHandItem().isEmpty()) {
                this.spawnRecoveryCloud(level, pos);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    private void spawnRecoveryCloud(Level level, BlockPos pos) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        cloud.setRadius(3.0F);
        cloud.setDuration(100);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-3.0F / 600.0F);

        cloud.addEffect(new MobEffectInstance(ModEffects.AROMATIC_SATIATION.get(), 400, 0));
        level.addFreshEntity(cloud);
    }
}
