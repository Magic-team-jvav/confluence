package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;

public class BlueJay extends Bird {

    public BlueJay(EntityType<? extends BlueJay> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createCritterAttributes().add(Attributes.FLYING_SPEED, 0.4);
    }

    @Override
    public ResourceLocation getModelPath() { return Confluence.asResource("animal/blue_jay"); }
    @Override
    public ResourceLocation getTexturePath() { return Confluence.asResource("textures/entity/blue_jay/blue_jay.png"); }
}
