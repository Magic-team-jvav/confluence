package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;

/**
 * 能够持续在三维空间活动的小动物基类。
 *
 * <p>飞行导航器只负责规划空间路径，飞行移动控制器负责把路径目标转换为平滑的三轴速度；
 * 两者缺一都会让空中小动物退化为地面寻路或直接坠落。具体的逃跑、巡游和特殊交互仍由
 * 子类行为树决定，避免把玩法参数集中到公共基类。</p>
 */
public abstract class BaseFlyingCritter extends BaseCritter {
    protected BaseFlyingCritter(EntityType<? extends BaseFlyingCritter> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        setNoGravity(true);
    }

    /**
     * 构建飞行控制器所需的完整属性集合。
     *
     * <p>{@link FlyingMoveControl} 在产生实际位移时读取飞行速度，只有移动速度而缺少该属性
     * 会在实体首个飞行 tick 直接抛错。所有空中小动物都应从这里创建属性，避免注册事件
     * 漏掉隐含依赖。</p>
     */
    public static AttributeSupplier.Builder createFlyingCritterAttributes() {
        return BaseCritter.createInsectAttributes()
                .add(Attributes.FLYING_SPEED, 0.25);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation =
                new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public boolean causeFallDamage(
            float fallDistance,
            float multiplier,
            net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    /**
     * 鸟类、飞行昆虫与仙灵在 1.21 中直接继承普通生物音量。
     */
    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }
}
