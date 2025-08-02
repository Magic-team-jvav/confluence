package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.recipe.ArrayRecipeInput;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.HardmodeForgeMenu;
import org.confluence.mod.common.recipe.EnhancedForgeRecipe;
import org.confluence.mod.common.recipe.HardmodeForgeRecipe;
import org.confluence.mod.common.recipe.HellforgeRecipe;
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

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends EnhancedForgeRecipe> boolean isForgeMatched(Level level, EnhancedForgeBlock.BEntity<T> entity, ItemStack[] itemStacks, boolean[] data) {
        RecipeHolder<T> recipeholder = null;
        if (entity instanceof BEntity bEntity) {
            recipeholder = (RecipeHolder<T>) bEntity.hellforge.getRecipeFor(new ArrayRecipeInput(itemStacks), level).orElse(null);
        }
        if (recipeholder == null) {
            recipeholder = entity.forge.getRecipeFor(new ArrayRecipeInput(itemStacks), level).orElse(null);
        }
        if (recipeholder != null) {
            if (!entity.isLit() && entity.canForgeBurn(recipeholder)) {
                data[0] = entity.doUpdateStatus();
            }
            if (entity.canForgeBurn(recipeholder)) {
                if (entity.doUpdateProgress(recipeholder, entity::burnForge)) {
                    data[0] = true;
                }
            }
            return true;
        }
        return false;
    }

    public static class BEntity extends EnhancedForgeBlock.BEntity<HardmodeForgeRecipe> {
        protected final RecipeManager.CachedCheck<RecipeInput, HellforgeRecipe> hellforge;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.HARDMODE_FORGE_ENTITY.get(), pos, blockState);
            this.hellforge = createCachedCheck(ModRecipes.HELLFORGE_TYPE.get());
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

        @Override
        protected int getTotalCookTime() {
            int time = forge.getRecipeFor(new ArrayRecipeInput(getItemStacks()), level)
                    .map(holder -> holder.value().getCookingTime()).orElse(50);
            return items.get(FUEL_SLOT).isEmpty() ? time * 4 : time;
        }
    }
}
