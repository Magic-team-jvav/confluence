package org.confluence.mod.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.confluence.mod.Confluence;

import java.util.ArrayList;
import java.util.List;

/// 按震源距离和剩余时间衰减相机偏角。
@EventBusSubscriber(modid = Confluence.MODID, value = Dist.CLIENT)
public final class CameraShake {
    private record Pulse(float angle, long endTick, int duration) {}

    private static final List<Pulse> pulses = new ArrayList<>();
    private static ClientLevel shakeLevel;

    /// 每次震动独立传入半径（格）、时长（tick）和最大偏角（度）。
    public static void start(Level level, Vec3 origin, double radius, int ticks, float maxAngle) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || level != client.level) return;
        double distance = client.player.getEyePosition().distanceTo(origin);
        if (distance >= radius || ticks <= 0 || maxAngle <= 0) return;
        if (shakeLevel != client.level) pulses.clear();
        shakeLevel = client.level;
        float angle = maxAngle * (float) (1 - distance / radius);
        pulses.add(new Pulse(angle, level.getGameTime() + ticks, ticks));
    }

    @SubscribeEvent
    public static void levelUnload(LevelEvent.Unload event) {
        if (event.getLevel() == shakeLevel) {
            shakeLevel = null;
            pulses.clear();
        }
    }

    @SubscribeEvent
    public static void cameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.level != shakeLevel || client.player == null) {
            shakeLevel = null;
            pulses.clear();
            return;
        }
        double time = client.level.getGameTime() + event.getPartialTick();
        float amount = 0;
        for (int i = pulses.size() - 1; i >= 0; i--) {
            Pulse pulse = pulses.get(i);
            double remaining = pulse.endTick() - time;
            if (remaining <= 0) pulses.remove(i);
            else
                amount = Math.max(amount, pulse.angle() * (float) Math.min(1, remaining / pulse.duration()));
        }
        if (amount == 0) return;
        event.setPitch(event.getPitch() + amount * (float) Math.sin(time * 2.3));
        event.setYaw(event.getYaw() + amount * (float) Math.sin(time * 1.7));
        event.setRoll(event.getRoll() + amount * (float) Math.cos(time * 2.7) * 0.5F);
    }
}
