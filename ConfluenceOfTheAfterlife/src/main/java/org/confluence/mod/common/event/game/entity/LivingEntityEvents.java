package org.confluence.mod.common.event.game.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.effect.beneficial.ArcheryEffect;
import org.confluence.mod.common.effect.beneficial.LuckEffect;
import org.confluence.mod.common.effect.beneficial.ThornsEffect;
import org.confluence.mod.common.effect.harmful.ManaSicknessEffect;
import org.confluence.mod.common.effect.neutral.LoveEffect;
import org.confluence.mod.common.init.ModAttachments;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.common.init.TCTags;
import org.confluence.terra_curio.util.TCUtils;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class LivingEntityEvents {
    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        if (event.getSource().getEntity() instanceof ServerPlayer) {
            if (CommonConfigs.DROP_MONEY.get() && living instanceof Enemy) {
                Level level = living.level();
                AttributeInstance attack = living.getAttribute(Attributes.ATTACK_DAMAGE);
                AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
                double healthFactor = living.getMaxHealth() * 0.05;
                double attackFactor = attack == null ? 0.0 : attack.getValue() * 0.25;
                double armorFactor = armor == null ? 0.45 : (armor.getValue() + 1.0) * 0.45;
                double difficultyFactor = switch (level.getDifficulty()) {
                    case PEACEFUL -> 0.5;
                    case EASY -> 0.75;
                    case NORMAL -> 1.0;
                    case HARD -> 1.5;
                };
                int amount = (int) Math.min(Math.round((healthFactor + attackFactor + armorFactor) * difficultyFactor), 7290L);
                ModUtils.dropMoney(amount, living.getX(), living.getEyeY() - 0.3, living.getZ(), level);
            }
        }
    }

    @SubscribeEvent
    public static void livingHeal(LivingHealEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide) return;
        if (living.hasEffect(ModEffects.FROST_BURN)) {
            event.setCanceled(true); // todo 免疫
        } else if (living.getData(ModAttachments.EVER_BENEFICIAL).isVitalCrystalUsed()) {
            event.setAmount(event.getAmount() * 1.2F);
        }
    }

    @SubscribeEvent
    public static void livingBreathe(LivingBreatheEvent event) {

    }

    @SubscribeEvent
    public static void livingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            DamageSource damageSource = event.getSource();
            float amount = event.getAmount();
            if (TCUtils.hasAccessoriesType(serverPlayer, AccessoryItems.HURT$GET$MANA)) {
                if (!damageSource.is(DamageTypes.DROWN) && !damageSource.is(TCTags.HARMFUL_EFFECT)) {
                    PlayerUtils.receiveMana(serverPlayer, () -> (int) amount);
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingDamage$Pre(LivingDamageEvent.Pre event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide) return;
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) return;

        float amount = event.getNewDamage();

        ThornsEffect.apply(living, damageSource.getEntity(), amount);

        amount = ArcheryEffect.apply(living, damageSource, amount);
        amount = ManaSicknessEffect.apply(damageSource, amount);
        //amount = BreathingReed.consumer(living, damageSource, amount);

        event.setNewDamage(amount);
    }

    @SubscribeEvent
    public static void livingDamage$Post(LivingDamageEvent.Post event) {
        DamageSource damageSource = event.getSource();
        LivingEntity damageEntity = event.getEntity();
        if (damageSource.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity.getItemInHand(event.getEntity().getUsedItemHand()).getItem() instanceof BaseSwordItem sword) {
                if (sword.modifier != null) {
                    sword.modifier.onHitEffects.forEach(effect -> effect.accept(livingEntity, damageEntity));
                }
            }
        }
    }

    @SubscribeEvent
    public static void mobEffect$Remove(MobEffectEvent.Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            LuckEffect.onRemove(event.getEntity(), effectInstance.getEffect(), effectInstance.getAmplifier());
        }
    }

    @SubscribeEvent
    public static void mobEffect$Added(MobEffectEvent.Added event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            LoveEffect.onAdd(effectInstance.getEffect(), event.getEntity(), event.getEffectSource());
        }
    }

    @SubscribeEvent
    public static void livingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.MAINHAND) return;
        LivingEntity living = event.getEntity();
        if (!living.level().isClientSide) {
            ItemStack to = event.getTo();
            if (PrefixUtils.canInit(to)) {
                PrefixUtils.initPrefix(living.getRandom(), to);
            }
        }
    }
}
