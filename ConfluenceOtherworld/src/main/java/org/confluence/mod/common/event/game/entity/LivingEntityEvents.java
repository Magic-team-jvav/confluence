package org.confluence.mod.common.event.game.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.*;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.api.event.ArmorPenetrationEvent;
import org.confluence.lib.api.event.ProcessCriticalDamageEvent;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.bestiary.ToBeBestiaryEntryEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.data.map.GamePhase2AttributeModifiers;
import org.confluence.mod.common.data.map.LivingInvulnerableEffects;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.effect.beneficial.ArcheryEffect;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.effect.harmful.ManaSicknessEffect;
import org.confluence.mod.common.entity.projectile.boulder.TombstoneBoulderEntity;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.gameevent.SlimeRainGameEvent;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.accessory.GuideVooDooDollItem;
import org.confluence.mod.common.item.axe.LucyTheAxe;
import org.confluence.mod.common.item.common.BaseLanceItem;
import org.confluence.mod.common.item.mana.CrystalVileShardItem;
import org.confluence.mod.common.item.sword.StarSteelSword;
import org.confluence.mod.common.item.sword.SweetSword;
import org.confluence.mod.common.particle.DamageIndicatorOptions;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.integration.terra_entity.TEHelper;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.network.s2c.DeathMotionPacketS2C;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.*;
import org.confluence.terra_curio.api.event.AfterAccessoryAbilitiesFlushedEvent;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terraentity.api.entity.IMinion;
import org.confluence.terraentity.entity.animal.SimpleVariantAnimal;
import org.confluence.terraentity.entity.boss.Skeletron;
import org.confluence.terraentity.entity.monster.slime.GoldenSlime;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.summon.AbstractSummonMob;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.TEYoyosItems;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.Collection;
import java.util.List;

import static org.confluence.mod.util.PlayerUtils.receiveMana;

@EventBusSubscriber(modid = Confluence.MODID)
public final class LivingEntityEvents {
    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource damageSource = event.getSource();

        if (victim.level() instanceof ServerLevel level) {
            GameEventSystem.INSTANCE.countKilled(victim);
            TombstoneBoulderEntity.createTombstoneEntity(victim);
            Entity attacker = LibUtils.getOwner(damageSource);

            if (attacker instanceof ServerPlayer) {
                if (victim instanceof Enemy &&
                        CommonConfigs.ENEMY_DROPS_MONEY.get() &&
                        level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) &&
                        (!(victim instanceof IMinion minion) || minion.minion_getOwnerUUID() == null)
                ) ModUtils.enemyDropMoney(victim, level);
                Bestiary.INSTANCE.updateEntry(victim, true);
            }
            if (attacker != null && attacker.getType().is(TETags.EntityTypes.CORRUPT)) {
                NatureBlocks.DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK.get().checkVisibilityAndSummonEntity(level, victim);
            }
            if (victim instanceof Boss boss && boss.shouldShowMessage()) {
                ModUtils.bossDeath(level, victim);
                SlimeRainGameEvent.INSTANCE.checkEnd(victim);
            }
            if (victim instanceof ServerPlayer player) {
                PlayerUtils.dropMoney(player);
            }
            for (ServerPlayer player : level.players()) {
                if (player.position().distanceToSqr(victim.position()) > 32 * 32) continue;
                if (ManaStorage.of(player).canReceive() && player.getRandom().nextFloat() < 0.083F) {
                    LibUtils.createItemEntity(DateUtils.getStarItem().getDefaultInstance(), victim.position(), level, 0);
                    break;
                }
            }
            if (victim instanceof AbstractTerraNPC npc) {
                NPCSpawner.INSTANCE.onNPCRemoved(npc);
                if (attacker != null && npc.getType() == TENpcEntities.CLOTHIER.get() &&
                        attacker instanceof Player player &&
                        LibDateUtils.isNight(level) && // 晚上杀死才生成
                        TCUtils.hasType(player, AccessoryItems.CLOTHIER$KILLER)
                ) {
                    Skeletron skeletron = new Skeletron(TEBossEntities.SKELETRON.get(), level);
                    skeletron.finalizeSpawn(level, level.getCurrentDifficultyAt(skeletron.blockPosition()), MobSpawnType.EVENT, null);
                    ModUtils.summonBoss(level, attacker.blockPosition(), skeletron);
                }

                if (npc.getType() == TENpcEntities.GUIDE.get() && level.dimension() == OverworldUtils.underworld() && damageSource.is(DamageTypes.LAVA)) {
                    GuideVooDooDollItem.summon(npc, level, npc.getRandom().nextBoolean(), () -> null);
                }
            }
            if (victim.hasEffect(ModEffects.BLOOD_BUTCHERED)) {
                NatureBlocks.BLOODTHIRST_CRYSTALLIZED_BLOCK.get().checkVisibility(level, victim);
            }
            DeathMotionPacketS2C.sendToAll(victim);
            NoTraps.entityDropsGrenade(victim);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingHeal(LivingHealEvent event) {
        LivingEntity living = event.getEntity();
        if (!(living.level() instanceof ServerLevel level)) return;
        float amount = event.getAmount();

        if (living instanceof Player player) {
            amount = ModArmorBonus.applyHealAmount(player, amount);
        }
        if (EverBeneficial.of(living).isVitalCrystalUsed()) {
            amount *= 1.2F;
        }
        if (living.hasEffect(ModEffects.COZY_FIRE)) {
            amount *= 1.1F;
        }
        if (living.hasEffect(ModEffects.HEART_LANTERN)) {
            amount *= 1.2F;
        }
        event.setAmount(amount);

        DamageIndicatorOptions.sendHealParticle(amount, level, living);
    }

    @SubscribeEvent
    public static void livingIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity living = event.getEntity();

        if (living instanceof ServerPlayer player) {
            AccessoryItems.applyHurtGetMana(player, damageSource, event.getAmount());
        }
        if (Immunity.getCause(event.getSource()) != null) {
            event.getContainer().setPostAttackInvulnerabilityTicks(living.invulnerableTime);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void livingDamage$Pre(LivingDamageEvent.Pre event) {
        float amount = event.getNewDamage();
        if (amount <= 0.0F) return; // 防止莫名的负数伤害
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level)) return;
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        @Nullable Entity attacker = damageSource.getEntity();

        ModUtils.applyBrainOfCthulhuDebuff(level, attacker, victim);
        ModUtils.applyCursedSkullDebuff(attacker, victim);

        if (attacker instanceof ServerPlayer player) {
            EnchantmentUtils.dropsStar(player, victim, damageSource);
            amount = EnchantmentUtils.processMagicAttack(player, damageSource, amount);
            amount = TheConstant.applyAttackDamage(player, amount);
            amount = ManaSicknessEffect.process(player, damageSource, amount);
            amount = AbstractEnemyBannerBlock.processAttacker(player, victim, amount);
        }
        if (victim instanceof ServerPlayer player) {
            amount = EnchantmentUtils.processManaProtection(player, damageSource, amount);
            amount = PlayerUtils.applyTerraFire(damageSource, amount);
            amount = AbstractEnemyBannerBlock.processVictim(player, attacker, amount);
        }
        amount = ArcheryEffect.apply(victim, damageSource, amount);
        // 芦苇呼吸管对溺水伤害减半
        if (damageSource.is(DamageTypes.DROWN) && LibUtils.anyHandHasItem(victim, SwordItems.BREATHING_REED.get())) {
            amount *= 0.5F;
        }
        amount = SwordItems.processEffect(damageSource, attacker, victim, amount);
        event.setNewDamage(amount);
    }

    @SubscribeEvent
    public static void livingDamage$Post(LivingDamageEvent.Post event) {
        float amount = event.getNewDamage();
        if (amount <= 0.0F) return; // 防止莫名的负数伤害
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel serverLevel)) return;
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) {
            return;
        }
        @Nullable Entity attacker = damageSource.getEntity();

        FlaskEffect.onLivingDamage(victim, attacker, damageSource, amount);
        Immunity.calculateInvTicks(damageSource, victim);
        DamageIndicatorOptions.sendDamageParticle(serverLevel, damageSource, amount, victim);
        if (victim instanceof ServerPlayer player) {
            AchievementUtils.luckyBreak_watchYourStep(player, damageSource, attacker);
            BaseLanceItem.cancelSting(player);
            ModArmorBonus.beAttacked(player, damageSource);
        }
        if (attacker instanceof ServerPlayer player) {
            ModArmorBonus.onAttacked(player, damageSource, victim);
            LucyTheAxe.onDamageLiving(player, victim);
            StarSteelSword.tryDropManaStar(victim, player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void processCriticalDamage(ProcessCriticalDamageEvent event) {
        if (event.getDamageSource().getEntity() instanceof ServerPlayer player) {
            StarSteelSword.processCriticalDamage(player, event.isCritical(), event::setCriticalDamageMultiplier);
        }
    }

    @SubscribeEvent
    public static void mobEffect$Applicable(MobEffectEvent.Applicable event) {
        if (event.getResult() == MobEffectEvent.Applicable.Result.DEFAULT && !(event.getEntity() instanceof Player)) {
            Holder<MobEffect> effect = event.getEffectInstance().getEffect();
            if (LivingInvulnerableEffects.isInvulnerableTo(event.getEntity(), effect)) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
        SweetSword.applyEffects(event);
    }

    @SubscribeEvent
    public static void mobEffect$Added(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (event.getEffectSource() != null) {
            ModEffects.onLoveEffectAdd(instance, event.getEntity(), event.getEffectSource());
        }
        FlaskEffect.removeAnotherFlaskEffects(instance, event.getEntity());
    }

    @SubscribeEvent
    public static void mobEffect$Remove(MobEffectEvent.Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) return;
        ModEffects.onLuckEffectRemove(event.getEntity(), effectInstance.getEffect(), effectInstance.amplifier);
    }

    @SubscribeEvent
    public static void livingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        AchievementUtils.matchingAttire_fashionStatement(event.getSlot().getType(), player);
        if (event.getTo().is(ModTags.Items.SHOW_SIGNAL)) {
            VisibilityPacketS2C.sendSignal(player, true);
        } else if (event.getFrom().is(ModTags.Items.SHOW_SIGNAL)) {
            VisibilityPacketS2C.sendSignal(player, false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void livingDrops(LivingDropsEvent event) {
        LivingEntity living = event.getEntity();
        ServerLevel level = (ServerLevel) living.level();
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) return;
        Collection<ItemEntity> drops = event.getDrops();
        double x = living.getX();
        double y = living.getY();
        double z = living.getZ();

        if (living instanceof Player player) { // 掉落玩家的钱币
            ExtraInventory data = ExtraInventory.of(player);
            for (int i = 0; i < ExtraInventory.SIZE_COINS; i++) {
                ItemStack itemStack = data.getCoins(i);
                if (!itemStack.isEmpty()) {
                    data.setItem(i, ItemStack.EMPTY);
                }
                drops.add(new ItemEntity(level, x, y, z, itemStack));
            }
        }
        if (living.getRandom().nextFloat() < 0.011F) dropsHolidayGift:{ // 掉落节日礼物
            Item holidayGift = DateUtils.getHolidayGift(living.getRandom());
            if (holidayGift == Items.AIR) break dropsHolidayGift;
            ItemEntity entity = new ItemEntity(level, x, y, z, holidayGift.getDefaultInstance());
            entity.setNoPickUpDelay();
            drops.add(entity);
        }
        boolean isEnemy = living instanceof Enemy;
        if (KillBoard.INSTANCE.getGamePhase().isHardmode() &&
                isEnemy &&
                (!(living instanceof IMinion minion) || minion.minion_getOwnerUUID() == null) &&
                !living.getType().is(ModTags.EntityTypes.DO_NOT_DROPS_EVIL_SOUL) &&
                (y < OverworldUtils.getUndergroundY() || ModSecretSeeds.DONT_DIG_UP.match(level) || ModSecretSeeds.GET_FIXED_BOI.match(level)) &&
                living.getRandom().nextFloat() < (LibUtils.isAtLeastExpert(level, living.blockPosition()) ? 0.36F : 0.2F)
        ) { // 掉落光明或暗影之魂
            Holder<Biome> biome = level.getBiome(living.blockPosition());
            ItemStack soul = ItemStack.EMPTY;
            if (biome.is(ModTags.Biomes.THE_HALLOW)) {
                soul = MaterialItems.SOUL_OF_LIGHT.toStack();
            } else if (biome.is(ModTags.Biomes.THE_CORRUPTION) || biome.is(ModTags.Biomes.THE_CRIMSON)) {
                soul = MaterialItems.SOUL_OF_NIGHT.toStack();
            }
            if (soul != ItemStack.EMPTY) {
                drops.add(new ItemEntity(level, x, y, z, soul, 0, 0.02, 0));
            }
        }
        if (isEnemy && level.dimension() == OverworldUtils.underworld() && living.getRandom().nextInt(400) == 0) { // 掉落喷流球
            drops.add(new ItemEntity(level, x, y, z, TEYoyosItems.CASCADE.toStack()));
        }

        for (ItemEntity entity : drops) {
            ModUtils.makeItemAntigravity(entity);
        }
    }

    @SubscribeEvent
    public static void livingGetProjectile(LivingGetProjectileEvent event) {
        LivingEntity living = event.getEntity();
        event.setProjectileItemStack(ExtraInventory.getProjectile(event.getProjectileItemStack(), event.getProjectileWeaponItemStack(), living));

        ItemStack projectileItemStack = event.getProjectileItemStack();
        if (!projectileItemStack.isEmpty() && living instanceof Player player && PlayerUtils.shouldSkipConsumeAmmo(player)) {
            event.setProjectileItemStack(projectileItemStack.copy());
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
        ModArmorBonus.onBreath(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void finalizeSpawn(FinalizeSpawnEvent event) {
        ServerLevel level = event.getLevel().getLevel();
        Mob mob = event.getEntity();
        EntityType<?> type = mob.getType();
        if (type == EntityType.ZOMBIE) {
            BlockPos blockPos = BlockPos.containing(event.getX(), event.getY(), event.getZ());
            Holder<Biome> biome = level.getBiome(blockPos);
            DifficultyInstance difficulty = event.getDifficulty();
            if (biome.is(Tags.Biomes.IS_ICY) || biome.is(Tags.Biomes.IS_SNOWY)) {
                boolean pink = mob.getRandom().nextFloat() < 0.01F;
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.HEAD, (pink ? ArmorItems.PINK_SNOW_CAPS : ArmorItems.SNOW_CAPS).get(), 0.003F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.CHEST, (pink ? ArmorItems.PINK_SNOW_SUITS : ArmorItems.SNOW_SUITS).get(), 0.003F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.LEGS, (pink ? ArmorItems.PINK_INSULATED_PANTS : ArmorItems.INSULATED_PANTS).get(), 0.003F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.FEET, (pink ? ArmorItems.PINK_INSULATED_SHOES : ArmorItems.INSULATED_SHOES).get(), 0.003F);
                mob.setCustomName(Component.translatable("entity.confluence.frozen_zombie"));
                mob.addTag("frozen_zombie");
                event.setCanceled(true);
            } else if (ModUtils.isRainingAt(level, blockPos)) {
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.HEAD, ArmorItems.RAIN_CAP.get(), 0.003F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.CHEST, ArmorItems.RAINCOAT.get(), 0.003F);
                mob.setCustomName(Component.translatable("entity.confluence.raincoat_zombie"));
                mob.addTag("raincoat_zombie");
                event.setCanceled(true);
            }
        } else if (type == EntityType.SKELETON) {
            DifficultyInstance difficulty = event.getDifficulty();
            if (!level.canSeeSky(BlockPos.containing(event.getX(), event.getY(), event.getZ())) && mob.getRandom().nextFloat() < 0.01F) {
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.HEAD, ArmorItems.MINING_HELMET.get(), 1.0F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.CHEST, ArmorItems.MINING_CHESTPLATE.get(), 1.0F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.LEGS, ArmorItems.MINING_LEGGINGS.get(), 1.0F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.FEET, ArmorItems.MINING_BOOTS.get(), 1.0F);
                LibUtils.setItemAndDropChance(mob, difficulty, EquipmentSlot.MAINHAND, PickaxeItems.BONE_PICKAXE.get(), 0.25F);
                mob.setCustomName(Component.translatable("entity.confluence.undead_miner"));
                mob.addTag("undead_miner");
                event.setCanceled(true);
            }
        } else if (event.getSpawnType() == MobSpawnType.NATURAL && mob instanceof Slime slime) {
            if ((ModSecretSeeds.CELEBRATIONMK10.match() || ModSecretSeeds.GET_FIXED_BOI.match()) && mob.getRandom().nextInt(140) == 1) {
                event.setCanceled(true);
                GoldenSlime goldenSlime = TEMonsterEntities.GOLDEN_SLIME.get().create(level);
                if (goldenSlime != null) {
                    goldenSlime.moveTo(slime.getX(), slime.getY(), slime.getZ(), slime.getYRot(), slime.getXRot());
                    level.addFreshEntity(goldenSlime);
                }
            }
        } else if (type == TEAnimals.WORM.get()) {
            SimpleVariantAnimal worm = TEAnimals.WORM.get().tryCast(mob);
            if (worm != null) {
                TEHelper.finalizeWormSpawn(worm);
            }
        } else if (type == ModEntities.INVERSE_ENDERMAN.get()) {
            mob.moveTo(mob.getX(), mob.getY() - mob.getBbHeight(), mob.getZ());
        }

        if (!event.isCanceled()) {
            GamePhase2AttributeModifiers.applyModifiers(mob);
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
        if (event.getSpawnType() != MobSpawnType.NATURAL) return;
        Mob mob = event.getEntity();
        if (event.getResult() != MobSpawnEvent.PositionCheck.Result.FAIL && (
                DungeonStructure.skipSpawn(mob, event.getLevel().getLevel()) ||
                        GameEventSystem.shouldDenyNatureSpawn()
        )) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
        if (mob.getType().is(ModTags.EntityTypes.SPAWN_AT_GRAVEYARD)) {
            ILevelChunkSection iSection = DynamicBiomeUtils.getISection(event.getLevel(), mob.blockPosition());
            if (iSection != null && iSection.confluence$isGraveyard()) {
                event.setResult(MobSpawnEvent.PositionCheck.Result.SUCCEED);
            }
        }
    }

    @SubscribeEvent
    public static void mobSpawn$SpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (event.getSpawnType() == MobSpawnType.NATURAL && !event.getPlacementCheckResult()) {
            EntityType<?> entityType = event.getEntityType();
//            if (entityType == TEMonsterEntities.GHOST.get()) {
//                ILevelChunkSection iSection = DynamicBiomeUtils.getISection(event.getLevel(), event.getPos());
//                event.setResult(iSection != null && iSection.confluence$isGraveyard()
//                        ? MobSpawnEvent.SpawnPlacementCheck.Result.SUCCEED
//                        : MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
//            } else
            if (entityType.is(ModTags.EntityTypes.SPAWN_AT_GRAVEYARD)) {
                ILevelChunkSection iSection = DynamicBiomeUtils.getISection(event.getLevel(), event.getPos());
                if (iSection != null && iSection.confluence$isGraveyard()) {
                    event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.SUCCEED);
                }
            }
        }
    }

    @SubscribeEvent
    public static void effectParticleModification(EffectParticleModificationEvent event) {
        if (event.isVisible() && !IMobEffectInstance.of(event.getEffect()).confluence$isEnabled()) {
            event.setVisible(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void spawnClusterSize(SpawnClusterSizeEvent event) {
        if (BloodMoonGameEvent.INSTANCE.started()) {
            Mob mob = event.getEntity();
            if (mob instanceof Enemy && mob.position().y > OverworldUtils.getSurfaceY()) {
                event.setSize(LibMathUtils.multiplyInt(event.getSize(), 2, mob.getRandom()));
            }
        }
    }

    @SubscribeEvent
    public static void afterAccessoryAbilitiesFlushed(AfterAccessoryAbilitiesFlushedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerUtils.flushPrimitiveValueData(player);
        }
    }

    @SubscribeEvent
    public static void curioChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (PrefixUtils.canInit(event.getTo())) {
                PrefixUtils.initPrefix(player.getRandom(), event.getTo());
            }
        }
    }

    @SubscribeEvent
    public static void toBeBestiaryEntry(ToBeBestiaryEntryEvent event) {
        LivingEntity living = event.getEntity();
        if (living instanceof AbstractSummonMob) {
            event.setCanceled(true);
        } else {
            EntityType<?> type = living.getType();
            if (type.is(ModTags.EntityTypes.BESTIARY_BLACKLIST)) {
                event.setCanceled(true);
            } else if (type == TEBossEntities.SKELETRON_HAND.get()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void armorPenetration(ArmorPenetrationEvent event) {
        DamageSource damageSource = event.getDamageSource();

        @Nullable Entity direct = damageSource.getDirectEntity();
        if (direct != null && direct.getType() == ModEntities.CRYSTAL_VILE_SHARD_PROJECTILE.get()) {
            event.setPenetration(event.getPenetration() + CrystalVileShardItem.ARMOR_PENETRATION);
        }

        if (damageSource.getEntity() instanceof LivingEntity living &&
                damageSource.is(LibTags.DamageTypes.AS_MELEE_ATTACK) &&
                living.hasEffect(ModEffects.SHARPENED)
        ) {
            event.setPenetration(event.getPenetration() + 12);
        }
    }
}
