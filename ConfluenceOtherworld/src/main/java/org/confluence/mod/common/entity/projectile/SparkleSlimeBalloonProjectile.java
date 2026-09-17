package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.entity.npc.TownSlimeNPC;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.mixed.IEntity;

public class SparkleSlimeBalloonProjectile extends ThrowableItemProjectile {
    public SparkleSlimeBalloonProjectile(EntityType<? extends SparkleSlimeBalloonProjectile> type, Level level) {
        super(type, level);
    }

    public SparkleSlimeBalloonProjectile(LivingEntity owner) {
        super(ModEntities.SPARKLE_SLIME_BALLOON.get(), owner, owner.level());
    }

    @Override
    public void tick() {
        super.tick();
        if (!isRemoved() && level() instanceof ServerLevel server && IEntity.of(this).confluence$isInShimmer()
                && TownSlimeNPC.unlock(server, NpcEntities.DIVA_SLIME.get(), position()) != null)
            discard();
        if (tickCount > 1200) discard();
    }

    @Override
    protected Item getDefaultItem() {
        return ConsumableItems.SPARKLE_SLIME_BALLOON.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ProjectileAge", tickCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        tickCount = Math.max(0, tag.getInt("ProjectileAge"));
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        if (level().isClientSide) return;
        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(9))) {
            if (living.distanceToSqr(this) <= 81)
                living.addEffect(new MobEffectInstance(ModEffects.SPARKLE_SLIME.get(), 600));
        }
        level().levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, blockPosition(), 0xAF52D6);
        discard();
    }
}
