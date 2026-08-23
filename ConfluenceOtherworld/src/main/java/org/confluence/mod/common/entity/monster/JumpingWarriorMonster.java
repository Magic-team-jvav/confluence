package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/// 在普通追击近战之前尝试中距离跃击的陆行怪物。
///
/// <p>该类只保存一组不可变的跃击参数，完整状态机仍由公共行为节点维护。适用于木乃伊、
/// 拉米亚和食尸鬼等行为相同但数值不同的简单变种，不需要为每个注册项创建空壳子类。</p>
public final class JumpingWarriorMonster extends BaseWarriorMonster {
    private final JumpProfile jumpProfile;

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
        super(type, level, 0.0, animationProfile, soundProfile, meleeSpeed, true);
        this.jumpProfile = jumpProfile;
    }

    @Override
    protected JumpProfile jumpProfile() {
        return jumpProfile;
    }
}
