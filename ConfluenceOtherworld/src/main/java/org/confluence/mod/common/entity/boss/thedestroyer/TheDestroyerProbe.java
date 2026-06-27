package org.confluence.mod.common.entity.boss.thedestroyer;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.common.LibAttributes;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.entity.proj.LineProj;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class TheDestroyerProbe extends AbstractTerraBossBase implements GeoEntity, RangedAttackMob, Boss {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // 引用本体
    private TheDestroyer head;
    private static final EntityDataAccessor<Optional<UUID>> DATA_HEAD_UUID = SynchedEntityData.defineId(TheDestroyerProbe.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_HEAD_ID = SynchedEntityData.defineId(TheDestroyerProbe.class, EntityDataSerializers.INT);

    public TheDestroyerProbe(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new ProbeMoveControl(this);
        this.xpReward = 5;
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HEAD_UUID, Optional.empty());
        builder.define(DATA_HEAD_ID, 0);
    }

    public void setHead(TheDestroyer newHead) {
        this.head = newHead;
        if(newHead != null) {
            entityData.set(DATA_HEAD_UUID, Optional.of(newHead.getUUID()));
            entityData.set(DATA_HEAD_ID, newHead.getId());
        }
    }

    @Override
    public void tick() {
        super.tick();

        // 恢复引用
        if (!level().isClientSide && tickCount % 20 == 0 && head == null && entityData.get(DATA_HEAD_UUID).isPresent()) {
            Entity e = ((ServerLevel)level()).getEntity(entityData.get(DATA_HEAD_UUID).get());
            if(e instanceof TheDestroyer d) this.head = d;
        }

        // 同步本体目标
        if (this.head != null && this.head.isAlive()) {
            LivingEntity headTarget = this.head.getTarget();
            if (headTarget != null && headTarget.isAlive() && this.getTarget() != headTarget) {
                this.setTarget(headTarget);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(LibAttributes.getAttackDamage(), 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(1, new ProbeAttackGoal(this));
        this.goalSelector.addGoal(2, new ProbeRandomFlyGoal(this));
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (this.level().isClientSide) return;
        // 注意：使用 TRAIL_PROJECTILE 或者你模组里的任何激光实体
        LineProj laser = TEProjectileEntities.TRAIL_PROJECTILE.get().create(level());
        if (laser != null) {
            laser.setOwner(this);
            laser.setPos(this.getX(), this.getY() + 0.5, this.getZ());
            laser.setDamage(20.0f);
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.5D) - laser.getY();
            double d2 = target.getZ() - this.getZ();
            laser.shoot(d0, d1, d2, 1.5F, 1.0F);
            this.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 2.0F);
            this.level().addFreshEntity(laser);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (head != null) compound.putUUID("HeadUUID", head.getUUID());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HeadUUID")) getEntityData().set(DATA_HEAD_UUID, Optional.of(tag.getUUID("HeadUUID")));
    }

    // --- Inner Classes (AI) ---
    class ProbeMoveControl extends MoveControl {
        public ProbeMoveControl(TheDestroyerProbe probe) { super(probe); }
        @Override public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                Vec3 vec3 = new Vec3(this.wantedX - mob.getX(), this.wantedY - mob.getY(), this.wantedZ - mob.getZ());
                double d0 = vec3.length();
                if (d0 < mob.getBoundingBox().getSize()) {
                    this.operation = Operation.WAIT;
                    mob.setDeltaMovement(mob.getDeltaMovement().scale(0.5D));
                } else {
                    mob.setDeltaMovement(mob.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
                    Vec3 look = mob.getDeltaMovement();
                    mob.setYRot(-((float)Mth.atan2(look.x, look.z)) * (180F / (float)Math.PI));
                    mob.yBodyRot = mob.getYRot();
                }
            }
        }
    }

    class ProbeRandomFlyGoal extends Goal {
        private final TheDestroyerProbe probe;
        public ProbeRandomFlyGoal(TheDestroyerProbe probe) { this.probe = probe; this.setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() { return !this.probe.getMoveControl().hasWanted() && this.probe.getRandom().nextInt(7) == 0; }
        @Override public void tick() {
            // 优先飞向本体附近
            if (probe.head != null && probe.head.isAlive() && probe.distanceToSqr(probe.head) > 64 * 64) {
                Vec3 headPos = probe.head.position();
                this.probe.moveControl.setWantedPosition(headPos.x + (random.nextDouble()-0.5)*10, headPos.y+5, headPos.z+(random.nextDouble()-0.5)*10, 1.0);
                return;
            }
            BlockPos bp = this.probe.blockPosition().offset(random.nextInt(15)-7, random.nextInt(11)-5, random.nextInt(15)-7);
            if (probe.level().isEmptyBlock(bp)) probe.moveControl.setWantedPosition(bp.getX()+0.5, bp.getY()+0.5, bp.getZ()+0.5, 0.25);
        }
    }

    class ProbeAttackGoal extends Goal {
        private final TheDestroyerProbe probe;
        private int attackTime;
        public ProbeAttackGoal(TheDestroyerProbe probe) { this.probe = probe; this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() { return probe.getTarget() != null && probe.getTarget().isAlive(); }
        @Override public void start() { attackTime = 0; }
        @Override public void tick() {
            LivingEntity t = probe.getTarget();
            if (t != null) {
                double dist = probe.distanceToSqr(t);
                if (dist < 100) {
                    Vec3 dir = probe.position().subtract(t.position()).normalize();
                    probe.moveControl.setWantedPosition(probe.getX()+dir.x*5, probe.getY()+2, probe.getZ()+dir.z*5, 1.0);
                } else if (dist > 256) {
                    probe.moveControl.setWantedPosition(t.getX(), t.getY()+3, t.getZ(), 1.0);
                }
                probe.getLookControl().setLookAt(t, 30, 30);
                if (--attackTime <= 0) {
                    attackTime = 60;
                    probe.performRangedAttack(t, 1.0f);
                }
            }
        }
    }

    @Override public boolean isNoGravity() { return true; }
    @Override protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {}
    @Override public boolean shouldShowBossBar() { return false; }
    @Override public boolean isMainBody() { return false; }
    @Override protected BossEvent.BossBarColor getBossBarColor() { return BossEvent.BossBarColor.RED; }
    @Override public void addSkills() {}
    @Override public boolean isPushable() { return true; }
    @Override protected SoundEvent getHurtSound(@NotNull DamageSource s) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_EXPLODE.value(); }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar c) { c.add(new AnimationController<>(this, "controller", 0, e -> PlayState.CONTINUE)); }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (!super.canAttack(target)) return false;
        return !(target instanceof TheDestroyer || target instanceof TheDestroyerPart || target instanceof TheDestroyerProbe);
    }
}
