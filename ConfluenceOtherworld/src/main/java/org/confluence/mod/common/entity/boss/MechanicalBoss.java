package org.confluence.mod.common.entity.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.particlestorm.mixed.IPSEntity;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public abstract class MechanicalBoss extends BaseBoss {
    private static final EntityDataAccessor<Boolean> PARALYZED = SynchedEntityData.defineId(MechanicalBoss.class, EntityDataSerializers.BOOLEAN);

    protected MechanicalBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PARALYZED, false);
    }

    public boolean isParalyzed() {
        return isAlive() && (level().isClientSide ? entityData.get(PARALYZED) : hasEffect(ModEffects.EMP));
    }

    @Override
    public void tick() {
        boolean paralyzed = isParalyzed();
        if (!level().isClientSide) entityData.set(PARALYZED, paralyzed);
        if (paralyzed) {
            setDeltaMovement(Vec3.ZERO);
            if (level().isClientSide && tickCount % 6 == 0) {
                ParticleEmitter arc = new ParticleEmitter(level(), position(), Confluence.asResource("the_destroyer_beam_lightning_1"));
                arc.attach(IPSEntity.of(this));
                MolangParticleEngine.INSTANCE.addEmitter(arc);
            }
        }
        super.tick();
    }

    @Override
    public void travel(Vec3 input) {
        if (isParalyzed()) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        super.travel(input);
    }

    @Override
    protected boolean canContactAttack(Entity target) {
        return !isParalyzed() && super.canContactAttack(target);
    }

    public float empShake(float partialTick) {
        return isParalyzed() ? (float) Math.sin((tickCount + partialTick) * 2.4F) * 4.0F : 0.0F;
    }
}
