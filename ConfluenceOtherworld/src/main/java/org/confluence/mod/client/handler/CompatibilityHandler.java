package org.confluence.mod.client.handler;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.network.s2c.CompatibilitySyncPacketS2c;

@OnlyIn(Dist.CLIENT)
public final class CompatibilityHandler {
    private static boolean[] configs;

    public static boolean isConvertArsNouveauMana() {
        return configs != null && configs[0];
    }

    public static boolean isConvertIronsSpellMana() {
        return configs != null && configs[1];
    }

    public static boolean isFtbChunksWormholePotion() {
        return configs == null || configs[2];
    }

    public static boolean isXaerosMapWormholePotion() {
        return configs == null || configs[3];
    }

    public static boolean isXaerosMapPylonWaypoint() {
        return configs == null || configs[4];
    }

    public static boolean isWaystonesPylonNonCost() {
        return configs != null && configs[5];
    }

    public static void reset() {
        configs = null;
    }

    public static void handle(int data) {
        if (configs == null) {
            configs = new boolean[CompatibilitySyncPacketS2c.getConfigs().length];
        }
        for (int i = 0; i < configs.length; i++) {
            configs[i] = (data & (1 << i)) != 0;
        }
    }
}
