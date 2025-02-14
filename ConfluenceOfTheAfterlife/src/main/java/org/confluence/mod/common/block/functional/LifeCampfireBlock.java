package org.confluence.mod.common.block.functional;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.entity.CampfireBlockEntity.cookTick;
import static net.minecraft.world.level.block.entity.CampfireBlockEntity.cooldownTick;

// todo 电线控制
public class LifeCampfireBlock extends CampfireBlock {
    public LifeCampfireBlock(Properties properties) {
        super(true, 1, properties);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return state.getValue(LIT) ? createTickerHelper(blockEntityType, BlockEntityType.CAMPFIRE, CampfireBlockEntity::particleTick) : null;
        } else {
            return createTickerHelper(blockEntityType, BlockEntityType.CAMPFIRE, (serverLevel, pos, blockState, blockEntity) -> {
                if (blockState.getValue(LIT)) {
                    cookTick(serverLevel, pos, blockState, blockEntity);
                    if (serverLevel.getGameTime() % 200 == 0) {
                        Vec3 center = pos.getCenter();
                        for (Player player : serverLevel.players()) {
                            if (player.distanceToSqr(center) < 32 * 32) {
                                player.addEffect(new MobEffectInstance(ModEffects.COZY_FIRE, 420));
                            }
                        }
                    }
                } else {
                    cooldownTick(serverLevel, pos, blockState, blockEntity);
                }
            });
        }
    }
}
