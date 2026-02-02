package org.confluence.terra_furniture.common.block.func.be;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.terra_furniture.common.util.SwayingController;

public abstract class BaseSwayingBE extends BlockEntity implements ISwayingBE {
    protected float movingFactor = 0.06f;
    private Vec3 delta = new Vec3(0, 0, 0);
    private Vec3 lastDelta = new Vec3(0, 0, 0);
    protected final SwayingController controller;

    public BaseSwayingBE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        controller = new SwayingController();
    }
    @Override
    public void applyDelta(Vec3 input) {
        delta = delta.add(input);
    }

    public void applyMovingAffectedDelta(Vec3 input) {
        applyDelta(input.scale(movingFactor));
    }

    public void tickAtClient() {
        lastDelta = delta;
        if (!delta.equals(Vec3.ZERO)) delta = Vec3.ZERO;
    }

    public void trigController() {
        controller.updateSwing(lastDelta);
    }

    @Override
    public float getSwingX() {
        return controller.getSwingX();
    }

    @Override
    public float getSwingZ() {
        return controller.getSwingZ();
    }
}
