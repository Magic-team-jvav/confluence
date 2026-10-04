package org.confluence.mod.common.summoner.network;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.summoner.info.NumberInfo;
import org.confluence.mod.client.summoner.info.TextInfo;
import org.confluence.mod.common.summoner.attachment.InfoData;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;


public record SummonerBatchedInfoPayload(List<Number> numbers, List<Text> texts) implements IPacketS2C {

    public static final Type<SummonerBatchedInfoPayload> TYPE = Confluence.createType("summoner_batched_info");

    public static final StreamCodec<RegistryFriendlyByteBuf, SummonerBatchedInfoPayload> STREAM_CODEC = StreamCodec.composite(
            Number.CODEC.apply(ByteBufCodecs.list(4096)), SummonerBatchedInfoPayload::numbers,
            Text.CODEC.apply(ByteBufCodecs.list(4096)), SummonerBatchedInfoPayload::texts,
            SummonerBatchedInfoPayload::new
    );

    @Override
    public void work(Player player) {
        InfoData.sync(player, numbers, texts);
    }

    @Override
    public Type<SummonerBatchedInfoPayload> type() {
        return TYPE;
    }

    public record Number(InfoData.Type type, float amount, Vec3 pos, Vec3 velocity) {

        public static final StreamCodec<RegistryFriendlyByteBuf, Number> CODEC = StreamCodec.composite(
                LibStreamCodecUtils.fromEnum(InfoData.Type.values()), Number::type,
                ByteBufCodecs.FLOAT, Number::amount,
                LibStreamCodecUtils.VEC_3, Number::pos,
                LibStreamCodecUtils.VEC_3, Number::velocity,
                Number::new
        );
    }

    public record Text(Component text, Vec3 pos, Vec3 velocity) {

        public static final StreamCodec<RegistryFriendlyByteBuf, Text> CODEC = StreamCodec.composite(
                ComponentSerialization.STREAM_CODEC, Text::text,
                LibStreamCodecUtils.VEC_3, Text::pos,
                LibStreamCodecUtils.VEC_3, Text::velocity,
                Text::new
        );
    }
}
