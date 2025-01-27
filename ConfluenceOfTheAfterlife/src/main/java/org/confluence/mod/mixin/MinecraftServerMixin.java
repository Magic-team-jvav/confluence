package org.confluence.mod.mixin;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.WorldData;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Predicate;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {
    @Shadow public abstract WorldData getWorldData();

    @Unique
    private final Object2BooleanMap<SecretSeed> confluence$secretSeedsCache = new Object2BooleanOpenHashMap<>();

    @Override
    public boolean confluence$cacheSecretSeeds(SecretSeed secretSeed) {
        return confluence$secretSeedsCache.computeIfAbsent(secretSeed, (Predicate<? super SecretSeed>) seed -> seed.match(((IWorldOptions) getWorldData().worldGenOptions()).confluence$getSecretFlag()));
    }
}
