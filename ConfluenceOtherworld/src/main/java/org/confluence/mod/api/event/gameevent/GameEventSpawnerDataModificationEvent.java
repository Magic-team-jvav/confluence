package org.confluence.mod.api.event.gameevent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.eventbus.api.Event;
import org.confluence.mod.common.gameevent.GameEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class GameEventSpawnerDataModificationEvent extends Event {
    private final ResourceKey<? extends GameEvent> key;
    private final ServerLevel level;
    private final List<MobSpawnSettings.SpawnerData> original;
    private final List<MobSpawnSettings.SpawnerData> modified;

    public GameEventSpawnerDataModificationEvent(ResourceKey<? extends GameEvent> key, ServerLevel level, MobSpawnSettings.SpawnerData... original) {
        this.key = key;
        this.level = level;
        this.original = ImmutableList.copyOf(original);
        this.modified = Lists.newArrayList(original);
    }

    public ResourceKey<? extends GameEvent> getKey() {
        return key;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public List<MobSpawnSettings.SpawnerData> getOriginal() {
        return original;
    }

    public List<MobSpawnSettings.SpawnerData> getModified() {
        return modified;
    }

    @ApiStatus.Internal
    public WeightedRandomList<MobSpawnSettings.SpawnerData> create() {
        return WeightedRandomList.create(modified);
    }
}
