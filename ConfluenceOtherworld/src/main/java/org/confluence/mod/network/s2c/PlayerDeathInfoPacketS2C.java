package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;

public record PlayerDeathInfoPacketS2C(Component deathMessage, int respawnTime, short platinum, byte gold, byte silver, byte copper) implements CustomPacketPayload {
    public static final Type<PlayerDeathInfoPacketS2C> TYPE = new Type<>(Confluence.asResource("player_death_info"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDeathInfoPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.TRUSTED_STREAM_CODEC, p -> p.deathMessage,
            ByteBufCodecs.INT, p -> p.respawnTime,
            ByteBufCodecs.SHORT, p -> p.platinum,
            ByteBufCodecs.BYTE, p -> p.gold,
            ByteBufCodecs.BYTE, p -> p.silver,
            ByteBufCodecs.BYTE, p -> p.copper,
            PlayerDeathInfoPacketS2C::new
    );

    @Override
    public Type<PlayerDeathInfoPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                ClientPacketHandler.handleDeathInfo(this, context.player());
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    /**
     * @see PlayerUtils#dropMoney(Player)
     */
    public static boolean replaceCombatKillPacket(ServerPlayer player, Component message) {
        if (CommonConfigs.SHOW_MONEY_DROPS.get()) {
            long drops = LibUtils.getOrCreatePersistedData(player).getLong("confluence:drops_money");
            Coins coins = PlayerUtils.decodeCoin(drops);
            PacketDistributor.sendToPlayer(player, new PlayerDeathInfoPacketS2C(message, PlayerUtils.getRespawnWaitTime(player), (short) coins.platinum(), (byte) coins.gold(), (byte) coins.silver(), (byte) coins.copper()));
            return false;
        }
        return true;
    }
}
