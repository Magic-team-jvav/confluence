package org.confluence.terra_curio.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.terra_curio.client.TCKeyBindings;
import org.confluence.terra_curio.mixed.IEntity;
import org.confluence.terra_curio.mixin.client.accessor.LocalPlayerAccessor;
import org.confluence.terra_curio.network.c2s.GravitationPacketC2S;
import org.confluence.terra_curio.network.s2c.BroadcastGravitationRotPacketS2C;

public final class GravitationHandler {
    public static final Vec3 DOWN = new Vec3(0.0, -0.3000001, 0.0);
    private static boolean keyDown = false;
    private static boolean shouldRot = false;
    static boolean hasGlobe = false;

    public static void handle(LocalPlayer player) {
        if (StepStoolHandler.onStool()) return;

        if (TCKeyBindings.FLIP_GRAVITATION.get().isDown()) {
            if (!keyDown) {
                shouldRot = !shouldRot;
                player.resetFallDistance();
                PacketDistributor.sendToServer(new GravitationPacketC2S(shouldRot));
            }
            keyDown = true;
        } else {
            keyDown = false;
        }
    }

    public static void force(LocalPlayer player) {
        if (StepStoolHandler.onStool() || player.getAbilities().flying) return;

        if (!shouldRot) {
            shouldRot = true;
            player.resetFallDistance();
            PacketDistributor.sendToServer(new GravitationPacketC2S(true));
        }
    }

    public static void expire() {
        if (shouldRot) {
            shouldRot = false;
            PacketDistributor.sendToServer(new GravitationPacketC2S(false));
        }
    }

    public static boolean isShouldRot() {
        return shouldRot;
    }

    public static void tryExpire(LocalPlayer player) {
        if (player.getY() > player.level().getMaxBuildHeight()) {
            expire();
        }
    }

    public static void reset() {
        shouldRot = false;
        hasGlobe = false;
    }

    public static void unCrouching(Player player) {
        if (shouldRot && player.onGround() && player.isCrouching() && !player.isShiftKeyDown()) {
            player.move(MoverType.SELF, DOWN);
            player.setPose(Pose.STANDING);
            ((LocalPlayerAccessor) player).setCrouching(false);
        }
    }

    public static boolean isHasGlobe() {
        return hasGlobe;
    }

    public static void handleRemoteRot(BroadcastGravitationRotPacketS2C packet, Player player) {
        Entity entity = player.level().getEntity(packet.entityId());
        if (entity != null) {
            IEntity.of(entity).terra_curio$setShouldRot(packet.enabled());
        }
    }

    public static boolean isShouldRot(Entity entity) {
        IEntity iEntity = IEntity.of(entity);
        return iEntity.terra_curio$isPlayer() && (((Player) entity).isLocalPlayer() ? isShouldRot() : iEntity.terra_curio$isShouldRot());
    }

    public static float getJumpDir() {
        return isShouldRot() ? -1.0F : 1.0F;
    }
}
