package org.confluence.mod.integration.waystones;

import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PylonBlock extends WaystoneBlock {
    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 16, 15);
    public final int id;
    private final Survive survive;

    public PylonBlock(int id, Properties properties, Survive survive) {
        super(properties);
        this.id = id;
        this.survive = survive;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return super.canSurvive(state, world, pos) && survive.canSurvive(world, pos, world.getBiome(pos));
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    public static class BEntity extends WaystoneBlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        private final ResourceLocation waystoneType;
        public final boolean isBase;

        public BEntity(BlockPos blockPos, BlockState blockState) {
            super(blockPos, blockState);
            this.waystoneType = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
            this.isBase = blockState.getValue(WaystoneBlock.HALF) == DoubleBlockHalf.LOWER;
        }

        @Override
        protected ResourceLocation getWaystoneType() {
            return waystoneType;
        }

        @Override
        public Component getName() {
            return Component.translatable("container.confluence.pylon");
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, state -> state.setAndContinue(RawAnimation.begin().thenLoop("animation.model.new"))));
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }

    @FunctionalInterface
    public interface Survive {
        boolean canSurvive(LevelReader world, BlockPos pos, Holder<Biome> biome);
    }
}
