package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.confluence.lib.util.damage.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.util.TerraStyleExplosion;

public class LavaBoulderEntity extends BoulderEntity {
    public LavaBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LavaBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.LAVA_BOULDER.get(), level, pos, blockState);
    }

    @Override
    protected void removeEffect(ServerLevel serverLevel) {
        super.removeEffect(serverLevel);

        final int blockRadius = 2;
        final BlockState toFill = Fluids.LAVA.defaultFluidState().createLegacyBlock();

        if (!serverLevel.dimensionType().ultraWarm() || toFill.getFluidState().getType().getFluidType() != NeoForgeMod.WATER_TYPE.value()) {
            BlockPos blockPos = blockPosition();
            BlockPos.MutableBlockPos mutable = blockPos.mutable();
            for (int i = -blockRadius; i < blockRadius; i++) {
                int x = blockPos.getX() + i;
                for (int j = 0; j < blockRadius; j++) {
                    int y = blockPos.getY() + j;
                    for (int k = -blockRadius; k < blockRadius; k++) {
                        int z = blockPos.getZ() + k;
                        mutable.set(x, y, z);
                        if (serverLevel.getBlockState(mutable).isEmpty()) {
                            serverLevel.setBlockAndUpdate(mutable, toFill);
                        }
                    }
                }
            }
        }
        TerraStyleExplosion.terraExplode(serverLevel, this, Explosion.getDefaultDamageSource(serverLevel, this), new MultiplyExplosionDamageCalculator(0.9F), getX(), getY(), getZ(), blockRadius, Level.ExplosionInteraction.NONE);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.igniteForTicks(5 * 20);
    }
}
