package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;

public class RollingCactusBoulderEntity extends BoulderEntity {
    public RollingCactusBoulderEntity(EntityType<RollingCactusBoulderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
            // todo 仙人球尖刺
            BlockPos pos = blockPosition();
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CACTUS.defaultBlockState())
                    .setPos(pos), getX(), getY() + 0.5, getZ(), 175, 0.0, 0.0, 0.0, 0.15);
            serverLevel.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 5.0F, 1.0F);
        }
        discard();
    }
}
