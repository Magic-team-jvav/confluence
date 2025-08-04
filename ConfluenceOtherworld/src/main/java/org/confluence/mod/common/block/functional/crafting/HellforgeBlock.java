package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.HellforgeMenu;
import org.confluence.mod.common.recipe.HellforgeRecipe;

public class HellforgeBlock extends EnhancedForgeBlock {
    public static final MapCodec<HellforgeBlock> CODEC = simpleCodec(HellforgeBlock::new);

    public HellforgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<HellforgeBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    protected BlockEntityType<BEntity> getBlockEntityType() {
        return FunctionalBlocks.HELLFORGE_ENTITY.get();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 0.375;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0, 0.0, 0.0);
            level.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
        }
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
