package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ManaStorage;

public record ManaPacketS2C(float currentMana) implements IPacketS2C {
    public static final Type<ManaPacketS2C> TYPE = Confluence.createType("mana");
    public static final StreamCodec<ByteBuf, ManaPacketS2C> STREAM_CODEC = ByteBufCodecs.FLOAT.map(ManaPacketS2C::new, ManaPacketS2C::currentMana);

    @Override
    public Type<ManaPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ManaStorage.of(player).setCurrentMana(currentMana);
    }
}
