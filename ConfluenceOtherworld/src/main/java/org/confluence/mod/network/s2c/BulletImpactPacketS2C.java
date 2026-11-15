package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.BulletEvent;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.item.gun.definition.BulletImpactEffect;

public record BulletImpactPacketS2C(double x, double y, double z,
                                    int effectId) implements IPacketS2C {
    public static final Type<BulletImpactPacketS2C> TYPE = Confluence.createType("bullet_impact");
    public static final StreamCodec<RegistryFriendlyByteBuf, BulletImpactPacketS2C> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE, BulletImpactPacketS2C::x,
                    ByteBufCodecs.DOUBLE, BulletImpactPacketS2C::y,
                    ByteBufCodecs.DOUBLE, BulletImpactPacketS2C::z,
                    ByteBufCodecs.VAR_INT, BulletImpactPacketS2C::effectId,
                    BulletImpactPacketS2C::new);

    @Override
    public void work(Player player) {
        NeoForge.EVENT_BUS.post(new BulletEvent.ImpactEffectEvent(new Vec3(x, y, z), BulletImpactEffect.byId(effectId)));
    }

    @Override
    public Type<BulletImpactPacketS2C> type() {
        return TYPE;
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
        PacketDistributor.sendToPlayersNear(level, null, position.x, position.y, position.z, 64.0D, new BulletImpactPacketS2C(position.x, position.y, position.z, effect.id()));
    }
}
