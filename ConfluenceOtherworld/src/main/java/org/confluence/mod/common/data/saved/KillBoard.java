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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.gameevent.LanternNightGameEvent;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.network.s2c.KillBoardSyncPacketS2C;
import org.confluence.mod.util.ModUtils;
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

    public boolean isDefeated(EntityType<?> type) {
        return defeatedBosses.getBoolean(type);
    }

    public boolean isDefeated(ResourceKey<? extends GameEvent> key) {
        return defeatedEvents.getBoolean(key);
    }

    public boolean isAnyDefeated(EntityType<?>... types) {
        for (EntityType<?> entityType : types) {
            if (isDefeated(entityType)) return true;
        }
        return false;
    }

    public boolean isEvilDefeated() {
        return isAnyDefeated(BossEntities.EATER_OF_WORLDS.get(), BossEntities.BRAIN_OF_CTHULHU.get());
    }

    public boolean isFleshDefeated() {
        return isAnyDefeated(BossEntities.WALL_OF_FLESH.get(), BossEntities.HILL_OF_FLESH.get());
    }

    public boolean isTwinsDefeated() {
        return isDefeated(BossEntities.RETINAZER.get()) && isDefeated(BossEntities.SPAZMATISM.get());
    }

    public boolean isAnyMechBossDefeated() {
        return isTwinsDefeated() ||
                isDefeated(BossEntities.THE_DESTROYER.get()) ||
                isDefeated(BossEntities.SKELETRON_PRIME.get());
    }

    public int countDefeated(EntityType<?>... types) {
        int count = 0;
        for (EntityType<?> entityType : types) {
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

    public void defeat(EntityType<?> type) {
        if (!defeatedBosses.put(type, true)) {
            LanternNightGameEvent.INSTANCE.schedule();
            if (isDefeated(BossEntities.SKELETRON.get())) {
                setGamePhase(GamePhase.AFTER_SKELETRON);
            } else if (isFleshDefeated()) {
                setGamePhase(GamePhase.WALL_OF_FLESH);
                ModUtils.prepareHardmode();
            } else if (isAnyMechBossDefeated()) {
                setGamePhase(GamePhase.MECHANICAL_BOSSES);
            } else if (isDefeated(BossEntities.PLANTERA.get())) {
                setGamePhase(GamePhase.PLANTERA);
            }
            // todo GOLEM MOON_LORD
            KillBoardSyncPacketS2C.sendToAll();
        }
    }

    public void defeat(ResourceKey<? extends GameEvent> key) {
        if (!defeatedEvents.put(key, true)) {
            if (GameEventSystem.isInvasionEvent(key)) {
                LanternNightGameEvent.INSTANCE.schedule();
            }
            KillBoardSyncPacketS2C.sendToAll();
        }
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    private void setGamePhase(GamePhase gamePhase) {
        if (gamePhase.isAboveThan(this.gamePhase)) {
            this.gamePhase = gamePhase;
        }
    }

    public void networkEncode(PortRegistryFriendlyByteBuf buffer) {
        DEFEATED_BOSSES_STREAM_CODEC.encode(buffer, defeatedBosses);
        DEFEATED_EVENTS_STREAM_CODEC.encode(buffer, defeatedEvents);
        GamePhase.STREAM_CODEC.encode(buffer, gamePhase);
    }

    public void networkDecode(PortRegistryFriendlyByteBuf buffer) {
        this.defeatedBosses = DEFEATED_BOSSES_STREAM_CODEC.decode(buffer);
        this.defeatedEvents = DEFEATED_EVENTS_STREAM_CODEC.decode(buffer);
        this.gamePhase = GamePhase.STREAM_CODEC.decode(buffer);
    }

    @Override
    public void decode(CompoundTag tag) {
        PortDataResultExtension.ifSuccess(DEFEATED_BOSSES_CODEC.parse(NbtOps.INSTANCE, tag.getCompound("defeated_bosses")), result -> this.defeatedBosses = new Object2BooleanOpenHashMap<>(result));
        PortDataResultExtension.ifSuccess(DEFEATED_EVENTS_CODEC.parse(NbtOps.INSTANCE, tag.get("defeated_events")), result -> this.defeatedEvents = new Object2BooleanOpenHashMap<>(result));
        this.gamePhase = GamePhase.getByOrder(tag.getInt("game_phase"));
    }

    @Override
    public void encode(CompoundTag tag) {
        PortDataResultExtension.ifSuccess(DEFEATED_BOSSES_CODEC.encodeStart(NbtOps.INSTANCE, defeatedBosses), nbt -> tag.put("defeated_bosses", nbt));
        PortDataResultExtension.ifSuccess(DEFEATED_EVENTS_CODEC.encodeStart(NbtOps.INSTANCE, defeatedEvents), nbt -> tag.put("defeated_events", nbt));
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
