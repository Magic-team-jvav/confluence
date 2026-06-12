package org.confluence.mod.network.task;

import net.minecraft.server.MinecraftServer;
import org.confluence.mod.Confluence;

import java.util.function.Consumer;

public record AchievementsTask(
        ServerConfigurationPacketListener listener
) implements ICustomConfigurationTask {
    public static final Type TYPE = new Type(Confluence.asResource("achievements"));

    @Override
    public void run(Consumer<CustomPacketPayload> sender) {
        if (listener instanceof ServerConfigurationPacketListenerImpl impl && impl.isSingleplayerOwner()) {
            listener.finishCurrentTask(TYPE);
        } else {
            MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                listener.finishCurrentTask(TYPE);
            } else {
                sender.accept(new RequestAchievementsPacketS2C(server.usesAuthentication()));
            }
        }
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
