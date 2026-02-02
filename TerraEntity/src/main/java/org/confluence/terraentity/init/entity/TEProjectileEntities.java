package org.confluence.terraentity.init.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.model.*;
import org.confluence.terraentity.client.entity.renderer.mob.GeoNegativeVolumeRenderer;
import org.confluence.terraentity.client.entity.renderer.proj.*;
import org.confluence.terraentity.client.util.RegisterUtils;
import org.confluence.terraentity.entity.proj.*;
import org.confluence.terraentity.init.TEEffectStrategies;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TEParticles;

public class TEProjectileEntities {
    // 回旋镖
    public static final DeferredHolder<EntityType<?>, EntityType<BoomerangProjectile>> BOOMERANG_PROJECTILE = TEEntities.ENTITIES.register("boomerang_projectile", () -> EntityType.Builder.<BoomerangProjectile>of(BoomerangProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(TEEntities.Key("boomerang_projectile")));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableProj>> CABBAGE_PROJ = registerProj("cabbage_proj", ThrowableProj::new, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<LineProj>> BEE_STICK_PROJ = registerProj("bee_stick_proj", (e, l) ->
            new LineProj(e, l).setTexture(TerraEntity.space("textures/entity/model/stinger.png")), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<LineProj>> SUMMON_BEE_STICK_PROJ = registerProj("summon_bee_stick_proj", (e, l) ->
            new SummonBeeStick(e, l).setTexture(TerraEntity.space("textures/entity/model/stinger.png")), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SkullProjectile>> SKULL = registerProj("skull_proj", SkullProjectile::new, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<ParticleLineProj>> VILE_SPIT_PROJ = registerProj("vile_spit", (e, l) ->
            (ParticleLineProj) new ParticleLineProj(e, l).setCanBeHurt().addEffect(new MobEffectInstance(MobEffects.HUNGER, 100)), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<ParticleLineProj>> DARK_CASTER_PROJ = registerProj("dark_caster_proj", (e, l) ->
            (ParticleLineProj) new ParticleLineProj(e, l).setParticleOptions(ParticleTypes.SOUL).setCanBeHurt(), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<ParticleLineProj>> FIRE_IMP_PROJ = registerProj("fire_imp_proj", (e, l) ->
            (ParticleLineProj) new ParticleLineProj(e, l).setParticleOptions(ParticleTypes.FLAME).setCanBeHurt().setEffectStrategy(TEEffectStrategies.SET_FIRE_EFFECT.get()), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<ParticleLineProj>> FIRE_BOUND_PROJ = registerProj("fire_bound_proj", (e, l) ->
            (ParticleLineProj) new ParticleLineProj(e, l).setParticleOptions(TEParticles.FIRE_BOUND.get()).setEffectStrategy(TEEffectStrategies.SET_FIRE_EFFECT.get()), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<LineProj>> HARPY_FEATURE_PROJ = registerProj("harpy_feature", (e, l) ->
            new LineProj(e, l).setCanBeHurt().setTexture(TerraEntity.space("textures/entity/model/harpy_feather_projectile.png")), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<DemonScytheProj>> DEMON_SCYTHE_PROJ = registerProj("demon_scythe_proj", (e, l) ->
            (DemonScytheProj) new DemonScytheProj(e, l, null).setTexture(TerraEntity.space("textures/entity/model/demon_scythe_projectile.png")), 1.2F, 1.2F);
    public static final DeferredHolder<EntityType<?>, EntityType<LavaPillar>> LAVA_PILLAR = registerProj("lava_pillar", (e, l) ->
            new LavaPillar(e, l).setEffectStrategy(TEEffectStrategies.SET_FIRE_EFFECT.get()), 1.2F, 1.2F);
    public static final DeferredHolder<EntityType<?>, EntityType<ParticleLineProj>> THE_DESTROYER_LASER_PROJ = registerProj("the_destroyer_laser", ParticleLineProj::new, 0.75F, 0.75F);
    public static final DeferredHolder<EntityType<?>, EntityType<SeedProjectile>> SEED = registerProj("seed_proj", SeedProjectile::new, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SporeProjectile>> SPORE = registerProj("spore_proj", (e, l) ->
            new SporeProjectile(e, l).setCanBeHurt(), 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SpikeBallProjectile>> SPIKE_BALL = registerProj("spike_ball_proj", SpikeBallProjectile::new, 1.5F, 1.5F);

    // 鞭子
    public static final DeferredHolder<EntityType<?>, EntityType<WhipEntity>> WHIP_PROJECTILE = TEEntities.ENTITIES.register("whip_projectile", () -> EntityType.Builder.<WhipEntity>of(WhipEntity::new, MobCategory.MISC).updateInterval(1).clientTrackingRange(1).sized(0.5F, 0.5F).build(TEEntities.Key("whip_projectile")));

    //子弹
    public static final DeferredHolder<EntityType<?>, EntityType<TrailProjectile>> TRAIL_PROJECTILE = TEEntities.ENTITIES.register("trail_projectile", () -> EntityType.Builder.<TrailProjectile>of(TrailProjectile::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).setUpdateInterval(2).setTrackingRange(64).setShouldReceiveVelocityUpdates(true)
            .build(TEEntities.Key("trail_projectile")));

    // OBB剑气
    public static final DeferredHolder<EntityType<?>, EntityType<TrailSwordProj>> TRAIL_SWORD_PROJECTILE = TEEntities.ENTITIES.register("trail_sword_projectile", () -> EntityType.Builder.<TrailSwordProj>of(TrailSwordProj::new, MobCategory.MISC).updateInterval(1).clientTrackingRange(1).sized(0.5F, 0.5F).build(TEEntities.Key("trail_sword_projectile")));

    public static final DeferredHolder<EntityType<?>, EntityType<BeeProj>> BEE_PROJ = registerProj("bee_proj", BeeProj::new, 1.2F, 1.2F);

    public static final DeferredHolder<EntityType<?>, EntityType<SlimeSpikeProjectile>> SLIME_SPIKE = registerProj("slime_spike_projectile", SlimeSpikeProjectile::blueSpike, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SlimeSpikeProjectile>> JUNGLE_SPIKE = registerProj("jungle_spike_projectile", SlimeSpikeProjectile::jungleSpike, 0.5F, 0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SlimeSpikeProjectile>> ICE_SPIKE = registerProj("ice_spike_projectile", SlimeSpikeProjectile::iceSpike, 0.5F, 0.5F);


    //悠悠球
    public static final DeferredHolder<EntityType<?>, EntityType<YoyosEntity>> YOYO_PROJ = registerProj("yoyo_projectile", YoyosEntity::new, 0.5f, 0.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownIceProjectile>> THROWN_ICE_PROJECTILE = registerProj("thrown_ice_projectile", ThrownIceProjectile::new, 1f, 1f);
    public static final DeferredHolder<EntityType<?>, EntityType<IcePillar>> ICE_PILLAR = registerProj("ice_pillar", IcePillar::new, 1f, 3f);
    public static final DeferredHolder<EntityType<?>, EntityType<ShadowHandProjectile>> SHADOW_HAND = registerProj("shadow_hand", ShadowHandProjectile::new, 0.5f, 0.5f);

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        RegisterUtils.registerBaseProjRenderer(event, CABBAGE_PROJ.get(), c -> new CabbageProjModel<>(c.bakeLayer(CabbageProjModel.LAYER_LOCATION)));
        RegisterUtils.registerBaseProjRenderer(event, BEE_STICK_PROJ.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));
        RegisterUtils.registerBaseProjRenderer(event, SUMMON_BEE_STICK_PROJ.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));
        event.registerEntityRenderer(BOOMERANG_PROJECTILE.get(), BoomerangProjRenderer::new);
        event.registerEntityRenderer(SKULL.get(), SkullProjectileRenderer::new);
        RegisterUtils.registerBaseProjRenderer(event, VILE_SPIT_PROJ.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION))); // 没有指定纹理，默认粒子弹幕
        RegisterUtils.registerBaseProjRenderer(event, DARK_CASTER_PROJ.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));
        RegisterUtils.registerBaseProjRenderer(event, FIRE_IMP_PROJ.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));
        RegisterUtils.registerBaseProjRenderer(event, FIRE_BOUND_PROJ.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));
        RegisterUtils.registerBaseProjRenderer(event, HARPY_FEATURE_PROJ.get(), c -> new HarpyFeatherProjectileModel<>(c.bakeLayer(HarpyFeatherProjectileModel.LAYER_LOCATION)));
//        RegisterUtils.registerBaseProjRenderer(event, DEMON_SCYTHE_PROJ.get(), c->new DemonScytheModel<>(c.bakeLayer(DemonScytheModel.LAYER_LOCATION)));

        event.registerEntityRenderer(DEMON_SCYTHE_PROJ.get(), c -> new DemonScytheProjRenderer(c, new DemonScytheModel(c.bakeLayer(DemonScytheModel.LAYER_LOCATION))));
//        RegisterUtils.registerBaseProjRenderer(event, LAVA_PILLAR.get(), c->new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));
        event.registerEntityRenderer(LAVA_PILLAR.get(), c -> new LavaPillarRenderer(c, LAVA_PILLAR.getId().withPrefix("proj/")));

        RegisterUtils.registerBaseProjRenderer(event, THE_DESTROYER_LASER_PROJ.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));

        RegisterUtils.registerBaseProjRenderer(event, SEED.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));
        RegisterUtils.registerBaseProjRenderer(event, SPORE.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));
        RegisterUtils.registerBaseProjRenderer(event, SPIKE_BALL.get(), c -> new Stinger<>(c.bakeLayer(Stinger.LAYER_LOCATION)));

        // 子弹
        event.registerEntityRenderer(TEProjectileEntities.TRAIL_PROJECTILE.get(), TrailProjectileRenderer::new);
        // 鞭子
        event.registerEntityRenderer(WHIP_PROJECTILE.get(), WhipEntityRenderer::new);
        event.registerEntityRenderer(TRAIL_SWORD_PROJECTILE.get(), TrailSwordProjectileRenderer::new);

        event.registerEntityRenderer(BEE_PROJ.get(), c -> new ProjRenderer<>(c, new BeeProjModel(c.bakeLayer(BeeProjModel.LAYER_LOCATION))));
        event.registerEntityRenderer(SLIME_SPIKE.get(), c -> new ProjRenderer<>(c, new SlimeSpikedProjectlieModel<>(c.bakeLayer(SlimeSpikedProjectlieModel.LAYER_LOCATION))));
        event.registerEntityRenderer(JUNGLE_SPIKE.get(), c -> new ProjRenderer<>(c, new JungleSpikedProjectlieModel<>(c.bakeLayer(JungleSpikedProjectlieModel.LAYER_LOCATION))));
        event.registerEntityRenderer(ICE_SPIKE.get(), c -> new ProjRenderer<>(c, new IceSpikeProjectileModel<>(c.bakeLayer(IceSpikeProjectileModel.LAYER_LOCATION))));

        //悠悠球
        event.registerEntityRenderer(YOYO_PROJ.get(), (c) -> new YoyosRenderer(c));

        event.registerEntityRenderer(THROWN_ICE_PROJECTILE.get(), (c) -> new ThrownIceProjectileRenderer(c));
        event.registerEntityRenderer(ICE_PILLAR.get(), (c) -> new IcePillarRenderer(c));
        event.registerEntityRenderer(SHADOW_HAND.get(), (c) -> new GeoNegativeVolumeRenderer<>(c, SHADOW_HAND.getId().withPrefix("proj/")));
    }

    public static <T extends Projectile> DeferredHolder<EntityType<?>, EntityType<T>> registerProj(String name, EntityType.EntityFactory<T> entityFactory, float w, float h) {
        return TEEntities.ENTITIES.register(name, () -> EntityType.Builder.of(entityFactory, MobCategory.MISC).clientTrackingRange(10).sized(w, h).build(TEEntities.Key(name)));
    }

    public static <T extends Projectile> DeferredHolder<EntityType<?>, EntityType<T>> registerProj(String name, EntityType.EntityFactory<T> entityFactory) {
        return registerProj(name, entityFactory, 1, 1);
    }

    public static void register() {}
}
