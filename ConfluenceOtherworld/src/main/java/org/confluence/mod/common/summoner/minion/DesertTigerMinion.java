package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.desert_tiger.DesertTigerAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.desert_tiger.DesertTigerIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** 沙漠虎：重复召唤会提升占用槽位并强化跳扑伤害。 */
public class DesertTigerMinion extends GroundMinion implements IEntityCollision<DesertTigerMinion> {

    public int attackTime = -1;
    public boolean pouncing;
    public final Set<UUID> pounced = new HashSet<>();

    public DesertTigerMinion() {
        super(SummonerAttachmentEntityTypes.DESERT_TIGER);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> attackTime, value -> attackTime = value);
        fields.field(LyraStreamCodecs.BOOL, () -> pouncing, value -> pouncing = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new DesertTigerAttackGoal(this));
        goalSelector.addGoal(1, new DesertTigerIdleGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "desert_tiger", 0, state -> {
            if (pouncing) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("attack.roll"));
            }
            if (attackTime != -1 && getTickCount() - attackTime < 10) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attack.strike"));
            }
            if (isWalking()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("move.walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"));
        }));
    }

    @Override
    public int getSearchDistance() {
        return 50;
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.4, 0, -0.4, 0.4, 0.55, 0.4);
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.25, 0, -0.3, 0.25, 0.55, 0.5);
    }

    @Override
    public boolean canCollideAttack() {
        return getTarget() != null || pouncing;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        float multiplier = 1.0F + 0.4F * (getSlotCost() - 1) + (pouncing ? 0.5F : 0.0F);
        for (HitContext hit : hitContexts) {
            attackTime = getTickCount();
            attack(hit.entity(), getDamage() * multiplier, 3);
        }
    }
}
