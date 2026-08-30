package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WanderDashCycleAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 蚁狮蜂及巨型蚁狮蜂共用的游走—冲刺实现。
///
/// 发现玩家后先保持三维游走，再锁定一次目标方向进行直线冲刺；玩家在冲刺开始后
/// 横向躲避不会让实体瞬间转弯。碰撞或阶段结束会返回游走状态。两个注册变种共享行为
/// 和动画资源，仅由注册尺寸与属性表表达体型、强度差异。
public class AntlionSwarmer extends ReboundingFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private final WanderDashCycleAction combatCycle;

    public AntlionSwarmer(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        setDiscardFriction(true);
        combatCycle = new WanderDashCycleAction(this, 100, 100, 0.3, 0.2);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 25.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return combatCycle;
            }
        };
    }

    boolean isDashing() {
        return combatCycle.isDashing();
    }

    Vec3 getDashDirection() {
        return combatCycle.getDashDirection();
    }

    @Override
    protected Vec3 reboundVelocity(Vec3 requested, Vec3 allowed) {
        double x = allowed.x == requested.x ? requested.x : -requested.x;
        double y = allowed.y == requested.y ? requested.y : -requested.y;
        double z = allowed.z == requested.z ? requested.z : -requested.z;
        if (x != requested.x || y != requested.y || z != requested.z) combatCycle.abortDash();
        return new Vec3(x, y, z);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ANTLION_SWARMER_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ANTLION_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ANTLION_SWARMER_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 2, state -> state.setAndContinue(FLY)));
    }
}
