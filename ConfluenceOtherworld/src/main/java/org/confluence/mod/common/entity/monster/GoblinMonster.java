package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.entity.ai.goal.EnemyBreakDoorGoal;
import org.confluence.mod.common.entity.ai.goal.EnemyOpenDoorGoal;
import org.confluence.mod.common.init.ModSoundEvents;

/// 哥布林族共用的陆地行为。
///
/// 哥布林落水后会主动上浮。该行为交由原版浮水目标统一控制跳跃与离水过程，避免
/// 持续叠加竖直速度后让陆地导航不断重新选择方向并原地转圈。普通人形敌怪不继承该目标，
/// 避免装甲幻影魔等实体被无关的水中规则影响。
public class GoblinMonster extends HumanoidWarriorMonster {
    public GoblinMonster(EntityType<? extends GoblinMonster> type, Level level, ItemStack defaultMainHand) {
        this(type, level, defaultMainHand, LandAnimationProfile.WALK_IDLE, DoorBehavior.NONE);
    }

    public GoblinMonster(EntityType<? extends GoblinMonster> type, Level level, ItemStack defaultMainHand, LandAnimationProfile animationProfile) {
        this(type, level, defaultMainHand, animationProfile, DoorBehavior.NONE);
    }

    public GoblinMonster(EntityType<? extends GoblinMonster> type, Level level, ItemStack defaultMainHand,
                         LandAnimationProfile animationProfile, DoorBehavior doorBehavior) {
        super(type, level, defaultMainHand, LandSoundProfile.ROUTINE, animationProfile);
        if (doorBehavior != DoorBehavior.NONE && navigation instanceof GroundPathNavigation groundNavigation) {
            configurePlayerTargetLineOfSight(false);
            groundNavigation.setCanOpenDoors(true);
            if (doorBehavior == DoorBehavior.BREAK) {
                goalSelector.addGoal(-1, new EnemyBreakDoorGoal(this));
            } else {
                goalSelector.addGoal(-1, new EnemyOpenDoorGoal(this));
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(-1, new FloatGoal(this));
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    public enum DoorBehavior {
        NONE,
        OPEN,
        BREAK
    }
}
