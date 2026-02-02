package org.confluence.terra_curio.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.terra_curio.mixin.accessor.LivingEntityAccessor;
import org.confluence.terra_curio.network.c2s.PlayerJumpPacketC2S;

import static org.confluence.terra_curio.network.c2s.PlayerJumpPacketC2S.JUMP_BY_SELF;
import static org.confluence.terra_curio.network.c2s.PlayerJumpPacketC2S.RESET_FALL_DISTANCE;

public final class PlayerClimbHandler {
    private static boolean wallJumped = false;
    private static byte climberAmount = 0;

    public static void handle(LocalPlayer localPlayer, Vec2 vector, boolean jumping) {
        if (climberAmount <= 0 || localPlayer.onGround() || (vector.x == 0.0 && vector.y == 0.0)) {
            wallJumped = true;
            return;
        }
        Vec3 motion = localPlayer.getDeltaMovement();
        double motionY = motion.y * GravitationHandler.getJumpDir();
        if (motionY > 0.0) return;
        float rad = localPlayer.getYRot() * Mth.DEG_TO_RAD;
        float cos = Mth.cos(rad);
        float sin = Mth.sin(rad);
        double x = vector.x * cos + vector.y * -sin;
        double z = vector.x * sin + vector.y * cos;

        if (hasMotionToWall(localPlayer, x, z)) {
            if (jumping) {
                if (!wallJumped) {
                    wallJump(localPlayer, x, z);
                    wallJumped = true;
                    return;
                }
            } else {
                wallJumped = false;
            }
            if (localPlayer.isShiftKeyDown()) {
                motionY = -0.1;
            } else if (climberAmount == 1) {
                motionY = -0.05;
            } else if (climberAmount >= 2) {
                motionY = 0.0;
            } else {
                return;
            }
            motionY *= GravitationHandler.getJumpDir();
            localPlayer.hasImpulse = true;
            localPlayer.fallDistance = 0.0F;
            localPlayer.setDeltaMovement(motion.x * 0.93, motionY, motion.z * 0.93);
            PlayerJumpHandler.reset(true);
            PacketDistributor.sendToServer(new PlayerJumpPacketC2S(RESET_FALL_DISTANCE, (float) motionY));
        }
    }

    public static void reset() {
        wallJumped = false;
        climberAmount = 0;
    }

    private static boolean hasMotionToWall(LocalPlayer localPlayer, double x, double z) {
        AABB aabb = localPlayer.getDimensions(localPlayer.getPose()).makeBoundingBox(localPlayer.position());
        double u = x * 0.1;
        double v = z * 0.1;
        return !localPlayer.level().noCollision(new AABB(
                aabb.minX - 0.01 + u,
                localPlayer.getEyeY(),
                aabb.minZ - 0.01 + v,
                aabb.maxX + 0.01 + u,
                aabb.maxY,
                aabb.maxZ + 0.01 + v
        ));
    }

    private static void wallJump(LocalPlayer localPlayer, double x, double z) {
        double motionY = ((LivingEntityAccessor) localPlayer).callGetJumpPower(GravitationHandler.getJumpDir()) * 1.1;
        Vec3 vec3 = localPlayer.getDeltaMovement();
        localPlayer.setDeltaMovement(vec3.add(vec3.x - x * 0.11, motionY, vec3.z - z * 0.11));
        localPlayer.hasImpulse = true;
        PacketDistributor.sendToServer(new PlayerJumpPacketC2S(JUMP_BY_SELF, (float) motionY));
    }

    public static void handlePacket(byte climberAmount) {
        PlayerClimbHandler.climberAmount = climberAmount;
    }
}
