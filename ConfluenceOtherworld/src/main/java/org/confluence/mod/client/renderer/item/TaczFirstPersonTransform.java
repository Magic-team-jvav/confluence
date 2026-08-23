package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.ArrayList;
import java.util.List;

/// 负责反向应用枪械模型中的 TACZ 风格定位骨骼。
///
/// 1.21 侧部分枪械不再完全依赖物品 JSON 的 display，而是把第一人称、GUI、展示框和地面
/// 等位置写进 geo 的定位骨骼中。GeckoLib 会先把这些骨骼烘焙进模型坐标，渲染时需要把定位骨骼
/// 自身的位移和旋转抵消掉，真正可见的枪械模型才会落在 1.21 设计的位置上。
final class TaczFirstPersonTransform {
    private static final float MODEL_UNIT = 1.0F / 16.0F;

    private TaczFirstPersonTransform() {}

    static void applyIdleViewInverse(PoseStack poseStack, GeoBone idleView) {
        applyPositioningInverse(poseStack, idleView);
    }

    /// 按 GeckoLib 的烘焙坐标系移除某条定位骨骼路径上的平移与旋转。
    static void applyPositioningInverse(PoseStack poseStack, GeoBone locator) {
        if (locator == null) {
            return;
        }

        List<GeoBone> path = new ArrayList<>();
        for (GeoBone bone = locator; bone != null; bone = bone.getParent()) {
            path.add(bone);
        }

        for (GeoBone bone : path) {
            poseStack.mulPose(Axis.XP.rotation(-bone.getRotX()));
            poseStack.mulPose(Axis.YP.rotation(-bone.getRotY()));
            poseStack.mulPose(Axis.ZN.rotation(bone.getRotZ()));

            GeoBone parent = bone.getParent();
            float partX = bone.getPivotX();
            float partY = bone.getPivotY();
            float partZ = bone.getPivotZ();
            if (parent != null) {
                partX -= parent.getPivotX();
                partY -= parent.getPivotY();
                partZ -= parent.getPivotZ();
            }

            poseStack.translate(-partX * MODEL_UNIT, -partY * MODEL_UNIT, -partZ * MODEL_UNIT);
        }
    }
}
