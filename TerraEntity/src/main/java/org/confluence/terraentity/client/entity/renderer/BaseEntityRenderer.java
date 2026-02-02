package org.confluence.terraentity.client.entity.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;

/**
 * 适用于实体和调用变体模型的渲染器基类
 * @param <T> 当前实体类
 * @param <S> 模型的实体类
 * @param <M> 模型类
 */
public abstract class BaseEntityRenderer<T extends Entity, S extends Entity, M extends EntityModel<S>> extends EntityRenderer<T> {
    protected final M model;
    protected float size;
    protected float offsetY;
    public BaseEntityRenderer(EntityRendererProvider.Context context, M model, float size, float offsetY) {
        super(context);
        this.model = model;
        this.size = size;
        this.offsetY = offsetY;
    }

    public BaseEntityRenderer(EntityRendererProvider.Context context, M model) {
        super(context);
        this.model = model;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.pushPose();

        preRender(entity, entityYaw, partialTick, poseStack, packedLight);

        if(this.model!= null) {
            VertexConsumer buffer1 = buffer.getBuffer(getRenderType(entity, partialTick));
            this.model.renderToBuffer(poseStack, buffer1, packedLight, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
    }

    public void preRender(T entity, float entityYaw, float partialTick, PoseStack poseStack, int packedLight){
        poseStack.translate(0,offsetY,0);
        poseStack.scale(size,size,size);
    }

    public RenderType getRenderType(T entity, float partialTicks) {
        return this.model.renderType(this.getTextureLocation(entity));
    }

}
