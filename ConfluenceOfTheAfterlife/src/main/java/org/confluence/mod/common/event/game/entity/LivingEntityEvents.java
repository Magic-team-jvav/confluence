package org.confluence.mod.common.event.game.entity;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.effect.beneficial.ArcheryEffect;
import org.confluence.mod.common.effect.beneficial.LuckEffect;
import org.confluence.mod.common.effect.beneficial.ThornsEffect;
import org.confluence.mod.common.effect.harmful.ManaSicknessEffect;
import org.confluence.mod.common.effect.neutral.LoveEffect;
import org.confluence.mod.common.init.ModAchievements;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.particle.DamageIndicatorOptions;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.mixed.IDamageSource;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.network.s2c.DeathMotionPacketS2C;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.entity.ai.Boss;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TETags;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class LivingEntityEvents {
    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            ServerLevel level = serverPlayer.serverLevel();
            if (living instanceof Enemy && CommonConfigs.DROP_MONEY.get() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                AttributeInstance attack = living.getAttribute(Attributes.ATTACK_DAMAGE);
                AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
                AttributeInstance knockbackResistance = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                double healthFactor = living.getMaxHealth() * 0.15;
                double attackFactor = attack == null ? 0.0 : attack.getValue() * 0.25;
                double armorFactor = armor == null ? 0.0 : armor.getValue() * 0.1;
                double knockbackResistanceFactor = knockbackResistance == null ? 10.0 : (1.0 + knockbackResistance.getValue()) * 10.0;
                double difficultyFactor = level.getCurrentDifficultyAt(living.blockPosition()).getEffectiveDifficulty() * 0.5;
                int amount = (int) Math.min(Math.round((healthFactor + attackFactor + armorFactor + knockbackResistanceFactor) * difficultyFactor) * 7.0, 99 * 99 * 10);
                ModUtils.dropMoney(amount, living.getX(), living.getEyeY() - 0.3, living.getZ(), level);
            }
            if (living instanceof Boss boss && boss.shouldShowMessage()) {
                EntityType<?> type = living.getType();
                ConfluenceData data = ConfluenceData.get(level);
                data.getKillBoard().defeated(type, data);
                boolean isEaterOfWorlds = type == TEEntities.EATER_OF_WORLDS.get();
                if (isEaterOfWorlds || type == TEEntities.BRAIN_OF_CTHULHU.get()) {
                    if (ModUtils.isWithinDayTime(0, 0, 4, 30, level.getDayTime())) { // 00:00 -> 04:30
                        MeteoriteTracker.INSTANCE.spawnAtNextNight = true;
                    } else if (!MeteoriteTracker.INSTANCE.spawnAtNextNight) {
                        MeteoriteTracker.INSTANCE.spawnAtNextNight = level.random.nextBoolean();
                    }
                }
                ResourceKey<Level> dimension = living.level().dimension();
                level.players().stream()
                        .filter(player -> player.level().dimension() == dimension)
                        .forEach(player -> {
                            TreasureBagItem.createItemEntity(living, player);
                            if (isEaterOfWorlds) {
                                PlayerUtils.awardAchievement(player, "worm_fodder");
                            }
                        });
            }
        }

        if (!living.level().isClientSide) {
            if (living instanceof ServerPlayer serverPlayer) {
                PlayerUtils.dropMoney(serverPlayer);
            }
            DeathMotionPacketS2C.sendToAll(living);
            NoTraps.entityDropsGrenade(living);
            if (living.getRandom().nextFloat() < 0.011F) {
                Item holidayGift = DateUtils.getHolidayGift();
                if (holidayGift != Items.AIR) {
                    ModUtils.createItemEntity(holidayGift.getDefaultInstance(), living.position(), living.level(), 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingHeal(LivingHealEvent event) {
        LivingEntity living = event.getEntity();
        if (!(living.level() instanceof ServerLevel level)) return;
        if (living.hasEffect(ModEffects.FROST_BURN) || living.hasEffect(ModEffects.BLEEDING) || living.hasEffect(ModEffects.HELL_FIRE)) {
            event.setCanceled(true); // todo 有些怪物对其免疫
        } else if (living.getData(ModAttachmentTypes.EVER_BENEFICIAL).isVitalCrystalUsed()) {
            event.setAmount(event.getAmount() * 1.2F);
        }

        // 治疗数字显示
        if (living.getHealth() < living.getMaxHealth()) {
            double y = living.getBoundingBoxForCulling().maxY;
            Vec3 pos = living.position();
            float amount = Math.round(event.getAmount() * 10.0F) / 10.0F;
            String text = amount % 1 == 0 ? Integer.toString((int) amount) : Float.toString(amount);
            Component component = Component.literal(text).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            level.sendParticles(new DamageIndicatorOptions(component, false), pos.x, y, pos.z, 1, 0.1, 0.1, 0.1, 0.0);
        }
    }

    @SubscribeEvent
    public static void livingIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            float amount = event.getAmount();
            AccessoryItems.applyHurtGetMana(serverPlayer, damageSource, (int) amount);
        }
        Immunity cause = ModUtils.getImmunityCause(event.getSource());
        if (((ILivingEntity) event.getEntity()).confluence$getImmunityTicks().containsKey(cause)) {
            event.setCanceled(true);
        }
        if (cause != null) {
            event.getContainer().setPostAttackInvulnerabilityTicks(event.getEntity().invulnerableTime);
        }
    }

    @SubscribeEvent
    public static void livingDamage$Pre(LivingDamageEvent.Pre event) {
        LivingEntity damagingEntity = event.getEntity();
        if (!(damagingEntity.level() instanceof ServerLevel level)) return;
        DamageSource damageSource = event.getSource();
        Entity sourceEntity = damageSource.getEntity();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) return;

        float amount = event.getNewDamage();
        Entity causer = damageSource.getEntity();

        ThornsEffect.apply(damagingEntity, causer, amount);
        amount = ArcheryEffect.apply(damagingEntity, damageSource, amount);
        amount = ManaSicknessEffect.apply(damageSource, amount);
        //amount = BreathingReed.consumer(damagingEntity, damageSource, amount);
        amount = TheConstant.applyAttackDamage(causer, amount);

        // 召唤物集火伤害加成
        if (damageSource.is(TETags.DamageTypes.SUMMONER)) {
            if (damagingEntity.hasEffect(TEEffects.SUMMON_FOCUS)) {
                amount = amount + 2;
                event.setNewDamage(amount);
            }
        }
        // 剑命中效果
        ItemStack weapon = damageSource.getWeaponItem();
        if (weapon != null && weapon.getItem() instanceof BaseSwordItem sword && sword.modifier != null) {
            if (sourceEntity instanceof Player player && player.getAttackStrengthScale(0.5f) > 0.95f) {
                sword.modifier.onHitEffects.forEach(effect -> effect.get().getEffect().accept(player, damagingEntity));
            }
        }
        // 暴击判定和伤害显示
        boolean crit = false;
        if (!TCAttributes.hasCustomAttribute(TCAttributes.CRIT_CHANCE) && causer instanceof Player player) {
            double chance = player.getAttributeValue(TCAttributes.CRIT_CHANCE);
            if (damagingEntity.level().random.nextFloat() < chance) {
                amount *= 2;
                player.crit(damagingEntity);
                crit = true;
            }
        }
        if (damageSource.getDirectEntity() instanceof AbstractArrow arrow) {
            crit |= arrow.isCritArrow();
        }
        crit |= ((IDamageSource) damageSource).confluence$isCritical();
        float roundedAmount = Math.round(amount * 10) / 10f;
        int intAmount = (int) roundedAmount;
        if (roundedAmount == 0F) return;
        String text = roundedAmount % 1 == 0 ? String.valueOf(intAmount) : String.valueOf(roundedAmount);
        Vec3 pos = damagingEntity.position();
        Component component = Component.literal(text).withStyle(crit ? ChatFormatting.DARK_RED : ChatFormatting.GOLD, ChatFormatting.BOLD);
        level.sendParticles(new DamageIndicatorOptions(component, crit), pos.x, damagingEntity.getBoundingBoxForCulling().maxY, pos.z, 1, 0.1, 0.1, 0.1, 0);
        event.setNewDamage(amount);


    }

    @SubscribeEvent
    public static void livingDamage$Post(LivingDamageEvent.Post event) {
        DamageSource damageSource = event.getSource();
        LivingEntity damagingEntity = event.getEntity();
        Entity sourceEntity = damageSource.getEntity();
        ItemStack weapon = damageSource.getWeaponItem();

        ModAchievements.luckyBreak_watchYourStep(damagingEntity, damageSource, sourceEntity);

        Immunity cause = ModUtils.getImmunityCause(damageSource);
        if (cause != null) {
            Object2IntMap<Immunity> invTicks = ((ILivingEntity) damagingEntity).confluence$getImmunityTicks();
            int time = cause.confluence$getImmunityDuration(damageSource);
//            System.out.println(event.getNewDamage());
            if (time != 0) {
                invTicks.put(cause, time);
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
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide) return;
        ModAchievements.matchingAttire_fashionStatement(event.getSlot(), living);
    }

    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        if (event.getEntity().getTags().contains(ModUtils.NO_DROPS_TAG)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void livingGetProjectile(LivingGetProjectileEvent event) {
        LivingEntity living = event.getEntity();
        ItemStack projectile = ExtraInventory.getProjectile(event.getProjectileItemStack(), event.getProjectileWeaponItemStack(), living);
        event.setProjectileItemStack(projectile);
        if (!projectile.isEmpty() && living.hasEffect(ModEffects.AMMO_BOX) && living.getRandom().nextFloat() < 0.2F) {
            event.setProjectileItemStack(projectile.copy());
        }
    }
}
