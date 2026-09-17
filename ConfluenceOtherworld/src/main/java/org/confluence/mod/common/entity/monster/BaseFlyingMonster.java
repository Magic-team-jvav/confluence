package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class BaseFlyingMonster extends BaseMonster {
    public BaseFlyingMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.lookControl = new LookControl(this) {
            @Override
            protected boolean resetXRotOnTick() {
                return false;
            }
        };
        this.setNoGravity(true);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        return navigation;
    }

    /// 飞行怪的接触伤害属于实体本身，而不是某个追击动作。
    ///
    /// 因此行为树切换到施法或等待时，已经取得目标的实体仍按同一冷却检测身体碰撞。
    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected float tickHeadTurn(float bodyYaw, float animationSpeed) {
        yBodyRot = getYRot();
        yHeadRot = getYRot();
        return animationSpeed;
    }

    /// 飞行怪默认始终使用无重力物理。
    ///
    /// 仅在构造器设置标志并不可靠，读档或外部逻辑仍可能改动它。需要阶段性落地的
    /// 特殊飞行怪应自行覆盖本方法。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {return false;}

    @Override
    public boolean isPushable() {
        return hasPushableBody() && super.isPushable();
    }

    /// 指示具体实体是否保留普通生物的推动行为。
    ///
    /// 通用飞行预制体默认不可推动；妖精、黄蜂及部分穿墙生物通过覆盖本方法保留推动，
    /// 因此不能在公共飞行基类中统一抹平差异。
    protected boolean hasPushableBody() {
        return false;
    }

}
