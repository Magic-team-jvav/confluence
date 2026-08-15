package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 哥布林族共用的陆地行为。
 *
 * <p>1.21 为哥布林额外注册的缓慢上浮目标只持续叠加竖直速度，实体到达水面后，
 * 陆地导航会不断重新选择方向，最终表现为原地转圈。这里保留哥布林会主动上浮的
 * 行为，但交由原版浮水目标统一控制跳跃与离水过程。普通人形敌怪不继承该目标，
 * 避免装甲幻影魔等实体被无关的哥布林水中规则影响。</p>
 */
public class GoblinMonster extends HumanoidWarriorMonster {
    public GoblinMonster(
            EntityType<? extends GoblinMonster> type,
            Level level,
            ItemStack defaultMainHand) {
        this(type, level, defaultMainHand, LandAnimationProfile.WALK_IDLE);
    }

    public GoblinMonster(
            EntityType<? extends GoblinMonster> type,
            Level level,
            ItemStack defaultMainHand,
            LandAnimationProfile animationProfile) {
        super(type, level, defaultMainHand, LandSoundProfile.ROUTINE,
                animationProfile);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(-1, new FloatGoal(this));
    }
}
