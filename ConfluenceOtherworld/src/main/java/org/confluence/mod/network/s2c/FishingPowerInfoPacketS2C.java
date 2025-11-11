package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.util.PlayerUtils;

public record FishingPowerInfoPacketS2C(float value) implements IPacketS2C {
    public static final Type<FishingPowerInfoPacketS2C> TYPE = Confluence.createType("fishing_power_info");
    public static final StreamCodec<ByteBuf, FishingPowerInfoPacketS2C> STREAM_CODEC = ByteBufCodecs.FLOAT.map(FishingPowerInfoPacketS2C::new, FishingPowerInfoPacketS2C::value);

    @Override
    public Type<FishingPowerInfoPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleFishingPower(this);
    }

    public static float sendAndGet(ServerPlayer serverPlayer) {
        float fishingPower = PlayerUtils.getFishingPower(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new FishingPowerInfoPacketS2C(fishingPower));
        return fishingPower;
    }
}
