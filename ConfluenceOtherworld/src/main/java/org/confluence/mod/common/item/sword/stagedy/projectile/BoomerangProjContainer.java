package org.confluence.mod.common.item.sword.stagedy.projectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.projectile.BoomerangProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.sword.Boomerang;


public class BoomerangProjContainer extends AbstractProjContainer {
    Boomerang.BoomerangModifier modifier;

    public BoomerangProjContainer(Boomerang.BoomerangModifier modifier) {
        this.modifier = modifier;
    }

    public int getCooldown() {
        return modifier.cd;
    }

    public float getDamage() {
        return modifier.damage;
    }

    public float getBaseVelocity() {
        return modifier.flySpeed;
    }

    public SoundEvent getSound(){// todo 默认冰雪剑声音
        return ModSoundEvents.WAVING.get();
    }

    public Projectile getProjectile(LivingEntity living, ItemStack weapon){
        return new BoomerangProjectile(living,modifier,weapon);
    }

    public void genProjectile(LivingEntity living, ItemStack weapon){
        living.level().playSound(living, living.blockPosition(), getSound(), SoundSource.AMBIENT, 1.0F, 1.0F);
        Projectile projectile = getProjectile(living,weapon);
        projectile.setPos(living.position().add(0,1.5,0));
        projectile.shootFromRotation(living, living.getXRot(), living.getYRot(), 0.0F, getVelocity(living), 0.0F);
        living.level().addFreshEntity(projectile);
    }

}
