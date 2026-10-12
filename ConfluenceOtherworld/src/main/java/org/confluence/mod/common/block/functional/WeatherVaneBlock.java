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
import org.confluence.mod.common.wind.WindMath;
import org.confluence.mod.common.wind.WindTier;
import org.jetbrains.annotations.Nullable;

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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public static class BEntity extends BlockEntity {
        private static final float INACCURACY = 0.3F / 20.0F;
        /// 追赶系数：每 tick 消除剩余角差的 20%。
        private static final float CHASE = 0.2F;
        /// 误差小于该值就直接贴住目标，避免无限逼近。
        private static final float SNAP = Mth.PI * 0.05F;
        /// 只有转向超过该角度才抖一下（约 3 度）。低于它的只是风的自然漂移，不该触发抖动。
        private static final float SHAKE_TRIGGER = 0.05F;
        /// 抖动最多持续多少 tick。
        private static final int MAX_SHAKE_TICKS = 10;

        /// 只在转向明显时用来触发一次抖动，记录上一 tick 的风向角。
        private float lastWindAngle = Float.NaN;
        private int shakeTime = 0;
        /// 期望朝向。与 {@link #rotation} 同处「连续域」（不做 ±π 归一），这样二者之差永远是最近角差。
        private float targetRotation = 0.0F;
        public float rotationO = 0.0F;
        public float rotation = 0.0F;
        public float shakeO = 0.0F;
        public float shake = 0.0F;

        public BEntity(BlockPos pos, BlockState blockState) {
            super(FunctionalBlocks.WEATHER_VANE_ENTITY.get(), pos, blockState);
        }

        /// 每 tick 都要跑完整套动画。
        ///
        /// 这里最容易踩的三个坑（都踩过）：
        /// 1. **不能因为"风向没变"就提前 return** —— 那样追赶与抖动动画只在收到新包的那几 tick 才推进，
        ///    风标会变成每秒跳 4 下的抽动。
        /// 2. **必须用最短角差**。风向扫过正西时 `windAngle` 会在 ±π 之间跳 2π，
        ///    直接相减会让风标整整倒转一圈，看起来就是剧烈抖动。
        /// 3. **抖动不能每次更新都重新触发**。旧实现每 tick 重设 `shakeTime`，
        ///    于是风标的摆动被无限续期，变成持续振动；现在只有"转向超过阈值且当前没在抖"才触发一次。
        public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, BEntity entity) {
            // 静风：停住且不晃，避免没风的时候乱转
            if (WeatherHandler.windExposedSpeed() < WindTier.CALM.max()) {
                entity.shake = 0.0F;
                entity.shakeO = 0.0F;
                entity.shakeTime = 0;
                return;
            }

            float windAngle = WeatherHandler.windAngle();
            // 角度约定：风向向量 = (cos θ, sin θ) 映射到 (x, z)，所以方位角就是 θ 本身。
            // 减去 θ 是因为风标模型的 0 度指向 +Z。取最近角差，避免跨 ±π 时绕远路。
            float delta = WindMath.wrapRadians(Mth.HALF_PI - windAngle - entity.rotation);
            entity.targetRotation = entity.rotation + delta;

            // 只有真正转向、且当前没在抖时才抖一次
            if (entity.shakeTime <= 0 && !Float.isNaN(entity.lastWindAngle)) {
                float turned = Mth.abs(WindMath.wrapRadians(windAngle - entity.lastWindAngle));
                if (turned > SHAKE_TRIGGER) {
                    entity.shakeTime = Mth.clamp((int) (turned * 10.0F), 1, MAX_SHAKE_TICKS);
                }
            }
            entity.lastWindAngle = windAngle;

            if (Mth.abs(delta) > SNAP) {
                entity.rotationO = entity.rotation;
                entity.shakeO = entity.shake;
                entity.shake = delta * CHASE;
                entity.rotation += entity.shake;
            } else {
                entity.rotationO = entity.rotation;
                entity.rotation = entity.targetRotation;
                if (entity.shakeTime > 0) {
                    entity.shakeO = entity.shake;
                    entity.shake = Mth.sin((level.getGameTime() % 360L) * Mth.DEG_TO_RAD * 30) * (entity.shakeTime * INACCURACY);
                    entity.shakeTime--;
                } else {
                    entity.shake = 0.0F;
                    entity.shakeO = 0.0F;
                }
            }
        }
    }
}
