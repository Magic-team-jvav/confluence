package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.projectile.mana.AbstractLittleCelestialEntity;
import org.confluence.mod.common.entity.projectile.mana.LittleSunEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/// 小日光：小小的发光立方体，飞行时会朝向移动方向。
public class LittleSunRenderer extends GeoEntityRenderer<LittleSunEntity> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/proj/little_sun.geo.json");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/proj/little_sun.png");

    public LittleSunRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        this.shadowRadius = 0.0F;
    }

    /// 外部剔除 + 自发光：默认的 entityCutoutNoCull 会让负体积外壳的外侧也画出来，
    /// 这里换成带背面剔除的半透明自发光类型（原版 entityTranslucentEmissive 是不剔除的），
    /// 只留朝内那层外壳并整体发亮。
    @Override
    public RenderType getRenderType(LittleSunEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderStateShardAccessor.entityEmissiveCull(texture);
    }

    @Override
    public void render(LittleSunEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        alignToHost(entity, partialTick, poseStack);
        faceMovement(entity, entityYaw, partialTick, poseStack);
        /// 满亮渲染：不跟随世界光照，免得日光/月光在暗处被压暗。
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    /// 让模型朝向移动方向；附着在敌怪头顶时保持朝向宿主
    static void faceMovement(AbstractLittleCelestialEntity entity, float entityYaw, float partialTick, PoseStack poseStack) {
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
    }

    /// 和钛金碎片屏障一致：附着体只由服务端传送到宿主头顶，客户端坐标会落后一截，
    /// 直接画出来就会抖；这里按「宿主插值位置 + 头顶偏移」与「自身插值位置」的差值平移，
    /// 让模型始终稳稳贴在宿主头上。
    static void alignToHost(AbstractLittleCelestialEntity entity, float partialTick, PoseStack poseStack) {
        LivingEntity host = entity.getHost();
        if (host == null) return;
        double x = Mth.lerp(partialTick, host.xOld, host.getX()) - Mth.lerp(partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTick, host.yOld, host.getY()) + AbstractLittleCelestialEntity.attachedAnchorY(host)
                - Mth.lerp(partialTick, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTick, host.zOld, host.getZ()) - Mth.lerp(partialTick, entity.zOld, entity.getZ());
        poseStack.translate((float) x, (float) y, (float) z);
    }

    private static final class Model extends GeoModel<LittleSunEntity> {
        @Override
        public ResourceLocation getModelResource(LittleSunEntity animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(LittleSunEntity animatable) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(LittleSunEntity animatable) {
            return null;
        }
    }
}
