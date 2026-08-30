package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagicDaggerProjectile extends ThrowableDropSelfProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MagicDaggerProjectile(EntityType<? extends ThrowableDropSelfProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MagicDaggerProjectile(LivingEntity living) {
        super(ModEntities.MAGIC_DAGGER_PROJECTILE.get(), living.level());
    }

    @Override
    public DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity.hurt(getDamageSource(), getCalculatedDamage())) {
            hitSet.add(entity.getUUID());
            this.damage -= deltaDamage;
            VectorUtils.knockBackA2B(this, entity, 0.5, 0.2);
            if (penetrate >= 2) {
                discard();
            } else {
                ++this.penetrate;
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        // super.onHitBlock(result); 跳过创建物品
        BlockState blockstate = level().getBlockState(result.getBlockPos());
        blockstate.onProjectileHit(level(), blockstate, result, this);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    protected void updateRotation() {
        Vec3 vec3 = getDeltaMovement();
        double d0 = vec3.horizontalDistance();
        if (shouldApplyGravity()) {
            setXRot(level().getGameTime() % 720 * 0.5F * Mth.RAD_TO_DEG);
        } else {
            setXRot(lerpRotation(xRotO, (float) (Mth.atan2(vec3.y, d0) * Mth.RAD_TO_DEG)));
        }
        setYRot(lerpRotation(yRotO, (float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
