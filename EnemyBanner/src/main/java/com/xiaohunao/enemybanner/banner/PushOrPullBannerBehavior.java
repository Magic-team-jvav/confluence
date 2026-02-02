package com.xiaohunao.enemybanner.banner;

import com.xiaohunao.enemybanner.BannerUtils;
import com.xiaohunao.enemybanner.blocks.EnemyBannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PushOrPullBannerBehavior extends BasicBannerBehavior{

    private final boolean isPull;

    public PushOrPullBannerBehavior(boolean isPull){
        this.isPull = isPull;
    }

    @Override
    public void onTick(Level level, BlockPos blockPos, int tickCount) {
        super.onTick(level, blockPos, tickCount);
        if (level.getBlockEntity(blockPos) instanceof EnemyBannerBlockEntity blockEntity) {
            List<Entity> entityList = BannerUtils.getEntityInRange(level, blockEntity.getParameters().getMonsterId(), blockPos, getRange());
            for (Entity entity : entityList) {
                double delta = 0.05 * (isPull ? -1 : 1);
                Vec3 subtract = entity.position().subtract(blockPos.getBottomCenter());
                entity.addDeltaMovement(subtract.multiply(delta, delta, delta));
            }
        }
    }
}
