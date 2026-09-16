package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.projectile.FrostMonsterProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/**
 * 冰雪鱼人和冰雪巨人共用陆行射击调度，导航与射击不会同时争夺朝向。
 */
public final class FrostFighter extends BaseMonster {
    private final Kind kind;
    private int shotCooldown;

    public FrostFighter(EntityType<? extends FrostFighter> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
        shotCooldown = 40;
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
                return SelectorNode.of(new CombatAction(), new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(FrostFighter.this, 0.7)));
            }
        };
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide && kind == Kind.GOLEM && random.nextInt(3) == 0)
            shotCooldown += random.nextInt(11);
        return damaged;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (kind == Kind.MERMAN) return;
        RawAnimation walk = RawAnimation.begin().thenLoop("move.walk");
        controllers.add(new AnimationController<>(this, "Movement", 4, state -> state.isMoving() ? state.setAndContinue(walk) : PlayState.STOP));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ShotCooldown", shotCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        shotCooldown = tag.contains("ShotCooldown") ? Math.max(0, tag.getInt("ShotCooldown")) : 40;
    }

    private final class CombatAction extends BTNode {
        private int pathCooldown;

        @Override
        public boolean canStart() {
            LivingEntity target = getTarget();
            return target != null && target.isAlive() && canAttack(target);
        }

        @Override
        public BTStatus execute() {
            if (!canStart()) return BTStatus.FAILURE;
            LivingEntity target = getTarget();
            boolean visible = hasLineOfSight(target);
            double healthRatio = getHealth() / getMaxHealth();
            if (kind == Kind.MERMAN && visible) {
                getNavigation().stop();
                getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0.0);
                setSpeed(0.0F);
                setXxa(0.0F);
                setZza(0.0F);
                getLookControl().setLookAt(target, 30.0F, 30.0F);
            } else if (--pathCooldown <= 0) {
                getNavigation().moveTo(target, kind == Kind.GOLEM ? 0.6 + Math.pow(1.0 - healthRatio, 2.0) * 1.4 : 1.0);
                pathCooldown = 10;
            }
            if (--shotCooldown <= 0) {
                boolean canFire = visible && (kind != Kind.GOLEM || onGround() && !target.hasEffect(ModEffects.FROZEN.get()));
                if (canFire) {
                    FrostMonsterProjectile projectile = (kind == Kind.GOLEM ? ModEntities.FROST_BEAM : ModEntities.ICEWATER_SPIT).get().create(level());
                    if (projectile != null) {
                        float multiplier = kind == Kind.GOLEM ? 64.0F / 60.0F : 74.0F / 60.0F;
                        projectile.configure(FrostFighter.this, target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier, kind == Kind.GOLEM ? 2.5F : 0.8F, 0.0F, 100);
                        if (level().addFreshEntity(projectile))
                            playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.8F);
                    }
                }
                shotCooldown = kind == Kind.GOLEM ? (int) (10 + 10 * healthRatio + random.nextDouble() * 290 * healthRatio) : 35;
            }
            return BTStatus.RUNNING;
        }

        @Override
        public void stop() {
            getNavigation().stop();
            pathCooldown = 0;
        }
    }

    public enum Kind {
        MERMAN,
        GOLEM
    }
}
