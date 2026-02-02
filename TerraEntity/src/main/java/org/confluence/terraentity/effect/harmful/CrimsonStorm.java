package org.confluence.terraentity.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.attachment.UnSyncableAttachment;
import org.confluence.terraentity.entity.boss.hillofflesh.HillOfFlesh;
import org.confluence.terraentity.init.TEAttachments;

/**
 * 肉山范围效果，附近的敌人会向肉山方向移动。
 */
public class CrimsonStorm extends MobEffect {

    public CrimsonStorm() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        UnSyncableAttachment data = livingEntity.getData(TEAttachments.UNSYNC);
        HillOfFlesh flesh = data.getFightingHillOfFlesh();
        if(flesh == null || !flesh.isAlive()){
            // 不能直接移除，会cme
//            livingEntity.removeEffect(TEEffects.CRIMSON_STORM);
            data.setFightingHillOfFlesh(null);
            return false;
        }
        data.tickInvulnerableStorm();

        if(data.isInvulnerableStormActive()){
            return true;
        }

        doEffect(flesh, livingEntity, amplifier);

        return true;
    }

    public static void doEffect(HillOfFlesh flesh, LivingEntity livingEntity, int amplifier){

        Vec3 dir = flesh.position().subtract(livingEntity.position());
        double dist = 1 / dir.length() * 0.5f;
        dist = Math.min(dist, 0.03);
        livingEntity.addDeltaMovement(dir.normalize().scale(dist));

    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
