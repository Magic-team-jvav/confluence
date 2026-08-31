package org.confluence.mod.client.renderer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * 虚空海多层纹理的默认值与本地测试值。
 * 默认值在重启后恢复；游戏内可使用 {@code /confluence voidSea get} 查看当前值，
 * 使用 {@code /confluence voidSea set <参数> <值>} 临时调整单项参数。
 */
public class VoidSeaRenderSettings {
    /**
     * 默认叠加层数（单位：层）。
     */
    public static final int DEFAULT_LAYER_COUNT = 32;
    /**
     * 默认纹理单元的世界尺寸（单位：格）。
     */
    public static final float DEFAULT_TILE_SIZE = 128;
    /**
     * 默认黑色海面透明度。
     */
    public static final float DEFAULT_BASE_ALPHA = 0.45F;
    /**
     * 默认纹理细节叠加强度。
     */
    public static final float DEFAULT_DETAIL_ALPHA = 0.35F;
    /**
     * 默认纹理细节亮度。
     */
    public static final float DEFAULT_DETAIL_BRIGHTNESS = 0.7F;
    /**
     * 默认纹理流动速度（单位：纹理坐标/刻）。
     */
    public static final float DEFAULT_FLOW_SPEED = 2;
    /**
     * 默认各层纹理缩放倍率。
     */
    public static final float DEFAULT_LAYER_SCALE_STEP = 1.15F;
    /**
     * 默认首层色相。
     */
    public static final float DEFAULT_HUE = 0.58F;
    /**
     * 默认相邻层色相间隔。
     */
    public static final float DEFAULT_HUE_STEP = 0.075F;
    /**
     * 默认纹理色调饱和度。
     */
    public static final float DEFAULT_SATURATION = 0.65F;
    /**
     * 默认闪烁亮度变化幅度。
     */
    public static final float DEFAULT_FLICKER_INTENSITY = 0.15F;
    /**
     * 默认闪烁速度（单位：弧度/刻）。
     */
    public static final float DEFAULT_FLICKER_SPEED = 0.07F;

    /**
     * 当前叠加层数（单位：层）。
     */
    private static int layerCount = DEFAULT_LAYER_COUNT;
    /**
     * 当前纹理单元的世界尺寸（单位：格）。
     */
    private static float tileSize = DEFAULT_TILE_SIZE;
    /**
     * 当前黑色海面透明度。
     */
    private static float baseAlpha = DEFAULT_BASE_ALPHA;
    /**
     * 当前纹理细节叠加强度。
     */
    private static float detailAlpha = DEFAULT_DETAIL_ALPHA;
    /**
     * 当前纹理细节亮度。
     */
    private static float detailBrightness = DEFAULT_DETAIL_BRIGHTNESS;
    /**
     * 当前纹理流动速度（单位：纹理坐标/刻）。
     */
    private static float flowSpeed = DEFAULT_FLOW_SPEED;
    /**
     * 当前各层纹理缩放倍率。
     */
    private static float layerScaleStep = DEFAULT_LAYER_SCALE_STEP;
    /**
     * 当前首层色相。
     */
    private static float hue = DEFAULT_HUE;
    /**
     * 当前相邻层色相间隔。
     */
    private static float hueStep = DEFAULT_HUE_STEP;
    /**
     * 当前纹理色调饱和度。
     */
    private static float saturation = DEFAULT_SATURATION;
    /**
     * 当前闪烁亮度变化幅度。
     */
    private static float flickerIntensity = DEFAULT_FLICKER_INTENSITY;
    /**
     * 当前闪烁速度（单位：弧度/刻）。
     */
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
                                .then(Commands.literal("layers").then(Commands.argument("value", IntegerArgumentType.integer(1, 32)).executes(context -> {
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
