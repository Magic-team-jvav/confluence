package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientBeamCache;
/**
 * <h1>守卫连枷光束同步</h1>
 * 将连枷当前锁定的激光目标实体ID 列表同步到客户端，供光束渲染使用
 *
 * @param flailId   连枷实体 ID
 * @param targetIds 目标实体 ID 列表
 * @param elder     是否为远古守卫（决定光束颜色）
 */
public record GuardianFlailBeamPacketS2C(int flailId, int[] targetIds, boolean elder) implements IPacketS2C {
public static final Type<GuardianFlailBeamPacketS2C> TYPE = Confluence.createType("guardian_flail_beam");

    private static final StreamCodec<ByteBuf, int[]> INT_ARRAY = new StreamCodec<>() {
        @Override
        public int[] decode(ByteBuf buf) {
            int len = ByteBufCodecs.VAR_INT.decode(buf);
            int[] arr = new int[len];
            for (int i = 0; i < len; i++) {
                arr[i] = ByteBufCodecs.VAR_INT.decode(buf);
            }
            return arr;
        }

        @Override
        public void encode(ByteBuf buf, int[] arr) {
            ByteBufCodecs.VAR_INT.encode(buf, arr.length);
            for (int v : arr) {
                ByteBufCodecs.VAR_INT.encode(buf, v);
            }
        }
    };

    public static final StreamCodec<ByteBuf, GuardianFlailBeamPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GuardianFlailBeamPacketS2C::flailId,
            INT_ARRAY, GuardianFlailBeamPacketS2C::targetIds,
            ByteBufCodecs.BOOL, GuardianFlailBeamPacketS2C::elder,
            GuardianFlailBeamPacketS2C::new
    );

    @Override
    public Type<GuardianFlailBeamPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {ClientBeamCache.put(flailId, targetIds, elder);
    }

    /**
     * 向追踪连枷实体的所有玩家发送光束目标数量
     */
    public static void send(ServerLevel level, int flailId, int[] targetIds, boolean elder) {
        Entity flail = level.getEntity(flailId);
        if (flail != null) {PacketDistributor.sendToPlayersTrackingEntity(flail,
                    new GuardianFlailBeamPacketS2C(flailId, targetIds, elder));
        } else {}
    }

    /**
     * 发送清空光束的包
     */
    public static void sendClear(ServerLevel level, int flailId, boolean elder) {
        send(level, flailId, new int[0], elder);
    }
}
