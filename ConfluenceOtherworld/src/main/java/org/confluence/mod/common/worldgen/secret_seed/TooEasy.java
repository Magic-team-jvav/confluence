package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.TaskScheduler;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class TooEasy extends SecretSeed {
    private static TaskScheduler scheduler;

    public TooEasy(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "too easy".equals(seed);
    }

    public static void setToHardmode(MinecraftServer server) {
        if (ModSecretSeeds.TOO_EASY.match(server) && !KillBoard.INSTANCE.getGamePhase().isHardmode()) {
            if (getScheduler(false) != null) return;
            getScheduler(true).schedule(() -> {
                Component component = Component.translatable("message.confluence.too_easy.ready").withColor(GlobalColors.MESSAGE.get());
                server.getPlayerList().broadcastSystemMessage(component, false);
            }, 500);
            getScheduler(true).schedule(() -> KillBoard.INSTANCE.setGamePhase(server, GamePhase.WALL_OF_FLESH), 600);
        }
    }

    @Contract("true -> !null")
    public static @Nullable TaskScheduler getScheduler(boolean create) {
        if (create && scheduler == null) {
            scheduler = new TaskScheduler(0);
        }
        return scheduler;
    }
}
