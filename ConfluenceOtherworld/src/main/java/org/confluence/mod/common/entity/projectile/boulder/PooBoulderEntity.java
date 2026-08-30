package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.MobEffectInstanceData;
import org.confluence.lib.util.damage.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.util.TerraStyleExplosion;

public class PooBoulderEntity extends BoulderEntity {
    public PooBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PooBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.POO_BOULDER.get(), level, pos, blockState);
    }

    @Override
    protected void removeEffect(ServerLevel serverLevel) {
        super.removeEffect(serverLevel);
        int blockRadius = 2;

        final BlockState toFill = ModBlocks.POO.get().defaultBlockState();
        final BlockPos blockPos = blockPosition();

        BlockPos.MutableBlockPos mutable = blockPos.mutable();
        int radiusSqr = Mth.square(blockRadius);
        for (int i = -blockRadius; i < blockRadius; i++) {
            int x = blockPos.getX() + i;
            for (int j = -blockRadius; j < blockRadius; j++) {
                int y = blockPos.getY() + j;
                for (int k = -blockRadius; k < blockRadius; k++) {
                    int z = blockPos.getZ() + k;
                    mutable.set(x, y, z);
                    if (mutable.distSqr(blockPos) <= radiusSqr && serverLevel.getBlockState(mutable).isEmpty()) {
                        serverLevel.setBlockAndUpdate(mutable, toFill);
                    }
                }
            }
        }
        TerraStyleExplosion.terraExplode(serverLevel, this, Explosion.getDefaultDamageSource(level(), this), new MultiplyExplosionDamageCalculator(0.9F), getX(), getY(), getZ(), blockRadius, Level.ExplosionInteraction.NONE);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstanceData(ModEffects.STINKY, 20 * 10, 1).create());
            livingEntity.addEffect(new MobEffectInstanceData(MobEffects.POISON, 20 * 10).create());
        }
    }
}
