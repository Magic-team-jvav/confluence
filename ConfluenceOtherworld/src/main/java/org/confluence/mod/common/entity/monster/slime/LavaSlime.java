package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.confluence.lib.util.LibUtils;

/// 岩浆史莱姆免疫火焰，并会在水中持续受伤。
///
/// 专家及更高难度下，它在死亡动画期间会把当前位置可替换的非熔岩源方块变为
/// 流动熔岩。放置时不覆盖固体方块，也不改写已有熔岩源。
public class LavaSlime extends BaseSlime {
    private int jumpCycle;
    private boolean deathLavaPlaced;

    public LavaSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false);
    }

    @Override
    protected boolean isFireImmune() {
        return true;
    }

    @Override
    protected boolean hurtByWater() {
        return true;
    }

    @Override
    protected int getJumpDelay() {
        return jumpCycle == 2 ? 22 : 13;
    }

    @Override
    protected float getJumpPower() {
        boolean highJump = jumpCycle == 2;
        jumpCycle = (jumpCycle + 1) % 3;
        return super.getJumpPower() * (highJump ? 1.68F : 1.0F);
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (deathLavaPlaced || !(level() instanceof ServerLevel serverLevel)
                || !LibUtils.isAtLeastExpert(serverLevel, blockPosition())) {
            return;
        }

        BlockPos pos = BlockPos.containing(position());
        BlockState state = serverLevel.getBlockState(pos);
        if (!state.canBeReplaced(Fluids.LAVA) || state.getFluidState().isSourceOfType(Fluids.LAVA)) {
            return;
        }

        serverLevel.setBlock(pos, Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 14), Block.UPDATE_ALL);
        serverLevel.scheduleTick(pos, Blocks.LAVA, 2);
        deathLavaPlaced = true;
    }
}
