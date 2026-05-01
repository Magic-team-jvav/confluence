package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.init.ModSecretSeeds;

public class GetFixedBoi extends SecretSeed {
    private static long allFlag = 0;

    public GetFixedBoi(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "getfixedboi".equals(seed) || "get fixed boi".equals(seed);
    }

    @Override
    public long applyFlag(long original) {
        return original | allFlag();
    }

    private static long allFlag() {
        if (allFlag == 0) {
            long flag = 0;
            for (SecretSeed secretSeed : ModSecretSeeds.VALUES) {
                if (!secretSeed.isHided() && secretSeed != ModSecretSeeds.SKYBLOCK) {
                    flag |= secretSeed.getFlag();
                }
            }
            allFlag = flag;
        }
        return allFlag;
    }
}
