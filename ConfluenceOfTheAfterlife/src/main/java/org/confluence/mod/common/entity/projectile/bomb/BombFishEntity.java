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

public class BombFishEntity extends BaseBombEntity {
    private BlockPos stickBlock = null;

    public BombFishEntity(EntityType<? extends BombFishEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bounceFactor = 0f;
    }

    public BombFishEntity(EntityType<? extends BombFishEntity> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pLevel);
    }

    public BombFishEntity(EntityType<? extends BombFishEntity> pEntityType, LivingEntity pShooter) {
        super(pEntityType, pShooter);
    }

    public BombFishEntity(Level pLevel, double pX, double pY, double pZ) {
        this(ModEntities.BOMB_FISH_ENTITY.get(), pX, pY, pZ, pLevel);
        this.bounceFactor = 0f;
    }

    public BombFishEntity(LivingEntity pShooter) {
        this(ModEntities.BOMB_FISH_ENTITY.get(), pShooter);
        this.bounceFactor = 0f;
    }

    @Override
    protected Item getDefaultItem() {
        return ConsumableItems.BOMB_FISH.get();
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
