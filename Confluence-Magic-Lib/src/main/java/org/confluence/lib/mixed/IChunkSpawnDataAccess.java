package org.confluence.lib.mixed;

import org.confluence.lib.util.NaturalSpawnerUtil;
import org.jetbrains.annotations.NotNull;

public interface IChunkSpawnDataAccess {
    void confluence$setData(NaturalSpawnerUtil.ChunkSpawnData data);

    NaturalSpawnerUtil.ChunkSpawnData confluence$getData();

    static @NotNull IChunkSpawnDataAccess of(Object o) {
        return (IChunkSpawnDataAccess) o;
    }
}
