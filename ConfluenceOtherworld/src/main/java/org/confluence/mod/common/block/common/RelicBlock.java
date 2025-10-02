package org.confluence.mod.common.block.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.terra_furniture.client.model.CacheItemRefBlockModel;
import org.confluence.terra_furniture.client.renderer.item.SimpleGeoItemRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class RelicBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<RelicBlock> CODEC = simpleCodec(RelicBlock::new);
    private static final VoxelShape SHAPE = Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);
    public RelicBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BEntity(blockPos, blockState);
    }

    public static class BEntity extends BlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public BEntity(BlockPos pPos, BlockState pBlockState) {
            super(DecorativeBlocks.RELIC_ENTITY.get(), pPos, pBlockState);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, state ->
                state.setAndContinue(RawAnimation.begin().thenLoop("running")))
            );
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }

    public static class BItem extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public BItem(RelicBlock pBlock) {
            super(pBlock, new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN));
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new SimpleGeoItemRendererProvider<>(new CacheItemRefBlockModel<>(Confluence::asResource)));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }
}
