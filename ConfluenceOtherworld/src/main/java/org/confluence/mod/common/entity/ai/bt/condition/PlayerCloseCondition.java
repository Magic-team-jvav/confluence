package org.confluence.mod.common.entity.ai.bt.condition;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 附近是否有玩家（用于小动物的逃跑触发）
 */
public class PlayerCloseCondition extends Condition<Mob> {
    private final double range;

    public PlayerCloseCondition(Mob mob, double range) {
        super(mob);
        this.range = range;
    }

    @Override
    protected boolean test() {
        AABB box = mob.getBoundingBox().inflate(range);
        List<Player> players = mob.level().getEntitiesOfClass(Player.class, box, p -> !p.isSpectator() && !p.isCreative());
        return !players.isEmpty();
    }
}
