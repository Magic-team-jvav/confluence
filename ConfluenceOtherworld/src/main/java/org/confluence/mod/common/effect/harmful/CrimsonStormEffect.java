package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.attachment.UnSyncableAttachment;
import org.confluence.terraentity.entity.boss.hillofflesh.HillOfFlesh;
import org.confluence.terraentity.init.TEAttachments;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

// TODO: 移植 HillOfFlesh / TEAttachments 后移除 terraentity 依赖
public class CrimsonStormEffect extends PortMobEffect {

    public CrimsonStormEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        UnSyncableAttachment data = living.getData(TEAttachments.UNSYNC);
        HillOfFlesh flesh = data.getFightingHillOfFlesh();
        if (flesh == null || !flesh.isAlive()) {
            data.setFightingHillOfFlesh(null);
            return;
        }
        data.tickInvulnerableStorm();

        if (data.isInvulnerableStormActive()) {
            return;
        }

        Vec3 dir = flesh.position().subtract(living.position());
        double dist = 1 / dir.length() * 0.5f;
        dist = Math.min(dist, 0.03);
        living.addDeltaMovement(dir.normalize().scale(dist));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
