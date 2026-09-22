package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.ModSoundEvents;

public class GoblinWarlock extends BaseMonster {
    private int casting;
    private int flying;
    private Vec3 flightDestination = Vec3.ZERO;

    public GoblinWarlock(EntityType<? extends GoblinWarlock> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    private int pathDelay;

                    @Override
                    public BTStatus execute() {
                        LivingEntity target = getTarget();
                        if (target == null || !target.isAlive() || !canAttack(target)) {
                            stop();
                            return BTStatus.FAILURE;
                        }
                        if (flying > 0) {
                            getNavigation().stop();
                            setNoGravity(true);
                            Vec3 offset = flightDestination.subtract(position());
                            setDeltaMovement(getDeltaMovement().lerp(offset.normalize().scale(Math.min(0.55, offset.length() * 0.1)), 0.12));
                            faceCombatPosition(target.getEyePosition(), 10, 10);
                            if (flying % 20 == 0) {
                                HostileParticleProjectile ball = ModEntities.CHAOS_BALL_PROJECTILE.get().create(level());
                                if (ball != null) {
                                    float damage = LibUtils.isMaster(level(), blockPosition()) ? 63 : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 42 : 21;
                                    damage *= (float) getAttributeValue(Attributes.ATTACK_DAMAGE) / 42.0F;
                                    ball.configure(GoblinWarlock.this, target, damage);
                                    if (level().addFreshEntity(ball))
                                        playSound(SoundEvents.EVOKER_CAST_SPELL, 1, 0.8F);
                                }
                            }
                            if (--flying == 0) setNoGravity(false);
                        } else if (onGround() && distanceToSqr(target) < 144 && hasLineOfSight(target)) {
                            getNavigation().stop();
                            faceCombatPosition(target.getEyePosition(), 10, 10);
                            if (++casting % 12 == 0) {
                                ShadowflameApparition apparition = MonsterEntities.SHADOWFLAME_APPARITION.get().create(level());
                                if (apparition != null) {
                                    apparition.moveTo(getX(), getEyeY(), getZ(), getYRot(), 0);
                                    apparition.setTarget(target);
                                    if (level().addFreshEntity(apparition))
                                        playSound(SoundEvents.EVOKER_CAST_SPELL, 0.7F, 1.2F);
                                }
                            }
                            if (casting >= 36) {
                                casting = 0;
                                flying = 80;
                                double angle = random.nextDouble() * Math.PI * 2;
                                flightDestination = target.position().add(Math.cos(angle) * 8, 5, Math.sin(angle) * 8);
                            }
                        } else {
                            casting = 0;
                            if (--pathDelay <= 0) {
                                pathDelay = 10;
                                getNavigation().moveTo(target, 1.2);
                            }
                        }
                        return BTStatus.RUNNING;
                    }

                    @Override
                    public void stop() {
                        getNavigation().stop();
                        setNoGravity(false);
                        casting = flying = pathDelay = 0;
                    }
                };
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CastingTicks", casting);
        tag.putInt("FlyingTicks", flying);
        tag.putDouble("FlightX", flightDestination.x);
        tag.putDouble("FlightY", flightDestination.y);
        tag.putDouble("FlightZ", flightDestination.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        casting = Mth.clamp(tag.getInt("CastingTicks"), 0, 35);
        flying = Mth.clamp(tag.getInt("FlyingTicks"), 0, 80);
        if (tag.contains("FlightX") && tag.contains("FlightY") && tag.contains("FlightZ")) {
            flightDestination = new Vec3(tag.getDouble("FlightX"), tag.getDouble("FlightY"), tag.getDouble("FlightZ"));
        } else {
            flightDestination = position();
            flying = 0;
        }
        setNoGravity(flying > 0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.GOBLIN_WARLOCK_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.GOBLIN_WARLOCK_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.GOBLIN_WARLOCK_DEATH.get();
    }

}
