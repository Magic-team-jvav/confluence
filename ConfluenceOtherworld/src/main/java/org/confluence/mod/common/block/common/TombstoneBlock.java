package org.confluence.mod.common.block.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TombstoneBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<TombstoneBlock> CODEC = simpleCodec(TombstoneBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public TombstoneBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public TombstoneBlock() {
        this(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return facing == Direction.DOWN && !canSurvive(state, level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof Entity entity) {
            if (level.isClientSide) {
                Util.pauseInIde(new IllegalStateException("Expected to only call this on server"));
            }
            if (!otherPlayerIsEditingSign(player, entity) && player.mayBuild()) {
                openTextEdit(player, entity);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public void openTextEdit(Player player, Entity entity) {
        entity.setAllowedPlayerEditor(player.getUUID());
        // todo player.openTextEdit(entity, isFrontText);
    }

    public boolean otherPlayerIsEditingSign(Player player, Entity entity) {
        UUID uuid = entity.getPlayerWhoMayEdit();
        return uuid != null && !uuid.equals(player.getUUID());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return ModUtils.getTicker(blockEntityType, ModBlocks.TOMBSTONE_ENTITY.get(), Entity::tick);
    }

    @Override
    protected MapCodec<TombstoneBlock> codec() {
        return CODEC;
    }

    public static class Entity extends BlockEntity {
        public Component[] texts = new Component[0];
        private @Nullable UUID playerWhoMayEdit;

        public Entity(BlockPos pos, BlockState blockState) {
            super(ModBlocks.TOMBSTONE_ENTITY.get(), pos, blockState);
        }

        public void setTexts(Component @NotNull [] texts) {
            this.texts = texts;
            markUpdated();
        }

        public Component[] getTexts() {
            return texts;
        }

        public void setAllowedPlayerEditor(@Nullable UUID playWhoMayEdit) {
            this.playerWhoMayEdit = playWhoMayEdit;
        }

        public @Nullable UUID getPlayerWhoMayEdit() {
            return playerWhoMayEdit;
        }

        public boolean playerIsTooFarAwayToEdit(UUID uuid) {
            if (level == null) return true;
            Player player = level.getPlayerByUUID(uuid);
            return player == null || !player.canInteractWithBlock(getBlockPos(), 4.0);
        }

        private void clearInvalidPlayerWhoMayEdit(Entity sign, UUID uuid) {
            if (sign.playerIsTooFarAwayToEdit(uuid)) {
                sign.setAllowedPlayerEditor(null);
            }
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            ListTag textsTag = new ListTag();
            for (Component text : texts) {
                textsTag.add(ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE, text).getOrThrow());
            }
            tag.put("texts", textsTag);
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            if (tag.get("texts") instanceof ListTag textsTag) {
                this.texts = new Component[textsTag.size()];
                int i = 0;
                for (Tag textTag : textsTag) {
                    texts[i++] = ComponentSerialization.CODEC.parse(NbtOps.INSTANCE, textTag).getOrThrow();
                }
            }
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            return saveCustomOnly(registries);
        }

        public static void tick(Level level, BlockPos pos, BlockState state, Entity sign) {
            UUID uuid = sign.getPlayerWhoMayEdit();
            if (uuid != null) {
                sign.clearInvalidPlayerWhoMayEdit(sign, uuid);
            }
        }

        private void markUpdated() {
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }
}
