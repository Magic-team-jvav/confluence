package org.confluence.terraentity.entity.monster.prefab;

import com.google.common.base.Suppliers;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import org.confluence.terraentity.entity.ai.goal.DashGoal;
import org.confluence.terraentity.entity.ai.goal.LookForwardWanderFlyGoal;
import org.confluence.terraentity.entity.ai.goal.MeleeAttackNoLookGoal;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.init.TESounds;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Supplier;

import static org.confluence.terraentity.entity.monster.AbstractMonster.copyFrom;

/**
 * 飞行怪预制体
 */
public class FlyMonsterPrefab extends AbstractPrefab {

    //在预制体上修改参数
    public static Supplier<AbstractMonster.Builder> CRIMSON_KEMERA_BUILDER =
            ()->new FlyMonsterPrefab(20,2,11,30,0.5f,0.1f).getPrefab()
                    .setHurtSound(TESounds.ROUTINE_HURT)
                    .setDeathSound(TESounds.ROUTINE_DEATH)
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,0.98f,0.4f,15));

                    })
            ;

    public static final Supplier<AbstractMonster.Builder> EATER_OF_SOULS_BUILDER =
            Suppliers.memoize(()->new FlyMonsterPrefab(20,2,11,30,0.5f,0.1f).getPrefab()
                    .setHurtSound(TESounds.ROUTINE_HURT)
                    .setDeathSound(TESounds.ROUTINE_DEATH)
                    .addGoal((g,e)->{
                        g.addGoal(0, new DashGoal(e,0.98f,0.4f,15));

                    }))
            ;

    public static final Supplier<AbstractMonster.Builder> DRIPPLER_BUILDER  =
            Suppliers.memoize(()->new FlyMonsterPrefab(26,3,14,64,0.5f,0.2f).getPrefab()
                .setHurtSound(TESounds.DRIPPLER_HURT)
                .setDeathSound(TESounds.DRIPPLER_DEATH)
                .addGoal((g,e)->{
                    g.addGoal(0, new DashGoal(e,0.8f,0.2f,10));

                }))
            ;

    public static Supplier<AbstractMonster.Builder> FLYING_FISH_BUILDER  =
            ()->new FlyMonsterPrefab(10,1,2,30,0.5f,0.3f).getPrefab()
                .setHurtSound(TESounds.ROUTINE_HURT)
                .setDeathSound(TESounds.ROUTINE_DEATH)
                .addGoal((g,e)->{
                    g.addGoal(0, new DashGoal(e,0.95f,0.5f,15,
                            0.02f,5,10,45));

                })
            ;


    //从一个预制体复制参数再调整参数
    public static Supplier<AbstractMonster.Builder> DO_NOTHING  = ()->copyFrom(CRIMSON_KEMERA_BUILDER)
            .setController((c,e)->c.add(new AnimationController<GeoAnimatable>(e,"move",10,s->PlayState.CONTINUE)));





    public FlyMonsterPrefab(int health,int armor,int attack,int followRange,float knockBack,float knockbackResistance) {
        super(health,armor,attack,followRange,knockBack,knockbackResistance);
        SIMPLE_MONSTER
                .setNavigation((e)->new FlyingPathNavigation(e,e.level()))
                .setSafeFall(1000)
                .setNoGravity()
                .setNoFriction()
                .addGoal((g,e)-> {
                    g.addGoal(1, new MeleeAttackNoLookGoal(e,  false));
                    g.addGoal(2, new LookForwardWanderFlyGoal(e,0.2f));
                })
                .setController((c,e)->c.add(new AnimationController<GeoAnimatable>(e,"move",10,
                        state->{state.setAnimation(RawAnimation.begin().thenLoop("fly"));return PlayState.CONTINUE;})))
        ;
    }

    public AbstractMonster.Builder getPrefab() {
        return SIMPLE_MONSTER;
    }

}
