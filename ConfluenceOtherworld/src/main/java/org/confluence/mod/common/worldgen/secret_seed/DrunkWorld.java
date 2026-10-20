package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.AboveYAddConstantDensityFunction;
import org.confluence.mod.mixed.IDensityFunctions$Ap2;
import org.confluence.mod.util.OverworldUtils;

/// [Wiki](https://terraria.wiki.gg/zh/wiki/Drunk_world)
public class DrunkWorld extends SecretSeed {
    public DrunkWorld(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        try {
            return Long.parseLong(seed) == 5162020L;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void modifyDepth(ServerLevel level) {
        /// 配置关闭时保持原版地形高度：醉酒世界只保留其它特性（双邪恶、图鉴等）。
        if (!CommonConfigs.DRUNK_WORLD_RAISE_SURFACE.get()) {
            return;
        }
        if (ModSecretSeeds.DRUNK_WORLD.match(level)) {
            DensityFunction function = level.registryAccess().holderOrThrow(NoiseRouterData.DEPTH).value();
            if (function instanceof IDensityFunctions$Ap2 ap2 && ap2.confluence$getType() == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD) {
                DensityFunction copy = DensityFunctions.add(ap2.confluence$getArg(true), ap2.confluence$getArg(false));
                DensityFunction limit = new AboveYAddConstantDensityFunction(copy, OverworldUtils.getSeaLevel(), 0.2);
                ap2.confluence$setArgs(limit, DensityFunctions.zero());
            }
        }
    }
}
