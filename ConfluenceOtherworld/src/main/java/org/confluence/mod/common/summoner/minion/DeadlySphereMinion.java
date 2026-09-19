package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IBlockCollision;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.deadly_sphere.DeadlySphereAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.deadly_sphere.DeadlySphereIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

/**
 * 致命球：三次冲撞后进入长冷却的冲刺型仆从，碰撞时造成伤害并弹开。
 */
public class DeadlySphereMinion extends MomentumMinion implements IEntityCollision<DeadlySphereMinion>, IBlockCollision<DeadlySphereMinion> {

    private int form;

    public DeadlySphereMinion() {
        super(SummonerAttachmentEntityTypes.DEADLY_SPHERE);
        setGravity(0);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> form, value -> form = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new DeadlySphereAttackGoal(this));
        goalSelector.addGoal(1, new DeadlySphereIdleGoal(this));
    }

    /**
     * 当前形态：0 尖刺、1 火焰、2 刀刃。
     */
    public int getForm() {
        return form;
    }

    public void setForm(int form) {
        this.form = Math.floorMod(form, 3);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "deadly_sphere", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop(form == 2 ? "misc.idle" : "attack.strike"))));
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.2, -0.2, -0.2, 0.2, 0.2, 0.2);
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.2, -0.2, -0.2, 0.2, 0.2, 0.2);
    }

    @Override
    public boolean canCollideAttack() {
        return getTarget() != null;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            attack(hit.entity(), getDamage(), 4);
        }
    }

    @Override
    public float getElasticity() {
        return 0.98F;
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }
}
