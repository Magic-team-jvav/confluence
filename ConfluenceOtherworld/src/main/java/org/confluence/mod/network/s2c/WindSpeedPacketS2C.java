package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.common.wind.WindField;
import org.confluence.mod.common.wind.WindState;
import org.joml.Vector2f;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 服务端 → 客户端的风同步包。
///
/// **风速全局一致，风向逐玩家**：{@code speed} 是全局风速（m/s），{@code angle} 是按该玩家所在位置
/// 求出的局地风向方位角（温度噪声梯度），{@code gustPhase} 是阵风相位。因此这个包不能广播同
/// 一份数据，必须逐玩家生成（见 {@link #sendToClient}）。
public record WindSpeedPacketS2C(float speed, float angle, float gustPhase) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("wind_speed");
    public static final PortStreamCodec<ByteBuf, WindSpeedPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.FLOAT, WindSpeedPacketS2C::speed,
            PortByteBufCodecs.FLOAT, WindSpeedPacketS2C::angle,
            PortByteBufCodecs.FLOAT, WindSpeedPacketS2C::gustPhase,
            WindSpeedPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        WeatherHandler.handleWindSpeed(this);
    }

    public static WindSpeedPacketS2C of(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        WindState state = WindState.INSTANCE;
        Vector2f dir = WindField.direction(level, state, player.getX(), player.getZ());
        return new WindSpeedPacketS2C(state.speed(), (float) Mth.atan2(dir.y, dir.x), state.gustPhase());
    }

    public static void sendToClient(ServerPlayer player) {
        PortPacketDistributor.sendToPlayer(player, of(player));
    }

    public static void sendToAll(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            sendToClient(player);
        }
    }
}
