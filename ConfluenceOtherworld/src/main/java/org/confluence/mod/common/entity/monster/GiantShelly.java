package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.UUID;

/// 在行走、缩壳和翻滚之间循环的巨大卷壳虫。
///
/// <p>受到攻击会提前进入缩壳阶段；缩壳期间提高护甲，随后朝目标翻滚并以实体碰撞结算一次
/// 近战伤害。阶段与外观变种均同步并保存，客户端只根据同步状态选择动画，不参与战斗判定。</p>
public final class GiantShelly extends BaseMonster {
    private static final String PHASE_TAG = "Phase";
    private static final String PHASE_TICKS_TAG = "PhaseTicks";
    private static final String VARIANT_TAG = "Variant";
    private static final UUID SHELL_ARMOR_UUID =
            UUID.fromString("a7cb55a9-27cf-4dad-a5c1-56fc96c504ea");
    private static final AttributeModifier SHELL_ARMOR =
            new AttributeModifier(
                    SHELL_ARMOR_UUID,
                    "Giant shelly shell armor",
                    2.0,
                    AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(
                    GiantShelly.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(
                    GiantShelly.class, EntityDataSerializers.INT);
    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ENTER_SHELL =
            RawAnimation.begin().thenPlayAndHold("shrinking_shell");
    private static final RawAnimation ROLL =
            RawAnimation.begin().thenLoop("turn");
    private static final RawAnimation RECOVER =
            RawAnimation.begin().thenPlayAndHold("turn2");
    private int phaseTicks;
    private boolean variantInitialized;
    private int collisionAttackTicks = 20;
    private Vec3 wanderTarget;

    public GiantShelly(
            EntityType<? extends GiantShelly> type,
            Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.1)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(PHASE, Phase.FREE.ordinal());
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

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        phaseTicks++;
        switch (getPhase()) {
            case FREE -> {
                getNavigation().stop();
                if (phaseTicks > 40) {
                    setPhase(Phase.WALK);
                }
            }
            case WALK -> {
                if (hurtTime > 0) {
                    setPhase(Phase.ENTERING_SHELL);
                } else {
                    updateWalking();
                }
                if (getPhase() == Phase.WALK && phaseTicks > 60) {
                    setPhase(getTarget() == null
                            ? Phase.FREE : Phase.ENTERING_SHELL);
                }
            }
            case ENTERING_SHELL -> {
                getNavigation().stop();
                if (phaseTicks > 50) {
                    setPhase(Phase.ROLLING);
                }
            }
            case ROLLING -> {
                updateRolling();
                if (phaseTicks > 50) {
                    setPhase(Phase.RECOVERING);
                }
            }
            case RECOVERING -> {
                if (phaseTicks > 15) {
                    setPhase(Phase.FREE);
                }
            }
        }
        updateCollisionAttack();
    }

    private void updateWalking() {
        LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            getNavigation().moveTo(target, 1.0);
            return;
        }
        if (wanderTarget == null
                || position().distanceToSqr(wanderTarget) < 1.0
                || phaseTicks % 40 == 0) {
            wanderTarget = LandRandomPos.getPos(this, 15, 7);
        }
        if (wanderTarget != null) {
            getNavigation().moveTo(
                    wanderTarget.x,
                    wanderTarget.y,
                    wanderTarget.z,
                    1.0);
        }
    }

    private void updateRolling() {
        if (phaseTicks != 20) {
            return;
        }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        Vec3 direction = target.position().add(0.0, 1.0, 0.0)
                .subtract(position());
        if (direction.lengthSqr() > 1.0E-8) {
            setDeltaMovement(direction.scale(0.5));
            hasImpulse = true;
        }
    }

    /// 缩壳、翻滚和复原阶段都保留 1.21 的扩大碰撞攻击。
    ///
    /// <p>命中后等待 20 tick；当前范围内没有玩家时只等待 1 tick 后重试。冷却仅在
    /// 封闭阶段推进，因此普通行走不会提前消耗下一次缩壳的首次检测时间。</p>
    private void updateCollisionAttack() {
        if (getPhase().ordinal() <= Phase.WALK.ordinal()) {
            return;
        }
        collisionAttackTicks--;
        if (collisionAttackTicks > 0) {
            return;
        }

        var players = level().getEntitiesOfClass(
                Player.class,
                getBoundingBox().inflate(1.0),
                player -> player.isAlive() && canAttack(player));
        if (players.isEmpty()) {
            collisionAttackTicks = 1;
            return;
        }
        for (Player player : players) {
            doHurtTarget(player);
        }
        collisionAttackTicks = 20;
    }

    private void setPhase(Phase phase) {
        entityData.set(PHASE, phase.ordinal());
        phaseTicks = 0;
        if (phase.ordinal() >= Phase.ENTERING_SHELL.ordinal()) {
            addShellArmor();
        } else {
            removeShellArmor();
        }
    }

    private void addShellArmor() {
        var armor = getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(SHELL_ARMOR_UUID) == null) {
            armor.addTransientModifier(SHELL_ARMOR);
        }
    }

    private void removeShellArmor() {
        var armor = getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.removeModifier(SHELL_ARMOR_UUID);
        }
    }

    public Phase getPhase() {
        int id = entityData.get(PHASE);
        Phase[] values = Phase.values();
        return values[Math.max(0, Math.min(id, values.length - 1))];
    }

    public int getVariant() {
        return entityData.get(VARIANT);
    }

    private void setVariant(int variant) {
        entityData.set(VARIANT, Math.floorMod(variant, 2));
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(PHASE_TAG, getPhase().ordinal());
        tag.putInt(PHASE_TICKS_TAG, phaseTicks);
        tag.putInt(VARIANT_TAG, getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        Phase[] phases = Phase.values();
        int savedPhase = tag.getInt(PHASE_TAG);
        setPhase(phases[Math.max(
                0, Math.min(savedPhase, phases.length - 1))]);
        phaseTicks = Math.max(0, tag.getInt(PHASE_TICKS_TAG));
        setVariant(tag.getInt(VARIANT_TAG));
        variantInitialized = true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return random.nextBoolean()
                ? ModSoundEvents.GIANT_SHELLY_FREE_0.get()
                : ModSoundEvents.GIANT_SHELLY_FREE_1.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.GIANT_SHELLY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.GIANT_SHELLY_DEATH.get();
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "phase",
                3,
                state -> switch (getPhase()) {
                    case WALK -> state.setAndContinue(WALK);
                    case ENTERING_SHELL -> state.setAndContinue(ENTER_SHELL);
                    case ROLLING -> state.setAndContinue(ROLL);
                    case RECOVERING -> state.setAndContinue(RECOVER);
                    case FREE -> PlayState.STOP;
                }));
    }

    public enum Phase {
        FREE,
        WALK,
        ENTERING_SHELL,
        ROLLING,
        RECOVERING
    }
}
