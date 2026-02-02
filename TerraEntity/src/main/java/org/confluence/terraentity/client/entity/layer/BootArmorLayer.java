package org.confluence.terraentity.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.client.util.DefaultBoneBoundIdents;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 由于早期的模型没有设置boot层，所以需要这个来修复boot渲染
 */
public class BootArmorLayer<T extends LivingEntity & GeoAnimatable> extends ItemArmorGeoLayer<T> {

    static final String LEFT_ARMOR_LEG = DefaultBoneBoundIdents.LEFT_LEG_ARMOR_BONE_IDENT;
    static final String RIGHT_ARMOR_LEG = DefaultBoneBoundIdents.RIGHT_LEG_ARMOR_BONE_IDENT;

    public BootArmorLayer(GeoRenderer<T> geoRenderer) {
        super(geoRenderer);
    }

    @Nullable
    @Override
    protected ItemStack getArmorItemForBone(GeoBone bone, T animatable) {
        // Return the items relevant to the bones being rendered for additional rendering
        return switch (bone.getName()) {
            case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG -> this.bootsStack;
            default -> null;
        };
    }

    // Return the equipment slot relevant to the bone we're using
    @Nonnull
    @Override
    protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, T animatable) {
        return switch (bone.getName()) {
            case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG -> EquipmentSlot.FEET;
            default -> super.getEquipmentSlotForBone(bone, stack, animatable);
        };
    }

    // Return the ModelPart responsible for the armor pieces we want to render
    @Nonnull
    @Override
    protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, T animatable, HumanoidModel<?> baseModel) {
        return switch (bone.getName()) {
            case LEFT_ARMOR_LEG -> baseModel.leftLeg;
            case RIGHT_ARMOR_LEG -> baseModel.rightLeg;
            default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
        };
    }

    protected void prepModelPartForRender(PoseStack poseStack, GeoBone bone, ModelPart sourcePart) {
        switch (bone.getName()) {
            case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG:
                bone.setRotY(0);
                bone.setRotZ(0);
                bone.setRotX(0);
                break;
            default:
                break;
        }
        super.prepModelPartForRender(poseStack, bone, sourcePart);

    }


}