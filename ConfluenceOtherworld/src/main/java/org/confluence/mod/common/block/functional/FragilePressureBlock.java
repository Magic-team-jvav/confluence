package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.ILibSimulatorBlock;

import java.util.function.Supplier;

public class FragilePressureBlock extends BehaviourPressurePlateBlock implements ILibSimulatorBlock {
    private static final net.minecraft.world.phys.AABB TOUCH_AABB = new AABB(0, 1, 0, 1, 2, 1);
    private final Supplier<BlockState> simulatorBlock;

    public FragilePressureBlock(Properties pProperties, BlockSetType pType, Supplier<BlockState> simulatorBlock) {
        super(new Behaviour() {
            @Override
            protected int getSignalStrength(Level level, BlockPos blockPos) {
                net.minecraft.world.phys.AABB aabb = TOUCH_AABB.move(blockPos);
                for (Player player : level.players()) {
                    if (aabb.contains(player.position())) {
                        return 15;
                    }
                }
                return 0;
            }
        }, pProperties, pType);
        this.simulatorBlock = simulatorBlock;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return simulatorBlock.get().getShape(level, pos, context);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {}

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public BlockState getSimulatedBlock(boolean isClient) {
        return simulatorBlock.get();
    }
}
