package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

public class HorrifiedEffect extends PortMobEffect {
    private WallOfFlesh wall;

    public HorrifiedEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        if (wall != null && wall.isAlive()
                && (!(living instanceof Player)
                || (living instanceof Player player && !player.isCreative() && !player.isSpectator()))) {
            if (!living.getBoundingBox().intersects(wall.getBoundingBox().inflate(48))
                    || living.level().dimension() != wall.level().dimension()) {
                living.kill();
            } else {
                TheTongueEffect tongue = ModEffects.THE_TONGUE.get();
                tongue.setWall(wall);
                if (!living.hasEffect(tongue)) {
                    living.addEffect(new MobEffectInstance(tongue, 60));
                }
            }
        } else {
            living.removeEffect(ModEffects.HORRIFIED.get());
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    public WallOfFlesh getWallOfFlesh() {
        return wall;
    }

    public void setWallOfFlesh(WallOfFlesh wall) {
        this.wall = wall;
    }
}
