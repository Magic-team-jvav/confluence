package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;

public record ExtraInventorySyncPacketS2C(
        int entityId,
        ExtraInventory extraInventory
) implements IPacketS2C {
    public static final Type<ExtraInventorySyncPacketS2C> TYPE = Confluence.createType("extra_inventory_sync");
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
        if (player.level().getEntity(entityId) instanceof Player target) {
            ExtraInventory.of(target).copyFrom(extraInventory);
        }
    }

    public static void sendToClient(ServerPlayer sendTo, ServerPlayer target) {
        ExtraInventory extraInventory = ExtraInventory.of(target);
        extraInventory.initialize(target);
        PacketDistributor.sendToPlayer(sendTo, new ExtraInventorySyncPacketS2C(target.getId(), extraInventory));
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer sendTo, ServerPlayer target) {
        ExtraInventory extraInventory = ExtraInventory.of(target);
        extraInventory.initialize(target);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(sendTo, new ExtraInventorySyncPacketS2C(target.getId(), extraInventory));
    }
}
