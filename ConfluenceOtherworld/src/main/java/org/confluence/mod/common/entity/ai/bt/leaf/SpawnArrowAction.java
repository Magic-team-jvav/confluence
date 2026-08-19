package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

import java.util.function.DoubleSupplier;

/// 让持弓敌怪生成一支遵循原版飞行与碰撞规则的箭。
///
/// <p>普通弓手使用 {@link #mobBowShot(Mob, float, float)}，其箭伤害、弹道高度和难度散布
/// 与 1.21 侧的人形弓手一致。显式伤害构造器仅供确实需要固定伤害的特殊行为使用。</p>
public final class SpawnArrowAction extends BTNode {
    private final Mob shooter;
    private final DoubleSupplier explicitDamage;
    private final float bowPower;
    private final float velocity;
    private final Float explicitInaccuracy;
    private boolean done;

    public SpawnArrowAction(Mob shooter, double damage, float velocity, float inaccuracy) {
        this(shooter, () -> damage, 1.0F, velocity, inaccuracy);
    }

    private SpawnArrowAction(Mob shooter, DoubleSupplier explicitDamage, float bowPower, float velocity, Float explicitInaccuracy) {
        this.shooter = shooter;
        this.explicitDamage = explicitDamage;
        this.bowPower = bowPower;
        this.velocity = velocity;
        this.explicitInaccuracy = explicitInaccuracy;
    }

    public static SpawnArrowAction usingAttackDamage(Mob shooter, double multiplier, float velocity, float inaccuracy) {
        return new SpawnArrowAction(shooter, () -> shooter.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier, 1.0F, velocity, inaccuracy);
    }

    public static SpawnArrowAction mobBowShot(Mob shooter, float bowPower, float velocity) {
        return new SpawnArrowAction(shooter, null, bowPower, velocity, null);
    }

    @Override
    public void start() {
        done = false;
    }

    @Override
    public BTStatus execute() {
        if (done) {
            return BTStatus.SUCCESS;
        }
        LivingEntity target = shooter.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }

        AbstractArrow arrow = createArrow();
        double dx = target.getX() - shooter.getX();
        double dy = target.getY(1.0 / 3.0) - arrow.getY();
        double dz = target.getZ() - shooter.getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float inaccuracy = explicitInaccuracy != null
                ? explicitInaccuracy
                : 14.0F - shooter.level().getDifficulty().getId() * 4.0F;
        arrow.shoot(dx, dy + horizontalDistance * 0.20000000298023224, dz, velocity, inaccuracy);
        if (!shooter.level().addFreshEntity(arrow)) {
            return BTStatus.FAILURE;
        }
        shooter.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (shooter.getRandom().nextFloat() * 0.4F + 0.8F));
        done = true;
        return BTStatus.SUCCESS;
    }

    private AbstractArrow createArrow() {
        if (explicitDamage == null) {
            return ProjectileUtil.getMobArrow(shooter, new ItemStack(Items.ARROW), bowPower);
        }
        Arrow arrow = new Arrow(shooter.level(), shooter);
        arrow.setBaseDamage(explicitDamage.getAsDouble());
        return arrow;
    }
}
