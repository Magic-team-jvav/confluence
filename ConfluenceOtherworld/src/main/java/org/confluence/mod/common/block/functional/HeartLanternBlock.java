package org.confluence.mod.common.block.functional;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

import static com.hollingsworth.arsnouveau.common.block.ITickableBlock.createTickerHelper;

public class HeartLanternBlock extends LanternBlock {
    public HeartLanternBlock(Properties properties) {
        super(properties);
    }

    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return null;
        } else {
            return createTickerHelper(blockEntityType, BlockEntityType.CAMPFIRE, (serverLevel, pos, blockState, blockEntity) -> {
                if (serverLevel.getGameTime() % 200 == 0) {
                    Vec3 center = pos.getCenter();

                    for (Player player : serverLevel.players()) {
                        if (player.distanceToSqr(center) < 32 * 32) {
                            player.addEffect(new MobEffectInstance(
                                    ModEffects.HEART_LANTERN,
                                    420
                            ));
                        }
                    }
                }
            });
        }
    }
}
