package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.util.OverworldUtils;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/// 具有红、蓝两种外观并会中距离跃击的龙虾。
public final class Crawdad extends BaseWarriorMonster {
    private static final String VARIANT_TAG = "Variant";
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Crawdad.class, EntityDataSerializers.INT);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final int CLAW_ATTACK_TICKS = 20;
    private static final int CLAW_WINDUP_TICKS = 5;
    private static final double CLAW_REACH = 0.75;

    private boolean variantInitialized;
    private int clawAttackTicks;

    public Crawdad(EntityType<? extends Crawdad> type, Level level) {
        super(type, level);
    }

    @Override
    protected JumpProfile jumpProfile() {
        return new JumpProfile(4.0, 2.0, 60, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(VARIANT, 0);
    }

    @Override
    public void onAddedToWorld() {
        if (!variantInitialized && !level().isClientSide) {
            setVariant(random.nextInt(2));
            variantInitialized = true;
        }
        super.onAddedToWorld();
    }

    public int getVariant() {
        return entityData.get(VARIANT);
    }

    private void setVariant(int variant) {
        entityData.set(VARIANT, Math.floorMod(variant, 2));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || clawAttackTicks <= 0) return;
        getNavigation().stop();
        Vec3 movement = getDeltaMovement();
        setDeltaMovement(0.0, movement.y, 0.0);
        if (--clawAttackTicks == CLAW_ATTACK_TICKS - CLAW_WINDUP_TICKS) performClawHit();
    }

    /// 龙虾贴身且站稳时先停下挥螯；地表白天不会发动这一强化攻击。
    @Override
    public boolean doHurtTarget(Entity target) {
        if (!canUseClawAttack()) return super.doHurtTarget(target);
        if (clawAttackTicks == 0) {
            clawAttackTicks = CLAW_ATTACK_TICKS;
            swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
        }
        return false;
    }

    private boolean canUseClawAttack() {
        return onGround() && Math.abs(getDeltaMovement().y) < 0.01
                && (getY() < OverworldUtils.getSurfaceY() || level().isNight());
    }

    private void performClawHit() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !canAttack(target) || !hasLineOfSight(target) || !getBoundingBox().inflate(CLAW_REACH).intersects(target.getBoundingBox()))
            return;
        AttributeInstance attackDamage = getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage == null) return;
        setSpecialState(AttackState.CLAW_ATTACK, true);
        try {
            super.doHurtTarget(target);
        } finally {
            setSpecialState(AttackState.CLAW_ATTACK, false);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(VARIANT_TAG, getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(tag.getInt(VARIANT_TAG));
        variantInitialized = true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "movement_and_attack",
                5,
                state -> {
                    if (swinging) {
                        return state.setAndContinue(ATTACK);
                    }
                    if (state.isMoving()) {
                        return state.setAndContinue(WALK);
                    }
                    state.getController().forceAnimationReset();
                    return PlayState.STOP;
                }));
    }

    @Override
    public int getCurrentSwingDuration() {
        return 20;
    }

    public enum AttackState {CLAW_ATTACK}
}
