package org.confluence.mod.common.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.OverworldUtils;

public final class CritterCorruption {
    private CritterCorruption() {}

    public static boolean selectsCrimson(Mob critter) {
        ServerLevel level = (ServerLevel) critter.level();
        long flags = IMinecraftServer.of(level.getServer()).confluence$getSecretFlag();
        if (!IMinecraftServer.matchesSecretFlag(flags, IWorldOptions.DOUBLE_EVIL)) {
            if (IMinecraftServer.matchesSecretFlag(flags, IWorldOptions.THE_CRIMSON)) return true;
            if (IMinecraftServer.matchesSecretFlag(flags, IWorldOptions.THE_CORRUPTION))
                return false;
        }
        var biome = level.getBiome(critter.blockPosition());
        if (OverworldUtils.isCrimson(biome)) return true;
        if (OverworldUtils.isCorruption(biome)) return false;
        return critter.getRandom().nextBoolean();
    }
}
