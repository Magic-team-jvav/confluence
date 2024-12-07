package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;

public class SpikeBlock extends Block {
    private static final VoxelShape SHAPE = box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0);

    public SpikeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!level.isClientSide) {
            entity.hurt(ModDamageTypes.of(level, ModDamageTypes.THORN), 12.0F);
            if (entity.isAlive() && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ModEffects.BLEEDING, ModUtils.switchByDifficulty(level, 200, 400, 500)));
            }
        }
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}
