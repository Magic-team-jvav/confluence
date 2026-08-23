package org.confluence.mod.common.effect.harmful;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.Harpy;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

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

    public DriveAwayEffect(double baseSpeed, double baseTime, double baseRangeRandomMin, double baseRangeRandomMax, double baseOffsetMax, double baseCubeRange) {
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
        if (!(living instanceof FlyingAnimal || living instanceof Harpy) || !(living instanceof Mob mob))
            return;
        MobEffectInstance instance = living.getEffect(this);
        if (instance == null) return;
        double angle = living.getRandom().nextDouble() * Math.PI * 2.0;
        Vec3 center = living.position().add(Math.cos(angle), 0.0, Math.sin(angle));
        DriveAwayController.start(mob, center, baseSpeed * (amplifier + 1.0), instance.getDuration());
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0 && duration % 20 == 0;
    }
}
