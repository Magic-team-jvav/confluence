package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;

public class HellButterfly extends BaseFlyingCritter {

    public HellButterfly(EntityType<? extends HellButterfly> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createEnemyAvoidingFlyingRoutine(), 1.25D);
            }
        };
    }

    @Override
    public ResourceLocation getModelPath() {
        return Confluence.asResource("animal/butterfly");
    }

    @Override
    public ResourceLocation getTexturePath() {
        return Confluence.asResource("textures/entity/animal/butterfly/hell_butterfly.png");
    }

    @Override
    public boolean isFullBright() {
        return true;
    }

}
