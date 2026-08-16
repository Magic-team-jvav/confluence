package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.BulletEvent;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.item.gun.definition.BulletImpactEffect;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 将服务端已经确认的子弹命中位置与表现类型同步给附近客户端。
public record BulletImpactPacketS2C(double x, double y, double z, int effectId)
        implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("bullet_impact");
    public static final PortStreamCodec<ByteBuf, BulletImpactPacketS2C> STREAM_CODEC =
            PortStreamCodec.composite(
                    PortByteBufCodecs.DOUBLE, BulletImpactPacketS2C::x,
                    PortByteBufCodecs.DOUBLE, BulletImpactPacketS2C::y,
                    PortByteBufCodecs.DOUBLE, BulletImpactPacketS2C::z,
                    PortByteBufCodecs.VAR_INT, BulletImpactPacketS2C::effectId,
                    BulletImpactPacketS2C::new);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void handle(IPortPacket.Context context) {
        Player player = context.player();
        if (player != null) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(Player player) {
        PortEventHandler.postEvent(new BulletEvent.ImpactEffectEvent(
                new Vec3(x, y, z), BulletImpactEffect.byId(effectId)));
    }

    /// 仅向同维度且距离命中点六十四格内的玩家发送表现，避免无意义的全服广播。
    public static void send(BaseBulletEntity entity, Vec3 position) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        BulletImpactEffect effect = entity.getBullet().getImpactEffect();
        if (effect == BulletImpactEffect.NONE) {
            return;
        }
        Confluence.NETWORK_HANDLER.sendToPlayersNear(
                level.dimension(), null,
                position.x, position.y, position.z, 64.0D,
                new BulletImpactPacketS2C(position.x, position.y, position.z, effect.id()));
    }
}
