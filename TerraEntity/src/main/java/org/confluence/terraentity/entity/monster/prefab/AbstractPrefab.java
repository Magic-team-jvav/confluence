package org.confluence.terraentity.entity.monster.prefab;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.confluence.terraentity.entity.monster.humanoid.HumanoidMonster;
import org.confluence.terraentity.init.TESounds;

import java.util.function.Function;
import java.util.function.Supplier;

public class AbstractPrefab {

    public static Supplier<AttributeBuilder> WARM_BUILDER =
            ()-> new AbstractPrefab().getPrefab().setNoGravity();

    public AbstractPrefab() {
        this.modifier = (builder)-> builder
                .setHurtSound(TESounds.ROUTINE_HURT)
                .setDeathSound(TESounds.ROUTINE_DEATH)
                .addTarget((t,e)->{
                    t.addGoal(1, new HurtByTargetGoal(e));
                    t.addGoal(2, new NearestAttackableTargetGoal<>(e, Player.class,false, LivingEntity::canBeSeenAsEnemy));
                });
    }
    private final Function<AttributeBuilder, AttributeBuilder> modifier;

    public AttributeBuilder getPrefab() {
        return modifier.apply(new AttributeBuilder());
    }

    public HumanoidMonster.HumanoidBuilder asHumanoid() {
        return (HumanoidMonster.HumanoidBuilder) modifier.apply(new HumanoidMonster.HumanoidBuilder());
    }
}
