package org.confluence.mod.common.block.functional.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static org.confluence.mod.common.block.functional.network.INetworkBlock.internalExecute;

public class PathService {
    public static final PathService INSTANCE = new PathService();
    private Queue<NetworkNode> queue;

    public void onServerStart() {
        this.queue = new ArrayDeque<>();
    }

    public void onServerStop() {
        this.queue = null;
    }

    // 方块加载时,创捷网络结点
    public void onBlockEntityLoad(INetworkEntity blockEntity) {
        NetworkService.INSTANCE.createNetworkNode(blockEntity);
        addToQueue(blockEntity);
    }

    // 方块卸载时,删除网络结点和所属网络
    public void onBlockEntityUnload(INetworkEntity blockEntity) {
        NetworkNode networkNode = blockEntity.getNetworkNode();
        if (networkNode == null) return;
        for (Network network : networkNode.getNetworks().values()) {
            // 网络已损坏,需要将所拥有的全部结点重新处理,放入处理队列中
            for (NetworkNode node : network.getNodes()) {
                if (!node.inQueue) {
                    node.inQueue = true;
                    node.cachedSignal = network.hasSignal();
                    queue.add(node);
                }
            }
            NetworkService.INSTANCE.removeNodeInNetwork(networkNode, network);
            NetworkService.INSTANCE.removeNetwork(network);
        }
        NetworkService.INSTANCE.removeNetworkNode(networkNode);
    }

    public void addToQueue(INetworkEntity blockEntity) {
        queue.add(blockEntity.getOrCreateNetworkNode());
    }

    public void pathFindingTick() {
        if (queue == null) return;
        boolean flag = false;
        while (!queue.isEmpty()) {
            flag = true;
            NetworkNode cur = queue.remove();
            cur.inQueue = false;
            INetworkEntity entity = cur.getEntity();
            Level level = entity.getSelf().getLevel();
            if (level == null || !level.isLoaded(cur.getPos()) || entity.getSelf().isRemoved()) continue;

            for (Int2ObjectMap.Entry<Set<BlockPos>> entry1 : entity.getConnectedPoses().int2ObjectEntrySet()) {
                int color = entry1.getIntKey();
                Iterator<BlockPos> iterator = entry1.getValue().iterator();
                while (iterator.hasNext()) {
                    BlockPos pos = iterator.next();
                    if (!level.isLoaded(pos)) continue;
                    Network curNetwork = null;
                    if (level.getBlockEntity(pos) instanceof INetworkEntity blockEntity) {
                        curNetwork = cur.getOrCreateNetwork(color);
                        NetworkNode next = blockEntity.getOrCreateNetworkNode();
                        Network nextNetwork = next.getNetwork(color);
                        if (nextNetwork == null) {
                            // 将结点加入到网络中
                            NetworkService.INSTANCE.addNodeToNetwork(next, curNetwork);
                            queue.add(next);
                        } else if (curNetwork != nextNetwork) {
                            // 合并网络
                            NetworkService.INSTANCE.mergeNetwork(curNetwork, nextNetwork);
                            curNetwork = cur.getNetwork(color);
                        }
                    } else {
                        // 移除不存在的坐标
                        iterator.remove();
                        Network network = cur.getNetwork(color);
                        if (network != null) queue.addAll(network.getNodes());
                    }
                    if (curNetwork != null) { // 获取新网络的信号
                        cur.cachedSignal = curNetwork.calculateSignal();
                        curNetwork.schedule = true;
                    }
                }
                if (entry1.getValue().isEmpty()) {
                    entity.getConnectedPoses().remove(color);
                }
            }
        }
        if (flag) {
//            Confluence.LOGGER.debug("Path finding finish");
            for (Network network : NetworkService.INSTANCE.getNetworks()) {
//                Confluence.LOGGER.debug("Network${}, #{}:", network.hasSignal(), network.getColor());
                if (network.schedule) {
                    network.schedule = false;
                    network.getNodes().stream()
//                        .peek(node -> Confluence.LOGGER.debug("Scheduled Node#{}: {}", node.getId(), node.getPos().toShortString()))
                        .filter(node -> node.cachedSignal != network.hasSignal())
                        .map(NetworkNode::getEntity)
                        .collect(Collectors.toSet())
                        .forEach(entity -> internalExecute((ServerLevel) entity.getSelf().getLevel(), null, -1, network.hasSignal(), entity));
                }/* else {
                    for (NetworkNode node : network.getNodes()) {
                        Confluence.LOGGER.debug("Node#{}: {}", node.getId(), node.getPos().toShortString());
                    }
                }*/
            }
        }
    }
}