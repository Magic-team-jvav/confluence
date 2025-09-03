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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.FlailModel;
import org.confluence.mod.common.entity.projectile.FlailBall;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FlailRenderer extends EntityRenderer<FlailBall> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/flail.png");
    protected final EntityModel<FlailBall> model;
    protected final BlockRenderDispatcher dispatcher;

    protected RenderType chainType(){return RenderType.entityCutoutNoCull(TEXTURE);}

    public FlailRenderer(EntityRendererProvider.Context pContext){
        super(pContext);
        this.model = new FlailModel(pContext.bakeLayer(FlailModel.LAYER_LOCATION));
        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    @Override
    public boolean shouldRender(FlailBall entity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
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
    }
    //TODO
    public BlockState getChain(FlailBall entity){
        return Blocks.CHAIN.defaultBlockState();
    }

    // TODO: 每种连枷用不同的贴图
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FlailBall pEntity){
        return TEXTURE;
    }

    //todo 屎山展示
/*
    @Override
    public void render(FlailBall entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight){
        Entity owner = entity.getOwner();
        if(owner == null) return;
        //Vec3 start = new Vec3(offset * Mth.cos(radians), 0, offset * Mth.sin(radians));
        Vec3 end = calculatePos(entity, owner);
        entity.frameCount++;
        Vec3 pt = entity.position().vectorTo(owner.position());
        float roty2 = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(pt.x, pt.z)));
        if(entity.getPhase() == FlailBall.PHASE_SPIN){
            poseStack.pushPose();
            poseStack.translate(end.x,end.y, end.z);
            //poseStack.mulPose(Axis.YP.rotationDegrees(owner.getYRot()));
            poseStack.mulPose(Axis.YN.rotationDegrees(owner.getYRot()));
            //poseStack.mulPose(Axis.XP.rotation(angle+90));
            //var ax = owner.getForward().multiply(1,0,1).add(0,1,0).toVector3f().rotateY(owner.getYRot()+90);
            Matrix4f m4 = new Matrix4f()
                    .translate(owner.position().add(0,1,0).toVector3f())
                    .rotate(Axis.XN.rotationDegrees(angle+90))
                    .translate(owner.position().add(0,1,0).toVector3f().mul(-1,-1,-1));
            poseStack.mulPoseMatrix(m4);
            //poseStack.mulPose(Axis.of(ax).rotationDegrees(angle+90));
            //poseStack.translate(0,-0.5, 0);
            //poseStack.mulPose(Axis.ZP.rotationDegrees((float) (entity.frameCount*0.5)));
            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            poseStack.popPose();
        }else {
            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(roty2 - 90.0F));
            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

            // model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
//            entity.setYRot(roty2);
//            Confluence.LOGGER.info("{}", roty2);
//            renderHook(entity, poseStack, multiBufferSource, packedLight);
            poseStack.popPose();
        }


        // TODO: 链条
        float scale = 0.5f;
        float scaleXZ = 0.2F;
        float uv = 0.25f;

        poseStack.pushPose();
        var consumer = multiBufferSource.getBuffer(chainType());
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        poseStack.scale(scaleXZ,scale,scaleXZ);

        Vec3 dir;
        if(entity.getPhase() == FlailBall.PHASE_SPIN) {
            float yr =  owner.getYRot();
            dir = end.subtract(1.5*Math.cos(yr),1,1.5*Math.sin(yr)) ;
            poseStack.translate(end.x/scale,end.y/scale,end.z/scale);
            poseStack.mulPose(Axis.YN.rotationDegrees(yr));
            //poseStack.translate(-0.25/scale,0,-0.25/scale);
            //var ax = owner.getForward().multiply(1,0,1).add(0,1,0).toVector3f().rotateY(owner.getYRot()+90);
            poseStack.mulPose(Axis.XP.rotation(angle+90));
            //poseStack.mulPose(Axis.of(ax).rotation(angle+90));

        }else{
            dir = owner.position().subtract(entity.position().add(0,1,0));

        }

        //poseStack.translate(0.5, 0, 0);

        double distance = dir.length();
        for (float i = 0; i < distance; i+=scale) {
            poseStack.mulPose(Axis.YN.rotationDegrees(90));
            poseStack.translate(-scaleXZ/2.0, scale, scaleXZ/2.0);
            vertex(consumer, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, uv);
            vertex(consumer, matrix4f, matrix3f, packedLight, 1, 0, uv, uv);
            vertex(consumer, matrix4f, matrix3f, packedLight, 1, 1, uv, 0);
            vertex(consumer, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0);
        }


        poseStack.popPose();

    }
    int factor = 1000;
    double semiMajorAxis = 1.5;
    double semiMinorAxis = 1.5;
    double offsetY = semiMinorAxis / 2.0;
    double xPos;
    double yPos;
    double zPos;
    float angle;
    float radians;
    public Vec3 calculatePos(FlailBall flail, Entity owner){

        angle = (float) (2 * Math.PI * flail.frameCount / factor);
        float ownerYRot = owner.getYRot();
        radians = (float) Math.toRadians(ownerYRot);
        xPos = semiMajorAxis * Mth.cos(angle) * Mth.sin(radians) + 0.25 * Mth.sin(radians);
        yPos = offsetY * Mth.sin(angle) + 1;
        zPos = semiMajorAxis * Mth.cos(angle) * Mth.cos(radians) + 0.25 * Mth.cos(radians);
        return new Vec3(xPos, yPos, -zPos);
    }
*/

    float rotv = 0;
    float rotva = 0.02f;//加速度
    float dirInit;//丢出去时的朝向
    @Override
    public void render(FlailBall entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight) {
        var owner = (Player)entity.getOwner();
        if(owner==null)return;

        poseStack.pushPose();
        rotv += rotv>5?0:rotva;
        if(entity.frameCount==0)  dirInit = owner.getXRot();
        entity.frameCount=entity.frameCount+5;
        float angle = (float) (entity.frameCount*2);
        float offsety = 0.25f;
        //todo 左手
        float r = 0.5f;//右手的半径
        //todo y的坐标还有点问题
        Vec3 pt = entity.position().add(0,offsety,0).vectorTo(owner.position().add(0,0.8,0))//抛出后的方向和修正
            .add(r*Math.cos(Math.toRadians(owner.yBodyRot+180) ),0,r*Math.sin(Math.toRadians(owner.yBodyRot+180)));//修正手的位置

        //
        float rotx = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(-pt.y, Math.sqrt(pt.x * pt.x + pt.z * pt.z))));
        float roty = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(pt.x, pt.z)));


        Matrix4f m4;

        if(entity.getPhase() == FlailBall.PHASE_SPIN){
            m4 = new Matrix4f()
                    .rotate(-(float) Math.toRadians(owner.getYRot()),new Vector3f(0,1,0));
            m4.translate(new Vector3f(0,1,0))
                    .rotate((float) Math.toRadians(angle),new Vector3f(1,0,0))
                    .translate(new Vector3f(0,-1,0));
            poseStack.mulPose(m4);
        }
        else{
            m4 = new Matrix4f().rotate((float) Math.toRadians(roty),new Vector3f(0,1,0));
            m4.translate(0,offsety,0)
                    .rotate((float)( Math.toRadians(rotx)+Math.PI/2),new Vector3f(1,0,0))
                    .translate(0,-offsety,0);
            poseStack.mulPose(m4);
        }

        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFF);
        poseStack.popPose();


        //链
        float scale = 0.25f;
        float scaleXZ = 0.15F;
        float uv = 0.25f;
        poseStack.pushPose();
        var consumer = multiBufferSource.getBuffer(chainType());
        Matrix4f matrix4f = poseStack.last().pose();
        PoseStack.Pose pose = poseStack.last();
        poseStack.scale(scaleXZ,scale,scaleXZ);
        double distance;
        Matrix4f m5;

        if(entity.getPhase() == FlailBall.PHASE_SPIN) {
            distance = scale;
            m5 = new Matrix4f().rotate(-(float) Math.toRadians(owner.getYRot()),new Vector3f(0,1,0));//y轴旋转
            m5.translate(new Vector3f(0,1/scale,0))
                    .rotate((float) Math.toRadians(angle),new Vector3f(1,0,0))//绕从player的x轴向上平移1单位的轴旋转
                    .translate(new Vector3f(0,-1/scale,0));
        }else{

            distance = pt.length();

            m5 = new Matrix4f().translate(0f, offsety/scale,0f)//先向上平移到链球中心中间位置
                    .rotate((float) Math.toRadians(roty),new Vector3f(0,1,0));//绕y轴旋转
            //todo 角度可能有问题  M注: 雀氏有问题，链锤不松手上下飞就能发现玩家终点跑天上去了
            m5.rotate((float)( Math.toRadians(rotx)+Math.PI/2),new Vector3f(1,0,0)).translate(0f, -offsety/scale,0f);//绕中心的x轴旋转

        }
        poseStack.mulPose(m5);
        float preDistance = 0.3f;
        for (float i = 0f; i < distance/scaleXZ+0.5; i+=scale) {
            if(i>preDistance) {
                vertex(consumer, matrix4f, pose, packedLight, 0.0F, 0, 0, uv);
                vertex(consumer, matrix4f, pose, packedLight, 1, 0, uv, uv);
                vertex(consumer, matrix4f, pose, packedLight, 1, 1, uv, 0);
                vertex(consumer, matrix4f, pose, packedLight, 0.0F, 1, 0, 0);
            }
            poseStack.mulPose(Axis.YN.rotationDegrees(30));//每步旋转角
            poseStack.translate(-scaleXZ/2.0, scale, scaleXZ/2.0);//回正到中心
        }
        poseStack.popPose();
    }

    protected static void vertex(VertexConsumer pConsumer, Matrix4f pPose, PoseStack.Pose pNormal, int pLightmapUV, float pX, float pY, float pU, float pV) {
        pConsumer.addVertex(pPose, pX - 0.5F, (float)pY - 0.25F, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv(pU, pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(pLightmapUV)
                .setNormal(pNormal,0.0F, 1.0F, 0.0F);
    }

}