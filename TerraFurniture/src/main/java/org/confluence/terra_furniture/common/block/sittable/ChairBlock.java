package org.confluence.terra_furniture.common.block.sittable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.block.func.BasePropertyHorizontalDirectionBlock;
import org.confluence.terra_furniture.client.generators.HorizontalBDG;
import org.confluence.terra_furniture.common.block.func.TFBlockSetType;
import org.confluence.terra_furniture.common.block.func.be.BaseSittableBE;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * 椅子方块基础类，提供乘坐的基本功能、乘坐位置调态和碰撞箱调态。
 */
public class ChairBlock extends BasePropertyHorizontalDirectionBlock<ChairBlock> implements EntityBlock {
    public final VoxelShape shapeCollision;
    private final float yOff;

    public ChairBlock(TFBlockSetType type, BlockState state, Consumer<Properties> extraProperties, float yOff) {
        super(type, state, extraProperties);
        this.yOff = yOff;
        shapeCollision = Block.box(4.0, 0.0, 4.0, 12.0, 16 * yOff, 12.0);
    }

    /**
     * 仅供给CODEC使用
     */
    public ChairBlock(TFBlockSetType type, BlockState state, Properties properties, float yOff) {
        super(type, state, properties);
        this.yOff = yOff;
        shapeCollision = Block.box(4.0, 0.0, 4.0, 12.0, 16 * yOff, 12.0);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shapeCollision;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

/*  When the behavior goes wrong, check if parent class still use this.

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
*/

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.PASS;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChairBE chairBlock)) {
            TerraFurniture.LOGGER.error("ChairBE block entity is missing, it's an unexpected state.");
            return InteractionResult.FAIL;
        }
        return chairBlock.useAct(level, pos, player);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ChairBE(blockPos, blockState);
    }

    @Override
    @Nullable
    public <E extends BlockEntity> BlockEntityTicker<E> getTicker(Level level, BlockState state, BlockEntityType<E> blockEntityType) {
        return level.isClientSide() ? null : (level1, pos, blockState, t) -> {
            if (t instanceof BaseSittableBE<?> blockEntity) blockEntity.tickAtServer();
        };
    }

    @Override
    protected BasePropertyHorizontalDirectionBlock<ChairBlock> createNewInstance(BlockState baseState, Properties properties) {
        return new ChairBlock(getType(), baseState, properties, yOff);
    }

    @Override
    public @Nullable BlockDataGenerator<? super ChairBlock> getGenerator() {
        if (
                this.equals(TFBlocks.GLASS_CHAIR.get())
                // || ...
        ) return null;
        return new HorizontalBDG<>() {
            @Override
            public String getTemplateType(ChairBlock block) {
                return "chair";
            }
        };
    }

    @Override
    public boolean hasParticle(ChairBlock block) {
        return false;
    }

    public static class ChairBE extends BaseSittableBE<ChairBE> {
        private double yOffset = 0.0;

        public ChairBE(BlockEntityType<? extends ChairBE> type, BlockPos pos, BlockState blockState) {
            super(type, pos, blockState);
            if (blockState.getBlock() instanceof ChairBlock chairBlock) yOffset = chairBlock.yOff;
        }
        public ChairBE(BlockPos pos, BlockState blockState) {
            super(TFBlocks.CHAIR_ENTITY.get(), pos, blockState);
            if (blockState.getBlock() instanceof ChairBlock chairBlock) yOffset = chairBlock.yOff;
        }

        @Override
        public double getYSvOffset() {
            return yOffset;
        }
    }
}
