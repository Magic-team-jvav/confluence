package org.confluence.mod.common.init.entity;

import org.confluence.mod.common.data.saved.Bestiary;

import net.minecraft.core.registries.BuiltInRegistries;
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
import org.confluence.mod.common.entity.storage.ChesterEntity;
import org.confluence.mod.common.entity.storage.FlyingPiggyBankEntity;
import org.confluence.mod.common.entity.yoyo.*;
import org.confluence.mod.integration.sable.SableHelper;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Confluence.MODID);
    private static final Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<CreatureAttributeBuilder.Definition>> ATTRIBUTES = new LinkedHashMap<>();
    private static final Map<EntityType<?>, CreatureAttributeBuilder.Definition> CREATURE_DEFINITIONS = new LinkedHashMap<>();

    // 牢枕专用
    public static final DeferredHolder<EntityType<?>, EntityType<EmptyEntity>> EMPTY_ENTITY = ENTITIES.register("empty_entity", id -> EntityType.Builder.of(EmptyEntity::new, MobCategory.MISC).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<MartianElectricBolt>> MARTIAN_ELECTRIC_BOLT = ENTITIES.register("martian_electric_bolt", id -> EntityType.Builder.of(MartianElectricBolt::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MonsterLaser>> MONSTER_LASER = ENTITIES.register("monster_laser", id -> EntityType.Builder.of(MonsterLaser::new, MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(16).updateInterval(1).build(id.toString()));

    // 炸弹
    public static final DeferredHolder<EntityType<?>, EntityType<BaseBombEntity>> BOMB_ENTITY = registerBomb("bomb_entity", BaseBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BouncyBombEntity>> BOUNCY_BOMB_ENTITY = registerBomb("bouncy_bomb_entity", BouncyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<ScarabBombEntity>> SCARAB_BOMB_ENTITY = registerBomb("scarab_bomb_entity", ScarabBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyBombEntity>> STICKY_BOMB_ENTITY = registerBomb("sticky_bomb_entity", StickyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<SmokeBombEntity>> SMOKE_BOMB_ENTITY = registerBomb("smoke_bomb_entity", SmokeBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BombFishEntity>> BOMB_FISH_ENTITY = registerBomb("bomb_fish_entity", BombFishEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseGrenadeEntity>> GRENADE = registerBomb("grenade", BaseGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BouncyGrenadeEntity>> BOUNCY_GRENADE = registerBomb("bouncy_grenade", BouncyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyGrenadeEntity>> STICKY_GRENADE = registerBomb("sticky_grenade", StickyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BeenadeEntity>> BEENADE = registerBomb("beenade", BeenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseDynamiteEntity>> DYNAMITE = registerBomb("dynamite", BaseDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BouncyDynamiteEntity>> BOUNCY_DYNAMITE = registerBomb("bouncy_dynamite", BouncyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyDynamiteEntity>> STICKY_DYNAMITE = registerBomb("sticky_dynamite", StickyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseDirtBombEntity>> DIRT_BOMB = registerBomb("dirt_bomb", BaseDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<StickyDirtBombEntity>> STICKY_DIRT_BOMB = registerBomb("sticky_dirt_bomb", StickyDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<DryBombEntity>> DRY_BOMB = registerBomb("dry_bomb", DryBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<LiquidBombEntity>> WET_BOMB = registerBomb("wet_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<LiquidBombEntity>> LAVA_BOMB = registerBomb("lava_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final DeferredHolder<EntityType<?>, EntityType<LiquidBombEntity>> HONEY_BOMB = registerBomb("honey_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);

    // 魔法
    public static final DeferredHolder<EntityType<?>, EntityType<BaseManaStaffProjectileEntity>> BASE_MANA_STAFF = ENTITIES.register("base_mana_staff", id -> EntityType.Builder.<BaseManaStaffProjectileEntity>of(BaseManaStaffProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<VilethronProjectile>> VILETHRON = ENTITIES.register("vilethron", id -> EntityType.Builder.<VilethronProjectile>of(VilethronProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalVileShardProjectile>> CRYSTAL_VILE_SHARD = ENTITIES.register("crystal_vile_shard", id -> EntityType.Builder.<CrystalVileShardProjectile>of(CrystalVileShardProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HurtnadoProjectile>> HURTNADO = ENTITIES.register("hurtnado", id -> EntityType.Builder.<HurtnadoProjectile>of(HurtnadoProjectile::new, MobCategory.MISC).sized(0.8F, 1.2F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<WaterStreamProjectile>> WATER_STREAM = ENTITIES.register("water_stream", id -> EntityType.Builder.<WaterStreamProjectile>of(WaterStreamProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<WaterBoltProjectile>> WATER_BOLT = ENTITIES.register("water_bolt", id -> EntityType.Builder.<WaterBoltProjectile>of(WaterBoltProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BallOfFireProjectile>> BALL_OF_FIRE = ENTITIES.register("ball_of_fire", id -> EntityType.Builder.<BallOfFireProjectile>of(BallOfFireProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EffectThrownPotion>> EFFECT_THROWN_POTION = ENTITIES.register("effect_thrown_potion", id -> EntityType.Builder.<EffectThrownPotion>of(EffectThrownPotion::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MagicDaggerProjectile>> MAGIC_DAGGER = ENTITIES.register("magic_dagger", id -> EntityType.Builder.<MagicDaggerProjectile>of(MagicDaggerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalStormProjectile>> CRYSTAL_STORM = ENTITIES.register("crystal_storm", id -> EntityType.Builder.<CrystalStormProjectile>of(CrystalStormProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CursedFlamesProjectile>> CURSED_FLAMES = ENTITIES.register("cursed_flames", id -> EntityType.Builder.<CursedFlamesProjectile>of(CursedFlamesProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BallOfFrostProjectile>> BALL_OF_FROST = ENTITIES.register("ball_of_frost", id -> EntityType.Builder.<BallOfFrostProjectile>of(BallOfFrostProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DemonScytheProjectile>> DEMON_SCYTHE = ENTITIES.register("demon_scythe", id -> EntityType.Builder.<DemonScytheProjectile>of(DemonScytheProjectile::new, MobCategory.MISC).sized(1.5F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SkullProjectile>> SKULL = ENTITIES.register("skull", id -> EntityType.Builder.<SkullProjectile>of(SkullProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletronSkullProjectile>> SKELETRON_SKULL = ENTITIES.register("skeletron_skull_projectile", id -> EntityType.Builder.of(SkeletronSkullProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CloudProjectile>> BLOOD_CLOUD = ENTITIES.register("blood_cloud", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainProjectile>> BLOOD_RAIN = ENTITIES.register("blood_rain", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CloudProjectile>> RAIN_CLOUD = ENTITIES.register("rain_cloud", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainProjectile>> RAIN = ENTITIES.register("rain", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GoldenShowerProjectile>> GOLDEN_SHOWER = ENTITIES.register("golden_shower", id -> EntityType.Builder.<GoldenShowerProjectile>of(GoldenShowerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MagicMissileProjectile>> MAGIC_MISSILE = ENTITIES.register("magic_missile", id -> EntityType.Builder.<MagicMissileProjectile>of(MagicMissileProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlamelashProjectile>> FLAMELASH = ENTITIES.register("flamelash", id -> EntityType.Builder.<FlamelashProjectile>of(FlamelashProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainbowProjectile>> RAINBOW = ENTITIES.register("rainbow", id -> EntityType.Builder.<RainbowProjectile>of(RainbowProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SkyFractureProjectile>> SKY_FRACTURE = ENTITIES.register("sky_fracture", id -> EntityType.Builder.<SkyFractureProjectile>of(SkyFractureProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalChargeProjectile>> CRYSTAL_CHARGE_1 = ENTITIES.register("crystal_charge_1", id -> EntityType.Builder.<CrystalChargeProjectile>of(CrystalChargeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalChargeProjectile>> CRYSTAL_CHARGE_2 = ENTITIES.register("crystal_charge_2", id -> EntityType.Builder.<CrystalChargeProjectile>of(CrystalChargeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));

    // 剑气
    public static final DeferredHolder<EntityType<?>, EntityType<GeoSwordProjectile>> GEO_SWORD_PROJECTILE = ENTITIES.register("geo_sword_projectile", id -> EntityType.Builder.of(GeoSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<IceBladeSwordProjectile>> ICE_BLADE_SWORD = ENTITIES.register("ice_blade_sword", id -> EntityType.Builder.of(IceBladeSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<StarFuryProjectile>> STAR_FURY = ENTITIES.register("star_fury", id -> EntityType.Builder.of(StarFuryProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));//星怒弹幕
    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedSwordProjectile>> ENCHANTED_SWORD = ENTITIES.register("enchanted_sword", id -> EntityType.Builder.of(EnchantedSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LightBaneProjectile>> LIGHTS_BANE = ENTITIES.register("lights_bane", id -> EntityType.Builder.of(LightBaneProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GrassSwordProjectile>> GRASS = ENTITIES.register("grass", id -> EntityType.Builder.of(GrassSwordProjectile::new, MobCategory.MISC).sized(2F, 2F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BeeKeeperProjectile>> BEE = ENTITIES.register("bee", id -> EntityType.Builder.of(BeeKeeperProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NightEdgeProjectile>> NIGHTS_EDGE = ENTITIES.register("nights_edge", id -> EntityType.Builder.of(NightEdgeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownPhasebladeProjectile>> PHASEBLADE_PROJECTILE = ENTITIES.register("phaseblade_projectile", id -> EntityType.Builder.of(ThrownPhasebladeProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownPhasesaberProjectile>> PHASESABER_PROJECTILE = ENTITIES.register("phasesaber_projectile", id -> EntityType.Builder.of(ThrownPhasesaberProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    // 弓箭
    public static final DeferredHolder<EntityType<?>, EntityType<BaseArrowEntity>> BASE_ARROW = ENTITIES.register("arrow", id -> EntityType.Builder.<BaseArrowEntity>of(BaseArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BeeArrowEntity>> BEE_ARROW = ENTITIES.register("bee_arrow", id -> EntityType.Builder.<BeeArrowEntity>of(BeeArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HellBatArrowEntity>> HELL_BAT_ARROW = ENTITIES.register("hell_bat_arrow", id -> EntityType.Builder.<HellBatArrowEntity>of(HellBatArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DriveAwayArrowEntity>> DRIVE_AWAY_ARROW = ENTITIES.register("drive_away_arrow", id -> EntityType.Builder.<DriveAwayArrowEntity>of(DriveAwayArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlamingArrowEntity>> FLAMING_ARROW = ENTITIES.register("flaming_arrow", id -> EntityType.Builder.<FlamingArrowEntity>of(FlamingArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<UnholyArrowEntity>> UNHOLY_ARROW = ENTITIES.register("unholy_arrow", id -> EntityType.Builder.<UnholyArrowEntity>of(UnholyArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<StarArrowEntity>> STAR_ARROW = ENTITIES.register("star_arrow", id -> EntityType.Builder.<StarArrowEntity>of(StarArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HellfireArrowEntity>> HELLFIRE_ARROW = ENTITIES.register("hellfire_arrow", id -> EntityType.Builder.<HellfireArrowEntity>of(HellfireArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostburnArrowEntity>> FROSTBURN_ARROW = ENTITIES.register("frostburn_arrow", id -> EntityType.Builder.<FrostburnArrowEntity>of(FrostburnArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BoneArrowEntity>> BONE_ARROW = ENTITIES.register("bone_arrow", id -> EntityType.Builder.<BoneArrowEntity>of(BoneArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ShimmerArrowEntity>> SHIMMER_ARROW = ENTITIES.register("shimmer_arrow", id -> EntityType.Builder.<ShimmerArrowEntity>of(ShimmerArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FossilArrowEntity>> FOSSIL_ARROW = ENTITIES.register("fossil_arrow", id -> EntityType.Builder.<FossilArrowEntity>of(FossilArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlyFishArrowEntity>> FLY_FISH_ARROW = ENTITIES.register("fly_fish_arrow", id -> EntityType.Builder.<FlyFishArrowEntity>of(FlyFishArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeveloperArrowEntity>> DEVELOPER_ARROW = ENTITIES.register("developer_arrow", id -> EntityType.Builder.<DeveloperArrowEntity>of(DeveloperArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));

    // 其它
    public static final DeferredHolder<EntityType<?>, EntityType<BoulderEntity>> BOULDER = ENTITIES.register("boulder", id -> EntityType.Builder.<BoulderEntity>of(BoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FollowerBoulderEntity>> FOLLOWER_BOULDER = ENTITIES.register("follower_boulder", id -> EntityType.Builder.<FollowerBoulderEntity>of(FollowerBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ExplodeBoulderEntity>> EXPLODE_BOULDER = ENTITIES.register("explode_boulder", id -> EntityType.Builder.<ExplodeBoulderEntity>of(ExplodeBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RollingCactusBoulderEntity>> ROLLING_CACTUS_BOULDER = ENTITIES.register("rolling_cactus_boulder", id -> EntityType.Builder.<RollingCactusBoulderEntity>of(RollingCactusBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RollingCactusBoulderEntity.SpikeProjectile>> ROLLING_CACTUS_SPIKE = ENTITIES.register("rolling_cactus_spike", id -> EntityType.Builder.of(RollingCactusBoulderEntity.SpikeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TombstoneBoulderEntity>> TOMBSTONE_BOULDER = ENTITIES.register("tombstone_boulder", id -> EntityType.Builder.<TombstoneBoulderEntity>of(TombstoneBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BouncyBoulderEntity>> BOUNCY_BOULDER = ENTITIES.register("bouncy_boulder", id -> EntityType.Builder.<BouncyBoulderEntity>of(BouncyBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GhoulderEntity>> GHOULDER = ENTITIES.register("ghoulder", id -> EntityType.Builder.<GhoulderEntity>of(GhoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LavaBoulderEntity>> LAVA_BOULDER = ENTITIES.register("lava_boulder", id -> EntityType.Builder.<LavaBoulderEntity>of(LavaBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PooBoulderEntity>> POO_BOULDER = ENTITIES.register("poo_boulder", id -> EntityType.Builder.<PooBoulderEntity>of(PooBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiderBoulderEntity>> SPIDER_BOULDER = ENTITIES.register("spider_boulder", id -> EntityType.Builder.<SpiderBoulderEntity>of(SpiderBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainbowBoulderEntity>> RAINBOW_BOULDER = ENTITIES.register("rainbow_boulder", id -> EntityType.Builder.<RainbowBoulderEntity>of(RainbowBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LifecrystalBoulderEntity>> LIFECRYSTAL_BOULDER = ENTITIES.register("lifecrystal_boulder", id -> EntityType.Builder.<LifecrystalBoulderEntity>of(LifecrystalBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<Boulder3x3Entity>> BOULDER_3X = ENTITIES.register("boulder_3x", id -> EntityType.Builder.<Boulder3x3Entity>of(Boulder3x3Entity::new, MobCategory.MISC).sized(3, 3).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> THROWN_KNIVE = ENTITIES.register("thrown_knive", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> BONE_THROWN_KNIVE = ENTITIES.register("bone_thrown_knive", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> FROST_DAGGERFISH = ENTITIES.register("frost_daggerfish", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> DUNGEON_DEMON_BONE = ENTITIES.register("dungeon_demon_bone", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> SHURIKEN = ENTITIES.register("shuriken", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> JAVELIN = ENTITIES.register("javelin", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RopeCoilsProjectile>> ROPE_COILS = ENTITIES.register("rope_coils", id -> EntityType.Builder.<RopeCoilsProjectile>of(RopeCoilsProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<IceTofuBrickProjectile>> ICE_TOFU_BRICK = ENTITIES.register("ice_tofu_brick", id -> EntityType.Builder.<IceTofuBrickProjectile>of(IceTofuBrickProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpikyBallProjectile>> SPIKY_BALL = ENTITIES.register("spiky_ball", id -> EntityType.Builder.<SpikyBallProjectile>of(SpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownWaterProjectile>> THROWN_WATER = ENTITIES.register("thrown_water", id -> EntityType.Builder.<ThrownWaterProjectile>of(ThrownWaterProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlowerPetalProjectile>> FLOWER_PETAL = ENTITIES.register("flower_petal", id -> EntityType.Builder.<FlowerPetalProjectile>of(FlowerPetalProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SparkleSlimeBalloonProjectile>> SPARKLE_SLIME_BALLOON = ENTITIES.register("sparkle_slime_balloon", id -> EntityType.Builder.<SparkleSlimeBalloonProjectile>of(SparkleSlimeBalloonProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TitaniumShardsProjectile>> TITANIUM_SHARDS = ENTITIES.register("titanium_shards", id -> EntityType.Builder.<TitaniumShardsProjectile>of(TitaniumShardsProjectile::new, MobCategory.MISC).sized(0, 0).fireImmune().noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FallingStarItemEntity>> FALLING_STAR = ENTITIES.register("falling_star", id -> EntityType.Builder.<FallingStarItemEntity>of(FallingStarItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TreasureBagItemEntity>> TREASURE_BAG = ENTITIES.register("treasure_bag", id -> EntityType.Builder.<TreasureBagItemEntity>of(TreasureBagItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CoinPortalEntity>> COIN_PORTAL = ENTITIES.register("coin_portal", id -> EntityType.Builder.<CoinPortalEntity>of(CoinPortalEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownPowderEntity>> THROWN_POWDER = ENTITIES.register("thrown_powder", id -> EntityType.Builder.<ThrownPowderEntity>of(ThrownPowderEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeadBodyPartEntity>> BODY_PART = ENTITIES.register("body_part", id -> EntityType.Builder.<DeadBodyPartEntity>of(DeadBodyPartEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlameCloudEntity>> FLAME_CLOUD = ENTITIES.register("flame_cloud", id -> EntityType.Builder.<FlameCloudEntity>of(FlameCloudEntity::new, MobCategory.MISC).sized(5, 5).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SuperSpikyBallProjectile>> SUPER_SPIKY_BALL = ENTITIES.register("super_spiky_ball", id -> EntityType.Builder.<SuperSpikyBallProjectile>of(SuperSpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpearEntity>> SPEAR = ENTITIES.register("spear", id -> EntityType.Builder.<SpearEntity>of(SpearEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<StormSpearProjectile>> STORM_SPEAR_SHOT = ENTITIES.register("storm_spear_shot", id -> EntityType.Builder.of(StormSpearProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SporeCloudProjectile>> SPORE_CLOUD = ENTITIES.register("spore_cloud", id -> EntityType.Builder.of(SporeCloudProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(64).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NorthPoleProjectile>> NORTH_POLE = ENTITIES.register("north_pole", id -> EntityType.Builder.of(NorthPoleProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NorthPoleSubProjectile>> NORTH_POLE_SUB = ENTITIES.register("north_pole_sub", id -> EntityType.Builder.of(NorthPoleSubProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MushroomProjectile>> MUSHROOM = ENTITIES.register("mushroom", id -> EntityType.Builder.of(MushroomProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GhastlyProjectile>> GHASTLY = ENTITIES.register("ghastly", id -> EntityType.Builder.of(GhastlyProjectile::new, MobCategory.MISC).sized(2.5F, 2.5F).clientTrackingRange(6).fireImmune().build(id.toString()));

    // 鱼钩
    public static final DeferredHolder<EntityType<?>, EntityType<BaseFishingHook>> BASE_FISHING_HOOK = ENTITIES.register("base_fishing_hook", id -> EntityType.Builder.<BaseFishingHook>of(BaseFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HotlineFishingHook>> HOTLINE_FISHING_HOOK = ENTITIES.register("hotline_fishing_hook", id -> EntityType.Builder.<HotlineFishingHook>of(HotlineFishingHook::new, MobCategory.MISC).noSave().noSummon().fireImmune().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CurioFishingHook>> CURIO_FISHING_HOOK = ENTITIES.register("curio_fishing_hook", id -> EntityType.Builder.<CurioFishingHook>of(CurioFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BloodyFishingHook>> BLOODY_FISHING_HOOK = ENTITIES.register("bloody_fishing_hook", id -> EntityType.Builder.<BloodyFishingHook>of(BloodyFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));

    // 钩爪
    public static final DeferredHolder<EntityType<?>, EntityType<BaseHookEntity>> BASE_HOOK = registerHook("base_hook", BaseHookEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> WEB_SLINGER = registerHook("web_slinger", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> SKELETRON_HAND = registerHook("skeletron_hand_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> SLIME_HOOK = registerHook("slime_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> FISH_HOOK = registerHook("fish_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> IVY_WHIP = registerHook("ivy_whip", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> BAT_HOOK = registerHook("bat_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> CANDY_CANE_HOOK = registerHook("candy_cane_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<DualHookEntity>> DUAL_HOOK = registerHook("dual_hook", DualHookEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<HookOfDissonanceEntity>> HOOK_OF_DISSONANCE = registerHook("hook_of_dissonance", HookOfDissonanceEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> THORN_HOOK = registerHook("thorn_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<MimicHookEntity>> MIMIC_HOOK = registerHook("mimic_hook", MimicHookEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> ANTI_GRAVITY_HOOK = registerHook("anti_gravity_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> SPOOKY_HOOK = registerHook("spooky_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> CHRISTMAS_HOOK = registerHook("christmas_hook", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<LunarHookEntity>> LUNAR_HOOK = registerHook("lunar_hook", LunarHookEntity::new);
    /* todo 静止钩 */

    // 连枷
    public static final DeferredHolder<EntityType<?>, EntityType<BaseFlailEntity>> FLAIL_ENTITY = ENTITIES.register("flail", id -> EntityType.Builder.of(BaseFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GuardianFlailEntity>> GUARDIAN_FLAIL_ENTITY = ENTITIES.register("guardian_flail", id -> EntityType.Builder.<GuardianFlailEntity>of((type, level) -> new GuardianFlailEntity(type, level, false), MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GuardianFlailEntity>> ANCIENT_GUARDIAN_FLAIL_ENTITY = ENTITIES.register("ancient_guardian_flail", id -> EntityType.Builder.<GuardianFlailEntity>of((type, level) -> new GuardianFlailEntity(type, level, true), MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(24).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlowerPowerFlailEntity>> FLOWER_POWER_FLAIL = ENTITIES.register("flower_power", id -> EntityType.Builder.of(FlowerPowerFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DripplerCripplerFlailEntity>> DRIPPLER_CRIPPLER_FLAIL = ENTITIES.register("drippler_crippler", id -> EntityType.Builder.of(DripplerCripplerFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlaironFlailEntity>> FLAIRON_FLAIL = ENTITIES.register("flairon", id -> EntityType.Builder.of(FlaironFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LaunchedFlailEntity>> CHAIN_KNIFE_FLAIL = ENTITIES.register("chain_knife", id -> EntityType.Builder.<LaunchedFlailEntity>of((type, level) -> new LaunchedFlailEntity(type, level, 0.0), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(20).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<AnchorFlailEntity>> ANCHOR_FLAIL = ENTITIES.register("anchor", id -> EntityType.Builder.of(AnchorFlailEntity::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(24).updateInterval(1).noSave().build(id.toString()));

    // 连枷投射物
    public static final DeferredHolder<EntityType<?>, EntityType<FlowerPowerPetalProjectile>> FLOWER_POWER_PETAL = ENTITIES.register("flower_power_petal", id -> EntityType.Builder.of(FlowerPowerPetalProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DripplerCripplerProjectile>> DRIPPLER_CRIPPLER_PROJECTILE = ENTITIES.register("drippler_crippler_projectile", id -> EntityType.Builder.of(DripplerCripplerProjectile::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlaironBubbleProjectile>> FLAIRON_BUBBLE = ENTITIES.register("flairon_bubble", id -> EntityType.Builder.of(FlaironBubbleProjectile::new, MobCategory.MISC).sized(0.65F, 0.65F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    // 矿车
    public static final DeferredHolder<EntityType<?>, EntityType<BaseMinecartEntity>> VANILLA_MINECART = registerMinecart("vanilla_minecart", BaseMinecartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<BaseMinecartEntity>> WOODEN_MINECART = registerMinecart("wooden_minecart", BaseMinecartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<GenericMinecartEntity>> GENERIC_MINECART = registerMinecart("generic_minecart", GenericMinecartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<MechanicalCartEntity>> MECHANICAL_CART = registerMinecart("mechanical_cart", MechanicalCartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<MinecarpEntity>> MINECARP = registerMinecart("minecarp", MinecarpEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<DemonicHellcartEntity>> DEMONIC_HELLCART = registerMinecart("demonic_hellcart", DemonicHellcartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<MeowmereMinecartEntity>> MEOWMERE_MINECART = registerMinecart("meowmere_minecart", MeowmereMinecartEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<DiggingMolecartEntity>> DIGGING_MOLECART = registerMinecart("digging_molecart", DiggingMolecartEntity::new);

    public static final DeferredHolder<EntityType<?>, EntityType<BestiaryEntryDisplay>> BESTIARY_ENTRY_DISPLAY = ENTITIES.register("bestiary_entry_display", id -> EntityType.Builder.of(BestiaryEntryDisplay::new, MobCategory.MISC).sized(1, 1).build(id.toString()));

    // 子弹
    public static final DeferredHolder<EntityType<?>, EntityType<StarCannonBulletEntity>> STAR_CANNON_BULLET = ENTITIES.register("star_cannon_bullet", id -> EntityType.Builder.<StarCannonBulletEntity>of(StarCannonBulletEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BeeGunBullet>> BEE_GUN_BULLET = ENTITIES.register("bee_gun_bullet", id -> EntityType.Builder.<BeeGunBullet>of(BeeGunBullet::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<RainbowSheep>> RAINBOW_SHEEP = ENTITIES.register("rainbow_sheep", id -> EntityType.Builder.of(RainbowSheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).eyeHeight(1.235F).passengerAttachments(1.2375F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<InverseEnderMan>> INVERSE_ENDERMAN = ENTITIES.register("inverse_enderman", id -> InverseEntityType.create(InverseEnderMan::new, MobCategory.MONSTER, id.toString(), builder -> builder.sized(0.6F, 2.9F).eyeHeight(2.55F).passengerAttachments(2.80625F).clientTrackingRange(8)));

    public static final DeferredHolder<EntityType<?>, EntityType<AccumulatingEnergyEntity>> ACCUMULATING_ENERGY = ENTITIES.register("accumulating_energy", id -> EntityType.Builder.<AccumulatingEnergyEntity>of(AccumulatingEnergyEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(1).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<SlimeSpikeEntity>> SLIME_SPIKE = ENTITIES.register("slime_spike", id -> EntityType.Builder.of(SlimeSpikeEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<DeerclopsThrownIceProjectile>> THROWN_ICE_PROJECTILE = ENTITIES.register("thrown_ice_projectile", id -> EntityType.Builder.of(DeerclopsThrownIceProjectile::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeerclopsIcePillarProjectile>> ICE_PILLAR = ENTITIES.register("ice_pillar", id -> EntityType.Builder.of(DeerclopsIcePillarProjectile::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeerclopsShadowHandProjectile>> SHADOW_HAND = ENTITIES.register("shadow_hand", id -> EntityType.Builder.of(DeerclopsShadowHandProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<DestroyerLaserProjectile>> DESTROYER_LASER = ENTITIES.register("destroyer_laser", id -> EntityType.Builder.of(DestroyerLaserProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HillLavaPillarProjectile>> HILL_LAVA_PILLAR = ENTITIES.register("hill_lava_pillar", id -> EntityType.Builder.of(HillLavaPillarProjectile::new, MobCategory.MISC).sized(1.0F, 0.2F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> HILL_FIRE_BOUND = ENTITIES.register("hill_fire_bound", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.HILL_FIRE_BOUND), MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> WALL_OF_FLESH_LASER = ENTITIES.register("wall_of_flesh_laser", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.WALL_OF_FLESH_LASER), MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PrimeLaserProjectile>> PRIME_LASER = ENTITIES.register("prime_laser", id -> EntityType.Builder.of(PrimeLaserProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraProjectile>> PLANTERA_SEED = ENTITIES.register("plantera_seed", id -> EntityType.Builder.<PlanteraProjectile>of((type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.SEED), MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraProjectile>> PLANTERA_THORN_BALL = ENTITIES.register("plantera_thorn_ball", id -> EntityType.Builder.<PlanteraProjectile>of((type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.THORN_BALL), MobCategory.MISC).sized(0.7F, 0.7F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PlanteraProjectile>> PLANTERA_SPORE = ENTITIES.register("plantera_spore", id -> EntityType.Builder.<PlanteraProjectile>of((type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.SPORE), MobCategory.MISC).sized(0.45F, 0.45F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<TwinEyeProjectile>> RETINAZER_LASER = ENTITIES.register("retinazer_laser", id -> EntityType.Builder.<TwinEyeProjectile>of((type, level) -> new TwinEyeProjectile(type, level, TwinEyeProjectile.Variant.LASER), MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TwinEyeProjectile>> SPAZMATISM_FLAME = ENTITIES.register("spazmatism_flame", id -> EntityType.Builder.<TwinEyeProjectile>of((type, level) -> new TwinEyeProjectile(type, level, TwinEyeProjectile.Variant.CURSED_FLAME), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<AncientLightProjectile>> ANCIENT_LIGHT = ENTITIES.register("ancient_light", id -> EntityType.Builder.of(AncientLightProjectile::new, MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CultistProjectile>> CULTIST_FIREBALL = ENTITIES.register("cultist_fireball", id -> EntityType.Builder.<CultistProjectile>of((type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.FIREBALL), MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CultistProjectile>> CULTIST_ICE_MIST = ENTITIES.register("cultist_ice_mist", id -> EntityType.Builder.<CultistProjectile>of((type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.ICE_MIST), MobCategory.MISC).sized(0.8F, 0.8F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<CultistProjectile>> CULTIST_LIGHTNING_ORB = ENTITIES.register("cultist_lightning_orb", id -> EntityType.Builder.<CultistProjectile>of((type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.LIGHTNING_ORB), MobCategory.MISC).sized(0.7F, 0.7F).clientTrackingRange(10).updateInterval(1).noSave().noSummon().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PrimeCannonballProjectile>> PRIME_CANNONBALL = ENTITIES.register("prime_cannonball", id -> EntityType.Builder.of(PrimeCannonballProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));

    // 魔法
    public static final DeferredHolder<EntityType<?>, EntityType<WhipAttackEntity>> WHIP_ATTACK = ENTITIES.register("whip_attack", id -> EntityType.Builder.of(WhipAttackEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ChesterEntity>> CHESTER = withAttributes(registerStorageCompanion("chester", ChesterEntity::new), () -> CreatureAttributeBuilder.critter().maxHealth(20).movementSpeed(0.35).flyingSpeed(0.45).followRange(32).build());
    public static final DeferredHolder<EntityType<?>, EntityType<FlyingPiggyBankEntity>> FLYING_PIGGY_BANK = withAttributes(registerStorageCompanion("piggy_bank", FlyingPiggyBankEntity::new), () -> CreatureAttributeBuilder.critter().maxHealth(20).movementSpeed(0.35).flyingSpeed(0.45).followRange(32).build());

    public static final DeferredHolder<EntityType<?>, EntityType<ChikCrystalProjectile>> CHIK_CRYSTAL = ENTITIES.register("chik_crystal", id -> EntityType.Builder.of(ChikCrystalProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<KrakenWaveProjectile>> KRAKEN_WAVE = ENTITIES.register("kraken_wave", id -> EntityType.Builder.of(KrakenWaveProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CthulhuEyeProjectile>> CTHULHU_EYE_PROJECTILE = ENTITIES.register("cthulhu_eye_projectile", id -> EntityType.Builder.of(CthulhuEyeProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CascadeFireProjectile>> CASCADE_FIRE = ENTITIES.register("cascade_fire", id -> EntityType.Builder.of(CascadeFireProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TerrarianProjectile>> TERRARIAN_PROJECTILE = ENTITIES.register("terrarian_projectile", id -> EntityType.Builder.of(TerrarianProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<YoyoEntity>> YOYO = ENTITIES.register("yoyo", id -> EntityType.Builder.of(YoyoEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));

    private static <E extends BaseMinecartEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerMinecart(String name, EntityType.EntityFactory<E> factory) {
        return ENTITIES.register(name, id -> EntityType.Builder.of(factory, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8).build(id.toString()));
    }

    private static <E extends AbstractHookEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerHook(String name, EntityType.EntityFactory<E> supplier) {
        int updateInterval = SableHelper.IS_LOADED ? 1 : 20;
        return ENTITIES.register(name, id -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(updateInterval).build(id.toString()));
    }

    private static <E extends BaseBombEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerBomb(String name, EntityType.EntityFactory<E> supplier, float size) {
        return ENTITIES.register(name, id -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(size, size).clientTrackingRange(4).updateInterval(10).fireImmune().build(id.toString()));
    }

    private static <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> registerStorageCompanion(String name, EntityType.EntityFactory<E> factory) {
        return ENTITIES.register(name, id -> EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(1.0F, 1.0F)
                .clientTrackingRange(10)
                .updateInterval(1)
                .noSummon()
                .noSave()
                .build(id.toString()));
    }

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownRockProjectile>> THROWN_ROCK = ENTITIES.register("thrown_rock", id -> EntityType.Builder.of(ThrownRockProjectile::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DandelionSeed>> DANDELION_SEED = ENTITIES.register("dandelion_seed", id -> EntityType.Builder.of(DandelionSeed::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HopliteJavelin>> HOPLITE_JAVELIN = ENTITIES.register("hoplite_javelin", id -> EntityType.Builder.of(HopliteJavelin::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostMonsterProjectile>> FROST_BLAST = ENTITIES.register("frost_blast", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.BLAST), MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostMonsterProjectile>> ICEWATER_SPIT = ENTITIES.register("icewater_spit", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.SPIT), MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostMonsterProjectile>> FROST_BEAM = ENTITIES.register("frost_beam", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.BEAM), MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<AntlionSandBall>> ANTLION_SAND_BALL = ENTITIES.register("antlion_sand_ball", id -> EntityType.Builder.of(AntlionSandBall::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DesertSpiritCurse>> DESERT_SPIRIT_CURSE = ENTITIES.register("desert_spirit_curse", id -> EntityType.Builder.of(DesertSpiritCurse::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileDemonScytheProjectile>> HOSTILE_DEMON_SCYTHE = ENTITIES.register("hostile_demon_scythe_projectile", id -> EntityType.Builder.of(HostileDemonScytheProjectile::new, MobCategory.MISC).sized(1.5F, 1.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PaladinHammerProjectile>> PALADIN_HAMMER_PROJECTILE = ENTITIES.register("paladin_hammer_projectile", id -> EntityType.Builder.of(PaladinHammerProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NimbusRain>> NIMBUS_RAIN = ENTITIES.register("nimbus_rain", id -> EntityType.Builder.of(NimbusRain::new, MobCategory.MISC).sized(0.1F, 1.0F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HarpyFeatherProjectile>> HARPY_FEATHER = ENTITIES.register("harpy_feather_projectile", id -> EntityType.Builder.of(HarpyFeatherProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HornetStingerProjectile>> HORNET_STINGER = ENTITIES.register("hornet_stinger_projectile", id -> EntityType.Builder.of(HornetStingerProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<UnholyTridentProjectile>> UNHOLY_TRIDENT = ENTITIES.register("unholy_trident", id -> EntityType.Builder.of(UnholyTridentProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PlantSpit>> CLINGER_FLAME = ENTITIES.register("clinger_flame", id -> EntityType.Builder.<PlantSpit>of((type, level) -> new PlantSpit(type, level, PlantSpit.Kind.CURSED_FLAME), MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PlantSpit>> FUNGI_SPORE = ENTITIES.register("fungi_spore", id -> EntityType.Builder.<PlantSpit>of((type, level) -> new PlantSpit(type, level, PlantSpit.Kind.SPORE), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpiderWebSpit>> SPIDER_WEB_SPIT = ENTITIES.register("spider_web_spit", id -> EntityType.Builder.of(SpiderWebSpit::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> RUNE_BLAST = ENTITIES.register("rune_blast", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.RUNE_BLAST), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> DARK_CASTER_PROJECTILE = ENTITIES.register("dark_caster_projectile", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.WATER_SPHERE), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> CHAOS_BALL_PROJECTILE = ENTITIES.register("chaos_ball_projectile", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.CHAOS_BALL), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> SHADOW_BEAM_PROJECTILE = ENTITIES.register("shadow_beam_projectile", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.SHADOW_BEAM), MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> INFERNO_BOLT_PROJECTILE = ENTITIES.register("inferno_bolt_projectile", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.INFERNO_BOLT), MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> LOST_SOUL_PROJECTILE = ENTITIES.register("lost_soul_projectile", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.LOST_SOUL), MobCategory.MISC).sized(0.45F, 0.45F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> VILE_SPIT_PROJECTILE = ENTITIES.register("vile_spit_projectile", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.VILE_SPIT), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> FIRE_IMP_PROJECTILE = ENTITIES.register("fire_imp_projectile", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.FIRE_IMP), MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HostileParticleProjectile>> GASTROPOD_PROJECTILE = ENTITIES.register("gastropod_projectile", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.GASTROPOD), MobCategory.MISC).sized(0.45F, 0.45F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PirateShot>> PIRATE_BULLET = ENTITIES.register("pirate_bullet", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.BULLET), MobCategory.MISC).sized(0.15F, 0.15F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PirateShot>> PIRATE_FLAMING_ARROW = ENTITIES.register("pirate_flaming_arrow", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.ARROW), MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<PirateShot>> PIRATE_CANNONBALL = ENTITIES.register("pirate_cannonball", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.CANNONBALL), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<NPCWeaponProjectile>> NPC_WEAPON_PROJECTILE = ENTITIES.register("npc_weapon_projectile", id -> EntityType.Builder.<NPCWeaponProjectile>of(NPCWeaponProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CyborgExplosiveProjectile>> CYBORG_EXPLOSIVE = ENTITIES.register("cyborg_explosive", id -> EntityType.Builder.<CyborgExplosiveProjectile>of(CyborgExplosiveProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BoomerangProjectile>> BOOMERANG_PROJECTILE = ENTITIES.register("boomerang_projectile", id -> EntityType.Builder.of(BoomerangProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<NPCShadowflameSkullProjectile>> NPC_SHADOWFLAME_SKULL = ENTITIES.register("npc_shadowflame_skull", id -> EntityType.Builder.of(NPCShadowflameSkullProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<BaseBulletEntity>> BASE_BULLET_ENTITY = ENTITIES.register("base_bullet", id -> EntityType.Builder.<BaseBulletEntity>of(BaseBulletEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CustomBulletEntity>> GRAVITY_BULLET_ENTITY = ENTITIES.register("gravity_bullet", id -> EntityType.Builder.<CustomBulletEntity>of(CustomBulletEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(id.toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<RideableUnicornMountEntity>> RIDEABLE_UNICORN = ENTITIES.register("rideable_unicorn", id -> EntityType.Builder.of(RideableUnicornMountEntity::new, MobCategory.MISC).sized(1.2F, 1.6F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RideableLavaSharkMountEntity>> RIDEABLE_LAVA_SHARK = ENTITIES.register("rideable_lava_shark", id -> EntityType.Builder.of(RideableLavaSharkMountEntity::new, MobCategory.MISC).sized(1.5F, 0.8F).fireImmune().clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RideableSlimeMountEntity>> RIDEABLE_SLIME = ENTITIES.register("rideable_slime", id -> EntityType.Builder.of(RideableSlimeMountEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RideableBeeMountEntity>> RIDEABLE_BEE = ENTITIES.register("rideable_bee", id -> EntityType.Builder.of(RideableBeeMountEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));

    public static <T extends LivingEntity> DeferredHolder<EntityType<?>, EntityType<T>> withAttributes(DeferredHolder<EntityType<?>, EntityType<T>> type, Supplier<CreatureAttributeBuilder.Definition> attributes) {
        ATTRIBUTES.put(type, attributes);
        return type;
    }

    public static @Nullable CreatureAttributeBuilder.Definition creatureAttributes(EntityType<?> type) {
        return CREATURE_DEFINITIONS.get(type);
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
    }
}
