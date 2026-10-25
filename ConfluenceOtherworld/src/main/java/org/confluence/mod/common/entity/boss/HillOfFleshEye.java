package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/// 肉山之眼——定位在肉山周围，向环形范围内的玩家射击。
public class HillOfFleshEye extends HillOfFlesh.Part {
    /// 肉山之眼首次射击的基础等待时间，单位 tick。
    private static final int INITIAL_SHOT_BASE_TICKS = 30;
    /// 首次射击额外随机等待的范围，单位 tick。
    private static final int INITIAL_SHOT_VARIATION_TICKS = 40;
    /// 肉山之眼每轮连续射击次数。
    private static final int SHOT_BURST_COUNT = 3;
    /// 一阶段两轮射击间的基础等待时间，单位 tick。
    private static final int PHASE_ONE_SHOT_BASE_TICKS = 40;
    /// 二阶段两轮射击间的基础等待时间，单位 tick。
    private static final int PHASE_TWO_SHOT_BASE_TICKS = 25;
    /// 两轮射击间额外随机等待的范围，单位 tick。
    private static final int SHOT_VARIATION_TICKS = 20;
    /// 同一轮连续射击的间隔，单位 tick。
    private static final int BURST_SHOT_INTERVAL_TICKS = 8;


    private int shootDelay;
    private int shootBurst;

    public HillOfFleshEye(HillOfFlesh master) {
        super(master, "entity.confluence.hill_of_flesh_eye", 3.0F);
        this.shootDelay = INITIAL_SHOT_BASE_TICKS + random.nextInt(INITIAL_SHOT_VARIATION_TICKS);
    }

    @Override
    protected void tickPart(HillOfFlesh master) {
        if (level().isClientSide) return;
        if (master.isInitializing()) return;

        LivingEntity target = master.findTargetForPart(position());
        if (target != null) {
            shootDelay--;
            if (shootDelay <= 0) {
                shootBurst++;
                shootAt(master, target);
                if (shootBurst >= SHOT_BURST_COUNT) {
                    shootBurst = 0;
                    shootDelay = (master.isPhase2() ? PHASE_TWO_SHOT_BASE_TICKS : PHASE_ONE_SHOT_BASE_TICKS) + random.nextInt(SHOT_VARIATION_TICKS);
                } else {
                    shootDelay = BURST_SHOT_INTERVAL_TICKS;
                }
            }
        }
    }

    private void shootAt(HillOfFlesh master, LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        Vec3 origin = getBoundingBox().getCenter();
        Vec3 dir = target.getEyePosition().subtract(origin);
        if (dir.lengthSqr() < 0.01) return;
        HostileParticleProjectile projectile = ModEntities.HILL_FIRE_BOUND.get().create(serverLevel);
        if (projectile == null) return;
        double speed = shootBurst >= SHOT_BURST_COUNT ? 1.0 : 0.5;
        projectile.configure(master, origin, dir.normalize().scale(speed), (float) master.getAttributeValue(Attributes.ATTACK_DAMAGE), 100);
        serverLevel.addFreshEntity(projectile);
    }

}
