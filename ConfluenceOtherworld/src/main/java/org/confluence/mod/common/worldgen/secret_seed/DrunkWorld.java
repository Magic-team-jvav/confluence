package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;

/**
 * <a href="https://terraria.wiki.gg/zh/wiki/Drunk_world">Wiki</a>
 */
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
}
