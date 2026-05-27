package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.function.Supplier;

public class VoidGrassBlock extends EndGrassBlock{

    ResourceLocation particleId = Confluence.asResource("void_step");

    public VoidGrassBlock(Supplier<? extends Block> degenerateTo) {
        super(degenerateTo);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!(entity instanceof Player)) return;
        Vec3 playerMove = entity.getDeltaMovement();
        float speed = (float) (playerMove.z * playerMove.z + playerMove.x * playerMove.x);
        if (speed < 0.01) return;
        if (level.getGameTime() % 7 == 0) {
            RandomSource random = level.getRandom();
            Vec3 playerPos = entity.getPosition(0);
            ParticleEmitter emitter = new ParticleEmitter(level,
                    playerPos.add(random.nextFloat() - 0.5 + playerMove.x, 0, random.nextFloat() - 0.5 + playerMove.z),
                    particleId, MolangExp.EMPTY);
            MolangParticleEngine.INSTANCE.addEmitter(emitter, false);
        }
    }
}
