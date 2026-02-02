package org.confluence.terraentity.init.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.client.entity.renderer.mob.*;
import org.confluence.terraentity.entity.summon.*;
import org.confluence.terraentity.init.TEEffectStrategies;
import org.confluence.terraentity.init.TEEntities;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class TESummonEntities {
    // tip 召唤物
    public static final DeferredHolder<EntityType<?>, EntityType<SummonFinch>> SUMMON_FINCH = TEEntities.registerCreature("finch_baby", SummonFinch::new ,  0.5F,0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSlime>> SUMMON_SLIME = TEEntities.registerCreature("slime_baby", SummonSlime::new , 0.5F,0.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonIronGolem>> SUMMON_IRON_GOLEM = TEEntities.registerCreature("i_32_iron_golem", SummonIronGolem::new, 1.5F,3F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonHornet>> SUMMON_HORNET = TEEntities.registerCreature("hornet_baby", SummonHornet::new, 0.5F,0.8F);
    public static final DeferredHolder<EntityType<?>, EntityType<SculkWisp>> SCULK_WISP = TEEntities.registerCreature("sculk_wisp", SculkWisp::new, 1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<FlyRangeAttackSummonMob<?>>> IMP = TEEntities.registerCreature("summon_imp", (e,l)->new FlyRangeAttackSummonMob<>(e,l,20,20,18,15, TEProjectileEntities.FIRE_IMP_PROJ), 1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSnowFlinx>> SUMMON_SNOW_FLINX = TEEntities.registerCreature("summon_snow_flinx", (e, l)->new SummonSnowFlinx(e,l),1F,1F);

    // 棱镜系列
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSword>> SUMMON_WOODEN_SWORD = TEEntities.registerCreature("summon_wooden_sword", (e, l)->new SummonSword(e,l, ()->Items.WOODEN_SWORD,0x714C11, TEEffectStrategies.POISON_5_SEC_2_AMP.get().getProvider(), 0.15f),1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSword>> SUMMON_STONE_SWORD = TEEntities.registerCreature("summon_stone_sword", (e, l)->new SummonSword(e,l, ()->Items.STONE_SWORD,0x8E9797, TEEffectStrategies.SLOW_5_SEC_2_AMP.get().getProvider(), 0.15f),1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSword>> SUMMON_IRON_SWORD = TEEntities.registerCreature("summon_iron_sword", (e, l)->new SummonSword(e,l, ()->Items.IRON_SWORD, 0xE6F0F3, TEEffectStrategies.HEAL_0_5_EFFECT.get().getProvider(), 0.15f),1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSword>> SUMMON_GOLDEN_SWORD = TEEntities.registerCreature("summon_golden_sword", (e, l)->new SummonSword(e,l, ()->Items.GOLDEN_SWORD, 0xE3D529, TEEffectStrategies.SET_FIRE_EFFECT.get().getProvider(), 0.15f),1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSword>> SUMMON_DIAMOND_SWORD = TEEntities.registerCreature("summon_diamond_sword", (e, l)->new SummonSword(e,l, ()->Items.DIAMOND_SWORD, 0x17CFC1, TEEffectStrategies.FROZEN_EFFECT.get().getProvider(), 0.15f),1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSword>> SUMMON_NETHERITE_SWORD = TEEntities.registerCreature("summon_netherite_sword", (e, l)->new SummonSword(e,l, ()->Items.NETHERITE_SWORD, 0x8136D2, TEEffectStrategies.HELL_FIRE_EFFECT.get().getProvider(), 0.15f),1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<Terraprisma>> TERRAPRISMA = TEEntities.registerCreature("terraprisma", (e,l)->new Terraprisma(e,l), 1F,1F);


    public static final DeferredHolder<EntityType<?>, EntityType<Chester>> CHESTER = TEEntities.registerCreature("chester", (e,l)->new Chester(e,l), 1F,1F);
    public static final DeferredHolder<EntityType<?>, EntityType<PiggyBank>> PIGGY_BANK = TEEntities.registerCreature("piggy_bank", (e,l)->new PiggyBank(e,l), 1F,1F);

//    Color c = new Color(0xFF714C11, true);  // 删掉注释查看颜色

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CHESTER.get(), c->new GeoNormalRenderer<>(c, TESummonEntities.CHESTER.getId().withPrefix("summon/"), false, 1f,0));
        event.registerEntityRenderer(PIGGY_BANK.get(), c->new GeoNormalRenderer<>(c, TESummonEntities.PIGGY_BANK.getId().withPrefix("summon/"), false, 1f,0));

        // sommon
        event.registerEntityRenderer(SUMMON_FINCH.get(), c-> new GeoNormalRenderer<>(c, SUMMON_FINCH.getId().withPrefix("summon/"),true));
        event.registerEntityRenderer(SUMMON_SLIME.get(), c-> new SlimeBabyRenderer(c, SUMMON_SLIME.getId().withPrefix("summon/")));
        event.registerEntityRenderer(SUMMON_IRON_GOLEM.get(), IronGolemRenderer::new);
        event.registerEntityRenderer(SUMMON_HORNET.get(), c->new GeoNormalRenderer<>(c, new GeoNormalModel<>(TEMonsterEntities.HORNET.getId(),false),true, 0.6f, 0.5f));
        event.registerEntityRenderer(SCULK_WISP.get(), c->new SculkWispRenderer(c, SCULK_WISP.getId().withPrefix("summon/")));
        event.registerEntityRenderer(IMP.get(), c->new GeoNormalRenderer<>(c, IMP.getId().withPrefix("summon/"), true, 0.8f,-0.5F));
        event.registerEntityRenderer(SUMMON_SNOW_FLINX.get(), c->new GeoNormalRenderer<>(c, SUMMON_SNOW_FLINX.getId().withPrefix("summon/"),false){
            @Override
            protected void adjustPose(PoseStack poseStack, SummonSnowFlinx animatable, BakedGeoModel model, float partialTick){
                poseStack.mulPose(Axis.YP.rotationDegrees(90+ Mth.lerp(partialTick, animatable.yBodyRotO - animatable.yHeadRotO,animatable.yBodyRot - animatable.yHeadRot)) );
            }
        });


        event.registerEntityRenderer(SUMMON_WOODEN_SWORD.get(), c->new SummonSwordRenderer<>(c));
        event.registerEntityRenderer(SUMMON_STONE_SWORD.get(), c->new SummonSwordRenderer<>(c));
        event.registerEntityRenderer(SUMMON_IRON_SWORD.get(), c->new SummonSwordRenderer<>(c));
        event.registerEntityRenderer(SUMMON_GOLDEN_SWORD.get(), c->new SummonSwordRenderer<>(c));
        event.registerEntityRenderer(SUMMON_DIAMOND_SWORD.get(), c->new SummonSwordRenderer<>(c));
        event.registerEntityRenderer(SUMMON_NETHERITE_SWORD.get(), c->new SummonSwordRenderer<>(c));
        event.registerEntityRenderer(TERRAPRISMA.get(), c->new TerraprismaRenderer(c));
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CHESTER.get(), AbstractSummonMob.createAttributes().build());
        event.put(PIGGY_BANK.get(), AbstractSummonMob.createAttributes().build());

        // sommon
        event.put(SUMMON_FINCH.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 32).add(Attributes.MOVEMENT_SPEED, 0.7).add(Attributes.GRAVITY, 0).add(Attributes.SAFE_FALL_DISTANCE, 1024).add(Attributes.ATTACK_KNOCKBACK, 0).build());
        event.put(SUMMON_SLIME.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 32).add(Attributes.MOVEMENT_SPEED, 0.7).build());
        event.put(SUMMON_IRON_GOLEM.get(), IronGolem.createAttributes().add(Attributes.FOLLOW_RANGE, 32).add(Attributes.MOVEMENT_SPEED, 0.6).build());
        event.put(SUMMON_HORNET.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 84).add(Attributes.MOVEMENT_SPEED, 0.7).build());
        event.put(SCULK_WISP.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 64).add(Attributes.MOVEMENT_SPEED, 0.7).build());
        event.put(IMP.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 84).add(Attributes.MOVEMENT_SPEED, 0.7).build());
        event.put(SUMMON_SNOW_FLINX.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 32).add(Attributes.MOVEMENT_SPEED, 0.7).build());


        event.put(SUMMON_WOODEN_SWORD.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 40).add(Attributes.MOVEMENT_SPEED, 1.5f).build());
        event.put(SUMMON_STONE_SWORD.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 40).add(Attributes.MOVEMENT_SPEED, 1.5f).build());
        event.put(SUMMON_IRON_SWORD.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 40).add(Attributes.MOVEMENT_SPEED, 1.5f).build());
        event.put(SUMMON_GOLDEN_SWORD.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 40).add(Attributes.MOVEMENT_SPEED, 1.5f).build());
        event.put(SUMMON_DIAMOND_SWORD.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 40).add(Attributes.MOVEMENT_SPEED, 1.5f).build());
        event.put(SUMMON_NETHERITE_SWORD.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 40).add(Attributes.MOVEMENT_SPEED, 1.5f).build());
        event.put(TERRAPRISMA.get(), AbstractSummonMob.createAttributes().add(Attributes.FOLLOW_RANGE, 40).add(Attributes.MOVEMENT_SPEED, 1.5f).build());
    }

    public static void register(){

    }
}
