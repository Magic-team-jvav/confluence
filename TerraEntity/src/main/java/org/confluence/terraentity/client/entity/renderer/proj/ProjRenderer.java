package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.proj.BaseProj;


public class ProjRenderer<T extends BaseProj> extends EntityRenderer<T> {
    protected final EntityModel<T> bulletModel;
    float size;
    float offsetY;
    public ProjRenderer(EntityRendererProvider.Context pContext, EntityModel<T> pModel, float size,float offsetY) {
        super(pContext);
        bulletModel = pModel;
        this.size = size;
        this.offsetY = offsetY;
    }

    public ProjRenderer(EntityRendererProvider.Context pContext, EntityModel<T> pModel) {
        this(pContext, pModel, 1.0f, 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.getTexture();
    }

    @Override
    public void render(T entity, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if(entity.getTexture()==null) return;

        super.render(entity, pEntityYaw, pPartialTick, poseStack, pBuffer, pPackedLight);

        this.defaultRender(entity, pEntityYaw, pPartialTick, poseStack, pBuffer, pPackedLight);
    }

    protected void defaultRender(T entity, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight){
        this.actualRender(entity, pEntityYaw, pPartialTick, poseStack, pBuffer, pPackedLight, this.getRenderType(entity));

    }

    protected RenderType getRenderType(T entity){
        return this.bulletModel.renderType(this.getTextureLocation(entity));
    }

    protected void actualRender(T entity, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, RenderType renderType){
        poseStack.pushPose();
        poseStack.scale(size,size,size);

        poseStack.translate(0,offsetY,0);

        this.adjustPosePost(poseStack, entity, pPartialTick);
//        if(rotateZ) {
//            float pitch = -(float) Math.atan2(v.y, Math.sqrt(v.x * v.x + v.z * v.z));
//            pPoseStack.mulPose(Axis.XN.rotation(pitch));
//            pPoseStack.mulPose(Axis.ZN.rotation((pEntity.tickCount + pPartialTick) * rotateZSpeed));
//        }

        this.renderModel(pBuffer, entity, poseStack, pPackedLight, renderType);

        poseStack.popPose();
    }

    protected void renderModel(MultiBufferSource pBuffer, T entity, PoseStack poseStack, int pPackedLight, RenderType renderType){
        VertexConsumer buffer = pBuffer.getBuffer(renderType);
        this.bulletModel.renderToBuffer(poseStack,buffer,pPackedLight, OverlayTexture.NO_OVERLAY);
    }


    protected void adjustPosePost(PoseStack poseStack, T entity, float partialTick){
        Vec3 v = entity.getDeltaMovement();

        float yaw = (float) Math.atan2(v.z, v.x);
        // 旋转到正前方yaw
        poseStack.mulPose(Axis.YN.rotation((float) (yaw - Math.PI/2)));
        float pitch = (float) Math.atan2(v.y, Math.sqrt(v.x*v.x + v.z*v.z));

        // 旋转到正前方pitch
        poseStack.mulPose(Axis.ZN.rotation( pitch));
    }

}
