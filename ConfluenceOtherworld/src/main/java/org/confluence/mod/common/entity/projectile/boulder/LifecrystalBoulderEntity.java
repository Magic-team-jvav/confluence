package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LifecrystalBoulderEntity extends BoulderEntity implements GeoAnimatable {
    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

    public LifecrystalBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public LifecrystalBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.LIFECRYSTAL_BOULDER.get(), level, pos, blockState);
    }

    @Override
    protected void removeEffect(ServerLevel serverLevel) {
        dropLifeCrystal(serverLevel);
        super.removeEffect(serverLevel);
    }

    private void dropLifeCrystal(ServerLevel serverLevel) {
        ItemStack lifeCrystal = ConsumableItems.LIFE_CRYSTAL.get().getDefaultInstance();
        BlockPos blockPos = blockPosition();
        BlockState blockState = serverLevel.getBlockState(blockPos);

        if (blockState.canBeReplaced()) {
            serverLevel.setBlock(blockPos, NatureBlocks.LIFE_CRYSTAL_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
        } else {
            Block.popResource(serverLevel, blockPos, lifeCrystal);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }

    @Override
    public double getTick(Object animatable) {
        return tickCount;
    }
}
