package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.PirateShot;
import org.confluence.mod.common.gameevent.PirateInvasionGameEvent;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;

public class PirateRangedMonster extends BaseMonster {
    private final Profile profile;
    private int interruption;
    private int cooldown = 10;
    private int bullets;
    private int chargeReduction;
    private boolean recovering;

    public PirateRangedMonster(EntityType<? extends PirateRangedMonster> type, Level level, Profile profile) {
        super(type, level);
        this.profile = profile;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && profile == Profile.CAPTAIN) {
            PirateFlyingMonster curse = MonsterEntities.PIRATES_CURSE.get().create(level());
            if (curse != null) {
                curse.moveTo(getX(), getY(), getZ(), getYRot(), 0);
                curse.setTarget(getTarget());
                if (getTags().contains(PirateInvasionGameEvent.ENTITY_TAG))
                    curse.addTag(PirateInvasionGameEvent.ENTITY_TAG);
                level().addFreshEntity(curse);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide && profile == Profile.CAPTAIN) {
            interruption = 10;
            if (recovering || bullets < 20) {
                recovering = false;
                cooldown = 2;
            } else {
                chargeReduction = Math.min(20, chargeReduction + 3);
                cooldown = Math.max(1, cooldown - 3);
            }
        }
        return hurt;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(new BTNode() {
                    private int pathDelay;

                    @Override
                    public boolean canStart() {
                        LivingEntity target = getTarget();
                        return target != null && target.isAlive() && canAttack(target);
                    }

                    @Override
                    public BTStatus execute() {
                        LivingEntity target = getTarget();
                        if (!canStart()) {
                            getNavigation().stop();
                            return BTStatus.FAILURE;
                        }
                        if (interruption > 0 || distanceToSqr(target) > 900 || !hasLineOfSight(target)) {
                            if (interruption > 0) interruption--;
                            if (--pathDelay <= 0) {
                                pathDelay = 10;
                                getNavigation().moveTo(target, 1);
                            }
                            return BTStatus.RUNNING;
                        }
                        getNavigation().stop();
                        getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0);
                        setSpeed(0);
                        setXxa(0);
                        setZza(0);
                        faceCombatPosition(target.getEyePosition(), 10, 10);
                        if (--cooldown <= 0) {
                            boolean cannon = profile == Profile.CAPTAIN && bullets == 20;
                            PirateShot shot = (cannon ? ModEntities.PIRATE_CANNONBALL : profile == Profile.CROSSBOWER
                                    ? ModEntities.PIRATE_FLAMING_ARROW : ModEntities.PIRATE_BULLET).get().create(level());
                            if (shot != null) {
                                Vec3 origin = getEyePosition();
                                Vec3 aim = target.getEyePosition().subtract(origin);
                                double speed = cannon ? 0.8 : profile == Profile.CROSSBOWER ? 1 : 1.5;
                                Vec3 velocity = aim.normalize().scale(speed);
                                if (cannon || profile == Profile.CROSSBOWER)
                                    velocity = velocity.add(0, aim.horizontalDistance() * 0.0125 / speed, 0);
                                shot.configure(PirateRangedMonster.this, origin, velocity, (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 100);
                                if (level().addFreshEntity(shot))
                                    playSound(cannon ? SoundEvents.GENERIC_EXPLODE : SoundEvents.CROSSBOW_SHOOT, 0.8F, 1);
                            }
                            if (profile == Profile.CAPTAIN) {
                                if (cannon) {
                                    bullets = 0;
                                    cooldown = Math.max(9, 29 - chargeReduction);
                                    recovering = true;
                                } else {
                                    recovering = false;
                                    bullets++;
                                    if (bullets == 20) chargeReduction = 0;
                                    cooldown = bullets == 20 ? 32 : 3;
                                }
                            } else cooldown = profile == Profile.CROSSBOWER ? 60 : 45;
                        }
                        return BTStatus.RUNNING;
                    }

                    @Override
                    public void stop() {
                        getNavigation().stop();
                        pathDelay = 0;
                    }
                }, new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(PirateRangedMonster.this, 0.7)));
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ShotCooldown", cooldown);
        tag.putInt("BurstBullets", bullets);
        tag.putInt("ShotInterruption", interruption);
        tag.putInt("ChargeReduction", chargeReduction);
        tag.putBoolean("ShotRecovery", recovering);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        cooldown = tag.contains("ShotCooldown") ? Mth.clamp(tag.getInt("ShotCooldown"), 0, 60) : 10;
        bullets = Mth.clamp(tag.getInt("BurstBullets"), 0, 20);
        interruption = Mth.clamp(tag.getInt("ShotInterruption"), 0, 10);
        chargeReduction = Mth.clamp(tag.getInt("ChargeReduction"), 0, 20);
        recovering = tag.getBoolean("ShotRecovery");
    }

    public enum Profile {DEADEYE, CROSSBOWER, CAPTAIN}
}
