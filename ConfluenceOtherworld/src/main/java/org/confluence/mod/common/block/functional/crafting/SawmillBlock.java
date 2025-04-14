package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTwoPartBlock;
import org.confluence.mod.common.menu.SawmillMenu;
import org.jetbrains.annotations.Nullable;

public class SawmillBlock extends HorizontalDirectionalWithHorizontalTwoPartBlock {
    public static final MapCodec<SawmillBlock> CODEC = simpleCodec(SawmillBlock::new);

    public SawmillBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<SawmillBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) -> new SawmillMenu(pContainerId, pPlayerInventory), Component.translatable("container.confluence.sawmill"));
    }
}
