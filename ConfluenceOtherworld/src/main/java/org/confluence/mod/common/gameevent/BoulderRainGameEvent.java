package org.confluence.mod.common.gameevent;

import com.google.common.collect.Streams;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.AchievementUtils;

import java.util.List;
import java.util.Objects;

public class BoulderRainGameEvent implements GameEvent {
    public static final ResourceKey<BoulderRainGameEvent> KEY = GameEvent.createKey(Confluence.asResource("boulder_rain"));
    public static final BoulderRainGameEvent INSTANCE = new BoulderRainGameEvent();
    private static final BoulderEntity.Builder BUILDER = BoulderEntity.Builder.of();
    private boolean started;
    private transient MinecraftServer server;
    private transient List<ServerLevel> availableLevels;
    private transient boolean isAvailableSeeds;
    private transient boolean forceStart;
    private transient boolean forceEnd;

    private BoulderRainGameEvent() {}

    @Override
    public void open(MinecraftServer server) {
        this.server = server;
        this.availableLevels = Streams.stream(server.getAllLevels()).filter(Objects::nonNull).filter(level -> {
            DimensionType type = level.dimensionType();
            return type.hasSkyLight() && !type.hasCeiling();
        }).toList();
        long flag = IMinecraftServer.of(server).confluence$getSecretFlag();
        this.isAvailableSeeds = IMinecraftServer.matchesSecretFlag(flag, IWorldOptions.BW_MASK) ||
                IMinecraftServer.equalsSecretFlag(flag, IWorldOptions.DW_MASK | IWorldOptions.FTW_MASK);
    }

    @Override
    public void close(MinecraftServer server) {
        this.server = null;
        this.availableLevels = null;
    }

    @Override
    public void tick() {
        if (!started) return;
        BlockState blockState = FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState();
        for (ServerLevel level : availableLevels) {
            for (ServerPlayer player : level.players()) {
                if (player.getRandom().nextInt(1000) == 0) {
                    Vec3 position = player.position();
                    double x = Mth.nextDouble(level.random, position.x - 32, position.x + 32);
                    double z = Mth.nextDouble(level.random, position.z - 32, position.z + 32);
                    int cx = SectionPos.blockToSectionCoord(x);
                    int cz = SectionPos.blockToSectionCoord(z);
                    if (LibUtils.getChunkIfLoaded(level.getChunkSource(), cx, cz) == null) {
                        continue;
                    }
                    level.addFreshEntity(BUILDER.build(level, new Vec3(x, position.y + 64, z), blockState));
                }
            }
        }
    }

    @Override
    public boolean canStart() {
        if (forceStart) {
            return true;
        }
        if (isAvailableSeeds) {
            ServerLevelData data = server.getWorldData().overworldData();
            return data.isRaining() && data.isThundering();
        }
        return false;
    }

    @Override
    public boolean canEnd() {
        if (forceEnd) {
            return true;
        }
        ServerLevelData data = server.getWorldData().overworldData();
        return !data.isRaining() || !data.isThundering();
    }

    @Override
    public void onStart() {
        this.started = true;
        this.forceStart = false;
    }

    @Override
    public void onEnd() {
        this.started = false;
        this.forceEnd = false;
        for (ServerLevel level : availableLevels) {
            for (ServerPlayer player : level.players()) {
                AchievementUtils.awardAchievement(player, "its_shaling_outside");
            }
        }
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public boolean forceStart() {
        if (started) return false;
        this.forceStart = true;
        return true;
    }

    @Override
    public void forceEnd() {
        if (started) {
            this.forceEnd = true;
        }
    }

    @Override
    public void decode(CompoundTag tag) {
        this.started = tag.getBoolean("Started");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", started);
    }

    @Override
    public ResourceKey<BoulderRainGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }
}
