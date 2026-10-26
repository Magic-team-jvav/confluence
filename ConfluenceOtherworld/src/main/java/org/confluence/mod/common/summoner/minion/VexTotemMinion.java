package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.vex_totem.VexTotemAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.vex_totem.VexTotemIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

/**
 * 恼鬼：在主人头顶悬浮，追击目标后发起一次无法打断的冲锋撞击。
 */
public class VexTotemMinion extends MomentumMinion implements IEntityCollision<VexTotemMinion> {

    public int chargeTime = -1;

    public VexTotemMinion() {
        super(SummonerAttachmentEntityTypes.VEX_TOTEM);
        setGravity(0);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> chargeTime, value -> chargeTime = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new VexTotemAttackGoal(this));
        goalSelector.addGoal(1, new VexTotemIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "vex_totem", 2, state -> {
            if (chargeTime >= 0) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attack.cast"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"));
        }));
    }

    @Override
    public void tick() {
        if (chargeTime >= 0) {
            chargeTime++;
        }
        super.tick();
        if (chargeTime >= 40) {
            chargeTime = -1;
        }
    }

    @Override
    public LivingEntity searchTarget() {
        if (chargeTime >= 0) {
            return getTarget();
        }
        return super.searchTarget();
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public boolean isAlive() {
        return PlayerUtils.isSoulMode(owner);
    }

    @Override
    public void lookAtDirection(Vec3 direction) {
        if (direction.horizontalDistanceSqr() >= 1.0E-14D) {
            setDesiredRotation(PathNode.yawFromDirection(direction), 0, 0);
        }
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.3, -0.6, 0, 0.3, 0.1, 0.8);
    }

    @Override
    public boolean canCollideAttack() {
        return chargeTime == 20;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            attack(hit.entity(), getDamage(), 4);
            LivingEntity victim = LibEntityUtils.logicalLivingTarget(hit.entity());
            if (victim != null && !(victim instanceof BaseBoss)) {
                Vec3 direction = victim.getBoundingBox().getCenter().subtract(getPos());
                double horizontal = direction.horizontalDistance();
                if (horizontal > 1.0E-6) {
                    double strength = Math.max(0.0, 1.0 - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)) * getKnockback();
                    victim.push(direction.x / horizontal * strength, 0.2 * strength, direction.z / horizontal * strength);
                    victim.hasImpulse = true;
                }
            }
        }
    }
}
