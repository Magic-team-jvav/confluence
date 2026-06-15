package org.confluence.mod.common.block.natural;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;

public class MagicMailBox extends Block {
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{
            Shapes.or(box(8, 5, 0, 13, 11, 15), box(8, 0, 0, 13, 5, 15), box(3, 0, 0, 8, 5, 15), box(3, 5, 0, 8, 11, 15), box(8, 5, 15, 13, 11, 16), box(3, 5, 15, 8, 11, 16), box(8, 0, 15, 13, 5, 16), box(3, 0, 15, 8, 5, 16), box(13, 4, 8, 15, 14, 10), box(13, 4, 6, 15, 14, 8), box(11, 4, 6, 13, 14, 8), box(11, 4, 8, 13, 14, 10), box(2, 1, 4, 3, 5, 8), box(2, 1, 8, 3, 5, 12), box(2, 5, 4, 3, 10, 8), box(2, 5, 8, 3, 10, 12)),
            Shapes.or(box(3, 5, 1, 8, 11, 16), box(3, 0, 1, 8, 5, 16), box(8, 0, 1, 13, 5, 16), box(8, 5, 1, 13, 11, 16), box(3, 5, 0, 8, 11, 1), box(8, 5, 0, 13, 11, 1), box(3, 0, 0, 8, 5, 1), box(8, 0, 0, 13, 5, 1), box(1, 4, 6, 3, 14, 8), box(1, 4, 8, 3, 14, 10), box(3, 4, 8, 5, 14, 10), box(3, 4, 6, 5, 14, 8), box(13, 1, 8, 14, 5, 12), box(13, 1, 4, 14, 5, 8), box(13, 5, 8, 14, 10, 12), box(13, 5, 4, 14, 10, 8)),
            Shapes.or(box(0, 5, 3, 15, 11, 8), box(0, 0, 3, 15, 5, 8), box(0, 0, 8, 15, 5, 13), box(0, 5, 8, 15, 11, 13), box(15, 5, 3, 16, 11, 8), box(15, 5, 8, 16, 11, 13), box(15, 0, 3, 16, 5, 8), box(15, 0, 8, 16, 5, 13), box(8, 4, 1, 10, 14, 3), box(6, 4, 1, 8, 14, 3), box(6, 4, 3, 8, 14, 5), box(8, 4, 3, 10, 14, 5), box(4, 1, 13, 8, 5, 14), box(8, 1, 13, 12, 5, 14), box(4, 5, 13, 8, 10, 14), box(8, 5, 13, 12, 10, 14)),
            Shapes.or(box(1, 5, 8, 16, 11, 13), box(1, 0, 8, 16, 5, 13), box(1, 0, 3, 16, 5, 8), box(1, 5, 3, 16, 11, 8), box(0, 5, 8, 1, 11, 13), box(0, 5, 3, 1, 11, 8), box(0, 0, 8, 1, 5, 13), box(0, 0, 3, 1, 5, 8), box(6, 4, 13, 8, 14, 15), box(8, 4, 13, 10, 14, 15), box(8, 4, 11, 10, 14, 13), box(6, 4, 11, 8, 14, 13), box(8, 1, 2, 12, 5, 3), box(4, 1, 2, 8, 5, 3), box(8, 5, 2, 12, 10, 3), box(4, 5, 2, 8, 10, 3)),
            Shapes.or(box(8, 5, 0, 13, 10, 15), box(8, 0, 0, 13, 5, 15), box(3, 0, 0, 8, 5, 15), box(3, 5, 0, 8, 10, 15), box(8, 5, 15, 13, 10, 16), box(3, 5, 15, 8, 10, 16), box(8, 0, 15, 13, 5, 16), box(3, 0, 15, 8, 5, 16), box(13, 4, 8, 15, 13, 10), box(13, 4, 6, 15, 13, 8), box(11, 4, 6, 13, 13, 8), box(11, 4, 8, 13, 13, 10), box(2, 1, 4, 3, 5, 8), box(2, 1, 8, 3, 5, 12), box(2, 5, 4, 3, 9, 8), box(2, 5, 8, 3, 9, 12)),
            Shapes.or(box(3, 5, 1, 8, 10, 16), box(3, 0, 1, 8, 5, 16), box(8, 0, 1, 13, 5, 16), box(8, 5, 1, 13, 10, 16), box(3, 5, 0, 8, 10, 1), box(8, 5, 0, 13, 10, 1), box(3, 0, 0, 8, 5, 1), box(8, 0, 0, 13, 5, 1), box(1, 4, 6, 3, 13, 8), box(1, 4, 8, 3, 13, 10), box(3, 4, 8, 5, 13, 10), box(3, 4, 6, 5, 13, 8), box(13, 1, 8, 14, 5, 12), box(13, 1, 4, 14, 5, 8), box(13, 5, 8, 14, 9, 12), box(13, 5, 4, 14, 9, 8)),
            Shapes.or(box(0, 5, 3, 15, 10, 8), box(0, 0, 3, 15, 5, 8), box(0, 0, 8, 15, 5, 13), box(0, 5, 8, 15, 10, 13), box(15, 5, 3, 16, 10, 8), box(15, 5, 8, 16, 10, 13), box(15, 0, 3, 16, 5, 8), box(15, 0, 8, 16, 5, 13), box(8, 4, 1, 10, 13, 3), box(6, 4, 1, 8, 13, 3), box(6, 4, 3, 8, 13, 5), box(8, 4, 3, 10, 13, 5), box(4, 1, 13, 8, 5, 14), box(8, 1, 13, 12, 5, 14), box(4, 5, 13, 8, 9, 14), box(8, 5, 13, 12, 9, 14)),
            Shapes.or(box(1, 5, 8, 16, 10, 13), box(1, 0, 8, 16, 5, 13), box(1, 0, 3, 16, 5, 8), box(1, 5, 3, 16, 10, 8), box(0, 5, 8, 1, 10, 13), box(0, 5, 3, 1, 10, 8), box(0, 0, 8, 1, 5, 13), box(0, 0, 3, 1, 5, 8), box(6, 4, 13, 8, 13, 15), box(8, 4, 13, 10, 13, 15), box(8, 4, 11, 10, 13, 13), box(6, 4, 11, 8, 13, 13), box(8, 1, 2, 12, 5, 3), box(4, 1, 2, 8, 5, 3), box(8, 5, 2, 12, 9, 3), box(4, 5, 2, 8, 9, 3))
    };
    protected static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;
    protected static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 19);
    protected static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected static final Object2IntMap<Block> BLOCK_TO_VARIANT = new Object2IntOpenHashMap<>();

    public MagicMailBox() {
        super(BlockBehaviour.Properties.of().strength(1.0f));
        registerDefaultState(this.stateDefinition.any()
                .setValue(SNOWY, false)
                .setValue(VARIANT, 0)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) return;
        ItemStack heldItem = player.getMainHandItem();
        Item item = heldItem.getItem();
        if (state.getValue(SNOWY) && item instanceof ShovelItem) {
            level.setBlock(pos, state.setValue(SNOWY, false), 3);
            level.playSound(null, pos, SoundEvents.SNOW_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            PortItemStackExtension.hurtAndBreak(heldItem, 1, player,  EquipmentSlot.MAINHAND);
            return;
        }
        if (!state.getValue(SNOWY) && (item == Items.SNOWBALL || item == Items.SNOW || item == Items.SNOW_BLOCK)) {
            level.setBlock(pos, state.setValue(SNOWY, true), 3);
            level.playSound(null, pos, SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return;
        }
        if (item instanceof BlockItem) {
            Block heldBlock = ((BlockItem) item).getBlock();
            int variant = BLOCK_TO_VARIANT.getInt(heldBlock);
            if (variant != -1 && state.getValue(VARIANT) != variant) {
                level.setBlockAndUpdate(pos, state.setValue(VARIANT, variant));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWY, VARIANT, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(SNOWY)) {
            return switch (state.getValue(FACING)) {
                case NORTH -> SHAPES[1];
                case EAST -> SHAPES[2];
                case WEST -> SHAPES[3];
                default -> SHAPES[0];
            };
        } else {
            return switch (state.getValue(FACING)) {
                case NORTH -> SHAPES[5];
                case EAST -> SHAPES[6];
                case WEST -> SHAPES[7];
                default -> SHAPES[4];
            };
        }
    }

    public static void registerVariants() {
        BLOCK_TO_VARIANT.defaultReturnValue(-1);
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
        BLOCK_TO_VARIANT.put(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS.get(), 10);
        BLOCK_TO_VARIANT.put(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get(), 11);
        BLOCK_TO_VARIANT.put(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS.get(), 12);
        BLOCK_TO_VARIANT.put(NatureBlocks.PALM_LOG_BLOCKS.PLANKS.get(), 13);
        BLOCK_TO_VARIANT.put(NatureBlocks.BAOBAB_LOG_BLOCKS.PLANKS.get(), 14);
        BLOCK_TO_VARIANT.put(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.PLANKS.get(), 15);
        BLOCK_TO_VARIANT.put(NatureBlocks.SPOOKY_LOG_BLOCKS.PLANKS.get(), 16);
        BLOCK_TO_VARIANT.put(NatureBlocks.LIVING_LOG_BLOCKS.PLANKS.get(), 17);
        BLOCK_TO_VARIANT.put(NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.PLANKS.get(), 18);
        BLOCK_TO_VARIANT.put(NatureBlocks.ASH_LOG_BLOCKS.PLANKS.get(), 19);
    }
}
