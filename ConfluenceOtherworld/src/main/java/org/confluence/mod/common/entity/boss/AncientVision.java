package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.SweptContactAttack;

/// 幻影龙仍存活时由错误仪式召出的远古幻象，反复沿锁定的水平航向穿越目标。
public final class AncientVision extends BaseLivingBossPart<LunaticCultist> {
    private Vec3 dash = Vec3.ZERO;
    private int contactCooldown;

    public AncientVision(EntityType<? extends Monster> type, Level level) {super(type, level);}

    public void setMaster(LunaticCultist master) {bindTo(master);}

    @Override
    protected Class<LunaticCultist> getOwnerType() {return LunaticCultist.class;}

    @Override
    protected void tickPart(LunaticCultist master) {
        if (level().isClientSide) {
            for (int i = 0; i < 6; i++) {
                level().addParticle(ParticleTypes.DRAGON_BREATH, getRandomX(0.8), getRandomY(), getRandomZ(0.8), -getDeltaMovement().x * 0.2, 0.0, -getDeltaMovement().z * 0.2);
            }
            return;
        }
        LivingEntity target = master.getTarget();
        if (target == null || !target.isAlive()) return;
        if (dash.lengthSqr() < 1.0E-7 || tickCount % 40 == 0) {
            Vec3 direction = target.getEyePosition().subtract(getBoundingBox().getCenter()).multiply(1.0, 0.0, 1.0);
            dash = direction.lengthSqr() < 1.0E-7 ? Vec3.directionFromRotation(0.0F, getYRot()) : direction.normalize();
        }
        double vertical = Mth.clamp((target.getEyeY() - getY()) * 0.04, -0.2, 0.2);
        Vec3 movement = dash.scale(0.8).add(0.0, vertical, 0.0);
        setDeltaMovement(movement);
        setPos(position().add(movement));
        setYRot((float) Math.toDegrees(Math.atan2(-dash.x, dash.z)));
        if (contactCooldown > 0) {
            contactCooldown--;
            return;
        }
        for (Entity entity : SweptContactAttack.findTargets(this, 0.0D, SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity living && master.canAttack(living))) {
            if (entity.hurt(damageSources().mobAttack(master), (float) getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                contactCooldown = 20;
                break;
            }
        }
    }
}
