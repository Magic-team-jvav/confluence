package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.event.ForgeEventFactory;

public final class AntlionSandBall extends StraightMonsterProjectile implements ItemSupplier {
    public static final double GRAVITY = 0.04;

    public AntlionSandBall(EntityType<? extends AntlionSandBall> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return velocity.add(0.0, -GRAVITY, 0.0);
    }

    private void placeSand(BlockPos pos) {
        if (!level().isClientSide && getOwner() != null && ForgeEventFactory.getMobGriefingEvent(level(), getOwner())
                && level().isInWorldBounds(pos) && level().getBlockState(pos).isAir()
                && level().isUnobstructed(Blocks.SAND.defaultBlockState(), pos, CollisionContext.empty())) {
            level().setBlockAndUpdate(pos, Blocks.SAND.defaultBlockState());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        placeSand(result.getBlockPos().relative(result.getDirection()));
        super.onHitBlock(result);
    }

    @Override
    protected void finishEntityHit(EntityHitResult result) {
        placeSand(BlockPos.containing(result.getLocation()));
        super.finishEntityHit(result);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.SAND);
    }
}
