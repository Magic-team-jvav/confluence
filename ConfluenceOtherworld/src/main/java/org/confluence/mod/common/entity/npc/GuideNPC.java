package org.confluence.mod.common.entity.npc;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * 向导 —— 使用弓攻击敌人。
 */
public class GuideNPC extends BaseNPC implements RangedAttackMob {

    public GuideNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RangedBowAttackGoal<>(this, 0.5, 20, 15));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        AbstractArrow projectile = ProjectileUtil.getMobArrow(this,
                new ItemStack(Items.ARROW), velocity, new ItemStack(Items.BOW));
        double dx = target.getX() - getX();
        double dy = target.getY(0.333) - projectile.getY();
        double dz = target.getZ() - getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        projectile.shoot(dx, dy + dist * 0.2, dz, 2.4f, (14 - level().getDifficulty().getId() * 4) / 10f);
        playSound(SoundEvents.ARROW_SHOOT, 1, 1 / (getRandom().nextFloat() * 0.4f + 0.8f));
        level().addFreshEntity(projectile);
    }
}
