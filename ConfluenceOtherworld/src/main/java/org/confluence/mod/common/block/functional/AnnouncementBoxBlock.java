package org.confluence.mod.common.block.functional;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.NetworkNode;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AnnouncementBoxBlock extends StandingSignBlock implements INetworkBlock {
    public static final BlockSetType BLOCK_SET_TYPE = BlockSetType.register(new BlockSetType(Confluence.MODID + ":announcement_box"));
    public static final WoodType WOOD_TYPE = WoodType.register(new WoodType(Confluence.MODID + ":announcement_box", BLOCK_SET_TYPE));

    public AnnouncementBoxBlock(Properties properties) {
        super(properties, WOOD_TYPE);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        onNodeRemove(pState, pLevel, pPos, pNewState);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (skipInteraction(stack)) {
            return InteractionResult.PASS;
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        BEntity entity = (BEntity) networkEntity;
        if (!level.isClientSide) {
            BEntity.sendMessages(level, pos, entity.getText(true).getMessages(false));
        }
    }

    public static class Wall extends WallSignBlock implements INetworkBlock {
        public Wall(Properties properties) {
            super(properties, WOOD_TYPE);
        }

        @Override
        public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
            onNodeRemove(pState, pLevel, pPos, pNewState);
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new BEntity(pos, state);
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
            ItemStack stack = player.getItemInHand(hand);
            if (skipInteraction(stack)) {
                return InteractionResult.PASS;
            }
            return super.use(state, level, pos, player, hand, hitResult);
        }

        @Override
        public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
            BEntity entity = (BEntity) networkEntity;
            if (!level.isClientSide) {
                BEntity.sendMessages(level, pos, entity.getText(true).getMessages(false));
            }
        }
    }

    public static class BEntity extends SignBlockEntity implements INetworkEntity {
        private NetworkNode networkNode;
        private final Int2ObjectMap<Set<BlockPos>> connectedPoses;
        private final Int2ObjectMap<Set<BlockPos>> relativePoses;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.ANNOUNCEMENT_BOX_ENTITY.get(), pos, blockState);
            this.connectedPoses = new Int2ObjectOpenHashMap<>();
            this.relativePoses = new Int2ObjectOpenHashMap<>();
        }

        @Override
        public void onLoad() {
            super.onLoad();
            onNodeLoad();
        }

        @Override
        public void onChunkUnloaded() {
            super.onChunkUnloaded();
            onNodeUnload();
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            deserializePoses(tag, "connectedPoses", connectedPoses);
            deserializePoses(tag, "relativePoses", relativePoses);
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            serializePoses(tag, "connectedPoses", connectedPoses);
            serializePoses(tag, "relativePoses", relativePoses);
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public CompoundTag getUpdateTag() {
            return serializePoses(new CompoundTag(), "connectedPoses", connectedPoses);
        }

        @Override
        public void handleUpdateTag(CompoundTag tag) {
            deserializePoses(tag, "connectedPoses", connectedPoses);
        }

        @Override
        public BlockEntity getSelf() {
            return this;
        }

        @Override
        public void setNetworkNode(NetworkNode node) {
            this.networkNode = node;
        }

        @Nullable
        @Override
        public NetworkNode getNetworkNode() {
            return networkNode;
        }

        @Override
        public Int2ObjectMap<Set<BlockPos>> getConnectedPoses() {
            return connectedPoses;
        }

        @Override
        public Int2ObjectMap<Set<BlockPos>> getRelativePoses() {
            return relativePoses;
        }

        public static void sendMessages(ServerLevel pLevel, BlockPos pPos, Component[] messages) {
            for (Component text : messages) {
                if (text.getContents() == ComponentContents.EMPTY) continue;
                Vec3 center = pPos.getCenter();
                int square = Mth.square(CommonConfigs.ANNOUNCEMENT_BOX_DISTANCE.get());
                for (Player player : pLevel.players()) {
                    if (player.position().distanceToSqr(center) <= square) {
                        player.sendSystemMessage(text);
                    }
                }
            }
        }
    }
}
