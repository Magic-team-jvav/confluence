package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.combat.gun.AmmoStats;
import org.confluence.mod.common.combat.gun.Ballistics;
import org.confluence.mod.common.combat.gun.BallisticsResolver;
import org.confluence.mod.common.combat.gun.GunStats;
import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.component.GunPropertyComponent;
import org.confluence.mod.common.entity.npc.ai.NPCRangedAttackGoal;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.item.GunItems;

/// 军火商 —— 使用燧发手枪攻击敌人。
public class ArmsDealerNPC extends BaseNPC implements RangedAttackMob {

    public ArmsDealerNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(GunItems.FLINTLOCK_PISTOL.get()));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new NPCRangedAttackGoal(this, 0.6, 13.0F, 10, 30));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        ItemStack gunStack = getMainHandItem();
        ItemStack bulletStack = new ItemStack(GunItems.MUSKET_BULLET.get());
        GunPropertyComponent gun = gunStack.get(ModDataComponentTypes.GUN_PROPERTY);
        BulletPropertyComponent bullet = bulletStack.get(ModDataComponentTypes.BULLET_PROPERTY);
        if (gun == null || bullet == null) return;
        Ballistics ballistics = BallisticsResolver.resolve(
                new GunStats(gun.damage(), gun.velocity(), gun.knockback(), gun.critical(), gun.penetrate(), 0.0F),
                new AmmoStats(bullet.damage(), bullet.velocity(), bullet.velocityMultiplier(), bullet.knockback(), bullet.penetrate()));
        BaseBulletEntity projectile = new BaseBulletEntity(this, bulletStack);
        projectile.damage = ballistics.damage();
        projectile.knockback = ballistics.knockback();
        projectile.penetrate = ballistics.penetrate();
        double dx = target.getX() - getX();
        double dy = target.getY(0.333) - projectile.getY();
        double dz = target.getZ() - getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        projectile.shoot(dx, dy + dist * 0.2, dz, ballistics.velocity(), ballistics.inaccuracy());
        playSound(GunSounds.getSound(gunStack), 1.0F, 1.0F);
        level().addFreshEntity(projectile);
    }
}
