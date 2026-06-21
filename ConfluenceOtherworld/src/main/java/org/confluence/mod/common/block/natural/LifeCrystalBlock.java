package org.confluence.mod.common.block.natural;

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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.client.model.block.LifeCrystalBlockModel;
import org.confluence.mod.client.renderer.item.SimpleGeoItemRenderer;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class LifeCrystalBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);

    public LifeCrystalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BEntity(pPos, pState);
    }

    public static class BEntity extends BlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public BEntity(BlockPos pPos, BlockState pBlockState) {
            super(NatureBlocks.LIFE_CRYSTAL_BLOCK_ENTITY.get(), pPos, pBlockState);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }

    public static class BItem extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public BItem(LifeCrystalBlock pBlock) {
            super(pBlock, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN));
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(new SimpleGeoItemRenderer<>(LifeCrystalBlockModel.MODEL, LifeCrystalBlockModel.TEXTURE, null));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }
}
