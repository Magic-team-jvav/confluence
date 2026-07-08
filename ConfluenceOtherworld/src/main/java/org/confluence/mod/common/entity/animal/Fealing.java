package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;

public class Fealing extends Fairy {
    public Fealing(EntityType<? extends Fealing> type, Level level) {
        super(type, level);
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
