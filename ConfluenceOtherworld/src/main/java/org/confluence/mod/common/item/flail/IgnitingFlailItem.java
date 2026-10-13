package org.confluence.mod.common.item.flail;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.jetbrains.annotations.Nullable;

/// 命中时按固定概率点燃目标的链锤。
public class IgnitingFlailItem extends BaseFlailItem {
    private static final int FIRE_TICKS = 60;
    private final float igniteChance;
    private final boolean extinguishesInWater;

    public IgnitingFlailItem(Parameters parameters, ModRarity rarity, float igniteChance) {
        this(parameters, rarity, igniteChance, false);
    }

    public IgnitingFlailItem(Parameters parameters, ModRarity rarity, float igniteChance, boolean extinguishesInWater) {
        super(parameters, rarity);
        if (igniteChance < 0.0F || igniteChance > 1.0F) {
            throw new IllegalArgumentException("Flail ignite chance must be between zero and one");
        }
        this.igniteChance = igniteChance;
        this.extinguishesInWater = extinguishesInWater;
    }

    @Override
    public void onFlailTick(Player owner, BaseFlailEntity flail) {
        if (!extinguishesInWater || !flail.isHitEffectEnabled()) return;
        AABB bounds = flail.getBoundingBox().deflate(0.001);
        boolean touchesWater = BlockPos.betweenClosedStream(bounds).anyMatch(pos -> {
            var fluid = flail.level().getFluidState(pos);
            return fluid.is(FluidTags.WATER) && pos.getY() + fluid.getHeight(flail.level(), pos) > bounds.minY;
        });
        if (touchesWater) {
            flail.setHitEffectEnabled(false);
            flail.playSound(SoundEvents.FIRE_EXTINGUISH, 0.5F, 1.0F);
        }
    }

    @Override
    public @Nullable ResourceLocation getFlailTexture(BaseFlailEntity flail) {
        return extinguishesInWater && !flail.isHitEffectEnabled()
                ? Parameters.MACE.get().ballTexture() : super.getFlailTexture(flail);
    }

    @Override
    public void onFlailHit(Player owner, LivingEntity target, BaseFlailEntity flail) {
        if (target.getRandom().nextFloat() < igniteChance) {
            target.setRemainingFireTicks(FIRE_TICKS);
        }
    }
}
