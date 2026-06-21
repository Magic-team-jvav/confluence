package org.confluence.mod.common.block.common;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.mesdag.portlib.wrapper.world.PortItemInteractionResult;

import java.util.Map;

public class AetheriumCauldronBlock extends AbstractCauldronBlock {
    public static final Map<Item, CauldronInteraction> DO_NOTHING = Util.make(CauldronInteraction.newInteractionMap(), map -> {});
    public static final CauldronInteraction FILL_AETHERIUM = (blockState, level, blockPos, player, hand, itemStack) -> {
        if (!level.isClientSide) {
            boolean bottomless = itemStack.is(ModTags.Items.BOTTOMLESS);
            ItemStack filledStack = bottomless ? itemStack : NatureBlocks.AETHERIUM_BLOCK.toStack();
            if (!bottomless && !player.hasInfiniteMaterials()) {
                itemStack.shrink(1);
            }
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(filledStack.getItem()));
            level.setBlockAndUpdate(blockPos, ModBlocks.AETHERIUM_CAULDRON.get().defaultBlockState());
            level.playSound(null, blockPos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
        }
        return PortItemInteractionResult.sidedSuccess(level.isClientSide).result();
    };

    public AetheriumCauldronBlock(Properties properties) {
        super(properties, DO_NOTHING);
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return 0.9375;
    }

    @Override
    public boolean isFull(BlockState state) {
        return true;
    }

    @Override
    public boolean isEntityInsideContent(BlockState state, BlockPos pos, Entity entity) {
        return super.isEntityInsideContent(state, pos, entity);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return 3;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            ItemStack filledStack = NatureBlocks.AETHERIUM_BLOCK.toStack();
            if (player.hasInfiniteMaterials()) {
                if (!player.getInventory().contains(filledStack)) {
                    player.getInventory().add(filledStack);
                }
            } else {
                if (!player.getInventory().add(filledStack)) {
                    player.drop(filledStack, false);
                }
            }
            player.awardStat(Stats.USE_CAULDRON);
            level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
