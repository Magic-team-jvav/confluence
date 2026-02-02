package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.skeletronprime.SkeletronPrimePart;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;

public class SkeletronPrimePartRenderer extends GeoNormalRenderer<SkeletronPrimePart> {


    public SkeletronPrimePartRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path, true, 1, 0.5F);
    }


    @Override
    public void render(SkeletronPrimePart entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.getGeoModel().getBone("bone3").ifPresent(b->b.setHidden(false));

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        this.offsetY = 0.5F;
    }

    // 在类中添加辅助方法
    private Vector3f getBoneWorldPosition(SkeletronPrimePart entity, GeoBone bone, Matrix4f modelMatrix) {
        // 获取骨骼在模型空间的位置
        Vector4f bonePos = new Vector4f(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ(), 1.0f);

        // 变换到世界空间（简化处理）
        bonePos.mul(modelMatrix);

        return new Vector3f(bonePos.x, bonePos.y, bonePos.z);
    }
    @Override
    public void renderRecursively(PoseStack poseStack, SkeletronPrimePart animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {

        if(!bone.getName().equals("bone2") && !bone.getName().equals("bone7")) {

            switch (bone.getName()) {
                case "prime_laser" ->{
                    if (animatable.getPartType() == 0) {
                        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
                        return;
                    }
                    return;
                }
                case "prime_saw" -> {
                    if (animatable.getPartType() == 1) {
                        poseStack.pushPose();
                        poseStack.translate(0, 0, 2);
                        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
                        poseStack.popPose();
                        return;
                    }
                    return;
                }
                case "prime_vice" -> {
                    if (animatable.getPartType() == 2) {
                        poseStack.pushPose();
                        poseStack.translate(0, 0, 4);
                        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
                        poseStack.popPose();
                        return;
                    }
                    return;
                }
                case "prime_cannon" -> {
                    if (animatable.getPartType() == 3) {
                        poseStack.pushPose();
                        poseStack.translate(0, 0, 6);
                        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
                        poseStack.popPose();
                        return;
                    }
                    return;
                }
            }

            if(animatable.owner != null) {
                // 小臂做手的背向和到达头部方向的1/2插值
                if(bone.getName().equals("ag2")) {
                    // 先恢复到旋转前的状态
                    poseStack.mulPose(new Quaternionf().setFromNormalized(poseStack.last().pose()).conjugate());
                    Vec3 halfDir = this.calHalfDir(animatable, partialTick);

                    poseStack.mulPose(new Quaternionf().rotateTo(new Vector3f(1,0,0), halfDir.toVector3f()));

                }
                // 让大臂始终指向头部
                if(bone.getName().equals("ag1")) {
                    // 先恢复到旋转前的状态
                    poseStack.mulPose(new Quaternionf().setFromNormalized(poseStack.last().pose()).conjugate());
                    Vec3 halfDir = this.calHalfDir(animatable, partialTick);

                    // 手部长度
                    float armLen = 4.8f;
                    poseStack.pushPose();

                    // 移动到肘关节
                    Vec3 relatePos = halfDir.scale(armLen);
                    poseStack.translate(relatePos.x, relatePos.y, relatePos.z);

                    // 肘关节位置
                    Vec3 ag2Pos = animatable.getPosition(partialTick).add(relatePos);

                    // 肘关节到头部方向
                    Vec3 toOwnerDir = animatable.owner.getPosition(partialTick).subtract(ag2Pos).normalize();

                    // 绕肘关节旋转到头部位置
                    poseStack.mulPose(new Quaternionf().rotateTo(new Vector3f(1,0,0), toOwnerDir.toVector3f()));

                    // 手臂模型偏移
                    poseStack.translate(-armLen, 0, 0);

                    super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
                    poseStack.popPose();
                    return;
                }
            }

            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        }
    }

    /**
     * 计算半程方向
     */
    private @NotNull Vec3 calHalfDir(SkeletronPrimePart animatable, float partialTick) {
        float lerpRotX = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
        float lerpRotY = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot());
        Vec3 currDir = Vec3.directionFromRotation(new Vec2(lerpRotX, lerpRotY)).scale(-1);
        Vec3 toOwnerDir = animatable.owner.getPosition(partialTick).subtract(animatable.getPosition(partialTick));
        return currDir.normalize().add(toOwnerDir.normalize()).normalize();

    }

}
