package org.confluence.mod.client.renderer.entity.projectile.sword;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class StarFuryProjectileRenderer extends EntityRenderer<SwordProjectile> {
    protected ResourceLocation TEXTURE_LOCATION = Confluence.asResource("textures/entity/star_fury_projectile.png");

    @Override
    public ResourceLocation getTextureLocation(SwordProjectile swordProjectile) {
        return TEXTURE_LOCATION;
    }

    private int frame = 0;

    private float getScale() {return 2;}

    @Override
    protected int getBlockLightLevel(SwordProjectile entity, BlockPos pos) {
        return 10;
    }

    public StarFuryProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(SwordProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(getScale(), getScale(), getScale());
        //poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        poseStack.mulPose(Axis.YP.rotationDegrees(frame++));

//        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(null)));
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(null)));


        vertex(vertexconsumer, matrix4f, pose, packedLight, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, matrix4f, pose, packedLight, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, matrix4f, pose, packedLight, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, matrix4f, pose, packedLight, 0.0F, 1, 0, 0);

        //poseStack.translate(0.5/getScale(),0,0.5/getScale());
        poseStack.mulPose(Axis.YP.rotationDegrees(90F));

        vertex(vertexconsumer, matrix4f, pose, packedLight, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, matrix4f, pose, packedLight, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, matrix4f, pose, packedLight, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, matrix4f, pose, packedLight, 0.0F, 1, 0, 0);

        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90F));

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    protected static void vertex(VertexConsumer pConsumer, Matrix4f pPose, PoseStack.Pose pNormal, int pLightmapUV, float pX, float pY, int pU, int pV) {
        pConsumer.vertex(pPose, pX - 0.5F, pY - 0.5F, 0.0F)
                .color(255, 150, 150, 255)
                .uv((float) pU, (float) pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .uv2(pLightmapUV)
                .setNormal(pNormal, 0.0F, 1.0F, 0.0F);
    }
}
