package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSpecialData;

import java.util.List;

public record SyncEnemyBannerEntriesPacketS2C(List<String> entries) implements IPacketS2C {
    public static final Type<SyncEnemyBannerEntriesPacketS2C> TYPE = Confluence.createType("sync_enemy_banner_entries");
    public static final StreamCodec<ByteBuf, SyncEnemyBannerEntriesPacketS2C> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())
            .map(SyncEnemyBannerEntriesPacketS2C::new, SyncEnemyBannerEntriesPacketS2C::entries);

    @Override
    public void work(Player player) {
        PlayerSpecialData.of(player).setEnemyBannerEntries(entries);
    }

    @Override
    public Type<SyncEnemyBannerEntriesPacketS2C> type() {
        return TYPE;
    }
}
