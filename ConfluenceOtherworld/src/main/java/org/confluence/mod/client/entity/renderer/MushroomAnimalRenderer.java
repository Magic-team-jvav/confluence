package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import org.confluence.mod.client.entity.model.MushroomAnimalModel;
import org.confluence.mod.common.entity.animal.Cluckshroom;
import org.confluence.mod.common.entity.animal.GlowingMooshroom;
import software.bernie.geckolib.animatable.GeoEntity;

public final class MushroomAnimalRenderer<T extends Animal & GeoEntity> extends GeoNormalRenderer<T> {
    public MushroomAnimalRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, new MushroomAnimalModel<>(path));
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return entity instanceof GlowingMooshroom || entity instanceof Cluckshroom chicken && chicken.isGlowing() ? 15 : super.getBlockLightLevel(entity, pos);
    }
}
