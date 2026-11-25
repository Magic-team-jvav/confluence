package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.CircleAroundTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EaterOfSouls extends BaseFlyingMonster {
    private static final RawAnimation SPAWN = RawAnimation.begin().thenPlayAndHold("spawn");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    public EaterOfSouls(EntityType<? extends EaterOfSouls> type, Level level) {
        super(type, level);
        setDiscardFriction(true);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(EaterOfSouls.this),
                                new CircleAroundTargetAction(EaterOfSouls.this, 0.35, 6.0),
                                new ChargeAttackAction(EaterOfSouls.this, 0.5, 5)),
                        new LookForwardWanderFlyAction(EaterOfSouls.this, 0.2, 0.0F));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 0, state ->
                state.setAndContinue(getType() == MonsterEntities.CRIMERA.get() ? FLY : SPAWN)));
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }
}
