package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.confluence.lib.api.projectile.ProjectileFireTrigger;
import org.confluence.lib.api.projectile.ServerProjectileFireService;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Objects;

/// 玩家主动武器弹幕的统一客户端意图包。
///
/// <p>负载严格固定为手和触发方式两个有限枚举，各占一个字节。包中不存在动作 ID、实体类型、
/// 伤害、速度、弹数、资源数量、目标坐标或客户端冷却；服务端收到后始终从对应手重新读取当前
/// 物品，并交给 MagicLib 的唯一发射事务入口解析。</p>
public record ProjectileFireIntentPacketC2S(
        InteractionHand hand,
        ProjectileFireTrigger trigger
) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("projectile_fire_intent");
    private static final InteractionHand[] HANDS = InteractionHand.values();
    private static final ProjectileFireTrigger[] TRIGGERS = ProjectileFireTrigger.values();
    private static final PortStreamCodec<ByteBuf, InteractionHand> HAND_CODEC = PortByteBufCodecs.BYTE.map(
            ProjectileFireIntentPacketC2S::decodeHand,
            value -> (byte) value.ordinal()
    );
    private static final PortStreamCodec<ByteBuf, ProjectileFireTrigger> TRIGGER_CODEC =
            PortByteBufCodecs.BYTE.map(
                    ProjectileFireIntentPacketC2S::decodeTrigger,
                    value -> (byte) value.ordinal()
            );
    public static final PortStreamCodec<ByteBuf, ProjectileFireIntentPacketC2S> STREAM_CODEC =
            PortStreamCodec.composite(
                    HAND_CODEC, ProjectileFireIntentPacketC2S::hand,
                    TRIGGER_CODEC, ProjectileFireIntentPacketC2S::trigger,
                    ProjectileFireIntentPacketC2S::new
            );

    public ProjectileFireIntentPacketC2S {
        Objects.requireNonNull(hand, "Interaction hand must not be null");
        Objects.requireNonNull(trigger, "Projectile fire trigger must not be null");
    }

    /// 发射事务会读取并修改服务端玩家的物品栏、冷却和世界实体，因此这个数据包必须显式切回
    /// 服务端主线程。PortLib 的默认数据包接口只负责转发，不替所有数据包猜测线程需求。
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    /// 仅转交服务端权威事务，不在网络层复制任何武器玩法。
    @Override
    public void work(ServerPlayer player) {
        ServerProjectileFireService.fire(player, hand, trigger);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 从客户端发送一个不含数值与目标信息的固定意图。
    public static void sendToServer(InteractionHand hand, ProjectileFireTrigger trigger) {
        Confluence.NETWORK_HANDLER.sendToServer(new ProjectileFireIntentPacketC2S(hand, trigger));
    }

    private static InteractionHand decodeHand(Byte encoded) {
        int id = Byte.toUnsignedInt(encoded);
        if (id >= HANDS.length) {
            throw new IllegalArgumentException("Unknown interaction hand id: " + id);
        }
        return HANDS[id];
    }

    private static ProjectileFireTrigger decodeTrigger(Byte encoded) {
        int id = Byte.toUnsignedInt(encoded);
        if (id >= TRIGGERS.length) {
            throw new IllegalArgumentException("Unknown projectile fire trigger id: " + id);
        }
        return TRIGGERS[id];
    }
}
