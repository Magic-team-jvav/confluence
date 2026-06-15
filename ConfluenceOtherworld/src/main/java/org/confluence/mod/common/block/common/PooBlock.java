package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModEffects;

public class PooBlock extends Block {
    public PooBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity living && !living.hasEffect(ModEffects.STINKY.get())) {
            living.addEffect(new MobEffectInstance(ModEffects.STINKY.get(), 60));
            level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {}
}
