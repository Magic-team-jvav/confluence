package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.confluence.mod.common.entity.ai.goal.EnemyBreakDoorGoal;
import org.confluence.mod.common.entity.ai.goal.EnemyOpenDoorGoal;

/// 哥布林族共用的陆地行为。
///
/// 浮水与导航由陆行怪物公共层处理，本类只保留哥布林的门行为差异。
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
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    public enum DoorBehavior {
        NONE,
        OPEN,
        BREAK
    }
}
