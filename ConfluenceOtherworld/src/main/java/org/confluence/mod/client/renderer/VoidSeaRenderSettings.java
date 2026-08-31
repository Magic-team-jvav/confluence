package org.confluence.mod.client.renderer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/// 虚空海多层纹理的值与本地测试值。<br/>
/// 值在重启后恢复；游戏内可使用 {@code /confluence voidSea get} 查看当前值，<br/>
/// 使用 {@code /confluence voidSea set <参数> <值>} 临时调整单项参数。
public class VoidSeaRenderSettings {
    /// 叠加层数（单位：层）。
    private static final int DEFAULT_LAYER_COUNT = 32;
    private static int layerCount = DEFAULT_LAYER_COUNT;

    /// 纹理单元的世界尺寸（单位：格）。
    private static final float DEFAULT_TILE_SIZE = 128;
    private static float tileSize = DEFAULT_TILE_SIZE;

    /// 黑色海面透明度。
    public static final float DEFAULT_BASE_ALPHA = 0.45F;
    private static float baseAlpha = DEFAULT_BASE_ALPHA;

    /// 纹理细节叠加强度。
    private static final float DEFAULT_DETAIL_ALPHA = 0.35F;
    private static float detailAlpha = DEFAULT_DETAIL_ALPHA;

    /// 纹理细节亮度。
    private static final float DEFAULT_DETAIL_BRIGHTNESS = 0.7F;
    private static float detailBrightness = DEFAULT_DETAIL_BRIGHTNESS;

    /// 纹理流动速度（单位：纹理坐标/刻）。
    private static final float DEFAULT_FLOW_SPEED = 2;
    private static float flowSpeed = DEFAULT_FLOW_SPEED;

    /// 各层纹理缩放倍率。
    private static final float DEFAULT_LAYER_SCALE_STEP = 1.15F;
    private static float layerScaleStep = DEFAULT_LAYER_SCALE_STEP;

    /// 首层色相。
    private static final float DEFAULT_HUE = 0.58F;
    private static float hue = DEFAULT_HUE;

    /// 相邻层色相间隔。
    private static final float DEFAULT_HUE_STEP = 0.075F;
    private static float hueStep = DEFAULT_HUE_STEP;

    /// 纹理色调饱和度。
    private static final float DEFAULT_SATURATION = 0.65F;
    private static float saturation = DEFAULT_SATURATION;

    /// 闪烁亮度变化幅度。
    private static final float DEFAULT_FLICKER_INTENSITY = 0.15F;
    private static float flickerIntensity = DEFAULT_FLICKER_INTENSITY;

    /// 闪烁速度（单位：弧度/刻）。
    private static final float DEFAULT_FLICKER_SPEED = 0.07F;
    private static float flickerSpeed = DEFAULT_FLICKER_SPEED;

    public static int getLayerCount() {
        return layerCount;
    }

    public static float getTileSize() {
        return tileSize;
    }

    public static float getBaseAlpha() {
        return baseAlpha;
    }

    public static float getDetailAlpha() {
        return detailAlpha;
    }

    public static float getDetailBrightness() {
        return detailBrightness;
    }

    public static float getFlowSpeed() {
        return flowSpeed;
    }

    public static float getLayerScaleStep() {
        return layerScaleStep;
    }

    public static float getHue() {
        return hue;
    }

    public static float getHueStep() {
        return hueStep;
    }

    public static float getSaturation() {
        return saturation;
    }

    public static float getFlickerIntensity() {
        return flickerIntensity;
    }

    public static float getFlickerSpeed() {
        return flickerSpeed;
    }

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("confluence")
                .then(Commands.literal("voidSea")
                        .then(Commands.literal("get").executes(context -> show(context.getSource())))
                        .then(Commands.literal("set")
                                .then(Commands.literal("layers").then(Commands.argument("value", IntegerArgumentType.integer(1, 128)).executes(context -> {
                                    layerCount = IntegerArgumentType.getInteger(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("tileSize").then(Commands.argument("value", FloatArgumentType.floatArg(1.0F)).executes(context -> {
                                    tileSize = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("baseAlpha").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, 1.0F)).executes(context -> {
                                    baseAlpha = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("detailAlpha").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, 1.0F)).executes(context -> {
                                    detailAlpha = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("detailBrightness").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F)).executes(context -> {
                                    detailBrightness = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("flowSpeed").then(Commands.argument("value", FloatArgumentType.floatArg()).executes(context -> {
                                    flowSpeed = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("layerScaleStep").then(Commands.argument("value", FloatArgumentType.floatArg(0.01F)).executes(context -> {
                                    layerScaleStep = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("hue").then(Commands.argument("value", FloatArgumentType.floatArg()).executes(context -> {
                                    hue = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("hueStep").then(Commands.argument("value", FloatArgumentType.floatArg()).executes(context -> {
                                    hueStep = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("saturation").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, 1.0F)).executes(context -> {
                                    saturation = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("flickerIntensity").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, 1.0F)).executes(context -> {
                                    flickerIntensity = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                                .then(Commands.literal("flickerSpeed").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F)).executes(context -> {
                                    flickerSpeed = FloatArgumentType.getFloat(context, "value");
                                    return show(context.getSource());
                                })))
                        )
                )
        );
    }

    private static int show(CommandSourceStack source) {
        source.sendSystemMessage(Component.literal("Void sea: layers=" + layerCount
                + ", tileSize=" + tileSize
                + ", baseAlpha=" + baseAlpha
                + ", detailAlpha=" + detailAlpha
                + ", detailBrightness=" + detailBrightness
                + ", flowSpeed=" + flowSpeed
                + ", layerScaleStep=" + layerScaleStep
                + ", hue=" + hue
                + ", hueStep=" + hueStep
                + ", saturation=" + saturation
                + ", flickerIntensity=" + flickerIntensity
                + ", flickerSpeed=" + flickerSpeed));
        return 1;
    }
}
