package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.DirectFloatingPursuitAction;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 穿过地形并直接追逐玩家的陨石怪。
public class MeteorHead extends BaseFlyingMonster {
    private static final RawAnimation FLOAT = RawAnimation.begin().thenLoop("move.walk");

    public MeteorHead(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return false;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(MeteorHead.this), new DirectFloatingPursuitAction(MeteorHead.this)),
                        new LookForwardWanderFlyAction(MeteorHead.this, 0.18, 0.0F));
            }
        };
    }

    /// 陨石怪接触玩家时有三分之一概率点燃目标，持续时间随难度延长。
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean damaged = super.doHurtTarget(target);
        if (damaged && target instanceof LivingEntity living && random.nextInt(3) == 0) {
            int duration = LibUtils.isMaster(level(), blockPosition()) ? 350
                    : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 280 : 140;
            living.setRemainingFireTicks(Math.max(living.getRemainingFireTicks(), duration));
        }
        return damaged;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Float", 3, state -> state.setAndContinue(FLOAT)));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.METAL_DEATH.get();
    }

    @Override
    protected boolean hasPushableBody() {
        return true;
    }
}
