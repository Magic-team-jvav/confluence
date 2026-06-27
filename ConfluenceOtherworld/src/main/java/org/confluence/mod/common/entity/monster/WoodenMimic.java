package org.confluence.mod.common.entity.monster;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.api.entity.ISharedFlagControllerHolder;
import org.confluence.mod.api.entity.SharedFlagController;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.entity.ai.goal.MutableRangeNearestAttackableTargetGoal;
import org.confluence.mod.util.entity.ai.goal.behavior.BTCommonRoot;
import org.confluence.mod.util.entity.ai.goal.behavior.BTFactory;
import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;
import org.confluence.mod.util.entity.ai.goal.behavior.BTRoot;
import org.confluence.mod.util.entity.ai.goal.behavior.composite.SequenceNode;
import org.confluence.mod.util.entity.ai.goal.behavior.condition.Condition;
import org.confluence.mod.util.entity.ai.goal.behavior.condition.TargetExistCondition;
import org.confluence.mod.util.entity.ai.goal.behavior.leaf.AnimCtrlAction;
import org.confluence.mod.util.entity.ai.goal.behavior.leaf.JumpAttackAction;
import org.confluence.mod.util.entity.ai.goal.behavior.leaf.LookAtTargetAction;
import org.confluence.mod.util.entity.ai.goal.behavior.leaf.SetAttributeAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.function.Function;

/// 普通宝箱怪
public class WoodenMimic extends AbstractMonster implements ISharedFlagControllerHolder {
    protected static final EntityDataAccessor<Integer> DATA_SHARE_FLAG = SynchedEntityData.defineId(WoodenMimic.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_IDLE_ANGLE = SynchedEntityData.defineId(WoodenMimic.class, EntityDataSerializers.INT);
    protected static final RawAnimation stand = RawAnimation.begin().thenLoop("Closed state");
    protected static final RawAnimation open = RawAnimation.begin().thenPlayAndHold("Open");
    protected static final RawAnimation jump = RawAnimation.begin().thenPlayAndHold("Jump");
    protected static final RawAnimation close = RawAnimation.begin().thenPlay("Closed");

    protected final SharedFlagController sharedFlagController;
    protected final SharedFlagController.SharedFlag openFlag;
    protected final SharedFlagController.SharedFlag closeFlag;
    protected final SharedFlagController.SharedFlag jumpFlag;

    public WoodenMimic(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.sharedFlagController = new SharedFlagController(this.entityData, DATA_SHARE_FLAG);
        this.openFlag = this.sharedFlagController.registerFlag();
        this.closeFlag = this.sharedFlagController.registerFlag();
        this.jumpFlag = this.sharedFlagController.registerFlag();
        this.collisionProperties.attackRangeExtent = 0.5f;
        this.entityData.set(DATA_IDLE_ANGLE, this.random.nextInt(4) * 90);
    }


    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(5, this.createBehaviorTree());

        this.targetSelector.addGoal(1, new MutableRangeNearestAttackableTargetGoal<>(this, Player.class, true));
    }

    protected BTRoot<WoodenMimic> createBehaviorTree() {
        return new MimicBT(this);
    }

    @Override
    public SharedFlagController getSharedFlagController() {
        return this.sharedFlagController;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHARE_FLAG, 0);
        this.entityData.define(DATA_IDLE_ANGLE, 0);

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_SHARE_FLAG) {
            if (!this.sharedFlagController.getFlag(openFlag)) {
                this.setYBodyRot(this.entityData.get(DATA_IDLE_ANGLE));
                this.setYHeadRot(this.entityData.get(DATA_IDLE_ANGLE));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
//            this.setYRot(0);
            this.setYBodyRot(this.getYHeadRot());

        }
    }

    protected static class MimicBT extends BTCommonRoot<WoodenMimic> {

        public MimicBT(WoodenMimic mob) {
            super(mob);
        }

        @Override
        public BTNode createWonderBehavior() {
            return BTFactory.sequence()
                    .addChild(new SetAttributeAction(this.mob, PortAttributesExtension.gravity().value(), 0.08))
                    .addChild(BTFactory.wait(1000))
                    ;
        }

        @Override
        protected BTNode createStageTrigger() {
            // 丢失目标100秒后，设置跟随距离为5
            return BTFactory.condition(Condition.not(new TargetExistCondition(this.mob)), BTFactory.sequence()
                    .addChild(BTFactory.wait(20))
                    .addChild(new SetAttributeAction(this.mob, Attributes.FOLLOW_RANGE, 5))
                    .addChild(new AdjustRotateAction(this.mob))
                    .addChild(BTFactory.wait(6))
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "open", this.mob.openFlag, false))
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "close", this.mob.closeFlag, true))
                    .addChild(BTFactory.waitForever())
            );
        }

        @Override
        public BTNode createAttackBehavior() {
            Function<Integer, BTNode> waitActionFunction = (time) -> BTFactory.withTimer(time, new LookAtTargetAction(this.mob));


            return BTFactory.sequence()
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "open", this.mob.openFlag, true))
                    .addChild(BTFactory.infinite(
                            this.createActualAttackBehavior(waitActionFunction)
                    ))
                    ;

        }

        private static class AdjustRotateAction extends BTNode {
            Mob mob;

            public AdjustRotateAction(Mob mob) {
                this.mob = mob;
            }

            @Override
            public BTStatus execute() {
                this.mob.setDeltaMovement(0, 0.3f, 0);
                int angle = this.mob.getRandom().nextInt(4) * 90;
                this.mob.moveTo(this.mob.blockPosition(), angle, 0);
                this.mob.getEntityData().set(DATA_IDLE_ANGLE, angle);

                return BTStatus.SUCCESS;
            }
        }

        protected SequenceNode createActualAttackBehavior(Function<Integer, BTNode> waitActionFunction) {
            BTNode waitAction = waitActionFunction.apply(15);
            return BTFactory.sequence()
                    .addChild(new SetAttributeAction(this.mob, Attributes.FOLLOW_RANGE, 16))
                    .addChild(BTFactory.repeater(2, BTFactory.sequence()
                            .addChild(waitAction)
                            .addChild(doJump(1, 0))
                    ))
                    .addChild(waitAction)
                    .addChild(doJump(1.5f, 0.5f))
                    .addChild(waitAction);
        }

        protected BTNode doJump(float horizonPower, float jumpAdditionSpeed) {
            return BTFactory.sequence()
                    .addChild(new JumpAttackAction(mob, horizonPower, jumpAdditionSpeed))
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "jump", this.mob.jumpFlag, true))
                    .addChild(BTFactory.wait(5))
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "jump", this.mob.jumpFlag, false))
                    .addChild(new AnimCtrlAction<>(this.mob, "Controller", "open", this.mob.openFlag, true))
                    ;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state -> state.setAndContinue(stand))
                .triggerableAnim("open", open)
                .triggerableAnim("close", close)
                .triggerableAnim("jump", jump)
        );
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSoundEvents.METAL_HURT.get();
    }
}
