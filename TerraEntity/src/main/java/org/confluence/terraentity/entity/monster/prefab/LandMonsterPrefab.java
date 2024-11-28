package org.confluence.terraentity.entity.monster.prefab;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.confluence.terraentity.entity.ai.goal.JumpAttack;
import org.confluence.terraentity.entity.ai.goal.JumpOverBlockGoal;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.entity.monster.BloodCrawler;
import org.confluence.terraentity.init.TEEntities;
import software.bernie.geckolib.constant.DefaultAnimations;

import java.util.function.Supplier;

public class LandMonsterPrefab extends AbstractPrefab {

    public static Supplier<AbstractMonster.Builder> FACE_MONSTER_BUILDER =
            ()->new LandMonsterPrefab(20,2,1,30,0.5f,0.1f).getPrefab()
                    .setStepHeight(3.2f)
                    .setJumpStrength(0.8f)
                    .addTarget((t,e)->{

                        t.addGoal(1, new NearestAttackableTargetGoal<>(e, Player.class,false, LivingEntity::canBeSeenAsEnemy));

                    })
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
                            Entity summon = TEEntities.BLOOD_CRAWLER.get().create(e.level());
                            summon.setPos(e.getX(),e.getY(),e.getZ());
                            summon.setDeltaMovement(new Vec3(0,0.25f,0));
                            e.level().addFreshEntity(summon);
                            e.discard();
                        }
                    })
                    .setController((c,e)->{
                        c.add(DefaultAnimations.genericIdleController(e));
                    })
            ;
    public LandMonsterPrefab(int health,int armor,int attack,int followRange,float knockBack,float knockbackResistance) {
        this(health,armor,attack,0.3f,followRange,knockBack,knockbackResistance);
    }

    public LandMonsterPrefab(int health,int armor,int attack,float moveSpeed,int followRange,float knockBack,float knockbackResistance) {
        super(health,armor,attack,followRange,knockBack,knockbackResistance);
        SIMPLE_FLY_DASH_MONSTER
                .setNavigation((e)->new GroundPathNavigation(e,e.level()))
                .setSafeFall(8)

                .setMovementSpeed(moveSpeed)
                .setController((c,e)->{
                    c.add(DefaultAnimations.genericWalkIdleController(e));
                })
        ;
    }


    public AbstractMonster.Builder getPrefab() {
        return SIMPLE_FLY_DASH_MONSTER;
    }

}
