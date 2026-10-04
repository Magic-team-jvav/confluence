package org.confluence.mod.client.model.entity.projectile;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileAppearance;
import org.confluence.mod.common.entity.projectile.sword.GeoSwordProjectile;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public final class SwordProjectileGeoModel extends GeoModel<GeoSwordProjectile> {
    private static final ResourceLocation FALLBACK_MODEL = Confluence.asResource("geo/entity/visual_neuron.geo.json");
    private static final ResourceLocation FALLBACK_TEXTURE = Confluence.asResource("textures/entity/visual_neuron.png");

    @Override
    public ResourceLocation getModelResource(GeoSwordProjectile entity) {
        SwordProjectileAppearance.Geo appearance = appearance(entity);
        return appearance == null ? FALLBACK_MODEL : appearance.model();
    }

    @Override
    public ResourceLocation getTextureResource(GeoSwordProjectile entity) {
        SwordProjectileAppearance.Geo appearance = appearance(entity);
        return appearance == null ? FALLBACK_TEXTURE : appearance.texture();
    }

    @Override
    public @Nullable ResourceLocation getAnimationResource(GeoSwordProjectile entity) {
        SwordProjectileAppearance.Geo appearance = appearance(entity);
        return appearance == null ? null : appearance.animation().orElse(null);
    }

    private static @Nullable SwordProjectileAppearance.Geo appearance(GeoSwordProjectile entity) {
        if (entity.getProjectileComponent() == null || !(entity.getProjectileComponent().appearance() instanceof SwordProjectileAppearance.Geo appearance))
            return null;
        return appearance;
    }
}
