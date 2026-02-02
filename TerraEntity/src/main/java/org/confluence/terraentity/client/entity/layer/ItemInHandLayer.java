package org.confluence.terraentity.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.confluence.terraentity.client.event.RenderEvent;
import org.confluence.terraentity.client.util.DefaultBoneBoundIdents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

public class ItemInHandLayer<T extends Mob & GeoEntity> extends BlockAndItemGeoLayer<T> {

    public static final String LEFT_HAND = DefaultBoneBoundIdents.LEFT_HAND_BONE_IDENT;
    public static final String RIGHT_HAND = DefaultBoneBoundIdents.RIGHT_HAND_BONE_IDENT;


    public ItemInHandLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    // 获取手中物品
    @Nullable
    @Override
    protected ItemStack getStackForBone(GeoBone bone, T animatable) {

        // 防止iris渲染阴影导致场景中只有一个同类实体时骨骼旋转被重置（原因未知）
        if (RenderEvent.isAfterSky) {
            // 重置原骨骼的旋转，防止geo多次应用旋转
            bone.setRotY(0);
            bone.setRotZ(0);
            bone.setRotX(0);
        }

        return switch (bone.getName()) {
            case LEFT_HAND -> animatable.isLeftHanded() ?
                    animatable.getMainHandItem() : animatable.getOffhandItem();
            case RIGHT_HAND -> animatable.isLeftHanded() ?
                    animatable.getOffhandItem() : animatable.getMainHandItem();
            default -> null;
        };
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
        // Apply the camera transform for the given hand

        return switch (bone.getName()) {
            case RIGHT_HAND, "torso" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            case LEFT_HAND -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            default -> ItemDisplayContext.NONE;
        };
    }

    // Do some quick render modifications depending on what the item is
    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable,
                                      MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {

        poseStack.translate(0, 0, -0.0625);
        poseStack.translate(0, -0.0625, 0);
        boolean offhand = stack == animatable.getOffhandItem();
//                if (stack.getItem() instanceof PotionItem) {
//                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
//                }
        if (!offhand) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

            if (stack.getItem() instanceof ShieldItem)
                poseStack.translate(0, 0.125, -0.25);
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

            if (stack.getItem() instanceof ShieldItem) {
                poseStack.translate(0, 0.125, 0.25);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
        }

        adjustHandItemRendering(poseStack, stack, animatable, partialTick, offhand);
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
    }

    private void adjustHandItemRendering(PoseStack poseStack, ItemStack stack, T animatable, float partialTick, boolean offhand) {
        poseStack.translate(0.03F, 0.05, -0.5F);
    }

}