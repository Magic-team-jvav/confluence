package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.loading.FMLEnvironment;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTwoPartBlock;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.mod.client.renderer.block.ExtractinatorBlockRenderer;
import org.confluence.mod.client.renderer.item.SimpleGeoItemRenderer;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.terra_curio.mixin.client.accessor.MinecraftAccessor;
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

public class ExtractinatorBlock extends HorizontalDirectionalWithHorizontalTwoPartBlock implements EntityBlock {
    private static final VoxelShape BASE_SHAPE_SOUTH = box(3.0, 0.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape BASE_SHAPE_WEST = box(3.0, 0.0, 3.0, 13.0, 16.0, 16.0);
    private static final VoxelShape BASE_SHAPE_NORTH = box(0.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final VoxelShape BASE_SHAPE_EAST = box(3.0, 0.0, 0.0, 13.0, 16.0, 13.0);
    private static final VoxelShape RIGHT_SHAPE_SOUTH = box(0.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final VoxelShape RIGHT_SHAPE_WEST = box(3.0, 0.0, 0.0, 13.0, 16.0, 13.0);
    private static final VoxelShape RIGHT_SHAPE_NORTH = box(3.0, 0.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape RIGHT_SHAPE_EAST = box(3.0, 0.0, 3.0, 13.0, 16.0, 16.0);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};

    public ExtractinatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return state.getValue(PART).isBase() ? BASE_SHAPES[index] : RIGHT_SHAPES[index];
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel) {
            ItemStack itemStack = player.getItemInHand(hand);
            ExtractinatorData data = itemStack.getItemHolder().getData(ModDataMaps.EXTRACTINATOR);
            if (data == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            ExtractinatorData.extract(level, pos, player, hand, serverLevel, itemStack, data);
        } else if (FMLEnvironment.dist.isClient()) {
            ((MinecraftAccessor) Minecraft.getInstance()).setRightClickDelay(1);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public static class BEntity extends BlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
        public final boolean isBase;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.EXTRACTINATOR_ENTITY.get(), pos, blockState);
            this.isBase = blockState.getValue(PART).isBase();
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            controllers.add(new AnimationController<>(this, "controller", state ->
                    state.setAndContinue(RawAnimation.begin().thenLoop("default")))
            );
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }

    public static class BItem extends TooltipBlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public BItem(ExtractinatorBlock block) {
            super(block, new Properties(), ModRarity.WHITE, "tooltip.item.confluence.extractinator.0");
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new SimpleGeoItemRenderer<>(ExtractinatorBlockRenderer.MODEL, ExtractinatorBlockRenderer.TEXTURE, ExtractinatorBlockRenderer.ANIMATION));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }
}
