package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.item.BowItems;
import org.confluence.mod.common.item.bow.ShortBowItem;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public final class ArrowInBowRenderer {
    private static final Map<Item, Vec3> offsets = new HashMap<>();

    public static void initAdaptionMap() {
        // 长弓有三个阶段参数
        offsets.put(BowItems.DAEDALUS_STORM_BOW.get(), new Vec3(0.365f, 0.2f, 0.0f));

        // 短弓只有x一个参数有效
        offsets.put(BowItems.PLATINUM_SHORT_BOW.get(), new Vec3(0.08f, 0.0f, 0.0f));
    }


    // tip 对长短弓的拉弓前后位移进行修改
    private static float getOffset(float charge, @Nullable Vec3 off, Item item) {
        float offset;
        // tip 在这里修改参数
        float a = 0F;    // pulling_0 / short bow
        float b = 0F;    // pulling_1
        float c = 0F;    // pulling_2

        /*----------------------------------------------------------------
         *  原理：1. 若初始off = null，使用默认参数 + abc。
         *          改变参数abc，热重载测试，将abc参数put offsets中。
         *          下次游戏off！=null，使用修改后的参数。
         * <p>
         *       2. 若初始off ！= null，使用原始参数 + abc。
         *          改变参数abc，热重载测试，将abc参数加到offsets中。
         *          下次游戏off ！= null，使用修改后的参数。
        --------------------------------------------------------------------*/

        if (item instanceof ShortBowItem) {return off == null ? a : (float) off.x + a;}

        if (off == null) {
            // 默认参数
            offset = charge < 0.65f ? 0.23f + a
                    : charge < 0.9f ? 0.62f + b
                    : 1f + c
            ;
        } else {
            // 修正后的参数
            offset = (float) (charge < 0.65f ? 0.23f + off.x + a
                    : charge < 0.9f ? 0.62f + off.y + b
                    : 1f + off.z + c
            );
        }
        return offset;
    }

    public static void bowTransform(ItemStack bow, PoseStack poseStack, float charge, ItemDisplayContext displayContext) {
        if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            // 拉弓前后位移
            Item item = bow.getItem();
            Vec3 off = offsets.get(item);
            float offset = getOffset(charge, off, item);

            poseStack.translate(0, 0.2, 0);

            if (item instanceof ShortBowItem) {
                poseStack.translate(0, 0, offset);
            } else {
                //                           前后阶段偏移系数      前后帧偏移系数
                poseStack.translate(0, -offset * 0.04, offset * 0.13);
            }
            if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
            else {
                poseStack.translate(0, -0.12, -0.25);
            }
            poseStack.scale(1.01f, 1.01f, 1.01f);
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {

            poseStack.mulPose(Axis.YN.rotationDegrees(-90));
            poseStack.mulPose(Axis.ZN.rotationDegrees(20));
            poseStack.mulPose(Axis.XN.rotationDegrees(10));

            poseStack.translate(0.1, -0.25, -0.15);

        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.mulPose(Axis.YN.rotationDegrees(-90));
            poseStack.mulPose(Axis.ZN.rotationDegrees(20));

            poseStack.translate(0.1, -0.3, 0);
        }
    }

    public static void repeaterTransform(ItemStack arrowItem, ItemStack repeater, PoseStack poseStack, ItemDisplayContext displayContext) {
        if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            if (arrowItem.getItem() instanceof FireworkRocketItem) {
                poseStack.translate(-0.12, 0.278, 0.15);
                poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                poseStack.mulPose(Axis.XN.rotationDegrees(55));
                return;
            }
            poseStack.translate(0.055, 0.278, 0.15);
            poseStack.mulPose(Axis.ZN.rotationDegrees(90));
            poseStack.mulPose(Axis.XN.rotationDegrees(100));
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            if (arrowItem.getItem() instanceof FireworkRocketItem) {
                poseStack.translate(0.12, 0.09, -0.11);
                Quaternionf quaternion = Axis.YN.rotationDegrees(-165);
                quaternion.rotateX(90 * (float) (Math.PI / 180.0));
                poseStack.mulPose(quaternion);
                return;
            }
            poseStack.translate(-0.042, 0.09, -0.11);
            Quaternionf quaternion = Axis.YN.rotationDegrees(-119);
            quaternion.rotateX(90 * (float) (Math.PI / 180.0));
            poseStack.mulPose(quaternion);
        }
    }
}
