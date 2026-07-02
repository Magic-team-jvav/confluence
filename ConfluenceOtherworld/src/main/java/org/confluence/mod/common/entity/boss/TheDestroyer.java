package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 毁灭者——巨型蠕虫 Boss，体节更比世界吞噬者更长。周期性释放探测器。
 */
public class TheDestroyer extends BaseWormBoss {
    private static final int SEGMENT_COUNT = 24;
    private static final int PROBE_COOLDOWN = 250;
    private int probeTimer = PROBE_COOLDOWN;

    public TheDestroyer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 2000;
    }

    @Override
    protected int getSegmentCount() {
        return SEGMENT_COUNT;
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createWormBossAttributes()
                .add(Attributes.MAX_HEALTH, 2000.0)
                .add(Attributes.ATTACK_DAMAGE, 30.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (getTarget() == null && tickCount % 30 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }

            // Spawn probes periodically
            if (getTarget() != null) {
                probeTimer--;
                if (probeTimer <= 0) {
                    probeTimer = PROBE_COOLDOWN + random.nextInt(80);
                    spawnProbes();
                }
            }
        }
    }

    private void spawnProbes() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        int count = isExpert() ? 4 : 2;
        for (int i = 0; i < count; i++) {
            Entity probe = org.confluence.mod.common.init.entity.BossEntities.SERVANT_OF_CTHULHU.get().create(level());
            if (probe instanceof Mob mob) {
                mob.setPos(
                        getX() + (random.nextFloat() - 0.5) * 4,
                        getY() + 2 + random.nextFloat() * 3,
                        getZ() + (random.nextFloat() - 0.5) * 4
                );
                if (getTarget() != null) mob.setTarget(getTarget());
                serverLevel.addFreshEntity(mob);
            }
        }
    }
}
