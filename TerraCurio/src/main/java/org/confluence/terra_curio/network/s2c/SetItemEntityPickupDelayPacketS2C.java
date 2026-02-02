package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;

public record SetItemEntityPickupDelayPacketS2C(int id, int delay) implements IPacketS2C {
    public static final Type<SetItemEntityPickupDelayPacketS2C> TYPE = new Type<>(TerraCurio.asResource("set_item_entity_pickup_delay"));
    public static final StreamCodec<ByteBuf, SetItemEntityPickupDelayPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetItemEntityPickupDelayPacketS2C::id,
            ByteBufCodecs.VAR_INT, SetItemEntityPickupDelayPacketS2C::delay,
            SetItemEntityPickupDelayPacketS2C::new
    );

    @Override
    public Type<SetItemEntityPickupDelayPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        TCClientPacketHandler.handleItemPickupDelay(id, delay);
    }

    public static void sendToAll(int id, int delay) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new SetItemEntityPickupDelayPacketS2C(id, delay));
        } else {
            TerraCurio.LOGGER.warn("Trying send a packet with no server!");
        }
    }
}
