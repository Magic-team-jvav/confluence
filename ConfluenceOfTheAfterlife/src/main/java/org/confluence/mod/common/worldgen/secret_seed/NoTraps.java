package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.confluence.mod.common.entity.projectile.bomb.BaseBombEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;

public class NoTraps extends SecretSeed {
    public NoTraps(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "notraps".equals(seed) || "no traps".equals(seed);
    }

    public static void dropBombWhenLeavesDestroy(ServerPlayer serverPlayer, BlockState blockState, BlockPos pos) {
        if (blockState.is(BlockTags.LEAVES) && (!blockState.hasProperty(BlockStateProperties.PERSISTENT) || !blockState.getValue(BlockStateProperties.PERSISTENT))) {
            ServerLevel level = serverPlayer.serverLevel();
            if (ModSecretSeeds.FOR_THE_WORTHY.match(serverPlayer.server) && blockState.is(BlockTags.LEAVES) && level.random.nextFloat() < 0.25F) {
                BaseBombEntity bomb = new BaseBombEntity(ModEntities.BOMB_ENTITY.get(), level);
                bomb.setPos(pos.getCenter());
                level.addFreshEntity(bomb);
            }
        }
    }
}
