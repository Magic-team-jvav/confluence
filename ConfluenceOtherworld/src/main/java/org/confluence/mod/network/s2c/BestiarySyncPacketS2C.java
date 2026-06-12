package org.confluence.mod.network.s2c;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryKeyEvent;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.BestiaryEntry;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.HashMap;
import java.util.Map;

public record BestiarySyncPacketS2C(
        Either<Map<String, BestiaryEntry>, String> either) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("bestiary_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, BestiarySyncPacketS2C> STREAM_CODEC = PortByteBufCodecs.either(
            LibStreamCodecUtils.map(HashMap::new, PortByteBufCodecs.STRING_UTF8, BestiaryEntry.STREAM_CODEC),
            PortByteBufCodecs.STRING_UTF8
    ).map(BestiarySyncPacketS2C::new, BestiarySyncPacketS2C::either);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ClientBestiary.getInstance().handle(player.level(), either);
    }

    public static void syncEntries(ServerPlayer player) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new BestiarySyncPacketS2C(Either.left(Bestiary.INSTANCE.getEntries())));
    }

    public static void syncEntry(LivingEntity living) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new BestiarySyncPacketS2C(Either.right(RegisterBestiaryKeyEvent.getKey(living))));
        }
    }

    public static void syncEntry(LivingEntity living, BestiaryEntry entry) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new BestiarySyncPacketS2C(Either.left(Map.of(RegisterBestiaryKeyEvent.getKey(living), entry))));
        }
    }
}
