package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.worldgen.secret_seed.ModSecretSeeds;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

@javax.annotation.ParametersAreNonnullByDefault
@net.minecraft.MethodsReturnNonnullByDefault
public class FallingStarItemEntity extends ItemEntity {
    private boolean wasOnGround;
    private ParticleEmitter emitter;

    public FallingStarItemEntity(EntityType<FallingStarItemEntity> entityType, Level level) {
        super(entityType, level);
        this.wasOnGround = false;
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
                if (!wasOnGround) {
                    this.wasOnGround = true;
                    level().playSound(null, getX(), getY(), getZ(), ModSoundEvents.STAR_LANDS.get(), SoundSource.AMBIENT, 2.0F, 1.0F);
                }
            } else if (!wasOnGround && !level().getBlockState(getOnPos().below(6)).isAir()) {
                level().playSound(null, getX(), getY(), getZ(), ModSoundEvents.STAR.get(), SoundSource.AMBIENT, 2.0F, 1.0F);
            } else if (level() instanceof ServerLevel serverLevel && ModSecretSeeds.DONT_DIG_UP.match(serverLevel)) {
                if (ProjectileUtil.getHitResultOnMoveVector(this, entity -> true) instanceof EntityHitResult entityHitResult) {
                    entityHitResult.getEntity().hurt(ModDamageTypes.of(level(), ModDamageTypes.FALLING_STAR), 100);
                    discard();
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("wasOnGround", wasOnGround);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.wasOnGround = pCompound.getBoolean("wasOnGround");
    }

    public static void summon(ServerLevel serverLevel) {
        if (serverLevel.getDayTime() % 24000 > 12000 && serverLevel.getGameTime() % 600 == 0) {
            RandomSource random = serverLevel.random;
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                int offsetX = (random.nextBoolean() ? 1 : -1) * random.nextInt(2);
                int offsetZ = (random.nextBoolean() ? 1 : -1) * random.nextInt(2);
                BlockPos pos = serverPlayer.getOnPos().offset(offsetX, 0, offsetZ).atY(256);
                if (serverLevel.isLoaded(pos)) {
                    serverLevel.addFreshEntity(new FallingStarItemEntity(serverLevel, pos.getCenter()));
                }
            }
        }
    }
}
