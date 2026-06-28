package org.confluence.mod.common.effect.harmful;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.monster.Harpy;
import org.confluence.terraentity.utils.DriveAwaySystem.DriveAwayExecutor;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

// TODO: 移植 Harpy / DriveAwaySystem 后移除 terraentity 依赖
public class DriveAwayEffect extends PortMobEffect {
    private final double baseSpeed;
    private final double baseTime;
    private final double baseRangeRandomMin;
    private final double baseRangeRandomMax;
    private final double baseOffsetMax;
    private final double baseCubeRange;

    public DriveAwayEffect(double baseSpeed, double baseTime,
                           double baseRangeRandomMin, double baseRangeRandomMax,
                           double baseOffsetMax, double baseCubeRange) {
        super(MobEffectCategory.HARMFUL, 0x5d478b);
        this.baseSpeed = baseSpeed;
        this.baseTime = baseTime;
        this.baseRangeRandomMin = baseRangeRandomMin;
        this.baseRangeRandomMax = baseRangeRandomMax;
        this.baseOffsetMax = baseOffsetMax;
        this.baseCubeRange = baseCubeRange;
    }

    public static final MapCodec<DriveAwayEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("base_speed").forGetter(DriveAwayEffect::getBaseSpeed),
            Codec.DOUBLE.fieldOf("base_time").forGetter(DriveAwayEffect::getBaseTime),
            Codec.DOUBLE.fieldOf("base_range_random_min").forGetter(DriveAwayEffect::getBaseRangeRandomMin),
            Codec.DOUBLE.fieldOf("base_range_random_max").forGetter(DriveAwayEffect::getBaseRangeRandomMax),
            Codec.DOUBLE.fieldOf("base_offset_max").forGetter(DriveAwayEffect::getBaseOffsetMax),
            Codec.DOUBLE.fieldOf("base_cube_range").forGetter(DriveAwayEffect::getBaseCubeRange)
    ).apply(instance, DriveAwayEffect::new));

    public double getBaseSpeed() { return baseSpeed; }
    public double getBaseTime() { return baseTime; }
    public double getBaseRangeRandomMin() { return baseRangeRandomMin; }
    public double getBaseRangeRandomMax() { return baseRangeRandomMax; }
    public double getBaseOffsetMax() { return baseOffsetMax; }
    public double getBaseCubeRange() { return baseCubeRange; }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Holder<MobEffect> effectHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this);
        if (entity.hasEffect(effectHolder)) {
            MobEffectInstance effectInstance = entity.getEffect(effectHolder);
            if (effectInstance != null) {
                applyDriveAway(entity, amplifier, effectInstance.getDuration(), null);
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0 && duration % 20 == 0;
    }

    private void applyDriveAway(LivingEntity entity, int amplifier, int duration,
                                @javax.annotation.Nullable Vec3 pos) {
        boolean isFlying = entity instanceof FlyingAnimal || entity instanceof Harpy;
        if (isFlying && entity instanceof Mob mob) {
            double factor = amplifier + 1;
            double speed = baseSpeed * factor;
            double time = duration;
            double rangeRandomMin = baseRangeRandomMin * factor;
            double rangeRandomMax = baseRangeRandomMax * factor;
            double offsetMax = baseOffsetMax * factor;

            Vec3 center;
            if (pos != null) {
                center = pos;
            } else {
                double angle = entity.level().random.nextDouble() * Math.PI * 2;
                center = entity.position().add(Math.cos(angle), 0, Math.sin(angle));
            }

            Vec3 direction = entity.position().subtract(center).normalize();
            double randomY = (Math.random() * 4) - 2;
            direction = new Vec3(direction.x, randomY, direction.z).normalize();

            DriveAwayExecutor.applyToSingleEntity(
                    mob, center, direction,
                    speed, time,
                    rangeRandomMin, rangeRandomMax,
                    offsetMax);
        }
    }
}
