package org.confluence.mod.common.gameevent;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.util.OverworldUtils;

/// 暴风雪。
///
/// 触发条件就是**雪原 + 下雨**，与原作一致（暴风雪是「雪原中的雨」，不是「风大的雪」），
/// **与风速无关**——所以它不参与风系统的任何阈值判定，只在激活期间推雪原露天的实体。
///
/// 事件资源（`confluence:blizzard_strong_loop` / `blizzard_inside_building_loop` 两个环境音效）
/// 在本模组里早已存在但此前没有任何逻辑使用，这里把它们接起来。
public enum BlizzardGameEvent implements GameEvent, WindPushedEvent {
    INSTANCE;

    public static final ResourceKey<BlizzardGameEvent> KEY = GameEvent.createKey(Confluence.asResource("blizzard"));
    /// 非玩家实体的推力系数。
    public static final float COUPLING = 0.04F;

    private ServerLevel level;
    private boolean active;
    private boolean requested;

    @Override
    public void open(MinecraftServer server) {
        level = OverworldUtils.getLevel(server);
        requested = false;
    }

    @Override
    public void close(MinecraftServer server) {
        active = false;
        level = null;
        requested = false;
    }

    @Override
    public void tick() {}

    @Override
    public boolean canStart() {
        if (level == null) return false;
        return requested || (level.isRaining() && anyPlayerInSnowyBiome());
    }

    private boolean anyPlayerInSnowyBiome() {
        for (ServerPlayer player : level.players()) {
            if (OverworldUtils.isSnowy(level.getBiome(player.blockPosition()))) return true;
        }
        return false;
    }

    @Override
    public boolean canEnd() {
        if (level == null) return true;
        return !requested && !level.isRaining();
    }

    @Override
    public void onStart() {
        active = true;
        requested = false;
    }

    @Override
    public void onEnd() {
        active = false;
    }

    @Override
    public boolean started() {
        return active;
    }

    @Override
    public boolean forceStart() {
        if (active || level == null) return false;
        requested = true;
        return true;
    }

    @Override
    public void forceEnd() {
        requested = false;
    }

    @Override
    public void decode(CompoundTag tag) {
        active = tag.getBoolean("Started");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", active);
    }

    @Override
    public ResourceKey<BlizzardGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }

    // ===================== WindPushedEvent =====================

    @Override
    public boolean inWindZone(Level level, BlockPos pos) {
        return OverworldUtils.isSnowy(level.getBiome(pos)) && level.canSeeSky(pos);
    }

    @Override
    public float couplingFor(Entity entity) {
        return COUPLING;
    }

    @Override
    public boolean windPushEnabled() {
        return CommonConfigs.BLIZZARD_PUSHES_ENTITIES.get();
    }
}
