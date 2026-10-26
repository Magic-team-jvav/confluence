package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.init.item.BowItems;
import org.confluence.mod.common.item.bow.Scarebow;

/// 稻草人弓的驱离光环：玩家手持稻草人弓时，范围内的鸟妖停止攻击并朝远离该玩家的方向飞。
///
/// 只判定「谁手里拿着弓」，与箭矢无关；放下弓、换手或走出范围后立刻回落到原本的战斗/游荡分支。
public final class ScarecrowFleeAction extends BTNode {
    /// 驱离生效半径（格），由 {@link Scarebow} 统一维护，tooltip 也读同一份数值。
    public static final double SCAREBOW_RANGE = Scarebow.SCAREBOW_RANGE;
    /// 逃离时的目标速度。
    private static final double FLEE_SPEED = 0.5;
    /// 逃离加速度比例。
    private static final double FLEE_ACCELERATION = 0.5;
    /// 逃离时的速度阻力。
    private static final double FLEE_DRAG = 0.75;
    private static final float FLEE_TURN_SPEED = 20.0F;

    private final BaseMonster mob;
    private Player scarecrowHolder;

    public ScarecrowFleeAction(BaseMonster mob) {
        this.mob = mob;
    }

    /// 玩家是否手持（或副手持）稻草人弓。
    public static boolean holdsScarebow(Player player) {
        return isScarebow(player.getMainHandItem()) || isScarebow(player.getOffhandItem());
    }

    private static boolean isScarebow(ItemStack stack) {
        return stack.is(BowItems.SCAREBOW.get());
    }

    @Override
    public boolean canStart() {
        return findHolder() != null;
    }

    @Override
    public void start() {
        this.scarecrowHolder = findHolder();
        stopAttacking();
    }

    @Override
    public BTStatus execute() {
        Player holder = scarecrowHolder != null && isValidHolder(scarecrowHolder) ? scarecrowHolder : findHolder();
        if (holder == null) {
            this.scarecrowHolder = null;
            return BTStatus.FAILURE;
        }
        this.scarecrowHolder = holder;
        stopAttacking();
        fleeFrom(holder);
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        this.scarecrowHolder = null;
    }

    /// 停止攻击：清掉目标即可同时关掉接触伤害与战斗分支。
    private void stopAttacking() {
        if (mob.getTarget() != null) {
            mob.setTarget(null);
        }
    }

    private void fleeFrom(Player player) {
        Vec3 away = mob.position().subtract(player.position());
        away = new Vec3(away.x, 0.0, away.z);
        if (away.lengthSqr() < 1.0E-6) {
            double randomX = mob.getRandom().nextDouble() - 0.5;
            double randomZ = mob.getRandom().nextDouble() - 0.5;
            away = new Vec3(randomX, 0.0, randomZ);
            if (away.lengthSqr() < 1.0E-6) away = new Vec3(1.0, 0.0, 0.0);
        }
        Vec3 desired = away.normalize().scale(FLEE_SPEED * FLEE_ACCELERATION);
        Vec3 movement = mob.getDeltaMovement().scale(FLEE_DRAG).add(desired);
        if (movement.lengthSqr() > FLEE_SPEED * FLEE_SPEED) {
            movement = movement.normalize().scale(FLEE_SPEED);
        }
        mob.setDeltaMovement(movement.add(0.0, 0.015, 0.0));
        mob.hasImpulse = true;
        mob.faceCombatDirection(movement, FLEE_TURN_SPEED, 30.0F);
    }

    private boolean isValidHolder(Player player) {
        return player.isAlive() && !player.isSpectator()
                && holdsScarebow(player)
                && mob.distanceToSqr(player) <= SCAREBOW_RANGE * SCAREBOW_RANGE;
    }

    private Player findHolder() {
        return mob.level().getNearestPlayer(mob.getX(), mob.getY(), mob.getZ(), SCAREBOW_RANGE,
                entity -> entity instanceof Player player && isValidHolder(player));
    }
}
