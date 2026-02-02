package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.FlailModel;
import org.confluence.mod.common.entity.projectile.FlailBall;
import org.joml.Matrix4f;
import org.joml.Vector3f;

// 搬运自1.20.1, 编写 by Viola & EDGtheXu, 搬运&修复 by MakerTechno.
public class FlailRenderer extends EntityRenderer<FlailBall> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/flail.png");
    protected final EntityModel<FlailBall> model;
    protected final BlockRenderDispatcher dispatcher;

    protected RenderType chainType() {return RenderType.entityCutoutNoCull(TEXTURE);}

    public FlailRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new FlailModel(pContext.bakeLayer(FlailModel.LAYER_LOCATION));
        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public boolean shouldRender(FlailBall entity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        /* 实测发现存在渲染不到的情况，暂时仅通过是否有持有者判断渲染。TODO: 测试结束请再次认证有效性并还原代码
        if (super.shouldRender(entity, pCamera, pCamX, pCamY, pCamZ)) {
            return true;
        } else {
            Entity owner = entity.getOwner();
            if (owner != null) {
                Vec3 vec3 = owner.position().add(0.0, owner.getBbHeight() * 0.65, 0.0);
                Vec3 vec31 = entity.position().add(0.0, 0.25, 0.0);
                return pCamera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
            }
        }
        return false;
        */

        return entity.getOwner() != null;
    }

    // TODO: 原作者可能想通过这种方式获取一些贴图参数
    public BlockState getChain(FlailBall entity) {
        return Blocks.CHAIN.defaultBlockState();
    }

    // TODO: 每种连枷用不同的贴图
    @Override
    public ResourceLocation getTextureLocation(FlailBall pEntity) {
        return TEXTURE;
    }

    // 屎山展示，但是视觉上不舒服我删了。想看的去1.20翻源码去

    // 这几个变量干什么的？没用到啊？
    /*float rotv = 0; // 线速度
    float rotva = 0.02f;//加速度
    float dirInit;//丢出去时的朝向*/
    @Override
    public void render(FlailBall entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        var owner = (Player) entity.getOwner();
        if (owner == null) return;

        poseStack.pushPose();
        /*rotv += rotv>5?0:rotva;*/
        // if(entity.frameCount==0)  dirInit = owner.getXRot();
        entity.frameCount = entity.frameCount + 5;
        float angle = (float) (entity.frameCount * 2);
        float yOffset = 0.25f;
        float r = -entity.getEntityData().get(FlailBall.DATA_OFFSET);
        //todo y的坐标还有点问题
        Vec3 ownerRotateCenterPos = entity.position().add(0, yOffset, 0).vectorTo(owner.position().add(0, 0.8, 0))//抛出后的方向和修正
                .add(r * Math.cos(Math.toRadians(owner.yBodyRot + 180)), 0, r * Math.sin(Math.toRadians(owner.yBodyRot + 180)));//修正手的位置

        float xRot = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(-ownerRotateCenterPos.y, Math.sqrt(ownerRotateCenterPos.x * ownerRotateCenterPos.x + ownerRotateCenterPos.z * ownerRotateCenterPos.z))));
        float yRot = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(ownerRotateCenterPos.x, ownerRotateCenterPos.z)));


        Matrix4f ballMatrix;

        if (entity.getPhase() == FlailBall.PHASE_SPIN) {
            ballMatrix = new Matrix4f()
                    .rotate(-(float) Math.toRadians(owner.getYRot()), new Vector3f(0, 1, 0));
            ballMatrix.translate(new Vector3f(0, 1, 0))
                    .rotate((float) Math.toRadians(angle), new Vector3f(1, 0, 0))
                    .translate(new Vector3f(0, -1, 0));
            poseStack.mulPose(ballMatrix);
        } else {
            ballMatrix = new Matrix4f().rotate((float) Math.toRadians(yRot), new Vector3f(0, 1, 0));
            ballMatrix.translate(0, yOffset, 0)
                    .rotate(xRot * Mth.DEG_TO_RAD + Mth.HALF_PI, new Vector3f(1, 0, 0))
                    .translate(0, -yOffset, 0);
            poseStack.mulPose(ballMatrix);
        }

        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFF);
        poseStack.popPose();


        //链
        float scale = 0.25f;
        float scaleXZ = 0.15F;
        float uv = 0.25f;
        poseStack.pushPose();
        var consumer = multiBufferSource.getBuffer(chainType());
        Matrix4f basePoseMatrix = poseStack.last().pose();
        PoseStack.Pose pose = poseStack.last();
        poseStack.scale(scaleXZ, scale, scaleXZ);
        double distance;
        Matrix4f chainMatrix;

        if (entity.getPhase() == FlailBall.PHASE_SPIN) {
            distance = scale;
            chainMatrix = new Matrix4f().rotate(-(float) Math.toRadians(owner.getYRot()), new Vector3f(0, 1, 0));//y轴旋转
            chainMatrix.translate(new Vector3f(0, 1 / scale, 0))
                    .rotate(angle * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0))//绕从player的x轴向上平移1单位的轴旋转
                    .translate(new Vector3f(0, -1 / scale, 0));
        } else {

            distance = ownerRotateCenterPos.length();

            chainMatrix = new Matrix4f().translate(0f, yOffset / scale, 0f)//先向上平移到链球中心中间位置
                    .rotate(yRot * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0));//绕y轴旋转
            //todo 角度可能有问题  M注: 雀氏有问题，链锤不松手上下飞就能发现玩家终点跑天上去了
            chainMatrix.rotate(xRot * Mth.DEG_TO_RAD + Mth.HALF_PI, new Vector3f(1, 0, 0)).translate(0f, -yOffset / scale, 0f);//绕中心的x轴旋转

        }
        poseStack.mulPose(chainMatrix);
        float preDistance = 0.3f;
        for (float i = 0f; i < distance / scaleXZ + 0.5; i += scale) {
            if (i > preDistance) {
                vertex(consumer, basePoseMatrix, pose, packedLight, 0.0F, 0, 0, uv);
                vertex(consumer, basePoseMatrix, pose, packedLight, 1, 0, uv, uv);
                vertex(consumer, basePoseMatrix, pose, packedLight, 1, 1, uv, 0);
                vertex(consumer, basePoseMatrix, pose, packedLight, 0.0F, 1, 0, 0);
            }
            poseStack.mulPose(Axis.YN.rotationDegrees(30));//每步旋转角
            poseStack.translate(-scaleXZ / 2.0, scale, scaleXZ / 2.0);//回正到中心
        } // M: 你这么渲染让我想起了我小时候把纸带卷成卷再展开后形成的类似弹簧的结构
        poseStack.popPose();
    }

    protected static void vertex(VertexConsumer pConsumer, Matrix4f pPose, PoseStack.Pose pNormal, int pLightmapUV, float pX, float pY, float pU, float pV) {
        pConsumer.addVertex(pPose, pX - 0.5F, (float) pY - 0.25F, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv(pU, pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(pLightmapUV)
                .setNormal(pNormal, 0.0F, 1.0F, 0.0F);
    }
}
