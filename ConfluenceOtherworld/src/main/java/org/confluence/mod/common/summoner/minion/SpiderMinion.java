package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.spider.SpiderAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.spider.SpiderIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/** 蜘蛛：按召唤次序切换三种形态，接触目标后吸附并持续咬击。 */
public class SpiderMinion extends GroundMinion {

    public int variant = -1;

    public SpiderMinion() {
        super(SummonerAttachmentEntityTypes.SPIDER);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> variant, value -> variant = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new SpiderAttackGoal(this));
        goalSelector.addGoal(1, new SpiderIdleGoal(this));
    }

    @Override
    public void tick() {
        if (variant < 0 && !getLevel().isClientSide()) {
            variant = Math.floorMod(getOrder(), 3);
        }
        super.tick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "spider", 0, state -> isWalking()
                ? state.setAndContinue(RawAnimation.begin().thenLoop("move.walk"))
                : state.setAndContinue(RawAnimation.begin().thenLoop("misc.idle"))));
    }

    @Override
    public Immunity.Type confluence$getImmunityType() {
        return Immunity.Type.STATIC;
    }

    @Override
    public int getSearchDistance() {
        return (int) (50 + getOrder() * 2.5);
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.35, 0, -0.35, 0.35, 0.5, 0.35);
    }
}
