package org.confluence.mod.common.item.lance;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.entity.projectile.StormSpearShotProjectile;
import org.confluence.mod.common.init.ModEntities;
import software.bernie.geckolib.animation.EasingType;

public class StormSpearItem extends AbstractLanceItem {
    public static final double knockBackScale = 0.6;
    public static final double knockBackMotionY = 0.1;

    public StormSpearItem() {
        super(new Properties().attributes(attributes(3, 3.9F)), ModRarity.BLUE, 15, 5, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.25, 6, EasingType.EASE_OUT_BACK),
                K.of(0.5, -16, EasingType.EASE_IN_EXPO),
                K.of(0.75, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        VectorUtils.knockBackA2B(owner, victim, knockBackScale, knockBackMotionY);
    }

    @Override
    protected void onStartSting(ItemStack stack, ServerLevel level, LivingEntity owner) {
        Vec3 viewVector = owner.getViewVector(1.0F);
        StormSpearShotProjectile projectile = new StormSpearShotProjectile(ModEntities.STORM_SPEAR_SHOT_PROJECTILE.get(), level);
        projectile.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        projectile.setDamage((float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F);
        projectile.shoot(viewVector.x, viewVector.y, viewVector.z, 1.16F, 0);
        level.addFreshEntity(projectile);
    }
}
