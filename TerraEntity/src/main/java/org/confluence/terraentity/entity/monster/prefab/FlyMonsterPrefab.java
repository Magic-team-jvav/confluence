package org.confluence.terraentity.entity.monster.prefab;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.goal.DashGoal;
import org.confluence.terraentity.entity.ai.goal.LookForwardWanderFlyGoal;
import org.confluence.terraentity.init.TESounds;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.terraentity.entity.monster.prefab.AttributeBuilder.copyFrom;

/**
 * 飞行怪预制体
 */
public class FlyMonsterPrefab extends AbstractPrefab {

    //在预制体上修改参数
    public static Supplier<AttributeBuilder> CRIMERA_BUILDER =
            ()->new FlyMonsterPrefab().getPrefab()
                    .setSpawnWithoutLight()
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,0.98f,0.4f,15));

                    })
            ;

    public static final Supplier<AttributeBuilder> EATER_OF_SOULS_BUILDER =
            ()->new FlyMonsterPrefab().getPrefab()
                    .setSpawnWithoutLight()
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,0.98f,0.4f,15));

                    })
            ;

    public static final Supplier<AttributeBuilder> DRIPPLER_BUILDER  =
            ()->new FlyMonsterPrefab().getPrefab()
                    .setSpawnWithoutLight()
                    .setHurtSound(TESounds.DRIPPLER_HURT)
                    .setDeathSound(TESounds.DRIPPLER_DEATH)
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,0.8f,0.2f,10));

                    })
            ;
    public static final Supplier<AttributeBuilder> SERVANT_OF_CTHULHU_BUILDER  =
            ()->new FlyMonsterPrefab().getPrefab()
                    .setHurtSound(TESounds.ROUTINE_HURT)
                    .setDeathSound(TESounds.ROUTINE_DEATH)
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,0.98f,0.4f,10));

                    })
            ;

    public static Supplier<AttributeBuilder> FLYING_FISH_BUILDER  =
            ()->new FlyMonsterPrefab().getPrefab()
                .addGoal((g,e)->{
                    g.addGoal(0, new DashGoal(e,0.95f,0.5f,15,
                            0.02f,5,10,45));

                })
            ;

    public static Supplier<AttributeBuilder> CAVE_BAT_BUILDER  =

            ()->new FlyMonsterPrefab().getPrefab()
                    .setHurtSound(TESounds.ROUTINE_HURT)
                    .setDeathSound(TESounds.BAT_DEATH)
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,1f,0.5f,30,
                                0.02f,20,20,45));
                    })
                    .setTicker(e->{
                        e.addDeltaMovement(new Vec3(0, Math.sin(e.tickCount*0.2f) * 0.03f ,0));
                    })
            ;

    public static Supplier<AttributeBuilder> JUNGLE_BAT_BUILDER  =
            ()->new FlyMonsterPrefab().getPrefab()
                    .setHurtSound(TESounds.ROUTINE_HURT)
                    .setDeathSound(TESounds.BAT_DEATH)
                    .setSpawnWithoutLight()
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,1f,0.5f,30,
                                0.02f,20,20,45));
                    })
                    .setTicker(e->{
                        e.addDeltaMovement(new Vec3(0, Math.sin(e.tickCount*0.2f) * 0.03f ,0));
                    })
            ;
    public static Supplier<AttributeBuilder> HELL_BAT_BUILDER  = ()-> copyFrom(CAVE_BAT_BUILDER)
            .setTicker(e->{
                e.addDeltaMovement(new Vec3(0, Math.sin(e.tickCount*0.2f) * 0.03f ,0));
                if(e.level().isClientSide){
                    int offset = e.getId() * 3;
                    float f = Mth.cos((float)(offset + e.tickCount) * 7.448451F * 0.017453292F + 3.1415927F);
                    float f2 = e.getBbWidth() * 0.5f;
                    float f3 = Mth.cos(e.getYRot() * 0.017453292F) * f2;
                    float f4 = Mth.sin(e.getYRot() * 0.017453292F) * f2;
                    float f5 = (0.3F + f * 0.45F) * e.getBbHeight() * 0.5f;
                    e.level().addParticle(ParticleTypes.LAVA, e.getX() + (double)f3, e.getY() + (double)f5, e.getZ() + (double)f4, 0.0, 0.0, 0.0);
                    e.level().addParticle(ParticleTypes.LAVA, e.getX() - (double)f3, e.getY() + (double)f5, e.getZ() - (double)f4, 0.0, 0.0, 0.0);
                }
            });
    public static Supplier<AttributeBuilder> SPORE_BAT_BUILDER = ()-> copyFrom(CAVE_BAT_BUILDER);
    public static Supplier<AttributeBuilder> ICE_BAT_BUILDER  = ()-> copyFrom(CAVE_BAT_BUILDER)
            .setTicker(e->{
                e.addDeltaMovement(new Vec3(0, Math.sin(e.tickCount*0.2f) * 0.03f ,0));
                if(e.level().isClientSide){
                    float f2 = e.getBbWidth() * 0.2f;
                    float f3 = Mth.cos(e.getYRot() * 0.017453292F + e.getRandom().nextFloat()) * f2;
                    float f4 = Mth.sin(e.getYRot() * 0.017453292F+ e.getRandom().nextFloat()) * f2;
                    float f5 = (0.3F) * (e.getBbHeight()+ e.getRandom().nextFloat() - 0.5f) * 2f;
                    e.level().addParticle(ParticleTypes.SNOWFLAKE, e.getX() + (double)f3, e.getY() + (double)f5, e.getZ() + (double)f4, 0.0, 0.0, 0.0);
                    e.level().addParticle(ParticleTypes.SNOWFLAKE, e.getX() - (double)f3, e.getY() + (double)f5, e.getZ() - (double)f4, 0.0, 0.0, 0.0);
                }
            });


    public static Supplier<AttributeBuilder> BEE_BUILDER  =
            ()->new AbstractPrefab()
                    .getPrefab()
                    .setNoAttachAttack()
                    .setNoGravity()
            ;


    public static final Supplier<AttributeBuilder> WANDERING_EYE_FISH_BUILDER =
            ()->new FlyMonsterPrefab().getPrefab()
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,0.98f,2.2f,15));
                    })
            ;
    public static Supplier<AttributeBuilder> METEOR_HEAD_BUILDER =
            ()->new FlyMonsterPrefab().getPrefab()
                    .setSpawnWithoutLight()
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,0.8f,0.2f,10));
                    })
            ;
    //从一个预制体复制参数再调整参数
    public static Supplier<AttributeBuilder> DO_NOTHING  = ()->copyFrom(CRIMERA_BUILDER)
            .setController((c,e)->c.add(new AnimationController<GeoAnimatable>(e,"move",10,s->PlayState.CONTINUE)));



    public FlyMonsterPrefab() {
        super();
        modifier = (b)->b
                .setNavigation((e)->new FlyingPathNavigation(e,e.level()))
                .setNoGravity()
                .setPushable(false)
                .setNoFriction()
                .addGoal((g,e)-> {
//                    g.addGoal(1, new MeleeAttackNoLookGoal(e,  false));
                    g.addGoal(2, new LookForwardWanderFlyGoal(e,0.2f, 0));
                })
                .setController((c,e)->c.add(new AnimationController<GeoAnimatable>(e,"move",10,
                        state->{state.setAnimation(FLY_ANIMATION);return PlayState.CONTINUE;})))
        ;
    }

    static final RawAnimation FLY_ANIMATION = RawAnimation.begin().thenLoop("fly");

    private final Function<AttributeBuilder, AttributeBuilder> modifier;

    public AttributeBuilder getPrefab() {
        return modifier.apply(super.getPrefab());
    }

}
