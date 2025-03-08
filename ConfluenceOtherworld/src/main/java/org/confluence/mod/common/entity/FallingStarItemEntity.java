package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.item.MaterialItems;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.HashSet;
import java.util.Set;

public class FallingStarItemEntity extends ItemEntity {
    private static final EntityDataAccessor<Boolean> DATA_WAS_ON_GROUND = SynchedEntityData.defineId(FallingStarItemEntity.class, EntityDataSerializers.BOOLEAN);
    private ParticleEmitter emitter;

    public FallingStarItemEntity(EntityType<FallingStarItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FallingStarItemEntity(Level level, Vec3 pos) {
        this(ModEntities.FALLING_STAR_ITEM_ENTITY.get(), level);
        setPos(pos);
        setDeltaMovement(level.random.nextDouble(), -8.0, level.random.nextDouble());
        setItem(MaterialItems.FALLING_STAR.get().getDefaultInstance());
        this.lifespan = 12000;
        setNeverPickUp();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_WAS_ON_GROUND, false);
    }

    @Override
    public void tick() {
        if (level().isClientSide && emitter == null) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("falling_star"));
            emitter.attached = this;
            PSGameClient.LOADER.addEmitter(emitter, false);
        }
        if (level().getDayTime() % 24000 < 12000) {
            discard();
        } else {
            super.tick();
            if (onGround()) {
                if (hasPickUpDelay()) setNoPickUpDelay();
                if (!wasOnGround()) {
                    setWasOnGround(true);
                    level().playSound(null, getX(), getY(), getZ(), ModSoundEvents.STAR_LANDS.get(), SoundSource.NEUTRAL, 2.0F, 1.0F);
                }
            } else if (!wasOnGround() && !level().getBlockState(getOnPos().below(6)).isAir()) {
                level().playSound(null, getX(), getY(), getZ(), ModSoundEvents.STAR.get(), SoundSource.NEUTRAL, 2.0F, 1.0F);
            } else if (level() instanceof ServerLevel serverLevel && ModSecretSeeds.DONT_DIG_UP.match(serverLevel)) {
                if (ProjectileUtil.getHitResultOnMoveVector(this, entity -> true) instanceof EntityHitResult entityHitResult) {
                    entityHitResult.getEntity().hurt(ModDamageTypes.of(level(), ModDamageTypes.FALLING_STAR), 100);
                    discard();
                }
            }
        }
    }

    public void setWasOnGround(boolean was) {
        entityData.set(DATA_WAS_ON_GROUND, was);
    }

    public boolean wasOnGround() {
        return entityData.get(DATA_WAS_ON_GROUND);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("wasOnGround", wasOnGround());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setWasOnGround(pCompound.getBoolean("wasOnGround"));
    }

    public static void summon(ServerLevel level) {
        if (level.getDayTime() % 24000 > 12000 && level.getGameTime() % CommonConfigs.fallingStarFrequency == 0) {
            RandomSource random = level.random;
            Set<Vec3> cache = new HashSet<>();
            for (ServerPlayer serverPlayer : level.players()) {
                if (cache.stream().anyMatch(pos -> serverPlayer.distanceToSqr(pos) < Mth.square(serverPlayer.requestedViewDistance() * 16))) {
                    continue;
                }
                int offsetX = (random.nextBoolean() ? 1 : -1) * random.nextInt(2);
                int offsetZ = (random.nextBoolean() ? 1 : -1) * random.nextInt(2);
                BlockPos pos = serverPlayer.getOnPos().offset(offsetX, 0, offsetZ).atY(256);
                if (level.isLoaded(pos)) {
                    level.addFreshEntity(new FallingStarItemEntity(level, pos.getCenter()));
                    cache.add(serverPlayer.position());
                }
            }
        }
    }
}
