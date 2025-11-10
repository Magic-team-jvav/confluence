package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.confluence.mod.common.init.ModSecretSeeds;

import javax.annotation.Nullable;

public class CrispyHoneyBlock extends Block {
    public CrispyHoneyBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.2F));
    }

    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (level instanceof ServerLevel serverLevel
                && (ModSecretSeeds.GET_FIXED_BOI.match(serverLevel) || ModSecretSeeds.FOR_THE_WORTHY.match(serverLevel))
                && level.random.nextBoolean()) {
            level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level instanceof ServerLevel serverLevel && (ModSecretSeeds.GET_FIXED_BOI.match(serverLevel) || ModSecretSeeds.FOR_THE_WORTHY.match(serverLevel))) {
            entity.igniteForSeconds(5.0F);
        }
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }
}
