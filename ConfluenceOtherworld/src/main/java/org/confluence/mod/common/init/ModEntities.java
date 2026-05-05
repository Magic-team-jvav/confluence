package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.*;
import org.confluence.mod.common.entity.fishing.BaseFishingHook;
import org.confluence.mod.common.entity.fishing.BloodyFishingHook;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.entity.fishing.HotlineFishingHook;
import org.confluence.mod.common.entity.hook.*;
import org.confluence.mod.common.entity.minecart.*;
import org.confluence.mod.common.entity.projectile.*;
import org.confluence.mod.common.entity.projectile.bomb.*;
import org.confluence.mod.common.entity.projectile.boulder.*;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.range.arrow.BeeArrow;
import org.confluence.mod.common.entity.projectile.range.arrow.DriveAwayArrow;
import org.confluence.mod.common.entity.projectile.range.arrow.HellBatArrowEntity;
import org.confluence.mod.common.entity.projectile.strip.CrystalVileShardProjectile;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.entity.projectile.sword.*;
import org.confluence.mod.integration.sable.SableHelper;

/// Fast Link:
///
/// 渲染器 [org.confluence.mod.client.event.ModClientEvents#registerEntityRenderers]
///
/// 发包   [org.confluence.mod.common.event.ModEvents#registerPayloadHandlers]
///
/// 属性   [org.confluence.mod.common.event.ModEvents#entityAttributeCreation]
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Confluence.MODID);

    // 牢枕专用
    public static final DeferredHolder<EntityType<?>, EntityType<EmptyEntity>> EMPTY_ENTITY = ENTITIES.register("empty_entity", id -> EntityType.Builder.of(EmptyEntity::new, MobCategory.MISC).build(id.toString()));

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
    public static final DeferredHolder<EntityType<?>, EntityType<BaseManaStaffProjectileEntity>> BASE_MANA_STAFF_PROJECTILE = ENTITIES.register("base_mana_staff_projectile", id -> EntityType.Builder.<BaseManaStaffProjectileEntity>of(BaseManaStaffProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<VilethronProjectile>> VILETHRON_PROJECTILE = ENTITIES.register("vilethron_projectile", id -> EntityType.Builder.<VilethronProjectile>of(VilethronProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalVileShardProjectile>> CRYSTAL_VILE_SHARD_PROJECTILE = ENTITIES.register("crystal_vile_shard_projectile", id -> EntityType.Builder.<CrystalVileShardProjectile>of(CrystalVileShardProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HurtnadoProjectile>> HURTNADO_PROJECTILE = ENTITIES.register("hurtnado_projectile", id -> EntityType.Builder.<HurtnadoProjectile>of(HurtnadoProjectile::new, MobCategory.MISC).sized(0.8F, 1.2F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<WaterStreamProjectile>> WATER_STREAM_PROJECTILE = ENTITIES.register("water_stream_projectile", id -> EntityType.Builder.<WaterStreamProjectile>of(WaterStreamProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<WaterBoltProjectile>> WATER_BOLT_PROJECTILE = ENTITIES.register("water_bolt_projectile", id -> EntityType.Builder.<WaterBoltProjectile>of(WaterBoltProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BallOfFireProjectile>> BALL_OF_FIRE_PROJECTILE = ENTITIES.register("ball_of_fire_projectile", id -> EntityType.Builder.<BallOfFireProjectile>of(BallOfFireProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<EffectThrownPotion>> EFFECT_THROWN_POTION = ENTITIES.register("effect_thrown_potion", id -> EntityType.Builder.<EffectThrownPotion>of(EffectThrownPotion::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MagicDaggerProjectile>> MAGIC_DAGGER_PROJECTILE = ENTITIES.register("magic_dagger_projectile", id -> EntityType.Builder.<MagicDaggerProjectile>of(MagicDaggerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalStormProjectile>> CRYSTAL_STORM_PROJECTILE = ENTITIES.register("crystal_storm_projectile", id -> EntityType.Builder.<CrystalStormProjectile>of(CrystalStormProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CursedFlamesProjectile>> CURSED_FLAMES_PROJECTILE = ENTITIES.register("cursed_flames_projectile", id -> EntityType.Builder.<CursedFlamesProjectile>of(CursedFlamesProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BallOfFrostProjectile>> BALL_OF_FROST_PROJECTILE = ENTITIES.register("ball_of_frost_projectile", id -> EntityType.Builder.<BallOfFrostProjectile>of(BallOfFrostProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DemonScytheProjectile>> DEMON_SCYTHE_PROJECTILE = ENTITIES.register("demon_scythe_projectile", id -> EntityType.Builder.<DemonScytheProjectile>of(DemonScytheProjectile::new, MobCategory.MISC).sized(1.5F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SkullProjectile>> SKULL_PROJECTILE = ENTITIES.register("skull_projectile", id -> EntityType.Builder.<SkullProjectile>of(SkullProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CloudProjectile>> BLOOD_CLOUD_PROJECTILE = ENTITIES.register("blood_cloud_projectile", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainProjectile>> BLOOD_RAIN_PROJECTILE = ENTITIES.register("blood_rain_projectile", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CloudProjectile>> RAIN_CLOUD_PROJECTILE = ENTITIES.register("rain_cloud_projectile", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RainProjectile>> RAIN_PROJECTILE = ENTITIES.register("rain_projectile", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GoldenShowerProjectile>> GOLDEN_SHOWER_PROJECTILE = ENTITIES.register("golden_shower_projectile", id -> EntityType.Builder.<GoldenShowerProjectile>of(GoldenShowerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<MagicMissileProjectile>> MAGIC_MISSILE_PROJECTILE = ENTITIES.register("magic_missile_projectile", id -> EntityType.Builder.<MagicMissileProjectile>of(MagicMissileProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlamelashProjectile>> FLAMELASH_PROJECTILE = ENTITIES.register("flamelash_projectile", id -> EntityType.Builder.<FlamelashProjectile>of(FlamelashProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));

    // 剑气
    public static final DeferredHolder<EntityType<?>, EntityType<IceBladeSwordProjectile>> ICE_BLADE_SWORD_PROJECTILE = ENTITIES.register("ice_blade_sword_projectile", id -> EntityType.Builder.of(IceBladeSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<StarFuryProjectile>> STAR_FURY_PROJECTILE = ENTITIES.register("star_fury_projectile", id -> EntityType.Builder.of(StarFuryProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));//星怒弹幕
    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedSwordProjectile>> ENCHANTED_SWORD_PROJECTILE = ENTITIES.register("enchanted_sword_projectile", id -> EntityType.Builder.of(EnchantedSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<LightBaneProjectile>> LIGHTS_BANE_PROJECTILE = ENTITIES.register("lights_bane_projectile", id -> EntityType.Builder.of(LightBaneProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<GrassSwordProjectile>> GRASS_PROJECTILE = ENTITIES.register("grass_projectile", id -> EntityType.Builder.of(GrassSwordProjectile::new, MobCategory.MISC).sized(2F, 2F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BeeKeeperProjectile>> BEE_PROJECTILE = ENTITIES.register("bee_projectile", id -> EntityType.Builder.of(BeeKeeperProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<NightEdgeProjectile>> NIGHTS_EDGE_PROJECTILE = ENTITIES.register("nights_edge_projectile", id -> EntityType.Builder.of(NightEdgeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));

    // 弓箭
    public static final DeferredHolder<EntityType<?>, EntityType<BaseArrowEntity>> ARROW_PROJECTILE = ENTITIES.register("arrow_projectile", id -> EntityType.Builder.<BaseArrowEntity>of(BaseArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BeeArrow>> BEE_ARROW = ENTITIES.register("bee_arrow_projectile", id -> EntityType.Builder.<BeeArrow>of(BeeArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HellBatArrowEntity>> HELL_BAT_ARROW = ENTITIES.register("hell_bat_arrow_projectile", id -> EntityType.Builder.<HellBatArrowEntity>of(HellBatArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DriveAwayArrow>> DRIVE_AWAY_ARROW = ENTITIES.register("drive_away_arrow", id -> EntityType.Builder.<DriveAwayArrow>of(DriveAwayArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));

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
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> THROWN_KNIVE_PROJECTILE = ENTITIES.register("thrown_knive_projectile", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> BONE_THROWN_KNIVE_PROJECTILE = ENTITIES.register("bone_thrown_knive_projectile", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> FROST_DAGGERFISH_PROJECTILE = ENTITIES.register("frost_daggerfish_projectile", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> DUNGEON_DEMON_BONE_PROJECTILE = ENTITIES.register("dungeon_demon_bone_projectile", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> SHURIKEN_PROJECTILE = ENTITIES.register("shuriken_projectile", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableDropSelfProjectile>> JAVELIN_PROJECTILE = ENTITIES.register("javelin_projectile", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<RopeCoilsProjectile>> ROPE_COILS = ENTITIES.register("rope_coils", id -> EntityType.Builder.<RopeCoilsProjectile>of(RopeCoilsProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<IceTofuBrickProjectile>> ICE_TOFU_BRICK_PROJECTILE = ENTITIES.register("ice_tofu_brick_projectile", id -> EntityType.Builder.<IceTofuBrickProjectile>of(IceTofuBrickProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpikyBallProjectile>> SPIKY_BALL_PROJECTILE = ENTITIES.register("spiky_ball_projectile", id -> EntityType.Builder.<SpikyBallProjectile>of(SpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownWaterProjectile>> THROWN_WATER_PROJECTILE = ENTITIES.register("thrown_water_projectile", id -> EntityType.Builder.<ThrownWaterProjectile>of(ThrownWaterProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlowerPetalProjectile>> FLOWER_PETAL_PROJECTILE = ENTITIES.register("flower_petal_projectile", id -> EntityType.Builder.<FlowerPetalProjectile>of(FlowerPetalProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TitaniumShardsProjectile>> TITANIUM_SHARDS_PROJECTILE = ENTITIES.register("titanium_shards_projectile", id -> EntityType.Builder.<TitaniumShardsProjectile>of(TitaniumShardsProjectile::new, MobCategory.MISC).sized(0, 0).fireImmune().noSummon().noSave().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FallingStarItemEntity>> FALLING_STAR_ITEM_ENTITY = ENTITIES.register("falling_star", id -> EntityType.Builder.<FallingStarItemEntity>of(FallingStarItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TreasureBagItemEntity>> TREASURE_BAG_ITEM_ENTITY = ENTITIES.register("treasure_bag", id -> EntityType.Builder.<TreasureBagItemEntity>of(TreasureBagItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CoinPortalEntity>> COIN_PORTAL = ENTITIES.register("coin_portal", id -> EntityType.Builder.<CoinPortalEntity>of(CoinPortalEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownPowderEntity>> THROWN_POWDER = ENTITIES.register("thrown_powder", id -> EntityType.Builder.<ThrownPowderEntity>of(ThrownPowderEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<DeadBodyPartEntity>> BODY_PART = ENTITIES.register("body_part", id -> EntityType.Builder.<DeadBodyPartEntity>of(DeadBodyPartEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().noSave().noSummon().build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<FlameCloudEntity>> FLAME_CLOUD = ENTITIES.register("flame_cloud", id -> EntityType.Builder.<FlameCloudEntity>of(FlameCloudEntity::new, MobCategory.MISC).sized(5, 5).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SuperSpikyBallProjectile>> SUPER_SPIKY_BALL_PROJECTILE = ENTITIES.register("super_spiky_ball_projectile", id -> EntityType.Builder.<SuperSpikyBallProjectile>of(SuperSpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<SpearEntity>> SPEAR = ENTITIES.register("spear", id -> EntityType.Builder.<SpearEntity>of(SpearEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<StormSpearShotProjectile>> STORM_SPEAR_SHOT_PROJECTILE = ENTITIES.register("storm_spear_shot_projectile", id -> EntityType.Builder.of(StormSpearShotProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));

    // 鱼钩
    public static final DeferredHolder<EntityType<?>, EntityType<BaseFishingHook>> BASE_FISHING_HOOK = ENTITIES.register("base_fishing_hook", id -> EntityType.Builder.<BaseFishingHook>of(BaseFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<HotlineFishingHook>> HOTLINE_FISHING_HOOK = ENTITIES.register("hotline_fishing_hook", id -> EntityType.Builder.<HotlineFishingHook>of(HotlineFishingHook::new, MobCategory.MISC).noSave().noSummon().fireImmune().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<CurioFishingHook>> CURIO_FISHING_HOOK = ENTITIES.register("curio_fishing_hook", id -> EntityType.Builder.<CurioFishingHook>of(CurioFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<BloodyFishingHook>> BLOODY_FISHING_HOOK = ENTITIES.register("bloody_fishing_hook", id -> EntityType.Builder.<BloodyFishingHook>of(BloodyFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));

    // 钩爪
    public static final DeferredHolder<EntityType<?>, EntityType<BaseHookEntity>> BASE_HOOK = registerHook("base_hook", BaseHookEntity::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> WEB_SLINGER = registerHook("web_slinger", AbstractHookEntity.Impl::new);
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractHookEntity.Impl>> SKELETRON_HAND = registerHook("skeletron_hand", AbstractHookEntity.Impl::new);
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

    // 链球
    public static final DeferredHolder<EntityType<?>, EntityType<FlailBall>> FLAIL_BALL = ENTITIES.register("flail_ball", id -> EntityType.Builder.<FlailBall>of((entityType, level) -> new FlailBall(level, null), MobCategory.MISC).sized(1F, 3F)/*.clientTrackingRange(6)*/.noSave().build(id.toString()));

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
}
