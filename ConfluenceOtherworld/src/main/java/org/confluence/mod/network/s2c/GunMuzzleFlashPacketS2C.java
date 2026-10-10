package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.client.light.DynamicLightEffects;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.gun.BeeGunItem;
import org.confluence.mod.common.item.gun.ManaGunItem;

public record GunMuzzleFlashPacketS2C(int shooterId) implements IPacketS2C {
    public static final Type<GunMuzzleFlashPacketS2C> TYPE = Confluence.createType("gun_muzzle_flash");
    public static final StreamCodec<RegistryFriendlyByteBuf, GunMuzzleFlashPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GunMuzzleFlashPacketS2C::shooterId, GunMuzzleFlashPacketS2C::new);

    @Override
    public Type<GunMuzzleFlashPacketS2C> type() {
        return TYPE;
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
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(shooter, new GunMuzzleFlashPacketS2C(shooter.getId()));
    }
}
