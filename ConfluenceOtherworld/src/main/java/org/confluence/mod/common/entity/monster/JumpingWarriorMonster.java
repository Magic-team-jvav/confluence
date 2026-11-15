package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.goal.EnemyOpenDoorGoal;
import org.jetbrains.annotations.Nullable;

/// 保存战士 AI 敌怪的可选跃击参数与木乃伊行为。
///
/// 只有确实具有跃击技能的实体才传入跃击参数；普通战士 AI 变体传入
/// {@code null}，只复用近战追击；命中减益由 AttackEffects 数据配置。
public final class JumpingWarriorMonster extends BaseWarriorMonster {
    private final @Nullable JumpProfile jumpProfile;
    private final boolean mummy;

    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, JumpProfile jumpProfile) {
        this(type, level, jumpProfile, LandAnimationProfile.WALK_ONLY);
    }

    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, JumpProfile jumpProfile, LandAnimationProfile animationProfile) {
        this(type, level, jumpProfile, animationProfile, LandSoundProfile.ROUTINE);
    }

    /// 创建同时具有跳跃参数、动画档案和音效档案的陆行怪物变种。
    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, JumpProfile jumpProfile, LandAnimationProfile animationProfile, LandSoundProfile soundProfile) {
        this(type, level, jumpProfile, animationProfile, soundProfile, 1.0);
    }

    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, JumpProfile jumpProfile, LandAnimationProfile animationProfile, LandSoundProfile soundProfile, double meleeSpeed) {
        this(type, level, jumpProfile, animationProfile, soundProfile, meleeSpeed, false);
    }

    public JumpingWarriorMonster(EntityType<? extends JumpingWarriorMonster> type, Level level, @Nullable JumpProfile jumpProfile, LandAnimationProfile animationProfile, LandSoundProfile soundProfile, double meleeSpeed, boolean mummy) {
        super(type, level, 0.0, animationProfile, soundProfile, meleeSpeed, true);
        this.jumpProfile = jumpProfile;
        this.mummy = mummy;
        if (mummy && navigation instanceof GroundPathNavigation groundNavigation) {
            groundNavigation.setCanOpenDoors(true);
            goalSelector.addGoal(-1, new EnemyOpenDoorGoal(this));
        }
    }

    @Override
    protected @Nullable JumpProfile jumpProfile() {
        return jumpProfile;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide || !mummy) return;
        boolean enraged = getHealth() <= getMaxHealth() * 0.5F;
        setSpecialState(CombatState.WOUNDED, enraged);
    }

    public enum CombatState {WOUNDED}
}
