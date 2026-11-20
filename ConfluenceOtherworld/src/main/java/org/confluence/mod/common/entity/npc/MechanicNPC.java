package org.confluence.mod.common.entity.npc;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.entity.boss.DestroyerPart;
import org.confluence.mod.common.entity.boss.MechanicalBoss;
import org.confluence.mod.common.entity.boss.SkeletronPrimeArm;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.network.s2c.EmpPulsePacketS2C;
import org.mesdag.portlib.network.PortPacketDistributor;

import java.util.HashSet;
import java.util.Set;

/// 未获救时留在地牢中的机械师。
public class MechanicNPC extends BaseNPC {
    // 两轮脉冲之间留出完整的攻击窗口，单位 tick。
    private static final int EMP_INTERVAL = 200;
    // 电磁脉冲的球形作用半径，单位为格。
    private static final double EMP_RADIUS = 63.75;
    private static final int MIN_EMP_TICKS = 20, MAX_EMP_TICKS = 60;

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (shouldInteract() || tickCount % EMP_INTERVAL != 0) return;
        Set<MechanicalBoss> targets = new HashSet<>();
        for (Entity entity : level().getEntities((Entity) null, getBoundingBox().inflate(EMP_RADIUS),
                entity -> entity instanceof MechanicalBoss || entity instanceof DestroyerPart || entity instanceof SkeletronPrimeArm)) {
            if (distanceToSqr(entity) > EMP_RADIUS * EMP_RADIUS) continue;
            if (entity instanceof MechanicalBoss boss) targets.add(boss);
            if (entity instanceof DestroyerPart part) targets.add(part.getParent());
            if (entity instanceof SkeletronPrimeArm arm && arm.getOwner() != null)
                targets.add(arm.getOwner());
        }
        targets.removeIf(boss -> !boss.isAlive() || boss.getTarget() == null);
        if (targets.isEmpty()) return;
        PortPacketDistributor.sendToPlayersTrackingEntity(this, new EmpPulsePacketS2C(getX(), getY(), getZ(), (float) EMP_RADIUS));
        for (MechanicalBoss boss : targets) {
            boss.addEffect(new MobEffectInstance(ModEffects.EMP.get(), random.nextIntBetweenInclusive(MIN_EMP_TICKS, MAX_EMP_TICKS)), this);
        }
    }

    public MechanicNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return shouldInteract();
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        if (isRemoved()) {
            NPCSpawner.INSTANCE.forgetNPC(this);
        }
    }
}
