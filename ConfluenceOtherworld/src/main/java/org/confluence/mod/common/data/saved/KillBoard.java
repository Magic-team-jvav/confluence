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
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.gameevent.LanternNightGameEvent;
import org.confluence.mod.common.init.block.OreBlocks;
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

    public boolean isAnyDefeated(EntityType<?>... entityTypes) {
        for (EntityType<?> entityType : entityTypes) {
            if (isDefeated(entityType)) return true;
        }
        return false;
    }

    public boolean isAnyMechBossDefeated() {
        return isDefeated(TEBossEntities.THE_TWINS.get()) ||
                isDefeated(TEBossEntities.THE_DESTROYER.get()) ||
                isDefeated(TEBossEntities.SKELETRON_PRIME.get());
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
        boolean defeated = defeatedBosses.put(entityType, true);
        if (!defeated) {
            LanternNightGameEvent.INSTANCE.schedule();
        }
        if (entityType == TEBossEntities.SKELETRON.get()) {
            setGamePhase(net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer(), GamePhase.AFTER_SKELETRON);
        } else if (entityType == TEBossEntities.WALL_OF_FLESH.get() || entityType == TEBossEntities.HILL_OF_FLESH.get()) {
            setGamePhase(net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer(), GamePhase.WALL_OF_FLESH);
        } else {
            KillBoardSyncPacketS2C.sendToAll();
        }
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
        this.gamePhase = gamePhase;
        KillBoardSyncPacketS2C.sendToAll();
        if (gamePhase.isGraduated()) {
            IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.GRADUATED);
        } else if (gamePhase.isHardmode()) {
            onUnlockHardmode(server);
            HardmodeConvertor.INSTANCE.start(server, false);
        }
    }

    public void onUnlockHardmode(MinecraftServer server) {
        IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.HARDMODE);
        GlobalCloakData.INSTANCE.reveal(OreBlocks.CHLOROPHYTE_ORE.get().defaultBlockState());
        if (!getGamePhase().isHardmode()) {
            setGamePhase(server, GamePhase.WALL_OF_FLESH);
        }
    }

    public void networkEncode(org.mesdag.portlib.network.PortRegistryFriendlyByteBuf buffer) {
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
        PortDataResultExtension.ifSuccess(DEFEATED_BOSSES_CODEC.parse(NbtOps.INSTANCE, tag.get("defeated_bosses")),
                result -> this.defeatedBosses = new Object2BooleanOpenHashMap<>(result));
        PortDataResultExtension.ifSuccess(DEFEATED_EVENTS_CODEC.parse(NbtOps.INSTANCE, tag.get("defeated_events")),
                result -> this.defeatedEvents = new Object2BooleanOpenHashMap<>(result));
        this.gamePhase = GamePhase.getByOrder(tag.getInt("game_phase"));
    }

    @Override
    public void encode(CompoundTag tag) {
        PortDataResultExtension.ifSuccess(DEFEATED_BOSSES_CODEC.encodeStart(NbtOps.INSTANCE, defeatedBosses),
                nbt -> tag.put("defeated_bosses", nbt));
        PortDataResultExtension.ifSuccess(DEFEATED_EVENTS_CODEC.encodeStart(NbtOps.INSTANCE, defeatedEvents),
                nbt -> tag.put("defeated_events", nbt));
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
