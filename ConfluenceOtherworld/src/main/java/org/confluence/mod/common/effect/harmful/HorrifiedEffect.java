package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionDefaults;
import net.minecraftforge.registries.DeferredHolder;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshMouth;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshPart;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// TODO: 移植 WallOfFlesh 后移除 terraentity 依赖
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
            if (!living.getBoundingBox().intersects(wall.getOutsideBox())
                    && !living.getBoundingBox().intersects(wall.getInsideBox())
                    || living.level().dimension() != wall.level().dimension()) {
                living.kill();
            } else if (living.getBoundingBox().intersects(wall.getOutsideBox())
                    && !living.getBoundingBox().intersects(wall.getInsideBox())) {
                List<WallOfFleshMouth> nearestMouths = new ArrayList<>();
                for (int i = 0; i < wall.subEntities.size(); i++) {
                    WallOfFleshPart segment = wall.subEntities.get(i);
                    if (segment instanceof WallOfFleshMouth mouth
                            && segment.getY() > segment.level().getMinBuildHeight()) {
                        nearestMouths.add(mouth);
                    }
                }

                List<WallOfFleshMouth> filteredAndSorted = nearestMouths.stream()
                        .filter(mouth -> (living.level().dimension() != Level.NETHER
                                || (living.level().dimension() == Level.NETHER
                                && mouth.getY() < DimensionDefaults.NETHER_GENERATION_HEIGHT * 2.0 / 3.0)))
                        .sorted(Comparator.comparingDouble(mouth -> mouth.distanceToSqr(living)))
                        .limit(20)
                        .toList();

                WallOfFleshMouth targetMouth = null;
                for (WallOfFleshMouth mouth : filteredAndSorted) {
                    if (!mouth.isInWall()) {
                        targetMouth = mouth;
                        break;
                    }
                }

                if (targetMouth == null && !filteredAndSorted.isEmpty()) {
                    targetMouth = filteredAndSorted.getFirst();
                }

                if (targetMouth != null) {
                    DeferredHolder<MobEffect, TheTongueEffect> horrifiedHolder =
                            ModEffects.THE_TONGUE;
                    horrifiedHolder.get().setWallOfFleshMouth(targetMouth);
                    if (!living.hasEffect(ModEffects.THE_TONGUE))
                        living.addEffect(new MobEffectInstance(horrifiedHolder, 60));
                }
            }
        } else {
            living.getActiveEffectsMap().remove(ModEffects.HORRIFIED).getEffect();
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
