package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalTwoPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.block.PylonBlocks;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BasePylonBlock extends HorizontalDirectionalWithVerticalTwoPartBlock implements EntityBlock {
    private static final VoxelShape SHAPE = box(1, 0, 1, 15, 16, 15);

    public final int id;
    private final Survive survive;

    public BasePylonBlock(int id, Properties properties, Survive survive) {
        super(properties);
        this.id = id;
        this.survive = survive;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return super.canSurvive(state, world, pos) && survive.canSurvive(world, pos, world.getBiome(pos));
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    public static class BEntity extends BlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        public final boolean isBase;

        public BEntity(BlockPos blockPos, BlockState blockState) {
            super(PylonBlocks.PYLON_ENTITY.get(), blockPos, blockState);
            this.isBase = blockState.getValue(PART) == StateProperties.VerticalTwoPart.BASE;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            RawAnimation anim = RawAnimation.begin().thenLoop("animation.model.new");
            controllers.add(new AnimationController<>(this, state -> state.setAndContinue(anim)));
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
