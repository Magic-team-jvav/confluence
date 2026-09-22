package org.confluence.mod.common.summoner.network;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.summoner.info.NumberInfo;
import org.confluence.mod.client.summoner.info.TextInfo;
import org.confluence.mod.common.summoner.attachment.InfoData;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.chat.PortComponentSerialization;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.List;

public record SummonerBatchedInfoPayload(List<Number> numbers, List<Text> texts) implements IPortPacket.S2C {

    public static final ResourceLocation ID = Confluence.asResource("summoner_batched_info");

    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, SummonerBatchedInfoPayload> STREAM_CODEC = PortStreamCodec.composite(
            Number.CODEC.apply(PortByteBufCodecs.list(4096)), SummonerBatchedInfoPayload::numbers,
            Text.CODEC.apply(PortByteBufCodecs.list(4096)), SummonerBatchedInfoPayload::texts,
            SummonerBatchedInfoPayload::new
    );

    @Override
    public void work(Player player) {
        InfoData.sync(player, numbers, texts);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public record Number(InfoData.Type type, float amount, Vec3 pos, Vec3 velocity) {

        public static final PortStreamCodec<PortRegistryFriendlyByteBuf, Number> CODEC = PortStreamCodec.composite(
                LibStreamCodecUtils.fromEnum(InfoData.Type.values()), Number::type,
                PortByteBufCodecs.FLOAT, Number::amount,
                LibStreamCodecUtils.VEC_3, Number::pos,
                LibStreamCodecUtils.VEC_3, Number::velocity,
                Number::new
        );
    }

    public record Text(Component text, Vec3 pos, Vec3 velocity) {

        public static final PortStreamCodec<PortRegistryFriendlyByteBuf, Text> CODEC = PortStreamCodec.composite(
                PortComponentSerialization.STREAM_CODEC, Text::text,
                LibStreamCodecUtils.VEC_3, Text::pos,
                LibStreamCodecUtils.VEC_3, Text::velocity,
                Text::new
        );
    }
}
