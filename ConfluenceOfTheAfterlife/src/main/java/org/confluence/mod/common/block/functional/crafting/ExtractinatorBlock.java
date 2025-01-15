package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.client.model.block.ExtractinatorBlockModel;
import org.confluence.mod.client.renderer.item.SimpleGeoItemRenderer;
import org.confluence.mod.common.block.StateProperties;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
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

public class ExtractinatorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<ExtractinatorBlock> CODEC = simpleCodec(ExtractinatorBlock::new);
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

    public ExtractinatorBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any().setValue(StateProperties.HORIZONTAL_TWO_PART, StateProperties.HorizontalTwoPart.BASE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<ExtractinatorBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int index = pState.getValue(FACING).get2DDataValue();
        return pState.getValue(StateProperties.HORIZONTAL_TWO_PART).isBase() ? BASE_SHAPES[index] : RIGHT_SHAPES[index];
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!pLevel.isClientSide) {
            BlockPos relativePos = pPos.relative(StateProperties.HorizontalTwoPart.getConnectedDirection(pState));
            pLevel.setBlockAndUpdate(relativePos, defaultBlockState().setValue(StateProperties.HORIZONTAL_TWO_PART, StateProperties.HorizontalTwoPart.RIGHT).setValue(FACING, pState.getValue(FACING)));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        BlockPos relativePos = pContext.getClickedPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(blockState));
        return level.getBlockState(relativePos).canBeReplaced(pContext) && level.getWorldBorder().isWithinBounds(relativePos) ? blockState : null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        pLevel.setBlockAndUpdate(pPos.relative(StateProperties.HorizontalTwoPart.getConnectedDirection(pState)), Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(StateProperties.HORIZONTAL_TWO_PART, FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Entity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel) {
            ItemStack item = player.getItemInHand(hand);
            ResourceKey<LootTable> path;
            Block block;
            if (item.is(ModTags.Items.DESERT_FOSSIL)) {
                path = ModLootTables.EXTRACT_DESERT_FOSSIL;
                block = NatureBlocks.DESERT_FOSSIL.get();
            } else if (item.is(ModTags.Items.GRAVEL)) {
                path = ModLootTables.EXTRACT_GRAVEL;
                block = Blocks.GRAVEL;
            } else if (item.is(ModTags.Items.JUNK)) {
                path = ModLootTables.EXTRACT_JUNK;
                block = Blocks.LILY_PAD;
            } else if (item.is(ModTags.Items.SLUSH)) {
                path = ModLootTables.EXTRACT_SLUSH;
                block = NatureBlocks.SLUSH.get();
            } else if (item.is(ModTags.Items.MARINE_GRAVEL)) {
                path = ModLootTables.EXTRACT_MARINE_GRAVEL;
                block = NatureBlocks.MARINE_GRAVEL.get();
            } else {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, block.defaultBlockState()),
                    pos.getX() + 0.5F,
                    pos.getY() + 0.75F,
                    pos.getZ() + 0.5F,
                    100, 0F, 0.0625F, 0F, 0.25F);

            LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(path);
            LootParams lootparams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.GIFT);
            for (ItemStack loot : lootTable.getRandomItems(lootparams)) {
                ModUtils.createItemEntity(loot, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, level);
            }

            player.getItemInHand(hand).setCount(item.getCount() - 1);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public static class Entity extends BlockEntity implements GeoBlockEntity {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public Entity(BlockPos pPos, BlockState pBlockState) {
            super(FunctionalBlocks.EXTRACTINATOR_ENTITY.get(), pPos, pBlockState);
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

    public static class Item extends BlockItem implements GeoItem {
        private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

        public Item(ExtractinatorBlock pBlock) {
            super(pBlock, new Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.WHITE).stacksTo(1));
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new SimpleGeoItemRenderer<>(ExtractinatorBlockModel.MODEL, ExtractinatorBlockModel.TEXTURE, ExtractinatorBlockModel.ANIMATION));
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.translatable(getDescriptionId(stack)).withStyle(style -> style.withColor(ModRarity.WHITE.getColor()));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return CACHE;
        }
    }
}
