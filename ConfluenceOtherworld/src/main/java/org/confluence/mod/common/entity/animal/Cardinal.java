package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;

public class Cardinal extends Bird {

    public Cardinal(EntityType<? extends Cardinal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createCritterAttributes().add(Attributes.FLYING_SPEED, 0.4);
    }

    @Override
    public ResourceLocation getModelPath() { return Confluence.asResource("animal/cardinal"); }
    @Override
    public ResourceLocation getTexturePath() { return Confluence.asResource("textures/entity/cardinal/cardinal.png"); }
}
