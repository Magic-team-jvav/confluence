package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.HashMap;
import java.util.Map;

public class MagicMailBox extends Block {
    private static final BooleanProperty SNOW = BooleanProperty.create("snow");
    private static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 19);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final Map<Block, Integer> BLOCK_TO_VARIANT = new HashMap<>();

    static {
        BLOCK_TO_VARIANT.put(Blocks.OAK_PLANKS, 0);
        BLOCK_TO_VARIANT.put(Blocks.SPRUCE_PLANKS, 1);
        BLOCK_TO_VARIANT.put(Blocks.BIRCH_PLANKS, 2);
        BLOCK_TO_VARIANT.put(Blocks.JUNGLE_PLANKS, 3);
        BLOCK_TO_VARIANT.put(Blocks.ACACIA_PLANKS, 4);
        BLOCK_TO_VARIANT.put(Blocks.DARK_OAK_PLANKS, 5);
        BLOCK_TO_VARIANT.put(Blocks.MANGROVE_PLANKS, 6);
        BLOCK_TO_VARIANT.put(Blocks.CHERRY_PLANKS, 7);
        BLOCK_TO_VARIANT.put(Blocks.CRIMSON_PLANKS, 8);
        BLOCK_TO_VARIANT.put(Blocks.WARPED_PLANKS, 9);
        BLOCK_TO_VARIANT.put(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks().get(), 10);
        BLOCK_TO_VARIANT.put(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks().get(), 11);
        BLOCK_TO_VARIANT.put(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks().get(), 12);
        BLOCK_TO_VARIANT.put(NatureBlocks.PALM_LOG_BLOCKS.getPlanks().get(), 13);
        BLOCK_TO_VARIANT.put(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks().get(), 14);
        BLOCK_TO_VARIANT.put(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks().get(), 15);
        BLOCK_TO_VARIANT.put(NatureBlocks.SPOOKY_LOG_BLOCKS.getPlanks().get(), 16);
        BLOCK_TO_VARIANT.put(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks().get(), 17);
        BLOCK_TO_VARIANT.put(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks().get(), 18);
        BLOCK_TO_VARIANT.put(NatureBlocks.ASH_LOG_BLOCKS.getPlanks().get(), 19);
    }

    public MagicMailBox() {
        super(BlockBehaviour.Properties.of().strength(1.0f));
        registerDefaultState(this.stateDefinition.any()
                .setValue(SNOW, false)
                .setValue(VARIANT, 0)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) return;
        ItemStack heldItem = player.getMainHandItem();
        Item item = heldItem.getItem();
        if (state.getValue(SNOW) && item instanceof ShovelItem) {
            level.setBlock(pos, state.setValue(SNOW, false), 3);
            level.playSound(null, pos, SoundEvents.SNOW_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            heldItem.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            return;
        }
        if (!state.getValue(SNOW) && (item == Items.SNOWBALL || item == Items.SNOW || item == Items.SNOW_BLOCK)) {
            level.setBlock(pos, state.setValue(SNOW, true), 3);
            level.playSound(null, pos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return;
        }
        if (item instanceof BlockItem) {
            Block heldBlock = ((BlockItem) item).getBlock();
            if (BLOCK_TO_VARIANT.containsKey(heldBlock)) {
                int variant = BLOCK_TO_VARIANT.get(heldBlock);
                if (state.getValue(VARIANT) != variant) {
                    level.setBlockAndUpdate(pos, state.setValue(VARIANT, variant));
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOW, VARIANT, FACING);
    }
}
