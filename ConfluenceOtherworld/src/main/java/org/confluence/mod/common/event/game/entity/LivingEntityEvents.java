package org.confluence.mod.common.event.game.entity;

import com.xiaohunao.heaven_destiny_moment.common.moment.MomentManager;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.*;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.effect.beneficial.ArcheryEffect;
import org.confluence.mod.common.effect.beneficial.LuckEffect;
import org.confluence.mod.common.effect.beneficial.ThornsEffect;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.effect.harmful.ManaSicknessEffect;
import org.confluence.mod.common.effect.neutral.LoveEffect;
import org.confluence.mod.common.entity.projectile.boulder.TombstoneBoulder;
import org.confluence.mod.common.init.ModAchievements;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.PickaxeItems;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.item.sword.SweetSword;
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
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terraentity.entity.ai.Boss;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class LivingEntityEvents {
    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        DamageSource damageSource = event.getSource();
        // 未知模组导致的null
        if (damageSource != null && damageSource.getEntity() instanceof ServerPlayer serverPlayer) {
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
                int amount = (int) Math.min(Math.round((healthFactor + attackFactor + armorFactor + knockbackResistanceFactor) * difficultyFactor) * 7.0, 100000);
                ModUtils.dropMoney(amount, living.getX(), living.getEyeY() - 0.3, living.getZ(), level);
            }
        }

        if (living.level() instanceof ServerLevel level) {
            if (living instanceof Boss boss && boss.shouldShowMessage()) {
                EntityType<?> type = living.getType();
                ConfluenceData data = ConfluenceData.get(level);
                data.getKillBoard().defeat(type, data);
                boolean isEaterOfWorlds = type == TEBossEntities.EATER_OF_WORLDS.get();
                if (isEaterOfWorlds || type == TEBossEntities.BRAIN_OF_CTHULHU.get()) {
                    if (DateUtils.isWithinDayTime(0, 0, 4, 30, level.getDayTime())) { // 00:00 -> 04:30
                        MeteoriteTracker.INSTANCE.spawnAtNextNight = true;
                    } else if (!MeteoriteTracker.INSTANCE.spawnAtNextNight) {
                        MeteoriteTracker.INSTANCE.spawnAtNextNight = level.random.nextBoolean();
                    }
                }
                boolean stickySituation = type == TEBossEntities.KING_SLIME.get() && MomentManager.of(level).hasMoment(TMMoments.SLIME_RAIN);
                ResourceKey<Level> dimension = living.level().dimension();
                level.players().stream()
                        .filter(player -> player.level().dimension() == dimension)
                        .forEach(player -> {
                            TreasureBagItem.createItemEntity(living, player);
                            if (isEaterOfWorlds) {
                                PlayerUtils.awardAchievement(player, "worm_fodder");
                            }
                            if (stickySituation) {
                                PlayerUtils.awardAchievement(player, "sticky_situation");
                            }
                        });
            }

            if (living instanceof ServerPlayer serverPlayer) {
                PlayerUtils.dropMoney(serverPlayer);
                TombstoneBoulder.createTombstone(serverPlayer);
            }
            DeathMotionPacketS2C.sendToAll(living);
            NoTraps.entityDropsGrenade(living);
            if (living.getRandom().nextFloat() < 0.011F) {
                Item holidayGift = DateUtils.getHolidayGift();
                if (holidayGift != Items.AIR) {
                    LibUtils.createItemEntity(holidayGift.getDefaultInstance(), living.position(), living.level(), 0);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingHeal(LivingHealEvent event) {
        LivingEntity living = event.getEntity();
        if (!(living.level() instanceof ServerLevel level)) return;
        if (living.hasEffect(TEEffects.FROST_BURN) || living.hasEffect(ModEffects.BLEEDING) || living.hasEffect(TEEffects.HELLFIRE)) {
            event.setCanceled(true); // todo 有些怪物对其免疫
        } else {
            float amount = event.getAmount();
            if (living.getData(ModAttachmentTypes.EVER_BENEFICIAL).isVitalCrystalUsed()) {
                amount *= 1.2F;
            }
            if (living.hasEffect(ModEffects.COZY_FIRE)) {
                amount *= 1.1F;
            }
            event.setAmount(amount);
        }

        // 治疗数字显示
        if (living.getHealth() < living.getMaxHealth()) {
            double y = living.getBoundingBoxForCulling().maxY;
            Vec3 pos = living.position();
            float amount = Math.round(event.getAmount() * 10.0F) / 10.0F;
            String text = amount % 1 == 0 ? Integer.toString((int) amount) : Float.toString(amount);
            Component component = Component.literal(text).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            level.sendParticles(new DamageIndicatorOptions(component, false, DamageIndicatorOptions.Type.HEAL), pos.x, y, pos.z, 1, 0.1, 0.1, 0.1, 0.0);
        }
    }

    @SubscribeEvent
    public static void livingIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            float amount = event.getAmount();
            AccessoryItems.applyHurtGetMana(serverPlayer, damageSource, (int) amount);
        }
        Immunity cause = Immunity.getCause(event.getSource());
        if (((ILivingEntity) event.getEntity()).confluence$getImmunityTicks().containsKey(cause)) {
            event.setCanceled(true);
        }
        if (cause != null) {
            event.getContainer().setPostAttackInvulnerabilityTicks(event.getEntity().invulnerableTime);
        }
    }

    @SubscribeEvent
    public static void livingDamage$Pre(LivingDamageEvent.Pre event) {
        LivingEntity living = event.getEntity();
        if (!(living.level() instanceof ServerLevel level)) return;
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) return;

        float amount = event.getNewDamage();
        Entity attacker = damageSource.getEntity();

        ThornsEffect.apply(living, attacker, damageSource, amount);
        amount = ArcheryEffect.apply(living, damageSource, amount);
        amount = ManaSicknessEffect.apply(damageSource, amount);
        amount = TheConstant.applyAttackDamage(attacker, amount);

        // 克苏鲁之脑和飞眼怪给的debuff
        if (attacker != null && LibUtils.isAtLeastExpert(level, living.blockPosition())) {
            EntityType<?> type = attacker.getType();
            if (type == TEMonsterEntities.VISUAL_NEURON.get() || (type == TEBossEntities.BRAIN_OF_CTHULHU.get() && attacker.getRandom().nextFloat() < 0.3333F)) {
                boolean master = LibUtils.isMaster(level, living.blockPosition());
                Holder<MobEffect> debuff;
                float min;
                int i = attacker.getRandom().nextInt(81);
                if (i < 11) {
                    debuff = MobEffects.POISON;
                    min = master ? 6.56F : 5.25F;
                } else if (i < 22) {
                    debuff = MobEffects.BLINDNESS;
                    min = master ? 3.75F : 3.0F;
                } else if (i < 24) {
                    debuff = ModEffects.CURSED;
                    min = master ? 0.94F : 0.75F;
                } else if (i < 35) {
                    debuff = ModEffects.BLEEDING;
                    min = master ? 9.38F : 7.5F;
                } else if (i < 37) {
                    debuff = TCEffects.CONFUSED;
                    min = master ? 1.88F : 1.5F;
                } else if (i < 48) {
                    debuff = MobEffects.MOVEMENT_SLOWDOWN;
                    min = master ? 6.56F : 5.25F;
                } else if (i < 59) {
                    debuff = MobEffects.WEAKNESS;
                    min = master ? 14.06F : 11.25F;
                } else if (i < 70) {
                    debuff = ModEffects.SILENCED;
                    min = master ? 1.88F : 1.5F;
                } else {
                    debuff = ModEffects.BROKEN_ARMOR;
                    min = master ? 12.19F : 9.75F;
                }
                living.addEffect(new MobEffectInstance(debuff, (int) ((attacker.getRandom().nextFloat() * min + min) * 20)));
            }
        }
        // 芦苇呼吸管对溺水伤害减半
        if (damageSource.is(DamageTypes.DROWN)) {
            if (LibUtils.anyHandHasItem(living, itemStack -> itemStack.is(SwordItems.BREATHING_REED))) {
                amount *= 0.5F;
            }
        }
        // 剑命中效果
        ItemStack weapon = damageSource.getWeaponItem();
        if (weapon != null && weapon.getItem() instanceof BaseSwordItem sword) {
            sword.applyHitEffects(weapon, attacker, living, damageSource, amount);
        }
        // 暴击判定和伤害显示
        boolean crit = false;
        if (!TCAttributes.hasCustomAttribute(TCAttributes.CRIT_CHANCE) && attacker instanceof Player player) {
            if (living.getRandom().nextFloat() < player.getAttributeValue(TCAttributes.CRIT_CHANCE)) {
                amount *= 1.5F;
                player.crit(living);
                crit = true;
            }
        }
        if (damageSource.getDirectEntity() instanceof AbstractArrow arrow) {
            crit |= arrow.isCritArrow();
        }
        crit |= ((IDamageSource) damageSource).confluence$isCritical();
        ((IDamageSource) damageSource).confluence$setCritical(crit);
        event.setNewDamage(amount);
    }

    @SubscribeEvent
    public static void livingDamage$Post(LivingDamageEvent.Post event) {
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel serverLevel)) return;
        DamageSource damageSource = event.getSource();
        Entity attacker = damageSource.getEntity();
        float amount = event.getNewDamage();

        ModAchievements.luckyBreak_watchYourStep(victim, damageSource, attacker);
        FlaskEffect.onLivingDamage(victim, damageSource, amount);
        Immunity.calculateInvTicks(damageSource, (ILivingEntity) victim);
        DamageIndicatorOptions.sendParticles(serverLevel, damageSource, amount, victim);
    }

    @SubscribeEvent
    public static void mobEffect$Applicable(MobEffectEvent.Applicable event) {
        Holder<MobEffect> effect = event.getEffectInstance().getEffect();
        if (effect == TCEffects.CONFUSED) {
            if (event.getEntity() instanceof Boss) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        } else if (effect == ModEffects.SHIMMER) {
            if (!(event.getEntity() instanceof Player)) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        } else {
            boolean flag = false;
            EntityType<?> type = event.getEntity().getType();
            if (type == TEBossEntities.KING_SLIME.get()) {
                flag = effect == MobEffects.POISON;
            } else if (type == TEBossEntities.QUEEN_BEE.get()) {
                flag = effect == MobEffects.POISON;
            }
            if (flag) event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
        SweetSword.applyEffects(event);
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
        LoveEffect.onAdd(effectInstance.getEffect(), event.getEntity(), event.getEffectSource());
        FlaskEffect.removeAnotherFlaskEffects(effectInstance, event.getEntity());
    }

    @SubscribeEvent
    public static void livingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity living = event.getEntity();
        if (!living.level().isClientSide) {
            ModAchievements.matchingAttire_fashionStatement(event.getSlot(), living);
        }
    }

    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        if (event.isCanceled()) return;
        if (event.getEntity() instanceof Player player && !player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            ExtraInventory data = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
            for (int i = 0; i < data.getContainerSize(); i++) {
                if (i >= ExtraInventory.COINS_START && i < ExtraInventory.COINS_START + ExtraInventory.SIZE_COINS) continue;
                ItemStack itemStack = data.getItem(i);
                if (!itemStack.isEmpty()) {
                    data.setItem(i, ItemStack.EMPTY);
                }
                event.getDrops().add(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), itemStack));
            }
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

    @SubscribeEvent
    public static void livingBreathe(LivingBreatheEvent event) {
        LivingEntity living = event.getEntity();
        if (living.hasEffect(ModEffects.CHOKING)) {
            living.setAirSupply(living.getAirSupply() - 5);
        } else if (event.canBreathe()) return;
        if (living.hasEffect(ModEffects.SHIMMER)) {
            event.setCanBreathe(true);
        } else if (LibUtils.anyHandHasItem(living, itemStack -> itemStack.is(SwordItems.BREATHING_REED))) {
            // todo 管子在水面上时允许呼吸
            event.setConsumeAirAmount(living.getRandom().nextInt(3) > 0 ? 0 : 1);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void finalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        Level level = mob.level();
        BlockPos blockPos = BlockPos.containing(event.getX(), event.getY(), event.getZ());
        DifficultyInstance difficulty = event.getDifficulty();
        if (mob.getType() == EntityType.ZOMBIE) {
            Holder<Biome> biome = level.getBiome(blockPos);
            if (biome.is(Tags.Biomes.IS_ICY) || biome.is(Tags.Biomes.IS_SNOWY)) {
                boolean pink = mob.getRandom().nextFloat() < 0.01F;
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.HEAD, (pink ? ArmorItems.PINK_SNOW_CAPS : ArmorItems.SNOW_CAPS).get(), 0.003F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.CHEST, (pink ? ArmorItems.PINK_SNOW_SUITS : ArmorItems.SNOW_SUITS).get(), 0.003F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.LEGS, (pink ? ArmorItems.PINK_INSULATED_PANTS : ArmorItems.INSULATED_PANTS).get(), 0.003F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.FEET, (pink ? ArmorItems.PINK_INSULATED_SHOES : ArmorItems.INSULATED_SHOES).get(), 0.003F);
                //mob.setCustomName(Component.translatable("entity.confluence.frozen_zombie"));
                event.setCanceled(true);
            } else if (level.isRainingAt(blockPos)) {
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.HEAD, ArmorItems.RAIN_CAP.get(), 0.003F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.CHEST, ArmorItems.RAINCOAT.get(), 0.003F);
                //mob.setCustomName(Component.translatable("entity.confluence.raincoat_zombie"));
                event.setCanceled(true);
            }
        } else if (mob.getType() == EntityType.SKELETON) {
            if (!level.canSeeSky(blockPos) && mob.getRandom().nextFloat() < 0.01F) {
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.HEAD, ArmorItems.MINING_HELMET.get(), 1.0F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.CHEST, ArmorItems.MINING_CHESTPLATE.get(), 1.0F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.LEGS, ArmorItems.MINING_LEGGINGS.get(), 1.0F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.FEET, ArmorItems.MINING_BOOTS.get(), 1.0F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.MAINHAND, PickaxeItems.BONE_PICKAXE.get(), 0.25F);
                //mob.setCustomName(Component.translatable("entity.confluence.undead_miner"));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void livingEntityUseItem$Start(LivingEntityUseItemEvent.Start event) {
        LivingEntity living = event.getEntity();
        if (!living.level().isClientSide && living.hasEffect(ModEffects.CHOKING)) {
            ItemStack itemStack = event.getItem();
            if (itemStack.getFoodProperties(living) != null) {
                event.setCanceled(true);
                living.sendSystemMessage(Component.translatable("message.confluence.choking"));
            }
        }
    }

    @SubscribeEvent
    public static void livingEntityUseItem$Finish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasEffect(ModEffects.CHOKING)) {
            ItemStack itemStack = event.getItem();
            if (ModUtils.isWaterBottle(itemStack)) {
                player.removeEffect(ModEffects.CHOKING);
                ItemStack resultItem = itemStack.finishUsingItem(player.level(), player);
                event.setResultStack(resultItem);
            }
        }
    }
}
