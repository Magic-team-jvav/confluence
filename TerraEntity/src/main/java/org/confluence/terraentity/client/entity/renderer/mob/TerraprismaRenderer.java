package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.model.TerraprismaModel;
import org.confluence.terraentity.entity.summon.Terraprisma;
import org.confluence.terraentity.integration.iris.IrisHelper;
import org.jetbrains.annotations.NotNull;

public class TerraprismaRenderer extends EntityRenderer<Terraprisma> {

    TerraprismaModel model;
    public TerraprismaRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model= new TerraprismaModel(context.bakeLayer(TerraprismaModel.LAYER_LOCATION));
    }

    @Override
    public void render(Terraprisma entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float pitch;
        float yaw;
        if(entity.getOwner() != null && entity.tickCount > 1){
            pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
            yaw = entityYaw;
        }else{
            return;
        }
        poseStack.pushPose();
        this.preRender(entity, yaw, pitch, partialTick, poseStack, bufferSource, packedLight);
        this.renderModel(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        PoseStack poseStack1 = new PoseStack();
        this.preRender(entity, yaw, pitch, partialTick, poseStack1, bufferSource, packedLight);
        PoseStack.Pose pose = poseStack1.last();

        poseStack.popPose();

        poseStack.pushPose();
        this.renderTrail(entity, poseStack, bufferSource, packedLight, partialTick, pose);
        poseStack.popPose();
    }

    protected void preRender(Terraprisma entity, float yaw,float pitch, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        this.setupPose(entity, yaw, pitch, partialTick, poseStack);
        this.additionPose(entity, yaw, pitch, partialTick, poseStack);
        this.customPose(entity, yaw, pitch, partialTick, poseStack);
    }


    protected void setupPose(Terraprisma entity, float yaw, float pitch, float partialTick, PoseStack poseStack){
        poseStack.mulPose(Axis.YN.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XN.rotationDegrees(-pitch+180));
    }


    protected void additionPose(Terraprisma entity, float yaw, float pitch, float partialTick, PoseStack poseStack){
        float x =  Mth.clamp((entity.backTicks + partialTick) / entity.backTicksMax,0,1);
        x = x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90 * x));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.sequence /2 * ((entity.sequence & 1) == 0? -1 : 1) * 15 * x));

    }

    protected void customPose(Terraprisma entity, float yaw, float pitch, float partialTick, PoseStack poseStack){
        if(entity.anim_x != null) {
            poseStack.mulPose(Axis.XN.rotationDegrees((float) entity.anim_x.cal(entity.tickCount, partialTick)));
        }
        if(entity.anim_y != null) {
            poseStack.mulPose(Axis.YN.rotationDegrees((float) entity.anim_y.cal(entity.tickCount, partialTick)));
        }
        if(entity.anim_z != null) {
            poseStack.mulPose(Axis.ZN.rotationDegrees((float) entity.anim_z.cal(entity.tickCount, partialTick)));
        }
    }

    protected void renderTrail(Terraprisma entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, PoseStack.Pose pose){
        entity.trail.renderTrail(entity, entity.position(), poseStack, bufferSource, packedLight, pose, Vec3.ZERO);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull Terraprisma summonSword) {
        return TerraEntity.space("textures/entity/model/terraprisma_gray.png");
    }

    protected void renderModel(Terraprisma entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
//        RenderSystem.disableDepthTest();
        if(RenderSystem.getShader() == null){
            return;
        }
        if(IrisHelper.isIrisShader()) {

            poseStack.pushPose();
            poseStack.scale(0.9f,0.9f,0.9f);
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY, entity.getRgb() | 0x2F000000);
            poseStack.popPose();
            // 不知道是什么原因，会出现深度始终小于实体
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY, entity.getRgb() | 0xFF000000);
        }else{
            // 原版这个效果好一点
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.energySwirl(getTextureLocation(entity),0,0)), packedLight, OverlayTexture.NO_OVERLAY, entity.getRgb() | 0xFF000000);
        }
//        RenderSystem.enableDepthTest();
    }
}
