package org.confluence.mod.common.block.functional.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.item.common.WireCutterItem;
import org.confluence.mod.common.item.common.WrenchItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public interface INetworkBlock {
    default void onNodeRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState pNewState) {
        if (!level.isClientSide && !state.is(pNewState.getBlock()) && level.getBlockEntity(pos) instanceof INetworkEntity entity) {
            PathService.INSTANCE.onBlockEntityUnload(entity);
            // 根据relativePoses,断开与该方块的连接
            for (Int2ObjectMap.Entry<Set<BlockPos>> entry : entity.getRelativePoses().int2ObjectEntrySet()) {
                int color = entry.getIntKey();
                for (BlockPos blockPos : entry.getValue()) {
                    if (level.getBlockEntity(blockPos) instanceof INetworkEntity entity1) {
                        Set<BlockPos> posSet = entity1.getConnectedPoses().get(color);
                        if (posSet == null) continue;
                        posSet.remove(pos);
                        if (posSet.isEmpty()) entity1.getConnectedPoses().remove(color);
                        entity1.markUpdated();
                    }
                }
            }
        }
    }

    /**
     * 默认的执行,比如红石激活
     */
    default void execute(BlockState state, ServerLevel level, BlockPos pos, boolean hasSignal) {
        execute(state, level, pos, -1, hasSignal);
    }

    /**
     * 执行机械方块的可执行代码,并执行联系的方块
     *
     * @param pos   方块自己的坐标
     * @param color 由什么颜色的连线执行
     */
    default void execute(BlockState state, ServerLevel level, BlockPos pos, int color, boolean hasSignal) {
        if (level.getBlockEntity(pos) instanceof INetworkEntity entity) {
            if (color == -1) { // 激活所有网络
                entity.getOrCreateNetworkNode().getNetworks().values().stream()
                        .filter(network -> hasSignal != network.hasSignal()) // 只处理不同的信号
                        .peek(network -> network.setSignal(hasSignal))
                        .flatMap(network -> network.getNodes().stream().map(networkNode -> new Tuple<>(network.getColor(), networkNode.getEntity())))
                        .collect(Collectors.toSet())
                        .forEach(tuple -> internalExecute(level, pos, tuple.getA(), hasSignal, tuple.getB()));
            } else {
                Network network = entity.getOrCreateNetworkNode().getNetwork(color);
                if (network != null && hasSignal != network.hasSignal()) { // 同样只处理不同的信号
                    network.setSignal(hasSignal);
                    network.getNodes().stream()
                            .map(NetworkNode::getEntity)
                            .collect(Collectors.toSet())
                            .forEach(entity1 -> internalExecute(level, pos, color, hasSignal, entity1));
                }
            }
            if (hasSignal) {
                onExecute(state, level, pos, color, entity);
            } else {
                onUnExecute(state, level, pos, color, entity);
            }
        }
    }

    /**
     * 仅执行特定的机械方块
     *
     * @param pos 激活该方块的来源坐标
     */
    static void internalExecute(ServerLevel level, @Nullable BlockPos pos, int color, boolean hasSignal, INetworkEntity networkEntity) {
        if (level == null) return;
        BlockState blockState = networkEntity.getSelf().getBlockState();
        BlockPos blockPos = networkEntity.getSelf().getBlockPos();
        if (blockPos.equals(pos)) return; // 确保被直接激活的方块最后再执行
        if (blockState.getBlock() instanceof INetworkBlock block) {
            if (hasSignal) {
                block.onExecute(blockState, level, blockPos, color, networkEntity);
            } else {
                block.onUnExecute(blockState, level, blockPos, color, networkEntity);
            }
        }
    }

    /**
     * 正脉冲执行的代码
     */
    void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity);

    /**
     * 负脉冲执行的代码
     */
    default void onUnExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {}

    default boolean skipInteraction(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof WrenchItem || item instanceof WireCutterItem;
    }
}
