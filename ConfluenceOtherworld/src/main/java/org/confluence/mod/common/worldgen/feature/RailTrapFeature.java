package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.FunctionalBlocks;

import java.util.ArrayList;
import java.util.List;

public class RailTrapFeature extends Feature<RailTrapFeature.Config> {
    public RailTrapFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos centerPos = pContext.origin();
        BlockPos checkPos;
        BlockState trapBlockState = config.trap().getState(pContext.random(), centerPos);
        BlockState signalBlockState = FunctionalBlocks.SIGNAL_ADAPTER.get().defaultBlockState().setValue(StateProperties.REVERSE, Boolean.TRUE);
        boolean place = true;
        List<BlockPos> trapList = new ArrayList<>();
        List<BlockPos> trapListTrue = new ArrayList<>();
        trapList.add(centerPos.offset(1, 1, 1));
        trapList.add(centerPos.offset(0, 1, 1));
        trapList.add(centerPos.offset(-1, 1, 1));
        trapList.add(centerPos.offset(1, 1, 0));
        trapList.add(centerPos.offset(0, 1, 0));
        trapList.add(centerPos.offset(-1, 1, 0));
        trapList.add(centerPos.offset(1, 1, -1));
        trapList.add(centerPos.offset(0, 1, -1));
        trapList.add(centerPos.offset(-1, 1, -1));
        INetworkEntity trapEntity = null;
        INetworkEntity signalEntity = null;
        for (BlockPos trap : trapList) {
            for (int i = 0; i < 100; i++) {
                checkPos = trap.offset(0, i, 0);
                if (!level.getBlockState(checkPos).canBeReplaced()) {
                    trapListTrue.add(trap.offset(0, i, 0));
                    break;
                }
            }
        }

        if (!trapListTrue.isEmpty()) {
            level.setBlock(centerPos.offset(0, -1, 0), signalBlockState, 3);
            for (BlockPos trap : trapListTrue) {
                level.setBlock(trap, trapBlockState, 3);
                trapEntity = ModFeatures.getNetworkEntity(level, trap);
                signalEntity = ModFeatures.getNetworkEntity(level, centerPos.offset(0, -1, 0));
                if (trapEntity != null && signalEntity != null)
                    trapEntity.connectTo(0xFF00FF, centerPos.offset(0, -1, 0), signalEntity);
            }
            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider trap) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = BlockStateProvider.CODEC.xmap(Config::new, Config::trap);
    }
}
