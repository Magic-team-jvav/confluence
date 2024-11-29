package org.confluence.mod.common.block.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ModAnvilBlock extends AnvilBlock {
    public static final MapCodec<ModAnvilBlock> CODEC = simpleCodec(ModAnvilBlock::new);
    private static final Component CONTAINER_TITLE;

    public ModAnvilBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL));
    }

    public ModAnvilBlock(Properties properties) {
        super(properties);
    }

    public static BlockState damage(BlockState state) {
        if (state.getBlock() instanceof AnvilBlock) {
            return Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(FACING, state.getValue(FACING));
        } else {
            return state.is(Blocks.CHIPPED_ANVIL) ? Blocks.DAMAGED_ANVIL.defaultBlockState().setValue(FACING, (Direction)state.getValue(FACING)) : null;
        }
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((p_48785_, p_48786_, p_48787_) ->
                new AnvilMenu(p_48785_, p_48786_, ContainerLevelAccess.create(level, pos)),
                CONTAINER_TITLE);
    }

    static {
        CONTAINER_TITLE = Component.translatable("container.repair");
    }
}
