package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

public class WeatherVaneBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = box(7.0, 0.0, 7.0, 9.0, 13.0, 9.0);

    public WeatherVaneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? LibUtils.getTicker(blockEntityType, FunctionalBlocks.WEATHER_VANE_ENTITY.get(), BEntity::clientTick) : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public static class BEntity extends BlockEntity {
        private static final float INACCURACY = 0.3F / 20.0F;
        private final Vector2f lastWindSpeed = new Vector2f();
        private int shakeTime = 20;
        private float targetRotation = 0.0F;
        public float rotationO = 0.0F;
        public float rotation = 0.0F;
        public float shakeO = 0.0F;
        public float shake = 0.0F;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.WEATHER_VANE_ENTITY.get(), pos, blockState);
        }

        public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, BEntity entity) {
            float windSpeedX = WeatherHandler.getWindSpeedX();
            float windSpeedZ = WeatherHandler.getWindSpeedZ();
            if (entity.lastWindSpeed.x != windSpeedX || entity.lastWindSpeed.y != windSpeedZ) {
                entity.lastWindSpeed.set(windSpeedX, windSpeedZ).normalize();
                float target = Mth.HALF_PI - (float) Mth.atan2(entity.lastWindSpeed.y, entity.lastWindSpeed.x);
                if (target > entity.targetRotation) {
                    entity.shakeTime = Mth.abs((int) ((entity.targetRotation - target) * 10));
                } else {
                    entity.shakeTime = Mth.abs((int) ((target - entity.targetRotation) * 10));
                }
                entity.targetRotation = target;
            }
            if (entity.rotation == entity.targetRotation) {
                if (entity.shakeTime > 0) {
                    entity.shakeO = entity.shake;
                    entity.shake = Mth.sin((level.getGameTime() % 360L) * Mth.DEG_TO_RAD * 30) * (entity.shakeTime * INACCURACY);
                    entity.shakeTime--;
                } else {
                    entity.shake = 0.0F;
                    entity.shakeO = 0.0F;
                }
            } else {
                entity.rotationO = entity.rotation;
                entity.shakeO = entity.shake;
                float delta = entity.targetRotation - entity.rotation;
                if (Mth.abs(delta) > Mth.PI * 0.05F) {
                    if (entity.targetRotation > entity.rotation) {
                        entity.shake = delta * 0.2F;
                        entity.rotation += entity.shake;
                    } else {
                        entity.shake = (entity.rotation - entity.targetRotation) * 0.2F;
                        entity.rotation -= entity.shake;
                    }
                } else {
                    entity.rotationO = entity.targetRotation;
                    entity.rotation = entity.targetRotation;
                }
            }
        }
    }
}
