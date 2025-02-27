package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.terra_curio.common.entity.projectile.BeeProjectile;

import javax.annotation.Nullable;

public class JungleHiveBlock extends Block {

    public JungleHiveBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEEHIVE));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if(level.isClientSide || player.getAbilities().instabuild) return;
        int randomNumber = level.random.nextInt(3);
        if (randomNumber == 0) {
            level.setBlockAndUpdate(pos, ModBlocks.HONEY.get().defaultBlockState());
        } else if (randomNumber == 1) {
            Entity Beeentity = new BeeProjectile(level, null, false);
            Beeentity.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
            level.addFreshEntity(Beeentity);
        } else if (randomNumber == 2) {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        } else {
            level.setBlockAndUpdate(pos, ModBlocks.HONEY.get().defaultBlockState());
        }
    }
}
