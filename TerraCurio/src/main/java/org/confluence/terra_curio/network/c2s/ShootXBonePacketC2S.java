package org.confluence.terra_curio.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.entity.XBoneProjectile;
import org.confluence.terra_curio.common.init.TCEntities;

public record ShootXBonePacketC2S() implements IPacketC2S {
    public static final ShootXBonePacketC2S INSTANCE = new ShootXBonePacketC2S();
    public static final Type<ShootXBonePacketC2S> TYPE = new Type<>(TerraCurio.asResource("shoot_x_bone"));
    public static final StreamCodec<ByteBuf, ShootXBonePacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<ShootXBonePacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        long l = LibUtils.getOrCreatePersistedData(player).getLong("terra_curio:last_x_bone");
        Level level = player.level();
        long gameTime = level.getGameTime();
        if (gameTime - l > 20) {
            LibUtils.getOrCreatePersistedData(player).putLong("terra_curio:last_x_bone", gameTime);
            XBoneProjectile projectile = new XBoneProjectile(TCEntities.X_BONE.get(), level);
            projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.2F, 0);
            projectile.setOwner(player);
            level.addFreshEntity(projectile);
        }
    }
}
