package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.HardmodeForgeMenu;
import org.confluence.mod.common.recipe.HardmodeForgeRecipe;
import org.jetbrains.annotations.Nullable;

public class HardmodeForgeBlock extends EnhancedForgeBlock {
    public static final MapCodec<HardmodeForgeBlock> CODEC = simpleCodec(HardmodeForgeBlock::new);

    public HardmodeForgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    protected BlockEntityType<BEntity> getBlockEntityType() {
        return FunctionalBlocks.HARDMODE_FORGE_ENTITY.get();
    }

    @Override
    protected MapCodec<HardmodeForgeBlock> codec() {
        return CODEC;
    }

    public static class BEntity extends EnhancedForgeBlock.BEntity<HardmodeForgeRecipe> {
        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.HARDMODE_FORGE_ENTITY.get(), pos, blockState);
        }

        @Override
        protected RecipeType<HardmodeForgeRecipe> getRecipeType() {
            return ModRecipes.HARDMODE_FORGE_TYPE.get();
        }

        @Override
        protected AbstractContainerMenu newMenu(int containerId, Inventory inventory, Container forgeContainer, ContainerData forgeData) {
            return new HardmodeForgeMenu(containerId, inventory, forgeContainer, forgeData);
        }

        @Override
        protected Component getDefaultName() {
            return Component.translatable("container.confluence." + BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()).getPath());
        }
    }
}
