package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.character.TerraCharacterRules;

import java.util.UUID;

/// 某个玩家的角色名（空字符串 = 没有角色），客户端用它在头顶显示名字
public record CharacterNamePacketS2C(UUID uuid, String name) implements IPacketS2C {
    public static final Type<CharacterNamePacketS2C> TYPE = Confluence.createType("character_name");
    public static final StreamCodec<ByteBuf, CharacterNamePacketS2C> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, CharacterNamePacketS2C::uuid,
            ByteBufCodecs.stringUtf8(TerraCharacterRules.MAX_NAME_LENGTH * 4), CharacterNamePacketS2C::name,
            CharacterNamePacketS2C::new
    );

    @Override
    public Type<CharacterNamePacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        if (name.isEmpty()) TerraCharacterRules.CLIENT_NAMES.remove(uuid);
        else TerraCharacterRules.CLIENT_NAMES.put(uuid, name);
        Player target = player.level().getPlayerByUUID(uuid);
        if (target != null) target.refreshDisplayName();
    }

    public static void send(ServerPlayer to, UUID uuid, String name) {
        PacketDistributor.sendToPlayer(to, new CharacterNamePacketS2C(uuid, name == null ? "" : name));
    }

    public static void sendToAll(MinecraftServer server, UUID uuid, String name) {
        PacketDistributor.sendToAllPlayers(new CharacterNamePacketS2C(uuid, name == null ? "" : name));
    }
}
