package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;

public record SoulPacketS2C(int maxSoul, float currentSoul, boolean fallenSoulCoreActive) implements IPacketS2C {
    public static final Type<SoulPacketS2C> TYPE = Confluence.createType("soul");
    public static final StreamCodec<ByteBuf, SoulPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SoulPacketS2C::maxSoul,
            ByteBufCodecs.FLOAT, SoulPacketS2C::currentSoul,
            ByteBufCodecs.BOOL, SoulPacketS2C::fallenSoulCoreActive,
            SoulPacketS2C::new
    );

    @Override
    public Type<SoulPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleSoul(this, player);
    }
}
