package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

/// 蚁狮蜂及巨型蚁狮蜂共用的游走—冲刺实现。
///
/// 发现玩家后直接沿三维方向追击，失去目标时才恢复随机飞行。两个注册变种共享行为
/// 和动画资源，仅由注册尺寸与属性表表达体型、强度差异。
public class AntlionSwarmer extends ReboundingFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");

    public AntlionSwarmer(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        setDiscardFriction(true);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(AntlionSwarmer.this), new DirectFloatingPursuitAction(AntlionSwarmer.this)),
                        new LookForwardWanderFlyAction(AntlionSwarmer.this, 0.2, 0.0F));
            }
        };
    }

    @Override
    protected Vec3 reboundVelocity(Vec3 requested, Vec3 allowed) {
        double x = allowed.x == requested.x ? requested.x : -requested.x;
        double y = allowed.y == requested.y ? requested.y : -requested.y;
        double z = allowed.z == requested.z ? requested.z : -requested.z;
        return new Vec3(x, y, z);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ANTLION_SWARMER_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ANTLION_SWARMER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ANTLION_SWARMER_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 2, state -> state.setAndContinue(FLY)));
    }
}
