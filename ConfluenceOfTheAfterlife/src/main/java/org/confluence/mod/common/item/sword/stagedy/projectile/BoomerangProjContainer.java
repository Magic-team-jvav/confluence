package org.confluence.mod.common.item.sword.stagedy.projectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.projectile.BoomerangProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.sword.Boomerang;
import org.confluence.terra_curio.common.init.TCAttributes;


public class BoomerangProjContainer extends IceSwordProjContainer {
    Boomerang.BoomerangModifier modifier;
    private Projectile projectile;
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

    //todo 生成弹幕
    public Projectile getProjectile(Player player, ItemStack weapon){
        return new BoomerangProjectile(player,modifier,weapon);
    }

    public void genProjectile(Player owner, ItemStack weapon){
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), getSound(), SoundSource.AMBIENT, 1.0F, 1.0F);
        Projectile projectile = getProjectile(owner,weapon);
        projectile.setPos(owner.position().add(0,1.5,0));
        projectile.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, getVelocity(owner), 0.0F);
        owner.level().addFreshEntity(projectile);
    }


    public float getVelocity(LivingEntity living) {
        float velocity = getBaseVelocity();
        AttributeInstance attributeInstance = living.getAttribute(TCAttributes.getRangedVelocity());
        if (attributeInstance != null) return velocity * (float) attributeInstance.getValue();
        return velocity;
    }

}
