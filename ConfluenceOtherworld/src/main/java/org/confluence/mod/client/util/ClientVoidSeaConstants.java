package org.confluence.mod.client.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ClientVoidSeaConstants {
    // 海面渲染
    /// 海面基础 RGB 颜色。
    public static final Vector3f SEA_COLOR = new Vector3f(0.0F, 0.0F, 0.0F);
    /// 海面交汇描边 RGB 颜色。
    public static final Vector3f EDGE_COLOR = new Vector3f(0.65F, 1.0F, 1.0F);
    /// 海面交汇核心描边宽度（单位：屏幕像素）。
    public static final float EDGE_CORE_WIDTH = 2.5F;
    /// 海面交汇外层光晕宽度（单位：屏幕像素）。
    public static final float EDGE_GLOW_WIDTH = 10.0F;
    /// 海面交汇核心描边强度。
    public static final float EDGE_CORE_STRENGTH = 1.0F;
    /// 海面交汇外层光晕强度。
    public static final float EDGE_GLOW_STRENGTH = 0.45F;
    /// 从海面下方观察时的交汇描边强度倍率。
    public static final float UNDERWATER_EDGE_STRENGTH_MULTIPLIER = 0.6F;
    /// 海面以下场景表面的发光 RGB 颜色。
    public static final Vector3f SUBMERGED_SURFACE_COLOR = new Vector3f(0.65F, 1.0F, 1.0F);
    /// 海面以下场景表面的基础发光强度。
    public static final float SUBMERGED_SURFACE_STRENGTH = 0.45F;
    /// 从海面下方观察时的场景表面发光强度倍率。
    public static final float UNDERWATER_SUBMERGED_SURFACE_STRENGTH_MULTIPLIER = 0.6F;
    /// 区块边长（单位：格）。
    public static final float CHUNK_SIZE = 16.0F;
    /// 海面中心简单网格半径（单位：格）。
    public static final float SIMPLE_MESH_RADIUS = 8.0F * CHUNK_SIZE;
    /// 首层海面环带单元尺寸（单位：格）。
    public static final float INITIAL_RING_CELL_SIZE = 16.0F;
    /// 海面的最小渲染半径（单位：格）。
    public static final float MIN_RENDER_RADIUS = 256.0F;
    /// 海面渲染范围相对视距的外扩距离（单位：格）。
    public static final float RENDER_DISTANCE_MARGIN = 32.0F;
    /// 海面纹理动画循环时长（单位：刻）。
    public static final long GAME_TIME_CYCLE = 24000L;
    /// 相邻海面环带的单元尺寸增长倍率。
    public static final float RING_CELL_SIZE_MULTIPLIER = 2.0F;

    // 海面纹理设置
    /// 测试命令允许的最小纹理层数。
    public static final int MIN_LAYER_COUNT = 1;
    /// 测试命令允许的最大纹理层数。
    public static final int MAX_LAYER_COUNT = 128;
    /// 测试命令允许的最小纹理世界尺寸（单位：格）。
    public static final float MIN_TILE_SIZE = 1.0F;
    /// 标准化参数的最小值。
    public static final float MIN_NORMALIZED_VALUE = 0.0F;
    /// 标准化参数的最大值。
    public static final float MAX_NORMALIZED_VALUE = 1.0F;
    /// 测试命令允许的最小纹理层缩放步长。
    public static final float MIN_LAYER_SCALE_STEP = 0.01F;
    /// 默认叠加纹理层数。
    public static final int DEFAULT_LAYER_COUNT = 32;
    /// 默认纹理单元世界尺寸（单位：格）。
    public static final float DEFAULT_TILE_SIZE = 128;
    /// 默认黑色海面透明度。
    public static final float DEFAULT_BASE_ALPHA = 0.45F;
    /// 默认纹理细节叠加强度。
    public static final float DEFAULT_DETAIL_ALPHA = 0.5F;
    /// 默认纹理细节亮度。
    public static final float DEFAULT_DETAIL_BRIGHTNESS = 1.0F;
    /// 默认纹理流动速度（单位：纹理坐标/刻）。
    public static final float DEFAULT_FLOW_SPEED = 2.0F;
    /// 默认相邻纹理层缩放倍率。
    public static final float DEFAULT_LAYER_SCALE_STEP = 1.15F;
    /// 默认首层色相。
    public static final float DEFAULT_HUE = 0.58F;
    /// 默认相邻纹理层色相间隔。
    public static final float DEFAULT_HUE_STEP = 0.075F;
    /// 默认纹理色调饱和度。
    public static final float DEFAULT_SATURATION = 0.65F;
    /// 默认闪烁亮度变化幅度。
    public static final float DEFAULT_FLICKER_INTENSITY = 0.15F;
    /// 默认闪烁速度（单位：弧度/刻）。
    public static final float DEFAULT_FLICKER_SPEED = 0.07F;

    // 折射与滤镜
    /// 原画面的折射缩放强度。
    public static final float REFRACTION_ZOOM = 0.1F;
    /// 屏幕边缘折射扭曲强度。
    public static final float REFRACTION_DISTORTION = 0.004F;
    /// 屏幕中心扭曲强度相对边缘的比例。
    public static final float REFRACTION_CENTER_DISTORTION = 0.5F;
    /// 滤镜每帧淡入淡出的插值速度。
    public static final float FILTER_FADE_SPEED = 0.15F;
    /// 折射波动速度（单位：循环/秒）。
    public static final float REFRACTION_SPEED = 0.8F;
    /// 黑色滤镜的初始圆半径（单位：屏幕比例）。
    public static final float BLACK_FILTER_RADIUS = 0.8F;
    /// 黑色滤镜的目标圆半径（单位：屏幕比例）。
    public static final float BLACK_FILTER_MAX_RADIUS = 0.4F;
    /// 紫色滤镜的初始圆半径（单位：屏幕比例）。
    public static final float PURPLE_FILTER_RADIUS = 0.4F;
    /// 紫色滤镜的目标圆半径（单位：屏幕比例）。
    public static final float PURPLE_FILTER_MAX_RADIUS = 0.8F;
    /// 黑色和紫色圆形滤镜的过渡范围比例。
    public static final float FILTER_TRANSITION_RATIO = 0.25F;
    /// 圆形滤镜过渡区的透明度降低比例。
    public static final float FILTER_TRANSITION_STRENGTH = 0.35F;
    /// 全屏黑色滤镜透明度。
    public static final float FULL_SCREEN_BLACK_FILTER_ALPHA = 0.35F;
    /// 黑色滤镜 RGB 颜色。
    public static final Vector3f BLACK_FILTER_COLOR = new Vector3f(0.0F, 0.0F, 0.0F);
    /// 黑色滤镜最大透明度。
    public static final float BLACK_FILTER_ALPHA = 0.70F;
    /// 黑色滤镜中心区域最小透明度。
    public static final float BLACK_FILTER_MIN_ALPHA = 0.5F;
    /// 紫色滤镜 RGB 颜色。
    public static final Vector3f PURPLE_FILTER_COLOR = new Vector3f(0.32F, 0.04F, 0.55F);
    /// 紫色滤镜最大透明度。
    public static final float PURPLE_FILTER_ALPHA = 0.60F;

    // 雾
    /// 虚空海水下雾 RGB 颜色。
    public static final Vector3f VOID_SEA_FOG_COLOR = new Vector3f(0.32F, 0.04F, 0.55F);
    /// 虚空海水下雾颜色亮度系数。
    public static final float VOID_SEA_FOG_BRIGHTNESS = 0.8F;
    /// 侵蚀伤害高度上方的滤镜变化范围（单位：格）。
    public static final float DAMAGE_EFFECT_RANGE = 20.0F;
    /// 常态虚空海雾的视野缩减比例。
    public static final float NORMAL_FOG_REDUCTION = 0.25F;
    /// 侵蚀干扰状态下虚空海雾的视野缩减比例。
    public static final float INTERFERENCE_FOG_REDUCTION = 0.6F;
    /// 虚空海雾的最小远平面距离（单位：格）。
    public static final float MINIMUM_FOG_DISTANCE = 64.0F;

    // 音效资源
    // TODO 替换为虚空海进入音效。
    /// 玩家进入虚空海时播放的音效。
    public static final SoundEvent ENTER_SOUND = SoundEvents.GENERIC_SPLASH;
    // TODO 替换为虚空海离开音效。
    /// 玩家离开虚空海时播放的音效。
    public static final SoundEvent EXIT_SOUND = SoundEvents.GENERIC_SPLASH;
    // TODO 替换为虚空海游泳音效。
    /// 玩家在虚空海内游泳时播放的音效。
    public static final SoundEvent SWIM_SOUND = SoundEvents.GENERIC_SWIM;

    // 粒子资源
    // TODO 替换为虚空海进入、离开粒子。
    /// 玩家穿过虚空海表面时生成的粒子类型。
    public static final SimpleParticleType SURFACE_PARTICLE = ParticleTypes.SPLASH;
    // TODO 替换为虚空海游泳粒子。
    /// 玩家在虚空海内游泳时生成的粒子类型。
    public static final SimpleParticleType SWIM_PARTICLE = ParticleTypes.BUBBLE;
    /// 虚空海海面持续生成的传送门粒子类型。
    public static final SimpleParticleType SEA_PORTAL_PARTICLE = ParticleTypes.PORTAL;

    // 音效参数
    /// 进入虚空海音效的音量。
    public static final float ENTER_SOUND_VOLUME = 0.8F;
    /// 进入虚空海音效的音调。
    public static final float ENTER_SOUND_PITCH = 0.9F;
    /// 离开虚空海音效的音量。
    public static final float EXIT_SOUND_VOLUME = 0.8F;
    /// 离开虚空海音效的音调。
    public static final float EXIT_SOUND_PITCH = 1.1F;
    /// 虚空海游泳音效的音量。
    public static final float SWIM_SOUND_VOLUME = 0.35F;
    /// 虚空海游泳音效的音调。
    public static final float SWIM_SOUND_PITCH = 1.0F;

    // 进入、离开和游泳粒子
    /// 每次穿过海面时生成的粒子数量。
    public static final int SURFACE_PARTICLE_COUNT = 8;
    /// 海面粒子水平随机值的中心偏移。
    public static final double SURFACE_PARTICLE_RANDOM_CENTER = 0.5D;
    /// 海面粒子生成高度相对玩家碰撞箱高度的比例。
    public static final float SURFACE_PARTICLE_HEIGHT_RATIO = 0.5F;
    /// 海面粒子水平速度相对玩家速度的倍率。
    public static final double SURFACE_PARTICLE_HORIZONTAL_SPEED_MULTIPLIER = 0.15D;
    /// 海面粒子的垂直速度（单位：格/刻）。
    public static final double SURFACE_PARTICLE_VERTICAL_SPEED = 0.08D;
    /// 游泳粒子和音效的触发间隔（单位：刻）。
    public static final int SWIM_PARTICLE_INTERVAL = 8;
    /// 触发游泳粒子和音效所需的最小速度平方。
    public static final double SWIM_MOVEMENT_THRESHOLD_SQR = 0.0025D;
    /// 游泳粒子生成高度相对玩家碰撞箱高度的比例。
    public static final float SWIM_PARTICLE_HEIGHT_RATIO = 0.5F;
    /// 游泳粒子水平速度相对玩家速度的倍率。
    public static final double SWIM_PARTICLE_HORIZONTAL_SPEED_MULTIPLIER = -0.25D;
    /// 游泳粒子的垂直速度（单位：格/刻）。
    public static final double SWIM_PARTICLE_VERTICAL_SPEED = 0.02D;

    // 水下悬浮粒子
    /// 每刻生成水下悬浮粒子的概率。
    public static final float SUSPENDED_PARTICLE_CHANCE = 0.7F;
    /// 水下悬浮粒子相对摄像机的水平生成范围（单位：格）。
    public static final float SUSPENDED_PARTICLE_HORIZONTAL_RANGE = 10.0F;
    /// 水下悬浮粒子相对摄像机的垂直生成范围（单位：格）。
    public static final float SUSPENDED_PARTICLE_VERTICAL_RANGE = 5.0F;
    /// 计算悬浮粒子海面高度时使用的插值进度。
    public static final float SUSPENDED_PARTICLE_SEA_HEIGHT_PARTIAL_TICK = 1.0F;
    /// 水下悬浮粒子的初速度（单位：格/刻）。
    public static final Vec3 SUSPENDED_PARTICLE_SPEED = Vec3.ZERO;
    /// 水下悬浮粒子的最短寿命（单位：刻）。
    public static final int SUSPENDED_PARTICLE_MIN_LIFETIME = 40;
    /// 水下悬浮粒子的最长寿命（单位：刻）。
    public static final int SUSPENDED_PARTICLE_MAX_LIFETIME = 79;
    /// 水下悬浮粒子的渲染尺寸。
    public static final float SUSPENDED_PARTICLE_SIZE = 0.04F;
    /// 水下悬浮粒子的红色通道。
    public static final float SUSPENDED_PARTICLE_RED = 0.5F;
    /// 水下悬浮粒子的绿色通道。
    public static final float SUSPENDED_PARTICLE_GREEN = 0.0625F;
    /// 水下悬浮粒子的蓝色通道。
    public static final float SUSPENDED_PARTICLE_BLUE = 0.75F;
    /// 水下悬浮粒子的透明度。
    public static final float SUSPENDED_PARTICLE_ALPHA = 1.0F;

    // 海面传送门粒子
    /// 每刻生成海面传送门粒子的概率。
    public static final float SEA_PORTAL_PARTICLE_CHANCE = 0.8F;
    /// 每次触发时生成的海面传送门粒子数量。
    public static final int SEA_PORTAL_PARTICLE_COUNT = 5;
    /// 海面传送门粒子相对摄像机的水平生成范围（单位：格）。
    public static final float SEA_PORTAL_PARTICLE_HORIZONTAL_RANGE = 12.0F;
    /// 摄像机可触发海面传送门粒子的最大垂直距离（单位：格）。
    public static final float SEA_PORTAL_PARTICLE_VISIBLE_VERTICAL_RANGE = 32.0F;
    /// 计算海面粒子高度时使用的潮位插值进度。
    public static final float SEA_PORTAL_PARTICLE_SEA_HEIGHT_PARTIAL_TICK = 1.0F;
    /// 传送门粒子相对海面的生成高度偏移（单位：格）。
    public static final double SEA_PORTAL_PARTICLE_HEIGHT_OFFSET = 0.05D;
    /// 传送门粒子的最大随机水平速度（单位：格/刻）。
    public static final float SEA_PORTAL_PARTICLE_HORIZONTAL_SPEED = 0.05F;
    /// 传送门粒子的垂直速度（单位：格/刻）。
    public static final float SEA_PORTAL_PARTICLE_VERTICAL_SPEED = 0.1F;
}
