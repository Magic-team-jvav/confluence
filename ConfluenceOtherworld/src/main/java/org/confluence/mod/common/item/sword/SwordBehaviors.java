package org.confluence.mod.common.item.sword;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.sword.BeeKeeperProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class SwordBehaviors {
    private static final String MOMENTUM_KEY = "confluence:sword_momentum";
    public static final SwordBehavior UMBRELLA = new SwordBehavior() {
        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            if (!level.isClientSide && isSelected && entity instanceof LivingEntity living && !living.swinging) {
                living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 2, false, false, false));
            }
        }
    };

    public static final SwordBehavior MOMENTUM = new SwordBehavior() {
        @Override
        public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
            if (level.isClientSide) return;
            LibUtils.updateItemStackNbt(stack, tag -> {
                float bonus = tag.getFloat(MOMENTUM_KEY);
                if (bonus > 0.0F && level.getGameTime() % 20 == 0)
                    tag.putFloat(MOMENTUM_KEY, Math.max(bonus - 0.3F, 0.0F));
            });
        }

        @Override
        public float modifyDamage(ItemStack stack, DamageSource source, @Nullable Entity attacker, LivingEntity victim, float amount) {
            CompoundTag tag = LibUtils.getItemStackNbtNoCopy(stack);
            return amount * (1.0F + tag.getFloat(MOMENTUM_KEY));
        }

        @Override
        public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            LibUtils.updateItemStackNbt(stack, tag -> {
                float bonus = tag.getFloat(MOMENTUM_KEY);
                if (bonus < 0.5F) tag.putFloat(MOMENTUM_KEY, Math.min(bonus + 0.12F, 0.5F));
            });
        }
    };

    public static final SwordBehavior PURPLE_CLUBBERFISH = effect(LibEffects.CONFUSED, 40, 1, 0.5F);
    public static final SwordBehavior BLOOD_BUTCHERER = effect(ModEffects.BLOOD_BUTCHERED, 180, 4, 1.0F);
    public static final SwordBehavior TENTACLE_MACE = effect(ModEffects.TENTACLE_SPIKES, 180, 4, 1.0F);
    public static final SwordBehavior BAT_BAT = new SwordBehavior() {
        @Override
        public void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity victim, DamageSource source) {
            attacker.heal(1.0F);
        }
    };
    public static final SwordBehavior VOLCANO = new SwordBehavior() {
        @Override
        public void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity victim, DamageSource source) {
            applyEffect(victim, ModEffects.HELLFIRE, 100, 0, 1.0F);
            victim.setRemainingFireTicks(100);
        }
    };
    public static final SwordBehavior BEE_KEEPER = new SwordBehavior() {
        @Override
        public void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity victim, DamageSource source) {
            for (int index = 0; index < 3; index++) {
                BeeKeeperProjectile projectile = ModEntities.BEE.get().create(attacker.level());
                if (projectile == null) continue;
                projectile.setOwner(attacker);
                projectile.setPos(victim.position().add(victim.getRandom().nextFloat() * 0.2F, victim.getEyeHeight() * 0.5F, victim.getRandom().nextFloat() * 0.2F));
                projectile.addAttackDamage(2.0F);
                attacker.level().addFreshEntity(projectile);
            }
            applyEffect(victim, LibEffects.CONFUSED, 40, 1, 1.0F);
        }
    };

    private static SwordBehavior effect(Supplier<? extends MobEffect> effect, int duration, int maxAmplifier, float chance) {
        return new SwordBehavior() {
            @Override
            public void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity victim, DamageSource source) {
                applyEffect(victim, effect, duration, maxAmplifier, chance);
            }
        };
    }

    private static void applyEffect(LivingEntity victim, Supplier<? extends MobEffect> effect, int duration, int maxAmplifier, float chance) {
        if (victim.getRandom().nextFloat() >= chance) return;
        MobEffect mobEffect = effect.get();
        MobEffectInstance current = victim.getEffect(mobEffect);
        int amplifier = current == null ? 0 : Math.min(current.getAmplifier() + 1, maxAmplifier);
        victim.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, false, true, false));
    }

    private SwordBehaviors() {}
}
