package org.confluence.terraentity.entity.monster.slime;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.confluence.lib.color.FloatRGB;
import org.confluence.terraentity.entity.util.DeathAnimOptions;
import org.confluence.terraentity.init.TEParticles;
import org.confluence.terraentity.mixin.accessor.SlimeAccessor;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlackSlime extends Slime implements DeathAnimOptions {
    private final FloatRGB color;

    public BlackSlime(EntityType<BlackSlime> slime, Level level) {
        super(slime, level);
        this.color = FloatRGB.fromInteger(0x373535);
    }

    @Override
    public void tick() {
        resetFallDistance();
        if (onGround() && !((SlimeAccessor) this).isWasOnGround()) {
            int i = getSize();
            for (int j = 0; j < i * 8; ++j) {
                float f = random.nextFloat() * Mth.TWO_PI;
                float f1 = random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
                float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
                level().addParticle(TEParticles.ITEM_GEL.get(), getX() + (double) f2, getY(), getZ() + (double) f3, color.red(), color.green(), color.blue());
            }
        }
        super.tick();
    }

    @Override
    protected boolean spawnCustomParticles() {
        return true;
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        if (getSize() == 2) {
            brain.clearMemories();
            setRemoved(removalReason);
//            invalidateCaps();
        } else {
            super.remove(removalReason);
        }
    }

    @Override
    public void setSize(int pSize, boolean setByVanilla) {
        if (setByVanilla) return; // 原版为true
        int i = Mth.clamp(pSize, 1, 127);
        entityData.set(ID_SIZE, i);
        reapplyPosition();
        refreshDimensions();
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2F + 0.1F * i);

        this.xpReward = i;
    }

    public void finalizeSpawn(RandomSource randomSource, DifficultyInstance difficulty) {
        int size = 2;
        float specialMultiplier = difficulty.getSpecialMultiplier();

        float probability;
        if (specialMultiplier <= 0.5F) {
            probability = 0.15F;
        } else if (specialMultiplier <= 1.0F) {
            probability = 0.60F;
        } else {
            probability = 0.85F;
        }

        if (randomSource.nextFloat() < probability) {
            size = 4;
        }

        setSize(size, false);
        AttributeInstance attackDamage = getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance maxHealth = getAttribute(Attributes.MAX_HEALTH);
        assert attackDamage != null && maxHealth != null;
        if (size == 2) {
            attackDamage.setBaseValue(6.0F);
            Objects.requireNonNull(getAttribute(Attributes.ARMOR)).setBaseValue(4);
            maxHealth.setBaseValue(25.0F);
        } else {
            attackDamage.setBaseValue(10.0F);
            Objects.requireNonNull(getAttribute(Attributes.ARMOR)).setBaseValue(7);
            maxHealth.setBaseValue(58.0F);
        }
        setHealth(getMaxHealth());
    }

    @Override
    public float[] getBloodColor(){
        return color.toArray();
    }

    @Override
    protected void dealDamage(@NotNull LivingEntity pLivingEntity) {
        if (isAlive()) {
            int i = getSize();
            if (distanceToSqr(pLivingEntity) < 0.6 * (double)i * 0.6 * (double)i && hasLineOfSight(pLivingEntity) && pLivingEntity.hurt(damageSources().mobAttack(this), getAttackDamage())) {
                playSound(SoundEvents.SLIME_ATTACK, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                DamageSource damagesource = this.damageSources().mobAttack(this);
                if (this.level() instanceof ServerLevel serverlevel)
                    EnchantmentHelper.doPostAttackEffects(serverlevel, pLivingEntity, damagesource);
                if (TEUtils.isMaster(level(), blockPosition()) || (TEUtils.isAtLeastExpert(level(), blockPosition()) && level().random.nextBoolean())) {
                    pLivingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300, 0), this);
                }
            }
        }
    }
    @Override
    public @NotNull Component getName() {
        if (!hasCustomName()){
            int size = getSize();
            if (size == 1) return Component.translatable("entity.terra_entity.baby_slime");
            if (size == 4) return Component.translatable("entity.terra_entity.mother_slime");
        }
        return super.getName();
    }
}
