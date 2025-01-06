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

public class SpikeBlock extends Block {
    private static final VoxelShape SHAPE = box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0);

    public SpikeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            entity.hurt(ModDamageTypes.of(level, ModDamageTypes.THORN), 12.0F);
            if (entity.isAlive() && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ModEffects.BLEEDING, ModUtils.switchByDifficulty(level, 200, 400, 500)));
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
