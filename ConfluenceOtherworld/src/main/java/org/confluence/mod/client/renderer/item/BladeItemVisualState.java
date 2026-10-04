package org.confluence.mod.client.renderer.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;

import java.util.Map;
import java.util.WeakHashMap;

final class BladeItemVisualState {
    private static final double TRANSITION_TICKS = 10;
    private static final double FLASH_TICKS = 14;
    private static final double FLASH_INTERVAL = 60;
    private static final Map<ItemStack, BladeItemVisualState> STATES = new WeakHashMap<>();
    private boolean on;
    private double changedAt;
    private double from;

    private BladeItemVisualState(double time) {
        changedAt = time;
    }

    static BladeItemVisualState get(ItemStack stack, double time) {
        BladeItemVisualState state = STATES.computeIfAbsent(stack, ignored -> new BladeItemVisualState(time));
        boolean on = BasePhasebladeItem.isTurnOn(stack);
        if (state.on != on || time < state.changedAt) {
            state.from = state.extension(time);
            state.on = on;
            state.changedAt = time;
        }
        return state;
    }

    static double time(float partialTick) {
        var level = Minecraft.getInstance().level;
        return level == null ? 0 : level.getGameTime() + partialTick;
    }

    double extension(double time) {
        double progress = Math.max(0, Math.min(1, (time - changedAt) / TRANSITION_TICKS));
        return from + ((on ? 1 : 0) - from) * progress;
    }

    int lightningFrame(double time) {
        if (!on) return -1;
        double elapsed = Math.max(0, time - changedAt);
        if (elapsed < FLASH_TICKS) return (int) (elapsed / 2);
        double idleTime = elapsed - TRANSITION_TICKS;
        if (idleTime < FLASH_INTERVAL) return -1;
        double phase = idleTime % FLASH_INTERVAL;
        return phase < FLASH_TICKS ? (int) (phase / 2) : -1;
    }
}
