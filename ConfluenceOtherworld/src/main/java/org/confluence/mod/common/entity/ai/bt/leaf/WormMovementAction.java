package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.Objects;

/// 为可穿墙蠕虫提供不依赖原版地面导航网格的三维移动。
///
/// <p>有目标时逐 tick 把当前航向平滑转向目标并维持恒定冲刺速度；无目标时
/// 围绕前方随机选择地表上下的落点。这样头部不会因为导航器无法为墙内位置建路
/// 而静止，体节也只需继续跟随头部，无需各自参与寻路。</p>
public final class WormMovementAction extends BTNode {
    private static final int WANDER_RESELECT_TICKS = 30;
    private static final double TURN_WEIGHT = 0.14;

    private final PathfinderMob worm;
    private final Profile profile;
    private Vec3 wanderTarget;
    private int wanderTicks;

    public WormMovementAction(PathfinderMob worm, Profile profile) {
        this.worm = Objects.requireNonNull(worm, "worm");
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    @Override
    public void start() {
        wanderTarget = null;
        wanderTicks = 0;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = worm.getTarget();
        if (target != null && target.isAlive()
                && worm.getY() <= profile.maximumAttackHeight()) {
            steerTowards(target.getEyePosition(), profile.attackSpeed());
            wanderTarget = null;
            wanderTicks = 0;
            return BTStatus.RUNNING;
        }

        if (wanderTarget == null || --wanderTicks <= 0
                || worm.distanceToSqr(wanderTarget) < 4.0) {
            wanderTarget = chooseWanderTarget();
            wanderTicks = WANDER_RESELECT_TICKS;
        }
        steerTowards(wanderTarget, profile.wanderSpeed());
        return BTStatus.RUNNING;
    }

    private void steerTowards(Vec3 destination, double speed) {
        Vec3 desired = destination.subtract(worm.position());
        if (desired.lengthSqr() < 1.0E-6) {
            return;
        }
        desired = desired.normalize();
        Vec3 current = worm.getDeltaMovement();
        if (current.lengthSqr() < 1.0E-6) {
            current = worm.getLookAngle();
        }
        Vec3 direction = current.normalize()
                .scale(1.0 - TURN_WEIGHT)
                .add(desired.scale(TURN_WEIGHT))
                .normalize();
        worm.setDeltaMovement(direction.scale(speed));

        Vec3 lookPoint = worm.position().add(direction.scale(8.0));
        worm.getLookControl().setLookAt(
                lookPoint.x, lookPoint.y, lookPoint.z, 10.0F, 30.0F);
        float yaw = (float) (Mth.atan2(direction.z, direction.x)
                * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) (-(Mth.atan2(
                direction.y,
                Math.sqrt(direction.x * direction.x
                        + direction.z * direction.z))
                * Mth.RAD_TO_DEG));
        worm.setYRot(Mth.rotLerp(0.2F, worm.getYRot(), yaw));
        worm.setXRot(Mth.rotLerp(0.2F, worm.getXRot(), pitch));
        worm.setYBodyRot(worm.getYRot());
    }

    private Vec3 chooseWanderTarget() {
        Vec3 forward = worm.getLookAngle().normalize().scale(10.0);
        double angle = worm.getRandom().nextDouble() * Mth.TWO_PI;
        double radius = 8.0 + worm.getRandom().nextDouble() * 12.0;
        double x = worm.getX() + forward.x + Math.cos(angle) * radius;
        double z = worm.getZ() + forward.z + Math.sin(angle) * radius;
        int surface = worm.level().getHeight(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mth.floor(x), Mth.floor(z));
        double baseY = profile.surfaceWander()
                ? surface + profile.wanderHeightOffset()
                : Math.min(surface, profile.maximumWanderHeight())
                + profile.wanderHeightOffset();
        double y = Math.max(
                worm.level().getMinBuildHeight() + 2.0,
                baseY + worm.getRandom().nextInt(9) - 3.0);
        return new Vec3(x, y, z);
    }

    /// 不同蠕虫族只声明移动边界，公共节点统一处理平滑转向和三维速度。
    public record Profile(double attackSpeed, double wanderSpeed, double maximumAttackHeight,
                          double maximumWanderHeight, double wanderHeightOffset,
                          boolean surfaceWander) {
        public Profile {
            if (attackSpeed <= 0.0 || wanderSpeed <= 0.0) {
                throw new IllegalArgumentException(
                        "Worm movement speeds must be positive");
            }
        }

        public static Profile underground() {
            return new Profile(0.4, 0.34, 50.0, 20.0, 0.0, false);
        }

        public static Profile surface() {
            return new Profile(
                    0.4, 0.34, Double.POSITIVE_INFINITY,
                    Double.POSITIVE_INFINITY, 2.0, true);
        }

        public static Profile flying() {
            return new Profile(
                    0.4, 0.34, Double.POSITIVE_INFINITY,
                    Double.POSITIVE_INFINITY, 0.0, false);
        }

        public static Profile boneSerpent() {
            return new Profile(
                    0.4, 0.34, Double.POSITIVE_INFINITY,
                    Double.POSITIVE_INFINITY, 7.0, false);
        }
    }
}
