package org.confluence.mod.common.init.entity;

import com.google.common.collect.Iterables;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.confluence.lib.common.entitiy.EmptyEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.*;
import org.confluence.mod.common.entity.fishing.BaseFishingHook;
import org.confluence.mod.common.entity.fishing.BloodyFishingHook;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.entity.fishing.HotlineFishingHook;
import org.confluence.mod.common.entity.flail.*;
import org.confluence.mod.common.entity.hook.*;
import org.confluence.mod.common.entity.minecart.*;
import org.confluence.mod.common.entity.monster.CreatureAttributeBuilder;
import org.confluence.mod.common.entity.mount.RideableBeeMountEntity;
import org.confluence.mod.common.entity.mount.RideableLavaSharkMountEntity;
import org.confluence.mod.common.entity.mount.RideableSlimeMountEntity;
import org.confluence.mod.common.entity.mount.RideableUnicornMountEntity;
import org.confluence.mod.common.entity.projectile.*;
import org.confluence.mod.common.entity.projectile.arrow.*;
import org.confluence.mod.common.entity.projectile.bomb.*;
import org.confluence.mod.common.entity.projectile.boulder.*;
import org.confluence.mod.common.entity.projectile.flail.DripplerCripplerProjectile;
import org.confluence.mod.common.entity.projectile.flail.FlaironBubbleProjectile;
import org.confluence.mod.common.entity.projectile.flail.FlowerPowerPetalProjectile;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.spear.*;
import org.confluence.mod.common.entity.projectile.strip.CrystalVileShardProjectile;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.entity.projectile.sword.*;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.entity.soulcerer.SoulStingProjectile;
import org.confluence.mod.common.entity.soulcerer.SpiritSurgeEntity;
import org.confluence.mod.common.entity.storage.ChesterEntity;
import org.confluence.mod.common.entity.storage.FlyingPiggyBankEntity;
import org.confluence.mod.common.entity.yoyo.*;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.integration.sable.SableHelper;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Confluence.MODID);
    private static final Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<CreatureAttributeBuilder.Definition>> ATTRIBUTES = new LinkedHashMap<>();
    private static final Map<EntityType<?>, CreatureAttributeBuilder.Definition> CREATURE_DEFINITIONS = new LinkedHashMap<>();

    /// 牢枕专用
    public static final DeferredHolder<EntityType<?>, EntityType<EmptyEntity>> EMPTY_ENTITY = registerEntityType("empty_entity", "空实体", id -> EntityType.Builder.of(EmptyEntity::new, MobCategory.MISC).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<MartianReinforcement>> MARTIAN_REINFORCEMENT = registerEntityType("martian_reinforcement", "火星支援信标", id -> EntityType.Builder.of(MartianReinforcement::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MartianElectricBolt>> MARTIAN_ELECTRIC_BOLT = registerEntityType("martian_electric_bolt", "火星电击束", id -> EntityType.Builder.of(MartianElectricBolt::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ElectricSpear>> ELECTRIC_SPEAR = registerEntityType("electric_spear", "电矛", id -> EntityType.Builder.of(ElectricSpear::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MonsterLaser>> MONSTER_LASER = registerEntityType("monster_laser", "怪物激光", id -> EntityType.Builder.of(MonsterLaser::new, MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(16).updateInterval(1).build(id.toString()));

    /// 炸弹
    public static final DeferredHolder<EntityType<?>, EntityType<BaseBombEntity>> BOMB_ENTITY = registerBomb("bomb_entity", "炸弹", BaseBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BouncyBombEntity>> BOUNCY_BOMB_ENTITY = registerBomb("bouncy_bomb_entity", "弹力炸弹", BouncyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<ScarabBombEntity>> SCARAB_BOMB_ENTITY = registerBomb("scarab_bomb_entity", "圣甲虫炸弹", ScarabBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyBombEntity>> STICKY_BOMB_ENTITY = registerBomb("sticky_bomb_entity", "黏性炸弹", StickyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<SmokeBombEntity>> SMOKE_BOMB_ENTITY = registerBomb("smoke_bomb_entity", "烟雾弹", SmokeBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BombFishEntity>> BOMB_FISH_ENTITY = registerBomb("bomb_fish_entity", "炸弹鱼", BombFishEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseGrenadeEntity>> GRENADE = registerBomb("grenade", "手雷", BaseGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BouncyGrenadeEntity>> BOUNCY_GRENADE = registerBomb("bouncy_grenade", "弹力手雷", BouncyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyGrenadeEntity>> STICKY_GRENADE = registerBomb("sticky_grenade", "黏性手雷", StickyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BeenadeEntity>> BEENADE = registerBomb("beenade", "蜜蜂手雷", BeenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseDynamiteEntity>> DYNAMITE = registerBomb("dynamite", "炸药", BaseDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BouncyDynamiteEntity>> BOUNCY_DYNAMITE = registerBomb("bouncy_dynamite", "弹力炸药", BouncyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyDynamiteEntity>> STICKY_DYNAMITE = registerBomb("sticky_dynamite", "黏性炸药", StickyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseDirtBombEntity>> DIRT_BOMB = registerBomb("dirt_bomb", "土炸弹", BaseDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyDirtBombEntity>> STICKY_DIRT_BOMB = registerBomb("sticky_dirt_bomb", "粘性土炸弹", StickyDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<DryBombEntity>> DRY_BOMB = registerBomb("dry_bomb", "干炸弹", DryBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<LiquidBombEntity>> WET_BOMB = registerBomb("wet_bomb", "湿炸弹", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<LiquidBombEntity>> LAVA_BOMB = registerBomb("lava_bomb", "熔岩炸弹", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<LiquidBombEntity>> HONEY_BOMB = registerBomb("honey_bomb", "蜂蜜炸弹", LiquidBombEntity::new, BaseBombEntity.DIAMETER);

    public static final DeferredHolder<EntityType<?>, EntityType<SoulLureProjectile>> SOUL_LURE_PROJECTILE = registerEntityType("soul_lure_projectile", "引魂弹幕", id -> EntityType.Builder.<SoulLureProjectile>of(SoulLureProjectile::new, MobCategory.MISC).sized(SoulSkillParameters.SOUL_LURE_ENTITY_SIZE, SoulSkillParameters.SOUL_LURE_ENTITY_SIZE).clientTrackingRange(SoulSkillParameters.SOUL_ENTITY_TRACKING_RANGE).updateInterval(SoulSkillParameters.SOUL_ENTITY_UPDATE_INTERVAL).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiritSurgeEntity>> SPIRIT_SURGE = registerEntityType("spirit_surge", "灵魂激荡", id -> EntityType.Builder.<SpiritSurgeEntity>of(SpiritSurgeEntity::new, MobCategory.MISC).sized(SoulSkillParameters.SOUL_ENTITY_SIZE, SoulSkillParameters.SOUL_ENTITY_SIZE).clientTrackingRange(SoulSkillParameters.SOUL_ENTITY_TRACKING_RANGE).updateInterval(SoulSkillParameters.SOUL_ENTITY_UPDATE_INTERVAL).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SoulStingProjectile>> SOUL_STING = registerEntityType("soul_sting", "魂师毒刺", id -> EntityType.Builder.<SoulStingProjectile>of(SoulStingProjectile::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(8).updateInterval(1).noSave().build(id.toString()));

    /// 魔法
    public static final DeferredHolder<EntityType<?>, EntityType<BaseManaStaffProjectileEntity>> BASE_MANA_STAFF = registerEntityType("base_mana_staff", "法杖射弹", id -> EntityType.Builder.<BaseManaStaffProjectileEntity>of(BaseManaStaffProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<VilethronProjectile>> VILETHRON = registerEntityType("vilethron", "魔刺射弹", id -> EntityType.Builder.<VilethronProjectile>of(VilethronProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalVileShardProjectile>> CRYSTAL_VILE_SHARD = registerEntityType("crystal_vile_shard", "魔晶碎块射弹", id -> EntityType.Builder.<CrystalVileShardProjectile>of(CrystalVileShardProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HurtnadoProjectile>> HURTNADO = registerEntityType("hurtnado", "痛苦飓风", id -> EntityType.Builder.<HurtnadoProjectile>of(HurtnadoProjectile::new, MobCategory.MISC).sized(0.8F, 1.2F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<WaterStreamProjectile>> WATER_STREAM = registerEntityType("water_stream", "水流射弹", id -> EntityType.Builder.<WaterStreamProjectile>of(WaterStreamProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<WaterBoltProjectile>> WATER_BOLT = registerEntityType("water_bolt", "水弹射弹", id -> EntityType.Builder.<WaterBoltProjectile>of(WaterBoltProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<OpalStaffProjectile>> OPAL_STAFF_PROJECTILE = registerEntityType("opal_staff_projectile", "欧泊洪流弹幕", id -> EntityType.Builder.<OpalStaffProjectile>of(OpalStaffProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(10).build(id.toString()));
    /// 昏晓的弹幕。
    public static final DeferredHolder<EntityType<?>, EntityType<DuskDawnProjectile>> DUSK_DAWN = registerEntityTypeWithEnglishName("dusk_dawn", "昏晓弹幕", "Dusk and Dawn Projectile", id -> EntityType.Builder.<DuskDawnProjectile>of(DuskDawnProjectile::new, MobCategory.MISC).sized(0.8F, 0.8F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    /// 风页的风弹。
    public static final DeferredHolder<EntityType<?>, EntityType<WindBulletProjectile>> WIND_BULLET = registerEntityTypeWithEnglishName("wind_bullet", "风弹", "Wind Bullet", id -> EntityType.Builder.<WindBulletProjectile>of(WindBulletProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    /// 通用的追踪坠星。
    public static final DeferredHolder<EntityType<?>, EntityType<HomingFallingStarProjectile>> HOMING_FALLING_STAR = registerEntityTypeWithEnglishName("homing_falling_star", "追踪坠星", "Homing Falling Star", id -> EntityType.Builder.<HomingFallingStarProjectile>of(HomingFallingStarProjectile::new, MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    /// 星乐长按蓄力时悬浮在玩家头顶的聚星（纯视觉）。
    public static final DeferredHolder<EntityType<?>, EntityType<StarMusicChargeEntity>> STAR_MUSIC_CHARGE = registerEntityTypeWithEnglishName("star_music_charge", "星乐聚星", "Star Music Charge", id -> EntityType.Builder.<StarMusicChargeEntity>of(StarMusicChargeEntity::new, MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    /// 星惧逃出的坠星。
    public static final DeferredHolder<EntityType<?>, EntityType<StarFearStarEntity>> STAR_FEAR_STAR = registerEntityTypeWithEnglishName("star_fear_star", "星惧坠星", "Star Fear Star", id -> EntityType.Builder.<StarFearStarEntity>of(StarFearStarEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    /// 风眼的旋风。
    public static final DeferredHolder<EntityType<?>, EntityType<WindEyeTornadoEntity>> WIND_EYE_TORNADO = registerEntityTypeWithEnglishName("wind_eye_tornado", "风眼旋风", "Wind Eye Tornado", id -> EntityType.Builder.<WindEyeTornadoEntity>of(WindEyeTornadoEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(16).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SeabedWaterColumnEntity>> SEABED_WATER_COLUMN = registerEntityType("seabed_water_column", "海床水柱", id -> EntityType.Builder.<SeabedWaterColumnEntity>of(SeabedWaterColumnEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(16).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BallOfFireProjectile>> BALL_OF_FIRE = registerEntityType("ball_of_fire", "火球弹", id -> EntityType.Builder.<BallOfFireProjectile>of(BallOfFireProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EffectThrownPotion>> EFFECT_THROWN_POTION = registerEntityType("effect_thrown_potion", "效果投掷药水", id -> EntityType.Builder.<EffectThrownPotion>of(EffectThrownPotion::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MagicDaggerProjectile>> MAGIC_DAGGER = registerEntityType("magic_dagger", "魔法飞刀射弹", id -> EntityType.Builder.<MagicDaggerProjectile>of(MagicDaggerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalStormProjectile>> CRYSTAL_STORM = registerEntityType("crystal_storm", "水晶风暴射弹", id -> EntityType.Builder.<CrystalStormProjectile>of(CrystalStormProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CursedFlamesProjectile>> CURSED_FLAMES = registerEntityType("cursed_flames", "诅咒火射弹", id -> EntityType.Builder.<CursedFlamesProjectile>of(CursedFlamesProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BallOfFrostProjectile>> BALL_OF_FROST = registerEntityType("ball_of_frost", "寒霜球射弹", id -> EntityType.Builder.<BallOfFrostProjectile>of(BallOfFrostProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DemonScytheProjectile>> DEMON_SCYTHE = registerEntityType("demon_scythe", "恶魔镰刀射弹", id -> EntityType.Builder.<DemonScytheProjectile>of(DemonScytheProjectile::new, MobCategory.MISC).sized(1.5F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SkullProjectile>> SKULL = registerEntityType("skull", "骷髅头射弹", id -> EntityType.Builder.<SkullProjectile>of(SkullProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronSkullProjectile>> SKELETRON_SKULL = registerEntityType("skeletron_skull_projectile", "骷髅王头骨射弹", id -> EntityType.Builder.of(SkeletronSkullProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CloudProjectile>> BLOOD_CLOUD = registerEntityType("blood_cloud", "血云射弹", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainProjectile>> BLOOD_RAIN = registerEntityType("blood_rain", "血雨射弹", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CloudProjectile>> RAIN_CLOUD = registerEntityType("rain_cloud", "雨云射弹", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainProjectile>> RAIN = registerEntityType("rain", "雨水射弹", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GoldenShowerProjectile>> GOLDEN_SHOWER = registerEntityType("golden_shower", "黄金雨射弹", id -> EntityType.Builder.<GoldenShowerProjectile>of(GoldenShowerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MagicMissileProjectile>> MAGIC_MISSILE = registerEntityType("magic_missile", "魔法飞弹", id -> EntityType.Builder.<MagicMissileProjectile>of(MagicMissileProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlamelashProjectile>> FLAMELASH = registerEntityType("flamelash", "烈焰火鞭", id -> EntityType.Builder.<FlamelashProjectile>of(FlamelashProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainbowProjectile>> RAINBOW = registerEntityType("rainbow", "彩虹射弹", id -> EntityType.Builder.<RainbowProjectile>of(RainbowProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SkyFractureProjectile>> SKY_FRACTURE = registerEntityType("sky_fracture", "裂天剑", id -> EntityType.Builder.<SkyFractureProjectile>of(SkyFractureProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalChargeProjectile>> CRYSTAL_CHARGE_1 = registerEntityType("crystal_charge_1", "爆炸水晶1", id -> EntityType.Builder.<CrystalChargeProjectile>of(CrystalChargeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalChargeProjectile>> CRYSTAL_CHARGE_2 = registerEntityType("crystal_charge_2", "爆炸水晶2", id -> EntityType.Builder.<CrystalChargeProjectile>of(CrystalChargeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));

    /// 剑气
    public static final DeferredHolder<EntityType<?>, EntityType<GeoSwordProjectile>> GEO_SWORD_PROJECTILE = registerEntityType("geo_sword_projectile", "地质剑射弹", id -> EntityType.Builder.of(GeoSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<IceBladeSwordProjectile>> ICE_BLADE_SWORD = registerEntityType("ice_blade_sword", "冰刃剑", id -> EntityType.Builder.of(IceBladeSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<StarFuryProjectile>> STAR_FURY = registerEntityType("star_fury", "天堂怒火", id -> EntityType.Builder.of(StarFuryProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));//星怒弹幕
    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedSwordProjectile>> ENCHANTED_SWORD = registerEntityTypeWithEnglishName("enchanted_sword", "附魔剑射弹", "Enchanted Sword Projectile", id -> EntityType.Builder.of(EnchantedSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LightBaneProjectile>> LIGHTS_BANE = registerEntityType("lights_bane", "魔光剑射弹", id -> EntityType.Builder.of(LightBaneProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GrassSwordProjectile>> GRASS = registerEntityType("grass", "草剑射弹", id -> EntityType.Builder.of(GrassSwordProjectile::new, MobCategory.MISC).sized(2F, 2F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BeeKeeperProjectile>> BEE = registerEntityTypeWithEnglishName("bee", "养蜂人射弹", "Beekeeper Projectile", id -> EntityType.Builder.of(BeeKeeperProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NightEdgeProjectile>> NIGHTS_EDGE = registerEntityType("nights_edge", "永夜刃射弹", id -> EntityType.Builder.of(NightEdgeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownPhasebladeProjectile>> PHASEBLADE_PROJECTILE = registerEntityType("phaseblade_projectile", "晶光刃射弹", id -> EntityType.Builder.of(ThrownPhasebladeProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownPhasesaberProjectile>> PHASESABER_PROJECTILE = registerEntityType("phasesaber_projectile", "晶光剑射弹", id -> EntityType.Builder.of(ThrownPhasesaberProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    /// 弓箭
    public static final DeferredHolder<EntityType<?>, EntityType<BaseArrowEntity>> BASE_ARROW = registerEntityType("arrow", "泰拉箭", id -> EntityType.Builder.<BaseArrowEntity>of(BaseArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BeeArrowEntity>> BEE_ARROW = registerEntityType("bee_arrow", "蜜蜂箭", id -> EntityType.Builder.<BeeArrowEntity>of(BeeArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HellBatArrowEntity>> HELL_BAT_ARROW = registerEntityType("hell_bat_arrow", "地狱蝙蝠箭", id -> EntityType.Builder.<HellBatArrowEntity>of(HellBatArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DriveAwayArrowEntity>> DRIVE_AWAY_ARROW = registerEntityType("drive_away_arrow", "驱离箭", id -> EntityType.Builder.<DriveAwayArrowEntity>of(DriveAwayArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlamingArrowEntity>> FLAMING_ARROW = registerEntityType("flaming_arrow", "烈焰箭", id -> EntityType.Builder.<FlamingArrowEntity>of(FlamingArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<UnholyArrowEntity>> UNHOLY_ARROW = registerEntityType("unholy_arrow", "邪箭", id -> EntityType.Builder.<UnholyArrowEntity>of(UnholyArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<StarArrowEntity>> STAR_ARROW = registerEntityType("star_arrow", "星辰箭", id -> EntityType.Builder.<StarArrowEntity>of(StarArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HellfireArrowEntity>> HELLFIRE_ARROW = registerEntityType("hellfire_arrow", "狱炎箭", id -> EntityType.Builder.<HellfireArrowEntity>of(HellfireArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostburnArrowEntity>> FROSTBURN_ARROW = registerEntityType("frostburn_arrow", "霜冻箭", id -> EntityType.Builder.<FrostburnArrowEntity>of(FrostburnArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BoneArrowEntity>> BONE_ARROW = registerEntityType("bone_arrow", "骨箭", id -> EntityType.Builder.<BoneArrowEntity>of(BoneArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ShimmerArrowEntity>> SHIMMER_ARROW = registerEntityType("shimmer_arrow", "微光箭", id -> EntityType.Builder.<ShimmerArrowEntity>of(ShimmerArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FossilArrowEntity>> FOSSIL_ARROW = registerEntityType("fossil_arrow", "化石箭", id -> EntityType.Builder.<FossilArrowEntity>of(FossilArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlyFishArrowEntity>> FLY_FISH_ARROW = registerEntityType("fly_fish_arrow", "飞鱼箭", id -> EntityType.Builder.<FlyFishArrowEntity>of(FlyFishArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeveloperArrowEntity>> DEVELOPER_ARROW = registerEntityType("developer_arrow", "开发者箭", id -> EntityType.Builder.<DeveloperArrowEntity>of(DeveloperArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    /// 风矢。
    public static final DeferredHolder<EntityType<?>, EntityType<WindArrowEntity>> WIND_ARROW = registerEntityTypeWithEnglishName("wind_arrow", "风矢", "Wind Arrow", id -> EntityType.Builder.<WindArrowEntity>of(WindArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    /// 坠落之星箭。
    public static final DeferredHolder<EntityType<?>, EntityType<FallenStarArrowEntity>> FALLEN_STAR_ARROW = registerEntityTypeWithEnglishName("fallen_star_arrow", "坠落之星箭", "Fallen Star Arrow", id -> EntityType.Builder.<FallenStarArrowEntity>of(FallenStarArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));

    /// 其它
    public static final DeferredHolder<EntityType<?>, EntityType<BoulderEntity>> BOULDER = registerEntityType("boulder", "巨石", id -> EntityType.Builder.<BoulderEntity>of(BoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FollowerBoulderEntity>> FOLLOWER_BOULDER = registerEntityType("follower_boulder", "跟踪巨石", id -> EntityType.Builder.<FollowerBoulderEntity>of(FollowerBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ExplodeBoulderEntity>> EXPLODE_BOULDER = registerEntityType("explode_boulder", "爆炸巨石", id -> EntityType.Builder.<ExplodeBoulderEntity>of(ExplodeBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RollingCactusBoulderEntity>> ROLLING_CACTUS_BOULDER = registerEntityType("rolling_cactus_boulder", "仙人球", id -> EntityType.Builder.<RollingCactusBoulderEntity>of(RollingCactusBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RollingCactusBoulderEntity.SpikeProjectile>> ROLLING_CACTUS_SPIKE = registerEntityType("rolling_cactus_spike", "仙人球尖刺", id -> EntityType.Builder.of(RollingCactusBoulderEntity.SpikeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TombstoneBoulderEntity>> TOMBSTONE_BOULDER = registerEntityType("tombstone_boulder", "墓石巨石", id -> EntityType.Builder.<TombstoneBoulderEntity>of(TombstoneBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BouncyBoulderEntity>> BOUNCY_BOULDER = registerEntityType("bouncy_boulder", "弹力巨石", id -> EntityType.Builder.<BouncyBoulderEntity>of(BouncyBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GhoulderEntity>> GHOULDER = registerEntityType("ghoulder", "幽灵巨石", id -> EntityType.Builder.<GhoulderEntity>of(GhoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LavaBoulderEntity>> LAVA_BOULDER = registerEntityType("lava_boulder", "熔岩巨石", id -> EntityType.Builder.<LavaBoulderEntity>of(LavaBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PooBoulderEntity>> POO_BOULDER = registerEntityType("poo_boulder", "臭臭巨石", id -> EntityType.Builder.<PooBoulderEntity>of(PooBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiderBoulderEntity>> SPIDER_BOULDER = registerEntityType("spider_boulder", "蜘蛛巨石", id -> EntityType.Builder.<SpiderBoulderEntity>of(SpiderBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainbowBoulderEntity>> RAINBOW_BOULDER = registerEntityType("rainbow_boulder", "彩虹巨石", id -> EntityType.Builder.<RainbowBoulderEntity>of(RainbowBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LifecrystalBoulderEntity>> LIFECRYSTAL_BOULDER = registerEntityTypeWithEnglishName("lifecrystal_boulder", "生命水晶巨石", "Life Crystal Boulder", id -> EntityType.Builder.<LifecrystalBoulderEntity>of(LifecrystalBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<Boulder3x3Entity>> BOULDER_3X = registerEntityTypeWithEnglishName("boulder_3x", "3x3巨石", "Boulder 3x", id -> EntityType.Builder.<Boulder3x3Entity>of(Boulder3x3Entity::new, MobCategory.MISC).sized(3, 3).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> THROWN_KNIVE = registerEntityType("thrown_knive", "投刀", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> BONE_THROWN_KNIVE = registerEntityType("bone_thrown_knive", "骨投刀射弹", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> FROST_DAGGERFISH = registerEntityType("frost_daggerfish", "寒霜飞鱼射弹", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> DUNGEON_DEMON_BONE = registerEntityType("dungeon_demon_bone", "地牢魔骨射弹", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> SHURIKEN = registerEntityType("shuriken", "手里剑", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> JAVELIN = registerEntityType("javelin", "标枪", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RopeCoilsProjectile>> ROPE_COILS = registerEntityType("rope_coils", "绳索卷", id -> EntityType.Builder.<RopeCoilsProjectile>of(RopeCoilsProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<IceTofuBrickProjectile>> ICE_TOFU_BRICK = registerEntityType("ice_tofu_brick", "冰豆腐砖", id -> EntityType.Builder.<IceTofuBrickProjectile>of(IceTofuBrickProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpikyBallProjectile>> SPIKY_BALL = registerEntityType("spiky_ball", "尖球", id -> EntityType.Builder.<SpikyBallProjectile>of(SpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownWaterProjectile>> THROWN_WATER = registerEntityType("thrown_water", "掷出的液体瓶", id -> EntityType.Builder.<ThrownWaterProjectile>of(ThrownWaterProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlowerPetalProjectile>> FLOWER_PETAL = registerEntityType("flower_petal", "花瓣射弹", id -> EntityType.Builder.<FlowerPetalProjectile>of(FlowerPetalProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SparkleSlimeBalloonProjectile>> SPARKLE_SLIME_BALLOON = registerEntityType("sparkle_slime_balloon", "闪耀史莱姆气球", id -> EntityType.Builder.<SparkleSlimeBalloonProjectile>of(SparkleSlimeBalloonProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TitaniumShardsProjectile>> TITANIUM_SHARDS = registerEntityType("titanium_shards", "钛金防护碎片", id -> EntityType.Builder.<TitaniumShardsProjectile>of(TitaniumShardsProjectile::new, MobCategory.MISC).sized(0, 0).fireImmune().noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FallingStarItemEntity>> FALLING_STAR = registerEntityType("falling_star", "坠落之星", id -> EntityType.Builder.<FallingStarItemEntity>of(FallingStarItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TreasureBagItemEntity>> TREASURE_BAG = registerEntityType("treasure_bag", "宝藏袋", id -> EntityType.Builder.<TreasureBagItemEntity>of(TreasureBagItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CoinPortalEntity>> COIN_PORTAL = registerEntityType("coin_portal", "金币传送门", id -> EntityType.Builder.<CoinPortalEntity>of(CoinPortalEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownPowderEntity>> THROWN_POWDER = registerEntityType("thrown_powder", "掷出的粉末", id -> EntityType.Builder.<ThrownPowderEntity>of(ThrownPowderEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeadBodyPartEntity>> BODY_PART = registerEntityType("body_part", "身体部件", id -> EntityType.Builder.<DeadBodyPartEntity>of(DeadBodyPartEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlameCloudEntity>> FLAME_CLOUD = registerEntityType("flame_cloud", "火焰云", id -> EntityType.Builder.<FlameCloudEntity>of(FlameCloudEntity::new, MobCategory.MISC).sized(5, 5).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SuperSpikyBallProjectile>> SUPER_SPIKY_BALL = registerEntityType("super_spiky_ball", "超级尖球射弹", id -> EntityType.Builder.<SuperSpikyBallProjectile>of(SuperSpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpearEntity>> SPEAR = registerEntityType("spear", "长矛", id -> EntityType.Builder.<SpearEntity>of(SpearEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<StormSpearProjectile>> STORM_SPEAR_SHOT = registerEntityType("storm_spear_shot", "风暴之矛射弹", id -> EntityType.Builder.of(StormSpearProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SporeCloudProjectile>> SPORE_CLOUD = registerEntityType("spore_cloud", "孢子云", id -> EntityType.Builder.of(SporeCloudProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(64).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NorthPoleProjectile>> NORTH_POLE = registerEntityType("north_pole", "北极矛射弹", id -> EntityType.Builder.of(NorthPoleProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NorthPoleSubProjectile>> NORTH_POLE_SUB = registerEntityType("north_pole_sub", "北极矛子射弹", id -> EntityType.Builder.of(NorthPoleSubProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MushroomProjectile>> MUSHROOM = registerEntityType("mushroom", "蘑菇孢子", id -> EntityType.Builder.of(MushroomProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GhastlyProjectile>> GHASTLY = registerEntityType("ghastly", "恐魂射弹", id -> EntityType.Builder.of(GhastlyProjectile::new, MobCategory.MISC).sized(2.5F, 2.5F).clientTrackingRange(6).fireImmune().build(id.toString()));

    /// 鱼钩
    public static final DeferredHolder<EntityType<?>, EntityType<BaseFishingHook>> BASE_FISHING_HOOK = registerEntityType("base_fishing_hook", "钓鱼钩", id -> EntityType.Builder.<BaseFishingHook>of(BaseFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HotlineFishingHook>> HOTLINE_FISHING_HOOK = registerEntityType("hotline_fishing_hook", "熔线钓鱼钩", id -> EntityType.Builder.<HotlineFishingHook>of(HotlineFishingHook::new, MobCategory.MISC).noSave().noSummon().fireImmune().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CurioFishingHook>> CURIO_FISHING_HOOK = registerEntityType("curio_fishing_hook", "钓鱼钩", id -> EntityType.Builder.<CurioFishingHook>of(CurioFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BloodyFishingHook>> BLOODY_FISHING_HOOK = registerEntityType("bloody_fishing_hook", "血腥钓鱼钩", id -> EntityType.Builder.<BloodyFishingHook>of(BloodyFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));

    /// 钩爪
    public static final DeferredHolder<EntityType<?>, EntityType<BaseHookEntity>> BASE_HOOK = registerHook("base_hook", "钩", BaseHookEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> WEB_SLINGER = registerHook("web_slinger", "蛛丝吊索钩", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> SKELETRON_HAND = registerHook("skeletron_hand_hook", "骷髅王之手", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> SLIME_HOOK = registerHook("slime_hook", "史莱姆钩", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> FISH_HOOK = registerHook("fish_hook", "钓鱼钩", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> IVY_WHIP = registerHook("ivy_whip", "常春藤鞭", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> BAT_HOOK = registerHook("bat_hook", "蝙蝠钩", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> CANDY_CANE_HOOK = registerHook("candy_cane_hook", "糖棒钩", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<DualHookEntity>> DUAL_HOOK = registerHook("dual_hook", "双钩", DualHookEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<HookOfDissonanceEntity>> HOOK_OF_DISSONANCE = registerHookWithEnglishName("hook_of_dissonance", "失谐钩爪", "Hook of Dissonance", HookOfDissonanceEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> THORN_HOOK = registerHook("thorn_hook", "荆棘钩", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<MimicHookEntity>> MIMIC_HOOK = registerHook("mimic_hook", "仿生钩", MimicHookEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> ANTI_GRAVITY_HOOK = registerHookWithEnglishName("anti_gravity_hook", "反重力钩", "Anti-Gravity Hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> SPOOKY_HOOK = registerHook("spooky_hook", "诡异钩", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> CHRISTMAS_HOOK = registerHook("christmas_hook", "圣诞钩", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<LunarHookEntity>> LUNAR_HOOK = registerHook("lunar_hook", "月亮钩", LunarHookEntity::new);
    /* todo 静止钩 */

    /// 连枷
    public static final DeferredHolder<EntityType<?>, EntityType<BaseFlailEntity>> FLAIL_ENTITY = registerEntityType("flail", "链球", id -> EntityType.Builder.of(BaseFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GuardianFlailEntity>> GUARDIAN_FLAIL_ENTITY = registerEntityType("guardian_flail", "守卫连枷", id -> EntityType.Builder.<GuardianFlailEntity>of((type, level) -> new GuardianFlailEntity(type, level, false), MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GuardianFlailEntity>> ANCIENT_GUARDIAN_FLAIL_ENTITY = registerEntityType("ancient_guardian_flail", "远古守卫连枷", id -> EntityType.Builder.<GuardianFlailEntity>of((type, level) -> new GuardianFlailEntity(type, level, true), MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(24).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlowerPowerFlailEntity>> FLOWER_POWER_FLAIL = registerEntityType("flower_power", "花之力", id -> EntityType.Builder.of(FlowerPowerFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DripplerCripplerFlailEntity>> DRIPPLER_CRIPPLER_FLAIL = registerEntityType("drippler_crippler", "滴滴怪致残者", id -> EntityType.Builder.of(DripplerCripplerFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlaironFlailEntity>> FLAIRON_FLAIL = registerEntityType("flairon", "猪鲨链球", id -> EntityType.Builder.of(FlaironFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LaunchedFlailEntity>> CHAIN_KNIFE_FLAIL = registerEntityType("chain_knife", "链刀", id -> EntityType.Builder.<LaunchedFlailEntity>of((type, level) -> new LaunchedFlailEntity(type, level, 0.0), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<AnchorFlailEntity>> ANCHOR_FLAIL = registerEntityType("anchor", "锚", id -> EntityType.Builder.of(AnchorFlailEntity::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(24).updateInterval(1).noSave().build(id.toString()));

    /// 连枷投射物
    public static final DeferredHolder<EntityType<?>, EntityType<FlowerPowerPetalProjectile>> FLOWER_POWER_PETAL = registerEntityType("flower_power_petal", "花之力花瓣", id -> EntityType.Builder.of(FlowerPowerPetalProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DripplerCripplerProjectile>> DRIPPLER_CRIPPLER_PROJECTILE = registerEntityType("drippler_crippler_projectile", "血肉射弹", id -> EntityType.Builder.of(DripplerCripplerProjectile::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlaironBubbleProjectile>> FLAIRON_BUBBLE = registerEntityType("flairon_bubble", "泡泡", id -> EntityType.Builder.of(FlaironBubbleProjectile::new, MobCategory.MISC).sized(0.65F, 0.65F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    /// 矿车
    public static final DeferredHolder<EntityType<?>, EntityType<BaseMinecartEntity>> VANILLA_MINECART = registerMinecart("vanilla_minecart", "矿车", BaseMinecartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseMinecartEntity>> WOODEN_MINECART = registerMinecart("wooden_minecart", "木制矿车", BaseMinecartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<GenericMinecartEntity>> GENERIC_MINECART = registerMinecart("generic_minecart", "矿车", GenericMinecartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<MechanicalCartEntity>> MECHANICAL_CART = registerMinecart("mechanical_cart", "机械矿车", MechanicalCartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<MinecarpEntity>> MINECARP = registerMinecart("minecarp", "鲤鱼矿车", MinecarpEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<DemonicHellcartEntity>> DEMONIC_HELLCART = registerMinecart("demonic_hellcart", "恶魔地狱车", DemonicHellcartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<MeowmereMinecartEntity>> MEOWMERE_MINECART = registerMinecart("meowmere_minecart", "喵喵矿车", MeowmereMinecartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<DiggingMolecartEntity>> DIGGING_MOLECART = registerMinecart("digging_molecart", "挖掘地鼠车", DiggingMolecartEntity::new);

    public static final DeferredHolder<EntityType<?>, EntityType<BestiaryEntryDisplay>> BESTIARY_ENTRY_DISPLAY = registerEntityType("bestiary_entry_display", "基础展示实体", id -> EntityType.Builder.of(BestiaryEntryDisplay::new, MobCategory.MISC).sized(1, 1).build(id.toString()));

    /// 子弹
    public static final DeferredHolder<EntityType<?>, EntityType<StarCannonBulletEntity>> STAR_CANNON_BULLET = registerEntityType("star_cannon_bullet", "星星炮射弹", id -> EntityType.Builder.<StarCannonBulletEntity>of(StarCannonBulletEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BeeGunBullet>> BEE_GUN_BULLET = registerEntityType("bee_gun_bullet", "蜜蜂枪射弹", id -> EntityType.Builder.<BeeGunBullet>of(BeeGunBullet::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<RainbowSheep>> RAINBOW_SHEEP = registerEntityType("rainbow_sheep", "彩虹羊", id -> EntityType.Builder.of(RainbowSheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).eyeHeight(1.235F).passengerAttachments(1.2375F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<InverseEnderMan>> INVERSE_ENDERMAN = registerEntityType("inverse_enderman", "反相末影人", id -> InverseEntityType.create(InverseEnderMan::new, MobCategory.MONSTER, id.toString(), builder -> builder.sized(0.6F, 2.9F).eyeHeight(2.55F).passengerAttachments(2.80625F).clientTrackingRange(8)));

    public static final DeferredHolder<EntityType<?>, EntityType<AccumulatingEnergyEntity>> ACCUMULATING_ENERGY = registerEntityType("accumulating_energy", "聚积能量", id -> EntityType.Builder.<AccumulatingEnergyEntity>of(AccumulatingEnergyEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(1).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<SlimeSpikeEntity>> SLIME_SPIKE = registerEntityType("slime_spike", "史莱姆尖刺", id -> EntityType.Builder.of(SlimeSpikeEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<DeerclopsThrownIceProjectile>> THROWN_ICE_PROJECTILE = registerEntityType("thrown_ice_projectile", "抛射冰块", id -> EntityType.Builder.of(DeerclopsThrownIceProjectile::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeerclopsIcePillarProjectile>> ICE_PILLAR = registerEntityType("ice_pillar", "冰柱", id -> EntityType.Builder.of(DeerclopsIcePillarProjectile::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeerclopsShadowHandProjectile>> SHADOW_HAND = registerEntityType("shadow_hand", "暗影之手", id -> EntityType.Builder.of(DeerclopsShadowHandProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<DestroyerLaserProjectile>> DESTROYER_LASER = registerEntityType("destroyer_laser", "毁灭者激光", id -> EntityType.Builder.of(DestroyerLaserProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HillLavaPillarProjectile>> HILL_LAVA_PILLAR = registerEntityType("hill_lava_pillar", "血肉山熔岩柱", id -> EntityType.Builder.of(HillLavaPillarProjectile::new, MobCategory.MISC).sized(1.0F, 0.2F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> HILL_FIRE_BOUND = registerEntityType("hill_fire_bound", "血肉山火焰束", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.HILL_FIRE_BOUND), MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> WALL_OF_FLESH_LASER = registerEntityType("wall_of_flesh_laser", "血肉墙激光", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.WALL_OF_FLESH_LASER), MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PrimeLaserProjectile>> PRIME_LASER = registerEntityType("prime_laser", "机械激光", id -> EntityType.Builder.of(PrimeLaserProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraProjectile>> PLANTERA_SEED = registerEntityType("plantera_seed", "世纪之花种子", id -> EntityType.Builder.<PlanteraProjectile>of((type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.SEED), MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraProjectile>> PLANTERA_THORN_BALL = registerEntityType("plantera_thorn_ball", "世纪之花刺球", id -> EntityType.Builder.<PlanteraProjectile>of((type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.THORN_BALL), MobCategory.MISC).sized(0.7F, 0.7F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraProjectile>> PLANTERA_SPORE = registerEntityType("plantera_spore", "世纪之花孢子", id -> EntityType.Builder.<PlanteraProjectile>of((type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.SPORE), MobCategory.MISC).sized(0.45F, 0.45F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<TwinEyeProjectile>> RETINAZER_LASER = registerEntityType("retinazer_laser", "激光眼激光", id -> EntityType.Builder.<TwinEyeProjectile>of((type, level) -> new TwinEyeProjectile(type, level, TwinEyeProjectile.Variant.LASER), MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TwinEyeProjectile>> SPAZMATISM_FLAME = registerEntityType("spazmatism_flame", "魔焰眼火焰", id -> EntityType.Builder.<TwinEyeProjectile>of((type, level) -> new TwinEyeProjectile(type, level, TwinEyeProjectile.Variant.CURSED_FLAME), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<AncientLightProjectile>> ANCIENT_LIGHT = registerEntityType("ancient_light", "远古光明妖", id -> EntityType.Builder.of(AncientLightProjectile::new, MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CultistProjectile>> PROPHECYS_END = registerEntityType("prophecys_end", "预言之末", id -> EntityType.Builder.<CultistProjectile>of((type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.PROPHECYS_END), MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CultistProjectile>> CULTIST_FIREBALL = registerEntityType("cultist_fireball", "教徒火球", id -> EntityType.Builder.<CultistProjectile>of((type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.FIREBALL), MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CultistProjectile>> CULTIST_ICE_MIST = registerEntityType("cultist_ice_mist", "教徒冰雾", id -> EntityType.Builder.<CultistProjectile>of((type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.ICE_MIST), MobCategory.MISC).sized(0.8F, 0.8F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CultistProjectile>> CULTIST_LIGHTNING_ORB = registerEntityType("cultist_lightning_orb", "教徒闪电球", id -> EntityType.Builder.<CultistProjectile>of((type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.LIGHTNING_ORB), MobCategory.MISC).sized(0.7F, 0.7F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PrimeCannonballProjectile>> PRIME_CANNONBALL = registerEntityType("prime_cannonball", "机械炮弹", id -> EntityType.Builder.of(PrimeCannonballProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));

    /// 魔法
    public static final DeferredHolder<EntityType<?>, EntityType<WhipAttackEntity>> WHIP_ATTACK = registerEntityType("whip_attack", "鞭击", id -> EntityType.Builder.of(WhipAttackEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ChesterEntity>> CHESTER = withAttributes(registerStorageCompanion("chester", "切斯特", ChesterEntity::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(20).movementSpeed(0.35).flyingSpeed(0.45).followRange(32).build());
    public static final DeferredHolder<EntityType<?>, EntityType<FlyingPiggyBankEntity>> FLYING_PIGGY_BANK = withAttributes(registerStorageCompanion("piggy_bank", "飞行存钱罐", FlyingPiggyBankEntity::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(20).movementSpeed(0.35).flyingSpeed(0.45).followRange(32).build());

    public static final DeferredHolder<EntityType<?>, EntityType<ChikCrystalProjectile>> CHIK_CRYSTAL = registerEntityType("chik_crystal", "吉克水晶", id -> EntityType.Builder.of(ChikCrystalProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<KrakenWaveProjectile>> KRAKEN_WAVE = registerEntityType("kraken_wave", "克拉肯波纹", id -> EntityType.Builder.of(KrakenWaveProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CthulhuEyeProjectile>> CTHULHU_EYE_PROJECTILE = registerEntityType("cthulhu_eye_projectile", "克苏鲁之眼射弹", id -> EntityType.Builder.of(CthulhuEyeProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CascadeFireProjectile>> CASCADE_FIRE = registerEntityType("cascade_fire", "喷流火焰", id -> EntityType.Builder.of(CascadeFireProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TerrarianProjectile>> TERRARIAN_PROJECTILE = registerEntityType("terrarian_projectile", "泰拉悠悠球射弹", id -> EntityType.Builder.of(TerrarianProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<YoyoEntity>> YOYO = registerEntityType("yoyo", "悠悠球", id -> EntityType.Builder.of(YoyoEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));

    private static <E extends AbstractHookEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerHook(String name, EntityType.EntityFactory<E> supplier) {
        int updateInterval = SableHelper.IS_LOADED ? 1 : 20;
        return ENTITIES.register(name, id -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(updateInterval).build(id.toString()));
    }

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownRockProjectile>> THROWN_ROCK = registerEntityType("thrown_rock", "掷出的岩石", id -> EntityType.Builder.of(ThrownRockProjectile::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DandelionSeed>> DANDELION_SEED = registerEntityType("dandelion_seed", "蒲公英种子", id -> EntityType.Builder.of(DandelionSeed::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HopliteJavelin>> HOPLITE_JAVELIN = registerEntityType("hoplite_javelin", "标枪", id -> EntityType.Builder.of(HopliteJavelin::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostMonsterProjectile>> FROST_BLAST = registerEntityType("frost_blast", "冰霜爆裂", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.BLAST), MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostMonsterProjectile>> ICEWATER_SPIT = registerEntityType("icewater_spit", "寒水唾液", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.SPIT), MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostMonsterProjectile>> FROST_BEAM = registerEntityType("frost_beam", "冰霜光束", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.BEAM), MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<AntlionSandBall>> ANTLION_SAND_BALL = registerEntityType("antlion_sand_ball", "沙球", id -> EntityType.Builder.of(AntlionSandBall::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DesertSpiritCurse>> DESERT_SPIRIT_CURSE = registerEntityType("desert_spirit_curse", "沙漠幽魂的诅咒", id -> EntityType.Builder.of(DesertSpiritCurse::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileDemonScytheProjectile>> HOSTILE_DEMON_SCYTHE = registerEntityType("hostile_demon_scythe_projectile", "敌对恶魔镰刀射弹", id -> EntityType.Builder.of(HostileDemonScytheProjectile::new, MobCategory.MISC).sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PaladinHammerProjectile>> PALADIN_HAMMER_PROJECTILE = registerEntityType("paladin_hammer_projectile", "圣骑士重锤弹幕", id -> EntityType.Builder.of(PaladinHammerProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NimbusRain>> NIMBUS_RAIN = registerEntityType("nimbus_rain", "雨滴", id -> EntityType.Builder.of(NimbusRain::new, MobCategory.MISC).sized(0.1F, 1.0F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HarpyFeatherProjectile>> HARPY_FEATHER = registerEntityType("harpy_feather_projectile", "鸟妖羽毛弹幕", id -> EntityType.Builder.of(HarpyFeatherProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HornetStingerProjectile>> HORNET_STINGER = registerEntityType("hornet_stinger_projectile", "黄蜂毒刺射弹", id -> EntityType.Builder.of(HornetStingerProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<UnholyTridentProjectile>> UNHOLY_TRIDENT = registerEntityType("unholy_trident", "邪恶三叉戟", id -> EntityType.Builder.of(UnholyTridentProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PlantSpit>> CLINGER_FLAME = registerEntityType("clinger_flame", "诅咒魔焰", id -> EntityType.Builder.<PlantSpit>of((type, level) -> new PlantSpit(type, level, PlantSpit.Kind.CURSED_FLAME), MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PlantSpit>> FUNGI_SPORE = registerEntityType("fungi_spore", "真菌孢子", id -> EntityType.Builder.<PlantSpit>of((type, level) -> new PlantSpit(type, level, PlantSpit.Kind.SPORE), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiderWebSpit>> SPIDER_WEB_SPIT = registerEntityType("spider_web_spit", "蛛网喷吐", id -> EntityType.Builder.of(SpiderWebSpit::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> RUNE_BLAST = registerEntityType("rune_blast", "符文冲击", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.RUNE_BLAST), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> DARK_CASTER_PROJECTILE = registerEntityType("dark_caster_projectile", "水球", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.WATER_SPHERE), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> CHAOS_BALL_PROJECTILE = registerEntityType("chaos_ball_projectile", "混沌球", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.CHAOS_BALL), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> SHADOW_BEAM_PROJECTILE = registerEntityType("shadow_beam_projectile", "暗影束", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.SHADOW_BEAM), MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> INFERNO_BOLT_PROJECTILE = registerEntityType("inferno_bolt_projectile", "狱火弹", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.INFERNO_BOLT), MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> LOST_SOUL_PROJECTILE = registerEntityType("lost_soul_projectile", "亡魂射弹", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.LOST_SOUL), MobCategory.MISC).sized(0.45F, 0.45F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> VILE_SPIT_PROJECTILE = registerEntityType("vile_spit_projectile", "魔唾液弹幕", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.VILE_SPIT), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> FIRE_IMP_PROJECTILE = registerEntityType("fire_imp_projectile", "火焰小鬼弹幕", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.FIRE_IMP), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> GASTROPOD_PROJECTILE = registerEntityType("gastropod_projectile", "腹足怪能量弹幕", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.GASTROPOD), MobCategory.MISC).sized(0.45F, 0.45F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PirateShot>> PIRATE_BULLET = registerEntityType("pirate_bullet", "海盗子弹", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.BULLET), MobCategory.MISC).sized(0.15F, 0.15F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PirateShot>> PIRATE_FLAMING_ARROW = registerEntityType("pirate_flaming_arrow", "海盗烈焰箭", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.ARROW), MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PirateShot>> PIRATE_CANNONBALL = registerEntityType("pirate_cannonball", "海盗炮弹", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.CANNONBALL), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<NPCWeaponProjectile>> NPC_WEAPON_PROJECTILE = registerEntityType("npc_weapon_projectile", "NPC武器射弹", id -> EntityType.Builder.<NPCWeaponProjectile>of(NPCWeaponProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CyborgExplosiveProjectile>> CYBORG_EXPLOSIVE = registerEntityType("cyborg_explosive", "机器侠爆炸弹", id -> EntityType.Builder.<CyborgExplosiveProjectile>of(CyborgExplosiveProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BoomerangProjectile>> BOOMERANG_PROJECTILE = registerEntityType("boomerang_projectile", "回旋镖", id -> EntityType.Builder.of(BoomerangProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<NPCShadowflameSkullProjectile>> NPC_SHADOWFLAME_SKULL = registerEntityType("npc_shadowflame_skull", "NPC暗影焰头骨", id -> EntityType.Builder.of(NPCShadowflameSkullProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<BaseBulletEntity>> BASE_BULLET_ENTITY = registerEntityType("base_bullet", "子弹", id -> EntityType.Builder.<BaseBulletEntity>of(BaseBulletEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CustomBulletEntity>> GRAVITY_BULLET_ENTITY = registerEntityType("gravity_bullet", "重力子弹", id -> EntityType.Builder.<CustomBulletEntity>of(CustomBulletEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<RideableUnicornMountEntity>> RIDEABLE_UNICORN = registerEntityType("rideable_unicorn", "独角兽坐骑", id -> EntityType.Builder.of(RideableUnicornMountEntity::new, MobCategory.MISC).sized(1.2F, 1.6F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RideableLavaSharkMountEntity>> RIDEABLE_LAVA_SHARK = registerEntityType("rideable_lava_shark", "岩浆鲨坐骑", id -> EntityType.Builder.of(RideableLavaSharkMountEntity::new, MobCategory.MISC).sized(1.5F, 0.8F).fireImmune().clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RideableSlimeMountEntity>> RIDEABLE_SLIME = registerEntityType("rideable_slime", "史莱姆坐骑", id -> EntityType.Builder.of(RideableSlimeMountEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RideableBeeMountEntity>> RIDEABLE_BEE = registerEntityType("rideable_bee", "蜜蜂坐骑", id -> EntityType.Builder.of(RideableBeeMountEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));

    public static <T extends LivingEntity> DeferredHolder<EntityType<?>, EntityType<T>> withAttributes(DeferredHolder<EntityType<?>, EntityType<T>> type, Supplier<CreatureAttributeBuilder.Definition> attributes) {
        ATTRIBUTES.put(type, attributes);
        return type;
    }

    public static @Nullable CreatureAttributeBuilder.Definition creatureAttributes(EntityType<?> type) {
        return CREATURE_DEFINITIONS.get(type);
    }

    /// 提供只读登记模板，供阶段属性数据生成器统一输出。
    public static Map<EntityType<?>, CreatureAttributeBuilder.Definition> creatureDefinitions() {
        return Map.copyOf(CREATURE_DEFINITIONS);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        ATTRIBUTES.forEach((type, attributes) -> {
            CreatureAttributeBuilder.Definition definition = attributes.get();
            CREATURE_DEFINITIONS.put(type.get(), definition);
            event.put(type.get(), definition.attributes());
        });
    }

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
        BossEntities.ENTITIES.register(eventBus);
        CritterEntities.ENTITIES.register(eventBus);
        MonsterEntities.ENTITIES.register(eventBus);
        NpcEntities.ENTITIES.register(eventBus);

        Iterables.concat(
                ENTITIES.getEntries(),
                BossEntities.ENTITIES.getEntries(),
                CritterEntities.ENTITIES.getEntries(),
                MonsterEntities.ENTITIES.getEntries(),
                NpcEntities.ENTITIES.getEntries()
        ).forEach(holder -> BuiltInRegistries.ENTITY_TYPE.addAlias(
                ResourceLocation.fromNamespaceAndPath("terra_entity", holder.getId().getPath()),
                holder.getId()
        ));
    }

    private static <E extends BaseMinecartEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerMinecart(String registryId, String chineseName, EntityType.EntityFactory<E> factory) {
        DeferredHolder<EntityType<?>, EntityType<E>> entry = ENTITIES.register(registryId, id -> EntityType.Builder.of(factory, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8).build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <E extends AbstractHookEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerHook(String registryId, String chineseName, EntityType.EntityFactory<E> supplier) {
        DeferredHolder<EntityType<?>, EntityType<E>> entry = registerHook(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <E extends BaseBombEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerBomb(String registryId, String chineseName, EntityType.EntityFactory<E> supplier, float size) {
        DeferredHolder<EntityType<?>, EntityType<E>> entry = ENTITIES.register(registryId, id -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(size, size).clientTrackingRange(4).updateInterval(10).fireImmune().build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> registerStorageCompanion(String registryId, String chineseName, EntityType.EntityFactory<E> factory) {
        DeferredHolder<EntityType<?>, EntityType<E>> entry = ENTITIES.register(registryId, id -> EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(1.0F, 1.0F)
                .clientTrackingRange(10)
                .updateInterval(1)
                .noSummon()
                .noSave()
                .build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntityType(String registryId, String chineseName, Function<ResourceLocation, EntityType<T>> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <E extends AbstractHookEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerHookWithEnglishName(String registryId, String chineseName, String englishName, EntityType.EntityFactory<E> supplier) {
        DeferredHolder<EntityType<?>, EntityType<E>> entry = registerHook(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntityTypeWithEnglishName(String registryId, String chineseName, String englishName, Function<ResourceLocation, EntityType<T>> factory) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = ENTITIES.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
