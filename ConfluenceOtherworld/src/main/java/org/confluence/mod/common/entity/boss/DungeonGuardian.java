package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModDamageTypes;

/**
 * 地牢守卫。
 *
 * <p>该实体不是常规 Boss 战，而是阻止玩家过早深入地牢的追杀单位。它不会显示 Boss 条、
 * 不发送 Boss 战败消息、不保存到区块，并以固定速度直接追向玩家。生成后的五十 tick 内若
 * 始终找不到存活玩家便立即撤销，避免触发方离场后留下无目标守卫。</p>
 *
 * <p>接触伤害使用独立伤害类型并绕过护甲，不能用普通生物攻击再依赖夸张攻击数值间接模拟；
 * 溺水伤害被明确忽略。行为树只保留永久等待节点，防止通用冲锋动作改写追击速度。</p>
 */
public class DungeonGuardian extends BaseBoss {
    private static final int INITIAL_PLAYER_CHECK_TICKS = 50;
    private static final double PURSUIT_SPEED = 0.80;

    private int playerCheckTicks = INITIAL_PLAYER_CHECK_TICKS;

    public DungeonGuardian(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 0;
    }

    /**
     * 地牢守卫的直线追击不叠加原版重力。
     */
    @Override
    public boolean isNoGravity() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 9999.0)
                .add(Attributes.ATTACK_DAMAGE, 9999.0)
                .add(Attributes.ARMOR, 9999.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 100.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(Integer.MAX_VALUE);
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1,
                new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved() || level().isClientSide) {
            return;
        }

        LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            Vec3 direction = target.position().subtract(position());
            if (direction.lengthSqr() > 1.0E-6) {
                Vec3 velocity = direction.normalize().scale(PURSUIT_SPEED);
                setDeltaMovement(velocity);
                Vec3 lookPosition = position().add(velocity);
                getLookControl().setLookAt(
                        lookPosition.x, lookPosition.y, lookPosition.z,
                        360.0F, 360.0F);
            }
            if (getBoundingBox().inflate(0.25)
                    .intersects(target.getBoundingBox())) {
                doHurtTarget(target);
            }
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.75));
        }

        /*
         * 与 1.21 保持一致：这是生成后的单次玩家存在性检查，并不是失去目标后的脱战倒计时。
         * 成功命中会重新开始一次检查；计数越过零后不会反复扫描或因稍后失去目标而直接撤销。
         */
        if (--playerCheckTicks == 0) {
            Player nearbyPlayer = level().getNearestPlayer(this, 100.0);
            if (nearbyPlayer == null || !nearbyPlayer.isAlive()) {
                discard();
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        DamageSource source = ModDamageTypes.of(
                level(), ModDamageTypes.DUNGEON_GUARDIAN, this);
        boolean hurt = target.hurt(
                source, (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (hurt) {
            playerCheckTicks = INITIAL_PLAYER_CHECK_TICKS;
        }
        return hurt;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_DROWNING)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean shouldShowMessage() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    int getPlayerCheckTicks() {
        return playerCheckTicks;
    }

    @Override
    public boolean causeFallDamage(
            float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
