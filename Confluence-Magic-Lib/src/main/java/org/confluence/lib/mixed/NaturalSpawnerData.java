package org.confluence.lib.mixed;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicReference;

public final class NaturalSpawnerData {
    public static final AtomicReference<ServerLevel> spawnForChunkServerLevel = new AtomicReference<>();
    public static final AtomicReference<ServerPlayer> canSpawnServerPlayer = new AtomicReference<>();
}
