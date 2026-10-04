package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.entity.ModEntities;

public class BombFishEntity extends BaseBombEntity {
    private BlockPos stickBlock = null;

    public BombFishEntity(EntityType<? extends BombFishEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bounceFactor = 0.0;
    }

    public BombFishEntity(LivingEntity pShooter) {
        super(ModEntities.BOMB_FISH_ENTITY.get(), pShooter);
        this.bounceFactor = 0.0;
    }

    @Override
    protected void blockHitCallBack(BlockHitResult blockHitResult) {
        super.blockHitCallBack(blockHitResult);
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        Vec3 collPos = blockHitResult.getLocation();
        moveTo(collPos.x, collPos.y, collPos.z, getYRot(), getXRot());
        this.stickBlock = blockPosition();
    }

    @Override
    public void tick() {
        super.tick();
        if (stickBlock != blockPosition()) {
            setNoGravity(false);
            this.stickBlock = null;
        }
    }
}
