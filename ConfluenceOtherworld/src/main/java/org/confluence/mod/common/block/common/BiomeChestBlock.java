package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BiomeChestBlock extends ChestBlock {
    public static final BooleanProperty UNLOCKED = StateProperties.UNLOCKED;
    private final Predicate<ItemStack> isKey;

    public BiomeChestBlock(Predicate<ItemStack> isKey) {
        super(BlockBehaviour.Properties.copy(Blocks.CHEST).explosionResistance(18000), ChestBlocks.BIOME_CHEST_ENTITY::get);
        this.isKey = isKey;
        registerDefaultState(defaultBlockState().setValue(UNLOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(UNLOCKED));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // 禁用合并，直接返回单箱状态
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean unlocked = context.getPlayer() == null || !context.getPlayer().isCreative();

        return this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(TYPE, ChestType.SINGLE)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                .setValue(UNLOCKED, unlocked);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof ServerPlayer serverPlayer && isKey.test(stack) && !state.getValue(UNLOCKED)) {
            level.setBlock(pos, state.setValue(UNLOCKED, true), Block.UPDATE_ALL);
            serverPlayer.level().playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS);
            double posX = pos.getX() + 0.5;
            double posZ = pos.getZ() + 0.5;
            serverPlayer.serverLevel().sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CHAIN.defaultBlockState()),
                    posX, pos.getY() + 0.5, posZ, 200, 0.0625, 0.0625, 0.0625, 0.15
            );
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
            AchievementUtils.awardAchievement(serverPlayer, "big_booty");
            return InteractionResult.SUCCESS;
        }

        // 已解锁时让 ChestBlock 处理
        if (state.getValue(UNLOCKED)) {
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        return state.getValue(UNLOCKED);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return state.getValue(UNLOCKED) ? super.getDestroyProgress(state, player, level, pos) : 0;
    }

    public static class BEntity extends ChestBlockEntity {
        public BEntity(BlockPos pos, BlockState blockState) {
            super(ChestBlocks.BIOME_CHEST_ENTITY.get(), pos, blockState);
        }

        @Override
        public boolean canOpen(Player player) {
            return getBlockState().getValue(UNLOCKED) && super.canOpen(player);
        }

        @Override
        protected Component getDefaultName() {
            return Component.translatable("container.confluence." + BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()).getPath());
        }

        @Override
        public boolean canTakeItem(Container target, int slot, ItemStack stack) {
            return getBlockState().getValue(UNLOCKED);
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return getBlockState().getValue(UNLOCKED);
        }
    }
}
