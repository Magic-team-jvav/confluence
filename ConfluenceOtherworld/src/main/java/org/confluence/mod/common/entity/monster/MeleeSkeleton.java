package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.humanoid.BaseHumanoidMonster;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

/// 泰拉近战骷髅族共用的基础行为。
///
/// <p>不同骨骼变种可以通过注册项配置属性和外观，但都共享近身追击、挥击和随机游荡
/// 逻辑。作为亡灵骨骼，它们不会溺水，也不会接受中毒效果；这些免疫在实体入口统一
/// 处理，确保环境伤害、药水和其他模组调用都得到相同结果。</p>
///
/// <p>受伤时使用骷髅声音，死亡时使用泰拉亡灵死亡声，与原版骷髅声音语义区分。
/// 模型动画由客户端骷髅动画族统一驱动，实体类仅保留游戏行为。</p>
public class MeleeSkeleton extends BaseHumanoidMonster {
    public MeleeSkeleton(EntityType<? extends MeleeSkeleton> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseHumanoidMonster.createHumanoidAttributes();
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
                        SequenceNode.of(new HasTargetCondition(MeleeSkeleton.this),
                                new MoveToTargetAction(MeleeSkeleton.this, 1.0, 2.0),
                                new MeleeAttackAction(MeleeSkeleton.this, 2.0)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                new RandomStrollAction(MeleeSkeleton.this, 0.8, 10)));
            }
        };
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
    public boolean addEffect(
            MobEffectInstance effect,
            @Nullable Entity source) {
        return effect.getEffect() != MobEffects.POISON
                && super.addEffect(effect, source);
    }

}
