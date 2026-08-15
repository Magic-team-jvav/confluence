package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.confluence.lib.util.LibUtils;

/**
 * 岩浆史莱姆免疫火焰、接触目标时点燃对方，并会在水中持续受伤。
 *
 * <p>专家及更高难度下，它在死亡动画期间会把当前位置可替换的非熔岩源方块变为
 * 流动熔岩。放置条件与 1.21 侧一致，不覆盖固体方块，也不改写已有熔岩源。</p>
 */
public class LavaSlime extends BaseSlime {

    public LavaSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFFB150, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(10.0f, 10, 30.0f);
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
    protected void onAttackTarget(LivingEntity target) {
        target.setRemainingFireTicks(100);
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (!(level() instanceof ServerLevel serverLevel)
                || !LibUtils.isAtLeastExpert(serverLevel, blockPosition())) {
            return;
        }

        BlockPos pos = BlockPos.containing(position());
        BlockState state = serverLevel.getBlockState(pos);
        if (!state.canBeReplaced(Fluids.LAVA)
                || state.getFluidState().isSourceOfType(Fluids.LAVA)) {
            return;
        }

        serverLevel.setBlock(
                pos,
                Blocks.LAVA.defaultBlockState().setValue(BlockStateProperties.LEVEL, 14),
                Block.UPDATE_ALL);
        serverLevel.scheduleTick(pos, Blocks.LAVA, 2);
    }
}
