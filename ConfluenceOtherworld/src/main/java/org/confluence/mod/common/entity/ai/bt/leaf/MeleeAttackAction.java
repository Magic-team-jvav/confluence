package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 近战攻击：在攻击范围内时 swing + doHurtTarget，带内置冷却。
/// 永远返回 SUCCESS，保证 BT 序列不会因距离不够而跌落到 idle 分支。
public class MeleeAttackAction extends BTNode {
    protected final Mob mob;
    protected final double attackRange;
    protected final GameTickCooldown cooldown = new GameTickCooldown();
    protected static final int ATTACK_COOLDOWN = 20;

    public MeleeAttackAction(Mob mob, double attackRange) {
        this.mob = mob;
        this.attackRange = attackRange;
    }

    @Override
    public BTStatus execute() {
        long gameTime = mob.level().getGameTime();
        if (!cooldown.isReady(gameTime)) {
            return BTStatus.SUCCESS;
        }
        LivingEntity target = mob.getTarget();
        if (target != null && mob.distanceToSqr(target) <= attackRange * attackRange) {
            mob.swing(InteractionHand.MAIN_HAND);
            mob.doHurtTarget(target);
            cooldown.restart(gameTime, ATTACK_COOLDOWN);
        }
        return BTStatus.SUCCESS;
    }
}
