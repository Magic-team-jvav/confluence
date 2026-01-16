package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.terraentity.init.entity.TEBossEntities;

/// [史莱姆雨](https://terraria.wiki.gg/zh/wiki/%E5%8F%B2%E8%8E%B1%E5%A7%86%E9%9B%A8)
public final class SlimeRainGameEvent implements GameEvent {
    public static final ResourceKey<SlimeRainGameEvent> KEY = GameEvent.createKey(Confluence.asResource("slime_rain"));
    public static final SlimeRainGameEvent INSTANCE = new SlimeRainGameEvent();
    public static final String ENTITY_TAG = "spawn_during_slime_rain";
    private static final int _12$00 = LibDateUtils.getDayTime(12, 0);
    private static final int factor = 675000; // 经典模式、困难模式前、未击败史莱姆王
    private MinecraftServer server;
    private ServerLevel level;
    private int cooldown;
    private int duration;
    private transient boolean canStart;
    private transient boolean canEnd;

    private SlimeRainGameEvent() {}

    @Override
    public void init(MinecraftServer server) {
        this.server = server;
        this.level = server.overworld();
        if (duration <= 0 && cooldown <= 0) {
            this.cooldown = level.getGameTime() < 24000L ? level.random.nextIntBetweenInclusive(24, 48) * 1200 : 0;
        }
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            --this.cooldown;
            this.canStart = false;
            return;
        }
        if (duration > 0) {
            running();
        } else {
            checkCanStart();
        }
    }

    private void running() {
        --this.duration;
        // todo 生成逻辑
    }

    private void checkCanStart() {
        int invChance = 0;
        if (LibDateUtils.isWithinDayTime(LibDateUtils._04$30, _12$00, level)) { // 时间
            if (!level.isRaining()) { // todo 事件
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    boolean expert = LibUtils.isAtLeastExpert(level, player.blockPosition());
                    if (player.getMaxHealth() >= 28 && player.getArmorValue() >= 14) { // 属性
                        invChance = factor; // 满足要求
                        break;
                    } else if (expert) {
                        invChance = factor * 5; // 不满足要求但是专家模式
                        break;
                    }
                }
            }
        }
        if (invChance > 0) {
            if (KillBoard.INSTANCE.isDefeated(TEBossEntities.KING_SLIME.get())) { // 击败史莱姆王
                invChance = invChance * 2;
            }
            if (KillBoard.INSTANCE.getGamePhase().isHardmode()) { // 困难模式
                invChance = invChance * 3 / 2;
            }
            this.canStart = level.random.nextInt(invChance) == 0;
        } else {
            this.canStart = false;
        }
    }

    public void setCanEnd() {
        this.canEnd = true;
    }

    @Override
    public boolean canStart() {
        return canStart;
    }

    @Override
    public boolean canEnd() {
        return canEnd || duration <= 0;
    }

    @Override
    public void onStart() {
        this.duration = server.isDedicatedServer() ? 15 * 1200 : level.random.nextIntBetweenInclusive(9, 15) * 1200;
        Component component = Component.translatable("message.confluence.slime_rain.start").withColor(GlobalColors.MESSAGE.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
        }
    }

    @Override
    public void onEnd() {
        this.cooldown = server.isDedicatedServer() ? 0 : level.random.nextIntBetweenInclusive(84, 180) * 1200;
        Component component = Component.translatable("message.confluence.slime_rain.end").withColor(GlobalColors.MESSAGE.get());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
        }
    }

    @Override
    public boolean started() {
        return duration > 0;
    }

    @Override
    public void decode(CompoundTag tag) {
        this.cooldown = tag.getInt("Cooldown");
        this.duration = tag.getInt("Duration");
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putInt("Cooldown", cooldown);
        tag.putInt("Duration", duration);
    }

    @Override
    public ResourceKey<SlimeRainGameEvent> key() {
        return KEY;
    }
}
