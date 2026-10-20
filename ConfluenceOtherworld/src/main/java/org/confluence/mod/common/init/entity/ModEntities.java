package org.confluence.mod.common.init.entity;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
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
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.entity.soulcerer.SpiritSurgeEntity;
import org.confluence.mod.common.entity.soulcerer.SoulStingProjectile;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public final class ModEntities {
    private static final Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<CreatureAttributeBuilder.Definition>> ATTRIBUTES = new LinkedHashMap<>();
    private static final Map<EntityType<?>, CreatureAttributeBuilder.Definition> CREATURE_DEFINITIONS = new LinkedHashMap<>();
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    // 牢枕专用
    public static final RegistryObject<EntityType<MartianReinforcement>> MARTIAN_REINFORCEMENT = register("martian_reinforcement", "火星支援信标", id -> EntityType.Builder.of(MartianReinforcement::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<EmptyEntity>> EMPTY_ENTITY = register("empty_entity", "空实体", id -> EntityType.Builder.of(EmptyEntity::new, MobCategory.MISC).build(id.toString()));

    public static final RegistryObject<EntityType<MartianElectricBolt>> MARTIAN_ELECTRIC_BOLT = register("martian_electric_bolt", "火星电击束", id -> EntityType.Builder.of(MartianElectricBolt::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<MonsterLaser>> MONSTER_LASER = register("monster_laser", "怪物激光", id -> EntityType.Builder.of(MonsterLaser::new, MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(16).updateInterval(1).build(id.toString()));

    public static final RegistryObject<EntityType<ThrownRockProjectile>> THROWN_ROCK = register("thrown_rock", "掷出的岩石", id -> EntityType.Builder.<ThrownRockProjectile>of(ThrownRockProjectile::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<DandelionSeed>> DANDELION_SEED = register("dandelion_seed", "蒲公英种子", id -> EntityType.Builder.<DandelionSeed>of(DandelionSeed::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<HopliteJavelin>> HOPLITE_JAVELIN = register("hoplite_javelin", "标枪", id -> EntityType.Builder.<HopliteJavelin>of(HopliteJavelin::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<UnholyTridentProjectile>> UNHOLY_TRIDENT = register("unholy_trident", "邪恶三叉戟", id -> EntityType.Builder.<UnholyTridentProjectile>of(UnholyTridentProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<PlantSpit>> CLINGER_FLAME = register("clinger_flame", "诅咒魔焰", id -> EntityType.Builder.<PlantSpit>of((type, level) -> new PlantSpit(type, level, PlantSpit.Kind.CURSED_FLAME), MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<PlantSpit>> FUNGI_SPORE = register("fungi_spore", "真菌孢子", id -> EntityType.Builder.<PlantSpit>of((type, level) -> new PlantSpit(type, level, PlantSpit.Kind.SPORE), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final RegistryObject<EntityType<AntlionSandBall>> ANTLION_SAND_BALL = register("antlion_sand_ball", "沙球", id -> EntityType.Builder.<AntlionSandBall>of(AntlionSandBall::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final RegistryObject<EntityType<NimbusRain>> NIMBUS_RAIN = register("nimbus_rain", "雨滴", id -> EntityType.Builder.<NimbusRain>of(NimbusRain::new, MobCategory.MISC).sized(0.1F, 1.0F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final RegistryObject<EntityType<HostileParticleProjectile>> RUNE_BLAST = register("rune_blast", "符文冲击", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.RUNE_BLAST), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final RegistryObject<EntityType<SpiderWebSpit>> SPIDER_WEB_SPIT = register("spider_web_spit", "蛛网喷吐", id -> EntityType.Builder.<SpiderWebSpit>of(SpiderWebSpit::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    public static final RegistryObject<EntityType<DesertSpiritCurse>> DESERT_SPIRIT_CURSE = register("desert_spirit_curse", "沙漠幽魂的诅咒", id -> EntityType.Builder.of(DesertSpiritCurse::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    // 冰雪敌怪射弹
    public static final RegistryObject<EntityType<FrostMonsterProjectile>> FROST_BLAST = register("frost_blast", "冰霜爆裂", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.BLAST), MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<FrostMonsterProjectile>> ICEWATER_SPIT = register("icewater_spit", "寒水唾液", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.SPIT), MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<FrostMonsterProjectile>> FROST_BEAM = register("frost_beam", "冰霜光束", id -> EntityType.Builder.<FrostMonsterProjectile>of((type, level) -> new FrostMonsterProjectile(type, level, FrostMonsterProjectile.Kind.BEAM), MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(10).updateInterval(1).build(id.toString()));

    // 炸弹
    public static final RegistryObject<EntityType<BaseBombEntity>> BOMB_ENTITY = registerBomb("bomb_entity", "炸弹", BaseBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<BouncyBombEntity>> BOUNCY_BOMB_ENTITY = registerBomb("bouncy_bomb_entity", "弹力炸弹", BouncyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<ScarabBombEntity>> SCARAB_BOMB_ENTITY = registerBomb("scarab_bomb_entity", "圣甲虫炸弹", ScarabBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<StickyBombEntity>> STICKY_BOMB_ENTITY = registerBomb("sticky_bomb_entity", "黏性炸弹", StickyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<SmokeBombEntity>> SMOKE_BOMB_ENTITY = registerBomb("smoke_bomb_entity", "烟雾弹", SmokeBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<BombFishEntity>> BOMB_FISH_ENTITY = registerBomb("bomb_fish_entity", "炸弹鱼", BombFishEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<BaseGrenadeEntity>> GRENADE = registerBomb("grenade", "手雷", BaseGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final RegistryObject<EntityType<BouncyGrenadeEntity>> BOUNCY_GRENADE = registerBomb("bouncy_grenade", "弹力手雷", BouncyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final RegistryObject<EntityType<StickyGrenadeEntity>> STICKY_GRENADE = registerBomb("sticky_grenade", "黏性手雷", StickyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final RegistryObject<EntityType<BeenadeEntity>> BEENADE = registerBomb("beenade", "蜜蜂手雷", BeenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final RegistryObject<EntityType<BaseDynamiteEntity>> DYNAMITE = registerBomb("dynamite", "炸药", BaseDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final RegistryObject<EntityType<BouncyDynamiteEntity>> BOUNCY_DYNAMITE = registerBomb("bouncy_dynamite", "弹力炸药", BouncyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final RegistryObject<EntityType<StickyDynamiteEntity>> STICKY_DYNAMITE = registerBomb("sticky_dynamite", "黏性炸药", StickyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final RegistryObject<EntityType<BaseDirtBombEntity>> DIRT_BOMB = registerBomb("dirt_bomb", "土炸弹", BaseDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<StickyDirtBombEntity>> STICKY_DIRT_BOMB = registerBomb("sticky_dirt_bomb", "粘性土炸弹", StickyDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<DryBombEntity>> DRY_BOMB = registerBomb("dry_bomb", "干炸弹", DryBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<LiquidBombEntity>> WET_BOMB = registerBomb("wet_bomb", "湿炸弹", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<LiquidBombEntity>> LAVA_BOMB = registerBomb("lava_bomb", "熔岩炸弹", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final RegistryObject<EntityType<LiquidBombEntity>> HONEY_BOMB = registerBomb("honey_bomb", "蜂蜜炸弹", LiquidBombEntity::new, BaseBombEntity.DIAMETER);

    public static final RegistryObject<EntityType<SoulLureProjectile>> SOUL_LURE_PROJECTILE = register("soul_lure_projectile", "引魂弹幕", id -> EntityType.Builder.<SoulLureProjectile>of(SoulLureProjectile::new, MobCategory.MISC).sized(SoulSkillParameters.SOUL_LURE_ENTITY_SIZE, SoulSkillParameters.SOUL_LURE_ENTITY_SIZE).clientTrackingRange(SoulSkillParameters.SOUL_ENTITY_TRACKING_RANGE).updateInterval(SoulSkillParameters.SOUL_ENTITY_UPDATE_INTERVAL).noSave().build(id.toString()));
    public static final RegistryObject<EntityType<SpiritSurgeEntity>> SPIRIT_SURGE = register("spirit_surge", "灵魂激荡", id -> EntityType.Builder.<SpiritSurgeEntity>of(SpiritSurgeEntity::new, MobCategory.MISC).sized(SoulSkillParameters.SOUL_ENTITY_SIZE, SoulSkillParameters.SOUL_ENTITY_SIZE).clientTrackingRange(SoulSkillParameters.SOUL_ENTITY_TRACKING_RANGE).updateInterval(SoulSkillParameters.SOUL_ENTITY_UPDATE_INTERVAL).noSave().build(id.toString()));
    public static final RegistryObject<EntityType<SoulStingProjectile>> SOUL_STING = register("soul_sting", "魂师毒刺", id -> EntityType.Builder.<SoulStingProjectile>of(SoulStingProjectile::new, MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(8).updateInterval(1).noSave().build(id.toString()));

    // 魔法
    public static final RegistryObject<EntityType<WhipAttackEntity>> WHIP_ATTACK = register("whip_attack", "鞭击", id -> EntityType.Builder.of(WhipAttackEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<ChesterEntity>> CHESTER = withAttributes(registerStorageCompanion("chester", "切斯特", ChesterEntity::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(20).movementSpeed(0.35).flyingSpeed(0.45).followRange(32).build());
    public static final RegistryObject<EntityType<FlyingPiggyBankEntity>> FLYING_PIGGY_BANK = withAttributes(registerStorageCompanion("piggy_bank", "飞行存钱罐", FlyingPiggyBankEntity::new),
            () -> CreatureAttributeBuilder.critter().maxHealth(20).movementSpeed(0.35).flyingSpeed(0.45).followRange(32).build());
    public static final RegistryObject<EntityType<BaseManaStaffProjectileEntity>> BASE_MANA_STAFF = register("base_mana_staff", "法杖射弹", id -> EntityType.Builder.<BaseManaStaffProjectileEntity>of(BaseManaStaffProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<VilethronProjectile>> VILETHRON = register("vilethron", "魔刺射弹", id -> EntityType.Builder.<VilethronProjectile>of(VilethronProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<CrystalVileShardProjectile>> CRYSTAL_VILE_SHARD = register("crystal_vile_shard", "魔晶碎块射弹", id -> EntityType.Builder.<CrystalVileShardProjectile>of(CrystalVileShardProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<HurtnadoProjectile>> HURTNADO = register("hurtnado", "痛苦飓风", id -> EntityType.Builder.<HurtnadoProjectile>of(HurtnadoProjectile::new, MobCategory.MISC).sized(0.8F, 1.2F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<WaterStreamProjectile>> WATER_STREAM = register("water_stream", "水流射弹", id -> EntityType.Builder.<WaterStreamProjectile>of(WaterStreamProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<WaterBoltProjectile>> WATER_BOLT = register("water_bolt", "水弹射弹", id -> EntityType.Builder.<WaterBoltProjectile>of(WaterBoltProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<BallOfFireProjectile>> BALL_OF_FIRE = register("ball_of_fire", "火球弹", id -> EntityType.Builder.<BallOfFireProjectile>of(BallOfFireProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));
    public static final RegistryObject<EntityType<EffectThrownPotion>> EFFECT_THROWN_POTION = register("effect_thrown_potion", "效果投掷药水", id -> EntityType.Builder.<EffectThrownPotion>of(EffectThrownPotion::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).build(id.toString()));
    public static final RegistryObject<EntityType<MagicDaggerProjectile>> MAGIC_DAGGER = register("magic_dagger", "魔法飞刀射弹", id -> EntityType.Builder.<MagicDaggerProjectile>of(MagicDaggerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CrystalStormProjectile>> CRYSTAL_STORM = register("crystal_storm", "水晶风暴射弹", id -> EntityType.Builder.<CrystalStormProjectile>of(CrystalStormProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CursedFlamesProjectile>> CURSED_FLAMES = register("cursed_flames", "诅咒火射弹", id -> EntityType.Builder.<CursedFlamesProjectile>of(CursedFlamesProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<BallOfFrostProjectile>> BALL_OF_FROST = register("ball_of_frost", "寒霜球射弹", id -> EntityType.Builder.<BallOfFrostProjectile>of(BallOfFrostProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<DemonScytheProjectile>> DEMON_SCYTHE = register("demon_scythe", "恶魔镰刀射弹", id -> EntityType.Builder.<DemonScytheProjectile>of(DemonScytheProjectile::new, MobCategory.MISC).sized(1.5F, 1.5F).build(id.toString()));
    public static final RegistryObject<EntityType<SkullProjectile>> SKULL = register("skull", "骷髅头射弹", id -> EntityType.Builder.<SkullProjectile>of(SkullProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).build(id.toString()));
    public static final RegistryObject<EntityType<CloudProjectile>> BLOOD_CLOUD = register("blood_cloud", "血云射弹", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final RegistryObject<EntityType<RainProjectile>> BLOOD_RAIN = register("blood_rain", "血雨射弹", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CloudProjectile>> RAIN_CLOUD = register("rain_cloud", "雨云射弹", id -> EntityType.Builder.<CloudProjectile>of(CloudProjectile::new, MobCategory.MISC).sized(2, 0.8F).build(id.toString()));
    public static final RegistryObject<EntityType<RainProjectile>> RAIN = register("rain", "雨水射弹", id -> EntityType.Builder.<RainProjectile>of(RainProjectile::new, MobCategory.MISC).sized(0.25F, 1.5F).build(id.toString()));
    public static final RegistryObject<EntityType<GoldenShowerProjectile>> GOLDEN_SHOWER = register("golden_shower", "黄金雨射弹", id -> EntityType.Builder.<GoldenShowerProjectile>of(GoldenShowerProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<MagicMissileProjectile>> MAGIC_MISSILE = register("magic_missile", "魔法飞弹", id -> EntityType.Builder.<MagicMissileProjectile>of(MagicMissileProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<FlamelashProjectile>> FLAMELASH = register("flamelash", "烈焰火鞭", id -> EntityType.Builder.<FlamelashProjectile>of(FlamelashProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<RainbowProjectile>> RAINBOW = register("rainbow", "彩虹射弹", id -> EntityType.Builder.<RainbowProjectile>of(RainbowProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<SkyFractureProjectile>> SKY_FRACTURE = register("sky_fracture", "裂天剑", id -> EntityType.Builder.<SkyFractureProjectile>of(SkyFractureProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CrystalChargeProjectile>> CRYSTAL_CHARGE_1 = register("crystal_charge_1", "爆炸水晶1", id -> EntityType.Builder.<CrystalChargeProjectile>of(CrystalChargeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<CrystalChargeProjectile>> CRYSTAL_CHARGE_2 = register("crystal_charge_2", "爆炸水晶2", id -> EntityType.Builder.<CrystalChargeProjectile>of(CrystalChargeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final RegistryObject<EntityType<NPCShadowflameSkullProjectile>> NPC_SHADOWFLAME_SKULL = register("npc_shadowflame_skull", "NPC暗影焰头骨", id -> EntityType.Builder.of(NPCShadowflameSkullProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build(id.toString()));

    // 剑气
    public static final RegistryObject<EntityType<GeoSwordProjectile>> GEO_SWORD_PROJECTILE = register("geo_sword_projectile", "地质剑射弹", id -> EntityType.Builder.of(GeoSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<IceBladeSwordProjectile>> ICE_BLADE_SWORD = register("ice_blade_sword", "冰刃剑", id -> EntityType.Builder.of(IceBladeSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<StarFuryProjectile>> STAR_FURY = register("star_fury", "天堂怒火", id -> EntityType.Builder.of(StarFuryProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));//星怒弹幕
    public static final RegistryObject<EntityType<EnchantedSwordProjectile>> ENCHANTED_SWORD = registerWithEnglishName("enchanted_sword", "附魔剑射弹", "Enchanted Sword Projectile", id -> EntityType.Builder.of(EnchantedSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<LightBaneProjectile>> LIGHTS_BANE = register("lights_bane", "魔光剑射弹", id -> EntityType.Builder.of(LightBaneProjectile::new, MobCategory.MISC).sized(1F, 1F).build(id.toString()));
    public static final RegistryObject<EntityType<GrassSwordProjectile>> GRASS = register("grass", "草剑射弹", id -> EntityType.Builder.of(GrassSwordProjectile::new, MobCategory.MISC).sized(2F, 2F).build(id.toString()));
    public static final RegistryObject<EntityType<BeeKeeperProjectile>> BEE = registerWithEnglishName("bee", "养蜂人射弹", "Beekeeper Projectile", id -> EntityType.Builder.of(BeeKeeperProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<NightEdgeProjectile>> NIGHTS_EDGE = register("nights_edge", "永夜刃射弹", id -> EntityType.Builder.of(NightEdgeProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));

    // 弓箭
    public static final RegistryObject<EntityType<BaseArrowEntity>> BASE_ARROW = register("arrow", "泰拉箭", id -> EntityType.Builder.<BaseArrowEntity>of(BaseArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<BeeArrowEntity>> BEE_ARROW = register("bee_arrow", "蜜蜂箭", id -> EntityType.Builder.<BeeArrowEntity>of(BeeArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<HellBatArrowEntity>> HELL_BAT_ARROW = register("hell_bat_arrow", "地狱蝙蝠箭", id -> EntityType.Builder.<HellBatArrowEntity>of(HellBatArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<DriveAwayArrowEntity>> DRIVE_AWAY_ARROW = register("drive_away_arrow", "驱离箭", id -> EntityType.Builder.<DriveAwayArrowEntity>of(DriveAwayArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<FlamingArrowEntity>> FLAMING_ARROW = register("flaming_arrow", "烈焰箭", id -> EntityType.Builder.<FlamingArrowEntity>of(FlamingArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<UnholyArrowEntity>> UNHOLY_ARROW = register("unholy_arrow", "邪箭", id -> EntityType.Builder.<UnholyArrowEntity>of(UnholyArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<StarArrowEntity>> STAR_ARROW = register("star_arrow", "星辰箭", id -> EntityType.Builder.<StarArrowEntity>of(StarArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<HellfireArrowEntity>> HELLFIRE_ARROW = register("hellfire_arrow", "狱炎箭", id -> EntityType.Builder.<HellfireArrowEntity>of(HellfireArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<FrostburnArrowEntity>> FROSTBURN_ARROW = register("frostburn_arrow", "霜冻箭", id -> EntityType.Builder.<FrostburnArrowEntity>of(FrostburnArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<BoneArrowEntity>> BONE_ARROW = register("bone_arrow", "骨箭", id -> EntityType.Builder.<BoneArrowEntity>of(BoneArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ShimmerArrowEntity>> SHIMMER_ARROW = register("shimmer_arrow", "微光箭", id -> EntityType.Builder.<ShimmerArrowEntity>of(ShimmerArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<FossilArrowEntity>> FOSSIL_ARROW = register("fossil_arrow", "化石箭", id -> EntityType.Builder.<FossilArrowEntity>of(FossilArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<FlyFishArrowEntity>> FLY_FISH_ARROW = register("fly_fish_arrow", "飞鱼箭", id -> EntityType.Builder.<FlyFishArrowEntity>of(FlyFishArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<DeveloperArrowEntity>> DEVELOPER_ARROW = register("developer_arrow", "开发者箭", id -> EntityType.Builder.<DeveloperArrowEntity>of(DeveloperArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));

    // 其它
    public static final RegistryObject<EntityType<BoulderEntity>> BOULDER = register("boulder", "巨石", id -> EntityType.Builder.<BoulderEntity>of(BoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<FollowerBoulderEntity>> FOLLOWER_BOULDER = register("follower_boulder", "跟踪巨石", id -> EntityType.Builder.<FollowerBoulderEntity>of(FollowerBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<ExplodeBoulderEntity>> EXPLODE_BOULDER = register("explode_boulder", "爆炸巨石", id -> EntityType.Builder.<ExplodeBoulderEntity>of(ExplodeBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<RollingCactusBoulderEntity>> ROLLING_CACTUS_BOULDER = register("rolling_cactus_boulder", "仙人球", id -> EntityType.Builder.<RollingCactusBoulderEntity>of(RollingCactusBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<RollingCactusBoulderEntity.SpikeProjectile>> ROLLING_CACTUS_SPIKE = register("rolling_cactus_spike", "仙人球尖刺", id -> EntityType.Builder.of(RollingCactusBoulderEntity.SpikeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<TombstoneBoulderEntity>> TOMBSTONE_BOULDER = register("tombstone_boulder", "墓石巨石", id -> EntityType.Builder.<TombstoneBoulderEntity>of(TombstoneBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<BouncyBoulderEntity>> BOUNCY_BOULDER = register("bouncy_boulder", "弹力巨石", id -> EntityType.Builder.<BouncyBoulderEntity>of(BouncyBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<GhoulderEntity>> GHOULDER = register("ghoulder", "幽灵巨石", id -> EntityType.Builder.<GhoulderEntity>of(GhoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<LavaBoulderEntity>> LAVA_BOULDER = register("lava_boulder", "熔岩巨石", id -> EntityType.Builder.<LavaBoulderEntity>of(LavaBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<PooBoulderEntity>> POO_BOULDER = register("poo_boulder", "臭臭巨石", id -> EntityType.Builder.<PooBoulderEntity>of(PooBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<SpiderBoulderEntity>> SPIDER_BOULDER = register("spider_boulder", "蜘蛛巨石", id -> EntityType.Builder.<SpiderBoulderEntity>of(SpiderBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<RainbowBoulderEntity>> RAINBOW_BOULDER = register("rainbow_boulder", "彩虹巨石", id -> EntityType.Builder.<RainbowBoulderEntity>of(RainbowBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<LifecrystalBoulderEntity>> LIFECRYSTAL_BOULDER = registerWithEnglishName("lifecrystal_boulder", "生命水晶巨石", "Life Crystal Boulder", id -> EntityType.Builder.<LifecrystalBoulderEntity>of(LifecrystalBoulderEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<Boulder3x3Entity>> BOULDER_3X = registerWithEnglishName("boulder_3x", "3x3巨石", "Boulder 3x", id -> EntityType.Builder.<Boulder3x3Entity>of(Boulder3x3Entity::new, MobCategory.MISC).sized(3, 3).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> THROWN_KNIVE = register("thrown_knive", "投刀", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<PirateShot>> PIRATE_BULLET = register("pirate_bullet", "海盗子弹", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.BULLET), MobCategory.MISC).sized(0.15F, 0.15F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<PirateShot>> PIRATE_FLAMING_ARROW = register("pirate_flaming_arrow", "海盗烈焰箭", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.ARROW), MobCategory.MISC).sized(0.2F, 0.2F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<PirateShot>> PIRATE_CANNONBALL = register("pirate_cannonball", "海盗炮弹", id -> EntityType.Builder.<PirateShot>of((type, level) -> new PirateShot(type, level, PirateShot.Kind.CANNONBALL), MobCategory.MISC).sized(0.4F, 0.4F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<NPCWeaponProjectile>> NPC_WEAPON_PROJECTILE = register("npc_weapon_projectile", "NPC武器射弹", id -> EntityType.Builder.<NPCWeaponProjectile>of(NPCWeaponProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<CyborgExplosiveProjectile>> CYBORG_EXPLOSIVE = register("cyborg_explosive", "机器侠爆炸弹", id -> EntityType.Builder.<CyborgExplosiveProjectile>of(CyborgExplosiveProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> BONE_THROWN_KNIVE = register("bone_thrown_knive", "骨投刀射弹", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> FROST_DAGGERFISH = register("frost_daggerfish", "寒霜飞鱼射弹", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> DUNGEON_DEMON_BONE = register("dungeon_demon_bone", "地牢魔骨射弹", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> SHURIKEN = register("shuriken", "手里剑", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrowableDropSelfProjectile>> JAVELIN = register("javelin", "标枪", id -> EntityType.Builder.of(ThrowableDropSelfProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<BoomerangProjectile>> BOOMERANG_PROJECTILE = register("boomerang_projectile", "回旋镖", id -> EntityType.Builder.of(BoomerangProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<RopeCoilsProjectile>> ROPE_COILS = register("rope_coils", "绳索卷", id -> EntityType.Builder.<RopeCoilsProjectile>of(RopeCoilsProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<IceTofuBrickProjectile>> ICE_TOFU_BRICK = register("ice_tofu_brick", "冰豆腐砖", id -> EntityType.Builder.<IceTofuBrickProjectile>of(IceTofuBrickProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(id.toString()));
    public static final RegistryObject<EntityType<SpikyBallProjectile>> SPIKY_BALL = register("spiky_ball", "尖球", id -> EntityType.Builder.<SpikyBallProjectile>of(SpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final RegistryObject<EntityType<ThrownWaterProjectile>> THROWN_WATER = register("thrown_water", "掷出的液体瓶", id -> EntityType.Builder.<ThrownWaterProjectile>of(ThrownWaterProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final RegistryObject<EntityType<SparkleSlimeBalloonProjectile>> SPARKLE_SLIME_BALLOON = register("sparkle_slime_balloon", "闪耀史莱姆气球", id -> EntityType.Builder.<SparkleSlimeBalloonProjectile>of(SparkleSlimeBalloonProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final RegistryObject<EntityType<FlowerPetalProjectile>> FLOWER_PETAL = register("flower_petal", "花瓣射弹", id -> EntityType.Builder.<FlowerPetalProjectile>of(FlowerPetalProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).build(id.toString()));
    public static final RegistryObject<EntityType<HarpyFeatherProjectile>> HARPY_FEATHER = register("harpy_feather_projectile", "鸟妖羽毛弹幕", id -> EntityType.Builder.of(HarpyFeatherProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileDemonScytheProjectile>> HOSTILE_DEMON_SCYTHE = register("hostile_demon_scythe_projectile", "敌对恶魔镰刀射弹", id -> EntityType.Builder.of(HostileDemonScytheProjectile::new, MobCategory.MISC)
                    .sized(1.5F, 1.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HornetStingerProjectile>> HORNET_STINGER = register("hornet_stinger_projectile", "黄蜂毒刺射弹", id -> EntityType.Builder.of(HornetStingerProjectile::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<SkeletronSkullProjectile>> SKELETRON_SKULL = register("skeletron_skull_projectile", "骷髅王头骨射弹", id -> EntityType.Builder.of(SkeletronSkullProjectile::new, MobCategory.MISC)
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HillLavaPillarProjectile>> HILL_LAVA_PILLAR = register("hill_lava_pillar", "血肉山熔岩柱", id -> EntityType.Builder.of(HillLavaPillarProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 0.2F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> HILL_FIRE_BOUND = register("hill_fire_bound", "血肉山火焰束", id -> EntityType.Builder.<HostileParticleProjectile>of((type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.HILL_FIRE_BOUND), MobCategory.MISC)
                    .sized(0.35F, 0.35F).clientTrackingRange(12).updateInterval(1).noSave().build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> WALL_OF_FLESH_LASER = register("wall_of_flesh_laser", "血肉墙激光", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.WALL_OF_FLESH_LASER),
                            MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<DestroyerLaserProjectile>> DESTROYER_LASER = register("destroyer_laser", "毁灭者激光", id -> EntityType.Builder.of(DestroyerLaserProjectile::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<PrimeLaserProjectile>> PRIME_LASER = register("prime_laser", "机械激光", id -> EntityType.Builder.of(PrimeLaserProjectile::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<PlanteraProjectile>> PLANTERA_SEED = register("plantera_seed", "世纪之花种子", id -> EntityType.Builder.<PlanteraProjectile>of(
                            (type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.SEED),
                            MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<PlanteraProjectile>> PLANTERA_THORN_BALL = register("plantera_thorn_ball", "世纪之花刺球", id -> EntityType.Builder.<PlanteraProjectile>of(
                            (type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.THORN_BALL),
                            MobCategory.MISC)
                    .sized(0.7F, 0.7F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<PlanteraProjectile>> PLANTERA_SPORE = register("plantera_spore", "世纪之花孢子", id -> EntityType.Builder.<PlanteraProjectile>of(
                            (type, level) -> new PlanteraProjectile(type, level, PlanteraProjectile.Variant.SPORE),
                            MobCategory.MISC)
                    .sized(0.45F, 0.45F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<TwinEyeProjectile>> RETINAZER_LASER = register("retinazer_laser", "激光眼激光", id -> EntityType.Builder.<TwinEyeProjectile>of(
                            (type, level) -> new TwinEyeProjectile(type, level, TwinEyeProjectile.Variant.LASER),
                            MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<TwinEyeProjectile>> SPAZMATISM_FLAME = register("spazmatism_flame", "魔焰眼火焰", id -> EntityType.Builder.<TwinEyeProjectile>of(
                            (type, level) -> new TwinEyeProjectile(type, level, TwinEyeProjectile.Variant.CURSED_FLAME),
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> DARK_CASTER_PROJECTILE = register("dark_caster_projectile", "水球", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.WATER_SPHERE),
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> CHAOS_BALL_PROJECTILE = register("chaos_ball_projectile", "混沌球", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.CHAOS_BALL),
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> SHADOW_BEAM_PROJECTILE = register("shadow_beam_projectile", "暗影束", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.SHADOW_BEAM),
                            MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> INFERNO_BOLT_PROJECTILE = register("inferno_bolt_projectile", "狱火弹", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.INFERNO_BOLT),
                            MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> LOST_SOUL_PROJECTILE = register("lost_soul_projectile", "亡魂射弹", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.LOST_SOUL),
                            MobCategory.MISC)
                    .sized(0.45F, 0.45F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> VILE_SPIT_PROJECTILE = register("vile_spit_projectile", "魔唾液弹幕", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.VILE_SPIT),
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> FIRE_IMP_PROJECTILE = register("fire_imp_projectile", "火焰小鬼弹幕", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.FIRE_IMP),
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<HostileParticleProjectile>> GASTROPOD_PROJECTILE = register("gastropod_projectile", "腹足怪能量弹幕", id -> EntityType.Builder.<HostileParticleProjectile>of(
                            (type, level) -> new HostileParticleProjectile(type, level, HostileParticleProjectile.Variant.GASTROPOD),
                            MobCategory.MISC)
                    .sized(0.45F, 0.45F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<PaladinHammerProjectile>> PALADIN_HAMMER_PROJECTILE = register("paladin_hammer_projectile", "圣骑士重锤弹幕", id -> EntityType.Builder.of(PaladinHammerProjectile::new, MobCategory.MISC)
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<DeerclopsThrownIceProjectile>> THROWN_ICE_PROJECTILE = register("thrown_ice_projectile", "抛射冰块", id -> EntityType.Builder.of(DeerclopsThrownIceProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .noSummon()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<DeerclopsIcePillarProjectile>> ICE_PILLAR = register("ice_pillar", "冰柱", id -> EntityType.Builder.of(DeerclopsIcePillarProjectile::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .noSummon()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<ThrownPhasebladeProjectile>> PHASEBLADE_PROJECTILE = register("phaseblade_projectile", "晶光刃射弹", id -> EntityType.Builder.of(ThrownPhasebladeProjectile::new, MobCategory.MISC)
                    .sized(0.55F, 0.55F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .noSummon()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<ThrownPhasesaberProjectile>> PHASESABER_PROJECTILE = register("phasesaber_projectile", "晶光剑射弹", id -> EntityType.Builder.of(ThrownPhasesaberProjectile::new, MobCategory.MISC)
                    .sized(0.55F, 0.55F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .noSummon()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<DeerclopsShadowHandProjectile>> SHADOW_HAND = register("shadow_hand", "暗影之手", id -> EntityType.Builder.of(DeerclopsShadowHandProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .noSummon()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<AncientLightProjectile>> ANCIENT_LIGHT = register("ancient_light", "远古光明妖", id -> EntityType.Builder.of(AncientLightProjectile::new, MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final RegistryObject<EntityType<CultistProjectile>> PROPHECYS_END = register("prophecys_end", "预言之末", id -> EntityType.Builder.<CultistProjectile>of((type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.PROPHECYS_END), MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final RegistryObject<EntityType<CultistProjectile>> CULTIST_FIREBALL = register("cultist_fireball", "教徒火球", id -> EntityType.Builder.<CultistProjectile>of(
                            (type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.FIREBALL),
                            MobCategory.MISC)
                    .sized(0.55F, 0.55F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .noSummon()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<CultistProjectile>> CULTIST_ICE_MIST = register("cultist_ice_mist", "教徒冰雾", id -> EntityType.Builder.<CultistProjectile>of(
                            (type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.ICE_MIST),
                            MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .noSummon()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<CultistProjectile>> CULTIST_LIGHTNING_ORB = register("cultist_lightning_orb", "教徒闪电球", id -> EntityType.Builder.<CultistProjectile>of(
                            (type, level) -> new CultistProjectile(type, level, CultistProjectile.Variant.LIGHTNING_ORB),
                            MobCategory.MISC)
                    .sized(0.7F, 0.7F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .noSave()
                    .noSummon()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<PrimeCannonballProjectile>> PRIME_CANNONBALL = register("prime_cannonball", "机械炮弹", id -> EntityType.Builder.of(PrimeCannonballProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(1).noSave().build(id.toString()));
    public static final RegistryObject<EntityType<TitaniumShardsProjectile>> TITANIUM_SHARDS = register("titanium_shards", "钛金防护碎片", id -> EntityType.Builder.<TitaniumShardsProjectile>of(TitaniumShardsProjectile::new, MobCategory.MISC).sized(0, 0).fireImmune().noSummon().noSave().build(id.toString()));
    public static final RegistryObject<EntityType<FallingStarItemEntity>> FALLING_STAR = register("falling_star", "坠落之星", id -> EntityType.Builder.<FallingStarItemEntity>of(FallingStarItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final RegistryObject<EntityType<TreasureBagItemEntity>> TREASURE_BAG = register("treasure_bag", "宝藏袋", id -> EntityType.Builder.<TreasureBagItemEntity>of(TreasureBagItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build(id.toString()));
    public static final RegistryObject<EntityType<CoinPortalEntity>> COIN_PORTAL = register("coin_portal", "金币传送门", id -> EntityType.Builder.<CoinPortalEntity>of(CoinPortalEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<ThrownPowderEntity>> THROWN_POWDER = register("thrown_powder", "掷出的粉末", id -> EntityType.Builder.<ThrownPowderEntity>of(ThrownPowderEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().build(id.toString()));
    public static final RegistryObject<EntityType<DeadBodyPartEntity>> BODY_PART = register("body_part", "身体部件", id -> EntityType.Builder.<DeadBodyPartEntity>of(DeadBodyPartEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().noSave().noSummon().build(id.toString()));
    public static final RegistryObject<EntityType<FlameCloudEntity>> FLAME_CLOUD = register("flame_cloud", "火焰云", id -> EntityType.Builder.<FlameCloudEntity>of(FlameCloudEntity::new, MobCategory.MISC).sized(5, 5).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<SuperSpikyBallProjectile>> SUPER_SPIKY_BALL = register("super_spiky_ball", "超级尖球射弹", id -> EntityType.Builder.<SuperSpikyBallProjectile>of(SuperSpikyBallProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<SpearEntity>> SPEAR = register("spear", "长矛", id -> EntityType.Builder.<SpearEntity>of(SpearEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<StormSpearProjectile>> STORM_SPEAR_SHOT = register("storm_spear_shot", "风暴之矛射弹", id -> EntityType.Builder.of(StormSpearProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final RegistryObject<EntityType<SporeCloudProjectile>> SPORE_CLOUD = register("spore_cloud", "孢子云", id -> EntityType.Builder.of(SporeCloudProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(64).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<NorthPoleProjectile>> NORTH_POLE = register("north_pole", "北极矛射弹", id -> EntityType.Builder.of(NorthPoleProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final RegistryObject<EntityType<NorthPoleSubProjectile>> NORTH_POLE_SUB = register("north_pole_sub", "北极矛子射弹", id -> EntityType.Builder.of(NorthPoleSubProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<MushroomProjectile>> MUSHROOM = register("mushroom", "蘑菇孢子", id -> EntityType.Builder.of(MushroomProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).fireImmune().build(id.toString()));
    public static final RegistryObject<EntityType<GhastlyProjectile>> GHASTLY = register("ghastly", "恐魂射弹", id -> EntityType.Builder.of(GhastlyProjectile::new, MobCategory.MISC).sized(2.5F, 2.5F).clientTrackingRange(6).fireImmune().build(id.toString()));

    // 鱼钩
    public static final RegistryObject<EntityType<BaseFishingHook>> BASE_FISHING_HOOK = register("base_fishing_hook", "钓鱼钩", id -> EntityType.Builder.<BaseFishingHook>of(BaseFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final RegistryObject<EntityType<HotlineFishingHook>> HOTLINE_FISHING_HOOK = register("hotline_fishing_hook", "熔线钓鱼钩", id -> EntityType.Builder.<HotlineFishingHook>of(HotlineFishingHook::new, MobCategory.MISC).noSave().noSummon().fireImmune().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final RegistryObject<EntityType<CurioFishingHook>> CURIO_FISHING_HOOK = register("curio_fishing_hook", "钓鱼钩", id -> EntityType.Builder.<CurioFishingHook>of(CurioFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));
    public static final RegistryObject<EntityType<BloodyFishingHook>> BLOODY_FISHING_HOOK = register("bloody_fishing_hook", "血腥钓鱼钩", id -> EntityType.Builder.<BloodyFishingHook>of(BloodyFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build(id.toString()));

    // 钩爪
    public static final RegistryObject<EntityType<BaseHookEntity>> BASE_HOOK = registerHook("base_hook", "钩", BaseHookEntity::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> WEB_SLINGER = registerHook("web_slinger", "蛛丝吊索钩", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> SKELETRON_HAND = registerHook("skeletron_hand_hook", "骷髅王之手", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> SLIME_HOOK = registerHook("slime_hook", "史莱姆钩", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> FISH_HOOK = registerHook("fish_hook", "钓鱼钩", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> IVY_WHIP = registerHook("ivy_whip", "常春藤鞭", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> BAT_HOOK = registerHook("bat_hook", "蝙蝠钩", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> CANDY_CANE_HOOK = registerHook("candy_cane_hook", "糖棒钩", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<DualHookEntity>> DUAL_HOOK = registerHook("dual_hook", "双钩", DualHookEntity::new);
    public static final RegistryObject<EntityType<HookOfDissonanceEntity>> HOOK_OF_DISSONANCE = registerHookWithEnglishName("hook_of_dissonance", "失谐钩爪", "Hook of Dissonance", HookOfDissonanceEntity::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> THORN_HOOK = registerHook("thorn_hook", "荆棘钩", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<MimicHookEntity>> MIMIC_HOOK = registerHook("mimic_hook", "仿生钩", MimicHookEntity::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> ANTI_GRAVITY_HOOK = registerHookWithEnglishName("anti_gravity_hook", "反重力钩", "Anti-Gravity Hook", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> SPOOKY_HOOK = registerHook("spooky_hook", "诡异钩", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<AbstractHookEntity.Impl>> CHRISTMAS_HOOK = registerHook("christmas_hook", "圣诞钩", AbstractHookEntity.Impl::new);
    public static final RegistryObject<EntityType<LunarHookEntity>> LUNAR_HOOK = registerHook("lunar_hook", "月亮钩", LunarHookEntity::new);

    // 连枷
    public static final RegistryObject<EntityType<BaseFlailEntity>> FLAIL_ENTITY = register("flail", "链球", id -> EntityType.Builder.of(BaseFlailEntity::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(6).noSave().build(id.toString()));
    public static final RegistryObject<EntityType<GuardianFlailEntity>> GUARDIAN_FLAIL_ENTITY =
            register("guardian_flail", "守卫连枷", id -> EntityType.Builder
                    .<GuardianFlailEntity>of(
                            (type, level) -> new GuardianFlailEntity(type, level, false),
                            MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(20)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<GuardianFlailEntity>> ANCIENT_GUARDIAN_FLAIL_ENTITY =
            register("ancient_guardian_flail", "远古守卫连枷", id -> EntityType.Builder
                    .<GuardianFlailEntity>of(
                            (type, level) -> new GuardianFlailEntity(type, level, true),
                            MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(24)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<FlowerPowerFlailEntity>> FLOWER_POWER_FLAIL =
            register("flower_power", "花之力", id -> EntityType.Builder
                    .of(FlowerPowerFlailEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(20)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<DripplerCripplerFlailEntity>> DRIPPLER_CRIPPLER_FLAIL =
            register("drippler_crippler", "滴滴怪致残者", id -> EntityType.Builder
                    .of(DripplerCripplerFlailEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(20)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<FlaironFlailEntity>> FLAIRON_FLAIL =
            register("flairon", "猪鲨链球", id -> EntityType.Builder
                    .of(FlaironFlailEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .clientTrackingRange(20)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<LaunchedFlailEntity>> CHAIN_KNIFE_FLAIL =
            register("chain_knife", "链刀", id -> EntityType.Builder
                    .<LaunchedFlailEntity>of(
                            (type, level) -> new LaunchedFlailEntity(type, level, 0.0),
                            MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(20)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<AnchorFlailEntity>> ANCHOR_FLAIL =
            register("anchor", "锚", id -> EntityType.Builder
                    .of(AnchorFlailEntity::new, MobCategory.MISC)
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(24)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));

    public static final RegistryObject<EntityType<FlowerPowerPetalProjectile>> FLOWER_POWER_PETAL =
            register("flower_power_petal", "花之力花瓣", id -> EntityType.Builder
                    .of(FlowerPowerPetalProjectile::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<DripplerCripplerProjectile>> DRIPPLER_CRIPPLER_PROJECTILE =
            register("drippler_crippler_projectile", "血肉射弹", id -> EntityType.Builder
                    .of(DripplerCripplerProjectile::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<FlaironBubbleProjectile>> FLAIRON_BUBBLE =
            register("flairon_bubble", "泡泡", id -> EntityType.Builder
                    .of(FlaironBubbleProjectile::new, MobCategory.MISC)
                    .sized(0.65F, 0.65F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSave()
                    .build(id.toString()));

    // 矿车
    public static final RegistryObject<EntityType<ChikCrystalProjectile>> CHIK_CRYSTAL = register("chik_crystal", "吉克水晶", id -> EntityType.Builder
            .of(ChikCrystalProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<KrakenWaveProjectile>> KRAKEN_WAVE = register("kraken_wave", "克拉肯波纹", id -> EntityType.Builder
            .of(KrakenWaveProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<CthulhuEyeProjectile>> CTHULHU_EYE_PROJECTILE = register("cthulhu_eye_projectile", "克苏鲁之眼射弹", id -> EntityType.Builder
            .of(CthulhuEyeProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<CascadeFireProjectile>> CASCADE_FIRE = register("cascade_fire", "喷流火焰", id -> EntityType.Builder
            .of(CascadeFireProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<TerrarianProjectile>> TERRARIAN_PROJECTILE = register("terrarian_projectile", "泰拉悠悠球射弹", id -> EntityType.Builder
            .of(TerrarianProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build(id.toString()));
    public static final RegistryObject<EntityType<YoyoEntity>> YOYO =
            register("yoyo", "悠悠球", id -> EntityType.Builder
                    .of(YoyoEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .noSummon()
                    .noSave()
                    .build(id.toString()));

    // 子弹
    public static final RegistryObject<EntityType<BaseMinecartEntity>> VANILLA_MINECART = registerMinecart("vanilla_minecart", "矿车", BaseMinecartEntity::new);
    public static final RegistryObject<EntityType<BaseMinecartEntity>> WOODEN_MINECART = registerMinecart("wooden_minecart", "木制矿车", BaseMinecartEntity::new);
    public static final RegistryObject<EntityType<GenericMinecartEntity>> GENERIC_MINECART = registerMinecart("generic_minecart", "矿车", GenericMinecartEntity::new);
    public static final RegistryObject<EntityType<MechanicalCartEntity>> MECHANICAL_CART = registerMinecart("mechanical_cart", "机械矿车", MechanicalCartEntity::new);
    public static final RegistryObject<EntityType<MinecarpEntity>> MINECARP = registerMinecart("minecarp", "鲤鱼矿车", MinecarpEntity::new);
    public static final RegistryObject<EntityType<DemonicHellcartEntity>> DEMONIC_HELLCART = registerMinecart("demonic_hellcart", "恶魔地狱车", DemonicHellcartEntity::new);
    public static final RegistryObject<EntityType<MeowmereMinecartEntity>> MEOWMERE_MINECART = registerMinecart("meowmere_minecart", "喵喵矿车", MeowmereMinecartEntity::new);
    public static final RegistryObject<EntityType<DiggingMolecartEntity>> DIGGING_MOLECART = registerMinecart("digging_molecart", "挖掘地鼠车", DiggingMolecartEntity::new);

    /// 临时坐骑由坐骑物品创建，不参与自然生成或区块存档。
    public static final RegistryObject<EntityType<RideableUnicornMountEntity>> RIDEABLE_UNICORN = register("rideable_unicorn", "独角兽坐骑", id -> EntityType.Builder.<RideableUnicornMountEntity>of(RideableUnicornMountEntity::new, MobCategory.MISC).sized(1.2F, 1.6F).clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));
    public static final RegistryObject<EntityType<RideableLavaSharkMountEntity>> RIDEABLE_LAVA_SHARK = register("rideable_lava_shark", "岩浆鲨坐骑", id -> EntityType.Builder.<RideableLavaSharkMountEntity>of(RideableLavaSharkMountEntity::new, MobCategory.MISC).sized(1.5F, 0.8F).fireImmune().clientTrackingRange(8).updateInterval(1).noSummon().noSave().build(id.toString()));
    public static final RegistryObject<EntityType<RideableSlimeMountEntity>> RIDEABLE_SLIME =
            register("rideable_slime", "史莱姆坐骑", id -> EntityType.Builder
                    .of(RideableSlimeMountEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .noSummon()
                    .noSave()
                    .build(id.toString()));
    public static final RegistryObject<EntityType<RideableBeeMountEntity>> RIDEABLE_BEE =
            register("rideable_bee", "蜜蜂坐骑", id -> EntityType.Builder
                    .of(RideableBeeMountEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .noSummon()
                    .noSave()
                    .build(id.toString()));

    public static final RegistryObject<EntityType<BestiaryEntryDisplay>> BESTIARY_ENTRY_DISPLAY = withAttributes(register("bestiary_entry_display", "基础展示实体", id -> EntityType.Builder.of(BestiaryEntryDisplay::new, MobCategory.MISC)
                    .sized(1, 1)
                    .noSummon()
                    .noSave()
                    .build(id.toString())),
            () -> CreatureAttributeBuilder.living().build());

    public static final RegistryObject<EntityType<StarCannonBulletEntity>> STAR_CANNON_BULLET = register("star_cannon_bullet", "星星炮射弹", id -> EntityType.Builder.<StarCannonBulletEntity>of(StarCannonBulletEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).build(id.toString()));
    public static final RegistryObject<EntityType<BeeGunBullet>> BEE_GUN_BULLET = register("bee_gun_bullet", "蜜蜂枪射弹", id -> EntityType.Builder.<BeeGunBullet>of(BeeGunBullet::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build(id.toString()));
    public static final RegistryObject<EntityType<BaseBulletEntity>> BASE_BULLET_ENTITY = register("base_bullet", "子弹", id -> EntityType.Builder.<BaseBulletEntity>of(BaseBulletEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(id.toString()));
    public static final RegistryObject<EntityType<CustomBulletEntity>> GRAVITY_BULLET_ENTITY = register("gravity_bullet", "重力子弹", id -> EntityType.Builder.<CustomBulletEntity>of(CustomBulletEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(id.toString()));

    public static final RegistryObject<EntityType<RainbowSheep>> RAINBOW_SHEEP = withAttributes(register("rainbow_sheep", "彩虹羊", id -> EntityType.Builder.of(RainbowSheep::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.3F)
                    .clientTrackingRange(10)
                    .build(id.toString())),
            () -> CreatureAttributeBuilder.critter().maxHealth(8).movementSpeed(0.23).build());

    // 史莱姆尖刺
    public static final RegistryObject<EntityType<SlimeSpikeEntity>> SLIME_SPIKE = register("slime_spike", "史莱姆尖刺", id -> EntityType.Builder.of(SlimeSpikeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(id.toString()));

    public static final RegistryObject<EntityType<AccumulatingEnergyEntity>> ACCUMULATING_ENERGY = register("accumulating_energy", "聚积能量", id -> EntityType.Builder.<AccumulatingEnergyEntity>of(AccumulatingEnergyEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(1).build(id.toString()));

    private static <E extends AbstractHookEntity> RegistryObject<EntityType<E>> registerHook(String name, EntityType.EntityFactory<E> supplier) {
        int updateInterval = 20;
        // 钩爪依赖物品参数与在线玩家，仅在当前操作周期存在；区块重载后由玩家重新发射。
        return register(name, id -> EntityType.Builder.of(supplier, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(4)
                .updateInterval(updateInterval)
                .noSave()
                .build(id.toString()));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Function<ResourceLocation, EntityType<T>> function) {
        return PortDeferredRegisterExtension.register(ENTITIES, name, function);
    }

    // 属性与实体一起声明，延迟到 Forge 创建属性时构建，避免提前访问注册表。
    public static <T extends LivingEntity> RegistryObject<EntityType<T>> withAttributes(RegistryObject<EntityType<T>> type, Supplier<CreatureAttributeBuilder.Definition> attributes) {
        ATTRIBUTES.put(type, attributes);
        return type;
    }

    public static CreatureAttributeBuilder.Definition creatureAttributes(EntityType<?> type) {
        return CREATURE_DEFINITIONS.get(type);
    }

    /// 数据生成读取登记模板的快照，不暴露可修改的内部注册表。
    public static Map<EntityType<?>, CreatureAttributeBuilder.Definition> creatureDefinitions() {
        return Map.copyOf(CREATURE_DEFINITIONS);
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener((EntityAttributeCreationEvent event) ->
                ATTRIBUTES.forEach((type, attributes) -> {
                    var definition = attributes.get();
                    CREATURE_DEFINITIONS.put(type.get(), definition);
                    event.put(type.get(), definition.attributes());
                }));
        for (DeferredRegister<EntityType<?>> register : getEntities()) {
            register.register(eventBus);
        }
    }

    public static List<DeferredRegister<EntityType<?>>> getEntities() {
        return List.of(
                ENTITIES,
                MonsterEntities.ENTITIES,
                CritterEntities.ENTITIES,
                NpcEntities.ENTITIES,
                BossEntities.ENTITIES
        );
    }

    private static <E extends BaseMinecartEntity> RegistryObject<EntityType<E>> registerMinecart(String registryId, String chineseName, EntityType.EntityFactory<E> factory) {
        RegistryObject<EntityType<E>> entry = register(registryId, id -> EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(0.98F, 0.7F)
                .clientTrackingRange(8)
                .build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <E extends AbstractHookEntity> RegistryObject<EntityType<E>> registerHook(String registryId, String chineseName, EntityType.EntityFactory<E> supplier) {
        RegistryObject<EntityType<E>> entry = registerHook(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <E extends BaseBombEntity> RegistryObject<EntityType<E>> registerBomb(String registryId, String chineseName, EntityType.EntityFactory<E> supplier, float size) {
        RegistryObject<EntityType<E>> entry = register(registryId, id -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(size, size).clientTrackingRange(4).updateInterval(10).fireImmune().build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <E extends Entity> RegistryObject<EntityType<E>> registerStorageCompanion(String registryId, String chineseName, EntityType.EntityFactory<E> factory) {
        RegistryObject<EntityType<E>> entry = register(registryId, id -> EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(1.0F, 1.0F)
                .clientTrackingRange(10)
                .updateInterval(1)
                .noSummon()
                .noSave()
                .build(id.toString()));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryId, String chineseName, Function<ResourceLocation, EntityType<T>> function) {
        RegistryObject<EntityType<T>> entry = PortDeferredRegisterExtension.register(ENTITIES, registryId, function);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <E extends AbstractHookEntity> RegistryObject<EntityType<E>> registerHookWithEnglishName(String registryId, String chineseName, String englishName, EntityType.EntityFactory<E> supplier) {
        RegistryObject<EntityType<E>> entry = registerHook(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerWithEnglishName(String registryId, String chineseName, String englishName, Function<ResourceLocation, EntityType<T>> function) {
        RegistryObject<EntityType<T>> entry = PortDeferredRegisterExtension.register(ENTITIES, registryId, function);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
