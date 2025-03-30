package org.confluence.mod.common.block.functional.announcement_box;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;

public abstract class AnnouncementBox extends SignBlock implements INetworkBlock {
    public static final WoodType METAL = new WoodType(
            "metal", BlockSetType.IRON
    );
    protected AnnouncementBox(WoodType type, Properties properties) {
        super(type, properties);
    }
    static class Shared{
        private final SignBlock signBlock;
        private final INetworkBlock iNetworkBlock;
        Shared(SignBlock signBlock, INetworkBlock iNetworkBlock){
            this.signBlock = signBlock;
            this.iNetworkBlock = iNetworkBlock;
        }
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new AnnouncementBoxEntity(pos, state);
        }
        public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {

        }
        public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
            iNetworkBlock.onNodeRemove(pState, pLevel, pPos, pNewState);
        }
    }
}
