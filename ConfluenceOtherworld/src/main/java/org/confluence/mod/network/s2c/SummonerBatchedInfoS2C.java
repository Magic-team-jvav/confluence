package org.confluence.mod.network.s2c;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.InfoData;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.chat.PortComponentSerialization;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.List;

public record SummonerBatchedInfoS2C(List<Number> numbers, List<Text> texts) implements IPortPacket.S2C {

    public static final ResourceLocation ID = Confluence.asResource("summoner_batched_info");

    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, SummonerBatchedInfoS2C> STREAM_CODEC = PortStreamCodec.composite(
            Number.CODEC.apply(PortByteBufCodecs.list(4096)), SummonerBatchedInfoS2C::numbers,
            Text.CODEC.apply(PortByteBufCodecs.list(4096)), SummonerBatchedInfoS2C::texts,
            SummonerBatchedInfoS2C::new
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
