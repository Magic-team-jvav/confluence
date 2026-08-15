package org.confluence.mod.common.effect.harmful;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.Harpy;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

/**
 * 飞行生物被驱离后的短时失控效果。
 *
 * <p>箭矢负责写入最初的逃离方向，本效果在持续期间停止原有导航与攻击目标，并防止
 * 飞行 AI 立刻覆盖逃离速度。非飞行生物即使通过命令获得效果也不会被改写行为。</p>
 */
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
        applyDriveAway(living, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

    private void applyDriveAway(LivingEntity entity, int amplifier) {
        if (!(entity instanceof FlyingAnimal
                || entity instanceof FlyingMob
                || entity instanceof Harpy)
                || !(entity instanceof Mob mob)) {
            return;
        }
        mob.setTarget(null);
        mob.getNavigation().stop();

        double minimumSpeed = baseSpeed * (amplifier + 1.0);
        Vec3 movement = entity.getDeltaMovement();
        if (movement.lengthSqr() < 1.0E-6) {
            double angle = entity.getRandom().nextDouble() * Math.PI * 2.0;
            movement = new Vec3(
                    Math.cos(angle), 0.15, Math.sin(angle));
        }
        if (movement.length() < minimumSpeed) {
            entity.setDeltaMovement(
                    movement.normalize().scale(minimumSpeed));
            entity.hasImpulse = true;
        }
    }
}
