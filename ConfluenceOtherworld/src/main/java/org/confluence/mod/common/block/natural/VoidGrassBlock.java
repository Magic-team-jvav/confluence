package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.function.Supplier;

public class VoidGrassBlock extends EndGrassBlock {
    public VoidGrassBlock(Supplier<? extends Block> degenerateTo) {
        super(degenerateTo);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide && entity instanceof Player player) {
            Vec3 vec3 = player.getDeltaMovement();
            if (vec3.lengthSqr() > 1e-6 && level.getGameTime() % 7 == 0) {
                RandomSource random = level.getRandom();
                MolangParticleEngine.INSTANCE.addEmitter(new ParticleEmitter(level, player.position().add(random.nextFloat() - 0.5 + vec3.x, 0, random.nextFloat() - 0.5 + vec3.z), Confluence.asResource("void_step")));
            }
        }
    }
}
