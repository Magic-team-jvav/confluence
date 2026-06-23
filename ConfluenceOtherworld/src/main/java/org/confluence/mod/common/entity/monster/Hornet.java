package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * 丛林黄蜂
 */
public class Hornet extends AbstractMonster implements FlyingAnimal{

    protected  int attackInternal = 20;
    public Hornet(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
        this.moveControl = new FlyingMoveControl(this, 20, true);

    }

    @Override
    protected void registerGoals() {
//        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2, true));
        this.goalSelector.addGoal(1, new BeeShootGoal(this));
        this.goalSelector.addGoal(2, new BeeKeepOnTargetGoal(this));
        this.goalSelector.addGoal(8, new BeeWanderGoal());
        this.goalSelector.addGoal(9, new FloatGoal(this));

        registerTargetGoal(this.targetSelector);
    }

    @Override
    protected void registerTargetGoal(GoalSelector targetSelector){
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_STRIKE));
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }


    @Override
    public boolean isFlying() {
        return true;
    }


    // move control

    protected class BeeWanderGoal extends Goal {
        private static final int WANDER_THRESHOLD = 22;

        BeeWanderGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return Hornet.this.getTarget() == null &&  Hornet.this.navigation.isDone() && Hornet.this.random.nextInt(10) == 0;
        }

        public boolean canContinueToUse() {
            return Hornet.this.navigation.isInProgress();
        }

        public void start() {
            Vec3 vec3 = this.findPos();
            if (vec3 != null) {
                Hornet.this.navigation.moveTo(Hornet.this.navigation.createPath(BlockPos.containing(vec3), 1), 1.0);
            }

        }

        @Nullable
        private Vec3 findPos() {
            Vec3 vec3= Hornet.this.getViewVector(0.0F);

            Vec3 vec32 = HoverRandomPos.getPos(Hornet.this, 8, 7, vec3.x, vec3.z, 1.5707964F, 3, 1);
            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(Hornet.this, 8, 4, -2, vec3.x, vec3.z, 1.5707963705062866);
        }
    }

    protected class BeeKeepOnTargetGoal extends Goal {
        private final int FIND_PATH_TIME = 200;
        int timeToRepath;
        Hornet bee;
//        Vec3 targetPos;

        public BeeKeepOnTargetGoal(Hornet bee) {
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.bee = bee;
        }

        public boolean canUse() {

            return bee.getTarget() != null && bee.getTarget().isAlive() && (--timeToRepath <= 0 || timeToRepath < FIND_PATH_TIME - attackInternal);
        }

        public boolean canContinueToUse() {
            return bee.getTarget() != null && bee.getTarget().isAlive() && bee.getNavigation().getPath() != null && bee.getNavigation().getPath().getDistToTarget() > 1.0F;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void start() {
            Vec3 vec3 = this.findPos();
            if (vec3 != null) {
                bee.swing(InteractionHand.MAIN_HAND);
                timeToRepath = FIND_PATH_TIME;
                bee.navigation.moveTo(bee.navigation.createPath(BlockPos.containing(vec3), 1), 1.5f);
//                System.out.println("moving");
            }
        }

        @Nullable
        private Vec3 findPos() {
            Vec3 vec3= Hornet.this.getViewVector(0.0F);

            Vec3 vec32 = HoverRandomPos.getPos(Hornet.this, 8, 7, vec3.x, vec3.z, 1.5707964F, 6, 3);
            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(Hornet.this, 8, 4, -2, vec3.x, vec3.z, 1.5707963705062866);
        }

        public void tick() {
            if(bee.getTarget() != null && bee.getTarget().isAlive()) {
                bee.lookControl.setLookAt(bee.getTarget());
                bee.lookAt(bee.getTarget(), 360, 360);
//                if (bee.distanceTo(bee.getTarget()) < 10) {
//
//                }
            }
        }
    }

    protected class BeeShootGoal extends Goal {
        protected int SHOOT_TIME;
        protected int timeToShoot;
        protected int prepareTime = 5;
        protected Hornet bee;
        float inaccuracy;

        public BeeShootGoal(Hornet bee) {
            this(bee, 5.0F, 25);
        }

        public BeeShootGoal(Hornet bee, float inaccuracy, int shootTime) {
            this.bee = bee;
            this.inaccuracy = inaccuracy;
            this.SHOOT_TIME = shootTime;
        }

        public boolean canUse() {
            return bee.getTarget() != null && bee.getTarget().isAlive() && --timeToShoot <= 0;
        }

        public boolean canContinueToUse() {
            boolean hasTarget = bee.getTarget() != null && bee.getTarget().isAlive();
            if(!hasTarget) return false;
            boolean can = canShoot(bee.getTarget());
            return !can;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void start() {
            bee.getNavigation().stop();

        }

        public void tick() {
            if(bee.getTarget() != null && bee.getTarget().isAlive()) {

                bee.lookAt(bee.getTarget(), 10,89);
                bee.lookControl.setLookAt(bee.getTarget());
            }
        }

        @Override
        public void stop() {
            if(bee.getTarget() != null){
                bee.swing(InteractionHand.MAIN_HAND);
                LineProj proj = createProj();
                if (proj != null) {
                    proj.setOwner(bee);
                    proj.setPos(bee.position());
                    proj.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                    double x = bee.getTarget().getX() - bee.getX();
                    double y = bee.getTarget().getY() + bee.getTarget().getEyeHeight() * 0.5f - bee.getY();
                    double z = bee.getTarget().getZ() - bee.getZ();
                    proj.shoot(x,y,z, 1, inaccuracy);
                    level().addFreshEntity(proj);
                }
                timeToShoot = SHOOT_TIME;
            }
        }
        protected boolean canShoot(Entity target) {
            if(TEUtils.angleBetween(bee.getForward(), target.getEyePosition().subtract(bee.getEyePosition())) < 0.1f){
                return true;
            }
            return false;
        }

    }


    protected LineProj createProj(){
        return TEProjectileEntities.BEE_STICK_PROJ.get().create(level());
    }

    @Override
    protected PathNavigation createNavigation(Level p_level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_level) {
            public boolean isStableDestination(BlockPos p_27947_) {
                return !this.level.getBlockState(p_27947_.below()).isAir();
            }

            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    void pathfindRandomlyTowards(BlockPos pos) {
        Vec3 vec3 = Vec3.atBottomCenterOf(pos);
        int i = 0;
        BlockPos blockpos = this.blockPosition();
        int j = (int)vec3.y - blockpos.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }

        int k = 6;
        int l = 8;
        int i1 = blockpos.distManhattan(pos);
        if (i1 < 15) {
            k = i1 / 2;
            l = i1 / 2;
        }

        Vec3 vec31 = AirRandomPos.getPosTowards(this, k, l, i, vec3, 0.3141592741012573);
        if (vec31 != null) {
            this.navigation.setMaxVisitedNodesMultiplier(0.5F);
            this.navigation.moveTo(vec31.x, vec31.y, vec31.z, 1.0);
        }

    }


    // attack

    @Override
    public boolean doHurtTarget(Entity entity) {
        DamageSource damagesource = this.damageSources().sting(this);
        this.swing(InteractionHand.MAIN_HAND);
        boolean flag = entity.hurt(damagesource, (float)((int)this.getAttributeValue(LibAttributes.getAttackDamage())));
        if (flag) {
            Level var5 = this.level();
            if (var5 instanceof ServerLevel) {
                ServerLevel serverlevel = (ServerLevel)var5;
                EnchantmentHelper.doPostAttackEffects(serverlevel, entity, damagesource);
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;
                livingentity.setStingerCount(livingentity.getStingerCount() + 1);
                int i = 0;
                if (this.level().getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.level().getDifficulty() == Difficulty.HARD) {
                    i = 18;
                }

                if (i > 0) {
                    livingentity.addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0), this);
                }
            }
            this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
        }
        return flag;
    }
}
