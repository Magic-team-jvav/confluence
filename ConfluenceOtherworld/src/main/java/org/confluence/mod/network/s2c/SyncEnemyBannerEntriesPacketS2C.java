package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.List;

public record SyncEnemyBannerEntriesPacketS2C(List<String> entries) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("sync_enemy_banner_entries");
    public static final PortStreamCodec<ByteBuf, SyncEnemyBannerEntriesPacketS2C> STREAM_CODEC = PortByteBufCodecs.STRING_UTF8.apply(PortByteBufCodecs.list())
            .map(SyncEnemyBannerEntriesPacketS2C::new, SyncEnemyBannerEntriesPacketS2C::entries);

    @Override
    public void work(Player player) {
        PlayerSpecialData.of(player).setEnemyBannerEntries(entries);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }
}
