package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.confluence.mod.common.init.block.ModBlocks;

import javax.annotation.Nullable;

public class JungleHiveBlock extends Block {
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

    public JungleHiveBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.BEEHIVE));
        registerDefaultState(stateDefinition.any().setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NATURAL);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (level.isClientSide || player.hasInfiniteMaterials() || !state.getValue(NATURAL)) return;
        int randomNumber = level.random.nextInt(3);
        if (randomNumber == 0) {
            level.setBlockAndUpdate(pos, ModBlocks.HONEY.get().defaultBlockState());
        } else if (randomNumber == 1) {
            LittleHornet BeeEntity = TEMonsterEntities.LITTLE_HORNET.get().create(level);
            if (BeeEntity != null) {
                BeeEntity.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                level.addFreshEntity(BeeEntity);
                BeeEntity.setTarget(player);
            }
        } else if (randomNumber == 2) {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        }
    }
}
