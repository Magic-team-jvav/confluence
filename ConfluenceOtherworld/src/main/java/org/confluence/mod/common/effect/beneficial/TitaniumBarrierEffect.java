package org.confluence.mod.common.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.entity.projectile.TitaniumShardsProjectile;

public class TitaniumBarrierEffect extends MobEffect {
    public TitaniumBarrierEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x777777);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof Player player) {
            long lastTime = livingEntity.getPersistentData().getLong("confluence:titanium_barrier");
            long gameTime = livingEntity.level().getGameTime();
            if (lastTime <= 0) {
                livingEntity.level().addFreshEntity(new TitaniumShardsProjectile(player));
                livingEntity.getPersistentData().putLong("confluence:titanium_barrier", gameTime);
            }
            return gameTime - lastTime <= 200;
        }
        return false;
    }

    @Override
    public void onMobRemoved(LivingEntity livingEntity, int amplifier, Entity.RemovalReason reason) {
        livingEntity.getPersistentData().putLong("confluence:titanium_barrier", 0);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
