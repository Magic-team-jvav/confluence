package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.light.DynamicLightEffects;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.gun.BeeGunItem;
import org.confluence.mod.common.item.gun.ManaGunItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record GunMuzzleFlashPacketS2C(int shooterId) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("gun_muzzle_flash");
    public static final PortStreamCodec<ByteBuf, GunMuzzleFlashPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, GunMuzzleFlashPacketS2C::shooterId, GunMuzzleFlashPacketS2C::new);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        if (!(player.level().getEntity(shooterId) instanceof Player shooter)
                || !(shooter.getMainHandItem().getItem() instanceof BaseGun gun)
                || gun instanceof BeeGunItem) return;
        Vec3 position = shooter.getEyePosition().add(shooter.getLookAngle().scale(0.8));
        DynamicLightEffects.flash(position, gun instanceof ManaGunItem ? 8 : 10, 2);
    }

    public static void send(ServerPlayer shooter) {
        PortPacketDistributor.sendToPlayersTrackingEntityAndSelf(shooter, new GunMuzzleFlashPacketS2C(shooter.getId()));
    }
}
