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
    default void onNodeRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState) {
        if (!pLevel.isClientSide && !pState.is(pNewState.getBlock()) && pLevel.getBlockEntity(pPos) instanceof INetworkEntity entity) {
            PathService.INSTANCE.onBlockEntityUnload(entity);
            // 根据relativePoses,断开与该方块的连接
            for (Int2ObjectMap.Entry<Set<BlockPos>> entry : entity.getRelativePoses().int2ObjectEntrySet()) {
                int color = entry.getIntKey();
                for (BlockPos pos : entry.getValue()) {
                    if (pLevel.getBlockEntity(pos) instanceof INetworkEntity entity1) {
                        Set<BlockPos> posSet = entity1.getConnectedPoses().get(color);
                        if (posSet == null) continue;
                        posSet.remove(pPos);
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
    default void execute(BlockState pState, ServerLevel pLevel, BlockPos pPos, boolean hasSignal) {
        execute(pState, pLevel, pPos, -1, hasSignal);
    }

    /**
     * 执行机械方块的可执行代码,并执行联系的方块
     *
     * @param pPos  方块自己的坐标
     * @param color 由什么颜色的连线执行
     */
    default void execute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int color, boolean hasSignal) {
        if (pLevel.getBlockEntity(pPos) instanceof INetworkEntity entity) {
            if (color == -1) { // 激活所有网络
                entity.getOrCreateNetworkNode().getNetworks().values().stream()
                        .filter(network -> hasSignal != network.hasSignal()) // 只处理不同的信号
                        .peek(network -> network.setSignal(hasSignal))
                        .flatMap(network -> network.getNodes().stream().map(networkNode -> new Tuple<>(network.getColor(), networkNode.getEntity())))
                        .collect(Collectors.toSet())
                        .forEach(tuple -> internalExecute(pLevel, pPos, tuple.getA(), hasSignal, tuple.getB()));
            } else {
                Network network = entity.getOrCreateNetworkNode().getNetwork(color);
                if (network != null && hasSignal != network.hasSignal()) { // 同样只处理不同的信号
                    network.setSignal(hasSignal);
                    network.getNodes().stream()
                            .map(NetworkNode::getEntity)
                            .collect(Collectors.toSet())
                            .forEach(entity1 -> internalExecute(pLevel, pPos, color, hasSignal, entity1));
                }
            }
            if (hasSignal) {
                onExecute(pState, pLevel, pPos, color, entity);
            } else {
                onUnExecute(pState, pLevel, pPos, color, entity);
            }
        }
    }

    /**
     * 仅执行特定的机械方块
     *
     * @param pPos 激活该方块的来源坐标
     */
    static void internalExecute(ServerLevel pLevel, @Nullable BlockPos pPos, int color, boolean hasSignal, INetworkEntity entity) {
        if (pLevel == null) return;
        BlockState blockState = entity.getSelf().getBlockState();
        BlockPos blockPos = entity.getSelf().getBlockPos();
        if (blockPos.equals(pPos)) return; // 确保被直接激活的方块最后再执行
        if (blockState.getBlock() instanceof INetworkBlock block) {
            if (hasSignal) {
                block.onExecute(blockState, pLevel, blockPos, color, entity);
            } else {
                block.onUnExecute(blockState, pLevel, blockPos, color, entity);
            }
        }
    }

    /**
     * 正脉冲执行的代码
     */
    void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity);

    /**
     * 负脉冲执行的代码
     */
    default void onUnExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {}

    default boolean skipInteraction(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof WrenchItem || item instanceof WireCutterItem;
    }
}
