package org.confluence.mod.common.summoner.particle;

import java.util.Random;

/**
 * 通用粒子配置构建器，支持链式配置颜色、寿命、旋转、阻力和大小。
 */
public class GenericParticleBuilder {

    private final Random random = new Random();
    private int centerColor = 0xFFFFFF;
    private int edgeColor = 0xFFFFFF;
    private int lifetime = 1;
    private int lifetimeJitter;
    private float spinSpeed;
    private float spinJitter;
    private float friction = 1.0F;
    private float scale = 1.0F;
    private float scaleJitter;

    private GenericParticleBuilder() {
    }

    public static GenericParticleBuilder create() {
        return new GenericParticleBuilder();
    }

    /** 设置中心色块 RGB 颜色。 */
    public GenericParticleBuilder centerColor(int rgb) {
        this.centerColor = rgb;
        return this;
    }

    /** 设置边缘色块 RGB 颜色。 */
    public GenericParticleBuilder edgeColor(int rgb) {
        this.edgeColor = rgb;
        return this;
    }

    /** 设置粒子寿命，单位为 tick。 */
    public GenericParticleBuilder lifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    /** 设置寿命随机抖动上限，范围为 0 到该值减一。 */
    public GenericParticleBuilder lifetimeRandom(int range) {
        this.lifetimeJitter = range;
        return this;
    }

    /** 设置旋转速度，单位为弧度每 tick。 */
    public GenericParticleBuilder spin(float spinSpeed) {
        this.spinSpeed = spinSpeed;
        return this;
    }

    /** 设置旋转速度随机抖动范围。 */
    public GenericParticleBuilder spinRandom(float range) {
        this.spinJitter = range;
        return this;
    }

    /** 设置每 tick 速度阻力乘数。 */
    public GenericParticleBuilder friction(float friction) {
        this.friction = friction;
        return this;
    }

    /** 设置粒子大小。 */
    public GenericParticleBuilder scale(float scale) {
        this.scale = scale;
        return this;
    }

    /** 设置大小随机增量上限。 */
    public GenericParticleBuilder scaleRandom(float range) {
        this.scaleJitter = range;
        return this;
    }

    /** 构建一次性的粒子参数。 */
    public GenericParticleOptions build() {
        return new GenericParticleOptions(
                centerColor,
                edgeColor,
                lifetime + (lifetimeJitter > 0 ? random.nextInt(lifetimeJitter) : 0),
                spinSpeed + (spinJitter > 0 ? random.nextFloat() * spinJitter * 2 - spinJitter : 0),
                friction,
                scale + (scaleJitter > 0 ? random.nextFloat() * scaleJitter : 0)
        );
    }
}
