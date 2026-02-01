package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;

public class RollingCactusBoulderEntity extends BoulderEntity {
    public RollingCactusBoulderEntity(EntityType<RollingCactusBoulderEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
        this.minimumBreakSpeed = 0.05;
        this.speed = 0.3;
    }

    public RollingCactusBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.ROLLING_CACTUS_BOULDER.get(), level, pos, blockState);
        this.minimumBreakSpeed = 0.05;
        this.speed = 0.3;
    }

    @Override
    public void onRemove() {
        if (level() instanceof ServerLevel serverLevel) {
            int count = random.nextInt(6, 13);
            float y = random.nextFloat() * Mth.PI;
            float d = Mth.TWO_PI / count;
            for (int i = 0; i < count; i++) {
                float x = -Mth.nextFloat(random, Mth.PI * 0.25F, Mth.PI * 0.4F);
                SpikeProjectile projectile = new SpikeProjectile(ModEntities.ROLLING_CACTUS_SPIKE.get(), level());
                float cos = Mth.cos(x);
                float f = -Mth.sin(y) * cos;
                float f1 = -Mth.sin(x);
                float f2 = Mth.cos(y) * cos;
                projectile.setPos(position().add(0.0, 1.25, 0.0));
                projectile.shoot(f, f1, f2, 0.4F, 0.1F);
                level().addFreshEntity(projectile);
                y += d;
            }
            BlockPos pos = blockPosition();
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CACTUS.defaultBlockState())
                    .setPos(pos), getX(), getY() + 0.5, getZ(), 175, 0.0, 0.0, 0.0, 0.15);
            serverLevel.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 5.0F, 1.0F);
        }
        discard();
    }

    public static class SpikeProjectile extends Projectile {
        public SpikeProjectile(EntityType<? extends Projectile> entityType, Level level) {
            super(entityType, level);
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder builder) {}

        @Override
        public void tick() {
            super.tick();
            Vec3 vec3 = getDeltaMovement();
            move(MoverType.SELF, vec3);
            Vec3 motion = getDeltaMovement();
            if (motion.x != vec3.x || motion.y != vec3.y || motion.z != vec3.z) {
                discard();
            } else if (!level().isClientSide) {
                if (ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity) instanceof EntityHitResult entityHitResult) {
                    Entity entity = entityHitResult.getEntity();
                    if (entity.hurt(damageSources().cactus(), 8.0F)) {
                        VectorUtils.knockBackA2B(this, entity, 1.0, 0.2);
                    }
                }
            }
            setDeltaMovement(motion.add(0.0, -0.08, 0.0));
            if (tickCount > 200) discard();
        }

        @Override
        protected boolean canHitEntity(Entity target) {
            return target.canBeHitByProjectile() && target != getOwner();
        }
    }
}
