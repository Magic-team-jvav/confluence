package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.entity.monster.humanoid.BaseHumanoidMonster;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

/// 泰拉近战骷髅族共用的基础行为。
///
/// 不同骨骼变种可以通过注册项配置属性和外观，但都共享近身追击、挥击和随机游荡
/// 逻辑。作为亡灵骨骼，它们不会溺水，也不会接受中毒效果；这些免疫在实体入口统一
/// 处理，确保环境伤害、药水和其他模组调用都得到相同结果。
///
/// 受伤时使用骷髅声音，死亡时使用泰拉亡灵死亡声，与原版骷髅声音语义区分。
/// 模型动画由客户端骷髅动画族统一驱动，实体类仅保留游戏行为。
public class MeleeSkeleton extends BaseHumanoidMonster {
    public MeleeSkeleton(EntityType<? extends MeleeSkeleton> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseHumanoidMonster.createHumanoidAttributes();
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }

    @Override
    public int getCurrentSwingDuration() {
        return 10;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        swing(InteractionHand.MAIN_HAND, true);
        return super.doHurtTarget(target);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new AvoidEntityGoal<>(MeleeSkeleton.this, Wolf.class, 6.0F, 1.0, 1.2)),
                        new VanillaGoalAction(new MeleeAttackGoal(MeleeSkeleton.this, 1.2, false)),
                        new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(MeleeSkeleton.this, 1.0)),
                        new VanillaGoalAction(new LookAtPlayerGoal(MeleeSkeleton.this, Player.class, 8.0F)),
                        new VanillaGoalAction(new RandomLookAroundGoal(MeleeSkeleton.this)));
            }
        };
    }

    @Override
    public float getWalkTargetValue(BlockPos pos) {
        return getType() == MonsterEntities.SPORE_SKELETON.get() ? 0.0F : super.getWalkTargetValue(pos);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk/Idle", 5, state -> {
            state.setControllerSpeed(2.0F);
            return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
        }));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.TR_SKELETON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.TR_ZOMBIE_DEATH.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypeTags.IS_DROWNING)
                && super.hurt(source, amount);
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
        return effect.getEffect() != MobEffects.POISON
                && super.addEffect(effect, source);
    }

}
