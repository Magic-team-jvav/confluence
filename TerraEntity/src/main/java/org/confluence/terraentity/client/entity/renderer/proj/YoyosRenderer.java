package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.proj.YoyosEntity;
import org.confluence.terraentity.item.YoyosItem;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class YoyosRenderer extends GeoEntityRenderer<YoyosEntity> {


    public YoyosRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeoModel<>(){

            @Override
            public ResourceLocation getModelResource(YoyosEntity animatable) {
                return TerraEntity.space("geo/entity/yoyos.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(YoyosEntity animatable) {
                return animatable.texture;
            }

            @Override
            public ResourceLocation getAnimationResource(YoyosEntity animatable) {
                return null;
            }
        });
    }

    @Override
    public boolean shouldRender(YoyosEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(YoyosEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        Entity living = entity.getOwner();
        if(entity.texture == null){
            return;
        }

        Item weapon = entity.getWeaponItem().getItem();
        if(!(weapon instanceof YoyosItem yoyosItem)) {
            return;
        }
        if (living instanceof Player player) {
            poseStack.pushPose();
            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks , player.yHeadRotO,player.yHeadRot)));
            poseStack.translate(0,0.25,0);
            poseStack.mulPose(Axis.XN.rotationDegrees((entity.tickCount + partialTicks) * 45));
            poseStack.translate(0,-0.5,0);


            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
//            model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entitySolid(getTextureLocation(entity))), OverlayTexture.NO_OVERLAY, packedLight);

            poseStack.popPose();

            float f = player.getAttackAnim(partialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * 3.1415927F);
            Vec3 vec3 = this.getPlayerHandPos(player, f1, partialTicks);

            Vec3 vec31 = entity.getPosition(partialTicks).add(0.0, 0.25, 0.0);
            float f2 = (float)(vec3.x - vec31.x);
            float f3 = (float)(vec3.y - vec31.y);
            float f4 = (float)(vec3.z - vec31.z);
            VertexConsumer vertexconsumer1 = buffer.getBuffer(RenderType.lineStrip());
            PoseStack.Pose posestack$pose1 = poseStack.last();

            for (int j = 0; j <= 16; ++j) {
                stringVertex(f2, f3, f4, vertexconsumer1, posestack$pose1, fraction(j, 16), fraction(j + 1, 16), yoyosItem.getStringColor());
            }

            poseStack.popPose();
        }

    }



    private Vec3 getPlayerHandPos(Player player, float p_340872_, float partialTick) {
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;

        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double d4 = 960.0 / (double) this.entityRenderDispatcher.options.fov().get();
            Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane()
                    .getPointOnPlane((float)i * 0.525F, -0.5F)
                    .scale(d4)
//                    .yRot(p_340872_ * 0.5F)
//                    .xRot(-p_340872_ * 0.7F)
                    ;
            return player.getEyePosition(partialTick).add(vec3);
        } else {
            float f = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * 0.017453292F;
            double d0 = Mth.sin(f);
            double d1 = Mth.cos(f);
            float f1 = player.getScale();
            double d2 = (double)i * 0.35 * (double)f1;
            double d3 = 0.25 * (double)f1;
            float f2 = player.isCrouching() ? -0.1875F : 0.0F;
            return player.getEyePosition(partialTick).add(-d1 * d2 - d0 * d3, (double)f2 - 0.65 * (double)f1, -d0 * d2 + d1 * d3);
        }
    }

    private static float fraction(int numerator, int denominator) {
        return (float)numerator / (float)denominator;
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, int y, int u, int v) {
        consumer.addVertex(pose, x - 0.5F, (float)y - 0.5F, 0.0F).setColor(-1).setUv((float)u, (float)v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static void stringVertex(float x, float y, float z, VertexConsumer consumer, PoseStack.Pose pose, float stringFraction, float nextStringFraction, int color) {
        float f = x * stringFraction;
        float f11 = y * (stringFraction * stringFraction + stringFraction) * 0.5F  + 0.25F; // 下方：(x * x + x) * 0.5F
        float f12 = y * (-(stringFraction - 1) * (stringFraction - 1) + 1 )   + 0.25F; // 上方：-(x - 1) ** 2 + 1

        float f1 = Mth.lerp((Mth.clamp(y, -5, 5) + 5) * 0.1f, f12, f11);

        f1 += (stringFraction - 1) * stringFraction * 2; // 让中间过渡更自然

        float f2 = z * stringFraction;
        float f3 = x * nextStringFraction - f;
        float f4 = y * (nextStringFraction * nextStringFraction + nextStringFraction) * 0.5F + 0.25F - f1;
        float f5 = z * nextStringFraction - f2;
        float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        f3 /= f6;
        f4 /= f6;
        f5 /= f6;
        consumer.addVertex(pose, f, f1, f2).setColor(color).setNormal(pose, f3, f4, f5);
    }

}
