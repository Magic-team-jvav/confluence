package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.PlayerCloseCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MeleeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;

public class HostileBunny extends Bunny {
    private static final Variant[] HOSTILE_VARIANTS = {Variant.CORRUPT, Variant.VICIOUS};

    public HostileBunny(EntityType<? extends Bunny> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Bunny.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected BTRoot createBT() {
        BTNode tree = SelectorNode.of(
                SequenceNode.of(new PlayerCloseCondition(this, 8.0),
                        new MoveToTargetAction(this, 1.2, 2),
                        new MeleeAttackAction(this, 2.0)),
                SequenceNode.of(new WaitAction(10 + random.nextInt(30)), new RandomStrollAction(this, 1.0, 5))
        );
        return new BTRoot() {
            @Override
            protected BTNode createTree() { return tree; }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(CritterVariantUtil.uniform(random, HOSTILE_VARIANTS));
    }
}
