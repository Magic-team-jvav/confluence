package org.confluence.mod.common.event.game.entity;

import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.*;
import org.confluence.lib.common.event.GameEvents;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.effect.beneficial.ArcheryEffect;
import org.confluence.mod.common.effect.beneficial.LuckEffect;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.effect.harmful.ManaSicknessEffect;
import org.confluence.mod.common.effect.neutral.LoveEffect;
import org.confluence.mod.common.entity.projectile.boulder.TombstoneBoulder;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.item.sword.SweetSword;
import org.confluence.mod.common.particle.DamageIndicatorOptions;
import org.confluence.mod.common.particle.WholeItemParticleOptions;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.mixed.IDamageSource;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.network.s2c.DeathMotionPacketS2C;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.entity.ai.Boss;
import org.confluence.terraentity.entity.monster.slime.GoldenSlime;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.confluence.mod.util.PlayerUtils.receiveMana;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class LivingEntityEvents {
    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        DamageSource damageSource = event.getSource();

        if (living.level() instanceof ServerLevel level) {
            if (living instanceof Enemy && damageSource.getEntity() instanceof ServerPlayer) {
                if (CommonConfigs.DROP_MONEY.get() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    ModUtils.enemyDropMoney(living, level);
                }
            }
            if (damageSource.getEntity() != null && damageSource.getEntity().getType().is(TETags.EntityTypes.CORRUPT)) {
                NatureBlocks.DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK.get().checkVisibilityAndSummonEntity(level, living);
            }
            if (living instanceof Boss boss && boss.shouldShowMessage()) {
                ModUtils.bossDeath(level, living);
            }
            if (living instanceof ServerPlayer serverPlayer) {
                PlayerUtils.dropMoney(serverPlayer);
                TombstoneBoulder.createTombstone(serverPlayer);
            }
            if (living.getRandom().nextFloat() < 0.011F) {
                Item holidayGift = DateUtils.getHolidayGift(living.getRandom());
                if (holidayGift != Items.AIR) {
                    LibUtils.createItemEntity(holidayGift.getDefaultInstance(), living.position(), level, 0);
                }
            }
            for (ServerPlayer serverPlayer : level.players()) {
                if (serverPlayer.position().distanceToSqr(living.position()) > 32 * 32) continue;
                if (serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE).canReceive() && serverPlayer.getRandom().nextFloat() < 0.083F) {
                    LibUtils.createItemEntity(DateUtils.getStarItem().getDefaultInstance(), living.position(), level, 0);
                    break;
                }
            }
            if (living instanceof AbstractTerraNPC npc) {
                NPCSpawner.INSTANCE.onNPCRemoved(npc);
            }
            if (living.hasEffect(ModEffects.BLOOD_BUTCHERED)) {
                NatureBlocks.BLOODTHIRST_CRYSTALLIZED_BLOCK.get().checkVisibility(level, living);
            }
            DeathMotionPacketS2C.sendToAll(living);
            NoTraps.entityDropsGrenade(living);
        }
    }

    /**
     * 阻止回血的药水效果，已改为使用EffectCure，并提取到Lib了
     *
     * @see GameEvents#livingHeal(LivingHealEvent)
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingHeal(LivingHealEvent event) {
        LivingEntity living = event.getEntity();
        if (!(living.level() instanceof ServerLevel level)) return;

        float amount = event.getAmount();
        if (living.getData(ModAttachmentTypes.EVER_BENEFICIAL).isVitalCrystalUsed()) {
            amount *= 1.2F;
        }
        if (living.hasEffect(ModEffects.COZY_FIRE)) {
            amount *= 1.1F;
        }
        event.setAmount(amount);

        DamageIndicatorOptions.sendHealParticle(event.getAmount(), level, living);
    }

    @SubscribeEvent
    public static void livingIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity living = event.getEntity();
        if (living instanceof ServerPlayer serverPlayer) {
            float amount = event.getAmount();
            AccessoryItems.applyHurtGetMana(serverPlayer, damageSource, (int) amount);
        }
        Immunity cause = Immunity.getCause(event.getSource());
        if (((ILivingEntity) living).confluence$getImmunityTicks().containsKey(cause)) {
            event.setCanceled(true);
        }
        if (cause != null) {
            event.getContainer().setPostAttackInvulnerabilityTicks(living.invulnerableTime);
        }
        if (living.getType() == TEMonsterEntities.GOLDEN_SLIME.get() && living.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 3; i++) {
                CoinItem item = PlayerUtils.INDEX_2_COIN.apply(i);
                serverLevel.sendParticles(
                        new WholeItemParticleOptions(item.getDefaultInstance()),
                        living.getX(), living.getY() + living.getBbHeight() / 2.0, living.getZ(),
                        2, 0.0, 0.5, 0.0, 0.1
                );
            }
        }
    }

    @SubscribeEvent
    public static void livingDamage$Pre(LivingDamageEvent.Pre event) {
        float amount = event.getNewDamage();
        if (amount <= 0.0F) return; // 防止莫名的负数伤害
        LivingEntity living = event.getEntity();
        if (!(living.level() instanceof ServerLevel level)) return;
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) return;
        @Nullable Entity attacker = damageSource.getEntity();

        amount = ArcheryEffect.apply(living, damageSource, amount);
        amount = ManaSicknessEffect.apply(damageSource, amount);
        amount = TheConstant.applyAttackDamage(attacker, amount);

        ModUtils.applyBrainOfCthulhuDebuff(level, attacker, living);
        ModUtils.applyCursedSkullDebuff(attacker, living);

        // 芦苇呼吸管对溺水伤害减半
        if (damageSource.is(DamageTypes.DROWN)) {
            if (LibUtils.anyHandHasItem(living, SwordItems.BREATHING_REED.get())) {
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

        AchievementUtils.luckyBreak_watchYourStep(victim, damageSource, attacker);
        FlaskEffect.onLivingDamage(victim, damageSource, amount);
        Immunity.calculateInvTicks(damageSource, victim);
        DamageIndicatorOptions.sendDamageParticle(serverLevel, damageSource, amount, victim);
    }

    @SubscribeEvent
    public static void mobEffect$Applicable(MobEffectEvent.Applicable event) { // 泰拉生物的免疫全扔mixin了
        Holder<MobEffect> effect = event.getEffectInstance().getEffect();
        if (effect == ModEffects.SHIMMER && !(event.getEntity() instanceof Player)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
        SweetSword.applyEffects(event);
    }

    @SubscribeEvent
    public static void mobEffect$Added(MobEffectEvent.Added event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        LoveEffect.onAdd(effectInstance, event.getEntity(), event.getEffectSource());
        FlaskEffect.removeAnotherFlaskEffects(effectInstance, event.getEntity());
    }

    @SubscribeEvent
    public static void mobEffect$Remove(MobEffectEvent.Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) return;
        LuckEffect.onRemove(event.getEntity(), effectInstance.getEffect(), effectInstance.getAmplifier());
    }

    @SubscribeEvent
    public static void livingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity living = event.getEntity();
        if (living instanceof ServerPlayer serverPlayer) {
            AchievementUtils.matchingAttire_fashionStatement(event.getSlot(), serverPlayer);
            if (event.getTo().is(ModTags.Items.SHOW_SIGNAL)) {
                VisibilityPacketS2C.sendSignal(serverPlayer, true);
            } else if (event.getFrom().is(ModTags.Items.SHOW_SIGNAL)) {
                VisibilityPacketS2C.sendSignal(serverPlayer, false);
            }
        }
    }

    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            ExtraInventory data = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
            for (int i = 0; i < data.getContainerSize(); i++) {
                if (i >= ExtraInventory.COINS_START && i < ExtraInventory.COINS_START + ExtraInventory.SIZE_COINS)
                    continue;
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
        event.setProjectileItemStack(ExtraInventory.getProjectile(event.getProjectileItemStack(), event.getProjectileWeaponItemStack(), living));

        if (!event.getProjectileItemStack().isEmpty()) {
            if (living.hasEffect(ModEffects.AMMO_BOX) && living.getRandom().nextFloat() < 0.2F) {
                event.setProjectileItemStack(event.getProjectileItemStack().copy());
                return;
            }

            if (event.getEntity() instanceof Player player) {
                HookMapManager.postHooks(ModHookTypes.SKIP_AMMO_CONSUME.get(), (owner, hook, original) -> {
                    if (hook.shouldSkipConsume(owner, original.getEntity(), original.getProjectileItemStack())) {
                        original.setProjectileItemStack(original.getProjectileItemStack().copy());
                    }
                    return original;
                }, player, event);
            }
        }
    }

    @SubscribeEvent
    public static void livingBreathe(LivingBreatheEvent event) {
        LivingEntity living = event.getEntity();
        boolean b = !living.getActiveEffectsMap().isEmpty();
        if (b && living.hasEffect(ModEffects.CHOKING)) {
            living.setAirSupply(living.getAirSupply() - 5);
        }
        if (event.canBreathe()) return;

        if (b && living.hasEffect(ModEffects.SHIMMER)) {
            event.setCanBreathe(true);
        } else if (LibUtils.anyHandHasItem(living, itemStack -> !itemStack.isEmpty() && itemStack.is(SwordItems.BREATHING_REED))) {
            if (living.canDrownInFluidType(living.level().getFluidState(living.blockPosition().offset(0, 2, 0)).getFluidType())) {
                event.setConsumeAirAmount(living.getRandom().nextInt(2) > 0 ? 0 : 1);
            } else {
                event.setCanBreathe(true);
            }
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
        } else if (event.getEntity() instanceof Slime slime && mob.level() instanceof ServerLevel serverLevel) {
            if (ModSecretSeeds.CELEBRATIONMK10.match(serverLevel) || ModSecretSeeds.GET_FIXED_BOI.match(serverLevel)) {
                if (event.getSpawnType().equals(MobSpawnType.NATURAL)) {
                    if (mob.getRandom().nextInt(140) == 1) {
                        event.setCanceled(true);

                        GoldenSlime goldenSlime = TEMonsterEntities.GOLDEN_SLIME.get().create(serverLevel);
                        if (goldenSlime != null) {
                            goldenSlime.moveTo(slime.getX(), slime.getY(), slime.getZ(), slime.getYRot(), slime.getXRot());
                            level.addFreshEntity(goldenSlime);
                        }
                    }
                }
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
    public static void livingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack itemStack = event.getItem();
        RandomSource random = player.getRandom();
        if (itemStack.is(FoodItems.GREEN_DUMPLING.get()) && random.nextInt(6) == 0) {
            player.addEffect(new MobEffectInstance(ModEffects.CHOKING, 2400));
        }
        if (player.hasEffect(ModEffects.CHOKING) && ModUtils.isWaterBottle(itemStack)) {
            player.removeEffect(ModEffects.CHOKING);
            ItemStack resultItem = itemStack.finishUsingItem(player.level(), player);
            event.setResultStack(resultItem);
        }
        if (itemStack.is(FoodItems.PIGLIN_STEW.get())) {
            player.setHealth(player.getMaxHealth());
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20.0f);
            receiveMana(player, () -> 1000);
            List<Holder<MobEffect>> negativeEffects = player.getActiveEffects().stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> !effect.value().isBeneficial())
                    .toList();
            for (int i = negativeEffects.size() - 1; i >= 0; i--) {
                player.removeEffect(negativeEffects.get(i));
            }
        }

    }

    @SubscribeEvent
    public static void mobSpawn$PositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getSpawnType() == MobSpawnType.NATURAL && event.getResult() != MobSpawnEvent.PositionCheck.Result.FAIL) {
            if (DungeonStructure.skipSpawn(event.getEntity(), event.getLevel().getLevel())) {
                event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
            }
        }
    }
}
