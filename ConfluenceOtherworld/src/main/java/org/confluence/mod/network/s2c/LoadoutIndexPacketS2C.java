package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.TerraLoadouts;

/// 当前是第几套装备栏位（登录时和切换后发给自己），背包里的按钮据此高亮
public record LoadoutIndexPacketS2C(int index) implements IPacketS2C {
    public static final Type<LoadoutIndexPacketS2C> TYPE = Confluence.createType("loadout_index");
    public static final StreamCodec<ByteBuf, LoadoutIndexPacketS2C> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(LoadoutIndexPacketS2C::new, LoadoutIndexPacketS2C::index);

    @Override
    public Type<LoadoutIndexPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        TerraLoadouts.clientIndex = index;
    }

    public static void send(ServerPlayer player, int index) {
        PacketDistributor.sendToPlayer(player, new LoadoutIndexPacketS2C(index));
    }
}
