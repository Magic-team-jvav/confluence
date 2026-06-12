package org.confluence.mod.network.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.chat.PortComponentSerialization;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record PlayerDeathInfoPacketS2C(Component deathMessage, int respawnTime, short platinum,
                                       byte gold, byte silver,
                                       byte copper) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("player_death_info");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, PlayerDeathInfoPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortComponentSerialization.TRUSTED_STREAM_CODEC, PlayerDeathInfoPacketS2C::deathMessage,
            PortByteBufCodecs.VAR_INT, PlayerDeathInfoPacketS2C::respawnTime,
            PortByteBufCodecs.SHORT, PlayerDeathInfoPacketS2C::platinum,
            PortByteBufCodecs.BYTE, PlayerDeathInfoPacketS2C::gold,
            PortByteBufCodecs.BYTE, PlayerDeathInfoPacketS2C::silver,
            PortByteBufCodecs.BYTE, PlayerDeathInfoPacketS2C::copper,
            PlayerDeathInfoPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
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
            Confluence.NETWORK_HANDLER.sendToPlayer(player, new PlayerDeathInfoPacketS2C(message, PlayerUtils.getRespawnWaitTime(player), (short) coins.platinum(), (byte) coins.gold(), (byte) coins.silver(), (byte) coins.copper()));
            return false;
        }
        return true;
    }
}
