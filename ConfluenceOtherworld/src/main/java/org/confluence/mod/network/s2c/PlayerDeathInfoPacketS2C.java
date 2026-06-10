package org.confluence.mod.network.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;

public record PlayerDeathInfoPacketS2C(Component deathMessage, int respawnTime, short platinum, byte gold, byte silver, byte copper) implements IPacketS2C {
    public static final Type<PlayerDeathInfoPacketS2C> TYPE = Confluence.createType("player_death_info");
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDeathInfoPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.TRUSTED_STREAM_CODEC, PlayerDeathInfoPacketS2C::deathMessage,
            ByteBufCodecs.VAR_INT, PlayerDeathInfoPacketS2C::respawnTime,
            ByteBufCodecs.SHORT, PlayerDeathInfoPacketS2C::platinum,
            ByteBufCodecs.BYTE, PlayerDeathInfoPacketS2C::gold,
            ByteBufCodecs.BYTE, PlayerDeathInfoPacketS2C::silver,
            ByteBufCodecs.BYTE, PlayerDeathInfoPacketS2C::copper,
            PlayerDeathInfoPacketS2C::new
    );

    @Override
    public Type<PlayerDeathInfoPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleDeathInfo(this, player);
    }

    /// @see PlayerUtils#dropMoney(Player)
    public static boolean replaceCombatKillPacket(ServerPlayer player, Component message) {
        if (CommonConfigs.SHOW_MONEY_DROPS.get()) {
            CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(player);
            long drops = tag.getLong("confluence:drops_money");
            tag.remove("confluence:drops_money");
            Coins coins = PlayerUtils.decodeCoin(drops);
            PacketDistributor.sendToPlayer(player, new PlayerDeathInfoPacketS2C(message, PlayerUtils.getRespawnWaitTime(player), (short) coins.platinum(), (byte) coins.gold(), (byte) coins.silver(), (byte) coins.copper()));
            return false;
        }
        return true;
    }
}
