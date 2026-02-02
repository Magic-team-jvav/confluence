package org.confluence.terraentity.client.post;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.confluence.terraentity.client.init.model.EntityBlockModelRegister;
import org.confluence.terraentity.effect.harmful.TheTongueEffect;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshMouth;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.utils.TEUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TongueRenderer {

    // 第一人称渲染
    public static void renderFirstPerson(RenderLevelStageEvent event) {
        if (Minecraft.getInstance().level == null) return;

        for (Player player : Minecraft.getInstance().level.players()) {
            if (!player.hasEffect(TEEffects.THE_TONGUE)) continue;
            if (player.getEffect(TEEffects.THE_TONGUE) == null) continue;

            MobEffect rawEffect = player.getEffect(TEEffects.THE_TONGUE).getEffect().value();
            if (rawEffect instanceof TheTongueEffect effect) {
                WallOfFleshMouth mouth = effect.getWallOfFleshMouth();
                if (mouth != null && mouth.isAlive() && player.isAlive()) {
                    boolean isFirstPerson = Minecraft.getInstance().player == player && Minecraft.getInstance().options.getCameraType().isFirstPerson();
                    if (!isFirstPerson) continue;

                    Vec3 init = mouth.position();
                    float partialTick = event.getPartialTick().getGameTimeDeltaTicks();
                    Vec3 targetLightPos = new Vec3(init.x, player.level().getMaxBuildHeight() + 1, init.z);
                    BlockPos lightPos = new BlockPos((int) targetLightPos.x, (int) targetLightPos.y, (int) targetLightPos.z);
                    int skyLight = player.level().getBrightness(LightLayer.SKY, lightPos);
                    int blockLight = player.level().getBrightness(LightLayer.BLOCK, lightPos);
                    int packedLight = LightTexture.pack(skyLight, blockLight);

                    renderTongueEffect(mouth, player, event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), partialTick, packedLight, true);
                }
            }
        }
    }
    // 第三人称渲染
    public static void render(RenderLivingEvent event) {

        LivingEntity livingEntity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        float partialTick = event.getPartialTick();
        int packedLight = event.getPackedLight();
        if (livingEntity.hasEffect(TEEffects.THE_TONGUE) && livingEntity.getEffect(TEEffects.THE_TONGUE) != null) {
            MobEffect rawEffect = livingEntity.getEffect(TEEffects.THE_TONGUE).getEffect().value();
            if (rawEffect instanceof TheTongueEffect effect) {
                WallOfFleshMouth mouth = effect.getWallOfFleshMouth();
                if (mouth != null && mouth.isAlive() && livingEntity.isAlive()) {
                    boolean isFirstPersonSelf = Minecraft.getInstance().player == livingEntity && Minecraft.getInstance().options.getCameraType().isFirstPerson();
                    if (isFirstPersonSelf) return;
                    renderTongueEffect(mouth, livingEntity, poseStack, bufferSource, partialTick, packedLight, false);
                }
            }
        }
    }

    private static void renderTongueEffect(WallOfFleshMouth mouth, LivingEntity livingEntity, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTick, int packedLight, boolean isFirstPerson) {
        Vec3 init = mouth.position();

        Vec3 lerpPos = new Vec3(
                Mth.lerp(partialTick, livingEntity.xOld, livingEntity.getX()),
                Mth.lerp(partialTick, livingEntity.yOld + livingEntity.getEyeHeight() * 0.5f, livingEntity.getY() + livingEntity.getEyeHeight() * 0.5f),
                Mth.lerp(partialTick, livingEntity.zOld, livingEntity.getZ())
        );

        Vec3 _diff = lerpPos.subtract(init);
        Vec3 diffNorm = _diff.normalize();

        // 让叶子紧贴实体
        Vec3 offset = diffNorm.scale(-1);
        Vec3 diff = _diff.subtract(offset);
        double distance = _diff.length(); // 获取实体与初始点的直线距离
        int baseCount = 5; // 基础数量
        float densityFactor = 0.8f;

        int count;
        if (isFirstPerson) {
            count = Math.max((int) (distance * densityFactor) + baseCount, baseCount);
        } else {
            count = Mth.clamp(
                    (int) (distance * densityFactor) + baseCount,
                    baseCount,
                    75
            );
        }
        double dx = diff.x / count;
        double dy = diff.y / count;
        double dz = diff.z / count;

        Quaternionf rotate = TEUtils.rotateFromV1ToV2(new Vector3f(0, 1, 0), new Vector3f((float) dx, (float) dy, (float) dz));
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(EntityBlockModelRegister.getInstance().getModelResourceLocation(TEMonsterEntities.THE_HUNGRY.get()));

        poseStack.pushPose();

        for (int i = 0; i < count; i++) {
            Vec3 pos = new Vec3(-i * dx + offset.x, -i * dy + offset.y, -i * dz + offset.z);
            poseStack.pushPose();

            poseStack.translate(pos.x, pos.y, pos.z);
            poseStack.mulPose(rotate);

            ItemStack stack = Items.AIR.getDefaultInstance();
            for (RenderType rendertype : model.getRenderTypes(stack, false)) {
                VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, rendertype, false, stack.isEnchanted());
                Minecraft.getInstance().getItemRenderer().renderModelLists(
                        model, stack, packedLight, OverlayTexture.NO_OVERLAY,
                        poseStack, vertexconsumer);
            }

            poseStack.popPose();
        }
        poseStack.popPose();
    }

}
