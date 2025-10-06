package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.xiaohunao.heaven_destiny_moment.common.init.HDMRegistries;
import com.xiaohunao.heaven_destiny_moment.common.moment.IMoment;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.ChlorophyteOreBlock;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.KillBoardSyncPacketS2C;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.phase_journey.common.util.PhaseUtils;
import org.confluence.terraentity.init.entity.TEBossEntities;

import java.util.Set;

public final class KillBoard implements IGlobalData {
    public static final KillBoard INSTANCE = new KillBoard();
    public static final Codec<Object2BooleanMap<EntityType<?>>> DEFEATED_BOSSES_CODEC = ExtraCodecs.object2BooleanMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec());
    public static final Codec<Object2BooleanMap<ResourceKey<IMoment>>> DEFEATED_EVENTS_CODEC = ExtraCodecs.object2BooleanMap(ResourceKey.codec(HDMRegistries.Keys.MOMENT));
    public static final StreamCodec<ByteBuf, Object2BooleanMap<EntityType<?>>> DEFEATED_BOSSES_STREAM_CODEC = LibStreamCodecUtils.object2BooleanMap(LibStreamCodecUtils.registry(BuiltInRegistries.ENTITY_TYPE));
    public static final StreamCodec<ByteBuf, Object2BooleanMap<ResourceKey<IMoment>>> DEFEATED_EVENTS_STREAM_CODEC = LibStreamCodecUtils.object2BooleanMap(ResourceKey.streamCodec(HDMRegistries.Keys.MOMENT));

    private Object2BooleanMap<EntityType<?>> defeatedBosses = new Object2BooleanOpenHashMap<>();
    private Object2BooleanMap<ResourceKey<IMoment>> defeatedEvents = new Object2BooleanOpenHashMap<>();
    private GamePhase gamePhase = GamePhase.BEFORE_SKELETRON;

    private KillBoard() {}

    public boolean isDefeated(EntityType<?> entityType) {
        return defeatedBosses.getBoolean(entityType);
    }

    public boolean isDefeated(ResourceKey<IMoment> moment) {
        return defeatedEvents.getBoolean(moment);
    }

    public boolean isAnyDefeated(EntityType<?>... entityTypes) {
        for (EntityType<?> entityType : entityTypes) {
            if (isDefeated(entityType)) return true;
        }
        return false;
    }

    public int countDefeated(EntityType<?>... entityTypes) {
        int count = 0;
        for (EntityType<?> entityType : entityTypes) {
            if (isDefeated(entityType)) count++;
        }
        return count;
    }

    @SafeVarargs
    public final int countDefeated(ResourceKey<IMoment>... keys) {
        int count = 0;
        for (ResourceKey<IMoment> key : keys) {
            if (isDefeated(key)) count++;
        }
        return count;
    }

    public Set<EntityType<?>> getDefeatedBosses() {
        return defeatedBosses.keySet();
    }

    public Set<ResourceKey<IMoment>> getDefeatedEvents() {
        return defeatedEvents.keySet();
    }

    public void defeat(EntityType<?> entityType) {
        defeatedBosses.put(entityType, true);
        if (entityType == TEBossEntities.SKELETRON.get()) {
            setGamePhase(ServerLifecycleHooks.getCurrentServer(), GamePhase.AFTER_SKELETRON);
        } else if (entityType == TEBossEntities.WALL_OF_FLESH.get() || entityType == TEBossEntities.HILL_OF_FLESH.get()) {
            setGamePhase(ServerLifecycleHooks.getCurrentServer(), GamePhase.WALL_OF_FLESH);
        } else {
            KillBoardSyncPacketS2C.sendToAll();
        }
    }

    public void defeat(IMoment moment) {
        HDMRegistries.MOMENT.getResourceKey(moment).ifPresentOrElse(key -> defeatedEvents.put(key, true), () -> Confluence.LOGGER.warn("Unknown moment: {}", moment));
        KillBoardSyncPacketS2C.sendToAll();
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(MinecraftServer server, GamePhase gamePhase) {
        this.gamePhase = gamePhase;
        KillBoardSyncPacketS2C.sendToAll();
        if (gamePhase.isGraduated()) {
            IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.GRADUATED);
        } else if (gamePhase.isHardmode()) {
            onUnlockHardmode(server);
            HardmodeConvertor.INSTANCE.start(server, false);
        }
    }

    public static void onUnlockHardmode(MinecraftServer server) {
        IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.HARDMODE);
        PhaseUtils.achieveLevelPhase(OverworldUtils.getLevel(server), ChlorophyteOreBlock.PHASE, true);
    }

    public void networkEncode(ByteBuf buffer) {
        DEFEATED_BOSSES_STREAM_CODEC.encode(buffer, defeatedBosses);
        DEFEATED_EVENTS_STREAM_CODEC.encode(buffer, defeatedEvents);
        GamePhase.STREAM_CODEC.encode(buffer, gamePhase);
    }

    public void networkDecode(ByteBuf buffer) {
        this.defeatedBosses = DEFEATED_BOSSES_STREAM_CODEC.decode(buffer);
        this.defeatedEvents = DEFEATED_EVENTS_STREAM_CODEC.decode(buffer);
        this.gamePhase = GamePhase.STREAM_CODEC.decode(buffer);
    }

    @Override
    public <T> void decode(Dynamic<T> tag) {
        tag.get("defeated_bosses").result().or(() -> tag.get("defeated_map").result()).orElseGet(tag::emptyMap).read(DEFEATED_BOSSES_CODEC).ifSuccess(result -> this.defeatedBosses = result);
        tag.get("defeated_events").orElseEmptyMap().read(DEFEATED_EVENTS_CODEC).ifSuccess(result -> this.defeatedEvents = result);
        this.gamePhase = GamePhase.getByOrder(tag.get("game_phase").asInt(0));
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.put("defeated_bosses", DEFEATED_BOSSES_CODEC.encodeStart(NbtOps.INSTANCE, defeatedBosses).result().orElseGet(CompoundTag::new));
        tag.put("defeated_events", DEFEATED_EVENTS_CODEC.encodeStart(NbtOps.INSTANCE, defeatedEvents).result().orElseGet(CompoundTag::new));
        tag.putInt("game_phase", gamePhase.getOrder());
    }

    @Override
    public String serializeKey() {
        return "confluence:kill_board";
    }

    @Override
    public void clear() {
        this.defeatedBosses = new Object2BooleanOpenHashMap<>();
        this.defeatedEvents = new Object2BooleanOpenHashMap<>();
        this.gamePhase = GamePhase.BEFORE_SKELETRON;
    }
}
