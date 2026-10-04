package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.animal.Snail;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 让蜗牛模型的腹面始终贴合当前攀爬表面。
 */
public final class SnailRenderer extends CritterRenderer<Snail> {
    public SnailRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.2F;
    }

    /// geckolib 4.8.4 的 5 参 `applyRotations` 已 `@Deprecated(forRemoval)`，基类调用的是 6 参版，
    /// 因此覆写 6 参并把 `nativeScale` 透传下去。
    @Override
    protected void applyRotations(Snail snail, PoseStack poseStack, float age, float yaw, float partialTick, float nativeScale) {
        Direction attachment = snail.getAttachmentFace();
        Direction crawl = snail.getCrawlDirection();
        if (attachment.getAxis().isHorizontal()) {
            // 碰撞箱的水平半径大于模型翻转后的厚度；只移动显示位置，不改变实体碰撞。
            float gap = Math.max(0.0F, (snail.getBbWidth() - snail.getBbHeight()) * 0.5F);
            poseStack.translate(-attachment.getStepX() * gap, 0.0F, -attachment.getStepZ() * gap);
        }
        float pivotY = snail.getBbHeight() * 0.5F;
        poseStack.translate(0.0F, pivotY, 0.0F);
        // 附着面旋转必须先于基类偏航进入矩阵；这样实体偏航是在墙面切平面内旋转，
        // 而不是先在地面旋转后再把局部前进轴翻到错误方向。
        Quaternionf surfaceRotation = switch (attachment) {
            case DOWN -> Axis.ZP.rotationDegrees(180.0F);
            case NORTH -> Axis.XN.rotationDegrees(90.0F);
            case SOUTH -> Axis.XP.rotationDegrees(90.0F);
            case WEST -> Axis.ZP.rotationDegrees(90.0F);
            case EAST -> Axis.ZN.rotationDegrees(90.0F);
            default -> new Quaternionf();
        };
        poseStack.mulPose(surfaceRotation);
        poseStack.translate(0.0F, -pivotY, 0.0F);
        // 实体本身保存的是世界空间视线（包括沿墙向上/向下的俯仰），供 AI、攻击和调试线使用；
        // 渲染偏航则必须换算到附着面的局部切平面，不能直接复用世界偏航。
        Vector3f localForward = surfaceRotation.conjugate(new Quaternionf()).transform(
                new Vector3f(crawl.getStepX(), crawl.getStepY(), crawl.getStepZ()));
        float localYaw = (float) (Mth.atan2(-localForward.x, localForward.z) * Mth.RAD_TO_DEG);
        super.applyRotations(snail, poseStack, age, localYaw, partialTick, nativeScale);
    }
}
