package org.confluence.mod.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ViewportEvent;

import java.util.ArrayList;
import java.util.List;

/// 按震源距离和剩余时间衰减相机偏角。
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

    public static void clear() {
        shakeLevel = null;
        pulses.clear();
    }

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
