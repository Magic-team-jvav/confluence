package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.PlayerContainer;
import org.confluence.lib.common.block.HorizontalDirectionalWaterloggedBlock;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.client.model.block.VoidVaultModel;
import org.confluence.mod.client.renderer.item.SimpleGeoItemRenderer;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.menu.VoidVaultMenu;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class VoidVaultBlock extends HorizontalDirectionalWaterloggedBlock implements EntityBlock {
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(0, 0, 3, 16, 21, 13);
    private static final VoxelShape EAST_WEST_SHAPE = box(3, 0, 0, 13, 21, 16);

    public VoidVaultBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? EAST_WEST_SHAPE : NORTH_SOUTH_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BEntity entity) {
            VoidVaultMenu.open(player, viewer -> !entity.isRemoved() && viewer.level() == level &&
                    level.getBlockEntity(pos) == entity && viewer.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    public static class BEntity extends BlockEntity implements PlayerContainer.ValidEntity, GeoBlockEntity {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        public BEntity(BlockPos pos, BlockState state) {
            super(FunctionalBlocks.VOID_VAULT_ENTITY.get(), pos, state);
        }

        @Override
        public BlockEntity self() {
            return this;
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }

    public static class BItem extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        public BItem(VoidVaultBlock block) {
            super(block, new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN));
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(new SimpleGeoItemRenderer<>(VoidVaultModel.MODEL, VoidVaultModel.TEXTURE, VoidVaultModel.ANIMATION));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }
    }
}
