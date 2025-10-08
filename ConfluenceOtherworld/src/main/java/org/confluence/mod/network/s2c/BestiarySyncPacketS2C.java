package org.confluence.mod.network.s2c;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryKeyEvent;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.BestiaryEntry;
import org.confluence.mod.network.IPacket;

import java.util.HashMap;
import java.util.Map;

public record BestiarySyncPacketS2C(Either<Map<String, BestiaryEntry>, String> either) implements IPacketS2C {
    public static final Type<BestiarySyncPacketS2C> TYPE = IPacket.createType("bestiary_sync");
    public static final StreamCodec<ByteBuf, BestiarySyncPacketS2C> STREAM_CODEC = ByteBufCodecs.either(
            LibStreamCodecUtils.map(HashMap::new, ByteBufCodecs.STRING_UTF8, BestiaryEntry.STREAM_CODEC),
            ByteBufCodecs.STRING_UTF8
    ).map(BestiarySyncPacketS2C::new, BestiarySyncPacketS2C::either);

    @Override
    public Type<BestiarySyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientBestiary.getInstance().handle(either);
    }

    public static void syncEntries(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new BestiarySyncPacketS2C(Either.left(Bestiary.INSTANCE.getEntries())));
    }

    public static void syncEntry(LivingEntity living) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new BestiarySyncPacketS2C(Either.right(RegisterBestiaryKeyEvent.getKey(living))));
        }
    }

    public static void syncEntry(LivingEntity living, BestiaryEntry entry) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new BestiarySyncPacketS2C(Either.left(Map.of(RegisterBestiaryKeyEvent.getKey(living), entry))));
        }
    }
}
