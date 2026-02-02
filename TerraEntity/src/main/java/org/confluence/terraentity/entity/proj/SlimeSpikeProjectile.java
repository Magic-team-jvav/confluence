package org.confluence.terraentity.entity.proj;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TEEffects;
import org.jetbrains.annotations.Nullable;

public class SlimeSpikeProjectile extends BaseProj<SlimeSpikeProjectile> {

    private static final EntityDataAccessor<Boolean> DATA_HAS_GRAVITY = SynchedEntityData.defineId(SlimeSpikeProjectile.class, EntityDataSerializers.BOOLEAN);

    public SlimeSpikeProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel, @Nullable MobEffectInstance pEffect, ResourceLocation location, boolean hasGravity) {
        super(pEntityType, pLevel, pEffect);
        this.setTexture(location);
        this.entityData.set(DATA_HAS_GRAVITY, hasGravity);

    }

    public static SlimeSpikeProjectile blueSpike(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        return new SlimeSpikeProjectile(pEntityType, pLevel, null, TerraEntity.space("textures/entity/proj/slime_spiked_projectile.png"), true);
    }

    public static SlimeSpikeProjectile jungleSpike(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        return new SlimeSpikeProjectile(pEntityType, pLevel, new MobEffectInstance(MobEffects.POISON, 100), TerraEntity.space("textures/entity/proj/jungle_spiked_projectile.png"), true);
    }

    public static SlimeSpikeProjectile iceSpike(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        return new SlimeSpikeProjectile(pEntityType, pLevel, new MobEffectInstance(TEEffects.FROST_BURN, 100), TerraEntity.space("textures/entity/proj/ice_spiked_projectile.png"), true);
    }

    public void setHasGravity(boolean hasGravity) {
        this.entityData.set(DATA_HAS_GRAVITY, hasGravity);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HAS_GRAVITY, true);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.entityData.get(DATA_HAS_GRAVITY)) {
            this.applyGravity();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.108f;
    }

    @Override
    public int getLifetime() {
        return 30;
    }
}
