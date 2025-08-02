package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.HellforgeMenu;
import org.confluence.mod.common.recipe.HellforgeRecipe;

public class HellforgeBlock extends EnhancedForgeBlock {
    public static final MapCodec<HellforgeBlock> CODEC = simpleCodec(HellforgeBlock::new);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{
            box(3, 0, 3, 16, 16, 13),
            box(3, 0, 3, 13, 16, 16),
            box(0, 0, 3, 13, 16, 13),
            box(3, 0, 0, 13, 16, 13)
    };
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{
            box(0, 0, 3, 13, 16, 13),
            box(3, 0, 0, 13, 16, 13),
            box(3, 0, 3, 16, 16, 13),
            box(3, 0, 3, 13, 16, 16)
    };

    public HellforgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<HellforgeBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return state.getValue(StateProperties.HORIZONTAL_TWO_PART).isBase() ? BASE_SHAPES[index] : RIGHT_SHAPES[index];
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    protected BlockEntityType<BEntity> getBlockEntityType() {
        return FunctionalBlocks.HELLFORGE_ENTITY.get();
    }

    public static class BEntity extends EnhancedForgeBlock.BEntity<HellforgeRecipe> {
        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.HELLFORGE_ENTITY.get(), pos, blockState);
        }

        @Override
        protected RecipeType<HellforgeRecipe> getRecipeType() {
            return ModRecipes.HELLFORGE_TYPE.get();
        }

        @Override
        protected AbstractContainerMenu newMenu(int containerId, Inventory inventory, Container forgeContainer, ContainerData forgeData) {
            return new HellforgeMenu(containerId, inventory, forgeContainer, forgeData);
        }

        @Override
        protected Component getDefaultName() {
            return Component.translatable("container.confluence.hellforge");
        }
    }
}
