package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;

public class StickyDynamiteEntity extends BaseDynamiteEntity {
    protected BlockState stickBlock = null;

    public StickyDynamiteEntity(EntityType<StickyDynamiteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bounceFactor = 0.0;
    }

    public StickyDynamiteEntity(LivingEntity pShooter) {
        super(ModEntities.STICKY_DYNAMITE.get(), pShooter);
        this.bounceFactor = 0.0;
    }

    @Override
    protected void blockHitCallBack(BlockHitResult blockHitResult) {
        super.blockHitCallBack(blockHitResult);
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        Vec3 collPos = blockHitResult.getLocation();
        moveTo(collPos.x, collPos.y, collPos.z, getYRot(), getXRot());
        this.stickBlock = level().getBlockState(blockPosition());
    }

    @Override
    public void tick() {
        super.tick();
        if (stickBlock != level().getBlockState(blockPosition())) {
            setNoGravity(false);
            this.stickBlock = null;
        }
    }
}
