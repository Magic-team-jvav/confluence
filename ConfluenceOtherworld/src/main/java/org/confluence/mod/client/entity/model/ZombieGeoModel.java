package org.confluence.mod.client.entity.model;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;

public class ZombieGeoModel extends VanillaZombieGeoModel<Zombie> {
    public ZombieGeoModel(EntityRendererProvider.Context context) {
        super(context, Zombie.Variant.NORMAL.modelPath());
    }

    @Override
    public ResourceLocation getModelResource(Zombie zombie) {
        return zombie.getVariant().modelPath();
    }

    @Override
    public ResourceLocation getTextureResource(Zombie zombie) {
        return zombie.getVariant().texturePath();
    }

    @Override
    public ResourceLocation getAnimationResource(Zombie zombie) {
        return zombie.getVariant().animationPath();
    }
}
