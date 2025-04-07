package org.confluence.mod.common.block.functional.network;

import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.block.StateProperties;

import java.util.HashSet;
import java.util.Set;

public class Network {
    private final int color;
    private final Set<NetworkNode> nodes;
    private boolean signal;
    boolean schedule;

    public Network(int color) {
        this.color = color;
        this.nodes = new HashSet<>();
        this.signal = false;
        this.schedule = false;
    }

    public int getColor() {
        return color;
    }

    public Set<NetworkNode> getNodes() {
        return nodes;
    }

    public void addNode(NetworkNode node) {
        nodes.add(node);
    }

    public void removeNode(NetworkNode node) {
        nodes.remove(node);
    }

    public void setSignal(boolean signal) {
        this.signal = signal;
    }

    public boolean hasSignal() {
        return signal;
    }

    public boolean calculateSignal() {
        this.signal = nodes.stream().anyMatch(node -> {
            BlockState blockState = node.getState();
            return blockState.hasProperty(StateProperties.SIGNAL) && blockState.getValue(StateProperties.SIGNAL);
        });
        return signal;
    }

    public void destroy() {
        for (NetworkNode node : nodes) {
            node.removeNetwork(color);
        }
        nodes.clear();
    }

    // 调用时需要保证n1大于n2
    private static Network mergeInPrior(Network n1, Network n2) {
        // 将n2的所有结点的所属网络设置为n1
        for (NetworkNode node : n2.nodes) {
            node.addNetwork(n1);
        }
        // 将n2的所有节点加入到n1中
        n1.nodes.addAll(n2.nodes);
        // 清空n2
        n2.nodes.clear();
        return n1;
    }

    // 按照大小合并网络
    public static Network merge(Network n1, Network n2) {
        if (n1.nodes.size() >= n2.nodes.size()) return mergeInPrior(n1, n2);
        else return mergeInPrior(n2, n1);
    }
}