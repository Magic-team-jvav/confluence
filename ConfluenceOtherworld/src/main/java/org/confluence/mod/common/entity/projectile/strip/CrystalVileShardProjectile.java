package org.confluence.mod.common.entity.projectile.strip;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CrystalVileShardProjectile extends StripedProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CrystalVileShardProjectile(EntityType<? extends StripedProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public CrystalVileShardProjectile(LivingEntity living) {
        super(ModEntities.CRYSTAL_VILE_SHARD_PROJECTILE.get(), living);
    }

    public CrystalVileShardProjectile(LivingEntity living, Vec3 pos) {
        super(ModEntities.CRYSTAL_VILE_SHARD_PROJECTILE.get(), living, pos);
    }

    @Override
    protected void onTouchEntity(EntityHitResult result) {
        result.getEntity().hurt(ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner()), getCalculatedDamage());
    }

    @Override
    protected StripedProjectile createBody(LivingEntity shooter) {
        return new CrystalVileShardProjectile(shooter, position());
    }

    public int getAlpha() {
        return Mth.clamp(255 - tickCount * 255 / ticksForBodyRemove, 0, 255);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
