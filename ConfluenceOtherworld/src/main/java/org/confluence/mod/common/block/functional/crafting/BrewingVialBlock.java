package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.menu.BrewingVialMenu;
import org.jetbrains.annotations.Nullable;

/// 炼药瓶：放置后右键打开炼药桌的界面，炼药配方与炼药桌完全一致，
/// 区别只是没有「概率不消耗材料」的效果，只是纯粹的合成。
public class BrewingVialBlock extends Block {
    /// 与模型一致（x/z 5~11，y 0~10）
    private static final VoxelShape SHAPE = box(5, 0, 5, 11, 10, 11);

    public BrewingVialBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        player.openMenu(state.getMenuProvider(level, pos));
        return InteractionResult.CONSUME;
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new BrewingVialMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("container.confluence.brewing_vial"));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
