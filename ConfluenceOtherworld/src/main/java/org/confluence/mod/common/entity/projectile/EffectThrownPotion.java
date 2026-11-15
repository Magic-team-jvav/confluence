package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.HitResult;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.item.potion.EffectThrowablePotionItem;

public class EffectThrownPotion extends ThrowableItemProjectile {
    private EffectThrowablePotionItem item;

    public EffectThrownPotion(EntityType<EffectThrownPotion> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EffectThrownPotion(double pX, double pY, double pZ, Level pLevel) {
        super(ModEntities.EFFECT_THROWN_POTION.get(), pX, pY, pZ, pLevel);
    }

    public EffectThrownPotion(LivingEntity pShooter, Level pLevel) {
        super(ModEntities.EFFECT_THROWN_POTION.get(), pShooter, pLevel);
    }

    @Override
    public void setItem(ItemStack pStack) {
        super.setItem(pStack);
        if (pStack.getItem() instanceof EffectThrowablePotionItem effectThrowablePotionItem) {
            this.item = effectThrowablePotionItem;
        }
    }

    @Override
    protected EffectThrowablePotionItem getDefaultItem() {
        return (EffectThrowablePotionItem) PotionItems.LOVE_POTION.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) return;
        if (item == null) item = getDefaultItem();
        Entity entity = getEffectSource();
        level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(4.0D, 2.0D, 4.0D)).forEach(living -> {
            if (living.isAffectedByPotions() && distanceToSqr(living) < 16.0) {
                living.addEffect(item.data.create(), entity);
            }
        });
        level().levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, this.blockPosition(), item.data.effect().value().getColor());
        discard();
    }
}
