package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.init.model.WhipModelRegister;
import org.confluence.terraentity.config.ClientConfig;
import org.confluence.terraentity.entity.ai.keyframe.FrameUtil;
import org.confluence.terraentity.entity.proj.WhipEntity;
import org.confluence.terraentity.item.BaseWhipItem;

import java.util.ArrayList;
import java.util.List;

public class WhipEntityRenderer extends EntityRenderer<WhipEntity> {

    public WhipEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(WhipEntity whipEntity) {
        return null;
    }

    public boolean shouldRender(WhipEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    public void render(WhipEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        if(entity.keyPositions == null) return;
        ItemStack stack = entity.getWeapon();
        if(stack.isEmpty()) return;
        Item item1 = stack.getItem();
        if(!(item1 instanceof BaseWhipItem whipItem)) return;

        if(entity.getOwner() instanceof Player player) {
            boolean paused = Minecraft.getInstance().isPaused();

            poseStack.pushPose();
//            poseStack.translate(-0.5, -0.5f, -0.5);
            float f = player.getAttackAnim(partialTick);
            float f1 = Mth.sin(Mth.sqrt(f) * 3.1415927F);
            Vec3 vec3 = this.getPlayerHandPos(player, f1, partialTick);
            List<Vec3> toInterpoloate = new ArrayList<>(entity.keyPositions.stream().map(Vec3::new).toList());
            List<Vec3> toInterpoloateO = new ArrayList<>(entity.keyPositionsO.stream().map(Vec3::new).toList());
            for(int i = 0; i < toInterpoloateO.size(); i++){
                // 关键点位置插值
                double lerpx = Mth.lerp(partialTick, toInterpoloateO.get(i).x(), toInterpoloate.get(i).x());
                double lerpy = Mth.lerp(partialTick, toInterpoloateO.get(i).y(), toInterpoloate.get(i).y());
                double lerpz = Mth.lerp(partialTick, toInterpoloateO.get(i).z(), toInterpoloate.get(i).z());
                toInterpoloate.set(i, new Vec3(lerpx, lerpy, lerpz));
            }
            // 位置插值
            double lerpx = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double lerpy = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double lerpz = Mth.lerp(partialTick, entity.zOld, entity.getZ());

            // 这里要 * 2
            toInterpoloate.add(vec3.subtract(new Vec3(lerpx, lerpy, lerpz)).scale(2));

            // Catmull-Rom样条插值
            int count = (int)(entity.getRange(player) * 20);
            List<Vec3> positions = FrameUtil.getInterpolatedPoints(toInterpoloate, count);

            float f5 = 0.0F;
            float f6 = 0.0F;
            float f7 = 0.0F;
            int i = 0;
            for(Vec3 vec : positions){
                // 变换到相机坐标系
                float f2 = (float) (vec3.x - vec.x - lerpx);
                float f3 = (float) (vec3.y - vec.y - lerpy);
                float f4 = (float) (vec3.z - vec.z - lerpz);
                // 生成粒子
                if(!paused && ClientConfig.GENERATE_PROJECTILE_PARTICLE.get() && whipItem.particleOptions != null) {
                    if (entity.getRandom().nextFloat() < whipItem.chance) {
                        entity.level().addParticle(whipItem.particleOptions.get(),
                                entity.getX() - f2, entity.getY() - f3, entity.getZ() - f4,
                                0, 0, 0);
                    }
                }
                // 排除第一个点
                if(i > 0) {
                    poseStack.pushPose();
    //                PoseStack.Pose posestack$pose1 = poseStack.last();

                    poseStack.translate(-f2 , -f3, -f4);
                    poseStack.scale(0.5f, 0.5f, 0.5f);

                    // 旋转到前方视角
                    float fx = f2 - f5;
                    float fy = f3 - f6;
                    float fz = f4 - f7;
                    float yaw = (float) (Math.PI - Math.atan2(fz, fx));
                    float pitch = (float) (-Math.atan2(fy,
                            Math.sqrt(fx * fx + fz * fz)));
                    poseStack.mulPose(Axis.YP.rotation(yaw));
                    poseStack.mulPose(Axis.ZP.rotation((float) (pitch + Math.PI / 2 )));
                    float ff = 0.5f;
                    poseStack.translate(ff,ff,ff);
                    poseStack.mulPose(Axis.YP.rotationDegrees(i * 10));
                    poseStack.translate(-ff,-ff,-ff);

    //                stringVertex(-f2, -f3, -f4, vertexconsumer1, posestack$pose1);
    //                poseStack.translate(-vec31.x, -vec31.y, -vec31.z);

                    ModelResourceLocation modelResourceLocation = WhipModelRegister.getInstance().getModelResourceLocation(stack.getItem());
                    BakedModel model;
                    VertexConsumer vertexconsumer;
                    if(modelResourceLocation != null){
                        // 当是模型的时候
                        model = Minecraft.getInstance().getModelManager().getModel(modelResourceLocation);
                        for (RenderType rendertype : model.getRenderTypes(stack, false)) {
                            vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, rendertype, false, stack.isEnchanted());
                            Minecraft.getInstance().getItemRenderer().renderModelLists(
                                    model, stack, packedLight, OverlayTexture.NO_OVERLAY,
                                    poseStack, vertexconsumer);
                        }
                    }else if (whipItem.blockStateSupplier != null) {
                        // 否则是方块
                        BlockState blockState = whipItem.blockStateSupplier.get();
                        model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(blockState);
                        for (RenderType rendertype : model.getRenderTypes(stack, false)) {
                            vertexconsumer = bufferSource.getBuffer(rendertype);
                            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), vertexconsumer,
                                    blockState, model, 1.0F, 1.0F, 1.0F, packedLight,
                                    OverlayTexture.NO_OVERLAY
                            );
                        }
                    }else{
                        // 否则是缺材质
                        model = Minecraft.getInstance().getModelManager().getMissingModel();
                        for (RenderType rendertype : model.getRenderTypes(stack, false)) {
                            vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, rendertype, false, stack.isEnchanted());
                            Minecraft.getInstance().getItemRenderer().renderModelLists(
                                    model, stack, packedLight, OverlayTexture.NO_OVERLAY,
                                    poseStack, vertexconsumer);
                        }
                    }
                    poseStack.popPose();
                }
                f5 = f2;
                f6 = f3;
                f7 = f4;
                i++;
            }
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private Vec3 getPlayerHandPos(Player player, float angle, float partialTick) {
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double d4 = 960.0 / (double) this.entityRenderDispatcher.options.fov().get();
            Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane()
                    .getPointOnPlane((float)i * 0.525F, -0.1F)
                    .scale(d4)
                    .yRot(angle * 0.5F - 1F)
                    .xRot(-angle * 0.7F);
            return player.getPosition(partialTick).add(vec3).add(0,player.getEyeHeight()*0.8f,0);
        } else {
            float f = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * 0.017453292F;
            double d0 = Mth.sin(f);
            double d1 = Mth.cos(f);
            float f1 = player.getScale();
            double d2 = (double)i * 0.5 * (double)f1;
            double d3 = 0* (double)f1;
            float f2 = player.isCrouching() ? -0.1875F : 0.0F;
            return player.getEyePosition(partialTick).add(-d1 * d2 - d0 * d3, (double)f2 - 1 * (double)f1, -d0 * d2 + d1 * d3);
        }
    }

    private static float fraction(int numerator, int denominator) {
        return (float)numerator / (float)denominator;
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, int y, int u, int v) {
        consumer.addVertex(pose, x - 0.5F, (float)y - 0.5F, 0.0F).setColor(-1).setUv((float)u, (float)v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static void stringVertex(float x, float y, float z, VertexConsumer consumer, PoseStack.Pose pose) {
        float f6 = Mth.sqrt(x*x + y*y + z*z);
        consumer.addVertex(pose, x, y, z).setColor(-16777216).setNormal(pose, x / f6, y / f6, z / f6);
    }
}
