package org.confluence.terraentity.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.api.entity.IDiscardWhenRespawnEntity;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.config.TEAttributeModifierConfig;
import org.confluence.terraentity.data.mappeddata.MonsterMappedDatas;
import org.confluence.terraentity.entity.config.InitialArmors;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import org.confluence.terraentity.entity.monster.demoneye.DemonEyeVariant;
import org.confluence.terraentity.entity.monster.prefab.IAttributeHolder;
import org.confluence.terraentity.entity.monster.slime.BaseSlime;
import org.confluence.terraentity.entity.monster.slime.BlackSlime;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.*;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.mixed.IPlayer;
import org.confluence.terraentity.mixed.IZombie;
import org.confluence.terraentity.mixin.accessor.MobAccessor;
import org.confluence.terraentity.network.s2c.SyncLevelNamePacketS2C;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.utils.TEUtils;

import static org.confluence.terraentity.TerraEntity.MODID;

@EventBusSubscriber(modid = MODID)
public class GameEntityEvent {
    @SubscribeEvent
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
        // 生成信息
        Entity entity = event.getEntity();
        Boss.sendBossSpawnMessage(entity);
//        if(event.getEntity() instanceof ServerPlayer player){
//            player.addItem(new ItemStack(TERiddenItems.HONEYED_GOGGLES.get()));
//        }

        Level level = event.getLevel();

        if (!level.isClientSide && entity instanceof Zombie zombie && !zombie.isBaby() && !zombie.isVehicle() && zombie.getRandom().nextFloat() < ServerConfig.CHANCE_TO_SPAWN_SLIME_ON_ZOMBIE_HEAD.get()) {
            BaseSlime slime = (zombie instanceof ZombifiedPiglin ? TEMonsterEntities.LAVA_SLIME.get() : TEMonsterEntities.BLUE_SLIME.get()).create(level);
            if (slime != null) {
                Vec3 position = zombie.getPassengerRidingPosition(slime);
                slime.moveTo(position.x, position.y, position.z, zombie.getYRot(), 0.0F);
//                slime.finalizeSpawn(level, event.getDifficulty(), MobSpawnType.JOCKEY, null);
                level.addFreshEntity(slime);
                slime.startRiding(zombie);
                IZombie.of(zombie).terra_entity$setSlimeZombie();
            }
        }

        if (!level.isClientSide && entity instanceof AbstractTerraNPC npc && npc.getSpawnAtPos() == null) {
            npc.setSpawnAtPos(entity.blockPosition());
        }
    }

    @SubscribeEvent
    public static void entityLeaveLevelEvent(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 清除召唤物
            player.getData(TEAttachments.SUMMONER_STORAGE.get()).clear(player);
        }
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 同步召唤栏信息
            player.getData(TEAttachments.SUMMONER_STORAGE.get()).sync(player);

            IDiscardWhenRespawnEntity.process(player);

        }
    }

    @SubscribeEvent
    public static void entityDeathLevel(LivingDeathEvent event) {
        Boss.sendBossDeathMessage(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getData(TEAttachments.SUMMONER_STORAGE.get()).clear(player);
        }
    }

    @SubscribeEvent
    public static void livingDamageEntity(LivingDamageEvent.Post event) {
        LivingEntity e1 = event.getEntity();
        Level level = event.getEntity().level();
        Entity attacker = event.getSource().getEntity();
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (attacker != null && attacker.getType() == TEMonsterEntities.DECAYEDER.get()) {
            if (!e1.hasEffect(TEEffects.DEMONIC_THOUGHTS)) {
                e1.addEffect(new MobEffectInstance(
                        TEEffects.DEMONIC_THOUGHTS, 200
                ), attacker);
            } else {
                e1.removeEffect(TEEffects.DEMONIC_THOUGHTS);
                e1.hurt(event.getSource(), 6);
                AbstractMonster soulEater = TEMonsterEntities.EATER_OF_SOULS.get().create(level);
                if (soulEater != null) {
                    soulEater.setPos(e1.getEyePosition());
                    soulEater.setTarget(e1);
                    level.addFreshEntity(soulEater);
                }
                e1.removeEffect(TEEffects.DEMONIC_THOUGHTS);
            }
        }
        if (attacker != null && (attacker.getType() == TEMonsterEntities.CRIMSLIME.get() || attacker.getType() == TEMonsterEntities.CORRUPT_SLIME.get())) {
            if (e1.getRandom().nextFloat() <= 0.25f) {
                e1.addEffect(new MobEffectInstance(
                        MobEffects.DARKNESS, 300
                ), attacker);
            }
        }
    }

    @SubscribeEvent
    public static void livingDamage$Pre(LivingDamageEvent.Pre event) {
        DamageSource damageSource = event.getSource();
        float amount = event.getNewDamage();
        LivingEntity hurter = event.getEntity();
        Entity attacker = event.getSource().getEntity();

        if (damageSource.is(TETags.DamageTypes.SUMMONER) || attacker instanceof ISummonMob summoner) {
            // 召唤物集火伤害加成
            if (hurter.hasEffect(TEEffects.SUMMON_FOCUS)) {
                amount = amount + 2;

            }
            // 召唤物标记伤害增加
            if (attacker instanceof ISummonMob summoner) {
                LivingEntity owner = summoner.summon_getOwner();
                if (owner != null) {
                    var att = owner.getAttribute(TEAttributes.MARK_DAMAGE);
                    if (att != null) {
                        double damage = att.getValue();
                        amount += (float) damage;
                    }
                }
            } else if (attacker instanceof LivingEntity owner) {
                var att = owner.getAttribute(TEAttributes.MARK_DAMAGE);
                if (att != null) {
                    double damage = att.getValue();
                    amount += (float) damage;
                }
            }
        }

        event.setNewDamage(amount);
    }

    @SubscribeEvent
    public static void entityInteract(PlayerInteractEvent.EntityInteract event) {
        // 打开商店
        if (event.getTarget() instanceof ITradeHolder holder) {
            ((IPlayer) event.getEntity()).terra_entity$setTradeHolder(holder);
        }
    }

    @SubscribeEvent
    public static void mobFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();

        if (mob instanceof Enemy && event.getEntity().getRandom().nextFloat() >= ServerConfig.ENEMY_SPAWN_CHANCE.get()
                && event.getSpawnType() == MobSpawnType.NATURAL) {
            if (ServerConfig.ENEMY_SPAWN_CHANCE_APPLY_ALL.get() || BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace().equals(MODID)) {
                event.setSpawnCancelled(true);
                return;
            }
        }

        RandomSource randomSource = mob.getRandom();
        if (mob instanceof DemonEye demonEye) {
            demonEye.setVariant(DemonEyeVariant.random(randomSource));
        } else if (mob instanceof BlackSlime blackSlime) {
            blackSlime.finalizeSpawn(randomSource, event.getDifficulty());
        }

        if (mob instanceof IAttributeHolder holder) {
            holder.getAttributeBuilder().modify(mob);
        }
        TEAttributeModifierConfig.getInstance().modify(mob);
        if (event.getEntity() instanceof Monster living && !(event.getEntity() instanceof ISummonMob))
            TEUtils.monsterEnhance(living);
        else if (event.getEntity() instanceof Slime slime)
            TEUtils.monsterEnhance(slime);
        if (mob instanceof Boss boss && boss.shouldEnhanceMultiplayer()) {
            TEUtils.multiplePlayerEnhance(mob);
        }

        InitialArmors data = MappedDataTypes.getData(MappedDataTypes.MONSTER_MAP_DATAS, MonsterMappedDatas.MONSTER_ARMOR);
        data.accept(mob);
        //
        Integer xp = BuiltInRegistries.ENTITY_TYPE.getData(TEDataMaps.ENTITY_XP_DATA_MAP, mob.getType().builtInRegistryHolder().unwrapKey().orElseThrow());
        if(xp != null){
            ((MobAccessor)mob).setXpReward(xp);
        }

    }
    // 貌似没必要使用这个，重写monster的方法就行
//    @SubscribeEvent
//    public static void onCheckSpawnPosition(MobSpawnEvent.PositionCheck event)  {
//        if(event.getEntity() instanceof IAttributeHolder holder){
//            if(holder.getAttributeBuilder().spawnWithoutLight && event.getSpawnType() == MobSpawnType.NATURAL){
//                event.setResult(MobSpawnEvent.PositionCheck.Result.SUCCEED);
//            }
//
//        }
//    }

    @SubscribeEvent
    public static void invulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.isInvulnerable() || !(event.getEntity() instanceof LivingEntity living)) return;

        DamageSource damageSource = event.getSource();
        if (living.hasEffect(TEEffects.THE_TONGUE)) {
            event.setInvulnerable(true);
        } else if (living.getType() == TEMonsterEntities.FLYING_FISH.get() && damageSource.is(DamageTypes.FALL)) {
            event.setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        SyncLevelNamePacketS2C.sendToClient((ServerPlayer) event.getEntity());
    }
}
