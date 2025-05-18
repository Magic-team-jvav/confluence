package org.confluence.mod.common.block.functional.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.block.StateProperties;

public class NetworkNode {
    private final int id;
    private final Int2ObjectMap<Network> networks;
    private final INetworkEntity blockEntity;
    boolean inQueue; // 仅用于寻路服务
    boolean cachedSignal; // 仅用于寻路服务

    public NetworkNode(int id, INetworkEntity blockEntity) {
        this.id = id;
        this.blockEntity = blockEntity;
        this.networks = new Int2ObjectOpenHashMap<>();
        this.inQueue = false;
        this.cachedSignal = false;
    }

    public int getId() {
        return id;
    }

    public Network getNetwork(int color) {
        return networks.get(color);
    }

    public Network getOrCreateNetwork(int color) {
        Network network = networks.get(color);
        if (network == null) {
            Network network1 = NetworkService.INSTANCE.createNetwork(color);
            network1.setSignal(cachedSignal);
            NetworkService.INSTANCE.addNodeToNetwork(this, network1);
            return network1;
        } else {
            return network;
        }
    }

    public Int2ObjectMap<Network> getNetworks() {
        return networks;
    }

    public void removeNetwork(int color) {
        networks.remove(color);
    }

    public void addNetwork(Network network) {
        networks.put(network.getColor(), network);
    }

    public BlockPos getPos() {
        return blockEntity.getSelf().getBlockPos();
    }

    public BlockState getState() {
        return blockEntity.getSelf().getBlockState();
    }

    public INetworkEntity getEntity() {
        return blockEntity;
    }

    public boolean hasSignal() {
        return networks.values().stream().anyMatch(Network::hasSignal);
    }

    /**
     * @param blockPos 不检测的方块坐标
     */
    public boolean hasSignal(BlockPos blockPos) {
        return networks.values().stream().flatMap(network -> network.getNodes().stream()).anyMatch(networkNode -> {
            BlockState blockState = networkNode.getState();
            return !networkNode.getPos().equals(blockPos) && blockState.hasProperty(StateProperties.SIGNAL) && blockState.getValue(StateProperties.SIGNAL);
        });
    }
}