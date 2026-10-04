package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.ai.NPCCombatActions;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;

public final class BurstGunNPC extends BaseNPC {
    private LivingEntity burstTarget;
    private NPCCombatProfile.Values burstValues;
    private int shots;
    private int delay;

    public BurstGunNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile profile) {
        super(type, level, profile);
    }

    public void startBurst(LivingEntity target, NPCCombatProfile.Values values) {
        burstTarget = target;
        burstValues = values;
        NPCCombatActions.BULLET.perform(this, target, values);
        shots = 2;
        delay = 3;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || shots == 0) return;
        if (!isAlive() || isNoAi() || burstTarget == null || !burstTarget.isAlive() || getTarget() != burstTarget
                || !getSensing().hasLineOfSight(burstTarget) || distanceToSqr(burstTarget) > burstValues.attackRange() * burstValues.attackRange()) {
            shots = 0;
            burstTarget = null;
        } else if (--delay <= 0) {
            NPCCombatActions.BULLET.perform(this, burstTarget, burstValues);
            shots--;
            delay = 3;
        }
    }
}
