package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.confluence.terraentity.entity.summon.SummonSword;
import org.jetbrains.annotations.NotNull;

/**
 * 模型为贴图的召唤剑渲染器（泰拉棱镜）
 */
public class SummonSwordRenderer<T extends SummonSword> extends EntityRenderer<T> {

    public SummonSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float pitch;
        float yaw;
        if(entity.getOwner() != null && entity.tickCount > 1){
            pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
            yaw = entityYaw;
        }else{
            return;
        }

        this.renderTrail(entity, poseStack, bufferSource, packedLight, partialTick);

        poseStack.pushPose();
        this.preRender(entity, yaw, pitch, partialTick, poseStack, bufferSource, packedLight);
        this.renderModel(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull SummonSword summonSword) {
        return null;
    }

    protected void renderTrail(T entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick){
        entity.trail.renderTrail(entity, entity.position(), poseStack, bufferSource, packedLight);

    }

    /**
     * 预先调整角度，使得刀刃面向眼前
     */
    protected void setupPose(T entity, float yaw, float pitch, float partialTick, PoseStack poseStack){
        // 旋转到正前方yaw
        poseStack.mulPose(Axis.YN.rotationDegrees(yaw - 90));

        // 旋转到正前方pitch
        poseStack.mulPose(Axis.ZN.rotationDegrees(-pitch));
    }

    /**
     * 对某些状态转换的额外插值
     */
    protected void additionPose(T entity, float yaw, float pitch, float partialTick, PoseStack poseStack){
        // 如果是返回状态，需要旋转z轴来使刀刃面向侧方向
        float x =  Mth.clamp((entity.backTicks + partialTick) / entity.backTicksMax,0,1);
        x = x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);

        poseStack.mulPose(Axis.XP.rotationDegrees(90 * x));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.sequence /2 * ((entity.sequence & 1) == 0? -1 : 1) * 15 * x));


    }

    /**
     * 技能动作
     */
    protected void customPose(T entity, float yaw, float pitch, float partialTick, PoseStack poseStack){
//        poseStack.mulPose(Axis.ZN.rotationDegrees(entity.getRotateZTimer(partialTick) * 30));
        if(entity.anim_x != null) {
            poseStack.mulPose(Axis.ZN.rotationDegrees((float) entity.anim_x.cal(entity.tickCount, partialTick)));
        }
        poseStack.mulPose(Axis.ZN.rotationDegrees(-45)); // 贴图需要旋转45度
    }

    protected void preRender(T entity, float yaw,float pitch, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        this.setupPose(entity, yaw, pitch, partialTick, poseStack);
        this.additionPose(entity, yaw, pitch, partialTick, poseStack);
        this.customPose(entity, yaw, pitch, partialTick, poseStack);
    }

    protected void renderModel(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        Minecraft.getInstance().getItemRenderer().renderStatic(entity.modelItem.getDefaultInstance(), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), 0);

    }

}
