package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;

/**
 * 发射尖刺弹射物：围绕自身发射 spikeCount 个尖刺。
 * 永远返回 SUCCESS（冷却期间也返回 SUCCESS，让 BT 继续循环）。
 */
public class ShootSpikesAction extends BTNode {
    protected final Mob mob;
    protected final int spikeCount;
    protected final float damage;
    protected final EntityType<? extends SlimeSpikeEntity> spikeType;
    protected int cooldown;
    protected static final int COOLDOWN_TICKS = 50;

    public ShootSpikesAction(Mob mob, int spikeCount, float damage,
                             EntityType<? extends SlimeSpikeEntity> spikeType) {
        this.mob = mob;
        this.spikeCount = spikeCount;
        this.damage = damage;
        this.spikeType = spikeType;
    }

    @Override
    public void start() {
        cooldown = 0;
    }

    @Override
    public BTStatus execute() {
        if (cooldown > 0) {
            cooldown--;
            return BTStatus.SUCCESS;
        }
        if (mob.getTarget() != null) {
            for (int i = 0; i < spikeCount; i++) {
                double angle = Math.PI * 2 * i / spikeCount;
                SlimeSpikeEntity spike = SlimeSpikeEntity.create(
                        mob.level(), mob, spikeType,
                        Math.cos(angle), 0.1, Math.sin(angle),
                        0.5f, damage);
                mob.level().addFreshEntity(spike);
            }
            cooldown = COOLDOWN_TICKS + mob.getRandom().nextInt(20);
        }
        return BTStatus.SUCCESS;
    }
}
