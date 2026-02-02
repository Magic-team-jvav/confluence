package com.xiaohunao.enemybanner.payloads;

import com.xiaohunao.enemybanner.EnemyBanner;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record PlayerBannerCountPayload(Object2IntOpenHashMap<String> playerBannerCount, String monsterId) implements CustomPacketPayload {
    public static final Type<PlayerBannerCountPayload> TYPE = new Type<>(EnemyBanner.asResource("player_banner_count"));
    public static final StreamCodec<ByteBuf, PlayerBannerCountPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2IntOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT), PlayerBannerCountPayload::playerBannerCount,
            ByteBufCodecs.STRING_UTF8, PlayerBannerCountPayload::monsterId,
            PlayerBannerCountPayload::new
    );

    @Override
    public @NotNull Type<PlayerBannerCountPayload> type() {
        return TYPE;
    }
}
