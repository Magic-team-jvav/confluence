package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
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
import org.mesdag.portlib.wrapper.world.item.crafting.PortRecipeInput;

public class HardmodeForgeBlock extends EnhancedForgeBlock {
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

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends EnhancedForgeRecipe> boolean isForgeMatched(Level level, EnhancedForgeBlock.BEntity<T> entity, ItemStack[] itemStacks, boolean[] data) {
        T recipeholder = null;
        if (entity instanceof BEntity bEntity) {
            recipeholder = (T) bEntity.hellforge.getRecipeFor(new ArrayRecipeInput(itemStacks), level).orElse(null);
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

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double d0 = pos.getX() + 0.5;
        double d1 = pos.getY();
        double d2 = pos.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            level.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    }

    public static class BEntity extends EnhancedForgeBlock.BEntity<HardmodeForgeRecipe> {
        protected final RecipeManager.CachedCheck<PortRecipeInput, HellforgeRecipe> hellforge;

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
                    .map(EnhancedForgeRecipe::getCookingTime).orElse(50);
            return items.get(FUEL_SLOT).isEmpty() ? time * 4 : time;
        }
    }
}
