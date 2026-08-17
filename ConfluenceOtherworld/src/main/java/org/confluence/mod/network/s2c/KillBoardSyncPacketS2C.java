package org.confluence.mod.network.s2c;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.gameevent.GameEvent;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record KillBoardSyncPacketS2C(Object2BooleanMap<EntityType<?>> defeatedBosses,
                                     Object2BooleanMap<ResourceKey<? extends GameEvent>> defeatedEvents,
                                     GamePhase gamePhase) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("kill_board_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, KillBoardSyncPacketS2C> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public KillBoardSyncPacketS2C decode(PortRegistryFriendlyByteBuf buffer) {
            return new KillBoardSyncPacketS2C(KillBoard.DEFEATED_BOSSES_STREAM_CODEC.decode(buffer), KillBoard.DEFEATED_EVENTS_STREAM_CODEC.decode(buffer), GamePhase.STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, KillBoardSyncPacketS2C value) {
            KillBoard.DEFEATED_BOSSES_STREAM_CODEC.encode(buffer, value.defeatedBosses);
            KillBoard.DEFEATED_EVENTS_STREAM_CODEC.encode(buffer, value.defeatedEvents);
            GamePhase.STREAM_CODEC.encode(buffer, value.gamePhase);
        }
    };

    @Override
    public void handle(IPortPacket.Context context) {
        Player player = context.player();
        if (player != null) context.enqueueWork(() -> work(player));
    }

    @Override
    public void work(Player player) {
        KillBoard.INSTANCE.applyNetworkState(defeatedBosses, defeatedEvents, gamePhase);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public static void sendToAll() {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(current());
        }
    }

    public static void sendToClient(ServerPlayer player) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, current());
    }

    private static KillBoardSyncPacketS2C current() {
        return new KillBoardSyncPacketS2C(KillBoard.INSTANCE.defeatedBossesSnapshot(), KillBoard.INSTANCE.defeatedEventsSnapshot(), KillBoard.INSTANCE.getGamePhase());
    }
}
