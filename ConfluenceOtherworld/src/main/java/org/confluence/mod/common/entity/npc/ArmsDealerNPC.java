package org.confluence.mod.common.entity.npc;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/// 军火商 —— 装备弩后攻击敌人，射程 10。
public class ArmsDealerNPC extends BaseNPC implements RangedAttackMob {

    public ArmsDealerNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.3, 20, 10));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Enemy));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (!getMainHandItem().is(Items.CROSSBOW)) return;
        AbstractArrow projectile = ProjectileUtil.getMobArrow(this, new ItemStack(Items.ARROW), velocity);
        double dx = target.getX() - getX();
        double dy = target.getY(0.333) - projectile.getY();
        double dz = target.getZ() - getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        projectile.shoot(dx, dy + dist * 0.2, dz, 2.0F, 14 - level().getDifficulty().getId() * 4);
        playSound(SoundEvents.CROSSBOW_SHOOT, 1, 1 / (getRandom().nextFloat() * 0.4f + 0.8f));
        level().addFreshEntity(projectile);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return getMainHandItem().is(Items.CROSSBOW) && super.canAttack(target);
    }
}
