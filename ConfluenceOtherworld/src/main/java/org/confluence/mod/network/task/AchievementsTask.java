package org.confluence.mod.network.task;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
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
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
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
