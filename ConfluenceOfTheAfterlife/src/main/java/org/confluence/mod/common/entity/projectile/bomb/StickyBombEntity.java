package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;

public class StickyBombEntity extends BaseBombEntity {
    protected BlockPos stickBlock = null;

    public StickyBombEntity(EntityType<? extends StickyBombEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bounceFactor = 0.0;
    }

    public StickyBombEntity(EntityType<? extends StickyBombEntity> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pLevel);
    }

    public StickyBombEntity(EntityType<? extends StickyBombEntity> pEntityType, LivingEntity pShooter) {
        super(pEntityType, pShooter);
    }

    public StickyBombEntity(Level pLevel, double pX, double pY, double pZ) {
        this(ModEntities.STICKY_BOMB_ENTITY.get(), pX, pY, pZ, pLevel);
        this.bounceFactor = 0.0;
    }

    public StickyBombEntity(LivingEntity pShooter) {
        this(ModEntities.STICKY_BOMB_ENTITY.get(), pShooter);
        this.bounceFactor = 0.0;
    }

    @Override
    protected Item getDefaultItem() {
        return ConsumableItems.STICKY_BOMB.get();
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
