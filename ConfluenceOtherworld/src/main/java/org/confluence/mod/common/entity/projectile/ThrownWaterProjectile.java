package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;

public class ThrownWaterProjectile extends ThrowableItemProjectile {
    private final ISpreadable.Type type;

    public ThrownWaterProjectile(EntityType<ThrownWaterProjectile> entityType, Level level) {
        super(entityType, level);
        this.type = ISpreadable.Type.PURE;
    }

    public ThrownWaterProjectile(LivingEntity shooter, ISpreadable.Type type) {
        super(ModEntities.THROWN_WATER_PROJECTILE.get(), shooter, shooter.level());
        this.type = type;
    }

    @Override
    protected Item getDefaultItem() {
        if (type == ISpreadable.Type.CORRUPT) return ConsumableItems.UNHOLY_WATER.get();
        if (type == ISpreadable.Type.CRIMSON) return ConsumableItems.BLOOD_WATER.get();
        return ConsumableItems.HOLY_WATER.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            BlockPos blockPos = result.getBlockPos();
            for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-2, -2, -2), blockPos.offset(2, 2, 2))) {
                if (pos.distSqr(blockPos) <= 25) type.spread(level(), pos);
            }
            level().levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, blockPosition(), getColor());
            discard();
        }
    }

    protected int getColor() {
        if (type == ISpreadable.Type.CORRUPT) return 0xFF00FF;
        if (type == ISpreadable.Type.CRIMSON) return 0xFF0000;
        return 0x0000FF;
    }
}
