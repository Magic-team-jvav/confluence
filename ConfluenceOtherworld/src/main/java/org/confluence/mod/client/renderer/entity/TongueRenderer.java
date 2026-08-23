package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.entity.boss.WallOfFleshMouth;
import org.confluence.mod.common.init.ModEffects;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.portlib.event.client.PortModelEvent;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;
import org.mesdag.portlib.event.client.PortRenderLivingEvent;

/// 绘制狂卷之舌效果中嘴部到受影响实体之间的连续饿鬼叶片。
public final class TongueRenderer {
    private static final ModelResourceLocation SEGMENT_MODEL = new ModelResourceLocation(Confluence.asResource("entity/the_hungry_leaf"), "inventory");
    private static final int BASE_SEGMENTS = 5;
    private static final float SEGMENTS_PER_BLOCK = 0.8F;

    private TongueRenderer() {}

    public static void registerAdditionalModels(PortModelEvent.RegisterAdditional event) {
        event.register(SEGMENT_MODEL);
    }

    public static void renderFirstPerson(PortRenderLevelStageEvent event) {
        if (event.getStage() != PortRenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || !minecraft.options.getCameraType().isFirstPerson())
            return;
        LivingEntity living = minecraft.player;
        if (!living.hasEffect(ModEffects.THE_TONGUE.get())) return;
        WallOfFleshMouth mouth = findMouth(living);
        if (mouth == null) return;

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 camera = event.getCamera().getPosition();
        Vec3 livingPosition = anchorPosition(living, partialTick);
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(livingPosition.x - camera.x, livingPosition.y - camera.y, livingPosition.z - camera.z);
        renderTongue(mouth, living, poseStack, minecraft.renderBuffers().bufferSource(), partialTick, LevelRenderer.getLightColor(living.level(), mouth.blockPosition()), true);
        poseStack.popPose();
    }

    public static void render(PortRenderLivingEvent.Post<?, ?> event) {
        LivingEntity living = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();
        if (!living.hasEffect(ModEffects.THE_TONGUE.get()) || minecraft.player == living && minecraft.options.getCameraType().isFirstPerson())
            return;
        WallOfFleshMouth mouth = findMouth(living);
        if (mouth != null)
            renderTongue(mouth, living, event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick(), event.getPackedLight(), false);
    }

    private static WallOfFleshMouth findMouth(LivingEntity living) {
        if (!(living.level() instanceof ClientLevel level)) return null;
        WallOfFleshMouth nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof WallOfFlesh wall) || wall.distanceToSqr(living) > 1000.0 * 1000.0)
                continue;
            WallOfFleshMouth mouth = wall.findTongueMouth(living);
            if (mouth == null) continue;
            double distance = mouth.distanceToSqr(living);
            if (distance < nearestDistance) {
                nearest = mouth;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private static void renderTongue(WallOfFleshMouth mouth, LivingEntity living, PoseStack poseStack, MultiBufferSource buffers, float partialTick, int packedLight, boolean firstPerson) {
        Vec3 livingPosition = anchorPosition(living, partialTick);
        Vec3 difference = interpolatedPosition(mouth, partialTick).subtract(livingPosition);
        double distance = difference.length();
        if (distance < 1.0E-5) return;

        int count = Math.max((int) (distance * SEGMENTS_PER_BLOCK) + BASE_SEGMENTS, BASE_SEGMENTS);
        if (!firstPerson) count = Math.min(count, 75);
        Vec3 step = difference.scale(1.0 / count);
        Vec3 offset = difference.normalize();
        Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), step.toVector3f());
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(SEGMENT_MODEL);

        poseStack.pushPose();
        if (!firstPerson) poseStack.translate(0.0, living.getEyeHeight() * 0.5, 0.0);
        for (int index = 0; index < count; index++) {
            poseStack.pushPose();
            poseStack.translate(offset.x + step.x * index, offset.y + step.y * index, offset.z + step.z * index);
            poseStack.mulPose(rotation);
            renderModel(model, poseStack, buffers, packedLight);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static void renderModel(BakedModel model, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        ItemStack stack = ItemStack.EMPTY;
        for (RenderType renderType : model.getRenderTypes(stack, false)) {
            VertexConsumer vertices = ItemRenderer.getFoilBuffer(buffers, renderType, false, false);
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, stack, packedLight, OverlayTexture.NO_OVERLAY, poseStack, vertices);
        }
    }

    private static Vec3 interpolatedPosition(Entity entity, float partialTick) {
        return new Vec3(Mth.lerp(partialTick, entity.xOld, entity.getX()), Mth.lerp(partialTick, entity.yOld, entity.getY()), Mth.lerp(partialTick, entity.zOld, entity.getZ()));
    }

    private static Vec3 anchorPosition(LivingEntity living, float partialTick) {
        return interpolatedPosition(living, partialTick).add(0.0, living.getEyeHeight() * 0.5, 0.0);
    }
}
