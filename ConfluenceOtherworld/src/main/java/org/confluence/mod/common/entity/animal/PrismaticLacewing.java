package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class PrismaticLacewing extends Bird {

    public PrismaticLacewing(EntityType<? extends PrismaticLacewing> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingCritter.createFlyingCritterAttributes();
    }

    @Override
    public ResourceLocation getModelPath() {
        return Confluence.asResource("animal/butterfly");
    }

    @Override
    public ResourceLocation getTexturePath() {
        return Confluence.asResource("textures/entity/animal/butterfly/prismatic_lacewing.png");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        registerFlyOnlyController(controllers);
    }
}
