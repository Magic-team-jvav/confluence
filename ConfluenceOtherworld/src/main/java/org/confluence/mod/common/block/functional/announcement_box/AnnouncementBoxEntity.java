package org.confluence.mod.common.block.functional.announcement_box;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.functional.network.NetworkNode;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Set;

public class AnnouncementBoxEntity extends SignBlockEntity implements INetworkEntity {
    private NetworkNode networkNode;
    private final Int2ObjectMap<Set<BlockPos>> connectedPoses;
    private final Int2ObjectMap<Set<BlockPos>> relativePoses;
    public AnnouncementBoxEntity(BlockPos pos, BlockState blockState) {
        super(FunctionalBlocks.ANNOUNCEMENT_BOX_ENTITY.get(), pos, blockState);
        this.connectedPoses = new Int2ObjectOpenHashMap<>();
        this.relativePoses = new Int2ObjectOpenHashMap<>();
    }
    @Override
    public BlockEntity getSelf() {
        return this;
    }
    @Override
    public void setNetworkNode(NetworkNode node) {
        this.networkNode = node;
    }
    @Override
    public @Nullable NetworkNode getNetworkNode() {
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
}
