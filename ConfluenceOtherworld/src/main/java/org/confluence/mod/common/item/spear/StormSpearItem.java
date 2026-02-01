package org.confluence.mod.common.item.spear;

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

public class StormSpearItem extends AbstractSpearItem {
    public static final double knockBackScale = 0.3;
    public static final double knockBackMotionY = 0.1;

    public StormSpearItem() {
        super(new Properties().attributes(attributes(3, 7F)), ModRarity.BLUE, 15, 5, createKeyframes(
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
    protected void onStingTick(ItemStack stack, ServerLevel level, LivingEntity owner, Vec3 tipPos, boolean last) {
        if (last) {
            Vec3 viewVector = owner.getViewVector(1.0F);
            StormSpearShotProjectile projectile = new StormSpearShotProjectile(ModEntities.STORM_SPEAR_SHOT_PROJECTILE.get(), level);
            projectile.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
            projectile.setDamage((float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.6F);
            projectile.shoot(viewVector.x, viewVector.y, viewVector.z, 1.92F, 0);
            projectile.setOwner(owner);
            level.addFreshEntity(projectile);
        }
    }
}
