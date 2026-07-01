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
 * 军火商 —— 使用弩攻击敌人，射程 10。
 */
public class ArmsDealerNPC extends BaseNPC implements RangedAttackMob {

    public ArmsDealerNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RangedBowAttackGoal<>(this, 0.6, 20, 10));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        AbstractArrow projectile = ProjectileUtil.getMobArrow(this,
                new ItemStack(Items.ARROW), velocity, new ItemStack(Items.CROSSBOW));
        double dx = target.getX() - getX();
        double dy = target.getY(0.333) - projectile.getY();
        double dz = target.getZ() - getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        projectile.shoot(dx, dy + dist * 0.2, dz, 3.0f, (14 - level().getDifficulty().getId() * 4) / 10f);
        playSound(SoundEvents.CROSSBOW_SHOOT, 1, 1 / (getRandom().nextFloat() * 0.4f + 0.8f));
        level().addFreshEntity(projectile);
    }
}
