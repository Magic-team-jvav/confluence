package org.confluence.mod.common.init;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.entitiy.EmptyEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.*;
import org.confluence.mod.common.entity.fishing.BaseFishingHook;
import org.confluence.mod.common.entity.fishing.BloodyFishingHook;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.entity.fishing.HotlineFishingHook;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.entity.hook.*;
import org.confluence.mod.common.entity.minecart.*;
import org.confluence.mod.common.entity.projectile.*;
import org.confluence.mod.common.entity.projectile.arrow.*;
import org.confluence.mod.common.entity.projectile.bomb.*;
import org.confluence.mod.common.entity.projectile.boulder.*;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.spear.*;
import org.confluence.mod.common.entity.projectile.strip.CrystalVileShardProjectile;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.entity.projectile.sword.*;

import java.util.function.Function;

/// Fast Link:
///
/// 渲染器 [org.confluence.mod.client.event.ModClientEvents#registerEntityRenderers]
///
/// 发包   [org.confluence.mod.common.event.ModEvents#registerPayloadHandlers]
///
/// 属性   [org.confluence.mod.common.event.ModEvents#entityAttributeCreation]
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    // 牢枕专用
    public static final RegistryObject<EntityType<EmptyEntity>> EMPTY_ENTITY = register("empty_entity", id -> EntityType.Builder.of(EmptyEntity::new, MobCategory.MISC).build(id.toString()));

    // 炸弹
    public static final RegistryObject<EntityType<BaseBombEntity>> BOMB_ENTITY = registerBomb("bomb_entity", BaseBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<BouncyBombEntity>> BOUNCY_BOMB_ENTITY = registerBomb("bouncy_bomb_entity", BouncyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<ScarabBombEntity>> SCARAB_BOMB_ENTITY = registerBomb("scarab_bomb_entity", ScarabBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<StickyBombEntity>> STICKY_BOMB_ENTITY = registerBomb("sticky_bomb_entity", StickyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<SmokeBombEntity>> SMOKE_BOMB_ENTITY = registerBomb("smoke_bomb_entity", SmokeBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<BombFishEntity>> BOMB_FISH_ENTITY = registerBomb("bomb_fish_entity", BombFishEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<BaseGrenadeEntity>> GRENADE = registerBomb("grenade", BaseGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final RegistryObject<EntityType<BouncyGrenadeEntity>> BOUNCY_GRENADE = registerBomb("bouncy_grenade", BouncyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final RegistryObject<EntityType<StickyGrenadeEntity>> STICKY_GRENADE = registerBomb("sticky_grenade", StickyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final RegistryObject<EntityType<BeenadeEntity>> BEENADE = registerBomb("beenade", BeenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final RegistryObject<EntityType<BaseDynamiteEntity>> DYNAMITE = registerBomb("dynamite", BaseDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final RegistryObject<EntityType<BouncyDynamiteEntity>> BOUNCY_DYNAMITE = registerBomb("bouncy_dynamite", BouncyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final RegistryObject<EntityType<StickyDynamiteEntity>> STICKY_DYNAMITE = registerBomb("sticky_dynamite", StickyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final RegistryObject<EntityType<BaseDirtBombEntity>> DIRT_BOMB = registerBomb("dirt_bomb", BaseDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<StickyDirtBombEntity>> STICKY_DIRT_BOMB = registerBomb("sticky_dirt_bomb", StickyDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<DryBombEntity>> DRY_BOMB = registerBomb("dry_bomb", DryBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<LiquidBombEntity>> WET_BOMB = registerBomb("wet_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<LiquidBombEntity>> LAVA_BOMB = registerBomb("lava_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<LiquidBombEntity>> HONEY_BOMB = registerBomb("honey_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);

    // 魔法
    public static final RegistryObject<EntityType<BaseManaStaffProjectileEntity>> BASE_MANA_STAFF = register("base_mana_staff", id -> EntityType.Builder.<BaseManaStaffProjectileEntity>of(BaseManaStaffProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<VilethronProjectile>> VILETHRON = register("vilethron", id -> EntityType.Builder.<VilethronProjectile>of(VilethronProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<CrystalVileShardProjectile>> CRYSTAL_VILE_SHARD = register("crystal_vile_shard", id -> EntityType.Builder.<CrystalVileShardProjectile>of(CrystalVileShardProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<HurtnadoProjectile>> HURTNADO = register("hurtnado", id -> EntityType.Builder.<HurtnadoProjectile>of(HurtnadoProjectile::new, MobCategory.MISC).sized(0.8F, 1.2F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<WaterStreamProjectile>> WATER_STREAM = register("water_stream", id -> EntityType.Builder.<WaterStreamProjectile>of(WaterStreamProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<WaterBoltProjectile>> WATER_BOLT = register("water_bolt", id -> EntityType.Builder.<WaterBoltProjectile>of(WaterBoltProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<BallOfFireProjectile>> BALL_OF_FIRE = register("ball_of_fire", id -> EntityType.Builder.<BallOfFireProjectile>of(BallOfFireProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<EffectThrownPotion>> EFFECT_THROWN_POTION = register("effect_thrown_potion", id -> EntityType.Builder.<EffectThrownPotion>of(EffectThrownPotion::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).build(id.toString()));
    public static final RegistryObject<EntityType<MagicDaggerProjectile>> MAGIC_DAGGER = register("magic_dagger", id -> EntityType.Builder.<MagicDaggerProjectile>of(MagicDaggerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CrystalStormProjectile>> CRYSTAL_STORM = register("crystal_storm", id -> EntityType.Builder.<CrystalStormProjectile>of(CrystalStormProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CursedFlamesProjectile>> CURSED_FLAMES = register("cursed_flames", id -> EntityType.Builder.<CursedFlamesProjectile>of(CursedFlamesProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<BallOfFrostProjectile>> BALL_OF_FROST = register("ball_of_frost", id -> EntityType.Builder.<BallOfFrostProjectile>of(BallOfFrostProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<DemonScytheProjectile>> DEMON_SCYTHE = register("demon_scythe", id -> EntityType.Builder.<DemonScytheProjectile>of(DemonScytheProjectile::new, MobCategory.MISC).sized(1.5F, 1.5F).build(id.toString()));
    public static final RegistryObject<EntityType<SkullProjectile>> SKULL = register("skull", id -> EntityType.Builder.<SkullProjectile>of(SkullProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).build(id.toString()));
    public static final RegistryObject<EntityType<CloudProjectile>> BLOOD_CLOUD = register("blood_cloud", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final RegistryObject<EntityType<RainProjectile>> BLOOD_RAIN = register("blood_rain", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CloudProjectile>> RAIN_CLOUD = register("rain_cloud", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final RegistryObject<EntityType<RainProjectile>> RAIN = register("rain", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final RegistryObject<EntityType<GoldenShowerProjectile>> GOLDEN_SHOWER = register("golden_shower", id -> EntityType.Builder.<GoldenShowerProjectile>of(GoldenShowerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<MagicMissileProjectile>> MAGIC_MISSILE = register("magic_missile", id -> EntityType.Builder.<MagicMissileProjectile>of(MagicMissileProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<FlamelashProjectile>> FLAMELASH = register("flamelash", id -> EntityType.Builder.<FlamelashProjectile>of(FlamelashProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<RainbowProjectile>> RAINBOW = register("rainbow", id -> EntityType.Builder.<RainbowProjectile>of(RainbowProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<SkyFractureProjectile>> SKY_FRACTURE = register("sky_fracture", id -> EntityType.Builder.<SkyFractureProjectile>of(SkyFractureProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CrystalChargeProjectile>> CRYSTAL_CHARGE_1 = register("crystal_charge_1", id -> EntityType.Builder.<CrystalChargeProjectile>of(CrystalChargeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CrystalChargeProjectile>> CRYSTAL_CHARGE_2 = register("crystal_charge_2", id -> EntityType.Builder.<CrystalChargeProjectile>of(CrystalChargeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));

    // 剑气
    public static final RegistryObject<EntityType<IceBladeSwordProjectile>> ICE_BLADE_SWORD = register("ice_blade_sword", id -> EntityType.Builder.of(IceBladeSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<StarFuryProjectile>> STAR_FURY = register("star_fury", id -> EntityType.Builder.of(StarFuryProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));//星怒弹幕
    public static final RegistryObject<EntityType<EnchantedSwordProjectile>> ENCHANTED_SWORD = register("enchanted_sword", id -> EntityType.Builder.of(EnchantedSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<LightBaneProjectile>> LIGHTS_BANE = register("lights_bane", id -> EntityType.Builder.of(LightBaneProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));
    public static final RegistryObject<EntityType<GrassSwordProjectile>> GRASS = register("grass", id -> EntityType.Builder.of(GrassSwordProjectile::new, MobCategory.MISC).sized(2F, 2F).build(id.toString()));
    public static final RegistryObject<EntityType<BeeKeeperProjectile>> BEE = register("bee", id -> EntityType.Builder.of(BeeKeeperProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<NightEdgeProjectile>> NIGHTS_EDGE = register("nights_edge", id -> EntityType.Builder.of(NightEdgeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));

    // 弓箭
    public static final RegistryObject<EntityType<BaseArrowEntity>> BASE_ARROW = register("arrow", id -> EntityType.Builder.<BaseArrowEntity>of(BaseArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<BeeArrowEntity>> BEE_ARROW = register("bee_arrow", id -> EntityType.Builder.<BeeArrowEntity>of(BeeArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<HellBatArrowEntity>> HELL_BAT_ARROW = register("hell_bat_arrow", id -> EntityType.Builder.<HellBatArrowEntity>of(HellBatArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<DriveAwayArrowEntity>> DRIVE_AWAY_ARROW = register("drive_away_arrow", id -> EntityType.Builder.<DriveAwayArrowEntity>of(DriveAwayArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<FlamingArrowEntity>> FLAMING_ARROW = register("flaming_arrow", id -> EntityType.Builder.<FlamingArrowEntity>of(FlamingArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<UnholyArrowEntity>> UNHOLY_ARROW = register("unholy_arrow", id -> EntityType.Builder.<UnholyArrowEntity>of(UnholyArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<StarArrowEntity>> STAR_ARROW = register("star_arrow", id -> EntityType.Builder.<StarArrowEntity>of(StarArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<HellfireArrowEntity>> HELLFIRE_ARROW = register("hellfire_arrow", id -> EntityType.Builder.<HellfireArrowEntity>of(HellfireArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<FrostburnArrowEntity>> FROSTBURN_ARROW = register("frostburn_arrow", id -> EntityType.Builder.<FrostburnArrowEntity>of(FrostburnArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<BoneArrowEntity>> BONE_ARROW = register("bone_arrow", id -> EntityType.Builder.<BoneArrowEntity>of(BoneArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ShimmerArrowEntity>> SHIMMER_ARROW = register("shimmer_arrow", id -> EntityType.Builder.<ShimmerArrowEntity>of(ShimmerArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<FossilArrowEntity>> FOSSIL_ARROW = register("fossil_arrow", id -> EntityType.Builder.<FossilArrowEntity>of(FossilArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<FlyFishArrowEntity>> FLY_FISH_ARROW = register("fly_fish_arrow", id -> EntityType.Builder.<FlyFishArrowEntity>of(FlyFishArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<DeveloperArrowEntity>> DEVELOPER_ARROW = register("developer_arrow", id -> EntityType.Builder.<DeveloperArrowEntity>of(DeveloperArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));

    // 其它
    public static final RegistryObject<EntityType<BoulderEntity>> BOULDER = register("boulder", id -> EntityType.Builder.<BoulderEntity>of(BoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<FollowerBoulderEntity>> FOLLOWER_BOULDER = register("follower_boulder", id -> EntityType.Builder.<FollowerBoulderEntity>of(FollowerBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<ExplodeBoulderEntity>> EXPLODE_BOULDER = register("explode_boulder", id -> EntityType.Builder.<ExplodeBoulderEntity>of(ExplodeBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<RollingCactusBoulderEntity>> ROLLING_CACTUS_BOULDER = register("rolling_cactus_boulder", id -> EntityType.Builder.<RollingCactusBoulderEntity>of(RollingCactusBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<RollingCactusBoulderEntity.SpikeProjectile>> ROLLING_CACTUS_SPIKE = register("rolling_cactus_spike", id -> EntityType.Builder.of(RollingCactusBoulderEntity.SpikeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<TombstoneBoulderEntity>> TOMBSTONE_BOULDER = register("tombstone_boulder", id -> EntityType.Builder.<TombstoneBoulderEntity>of(TombstoneBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<BouncyBoulderEntity>> BOUNCY_BOULDER = register("bouncy_boulder", id -> EntityType.Builder.<BouncyBoulderEntity>of(BouncyBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<GhoulderEntity>> GHOULDER = register("ghoulder", id -> EntityType.Builder.<GhoulderEntity>of(GhoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<LavaBoulderEntity>> LAVA_BOULDER = register("lava_boulder", id -> EntityType.Builder.<LavaBoulderEntity>of(LavaBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<PooBoulderEntity>> POO_BOULDER = register("poo_boulder", id -> EntityType.Builder.<PooBoulderEntity>of(PooBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<SpiderBoulderEntity>> SPIDER_BOULDER = register("spider_boulder", id -> EntityType.Builder.<SpiderBoulderEntity>of(SpiderBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<RainbowBoulderEntity>> RAINBOW_BOULDER = register("rainbow_boulder", id -> EntityType.Builder.<RainbowBoulderEntity>of(RainbowBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<LifecrystalBoulderEntity>> LIFECRYSTAL_BOULDER = register("lifecrystal_boulder", id -> EntityType.Builder.<LifecrystalBoulderEntity>of(LifecrystalBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<Boulder3x3Entity>> BOULDER_3X = register("boulder_3x", id -> EntityType.Builder.<Boulder3x3Entity>of(Boulder3x3Entity::new, MobCategory.MISC).sized(3, 3).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> THROWN_KNIVE = register("thrown_knive", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> BONE_THROWN_KNIVE = register("bone_thrown_knive", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> FROST_DAGGERFISH = register("frost_daggerfish", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> DUNGEON_DEMON_BONE = register("dungeon_demon_bone", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> SHURIKEN = register("shuriken", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> JAVELIN = register("javelin", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<RopeCoilsProjectile>> ROPE_COILS = register("rope_coils", id -> EntityType.Builder.<RopeCoilsProjectile>of(RopeCoilsProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<IceTofuBrickProjectile>> ICE_TOFU_BRICK = register("ice_tofu_brick", id -> EntityType.Builder.<IceTofuBrickProjectile>of(IceTofuBrickProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<SpikyBallProjectile>> SPIKY_BALL = register("spiky_ball", id -> EntityType.Builder.<SpikyBallProjectile>of(SpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrownWaterProjectile>> THROWN_WATER = register("thrown_water", id -> EntityType.Builder.<ThrownWaterProjectile>of(ThrownWaterProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final RegistryObject<EntityType<FlowerPetalProjectile>> FLOWER_PETAL = register("flower_petal", id -> EntityType.Builder.<FlowerPetalProjectile>of(FlowerPetalProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final RegistryObject<EntityType<TitaniumShardsProjectile>> TITANIUM_SHARDS = register("titanium_shards", id -> EntityType.Builder.<TitaniumShardsProjectile>of(TitaniumShardsProjectile::new, MobCategory.MISC).sized(0, 0).fireImmune().noSummon().noSave().build(id.toString()));
    public static final RegistryObject<EntityType<FallingStarItemEntity>> FALLING_STAR = register("falling_star", id -> EntityType.Builder.<FallingStarItemEntity>of(FallingStarItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final RegistryObject<EntityType<TreasureBagItemEntity>> TREASURE_BAG = register("treasure_bag", id -> EntityType.Builder.<TreasureBagItemEntity>of(TreasureBagItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final RegistryObject<EntityType<CoinPortalEntity>> COIN_PORTAL = register("coin_portal", id -> EntityType.Builder.<CoinPortalEntity>of(CoinPortalEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<ThrownPowderEntity>> THROWN_POWDER = register("thrown_powder", id -> EntityType.Builder.<ThrownPowderEntity>of(ThrownPowderEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().build(id.toString()));
    public static final RegistryObject<EntityType<DeadBodyPartEntity>> BODY_PART = register("body_part", id -> EntityType.Builder.<DeadBodyPartEntity>of(DeadBodyPartEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().noSave().noSummon().build(id.toString()));
    public static final RegistryObject<EntityType<FlameCloudEntity>> FLAME_CLOUD = register("flame_cloud", id -> EntityType.Builder.<FlameCloudEntity>of(FlameCloudEntity::new, MobCategory.MISC).sized(5, 5).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<SuperSpikyBallProjectile>> SUPER_SPIKY_BALL = register("super_spiky_ball", id -> EntityType.Builder.<SuperSpikyBallProjectile>of(SuperSpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<SpearEntity>> SPEAR = register("spear", id -> EntityType.Builder.<SpearEntity>of(SpearEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<StormSpearProjectile>> STORM_SPEAR_SHOT = register("storm_spear_shot", id -> EntityType.Builder.of(StormSpearProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final RegistryObject<EntityType<SporeCloudProjectile>> SPORE_CLOUD = register("spore_cloud", id -> EntityType.Builder.of(SporeCloudProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(64).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<NorthPoleProjectile>> NORTH_POLE = register("north_pole", id -> EntityType.Builder.of(NorthPoleProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final RegistryObject<EntityType<NorthPoleSubProjectile>> NORTH_POLE_SUB = register("north_pole_sub", id -> EntityType.Builder.of(NorthPoleSubProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<MushroomProjectile>> MUSHROOM = register("mushroom", id -> EntityType.Builder.of(MushroomProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final RegistryObject<EntityType<GhastlyProjectile>> GHASTLY = register("ghastly", id -> EntityType.Builder.of(GhastlyProjectile::new, MobCategory.MISC).sized(2.5F, 2.5F).clientTrackingRange(6).fireImmune().build(id.toString()));

    // 鱼钩
    public static final RegistryObject<EntityType<BaseFishingHook>> BASE_FISHING_HOOK = register("base_fishing_hook", id -> EntityType.Builder.<BaseFishingHook>of(BaseFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final RegistryObject<EntityType<HotlineFishingHook>> HOTLINE_FISHING_HOOK = register("hotline_fishing_hook", id -> EntityType.Builder.<HotlineFishingHook>of(HotlineFishingHook::new, MobCategory.MISC).noSave().noSummon().fireImmune().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final RegistryObject<EntityType<CurioFishingHook>> CURIO_FISHING_HOOK = register("curio_fishing_hook", id -> EntityType.Builder.<CurioFishingHook>of(CurioFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final RegistryObject<EntityType<BloodyFishingHook>> BLOODY_FISHING_HOOK = register("bloody_fishing_hook", id -> EntityType.Builder.<BloodyFishingHook>of(BloodyFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));

    // 钩爪
    public static final RegistryObject<EntityType<BaseHookEntity>> BASE_HOOK = registerHook("base_hook", BaseHookEntity::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> WEB_SLINGER = registerHook("web_slinger", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> SKELETRON_HAND = registerHook("skeletron_hand", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> SLIME_HOOK = registerHook("slime_hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> FISH_HOOK = registerHook("fish_hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> IVY_WHIP = registerHook("ivy_whip", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> BAT_HOOK = registerHook("bat_hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> CANDY_CANE_HOOK = registerHook("candy_cane_hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<DualHookEntity>> DUAL_HOOK = registerHook("dual_hook", DualHookEntity::new);
    public static final RegistryObject<EntityType<HookOfDissonanceEntity>> HOOK_OF_DISSONANCE = registerHook("hook_of_dissonance", HookOfDissonanceEntity::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> THORN_HOOK = registerHook("thorn_hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<MimicHookEntity>> MIMIC_HOOK = registerHook("mimic_hook", MimicHookEntity::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> ANTI_GRAVITY_HOOK = registerHook("anti_gravity_hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> SPOOKY_HOOK = registerHook("spooky_hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> CHRISTMAS_HOOK = registerHook("christmas_hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<LunarHookEntity>> LUNAR_HOOK = registerHook("lunar_hook", LunarHookEntity::new);
    /* todo 静止钩 */

    // 连枷
    public static final RegistryObject<EntityType<BaseFlailEntity>> FLAIL_ENTITY = register("flail", id -> EntityType.Builder.of(BaseFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).noSave().build(id.toString()));

    // 矿车
    public static final RegistryObject<EntityType<BaseMinecartEntity>> VANILLA_MINECART = registerMinecart("vanilla_minecart", BaseMinecartEntity::new);
    public static final RegistryObject<EntityType<BaseMinecartEntity>> WOODEN_MINECART = registerMinecart("wooden_minecart", BaseMinecartEntity::new);
    public static final RegistryObject<EntityType<GenericMinecartEntity>> GENERIC_MINECART = registerMinecart("generic_minecart", GenericMinecartEntity::new);
    public static final RegistryObject<EntityType<MechanicalCartEntity>> MECHANICAL_CART = registerMinecart("mechanical_cart", MechanicalCartEntity::new);
    public static final RegistryObject<EntityType<MinecarpEntity>> MINECARP = registerMinecart("minecarp", MinecarpEntity::new);
    public static final RegistryObject<EntityType<DemonicHellcartEntity>> DEMONIC_HELLCART = registerMinecart("demonic_hellcart", DemonicHellcartEntity::new);
    public static final RegistryObject<EntityType<MeowmereMinecartEntity>> MEOWMERE_MINECART = registerMinecart("meowmere_minecart", MeowmereMinecartEntity::new);
    public static final RegistryObject<EntityType<DiggingMolecartEntity>> DIGGING_MOLECART = registerMinecart("digging_molecart", DiggingMolecartEntity::new);

    public static final RegistryObject<EntityType<BestiaryEntryDisplay>> BESTIARY_ENTRY_DISPLAY = register("bestiary_entry_display", id -> EntityType.Builder.of(BestiaryEntryDisplay::new, MobCategory.MISC).sized(1, 1).build(id.toString()));

    // 子弹
    public static final RegistryObject<EntityType<StarCannonBulletEntity>> STAR_CANNON_BULLET = register("star_cannon_bullet", id -> EntityType.Builder.<StarCannonBulletEntity>of(StarCannonBulletEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).build(id.toString()));
    public static final RegistryObject<EntityType<BeeGunBullet>> BEE_GUN_BULLET = register("bee_gun_bullet", id -> EntityType.Builder.<BeeGunBullet>of(BeeGunBullet::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<BaseBulletEntity>> BASE_BULLET_ENTITY = register("base_bullet", id -> EntityType.Builder.<BaseBulletEntity>of(BaseBulletEntity::new, MobCategory.MISC).sized(0.1f, 0.1f).build(id.toString()));
    public static final RegistryObject<EntityType<CustomBulletEntity>> GRAVITY_BULLET_ENTITY = register("gravity_bullet", id -> EntityType.Builder.<CustomBulletEntity>of(CustomBulletEntity::new, MobCategory.MISC).sized(0.1f, 0.1f).build(id.toString()));

    public static final RegistryObject<EntityType<RainbowSheep>> RAINBOW_SHEEP = register("rainbow_sheep", id -> EntityType.Builder.of(RainbowSheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F)/* todo .eyeHeight(1.235F).passengerAttachments(1.2375F)*/.clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<InverseEnderMan>> INVERSE_ENDERMAN = register("inverse_enderman", id -> InverseEntityType.create(InverseEnderMan::new, MobCategory.MONSTER, id.toString(), builder -> builder.sized(0.6F, 2.9F)/* todo .eyeHeight(2.55F).passengerAttachments(2.80625F)*/.clientTrackingRange(8)));

    public static final RegistryObject<EntityType<AccumulatingEnergyEntity>> ACCUMULATING_ENERGY = register("accumulating_energy", id -> EntityType.Builder.<AccumulatingEnergyEntity>of(AccumulatingEnergyEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(1).build(id.toString()));

    private static <E extends BaseMinecartEntity> RegistryObject<EntityType<E>> registerMinecart(String name, EntityType.EntityFactory<E> factory) {
        return register(name, id -> EntityType.Builder.of(factory, MobCategory.MISC).sized(0.98F, 0.7F)/* todo .passengerAttachments(0.1875F)*/.clientTrackingRange(8).build(id.toString()));
    }

    private static <E extends AbstractHookEntity> RegistryObject<EntityType<E>> registerHook(String name, EntityType.EntityFactory<E> supplier) {
        int updateInterval = 20;
        return register(name, id -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(updateInterval).build(id.toString()));
    }

    private static <E extends BaseBombEntity> RegistryObject<EntityType<E>> registerBomb(String name, EntityType.EntityFactory<E> supplier, float size) {
        return register(name, id -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(size, size).clientTrackingRange(4).updateInterval(10).fireImmune().build(id.toString()));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Function<ResourceLocation, EntityType<T>> function) {
        return PortDeferredRegisterExtension.register(ENTITIES, name, function);
    }
}
