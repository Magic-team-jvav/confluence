package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class BaseFlyingMonster extends BaseMonster {
    public BaseFlyingMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
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

    /**
     * 1.21 的飞行怪接触伤害属于实体本身，而不是某个追击动作。
     *
     * <p>因此行为树切换到施法或等待时，已经取得目标的实体仍按同一冷却检测身体碰撞。
     * 黄蜂等明确禁用接触攻击的远程实体应覆盖本方法。</p>
     */
    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    public static AttributeSupplier.Builder createFlyingAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 18.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    /**
     * 飞行怪默认始终使用无重力物理。
     *
     * <p>仅在构造器设置标志并不可靠，读档或外部逻辑仍可能改动它。需要阶段性落地的
     * 特殊飞行怪应自行覆盖本方法。</p>
     */
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean causeFallDamage(
            float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return hasPushableBody() && super.isPushable();
    }

    /**
     * 指示具体实体是否保留普通生物的推动行为。
     *
     * <p>1.21 的飞行预制体默认不可推动，但妖精、黄蜂及部分穿墙生物并未使用该预制体，
     * 因此不能在公共飞行基类中统一抹平差异。</p>
     */
    protected boolean hasPushableBody() {
        return false;
    }

    /**
     * 供同包行为测试核对实体是否真正启用了穿墙移动。
     *
     * <p>穿墙是幽灵类生物的独立行为，不能因为实体会飞就统一开启。该方法保持包级可见，
     * 避免把内部碰撞开关扩散成公共 API。</p>
     */
    boolean isPhasingThroughBlocks() {
        return noPhysics;
    }
}
