package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.projectile.SuperSpikyBallProjectile;

import java.util.concurrent.atomic.AtomicInteger;

public class SpikyBallTrapBlock extends AbstractDispenserMechanicalBlock {
    private static final double sigma = Mth.square(50.0 / 8.0);

    public SpikyBallTrapBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean behaviour(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        AtomicInteger value = new AtomicInteger();
        pLevel.getProfiler().incrementCounter("getEntities");
        pLevel.getEntities().get(EntityTypeTest.forClass(SuperSpikyBallProjectile.class), projectile -> {
            double sqr = pPos.distToCenterSqr(projectile.position());
            int newValue = value.get();

            if (sqr <= sigma) newValue += 50;
            else if (sqr <= sigma * 2) newValue += 15;
            else if (sqr <= sigma * 4) newValue += 10;
            else if (sqr <= sigma * 6) newValue += 8;
            else if (sqr <= sigma * 8) newValue += 6;
            else if (sqr <= sigma * 10) newValue += 5;
            else if (sqr <= sigma * 14) newValue += 4;
            else if (sqr <= sigma * 18) newValue += 3;
            else if (sqr <= sigma * 24) newValue += 2;
            else newValue += 1;

            if (newValue >= 200) {
                return AbortableIterationConsumer.Continuation.ABORT;
            }
            value.set(newValue);
            return AbortableIterationConsumer.Continuation.CONTINUE;
        });
        if (value.get() >= 200) return false;
        Direction direction = pState.getValue(FACING);
        double x = pPos.getX() + 0.5 + 1.7 * direction.getStepX(); // 可以穿过一个遮挡方块
        double y = pPos.getY() + 0.5 + 1.7 * direction.getStepY();
        double z = pPos.getZ() + 0.5 + 1.7 * direction.getStepZ();
        SuperSpikyBallProjectile projectile = new SuperSpikyBallProjectile(pLevel);
        projectile.setPos(x, y, z);
        projectile.shoot(
                direction.getStepX() + Mth.nextDouble(pLevel.random, -0.1, 0.1),
                direction.getStepY() + Mth.nextDouble(pLevel.random, -0.1, 0.1),
                direction.getStepZ() + Mth.nextDouble(pLevel.random, -0.1, 0.1),
                1.0F, 0.0F);
        return pLevel.addFreshEntity(projectile);
    }

    @Override
    protected int delay() {
        return 100;
    }
}
