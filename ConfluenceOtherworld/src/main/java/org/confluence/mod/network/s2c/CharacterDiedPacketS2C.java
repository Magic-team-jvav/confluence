package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;

/// 硬核角色死了：客户端在角色列表里把它标为已死（之后不能再用它进入世界）
public record CharacterDiedPacketS2C(String id) implements IPacketS2C {
    public static final Type<CharacterDiedPacketS2C> TYPE = Confluence.createType("character_died");
    public static final StreamCodec<ByteBuf, CharacterDiedPacketS2C> STREAM_CODEC = ByteBufCodecs.stringUtf8(64).map(CharacterDiedPacketS2C::new, CharacterDiedPacketS2C::id);

    @Override
    public Type<CharacterDiedPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        org.confluence.mod.client.character.CharacterStore.markDead(id);
    }

    public static void send(ServerPlayer player, String id) {
        PacketDistributor.sendToPlayer(player, new CharacterDiedPacketS2C(id));
    }
}
