package org.confluence.lib.client.render.visual_effects;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public abstract class VisualEffects {
    public abstract void render(
            final Vec3 vec3,
            final RandomSource randomSource,
            final PoseStack poseStack,
            final MultiBufferSource bufferSource,
            final int packedLight);

    /**
     * 获取客户端Minecraft
     */
    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    /**
     * 获取客户端世界
     */
    public static ClientLevel getLevel() {
        return getMinecraft().level;
    }

    /**
     * 获取客户端玩家
     */
    public static LocalPlayer getPlayer() {
        return getMinecraft().player;
    }

    /**
     * 获取游戏渲染器
     */
    public static GameRenderer getGameRenderer() {
        return getMinecraft().gameRenderer;
    }

    /**
     * 获取世界渲染器
     */
    public static LevelRenderer getLevelRenderer() {
        return getMinecraft().levelRenderer;
    }

    /**
     * 获取游戏相机
     */
    public static Camera getCamera() {
        return getGameRenderer().getMainCamera();
    }

    /**
     * 获取游戏字体
     */
    public static Font getFont() {
        return getMinecraft().font;
    }

    /**
     * 获取游戏窗口
     */
    public static Window getWindow() {
        return getMinecraft().getWindow();
    }

    /**
     * 获取游戏计时器
     */
    public static DeltaTracker getDeltaTracker() {
        return getMinecraft().getTimer();
    }

    /**
     * 获取游戏时间差
     */
    public static float getPartialTicks() {
        return getDeltaTracker().getGameTimeDeltaPartialTick(true);
    }
}
