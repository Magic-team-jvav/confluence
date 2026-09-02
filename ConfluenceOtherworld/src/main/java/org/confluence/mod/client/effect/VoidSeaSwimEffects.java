package org.confluence.mod.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.util.VoidSeaHelper;

import static org.confluence.mod.client.util.ClientVoidSeaConstants.*;

public final class VoidSeaSwimEffects {
    // 状态
    /// 玩家上一刻是否位于虚空海内。
    private static boolean wasInVoidSea;

    private VoidSeaSwimEffects() {}

    public static void tick(LocalPlayer player) {
        if (!VoidSeaHelper.isEnd(player.level())
                || !VoidSeaHelper.isDimensionalOverlapEffect(player)) {
            wasInVoidSea = false;
            return;
        }

        boolean isInVoidSea = VoidSeaHelper.isTrigger(player);
        if (isInVoidSea != wasInVoidSea) {
            if (isInVoidSea) {
                player.playSound(ENTER_SOUND, ENTER_SOUND_VOLUME, ENTER_SOUND_PITCH);
            } else {
                player.playSound(EXIT_SOUND, EXIT_SOUND_VOLUME, EXIT_SOUND_PITCH);
            }
            spawnSurfaceParticles(player);
        }

        if (isInVoidSea
                && player.isSwimming()
                && player.getDeltaMovement().lengthSqr() >= SWIM_MOVEMENT_THRESHOLD_SQR
                && player.tickCount % SWIM_PARTICLE_INTERVAL == 0) {
            player.playSound(SWIM_SOUND, SWIM_SOUND_VOLUME, SWIM_SOUND_PITCH);
            Vec3 movement = player.getDeltaMovement();
            player.clientLevel.addParticle(SWIM_PARTICLE, player.getX(), player.getY() + player.getBbHeight() * SWIM_PARTICLE_HEIGHT_RATIO, player.getZ(), movement.x * SWIM_PARTICLE_HORIZONTAL_SPEED_MULTIPLIER, SWIM_PARTICLE_VERTICAL_SPEED, movement.z * SWIM_PARTICLE_HORIZONTAL_SPEED_MULTIPLIER);
        }

        spawnSeaPortalParticles(player);
        spawnSuspendedParticle(player);
        wasInVoidSea = isInVoidSea;
    }

    public static void reset() {
        wasInVoidSea = false;
    }

    private static void spawnSurfaceParticles(LocalPlayer player) {
        Vec3 movement = player.getDeltaMovement();
        for (int index = 0; index < SURFACE_PARTICLE_COUNT; index++) {
            double x = player.getX() + (player.getRandom().nextDouble() - SURFACE_PARTICLE_RANDOM_CENTER) * player.getBbWidth();
            double y = player.getY() + player.getBbHeight() * SURFACE_PARTICLE_HEIGHT_RATIO;
            double z = player.getZ() + (player.getRandom().nextDouble() - SURFACE_PARTICLE_RANDOM_CENTER) * player.getBbWidth();
            player.clientLevel.addParticle(SURFACE_PARTICLE, x, y, z, movement.x * SURFACE_PARTICLE_HORIZONTAL_SPEED_MULTIPLIER, SURFACE_PARTICLE_VERTICAL_SPEED, movement.z * SURFACE_PARTICLE_HORIZONTAL_SPEED_MULTIPLIER);
        }
    }

    private static void spawnSuspendedParticle(LocalPlayer player) {
        Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        float seaHeight = VoidSeaHelper.getHeight(SUSPENDED_PARTICLE_SEA_HEIGHT_PARTIAL_TICK);
        if (!VoidSeaHelper.isEnd(player.level())
                || !VoidSeaHelper.isDimensionalOverlapEffect(player)
                || cameraPosition.y >= seaHeight
                || player.getRandom().nextFloat() >= SUSPENDED_PARTICLE_CHANCE) {
            return;
        }

        double x = cameraPosition.x + Mth.nextFloat(player.getRandom(), -SUSPENDED_PARTICLE_HORIZONTAL_RANGE, SUSPENDED_PARTICLE_HORIZONTAL_RANGE);
        double y = cameraPosition.y + Mth.nextFloat(player.getRandom(), -SUSPENDED_PARTICLE_VERTICAL_RANGE, SUSPENDED_PARTICLE_VERTICAL_RANGE);
        double z = cameraPosition.z + Mth.nextFloat(player.getRandom(), -SUSPENDED_PARTICLE_HORIZONTAL_RANGE, SUSPENDED_PARTICLE_HORIZONTAL_RANGE);
        if (y < seaHeight) {
            player.clientLevel.addParticle(ModParticleTypes.VOID_SEA_SUSPENDED.get(), x, y, z, SUSPENDED_PARTICLE_SPEED.x, SUSPENDED_PARTICLE_SPEED.y, SUSPENDED_PARTICLE_SPEED.z);
        }
    }

    private static void spawnSeaPortalParticles(LocalPlayer player) {
        Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        float seaHeight = VoidSeaHelper.getHeight(SEA_PORTAL_PARTICLE_SEA_HEIGHT_PARTIAL_TICK);
        if (!VoidSeaHelper.isEnd(player.level())
                || !VoidSeaHelper.isDimensionalOverlapEffect(player)
                || Math.abs(cameraPosition.y - seaHeight) > SEA_PORTAL_PARTICLE_VISIBLE_VERTICAL_RANGE
                || player.getRandom().nextFloat() >= SEA_PORTAL_PARTICLE_CHANCE) {
            return;
        }

        for (int index = 0; index < SEA_PORTAL_PARTICLE_COUNT; index++) {
            double x = cameraPosition.x + Mth.nextFloat(player.getRandom(), -SEA_PORTAL_PARTICLE_HORIZONTAL_RANGE, SEA_PORTAL_PARTICLE_HORIZONTAL_RANGE);
            double y = seaHeight + SEA_PORTAL_PARTICLE_HEIGHT_OFFSET;
            double z = cameraPosition.z + Mth.nextFloat(player.getRandom(), -SEA_PORTAL_PARTICLE_HORIZONTAL_RANGE, SEA_PORTAL_PARTICLE_HORIZONTAL_RANGE);
            double xSpeed = Mth.nextFloat(player.getRandom(), -SEA_PORTAL_PARTICLE_HORIZONTAL_SPEED, SEA_PORTAL_PARTICLE_HORIZONTAL_SPEED);
            double zSpeed = Mth.nextFloat(player.getRandom(), -SEA_PORTAL_PARTICLE_HORIZONTAL_SPEED, SEA_PORTAL_PARTICLE_HORIZONTAL_SPEED);
            player.clientLevel.addParticle(SEA_PORTAL_PARTICLE, x, y, z, xSpeed, SEA_PORTAL_PARTICLE_VERTICAL_SPEED, zSpeed);
        }
    }
}
