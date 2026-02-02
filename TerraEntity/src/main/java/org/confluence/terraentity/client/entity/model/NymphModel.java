package org.confluence.terraentity.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.monster.Nymph;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class NymphModel<T extends Nymph> extends GeoNormalModel<T> {
    private final ResourceLocation animation;
    private final ResourceLocation model;
    private final ResourceLocation tex;
    private final ResourceLocation tex_dark;
    private final ResourceLocation tex_dark_blood;


    public NymphModel(ResourceLocation path) {
        super(path, true); // 没用
        String path_name = path.getPath();
        animation = TerraEntity.space("animations/entity/" + path_name + ".animation.json");
        model = TerraEntity.space("geo/entity/" + path_name + ".geo.json");
        tex = TerraEntity.space("textures/entity/" + path_name + ".png");
        tex_dark = TerraEntity.space("textures/entity/" + path_name + "_dark.png");
        tex_dark_blood = TerraEntity.space("textures/entity/" + path_name + "_dark_blood.png");

    }

    @Override
    public ResourceLocation getModelResource(T entity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        if (!entity.isTrigger()) {
            return tex;
        }
        if (entity.getHealth() > entity.getMaxHealth() * 0.5) {
            return tex_dark;
        }
        return tex_dark_blood;
    }

    @Override
    public ResourceLocation getAnimationResource(T entity) {
        return animation;
    }

    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        if (animatable.isTamed()) {
            GeoBone bone = getHead();
            if (bone != null) {
                bone.setRotZ(0);
            }
        }
        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}
