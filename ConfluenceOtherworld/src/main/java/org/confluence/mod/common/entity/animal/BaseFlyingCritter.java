package org.confluence.mod.common.entity.animal;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 能够持续在三维空间活动的小动物基类。
///
/// 飞行导航器只负责规划空间路径，飞行移动控制器负责把路径目标转换为平滑的三轴速度；
/// 两者缺一都会让空中小动物退化为地面寻路或直接坠落。具体的逃跑、巡游和特殊交互仍由
/// 子类行为树决定，避免把玩法参数集中到公共基类。
public abstract class BaseFlyingCritter extends BaseCritter {
    private static final RawAnimation FLY_ONLY = RawAnimation.begin().thenLoop("move.fly");

    protected BaseFlyingCritter(EntityType<? extends BaseFlyingCritter> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        setNoGravity(true);
    }

    /// 构建飞行控制器所需的完整属性集合。
    ///
    /// {@link FlyingMoveControl} 在产生实际位移时读取飞行速度，只有移动速度而缺少该属性
    /// 会在实体首个飞行 tick 直接抛错。所有空中小动物都应从这里创建属性，避免注册事件
    /// 漏掉隐含依赖。
    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    /**
     * 创建飞行小动物共用的巡游行为，不包含任何鸟类落脚或社交逻辑。
     */
    protected final BTNode createFlyingRoutine() {
        return SelectorNode.of(
                new VanillaGoalAction(new FloatGoal(this)),
                new VanillaGoalAction(new WaterAvoidingRandomFlyingGoal(this, 1.0D)),
                new VanillaGoalAction(new LookAtPlayerGoal(this, Player.class, 6.0F)),
                new VanillaGoalAction(new RandomLookAroundGoal(this))
        );
    }

    /**
     * 为会主动避开敌怪的飞行小动物组合逃逸与日常巡游。
     */
    protected final BTNode createEnemyAvoidingFlyingRoutine() {
        return SelectorNode.of(
                new VanillaGoalAction(new AvoidEntityGoal<>(this, Monster.class, 6.0F, 1.0D, 1.35D)),
                createFlyingRoutine()
        );
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createFlyingRoutine(), 1.25D);
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 5, state -> state.setAndContinue(FLY_ONLY)));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    /// 飞行昆虫与仙灵使用普通生物音量。
    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }
}
