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
import org.confluence.mod.common.entity.npc.AbstractTerraNPC;
import org.confluence.mod.common.entity.projectile.*;
import org.confluence.mod.common.entity.projectile.bomb.*;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.confluence.mod.common.entity.projectile.boulder.ExplodeBoulderEntity;
import org.confluence.mod.common.entity.projectile.boulder.FollowerBoulderEntity;
import org.confluence.mod.common.entity.projectile.boulder.RollingCactusBoulderEntity;
import org.confluence.mod.common.entity.projectile.mana.*;
import org.confluence.mod.common.entity.projectile.strip.VilethronProjectile;
import org.confluence.mod.common.entity.projectile.sword.ForwardSwordProjectile;
import org.confluence.mod.common.entity.projectile.sword.LightBaneProjectile;
import org.confluence.mod.common.entity.projectile.sword.StarFuryProjectile;
import org.confluence.mod.common.event.ModEvents;
import org.confluence.terraentity.entity.proj.WhipEntity;

import java.util.function.Supplier;

/**
 * Fast Link:
 * 渲染器 {@link org.confluence.mod.client.event.ModClientEvents#registerEntityRenderers}
 * 发包   {@link ModEvents#registerPayloadHandlers)}
 * 属性   {@link ModEvents#registerAttributes}
 */
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Confluence.MODID);

    public static final Supplier<EntityType<BaseBombEntity>> BOMB_ENTITY = registerBomb("bomb_entity", BaseBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<BouncyBombEntity>> BOUNCY_BOMB_ENTITY = registerBomb("bouncy_bomb_entity", BouncyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<ScarabBombEntity>> SCARAB_BOMB_ENTITY = registerBomb("scarab_bomb_entity", ScarabBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<StickyBombEntity>> STICKY_BOMB_ENTITY = registerBomb("sticky_bomb_entity", StickyBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<BombFishEntity>> BOMB_FISH_ENTITY = registerBomb("bomb_fish_entity", BombFishEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<BaseGrenadeEntity>> GRENADE = registerBomb("grenade", BaseGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final Supplier<EntityType<BouncyGrenadeEntity>> BOUNCY_GRENADE = registerBomb("bouncy_grenade", BouncyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final Supplier<EntityType<StickyGrenadeEntity>> STICKY_GRENADE = registerBomb("sticky_grenade", StickyGrenadeEntity::new, BaseGrenadeEntity.DIAMETER);
    public static final Supplier<EntityType<BaseDynamiteEntity>> DYNAMITE = registerBomb("dynamite", BaseDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final Supplier<EntityType<BouncyDynamiteEntity>> BOUNCY_DYNAMITE = registerBomb("bouncy_dynamite", BouncyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final Supplier<EntityType<StickyDynamiteEntity>> STICKY_DYNAMITE = registerBomb("sticky_dynamite", StickyDynamiteEntity::new, BaseDynamiteEntity.DIAMETER);
    public static final Supplier<EntityType<BaseDirtBombEntity>> DIRT_BOMB = registerBomb("dirt_bomb", BaseDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<StickyDirtBombEntity>> STICKY_DIRT_BOMB = registerBomb("sticky_dirt_bomb", StickyDirtBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<DryBombEntity>> DRY_BOMB = registerBomb("dry_bomb", DryBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<LiquidBombEntity>> WET_BOMB = registerBomb("wet_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<LiquidBombEntity>> LAVA_BOMB = registerBomb("lava_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);
    public static final Supplier<EntityType<LiquidBombEntity>> HONEY_BOMB = registerBomb("honey_bomb", LiquidBombEntity::new, BaseBombEntity.DIAMETER);

    public static final Supplier<EntityType<BaseManaStaffProjectileEntity>> BASE_MANA_STAFF_PROJECTILE = ENTITIES.register("base_mana_staff_projectile", () -> EntityType.Builder.<BaseManaStaffProjectileEntity>of(BaseManaStaffProjectileEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build("confluence:base_mana_staff_projectile"));
    public static final Supplier<EntityType<VilethronProjectile>> VILETHRON_PROJECTILE = ENTITIES.register("vilethron_projectile", () -> EntityType.Builder.<VilethronProjectile>of(VilethronProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).build("confluence:vilethron_projectile"));
    public static final Supplier<EntityType<HurtnadoProjectile>> HURTNADO_PROJECTILE = ENTITIES.register("hurtnado_projectile", () -> EntityType.Builder.<HurtnadoProjectile>of(HurtnadoProjectile::new, MobCategory.MISC).sized(0.8F, 1.2F).clientTrackingRange(10).build("confluence:hurtnado_projectile"));
    public static final Supplier<EntityType<WaterStreamProjectile>> WATER_STREAM_PROJECTILE = ENTITIES.register("water_stream_projectile", () -> EntityType.Builder.<WaterStreamProjectile>of(WaterStreamProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).build("confluence:water_stream_projectile"));
    public static final Supplier<EntityType<WaterBoltProjectile>> WATER_BOLT_PROJECTILE = ENTITIES.register("water_bolt_projectile", () -> EntityType.Builder.<WaterBoltProjectile>of(WaterBoltProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).build("confluence:water_bolt_projectile"));
    public static final Supplier<EntityType<BallOfFireProjectile>> BALL_OF_FIRE_PROJECTILE = ENTITIES.register("ball_of_fire_projectile", () -> EntityType.Builder.<BallOfFireProjectile>of(BallOfFireProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10).build("confluence:ball_of_fire_projectile"));
    public static final Supplier<EntityType<BaseArrowEntity>> ARROW_PROJECTILE = ENTITIES.register("arrow_projectile", () -> EntityType.Builder.<BaseArrowEntity>of(BaseArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).build("confluence:arrow_projectile"));
    public static final Supplier<EntityType<EffectThrownPotion>> EFFECT_THROWN_POTION = ENTITIES.register("effect_thrown_potion", () -> EntityType.Builder.<EffectThrownPotion>of(EffectThrownPotion::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("confluence:effect_thrown_potion"));
    // 剑气
    public static final DeferredHolder<EntityType<?>,EntityType<ForwardSwordProjectile>> ICE_BLADE_SWORD_PROJECTILE = ENTITIES.register("ice_blade_sword_projectile", () -> EntityType.Builder.of(ForwardSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("confluence:ice_blade_sword_projectile"));
    public static final DeferredHolder<EntityType<?>,EntityType<StarFuryProjectile>> STAR_FURY_PROJECTILE = ENTITIES.register("star_fury_projectile", () -> EntityType.Builder.of(StarFuryProjectile::new, MobCategory.MISC).sized(1F, 1F).build("confluence:star_fury_projectile"));//星怒弹幕
    public static final DeferredHolder<EntityType<?>,EntityType<ForwardSwordProjectile>> ENCHANTED_SWORD_PROJECTILE = ENTITIES.register("enchanted_sword_projectile", () -> EntityType.Builder.of(ForwardSwordProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("confluence:enchanted_sword_projectile"));
    public static final DeferredHolder<EntityType<?>,EntityType<LightBaneProjectile>> LIGHTS_BANE_PROJECTILE = ENTITIES.register("lights_bane_projectile", () -> EntityType.Builder.of(LightBaneProjectile::new, MobCategory.MISC).sized(1F, 1F).build("confluence:lights_bane_projectile"));


    public static final Supplier<EntityType<BoomerangProjectile>> BOOMERANG_PROJECTILE = ENTITIES.register("boomerang_projectile", () -> EntityType.Builder.<BoomerangProjectile>of(BoomerangProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("confluence:boomerang_projectile"));
    public static final Supplier<EntityType<BoulderEntity>> BOULDER = ENTITIES.register("boulder", () -> EntityType.Builder.<BoulderEntity>of(BoulderEntity::new, MobCategory.MISC).sized(BoulderEntity.DIAMETER, BoulderEntity.DIAMETER).clientTrackingRange(6).build("confluence:boulder"));
    public static final Supplier<EntityType<FollowerBoulderEntity>> FOLLOWER_BOULDER = ENTITIES.register("follower_boulder", () -> EntityType.Builder.<FollowerBoulderEntity>of(FollowerBoulderEntity::new, MobCategory.MISC).sized(BoulderEntity.DIAMETER, BoulderEntity.DIAMETER).clientTrackingRange(6).build("confluence:follower_boulder"));
    public static final Supplier<EntityType<ExplodeBoulderEntity>> EXPLODE_BOULDER = ENTITIES.register("explode_boulder", () -> EntityType.Builder.<ExplodeBoulderEntity>of(ExplodeBoulderEntity::new, MobCategory.MISC).sized(BoulderEntity.DIAMETER, BoulderEntity.DIAMETER).clientTrackingRange(6).build("confluence:explode_boulder"));
    public static final Supplier<EntityType<RollingCactusBoulderEntity>> ROLLING_CACTUS_BOULDER = ENTITIES.register("rolling_cactus_boulder", () -> EntityType.Builder.<RollingCactusBoulderEntity>of(RollingCactusBoulderEntity::new, MobCategory.MISC).sized(BoulderEntity.DIAMETER, BoulderEntity.DIAMETER).clientTrackingRange(6).build("confluence:rolling_cactus_boulder"));
    public static final Supplier<EntityType<RollingCactusBoulderEntity.SpikeProjectile>> ROLLING_CACTUS_SPIKE = ENTITIES.register("rolling_cactus_spike", () -> EntityType.Builder.of(RollingCactusBoulderEntity.SpikeProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).build("confluence:rolling_cactus_spike"));
    public static final Supplier<EntityType<ThrownKnivesProjectile>> THROWN_KNIVES_PROJECTILE = ENTITIES.register("thrown_knives_projectile", () -> EntityType.Builder.<ThrownKnivesProjectile>of(ThrownKnivesProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("confluence:thrown_knives_projectile"));
    public static final Supplier<EntityType<ShurikenProjectile>> SHURIKEN_PROJECTILE = ENTITIES.register("shuriken_projectile", () -> EntityType.Builder.<ShurikenProjectile>of(ShurikenProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("confluence:shuriken_projectile"));
    public static final Supplier<EntityType<RopeCoilsProjectile>> ROPE_COILS = ENTITIES.register("rope_coils", () -> EntityType.Builder.<RopeCoilsProjectile>of(RopeCoilsProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("confluence:rope_coils"));
    public static final Supplier<EntityType<IceTofuBrickProjectile>> ICE_TOFU_BRICK_PROJECTILE = ENTITIES.register("ice_tofu_brick_projectile", () -> EntityType.Builder.<IceTofuBrickProjectile>of(IceTofuBrickProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build("confluence:ice_tofu_brick_projectile"));

    public static final Supplier<EntityType<BaseFishingHook>> BASE_FISHING_HOOK = ENTITIES.register("base_fishing_hook", () -> EntityType.Builder.<BaseFishingHook>of(BaseFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build("confluence:base_fishing_hook"));
    public static final Supplier<EntityType<HotlineFishingHook>> HOTLINE_FISHING_HOOK = ENTITIES.register("hotline_fishing_hook", () -> EntityType.Builder.<HotlineFishingHook>of(HotlineFishingHook::new, MobCategory.MISC).noSave().noSummon().fireImmune().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build("confluence:hotline_fishing_hook"));
    public static final Supplier<EntityType<CurioFishingHook>> CURIO_FISHING_HOOK = ENTITIES.register("curio_fishing_hook", () -> EntityType.Builder.<CurioFishingHook>of(CurioFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build("confluence:curio_fishing_hook"));
    public static final Supplier<EntityType<BloodyFishingHook>> BLOODY_FISHING_HOOK = ENTITIES.register("bloody_fishing_hook", () -> EntityType.Builder.<BloodyFishingHook>of(BloodyFishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build("confluence:bloody_fishing_hook"));

    public static final Supplier<EntityType<BaseHookEntity>> BASE_HOOK = registerHook("base_hook", BaseHookEntity::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> WEB_SLINGER = registerHook("web_slinger", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> SKELETRON_HAND = registerHook("skeletron_hand", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> SLIME_HOOK = registerHook("slime_hook", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> FISH_HOOK = registerHook("fish_hook", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> IVY_WHIP = registerHook("ivy_whip", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> BAT_HOOK = registerHook("bat_hook", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> CANDY_CANE_HOOK = registerHook("candy_cane_hook", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<DualHookEntity>> DUAL_HOOK = registerHook("dual_hook", DualHookEntity::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> HOOK_OF_DISSONANCE = registerHook("hook_of_dissonance", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> THORN_HOOK = registerHook("thorn_hook", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<MimicHookEntity>> MIMIC_HOOK = registerHook("mimic_hook", MimicHookEntity::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> ANTI_GRAVITY_HOOK = registerHook("anti_gravity_hook", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> SPOOKY_HOOK = registerHook("spooky_hook", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<AbstractHookEntity.Impl>> CHRISTMAS_HOOK = registerHook("christmas_hook", AbstractHookEntity.Impl::new);
    public static final Supplier<EntityType<LunarHookEntity>> LUNAR_HOOK = registerHook("lunar_hook", LunarHookEntity::new);
    /* todo 静止钩 */

    public static final Supplier<EntityType<BaseMinecartEntity>> WOODEN_MINECART = registerMinecart("wooden_minecart", BaseMinecartEntity::new);
    public static final Supplier<EntityType<GenericMinecartEntity>> GENERIC_MINECART = registerMinecart("generic_minecart", GenericMinecartEntity::new);
    public static final Supplier<EntityType<MechanicalCartEntity>> MECHANICAL_CART = registerMinecart("mechanical_cart", MechanicalCartEntity::new);
    public static final Supplier<EntityType<MinecarpEntity>> MINECARP = registerMinecart("minecarp", MinecarpEntity::new);
    public static final Supplier<EntityType<DemonicHellcartEntity>> DEMONIC_HELLCART = registerMinecart("demonic_hellcart", DemonicHellcartEntity::new);
    public static final Supplier<EntityType<MeowmereMinecartEntity>> MEOWMERE_MINECART = registerMinecart("meowmere_minecart", MeowmereMinecartEntity::new);
    public static final Supplier<EntityType<DiggingMolecartEntity>> DIGGING_MOLECART = registerMinecart("digging_molecart", DiggingMolecartEntity::new);

    public static final Supplier<EntityType<FallingStarItemEntity>> FALLING_STAR_ITEM_ENTITY = ENTITIES.register("falling_star", () -> EntityType.Builder.<FallingStarItemEntity>of(FallingStarItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build("confluence:falling_star"));
    public static final Supplier<EntityType<TreasureBagItemEntity>> TREASURE_BAG_ITEM_ENTITY = ENTITIES.register("treasure_bag", () -> EntityType.Builder.<TreasureBagItemEntity>of(TreasureBagItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(20).build("confluence:treasure_bag"));
    public static final Supplier<EntityType<CoinPortalEntity>> COIN_PORTAL = ENTITIES.register("coin_portal", () -> EntityType.Builder.<CoinPortalEntity>of(CoinPortalEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(6).build("confluence:coin_portal"));
    public static final Supplier<EntityType<ThrownPowderEntity>> THROWN_POWDER = ENTITIES.register("thrown_powder", () -> EntityType.Builder.<ThrownPowderEntity>of(ThrownPowderEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().build("confluence:thrown_powder"));
    public static final Supplier<EntityType<DeadBodyPartEntity>> BODY_PART = ENTITIES.register("body_part", () -> EntityType.Builder.<DeadBodyPartEntity>of(DeadBodyPartEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().noSave().noSummon().build("confluence:body_part"));

    public static final Supplier<EntityType<TargetDummyEntity>> TARGET_DUMMY = ENTITIES.register("target_dummy", () -> EntityType.Builder.<TargetDummyEntity>of(TargetDummyEntity::new, MobCategory.MISC).sized(1.0F, 2.0F).clientTrackingRange(6).build("confluence:target_dummy"));

    // npc
    public static final DeferredHolder<EntityType<?>, EntityType<AbstractTerraNPC>> GUIDE = ENTITIES.register("guide", () -> EntityType.Builder.of(AbstractTerraNPC::new, MobCategory.CREATURE).sized(0.95F, 1.95F).clientTrackingRange(6).build("confluence:guide"));


    private static <E extends BaseMinecartEntity> Supplier<EntityType<E>> registerMinecart(String id, EntityType.EntityFactory<E> factory) {
        return ENTITIES.register(id, () -> EntityType.Builder.of(factory, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8).build("confluence:" + id));
    }

    private static <E extends AbstractHookEntity> Supplier<EntityType<E>> registerHook(String id, EntityType.EntityFactory<E> supplier) {
        return ENTITIES.register(id, () -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("confluence:" + id));
    }

    private static <E extends BaseBombEntity> Supplier<EntityType<E>> registerBomb(String id, EntityType.EntityFactory<E> supplier, float size) {
        return ENTITIES.register(id, () -> EntityType.Builder.of(supplier, MobCategory.MISC).sized(size, size).clientTrackingRange(4).updateInterval(10).fireImmune().build("confluence:" + id));
    }
}
