package org.confluence.mod.mixed;

import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;
import org.jetbrains.annotations.NotNull;

public interface IMinecraftServer {
    void confluence$updateSecretFlag(long flag);

    long confluence$getSecretFlag();

    default boolean confluence$matchesSecretFlag(long flag) {
        return matchesSecretFlag(confluence$getSecretFlag(), flag);
    }

    default boolean confluence$matchesSecretFlag(SecretSeed secretSeed) {
        return confluence$matchesSecretFlag(secretSeed.getFlag());
    }

    /**
     * 单人模式与多人模式通用
     */
    static boolean matchesSecretFlag(@NotNull MinecraftServer server, long flag) {
        return IMinecraftServer.of(server).confluence$matchesSecretFlag(flag);
    }

    static boolean matchesSecretFlag(long secretFlag, long flag) {
        return (secretFlag & flag) != 0;
    }

    static boolean matchesSecretFlag(long flag) {
        if (LibUtils.isLogicalClient()) {
            return matchesSecretFlag(ClientPacketHandler.getSecretFlag(), flag);
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && matchesSecretFlag(server, flag);
    }

    /// @see org.confluence.mod.common.data.saved.GamePhase#isHardmode
    static boolean isHardmode(MinecraftServer server) {
        return IMinecraftServer.of(server).confluence$matchesSecretFlag(IWorldOptions.HARDMODE);
    }

    static boolean isGraduated(MinecraftServer server) {
        return IMinecraftServer.of(server).confluence$matchesSecretFlag(IWorldOptions.GRADUATED);
    }

    static IMinecraftServer of(MinecraftServer server) {
        return (IMinecraftServer) server;
    }
}
