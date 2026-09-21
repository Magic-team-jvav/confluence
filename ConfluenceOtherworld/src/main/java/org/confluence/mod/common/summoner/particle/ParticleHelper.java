package org.confluence.mod.common.summoner.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;

import java.util.Random;
import java.util.function.Consumer;

/**
 * 粒子生成辅助类，支持链式配置粒子参数。
 * <p>
 * count 大于 0 时发射多个随机散射粒子，count 小于等于 0 时只发射一个使用精确速度的粒子。
 * 服务端粒子会先累积到 {@link SummonerParticleData}，由 Level tick 末统一打包下发。
 * </p>
 */
public class ParticleHelper {

    private final Level level;
    private final Random random = new Random();
    private ParticleOptions particleType;
    private GenericParticleBuilder genericBuilder;
    private double x;
    private double y;
    private double z;
    private double vx;
    private double vy;
    private double vz;
    private int count = 1;
    private double speed = 1.0;
    private double spreadAngle = 0.4;
    private double offsetX;
    private double offsetY;
    private double offsetZ;

    public ParticleHelper(Level level) {
        this.level = level;
    }

    public static ParticleHelper create(Level level) {
        return new ParticleHelper(level);
    }

    /** 设置普通粒子类型。 */
    public ParticleHelper type(ParticleOptions type) {
        this.particleType = type;
        return this;
    }

    /** 使用通用粒子，并通过 Consumer 配置参数。 */
    public ParticleHelper generic(Consumer<GenericParticleBuilder> configurator) {
        GenericParticleBuilder builder = GenericParticleBuilder.create();
        configurator.accept(builder);
        this.genericBuilder = builder;
        return this;
    }

    /** 使用通用粒子，并通过已有 Builder 配置参数。 */
    public ParticleHelper generic(GenericParticleBuilder builder) {
        this.genericBuilder = builder;
        return this;
    }

    /** 设置粒子中心位置。 */
    public ParticleHelper pos(Vec3 position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        return this;
    }

    /** 设置粒子中心位置。 */
    public ParticleHelper pos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /** 设置基础速度方向。 */
    public ParticleHelper velocity(Vec3 velocity) {
        this.vx = velocity.x;
        this.vy = velocity.y;
        this.vz = velocity.z;
        return this;
    }

    /** 设置基础速度方向。 */
    public ParticleHelper velocity(double vx, double vy, double vz) {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        return this;
    }

    /** 设置粒子数量。 */
    public ParticleHelper count(int count) {
        this.count = count;
        return this;
    }

    /** 设置多粒子模式下的速度倍率。 */
    public ParticleHelper speed(double speed) {
        this.speed = speed;
        return this;
    }

    /** 设置多粒子模式下的最大散射角。 */
    public ParticleHelper spread(double spreadAngle) {
        this.spreadAngle = spreadAngle;
        return this;
    }

    /** 设置多粒子模式下的位置随机偏移。 */
    public ParticleHelper offset(double x, double y, double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        return this;
    }

    /** 设置三轴相同的位置随机偏移。 */
    public ParticleHelper offset(double radius) {
        this.offsetX = radius;
        this.offsetY = radius;
        this.offsetZ = radius;
        return this;
    }

    /** 发射粒子，根据 count 自动选择单粒子或多粒子模式。 */
    public void emit() {
        if (particleType == null && genericBuilder == null) {
            throw new IllegalStateException("Particle type not set. Call type() or generic() first.");
        }
        boolean server = !level.isClientSide();
        SummonerParticleData batch = server ? level.getData(SummonerAttachmentTypes.BATCHED_PARTICLES) : null;
        if (count <= 0) {
            ParticleOptions options = genericBuilder != null ? genericBuilder.build() : particleType;
            if (server) {
                batch.add(options, x, y, z, vx, vy, vz);
            } else {
                level.addParticle(options, x, y, z, vx, vy, vz);
            }
        } else {
            Vec3 baseDirection = new Vec3(vx, vy, vz).normalize();
            for (int i = 0; i < count; i++) {
                double theta = (random.nextDouble() - 0.5) * spreadAngle * 2;
                double phi = (random.nextDouble() - 0.5) * spreadAngle * 2;
                double speedVariation = speed * (0.5 + random.nextDouble() * 0.5);
                Vec3 velocity = baseDirection.yRot((float) theta).xRot((float) phi).scale(speedVariation);
                double px = x + (offsetX > 0 ? (random.nextDouble() - 0.5) * 2 * offsetX : 0);
                double py = y + (offsetY > 0 ? (random.nextDouble() - 0.5) * 2 * offsetY : 0);
                double pz = z + (offsetZ > 0 ? (random.nextDouble() - 0.5) * 2 * offsetZ : 0);
                ParticleOptions options = genericBuilder != null ? genericBuilder.build() : particleType;
                if (server) {
                    batch.add(options, px, py, pz, velocity.x, velocity.y, velocity.z);
                } else {
                    level.addParticle(options, px, py, pz, velocity.x, velocity.y, velocity.z);
                }
            }
        }
    }
}
