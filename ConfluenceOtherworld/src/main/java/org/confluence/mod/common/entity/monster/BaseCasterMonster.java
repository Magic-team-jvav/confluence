package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.CasterCycleAction;
import org.confluence.mod.common.entity.projectile.StraightMonsterProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;

/// 法师怪物基类：三次远程施法后向目标方向重新选取安全落点。
///
/// 施法生成具有飞行时间和方块碰撞的真实弹幕。子类只需覆盖
/// {@link #projectileType()} 就能选择自己的法术类型，攻击节奏和瞬移流程不必复制。
public abstract class BaseCasterMonster extends BaseMonster {
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("attack.cast");
    private final CasterCycleAction.HurtResponse hurtResponse;
    private CasterCycleAction cycleAction;

    public BaseCasterMonster(EntityType<? extends BaseCasterMonster> type, Level level) {
        this(type, level, CasterCycleAction.HurtResponse.CONTINUE_CYCLE);
    }

    public BaseCasterMonster(EntityType<? extends BaseCasterMonster> type, Level level, CasterCycleAction.HurtResponse hurtResponse) {
        super(type, level);
        this.hurtResponse = hurtResponse;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected BTRoot createBT() {
        if (cycleAction == null) {
            cycleAction = new CasterCycleAction(this, this::createProjectile, hurtResponse, casterTiming(), castsPerCycle(), projectilesPerVolley(), projectileIntervalTicks());
        }
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return cycleAction;
            }
        };
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted && cycleAction != null && shouldInterruptCastingAfterHurt()) {
            cycleAction.interruptAfterHurt();
        }
        return accepted;
    }

    /// 决定本次有效受击是否打断施法周期。
    protected boolean shouldInterruptCastingAfterHurt() {
        return true;
    }

    /// 返回当前法师固定使用的弹幕类型。
    protected abstract EntityType<? extends StraightMonsterProjectile> projectileType();

    /// 返回一次施法动作连续生成的弹幕数量；普通法师每轮只生成一枚。
    protected int projectilesPerVolley() {
        return 1;
    }

    /// 返回每次传送之间的施法动作次数；普通法师保持三次。
    protected int castsPerCycle() {
        return 3;
    }

    /// 返回同一轮内相邻弹幕的间隔。
    protected int projectileIntervalTicks() {
        return 1;
    }

    /// 返回当前法师的完整战斗时序；只有资料明确存在独立周期的变体需要覆盖。
    protected CasterCycleAction.Timing casterTiming() {
        return CasterCycleAction.DEFAULT_TIMING;
    }

    @Nullable
    StraightMonsterProjectile createProjectile(LivingEntity target) {
        StraightMonsterProjectile projectile = projectileType().create(level());
        if (projectile == null) {
            return null;
        }
        projectile.configure(this, target, projectileDamage());
        return projectile;
    }

    protected float projectileDamage() {
        return (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public void beginCastAnimation() {
        stopTriggeredAnimation("caster_state", "cast");
        triggerAnim("caster_state", "cast");
    }

    public void cancelCastAnimation() {
        stopTriggeredAnimation("caster_state", "cast");
    }

    public void playCastReleaseSound() {
        playSound(getCastSound(), 1.0F, 1.0F);
    }

    protected SoundEvent getCastSound() {
        return ModSoundEvents.REGULAR_STAFF_SHOOT.get();
    }

    protected RawAnimation getRestAnimation(boolean moving) {
        return moving ? WALK : IDLE;
    }

    /// 施法由战斗周期触发一次，播放结束后再恢复移动或待机，不受普通挥手时长截断。
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "caster_state", 0, state -> state.setAndContinue(getRestAnimation(state.isMoving()))).triggerableAnim("cast", CAST));
    }

}
