package org.confluence.mod.common.block.functional;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.data.event.ParticleEffect;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;

import java.util.Map;

// todo
public class ConfettiCannonBlock extends HorizontalDirectionalBlock implements EntityBlock, INetworkBlock {
    public static final IntegerProperty PITCH_30 = IntegerProperty.create("pitch_30", 0, 6);
    public static final MapCodec<ConfettiCannonBlock> CODEC = simpleCodec(ConfettiCannonBlock::new);

    public ConfettiCannonBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PITCH_30, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PITCH_30);
    }

    @Override
    protected MapCodec<ConfettiCannonBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbstractMechanicalBlock.Entity(pos, state);
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        int pitch = state.getValue(PITCH_30) * 30;
        int yaw = (state.getValue(FACING).get2DDataValue() & 3) * 90;
        MolangExp expression = new MolangExp(Map.of(
                "variable.pitch", Integer.toString(pitch),
                "variable.yaw", Integer.toString(yaw)
        ));
        EmitterCreationPacketS2C.sendToAll(Confluence.asResource("confetti"), pos.getCenter().toVector3f(), ParticleEffect.Type.PARTICLE_WITH_VELOCITY, expression);
    }
}
