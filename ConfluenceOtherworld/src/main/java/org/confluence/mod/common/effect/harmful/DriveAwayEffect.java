package org.confluence.mod.common.effect.harmful;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.Harpy;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import java.util.Objects;

// TODO: 移植 Harpy / DriveAwaySystem 后移除 terraentity 依赖
public class DriveAwayEffect extends PortMobEffect {
    public static final MapCodec<DriveAwayEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("base_speed").forGetter(effect -> effect.baseSpeed),
            Codec.DOUBLE.fieldOf("base_time").forGetter(effect -> effect.baseTime),
            Codec.DOUBLE.fieldOf("base_range_random_min").forGetter(effect -> effect.baseRangeRandomMin),
            Codec.DOUBLE.fieldOf("base_range_random_max").forGetter(effect -> effect.baseRangeRandomMax),
            Codec.DOUBLE.fieldOf("base_offset_max").forGetter(effect -> effect.baseOffsetMax),
            Codec.DOUBLE.fieldOf("base_cube_range").forGetter(effect -> effect.baseCubeRange)
    ).apply(instance, DriveAwayEffect::new));

    private final double baseSpeed;
    private final double baseTime;
    private final double baseRangeRandomMin;
    private final double baseRangeRandomMax;
    private final double baseOffsetMax;
    private final double baseCubeRange;

    public DriveAwayEffect(
            double baseSpeed, double baseTime,
            double baseRangeRandomMin, double baseRangeRandomMax,
            double baseOffsetMax, double baseCubeRange
    ) {
        super(MobEffectCategory.HARMFUL, 0x5d478b);
        this.baseSpeed = baseSpeed;
        this.baseTime = baseTime;
        this.baseRangeRandomMin = baseRangeRandomMin;
        this.baseRangeRandomMax = baseRangeRandomMax;
        this.baseOffsetMax = baseOffsetMax;
        this.baseCubeRange = baseCubeRange;
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        applyDriveAway(living, amplifier, Objects.requireNonNull(living.getEffect(this)).duration);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0 && duration % 20 == 0;
    }

    private void applyDriveAway(LivingEntity entity, int amplifier, int duration) {
        boolean isFlying = entity instanceof FlyingAnimal || entity instanceof Harpy;
        if (isFlying && entity instanceof Mob mob) {
            double factor = amplifier + 1;
            double speed = baseSpeed * factor;
            double time = duration;
            double rangeRandomMin = baseRangeRandomMin * factor;
            double rangeRandomMax = baseRangeRandomMax * factor;
            double offsetMax = baseOffsetMax * factor;

            Vec3 center;
            double angle = entity.level().random.nextDouble() * Math.PI * 2;
            center = entity.position().add(Math.cos(angle), 0, Math.sin(angle));

            Vec3 direction = entity.position().subtract(center).normalize();
            double randomY = (Math.random() * 4) - 2;
            direction = new Vec3(direction.x, randomY, direction.z).normalize();

// todo effect           DriveAwayExecutor.applyToSingleEntity(
//                    mob, center, direction,
//                    speed, time,
//                    rangeRandomMin, rangeRandomMax,
//                    offsetMax);
        }
    }
}
