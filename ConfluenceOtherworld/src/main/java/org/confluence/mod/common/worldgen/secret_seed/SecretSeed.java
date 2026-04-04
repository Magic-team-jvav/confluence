package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
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

    public boolean match(MinecraftServer server) {
        return IMinecraftServer.of(server).confluence$matchesSecretFlag(this);
    }

    public boolean match(ServerLevel serverLevel) {
        return match(serverLevel.getServer());
    }

    public boolean match() {
        if (LibUtils.isLogicalClient()) {
            return match(ClientPacketHandler.getSecretFlag());
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
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
