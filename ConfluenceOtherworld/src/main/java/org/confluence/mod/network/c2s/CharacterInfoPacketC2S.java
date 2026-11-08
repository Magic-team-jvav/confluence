package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.character.TerraCharacterRules;

/// 进入世界时客户端告诉服务端：用的是哪个泰拉式角色（编号、名字、难度）
public record CharacterInfoPacketC2S(String id, String name, byte difficulty) implements IPacketC2S {
    public static final Type<CharacterInfoPacketC2S> TYPE = Confluence.createType("character_info");
    public static final StreamCodec<ByteBuf, CharacterInfoPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(64), CharacterInfoPacketC2S::id,
            ByteBufCodecs.stringUtf8(TerraCharacterRules.MAX_NAME_LENGTH * 4), CharacterInfoPacketC2S::name,
            ByteBufCodecs.BYTE, CharacterInfoPacketC2S::difficulty,
            CharacterInfoPacketC2S::new
    );

    @Override
    public Type<CharacterInfoPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        TerraCharacterRules.apply(player, id, name, difficulty);
    }

    public static void sendToServer(String id, String name, byte difficulty) {
        PacketDistributor.sendToServer(new CharacterInfoPacketC2S(id, name, difficulty));
    }
}
