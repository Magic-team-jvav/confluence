package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.OverworldUtils;

public enum LanternNightGameEvent implements GameEvent {
    INSTANCE;
    public static final ResourceKey<LanternNightGameEvent> KEY = GameEvent.createKey(Confluence.asResource("lantern_night"));
    private static final AttributeModifier MODIFIER = new AttributeModifier(Confluence.asResource("lantern_night"), 0.3, AttributeModifier.Operation.ADD_VALUE);

    private transient MinecraftServer server;
    private transient ServerLevel level;
    private transient boolean forceStart;
    private transient boolean forceEnd;
    private boolean started;
    private boolean scheduled;
    private int cooldown;

    @Override
    public void open(MinecraftServer server) {
        this.server = server;
        this.level = OverworldUtils.getLevel(server);
    }

    @Override
    public void close(MinecraftServer server) {
        this.server = null;
        this.level = null;
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            --this.cooldown;
            return;
        }
        if (started && level.getGameTime() % 20 == 5) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                AttributeInstance instance = player.getAttribute(Attributes.LUCK);
                if (instance == null) continue;
                if (player.level().dimension() == OverworldUtils.dimension()) {
                    instance.addOrUpdateTransientModifier(MODIFIER);
                } else {
                    instance.removeModifier(MODIFIER);
                }
            }
        }
    }

    public void schedule() {
        this.scheduled = true;
    }

    @Override
    public boolean canStart() {
        if (forceStart) {
            return true;
        }
        boolean canStart = false;
        if (LibDateUtils.getDayTime(level) == LibDateUtils._19$30 && cooldown <= 0) {
            if (scheduled) {
                canStart = true;
            } else if (DateUtils.isYuanXiao(DateUtils.getLunar())) {
                canStart = true;
            } else if (KillBoard.INSTANCE.getGamePhase().isGraduated() && level.random.nextInt(14) == 0) {
                this.cooldown = level.random.nextIntBetweenInclusive(120000, 240000); // 5-10天
                canStart = true;
            }
        }
        if (canStart) {
            return !BloodMoonGameEvent.INSTANCE.started() && Boss.noBossInWorld(level);
        }
        return false;
    }

    @Override
    public boolean canEnd() {
        if (forceEnd) {
            return true;
        }
        return LibDateUtils.isWithinDayTime(LibDateUtils._04$30, LibDateUtils._19$30, level);
    }

    @Override
    public void onStart() {
        this.forceStart = false;
        this.scheduled = false;
        this.started = true;
    }

    @Override
    public void onEnd() {
        this.forceEnd = false;
        this.started = false;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            AttributeInstance instance = player.getAttribute(Attributes.LUCK);
            if (instance == null) continue;
            instance.removeModifier(MODIFIER);
        }
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public boolean forceStart() {
        if (started) return false;
        if (LibDateUtils.isWithinDayTime(LibDateUtils._19$30, LibDateUtils._04$30, level)) {
            this.forceStart = true;
            return true;
        }
        return false;
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
        this.scheduled = tag.getBoolean("Scheduled");
        this.cooldown = tag.getInt("Cooldown");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", started);
        tag.putBoolean("Scheduled", scheduled);
        tag.putInt("Cooldown", cooldown);
    }

    @Override
    public ResourceKey<LanternNightGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }
}
