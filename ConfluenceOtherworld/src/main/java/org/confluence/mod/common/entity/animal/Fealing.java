package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;

/**
 * 飞灵是会躲避靠近玩家的以太小动物，不具备仙灵的寻宝引导行为。
 */
public final class Fealing extends BaseFlyingCritter {
    public Fealing(EntityType<? extends BaseFlyingCritter> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new AvoidEntityGoal<>(Fealing.this, Player.class, 6.0F, 1.0D, 1.3D)),
                        createFlyingRoutine()
                );
            }
        };
    }

    @Override
    public ResourceLocation getModelPath() {
        return Confluence.asResource("animal/fairy");
    }

    @Override
    public ResourceLocation getTexturePath() {
        return Confluence.asResource("textures/entity/animal/fairy/faeling.png");
    }
}
