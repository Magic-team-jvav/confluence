package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;

/// 发射尖刺弹射物：围绕自身发射 spikeCount 个尖刺。
/// 永远返回 SUCCESS（冷却期间也返回 SUCCESS，让 BT 继续循环）。
public class ShootSpikesAction extends BTNode {
    protected final Mob mob;
    protected final int spikeCount;
    protected final float damage;
    protected final EntityType<? extends SlimeSpikeEntity> spikeType;
    protected final GameTickCooldown cooldown = new GameTickCooldown();
    protected static final int COOLDOWN_TICKS = 50;

    public ShootSpikesAction(Mob mob, int spikeCount, float damage, EntityType<? extends SlimeSpikeEntity> spikeType) {
        if (spikeCount <= 0 || !Float.isFinite(damage) || damage < 0.0F) {
            throw new IllegalArgumentException("Spike count must be positive and damage must be finite and non-negative");
        }
        this.mob = mob;
        this.spikeCount = spikeCount;
        this.damage = damage;
        this.spikeType = spikeType;
    }

    @Override
    public BTStatus execute() {
        long gameTime = mob.level().getGameTime();
        if (!cooldown.isReady(gameTime)) {
            return BTStatus.SUCCESS;
        }
        if (mob.getTarget() != null) {
            for (int i = 0; i < spikeCount; i++) {
                double angle = Math.PI * 2 * i / spikeCount;
                SlimeSpikeEntity spike = SlimeSpikeEntity.create(mob.level(), mob, spikeType, Math.cos(angle), 0.1, Math.sin(angle), 0.5f, damage);
                mob.level().addFreshEntity(spike);
            }
            cooldown.restart(gameTime, COOLDOWN_TICKS + mob.getRandom().nextInt(20));
        }
        return BTStatus.SUCCESS;
    }
}
