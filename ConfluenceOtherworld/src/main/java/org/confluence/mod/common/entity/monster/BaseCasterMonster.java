package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 法师怪物基类：三次远程施法后向目标方向重新选取安全落点。
///
/// <p>施法生成具有飞行时间和方块碰撞的真实弹幕。子类只需覆盖
/// {@link #projectileType()} 就能选择自己的法术类型，攻击节奏和瞬移流程不必复制。</p>
public abstract class BaseCasterMonster extends BaseMonster {
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private final CycleMode cycleMode;
    private CasterCycleAction sharedCycleAction;

    public BaseCasterMonster(EntityType<? extends BaseCasterMonster> type, Level level) {
        this(type, level, CycleMode.SHARED_1_21);
    }

    protected BaseCasterMonster(EntityType<? extends BaseCasterMonster> type, Level level, CycleMode cycleMode) {
        super(type, level);
        this.cycleMode = cycleMode;
    }

    public static AttributeSupplier.Builder createCasterAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected BTRoot createBT() {
        if (cycleMode == CycleMode.SHARED_1_21) {
            if (sharedCycleAction == null) {
                sharedCycleAction = new CasterCycleAction(this, this::createProjectile);
            }
            return createSharedCycleTree(sharedCycleAction);
        }
        return createLegacyCycleTree();
    }

    private BTRoot createSharedCycleTree(CasterCycleAction cycleAction) {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(cycleAction, new RandomStrollAction(BaseCasterMonster.this, 0.8, 8));
            }
        };
    }

    private BTRoot createLegacyCycleTree() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(BaseCasterMonster.this),
                                new WaitAction(20),
                                new SpawnProjectileAction(
                                        BaseCasterMonster.this,
                                        BaseCasterMonster.this
                                                ::createImmediateProjectile),
                                new WaitAction(50),
                                new SpawnProjectileAction(
                                        BaseCasterMonster.this,
                                        BaseCasterMonster.this
                                                ::createImmediateProjectile),
                                new WaitAction(50),
                                new SpawnProjectileAction(BaseCasterMonster.this, BaseCasterMonster.this::createImmediateProjectile),
                                new WaitAction(80),
                                new TeleportNearTargetAction(BaseCasterMonster.this, 20, 5, 8)),
                        new RandomStrollAction(BaseCasterMonster.this, 0.8, 8));
            }
        };
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted && sharedCycleAction != null) {
            sharedCycleAction.interruptAfterHurt();
        }
        return accepted;
    }

    /// 返回当前法师固定使用的弹幕类型。
    protected EntityType<HostileParticleProjectile> projectileType() {
        return ModEntities.DARK_CASTER_PROJECTILE.get();
    }

    HostileParticleProjectile createProjectile(LivingEntity target) {
        HostileParticleProjectile projectile = projectileType().create(level());
        if (projectile == null) {
            return null;
        }
        projectile.configure(this, target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
        return projectile;
    }

    @Override
    public int getCurrentSwingDuration() {
        return 20;
    }

    private HostileParticleProjectile createImmediateProjectile(LivingEntity target) {
        HostileParticleProjectile projectile = createProjectile(target);
        if (projectile != null) {
            swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
        return projectile;
    }

    /// 施法挥手期间播放法术动作，其余时间按实际移动状态选择行走或待机。
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "caster_state", 5, state -> {
            if (swingTime > 0) {
                return state.setAndContinue(DefaultAnimations.ATTACK_CAST);
            }
            return state.setAndContinue(state.isMoving() ? WALK : IDLE);
        }));
    }

    /// 1.21 共有怪使用预施法周期；只存在于 1.20 的旧怪暂时保留当前节奏。
    protected enum CycleMode {
        SHARED_1_21,
        LEGACY_1_20
    }
}
