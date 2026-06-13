package org.confluence.mod.client.renderer.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtils;

public class MultiHeadArmorItemRenderer<T extends Item & GeoItem> extends NormalArmorItemRenderer<T> {
    private final T[] headItems;
    protected GeoBone @NotNull [] headBones;

    public MultiHeadArmorItemRenderer(String path, T[] headItems) {
        super(path);
        this.headItems = headItems;
        this.headBones = new GeoBone[headItems.length];
    }

    @Override
    public @Nullable GeoBone getHeadBone() {
        return null;
    }

    @Override
    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        if (lastModel != bakedModel) {
            this.headBones = new GeoBone[headItems.length];
            for (int i = 0; i < headItems.length; i++) {
                headBones[i] = getGeoModel().getBone(BuiltInRegistries.ITEM.getKey(headItems[i]).getPath()).orElse(null);
            }
        }
        super.grabRelevantBones(bakedModel);
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        super.applyBoneVisibilityBySlot(currentSlot);
        if (currentSlot == EquipmentSlot.HEAD) {
            boolean visible = ((HumanoidModel<?>) this).head.visible;
            for (int i = 0; i < headItems.length; i++) {
                if (headItems[i] == animatable) {
                    setBoneVisible(headBones[i], visible);
                    break;
                }
            }
        }
    }

    @Override
    protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
        super.applyBaseTransformations(baseModel);
        for (GeoBone headBone : headBones) {
            if (headBone == null) continue;
            ModelPart headPart = baseModel.head;
            RenderUtils.matchModelPartRot(headPart, headBone);
            headBone.updatePosition(headPart.x, -headPart.y, headPart.z);
        }
    }

    @Override
    protected void setAllBonesVisible(boolean visible) {
        super.setAllBonesVisible(visible);
        for (GeoBone headBone : headBones) {
            setBoneVisible(headBone, visible);
        }
    }
}
