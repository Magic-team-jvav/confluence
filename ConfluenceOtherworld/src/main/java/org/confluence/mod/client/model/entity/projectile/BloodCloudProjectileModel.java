package org.confluence.mod.client.model.entity.projectile;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.mana.CloudProjectile;
import software.bernie.geckolib.model.GeoModel;

public class BloodCloudProjectileModel extends GeoModel<CloudProjectile> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/entity/blood_cloud_projectile.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/blood_cloud_projectile.png");

    @Override
    public ResourceLocation getModelResource(CloudProjectile animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(CloudProjectile animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(CloudProjectile animatable) {
        return null;
    }
}
