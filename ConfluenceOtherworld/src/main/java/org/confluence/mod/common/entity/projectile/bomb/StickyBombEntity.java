package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;

public class StickyBombEntity extends BaseBombEntity {
    protected BlockState stickBlock;
    protected BlockPos stickPos;

    public StickyBombEntity(EntityType<? extends StickyBombEntity> type, Level level) {
        super(type, level);
        this.bounceFactor = 0.0;
    }

    public StickyBombEntity(EntityType<? extends StickyBombEntity> type, LivingEntity shooter) {
        super(type, shooter);
        this.bounceFactor = 0.0;
    }

    public StickyBombEntity(LivingEntity shooter) {
        super(ModEntities.STICKY_BOMB_ENTITY.get(), shooter);
        this.bounceFactor = 0.0;
    }

    @Override
    protected void blockHitCallBack(BlockHitResult blockHitResult) {
        super.blockHitCallBack(blockHitResult);
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        Vec3 collPos = blockHitResult.getLocation();
        moveTo(collPos.x, collPos.y, collPos.z, getYRot(), getXRot());
        this.stickPos = blockHitResult.getBlockPos();
        this.stickBlock = level().getBlockState(stickPos);
    }

    @Override
    public void tick() {
        super.tick();
        if (stickPos == null || stickBlock != level().getBlockState(blockPosition())) {
            setNoGravity(false);
            this.stickBlock = null;
        }
    }
}
