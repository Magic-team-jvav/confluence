package org.confluence.mod.network.s2c;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record ExtraInventorySyncPacketS2C(
        int entityId,
        ExtraInventory extraInventory
) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("extra_inventory_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, ExtraInventorySyncPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, ExtraInventorySyncPacketS2C::entityId,
            ExtraInventory.STREAM_CODEC, ExtraInventorySyncPacketS2C::extraInventory,
            ExtraInventorySyncPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        if (player.level().getEntity(entityId) instanceof Player target) {
            ExtraInventory.of(target).copyFrom(extraInventory);
        }
    }

    public static void sendToClient(ServerPlayer sendTo, ServerPlayer target) {
        ExtraInventory extraInventory = ExtraInventory.of(target);
        extraInventory.initialize(target);
        Confluence.NETWORK_HANDLER.sendToPlayer(sendTo, new ExtraInventorySyncPacketS2C(target.getId(), extraInventory));
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer sendTo, ServerPlayer target) {
        ExtraInventory extraInventory = ExtraInventory.of(target);
        extraInventory.initialize(target);
        Confluence.NETWORK_HANDLER.sendToPlayersTrackingEntityAndSelf(sendTo, new ExtraInventorySyncPacketS2C(target.getId(), extraInventory));
    }
}
