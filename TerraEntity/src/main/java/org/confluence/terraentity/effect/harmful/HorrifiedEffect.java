package org.confluence.terraentity.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionDefaults;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshMouth;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshPart;
import org.confluence.terraentity.init.TEEffects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HorrifiedEffect extends MobEffect {
    private WallOfFlesh wall;
    public HorrifiedEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB1122);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (wall != null && wall.isAlive() && (!(living instanceof Player) || (living instanceof Player player && !player.isCreative() && !player.isSpectator()))){
            if(!living.getBoundingBox().intersects(wall.getOutsideBox())&&!living.getBoundingBox().intersects(wall.getInsideBox())||living.level().dimension()!=(wall.level().dimension())){
                living.kill();
            }else if(living.getBoundingBox().intersects(wall.getOutsideBox())&&!living.getBoundingBox().intersects(wall.getInsideBox())){
                List<WallOfFleshMouth> nearestMouths = new ArrayList<>();
                for (int i = 0; i < wall.subEntities.size(); i++) {
                    WallOfFleshPart segment = wall.subEntities.get(i);
                    if (segment instanceof WallOfFleshMouth mouth && segment.getY() > segment.level().getMinBuildHeight()) {
                        nearestMouths.add(mouth);
                    }
                }

                List<WallOfFleshMouth> filteredAndSorted = nearestMouths.stream()
                        .filter(mouth -> (living.level().dimension() != Level.NETHER
                                || (living.level().dimension() == Level.NETHER &&  mouth.getY()<DimensionDefaults.NETHER_GENERATION_HEIGHT * 2.0 / 3.0)))
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

                // 如果没有找到合适的嘴，返回第一个嘴
                if (targetMouth == null && !filteredAndSorted.isEmpty()) {
                    targetMouth = filteredAndSorted.getFirst();
                }

                if (targetMouth != null) {
                    DeferredHolder<MobEffect, TheTongueEffect> horrifiedHolder = TEEffects.THE_TONGUE;
                    horrifiedHolder.get().setWallOfFleshMouth(targetMouth);
                    if(!living.hasEffect(TEEffects.THE_TONGUE))
                        living.addEffect(new MobEffectInstance(horrifiedHolder, 60));
                }
            }
        }else living.getActiveEffectsMap().remove(TEEffects.HORRIFIED).getEffect();
        return true;
    }

    public WallOfFlesh getWallOfFlesh() {
        return this.wall;
    }

    public void setWallOfFlesh(WallOfFlesh wall) {
        this.wall = wall;
    }
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
