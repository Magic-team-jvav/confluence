package org.confluence.mod.common.block.functional.announcement_box;

import com.llamalad7.mixinextras.sugar.Share;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;

public class StandingAnnouncementBoxBlock extends StandingSignBlock implements INetworkBlock {
    private final AnnouncementBox.Shared shared = new AnnouncementBox.Shared(this, this);
    public StandingAnnouncementBoxBlock(WoodType type, Properties properties) {
        super(type, properties);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return shared.newBlockEntity(pos, state);
    }
    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        shared.onExecute(pState, pLevel, pPos, pColor, pEntity);
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        shared.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
}
