package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.network.IPacket;

public record ExtraInventorySyncPacketS2C(int entityId, ExtraInventory extraInventory) implements IPacketS2C {
    public static final Type<ExtraInventorySyncPacketS2C> TYPE = IPacket.createType("extra_inventory_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraInventorySyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ExtraInventorySyncPacketS2C::entityId,
            ExtraInventory.STREAM_CODEC, ExtraInventorySyncPacketS2C::extraInventory,
            ExtraInventorySyncPacketS2C::new
    );

    @Override
    public Type<ExtraInventorySyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        if (player.level().getEntity(entityId) instanceof Player entity) {
            ExtraInventory.of(entity).copyFrom(extraInventory);
        }
    }

    public static void sendToClient(ServerPlayer sendTo, ServerPlayer target, ExtraInventory extraInventory) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            extraInventory.initialize(sendTo);
            PacketDistributor.sendToPlayer(sendTo, new ExtraInventorySyncPacketS2C(target.getId(), extraInventory));
        }
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer from, ServerPlayer to, ExtraInventory extraInventory) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            extraInventory.initialize(from);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(from, new ExtraInventorySyncPacketS2C(to.getId(), extraInventory));
        }
    }
}
