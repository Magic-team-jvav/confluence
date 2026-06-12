package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.mixed.IMinecraftServer;
import org.jetbrains.annotations.ApiStatus;

public abstract class SecretSeed {
    private final long flag;
    private final ResourceLocation id;

    public SecretSeed(long flag, ResourceLocation id) {
        this.flag = flag;
        this.id = id;
    }

    public abstract boolean match(String seed);

    public long getFlag() {
        return flag;
    }

    public ResourceLocation getId() {
        return id;
    }

    public long applyFlag(long original) {
        return original | flag;
    }

    public boolean match(MinecraftServer server) {
        return match(IMinecraftServer.of(server).confluence$getSecretFlag());
    }

    public boolean match(ServerLevel serverLevel) {
        return match(serverLevel.getServer());
    }

    public boolean match() {
        if (LibUtils.isLogicalClient()) {
            return match(ClientPacketHandler.getSecretFlag());
        }
        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        return server != null && match(server);
    }

    public boolean match(long secretFlag) {
        return (secretFlag & flag) != 0;
    }

    @ApiStatus.OverrideOnly
    public boolean isHided() {
        return false;
    }
}
