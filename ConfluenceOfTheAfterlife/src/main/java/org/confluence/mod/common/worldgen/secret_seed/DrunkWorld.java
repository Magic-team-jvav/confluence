package org.confluence.mod.common.worldgen.secret_seed;

/**
 * <a href="https://terraria.wiki.gg/zh/wiki/Drunk_world">Wiki</a>
 */
public class DrunkWorld extends SecretSeed {
    public static final int SEA_LEVEL = 47;

    public DrunkWorld(long flag) {
        super(flag);
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
