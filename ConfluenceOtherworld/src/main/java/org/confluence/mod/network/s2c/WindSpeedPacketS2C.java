package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.common.wind.WindField;
import org.confluence.mod.common.wind.WindState;
import org.joml.Vector2f;

/// 全局风速 + **接收者所在位置**的局地风向。
///
/// 风速全局一致，风向随位置变化（温度噪声梯度，见 {@link WindField}），而客户端拿不到 `RandomState`，
/// 所以局地风向必须由服务端逐玩家下发。载荷只有 3 个 float，按玩家下发约 4 包/秒。
///
/// @param speed     全局风速（m/s），不含阵风与曝露度；客户端用 `WindMath.gust` 自行求瞬时值
/// @param angle     接收者所在位置的局地风向（弧度），已含温度噪声偏差与 directionBlend
/// @param gustPhase 世界阵风相位，保证两端阵风完全一致
public record WindSpeedPacketS2C(float speed, float angle, float gustPhase) implements IPacketS2C {
    public static final Type<WindSpeedPacketS2C> TYPE = Confluence.createType("wind_speed");
    public static final StreamCodec<ByteBuf, WindSpeedPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::speed,
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::angle,
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::gustPhase,
            WindSpeedPacketS2C::new
    );

    @Override
    public Type<WindSpeedPacketS2C> type() {
        return TYPE;
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
        PacketDistributor.sendToPlayer(player, of(player));
    }

    public static void sendToAll(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            sendToClient(player);
        }
    }
}
