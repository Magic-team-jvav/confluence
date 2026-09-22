package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.projectile.AntlionSandBall;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class Antlion extends BaseMonster {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation SHOOT = RawAnimation.begin().thenPlay("attack.shoot");

    public Antlion(EntityType<? extends Antlion> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {}

    @Override
    public void push(double x, double y, double z) {}

    @Override
    public boolean isPushedByFluid() {
        return false;
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
                    private int cooldown = 67;
                    private int attackTick;

                    @Override
                    public BTStatus execute() {
                        getNavigation().stop();
                        LivingEntity target = getTarget();
                        if (cooldown > 0) cooldown--;
                        boolean visible = target != null && target.isAlive() && getSensing().hasLineOfSight(target);
                        if (visible) faceCombatPosition(target.getEyePosition(), 8.0F, 8.0F);
                        if (attackTick > 0) {
                            if (++attackTick == 17 && visible) shootSand(target);
                            if (attackTick >= 24) attackTick = 0;
                        } else if (cooldown == 0 && visible && target.getEyeY() >= getEyeY() && distanceToSqr(target) <= 400.0) {
                            cooldown = 67;
                            attackTick = 1;
                            swing(InteractionHand.MAIN_HAND);
                        }
                        return BTStatus.RUNNING;
                    }

                    @Override
                    public void stop() {
                        attackTick = 0;
                    }
                };
            }
        };
    }

    private void shootSand(LivingEntity target) {
        Vec3 offset = target.getEyePosition().subtract(getEyePosition());
        if (offset.y < 0.0) return;
        double horizontal = offset.horizontalDistance();
        double speed = CreatureDefinition.ProjectileOverrides.get(this, ModEntities.ANTLION_SAND_BALL.get()).speedOr(1.1F);
        if (speed <= 0) return;
        double speedSquared = speed * speed;
        double gravity = AntlionSandBall.GRAVITY * horizontal * horizontal + 2.0 * offset.y * speedSquared;
        double discriminant = speedSquared * speedSquared - AntlionSandBall.GRAVITY * gravity;
        if (discriminant < 0.0 || horizontal < 0.01) return;
        // 取低抛解；近距离的仰角趋近瞄准方向，不再近身也向头顶高抛。
        double tangent = gravity / (horizontal * (speedSquared + Math.sqrt(discriminant)));
        Vec3 direction = new Vec3(offset.x, horizontal * tangent, offset.z).normalize();
        AntlionSandBall sand = ModEntities.ANTLION_SAND_BALL.get().create(level());
        if (sand == null) return;
        sand.configure(this, getEyePosition(), direction.scale(speed), (float) getAttributeValue(Attributes.ATTACK_DAMAGE), 120);
        if (level().addFreshEntity(sand)) playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.8F);
        else sand.discard();
    }

    @Override
    public int getCurrentSwingDuration() {
        return 24;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle/Shoot", 2, state -> state.setAndContinue(swinging ? SHOOT : IDLE)));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ANTLION_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ANTLION_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ANTLION_DEATH.get();
    }

}
