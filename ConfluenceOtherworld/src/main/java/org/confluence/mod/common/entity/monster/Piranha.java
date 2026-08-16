package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MeleeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomSwimAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

/// 食人鱼及其共用水生近战变体。
///
/// <p>移动和离水扑腾由水生基类处理；食人鱼本身补充持续咬合表现、攻击动作周期
/// 与长时间水下供气。持续设置挥击状态是 1.21 的模型语义，用于让嘴部和尾部
/// 始终播放快速咬合动画，并不代表每 tick 都结算一次伤害。</p>
public class Piranha extends BaseAquaticMonster {

    public Piranha(EntityType<? extends Piranha> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AquaticAttributeProfiles.PIRANHA.createBuilder();
    }

    public static AttributeSupplier.Builder createArapaimaAttributes() {
        return AquaticAttributeProfiles.ARAPAIMA.createBuilder();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(Piranha.this),
                                new MoveToTargetAction(Piranha.this, 0.7, 2.0),
                                new MeleeAttackAction(Piranha.this, 2.0),
                                new WaitAction(10)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                new RandomSwimAction(Piranha.this, 0.4, 6, 3)));
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        swinging = true;
        updateSwingTime();
        if (isNoAi()) {
            setAirSupply(getMaxAirSupply());
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
        }
        return hit;
    }

    @Override
    public int getMaxAirSupply() {
        return 4800;
    }

    @Override
    protected int increaseAirSupply(int currentAir) {
        return getMaxAirSupply();
    }

    @Override
    public int getCurrentSwingDuration() {
        return 12;
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "Swim/Attack",
                5,
                state -> {
                    if (swinging) {
                        return state.setAndContinue(DefaultAnimations.ATTACK_STRIKE);
                    }
                    if (state.isMoving()) {
                        return state.setAndContinue(DefaultAnimations.SWIM);
                    }
                    return PlayState.STOP;
                }));
    }
}
