package org.confluence.terraentity.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.client.util.DefaultBoneBoundIdents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArmorLayer<T extends Mob & GeoEntity> extends ItemArmorGeoLayer<T> {

    public static final String HELMET = DefaultBoneBoundIdents.HEAD_ARMOR_BONE_IDENT;

    public static final String CHESTPLATE = DefaultBoneBoundIdents.BODY_ARMOR_BONE_IDENT;
    public static final String LEFT_HAND = DefaultBoneBoundIdents.LEFT_HAND_BONE_IDENT;
    public static final String RIGHT_HAND = DefaultBoneBoundIdents.RIGHT_HAND_BONE_IDENT;

    public static final String LEFT_ARMOR_LEG = DefaultBoneBoundIdents.LEFT_LEG_ARMOR_BONE_IDENT;
    public static final String RIGHT_ARMOR_LEG = DefaultBoneBoundIdents.RIGHT_LEG_ARMOR_BONE_IDENT;

    public static final String LEFT_BOOT = DefaultBoneBoundIdents.LEFT_FOOT_ARMOR_BONE_IDENT;
    public static final String RIGHT_BOOT = DefaultBoneBoundIdents.RIGHT_FOOT_ARMOR_BONE_IDENT;

    public ArmorLayer(GeoRenderer<T> geoRenderer) {
        super(geoRenderer);
    }

    @Nullable
    @Override
    protected ItemStack getArmorItemForBone(GeoBone bone, T animatable) {
        // Return the items relevant to the bones being rendered for additional rendering
        return switch (bone.getName()) {
            case LEFT_BOOT, RIGHT_BOOT -> this.bootsStack;
            case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG -> this.leggingsStack;
            case CHESTPLATE, LEFT_HAND, RIGHT_HAND -> this.chestplateStack;
            case HELMET -> this.helmetStack;
            default -> null;
        };
    }

    // Return the equipment slot relevant to the bone we're using
    @Nonnull
    @Override
    protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, T animatable) {
        return switch (bone.getName()) {
            case LEFT_BOOT, RIGHT_BOOT -> EquipmentSlot.FEET;
            case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG -> EquipmentSlot.LEGS;
            case RIGHT_HAND -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            case LEFT_HAND -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            case CHESTPLATE -> EquipmentSlot.CHEST;
            case HELMET -> EquipmentSlot.HEAD;
            default -> super.getEquipmentSlotForBone(bone, stack, animatable);
        };
    }

    // Return the ModelPart responsible for the armor pieces we want to render
    @Nonnull
    @Override
    protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, T animatable, HumanoidModel<?> baseModel) {
        return switch (bone.getName()) {
            case LEFT_BOOT, LEFT_ARMOR_LEG -> baseModel.leftLeg;
            case RIGHT_BOOT, RIGHT_ARMOR_LEG -> baseModel.rightLeg;
            case RIGHT_HAND -> baseModel.rightArm;
            case LEFT_HAND -> baseModel.leftArm;
            case CHESTPLATE -> baseModel.body;
            case HELMET -> baseModel.head;
            default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
        };
    }

    protected void prepModelPartForRender(PoseStack poseStack, GeoBone bone, ModelPart sourcePart) {

        switch (bone.getName()) {
            case LEFT_BOOT, LEFT_ARMOR_LEG, RIGHT_HAND,
                 RIGHT_BOOT, RIGHT_ARMOR_LEG, LEFT_HAND:
                bone.setRotY(0);
                bone.setRotZ(0);
                bone.setRotX(0);
//                oseStack.translate(0,-0.1f,0);
//                oseStack.scale(1f,1.2F,1f);
//                oseStack.translate(0,0.2f,0);
                break;

            case CHESTPLATE:
                break;

            case HELMET: {
                bone.setRotY(0);
                bone.setRotZ(0);
                bone.setRotX(0);
                break;
            }
            default:
                break;
        }
        super.prepModelPartForRender(poseStack, bone, sourcePart);

    }


}