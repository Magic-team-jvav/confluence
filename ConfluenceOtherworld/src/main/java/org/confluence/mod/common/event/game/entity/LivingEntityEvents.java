package org.confluence.mod.common.event.game.entity;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.api.event.ArmorPenetrationEvent;
import org.confluence.lib.api.event.ProcessCriticalDamageEvent;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.common.entity.PartHitTarget;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.SoulCollectionEvent;
import org.confluence.mod.api.event.SoulOverdraftEvent;
import org.confluence.mod.api.event.bestiary.ToBeBestiaryEntryEvent;
import org.confluence.mod.api.item.ISoulBow;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.data.map.AttackEffects;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.data.map.GamePhase2AttributeModifiers;
import org.confluence.mod.common.data.map.LivingInvulnerableEffects;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.effect.beneficial.ArcheryEffect;
import org.confluence.mod.common.effect.beneficial.ThornsEffect;
import org.confluence.mod.common.effect.flask.FlaskEffect;
import org.confluence.mod.common.effect.harmful.ManaSicknessEffect;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.EnemyTargeting;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossMultiplayerEnhancement;
import org.confluence.mod.common.entity.boss.BossOwnedEntity;
import org.confluence.mod.common.entity.boss.Skeletron;
import org.confluence.mod.common.entity.monster.Decayeder;
import org.confluence.mod.common.entity.monster.TheHungry;
import org.confluence.mod.common.entity.monster.slime.GoldenSlime;
import org.confluence.mod.common.entity.mount.AbstractMountEntity;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.TownPopulation;
import org.confluence.mod.common.entity.projectile.boulder.TombstoneBoulderEntity;
import org.confluence.mod.common.entity.projectile.sword.BeeKeeperProjectile;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.entity.yoyo.BaseYoyoProjectile;
import org.confluence.mod.common.entity.yoyo.TerrarianProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.gameevent.SlimeRainGameEvent;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.accessory.GuideVooDooDollItem;
import org.confluence.mod.common.item.axe.LucyTheAxe;
import org.confluence.mod.common.item.common.BaseLanceItem;
import org.confluence.mod.common.item.common.HomingBeeItem;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.mana.CrystalVileShardItem;
import org.confluence.mod.common.item.soulcerer.SoulWeaponItem;
import org.confluence.mod.common.item.sword.StarSteelSword;
import org.confluence.mod.common.item.sword.SweetSword;
import org.confluence.mod.common.item.whip.WhipDamageSource;
import org.confluence.mod.common.particle.DamageIndicatorOptions;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.common.worldgen.secret_seed.NoTraps;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.network.s2c.DeathMotionPacketS2C;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.*;
import org.confluence.terra_curio.api.event.AfterAccessoryAbilitiesFlushedEvent;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.Collection;
import java.util.List;

import static org.confluence.mod.util.PlayerUtils.receiveMana;

@EventBusSubscriber(modid = Confluence.MODID)
public final class LivingEntityEvents {
    /// 原版僵尸变为史莱姆僵尸的概率。
    private static final float SLIME_ZOMBIE_CHANCE = 0.01F;

    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        DamageSource damageSource = event.getSource();

        if (victim.level() instanceof ServerLevel level) {
            GameEventSystem.INSTANCE.countKilled(victim);
            if (victim instanceof Enemy && !(victim instanceof OwnedSummon)
                    && !(victim instanceof TheHungry hungry && hungry.isLootSuppressed())
                    && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)
                    && KillBoard.INSTANCE.getGamePhase().isHardmode()
                    && level.getBiome(victim.blockPosition()).is(Tags.Biomes.IS_OCEAN)
                    && victim.getRandom().nextInt(100) == 0) {
                victim.spawnAtLocation(ConsumableItems.PIRATE_MAP.get());
            }
            TombstoneBoulderEntity.createTombstoneEntity(victim);
            Entity attacker = LibEntityUtils.getOwner(damageSource);

            if (attacker instanceof ServerPlayer) {
                if (victim instanceof Enemy &&
                        CommonConfigs.ENEMY_DROPS_MONEY.get() &&
                        level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) &&
                        !(victim instanceof OwnedSummon)
                ) ModUtils.enemyDropMoney(victim, level, damageSource);
                Bestiary.INSTANCE.updateEntry(victim, true);
            }
            if (attacker != null && attacker.getType().is(ModTags.EntityTypes.CORRUPT)) {
                NatureBlocks.DECOMPOSE_THE_SOURCE_EXTRACT_BLOCK.get().checkVisibilityAndSummonEntity(level, victim);
            }
            if (victim instanceof Boss boss && boss.shouldShowMessage()) {
                ModUtils.bossDeath(level, victim);
                SlimeRainGameEvent.INSTANCE.checkEnd(victim);
            }
            for (ServerPlayer player : level.players()) {
                if (player.position().distanceToSqr(victim.position()) > 32 * 32) continue;
                if (ManaStorage.of(player).canReceive() && player.getRandom().nextFloat() < 0.083F) {
                    LibEntityUtils.createItemEntity(DateUtils.getStarItem().getDefaultInstance(), victim.position(), level, 0);
                    break;
                }
            }
            if (victim instanceof BaseNPC npc) {
                NPCSpawner.INSTANCE.onNPCRemoved(npc);
                if (attacker != null && npc.getType() == NpcEntities.CLOTHIER.get() &&
                        attacker instanceof Player player &&
                        LibDateUtils.isNight(level) && // 晚上杀死才生成
                        TCUtils.hasType(player, AccessoryItems.CLOTHIER$KILLER)
                ) {
                    Skeletron skeletron = new Skeletron(BossEntities.SKELETRON.get(), level);
                    skeletron.finalizeSpawn(level, level.getCurrentDifficultyAt(skeletron.blockPosition()), MobSpawnType.EVENT, null);
                    ModUtils.summonBoss(level, attacker.blockPosition(), skeletron);
                }

                if (npc.getType() == NpcEntities.GUIDE.get() && level.dimension() == OverworldUtils.underworld() && damageSource.is(DamageTypes.LAVA)) {
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
    private static void dropMoneyOnDeath(LivingDeathEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof ServerPlayer player) {
            PlayerUtils.dropMoney(player);
        }
    }

    /// 在未取消的死亡结算后保留玩家灵魂，并处理击杀收魂。
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingDeathSoul(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim instanceof ServerPlayer player) {
            SoulStorage.of(player).onDeath();
        }
        PlayerUtils.collectSoul(victim, event.getSource());
        /// 附魂印记：击杀带印记的敌人额外回 2 点灵魂（普通收魂照常结算，这是叠加的一份）。
        if (victim.hasEffect(ModEffects.SOUL_MARK)
                && LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer killer) {
            PlayerUtils.adjustSoul(killer, SoulSkillParameters.SOUL_MARK_KILL_SOUL);
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
        if (damageSource.is(LibTags.DamageTypes.SOUL_OVERDRAFT)) {
            if (event.getEntity() instanceof ServerPlayer player) {
                SoulOverdraftEvent overdraftEvent = NeoForge.EVENT_BUS.post(new SoulOverdraftEvent(player, event.getAmount()));
                float amount = Math.max(0.0F, overdraftEvent.getAmount());
                if (overdraftEvent.isCanceled() || !player.isAlive() || amount <= 0.0F) {
                    event.setCanceled(true);
                    return;
                }

                event.setAmount(amount);
            }

            return;
        }

        if (EnemyDamageRules.blocks(event.getEntity(), event.getSource())) {
            event.setCanceled(true);
            return;
        }
        LivingEntity living = event.getEntity();

        applyBossDefinitionDamage(event, living, damageSource);

        if (living instanceof ServerPlayer player) {
            AccessoryItems.applyHurtGetMana(player, damageSource, event.getAmount());
        }
        if (Immunity.getCause(event.getSource()) != null) {
            event.getContainer().setPostAttackInvulnerabilityTicks(living.invulnerableTime);
        }
    }

    /// 祈灵之刃：向前贯穿扁矩形范围，主目标仍由原版结算，这里只补打其余敌怪。
    @SubscribeEvent
    public static void soulKnifeReach(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.getMainHandItem().is(SoulItems.SPIRITWARD_BLADE.get())) {
            return;
        }
        float scale = player.getAttackStrengthScale(0.5F);
        if (scale < SoulSkillParameters.SPIRITWARD_BLADE_MIN_CHARGE) {
            return;
        }
        Vec3 eye = player.getEyePosition();
        AABB box = new AABB(eye, eye.add(player.getLookAngle().scale(SoulSkillParameters.SPIRITWARD_BLADE_REACH)))
                .inflate(SoulSkillParameters.SPIRITWARD_BLADE_HALF_WIDTH, SoulSkillParameters.SPIRITWARD_BLADE_HALF_HEIGHT, SoulSkillParameters.SPIRITWARD_BLADE_HALF_WIDTH);
        Entity primary = event.getTarget();
        float damage = (float) player.getAttributeValue(LibAttributes.getAttackDamage()) * (0.2F + scale * scale * 0.8F);
        DamageSource source = player.damageSources().playerAttack(player);
        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, box,
                entity -> entity != player && entity != primary && entity.isAlive() && !entity.isSpectator() && player.canAttack(entity))) {
            target.hurt(source, damage);
        }
    }

    /// 祈灵之刃：当前灵魂高于阈值时，持有者造成的近战伤害额外提升。
    @SubscribeEvent
    public static void soulKnifeBonusDamage(LivingIncomingDamageEvent event) {
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer player)
                || !player.getMainHandItem().is(SoulItems.SPIRITWARD_BLADE.get())) {
            return;
        }
        if (PlayerUtils.getSoul(player) > SoulSkillParameters.SPIRITWARD_BLADE_SOUL_THRESHOLD) {
            event.setAmount(event.getAmount() + SoulSkillParameters.SPIRITWARD_BLADE_DAMAGE_BONUS);
        }
    }

    /// 祈灵之刃：用它击杀的敌怪额外收取灵魂。
    @SubscribeEvent
    public static void soulKnifeExtraSoul(SoulCollectionEvent event) {
        if (event.getEntity().getMainHandItem().is(SoulItems.SPIRITWARD_BLADE.get())) {
            event.setAmount(event.getAmount() + SoulSkillParameters.SPIRITWARD_BLADE_KILL_SOUL_BONUS);
        }
    }

    /// 灵魂短弓 / 灵魂弓：当前灵魂高于阈值时，由它射出的箭矢额外提升伤害。
    @SubscribeEvent
    public static void soulBowBonusDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)
                || !(arrow.getOwner() instanceof ServerPlayer player)
                || !(ISoulBow.isSoulBow(player.getMainHandItem()) || ISoulBow.isSoulBow(player.getOffhandItem()))) {
            return;
        }
        if (PlayerUtils.getSoul(player) > SoulSkillParameters.SOUL_BOW_SOUL_THRESHOLD) {
            event.setAmount(event.getAmount() + SoulSkillParameters.SOUL_BOW_DAMAGE_BONUS);
        }
    }

    /// 灵魂短弓 / 灵魂弓：用它击杀的敌怪额外收取灵魂。
    @SubscribeEvent
    public static void soulBowExtraSoul(SoulCollectionEvent event) {
        Player player = event.getEntity();
        if (ISoulBow.isSoulBow(player.getMainHandItem()) || ISoulBow.isSoulBow(player.getOffhandItem())) {
            event.setAmount(event.getAmount() + SoulSkillParameters.SOUL_BOW_KILL_SOUL_BONUS);
        }
    }

    /// 束魂锁链 / 手环系列（灵魂手环、灵魂再生手环、通灵手环共用 `band_of_soul` 标记）/ 刻骨的记忆：
    /// 每佩戴一种，每次收魂额外 +1 灵魂。
    ///
    /// 三种手环共用同一个 `ValueType.UnitType`，因此戴多只也只算一份加成。
    @SubscribeEvent
    public static void soulCollectionAccessoryBonus(SoulCollectionEvent event) {
        Player player = event.getEntity();
        float bonus = 0.0F;
        if (TCUtils.hasType(player, AccessoryItems.SOUL$BINDING$CHAIN)) {
            bonus += SoulSkillParameters.SOUL_COLLECTION_ACCESSORY_BONUS;
        }
        if (TCUtils.hasType(player, AccessoryItems.BAND$OF$SOUL)) {
            bonus += SoulSkillParameters.SOUL_COLLECTION_ACCESSORY_BONUS;
        }
        if (TCUtils.hasType(player, AccessoryItems.MEMORY$OF$BONEBRAND)) {
            bonus += SoulSkillParameters.SOUL_COLLECTION_ACCESSORY_BONUS;
        }
        if (bonus > 0.0F) {
            event.setAmount(event.getAmount() + bonus);
        }
    }

    /// 祈灵之刃 / 灵魂弓 / 灵魂短弓：攻击命中敌怪时有 50% 概率额外获得 1 点灵魂。
    @SubscribeEvent
    public static void soulWeaponHitSoul(LivingIncomingDamageEvent event) {
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer player) || event.getEntity() == player) {
            return;
        }
        boolean soulKnife = player.getMainHandItem().is(SoulItems.SPIRITWARD_BLADE.get());
        boolean soulBow = ISoulBow.isSoulBow(player.getMainHandItem()) || ISoulBow.isSoulBow(player.getOffhandItem());
        if (!soulKnife && !soulBow) {
            return;
        }
        if (!PlayerUtils.isSoulMode(player) || player.getRandom().nextFloat() >= SoulSkillParameters.SOUL_WEAPON_HIT_SOUL_CHANCE) {
            return;
        }
        PlayerUtils.adjustSoul(player, SoulSkillParameters.SOUL_WEAPON_HIT_SOUL);
    }

    /// 主武器自带效果里「技能命中」的那几条：星魄补一颗坠星、丛林的灯附带中毒、无分昼夜附带暗影焰；
    /// 另外统一结算**附魂印记**的回魂：技能命中带印记的敌人回 1 魂。
    ///
    /// 只认灵魂伤害来源，因此普通近战 / 弓箭不会触发。
    @SubscribeEvent
    public static void soulWeaponTraitsOnSkillHit(LivingIncomingDamageEvent event) {
        if (!event.getSource().is(LibDamageTypes.SOUL)) {
            return;
        }
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (victim == player || !victim.isAlive()) {
            return;
        }
        /// 附魂印记：这只敌人身上有印记，任何技能（灵魂伤害）打中它都回 1 魂。
        if (victim.hasEffect(ModEffects.SOUL_MARK)) {
            PlayerUtils.adjustSoul(player, SoulSkillParameters.SOUL_MARK_HIT_SOUL);
        }
        if (PlayerUtils.holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.STAR_ON_SKILL_HIT)
                && !isStarProjectile(event)
                && player.getRandom().nextFloat() < SoulSkillParameters.STAR_SPIRIT_STAR_CHANCE) {
            Vec3 center = victim.getBoundingBox().getCenter();
            float damage = PlayerUtils.getSoulSkillDamage(player, ModSoulSkills.STAR_CALL.get().getStack());
            SoulLureProjectile star = SoulLureProjectile.star(player,
                    new Vec3(center.x, PlayerUtils.starCallSpawnY(player.level(), victim), center.z),
                    new Vec3(0.0, -1.0, 0.0), damage, 2.0F, 0.0);
            player.level().addFreshEntity(star);
        }
        if (PlayerUtils.holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.POISON_ON_SKILL_HIT)) {
            victim.addEffect(new MobEffectInstance(MobEffects.POISON, SoulSkillParameters.LIFE_SPLENDOR_POISON_TICKS, 0, false, true, true));
        }
        if (PlayerUtils.holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.SHADOWFLAME_ON_SKILL_HIT)) {
            victim.addEffect(new MobEffectInstance(ModEffects.SHADOWFLAME, SoulSkillParameters.DAY_AND_NIGHT_ALIKE_SHADOWFLAME_TICKS, 0, false, true, true));
        }
    }

    /// 星魄的坠星本身也是灵魂伤害：它命中时不再触发星魄，避免自我循环。
    private static boolean isStarProjectile(LivingIncomingDamageEvent event) {
        return event.getSource().getDirectEntity() instanceof SoulLureProjectile lure && lure.isStarAppearance();
    }

    /// 残破微光：手持时获得灵魂，有 20% 概率回复 4 点生命。
    ///
    /// 「获得灵魂」走收魂事件（击杀结算），灵魂再生与灵魂手环的缓慢回复不受它影响。
    @SubscribeEvent
    public static void brokenGlimmerHeal(SoulCollectionEvent event) {
        Player player = event.getEntity();
        if (!PlayerUtils.holdsSoulWeaponTrait(player, SoulWeaponItem.Trait.HEAL_ON_SOUL_GAIN)) {
            return;
        }
        if (player.getRandom().nextFloat() < SoulSkillParameters.BROKEN_GLIMMER_HEAL_CHANCE) {
            player.heal(SoulSkillParameters.BROKEN_GLIMMER_HEAL_AMOUNT);
        }
    }

    /// 逐灵全套：当前灵魂高于阈值时造成的伤害提高。
    @SubscribeEvent
    public static void seekerSetDamage(LivingIncomingDamageEvent event) {
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer player)
                || !PlayerUtils.isWearingSeekerSet(player)
                || PlayerUtils.getSoul(player) <= SoulSkillParameters.SEEKER_DAMAGE_SOUL_THRESHOLD) {
            return;
        }
        event.setAmount(event.getAmount() * (1.0F + SoulSkillParameters.SEEKER_DAMAGE_BONUS));
    }

    /// 魂师套装：逐件与套装的灵魂掉落加成统一汇总，幻翼套在夜间再加。
    @SubscribeEvent
    public static void soulDropBonus(SoulCollectionEvent event) {
        Player player = event.getEntity();
        float bonus = ModArmorBonus.getValue(player, ModArmorBonus.SOUL$DROP);
        if (PlayerUtils.isWearingPhantomSet(player) && player.level().isNight()) {
            bonus += SoulSkillParameters.PHANTOM_NIGHT_SOUL_DROP_BONUS;
        }
        if (bonus > 0.0F) {
            event.setAmount(event.getAmount() + bonus);
        }
        PlayerUtils.onSoulCollected(player, player.level().getGameTime());
    }

    /// 魂舞全套：收集灵魂后的限时减伤。
    @SubscribeEvent
    public static void souldancerDamageReduction(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.level().getGameTime() < SoulStorage.of(player).getSouldancerBuffEnd()) {
            event.setAmount(event.getAmount() * (1.0F - SoulSkillParameters.SOULDANCER_DAMAGE_REDUCTION));
        }
    }

    /// 魂师召唤物被动：召唤物命中时概率回魂、耗魂换取增伤。
    @SubscribeEvent
    public static void soulSkillSummonDamage(LivingIncomingDamageEvent event) {
        if (!(LibEntityUtils.getOwner(event.getSource()) instanceof ServerPlayer owner)) {
            return;
        }
        Entity direct = event.getSource().getDirectEntity();
        if (direct == owner || event.getEntity() == owner) {
            return;
        }
        if (PlayerUtils.isPassiveSoulSkillActive(owner, ModSoulSkills.SOUL_DEVOUR.get())
                && PlayerUtils.consumeSoul(owner, SoulSkillParameters.SOUL_DEVOUR_SOUL_COST)) {
            event.setAmount(event.getAmount() * (1.0F + SoulSkillParameters.SOUL_DEVOUR_DAMAGE_BONUS));
        }
        if (PlayerUtils.isPassiveSoulSkillActive(owner, ModSoulSkills.SOUL_HARVEST.get())
                && owner.getRandom().nextFloat() < SoulSkillParameters.SOUL_HARVEST_CHANCE) {
            PlayerUtils.adjustSoul(owner, SoulSkillParameters.SOUL_HARVEST_SOUL);
        }
    }

    /// 血液沸腾：把下一次近战攻击放大并消耗掉增益。
    @SubscribeEvent
    public static void boilingBloodHit(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof ServerPlayer player) || !player.hasEffect(ModEffects.BOILING_BLOOD)) {
            return;
        }
        event.setAmount(event.getAmount() * (1.0F + SoulSkillParameters.BOILING_BLOOD_DAMAGE_MULTIPLIER));
        player.removeEffect(ModEffects.BOILING_BLOOD);
    }

    /// 灵魂缓冲把摔落伤害改成回复；生命归还记录期间失去的生命。
    @SubscribeEvent
    public static void soulSkillOnHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.IS_FALL) && player.hasEffect(ModEffects.SOUL_BUFFER)) {
            player.heal(event.getAmount() * SoulSkillParameters.SOUL_BUFFER_CONVERT_RATIO);
            event.setCanceled(true);
            return;
        }
        if (player.hasEffect(ModEffects.LIFE_RETURN)) {
            SoulStorage storage = SoulStorage.of(player);
            storage.setLifeReturnLost(storage.getLifeReturnLost() + event.getAmount());
        }
    }

    /// 血怒：击杀额外收魂。
    @SubscribeEvent
    public static void bloodRageSoul(SoulCollectionEvent event) {
        if (event.getEntity().hasEffect(ModEffects.BLOOD_RAGE)) {
            event.setAmount(event.getAmount() + SoulSkillParameters.BLOOD_RAGE_KILL_SOUL_BONUS);
        }
    }
    /// 对所有能追溯到 Boss 本体的伤害统一应用数据包倍率。
    ///
    /// 该入口位于护甲、抗性和暴击等后续减伤之前，因此普通近战、固定接触伤害以及
    /// Boss 所有的弹幕共享完全相同的基础伤害倍率，不需要每个攻击类重复读取 JSON。
    private static void applyBossDefinitionDamage(LivingIncomingDamageEvent event, LivingEntity victim, DamageSource source) {
        Entity attacker = LibEntityUtils.getOwner(source);
        if (attacker instanceof PartHitTarget part) {
            attacker = part.encounterOwner();
        }
        if (attacker instanceof BossOwnedEntity owned) {
            attacker = owned.getBossOwner();
        }
        if (!(attacker instanceof BaseBoss boss) || boss == victim) return;

        double multiplier = CreatureDefinition.get(boss.getType()).boss().damageMultiplierOr(1.0D) * CommonConfigs.BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE.get();
        if (multiplier != 1.0D) {
            event.setAmount((float) Math.max(0.0D, event.getAmount() * multiplier));
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
        /// 芦苇呼吸管对溺水伤害减半
        if (damageSource.is(DamageTypes.DROWN) && LibEntityUtils.anyHandHasItem(victim, SwordItems.BREATHING_REED.get())) {
            amount *= 0.5F;
        }
        amount = SwordItems.processEffect(damageSource, attacker, victim, amount);
        if (victim.getVehicle() instanceof AbstractMountEntity mount) {
            amount = mount.modifyRiderDamage(damageSource, amount);
        }
        event.setNewDamage(amount);
    }

    @SubscribeEvent
    public static void livingDamage$Post(LivingDamageEvent.Post event) {
        float amount = event.getNewDamage();
        if (amount <= 0.0F) return; // 防止莫名的负数伤害
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level)) return;
        DamageSource damageSource = event.getSource();
        SwordItems.afterSuccessfulDamage(damageSource, damageSource.getEntity(), victim);
        AttackEffects.afterDamage(victim, damageSource);
        if (victim instanceof Mob mob && EnemyTargeting.isConfluenceEnemy(mob)) {
            LivingEntity retaliateAgainst = EnemyTargeting.attacker(damageSource);
            if (retaliateAgainst != null && EnemyTargeting.allowsTarget(mob, retaliateAgainst)) {
                mob.setLastHurtByMob(retaliateAgainst);
                if (EnemyTargeting.applies(mob))
                    mob.setTarget(EnemyTargeting.select(mob, retaliateAgainst));
                if (mob instanceof BossOwnedEntity dependent && dependent.getBossOwner() != null)
                    dependent.getBossOwner().onEncounterHurt(damageSource);
            }
        }
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) {
            return;
        }
        // 在效果集合开始遍历之前反伤；套装命中奖励可安全增删玩家效果。
        ThornsEffect.onMobHurt(victim, damageSource, amount);
        @Nullable Entity attacker = damageSource.getEntity();

        if (attacker instanceof Decayeder decayeder) decayeder.onDamageDealt(level, victim, damageSource);

        FlaskEffect.onLivingDamage(victim, attacker, damageSource, amount);
        Immunity.calculateInvTicks(damageSource, victim);
        DamageIndicatorOptions.sendDamageParticle(level, damageSource, amount, victim);
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
        Entity directEntity = event.getDamageSource().getDirectEntity();
        if (directEntity instanceof BeeKeeperProjectile bee) {
            event.setCritical(bee.getRandom().nextFloat() < bee.getCriticalChance());
        } else if (directEntity instanceof YoyoEntity yoyo) {
            event.setCritical(yoyo.getRandom().nextFloat() < yoyo.getCriticalChance());
            if (!yoyo.isCounterweight() && yoyo.getYoyoItem() != null) {
                event.setCriticalDamageMultiplier(yoyo.getYoyoItem().criticalDamageMultiplier());
            }
        } else if (directEntity instanceof BaseYoyoProjectile shot) {
            event.setCritical(shot.getRandom().nextFloat() < shot.getCriticalChance());
        } else if (directEntity instanceof TerrarianProjectile shot) {
            event.setCritical(shot.getRandom().nextFloat() < shot.getCriticalChance());
        } else if (event.getDamageSource().getEntity() instanceof ServerPlayer player) {
            StarSteelSword.processCriticalDamage(player, event.isCritical(), event::setCriticalDamageMultiplier);
            /// 白南瓜全套：近战暴击回复灵魂
            if (event.isCritical() && event.getDamageSource().getDirectEntity() == player
                    && PlayerUtils.isWearingWhitePumpkinSet(player)) {
                SoulStorage.of(player).receive(SoulSkillParameters.WHITE_PUMPKIN_CRIT_SOUL);
            }
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

        if (event.getEntity() instanceof Player player) {
            Object2IntMap<Holder<MobEffect>> value = ModArmorBonus.getValue(player, ModArmorBonus.ENHANCE$EFFECT$DURATION);
            int extraDuration = value.getInt(instance.getEffect());
            instance.duration += extraDuration;
        }
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
        if (event.getSlot() == EquipmentSlot.MAINHAND && event.getFrom().getItem() instanceof BaseGun gun) {
            gun.putAwayAnimator(event.getFrom(), player);
        }
        if (event.getSlot() == EquipmentSlot.MAINHAND && event.getTo().getItem() instanceof BaseGun gun) {
            gun.pickAnimator(event.getTo(), player);
        }
        AchievementUtils.matchingAttire_fashionStatement(event.getSlot().getType(), player);
        if (event.getSlot().getType() == EquipmentSlot.Type.HAND) {
            VisibilityPacketS2C.sendSignal(player, event.getTo().is(ModTags.Items.SHOW_SIGNAL));
        } else if (event.getSlot() == EquipmentSlot.HEAD) {
            VisibilityPacketS2C.sendSunglasses(player, event.getTo().is(VanityArmorItems.SUNGLASSES) ? TriState.TRUE : TriState.FALSE, TriState.DEFAULT);
            /// 归巢蜜蜂：第一次戴到头上时一次性绑定最近的蜂巢（与地牢指南针一样，坐标只绑定一次、之后不再搜索）
            if (event.getTo().getItem() instanceof HomingBeeItem) {
                HomingBeeItem.bindHiveTarget(player, player.getItemBySlot(EquipmentSlot.HEAD));
            }
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
        if (LibMathUtils.checkChance(0.011F, living.getRandom())) dropsHolidayGift:{ // 掉落节日礼物
            Item holidayGift = DateUtils.getHolidayGift(living.getRandom());
            if (holidayGift == Items.AIR) break dropsHolidayGift;
            ItemEntity entity = new ItemEntity(level, x, y, z, holidayGift.getDefaultInstance());
            entity.setNoPickUpDelay();
            drops.add(entity);
        }
        boolean isEnemy = living instanceof Enemy;
        if (KillBoard.INSTANCE.getGamePhase().isHardmode() &&
                isEnemy &&
                !(living instanceof OwnedSummon) &&
                !living.getType().is(ModTags.EntityTypes.DO_NOT_DROPS_EVIL_SOUL) &&
                (y < OverworldUtils.getUndergroundY() || ModSecretSeeds.DONT_DIG_UP.match(level) || ModSecretSeeds.GET_FIXED_BOI.match(level)) &&
                living.getRandom().nextFloat() < (LibUtils.isAtLeastExpert(level) ? 0.36F : 0.2F)
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
            drops.add(new ItemEntity(level, x, y, z, YoyoItems.CASCADE.toStack()));
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
        } else if (LibEntityUtils.anyHandHasItem(living, itemStack -> !itemStack.isEmpty() && itemStack.is(SwordItems.BREATHING_REED))) {
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
                Mob replacement = replaceSpawn(level, mob, (pink ? MonsterEntities.PINK_FROZEN_ZOMBIE : MonsterEntities.FROZEN_ZOMBIE).get());
                Mob equipped = replacement == null ? mob : replacement;
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.HEAD, (pink ? ArmorItems.PINK_SNOW_CAPS : ArmorItems.SNOW_CAPS).get(), 0.003F);
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.CHEST, (pink ? ArmorItems.PINK_SNOW_SUITS : ArmorItems.SNOW_SUITS).get(), 0.003F);
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.LEGS, (pink ? ArmorItems.PINK_INSULATED_PANTS : ArmorItems.INSULATED_PANTS).get(), 0.003F);
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.FEET, (pink ? ArmorItems.PINK_INSULATED_SHOES : ArmorItems.INSULATED_SHOES).get(), 0.003F);
                event.setCanceled(true);
                if (replacement != null && level.addFreshEntity(replacement)) {
                    event.setSpawnCancelled(true);
                    return;
                }
            } else if (ModUtils.isRainingAt(level, blockPos)) {
                Mob replacement = replaceSpawn(level, mob, MonsterEntities.RAINCOAT_ZOMBIE.get());
                Mob equipped = replacement == null ? mob : replacement;
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.HEAD, ArmorItems.RAIN_CAP.get(), 0.003F);
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.CHEST, ArmorItems.RAINCOAT.get(), 0.003F);
                event.setCanceled(true);
                if (replacement != null && level.addFreshEntity(replacement)) {
                    event.setSpawnCancelled(true);
                    return;
                }
            } else if (mob.getRandom().nextFloat() < SLIME_ZOMBIE_CHANCE) {
                Mob replacement = replaceSpawn(level, mob, MonsterEntities.SLIME_ZOMBIE.get());
                if (replacement != null && level.addFreshEntity(replacement)) {
                    event.setSpawnCancelled(true);
                    return;
                }
            }
        } else if (type == EntityType.SKELETON) {
            DifficultyInstance difficulty = event.getDifficulty();
            if (!level.canSeeSky(BlockPos.containing(event.getX(), event.getY(), event.getZ())) && mob.getRandom().nextFloat() < 0.01F) {
                Mob replacement = replaceSpawn(level, mob, MonsterEntities.UNDEAD_MINER.get());
                Mob equipped = replacement == null ? mob : replacement;
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.HEAD, ArmorItems.MINING_HELMET.get(), 1.0F);
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.CHEST, ArmorItems.MINING_CHESTPLATE.get(), 1.0F);
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.LEGS, ArmorItems.MINING_LEGGINGS.get(), 1.0F);
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.FEET, ArmorItems.MINING_BOOTS.get(), 1.0F);
                LibEntityUtils.setItemAndDropChance(equipped, difficulty, EquipmentSlot.MAINHAND, PickaxeItems.BONE_PICKAXE.get(), 0.25F);
                event.setCanceled(true);
                if (replacement != null && level.addFreshEntity(replacement)) {
                    event.setSpawnCancelled(true);
                    return;
                }
            }
        } else if (event.getSpawnType() == MobSpawnType.NATURAL && mob.getType().is(ModTags.EntityTypes.GOLDEN_SLIME_REPLACEABLE)) {
            if ((ModSecretSeeds.CELEBRATIONMK10.match() || ModSecretSeeds.GET_FIXED_BOI.match()) && mob.getRandom().nextInt(180) == 0) {
                GoldenSlime goldenSlime = MonsterEntities.GOLDEN_SLIME.get().create(level);
                if (goldenSlime != null) {
                    goldenSlime.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
                    if (level.addFreshEntity(goldenSlime)) event.setCanceled(true);
                }
            }
        } else if (type == ModEntities.INVERSE_ENDERMAN.get()) {
            mob.moveTo(mob.getX(), mob.getY() - mob.getBbHeight(), mob.getZ());
        }

        if (!event.isCanceled()) {
            GamePhase2AttributeModifiers.applyModifiers(mob);
            if (mob instanceof Boss boss && boss.isMainBody() && boss.shouldEnhanceMultiplayer()) {
                BossMultiplayerEnhancement.apply(mob, true);
            }
        }
    }

    /// 把即将生成的生物替换成独立类型；位置、朝向与速度保持一致。
    ///
    /// 返回 null 表示创建失败，调用方需要退回到直接修改原生物的做法。
    private static <T extends Mob> @Nullable T replaceSpawn(ServerLevel level, Mob original, EntityType<T> replacementType) {
        T replacement = replacementType.create(level);
        if (replacement == null) return null;
        replacement.moveTo(original.getX(), original.getY(), original.getZ(), original.getYRot(), original.getXRot());
        replacement.setYHeadRot(original.getYHeadRot());
        replacement.yBodyRot = original.yBodyRot;
        replacement.setDeltaMovement(original.getDeltaMovement());
        return replacement;
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
    public static void mobDespawn(MobDespawnEvent event) {
        Mob mob = event.getEntity();
        if (event.getResult() != MobDespawnEvent.Result.DEFAULT || !mob.isAlive()
                || mob.getType().getCategory() != MobCategory.MONSTER
                || mob.hasCustomName() || mob instanceof Boss
                || mob instanceof BossOwnedEntity owned && owned.getBossOwner() != null
                || !mob.removeWhenFarAway(Double.POSITIVE_INFINITY)
                || !mob.level().players().isEmpty()) {
            return;
        }
        if (mob.getNoActionTime() > 600 && mob.getRandom().nextInt(800) == 0)
            event.setResult(MobDespawnEvent.Result.ALLOW);
    }

    @SubscribeEvent
    public static void mobSpawn$PositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getSpawnType() != MobSpawnType.NATURAL) return;
        Mob mob = event.getEntity();
        if (event.getResult() == MobSpawnEvent.PositionCheck.Result.FAIL) return;
        if (DungeonStructure.skipSpawn(mob, event.getLevel().getLevel()) || GameEventSystem.shouldDenyNatureSpawn() || TownPopulation.suppressesSpawn(event.getLevel().getLevel(), mob)) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
            return;
        }
        if (mob.getType().is(ModTags.EntityTypes.SPAWN_AT_GRAVEYARD)
                && ModBlockCounters.isGraveyard(event.getLevel(), mob.blockPosition())
                && mob.checkSpawnObstruction(event.getLevel())) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.SUCCEED);
        }
    }

    @SubscribeEvent
    public static void mobSpawn$SpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if ((event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION)
                && !DevelopmentSpawnPolicy.allowsAutomaticSpawn(event.getEntityType())) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
            return;
        }
        if (event.getResult() == MobSpawnEvent.SpawnPlacementCheck.Result.FAIL) return;
        if (event.getSpawnType() == MobSpawnType.NATURAL && !event.getPlacementCheckResult()) {
            EntityType<?> entityType = event.getEntityType();
///            if (entityType == MonsterEntities.GHOST.get()) {
///                ILevelChunkSection iSection = DynamicBiomeUtils.getISection(event.getLevel(), event.getPos());
///                event.setResult(iSection != null && iSection.confluence$isGraveyard()
///                        ? MobSpawnEvent.SpawnPlacementCheck.Result.SUCCEED
///                        : MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
///            } else
            if (entityType.is(ModTags.EntityTypes.SPAWN_AT_GRAVEYARD)) {
                if (ModBlockCounters.isGraveyard(event.getLevel(), event.getPos())) {
                    event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.SUCCEED);
                }
            }
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
        if (living instanceof OwnedSummon) {
            event.setCanceled(true);
        } else {
            EntityType<?> type = living.getType();
            if (type.is(ModTags.EntityTypes.BESTIARY_BLACKLIST)) {
                event.setCanceled(true);
            } else if (type == BossEntities.SKELETRON_HAND.get()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void armorPenetration(ArmorPenetrationEvent event) {
        DamageSource damageSource = event.getDamageSource();
        if (damageSource instanceof WhipDamageSource whipSource) {
            event.setPenetration(event.getPenetration() + whipSource.armorPenetration());
        }

        @Nullable Entity direct = damageSource.getDirectEntity();
        if (direct != null && direct.getType() == ModEntities.CRYSTAL_VILE_SHARD.get()) {
            event.setPenetration(event.getPenetration() + CrystalVileShardItem.ARMOR_PENETRATION);
        }
        if (direct instanceof org.confluence.mod.common.entity.projectile.sword.GrassSwordProjectile) {
            event.setPenetration(event.getPenetration() + 20.0F);
        } else if (direct instanceof org.confluence.mod.common.entity.projectile.sword.LightBaneProjectile) {
            event.setPenetration(event.getPenetration() + 5.0F);
        } else if (direct instanceof org.confluence.mod.common.entity.projectile.sword.MuramasaSlashProjectile) {
            event.setPenetration(event.getPenetration() + 5.0F);
        }

        if (damageSource.getEntity() instanceof LivingEntity living &&
                damageSource.is(LibTags.DamageTypes.AS_MELEE_ATTACK) &&
                living.hasEffect(ModEffects.SHARPENED)
        ) {
            event.setPenetration(event.getPenetration() + 12);
        }
    }
}
