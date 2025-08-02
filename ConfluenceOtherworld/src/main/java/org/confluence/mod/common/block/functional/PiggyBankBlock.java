package org.confluence.mod.common.block.functional;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.PlayerContainer;
import org.confluence.lib.common.block.HorizontalDirectionalWaterloggedBlock;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class PiggyBankBlock extends HorizontalDirectionalWaterloggedBlock implements EntityBlock {
    public static final MapCodec<PiggyBankBlock> CODEC = simpleCodec(PiggyBankBlock::new);
    private static final VoxelShape SHAPE_X = box(2, 0, 4, 14, 10, 12);
    private static final VoxelShape SHAPE_Z = box(4, 0, 2, 12, 10, 14);

    public PiggyBankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<PiggyBankBlock> codec() {
        return CODEC;
    }

    @Override
    protected SoundType getSoundType(BlockState p_277561_) {
        return SoundType.DECORATED_POT;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof BEntity entity) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            PlayerPiggyBankContainer container = PlayerPiggyBankContainer.of(player);
            container.setActiveContainer(entity);
            player.openMenu(new SimpleMenuProvider((id, inventory, player1) -> new ChestMenu(MenuType.GENERIC_9x6, id, inventory, container, 6), Component.translatable("container.confluence.piggy_bank")));
            PiglinAi.angerNearbyPiglins(player, true);
            level.playSound(null, pos, SoundEvents.PIG_AMBIENT, SoundSource.BLOCKS);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    public static class BEntity extends BlockEntity implements PlayerContainer.ValidEntity {
        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.PIGGY_BANK_ENTITY.get(), pos, blockState);
        }

        @Override
        public BlockEntity self() {
            return this;
        }
    }

    public static class BItem extends TooltipBlockItem {
        public BItem(Block block) {
            super(block, new Item.Properties(), ModRarity.WHITE, TooltipItem.getTooltipsFromString("piggy_bank", 2, ChatFormatting.GRAY));
        }
    }
}
