package org.confluence.mod.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.confluence.mod.Confluence;

/// 头部事件触发的距离衰减震屏；不保存实体引用，退出世界后自动失效。
@EventBusSubscriber(modid = Confluence.MODID, value = Dist.CLIENT)
public final class DestroyerCameraShake {
    /// 最大相机偏角，单位度；只影响画面，不修改玩家瞄准和服务端朝向。
    private static final float MAX_ANGLE = 1.2F;
    private static ClientLevel shakeLevel;
    private static long endTick;
    private static int duration;
    private static float strength;

    public static void start(Entity head, double radius, int ticks) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || head.level() != client.level) return;
        double distance = client.player.getEyePosition().distanceTo(head.getBoundingBox().getCenter());
        if (distance >= radius) return;
        float amount = (float) (1 - distance / radius);
        long now = client.level.getGameTime();
        if (shakeLevel != client.level || now >= endTick) strength = 0;
        shakeLevel = client.level;
        strength = Math.max(strength, amount);
        endTick = Math.max(now + ticks, endTick > now && endTick <= now + 20 ? endTick : now);
        duration = ticks;
    }

    @SubscribeEvent
    public static void levelUnload(LevelEvent.Unload event) {
        if (event.getLevel() == shakeLevel) {
            shakeLevel = null;
            strength = 0;
            endTick = 0;
        }
    }

    @SubscribeEvent
    public static void cameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.level != shakeLevel || client.player == null) {
            shakeLevel = null;
            return;
        }
        double time = client.level.getGameTime() + event.getPartialTick();
        if (time >= endTick) {
            shakeLevel = null;
            return;
        }
        float amount = strength * MAX_ANGLE * (float) Math.min(1, (endTick - time) / duration);
        event.setPitch(event.getPitch() + amount * (float) Math.sin(time * 2.3));
        event.setYaw(event.getYaw() + amount * (float) Math.sin(time * 1.7));
        event.setRoll(event.getRoll() + amount * (float) Math.cos(time * 2.7) * 0.5F);
    }
}
