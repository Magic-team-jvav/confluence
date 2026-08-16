package org.confluence.mod.common.data.saved;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.gameevent.LanternNightGameEvent;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.KillBoardSyncPacketS2C;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Set;

public enum KillBoard implements IGlobalData {
    INSTANCE;
    public static final Codec<Object2BooleanMap<EntityType<?>>> DEFEATED_BOSSES_CODEC = PortCodecExtension.object2BooleanMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec());
    public static final Codec<Object2BooleanMap<ResourceKey<? extends GameEvent>>> DEFEATED_EVENTS_CODEC = PortCodecExtension.object2BooleanMap(GameEvent.KEY_CODEC);
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, Object2BooleanMap<EntityType<?>>> DEFEATED_BOSSES_STREAM_CODEC = LibStreamCodecUtils.object2BooleanMap(PortByteBufCodecs.registry(Registries.ENTITY_TYPE));
    public static final PortStreamCodec<ByteBuf, Object2BooleanMap<ResourceKey<? extends GameEvent>>> DEFEATED_EVENTS_STREAM_CODEC = LibStreamCodecUtils.object2BooleanMap(GameEvent.KEY_STREAM_CODEC);

    private Object2BooleanMap<EntityType<?>> defeatedBosses = new Object2BooleanOpenHashMap<>();
    private Object2BooleanMap<ResourceKey<? extends GameEvent>> defeatedEvents = new Object2BooleanOpenHashMap<>();
    private GamePhase gamePhase = GamePhase.BEFORE_SKELETRON;

    public boolean isDefeated(EntityType<?> entityType) {
        return defeatedBosses.getBoolean(entityType);
    }

    public boolean isDefeated(ResourceKey<? extends GameEvent> moment) {
        return defeatedEvents.getBoolean(moment);
    }

    public boolean isAnyDefeated(EntityType<?>... types) {
        for (EntityType<?> type : types) {
            if (isDefeated(type)) return true;
        }
        return false;
    }

    public boolean isAnyMechBossDefeated() {
        return isDefeated(BossEntities.THE_TWINS.get()) ||
                isDefeated(BossEntities.THE_DESTROYER.get()) ||
                isDefeated(BossEntities.SKELETRON_PRIME.get());
    }

    public int countDefeated(EntityType<?>... entityTypes) {
        int count = 0;
        for (EntityType<?> entityType : entityTypes) {
            if (isDefeated(entityType)) count++;
        }
        return count;
    }

    @SafeVarargs
    public final int countDefeated(ResourceKey<? extends GameEvent>... keys) {
        int count = 0;
        for (ResourceKey<? extends GameEvent> key : keys) {
            if (isDefeated(key)) count++;
        }
        return count;
    }

    public Set<EntityType<?>> getDefeatedBosses() {
        return defeatedBosses.keySet();
    }

    public Set<ResourceKey<? extends GameEvent>> getDefeatedEvents() {
        return defeatedEvents.keySet();
    }

    public void defeat(EntityType<?> entityType) {
        defeat(net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer(), entityType);
    }

    public void defeat(MinecraftServer server, EntityType<?> entityType) {
        boolean defeated = defeatedBosses.put(entityType, true);
        if (!defeated) {
            LanternNightGameEvent.INSTANCE.schedule();
        }
        if (entityType == BossEntities.SKELETRON.get()) {
            advanceGamePhase(server, GamePhase.AFTER_SKELETRON);
        } else if (entityType == BossEntities.WALL_OF_FLESH.get() || entityType == BossEntities.HILL_OF_FLESH.get()) {
            advanceGamePhase(server, GamePhase.WALL_OF_FLESH);
        } else if (isMechanicalBoss(entityType) && areAllMechanicalBossesDefeated()) {
            advanceGamePhase(server, GamePhase.MECHANICAL_BOSSES);
        } else if (entityType == BossEntities.PLANTERA.get()) {
            advanceGamePhase(server, GamePhase.PLANTERA);
        } else {
            KillBoardSyncPacketS2C.sendToAll();
        }
    }

    private static boolean isMechanicalBoss(EntityType<?> entityType) {
        return entityType == BossEntities.THE_DESTROYER.get()
                || entityType == BossEntities.THE_TWINS.get()
                || entityType == BossEntities.SKELETRON_PRIME.get();
    }

    private boolean areAllMechanicalBossesDefeated() {
        return isDefeated(BossEntities.THE_DESTROYER.get())
                && isDefeated(BossEntities.THE_TWINS.get())
                && isDefeated(BossEntities.SKELETRON_PRIME.get());
    }

    public void defeat(ResourceKey<? extends GameEvent> key) {
        boolean defeated = defeatedEvents.put(key, true);
        if (!defeated && GameEventSystem.isInvasionEvent(key)) {
            LanternNightGameEvent.INSTANCE.schedule();
        }
        KillBoardSyncPacketS2C.sendToAll();
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(MinecraftServer server, GamePhase gamePhase) {
        if (this.gamePhase == gamePhase) return;
        GamePhase previousPhase = this.gamePhase;
        this.gamePhase = gamePhase;
        KillBoardSyncPacketS2C.sendToAll();
        if (!previousPhase.isGraduated() && gamePhase.isGraduated()) {
            IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.GRADUATED);
        }
        if (!previousPhase.isHardmode() && gamePhase.isHardmode()) {
            onUnlockHardmode(server);
            HardmodeConvertor.INSTANCE.start(server, false);
        }
    }

    public void advanceGamePhase(MinecraftServer server, GamePhase gamePhase) {
        if (gamePhase.isAboveThan(this.gamePhase)) {
            setGamePhase(server, gamePhase);
        } else {
            KillBoardSyncPacketS2C.sendToAll();
        }
    }

    public void onUnlockHardmode(MinecraftServer server) {
        IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.HARDMODE);
        GlobalCloakData.INSTANCE.reveal(OreBlocks.CHLOROPHYTE_ORE.get().defaultBlockState());
        if (!getGamePhase().isHardmode()) {
            setGamePhase(server, GamePhase.WALL_OF_FLESH);
        }
    }

    /// 为网络编码创建稳定快照，避免编码过程读取到正在变化的服务端集合。
    public Object2BooleanMap<EntityType<?>> defeatedBossesSnapshot() {
        return new Object2BooleanOpenHashMap<>(defeatedBosses);
    }

    /// 为网络编码创建稳定的事件进度快照。
    public Object2BooleanMap<ResourceKey<? extends GameEvent>> defeatedEventsSnapshot() {
        return new Object2BooleanOpenHashMap<>(defeatedEvents);
    }

    /// 在客户端主线程一次性应用完整击杀榜状态。
    public void applyNetworkState(
            Object2BooleanMap<EntityType<?>> bosses,
            Object2BooleanMap<ResourceKey<? extends GameEvent>> events,
            GamePhase phase) {
        this.defeatedBosses = new Object2BooleanOpenHashMap<>(bosses);
        this.defeatedEvents = new Object2BooleanOpenHashMap<>(events);
        this.gamePhase = phase;
    }

    @Override
    public void decode(CompoundTag tag) {
        if (tag.isEmpty()) {
            return;
        }
        if (!tag.contains("defeated_bosses")
                || !tag.contains("defeated_events")
                || !tag.contains("game_phase", Tag.TAG_INT)) {
            throw new IllegalArgumentException(
                    "Kill-board data is missing a required field or contains an invalid field type");
        }
        Object2BooleanMap<EntityType<?>> decodedBosses =
                PortDataResultExtension.getOrThrow(
                        DEFEATED_BOSSES_CODEC.parse(
                                NbtOps.INSTANCE, tag.get("defeated_bosses")),
                        message -> new IllegalArgumentException(
                                "Failed to decode defeated bosses: " + message));
        Object2BooleanMap<ResourceKey<? extends GameEvent>> decodedEvents =
                PortDataResultExtension.getOrThrow(
                        DEFEATED_EVENTS_CODEC.parse(
                                NbtOps.INSTANCE, tag.get("defeated_events")),
                        message -> new IllegalArgumentException(
                                "Failed to decode defeated events: " + message));
        int phaseOrder = tag.getInt("game_phase");
        GamePhase decodedPhase = GamePhase.getByOrder(phaseOrder);
        if (decodedPhase.getOrder() != phaseOrder) {
            throw new IllegalArgumentException(
                    "Unsupported game phase order: " + phaseOrder);
        }
        this.defeatedBosses = new Object2BooleanOpenHashMap<>(decodedBosses);
        this.defeatedEvents = new Object2BooleanOpenHashMap<>(decodedEvents);
        this.gamePhase = decodedPhase;
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.put("defeated_bosses", PortDataResultExtension.getOrThrow(
                DEFEATED_BOSSES_CODEC.encodeStart(
                        NbtOps.INSTANCE, defeatedBosses),
                message -> new IllegalStateException(
                        "Failed to encode defeated bosses: " + message)));
        tag.put("defeated_events", PortDataResultExtension.getOrThrow(
                DEFEATED_EVENTS_CODEC.encodeStart(
                        NbtOps.INSTANCE, defeatedEvents),
                message -> new IllegalStateException(
                        "Failed to encode defeated events: " + message)));
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
