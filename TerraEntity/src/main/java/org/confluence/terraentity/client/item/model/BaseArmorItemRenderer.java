package org.confluence.terraentity.client.item.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import org.confluence.terraentity.TerraEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

public class BaseArmorItemRenderer<T extends Item & GeoItem> extends DyeableGeoArmorRenderer<T> {

    public BaseArmorItemRenderer(String path) {
        super(new DefaultedItemGeoModel<>(TerraEntity.space(path)));
    }

    @Override
    protected boolean isBoneDyeable(GeoBone bone) {
        return true;
    }

    @Override
    protected Color getColorForBone(GeoBone bone) {

        return Color.WHITE;
    }

    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        return super.getTextureLocation(animatable);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}