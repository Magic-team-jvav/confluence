package org.confluence.mod.common.block.functional;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.common.init.TCEffects;

public class StepOnTrapBlock extends Block {
    public static final Behaviour SHIMMER = new Behaviour() {
        private static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

        @Override
        protected void onStep(Level level, BlockPos pos, BlockState state, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ModEffects.SHIMMER, MobEffectInstance.INFINITE_DURATION));
            }
        }

        @Override
        protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return SHAPE;
        }
    };
    public static final Behaviour GRAVITATION = new Behaviour() {
        @Override
        protected void onStep(Level level, BlockPos pos, BlockState state, Entity entity) {
            if (!level.isClientSide && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(TCEffects.GRAVITATION, 100, 1));
            }
        }
    };
    public static final Behaviour PNEUMATIC = new Behaviour() {
        private static final Vec3 FLIGHT = new Vec3(0.0, 2.5, 0.0);

        @Override
        protected void onStep(Level level, BlockPos pos, BlockState state, Entity entity) {
            if (level.isClientSide) {
                if (entity instanceof LocalPlayer) {
                    entity.addDeltaMovement(FLIGHT);
                }
            }
        }
    };

    private final Behaviour behaviour;

    public StepOnTrapBlock(Properties properties, Behaviour behaviour) {
        super(properties);
        this.behaviour = behaviour;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return behaviour.getCollisionShape(state, level, pos, context);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        behaviour.onStep(level, pos, state, entity);
    }

    public static abstract class Behaviour {
        protected abstract void onStep(Level level, BlockPos pos, BlockState state, Entity entity);

        protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return Shapes.block();
        }
    }
}
