package org.confluence.terraentity.entity.monster.prefab;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.goal.AccelerateOnSeeingGoal;
import org.confluence.terraentity.entity.ai.goal.JumpAttack;
import org.confluence.terraentity.entity.ai.goal.JumpOverBlockGoal;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.init.TEEntities;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;

import java.util.List;
import java.util.function.Supplier;

import static software.bernie.geckolib.constant.DefaultAnimations.*;

public class LandMonsterPrefab extends AbstractPrefab {

    public static Supplier<AbstractMonster.Builder> FACE_MONSTER_BUILDER =
            ()->new LandMonsterPrefab(20,2,1,30,0.5f,0.1f).getPrefab()
                    .setStepHeight(3.2f)
                    .setJumpStrength(0.8f)
                    .addTarget((t,e)-> t.addGoal(1, new NearestAttackableTargetGoal<>(e, Player.class,false, LivingEntity::canBeSeenAsEnemy)))
                    .addGoal((g,e)-> {
                        g.addGoal(1, new JumpAttack(e, 3, 8));
                        g.addGoal(2, new JumpOverBlockGoal(e));
                        g.addGoal(3, new MeleeAttackGoal(e,  1f, true));
                        g.addGoal(7, new WaterAvoidingRandomStrollGoal(e, 1.0));
                    })
            ;

    public static Supplier<AbstractMonster.Builder> BLOOD_TUMORS =
            ()->new LandMonsterPrefab(1,0,0,0,0,0,0).getPrefab()
                    .setSafeFall(80)
                    .setNoAttackAttack()
                    .setAttackDamage((int) (Math.random() * 60 + 100))
                    .setTicker(e->{
                        if(!e.level().isClientSide && e.isAlive() && e.tickCount == e.getAttributeValue(Attributes.ATTACK_DAMAGE)){
                            List<EntityType<? extends Entity>> entities = List.of(TEEntities.BLOOD_CRAWLER.get(),TEEntities.FACE_MONSTER.get(),TEEntities.CRIMSON_KEMERA.get());
                            Entity summon = entities.get(e.getRandom().nextIntBetweenInclusive(0,entities.size()-1)).create(e.level());
                            summon.setPos(e.getX(),e.getY(),e.getZ());
                            summon.setDeltaMovement(new Vec3(0,0.4f,0));
                            e.level().addFreshEntity(summon);
                            e.kill();
                        }
                    })
                    .setController((c,e)->{
                        c.add(DefaultAnimations.genericIdleController(e));
                    })
            ;

    public static Supplier<AbstractMonster.Builder> BLOOD_ZOMBIE_BUILDER =
            ()->new LandMonsterPrefab(20,2,1,30,0.5f,0.1f).getPrefab()
                    .setMovementSpeed(0.20f)
                    .addTarget((t,e)-> {
                        t.addGoal(1,new AccelerateOnSeeingGoal(e,0.5f));
                        t.addGoal(2, new NearestAttackableTargetGoal<>(e, Player.class,false, LivingEntity::canBeSeenAsEnemy));

                    })
                    .setController((c,e)->{
                        c.add(new AnimationController<>(e,10,state -> state.setAndContinue(!state.isMoving() ? DefaultAnimations.IDLE :
                                e.clientTarget != null && e.clientTarget.isAlive()  ? RUN :WALK )
                        ));
                        c.add(DefaultAnimations.genericAttackAnimation(e,DefaultAnimations.ATTACK_STRIKE));
                    })
                    .addGoal((g,e)-> {
                        g.addGoal(2, new JumpOverBlockGoal(e));
                        g.addGoal(3, new MeleeAttackGoal(e,  0.8f, true));
                        g.addGoal(7, new WaterAvoidingRandomStrollGoal(e, 1.0));
                    });


    public LandMonsterPrefab(int health,int armor,int attack,int followRange,float knockBack,float knockbackResistance) {
        this(health,armor,attack,0.3f,followRange,knockBack,knockbackResistance);
    }

    public LandMonsterPrefab(int health,int armor,int attack,float moveSpeed,int followRange,float knockBack,float knockbackResistance) {
        super(health,armor,attack,followRange,knockBack,knockbackResistance);
        SIMPLE_MONSTER
                .setNavigation((e)->new GroundPathNavigation(e,e.level()))
                .setSafeFall(8)

                .setMovementSpeed(moveSpeed)
                .addTarget((t,e)->{
                    t.addGoal(2,new HurtByTargetGoal(e, Monster.class));
                })
                .setController((c,e)->{
                    c.add(DefaultAnimations.genericWalkIdleController(e));
                })
        ;
    }


    public AbstractMonster.Builder getPrefab() {
        return SIMPLE_MONSTER;
    }

}
