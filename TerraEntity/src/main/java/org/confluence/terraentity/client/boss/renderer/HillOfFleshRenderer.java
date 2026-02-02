package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.ModRenderTypes;
import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.client.event.RenderEvent;
import org.confluence.terraentity.entity.boss.hillofflesh.HillOfFlesh;
import org.confluence.terraentity.entity.boss.hillofflesh.HillOfFleshPart;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.mixed.IShaderInstance;
import org.confluence.terraentity.mixin.accessor.GeoRendererAccessor;
import org.confluence.terraentity.utils.Easing;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.opengl.GL11C;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtil;

import java.lang.Math;

public class HillOfFleshRenderer extends GeoNormalRenderer<HillOfFlesh> {


//    static ModelPositionTable.Builder tableBuilder = new ModelPositionTable.Builder(); // for client loading

    public HillOfFleshRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeoBossModel<>(TEBossEntities.HILL_OF_FLESH), false, 1, 0);

    }

    @Override
    public void defaultRender(PoseStack poseStack, HillOfFlesh animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer,
                               float yaw, float partialTick, int packedLight) {
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);

        if(!RenderEvent.isAfterSky){
            return;
        }

        float radius = 10;
        int count = 50;
        double delta = Math.PI * 2 / count;
        float height = animatable.height;
        double angle = 0;

        float progress = animatable.getSpawnProgress(partialTick);
        if(progress < 0.5f){

            height = Mth.lerp(progress * 2, 1, 20);
        }else{
            radius = animatable.getOutRadius();
            height = Mth.lerp(progress * 2 - 1, 20, height);
        }

        if(RenderEvent.isIrisShader){
            renderPillarWithIris(poseStack, animatable, partialTick, radius, angle, delta, count, height);
        }else{
            renderPillar(poseStack, animatable, partialTick, radius, angle, delta, count, height);
        }
    }

    @Override
    protected void adjustPose(PoseStack poseStack, HillOfFlesh animatable, BakedGeoModel model, float partialTick){
        float progress = animatable.getSpawnProgress(partialTick);
        if(progress < 0.5f){
            float x = progress * 2;
            poseStack.translate(0, Easing.EASE_IN_OUT_QUAD.easeToRange(x, -15, 0), 0);
            poseStack.mulPose(Axis.YP.rotation((float) Mth.lerp( x, 0, Math.PI * 4)));
        }else{
//            float scale = animatable.getExpandingScale(partialTick);
//            poseStack.scale(scale, scale, scale);
        }

    }

    private static void renderPillar(PoseStack poseStack, HillOfFlesh animatable, float partialTick, float radius, double angle, double delta, int count, float height) {
//        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShader(ModRenderTypes::getFloatFireShader);
        int ii = 0xFFFFFF;
        float f = (float)(ii >> 16 & 0xFF) / 255.0F;
        float f1 = (float)(ii >> 8 & 0xFF) / 255.0F;
        float f2 = (float)(ii & 0xFF) / 255.0F;
        RenderSystem.setShaderColor(f, f1, f2, 1);
//        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TerraEntity.space("textures/gui/noise.png")));
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);


        RenderSystem.setShaderTexture(0, TerraEntity.space("textures/gui/noise.png"));
        Vec3 pos = new Vec3(radius * Math.cos(angle), -10, radius * Math.sin(angle));
        angle += delta;

        double lastX = pos.x;
        double lastZ = pos.z;
        float lastU = 0;
//        float dv = (animatable.tickCount + partialTick) * -0.02f;
//        float du = (animatable.tickCount + partialTick) * -0.015f;
        float dv = 0;
        float du = 0;
        float switchProgress = animatable.getSwitchProgress(partialTick);
        int red = (int) Mth.lerp(switchProgress, 230,130);
        int green = (int) Mth.lerp(switchProgress, 50,100);
        int blue = 50;
        for(int i = 1; i<= count; i++){
            pos = new Vec3(radius * Math.cos(angle), pos.y, radius * Math.sin(angle));
            float u = i * 1.0f / count;

            builder.addVertex(poseStack.last().pose(), (float) pos.x, (float) pos.y, (float) pos.z).setColor(red, green, blue,150).setUv(u + du,0 + dv);
            builder.addVertex(poseStack.last().pose(), (float) pos.x,  (float) pos.y+ height, (float) pos.z).setColor(red, green, blue,0).setUv(u+ du,1+ dv);
            builder.addVertex(poseStack.last().pose(), (float) lastX,  (float) pos.y+ height, (float) lastZ).setColor(red, green, blue,0).setUv(lastU+ du,1+ dv);
            builder.addVertex(poseStack.last().pose(), (float) lastX, (float) pos.y, (float) lastZ).setColor(red, green, blue,150).setUv(lastU+ du,0+ dv);

            angle += delta;
            lastX = pos.x;
            lastZ = pos.z;
            lastU = u;
        }

        MeshData meshdata = builder.build();
        if (meshdata != null) {
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.depthFunc(GL11C.GL_LESS);

            float time = -(animatable.tickCount + partialTick) * 0.01f;
            ((IShaderInstance)ModRenderTypes.Shaders.floatFireShader).getTerra_entity$Time().set(time);

            BufferUploader.drawWithShader(meshdata);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
        }
    }

    private static void renderPillarWithIris(PoseStack poseStack, HillOfFlesh animatable, float partialTick, float radius, double angle, double delta, int count, float height) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
//        RenderSystem.setShader(ModRenderTypes::getFloatFireShader);
        int ii = 0xFFFFFF;
        float f = (float)(ii >> 16 & 0xFF) / 255.0F;
        float f1 = (float)(ii >> 8 & 0xFF) / 255.0F;
        float f2 = (float)(ii & 0xFF) / 255.0F;
        RenderSystem.setShaderColor(f, f1, f2, 1);
//        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TerraEntity.space("textures/gui/noise.png")));
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);


        RenderSystem.setShaderTexture(0, TerraEntity.space("textures/gui/noise.png"));
        Vec3 pos = new Vec3(radius * Math.cos(angle), -10, radius * Math.sin(angle));
        angle += delta;

        double lastX = pos.x;
        double lastZ = pos.z;
        float lastU = 0;
        float dv = (animatable.tickCount + partialTick) * -0.005f;
        float du = (animatable.tickCount + partialTick) * -0.002f;
//        float dv = 0;
//        float du = 0;
        float switchProgress = animatable.getSwitchProgress(partialTick);
        int red = (int) Mth.lerp(switchProgress, 230,130);
        int green = (int) Mth.lerp(switchProgress, 50,100);
        int blue = 50;
        for(int i = 1; i<= count; i++){
            pos = new Vec3(radius * Math.cos(angle), pos.y, radius * Math.sin(angle));
            float u = i * 1.0f / count;

            builder.addVertex(poseStack.last().pose(), (float) pos.x, (float) pos.y, (float) pos.z).setColor(red, green, blue,150).setUv(u + du,0 + dv);
            builder.addVertex(poseStack.last().pose(), (float) pos.x,  (float) pos.y+ height, (float) pos.z).setColor(red, green, blue,0).setUv(u+ du,1+ dv);
            builder.addVertex(poseStack.last().pose(), (float) lastX,  (float) pos.y+ height, (float) lastZ).setColor(red, green, blue,0).setUv(lastU+ du,1+ dv);
            builder.addVertex(poseStack.last().pose(), (float) lastX, (float) pos.y, (float) lastZ).setColor(red, green, blue,150).setUv(lastU+ du,0+ dv);

            angle += delta;
            lastX = pos.x;
            lastZ = pos.z;
            lastU = u;
        }

        MeshData meshdata = builder.build();
        if (meshdata != null) {
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.depthFunc(GL11C.GL_LESS);

            BufferUploader.drawWithShader(meshdata);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
        }
    }


    @SuppressWarnings("all")
    @Override
    public void renderRecursively(PoseStack poseStack, HillOfFlesh animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {
        HillOfFleshPart part = animatable.getPart(bone.getName());

        // Loading in Client
//        if(part != null && RenderEvent.isAfterSky) {
//            if (animatable.tickCount >= 40 && animatable.tickCount % 5 == 0) {
//                var v = bone.getWorldPosition().sub(animatable.getX(), animatable.getY(), animatable.getZ());
//                tableBuilder.replacePosition(bone.getName(), animatable.tickCount % 40, new Vec3(v.x, v.y, v.z));
//            }
////            var data = ModelPositionTable.CODEC.encodeStart(JsonOps.INSTANCE,animatable.tableBuilder.build() ).result().get();
//        }

        if(part != null && RenderEvent.isAfterSky && part.name.startsWith("E") && !animatable.isSpawning()){
            poseStack.pushPose();
            RenderUtil.translateMatrixToBone(poseStack, bone);
            RenderUtil.translateToPivotPoint(poseStack, bone);
//            RenderUtil.rotateMatrixAroundBone(poseStack, bone);
            RenderUtil.scaleMatrixForBone(poseStack, bone);

            float scale = (float) Math.sqrt(1 / animatable.currentScale); // 奇怪的缩放
            poseStack.scale(scale,scale,scale);
            adjustParts(poseStack, animatable, bone, partialTick, part);

            if (bone.isTrackingMatrices()) {
                Matrix4f poseState = new Matrix4f(poseStack.last().pose());
                Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

                bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
                bone.setLocalSpaceMatrix(RenderUtil.translateMatrix(localMatrix, getRenderOffset(this.animatable, 1).toVector3f()));
                bone.setWorldSpaceMatrix(RenderUtil.translateMatrix(new Matrix4f(localMatrix), this.animatable.position().toVector3f()));
            }

            RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
            buffer = ((GeoRendererAccessor)this).callCheckAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);
            renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);

            if (!isReRender)
                applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);


            renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
            poseStack.popPose();
            return;
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    private void adjustParts(PoseStack poseStack, HillOfFlesh animatable, GeoBone bone, float partialTick, HillOfFleshPart part) {
        float[] yawPitch = this.extractYawPitch(poseStack.last().pose());
        poseStack.mulPose(new Quaternionf().setFromNormalized(poseStack.last().pose()).conjugate());
        if(animatable.deathTime > 0){
            poseStack.translate(0, part.getDeathOffsetY(partialTick),0);
        }

        LivingEntity target = part.target;
        if(target != null && animatable.deathTime <= 0){

            Vector3d bonePos = bone.getWorldPosition();
            Vec3 dist = new Vec3(target.xo, target.yo, target.zo)
                    .lerp(target.position(), partialTick)
                    .subtract(bonePos.x, bonePos.y, bonePos.z);

            float yaw = (float) (Math.atan2(dist.z, dist.x));
            float pitch = (float) (-Math.atan2(dist.y,
                    Math.sqrt(dist.x * dist.x + dist.z * dist.z)));
            part.stareYaw = (float) (Math.PI/2 - yaw);
            part.starePitch = pitch;

            poseStack.mulPose(Axis.YP.rotation(part.lerpYaw(partialTick)));
            poseStack.mulPose(Axis.XP.rotation(part.lerpPitch(partialTick)));

        }else{

            part.stareStartYaw = yawPitch[0];
            part.stareStartPitch = yawPitch[1];

            poseStack.mulPose(Axis.YP.rotation(part.lerpYaw(-partialTick)));
            poseStack.mulPose(Axis.XP.rotation(part.lerpPitch(-partialTick)));

        }
    }

    /**
     * 从旋转矩阵提取 Yaw 和 Pitch
     *
     * @param matrix 旋转矩阵（左上3x3为旋转部分）
     * @return [yaw, pitch] 弧度值
     */
    protected float[] extractYawPitch(Matrix4f matrix) {
        // 获取旋转矩阵的3x3部分
        Matrix3f rotation = new Matrix3f();
        matrix.get3x3(rotation);

        // 提取前向向量（Z轴）
        Vector3f forward = new Vector3f(
                rotation.m02,
                rotation.m12,
                rotation.m22
        ).normalize();

        // 计算俯仰角 (Pitch) - 绕X轴的旋转
        float pitch = (float) Math.asin(forward.y);

        // 计算偏航角 (Yaw) - 绕Y轴的旋转
        float yaw = (float) Math.atan2(-forward.x, forward.z);

        return new float[]{yaw, pitch};
    }



    @Override
    protected float getDeathMaxRotation(HillOfFlesh animatable) {
        return 0f;
    }

    @Override
    public RenderType getRenderType(HillOfFlesh animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

}
