package org.confluence.mod.common.block.functional.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public interface INetworkEntity {
    BlockEntity getSelf();

    void setNetworkNode(NetworkNode node);

    @Nullable NetworkNode getNetworkNode();

    /**
     * 主动连接,用于寻路与渲染
     */
    Int2ObjectMap<Set<BlockPos>> getConnectedPoses();

    /**
     * 被动连接,用于朔源
     */
    Int2ObjectMap<Set<BlockPos>> getRelativePoses();

    default void markUpdated() {
        BlockEntity self = getSelf();
        self.setChanged();
        if (self.getLevel() != null) {
            self.getLevel().sendBlockUpdated(self.getBlockPos(), self.getBlockState(), self.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    default void onNodeLoad() {
        if (getSelf().getLevel() != null && !getSelf().getLevel().isClientSide) {
            PathService.INSTANCE.onBlockEntityLoad(this);
        }
    }

    default void onNodeUnload() {
        if (getSelf().getLevel() != null && !getSelf().getLevel().isClientSide) {
            PathService.INSTANCE.onBlockEntityUnload(this);
        }
    }

    default void deserializePoses(@NotNull CompoundTag nbt, String posesKey, Int2ObjectMap<Set<BlockPos>> map) {
        map.clear();
        CompoundTag compoundTag = nbt.getCompound(posesKey);
        for (String key : compoundTag.getAllKeys()) {
            ListTag listTag = compoundTag.getList(key, Tag.TAG_COMPOUND);
            int color = Integer.parseInt(key);
            Set<BlockPos> posSet = new HashSet<>();
            listTag.forEach(tag -> {
                BlockPos offset = NbtUtils.readBlockPos((CompoundTag) tag, "offset").orElse(BlockPos.ZERO);
                posSet.add(getSelf().getBlockPos().offset(offset.getX(), offset.getY(), offset.getZ()));
            });
            map.put(color, posSet);
        }
    }

    default CompoundTag serializePoses(CompoundTag nbt, String posesKey, Int2ObjectMap<Set<BlockPos>> map) {
        CompoundTag compoundTag = new CompoundTag();
        for (Int2ObjectMap.Entry<Set<BlockPos>> entry : map.int2ObjectEntrySet()) {
            ListTag listTag = new ListTag();
            for (BlockPos blockPos : entry.getValue()) {
                BlockPos offset = blockPos.subtract(getSelf().getBlockPos()); // 调整为相对坐标
                CompoundTag tag = new CompoundTag();
                tag.put("offset", NbtUtils.writeBlockPos(offset));
                listTag.add(tag);
            }
            compoundTag.put(String.valueOf(entry.getIntKey()), listTag);
        }
        nbt.put(posesKey, compoundTag);
        return nbt;
    }

    default NetworkNode getOrCreateNetworkNode() {
        if (getNetworkNode() == null) {
            PathService.INSTANCE.onBlockEntityLoad(this);
        }
        return getNetworkNode();
    }

    default void connectTo(int color, BlockPos relatedPos, INetworkEntity related) {
        if (relatedPos.equals(getSelf().getBlockPos()) || !relatedPos.equals(related.getSelf().getBlockPos()))
            return;
        Set<BlockPos> posSet = getConnectedPoses().computeIfAbsent(color, i -> new HashSet<>());
        if (!posSet.contains(relatedPos)) {
            posSet.add(relatedPos);
            markUpdated();
        }
        posSet = related.getRelativePoses().computeIfAbsent(color, i -> new HashSet<>());
        if (!posSet.contains(getSelf().getBlockPos())) {
            posSet.add(getSelf().getBlockPos());
            related.markUpdated();
        }
    }

    default void disconnectWith(int color, BlockPos relatedPos, INetworkEntity related) {
        if (relatedPos.equals(getSelf().getBlockPos()) || !relatedPos.equals(related.getSelf().getBlockPos()))
            return;
        Set<BlockPos> posSet = getConnectedPoses().get(color);
        if (posSet != null) {
            posSet.remove(relatedPos);
            if (posSet.isEmpty()) getConnectedPoses().remove(color);
            markUpdated();
        }
        posSet = related.getRelativePoses().get(color);
        if (posSet != null) {
            posSet.remove(getSelf().getBlockPos());
            if (posSet.isEmpty()) related.getRelativePoses().remove(color);
            related.markUpdated();
        }
    }
}
